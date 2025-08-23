package com.topdon.lib.ui.fence

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Fence rectangle view for thermal measurements
 */
class FenceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    var listener: CallBack? = null
    
    interface CallBack {
        fun onRectChanged(left: Float, top: Float, right: Float, bottom: Float)
    }
}