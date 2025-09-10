package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.lib.core.R
import com.topdon.menu.constant.MenuType
import com.topdon.menu.R as MenuR

/**
 * Adapter used for Temperature measurement mode - Menu 6 - High/Low temperature level menu, single selection and must select one.
 *
 * Low temperature level (high gain), High temperature level (low gain), Auto switch
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class TempLevelAdapter(menuType: MenuType) : BaseMenuAdapter() {
    /**
     * Whether to use Fahrenheit as unit
     *
     * true - Fahrenheit, false - Celsius
     */
    var isUnitF = false
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    /**
     * CurrentSelecteditem code.
     *
     * itemHistorical legacy（Already saveditem SharedPreferences item），item code item
     * - itemSwitch：-1
     * - High temperature(Low gain)：0
     * - Normal temperature(High gain)：1
     */
    var selectCode: Int = 1
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    /**
     * Menuitem，item。
     */
    var onTempLevelListener: ((code: Int) -> Unit)? = null

    private val dataList: ArrayList<Data> = ArrayList(6)

    init {
        dataList.add(
            Data(
                R.string.thermal_normal_temperature,
                MenuR.drawable.selector_menu2_temp_level_1,
                IntRange(-20, 150),
                1,
            ),
        )
        if (menuType == MenuType.Lite) {
            dataList.add(
                Data(
                    R.string.thermal_high_temperature,
                    MenuR.drawable.selector_menu2_temp_level_1,
                    IntRange(150, 450),
                    0,
                ),
            )
        } else {
            dataList.add(
                Data(
                    R.string.thermal_high_temperature,
                    MenuR.drawable.selector_menu2_temp_level_1,
                    IntRange(150, 550),
                    0,
                ),
            )
        }
        dataList.add(Data(R.string.thermal_automatic, MenuR.drawable.selector_menu2_temp_level_2, code = -1))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val data: Data = dataList[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.tvIconText.text = data.range?.getTempStr(isUnitF) ?: ""
        holder.binding.ivIcon.isSelected = data.code == selectCode
        holder.binding.tvText.isSelected = data.code == selectCode
        holder.binding.tvIconText.isSelected = data.code == selectCode
        holder.binding.clRoot.setOnClickListener {
            if (data.code != selectCode) {
                selectCode = data.code
                onTempLevelListener?.invoke(data.code)
            }
        }
    }

    private fun IntRange.getTempStr(isUnitF: Boolean): String =
        if (isUnitF) {
            "${c2f(start)}\n~\n${c2f(endInclusive)}°F"
        } else {
            "${start}\n~\n$endInclusive°C"
        }

    /**
     * itemSpecified Celsius°C item Fahrenheit°F
     */
    private fun c2f(cValue: Int): Int = (cValue * 1.8f + 32).toInt()

    override fun getItemCount(): Int = dataList.size

    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes val drawableId: Int,
        val range: IntRange? = null,
        val code: Int,
    )
}
