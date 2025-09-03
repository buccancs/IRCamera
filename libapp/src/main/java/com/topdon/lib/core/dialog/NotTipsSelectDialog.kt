package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.topdon.lib.core.R
import com.topdon.lib.core.databinding.DialogNotTipsSelectBinding
import com.topdon.lib.core.utils.ScreenUtil

/**
 * 与 TipDialog 类似，不过多了个 “不再提示” 选中效果的提示弹窗.
 *
 * Created by LCG on 2024/10/26.
 */
class NotTipsSelectDialog(context: Context) : Dialog(context, R.style.InfoDialog) {

    @StringRes
    private var tipsResId: Int = 0
    private var onConfirmListener: ((isSelect: Boolean) -> Unit)? = null
    
    private val binding: DialogNotTipsSelectBinding = DialogNotTipsSelectBinding.inflate(layoutInflater)


    fun setTipsResId(@StringRes tipsResId: Int): NotTipsSelectDialog {
        this.tipsResId = tipsResId
        return this
    }

    /**
     * 点击 “我知道了” 事件监听.
     */
    fun setOnConfirmListener(l: ((isSelect: Boolean) -> Unit)?): NotTipsSelectDialog {
        onConfirmListener = l
        return this
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(binding.root)

        if (tipsResId != 0) {
            binding.tvMessage.setText(tipsResId)
        }
        binding.tvSelect.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        binding.tvIKnow.setOnClickListener {
            onConfirmListener?.invoke(binding.tvSelect.isSelected)
            dismiss()
        }

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.73f).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }
}