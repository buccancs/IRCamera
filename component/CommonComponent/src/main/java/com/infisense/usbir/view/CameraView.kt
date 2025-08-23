package com.infisense.usbir.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View

/**
 * Stub implementation of CameraView for compilation
 * Real implementation should be provided by Infisense USB IR library
 */
class CameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    val bitmap: Bitmap?
        get() = null // Stub - returns null bitmap
    
    fun openCamera() {
        // Stub implementation
    }
    
    fun closeCamera() {
        // Stub implementation 
    }
    
    fun setCameraAlpha(alpha: Float) {
        // Stub implementation
    }
    
    fun setPreviewSize(width: Int, height: Int) {
        // Stub implementation
    }
    
    fun release() {
        // Stub implementation
    }
}