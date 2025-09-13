package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR
import com.topdon.menu.constant.TargetType

/**
 * observation模式-menu4-target menuAdapter used for.
 *
 * measurement mode(MODE)、target(STYLE)、targetcolor(COLOR)、删除(DELETE)、帮助(HELP)
 *
 * - measurement mode(MODE)、target(STYLE) 捆绑，要么都selected，要么都不selected，与 删除(DELETE) 互斥
 * - 删除(DELETE) 与 {measurement mode(MODE)、target(STYLE)、targetcolor(COLOR)} 互斥
 * - targetcolor(COLOR) effective且未处于删除亮，color为默认绿色或处于删除不亮，丢给上层维护这个state
 * - 帮助(HELP) 显示弹框亮，close弹框不亮，丢给上层维护这个state
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
     * settingsspecified option的selectedstate.
     * 对于一些互斥的selected取消selected操作，由于legacy现在先不改动，丢给上层去维护这个互斥state.
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
     * Set icon type for Observation mode - Menu 4 - Target - Measurement mode.
     *
     * Due to legacy constraints (saved in SharedPreferences), the code values are:
     * - Human: 10
     * - Sheep: 11
     * - Dog: 12
     * - Bird: 13
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
            // targetcolor以effective才视为highlightselected的，Maintain original code logic here，
            // menu的selectedrefreshleave to upper-layer listener to handle，consider changes later when time permits
//            data.isSelected = !data.isSelected
//            holder.binding.ivIcon.isSelected = data.isSelected
//            holder.binding.tvText.isSelected = data.isSelected
            onTargetListener?.invoke(data.targetType)
        }
    }

    override fun getItemCount(): Int = dataArray.size

/**
 * Custom Data view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes var drawableId: Int,
        val targetType: TargetType,
        var isSelected: Boolean = false,
    )
}
