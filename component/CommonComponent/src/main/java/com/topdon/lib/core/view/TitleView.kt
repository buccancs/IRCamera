package com.topdon.lib.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

/**
 * Simplified TitleView stub for component modules
 * Provides basic title functionality without complex UI dependencies
 */
class TitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    fun setLeftClickListener(listener: OnClickListener) {
        // Simplified implementation - component modules should use standard toolbar/actionbar
    }
    
    fun setTitle(title: String) {
        // Simplified implementation
    }
    
    fun setTitle(titleRes: Int) {
        setTitle(context.getString(titleRes))
    }
}