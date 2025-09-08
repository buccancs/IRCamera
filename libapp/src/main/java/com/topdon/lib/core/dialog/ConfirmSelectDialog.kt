package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.csl.irCamera.libapp.R
import com.csl.irCamera.libapp.databinding.DialogConfirmSelectBinding
import com.topdon.lib.core.utils.ScreenUtil

 * TS004 .
/**
 * @author LCG
 * @since Unknown
 */
class ConfirmSelectDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    private lateinit var binding: DialogConfirmSelectBinding
    /** onConfirmClickListener property */
    var onConfirmClickListener: ((isSelect: Boolean) -> Unit)? = null

     * .
    /**
     * Function description.
     */
    fun setShowIcon(isShowIcon: Boolean) {
        binding.ivIcon.isVisible = isShowIcon
    }

    /**
     * Function description.
     */
    fun setTitleRes(@StringRes titleRes: Int) {
        binding.tvTitle.setText(titleRes)
    }

    /**
     * Function description.
     */
    fun setTitleStr(titleStr: String) {
        binding.tvTitle.text = titleStr
    }

     * .
    /**
     * Function description.
     */
    fun setShowMessage(isShowMessage: Boolean) {
        binding.rlMessage.isVisible = isShowMessage
    }

    /**
     * Function description.
     */
    fun setMessageRes(@StringRes messageRes: Int) {
        binding.tvMessage.setText(messageRes)
    }

     * “”.
    /**
     * Function description.
     */
    fun setShowCancel(isShowCancel: Boolean) {
        binding.tvCancel.isVisible = isShowCancel
    }
     * “”.
    /**
     * Function description.
     */
    fun setCancelText(@StringRes cancelRes: Int) {
        binding.tvCancel.setText(cancelRes)
    }

     * “
    /**
     * Function description.
     */
    fun setConfirmText(@StringRes confirmRes: Int) {
        binding.tvConfirm.setText(confirmRes)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        
        binding = DialogConfirmSelectBinding.inflate(LayoutInflater.from(context))
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
            binding.rlMessage -> {//
                binding.ivSelect.isSelected = !binding.ivSelect.isSelected
            }
            binding.tvCancel -> {//
                dismiss()
            }
            binding.tvConfirm -> {//
                dismiss()
                onConfirmClickListener?.invoke(binding.ivSelect.isSelected)
            }
        }
    }
}