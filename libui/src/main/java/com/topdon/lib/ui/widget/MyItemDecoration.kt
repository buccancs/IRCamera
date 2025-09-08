package com.topdon.lib.ui.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * RecyclerView used by, [Chinese text].
 *
 * Created by LCG on 2023/9/20.
 */
class MyItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    /**
     * [Chinese text] RecyclerView left side[Chinese text], [Chinese text] dp.
     * [Chinese text]only[Chinese text]left side item [Chinese text], [Chinese text] [itemLeft] [Chinese text]([Chinese text]).
     */
    var wholeLeft: Float? = null

    /**
     * [Chinese text] RecyclerView right side[Chinese text], [Chinese text] dp.
     * [Chinese text]only[Chinese text]right side item [Chinese text], [Chinese text] [itemRight] [Chinese text]([Chinese text]).
     */
    var wholeRight: Float? = null

    /**
     * [Chinese text] RecyclerView [Chinese text], [Chinese text] dp.
     * [Chinese text]only[Chinese text] item [Chinese text], [Chinese text] [itemTop] [Chinese text]([Chinese text]).
     */
    var wholeTop: Float? = null

    /**
     * [Chinese text] RecyclerView [Chinese text], [Chinese text] dp.
     * [Chinese text]only[Chinese text] item [Chinese text], [Chinese text] [itemBottom] [Chinese text]([Chinese text]).
     */
    var wholeBottom: Float? = null

    /**
     * [Chinese text] item left side[Chinese text], [Chinese text] dp.
     */
    var itemLeft: Float? = null

    /**
     * [Chinese text] item right side[Chinese text], [Chinese text] dp.
     */
    var itemRight: Float? = null

    /**
     * [Chinese text] item [Chinese text], [Chinese text] dp.
     */
    var itemTop: Float? = null

    /**
     * [Chinese text] item [Chinese text], [Chinese text] dp.
     */
    var itemBottom: Float? = null

    /**
     * [Chinese text], for dp [Chinese text] px
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
     * [Chinese text] RecyclerView [Chinese text] 1 [Chinese text], Settings[Chinese text].
     * @param itemCount [Chinese text]
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
     * [Chinese text] RecyclerView [Chinese text] 1 [Chinese text], Settings[Chinese text].
     * @param itemCount [Chinese text]
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
     * [Chinese text] RecyclerView [Chinese text], Settings[Chinese text].
     * @param itemCount [Chinese text]
     * @param spanCount [Chinese text]([Chinese text])
     */
    private fun setVerticalMulti(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        spanCount: Int,
    ) {
        val totalRow = itemCount / spanCount + if (itemCount % spanCount == 0) 0 else 1 // [Chinese text]
        val rowPosition = position / spanCount // [Chinese text] position [Chinese text][0, totalRow)
        val columnPosition = position % spanCount // [Chinese text] position [Chinese text][0, spanCount)

        val left: Int = dp2px(if (columnPosition == 0) wholeLeft ?: ((itemLeft ?: 0f) * 2) else (itemLeft ?: 0f))
        val right: Int = dp2px(if (columnPosition == spanCount - 1) wholeRight ?: ((itemRight ?: 0f) * 2) else (itemRight ?: 0f))
        val top: Int = dp2px(if (rowPosition == 0) wholeTop ?: ((itemTop ?: 0f) * 2) else (itemTop ?: 0f))
        val bottom: Int = dp2px(if (rowPosition == totalRow - 1) wholeBottom ?: ((itemBottom ?: 0f) * 2) else (itemBottom ?: 0f))
        outRect.set(left, top, right, bottom)
    }

    /**
     * [Chinese text] RecyclerView [Chinese text], Settings[Chinese text].
     * @param itemCount [Chinese text]
     * @param spanCount [Chinese text]([Chinese text])
     * @param spanIndex [Chinese text]in progress[Chinese text]index[0, spanCount), [Chinese text]
     */
    private fun setVerticalMultiStaggered(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        spanCount: Int,
        spanIndex: Int,
    ) {
        val totalRow = itemCount / spanCount + if (itemCount % spanCount == 0) 0 else 1 // [Chinese text]
        val rowPosition = position / spanCount // [Chinese text]position[Chinese text][0, totalRow)

        val left: Int = dp2px(if (spanIndex == 0) wholeLeft ?: ((itemLeft ?: 0f) * 2) else (itemLeft ?: 0f))
        val right: Int = dp2px(if (spanIndex == spanCount - 1) wholeRight ?: ((itemRight ?: 0f) * 2) else (itemRight ?: 0f))
        val top: Int = dp2px(if (rowPosition == 0) wholeTop ?: ((itemTop ?: 0f) * 2) else (itemTop ?: 0f))
        val bottom: Int = dp2px(if (rowPosition == totalRow - 1) wholeBottom ?: ((itemBottom ?: 0f) * 2) else (itemBottom ?: 0f))
        outRect.set(left, top, right, bottom)
    }

    /**
     * [Chinese text] RecyclerView [Chinese text], Settings[Chinese text].
     * @param itemCount [Chinese text]
     * @param spanCount [Chinese text]([Chinese text])
     */
    private fun setHorizontalMulti(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        spanCount: Int,
    ) {
        // TODO: [Chinese text]
    }

    private fun dp2px(dpValue: Float): Int = (dpValue * density + 0.5f).toInt()
}
