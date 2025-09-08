package com.topdon.lib.core.binding

import android.content.res.TypedArray
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.SizeUtils

/**
 * RecyclerView [Chinese text] BindingAdapter.
 *
 * Created by LCG on 2024/11/5.
 */
object ViewBindingAdapter {
    /**
     * [Chinese text] view [Chinese text] background [Chinese text] selectableItemBackground [Chinese text].
     */
    @JvmStatic
    @BindingAdapter("bgEffect")
    fun setBgEffect(
        view: View,
        wantEffect: Boolean,
    ) {
        val oldDrawable: Drawable? = view.background
        if (oldDrawable is LayerDrawable) {
            val layerCount = oldDrawable.numberOfLayers
            val drawableList = ArrayList<Drawable>(layerCount + 1)
            for (i in 0 until layerCount) {
                if (oldDrawable.getId(i) == android.R.id.hint) {
                    if (wantEffect) {
                        return
                    }
                } else {
                    drawableList.add(oldDrawable.getDrawable(i))
                }
            }
            if (wantEffect) {
                val typedArray: TypedArray = view.context.obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
                val effectDrawable: Drawable? = typedArray.getDrawable(0)
                typedArray.recycle()
                if (effectDrawable != null) {
                    drawableList.add(effectDrawable)
                }
            } else {
                if (drawableList.size == layerCount) { // [Chinese text] hint
                    return
                }
                if (drawableList.isEmpty()) { // [Chinese text]1[Chinese text] hint, [Chinese text]
                    view.background = null
                    return
                }
            }

            if (drawableList.size == 1) {
                view.background = drawableList[0]
                return
            }

            val newDrawable = LayerDrawable(drawableList.toArray(arrayOf()))
            if (drawableList.size == 2 && drawableList[0] is GradientDrawable) {
                oldDrawable.setId(0, android.R.id.content)
            }
            if (wantEffect) {
                oldDrawable.setId(drawableList.size - 1, android.R.id.hint)
            }
            view.background = newDrawable
        } else {
            val typedArray: TypedArray = view.context.obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
            val effectDrawable: Drawable? = typedArray.getDrawable(0)
            typedArray.recycle()

            val newDrawable = LayerDrawable(if (oldDrawable == null) arrayOf(effectDrawable) else arrayOf(oldDrawable, effectDrawable))
            if (oldDrawable is GradientDrawable) {
                newDrawable.setId(0, android.R.id.content)
            }
            newDrawable.setId(if (oldDrawable == null) 0 else 1, android.R.id.hint)
            view.background = newDrawable
        }
    }

    /**
     * [Chinese text] shape [Chinese text] view [Chinese text] background [Chinese text]Settings[Chinese text].
     *
     * [Chinese text]: [Chinese text] bgXXX [Chinese text]Settings, [Chinese text]Settings[Chinese text] android:background [Chinese text]? 
     */
    @JvmStatic
    @BindingAdapter("bgColor")
    fun setBgColor(
        view: View,
        @ColorInt color: Int,
    ) {
        val gradientDrawable: GradientDrawable = buildGradientDrawable(view)
        gradientDrawable.setColor(color)
        view.background = buildEffectDrawable(view, gradientDrawable)
    }

    /**
     * [Chinese text] shape [Chinese text] view [Chinese text] background Settings[Chinese text], [Chinese text]**dp**.
     *
     * [Chinese text]: [Chinese text] bgXXX [Chinese text]Settings, [Chinese text]Settings. 
     * @param bgCorners 4[Chinese text], [Chinese text]dp
     * @param bgCornersLT left-top [Chinese text], [Chinese text], [Chinese text]dp
     * @param bgCornersRT right-top [Chinese text], [Chinese text], [Chinese text]dp
     * @param bgCornersLB left-bottom [Chinese text], [Chinese text], [Chinese text]dp
     * @param bgCornersRB right-bottom [Chinese text], [Chinese text], [Chinese text]dp
     */
    @JvmStatic
    @BindingAdapter(value = ["bgCorners", "bgCornersLT", "bgCornersRT", "bgCornersLB", "bgCornersRB"], requireAll = false)
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
            floatArrayOf(lt.toFloat(), lt.toFloat(), rt.toFloat(), rt.toFloat(), rb.toFloat(), rb.toFloat(), lb.toFloat(), lb.toFloat())
        val gradientDrawable: GradientDrawable = buildGradientDrawable(view)
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.cornerRadii = radii
        view.background = buildEffectDrawable(view, gradientDrawable)
    }

    /**
     * [Chinese text] shape [Chinese text] view [Chinese text] background Settings[Chinese text].
     * @param width [Chinese text], [Chinese text]dp
     * @param color [Chinese text]
     */
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

    /**
     * [Chinese text] shape [Chinese text] view [Chinese text] background Settings[Chinese text].
     */
    @JvmStatic
    @BindingAdapter(value = ["bgStartColor", "bgCenterColor", "bgEndColor"], requireAll = false)
    fun setBgGradientColor(
        view: View,
        @ColorInt startColor: Int,
        @ColorInt centerColor: Int?,
        @ColorInt endColor: Int,
    ) {
        val gradientDrawable: GradientDrawable = buildGradientDrawable(view)
        gradientDrawable.colors = if (centerColor == null) intArrayOf(startColor, endColor) else intArrayOf(startColor, centerColor, endColor)
        view.background = buildEffectDrawable(view, gradientDrawable)
    }

    /**
     * [Chinese text] shape [Chinese text] view [Chinese text] background Settings[Chinese text].
     * @param angle line[Chinese text]: [Chinese text], [Chinese text] 45 [Chinese text], 0[Chinese text] 90[Chinese text] -90[Chinese text]270[Chinese text]
     * @param radius [Chinese text]: [Chinese text]
     * @param centerX [Chinese text]: in progress[Chinese text]pointX[Chinese text]
     * @param centerY [Chinese text]: in progress[Chinese text]pointY[Chinese text]
     */
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
     * [Chinese text] view [Chinese text] background in progress[Chinese text] GradientDrawable.
     *
     * [Chinese text] view [Chinese text] background [Chinese text] GradientDrawable, [Chinese text]; 
     *
     * [Chinese text] view [Chinese text] background [Chinese text] ColorDrawable, [Chinese text] GradientDrawable; 
     *
     * [Chinese text] view [Chinese text] background [Chinese text] LayerDrawable, [Chinese text] background id [Chinese text] GradientDrawable; 
     *
     * [Chinese text] GradientDrawable [Chinese text]. 
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
     * [Chinese text] view [Chinese text] background [Chinese text]Settings bgEffect, [Chinese text] bgDrawable [Chinese text] bgEffect [Chinese text] LayerDrawable; 
     * [Chinese text] bgDrawable
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
