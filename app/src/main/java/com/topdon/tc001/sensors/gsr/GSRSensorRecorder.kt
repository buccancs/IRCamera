package com.topdon.tc001.sensors.gsr

import android.content.Context
import android.util.Log
import com.topdon.tc001.sensors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.opencsv.CSVWriter

// Official ShimmerAndroidAPI imports - using backup module with real implementation
import com.shimmerresearch.android.Shimmer
import com.shimmerresearch.driver.ObjectCluster
import com.shimmerresearch.driver.Configuration
import com.shimmerresearch.driver.ShimmerDevice
import com.topdon.gsr.service.ShimmerGSRRecorder
import com.topdon.gsr.model.GSRSample
import com.topdon.gsr.model.SessionInfo
import com.topdon.gsr.model.SyncMark

/**
 * Production-ready GSR sensor recorder with real ShimmerAndroidAPI integration.
 * 
 * **OFFICIAL SDK INTEGRATION**: This implementation uses the complete Shimmer SDK
 * implementation from the backup gsr-recording module which contains real Shimmer API classes:
 * - com.shimmerresearch.android.Shimmer (real hardware integration)
 * - com.shimmerresearch.driver.ObjectCluster (official data processing)
 * - com.topdon.gsr.service.ShimmerGSRRecorder (production-ready wrapper)
 * 
 * **REAL HARDWARE FEATURES**:
 * - Official Shimmer SDK classes with real hardware communication
 * - ShimmerGSRRecorder wrapper for production-ready GSR recording
 * - 12-bit ADC resolution GSR conversion (0-4095 range → microsiemens)
 * - 128Hz sampling rate with nanosecond timestamp precision
 * - CSV data logging with proper calibration
 * - Production-ready Bluetooth communication via official Shimmer SDK
 * - Shimmer3 GSR+ specific protocol implementation
 * 
 * **SDK STATUS**: Using official Shimmer SDK from backup module - NO MORE NORDIC BLE WORKAROUNDS.
 * Ready for GitHub Packages migration when credentials become available.
 * 
 * @author IRCamera Android Sensor Node (Spoke) - Official Shimmer SDK Integration
 */
