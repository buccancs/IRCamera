package com.topdon.house.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.topdon.house.R
import com.topdon.house.activity.ImagesDetailActivity
import com.topdon.house.activity.ItemEditActivity
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.db.entity.DirDetect
import com.topdon.lib.core.db.entity.ItemDetect
import com.topdon.lib.core.tools.SpanBuilder
import com.topdon.lms.sdk.weiget.TToast
import kotlinx.android.synthetic.main.item_report_add_default.view.*
import kotlinx.android.synthetic.main.item_report_add_head.view.*

            private fun handleImageDel(imageNum: Int) {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return
                }
                val itemDetect: ItemDetect = dataList[position] as ItemDetect
                itemDetect.delOneImage(imageNum)
                onItemChangeListener?.invoke(itemDetect)
                notifyItemChanged(position)
            }
        }
    }

    /**
     * 从指定 itemPosition 处，往上遍历查找该 item 对应的 Dir position.
     */
    private fun findDirPosition(itemPosition: Int): Int {
        for (i in itemPosition downTo 0) {
            if (dataList[i] is DirDetect) {
                return i
            }
        }
        return 0
    }

    private inner class MyOnLayoutChangeListener : OnLayoutChangeListener {
        override fun onLayoutChange(
            v: View?,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int,
        ) {
            val seeFirstPosition = layoutManager.findFirstVisibleItemPosition()
            if (seeFirstPosition == RecyclerView.NO_POSITION || seeFirstPosition >= dataList.size) {
                return
            }
            // notify 后旧 currentPosition 已不准确，需要刷新
            currentPosition =
                if (dataList[seeFirstPosition] is DirDetect) {
                    seeFirstPosition
                } else {
                    findDirPosition(
                        seeFirstPosition,
                    )
                }
            titleView.translationY = 0f
            adapter.refreshDir(titleView, dataList[currentPosition] as DirDetect)
            onScrollListener.onScrolled(recyclerView, 0, 0)
        }
    }

    private inner class MyOnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(
            recyclerView: RecyclerView,
            newState: Int,
        ) {
        }

        override fun onScrolled(
            recyclerView: RecyclerView,
            dx: Int,
            dy: Int,
        ) {
            val seeFirstPosition = layoutManager.findFirstVisibleItemPosition()
            if (seeFirstPosition == RecyclerView.NO_POSITION || seeFirstPosition >= dataList.size - 1) {
                return
            }

            // 刷新 currentPosition
            if (currentPosition != seeFirstPosition) {
                if (dataList[seeFirstPosition] is DirDetect) {
                    currentPosition = seeFirstPosition
                    titleView.translationY = 0f
                    adapter.refreshDir(titleView, dataList[currentPosition] as DirDetect)
                } else {
                    if (dataList[seeFirstPosition + 1] is DirDetect) { // 第1个可见的item为尾部
                        currentPosition = findDirPosition(seeFirstPosition)
                        titleView.translationY = 0f
                        adapter.refreshDir(titleView, dataList[currentPosition] as DirDetect)
                    }
                }
            }

            // 刷新 titleView 背景
            /*if ((dataList[currentPosition] as DirDetect).isExpand) {
                if (dataList[seeFirstPosition] is ItemDetect && dataList[seeFirstPosition + 1] is DirDetect) {
                    titleView.view_bg_dir.setBackgroundResource(R.drawable.bg_corners10_solid_23202e)
                } else {
                    titleView.view_bg_dir.setBackgroundResource(R.drawable.bg_corners10_top_solid_23202e)
                }
            } else {//收起肯定是4圆角
                titleView.view_bg_dir.setBackgroundResource(R.drawable.bg_corners10_solid_23202e)
            }*/
            if (dataList[seeFirstPosition] is ItemDetect && dataList[seeFirstPosition + 1] is DirDetect) {
                titleView.view_bg_dir.setBackgroundResource(R.drawable.bg_corners10_solid_23202e)
            }

            // 刷新 titleView 位置
            if (dataList[seeFirstPosition + 1] is DirDetect) { // 第1个可见的item为尾部
                val nextView: View? = layoutManager.findViewByPosition(seeFirstPosition + 1)
                if (nextView != null) {
                    if (nextView.top <= titleView.height) {
                        titleView.translationY = -(titleView.height - nextView.top).toFloat()
                    } else {
                        titleView.translationY = 0f
                    }
                }
            } else {
                if (titleView.translationY != 0f) {
                    titleView.translationY = 0f
                }
            }
        }
    }
}
