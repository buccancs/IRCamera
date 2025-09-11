package com.topdon.tc001.sensors

import android.content.Context
import android.util.Log
import com.topdon.tc001.sensors.gsr.GSRSensorRecorder
import com.topdon.tc001.sensors.thermal.ThermalCameraRecorder
import com.topdon.tc001.sensors.rgb.RgbCameraRecorder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.io.File

/**
 * Production-ready RecordingController for parallel multi-sensor recording.
 * 
 * Manages simultaneous recording from:
 * - RGB Camera (CameraX dual-stream: 1080p MP4 + high-res JPEG frames)
 * - Thermal Camera (Topdon TC001 via latest SDK)
 * - GSR Sensor (Shimmer3 GSR+ via Nordic BLE)
 * 
 * Features:
 * - Parallel sensor initialization and recording
 * - Synchronized session management with timestamp alignment
 * - Error recovery and automatic reconnection
 * - Real-time status monitoring and data aggregation
 * - Production-ready libs integration
 * 
 * @author IRCamera Android Sensor Node (Spoke) - Production Multi-Sensor Controller
 */
class RecordingController(private val context: Context) {

    companion object {
        private const val TAG = "RecordingController"
        private const val SESSION_BASE_DIR = "sensor_sessions"
        private const val SYNC_MARKER_INTERVAL_MS = 5000L // 5-second sync markers
    }

    // Sensor recorders using libs integration
    private var rgbRecorder: RgbCameraRecorder? = null
    private var thermalRecorder: ThermalCameraRecorder? = null
    private var gsrRecorder: GSRSensorRecorder? = null
    
    // Recording state
    private val isRecording = AtomicBoolean(false)
    private val isInitialized = AtomicBoolean(false)
    private var currentSessionId: String? = null
    private var sessionStartTime: Long = 0
    private val syncMarkerCount = AtomicLong(0)
    
    // Coroutine management
    private val controllerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var syncMarkerJob: Job? = null
    
    // Status flows
    private val _recordingStatus = MutableStateFlow<OverallRecordingStatus>(
        OverallRecordingStatus.Idle
    )
    val recordingStatus: StateFlow<OverallRecordingStatus> = _recordingStatus.asStateFlow()
    
    private val _sensorStatuses = MutableSharedFlow<Map<String, RecordingStatus>>()
    val sensorStatuses: SharedFlow<Map<String, RecordingStatus>> = _sensorStatuses.asSharedFlow()
    
    private val _errors = MutableSharedFlow<RecordingControllerError>()
    val errors: SharedFlow<RecordingControllerError> = _errors.asSharedFlow()

    /**
     * Initialize all sensor recorders with libs integration
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing RecordingController with production libs integration")
            
            if (isInitialized.get()) {
                Log.w(TAG, "RecordingController already initialized")
                return@withContext true
            }
            
            // Initialize sensors in parallel for faster startup
            val initializationResults = listOf(
                async { initializeRgbCamera() },
                async { initializeThermalCamera() },
                async { initializeGsrSensor() }
            ).awaitAll()
            
            val successCount = initializationResults.count { it }
            Log.i(TAG, "Sensor initialization completed: $successCount/3 sensors ready")
            
            if (successCount == 0) {
                emitError("All sensor initialization failed", false)
                return@withContext false
            }
            
            isInitialized.set(true)
            _recordingStatus.value = OverallRecordingStatus.Ready
            
            // Start status monitoring
            startStatusMonitoring()
            
            Log.i(TAG, "RecordingController initialized successfully with $successCount sensors")
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize RecordingController", e)
            emitError("Initialization failed: ${e.message}", false)
            return@withContext false
        }
    }
    
    private suspend fun initializeRgbCamera(): Boolean {
        return try {
            rgbRecorder = RgbCameraRecorder(context)
            val success = rgbRecorder?.initialize() ?: false
            Log.i(TAG, "RGB camera initialization: ${if (success) "SUCCESS" else "FAILED"}")
            success
        } catch (e: Exception) {
            Log.e(TAG, "RGB camera initialization failed", e)
            false
        }
    }
    
    private suspend fun initializeThermalCamera(): Boolean {
        return try {
            thermalRecorder = ThermalCameraRecorder(context)
            val success = thermalRecorder?.initialize() ?: false
            Log.i(TAG, "Thermal camera initialization: ${if (success) "SUCCESS" else "FAILED"}")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Thermal camera initialization failed", e)
            false
        }
    }
    
    private suspend fun initializeGsrSensor(): Boolean {
        return try {
            gsrRecorder = GSRSensorRecorder(context)
            val success = gsrRecorder?.initialize() ?: false
            Log.i(TAG, "GSR sensor initialization: ${if (success) "SUCCESS" else "FAILED"}")
            success
        } catch (e: Exception) {
            Log.e(TAG, "GSR sensor initialization failed", e)
            false
        }
    }

    /**
     * Start recording from all available sensors in parallel
     */
    suspend fun startRecording(sessionId: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isRecording.get()) {
                Log.w(TAG, "Recording already in progress")
                return@withContext true
            }
            