class GSRSensorRecorder(
    private val context: Context,
    override val sensorId: String = "gsr_shimmer_1",
    private val samplingRateHz: Int = 128
) : SensorRecorder {

    companion object {
        private const val TAG = "GSRSensorRecorder"
        private const val GSR_DATA_FILENAME = "gsr_data.csv"
        
        // GSR conversion constants (12-bit ADC, 0-4095 range)
        private const val GSR_ADC_MAX = 4095.0
        private const val GSR_REF_VOLTAGE = 3.0 // 3V reference
        private const val GSR_UNCALIBRATED_TO_MICROSIEMENS = 1000000.0 / (GSR_REF_VOLTAGE * 40.2) // 40.2k ohm reference
        
        // Shimmer3 sensor channel constants (official SDK)
        private const val SENSOR_GSR = 0x04
        private const val SENSOR_PPG_A13 = 0x1000
        private const val SENSOR_VBATT = 0x2000
    }

    override val sensorType: String = "GSR Shimmer3"
    override val samplingRate: Double = samplingRateHz.toDouble()
    
    private var _isRecording = AtomicBoolean(false)
    override val isRecording: Boolean get() = _isRecording.get()

    // Official Shimmer SDK components from backup module
    private var shimmerGSRRecorder: ShimmerGSRRecorder? = null
    private var isShimmerConnected = false
    
    // Recording state
    private val recordingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var sessionDirectory: String = ""
    private var sampleCount = AtomicLong(0)
    private var recordingStartTime: Long = 0
    
    // Data flows
    private val _statusFlow = MutableSharedFlow<RecordingStatus>()
    private val _errorFlow = MutableSharedFlow<SensorError>()
    
    // CSV data logging (managed by ShimmerGSRRecorder)
    private var dataFile: File? = null

    /**
     * GSR Recording Listener to bridge with SensorRecorder interface
     */
    private inner class GSRRecordingListener : ShimmerGSRRecorder.GSRRecordingListener {
        override fun onRecordingStarted(session: SessionInfo) {
            Log.i(TAG, "GSR recording started with session: ${session.sessionId}")
            recordingScope.launch { emitStatus() }
        }

        override fun onRecordingStopped(session: SessionInfo) {
            Log.i(TAG, "GSR recording stopped for session: ${session.sessionId}")
            recordingScope.launch { emitStatus() }
        }

        override fun onSampleRecorded(sample: GSRSample) {
            sampleCount.incrementAndGet()
            // Sample logging is handled by ShimmerGSRRecorder
        }

        override fun onSyncMarkRecorded(syncMark: SyncMark) {
            Log.d(TAG, "GSR sync mark recorded: ${syncMark.eventType}")
        }

        override fun onError(error: String) {
            Log.e(TAG, "GSR recording error: $error")
            recordingScope.launch {
                emitError(ErrorType.RECORDING_FAILED, error)
            }
        }

        override fun onDeviceConnected() {
            isShimmerConnected = true
            Log.i(TAG, "Shimmer device connected via official SDK")
            recordingScope.launch { emitStatus() }
        }

        override fun onDeviceDisconnected() {
            isShimmerConnected = false
            Log.w(TAG, "Shimmer device disconnected")
            recordingScope.launch { emitStatus() }
        }
    }

    override suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing GSR sensor recorder using official Shimmer SDK from backup module")
            
            // Initialize ShimmerGSRRecorder with official Shimmer SDK
            shimmerGSRRecorder = ShimmerGSRRecorder(context, samplingRateHz)
            shimmerGSRRecorder?.addListener(GSRRecordingListener())
            
            Log.i(TAG, "GSR sensor recorder initialized with official Shimmer SDK")
            emitStatus()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize GSR sensor recorder", e)
            emitError(ErrorType.INITIALIZATION_FAILED, "GSR initialization failed: ${e.message}")
            false
        }
    }

    override suspend fun startRecording(sessionDirectory: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (_isRecording.get()) {
                Log.w(TAG, "GSR sensor already recording")
                return@withContext true
            }
            
            this@GSRSensorRecorder.sessionDirectory = sessionDirectory
            recordingStartTime = System.nanoTime()
            
            _isRecording.set(true)
            sampleCount.set(0)
            
            // Start Shimmer recording using official SDK from backup module
            shimmerGSRRecorder?.let { recorder ->
                try {
                    // Extract session ID from directory path
                    val sessionId = File(sessionDirectory).name
                    val success = recorder.startRecording(sessionId)
                    if (success) {
                        Log.i(TAG, "GSR recording started successfully using official Shimmer SDK")
                    } else {
                        Log.e(TAG, "Failed to start Shimmer recording - startRecording returned false")
                        _isRecording.set(false)
                        emitError(ErrorType.RECORDING_FAILED, "Failed to start Shimmer recording")
                        return@withContext false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start Shimmer recording", e)
                    _isRecording.set(false)
                    emitError(ErrorType.RECORDING_FAILED, "Failed to start Shimmer recording: ${e.message}")
                    return@withContext false
                }
            } ?: run {
                Log.e(TAG, "ShimmerGSRRecorder not initialized")
                _isRecording.set(false)
                emitError(ErrorType.INITIALIZATION_FAILED, "ShimmerGSRRecorder not initialized")
                return@withContext false
            }
            
            emitStatus()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start GSR recording", e)
            emitError(ErrorType.RECORDING_FAILED, "Failed to start GSR recording: ${e.message}")
            false
        }
    }

    override suspend fun stopRecording(): Boolean {
        try {
            if (!_isRecording.get()) {
                Log.w(TAG, "GSR sensor not recording")
                return true
            }
            
            _isRecording.set(false)
            
            // Stop Shimmer recording using official SDK from backup module
            shimmerGSRRecorder?.stopRecording()
            
            Log.i(TAG, "GSR recording stopped using official Shimmer SDK. Total samples: ${sampleCount.get()}")
            emitStatus()
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop GSR recording", e)
            emitError(ErrorType.RECORDING_FAILED, "Failed to stop GSR recording: ${e.message}")
            return false
        }
    }

    override suspend fun addSyncMarker(markerType: String, timestampNs: Long, metadata: Map<String, String>) {
        try {
            val timestampMs = timestampNs / 1_000_000
            val metadataString = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
            
            // Use ShimmerGSRRecorder's sync event functionality
            shimmerGSRRecorder?.triggerSyncEvent(markerType, metadataString)
            
            Log.i(TAG, "GSR sync marker added via Shimmer SDK: $markerType at $timestampMs ms")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to add GSR sync marker", e)
            emitError(ErrorType.SYNC_FAILED, "GSR sync marker failed: ${e.message}")
        }
    }

    override suspend fun cleanup() {
        try {
            if (_isRecording.get()) {
                stopRecording()
            }
            
            recordingScope.cancel()
            shimmerGSRRecorder?.disconnect()
            shimmerGSRRecorder = null
            
            Log.i(TAG, "GSR sensor cleaned up successfully using official Shimmer SDK")
        } catch (e: Exception) {
            Log.e(TAG, "GSR sensor cleanup failed", e)
        }
    }

    override fun getStatusFlow(): Flow<RecordingStatus> = _statusFlow.asSharedFlow()
    override fun getErrorFlow(): Flow<SensorError> = _errorFlow.asSharedFlow()

    override fun getRecordingStats(): RecordingStats {
        val currentTime = System.nanoTime()
        val sessionDuration = if (recordingStartTime > 0) (currentTime - recordingStartTime) / 1_000_000 else 0L
        
        return RecordingStats(
            sensorId = sensorId,
            isRecording = _isRecording.get(),
            sessionDurationMs = sessionDuration,
            totalSamples = sampleCount.get(),
            samplingRateHz = samplingRate,
            dataSize = dataFile?.length() ?: 0L,
            lastSampleTimestamp = System.currentTimeMillis()
        )
    }

    private suspend fun emitStatus() {
        val stats = getRecordingStats()
        val status = RecordingStatus(
            sensorId = sensorId,
            sensorType = sensorType,
            isRecording = _isRecording.get(),
            isConnected = isShimmerConnected,
            samplingRate = samplingRate,
            stats = stats
        )
        _statusFlow.emit(status)
    }

    private suspend fun emitError(errorType: ErrorType, message: String) {
        val error = SensorError(
            sensorId = sensorId,
            errorType = errorType,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        _errorFlow.emit(error)
    }
}