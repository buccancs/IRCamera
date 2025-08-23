package com.guide.zm04c.matrix

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.SurfaceView

/**
 * IR Surface View stub for external library dependency
 */
class IrSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {
    
    /**
     * Set matrix parameters - stub implementation
     */
    fun setMatrixParams(params: Any?) {
        // Stub implementation
    }
    
    /**
     * Start surface view
     */
    fun start() {
        // Stub implementation
    }
    
    /**
     * Stop surface view
     */
    fun stop() {
        // Stub implementation
    }
    
    /**
     * Set matrix transformation for surface view
     */
    fun setMatrix(matrix: android.graphics.Matrix?, width: Float, height: Float) {
        // Stub implementation for matrix transformation
    }
    
    /**
     * Draw bitmap to surface with image status
     */
    fun doDraw(bitmap: Bitmap?, imageStatus: Int) {
        // Stub implementation for drawing bitmap to surface
    }
}