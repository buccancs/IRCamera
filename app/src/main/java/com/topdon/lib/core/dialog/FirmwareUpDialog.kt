package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.topdon.tc001.R
import com.topdon.lib.core.utils.ScreenUtil
import android.widget.TextView
import androidx.core.view.isVisible

class FirmwareUpDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    private val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_firmware_up, null)
    private val tvTitle: TextView by lazy { rootView.findViewById(R.id.tv_title) }
    private val tvSize: TextView by lazy { rootView.findViewById(R.id.tv_size) }
    private val tvContent: TextView by lazy { rootView.findViewById(R.id.tv_content) }
    private val tvRestartTips: TextView by lazy { rootView.findViewById(R.id.tv_restart_tips) }
    private val tvCancel: TextView by lazy { rootView.findViewById(R.id.tv_cancel) }
    private val tvConfirm: TextView by lazy { rootView.findViewById(R.id.tv_confirm) }

    var titleStr: CharSequence?
        get() = tvTitle.text
        set(value) {
            tvTitle.text = value
        }

    var sizeStr: CharSequence?
        get() = tvSize.text
        set(value) {
            tvSize.text = value
        }

    var contentStr: CharSequence?
        get() = tvContent.text
        set(value) {
            tvContent.text = value
        }

    var isShowRestartTips: Boolean
        get() = tvRestartTips.isVisible
        set(value) {
            tvRestartTips.isVisible = value
        }

    var isShowCancel: Boolean
        get() = tvCancel.isVisible
        set(value) {
            tvCancel.isVisible = value
        }


    var onCancelClickListener: (() -> Unit)? = null
    var onConfirmClickListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(rootView)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.72).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }

        tvCancel.setOnClickListener(this)
        tvConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            tvCancel -> {//取消
                dismiss()
                onCancelClickListener?.invoke()
            }
            tvConfirm -> {//确认
                dismiss()
                onConfirmClickListener?.invoke()
            }
        }
    }
}