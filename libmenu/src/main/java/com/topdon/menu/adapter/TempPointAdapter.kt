package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.R as MenuR
import com.topdon.lib.core.R
import com.topdon.menu.constant.TempPointType

/**
 * Adapter used for Observation mode - Menu 5 - High/Low temperature points menu, allows all unselected state according to legacy logic.
 *
 * - High temperature point and Low temperature point are independent, support multiple selection
 * - {High temperature point, Low temperature point} are mutually exclusive with Delete
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
     * Set selection state of High temperature point or Low temperature point.
     */
    fun setSelected(tempPointType: TempPointType, isSelected: Boolean) {
        for (i in dataArray.indices) {
            if (dataArray[i].tempPointType == tempPointType) {
                dataArray[i].isSelected = isSelected
                notifyItemChanged(i)
                break
            }
        }
    }

    /**
     * ClearAllMenu[CN_TEXT]SelectedState。
     * [CN_TEXT]Maintain original logic，Consider in the futureWhether to directlySelectedDeletedone。
     */
    fun clearAllSelect() {
        for (data in dataArray) {
            data.isSelected = false
        }
        notifyDataSetChanged()
    }


    private val dataArray: Array<Data> = arrayOf(
        Data(R.string.main_tab_second_high_temperature_point, MenuR.drawable.selector_menu2_temp_point_1, TempPointType.HIGH),
        Data(R.string.main_tab_second_low_temperature_point, MenuR.drawable.selector_menu2_temp_point_2, TempPointType.LOW),
        Data(R.string.thermal_delete, MenuR.drawable.selector_menu2_del, TempPointType.DELETE),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataArray[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = data.isSelected
        holder.binding.tvText.isSelected = data.isSelected
        holder.binding.clRoot.setOnClickListener {
            if (data.tempPointType == TempPointType.DELETE) {
                if (!data.isSelected) {//Selected[CN_TEXT]Delete[CN_TEXT]，[CN_TEXT]Selected[CN_TEXT]
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
                if (data.isSelected) {//SelectedHigh temperature[CN_TEXT]、Low temperature[CN_TEXT]“Delete”[CN_TEXT]Selected；[CN_TEXT]Selected[CN_TEXT]Delete
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

    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes val drawableId: Int,
        val tempPointType: TempPointType,
        var isSelected: Boolean = false,
    )
}