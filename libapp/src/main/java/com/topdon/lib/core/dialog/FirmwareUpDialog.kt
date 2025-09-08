package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.topdon.lib.core.R
import com.topdon.lib.core.databinding.DialogFirmwareUpBinding
import com.topdon.lib.core.utils.ScreenUtil

/**
 * [Chinese text].
 * Created by LCG on 2024/3/4.
 */
class FirmwareUpDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {
    private var _binding: DialogFirmwareUpBinding? = null
    private val binding get() = _binding!!

    /**
     * [Chinese text]text, [Chinese text] "[Chinese text] V3.50"
     */
    var titleStr: CharSequence?
        get() = binding.tvTitle.text
        set(value) {
            binding.tvTitle.text = value
        }

    /**
     * [Chinese text]text, [Chinese text] "[Chinese text]: 239.6MB"
     */
    var sizeStr: CharSequence?
        get() = binding.tvSize.text
        set(value) {
            binding.tvSize.text = value
        }

    /**
     * [Chinese text], [Chinese text]
     */
    var contentStr: CharSequence?
        get() = binding.tvContent.text
        set(value) {
            binding.tvContent.text = value
        }

    /**
     * [Chinese text], [Chinese text]only[Chinese text], [Chinese text](Gone).
     */
    var isShowRestartTips: Boolean
        get() = binding.tvRestartTips.isVisible
        set(value) {
            binding.tvRestartTips.isVisible = value
        }

    /**
     * [Chinese text]button, [Chinese text].
     */
    var isShowCancel: Boolean
        get() = binding.tvCancel.isVisible
        set(value) {
            binding.tvCancel.isVisible = value
        }

    /**
     * [Chinese text]point[Chinese text]eventlistener.
     */
    var onCancelClickListener: (() -> Unit)? = null

    /**
     * [Chinese text]point[Chinese text]eventlistener.
     */
    var onConfirmClickListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogFirmwareUpBinding.inflate(LayoutInflater.from(context))
        setCancelable(false)
        setCanceledOnTouchOutside(false)
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
            binding.tvCancel -> { // [Chinese text]
                dismiss()
                onCancelClickListener?.invoke()
            }
            binding.tvConfirm -> { // [Chinese text]
                dismiss()
                onConfirmClickListener?.invoke()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }
}
