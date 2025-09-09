package com.infisense.usbir.utils
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation

/**
 * @author: CaiSongL
 * @date: 2022/6/9 22:14
 */
public object AnimaUtils{
    /**
     * [CN_TEXT]
     */
    const val DEFAULT_ANIMATION_DURATION: Long = 400

    /**
     * [CN_TEXT]Rotate[CN_TEXT]
     *
     * @param fromDegrees       [CN_TEXT]Angle
     * @param toDegrees         [CN_TEXT]Angle
     * @param pivotXType        Rotate[CN_TEXT]X[CN_TEXT]Type
     * @param pivotXValue       Rotate[CN_TEXT]X[CN_TEXT]
     * @param pivotYType        Rotate[CN_TEXT]Y[CN_TEXT]Type
     * @param pivotYValue       Rotate[CN_TEXT]Y[CN_TEXT]
     * @param durationMillis    [CN_TEXT]
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]Rotate[CN_TEXT]
     */
    fun getRotateAnimation(
        fromDegrees: Float,
        toDegrees: Float,
        pivotXType: Int,
        pivotXValue: Float,
        pivotYType: Int,
        pivotYValue: Float,
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): RotateAnimation {
        val rotateAnimation = RotateAnimation(
            fromDegrees,
            toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue
        )
        rotateAnimation.duration = durationMillis
        if (animationListener != null) {
            rotateAnimation.setAnimationListener(animationListener)
        }
        return rotateAnimation
    }

    /**
     * [CN_TEXT]Rotate[CN_TEXT]
     *
     * @param durationMillis    [CN_TEXT]
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]Rotate[CN_TEXT]
     */
    fun getRotateAnimationByCenter(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): RotateAnimation {
        return getRotateAnimation(
            0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f, durationMillis,
            animationListener
        )
    }

    /**
     * [CN_TEXT]Rotate[CN_TEXT]
     *
     * @param duration [CN_TEXT]
     * @return [CN_TEXT]Rotate[CN_TEXT]
     */
    fun getRotateAnimationByCenter(duration: Long): RotateAnimation {
        return getRotateAnimationByCenter(duration, null)
    }

