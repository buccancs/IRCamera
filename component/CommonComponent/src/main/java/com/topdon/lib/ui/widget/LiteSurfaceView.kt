package com.topdon.lib.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.SurfaceView

/**
 * Lite Surface View for thermal imaging camera preview
 * Stub implementation for missing external library dependency
 */
class LiteSurfaceView @JvmOverloads constructor(
    context: Context, 
    attrs: AttributeSet? = null, 
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {
    
    // Stub properties for compatibility
    var scaleBitmap: Bitmap? = null
    var temperatureRegionMode: Int = REGION_MODE_CLEAN
    
    companion object {
        const val REGION_MODE_CLEAN = 0
        const val REGION_MODE_OVERLAY = 1
    }
    
    // Stub methods referenced in thermal-ir component
    fun mergeBitmap(bitmap1: Bitmap?, bitmap2: Bitmap?): Bitmap? {
        // Stub implementation - in real implementation would merge bitmaps
        return bitmap1 ?: bitmap2
    }
    
    fun setTemperatureDisplay(enabled: Boolean) {
        // Stub implementation for temperature overlay
    }
    
    fun updatePreview() {
        // Stub implementation for preview update
    }
}