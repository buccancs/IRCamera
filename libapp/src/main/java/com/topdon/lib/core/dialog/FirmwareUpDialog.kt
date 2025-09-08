package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.csl.irCamera.libapp.R
import com.csl.irCamera.libapp.databinding.DialogFirmwareUpBinding
import com.topdon.lib.core.utils.ScreenUtil

 * .
 * Created by LCG on 2024/3/4.
class FirmwareUpDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    private lateinit var binding: DialogFirmwareUpBinding

     * V3.50
    var titleStr: CharSequence?
        get() = binding.tvTitle.text
        set(value) {
            binding.tvTitle.text = value
        }

     * : 239.6MB
    var sizeStr: CharSequence?
        get() = binding.tvSize.text
        set(value) {
            binding.tvSize.text = value
        }

    var contentStr: CharSequence?
        get() = binding.tvContent.text
        set(value) {
            binding.tvContent.text = value
        }

     * (Gone).
    var isShowRestartTips: Boolean
        get() = binding.tvRestartTips.isVisible
        set(value) {
            binding.tvRestartTips.isVisible = value
        }

     * .
    var isShowCancel: Boolean
        get() = binding.tvCancel.isVisible
        set(value) {
            binding.tvCancel.isVisible = value
        }

     * .
    var onCancelClickListener: (() -> Unit)? = null
     * .
    var onConfirmClickListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        
        binding = DialogFirmwareUpBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.72).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }

        binding.tvCancel.setOnClickListener(this)
        binding.tvConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvCancel -> {//
                dismiss()
                onCancelClickListener?.invoke()
            }
            binding.tvConfirm -> {//
                dismiss()
                onConfirmClickListener?.invoke()
            }
        }
    }
}