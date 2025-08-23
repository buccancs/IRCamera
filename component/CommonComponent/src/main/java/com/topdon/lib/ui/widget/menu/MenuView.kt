package com.topdon.lib.ui.widget.menu

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Menu view with tab click functionality  
 */
class MenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    var onTabClickListener: ((tabInfo: TabInfo) -> Unit)? = null
}

/**
 * Tab info for menu clicks
 */
data class TabInfo(
    val selectPosition: Int,
    val isObserveMode: Boolean
)