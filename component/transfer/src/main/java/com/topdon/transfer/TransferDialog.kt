package com.topdon.transfer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.topdon.component.transfer.R as TransferR  // Module-specific resources
import com.topdon.lib.core.R  // Shared resources from libapp
import com.topdon.lib.core.utils.ScreenUtil

/**
 * 相册迁移进度弹框.
 *
 * Created by LCG on 2024/3/26.
 */
class TransferDialog(context: Context) : Dialog(context, R.style.InfoDialog) {

    private lateinit var seekBar: SeekBar

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


    private val contentView: View = LayoutInflater.from(context).inflate(TransferR.layout.dialog_transfer, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        seekBar = contentView.findViewById(TransferR.id.seek_bar)
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