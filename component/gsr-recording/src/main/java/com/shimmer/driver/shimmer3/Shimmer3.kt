package com.shimmer.driver.shimmer3

import android.content.Context
import android.os.Handler
import com.shimmer.driver.ObjectCluster
import com.shimmer.driver.ShimmerDevice

/**
 * Stub implementation of Shimmer3 for compilation
 * This should be replaced with the actual Shimmer Android API
 */
class Shimmer3(context: Context, handler: Handler) : ShimmerDevice(context, handler) {
    
    private var dataCallback: ((ObjectCluster) -> Unit)? = null
    private var connectionCallback: ((String) -> Unit)? = null
    private var isStreaming = false
    
    override fun setShimmerDataCallback(callback: (ObjectCluster) -> Unit) {
        dataCallback = callback
    }
    
    override fun setBluetoothConnectionCallback(callback: (String) -> Unit) {
        connectionCallback = callback
    }
    
    override fun writeConfigurationBytes(config: ByteArray) {
        // Stub - configuration would be written to actual device
    }
    
    override fun connect(address: String, name: String) {
        // Simulate connection
        connectionCallback?.invoke("CONNECTED")
    }
    
    override fun startStreaming() {
        isStreaming = true
        // In real implementation, this would start receiving data from device
        // For now, we don't generate stub data here as it would interfere with the main loop
    }
    
    override fun stopStreaming() {
        isStreaming = false
    }
    
    override fun disconnect() {
        isStreaming = false
        connectionCallback?.invoke("DISCONNECTED")
    }
}