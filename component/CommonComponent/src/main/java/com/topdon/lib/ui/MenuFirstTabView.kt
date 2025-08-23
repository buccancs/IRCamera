package com.topdon.lib.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * First level menu tab view for thermal navigation
 */
class MenuFirstTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    
    /**
     * Interface for handling tab item selection
     */
    interface OnItemListener {
        fun selectPosition(position: Int)
    }
    
    private var onItemListener: OnItemListener? = null
    
    /**
     * Set item selection listener
     */
    fun setOnItemListener(listener: OnItemListener) {
        this.onItemListener = listener
    }
    
    /**
     * Trigger item selection
     */
    fun selectItem(position: Int) {
        onItemListener?.selectPosition(position)
    }
    
    /**
     * Set visibility of the tab view
     */
    fun setTabVisibility(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }
}