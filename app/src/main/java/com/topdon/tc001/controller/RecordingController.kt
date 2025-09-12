package com.topdon.tc001.controller

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.topdon.tc001.sensors.*
import com.topdon.tc001.sensors.rgb.RgbCameraRecorder
import com.topdon.tc001.sensors.thermal.ThermalCameraRecorder
import com.topdon.tc001.sensors.gsr.GSRSensorRecorder
import com.topdon.tc001.config.ConfigurationManager
import com.topdon.tc001.sync.TimeSynchronizationService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Enhanced Central coordinator for all sensor recording in the Multi-Modal Physiological Sensing Platform.
 * 
 * This controller manages the complete sensor recording pipeline with:
 * - Configuration-driven sensor initialization
 * - Synchronized start/stop across all sensors using configuration parameters
 * - Real-time monitoring and sophisticated error recovery
 * - Temporal synchronization with NTP-like protocol
 * - Performance monitoring and quality assurance
 * - Auto-reconnection and recovery mechanisms
 * 
 * The RecordingController implements the core logic for the Android Sensor Node (Spoke)
 * in the Hub-and-Spoke architecture, coordinating multi-modal data collection.
 * 
 * @author IRCamera Android Sensor Node (Spoke)
 */
class RecordingController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    companion object {
        private const val TAG = "RecordingController"
        private const val SYNC_MARKER_DISTRIBUTION_DELAY_MS = 50L
        private const val STATUS_UPDATE_INTERVAL_MS = 1000L
        private const val ERROR_RECOVERY_DELAY_MS = 2000L
        private const val MAX_RECOVERY_ATTEMPTS = 3
        private const val HEALTH_CHECK_INTERVAL_MS = 5000L
    }

    // Enhanced services
    private val configurationManager = ConfigurationManager(context)
    private val timeSyncService = TimeSynchronizationService()
    
    // Sensor recorders
    private val sensorRecorders = ConcurrentHashMap<String, SensorRecorder>()
    private val sensorHealthStatus = ConcurrentHashMap<String, SensorHealth>()
    private val recoveryAttempts = ConcurrentHashMap<String, Int>()
    
    // Recording state
    private var _isRecording = AtomicBoolean(false)
    val isRecording: Boolean get() = _isRecording.get()
    
    private var currentSessionDirectory: String? = null
    private var recordingStartTime: Long = 0
    private var hubIpAddress: String? = null
    
    // Coroutine management
    private val controllerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var statusMonitoringJob: Job? = null
    private var errorMonitoringJob: Job? = null
    private var healthCheckJob: Job? = null
    
    // Data flows
    private val _recordingStateFlow = MutableStateFlow(RecordingState.STOPPED)
    val recordingStateFlow: StateFlow<RecordingState> = _recordingStateFlow.asStateFlow()
    
    private val _sensorStatusFlow = MutableSharedFlow<List<RecordingStatus>>()
    val sensorStatusFlow: SharedFlow<List<RecordingStatus>> = _sensorStatusFlow.asSharedFlow()
    
    private val _errorFlow = MutableSharedFlow<RecordingControllerError>()
    val errorFlow: SharedFlow<RecordingControllerError> = _errorFlow.asSharedFlow()
    
    private val _syncEventFlow = MutableSharedFlow<SyncEvent>()
    val syncEventFlow: SharedFlow<SyncEvent> = _syncEventFlow.asSharedFlow()
    
    private val _healthStatusFlow = MutableSharedFlow<List<SensorHealth>>()
    val healthStatusFlow: SharedFlow<List<SensorHealth>> = _healthStatusFlow.asSharedFlow()

    /**
     * Initialize all sensor recorders using configuration
     */
    suspend fun initializeSensors(hubIpAddress: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Initializing sensor recorders with configuration")
                
                // Load configuration
                val config = configurationManager.loadConfiguration()
                this@RecordingController.hubIpAddress = hubIpAddress
                
                // Initialize time synchronization if hub address provided
                if (hubIpAddress != null) {
                    Log.i(TAG, "Initializing time synchronization with hub: $hubIpAddress")
                    val syncSuccess = timeSyncService.initialize(hubIpAddress)
                    if (!syncSuccess) {
                        Log.w(TAG, "Time synchronization failed, continuing without sync")
                    }
                }
                
                // Create sensor recorders with configuration
                val sensorSpecs = listOf(
                    SensorSpec("rgb_camera_1", SensorType.RGB_CAMERA) { 
                        RgbCameraRecorder(context, lifecycleOwner, "rgb_camera_1").apply {
                            configureFromSettings(config.rgbCamera)
                        }
                    },
                    SensorSpec("thermal_camera_1", SensorType.THERMAL_CAMERA) { 
                        ThermalCameraRecorder(
                            context, 
                            "thermal_camera_1",
                            config.thermalCamera.frameRate,
                            Pair(config.thermalCamera.resolution.width, config.thermalCamera.resolution.height)
                        ).apply {
                            updateCalibration(
                                config.thermalCamera.ambientTemperature,
                                config.thermalCamera.emissivity,
                                config.thermalCamera.reflectedTemperature
                            )
                        }
                    },
                    SensorSpec("gsr_shimmer_1", SensorType.GSR_SENSOR) { 
                        GSRSensorRecorder(context, "gsr_shimmer_1").apply {
                            configureSamplingRate(config.gsrSensor.samplingRate)
                        }
                    }
                )
                
                // Initialize sensors with enhanced error handling
                val initResults = sensorSpecs.map { spec ->
                    async {
                        try {
                            Log.i(TAG, "Initializing ${spec.sensorType} sensor: ${spec.sensorId}")
                            val sensor = spec.factory()
                            val success = sensor.initialize()
                            
                            if (success) {
                                sensorRecorders[spec.sensorId] = sensor
                                updateSensorHealth(spec.sensorId, SensorHealthStatus.HEALTHY, "Initialized successfully")
                                Log.i(TAG, "Sensor ${spec.sensorId} initialized successfully")
                                InitResult(spec.sensorId, true, null)
                            } else {
                                updateSensorHealth(spec.sensorId, SensorHealthStatus.FAILED, "Initialization failed")
                                Log.w(TAG, "Sensor ${spec.sensorId} failed to initialize")
                                InitResult(spec.sensorId, false, "Initialization failed")
                            }
                        } catch (e: Exception) {
                            updateSensorHealth(spec.sensorId, SensorHealthStatus.ERROR, "Init error: ${e.message}")
                            Log.e(TAG, "Error initializing sensor ${spec.sensorId}", e)
                            InitResult(spec.sensorId, false, e.message)
                        }
                    }
                }.awaitAll()
                
                // Process results and setup monitoring
                val successfulInits = initResults.filter { it.success }
                val failedInits = initResults.filter { !it.success }
                
                successfulInits.forEach { result ->
                    recoveryAttempts[result.sensorId] = 0
                }
                
                failedInits.forEach { result ->
                    emitError(RecordingControllerError(
                        errorType = "SENSOR_INIT_FAILED",
                        message = "Failed to initialize sensor: ${result.sensorId} - ${result.errorMessage}",
                        sensorId = result.sensorId,
                        isRecoverable = true
                    ))
                }
                
                // Start monitoring systems
                startEnhancedMonitoring()
                
                val successCount = successfulInits.size
                val totalCount = initResults.size
                
                Log.i(TAG, "Sensor initialization complete: $successCount/$totalCount sensors ready")
                
                // Return true if at least one sensor is available
                successCount > 0
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize sensors", e)
                emitError(RecordingControllerError(
                    errorType = "INIT_FAILED",
                    message = "Sensor initialization failed: ${e.message}",
                    isRecoverable = false
                ))
                false
            }
        }
    }

    /**
     * Start recording on all available sensors with configuration-driven parameters
     */
    suspend fun startRecording(sessionDirectory: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (_isRecording.get()) {
                    Log.w(TAG, "Recording already in progress")
                    return@withContext true
                }
                
                Log.i(TAG, "Starting multi-modal recording with enhanced coordination")
                _recordingStateFlow.value = RecordingState.STARTING
                
                // Validate session directory
                val sessionDir = File(sessionDirectory)
                if (!sessionDir.exists()) {
                    sessionDir.mkdirs()
                }
                
                currentSessionDirectory = sessionDirectory
                recordingStartTime = if (timeSyncService.isSynchronized()) {
                    timeSyncService.getSynchronizedTimestamp()
                } else {
                    System.nanoTime()
                }
                
                // Perform health check before starting
                performHealthCheck()
                
                // Filter healthy sensors for recording
                val healthySensors = sensorRecorders.filter { (sensorId, _) ->
                    sensorHealthStatus[sensorId]?.status == SensorHealthStatus.HEALTHY
                }
                
                if (healthySensors.isEmpty()) {
                    Log.e(TAG, "No healthy sensors available for recording")
                    _recordingStateFlow.value = RecordingState.ERROR
                    return@withContext false
                }
                
                Log.i(TAG, "Starting recording with ${healthySensors.size} healthy sensors")
                
                // Start all healthy sensors concurrently
                val startJobs = healthySensors.map { (sensorId, sensor) ->
                    async {
                        try {
                            Log.d(TAG, "Starting sensor: $sensorId")
                            val success = sensor.startRecording(sessionDirectory)
                            if (success) {
                                updateSensorHealth(sensorId, SensorHealthStatus.RECORDING, "Recording started")
                            } else {
                                updateSensorHealth(sensorId, SensorHealthStatus.FAILED, "Failed to start recording")
                            }
                            sensorId to success
                        } catch (e: Exception) {
                            Log.e(TAG, "Error starting sensor $sensorId", e)
                            updateSensorHealth(sensorId, SensorHealthStatus.ERROR, "Start error: ${e.message}")
                            sensorId to false
                        }
                    }
                }
                
                val startResults = startJobs.awaitAll()
                val successfulStarts = startResults.filter { it.second }
                val failedStarts = startResults.filter { !it.second }
                
                // Log results
                successfulStarts.forEach { (sensorId, _) ->
                    Log.i(TAG, "Sensor $sensorId started successfully")
                }
                
                failedStarts.forEach { (sensorId, _) ->
                    Log.w(TAG, "Sensor $sensorId failed to start")
                    emitError(RecordingControllerError(
                        errorType = "SENSOR_START_FAILED",
                        message = "Failed to start sensor: $sensorId",
                        sensorId = sensorId,
                        isRecoverable = true
                    ))
                    
                    // Attempt immediate recovery for failed starts
                    scheduleRecovery(sensorId)
                }
                
                if (successfulStarts.isNotEmpty()) {
                    _isRecording.set(true)
                    _recordingStateFlow.value = RecordingState.RECORDING
                    
                    // Add initial sync marker with timestamp
                    addSyncMarker("session_start", recordingStartTime)
                    
                    Log.i(TAG, "Multi-modal recording started with ${successfulStarts.size} sensors")
                    true
                } else {
                    _recordingStateFlow.value = RecordingState.ERROR
                    Log.e(TAG, "All sensors failed to start")
                    false
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start recording", e)
                _recordingStateFlow.value = RecordingState.ERROR
                emitError(RecordingControllerError(
                    errorType = "START_FAILED",
                    message = "Failed to start recording: ${e.message}",
                    isRecoverable = true
                ))
                false
            }
        }
    }

    /**
     * Enhanced monitoring with health checks and recovery
     */
    private fun startEnhancedMonitoring() {
        // Start existing monitoring
        startMonitoring()
        
        // Add health monitoring
        healthCheckJob = controllerScope.launch {
            while (isActive) {
                try {
                    delay(HEALTH_CHECK_INTERVAL_MS)
                    performHealthCheck()
                    emitHealthStatus()
                } catch (e: Exception) {
                    Log.w(TAG, "Health check error", e)
                }
            }
        }
    }
    
    /**
     * Perform comprehensive health check on all sensors
     */
    private suspend fun performHealthCheck() {
        sensorRecorders.forEach { (sensorId, sensor) ->
            try {
                val isHealthy = when {
                    !sensor.isRecording && _isRecording.get() -> {
                        Log.w(TAG, "Sensor $sensorId stopped recording unexpectedly")
                        false
                    }
                    else -> true
                }
                
                if (isHealthy) {
                    updateSensorHealth(sensorId, SensorHealthStatus.HEALTHY, "Health check passed")
                } else {
                    updateSensorHealth(sensorId, SensorHealthStatus.DEGRADED, "Health check failed")
                    scheduleRecovery(sensorId)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Health check failed for sensor $sensorId", e)
                updateSensorHealth(sensorId, SensorHealthStatus.ERROR, "Health check error: ${e.message}")
                scheduleRecovery(sensorId)
            }
        }
    }
    
    /**
     * Schedule recovery for a failed sensor
     */
    private suspend fun scheduleRecovery(sensorId: String) {
        val attempts = recoveryAttempts.getOrPut(sensorId) { 0 }
        
        if (attempts >= MAX_RECOVERY_ATTEMPTS) {
            Log.w(TAG, "Max recovery attempts reached for sensor $sensorId")
            updateSensorHealth(sensorId, SensorHealthStatus.FAILED, "Max recovery attempts exceeded")
            return
        }
        
        recoveryAttempts[sensorId] = attempts + 1
        
        controllerScope.launch {
            try {
                delay(ERROR_RECOVERY_DELAY_MS * (attempts + 1)) // Exponential backoff
                attemptSensorRecovery(sensorId)
            } catch (e: Exception) {
                Log.e(TAG, "Recovery scheduling failed for sensor $sensorId", e)
            }
        }
    }
    
    /**
     * Attempt to recover a failed sensor
     */
    private suspend fun attemptSensorRecovery(sensorId: String) {
        val sensor = sensorRecorders[sensorId] ?: return
        val attempts = recoveryAttempts[sensorId] ?: 0
        
        Log.i(TAG, "Attempting recovery for sensor $sensorId (attempt ${attempts}/$MAX_RECOVERY_ATTEMPTS)")
        updateSensorHealth(sensorId, SensorHealthStatus.RECOVERING, "Recovery attempt $attempts")
        
        try {
            // Stop sensor if it's still running
            if (sensor.isRecording) {
                sensor.stopRecording()
                delay(1000) // Wait for cleanup
            }
            
            // Reinitialize
            val reinitSuccess = sensor.initialize()
            if (!reinitSuccess) {
                throw Exception("Reinitialization failed")
            }
            
            // Restart recording if session is active
            if (_isRecording.get() && currentSessionDirectory != null) {
                val restartSuccess = sensor.startRecording(currentSessionDirectory!!)
                if (!restartSuccess) {
                    throw Exception("Recording restart failed")
                }
                updateSensorHealth(sensorId, SensorHealthStatus.RECORDING, "Recovery successful - recording")
            } else {
                updateSensorHealth(sensorId, SensorHealthStatus.HEALTHY, "Recovery successful - ready")
            }
            
            // Reset recovery attempts on success
            recoveryAttempts[sensorId] = 0
            
            Log.i(TAG, "Sensor $sensorId recovery successful")
            
            emitError(RecordingControllerError(
                errorType = "RECOVERY_SUCCESS",
                message = "Sensor $sensorId recovered successfully",
                sensorId = sensorId,
                isRecoverable = true
            ))
            
        } catch (e: Exception) {
            Log.e(TAG, "Recovery failed for sensor $sensorId", e)
            updateSensorHealth(sensorId, SensorHealthStatus.FAILED, "Recovery failed: ${e.message}")
            
            emitError(RecordingControllerError(
                errorType = "RECOVERY_FAILED",
                message = "Recovery failed for sensor $sensorId: ${e.message}",
                sensorId = sensorId,
                isRecoverable = attempts < MAX_RECOVERY_ATTEMPTS
            ))
        }
    }
    
    /**
     * Update sensor health status
     */
    private fun updateSensorHealth(sensorId: String, status: SensorHealthStatus, message: String) {
        val health = SensorHealth(
            sensorId = sensorId,
            status = status,
            message = message,
            lastUpdate = System.nanoTime(),
            recoveryAttempts = recoveryAttempts[sensorId] ?: 0
        )
        sensorHealthStatus[sensorId] = health
    }
    
    /**
     * Emit health status updates
     */
    private suspend fun emitHealthStatus() {
        val healthList = sensorHealthStatus.values.toList()
        _healthStatusFlow.emit(healthList)
    }

    /**
     * Stop recording on all sensors
     */
    suspend fun stopRecording(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!_isRecording.get()) {
                    Log.w(TAG, "No recording in progress")
                    return@withContext true
                }
                
                Log.i(TAG, "Stopping multi-modal recording")
                _recordingStateFlow.value = RecordingState.STOPPING
                
                // Add final sync marker
                val stopTime = if (timeSyncService.isSynchronized()) {
                    timeSyncService.getSynchronizedTimestamp()
                } else {
                    System.nanoTime()
                }
                addSyncMarker("session_end", stopTime)
                
                // Wait a moment for sync marker to propagate
                delay(SYNC_MARKER_DISTRIBUTION_DELAY_MS)
                
                // Stop all sensors concurrently
                val stopJobs = sensorRecorders.values.map { sensor ->
                    async {
                        val success = sensor.stopRecording()
                        sensor.sensorId to success
                    }
                }
                
                val stopResults = stopJobs.awaitAll()
                val successfulStops = stopResults.filter { it.second }
                val failedStops = stopResults.filter { !it.second }
                
                // Update health status for stopped sensors
                successfulStops.forEach { (sensorId, _) ->
                    Log.i(TAG, "Sensor $sensorId stopped successfully")
                    updateSensorHealth(sensorId, SensorHealthStatus.HEALTHY, "Stopped successfully")
                }
                
                failedStops.forEach { (sensorId, _) ->
                    Log.w(TAG, "Sensor $sensorId failed to stop cleanly")
                    updateSensorHealth(sensorId, SensorHealthStatus.DEGRADED, "Stop failed")
                }
                
                _isRecording.set(false)
                _recordingStateFlow.value = RecordingState.STOPPED
                
                val sessionDuration = (stopTime - recordingStartTime) / 1_000_000_000.0
                Log.i(TAG, "Multi-modal recording stopped (duration: ${sessionDuration}s)")
                
                true
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop recording", e)
                _recordingStateFlow.value = RecordingState.ERROR
                emitError(RecordingControllerError(
                    errorType = "STOP_FAILED",
                    message = "Failed to stop recording: ${e.message}",
                    isRecoverable = true
                ))
                false
            }
        }
    }

    /**
     * Add synchronization marker to all sensors with enhanced timing
     */
    suspend fun addSyncMarker(markerType: String, timestampNs: Long, metadata: Map<String, String> = emptyMap()) {
        controllerScope.launch {
            try {
                val syncTimestamp = if (timeSyncService.isSynchronized()) {
                    timeSyncService.getSynchronizedTimestamp()
                } else {
                    timestampNs
                }
                
                Log.i(TAG, "Distributing sync marker: $markerType at $syncTimestamp")
                
                // Add time synchronization metadata
                val enhancedMetadata = metadata.toMutableMap().apply {
                    put("sync_quality", timeSyncService.getSyncQuality().toString())
                    put("clock_offset_ms", timeSyncService.getClockOffsetMs().toString())
                    put("network_latency_ms", timeSyncService.getNetworkLatencyMs().toString())
                }
                
                // Distribute sync marker to all active sensors
                val syncJobs = sensorRecorders.values.map { sensor ->
                    async {
                        try {
                            sensor.addSyncMarker(markerType, syncTimestamp, enhancedMetadata)
                            sensor.sensorId to true
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to add sync marker to ${sensor.sensorId}", e)
                            sensor.sensorId to false
                        }
                    }
                }
                
                val syncResults = syncJobs.awaitAll()
                val successfulSyncs = syncResults.count { it.second }
                val totalSensors = syncResults.size
                
                // Emit sync event
                val syncEvent = SyncEvent(
                    markerType = markerType,
                    timestampNs = syncTimestamp,
                    metadata = enhancedMetadata,
                    successfulSensors = successfulSyncs,
                    totalSensors = totalSensors
                )
                _syncEventFlow.emit(syncEvent)
                
                Log.i(TAG, "Sync marker distributed: $successfulSyncs/$totalSensors sensors")
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to distribute sync marker", e)
            }
        }
    }

    /**
     * Get current recording statistics for all sensors
     */
    fun getRecordingStatistics(): RecordingStatistics {
        val sensorStats = sensorRecorders.values.map { it.getRecordingStats() }
        val totalSamples = sensorStats.sumOf { it.totalSamplesRecorded }
        val totalStorage = sensorStats.sumOf { it.storageUsedMB }
        val totalDropped = sensorStats.sumOf { it.droppedSamples }
        
        val sessionDuration = if (recordingStartTime > 0) {
            val currentTime = if (timeSyncService.isSynchronized()) {
                timeSyncService.getSynchronizedTimestamp()
            } else {
                System.nanoTime()
            }
            (currentTime - recordingStartTime) / 1_000_000_000.0
        } else 0.0
        
        return RecordingStatistics(
            isRecording = _isRecording.get(),
            sessionDurationSeconds = sessionDuration,
            activeSensors = sensorRecorders.size,
            totalSamplesRecorded = totalSamples,
            totalStorageUsedMB = totalStorage,
            totalDroppedSamples = totalDropped,
            sensorStatistics = sensorStats,
            syncQuality = timeSyncService.getSyncQuality(),
            clockOffsetMs = timeSyncService.getClockOffsetMs(),
            networkLatencyMs = timeSyncService.getNetworkLatencyMs()
        )
    }

    /**
     * Get list of available sensors and their health status
     */
    fun getAvailableSensors(): List<SensorInfo> {
        return sensorRecorders.values.map { sensor ->
            val health = sensorHealthStatus[sensor.sensorId]
            SensorInfo(
                sensorId = sensor.sensorId,
                sensorType = sensor.sensorType,
                isRecording = sensor.isRecording,
                samplingRate = sensor.samplingRate,
                healthStatus = health?.status ?: SensorHealthStatus.UNKNOWN,
                healthMessage = health?.message ?: "Status unknown"
            )
        }
    }

    /**
     * Clean up all resources
     */
    suspend fun cleanup() {
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Cleaning up enhanced recording controller")
                
                // Stop recording if active
                if (_isRecording.get()) {
                    stopRecording()
                }
                
                // Stop monitoring
                statusMonitoringJob?.cancel()
                errorMonitoringJob?.cancel()
                healthCheckJob?.cancel()
                
                // Shutdown time synchronization
                timeSyncService.shutdown()
                
                // Cleanup all sensors
                val cleanupJobs = sensorRecorders.values.map { sensor ->
                    async {
                        try {
                            sensor.cleanup()
                            Log.d(TAG, "Sensor ${sensor.sensorId} cleaned up")
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to cleanup sensor ${sensor.sensorId}", e)
                        }
                    }
                }
                
                cleanupJobs.awaitAll()
                sensorRecorders.clear()
                sensorHealthStatus.clear()
                recoveryAttempts.clear()
                
                // Cancel controller scope
                controllerScope.cancel()
                
                Log.i(TAG, "Enhanced recording controller cleanup complete")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error during cleanup", e)
            }
        }
    }

    private fun startMonitoring() {
        // Original monitoring implementation (simplified for space)
        statusMonitoringJob = controllerScope.launch {
            while (isActive) {
                try {
                    val statusList = sensorRecorders.values.map { sensor ->
                        sensor.getRecordingStats().let { stats ->
                            RecordingStatus(
                                sensorId = stats.sensorId,
                                sensorType = stats.sensorType,
                                isRecording = sensor.isRecording,
                                samplesRecorded = stats.totalSamplesRecorded,
                                currentDataRate = stats.averageDataRate,
                                storageUsedMB = stats.storageUsedMB,
                                timestampNs = System.nanoTime()
                            )
                        }
                    }
                    
                    _sensorStatusFlow.emit(statusList)
                    
                } catch (e: Exception) {
                    Log.w(TAG, "Status monitoring error", e)
                }
                
                delay(STATUS_UPDATE_INTERVAL_MS)
            }
        }
        
        // Enhanced error monitoring
        errorMonitoringJob = controllerScope.launch {
            sensorRecorders.values.forEach { sensor ->
                launch {
                    sensor.getErrorFlow().collect { sensorError ->
                        Log.w(TAG, "Sensor error: ${sensorError.sensorId} - ${sensorError.errorMessage}")
                        
                        val controllerError = RecordingControllerError(
                            errorType = "SENSOR_ERROR",
                            message = sensorError.errorMessage,
                            sensorId = sensorError.sensorId,
                            isRecoverable = sensorError.isRecoverable,
                            originalError = sensorError
                        )
                        
                        emitError(controllerError)
                        
                        // Update health status
                        updateSensorHealth(sensorError.sensorId, SensorHealthStatus.ERROR, sensorError.errorMessage)
                        
                        // Attempt recovery for recoverable errors
                        if (sensorError.isRecoverable) {
                            scheduleRecovery(sensorError.sensorId)
                        }
                    }
                }
            }
        }
    }

    private suspend fun emitError(error: RecordingControllerError) {
        _errorFlow.emit(error)
    }
}

