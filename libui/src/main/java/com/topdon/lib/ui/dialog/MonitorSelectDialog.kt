package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR

/**
 * 提示窗
 * create by fylder on 2018/6/15
 **/
class MonitorSelectDialog(context: Context) : Dialog(context, R.style.InfoDialog) {

    class Builder(private val context: Context) {
        /**
         * 是否处于第 1 步.
         */
        private var isFirstStep = true
        /**
         * 当前选中的监控类型 1-点 2-线 3-面.
         */
        private var monitorType = 0



        private var positiveClickListener: ((select: Int) -> Unit)? = null

        fun setPositiveListener(listener: ((select: Int) -> Unit)?): Builder {
            this.positiveClickListener = listener
            return this
        }


        fun create(): MonitorSelectDialog {
            val dialog = MonitorSelectDialog(context)
            dialog.setCanceledOnTouchOutside(false)
            
            val view = LayoutInflater.from(context).inflate(UiR.layout.dialog_monitor_select, null)
            dialog.setContentView(view)

            val lp = dialog.window!!.attributes
            lp.width = (ScreenUtil.getScreenWidth(context) * if (ScreenUtil.isPortrait(context)) 0.85 else 0.35).toInt() //设置宽度
            dialog.window!!.attributes = lp

            view.findViewById<Button>(UiR.id.btn_confirm_or_back).setOnClickListener {
                if (isFirstStep) {//步骤1->步骤2 逻辑为“确认”
                    if (monitorType == 0) {//还没选取类型不允许点确认
                        return@setOnClickListener
                    }
                    isFirstStep = false
                    view.findViewById<Button>(UiR.id.btn_cancel).visibility = View.VISIBLE
                    view.findViewById<ConstraintLayout>(UiR.id.cl_first_step).visibility = View.INVISIBLE
                    view.findViewById<ConstraintLayout>(UiR.id.cl_second_step).visibility = View.VISIBLE
                    view.findViewById<TextView>(UiR.id.tv_title).text = context.getString(R.string.select_monitor_type_step2)
                    view.findViewById<Button>(UiR.id.btn_confirm_or_back).text = context.getString(R.string.select_monitor_return)
                } else {//步骤2->步骤1 逻辑为“返回”
                    isFirstStep = true
                    view.findViewById<Button>(UiR.id.btn_cancel).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(UiR.id.cl_first_step).visibility = View.VISIBLE
                    view.findViewById<ConstraintLayout>(UiR.id.cl_second_step).visibility = View.GONE
                    view.findViewById<TextView>(UiR.id.tv_title).text = context.getString(R.string.select_monitor_type_step1)
                    view.findViewById<Button>(UiR.id.btn_confirm_or_back).text = context.getString(R.string.app_confirm)
                }
            }

            view.findViewById<Button>(UiR.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            view.findViewById<Button>(UiR.id.btn_select_location).setOnClickListener {
                dialog.dismiss()
                positiveClickListener?.invoke(monitorType)
            }

            view.findViewById<TextView>(UiR.id.tv_point).setOnClickListener {
                updateUI(view, 1)
            }
            view.findViewById<TextView>(UiR.id.tv_line).setOnClickListener {
                updateUI(view, 2)
            }
            view.findViewById<TextView>(UiR.id.tv_rect).setOnClickListener {
                updateUI(view, 3)
            }
            return dialog
        }


        private fun updateUI(view: View, index: Int) {
            view.findViewById<TextView>(UiR.id.tv_point).isSelected = index == 1
            view.findViewById<TextView>(UiR.id.tv_line).isSelected = index == 2
            view.findViewById<TextView>(UiR.id.tv_rect).isSelected = index == 3
            monitorType = index
        }
    }
}