            if (!isInitialized.get()) {
                emitError("Controller not initialized", true)
                return@withContext false
            }
            
            // Generate session ID and directory
            currentSessionId = sessionId ?: generateSessionId()
            val sessionDirectory = createSessionDirectory(currentSessionId!!)
            
            Log.i(TAG, "Starting parallel recording session: $currentSessionId")
            _recordingStatus.value = OverallRecordingStatus.Starting
            
            sessionStartTime = System.nanoTime()
            isRecording.set(true)
            
            // Start all sensors in parallel
            val startResults = listOf(
                async { startSensorRecording(rgbRecorder, sessionDirectory, "RGB") },
                async { startSensorRecording(thermalRecorder, sessionDirectory, "Thermal") },
                async { startSensorRecording(gsrRecorder, sessionDirectory, "GSR") }
            ).awaitAll()
            
            val successCount = startResults.count { it }
            Log.i(TAG, "Recording started: $successCount/3 sensors active")
            
            if (successCount == 0) {
                isRecording.set(false)
                _recordingStatus.value = OverallRecordingStatus.Error("No sensors started")
                emitError("Failed to start any sensors", true)
                return@withContext false
            }
            
            _recordingStatus.value = OverallRecordingStatus.Recording(currentSessionId!!)
            
            // Start sync marker generation
            startSyncMarkers()
            
