package com.topdon.transfer
import com.topdon.tc001.R

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.topdon.lib.core.utils.ScreenUtil

class TransferDialog(context: Context) : Dialog(context, R.style.InfoDialog) {
    
    private val contentView: View = LayoutInflater.from(context).inflate(R.layout.dialog_transfer, null)
    private val seekBar: ProgressBar = contentView.findViewById(R.id.seek_bar)

    var max: Int
        get() = seekBar.max
        set(value) {
            seekBar.max = value
        }

    var progress: Int
        get() = seekBar.progress
        set(value) {
            seekBar.progress = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        seekBar.isEnabled = false
        setContentView(contentView)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.84f).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }
}