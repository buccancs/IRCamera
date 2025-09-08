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
 * [Chinese text].
 * Created by LCG on 2024/3/4.
 */
class FirmwareUpDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    /**
     * [Chinese text]text, [Chinese text] "[Chinese text] V3.50"
     */
    var titleStr: CharSequence?
        get() = rootView.tv_title.text
        set(value) {
            rootView.tv_title.text = value
        }

    /**
     * [Chinese text]text, [Chinese text] "[Chinese text]: 239.6MB"
     */
    var sizeStr: CharSequence?
        get() = rootView.tv_size.text
        set(value) {
            rootView.tv_size.text = value
        }

    /**
     * [Chinese text], [Chinese text]
     */
    var contentStr: CharSequence?
        get() = rootView.tv_content.text
        set(value) {
            rootView.tv_content.text = value
        }

    /**
     * [Chinese text], [Chinese text]only[Chinese text], [Chinese text](Gone).
     */
    var isShowRestartTips: Boolean
        get() = rootView.tv_restart_tips.isVisible
        set(value) {
            rootView.tv_restart_tips.isVisible = value
        }

    /**
     * [Chinese text]button, [Chinese text].
     */
    var isShowCancel: Boolean
        get() = rootView.tv_cancel.isVisible
        set(value) {
            rootView.tv_cancel.isVisible = value
        }


    /**
     * [Chinese text]point[Chinese text]eventlistener.
     */
    var onCancelClickListener: (() -> Unit)? = null
    /**
     * [Chinese text]point[Chinese text]eventlistener.
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
            rootView.tv_cancel -> {//[Chinese text]
                dismiss()
                onCancelClickListener?.invoke()
            }
            rootView.tv_confirm -> {//[Chinese text]
                dismiss()
                onConfirmClickListener?.invoke()
            }
        }
    }
}