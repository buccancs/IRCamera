package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.topdon.tc001.R
import com.topdon.lib.core.utils.ScreenUtil
import android.widget.TextView
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class ConfirmSelectDialog(context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    var onConfirmClickListener: ((isSelect: Boolean) -> Unit)? = null

    private val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_select, null)
    private val ivIcon: ImageView by lazy { rootView.findViewById(R.id.iv_icon) }
    private val tvTitle: TextView by lazy { rootView.findViewById(R.id.tv_title) }
    private val rlMessage: ConstraintLayout by lazy { rootView.findViewById(R.id.rl_message) }
    private val tvMessage: TextView by lazy { rootView.findViewById(R.id.tv_message) }
    private val tvCancel: TextView by lazy { rootView.findViewById(R.id.tv_cancel) }
    private val tvConfirm: TextView by lazy { rootView.findViewById(R.id.tv_confirm) }
    private val ivSelect: ImageView by lazy { rootView.findViewById(R.id.iv_select) }

    fun setShowIcon(isShowIcon: Boolean) {
        ivIcon.isVisible = isShowIcon
    }

    fun setTitleRes(@StringRes titleRes: Int) {
        tvTitle.setText(titleRes)
    }

    fun setTitleStr(titleStr: String) {
        tvTitle.text = titleStr
    }

    fun setShowMessage(isShowMessage: Boolean) {
        rlMessage.isVisible = isShowMessage
    }

    fun setMessageRes(@StringRes messageRes: Int) {
        tvMessage.setText(messageRes)
    }

    fun setShowCancel(isShowCancel: Boolean) {
        tvCancel.isVisible = isShowCancel
    }
    fun setCancelText(@StringRes cancelRes: Int) {
        tvCancel.setText(cancelRes)
    }

    fun setConfirmText(@StringRes confirmRes: Int) {
        tvConfirm.setText(confirmRes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setContentView(rootView)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.72).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }

        rlMessage.setOnClickListener(this)
        tvCancel.setOnClickListener(this)
        tvConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            rlMessage -> {//选中状态
                ivSelect.isSelected = !ivSelect.isSelected
            }
            tvCancel -> {//取消
                dismiss()
            }
            tvConfirm -> {//确认
                dismiss()
                onConfirmClickListener?.invoke(ivSelect.isSelected)
            }
        }
    }
}