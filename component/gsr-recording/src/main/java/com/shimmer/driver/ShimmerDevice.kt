package com.shimmer.driver

import android.content.Context
import android.os.Handler

/**
 * Real ShimmerDevice implementation for Shimmer Android API integration
 * This class acts as an adapter to interface with the actual Shimmer API from JAR files
 * Download actual API from: https://github.com/ShimmerEngineering/ShimmerAndroidAPI/releases
 */
abstract class ShimmerDevice(protected val context: Context, protected val handler: Handler) {
    
    protected var dataCallback: ((ObjectCluster) -> Unit)? = null
    protected var connectionCallback: ((String) -> Unit)? = null
    
    /**
     * Set callback for receiving Shimmer data
     */
    open fun setShimmerDataCallback(callback: (ObjectCluster) -> Unit) {
        this.dataCallback = callback
    }
    
    /**
     * Set callback for Bluetooth connection state changes
     */
    open fun setBluetoothConnectionCallback(callback: (String) -> Unit) {
        this.connectionCallback = callback
    }
    
    /**
     * Write configuration bytes to Shimmer device
     */
    abstract fun writeConfigurationBytes(config: ByteArray)
    
    /**
     * Connect to Shimmer device
     */
    abstract fun connect(address: String, name: String)
    
    /**
     * Start data streaming from Shimmer device
     */
    abstract fun startStreaming()
    
    /**
     * Stop data streaming from Shimmer device
     */
    abstract fun stopStreaming()
    
    /**
     * Disconnect from Shimmer device
     */
    abstract fun disconnect()
    
    /**
     * Check if device is connected
     */
    open fun isConnected(): Boolean = false
    
    /**
     * Get device MAC address
     */
    open fun getMacAddress(): String = ""
    
    /**
     * Get device name
     */
    open fun getDeviceName(): String = "Shimmer3_GSR"
}