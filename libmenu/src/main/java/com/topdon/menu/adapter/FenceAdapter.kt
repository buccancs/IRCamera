package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.constant.MenuType
import com.topdon.menu.R as MenuR
import com.topdon.lib.core.R
import com.topdon.menu.constant.FenceType

/**
 * Point, Line, Area, Full image, Trend chart (optional), Delete menu Adapter.
 *
 * - Single light: Point, Line, Area, Full image, Trend chart, Delete
 * - Dual light: Point, Line, Area, Full image, Trend chart, Delete
 * - Lite: Point, Line, Area, Full image, Trend chart, Delete
 * - TC007: Point, Line, Area, Full image, Trend chart, Delete
 * - 2D editing: Point, Line, Area, Full image, Delete
 *
 * Point, Line, Area, Trend chart, Full image are mutually exclusive with Delete
 *
 * Point, Line, Area, Trend chart are mutually exclusive, Full image is independently selectable
 *
 * Created by LCG on 2024/11/18.
 */
@SuppressLint("NotifyDataSetChanged")
internal class FenceAdapter(menuType: MenuType) : BaseMenuAdapter() {
    /**
     * Currently selected menu type, null indicates none are selected.
     */
    var selectType: FenceType? = null
        set(value) {
            when (value) {
                FenceType.FULL -> isFullSelect = true
                FenceType.DEL -> isFullSelect = false
                else -> {//点、线、面、趋势图，不会影响全图状态

                }
            }
            field = value
            notifyDataSetChanged()
        }
    /**
     * 全图是否已选中.
     */
    private var isFullSelect: Boolean = false

    /**
     * 菜单点击事件监听，目前都是单选，等后续有空重构了，再搞成 IOS 那样“全图”可以多选。
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)? = null



    private val dataList: ArrayList<Data> = ArrayList(6)

    init {
        dataList.add(Data(R.string.thermal_point, MenuR.drawable.selector_menu2_fence_point, FenceType.POINT))
        dataList.add(Data(R.string.thermal_line, MenuR.drawable.selector_menu2_fence_line, FenceType.LINE))
        dataList.add(Data(R.string.thermal_rect, MenuR.drawable.selector_menu2_fence_rect, FenceType.RECT))
        dataList.add(Data(R.string.thermal_full_rect, MenuR.drawable.selector_menu2_fence_full, FenceType.FULL))
        if (menuType != MenuType.GALLERY_EDIT) {//2D编辑的菜单没有趋势图
            dataList.add(Data(R.string.thermal_trend, MenuR.drawable.selector_menu2_fence_trend, FenceType.TREND))
        }
        dataList.add(Data(R.string.thermal_delete, MenuR.drawable.selector_menu2_del, FenceType.DEL))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataList[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = if (data.fenceType == FenceType.FULL) isFullSelect else data.fenceType == selectType
        holder.binding.tvText.isSelected = if (data.fenceType == FenceType.FULL) isFullSelect else data.fenceType == selectType
        holder.binding.clRoot.setOnClickListener {
            if (data.fenceType == FenceType.FULL) {
                isFullSelect = !isFullSelect
                onFenceListener?.invoke(data.fenceType, isFullSelect)
                if (selectType == FenceType.DEL) {
                    selectType = FenceType.FULL
                    notifyDataSetChanged()
                } else {
                    holder.binding.ivIcon.isSelected = isFullSelect
                    holder.binding.tvText.isSelected = isFullSelect
                }
            } else {
                if (data.fenceType != selectType) {
                    selectType = data.fenceType
                    onFenceListener?.invoke(data.fenceType, true)
                }
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    data class Data(@StringRes val stringId: Int, @DrawableRes val drawableId: Int, val fenceType: FenceType)
}