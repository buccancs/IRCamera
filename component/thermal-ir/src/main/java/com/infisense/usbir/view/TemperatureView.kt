package com.infisense.usbir.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View

/**
 * Temperature view for thermal imaging display with region mode support
 */
class TemperatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    companion object {
        const val REGION_MODE_CLEAN = 0
        const val REGION_MODE_NORMAL = 1
    }
    
    var temperatureRegionMode: Int = REGION_MODE_CLEAN
    
    val regionAndValueBitmap: Bitmap?
        get() {
            // Stub implementation - return null bitmap for now
            return null
        }
    
    /**
     * Set temperature value
     */
    fun setTemperature(temp: Float) {
        // Stub implementation
    }
    
    /**
     * Set image size
     */
    fun setImageSize(width: Int, height: Int) {
        // Stub implementation
    }
    
    /**
     * Set sync image
     */
    fun setSyncimage(syncImage: Boolean) {
        // Stub implementation
    }
    
    /**
     * Set use IRISP
     */
    fun setUseIRISP(useIRISP: Boolean) {
        // Stub implementation
    }
}