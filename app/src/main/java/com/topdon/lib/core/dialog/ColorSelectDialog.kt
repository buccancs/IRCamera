package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.SizeUtils
import com.topdon.tc001.R
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.core.view.ColorSelectView
import android.widget.TextView

class ColorSelectDialog(context: Context, @ColorInt private var color: Int) : Dialog(context, R.style.InfoDialog) {

    var onPickListener: ((color: Int) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)


        val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_color_select, null)
        setContentView(rootView)
        
        val colorSelectView: ColorSelectView = rootView.findViewById(R.id.color_select_view)
        val tvSave: TextView = rootView.findViewById(R.id.tv_save)
        
        colorSelectView.selectColor(color)
        colorSelectView.onSelectListener = {
            color = it
        }
        tvSave.setOnClickListener {
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