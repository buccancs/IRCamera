package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.SizeUtils
import com.csl.irCamera.libapp.R
import com.csl.irCamera.libapp.databinding.DialogColorSelectBinding
import com.topdon.lib.core.utils.ScreenUtil

 * .
/**
 * @author LCG
 * @since Unknown
 */
class ColorSelectDialog(context: Context, @ColorInt private var color: Int) : Dialog(context, R.style.InfoDialog) {

     * .
    /** onPickListener property */
    var onPickListener: ((color: Int) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)


        val binding = DialogColorSelectBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        binding.colorSelectView.selectColor(color)
        binding.colorSelectView.onSelectListener = {
            color = it
        }
        binding.tvSave.setOnClickListener {
            dismiss()
            onPickListener?.invoke(color)
        }


        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = ScreenUtil.getScreenWidth(context) - SizeUtils.dp2px(36f)
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }
}