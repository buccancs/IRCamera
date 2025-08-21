package com.topdon.libcom.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.jaygoo.widget.DefRangeSeekBar
import com.jaygoo.widget.OnRangeChangedListener
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.tc001.R
import com.topdon.libcom.util.ColorUtils

class ColorPickDialog(context: Context, @ColorInt private var color: Int,var textSize: Int,var textSizeIsDP : Boolean = false) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    var onPickListener: ((color: Int,textSize : Int) -> Unit)? = null

    private val rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_color_pick, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setContentView(rootView)

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
            0xff0000ff.toInt() -> rootView.view_color1.isSelected = true
            0xffff0000.toInt() -> rootView.view_color2.isSelected = true
            0xff00ff00.toInt() -> rootView.view_color3.isSelected = true
            0xffffff00.toInt() -> rootView.view_color4.isSelected = true
            0xff000000.toInt() -> rootView.view_color5.isSelected = true
            0xffffffff.toInt() -> rootView.view_color6.isSelected = true
            else -> rootView.color_select_view.selectColor(color)
        }

        rootView.color_select_view.onSelectListener = {
            unSelect6Color()
            color = it
        }
        if (textSize != -1){
            val tvSizeTitle = findViewById<TextView>(R.id.tv_size_title)
            val tvSizeValue = findViewById<TextView>(R.id.tv_size_value)
            val tvNiftyLeft = findViewById<TextView>(R.id.tv_nifty_left)
            val tvNiftyRight = findViewById<TextView>(R.id.tv_nifty_right)
            val niftySliderView = findViewById<DefRangeSeekBar>(R.id.nifty_slider_view)
            
            tvSizeTitle.visibility = View.VISIBLE
            tvSizeValue.visibility = View.VISIBLE
            tvNiftyLeft.visibility = View.VISIBLE
            tvNiftyRight.visibility = View.VISIBLE
            niftySliderView.visibility = View.VISIBLE
            niftySliderView.setOnRangeChangedListener(object : OnRangeChangedListener{
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
                    tvSizeValue?.text = text
                }

                override fun onStartTrackingTouch(view: DefRangeSeekBar?, isLeft: Boolean) {
                }

                override fun onStopTrackingTouch(view: DefRangeSeekBar?, isLeft: Boolean) {
                }

            })
            niftySliderView.setProgress(textSizeToNifyValue(textSize,textSizeIsDP))
        }else{
            val niftySliderView = findViewById<DefRangeSeekBar>(R.id.nifty_slider_view)
            niftySliderView.visibility = View.GONE
        }
        rootView.view_color1.setOnClickListener(this)
        rootView.view_color2.setOnClickListener(this)
        rootView.view_color3.setOnClickListener(this)
        rootView.view_color4.setOnClickListener(this)
        rootView.view_color5.setOnClickListener(this)
        rootView.view_color6.setOnClickListener(this)
        rootView.rl_close.setOnClickListener(this)
        rootView.tv_save.setOnClickListener(this)
    }

    private fun textSizeToNifyValue(size: Int, isTC007: Boolean): Float {
        // TC007 support removed - always use standard size conversion
        return when (size) {
            SizeUtils.sp2px(14f) -> 0f
            SizeUtils.sp2px(16f) -> 50f
            else -> 100f
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            rootView.rl_close -> dismiss()
            rootView.tv_save -> {
                dismiss()
                onPickListener?.invoke(color,textSize)
            }

            rootView.view_color1 -> {
                unSelect6Color()
                rootView.color_select_view.reset()
                rootView.view_color1.isSelected = true
                color = 0xff0000ff.toInt()
            }
            rootView.view_color2 -> {
                unSelect6Color()
                rootView.color_select_view.reset()
                rootView.view_color2.isSelected = true
                color = 0xffff0000.toInt()
            }
            rootView.view_color3 -> {
                unSelect6Color()
                rootView.color_select_view.reset()
                rootView.view_color3.isSelected = true
                color = 0xff00ff00.toInt()
            }
            rootView.view_color4 -> {
                unSelect6Color()
                rootView.color_select_view.reset()
                rootView.view_color4.isSelected = true
                color = 0xffffff00.toInt()
            }
            rootView.view_color5 -> {
                unSelect6Color()
                rootView.color_select_view.reset()
                rootView.view_color5.isSelected = true
                color = 0xff000000.toInt()
            }
            rootView.view_color6 -> {
                unSelect6Color()
                rootView.color_select_view.reset()
                rootView.view_color6.isSelected = true
                color = 0xffffffff.toInt()
            }
        }
    }

    private fun unSelect6Color() {
        rootView.view_color1.isSelected = false
        rootView.view_color2.isSelected = false
        rootView.view_color3.isSelected = false
        rootView.view_color4.isSelected = false
        rootView.view_color5.isSelected = false
        rootView.view_color6.isSelected = false
    }
}