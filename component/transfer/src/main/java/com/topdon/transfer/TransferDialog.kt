package com.topdon.transfer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.topdon.lib.core.utils.ScreenUtil

/**
 * 相册迁移进度弹框.
 *
 * Created by LCG on 2024/3/26.
 */
class TransferDialog(context: Context) : Dialog(context, R.style.InfoDialog) {

    var max: Int
        get() = contentView.findViewById<SeekBar>(R.id.seek_bar).max
        set(value) {
            contentView.findViewById<SeekBar>(R.id.seek_bar).max = value
        }

    var progress: Int
        get() = contentView.findViewById<SeekBar>(R.id.seek_bar).progress
        set(value) {
            contentView.findViewById<SeekBar>(R.id.seek_bar).progress = value
        }


    private val contentView: View = LayoutInflater.from(context).inflate(R.layout.dialog_transfer, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        val seekBar = contentView.findViewById<SeekBar>(R.id.seek_bar)
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