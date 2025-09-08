package com.topdon.lib.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.utils.Constants.IR_OBSERVE_MODE
import com.topdon.lib.core.utils.Constants.IR_TCPLUS_MODE
import com.topdon.lib.core.utils.Constants.IR_TEMPERATURE_LITE
import com.topdon.lib.core.utils.Constants.IR_TEMPERATURE_MODE
import com.csl.irCamera.libui.R
import com.topdon.lib.ui.bean.ColorBean
import com.topdon.lib.ui.config.CameraHelp
import com.csl.irCamera.libui.databinding.UiItemMenuSecondViewBinding
import com.topdon.menu.R as MenuR
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.lib.ui.listener.SingleClickListener


@Deprecated("，")
@SuppressLint("NotifyDataSetChanged")
class MenuFourNightAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var listener: ((index: Int, code: Int) -> Unit)? = null

    private var colorEnable = false //
    private var contrastEnable = false //
    private var ddeEnable = false //
    private var alarmEnable = false //
    private var textColorEnable = false //
    private var mirrorEnable = false //
    private var waterMarkEnable = false //
    private var compassEnable = false //


    private var rotateAngle = DeviceConfig.S_ROTATE_ANGLE //0
    fun selectRotate(rotateAngle: Int) {
        this.rotateAngle = rotateAngle
        notifyDataSetChanged()
    }

    fun enColor(colorEnable: Boolean) {
        this.colorEnable = colorEnable
        notifyDataSetChanged()
    }

    fun enContrast(param: Boolean) {
        this.contrastEnable = param
        notifyDataSetChanged()
    }

    fun enDde(param: Boolean) {
        this.ddeEnable = param
        notifyDataSetChanged()
    }

    fun enAlarm(param: Boolean) {
        this.alarmEnable = param
        notifyDataSetChanged()
    }
    fun enTextColor(param: Boolean) {
        this.textColorEnable = param
        notifyDataSetChanged()
    }
    fun enMirror(param: Boolean) {
        this.mirrorEnable = param
        notifyDataSetChanged()
    }
    fun enCompass(param: Boolean) {
        this.compassEnable = param
        notifyDataSetChanged()
    }
    fun enWaterMark(param: Boolean) {
        this.waterMarkEnable = param
        notifyDataSetChanged()
    }

     *  [Constants.IR_TEMPERATURE_MODE] = 1
     *  [Constants.IR_TCPLUS_MODE] = 5
     *  [Constants.IR_TEMPERATURE_LITE] = 7 Lite
     * else - 2D
     *  [Constants.IR_OBSERVE_MODE] = 2
    fun setShowMenuFour(modeType: Int){
        fourBean.clear()
        when (modeType) {
            IR_TEMPERATURE_MODE -> {
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_1, context.getString(MenuR.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_2, context.getString(MenuR.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_3, context.getString(MenuR.string.thermal_sharpen), CameraHelp.TYPE_SET_ParamLevelDde))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_6, context.getString(MenuR.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_4, context.getString(MenuR.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_7, context.getString(MenuR.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_5, context.getString(MenuR.string.mirror), CameraHelp.TYPE_SET_MIRROR))
            }
            IR_TCPLUS_MODE -> {
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_1, context.getString(MenuR.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_2, context.getString(MenuR.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_3, context.getString(MenuR.string.thermal_sharpen), CameraHelp.TYPE_SET_ParamLevelDde))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_6, context.getString(MenuR.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_4, context.getString(MenuR.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_7, context.getString(MenuR.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
            }
            IR_TEMPERATURE_LITE -> {
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_1, context.getString(MenuR.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_2, context.getString(MenuR.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_6, context.getString(MenuR.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_4, context.getString(MenuR.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_7, context.getString(MenuR.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_5, context.getString(MenuR.string.mirror), CameraHelp.TYPE_SET_MIRROR))
            }
            IR_OBSERVE_MODE -> {
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_8, context.getString(MenuR.string.main_tab_second_compass), CameraHelp.TYPE_SET_COMPASS))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_4, context.getString(MenuR.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_5, context.getString(MenuR.string.mirror), CameraHelp.TYPE_SET_MIRROR))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_2, context.getString(MenuR.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
            }
            else -> {
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_6, context.getString(MenuR.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_7, context.getString(MenuR.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
                fourBean.add(ColorBean(MenuR.drawable.selector_menu2_setting_9, context.getString(MenuR.string.app_watemarking), CameraHelp.TYPE_SET_WATERMARK))
            }
        }
        notifyDataSetChanged()
    }

    private val fourBean = arrayListOf(
        ColorBean(MenuR.drawable.selector_menu2_setting_1, context.getString(MenuR.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR),
        ColorBean(MenuR.drawable.selector_menu2_setting_2, context.getString(MenuR.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast),
        ColorBean(MenuR.drawable.selector_menu2_setting_3, context.getString(MenuR.string.thermal_sharpen), CameraHelp.TYPE_SET_ParamLevelDde),
        ColorBean(MenuR.drawable.selector_menu2_setting_6, context.getString(MenuR.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM),
        ColorBean(MenuR.drawable.selector_menu2_setting_4, context.getString(MenuR.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE),
        ColorBean(MenuR.drawable.selector_menu2_setting_7, context.getString(MenuR.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR),
        ColorBean(MenuR.drawable.selector_menu2_setting_5, context.getString(MenuR.string.mirror), CameraHelp.TYPE_SET_MIRROR),
        )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UiItemMenuSecondViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        compassEnable = SaveSettingUtil.isOpenCompass
        return ItemView(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (holder is ItemView) {
            //Tabitem
            updateViewWidth(holder.itemView,holder.img)
            val bean = fourBean[position]
            holder.name.text = bean.name
            if(bean.code == CameraHelp.TYPE_SET_ROTATE){
                when (rotateAngle) {
                    0 -> {
                        holder.img.setImageResource(MenuR.drawable.svg_menu2_setting_4_rotate270)
                    }
                    90 -> {
                        holder.img.setImageResource(MenuR.drawable.svg_menu2_setting_4_rotate180)
                    }
                    180 -> {
                        holder.img.setImageResource(MenuR.drawable.svg_menu2_setting_4_rotate90)
                    }
                    270 -> {
                        holder.img.setImageResource(MenuR.drawable.svg_menu2_setting_4_rotate0)
                    }
                }
            }else{
                holder.img.setImageResource(bean.res)
            }
            holder.lay.setOnClickListener(object : SingleClickListener(){
                override fun onSingleClick() {
                    listener?.invoke(position, bean.code)
                }
            })
            when (bean.code) {
                CameraHelp.TYPE_SET_ROTATE -> {
                    when (rotateAngle) {
                        0 -> {
                            holder.name.setTextColor(ContextCompat.getColor(context, R.color.white))
                        }
                        90 -> {
                            holder.name.setTextColor(ContextCompat.getColor(context, R.color.white))
                        }
                        180 -> {
                            holder.name.setTextColor(ContextCompat.getColor(context, R.color.white))
                        }
                        270 -> {
                            holder.name.setTextColor(ContextCompat.getColor(context, LibAppR.color.font_third_color))
                        }
                    }
                }
                CameraHelp.TYPE_SET_ParamLevelDde -> {
                    iconUI(ddeEnable, holder.img, holder.name)
                }
                CameraHelp.TYPE_SET_ParamLevelContrast -> {
                    iconUI(contrastEnable, holder.img, holder.name)
                }
                CameraHelp.TYPE_SET_PSEUDOCOLOR -> {
                    iconUI(colorEnable, holder.img, holder.name)
                }
                CameraHelp.TYPE_SET_ALARM -> {
                    iconUI(alarmEnable, holder.img, holder.name)
                }
                CameraHelp.TYPE_SET_COLOR -> {
                    iconUI(textColorEnable, holder.img, holder.name)
                }
                CameraHelp.TYPE_SET_MIRROR -> {
                    iconUI(mirrorEnable, holder.img, holder.name)
                }
                CameraHelp.TYPE_SET_COMPASS -> {
                    iconUI(compassEnable, holder.img, holder.name)
                }
                CameraHelp.TYPE_SET_WATERMARK -> {
                    iconUI(waterMarkEnable, holder.img, holder.name)
                }
            }
        }
    }

    private fun iconUI(isActive: Boolean, img: ImageView, nameText: TextView) {
        img.isSelected = isActive
        if (isActive) {
            nameText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            nameText.setTextColor(ContextCompat.getColor(context, LibAppR.color.font_third_color))
        }
    }

    override fun getItemCount(): Int {
        return fourBean.size
    }

    private fun updateViewWidth(itemView: View, itemMenu:ImageView){

        if (fourBean.size <= 4){
            itemView.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }else{
            itemView.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
//        if (fourBean.size <= 4) {  //item41/4
//            val canSeeCount = fourBean.size //4
//            val with = (ScreenUtils.getScreenWidth() / canSeeCount)
//            itemView.layoutParams =
//                ViewGroup.LayoutParams(with, ViewGroup.LayoutParams.WRAP_CONTENT)
//            val imageSize = (ScreenUtils.getScreenWidth() * 62 / 375f).toInt()
//            val layoutParams = itemMenu.layoutParams
//            layoutParams.width = imageSize
//            layoutParams.height = imageSize
//            itemMenu.layoutParams = layoutParams
//        } else {    //item44.5item
//            val canSeeCount = 4.5 //4
//            val with = (ScreenUtils.getScreenWidth() / canSeeCount).toInt()
//            itemView.layoutParams =
//                ConstraintLayout.LayoutParams(with, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//            val imageSize = (ScreenUtils.getScreenWidth() * 62 / 375f).toInt()
//            val layoutParams = itemMenu.layoutParams
//            layoutParams.width = imageSize
//            layoutParams.height = imageSize
//            itemMenu.layoutParams = layoutParams
//        }
    }

    inner class ItemView(val binding: UiItemMenuSecondViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val lay: View = binding.itemMenuTabLay
        val img: ImageView = binding.itemMenuTabImg
        val name: TextView = binding.itemMenuTabText
    }


}