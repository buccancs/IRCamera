package com.topdon.lib.ui.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * RecyclerView data，data.
 *
 * Created by LCG on 2023/9/20.
 */
class MyItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    /**
     * data RecyclerView data，data dp.
     * data item data，data [itemLeft] data（dataCurrentdata）.
     */
    var wholeLeft: Float? = null

    /**
     * data RecyclerView data，data dp.
     * data item data，data [itemRight] data（dataCurrentdata）.
     */
    var wholeRight: Float? = null

    /**
     * data RecyclerView data，data dp.
     * data item data，data [itemTop] data（dataCurrentdata）.
     */
    var wholeTop: Float? = null

    /**
     * data RecyclerView data，data dp.
     * data item data，data [itemBottom] data（dataCurrentdata）.
     */
    var wholeBottom: Float? = null

    /**
     * data item data，data dp.
     */
    var itemLeft: Float? = null

    /**
     * data item data，data dp.
     */
    var itemRight: Float? = null

    /**
     * data item data，data dp.
     */
    var itemTop: Float? = null

    /**
     * data item data，data dp.
     */
    var itemBottom: Float? = null

    /**
     * data，data dp data px
     */
    private val density: Float = context.resources.displayMetrics.density

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemCount = parent.adapter?.itemCount ?: return
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager

        when (layoutManager) {
            is GridLayoutManager -> {
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    setVerticalMulti(outRect, position, itemCount, layoutManager.spanCount)
                } else {
                    setHorizontalMulti(outRect, position, itemCount, layoutManager.spanCount)
                }
            }
            is LinearLayoutManager -> {
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    setVerticalOne(outRect, position, itemCount)
                } else {
                    setHorizontalOne(outRect, position, itemCount)
                }
            }
            is StaggeredGridLayoutManager -> {
                val layoutParams = view.layoutParams
                val spanIndex = if (layoutParams is StaggeredGridLayoutManager.LayoutParams) layoutParams.spanIndex else 0
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    setVerticalMultiStaggered(outRect, position, itemCount, layoutManager.spanCount, spanIndex)
                } else {
                    setHorizontalMulti(outRect, position, itemCount, layoutManager.spanCount)
                }
            }
        }
    }

    /**
     * data RecyclerView data 1 data，Settingsdata.
     * @param itemCount data
     */
    private fun setVerticalOne(
        outRect: Rect,
        position: Int,
        itemCount: Int,
    ) {
        val left: Int = dp2px(wholeLeft ?: ((itemLeft ?: 0f) * 2))
        val right: Int = dp2px(wholeRight ?: ((itemRight ?: 0f) * 2))
        val top: Int = dp2px(if (position == 0) wholeTop ?: ((itemTop ?: 0f) * 2) else (itemTop ?: 0f))
        val bottom: Int = dp2px(if (position == itemCount - 1) wholeBottom ?: ((itemBottom ?: 0f) * 2) else (itemBottom ?: 0f))
        outRect.set(left, top, right, bottom)
    }

    /**
     * data RecyclerView data 1 data，Settingsdata.
     * @param itemCount data
     */
    private fun setHorizontalOne(
        outRect: Rect,
        position: Int,
        itemCount: Int,
    ) {
        val left: Int = dp2px(if (position == 0) wholeLeft ?: ((itemLeft ?: 0f) * 2) else (itemLeft ?: 0f))
        val right: Int = dp2px(if (position == itemCount - 1) wholeRight ?: ((itemRight ?: 0f) * 2) else (itemRight ?: 0f))
        val top: Int = dp2px(wholeTop ?: ((itemTop ?: 0f) * 2))
        val bottom: Int = dp2px(wholeBottom ?: ((itemBottom ?: 0f) * 2))
        outRect.set(left, top, right, bottom)
    }

    /**
     * data RecyclerView data，Settingsdata.
     * @param itemCount data
     * @param spanCount data(data)
     */
    private fun setVerticalMulti(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        spanCount: Int,
    ) {
        val totalRow = itemCount / spanCount + if (itemCount % spanCount == 0) 0 else 1 // data
        val rowPosition = position / spanCount // Current position data[0, totalRow)
        val columnPosition = position % spanCount // Current position data[0, spanCount)

        val left: Int = dp2px(if (columnPosition == 0) wholeLeft ?: ((itemLeft ?: 0f) * 2) else (itemLeft ?: 0f))
        val right: Int = dp2px(if (columnPosition == spanCount - 1) wholeRight ?: ((itemRight ?: 0f) * 2) else (itemRight ?: 0f))
        val top: Int = dp2px(if (rowPosition == 0) wholeTop ?: ((itemTop ?: 0f) * 2) else (itemTop ?: 0f))
        val bottom: Int = dp2px(if (rowPosition == totalRow - 1) wholeBottom ?: ((itemBottom ?: 0f) * 2) else (itemBottom ?: 0f))
        outRect.set(left, top, right, bottom)
    }

    /**
     * data RecyclerView data，Settingsdata.
     * @param itemCount data
     * @param spanCount data(data)
     * @param spanIndex Currentdataindex[0, spanCount)，data
     */
    private fun setVerticalMultiStaggered(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        spanCount: Int,
        spanIndex: Int,
    ) {
        val totalRow = itemCount / spanCount + if (itemCount % spanCount == 0) 0 else 1 // data
        val rowPosition = position / spanCount // Currentpositiondata[0, totalRow)

        val left: Int = dp2px(if (spanIndex == 0) wholeLeft ?: ((itemLeft ?: 0f) * 2) else (itemLeft ?: 0f))
        val right: Int = dp2px(if (spanIndex == spanCount - 1) wholeRight ?: ((itemRight ?: 0f) * 2) else (itemRight ?: 0f))
        val top: Int = dp2px(if (rowPosition == 0) wholeTop ?: ((itemTop ?: 0f) * 2) else (itemTop ?: 0f))
        val bottom: Int = dp2px(if (rowPosition == totalRow - 1) wholeBottom ?: ((itemBottom ?: 0f) * 2) else (itemBottom ?: 0f))
        outRect.set(left, top, right, bottom)
    }

    /**
     * data RecyclerView data，Settingsdata.
     * @param itemCount data
     * @param spanCount data(data)
     */
    private fun setHorizontalMulti(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        spanCount: Int,
    ) {
        // Note: Implementation to be added when specific requirements are defined
    }

    private fun dp2px(dpValue: Float): Int = (dpValue * density + 0.5f).toInt()
}
