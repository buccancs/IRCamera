package com.topdon.transfer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.transfer.databinding.DialogTransferBinding

 * .
/**
 * @author LCG
 * @since Unknown
 */
class TransferDialog(context: Context) : Dialog(context, R.style.InfoDialog) {

    private lateinit var binding: DialogTransferBinding

    /** max property */
    var max: Int
        get() = binding.seekBar.max
        set(value) {
            binding.seekBar.max = value
        }

    /** progress property */
    var progress: Int
        get() = binding.seekBar.progress
        set(value) {
            binding.seekBar.progress = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        binding = DialogTransferBinding.inflate(LayoutInflater.from(context))
        binding.seekBar.isEnabled = false
        setContentView(binding.root)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.84f).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }
}