package com.topdon.pseudo.activity

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.view.ColorSelectView
import com.topdon.pseudo.R
import com.topdon.pseudo.bean.CustomPseudoBean
import com.topdon.pseudo.constant.*
import com.topdon.pseudo.view.PseudoPickView
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode
import com.topdon.lib.core.R as RCore // For drawable resources from libapp
import com.topdon.lib.ui.R as RUi // For string resources from libui

    private fun switchDynamicCustom(isToCustom: Boolean) {
        customPseudoBean.isUseCustomPseudo = isToCustom
        clCustomContent.isVisible = isToCustom
        clDynamic.isSelected = !isToCustom
        clCustom.isSelected = isToCustom
        ivDynamic.setImageResource(
            if (isToCustom) R.drawable.svg_pseudo_set_dynamic_not_select else R.drawable.svg_pseudo_set_dynamic_select,
        )
        ivCustom.setImageResource(
            if (isToCustom) R.drawable.svg_pseudo_set_custom_select else R.drawable.svg_pseudo_set_custom_not_select,
        )
        tvDynamicTitle.setTextColor(if (isToCustom) 0xffffffff.toInt() else 0xffffba42.toInt())
        tvCustomTitle.setTextColor(if (isToCustom) 0xffffba42.toInt() else 0xffffffff.toInt())
    }

    private fun switchColorType(isToCustom: Boolean) {
        customPseudoBean.isColorCustom = isToCustom
        clColorCustom.isVisible = isToCustom
        clColorRecommend.isVisible = !isToCustom
        tvColorCustom.setTextColor(if (isToCustom) 0xffffba42.toInt() else 0xffffffff.toInt())
        tvColorRecommend.setTextColor(if (isToCustom) 0xffffffff.toInt() else 0xffffba42.toInt())
        tvColorCustom.setBackgroundResource(
            if (isToCustom) RCore.drawable.bg_corners50_solid_2a183e_stroke_theme else 0,
        )
        tvColorRecommend.setBackgroundResource(
            if (isToCustom) 0 else RCore.drawable.bg_corners50_solid_2a183e_stroke_theme,
        )
    }

    private fun switchRecommendColorIndex(index: Int) {
        when (customPseudoBean.customRecommendIndex) {
            0 -> {
                tvRecommendColor1.setTextColor(0x80ffffff.toInt())
                viewRecommendBgColor1.setBackgroundResource(RCore.drawable.bg_corners04_stroke_30_ff)
            }
            1 -> {
                tvRecommendColor2.setTextColor(0x80ffffff.toInt())
                viewRecommendBgColor2.setBackgroundResource(RCore.drawable.bg_corners04_stroke_30_ff)
            }
            2 -> {
                tvRecommendColor3.setTextColor(0x80ffffff.toInt())
                viewRecommendBgColor3.setBackgroundResource(RCore.drawable.bg_corners04_stroke_30_ff)
            }
            3 -> {
                tvRecommendColor4.setTextColor(0x80ffffff.toInt())
                viewRecommendBgColor4.setBackgroundResource(RCore.drawable.bg_corners04_stroke_30_ff)
            }
            4 -> {
                tvRecommendColor5.setTextColor(0x80ffffff.toInt())
                viewRecommendBgColor5.setBackgroundResource(RCore.drawable.bg_corners04_stroke_30_ff)
            }
        }
        when (index) {
            0 -> {
                tvRecommendColor1.setTextColor(0xffffba42.toInt())
                viewRecommendBgColor1.setBackgroundResource(RCore.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            1 -> {
                tvRecommendColor2.setTextColor(0xffffba42.toInt())
                viewRecommendBgColor2.setBackgroundResource(RCore.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            2 -> {
                tvRecommendColor3.setTextColor(0xffffba42.toInt())
                viewRecommendBgColor3.setBackgroundResource(RCore.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            3 -> {
                tvRecommendColor4.setTextColor(0xffffba42.toInt())
                viewRecommendBgColor4.setBackgroundResource(RCore.drawable.bg_corners04_stroke_2dp_ffba42)
            }
            4 -> {
                tvRecommendColor5.setTextColor(0xffffba42.toInt())
                viewRecommendBgColor5.setBackgroundResource(RCore.drawable.bg_corners04_stroke_2dp_ffba42)
            }
        }
        customPseudoBean.customRecommendIndex = index
    }

    private fun switchUseGray(isUseGray: Boolean) {
        ivOverGreySelect.isVisible = isUseGray
        ivOverColorSelect.isVisible = !isUseGray
        tvOverGrey.setTextColor(if (isUseGray) 0xffffba42.toInt() else 0xffffffff.toInt())
        tvOverColor.setTextColor(if (isUseGray) 0xffffffff.toInt() else 0xffffba42.toInt())
        clOverGrey.setBackgroundResource(
            if (isUseGray) RCore.drawable.bg_corners05_solid_2a183e_stroke_theme else RCore.drawable.bg_corners05_solid_626569,
        )
        clOverColor.setBackgroundResource(
            if (isUseGray) RCore.drawable.bg_corners05_solid_626569 else RCore.drawable.bg_corners05_solid_2a183e_stroke_theme,
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
