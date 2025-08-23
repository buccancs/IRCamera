package com.guide.zm04c.matrix

import android.content.Context
import android.graphics.Bitmap

/**
 * Stub GuideInterface for thermal camera external library compatibility
 */
class GuideInterface {
    
    interface IrDataCallback {
        fun processIrData(yuv: ByteArray, temp: FloatArray)
    }
    
    fun init(context: Context, callback: IrDataCallback): Int {
        // Stub implementation - return success code
        return 5
    }
    
    fun exit() {
        // Stub implementation - cleanup
    }
    
    fun yuv2Bitmap(bitmap: Bitmap?, yuvData: ByteArray): Boolean {
        // Stub implementation for YUV to bitmap conversion
        return bitmap != null
    }
    
    fun getImageStatus(): Int {
        // Stub implementation - return default image status
        return 0
    }
    
    fun setRange(min: Float, max: Float) {
        // Stub implementation for temperature range setting
    }
    
    fun nuc() {
        // Stub implementation for non-uniformity correction
    }
    
    fun setOpenLut(enabled: Boolean) {
        // Stub implementation for lookup table control
    }
    
    fun changePalette(paletteIndex: Int) {
        // Stub implementation for palette change
    }
    
    fun shotScreenBitmap(bitmap: Bitmap?, width: Int, height: Int): Bitmap? {
        // Stub implementation for screen capture
        return bitmap
    }
}