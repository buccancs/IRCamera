package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.topdon.tc001.R
import android.widget.Button

class TipDialog : Dialog {

    constructor(context: Context) : super(context)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun onBackPressed() {

    }


    class Builder(private val context: Context) {
        var dialog: TipDialog? = null

        private var message: String? = null
        private var titleMessage: String? = null
        private var positiveStr: String? = null
        private var cancelStr: String? = null
        private var positiveEvent: (() -> Unit)? = null
        private var cancelEvent: (() -> Unit)? = null
        private var canceled = false
        private var isShowRestartTips = false

        fun setTitleMessage(message: String): Builder {
            this.titleMessage = message
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setMessage(@StringRes message: Int): Builder {
            this.message = context.getString(message)
            return this
        }

        fun setPositiveListener(@StringRes strRes: Int, event: (() -> Unit)? = null): Builder {
            return setPositiveListener(context.getString(strRes), event)
        }

        fun setPositiveListener(str: String, event: (() -> Unit)? = null): Builder {
            this.positiveStr = str
            this.positiveEvent = event
            return this
        }

        fun setCancelListener(@StringRes strRes: Int, event: (() -> Unit)? = null): Builder {
            return setCancelListener(context.getString(strRes), event)
        }

        fun setCancelListener(str: String, event: (() -> Unit)? = null): Builder {
            this.cancelStr = str
            this.cancelEvent = event
            return this
        }

        fun setCanceled(canceled: Boolean): Builder {
            this.canceled = canceled
            return this
        }

        fun setShowRestartTops(isShowRestartTips: Boolean): Builder {
            this.isShowRestartTips = isShowRestartTips
            return this
        }

        fun dismiss() {
            this.dialog!!.dismiss()
        }


        fun create(): TipDialog {
            if (dialog == null) {
                dialog = TipDialog(context, R.style.InfoDialog)
            }

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_tip, null)
            dialog!!.addContentView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val widthPixels = context.resources.displayMetrics.widthPixels
            val lp = dialog!!.window!!.attributes
            lp.width = (widthPixels * if (isPortrait) 0.85 else 0.35).toInt() //设置宽度
            dialog!!.window!!.attributes = lp

            dialog!!.setCanceledOnTouchOutside(canceled)
            
            val dialogTipSuccessBtn: Button = view.findViewById(R.id.dialog_tip_success_btn)
            val dialogTipCancelBtn: Button = view.findViewById(R.id.dialog_tip_cancel_btn)
            val spaceMargin: View = view.findViewById(R.id.space_margin)
            
            val dialogTipMsgText: TextView = view.findViewById(R.id.dialog_tip_msg_text)
            val dialogTipTitleMsgText: TextView = view.findViewById(R.id.dialog_tip_title_msg_text)
            val tvRestartTips: TextView = view.findViewById(R.id.tv_restart_tips)
            
            dialogTipSuccessBtn.setOnClickListener {
                dismiss()
                positiveEvent?.invoke()
            }
            dialogTipCancelBtn.setOnClickListener {
                dismiss()
                cancelEvent?.invoke()
            }

            if (positiveStr != null) {
                dialogTipSuccessBtn.text = positiveStr
            }
            if (!TextUtils.isEmpty(cancelStr)) {
                spaceMargin.visibility = View.VISIBLE
                dialogTipCancelBtn.visibility = View.VISIBLE
                dialogTipCancelBtn.text = cancelStr
            } else {
                spaceMargin.visibility = View.GONE
                dialogTipCancelBtn.visibility = View.GONE
                dialogTipCancelBtn.text = ""
            }
            if (message != null) {
                dialogTipMsgText.visibility = View.VISIBLE
                dialogTipMsgText.setText(message, TextView.BufferType.NORMAL)
            } else {
                dialogTipMsgText.visibility = View.GONE
            }

            if (titleMessage != null) {
                dialogTipTitleMsgText.visibility = View.VISIBLE
                dialogTipTitleMsgText.setText(titleMessage, TextView.BufferType.NORMAL)
            } else {
                dialogTipTitleMsgText.visibility = View.GONE
            }

            tvRestartTips.isVisible = isShowRestartTips

            dialog!!.setContentView(view)
            return dialog as TipDialog
        }
    }

}