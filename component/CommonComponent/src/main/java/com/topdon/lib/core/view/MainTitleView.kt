package com.topdon.lib.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

/**
 * MainTitleView for main activities title handling
 */
class MainTitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    fun setTitle(title: String) {
        // Simplified implementation
    }
    
    fun setTitle(titleRes: Int) {
        setTitle(context.getString(titleRes))
    }
    
    fun setRightText(text: String) {
        // Simplified implementation for setting right text
    }
    
    fun setRightText(textRes: Int) {
        setRightText(context.getString(textRes))
    }
    
    fun setLeftClickListener(listener: OnClickListener) {
        // Simplified implementation
    }
    
    fun setRightClickListener(listener: OnClickListener) {
        // Simplified implementation
    }
    
    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }
}