    /**
     * [CN_TEXT]Rotate[CN_TEXT]
     *
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]Rotate[CN_TEXT]
     */
    fun getRotateAnimationByCenter(animationListener: Animation.AnimationListener?): RotateAnimation {
        return getRotateAnimationByCenter(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [CN_TEXT]Rotate[CN_TEXT]
     *
     * @return [CN_TEXT]Rotate[CN_TEXT]，[CN_TEXT]DEFAULT_ANIMATION_DURATION
     */
    val rotateAnimationByCenter: RotateAnimation
        get() = getRotateAnimationByCenter(DEFAULT_ANIMATION_DURATION, null)

    /**
     * [CN_TEXT]
     *
     * @param fromAlpha         [CN_TEXT]
     * @param toAlpha           [CN_TEXT]
     * @param durationMillis    [CN_TEXT]
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        val alphaAnimation = AlphaAnimation(fromAlpha, toAlpha)
        alphaAnimation.duration = durationMillis
        if (animationListener != null) {
            alphaAnimation.setAnimationListener(animationListener)
        }
        return alphaAnimation
    }

    /**
     * [CN_TEXT]
     *
     * @param fromAlpha      [CN_TEXT]
     * @param toAlpha        [CN_TEXT]
     * @param durationMillis [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long
    ): AlphaAnimation {
        return getAlphaAnimation(fromAlpha, toAlpha, durationMillis, null)
    }

    /**
     * [CN_TEXT]
     *
     * @param fromAlpha         [CN_TEXT]
     * @param toAlpha           [CN_TEXT]
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]，[CN_TEXT]DEFAULT_ANIMATION_DURATION
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        return getAlphaAnimation(
            fromAlpha, toAlpha, DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [CN_TEXT]
     *
     * @param fromAlpha [CN_TEXT]
     * @param toAlpha   [CN_TEXT]
     * @return [CN_TEXT]，[CN_TEXT]DEFAULT_ANIMATION_DURATION
     */
    fun getAlphaAnimation(fromAlpha: Float, toAlpha: Float): AlphaAnimation {
        return getAlphaAnimation(
            fromAlpha, toAlpha, DEFAULT_ANIMATION_DURATION,
            null
        )
    }

    /**
     * [CN_TEXT]
     *
     * @param durationMillis    [CN_TEXT]
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getHiddenAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener)
    }

    /**
     * [CN_TEXT]
     *
     * @param durationMillis [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getHiddenAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getHiddenAlphaAnimation(durationMillis, null)
    }

    /**
     * [CN_TEXT]
     *
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]，[CN_TEXT]DEFAULT_ANIMATION_DURATION
     */
    fun getHiddenAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getHiddenAlphaAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [CN_TEXT]
     *
     * @return [CN_TEXT]，[CN_TEXT]DEFAULT_ANIMATION_DURATION
     */
    val hiddenAlphaAnimation: AlphaAnimation
        get() = getHiddenAlphaAnimation(DEFAULT_ANIMATION_DURATION, null)

    /**
     * [CN_TEXT]
     *
     * @param durationMillis    [CN_TEXT]
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getShowAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, animationListener)
    }

    /**
     * [CN_TEXT]
     *
     * @param durationMillis [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
    }

    /**
     * [CN_TEXT]
     *
     * @param animationListener [CN_TEXT]
     * @return [CN_TEXT]，[CN_TEXT]DEFAULT_ANIMATION_DURATION
     */
    fun getShowAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getAlphaAnimation(
            0.0f, 1.0f, DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [CN_TEXT]
     *
     * @return [CN_TEXT]，[CN_TEXT]DEFAULT_ANIMATION_DURATION
     */
    val showAlphaAnimation: AlphaAnimation
        get() = getAlphaAnimation(0.0f, 1.0f, DEFAULT_ANIMATION_DURATION, null)

    /**
     * [CN_TEXT]
     *
     * @param durationMillis   [CN_TEXT]
     * @param animationListener  [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getLessenScaleAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): ScaleAnimation {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.0f, 1.0f,
            0.0f, ScaleAnimation.RELATIVE_TO_SELF.toFloat(),
            ScaleAnimation.RELATIVE_TO_SELF.toFloat()
        )
        scaleAnimation.duration = durationMillis
        scaleAnimation.setAnimationListener(animationListener)
        return scaleAnimation
    }

    /**
     * [CN_TEXT]
     *
     * @param durationMillis [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getLessenScaleAnimation(durationMillis: Long): ScaleAnimation {
        return getLessenScaleAnimation(durationMillis, null)
    }

    /**
     * [CN_TEXT]
     *
     * @param animationListener  [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getLessenScaleAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getLessenScaleAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [CN_TEXT]
     * @param durationMillis   [CN_TEXT]
     * @param animationListener  [CN_TEXT]
     *
     * @return [CN_TEXT]
     */
    fun getAmplificationAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): ScaleAnimation {
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f, 0.0f,
            1.0f, ScaleAnimation.RELATIVE_TO_SELF.toFloat(),
            ScaleAnimation.RELATIVE_TO_SELF.toFloat()
        )
        scaleAnimation.duration = durationMillis
        scaleAnimation.setAnimationListener(animationListener)
        return scaleAnimation
    }

    /**
     * [CN_TEXT]
     *
     * @param durationMillis   [CN_TEXT]
     *
     * @return [CN_TEXT]
     */
    fun getAmplificationAnimation(durationMillis: Long): ScaleAnimation {
        return getAmplificationAnimation(durationMillis, null)
    }

    /**
     * [CN_TEXT]
     *
     * @param animationListener  [CN_TEXT]
     * @return [CN_TEXT]
     */
    fun getAmplificationAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getAmplificationAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }
}