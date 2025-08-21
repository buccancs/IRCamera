package com.topdon.lib.core.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.TextView
import com.blankj.utilcode.util.SizeUtils
import com.topdon.tc001.R
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.UnitTools

class EmissivityTipPopup(val context: Context, val isTC007: Boolean) {
    private var text: String = ""
    private var radiation: Float = 0f
    private var distance: Float = 0f
    private var environment: Float = 0f
    private var popupWindow: PopupWindow? = null
    private lateinit var view: View
    private var titleText: TextView? = null
    private var messageText: TextView? = null
    private var checkBox: CheckBox? = null
    private var closeEvent: ((check: Boolean) -> Unit)? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_popup_tip_emissivity, null)
    }

    fun setTitle(title: String): EmissivityTipPopup {
        titleText?.text = title
        return this
    }

    fun setMessage(message: String): EmissivityTipPopup {
        messageText?.text = message
        return this
    }

    fun setDataBean(environment: Float,distance : Float,radiation : Float,text : String): EmissivityTipPopup {
        this.environment = environment
        this.distance = distance
        this.radiation = radiation
        this.text = text
        return this
    }

    fun setCancelListener(event: ((check: Boolean) -> Unit)?): EmissivityTipPopup {
        this.closeEvent = event
        return this
    }

    fun build(): PopupWindow {
        if (popupWindow == null) {
            val tvEnvironmentTitle: TextView = view.findViewById(R.id.tv_environment_title)
            val tvDistanceTitle: TextView = view.findViewById(R.id.tv_distance_title)
            val tvTitle: TextView = view.findViewById(R.id.tv_title)
            val tvEmissivityMaterials: TextView = view.findViewById(R.id.tv_emissivity_materials)
            val dialogTipCancelBtn: Button = view.findViewById(R.id.dialog_tip_cancel_btn)
            val dialogTipSuccessBtn: Button = view.findViewById(R.id.dialog_tip_success_btn)
            val dialogTipCheck: CheckBox = view.findViewById(R.id.dialog_tip_check)
            val tvEmissivity: TextView = view.findViewById(R.id.tv_emissivity)
            val tvEnvironmentValue: TextView = view.findViewById(R.id.tv_environment_value)
            val tvDistanceValue: TextView = view.findViewById(R.id.tv_distance_value)
            
            tvEnvironmentTitle.text = context.getString(R.string.thermal_config_environment) + ":"
            tvDistanceTitle.text = context.getString(R.string.thermal_config_distance) + ":"

            tvTitle.visibility = View.GONE
            if (text.isNotEmpty()){
                tvEmissivityMaterials.text = text
                tvEmissivityMaterials.visibility = View.VISIBLE
            }else{
                tvEmissivityMaterials.visibility = View.GONE
            }
            dialogTipCancelBtn.visibility = View.GONE
            dialogTipSuccessBtn.text = context.getString(R.string.tc_modify_params)
            dialogTipCheck.visibility = View.GONE
            tvEmissivity.text = "${context?.getString(R.string.thermal_config_radiation)}: ${
                NumberTools.to02(radiation)}"
            tvEnvironmentValue.text = UnitTools.showC(environment)
            tvDistanceValue.text = "${NumberTools.to02(distance)}m"
            popupWindow = PopupWindow(
                view,
                SizeUtils.dp2px(275f),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow?.apply {
                isFocusable = true
                isOutsideTouchable = true
                isTouchable = true
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 必要时可以替换为其他Drawable
            }
            dialogTipSuccessBtn.setOnClickListener {
            // TODO: Replace RouterConfig reference with direct navigation
                dismiss()
            }
        }
        return popupWindow!!
    }

    fun show(anchorView: View) {
        popupWindow?.showAtLocation(anchorView, Gravity.CENTER, -SizeUtils.dp2px(10f), 0)
    }

    fun dismiss() {
        popupWindow?.dismiss()
        closeEvent?.invoke(checkBox?.isChecked ?: false)
    }
}