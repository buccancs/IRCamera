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

class LoadingDialog(context: Context) : Dialog(context, R.style.TransparentDialog) {

    private val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
    private val tvTips: TextView by lazy { rootView.findViewById(R.id.tv_tips) }

    fun setTips(@StringRes resId: Int) {
        tvTips.setText(resId)
        tvTips.isVisible = true
    }

    fun setTips(text: CharSequence?) {
        tvTips.text = text
        tvTips.isVisible = text?.isNotEmpty() == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(rootView)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * if (ScreenUtil.isPortrait(context)) 0.3 else 0.15).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }

}