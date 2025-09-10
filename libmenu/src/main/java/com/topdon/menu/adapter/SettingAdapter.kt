package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.menu.R as MenuR
import com.topdon.lib.core.R
import com.topdon.menu.constant.MenuType
import com.topdon.menu.constant.SettingType

/**
 * Adapter used for Settings menu, all options are independent and support multiple selection.
 *
 * - Single light: Pseudo color bar, Contrast, Sharpness, Alarm, Rotation, Font, Mirror
 * - Dual light: Pseudo color bar, Contrast, Sharpness, Alarm, Rotation, Font
 * - Lite: Pseudo color bar, Contrast, Alarm, Rotation, Font, Mirror
 * - TC007: Pseudo color bar, Contrast, Sharpness, Alarm, Font, Mirror
 * - 2D editing: Alarm, Font, Watermark
 *
 * - TS001 observation: Compass, Rotation, Mirror, Contrast
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class SettingAdapter(menuType: MenuType = MenuType.SINGLE_LIGHT, isObserver: Boolean = false) : BaseMenuAdapter() {
    /**
     * Settings menu click event listener.
     * isSelected: Whether in selected state when clicked
     */
    var onSettingListener: ((settingType: SettingType, isSelected: Boolean) -> Unit)? = null


    /**
     * item：
     * - itemCoreitem，256x192 itemRotateAngleitem 0 itemRotateState；
     * itemAPPitem，192x256 item(CoreRotateAngle270)itemRotateAngleitem 0 itemRotateState。
     * - item，CoreitemRotateAngleitemRotateAngle，itemRotateAngle。
     *
     * item，this propertyitem **CoreRotateAngle**
     */
    var rotateAngle: Int = 270
        set(value) {
            if (field != value) {
                field = value
                setSelected(SettingType.ROTATE, value != 270)
            }
        }

    /**
     * SettingsSpecifiedOptionitemSelectedState，Rotateitem，itemRotateitem 4 itemState
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
            if (menuType == MenuType.GALLERY_EDIT) {//2Ditem
                dataList.add(Data(R.string.temp_alarm_alarm, MenuR.drawable.selector_menu2_setting_6, SettingType.ALARM))
                dataList.add(Data(R.string.menu_thermal_font, MenuR.drawable.selector_menu2_setting_7, SettingType.FONT))
                dataList.add(Data(R.string.app_watemarking, MenuR.drawable.selector_menu2_setting_9, SettingType.WATERMARK))
            } else {
                dataList.add(Data(R.string.thermal_pseudo, MenuR.drawable.selector_menu2_setting_1, SettingType.PSEUDO_BAR))
                dataList.add(Data(R.string.thermal_contrast, MenuR.drawable.selector_menu2_setting_2, SettingType.CONTRAST))
                if (menuType != MenuType.Lite) {// Lite item(item)
                    dataList.add(Data(R.string.thermal_sharpen, MenuR.drawable.selector_menu2_setting_3, SettingType.DETAIL))
                }
                dataList.add(Data(R.string.temp_alarm_alarm, MenuR.drawable.selector_menu2_setting_6, SettingType.ALARM))
                if (menuType != MenuType.TC007) {// TC007 itemRotate
                    dataList.add(Data(R.string.thermal_rotate, MenuR.drawable.selector_menu2_setting_4, SettingType.ROTATE))
                }
                dataList.add(Data(R.string.menu_thermal_font, MenuR.drawable.selector_menu2_setting_7, SettingType.FONT))
                if (menuType != MenuType.DOUBLE_LIGHT) {// TC001 Plus item
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
            // Adapter item、item、itemSelecteditem，item，
            //MenuitemSelecteditem listener item，item
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