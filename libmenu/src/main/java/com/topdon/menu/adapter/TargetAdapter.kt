package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.lib.core.R
import com.topdon.menu.constant.TargetType
import com.topdon.menu.R as MenuR

/**
 * Adapter used for Observation mode - Menu 4 - Target menu.
 *
 * Measurement mode (MODE), Target (STYLE), Target color (COLOR), Delete (DELETE), Help (HELP)
 *
 * - Measurement mode (MODE) and Target (STYLE) are bundled, either both selected or both unselected, mutually exclusive with Delete (DELETE)
 * - Delete (DELETE) is mutually exclusive with {Measurement mode (MODE), Target (STYLE), Target color (COLOR)}
 * - Target color (COLOR) is effective and not in delete state - color is default green, or in delete state - not lit, leave state management to upper layer
 * - Help (HELP) shows dialog - lit, closes dialog - not lit, leave state management to upper layer
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class TargetAdapter : BaseMenuAdapter() {
    /**
     * Observation mode - Menu 4 - Target click event listener.
     */
    var onTargetListener: ((targetType: TargetType) -> Unit)? = null

    /**
     * SettingsSpecifiedOptionitemSelectedState.
     * itemSelecteditemSelecteditem，itemHistorical legacyitem，itemState.
     */
    fun setSelected(
        targetType: TargetType,
        isSelected: Boolean,
    ) {
        for (i in dataArray.indices) {
            if (dataArray[i].targetType == targetType) {
                dataArray[i].isSelected = isSelected
                notifyItemChanged(i)
                break
            }
        }
    }

    /**
     * Settings ObservationMode-Menu4-Target-itemMode itemType.
     *
     * itemHistorical legacy（Already saveditem SharedPreferences item），item code item
     * - Person：10
     * - Sheep：11
     * - Dog：12
     * - Bird：13
     */
    fun setTargetMode(modeCode: Int) {
        for (i in dataArray.indices) {
            if (dataArray[i].targetType == TargetType.MODE) {
                dataArray[i].drawableId =
                    when (modeCode) {
                        11 -> MenuR.drawable.selector_menu2_target_1_sheep
                        12 -> MenuR.drawable.selector_menu2_target_1_dog
                        13 -> MenuR.drawable.selector_menu2_target_1_bird
                        else -> MenuR.drawable.selector_menu2_target_1_person
                    }
                notifyItemChanged(i)
                break
            }
        }
    }

    private val dataArray: Array<Data> =
        arrayOf(
            Data(R.string.main_tab_second_measure_mode, MenuR.drawable.selector_menu2_target_1_person, TargetType.MODE),
            Data(R.string.main_tab_first_target, MenuR.drawable.selector_menu2_target_2_style, TargetType.STYLE),
            Data(R.string.main_tab_second_target_color, MenuR.drawable.selector_menu2_target_3_color, TargetType.COLOR),
            Data(R.string.thermal_delete, MenuR.drawable.selector_menu2_del, TargetType.DELETE),
            Data(R.string.main_tab_second_target_help, MenuR.drawable.selector_menu2_target_4_help, TargetType.HELP),
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
onTargetListener?.invoke(data.targetType)
        }
    }

    override fun getItemCount(): Int = dataArray.size

    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes var drawableId: Int,
        val targetType: TargetType,
        var isSelected: Boolean = false,
    )
}
