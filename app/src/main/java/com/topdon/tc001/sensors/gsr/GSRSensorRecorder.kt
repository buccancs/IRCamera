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

// Official ShimmerAndroidAPI imports for Shimmer3 GSR+ integration
import com.shimmerresearch.android.Shimmer
import com.shimmerresearch.android.manager.ShimmerBluetoothManager
import com.shimmerresearch.android.manager.ShimmerBluetoothManagerCallback
import com.shimmerresearch.driver.Configuration
import com.shimmerresearch.driver.ShimmerDevice
import com.shimmerresearch.driver.ObjectCluster
import com.shimmerresearch.driver.Configuration.COMMUNICATION_TYPE
import com.shimmerresearch.driverUtilities.SensorDetails
import com.shimmerresearch.driverUtilities.ShimmerVerDetails.HW_ID

/**
 * GSR (Galvanic Skin Response) sensor recorder using official ShimmerAndroidAPI for Shimmer3 GSR+ integration.
 * 
 * This implementation uses the official ShimmerAndroidAPI v3.2.4Beta for robust Shimmer3 GSR+ device communication.
 * Replaces the Nordic BLE implementation with proper ShimmerAndroidAPI integration.
 * 
 * Technical Requirements:
 * - Uses official ShimmerAndroidAPI v3.2.4Beta for Shimmer integration
 * - 12-bit ADC resolution (0-4095 range) as mandated  
 * - 128Hz sampling rate for high-frequency GSR analysis
 * - Proper start/stop streaming with ShimmerAndroidAPI
 * - Real-time data conversion from raw to microsiemens using official formulas
 * - CSV data logging with nanosecond timestamps
 * 
 * Connection Mode:
 * - High-Mobility Mode: ShimmerAndroidAPI managed BLE connection to Shimmer3 GSR+
 * 
 * Hardware Setup:
 * 1. Ensure Shimmer3 GSR+ device is discoverable
 * 2. Enable Bluetooth on Android device
 * 3. Start recording to begin real GSR data capture using official API
 * 
 * @author IRCamera Android Sensor Node (Spoke) - Official ShimmerAndroidAPI Integration
 */
