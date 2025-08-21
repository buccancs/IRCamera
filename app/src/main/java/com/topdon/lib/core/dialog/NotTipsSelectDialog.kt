package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.topdon.tc001.R
import com.topdon.lib.core.utils.ScreenUtil
import android.widget.TextView
import android.widget.CheckBox

class NotTipsSelectDialog(context: Context) : Dialog(context, R.style.InfoDialog) {

    @StringRes
    private var tipsResId: Int = 0
    private var onConfirmListener: ((isSelect: Boolean) -> Unit)? = null


    fun setTipsResId(@StringRes tipsResId: Int): NotTipsSelectDialog {
        this.tipsResId = tipsResId
        return this
    }

    fun setOnConfirmListener(l: ((isSelect: Boolean) -> Unit)?): NotTipsSelectDialog {
        onConfirmListener = l
        return this
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_not_tips_select)

        val tvMessage: TextView = findViewById(R.id.tv_message)
        val tvSelect: CheckBox = findViewById(R.id.tv_select)
        val tvIKnow: TextView = findViewById(R.id.tv_i_know)

        if (tipsResId != 0) {
            tvMessage.setText(tipsResId)
        }
        tvSelect.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        tvIKnow.setOnClickListener {
            onConfirmListener?.invoke(tvSelect.isSelected)
            dismiss()
        }

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.73f).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }
}