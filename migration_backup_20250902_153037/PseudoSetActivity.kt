package com.topdon.pseudo.activity

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.UnitTools
import com.topdon.pseudo.R
import com.topdon.pseudo.bean.CustomPseudoBean
import com.topdon.pseudo.constant.*
import kotlinx.android.synthetic.main.activity_pseudo_set.*
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode

    private fun switchDynamicCustom(isToCustom: Boolean) {
        customPseudoBean.isUseCustomPseudo = isToCustom
        cl_custom_content.isVisible = isToCustom
        cl_dynamic.isSelected = !isToCustom
        cl_custom.isSelected = isToCustom
        iv_dynamic.setImageResource(
            if (isToCustom) R.drawable.svg_pseudo_set_dynamic_not_select else R.drawable.svg_pseudo_set_dynamic_select,
        )
        iv_custom.setImageResource(
            if (isToCustom) R.drawable.svg_pseudo_set_custom_select else R.drawable.svg_pseudo_set_custom_not_select,
        )
        tv_dynamic_title.setTextColor(if (isToCustom) 0xffffffff.toInt() else 0xffffba42.toInt())
        tv_custom_title.setTextColor(if (isToCustom) 0xffffba42.toInt() else 0xffffffff.toInt())
    }

    private fun switchColorType(isToCustom: Boolean) {
        customPseudoBean.isColorCustom = isToCustom
        cl_color_custom.isVisible = isToCustom
        cl_color_recommend.isVisible = !isToCustom
        tv_color_custom.setTextColor(if (isToCustom) 0xffffba42.toInt() else 0xffffffff.toInt())
        tv_color_recommend.setTextColor(if (isToCustom) 0xffffffff.toInt() else 0xffffba42.toInt())
        tv_color_custom.setBackgroundResource(if (isToCustom) R.drawable.bg_corners50_solid_2a183e_stroke_theme else 0)
        tv_color_recommend.setBackgroundResource(
            if (isToCustom) 0 else R.drawable.bg_corners50_solid_2a183e_stroke_theme,
        )
    }

    private fun switchRecommendColorIndex(index: Int) {
        when (customPseudoBean.customRecommendIndex) {
            0 -> {
                tv_recommend_color1.setTextColor(0x80ffffff.toInt())
                view_recommend_bg_color1.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            1 -> {
                tv_recommend_color2.setTextColor(0x80ffffff.toInt())
                view_recommend_bg_color2.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            2 -> {
                tv_recommend_color3.setTextColor(0x80ffffff.toInt())
                view_recommend_bg_color3.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            3 -> {
                tv_recommend_color4.setTextColor(0x80ffffff.toInt())
                view_recommend_bg_color4.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
            4 -> {
                tv_recommend_color5.setTextColor(0x80ffffff.toInt())
                view_recommend_bg_color5.setBackgroundResource(R.drawable.bg_corners04_stroke_30_ff)
            }
        }
        when (index) {
            0 -> {
                tv_recommend_color1.setTextColor(0xffffba42.toInt())
                view_recommend_bg_color1.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            1 -> {
                tv_recommend_color2.setTextColor(0xffffba42.toInt())
                view_recommend_bg_color2.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            2 -> {
                tv_recommend_color3.setTextColor(0xffffba42.toInt())
                view_recommend_bg_color3.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            3 -> {
                tv_recommend_color4.setTextColor(0xffffba42.toInt())
                view_recommend_bg_color4.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            4 -> {
                tv_recommend_color5.setTextColor(0xffffba42.toInt())
                view_recommend_bg_color5.setBackgroundResource(R.drawable.bg_corners04_stroke_2dp_ffba42)
            }
        }
        customPseudoBean.customRecommendIndex = index
    }

    private fun switchUseGray(isUseGray: Boolean) {
        iv_over_grey_select.isVisible = isUseGray
        iv_over_color_select.isVisible = !isUseGray
        tv_over_grey.setTextColor(if (isUseGray) 0xffffba42.toInt() else 0xffffffff.toInt())
        tv_over_color.setTextColor(if (isUseGray) 0xffffffff.toInt() else 0xffffba42.toInt())
        cl_over_grey.setBackgroundResource(
            if (isUseGray) R.drawable.bg_corners05_solid_2a183e_stroke_theme else R.drawable.bg_corners05_solid_626569,
        )
        cl_over_color.setBackgroundResource(
            if (isUseGray) R.drawable.bg_corners05_solid_626569 else R.drawable.bg_corners05_solid_2a183e_stroke_theme,
        )
        customPseudoBean.isUseGray = isUseGray
    }

    private fun buildRectDrawableArray(color: IntArray): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = SizeUtils.dp2px(4f).toFloat()
        drawable.colors = color
        drawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        return drawable
    }
}
