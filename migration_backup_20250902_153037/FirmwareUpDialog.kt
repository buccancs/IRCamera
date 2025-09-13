package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.topdon.lib.core.R
import com.topdon.lib.core.utils.ScreenUtil
import kotlinx.android.synthetic.main.dialog_firmware_up.view.*

/**
 * firmwareUpgrade有新versiontip弹框.
 * Created by LCG on 2024/3/4.
 */
class FirmwareUpDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {
    /**
     * titletext，如 “发现新version V3.50”
     */
    var titleStr: CharSequence?
        get() = rootView.tv_title.text
        set(value) {
            rootView.tv_title.text = value
        }

    /**
     * file大小text，如 “大小: 239.6MB”
     */
    var sizeStr: CharSequence?
        get() = rootView.tv_size.text
        set(value) {
            rootView.tv_size.text = value
        }

    /**
     * Upgrade内容，一般直接扔从interface拿到的东西
     */
    var contentStr: CharSequence?
        get() = rootView.tv_content.text
        set(value) {
            rootView.tv_content.text = value
        }

    /**
     * 是否Show/Display底部device重启tip，目前仅firmwareUpgrade需要Show/Display，默认Hide(Gone).
     */
    var isShowRestartTips: Boolean
        get() = rootView.tv_restart_tips.isVisible
        set(value) {
            rootView.tv_restart_tips.isVisible = value
        }

    /**
     * 是否Show/DisplayCancelbutton，默认Show/Display.
     */
    var isShowCancel: Boolean
        get() = rootView.tv_cancel.isVisible
        set(value) {
            rootView.tv_cancel.isVisible = value
        }

    /**
     * CancelclickEventListener.
     */
    var onCancelClickListener: (() -> Unit)? = null

    /**
     * updateclickEventListener.
     */
    var onConfirmClickListener: (() -> Unit)? = null

    private val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_firmware_up, null)

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

        rootView.tv_cancel.setOnClickListener(this)
        rootView.tv_confirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            rootView.tv_cancel -> { // Cancel
                dismiss()
                onCancelClickListener?.invoke()
            }
            rootView.tv_confirm -> { // Confirm
                dismiss()
                onConfirmClickListener?.invoke()
            }
        }
    }
}
