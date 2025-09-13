package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR
import com.topdon.menu.constant.TempPointType

/**
 * observation模式-menu5-high/low temperature点 menuAdapter used for，按旧逻辑存在全部未选择的state。
 *
 * - 高温点、低温点 互相独立，可多选
 * - {高温点、低温点} 与 删除 互斥
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class TempPointAdapter : BaseMenuAdapter() {
    /**
     * Observation mode - Menu 5 - High/Low temperature points click event listener.
     */
    var onTempPointListener: ((type: TempPointType, isSelected: Boolean) -> Unit)? = null

    /**
     * settings 高温点 或 低稳点 的selectedstate。
     */
    fun setSelected(
        tempPointType: TempPointType,
        isSelected: Boolean,
    ) {
        for (i in dataArray.indices) {
            if (dataArray[i].tempPointType == tempPointType) {
                dataArray[i].isSelected = isSelected
                notifyItemChanged(i)
                break
            }
        }
    }

    /**
     * clear所有menu的selectedstate。
     * Maintain original logic here, consider whether to directly delete selected items later。
     */
    fun clearAllSelect() {
        for (data in dataArray) {
            data.isSelected = false
        }
        notifyDataSetChanged()
    }

    private val dataArray: Array<Data> =
        arrayOf(
            Data(R.string.main_tab_second_high_temperature_point, MenuR.drawable.selector_menu2_temp_point_1, TempPointType.HIGH),
            Data(R.string.main_tab_second_low_temperature_point, MenuR.drawable.selector_menu2_temp_point_2, TempPointType.LOW),
            Data(R.string.thermal_delete, MenuR.drawable.selector_menu2_del, TempPointType.DELETE),
        )

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val data: Data = dataArray[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = data.isSelected
        holder.binding.tvText.isSelected = data.isSelected
        holder.binding.clRoot.setOnClickListener {
            if (data.tempPointType == TempPointType.DELETE) {
                if (!data.isSelected) { // selected时再次删除没卵用，未selected时才处理
                    for (temp in dataArray) {
                        temp.isSelected = temp.tempPointType == TempPointType.DELETE
                    }
                    notifyDataSetChanged()
                    onTempPointListener?.invoke(TempPointType.DELETE, true)
                }
            } else {
                data.isSelected = !data.isSelected
                holder.binding.ivIcon.isSelected = data.isSelected
                holder.binding.tvText.isSelected = data.isSelected
                if (data.isSelected) { // selected高温点、低温点时要把“删除”设为未selected；取消selected时不耦合删除
                    for (i in dataArray.indices) {
                        if (dataArray[i].tempPointType == TempPointType.DELETE && dataArray[i].isSelected) {
                            dataArray[i].isSelected = false
                            notifyItemChanged(i)
                        }
                    }
                }
                onTempPointListener?.invoke(data.tempPointType, data.isSelected)
            }
        }
    }

    override fun getItemCount(): Int = dataArray.size

/**
 * Custom Data view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes val drawableId: Int,
        val tempPointType: TempPointType,
        var isSelected: Boolean = false,
    )
}
