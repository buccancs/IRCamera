package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.R as MenuR
import com.topdon.lib.core.R
import com.topdon.menu.constant.MenuType
import com.topdon.menu.constant.SettingType

/**
 * Settingsmenuused by Adapter, [Chinese text], [Chinese text].
 *
 * - [Chinese text]:    Pseudo color[Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - Dual light:    Pseudo color[Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - Lite:   Pseudo color[Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - TC007:  Pseudo color[Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 * - 2D [Chinese text]: [Chinese text], [Chinese text], [Chinese text]
 *
 * - TS001 [Chinese text]: [Chinese text], [Chinese text], [Chinese text], [Chinese text]
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class SettingAdapter(menuType: MenuType = MenuType.SINGLE_LIGHT, isObserver: Boolean = false) : BaseMenuAdapter() {
    /**
     * Settingsmenupoint[Chinese text]eventlistener. 
     * isSelected: point[Chinese text]in progress[Chinese text]
     */
    var onSettingListener: ((settingType: SettingType, isSelected: Boolean) -> Unit)? = null

    /**
     * [Chinese text]: 
     * - [Chinese text], 256x192 [Chinese text] 0 [Chinese text]; 
     * [Chinese text]APP[Chinese text], 192x256 [Chinese text]([Chinese text]270)[Chinese text] 0 [Chinese text]. 
     * - [Chinese text], [Chinese text], [Chinese text]. 
     *
     * [Chinese text], [Chinese text] **[Chinese text]**
     */
    var rotateAngle: Int = 270
        set(value) {
            if (field != value) {
                field = value
                setSelected(SettingType.ROTATE, value != 270)
            }
        }

    /**
     * Settings[Chinese text]in progress[Chinese text], [Chinese text], [Chinese text] 4 [Chinese text]
     */
    fun setSelected(settingType: SettingType, isSelected: Boolean) {
        for (i in dataList.indices) {
            if (dataList[i].settingType == settingType) {
                dataList[i].isSelected = isSelected
                notifyItemChanged(i)
                break
            }
        }
    }

    private val dataList: ArrayList<Data> = ArrayList(7)

    init {
        if (isObserver) {
            dataList.add(Data(R.string.main_tab_second_compass, MenuR.drawable.selector_menu2_setting_8, SettingType.COMPASS))
            dataList.add(Data(R.string.thermal_rotate, MenuR.drawable.selector_menu2_setting_4, SettingType.ROTATE))
            dataList.add(Data(R.string.mirror, MenuR.drawable.selector_menu2_setting_5, SettingType.MIRROR))
            dataList.add(Data(R.string.thermal_contrast, MenuR.drawable.selector_menu2_setting_2, SettingType.CONTRAST))
        } else {
            if (menuType == MenuType.GALLERY_EDIT) {// 2D[Chinese text]
                dataList.add(Data(R.string.temp_alarm_alarm, MenuR.drawable.selector_menu2_setting_6, SettingType.ALARM))
                dataList.add(Data(R.string.menu_thermal_font, MenuR.drawable.selector_menu2_setting_7, SettingType.FONT))
                dataList.add(Data(R.string.app_watemarking, MenuR.drawable.selector_menu2_setting_9, SettingType.WATERMARK))
            } else {
                dataList.add(Data(R.string.thermal_pseudo, MenuR.drawable.selector_menu2_setting_1, SettingType.PSEUDO_BAR))
                dataList.add(Data(R.string.thermal_contrast, MenuR.drawable.selector_menu2_setting_2, SettingType.CONTRAST))
                if (menuType != MenuType.Lite) {// Lite [Chinese text]([Chinese text])
                    dataList.add(Data(R.string.thermal_sharpen, MenuR.drawable.selector_menu2_setting_3, SettingType.DETAIL))
                }
                dataList.add(Data(R.string.temp_alarm_alarm, MenuR.drawable.selector_menu2_setting_6, SettingType.ALARM))
                if (menuType != MenuType.TC007) {// TC007 [Chinese text]
                    dataList.add(Data(R.string.thermal_rotate, MenuR.drawable.selector_menu2_setting_4, SettingType.ROTATE))
                }
                dataList.add(Data(R.string.menu_thermal_font, MenuR.drawable.selector_menu2_setting_7, SettingType.FONT))
                if (menuType != MenuType.DOUBLE_LIGHT) {// TC001 Plus [Chinese text]
                    dataList.add(Data(R.string.mirror, MenuR.drawable.selector_menu2_setting_5, SettingType.MIRROR))
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataList[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        if (data.settingType == SettingType.ROTATE) {
            when (rotateAngle) {
                0 -> holder.binding.ivIcon.setImageLevel(270)
                90 -> holder.binding.ivIcon.setImageLevel(180)
                180 -> holder.binding.ivIcon.setImageLevel(90)
                270 -> holder.binding.ivIcon.setImageLevel(0)
            }
        } else {
            holder.binding.ivIcon.isSelected = data.isSelected
        }
        holder.binding.tvText.isSelected = data.isSelected
        holder.binding.clRoot.setOnClickListener {
            // [Chinese text], [Chinese text], [Chinese text]high[Chinese text]in progress[Chinese text], [Chinese text], 
            // menu[Chinese text]in progress[Chinese text] listener [Chinese text], [Chinese text]
//            data.isSelected = !data.isSelected
//            holder.binding.ivIcon.isSelected = data.isSelected
//            holder.binding.tvText.isSelected = data.isSelected
            onSettingListener?.invoke(data.settingType, data.isSelected)
        }
    }

    override fun getItemCount(): Int = dataList.size

    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes val drawableId: Int,
        val settingType: SettingType,
        var isSelected: Boolean = false,
    )
}