// Data classes and enums for enhanced functionality

data class SensorSpec(
    val sensorId: String,
    val sensorType: SensorType,
    val factory: () -> SensorRecorder
)

data class InitResult(
    val sensorId: String,
    val success: Boolean,
    val errorMessage: String?
)

enum class SensorType {
    RGB_CAMERA,
    THERMAL_CAMERA,
    GSR_SENSOR
}

/**
 * Recording states for the controller
 */
enum class RecordingState {
    STOPPED,
    STARTING,
    RECORDING,
    STOPPING,
    ERROR
}

/**
 * Enhanced sensor health status
 */
enum class SensorHealthStatus {
    UNKNOWN,
    HEALTHY,
    RECORDING,
    DEGRADED,
    RECOVERING,
    FAILED,
    ERROR
}

/**
 * Sensor health information
 */
data class SensorHealth(
    val sensorId: String,
    val status: SensorHealthStatus,
    val message: String,
    val lastUpdate: Long,
    val recoveryAttempts: Int
)

/**
 * Error information from the recording controller
 */
data class RecordingControllerError(
    val errorType: String,
    val message: String,
    val sensorId: String? = null,
    val isRecoverable: Boolean = true,
    val timestampNs: Long = System.nanoTime(),
    val originalError: SensorError? = null
)

