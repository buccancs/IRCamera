package com.topdon.lib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import androidx.databinding.DataBindingUtil
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.R
import com.topdon.lib.ui.databinding.DialogProgressBinding

/**
 * 带进度条的提示弹框.
 */
class ProgressDialog(context: Context) : Dialog(context, R.style.InfoDialog) {
    var max: Int = 100
        set(value) {
            binding.progressBar.max = value
            field = value
        }

    var progress: Int = 0
        set(value) {
            binding.progressBar.progress = value
            field = value
        }



    private val binding: DialogProgressBinding
    init {
        binding = DataBindingUtil.inflate(
            android.view.LayoutInflater.from(context), 
            R.layout.dialog_progress, 
            null, 
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(binding.root)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * if (ScreenUtil.isPortrait(context)) 0.8 else 0.45).toInt()
            layoutParams.height = LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }

    override fun show() {
        super.show()
        binding.progressBar.max = max
        binding.progressBar.progress = progress
    }
}