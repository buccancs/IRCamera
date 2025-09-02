package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.databinding.DialogColorSelectBinding
import com.topdon.lib.core.R
import com.topdon.lib.core.utils.ScreenUtil
// import kotlinx.android.synthetic.  // TODO: Replace with ViewBindingmain.dialog_color_select.view.*

/**
 * 仅拾取颜色的弹框.
 *
 * Created by LCG on 2024/2/2.
 */
class ColorSelectDialog(context: Context, @ColorInt private var color: Int) : Dialog(context, R.style.InfoDialog) {

    /**
     * 颜色值拾取事件监听.
     */
    var onPickListener: ((color: Int) -> Unit)? = null
    
    private lateinit var binding: DialogColorSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        binding = DialogColorSelectBinding.inflate(LayoutInflater.from(context))
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