/**
 * Synchronization event information
 */
data class SyncEvent(
    val markerType: String,
    val timestampNs: Long,
    val metadata: Map<String, String>,
    val successfulSensors: Int,
    val totalSensors: Int
)

/**
 * Enhanced recording statistics with sync info
 */
data class RecordingStatistics(
    val isRecording: Boolean,
    val sessionDurationSeconds: Double,
    val activeSensors: Int,
    val totalSamplesRecorded: Long,
    val totalStorageUsedMB: Double,
    val totalDroppedSamples: Long,
    val sensorStatistics: List<RecordingStats>,
    val syncQuality: Int = 0,
    val clockOffsetMs: Double = 0.0,
    val networkLatencyMs: Double = 0.0
)

/**
 * Enhanced sensor information for UI display
 */
data class SensorInfo(
    val sensorId: String,
    val sensorType: String,
    val isRecording: Boolean,
    val samplingRate: Double,
    val healthStatus: SensorHealthStatus,
    val healthMessage: String
)

    /**
     * Start recording on all available sensors
     */
    suspend fun startRecording(sessionDirectory: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (_isRecording.get()) {
                    Log.w(TAG, "Recording already in progress")
                    return@withContext true
                }
                
                Log.i(TAG, "Starting multi-modal recording")
                _recordingStateFlow.value = RecordingState.STARTING
                
                // Create session directory
                val sessionDir = File(sessionDirectory)
                if (!sessionDir.exists()) {
                    sessionDir.mkdirs()
                }
                
                currentSessionDirectory = sessionDirectory
                recordingStartTime = System.nanoTime()
                
                // Start all sensors concurrently
                val startJobs = sensorRecorders.values.map { sensor ->
                    async {
                        val success = sensor.startRecording(sessionDirectory)
                        sensor.sensorId to success
                    }
                }
                
                val startResults = startJobs.awaitAll()
                val successfulStarts = startResults.filter { it.second }
                val failedStarts = startResults.filter { !it.second }
                
                // Log results
                successfulStarts.forEach { (sensorId, _) ->
                    Log.i(TAG, "Sensor $sensorId started successfully")
                }
                
                failedStarts.forEach { (sensorId, _) ->
                    Log.w(TAG, "Sensor $sensorId failed to start")
                    emitError(RecordingControllerError(
                        errorType = "SENSOR_START_FAILED",
                        message = "Failed to start sensor: $sensorId",
                        sensorId = sensorId,
                        isRecoverable = true
                    ))
                }
                
                if (successfulStarts.isNotEmpty()) {
                    _isRecording.set(true)
                    _recordingStateFlow.value = RecordingState.RECORDING
                    
                    // Add initial sync marker
                    addSyncMarker("session_start", recordingStartTime)
                    
                    Log.i(TAG, "Multi-modal recording started with ${successfulStarts.size} sensors")
                    true
                } else {
                    _recordingStateFlow.value = RecordingState.ERROR
                    Log.e(TAG, "All sensors failed to start")
                    false
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start recording", e)
                _recordingStateFlow.value = RecordingState.ERROR
                emitError(RecordingControllerError(
                    errorType = "START_FAILED",
                    message = "Failed to start recording: ${e.message}",
                    isRecoverable = true
                ))
                false
            }
        }
    }

    /**
     * Stop recording on all sensors
     */
    suspend fun stopRecording(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!_isRecording.get()) {
                    Log.w(TAG, "No recording in progress")
                    return@withContext true
                }
                
                Log.i(TAG, "Stopping multi-modal recording")
                _recordingStateFlow.value = RecordingState.STOPPING
                
                // Add final sync marker
                addSyncMarker("session_end", System.nanoTime())
                
                // Wait a moment for sync marker to propagate
                delay(SYNC_MARKER_DISTRIBUTION_DELAY_MS)
                
                // Stop all sensors concurrently
                val stopJobs = sensorRecorders.values.map { sensor ->
                    async {
                        val success = sensor.stopRecording()
                        sensor.sensorId to success
                    }
                }
                
                val stopResults = stopJobs.awaitAll()
                val successfulStops = stopResults.filter { it.second }
                val failedStops = stopResults.filter { !it.second }
                
                // Log results
                successfulStops.forEach { (sensorId, _) ->
                    Log.i(TAG, "Sensor $sensorId stopped successfully")
                }
                
                failedStops.forEach { (sensorId, _) ->
                    Log.w(TAG, "Sensor $sensorId failed to stop cleanly")
                }
                
                _isRecording.set(false)
                _recordingStateFlow.value = RecordingState.STOPPED
                
                val sessionDuration = (System.nanoTime() - recordingStartTime) / 1_000_000_000.0
                Log.i(TAG, "Multi-modal recording stopped (duration: ${sessionDuration}s)")
                
                true
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop recording", e)
                _recordingStateFlow.value = RecordingState.ERROR
                emitError(RecordingControllerError(
                    errorType = "STOP_FAILED",
                    message = "Failed to stop recording: ${e.message}",
                    isRecoverable = true
                ))
                false
            }
        }
    }

    /**
     * Add synchronization marker to all sensors
     */
    suspend fun addSyncMarker(markerType: String, timestampNs: Long, metadata: Map<String, String> = emptyMap()) {
        controllerScope.launch {
            try {
                Log.i(TAG, "Distributing sync marker: $markerType at $timestampNs")
                
                // Distribute sync marker to all active sensors
                val syncJobs = sensorRecorders.values.map { sensor ->
                    async {
                        try {
                            sensor.addSyncMarker(markerType, timestampNs, metadata)
                            sensor.sensorId to true
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to add sync marker to ${sensor.sensorId}", e)
                            sensor.sensorId to false
                        }
                    }
                }
                
                val syncResults = syncJobs.awaitAll()
                val successfulSyncs = syncResults.count { it.second }
                val totalSensors = syncResults.size
                
                // Emit sync event
                val syncEvent = SyncEvent(
                    markerType = markerType,
                    timestampNs = timestampNs,
                    metadata = metadata,
                    successfulSensors = successfulSyncs,
                    totalSensors = totalSensors
                )
                _syncEventFlow.emit(syncEvent)
                
                Log.i(TAG, "Sync marker distributed: $successfulSyncs/$totalSensors sensors")
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to distribute sync marker", e)
            }
        }
    }

    /**
     * Get current recording statistics for all sensors
     */
    fun getRecordingStatistics(): RecordingStatistics {
        val sensorStats = sensorRecorders.values.map { it.getRecordingStats() }
        val totalSamples = sensorStats.sumOf { it.totalSamplesRecorded }
        val totalStorage = sensorStats.sumOf { it.storageUsedMB }
        val totalDropped = sensorStats.sumOf { it.droppedSamples }
        
        val sessionDuration = if (recordingStartTime > 0) {
            (System.nanoTime() - recordingStartTime) / 1_000_000_000.0
        } else 0.0
        
        return RecordingStatistics(
            isRecording = _isRecording.get(),
            sessionDurationSeconds = sessionDuration,
            activeSensors = sensorRecorders.size,
            totalSamplesRecorded = totalSamples,
            totalStorageUsedMB = totalStorage,
            totalDroppedSamples = totalDropped,
            sensorStatistics = sensorStats
        )
    }

    /**
     * Get list of available sensors and their status
     */
    fun getAvailableSensors(): List<SensorInfo> {
        return sensorRecorders.values.map { sensor ->
            SensorInfo(
                sensorId = sensor.sensorId,
                sensorType = sensor.sensorType,
                isRecording = sensor.isRecording,
                samplingRate = sensor.samplingRate
            )
        }
    }

    /**
     * Clean up all resources
     */
    suspend fun cleanup() {
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Cleaning up recording controller")
                
                // Stop recording if active
                if (_isRecording.get()) {
                    stopRecording()
                }
                
                // Stop monitoring
                statusMonitoringJob?.cancel()
                errorMonitoringJob?.cancel()
                
                // Cleanup all sensors
                val cleanupJobs = sensorRecorders.values.map { sensor ->
                    async {
                        try {
                            sensor.cleanup()
                            Log.d(TAG, "Sensor ${sensor.sensorId} cleaned up")
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to cleanup sensor ${sensor.sensorId}", e)
                        }
                    }
                }
                
                cleanupJobs.awaitAll()
                sensorRecorders.clear()
                
                // Cancel controller scope
                controllerScope.cancel()
                
                Log.i(TAG, "Recording controller cleanup complete")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error during cleanup", e)
            }
        }
    }

    private fun startMonitoring() {
        // Start status monitoring
        statusMonitoringJob = controllerScope.launch {
            while (isActive) {
                try {
                    val statusList = sensorRecorders.values.map { sensor ->
                        sensor.getRecordingStats().let { stats ->
                            RecordingStatus(
                                sensorId = stats.sensorId,
                                sensorType = stats.sensorType,
                                isRecording = sensor.isRecording,
                                samplesRecorded = stats.totalSamplesRecorded,
                                currentDataRate = stats.averageDataRate,
                                storageUsedMB = stats.storageUsedMB,
                                timestampNs = System.nanoTime()
                            )
                        }
                    }
                    
                    _sensorStatusFlow.emit(statusList)
                    
                } catch (e: Exception) {
                    Log.w(TAG, "Status monitoring error", e)
                }
                
                delay(STATUS_UPDATE_INTERVAL_MS)
            }
        }
        
        // Start error monitoring
        errorMonitoringJob = controllerScope.launch {
            sensorRecorders.values.forEach { sensor ->
                launch {
                    sensor.getErrorFlow().collect { sensorError ->
                        Log.w(TAG, "Sensor error: ${sensorError.sensorId} - ${sensorError.errorMessage}")
                        
                        val controllerError = RecordingControllerError(
                            errorType = "SENSOR_ERROR",
                            message = sensorError.errorMessage,
                            sensorId = sensorError.sensorId,
                            isRecoverable = sensorError.isRecoverable,
                            originalError = sensorError
                        )
                        
                        emitError(controllerError)
                        
                        // Attempt recovery for recoverable errors
                        if (sensorError.isRecoverable) {
                            attemptErrorRecovery(sensor, sensorError)
                        }
                    }
                }
            }
        }
    }

    private suspend fun attemptErrorRecovery(sensor: SensorRecorder, error: SensorError) {
        controllerScope.launch {
            try {
                Log.i(TAG, "Attempting error recovery for sensor ${sensor.sensorId}")
                
                delay(ERROR_RECOVERY_DELAY_MS)
                
                // Simple recovery: reinitialize the sensor
                val recoverySuccess = sensor.initialize()
                
                if (recoverySuccess) {
                    Log.i(TAG, "Error recovery successful for sensor ${sensor.sensorId}")
                    
                    // Restart recording if session is active
                    if (_isRecording.get() && currentSessionDirectory != null) {
                        sensor.startRecording(currentSessionDirectory!!)
                    }
                } else {
                    Log.w(TAG, "Error recovery failed for sensor ${sensor.sensorId}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error during recovery attempt", e)
            }
        }
    }

    private suspend fun emitError(error: RecordingControllerError) {
        _errorFlow.emit(error)
    }
}
