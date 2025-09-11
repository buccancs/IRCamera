package com.topdon.tc001.sensors.thermal

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.opencsv.CSVWriter
import com.topdon.tc001.sensors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileWriter
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

// Latest Topdon SDK imports (libusbirsdk_1.3.7_23051019_standard.aar)
import com.energy.ac020library.IrcamEngine
import com.energy.ac020library.bean.IIrFrameCallback
import com.energy.ac020library.bean.CommonParams
import com.energy.ac020library.bean.DualCommonParams
import com.energy.ac020library.bean.UvcHandleParam

/**
 * Thermal Camera recorder using latest Topdon SDK (libusbirsdk_1.3.7_23051019_standard.aar).
 * 
 * This implementation uses the latest Topdon SDK as provided by the user for TC001 hardware support.
 * Updated from previous libAC020sdk_USB_IR_1.1.1_2408291439.aar to libusbirsdk_1.3.7_23051019_standard.aar.
 * 
 * @author IRCamera Android Sensor Node (Spoke) - Latest Topdon SDK Integration
 */
class ThermalCameraRecorder(
    private val context: Context,
    override val sensorId: String = "thermal_camera_1",
    private val thermalFrameRate: Double = 9.0, // TC001 typical frame rate
    private val thermalResolution: Pair<Int, Int> = Pair(256, 192) // TC001 resolution
) : SensorRecorder {

    companion object {
        private const val TAG = "ThermalCameraRecorder"
        private const val THERMAL_DATA_FILENAME = "thermal_data.csv"
        private const val THERMAL_FRAMES_FILENAME = "thermal_frames.csv"
        private const val CALIBRATION_FILENAME = "thermal_calibration.json"
        
        // IR Camera specific constants (real hardware specs)
        private const val IR_CAMERA_WIDTH = 256 // Real IR camera resolution
        private const val IR_CAMERA_HEIGHT = 192 // Real IR camera resolution
        private const val IR_FRAME_RATE = 9.0 // Typical IR camera frame rate
        
        // Thermal data constants for IR Camera
        private const val TEMPERATURE_OFFSET = 273.15 // Kelvin to Celsius
        private const val THERMAL_SENSITIVITY = 0.1 // Temperature resolution for IR Camera
        private const val IR_TEMP_RANGE_MIN = -20.0f // IR camera minimum temperature
        private const val IR_TEMP_RANGE_MAX = 400.0f // IR camera maximum temperature
    }

    override val sensorType: String = "IR Thermal Camera"
    override val samplingRate: Double = thermalFrameRate
    
    private var _isRecording = AtomicBoolean(false)
    override val isRecording: Boolean get() = _isRecording.get()

    // Real IR Camera SDK components using latest Topdon SDK
    private var ircamEngine: IrcamEngine? = null
    private var uvcHandleParam: UvcHandleParam? = null
    private var isIRCameraConnected = false
    
    // USB management for IR Camera
    private var usbManager: UsbManager? = null
    private var irCameraDevice: UsbDevice? = null
    
    // Recording components
    private val recordingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var csvWriter: CSVWriter? = null
    private var framesCsvWriter: CSVWriter? = null
    
    // Data flows
    private val _statusFlow = MutableSharedFlow<RecordingStatus>()
    private val _errorFlow = MutableSharedFlow<SensorError>()
    
    // Recording state
    private var sessionDirectory: String = ""
    private var frameCount = AtomicLong(0)
    private var recordingStartTime: Long = 0
    private var thermalDataFile: File? = null
    private var thermalFramesFile: File? = null
    
    // Thermal calibration
    private var ambientTemperature = 25.0 // Default ambient temp in Celsius
    private var emissivity = 0.95 // Default emissivity
    private var reflectedTemperature = 23.0 // Default reflected temperature

    override suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing real IR thermal camera using latest Topdon SDK for sensor $sensorId")
            
            // Initialize USB manager for IR camera detection
            usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            
            // Initialize real IR camera using latest Topdon SDK
            val connectionSuccess = initializeRealIRCamera()
            
            if (!connectionSuccess) {
                Log.e(TAG, "Failed to initialize real IR thermal camera")
                emitError(ErrorType.DEVICE_ERROR, "IR thermal camera not found or initialization failed")
                return@withContext false
            }
            
            Log.i(TAG, "Real IR thermal camera initialized successfully using latest Topdon SDK")
            emitStatus()
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize real IR thermal camera", e)
            emitError(ErrorType.INITIALIZATION_FAILED, "Real IR thermal camera initialization failed: ${e.message}")
            return@withContext false
        }
    }
    
    private suspend fun initializeRealIRCamera(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing real IR thermal camera hardware using latest Topdon SDK")
            
            // Initialize IrcamEngine with latest SDK
            ircamEngine = IrcamEngine()
            
            // Initialize UVC handle parameters for the latest SDK
            uvcHandleParam = UvcHandleParam().apply {
                // Set default parameters for TC001
                width = IR_CAMERA_WIDTH
                height = IR_CAMERA_HEIGHT
            }
            
            // Setup thermal frame callback for real-time data processing
            val frameCallback = object : IIrFrameCallback {
                override fun onIrFrame(
                    frameData: ByteArray?,
                    width: Int,
                    height: Int,
                    timestamp: Long
                ) {
                    frameData?.let { data ->
                        if (_isRecording.get()) {
                            recordingScope.launch {
                                processThermalFrame(data, width, height, timestamp)
                            }
                        }
                    }
                }
                
                override fun onError(errorCode: Int, errorMessage: String?) {
                    Log.e(TAG, "Thermal camera frame error: $errorCode - $errorMessage")
                    recordingScope.launch {
                        emitError(ErrorType.HARDWARE_ERROR, "Frame error: $errorMessage")
                    }
                }
            }
            
            // Initialize thermal camera with callback
            val initResult = ircamEngine?.initCamera(context, frameCallback)
            if (initResult != true) {
                Log.e(TAG, "Failed to initialize IrcamEngine")
                return@withContext false
            }
            
            // Search for thermal camera devices
            val deviceConnected = ircamEngine?.connectDevice() ?: false
            if (!deviceConnected) {
                Log.e(TAG, "No thermal camera device found or connection failed")
                return@withContext false
            }
            
            isIRCameraConnected = true
            Log.i(TAG, "Real IR thermal camera initialized successfully using latest Topdon SDK")
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception during IR camera initialization", e)
            return@withContext false
        }
    }
    
    private suspend fun processThermalFrame(frameData: ByteArray, width: Int, height: Int, timestamp: Long) {
        try {
            val timestampNs = System.nanoTime()
            val timestampMs = System.currentTimeMillis()
            
            // Process thermal frame data from latest SDK
            recordingScope.launch {
                logThermalData(timestampNs, timestampMs, frameData, width, height)
                frameCount.incrementAndGet()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error processing thermal frame", e)
        }
    }
    
    private suspend fun logThermalData(timestampNs: Long, timestampMs: Long, thermalData: ByteArray, width: Int, height: Int) {
        try {
            // Calculate thermal statistics for logging
            val temperatures = FloatArray(thermalData.size / 2)
            val buffer = ByteBuffer.wrap(thermalData)
            for (i in temperatures.indices) {
                temperatures[i] = buffer.getShort(i * 2).toFloat() / 100.0f // Convert to Celsius
            }
            
            val minTemp = temperatures.minOrNull() ?: 0.0f
            val maxTemp = temperatures.maxOrNull() ?: 0.0f
            val avgTemp = temperatures.average().toFloat()
            
            // Log thermal data to CSV
            csvWriter?.let { writer ->
                val record = arrayOf(
                    timestampNs.toString(),
                    timestampMs.toString(),
                    frameCount.get().toString(),
                    minTemp.toString(),
                    maxTemp.toString(),
                    avgTemp.toString(),
                    ambientTemperature.toString(),
                    emissivity.toString()
                )
                writer.writeNext(record)
                writer.flush()
            }
            
        } catch (e: Exception) {
            Log.w(TAG, "Failed to log thermal data", e)
        }
    }

    override suspend fun startRecording(sessionDirectory: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (_isRecording.get()) {
                Log.w(TAG, "Thermal camera already recording")
                return@withContext true
            }
            
            if (!isIRCameraConnected) {
                Log.e(TAG, "Thermal camera not connected")
                emitError(ErrorType.DEVICE_ERROR, "Thermal camera not connected")
                return@withContext false
            }
            
            this@ThermalCameraRecorder.sessionDirectory = sessionDirectory
            recordingStartTime = System.nanoTime()
            
            // Setup CSV data logging
            if (!setupDataLogging(sessionDirectory)) {
                emitError(ErrorType.STORAGE_ERROR, "Failed to setup thermal data logging")
                return@withContext false
            }
            
            _isRecording.set(true)
            frameCount.set(0)
            
            Log.i(TAG, "Thermal camera recording started successfully")
            emitStatus()
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start thermal camera recording", e)
            emitError(ErrorType.RECORDING_FAILED, "Failed to start thermal camera recording: ${e.message}")
            return@withContext false
        }
    }
    
    private fun setupDataLogging(sessionDir: String): Boolean {
        return try {
            val sessionDirFile = File(sessionDir)
            if (!sessionDirFile.exists()) {
                sessionDirFile.mkdirs()
            }
            
            // Setup thermal data CSV
            thermalDataFile = File(sessionDirFile, THERMAL_DATA_FILENAME)
            csvWriter = CSVWriter(FileWriter(thermalDataFile!!))
            
            // Write CSV header
            val header = arrayOf(
                "timestamp_ns", "timestamp_ms", "frame_number",
                "min_temp_c", "max_temp_c", "avg_temp_c",
                "ambient_temp_c", "emissivity"
            )
            csvWriter!!.writeNext(header)
            csvWriter!!.flush()
            
            Log.i(TAG, "Thermal data logging setup completed: ${thermalDataFile!!.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setup thermal data logging", e)
            false
        }
    }

    override suspend fun stopRecording(): Boolean {
        try {
            if (!_isRecording.get()) {
                Log.w(TAG, "Thermal camera not recording")
                return true
            }
            
            _isRecording.set(false)
            
            // Close CSV writer
            csvWriter?.let { writer ->
                try {
                    writer.flush()
                    writer.close()
                    Log.i(TAG, "Thermal data file closed: ${thermalDataFile?.absolutePath}")
                } catch (e: Exception) {
                    Log.w(TAG, "Error closing CSV writer", e)
                }
            }
            csvWriter = null
            
            Log.i(TAG, "Thermal camera recording stopped. Total frames: ${frameCount.get()}")
            emitStatus()
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop thermal camera recording", e)
            emitError(ErrorType.RECORDING_FAILED, "Failed to stop thermal camera recording: ${e.message}")
            return false
        }
    }

    override suspend fun addSyncMarker(markerType: String, timestampNs: Long, metadata: Map<String, String>) {
        try {
            val timestampMs = timestampNs / 1_000_000
            val metadataString = metadata.entries.joinToString(", ") { "${it.key}=${it.value}" }
            
            // Add sync marker to CSV data
            csvWriter?.let { writer ->
                val syncRecord = arrayOf(
                    "# SYNC_MARKER: $markerType at $timestampMs ms - $metadataString"
                )
                writer.writeNext(syncRecord)
                writer.flush()
            }
            
            Log.i(TAG, "Thermal sync marker added: $markerType at $timestampMs ms")
            
        } catch (e: Exception) {
            Log.w(TAG, "Failed to add thermal sync marker", e)
            emitError(ErrorType.SYNC_FAILED, "Thermal sync marker failed: ${e.message}")
        }
    }

    override suspend fun cleanup() {
        try {
            if (_isRecording.get()) {
                stopRecording()
            }
            
            recordingScope.cancel()
            
            // Disconnect thermal camera using latest SDK
            if (isIRCameraConnected) {
                ircamEngine?.let { engine ->
                    engine.disconnectDevice()
                    engine.releaseCamera()
                }
                isIRCameraConnected = false
                Log.i(TAG, "Thermal camera disconnected using latest SDK")
            }
            
            Log.i(TAG, "Thermal camera cleaned up successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Thermal camera cleanup failed", e)
        }
    }

    override fun getStatusFlow(): Flow<RecordingStatus> = _statusFlow.asSharedFlow()
    override fun getErrorFlow(): Flow<SensorError> = _errorFlow.asSharedFlow()

    override fun getRecordingStats(): RecordingStats {
        val currentTime = System.nanoTime()
        val sessionDuration = if (recordingStartTime > 0) (currentTime - recordingStartTime) / 1_000_000 else 0L
        
        return RecordingStats(
            sensorId = sensorId,
            sensorType = sensorType,
            sessionDurationMs = sessionDuration,
            totalSamplesRecorded = frameCount.get(),
            averageDataRate = if (sessionDuration > 0) frameCount.get() * 1000.0 / sessionDuration else 0.0,
            droppedSamples = 0L, // Would be calculated from frame monitoring
            storageUsedMB = calculateStorageUsed(),
            syncMarkersCount = 0, // Would track sync markers
            lastSampleTimestampNs = System.nanoTime()
        )
    }

    private fun calculateStorageUsed(): Double {
        // Estimate storage based on frame count and thermal data size
        val bytesPerFrame = 256 * 192 * 2 + 100 // Thermal data + CSV overhead
        val totalBytes = frameCount.get() * bytesPerFrame
        return totalBytes / (1024.0 * 1024.0)
    }

    private suspend fun emitStatus() {
        val status = RecordingStatus(
            sensorId = sensorId,
            sensorType = sensorType,
            isRecording = _isRecording.get(),
            samplesRecorded = frameCount.get(),
            currentDataRate = thermalFrameRate,
            storageUsedMB = calculateStorageUsed(),
            timestampNs = System.nanoTime()
        )
        _statusFlow.emit(status)
    }

    private suspend fun emitError(errorType: ErrorType, message: String, isRecoverable: Boolean = true) {
        val error = SensorError(
            sensorId = sensorId,
            sensorType = sensorType,
            errorType = errorType,
            errorMessage = message,
            timestampNs = System.nanoTime(),
            isRecoverable = isRecoverable
        )
        _errorFlow.emit(error)
    }

    /**
     * Get connection status of the thermal camera
     */
    fun getThermalCameraStatus(): String {
        return when {
            isIRCameraConnected -> "IR Camera Connected (IRUVCTC)"
            iruvctc != null -> "IR Camera Initializing"
            else -> "No Device Found"
        }
    }

    /**
     * Get current thermal camera configuration
     */
    fun getThermalConfiguration(): Map<String, Any> {
        return mapOf(
            "frame_rate_fps" to thermalFrameRate,
            "sensor_id" to sensorId,
            "resolution" to "${thermalResolution.first}x${thermalResolution.second}",
            "connection_status" to getThermalCameraStatus(),
            "recording_active" to _isRecording.get(),
            "iruvctc_integration" to true,
            "camera_connected" to isIRCameraConnected,
            "data_file" to (thermalDataFile?.absolutePath ?: "Not recording"),
            "ambient_temperature" to ambientTemperature,
            "emissivity" to emissivity
        )
    }
}