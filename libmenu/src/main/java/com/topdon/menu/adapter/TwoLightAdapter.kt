package com.topdon.menu.adapter

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.topdon.lib.core.R // Import R from libapp for strings
import com.topdon.menu.R as MenuR // Import R from libmenu for drawables
import com.topdon.menu.constant.MenuType
import com.topdon.menu.constant.TwoLightType

/**
 * Adapter used for Temperature measurement mode - Menu 3 - Dual light menu.
 *
 * - Single light: Picture-in-picture, Blend extent
 * - Lite: Picture-in-picture, Blend extent
 * - Dual light: Dual light 1, Dual light 2, Infrared, Visible light, Calibration, Picture-in-picture, Blend extent
 * - TC007: Dual light, Infrared, Visible light, Calibration, Picture-in-picture, Blend extent
 * - 2D editing: No such menu
 *
 * Single light, Lite: Picture-in-picture, Blend extent are independently selectable
 *
 * Dual light: Dual light 1, Dual light 2, Infrared, Visible light are mutually exclusive; Calibration, Picture-in-picture, Blend extent are independently selectable
 *
 * TC007: Dual light, Infrared, Visible light, Picture-in-picture are mutually exclusive; Calibration, Blend extent are independently selectable
 *
 * Created by LCG on 2024/11/20.
 */
@SuppressLint("NotifyDataSetChanged")
internal class TwoLightAdapter(private val menuType: MenuType) : BaseMenuAdapter() {

    /**
     * Dual light menu click event listener.
     */
    var onTwoLightListener: ((twoLightType: TwoLightType, isSelected: Boolean) -> Unit)? = null

    /**
     * Currently selected single dual light type
     * - Single light: Should not use this property
     * - Lite: Should not use this property
     * - Dual light: Dual light 1, Dual light 2, Infrared, Visible light
     * - TC007: Dual light, Infrared, Visible light, Picture-in-picture
     */
    var twoLightType: TwoLightType
        get() {
            for (data in dataList) {
                if (data.isSingle && data.isSelected) {
                    return data.twoLightType
                }
            }
            return TwoLightType.TWO_LIGHT_1
        }
        set(value) {
            if (value == TwoLightType.CORRECT || value == TwoLightType.BLEND_EXTENT) {
                return
            }
            if (value == TwoLightType.P_IN_P && menuType != MenuType.TC007) {
                return
            }
            for (data in dataList) {
                if (data.isSingle) {
                    if (menuType == MenuType.TC007 && value == TwoLightType.TWO_LIGHT_1) {
                        // For TC007, both dual light 1 and dual light 2 are considered as dual light
                        data.isSelected = data.twoLightType == TwoLightType.TWO_LIGHT_2
                    } else {
                        data.isSelected = data.twoLightType == value
                    }
                }
            }
            notifyDataSetChanged()
        }

    /**
     * Set multiple selection state
     * - Single light: Picture-in-picture, Blend extent
     * - Lite: Picture-in-picture, Blend extent
     * - Dual light: Calibration, Picture-in-picture, Blend extent
     * - TC007: Calibration, Blend extent
     */
    fun setSelected(twoLightType: TwoLightType, isSelected: Boolean) {
        if (twoLightType == TwoLightType.TWO_LIGHT_1 || twoLightType == TwoLightType.TWO_LIGHT_2) {//Dual light1、Dual light2
            return
        }
        if (twoLightType == TwoLightType.IR || twoLightType == TwoLightType.LIGHT) {// Adapter itemInfrared、Visible light
            return
        }
        if (menuType == MenuType.TC007 && twoLightType == TwoLightType.P_IN_P) {//TC007 itemPicture in picture
            return
        }
        for (data in dataList) {
            if (data.twoLightType == twoLightType) {
                data.isSelected = isSelected
            }
        }
        notifyDataSetChanged()
    }



    private val dataList: ArrayList<Data> = ArrayList(7)

    init {
        if (menuType == MenuType.DOUBLE_LIGHT || menuType == MenuType.TC007) {
            if (menuType == MenuType.DOUBLE_LIGHT) {
                dataList.add(Data(R.string.dual_menu_1, MenuR.drawable.selector_menu2_two_light_1, TwoLightType.TWO_LIGHT_1, true))
                dataList.add(Data(R.string.dual_menu_2, MenuR.drawable.selector_menu2_two_light_2, TwoLightType.TWO_LIGHT_2, true))
            } else {
                dataList.add(Data(R.string.menu_thermal_merge, MenuR.drawable.selector_menu2_two_light_2, TwoLightType.TWO_LIGHT_2, true))
            }
            dataList.add(Data(R.string.menu_thermal_imaging, MenuR.drawable.selector_menu2_two_light_3, TwoLightType.IR, true))
            dataList.add(Data(R.string.menu_thermal_visible_light, MenuR.drawable.selector_menu2_two_light_4, TwoLightType.LIGHT, true))
            dataList.add(Data(R.string.menu_thermal_registration, MenuR.drawable.selector_menu2_two_light_5, TwoLightType.CORRECT, false))
        }
        dataList.add(Data(R.string.thermal_picture_in_camera, MenuR.drawable.selector_menu2_two_light_6, TwoLightType.P_IN_P, menuType == MenuType.TC007))
        dataList.add(Data(R.string.ios_double_light, MenuR.drawable.selector_menu2_two_light_7, TwoLightType.BLEND_EXTENT, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataList[position]
        holder.binding.ivIcon.setImageResource(data.drawableId)
        holder.binding.tvText.setText(data.stringId)
        holder.binding.ivIcon.isSelected = data.isSelected
        holder.binding.tvText.isSelected = data.isSelected
        holder.binding.clRoot.setOnClickListener {
            if (data.isSingle) {// Adapter item
                if (!data.isSelected) {// Adapter item
                    twoLightType = data.twoLightType
                    onTwoLightListener?.invoke(data.twoLightType, true)
                }
            } else {// Adapter item
                data.isSelected = !data.isSelected
                holder.binding.ivIcon.isSelected = data.isSelected
                holder.binding.tvText.isSelected = data.isSelected
                onTwoLightListener?.invoke(data.twoLightType, data.isSelected)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    /**
     * @param isSingle item，item1item，item Boolean item
     * @param isSelected CurrentitemSelected
     */
    data class Data(
        @StringRes val stringId: Int,
        @DrawableRes val drawableId: Int,
        val twoLightType: TwoLightType,
        val isSingle: Boolean,
        var isSelected: Boolean = false,
    )
}