package com.guide.zm04c.matrix

import android.content.Context

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
}