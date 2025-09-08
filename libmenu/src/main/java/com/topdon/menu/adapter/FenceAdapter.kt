package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.constant.MenuType
import com.topdon.menu.R as MenuR
import com.topdon.lib.core.R
import com.topdon.menu.constant.FenceType

/**
 * point, line, [Chinese text], [Chinese text], [Chinese text]([Chinese text]), [Chinese text] menu Adapter.
 *
 * - [Chinese text]:    point, line, [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - Dual light:    point, line, [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - Lite:   point, line, [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - TC007:  point, line, [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - 2D [Chinese text]: point, line, [Chinese text], [Chinese text], [Chinese text]
 *
 * point, line, [Chinese text], [Chinese text], [Chinese text] [Chinese text] [Chinese text]
 *
 * point, line, [Chinese text], [Chinese text] [Chinese text], [Chinese text]
 *
 * Created by LCG on 2024/11/18.
 */
@SuppressLint("NotifyDataSetChanged")
internal class FenceAdapter(menuType: MenuType) : BaseMenuAdapter() {
    /**
     * [Chinese text]in progress[Chinese text]menu[Chinese text], [Chinese text] null [Chinese text]in progress.
     */
    var selectType: FenceType? = null
        set(value) {
            when (value) {
                FenceType.FULL -> isFullSelect = true
                FenceType.DEL -> isFullSelect = false
                else -> {// point, line, [Chinese text], [Chinese text], [Chinese text]

                }
            }
            field = value
            notifyDataSetChanged()
        }
    /**
     * [Chinese text]in progress.
     */
    private var isFullSelect: Boolean = false

    /**
     * menupoint[Chinese text]eventlistener, [Chinese text], [Chinese text], [Chinese text] IOS [Chinese text]"[Chinese text]"[Chinese text]. 
     */
    var onFenceListener: ((fenceType: FenceType, isSelected: Boolean) -> Unit)? = null

    private val dataList: ArrayList<Data> = ArrayList(6)

    init {
        dataList.add(Data(R.string.thermal_point, MenuR.drawable.selector_menu2_fence_point, FenceType.POINT))
        dataList.add(Data(R.string.thermal_line, MenuR.drawable.selector_menu2_fence_line, FenceType.LINE))
        dataList.add(Data(R.string.thermal_rect, MenuR.drawable.selector_menu2_fence_rect, FenceType.RECT))
        dataList.add(Data(R.string.thermal_full_rect, MenuR.drawable.selector_menu2_fence_full, FenceType.FULL))
        if (menuType != MenuType.GALLERY_EDIT) {// 2D[Chinese text]menu[Chinese text]
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