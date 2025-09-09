package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.topdon.lib.core.R
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.databinding.DialogMonitorSelectBinding
import com.topdon.lib.ui.R as UiR

/**
 * [CN_TEXT]
 * create by fylder on 2018/6/15
 **/
class MonitorSelectDialog(context: Context) : Dialog(context, R.style.InfoDialog) {
    class Builder(private val context: Context) {
        /**
         * [CN_TEXT] 1 [CN_TEXT].
         */
        private var isFirstStep = true

        /**
         * CurrentSelected[CN_TEXT]Type 1-[CN_TEXT] 2-[CN_TEXT] 3-[CN_TEXT].
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

            val binding = DialogMonitorSelectBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            val lp = dialog.window!!.attributes
            lp.width = (ScreenUtil.getScreenWidth(context) * if (ScreenUtil.isPortrait(context)) 0.85 else 0.35).toInt() // Settings[CN_TEXT]
            dialog.window!!.attributes = lp

            binding.btnConfirmOrBack.setOnClickListener {
                if (isFirstStep) { // [CN_TEXT]1->[CN_TEXT]2 [CN_TEXT]“[CN_TEXT]”
                    if (monitorType == 0) { // [CN_TEXT]Type[CN_TEXT]
                        return@setOnClickListener
                    }
                    isFirstStep = false
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.clFirstStep.visibility = View.INVISIBLE
                    binding.clSecondStep.visibility = View.VISIBLE
                    binding.tvTitle.text = context.getString(R.string.select_monitor_type_step2)
                    binding.btnConfirmOrBack.text = context.getString(R.string.select_monitor_return)
                } else { // [CN_TEXT]2->[CN_TEXT]1 [CN_TEXT]“[CN_TEXT]”
                    isFirstStep = true
                    binding.btnCancel.visibility = View.GONE
                    binding.clFirstStep.visibility = View.VISIBLE
                    binding.clSecondStep.visibility = View.GONE
                    binding.tvTitle.text = context.getString(R.string.select_monitor_type_step1)
                    binding.btnConfirmOrBack.text = context.getString(R.string.app_confirm)
                }
            }

            binding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            binding.btnSelectLocation.setOnClickListener {
                dialog.dismiss()
                positiveClickListener?.invoke(monitorType)
            }

            binding.tvPoint.setOnClickListener {
                updateUI(binding, 1)
            }
            binding.tvLine.setOnClickListener {
                updateUI(binding, 2)
            }
            binding.tvRect.setOnClickListener {
                updateUI(binding, 3)
            }
            return dialog
        }

        private fun updateUI(
            binding: DialogMonitorSelectBinding,
            index: Int,
        ) {
            binding.tvPoint.isSelected = index == 1
            binding.tvLine.isSelected = index == 2
            binding.tvRect.isSelected = index == 3
            monitorType = index
        }
    }
}
