package com.topdon.lib.ui.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR

/**
 * 自定义FooterView
 */
class LoadingFooter : LinearLayout, RefreshFooter {

    private val llLoading: LinearLayout
    private val clLoadEnd: ConstraintLayout

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        inflate(context, UiR.layout.ui_footer_view, this)

        llLoading = findViewById(UiR.id.ll_loading)
        clLoadEnd = findViewById(UiR.id.cl_load_end)
    }


    override fun isSupportHorizontalDrag(): Boolean = false

    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        llLoading.isVisible = !noMoreData
        clLoadEnd.isVisible = noMoreData
        return true
    }

    override fun getView(): View = this

    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate

    override fun setPrimaryColors(vararg colors: Int) {

    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {

    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int = 0



    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {

    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        // Empty implementation
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        // Empty implementation  
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
        // Empty implementation
    }
}