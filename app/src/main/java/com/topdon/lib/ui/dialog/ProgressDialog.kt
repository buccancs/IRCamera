package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ProgressBar
import android.widget.TextView
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.R


class ProgressDialog(context: Context) : Dialog(context, R.style.InfoDialog) {
    var max: Int = 100
        set(value) {
            rootView.progress_bar.max = value
            field = value
        }

    var progress: Int = 0
        set(value) {
            rootView.progress_bar.progress = value
            field = value
        }



    private val rootView: View
    init {
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(rootView)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * if (ScreenUtil.isPortrait(context)) 0.8 else 0.45).toInt()
            layoutParams.height = LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }

    override fun show() {
        super.show()
        rootView.findViewById<ProgressBar>(R.id.progress_bar).max = max
        rootView.findViewById<ProgressBar>(R.id.progress_bar).progress = progress
    }
}