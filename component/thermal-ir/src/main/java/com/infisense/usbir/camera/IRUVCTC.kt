package com.infisense.usbir.camera

import android.content.Context

/**
 * IRUVCTC class stub for external library compatibility
 */
class IRUVCTC(
    private val width: Int,
    private val height: Int, 
    private val context: Context,
    private val syncImage: Any?,
    private val imageListener: Any?,
    private val usbCallback: USBMonitorCallback
) {
    var isRestart: Boolean = false
    
    fun setImageSrc(imageBytes: ByteArray?) {
        // Stub implementation for external library method
    }
    
    fun setTemperatureSrc(temperatureBytes: ByteArray?) {
        // Stub implementation for external library method  
    }
    
    fun setRotate(angle: Int) {
        // Stub implementation for external library method
    }
    
    fun registerUSB() {
        // Stub implementation for external library method
    }
    
    fun unregisterUSB() {
        // Stub implementation for external library method
    }
    
    fun startPreview() {
        // Stub implementation for external library method
    }
    
    fun stopPreview() {
        // Stub implementation for external library method
    }
    
    fun release() {
        // Stub implementation for external library method
    }
}