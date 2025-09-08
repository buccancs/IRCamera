package com.topdon.lib.ui.utils

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: CaiSongL
 * @date: 2023/4/1 14:44
 */
class RecyclerViewProxy(val layoutManager: RecyclerView.LayoutManager) {
    /**
     * Function description.
     */
    fun attachView(view: View?) {
        layoutManager.attachView(view!!)
    }

    /**
     * Function description.
     */
    fun detachView(view: View?) {
        layoutManager.detachView(view!!)
    }

    /**
     * Function description.
     */
    fun detachAndScrapView(view: View?, recycler: RecyclerView.Recycler?) {
        layoutManager.detachAndScrapView(view!!, recycler!!)
    }

    /**
     * Function description.
     */
    fun detachAndScrapAttachedViews(recycler: RecyclerView.Recycler?) {
        layoutManager.detachAndScrapAttachedViews(recycler!!)
    }

    /**
     * Function description.
     */
    fun recycleView(view: View?, recycler: RecyclerView.Recycler) {
        recycler.recycleView(view!!)
    }

    /**
     * Function description.
     */
    fun removeAndRecycleAllViews(recycler: RecyclerView.Recycler?) {
        layoutManager.removeAndRecycleAllViews(recycler!!)
    }

    /** childCount property */
    val childCount: Int
        get() = layoutManager.childCount
    /** itemCount property */
    val itemCount: Int
        get() = layoutManager.itemCount

    /**
     * Function description.
     */
    fun getMeasuredChildForAdapterPosition(position: Int, recycler: RecyclerView.Recycler): View {
        val view = recycler.getViewForPosition(position)
        layoutManager.addView(view)
        layoutManager.measureChildWithMargins(view, 0, 0)
        return view
    }

    /**
     * Function description.
     */
    fun layoutDecoratedWithMargins(v: View?, left: Int, top: Int, right: Int, bottom: Int) {
        layoutManager.layoutDecoratedWithMargins(v!!, left, top, right, bottom)
    }

    /**
     * Function description.
     */
    fun getChildAt(index: Int): View? {
        return layoutManager.getChildAt(index)
    }

    /**
     * Function description.
     */
    fun getPosition(view: View?): Int {
        return layoutManager.getPosition(view!!)
    }

    /**
     * Function description.
     */
    fun getMeasuredWidthWithMargin(child: View): Int {
        val lp = child.layoutParams as MarginLayoutParams
        return layoutManager.getDecoratedMeasuredWidth(child) + lp.leftMargin + lp.rightMargin
    }

    /**
     * Function description.
     */
    fun getMeasuredHeightWithMargin(child: View): Int {
        val lp = child.layoutParams as MarginLayoutParams
        return layoutManager.getDecoratedMeasuredHeight(child) + lp.topMargin + lp.bottomMargin
    }

    /** width property */
    val width: Int
        get() = layoutManager.width
    /** height property */
    val height: Int
        get() = layoutManager.height

    /**
     * Function description.
     */
    fun offsetChildrenHorizontal(amount: Int) {
        layoutManager.offsetChildrenHorizontal(amount)
    }

    /**
     * Function description.
     */
    fun offsetChildrenVertical(amount: Int) {
        layoutManager.offsetChildrenVertical(amount)
    }

    /**
     * Function description.
     */
    fun requestLayout() {
        layoutManager.requestLayout()
    }

    /**
     * Function description.
     */
    fun startSmoothScroll(smoothScroller: RecyclerView.SmoothScroller?) {
        layoutManager.startSmoothScroll(smoothScroller)
    }

    /**
     * Function description.
     */
    fun removeAllViews() {
        layoutManager.removeAllViews()
    }
}