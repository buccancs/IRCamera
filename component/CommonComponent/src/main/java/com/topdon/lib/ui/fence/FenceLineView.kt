package com.topdon.lib.ui.fence

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Fence line view for thermal measurements
 */
class FenceLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    var listener: CallBack? = null
    
    interface CallBack {
        fun onLineChanged(startX: Float, startY: Float, endX: Float, endY: Float)
        
        // Legacy callback method for compatibility
        fun callback(startPoint: IntArray, endPoint: IntArray, srcRect: IntArray) {
            // Default implementation
        }
    }
    
    /**
     * Clear fence selection
     */
    fun clear() {
        // Clear the fence selection
        invalidate()
    }
}