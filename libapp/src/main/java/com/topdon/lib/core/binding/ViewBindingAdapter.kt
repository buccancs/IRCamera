package com.topdon.lib.core.binding

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.SizeUtils

/**
 * Data binding adapters for view background styling
 */
object ViewBindingAdapter {

    @JvmStatic
    @BindingAdapter(
        value = ["bgCorners", "bgCornersLT", "bgCornersRT", "bgCornersLB", "bgCornersRB"],
        requireAll = false,
    )
    fun setBgCorners(
        view: View,
        bgCorners: Int = 0,
        bgCornersLT: Int?,
        bgCornersRT: Int?,
        bgCornersLB: Int?,
        bgCornersRB: Int?,
    ) {
        val lt: Int = SizeUtils.dp2px(bgCornersLT?.toFloat() ?: bgCorners.toFloat())
        val rt: Int = SizeUtils.dp2px(bgCornersRT?.toFloat() ?: bgCorners.toFloat())
        val lb: Int = SizeUtils.dp2px(bgCornersLB?.toFloat() ?: bgCorners.toFloat())
        val rb: Int = SizeUtils.dp2px(bgCornersRB?.toFloat() ?: bgCorners.toFloat())
        val radii =
            floatArrayOf(
                lt.toFloat(),
                lt.toFloat(),
                rt.toFloat(),
                rt.toFloat(),
                rb.toFloat(),
                rb.toFloat(),
                lb.toFloat(),
                lb.toFloat(),
            )
        val gradientDrawable: GradientDrawable = buildGradientDrawable(view)
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.cornerRadii = radii
        view.background = buildEffectDrawable(view, gradientDrawable)
    }

    @JvmStatic
    @BindingAdapter(value = ["bgStrokeWidth", "bgStrokeColor"], requireAll = false)
    fun setBgStroke(
        view: View,
        width: Int,
        @ColorInt color: Int,
    ) {
        val gradientDrawable: GradientDrawable = buildGradientDrawable(view)
        gradientDrawable.setStroke(SizeUtils.dp2px(width.toFloat()), color)
        view.background = buildEffectDrawable(view, gradientDrawable)
    }

    @JvmStatic
    @BindingAdapter(value = ["bgAngle", "bgRadius", "bgCenterX", "bgCenterY"], requireAll = false)
    fun setBgGradient(
        view: View,
        angle: Int?,
        radius: Float?,
        centerX: Float?,
        centerY: Float?,
    ) {
        val gradientDrawable: GradientDrawable = buildGradientDrawable(view)
        if (angle == null) {
            if (radius == null) {
                gradientDrawable.gradientType = GradientDrawable.SWEEP_GRADIENT
            } else {
                gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
                gradientDrawable.gradientRadius = radius
            }
            if (centerX != null && centerY != null) {
                gradientDrawable.setGradientCenter(centerX, centerY)
            }
        } else {
            gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            gradientDrawable.orientation = getOrientation(angle)
        }
        view.background = buildEffectDrawable(view, gradientDrawable)
    }

    @JvmStatic
    private fun getOrientation(angle: Int): GradientDrawable.Orientation {
        return when ((angle % 360 + 360) % 360) {
            0 -> GradientDrawable.Orientation.LEFT_RIGHT
            45 -> GradientDrawable.Orientation.BL_TR
            90 -> GradientDrawable.Orientation.BOTTOM_TOP
            135 -> GradientDrawable.Orientation.BR_TL
            180 -> GradientDrawable.Orientation.RIGHT_LEFT
            225 -> GradientDrawable.Orientation.TR_BL
            270 -> GradientDrawable.Orientation.TOP_BOTTOM
            315 -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.LEFT_RIGHT
        }
    }

    /**
     * itemSpecified view item background item GradientDrawable.
     *
     * itemSpecified view item background item GradientDrawable，item；
     *
     * itemSpecified view item background item ColorDrawable，item GradientDrawable；
     *
     * itemSpecified view item background item LayerDrawable，item background id item GradientDrawable；
     *
     * item GradientDrawable item。
     */
    @JvmStatic
    private fun buildGradientDrawable(view: View): GradientDrawable {
        val oldDrawable: Drawable? = view.background
        if (oldDrawable is GradientDrawable) {
            return oldDrawable
        }
        if (oldDrawable is ColorDrawable) {
            val drawable = GradientDrawable()
            drawable.setColor(oldDrawable.color)
            return drawable
        }
        if (oldDrawable is LayerDrawable) {
            val drawable: Drawable? = oldDrawable.findDrawableByLayerId(android.R.id.content)
            if (drawable is GradientDrawable) {
                return drawable
            }
        }
        return GradientDrawable()
    }

    /**
     * item view item background itemSettings bgEffect，item bgDrawable item bgEffect item LayerDrawable；
     * item bgDrawable
     */
    @JvmStatic
    private fun buildEffectDrawable(
        view: View,
        bgDrawable: GradientDrawable,
    ): Drawable {
        val oldDrawable: Drawable? = view.background
        if (oldDrawable is LayerDrawable) {
            val effectDrawable: Drawable = oldDrawable.findDrawableByLayerId(android.R.id.hint) ?: return bgDrawable
            val newDrawable = LayerDrawable(arrayOf(bgDrawable, effectDrawable))
            newDrawable.setId(0, android.R.id.content)
            newDrawable.setId(1, android.R.id.hint)
            return newDrawable
        }
        return bgDrawable
    }
}
