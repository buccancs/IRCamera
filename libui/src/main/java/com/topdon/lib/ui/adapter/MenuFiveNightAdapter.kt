package com.topdon.lib.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.ui.R
import com.topdon.lib.ui.bean.TemperatureBean
import com.topdon.lib.ui.databinding.UiItemMenuFiveViewBinding

@Deprecated("旧的温度档位菜单，已重构过了")
class MenuFiveNightAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onTempLevelListener: ((index: Int) -> Unit)? = null

    private var selectedCode = SaveSettingUtil.temperatureMode

    fun selected(code: Int) {
        selectedCode = code
        notifyDataSetChanged()
    }

    private val fiveBean = arrayListOf(
        TemperatureBean(
            R.drawable.selector_menu2_temp_level_1,
            context.getString(R.string.thermal_normal_temperature),
            getTempStr(-20, 150),
            CameraItemBean.TYPE_TMP_C
        ),
        if (DeviceTools.isTC001LiteConnect()) {
            TemperatureBean(
                R.drawable.selector_menu2_temp_level_1,
                context.getString(R.string.thermal_high_temperature),
                getTempStr(150, 450),
                CameraItemBean.TYPE_TMP_H
            )
        } else {
            TemperatureBean(
                R.drawable.selector_menu2_temp_level_1,
                context.getString(R.string.thermal_high_temperature),
                getTempStr(150, 550),
                CameraItemBean.TYPE_TMP_H
            )
        },
        TemperatureBean(
            R.drawable.selector_menu2_temp_level_2,
            context.getString(R.string.thermal_automatic),
            "",
            CameraItemBean.TYPE_TMP_ZD
        ),
    )

    private fun getTempStr(min: Int, max: Int): String = if (SharedManager.getTemperature() == 1) {
        "${min}\n~\n${max}°C"
    } else {
        "${(min * 1.8 + 32).toInt()}\n~\n${(max * 1.8 + 32).toInt()}°F"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UiItemMenuFiveViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            holder.binding.itemMenuTabImg.setImageResource(fiveBean[position].res)
            holder.binding.itemMenuTabLay.setOnClickListener {
                onTempLevelListener?.invoke(fiveBean[position].code)
                selected(fiveBean[position].code)
            }
            holder.binding.itemMenuTabImg.isSelected = fiveBean[position].code == selectedCode
            holder.binding.itemMenuTabText.text = fiveBean[position].name
            holder.binding.itemMenuTabInfoText.text = fiveBean[position].info
            holder.binding.itemMenuTabText.isSelected = fiveBean[position].code == selectedCode
            holder.binding.itemMenuTabInfoText.isSelected = fiveBean[position].code == selectedCode
            holder.binding.itemMenuTabText.setTextColor(
                if (fiveBean[position].code == selectedCode) ContextCompat.getColor(context, R.color.white)
                else ContextCompat.getColor(context, R.color.font_third_color)
            )
            holder.binding.itemMenuTabInfoText.setTextColor(
                if (fiveBean[position].code == selectedCode) ContextCompat.getColor(context, R.color.color_FFBA42)
                else ContextCompat.getColor(context, R.color.font_third_color)
            )
        }
    }

    override fun getItemCount(): Int {
        return fiveBean.size
    }

    inner class ItemView(val binding: UiItemMenuFiveViewBinding) : RecyclerView.ViewHolder(binding.root)

}