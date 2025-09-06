package com.topdon.module.thermal.ir.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.thermal.R
import com.topdon.module.thermal.databinding.DialogIrConfigInputBinding
import java.lang.NumberFormatException

/**
 * 温度修正 环境温度、测温距离、发射率 修改值时输入弹框.
 *
 * Created by LCG on 2024/10/24.
 */
class IRConfigInputDialog(context: Context, val type: Type, val isTC007: Boolean) : Dialog(context, R.style.TextInputDialog) {

    private lateinit var binding: DialogIrConfigInputBinding
    private var value: Float? = null
    private var onConfirmListener: ((value: Float) -> Unit)? = null

    /**
     * 设置输入框默认值
     */
    fun setInput(value: Float?): IRConfigInputDialog {
        this.value = value
        return this
    }
    /**
     * 设置确认点击事件监听.
     */
    fun setConfirmListener(l: (value: Float) -> Unit): IRConfigInputDialog {
        this.onConfirmListener = l
        return this
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        binding = DialogIrConfigInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (type) {
            Type.TEMP -> {
                binding.tvTitle.text = "${context.getString(R.string.thermal_config_environment)} ${UnitTools.showConfigC(-10, if (isTC007) 50 else 55)}"
                binding.tvUnit.text = UnitTools.showUnit()
                binding.tvUnit.isVisible = true
            }
            Type.DIS -> {
                binding.tvTitle.text = "${context.getString(R.string.thermal_config_distance)} (0.2~${if (isTC007) 4 else 5}m)"
                binding.tvUnit.text = "m"
                binding.tvUnit.isVisible = true
            }
            Type.EM -> {
                binding.tvTitle.text = "${context.getString(R.string.thermal_config_radiation)} (${if (isTC007) "0.1" else "0.01"}~1.00)"
                binding.tvUnit.text = ""
                binding.tvUnit.isVisible = false
            }
        }
        binding.etInput.setText(if (value == null) "" else value.toString())
        binding.etInput.setSelection(0, binding.etInput.length())
        binding.etInput.requestFocus()

        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvConfirm.setOnClickListener {
            try {
                val input: Float = binding.etInput.text.toString().toFloat()
                val isRight = when (type) {
                    Type.TEMP -> input in UnitTools.showUnitValue(-10f) .. UnitTools.showUnitValue(if (isTC007) 50f else 55f)
                    Type.DIS -> input in 0.2f .. if (isTC007) 4f else 5f
                    Type.EM -> input in (if (isTC007) 0.1f else 0.01f) .. 1f
                }
                if (isRight) {
                    dismiss()
                    onConfirmListener?.invoke(input)
                } else {
                    TToast.shortToast(context, R.string.tip_input_format)
                }
            } catch (e: NumberFormatException) {
                TToast.shortToast(context, R.string.tip_input_format)
            }
        }

        window?.let {
            val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * if (isPortrait) 0.73f else 0.48f).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }

    enum class Type {
        /**
         * 环境温度
         */
        TEMP,

        /**
         * 测温距离
         */
        DIS,

        /**
         * 发射率
         */
        EM,
    }
}