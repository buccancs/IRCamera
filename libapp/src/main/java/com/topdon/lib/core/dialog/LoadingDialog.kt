package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.csl.irCamera.libapp.R
import com.csl.irCamera.libapp.databinding.DialogLoadingBinding
import com.topdon.lib.core.utils.ScreenUtil

 *  UI  LMS  LMS
/**
 * @author LCG
 * @since Unknown
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.TransparentDialog) {

    private lateinit var binding: DialogLoadingBinding

    /**
     * Function description.
     */
    fun setTips(@StringRes resId: Int) {
        binding.tvTips.setText(resId)
        binding.tvTips.isVisible = true
    }

    /**
     * Function description.
     */
    fun setTips(text: CharSequence?) {
        binding.tvTips.text = text
        binding.tvTips.isVisible = text?.isNotEmpty() == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        
        binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * if (ScreenUtil.isPortrait(context)) 0.3 else 0.15).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }

}