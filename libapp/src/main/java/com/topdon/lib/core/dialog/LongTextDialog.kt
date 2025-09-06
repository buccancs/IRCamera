package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.topdon.lib.core.R
import com.topdon.lib.core.databinding.DialogLongTextBinding
import com.topdon.lib.core.utils.ScreenUtil

/**
 * 展示很长文字的弹框.
 *
 * Created by LCG on 2024/2/2.
 */
class LongTextDialog(context: Context, val title: String?, val content: String?) : Dialog(context, R.style.InfoDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        val binding = DialogLongTextBinding.inflate(LayoutInflater.from(context))
        binding.tvTitle.text = title
        binding.tvText.text = content
        setContentView(binding.root)
        binding.tvIKnow.setOnClickListener {
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