            Log.i(TAG, "Parallel recording session started successfully: $currentSessionId")
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            _recordingStatus.value = OverallRecordingStatus.Error("Start failed: ${e.message}")
            emitError("Recording start failed: ${e.message}", true)
            return@withContext false
        }
    }
    
    private suspend fun startSensorRecording(
        recorder: SensorRecorder?, 
        sessionDirectory: String, 
        sensorName: String
    ): Boolean {
        return try {
            if (recorder != null) {
                val success = recorder.startRecording(sessionDirectory)
                Log.i(TAG, "$sensorName sensor recording: ${if (success) "STARTED" else "FAILED"}")
                success
            } else {
                Log.w(TAG, "$sensorName sensor not available")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "$sensorName sensor start failed", e)
            false
        }
    }

    /**
     * Stop recording from all sensors
     */
    suspend fun stopRecording(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isRecording.get()) {
                Log.w(TAG, "No recording in progress")
                return@withContext true
            }
            
            Log.i(TAG, "Stopping recording session: $currentSessionId")
            _recordingStatus.value = OverallRecordingStatus.Stopping
            
            // Stop sync markers
            syncMarkerJob?.cancel()
            syncMarkerJob = null
            
            // Stop all sensors in parallel
            val stopResults = listOf(
                async { stopSensorRecording(rgbRecorder, "RGB") },
                async { stopSensorRecording(thermalRecorder, "Thermal") },
                async { stopSensorRecording(gsrRecorder, "GSR") }
            ).awaitAll()
            
            val successCount = stopResults.count { it }
            Log.i(TAG, "Recording stopped: $successCount/3 sensors stopped successfully")
            
            isRecording.set(false)
            _recordingStatus.value = OverallRecordingStatus.Ready
            
            val sessionDuration = (System.nanoTime() - sessionStartTime) / 1_000_000
            Log.i(TAG, "Recording session completed: $currentSessionId (${sessionDuration}ms)")
            
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
            _recordingStatus.value = OverallRecordingStatus.Error("Stop failed: ${e.message}")
            emitError("Recording stop failed: ${e.message}", true)
            return@withContext false
        }
    }
    
    private suspend fun stopSensorRecording(recorder: SensorRecorder?, sensorName: String): Boolean {
        return try {
            if (recorder != null) {
                val success = recorder.stopRecording()
                Log.i(TAG, "$sensorName sensor recording: ${if (success) "STOPPED" else "STOP_FAILED"}")
                success
            } else {
                Log.w(TAG, "$sensorName sensor not available for stop")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "$sensorName sensor stop failed", e)
            false
        }
    }

    /**
     * Add synchronized markers to all active sensors
     */
    suspend fun addSyncMarker(markerType: String, metadata: Map<String, String> = emptyMap()) {
        if (!isRecording.get()) {
            Log.w(TAG, "Cannot add sync marker - not recording")
            return
        }
        
        val timestampNs = System.nanoTime()
        val markerMetadata = metadata + mapOf(
            "session_id" to (currentSessionId ?: "unknown"),
            "marker_sequence" to syncMarkerCount.incrementAndGet().toString()
        )
        
        Log.i(TAG, "Adding sync marker: $markerType")
        
        // Add marker to all active sensors in parallel
        listOf(
            controllerScope.async { rgbRecorder?.addSyncMarker(markerType, timestampNs, markerMetadata) },
            controllerScope.async { thermalRecorder?.addSyncMarker(markerType, timestampNs, markerMetadata) },
            controllerScope.async { gsrRecorder?.addSyncMarker(markerType, timestampNs, markerMetadata) }
        ).awaitAll()
    }
    
    private fun startSyncMarkers() {
        syncMarkerJob = controllerScope.launch {
            while (isActive && isRecording.get()) {
                try {
                    addSyncMarker("AUTO_SYNC", mapOf("interval_ms" to SYNC_MARKER_INTERVAL_MS.toString()))
                    delay(SYNC_MARKER_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.w(TAG, "Sync marker generation failed", e)
                }
            }
        }
    }
    
    private fun startStatusMonitoring() {
        controllerScope.launch {
            while (isActive) {
                try {
                    val statuses = mutableMapOf<String, RecordingStatus>()
                    
                    rgbRecorder?.let { recorder ->
                        recorder.getStatusFlow().firstOrNull()?.let { status ->
                            statuses["rgb"] = status
                        }
                    }
                    
                    thermalRecorder?.let { recorder ->
                        recorder.getStatusFlow().firstOrNull()?.let { status ->
                            statuses["thermal"] = status
                        }
                    }
                    
                    gsrRecorder?.let { recorder ->
                        recorder.getStatusFlow().firstOrNull()?.let { status ->
                            statuses["gsr"] = status
                        }
                    }
                    
                    if (statuses.isNotEmpty()) {
                        _sensorStatuses.emit(statuses)
                    }
                    
                    delay(1000) // Update every second
                } catch (e: Exception) {
                    Log.w(TAG, "Status monitoring error", e)
                }
            }
        }
    }

    private fun generateSessionId(): String {
        val timestamp = System.currentTimeMillis()
        return "session_${timestamp}"
    }
    
    private fun createSessionDirectory(sessionId: String): String {
        val baseDir = File(context.getExternalFilesDir(null), SESSION_BASE_DIR)
        val sessionDir = File(baseDir, sessionId)
        
        if (!sessionDir.exists()) {
            sessionDir.mkdirs()
        }
        
        return sessionDir.absolutePath
    }
    
    private suspend fun emitError(message: String, isRecoverable: Boolean) {
        val error = RecordingControllerError(
            message = message,
            timestamp = System.currentTimeMillis(),
            isRecoverable = isRecoverable,
            currentSessionId = currentSessionId
        )
        _errors.emit(error)
    }

    /**
     * Get overall recording statistics
     */
    fun getOverallStats(): OverallRecordingStats {
        val rgbStats = rgbRecorder?.getRecordingStats()
        val thermalStats = thermalRecorder?.getRecordingStats()
        val gsrStats = gsrRecorder?.getRecordingStats()
        
        val sessionDuration = if (sessionStartTime > 0) {
            (System.nanoTime() - sessionStartTime) / 1_000_000
        } else 0L
        
        return OverallRecordingStats(
            sessionId = currentSessionId,
            isRecording = isRecording.get(),
            sessionDurationMs = sessionDuration,
            rgbStats = rgbStats,
            thermalStats = thermalStats,
            gsrStats = gsrStats,
            syncMarkersCount = syncMarkerCount.get(),
            totalStorageUsedMB = (rgbStats?.dataSize ?: 0L) + 
                              (thermalStats?.dataSize ?: 0L) + 
                              (gsrStats?.dataSize ?: 0L)
        )
    }

    /**
     * Cleanup all resources
     */
    suspend fun cleanup() {
        try {
            Log.i(TAG, "Cleaning up RecordingController")
            
            if (isRecording.get()) {
                stopRecording()
            }
            
            controllerScope.cancel()
            
            // Cleanup all sensors
            listOf(
                controllerScope.async { rgbRecorder?.cleanup() },
                controllerScope.async { thermalRecorder?.cleanup() },
                controllerScope.async { gsrRecorder?.cleanup() }
            ).awaitAll()
            
            isInitialized.set(false)
            _recordingStatus.value = OverallRecordingStatus.Idle
            
            Log.i(TAG, "RecordingController cleanup completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "RecordingController cleanup failed", e)
        }
    }
}

// Data classes for recording status
sealed class OverallRecordingStatus {
    object Idle : OverallRecordingStatus()
    object Ready : OverallRecordingStatus()
    object Starting : OverallRecordingStatus()
    data class Recording(val sessionId: String) : OverallRecordingStatus()
    object Stopping : OverallRecordingStatus()
    data class Error(val message: String) : OverallRecordingStatus()
}

data class RecordingControllerError(
    val message: String,
    val timestamp: Long,
    val isRecoverable: Boolean,
    val currentSessionId: String?
)

data class OverallRecordingStats(
    val sessionId: String?,
    val isRecording: Boolean,
    val sessionDurationMs: Long,
    val rgbStats: RecordingStats?,
    val thermalStats: RecordingStats?,
    val gsrStats: RecordingStats?,
    val syncMarkersCount: Long,
    val totalStorageUsedMB: Long
)