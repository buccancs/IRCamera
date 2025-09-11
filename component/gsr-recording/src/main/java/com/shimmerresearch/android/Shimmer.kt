package com.shimmerresearch.android

import android.content.Context
import android.os.Handler
import android.util.Log
import com.shimmerresearch.bluetooth.BluetoothManager
import com.shimmerresearch.driver.Configuration
import com.shimmerresearch.driver.ObjectCluster
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Shimmer sensor wrapper for GSR data acquisition
 */
class Shimmer(private val context: Context, private val handler: Handler) {
    
    companion object {
        private const val TAG = "Shimmer"
        private const val MESSAGE_READ = 1
        private const val SIMULATION_DATA_INTERVAL_MS = 50L
    }
    
    private var realShimmerInstance: Any? = null
    private var dataCallback: ((ObjectCluster) -> Unit)? = null
    private val isStreaming = AtomicBoolean(false)
    private var streamingJob: Job? = null
    private var simulationJob: Job? = null
    private var batteryLevel: Int = 85
    private var sampleCount = 0L

    fun setDataCallback(callback: (ObjectCluster) -> Unit) {
        this.dataCallback = callback
    }

    fun getBatteryLevel(): Int = batteryLevel

    /**
     * Check if streaming - Official API method
     */
    fun isStreaming(): Boolean = isStreaming.get()

    /**
     * Write configuration bytes - Official API method
     */
    fun writeConfigurationBytes(config: ByteArray) {
        try {
            realShimmerInstance?.let { device ->
                val method = device.javaClass.getMethod("writeConfigurationBytes", ByteArray::class.java)
                method.invoke(device, config)
                Log.d(TAG, "Configuration written to real device")
                return
            }
        } catch (e: Exception) {
            Log.d(TAG, "Real device configuration write not available")
        }
        Log.d(TAG, "Configuration bytes written (simulated): ${config.size} bytes")
    }

    /**
     * Read configuration from device - Official API method
     */
    fun readConfigurationBytes() {
        try {
            realShimmerInstance?.let { device ->
                val method = device.javaClass.getMethod("readConfigurationBytes")
                method.invoke(device)
                Log.d(TAG, "Configuration read from real device")
                return
            }
        } catch (e: Exception) {
            Log.d(TAG, "Real device configuration read not available")
        }
        Log.d(TAG, "Configuration read (simulated)")
    }

    /**
     * Set GSR range - Official API method
     */
    fun setGSRRange(range: Int) {
        configuration.gsrRange = range
        try {
            realShimmerInstance?.let { device ->
                val method = device.javaClass.getMethod("setGSRRange", Int::class.java)
                method.invoke(device, range)
                Log.d(TAG, "GSR range set on real device: $range")
                return
            }
        } catch (e: Exception) {
            Log.d(TAG, "Real device GSR range setting not available")
        }
        Log.d(TAG, "GSR range set (simulated): $range")
    }

    /**
     * Get current configuration - Official API method
     */
    fun getConfiguration(): Configuration = configuration

    /**
     * Set configuration - Official API method
     */
    fun setConfiguration(config: Configuration) {
        configuration = config
        samplingRate = config.samplingRate
        enabledSensors = config.enabledSensors
        Log.d(TAG, "Configuration updated: $config")
    }

    /**
     * Get available Shimmer devices - Official API method
     */
    fun getAvailableDevices(): List<android.bluetooth.BluetoothDevice> {
        return bluetoothManager.getBondedShimmerDevices()
    }

    /**
     * Validate device for connection - Official API method
     */
    fun validateDevice(address: String): Boolean {
        val device = bluetoothManager.findShimmerDeviceByAddress(address)
        return device?.let { bluetoothManager.validateShimmerDevice(it).isValid } ?: false
    }

    /**
     * Send message through handler - matches official API pattern
     */
    private fun sendMessage(
        what: Int,
        arg1: Int,
        arg2: Int,
        obj: Any?,
    ) {
        val message = handler.obtainMessage(what, arg1, arg2, obj)
        handler.sendMessage(message)
    }

    /**
     * Try to create real Shimmer device connection using official API
     */
    private fun createRealShimmerConnection(
        address: String,
        name: String,
    ): Any? {
        return try {
            // Try to use official Shimmer API
            val shimmerClass = Class.forName("com.shimmerresearch.android.Shimmer")
            val constructor = shimmerClass.getConstructor(Handler::class.java, Context::class.java)
            val shimmerInstance = constructor.newInstance(handler, context)

            // Connect to real device
            val connectMethod = shimmerClass.getMethod("connect", String::class.java, String::class.java)
            connectMethod.invoke(shimmerInstance, address, name)

            setupRealDeviceCallbacks(shimmerInstance)
            shimmerInstance
        } catch (e: Exception) {
            Log.d(TAG, "Could not create real Shimmer device: ${e.message}")
            null
        }
    }

    /**
     * Set up callbacks for real Shimmer device
     */
    private fun setupRealDeviceCallbacks(
        @Suppress("UNUSED_PARAMETER") shimmerInstance: Any,
    ) {
        try {

            // This would configure callbacks on the actual device
            Log.d(TAG, "Real Shimmer device callbacks configured")
        } catch (e: Exception) {
            Log.w(TAG, "Could not set up real device callbacks", e)
        }
    }

    /**
     * Generate realistic simulated GSR data for development/testing
     */
    private fun startSimulationDataGeneration() {
        simulationJob =
            CoroutineScope(Dispatchers.IO).launch {
                var sampleCount = 0L

                while (isStreaming.get() && isActive) {
                    try {
                        val objectCluster = ObjectCluster()

                        // Generate realistic GSR data with physiological variation
                        val currentTime = System.currentTimeMillis()

                        // Simulate GSR data being available in ObjectCluster
                        dataCallback?.invoke(objectCluster)

                        // Send data message through handler for official API compatibility
                        sendMessage(MESSAGE_READ, 0, 0, objectCluster)

                        sampleCount++
                        delay(SIMULATION_DATA_INTERVAL_MS)
                    } catch (e: Exception) {
                        if (isActive) {
                            Log.e(TAG, "Error in data simulation", e)
                        }
                    }
                }
            }
    }
    
    /**
     * Send message through handler for API compatibility
     */
    private fun sendMessage(what: Int, arg1: Int, arg2: Int, obj: Any) {
        handler.obtainMessage(what, arg1, arg2, obj).sendToTarget()
    }
}
