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
import com.topdon.lib.core.utils.Constants.IR_TC007_MODE
import com.topdon.lib.core.utils.Constants.IR_TCPLUS_MODE
import com.topdon.lib.core.utils.Constants.IR_TEMPERATURE_LITE
import com.topdon.lib.core.utils.Constants.IR_TEMPERATURE_MODE
import com.topdon.lib.ui.R
import com.topdon.lib.ui.bean.ColorBean
import com.topdon.lib.ui.config.CameraHelp
import com.topdon.lib.ui.listener.SingleClickListener
import kotlinx.android.synthetic.main.ui_item_menu_second_view.view.*


@Deprecated("[Chinese text]settingsmenu, [Chinese text]")
@SuppressLint("NotifyDataSetChanged")
class MenuFourNightAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var listener: ((index: Int, code: Int) -> Unit)? = null

    private var colorEnable = false //[Chinese text]
    private var contrastEnable = false //[Chinese text]
    private var ddeEnable = false //[Chinese text]
    private var alarmEnable = false //[Chinese text]
    private var textColorEnable = false //[Chinese text]
    private var mirrorEnable = false //[Chinese text]
    private var waterMarkEnable = false //[Chinese text]
    private var compassEnable = false //[Chinese text]


    private var rotateAngle = DeviceConfig.S_ROTATE_ANGLE //[Chinese text]0
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

    /**
     * [Chinese text]
     * [Chinese text] [Constants.IR_TEMPERATURE_MODE] = 1 [Chinese text]mode   [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]
     * [Chinese text] [Constants.IR_TCPLUS_MODE] = 5 dual light[Chinese text]        [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], 
     * [Chinese text] [Constants.IR_TEMPERATURE_LITE] = 7 Lite[Chinese text]  [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]
     * [Chinese text] [Constants.IR_TC007_MODE] = 6 TC007          [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text], [Chinese text]
     * else - 2D[Chinese text]menu                                  [Chinese text], [Chinese text], [Chinese text]
     * [Chinese text] [Constants.IR_OBSERVE_MODE] = 2 [Chinese text]mode  [Chinese text], [Chinese text], [Chinese text], [Chinese text]
     */
    fun setShowMenuFour(modeType: Int){
        fourBean.clear()
        when (modeType) {
            IR_TEMPERATURE_MODE -> {
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_1, context.getString(R.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_2, context.getString(R.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_3, context.getString(R.string.thermal_sharpen), CameraHelp.TYPE_SET_ParamLevelDde))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_6, context.getString(R.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_4, context.getString(R.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_7, context.getString(R.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_5, context.getString(R.string.mirror), CameraHelp.TYPE_SET_MIRROR))
            }
            IR_TCPLUS_MODE -> {
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_1, context.getString(R.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_2, context.getString(R.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_3, context.getString(R.string.thermal_sharpen), CameraHelp.TYPE_SET_ParamLevelDde))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_6, context.getString(R.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_4, context.getString(R.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_7, context.getString(R.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
            }
            IR_TEMPERATURE_LITE -> {
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_1, context.getString(R.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_2, context.getString(R.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_6, context.getString(R.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_4, context.getString(R.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_7, context.getString(R.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_5, context.getString(R.string.mirror), CameraHelp.TYPE_SET_MIRROR))
            }
            IR_TC007_MODE ->{
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_1, context.getString(R.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_2, context.getString(R.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_3, context.getString(R.string.thermal_sharpen), CameraHelp.TYPE_SET_ParamLevelDde))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_6, context.getString(R.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_7, context.getString(R.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_5, context.getString(R.string.mirror), CameraHelp.TYPE_SET_MIRROR))
            }
            IR_OBSERVE_MODE -> {
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_8, context.getString(R.string.main_tab_second_compass), CameraHelp.TYPE_SET_COMPASS))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_4, context.getString(R.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_5, context.getString(R.string.mirror), CameraHelp.TYPE_SET_MIRROR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_2, context.getString(R.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast))
            }
            else -> {
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_6, context.getString(R.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_7, context.getString(R.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR))
                fourBean.add(ColorBean(R.drawable.selector_menu2_setting_9, context.getString(R.string.app_watemarking), CameraHelp.TYPE_SET_WATERMARK))
            }
        }
        notifyDataSetChanged()
    }

    private val fourBean = arrayListOf(
        ColorBean(R.drawable.selector_menu2_setting_1, context.getString(R.string.thermal_pseudo), CameraHelp.TYPE_SET_PSEUDOCOLOR),
        ColorBean(R.drawable.selector_menu2_setting_2, context.getString(R.string.thermal_contrast), CameraHelp.TYPE_SET_ParamLevelContrast),
        ColorBean(R.drawable.selector_menu2_setting_3, context.getString(R.string.thermal_sharpen), CameraHelp.TYPE_SET_ParamLevelDde),
        ColorBean(R.drawable.selector_menu2_setting_6, context.getString(R.string.temp_alarm_alarm), CameraHelp.TYPE_SET_ALARM),
        ColorBean(R.drawable.selector_menu2_setting_4, context.getString(R.string.thermal_rotate), CameraHelp.TYPE_SET_ROTATE),
        ColorBean(R.drawable.selector_menu2_setting_7, context.getString(R.string.menu_thermal_font), CameraHelp.TYPE_SET_COLOR),
        ColorBean(R.drawable.selector_menu2_setting_5, context.getString(R.string.mirror), CameraHelp.TYPE_SET_MIRROR),
        )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ui_item_menu_second_view, parent, false)
        compassEnable = SaveSettingUtil.isOpenCompass
        return ItemView(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (holder is ItemView) {
            //[Chinese text]switchTab[Chinese text]item[Chinese text]
            updateViewWidth(holder.itemView,holder.img)
            val bean = fourBean[position]
            holder.name.text = bean.name
            if(bean.code == CameraHelp.TYPE_SET_ROTATE){
                when (rotateAngle) {
                    0 -> {
                        holder.img.setImageResource(R.drawable.svg_menu2_setting_4_rotate270)
                    }
                    90 -> {
                        holder.img.setImageResource(R.drawable.svg_menu2_setting_4_rotate180)
                    }
                    180 -> {
                        holder.img.setImageResource(R.drawable.svg_menu2_setting_4_rotate90)
                    }
                    270 -> {
                        holder.img.setImageResource(R.drawable.svg_menu2_setting_4_rotate0)
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
                            holder.name.setTextColor(ContextCompat.getColor(context, R.color.font_third_color))
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

    // [Chinese text]
    private fun iconUI(isActive: Boolean, img: ImageView, nameText: TextView) {
        img.isSelected = isActive
        if (isActive) {
            nameText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            nameText.setTextColor(ContextCompat.getColor(context, R.color.font_third_color))
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
//        if (fourBean.size <= 4) {  //item[Chinese text]4[Chinese text], [Chinese text]1/4
//            val canSeeCount = fourBean.size //[Chinese text]4[Chinese text]
//            val with = (ScreenUtils.getScreenWidth() / canSeeCount)
//            itemView.layoutParams =
//                ViewGroup.LayoutParams(with, ViewGroup.LayoutParams.WRAP_CONTENT)
//            val imageSize = (ScreenUtils.getScreenWidth() * 62 / 375f).toInt()
//            val layoutParams = itemMenu.layoutParams
//            layoutParams.width = imageSize
//            layoutParams.height = imageSize
//            itemMenu.layoutParams = layoutParams
//        } else {    //item[Chinese text]4[Chinese text], [Chinese text]4.5[Chinese text]item
//            val canSeeCount = 4.5 //[Chinese text]4[Chinese text]
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

    inner class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lay: View = itemView.item_menu_tab_lay
        val img: ImageView = itemView.item_menu_tab_img
        val name: TextView = itemView.item_menu_tab_text
    }


}