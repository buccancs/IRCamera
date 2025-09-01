package com.shimmer.driver

import android.content.Context
import android.os.Handler

/**
 * Stub implementation of ShimmerDevice for compilation
 * This should be replaced with the actual Shimmer Android API
 * Download from: https://github.com/ShimmerEngineering/ShimmerAndroidAPI/releases
 */
abstract class ShimmerDevice(context: Context, handler: Handler) {
    
    abstract fun setShimmerDataCallback(callback: (ObjectCluster) -> Unit)
    
    abstract fun setBluetoothConnectionCallback(callback: (String) -> Unit)
    
    abstract fun writeConfigurationBytes(config: ByteArray)
    
    abstract fun connect(address: String, name: String)
    
    abstract fun startStreaming()
    
    abstract fun stopStreaming()
    
    abstract fun disconnect()
}