package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.R as MenuR
import com.topdon.lib.core.R

/**
 * Observation mode-menu2-High/Low temperature sourcemenu used by Adapter, [Chinese text]in progress.
 *
 * [Chinese text], high[Chinese text], low[Chinese text]
 *
 * Created by LCG on 2024/11/29.
 */
@SuppressLint("NotifyDataSetChanged")
internal class TempSourceAdapter : BaseMenuAdapter() {
    /**
     * [Chinese text]in progress[Chinese text] code.
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]in progress: -1
     * - [Chinese text]: 0
     * - high[Chinese text]: 1
     * - low[Chinese text]: 2
     */
    var selectCode: Int = -1
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    /**
     * Observation mode-menu2-High/Low temperature source point[Chinese text]eventlistener, [Chinese text]. 
     */
    var onTempSourceListener: ((code: Int) -> Unit)? = null

    private val dataArray: Array<Data> = arrayOf(
        Data(R.string.main_tab_second_dynamic_recognition, MenuR.drawable.selector_menu2_source_1_auto, 0),
        Data(R.string.main_tab_second_high_temperature_source, MenuR.drawable.selector_menu2_source_2_high, 1),
        Data(R.string.main_tab_second_low_temperature_source, MenuR.drawable.selector_menu2_source_3_low, 2),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataArray[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = data.code == selectCode
        holder.binding.tvText.isSelected = data.code == selectCode
        holder.binding.clRoot.setOnClickListener {
            selectCode = if (data.code == selectCode) -1 else data.code
            onTempSourceListener?.invoke(selectCode)
        }
    }

    override fun getItemCount(): Int = dataArray.size

    data class Data(@StringRes val stringId: Int, @DrawableRes val drawableId: Int, val code: Int)
}