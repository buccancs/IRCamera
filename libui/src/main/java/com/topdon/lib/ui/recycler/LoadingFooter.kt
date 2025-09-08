package com.topdon.lib.ui.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.topdon.lib.ui.R as UiR

/**
 * [Chinese text]FooterView - Simplified version without SmartRefreshLayout dependency
 */
class LoadingFooter : LinearLayout {
    private val llLoading: LinearLayout
    private val clLoadEnd: ConstraintLayout

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        inflate(context, UiR.layout.ui_footer_view, this)

        llLoading = findViewById(UiR.id.ll_loading)
        clLoadEnd = findViewById(UiR.id.cl_load_end)
    }

    fun setNoMoreData(noMoreData: Boolean): Boolean {
        llLoading.isVisible = !noMoreData
        clLoadEnd.isVisible = noMoreData
        return true
    }

    fun getCustomView(): View = this
}
