package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.R as MenuR
import com.topdon.lib.core.R
import com.topdon.menu.constant.TargetType

/**
 * Observation mode-menu4-Target menuused by Adapter.
 *
 * measurementmode(MODE), Target(STYLE), Target[Chinese text](COLOR), [Chinese text](DELETE), [Chinese text](HELP)
 *
 * - measurementmode(MODE), Target(STYLE) [Chinese text], [Chinese text]in progress, [Chinese text]in progress, [Chinese text] [Chinese text](DELETE) [Chinese text]
 * - [Chinese text](DELETE) [Chinese text] {measurementmode(MODE), Target(STYLE), Target[Chinese text](COLOR)} [Chinese text]
 * - Target[Chinese text](COLOR) [Chinese text], [Chinese text], [Chinese text]
 * - [Chinese text](HELP) [Chinese text], [Chinese text], [Chinese text]
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class TargetAdapter : BaseMenuAdapter() {

    /**
     * Observation mode-menu4-Target point[Chinese text]eventlistener.
     */
    var onTargetListener: ((targetType: TargetType) -> Unit)? = null

    /**
     * Settings[Chinese text]in progress[Chinese text].
     * [Chinese text]in progress[Chinese text]in progressoperation, [Chinese text], [Chinese text].
     */
    fun setSelected(targetType: TargetType, isSelected: Boolean) {
        for (i in dataArray.indices) {
            if (dataArray[i].targetType == targetType) {
                dataArray[i].isSelected = isSelected
                notifyItemChanged(i)
                break
            }
        }
    }

    /**
     * Settings Observation mode-menu4-Target-measurementmode [Chinese text].
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]: 10
     * - [Chinese text]: 11
     * - [Chinese text]: 12
     * - [Chinese text]: 13
     */
    fun setTargetMode(modeCode: Int) {
        for (i in dataArray.indices) {
            if (dataArray[i].targetType == TargetType.MODE) {
                dataArray[i].drawableId = when (modeCode) {
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

    private val dataArray: Array<Data> = arrayOf(
        Data(R.string.main_tab_second_measure_mode, MenuR.drawable.selector_menu2_target_1_person, TargetType.MODE),
        Data(R.string.main_tab_first_target, MenuR.drawable.selector_menu2_target_2_style, TargetType.STYLE),
        Data(R.string.main_tab_second_target_color, MenuR.drawable.selector_menu2_target_3_color, TargetType.COLOR),
        Data(R.string.thermal_delete, MenuR.drawable.selector_menu2_del, TargetType.DELETE),
        Data(R.string.main_tab_second_target_help, MenuR.drawable.selector_menu2_target_4_help, TargetType.HELP),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataArray[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = data.isSelected
        holder.binding.tvText.isSelected = data.isSelected
        holder.binding.clRoot.setOnClickListener {
            // Target[Chinese text]high[Chinese text]in progress[Chinese text], [Chinese text], 
            // menu[Chinese text]in progress[Chinese text] listener [Chinese text], [Chinese text]
//            data.isSelected = !data.isSelected
//            holder.binding.ivIcon.isSelected = data.isSelected
//            holder.binding.tvText.isSelected = data.isSelected
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