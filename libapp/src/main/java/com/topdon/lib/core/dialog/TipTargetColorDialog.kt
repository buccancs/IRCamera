package com.topdon.lib.core.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.R
import com.topdon.lib.core.adapter.TargetColorAdapter
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.core.databinding.DialogTipTargetColorBinding
import com.topdon.lib.core.utils.ScreenUtil

/**
 * 观测-标靶颜色
 */
class TipTargetColorDialog : Dialog {

    constructor(context: Context) : super(context)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    class Builder {
        var dialog: TipTargetColorDialog? = null
        private var context: Context? = null
        private var closeEvent: ((targetColor: Int) -> Unit)? = null
        private var canceled = false
        private var targetColor = ObserveBean.TYPE_TARGET_COLOR_GREEN

        private lateinit var binding: DialogTipTargetColorBinding

        constructor(context: Context) {
            this.context = context
        }

        fun setCancelListener(event: ((targetColor: Int) -> Unit)? = null): Builder {
            this.closeEvent = event
            return this
        }

        fun setTargetColor(color: Int): Builder {
            this.targetColor = color
            return this
        }

        fun setCanceled(canceled: Boolean): Builder {
            this.canceled = canceled
            return this
        }

        fun dismiss() {
            this.dialog!!.dismiss()
        }

        fun create(): TipTargetColorDialog {
            if (dialog == null) {
                dialog = TipTargetColorDialog(context!!, R.style.InfoDialog)
            }
            val inflater =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            binding = DialogTipTargetColorBinding.inflate(inflater)
            val view = binding.root

            binding.tvIKnow.setOnClickListener {
                dismiss()
                closeEvent?.invoke(targetColor)
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(context!!, RecyclerView.HORIZONTAL, false)
            val targetColorAdapter = TargetColorAdapter(context!!,targetColor)
            targetColorAdapter.listener = listener@{ _, item ->
                targetColor = item
                targetColorAdapter.selectedCode(item)
            }
            binding.recyclerView.adapter = targetColorAdapter
            dialog!!.addContentView(
                view,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            )
            val lp = dialog!!.window!!.attributes
            val wRatio =
                if (context!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //竖屏
                    0.90
                } else {
                    //横屏
                    0.35
                }
            lp.width = (ScreenUtil.getScreenWidth(context!!) * wRatio).toInt() //设置宽度
            dialog!!.window!!.attributes = lp

            dialog!!.setCanceledOnTouchOutside(canceled)
            binding.imgClose.setOnClickListener {
                dismiss()
//              closeEvent?.invoke(targetColor)
            }
            dialog!!.setContentView(view)
            return dialog as TipTargetColorDialog
        }
    }
}