class GSRSensorRecorder(
    private val context: Context,
    override val sensorId: String = "gsr_shimmer_1",
    private val samplingRateHz: Int = 128
) : SensorRecorder {

    companion object {
        private const val TAG = "GSRSensorRecorder"
        // Shimmer3 GSR+ specific constants using official API
        private const val SHIMMER_DEFAULT_SAMPLING_RATE = 128.0 // Hz
        private const val GSR_RANGE_AUTO = 4 // Auto range for GSR
        private const val SHIMMER_SAMPLING_RATE_128HZ = 7.8125 // Shimmer sampling rate constant for 128Hz
        
        // Official Shimmer sensor IDs
        private const val SENSOR_GSR = Configuration.Shimmer3.SENSOR_GSR
        private const val SENSOR_PPG_A13 = Configuration.Shimmer3.SENSOR_PPG_A13
        private const val SENSOR_VBATT = Configuration.Shimmer3.SENSOR_VBATT
        
        // Data logging constants
        private const val GSR_CSV_HEADER = "timestamp_ns,timestamp_ms,gsr_raw,gsr_microsiemens,ppg_raw,battery_voltage\n"
        
        // GSR conversion constants (12-bit ADC, 0-4095 range)
        private const val GSR_ADC_MAX = 4095.0
        private const val GSR_REF_VOLTAGE = 3.0 // 3V reference
        private const val GSR_UNCALIBRATED_TO_MICROSIEMENS = 1000000.0 / (GSR_REF_VOLTAGE * 40.2) // 40.2k ohm reference
    }

    override val sensorType: String = "GSR Shimmer3"
    override val samplingRate: Double = samplingRateHz.toDouble()
    
    private var _isRecording = AtomicBoolean(false)
    override val isRecording: Boolean get() = _isRecording.get()

    // Official ShimmerAndroidAPI components
    private var shimmerBluetoothManager: ShimmerBluetoothManager? = null
    private var shimmerDevice: Shimmer? = null
    private var shimmerMacAddress: String? = null
    private var isShimmerConnected = false
    
    // Recording state
    private val recordingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var sessionDirectory: String = ""
    private var sampleCount = AtomicLong(0)
    private var recordingStartTime: Long = 0
    private var syncMarkerCount = AtomicLong(0)
    
    // Data flows
    private val _statusFlow = MutableSharedFlow<RecordingStatus>()
    private val _errorFlow = MutableSharedFlow<SensorError>()
    
    // CSV data logging
    private var csvWriter: FileWriter? = null
    private var dataFile: File? = null
    
    // Data monitoring
    private var lastSampleTimestamp: Long = 0
    private var dataMonitoringJob: Job? = null

    // Official ShimmerAndroidAPI Bluetooth Manager Callback
    private val shimmerBluetoothCallback = object : ShimmerBluetoothManagerCallback {
        override fun onShimmerStateChange(shimmer: Shimmer, state: Int, bluetoothAdapter: Int) {
            when (state) {
                Shimmer.STATE_CONNECTED -> {
                    Log.i(TAG, "Shimmer connected: ${shimmer.macId}")
                    shimmerDevice = shimmer
                    isShimmerConnected = true
                    configureShimmerForGSR()
                }
                Shimmer.STATE_CONNECTING -> {
                    Log.i(TAG, "Shimmer connecting: ${shimmer.macId}")
                }
                Shimmer.STATE_NONE -> {
                    Log.i(TAG, "Shimmer disconnected: ${shimmer.macId}")
                    isShimmerConnected = false
                    shimmerDevice = null
                }
            }
        }

        override fun onDataChanged(shimmer: Shimmer, newDataPacket: ObjectCluster) {
            // Process real-time GSR data from official ShimmerAndroidAPI
            processShimmerDataPacket(newDataPacket)
        }

        override fun onBluetoothStateChange(state: Int) {
            Log.i(TAG, "Bluetooth state changed: $state")
        }
    }

    override fun statusFlow(): Flow<RecordingStatus> = _statusFlow.asSharedFlow()
    override fun errorFlow(): Flow<SensorError> = _errorFlow.asSharedFlow()

    override suspend fun initialize(sessionDirectory: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                this@GSRSensorRecorder.sessionDirectory = sessionDirectory
                
                Log.i(TAG, "Initializing GSR sensor recorder with official ShimmerAndroidAPI")
                
                // Initialize official ShimmerAndroidAPI Bluetooth Manager
                shimmerBluetoothManager = ShimmerBluetoothManager(context, shimmerBluetoothCallback)
                
                // Scan for Shimmer devices
                val availableDevices = shimmerBluetoothManager?.getShimmerDeviceList()
                if (availableDevices.isNullOrEmpty()) {
                    Log.w(TAG, "No Shimmer devices found, starting discovery...")
                    shimmerBluetoothManager?.startScanShimmerDevices()
                    
                    // Wait for device discovery (timeout after 10 seconds)
                    delay(10000)
                    val discoveredDevices = shimmerBluetoothManager?.getShimmerDeviceList()
                    if (discoveredDevices.isNullOrEmpty()) {
                        throw Exception("No Shimmer3 GSR+ devices discovered")
                    }
                }
                
                // Select first available Shimmer3 device
                val targetDevice = shimmerBluetoothManager?.getShimmerDeviceList()?.firstOrNull { device ->
                    device.shimmerDeviceDetails?.hardwareDetails?.hardwareId == HW_ID.SHIMMER_3
                }
                
                if (targetDevice != null) {
                    shimmerMacAddress = targetDevice.macId
                    Log.i(TAG, "Selected Shimmer3 device: $shimmerMacAddress")
                } else {
                    throw Exception("No Shimmer3 devices found in discovered devices")
                }
                
                // Prepare CSV data file
                setupDataLogging()
                
                _statusFlow.emit(RecordingStatus.Initialized)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize GSR sensor", e)
                _errorFlow.emit(SensorError.InitializationError("GSR initialization failed: ${e.message}"))
                Result.failure(e)
            }
        }
    }

    override suspend fun startRecording(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (_isRecording.get()) {
                    return@withContext Result.failure(Exception("GSR recording already in progress"))
                }
                
                Log.i(TAG, "Starting GSR recording with ShimmerAndroidAPI")
                
                // Connect to Shimmer device using official API
                shimmerMacAddress?.let { macAddress ->
                    shimmerBluetoothManager?.connectShimmerThroughBTAddress(macAddress)
                }
                
                // Wait for connection
                var connectionTimeout = 0
                while (!isShimmerConnected && connectionTimeout < 30) { // 30 second timeout
                    delay(1000)
                    connectionTimeout++
                }
                
                if (!isShimmerConnected) {
                    throw Exception("Failed to connect to Shimmer device")
                }
                
                // Start streaming using official API
                shimmerDevice?.startStreaming()
                
                recordingStartTime = System.nanoTime()
                _isRecording.set(true)
                sampleCount.set(0)
                
                // Start data monitoring
                startDataMonitoring()
                
                _statusFlow.emit(RecordingStatus.Recording)
                Log.i(TAG, "GSR recording started successfully with official ShimmerAndroidAPI")
                
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start GSR recording", e)
                _errorFlow.emit(SensorError.StartError("GSR start failed: ${e.message}"))
                Result.failure(e)
            }
        }
    }

    override suspend fun stopRecording(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!_isRecording.get()) {
                    return@withContext Result.success(Unit)
                }
                
                Log.i(TAG, "Stopping GSR recording")
                
                // Stop streaming using official API
                shimmerDevice?.stopStreaming()
                
                _isRecording.set(false)
                
                // Stop data monitoring
                dataMonitoringJob?.cancel()
                dataMonitoringJob = null
                
                // Close CSV file
                csvWriter?.flush()
                csvWriter?.close()
                csvWriter = null
                
                // Disconnect from device
                shimmerMacAddress?.let { macAddress ->
                    shimmerBluetoothManager?.disconnectShimmer(macAddress)
                }
                
                val totalSamples = sampleCount.get()
                Log.i(TAG, "GSR recording stopped. Total samples: $totalSamples")
                
                _statusFlow.emit(RecordingStatus.Stopped)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop GSR recording", e)
                _errorFlow.emit(SensorError.StopError("GSR stop failed: ${e.message}"))
                Result.failure(e)
            }
        }
    }

    override suspend fun insertSyncMarker(markerId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!_isRecording.get()) {
                    return@withContext Result.failure(Exception("GSR not recording"))
                }
                
                val currentTimeNs = System.nanoTime()
                val currentTimeMs = System.currentTimeMillis()
                val markerCount = syncMarkerCount.incrementAndGet()
                
                // Write sync marker to CSV
                csvWriter?.apply {
                    write("${currentTimeNs},${currentTimeMs},SYNC_MARKER_${markerId},${markerCount},SYNC,0.0\n")
                    flush()
                }
                
                Log.i(TAG, "GSR sync marker inserted: $markerId (count: $markerCount)")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert GSR sync marker", e)
                Result.failure(e)
            }
        }
    }

    override fun cleanup() {
        recordingScope.cancel()
        csvWriter?.close()
        shimmerBluetoothManager?.disconnectAllShimmers()
        shimmerBluetoothManager = null
        Log.i(TAG, "GSR sensor recorder cleaned up")
    }

    private fun configureShimmerForGSR() {
        shimmerDevice?.let { shimmer ->
            Log.i(TAG, "Configuring Shimmer3 for GSR recording")
            
            // Configure sampling rate (128Hz)
            shimmer.setSamplingRate(SHIMMER_DEFAULT_SAMPLING_RATE)
            
            // Enable GSR sensor
            shimmer.enableSensor(SENSOR_GSR)
            
            // Enable PPG sensor for additional physiological data
            shimmer.enableSensor(SENSOR_PPG_A13)
            
            // Enable battery monitoring
            shimmer.enableSensor(SENSOR_VBATT)
            
            // Set GSR range to auto
            shimmer.setGSRRange(GSR_RANGE_AUTO)
            
            // Write configuration to device
            shimmer.writeConfiguration()
            
            Log.i(TAG, "Shimmer3 GSR configuration complete")
        }
    }

    private fun processShimmerDataPacket(dataPacket: ObjectCluster) {
        if (!_isRecording.get()) return
        
        try {
            val currentTimeNs = System.nanoTime()
            val currentTimeMs = System.currentTimeMillis()
            
            // Extract GSR data using official ShimmerAndroidAPI methods
            val gsrRaw = dataPacket.getFormatClusterValue(Configuration.Shimmer3.ObjectClusterSensorName.GSR, "RAW")?.data ?: 0.0
            val gsrCal = dataPacket.getFormatClusterValue(Configuration.Shimmer3.ObjectClusterSensorName.GSR, "CAL")?.data ?: 0.0
            
            // Extract PPG data
            val ppgRaw = dataPacket.getFormatClusterValue(Configuration.Shimmer3.ObjectClusterSensorName.PPG_A13, "RAW")?.data ?: 0.0
            
            // Extract battery voltage
            val batteryVoltage = dataPacket.getFormatClusterValue(Configuration.Shimmer3.ObjectClusterSensorName.BATTERY, "CAL")?.data ?: 0.0
            
            // Calculate GSR in microsiemens using official calibration (if not available, use raw conversion)
            val gsrMicrosiemens = if (gsrCal > 0) {
                // Use official calibrated value (already in microsiemens)
                gsrCal
            } else {
                // Convert raw GSR to microsiemens using 12-bit ADC formula
                convertRawGSRToMicrosiemens(gsrRaw)
            }
            
            // Log data to CSV
            csvWriter?.apply {
                write("${currentTimeNs},${currentTimeMs},${gsrRaw.toInt()},%.2f,${ppgRaw.toInt()},%.3f\n".format(gsrMicrosiemens, batteryVoltage))
                
                // Flush every 128 samples (1 second at 128Hz)
                if (sampleCount.get() % 128 == 0L) {
                    flush()
                }
            }
            
            sampleCount.incrementAndGet()
            lastSampleTimestamp = currentTimeNs
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing Shimmer data packet", e)
        }
    }

    private fun convertRawGSRToMicrosiemens(rawValue: Double): Double {
        // Convert 12-bit ADC raw value (0-4095) to microsiemens
        // Using standard GSR conversion formula for Shimmer3
        val voltage = (rawValue / GSR_ADC_MAX) * GSR_REF_VOLTAGE
        return if (voltage > 0) {
            GSR_UNCALIBRATED_TO_MICROSIEMENS / voltage
        } else {
            0.0
        }
    }

    private fun setupDataLogging() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        dataFile = File(sessionDirectory, "gsr_shimmer_${timestamp}.csv")
        
        csvWriter = FileWriter(dataFile!!, false).apply {
            write(GSR_CSV_HEADER)
            flush()
        }
        
        Log.i(TAG, "GSR data logging setup: ${dataFile!!.absolutePath}")
    }

    private fun startDataMonitoring() {
        dataMonitoringJob = recordingScope.launch {
            while (_isRecording.get()) {
                delay(5000) // Check every 5 seconds
                
                val currentTime = System.nanoTime()
                val timeSinceLastSample = (currentTime - lastSampleTimestamp) / 1_000_000L // Convert to ms
                
                if (timeSinceLastSample > 10000) { // No data for 10 seconds
                    Log.w(TAG, "GSR data monitoring: No data received for ${timeSinceLastSample}ms")
                    _errorFlow.emit(SensorError.DataError("GSR data stream interrupted"))
                }
                
                val currentSampleCount = sampleCount.get()
                Log.d(TAG, "GSR data monitoring: $currentSampleCount samples, last sample ${timeSinceLastSample}ms ago")
            }
        }
    }
}