package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR
import com.topdon.menu.constant.MenuType
import com.topdon.menu.constant.SettingType

/**
 * settingsmenuAdapter used for，所有选项互相独立，可多选.
 *
 * - 单光：   pseudo color条、对比度、锐度、warning、旋转、font、镜像
 * - dual light：   pseudo color条、对比度、锐度、warning、旋转、font
 * - Lite：  pseudo color条、对比度、warning、旋转、font、镜像
 * - TC007： pseudo color条、对比度、锐度、warning、font、镜像
 * - 2D 编辑：warning、font、watermark
 *
 * - TS001 observation：指南针、旋转、镜像、对比度
 *
 * Created by LCG on 2024/11/28.
 */
@SuppressLint("NotifyDataSetChanged")
internal class SettingAdapter(menuType: MenuType = MenuType.SINGLE_LIGHT, isObserver: Boolean = false) : BaseMenuAdapter() {
    /**
     * settingsmenuclickevent listener。
     * isSelected：click时是否处于selectedstate
     */
    var onSettingListener: ((settingType: SettingType, isSelected: Boolean) -> Unit)? = null

    /**
     * 这里有几个坑：
     * - 对于core而言，256x192 横屏尺寸才是rotation angle为 0 的未旋转state；
     * 对于APP而言，192x256 竖屏尺寸(corerotation angle270)才是rotation angle为 0 的未旋转state。
     * - 对某供应商而言，core里的rotation angle是逆时针rotation angle，而非一般理解的顺时针rotation angle。
     *
     * 考虑到旧代码兼容，这个属性用来放 **corerotation angle**
     */
    var rotateAngle: Int = 270
        set(value) {
            if (field != value) {
                field = value
                setSelected(SettingType.ROTATE, value != 270)
            }
        }

    /**
     * settingsspecified option的selectedstate，旋转不要调这个方法，因为旋转有 4 个state
     */
    fun setSelected(
        settingType: SettingType,
        isSelected: Boolean,
    ) {
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
            if (menuType == MenuType.GALLERY_EDIT) { // 2D编辑
                dataList.add(Data(R.string.temp_alarm_alarm, MenuR.drawable.selector_menu2_setting_6, SettingType.ALARM))
                dataList.add(Data(R.string.menu_thermal_font, MenuR.drawable.selector_menu2_setting_7, SettingType.FONT))
                dataList.add(Data(R.string.app_watemarking, MenuR.drawable.selector_menu2_setting_9, SettingType.WATERMARK))
            } else {
                dataList.add(Data(R.string.thermal_pseudo, MenuR.drawable.selector_menu2_setting_1, SettingType.PSEUDO_BAR))
                dataList.add(Data(R.string.thermal_contrast, MenuR.drawable.selector_menu2_setting_2, SettingType.CONTRAST))
                if (menuType != MenuType.Lite) { // Lite 没有细节(锐度)
                    dataList.add(Data(R.string.thermal_sharpen, MenuR.drawable.selector_menu2_setting_3, SettingType.DETAIL))
                }
                dataList.add(Data(R.string.temp_alarm_alarm, MenuR.drawable.selector_menu2_setting_6, SettingType.ALARM))
                if (menuType != MenuType.TC007) { // TC007 没有旋转
                    dataList.add(Data(R.string.thermal_rotate, MenuR.drawable.selector_menu2_setting_4, SettingType.ROTATE))
                }
                dataList.add(Data(R.string.menu_thermal_font, MenuR.drawable.selector_menu2_setting_7, SettingType.FONT))
                if (menuType != MenuType.DOUBLE_LIGHT) { // TC001 Plus 没有镜像
                    dataList.add(Data(R.string.mirror, MenuR.drawable.selector_menu2_setting_5, SettingType.MIRROR))
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
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
            // warning、font、watermark是以effective才视为highlightselected的，Maintain original code logic here，
            // menu的selectedrefreshleave to upper-layer listener to handle，consider changes later when time permits
//            data.isSelected = !data.isSelected
//            holder.binding.ivIcon.isSelected = data.isSelected
//            holder.binding.tvText.isSelected = data.isSelected
            onSettingListener?.invoke(data.settingType, data.isSelected)
        }
    }

    override fun getItemCount(): Int = dataList.size

/**
 * Custom Data view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes val drawableId: Int,
        val settingType: SettingType,
        var isSelected: Boolean = false,
    )
}
