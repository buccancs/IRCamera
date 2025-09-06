package com.topdon.libcom.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.jaygoo.widget.DefRangeSeekBar
import com.jaygoo.widget.OnRangeChangedListener
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.libcom.R
import com.topdon.libcom.util.ColorUtils
import com.topdon.libcom.databinding.DialogColorPickBinding

/**
 * 颜色拾取弹框.
 *
 * Created by chenggeng.lin on 2023/12/18.
 */
class ColorPickDialog(context: Context, @ColorInt private var color: Int,var textSize: Int,var textSizeIsDP : Boolean = false) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    /**
     * 颜色值拾取事件监听.
     */
    var onPickListener: ((color: Int,textSize : Int) -> Unit)? = null

    private val binding: DialogColorPickBinding = DialogColorPickBinding.inflate(LayoutInflater.from(context))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setContentView(binding.root)

        window?.let {
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * 0.9).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }

        val activeTrackColor =
            ColorUtils.setColorAlpha(ContextCompat.getColor(context, R.color.we_read_theme_color), 0.1f)
        val iconTintColor =
            ColorUtils.setColorAlpha(ContextCompat.getColor(context, R.color.we_read_theme_color), 0.7f)

        when (color) {
            0xff0000ff.toInt() -> binding.viewColor1.isSelected = true
            0xffff0000.toInt() -> binding.viewColor2.isSelected = true
            0xff00ff00.toInt() -> binding.viewColor3.isSelected = true
            0xffffff00.toInt() -> binding.viewColor4.isSelected = true
            0xff000000.toInt() -> binding.viewColor5.isSelected = true
            0xffffffff.toInt() -> binding.viewColor6.isSelected = true
            else -> binding.colorSelectView.selectColor(color)
        }

        binding.colorSelectView.onSelectListener = {
            unSelect6Color()
            color = it
        }
        if (textSize != -1){
            binding.tvSizeTitle.visibility = View.VISIBLE
            binding.tvSizeValue.visibility = View.VISIBLE
            binding.tvNiftyLeft.visibility = View.VISIBLE
            binding.tvNiftyRight.visibility = View.VISIBLE
            binding.niftySliderView.visibility = View.VISIBLE
            binding.niftySliderView.setOnRangeChangedListener(object : OnRangeChangedListener{
                override fun onRangeChanged(
                    view: DefRangeSeekBar?,
                    leftValue: Float,
                    rightValue: Float,
                    isFromUser: Boolean
                ) {
                    var text = "标准"
                    text = if (leftValue <= 0){
                        textSize = 14
                        context.getString(R.string.temp_text_standard)
                    }else if(leftValue <= 50){
                        textSize = 16
                        context.getString(R.string.temp_text_big)
                    }else{
                        textSize = 18
                        context.getString(R.string.temp_text_sup_big)
                    }
                    binding.tvSizeValue?.text = text
                }

                override fun onStartTrackingTouch(view: DefRangeSeekBar?, isLeft: Boolean) {
                }

                override fun onStopTrackingTouch(view: DefRangeSeekBar?, isLeft: Boolean) {
                }

            })
            binding.niftySliderView.setProgress(textSizeToNifyValue(textSize,textSizeIsDP))
        }else{
            binding.niftySliderView.visibility = View.GONE
        }
        binding.viewColor1.setOnClickListener(this)
        binding.viewColor2.setOnClickListener(this)
        binding.viewColor3.setOnClickListener(this)
        binding.viewColor4.setOnClickListener(this)
        binding.viewColor5.setOnClickListener(this)
        binding.viewColor6.setOnClickListener(this)
        binding.rlClose.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)
    }

    private fun textSizeToNifyValue(size: Int, isTC007: Boolean) : Float{
        if (isTC007){
            return when(size){
                14 -> 0f
                16 -> 50f
                else -> 100f
            }
        }
        return when(size){
            SizeUtils.sp2px(14f) -> 0f
            SizeUtils.sp2px(16f) -> 50f
            else -> 100f
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.rlClose -> dismiss()

            binding.tvSave -> {//保存
                dismiss()
                onPickListener?.invoke(color,textSize)
            }

            binding.viewColor1 -> {
                unSelect6Color()
                binding.colorSelectView.reset()
                binding.viewColor1.isSelected = true
                color = 0xff0000ff.toInt()
            }
            binding.viewColor2 -> {
                unSelect6Color()
                binding.colorSelectView.reset()
                binding.viewColor2.isSelected = true
                color = 0xffff0000.toInt()
            }
            binding.viewColor3 -> {
                unSelect6Color()
                binding.colorSelectView.reset()
                binding.viewColor3.isSelected = true
                color = 0xff00ff00.toInt()
            }
            binding.viewColor4 -> {
                unSelect6Color()
                binding.colorSelectView.reset()
                binding.viewColor4.isSelected = true
                color = 0xffffff00.toInt()
            }
            binding.viewColor5 -> {
                unSelect6Color()
                binding.colorSelectView.reset()
                binding.viewColor5.isSelected = true
                color = 0xff000000.toInt()
            }
            binding.viewColor6 -> {
                unSelect6Color()
                binding.colorSelectView.reset()
                binding.viewColor6.isSelected = true
                color = 0xffffffff.toInt()
            }
        }
    }

    /**
     * 将 6 个固定的颜色按钮重置为未选中状态.
     */
    private fun unSelect6Color() {
        binding.viewColor1.isSelected = false
        binding.viewColor2.isSelected = false
        binding.viewColor3.isSelected = false
        binding.viewColor4.isSelected = false
        binding.viewColor5.isSelected = false
        binding.viewColor6.isSelected = false
    }
}