package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ProgressBar
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR

/**
 * 带进度条的提示弹框.
 */
class ProgressDialog(context: Context) : Dialog(context, R.style.InfoDialog) {
    var max: Int = 100
        set(value) {
            rootView.findViewById<ProgressBar>(UiR.id.progress_bar).max = value
            field = value
        }

    var progress: Int = 0
        set(value) {
            rootView.findViewById<ProgressBar>(UiR.id.progress_bar).progress = value
            field = value
        }



    private val rootView: View
    init {
        rootView = LayoutInflater.from(context).inflate(UiR.layout.dialog_progress, null)
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
        val progressBar = rootView.findViewById<ProgressBar>(UiR.id.progress_bar)
        progressBar.max = max
        progressBar.progress = progress
    }
}