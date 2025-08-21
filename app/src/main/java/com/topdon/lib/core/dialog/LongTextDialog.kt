package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topdon.tc001.R
import com.topdon.lib.core.utils.ScreenUtil
import android.widget.TextView

class LongTextDialog(context: Context, val title: String?, val content: String?) : Dialog(context, R.style.InfoDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_long_text, null)
        val tvTitle: TextView = rootView.findViewById(R.id.tv_title)
        val tvText: TextView = rootView.findViewById(R.id.tv_text)
        val tvIKnow: TextView = rootView.findViewById(R.id.tv_i_know)
        
        tvTitle.text = title
        tvText.text = content
        setContentView(rootView)
        tvIKnow.setOnClickListener {
            dismiss()
        }


        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.74f).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }
}