package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.topdon.libapp.R
import com.topdon.libapp.databinding.DialogConfirmSelectBinding
import com.topdon.lib.core.utils.ScreenUtil

/**
 * TS004 远端图库删除提示弹框.
 *
 * Created by LCG on 2024/2/29.
 */
class ConfirmSelectDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    var onConfirmClickListener: ((isSelect: Boolean) -> Unit)? = null

    private val binding = DialogConfirmSelectBinding.inflate(LayoutInflater.from(context))

    /**
     * 是否显示顶部信息图标，默认不显示.
     */
    fun setShowIcon(isShowIcon: Boolean) {
        binding.ivIcon.isVisible = isShowIcon
    }

    fun setTitleRes(@StringRes titleRes: Int) {
        binding.tvTitle.setText(titleRes)
    }

    fun setTitleStr(titleStr: String) {
        binding.tvTitle.text = titleStr
    }

    /**
     * 是否显示提示文字及选中效果，默认不显示.
     */
    fun setShowMessage(isShowMessage: Boolean) {
        binding.rlMessage.isVisible = isShowMessage
    }

    fun setMessageRes(@StringRes messageRes: Int) {
        binding.tvMessage.setText(messageRes)
    }

    /**
     * 是否显示取消按钮，默认显示且默认文字为“取消”.
     */
    fun setShowCancel(isShowCancel: Boolean) {
        binding.tvCancel.isVisible = isShowCancel
    }
    /**
     * 设置取消按钮文字，默认为“取消”.
     */
    fun setCancelText(@StringRes cancelRes: Int) {
        binding.tvCancel.setText(cancelRes)
    }

    /**
     * 设置确认按钮文字，默认为“删除"
     */
    fun setConfirmText(@StringRes confirmRes: Int) {
        binding.tvConfirm.setText(confirmRes)
    }


    private val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_select, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setContentView(binding.root)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.72).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }

        binding.rlMessage.setOnClickListener(this)
        binding.tvCancel.setOnClickListener(this)
        binding.tvConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.rlMessage -> {//选中状态
                binding.ivSelect.isSelected = !binding.ivSelect.isSelected
            }
            binding.tvCancel -> {//取消
                dismiss()
            }
            binding.tvConfirm -> {//确认
                dismiss()
                onConfirmClickListener?.invoke(binding.ivSelect.isSelected)
            }
        }
    }
}