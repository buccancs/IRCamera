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
     * [Chinese text]
     */
    const val DEFAULT_ANIMATION_DURATION: Long = 400

    /**
     * [Chinese text]
     *
     * @param fromDegrees       start[Chinese text]
     * @param toDegrees         [Chinese text]
     * @param pivotXType        [Chinese text]in progress[Chinese text]pointX[Chinese text]
     * @param pivotXValue       [Chinese text]in progress[Chinese text]pointX[Chinese text]
     * @param pivotYType        [Chinese text]in progress[Chinese text]pointY[Chinese text]
     * @param pivotYValue       [Chinese text]in progress[Chinese text]pointY[Chinese text]
     * @param durationMillis    [Chinese text]
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]
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
     * [Chinese text]in progress[Chinese text]point[Chinese text]
     *
     * @param durationMillis    [Chinese text]
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]in progress[Chinese text]point[Chinese text]
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
     * [Chinese text]in progress[Chinese text]point[Chinese text]
     *
     * @param duration [Chinese text]
     * @return [Chinese text]in progress[Chinese text]point[Chinese text]
     */
    fun getRotateAnimationByCenter(duration: Long): RotateAnimation {
        return getRotateAnimationByCenter(duration, null)
    }

    /**
     * [Chinese text]in progress[Chinese text]point[Chinese text]
     *
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]in progress[Chinese text]point[Chinese text]
     */
    fun getRotateAnimationByCenter(animationListener: Animation.AnimationListener?): RotateAnimation {
        return getRotateAnimationByCenter(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [Chinese text]in progress[Chinese text]point[Chinese text]
     *
     * @return [Chinese text]in progress[Chinese text]point[Chinese text], [Chinese text]DEFAULT_ANIMATION_DURATION
     */
    val rotateAnimationByCenter: RotateAnimation
        get() = getRotateAnimationByCenter(DEFAULT_ANIMATION_DURATION, null)

    /**
     * [Chinese text]
     *
     * @param fromAlpha         start[Chinese text]
     * @param toAlpha           [Chinese text]
     * @param durationMillis    [Chinese text]
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]
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
     * [Chinese text]
     *
     * @param fromAlpha      start[Chinese text]
     * @param toAlpha        [Chinese text]
     * @param durationMillis [Chinese text]
     * @return [Chinese text]
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long
    ): AlphaAnimation {
        return getAlphaAnimation(fromAlpha, toAlpha, durationMillis, null)
    }

    /**
     * [Chinese text]
     *
     * @param fromAlpha         start[Chinese text]
     * @param toAlpha           [Chinese text]
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text], [Chinese text]DEFAULT_ANIMATION_DURATION
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
     * [Chinese text]
     *
     * @param fromAlpha start[Chinese text]
     * @param toAlpha   [Chinese text]
     * @return [Chinese text], [Chinese text]DEFAULT_ANIMATION_DURATION
     */
    fun getAlphaAnimation(fromAlpha: Float, toAlpha: Float): AlphaAnimation {
        return getAlphaAnimation(
            fromAlpha, toAlpha, DEFAULT_ANIMATION_DURATION,
            null
        )
    }

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @param durationMillis    [Chinese text]
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]visible[Chinese text]
     */
    fun getHiddenAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener)
    }

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @param durationMillis [Chinese text]
     * @return [Chinese text]visible[Chinese text]
     */
    fun getHiddenAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getHiddenAlphaAnimation(durationMillis, null)
    }

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]visible[Chinese text], [Chinese text]DEFAULT_ANIMATION_DURATION
     */
    fun getHiddenAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getHiddenAlphaAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @return [Chinese text]visible[Chinese text], [Chinese text]DEFAULT_ANIMATION_DURATION
     */
    val hiddenAlphaAnimation: AlphaAnimation
        get() = getHiddenAlphaAnimation(DEFAULT_ANIMATION_DURATION, null)

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @param durationMillis    [Chinese text]
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]visible[Chinese text]
     */
    fun getShowAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, animationListener)
    }

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @param durationMillis [Chinese text]
     * @return [Chinese text]visible[Chinese text]
     */
    fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
    }

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @param animationListener [Chinese text]listener[Chinese text]
     * @return [Chinese text]visible[Chinese text], [Chinese text]DEFAULT_ANIMATION_DURATION
     */
    fun getShowAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getAlphaAnimation(
            0.0f, 1.0f, DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [Chinese text]visible[Chinese text]
     *
     * @return [Chinese text]visible[Chinese text], [Chinese text]DEFAULT_ANIMATION_DURATION
     */
    val showAlphaAnimation: AlphaAnimation
        get() = getAlphaAnimation(0.0f, 1.0f, DEFAULT_ANIMATION_DURATION, null)

    /**
     * [Chinese text]
     *
     * @param durationMillis   [Chinese text]
     * @param animationListener  listener
     * @return [Chinese text]
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
     * [Chinese text]
     *
     * @param durationMillis [Chinese text]
     * @return [Chinese text]
     */
    fun getLessenScaleAnimation(durationMillis: Long): ScaleAnimation {
        return getLessenScaleAnimation(durationMillis, null)
    }

    /**
     * [Chinese text]
     *
     * @param animationListener  listener
     * @return [Chinese text]
     */
    fun getLessenScaleAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getLessenScaleAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * [Chinese text]
     * @param durationMillis   [Chinese text]
     * @param animationListener  listener
     *
     * @return [Chinese text]
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
     * [Chinese text]
     *
     * @param durationMillis   [Chinese text]
     *
     * @return [Chinese text]
     */
    fun getAmplificationAnimation(durationMillis: Long): ScaleAnimation {
        return getAmplificationAnimation(durationMillis, null)
    }

    /**
     * [Chinese text]
     *
     * @param animationListener  listener
     * @return [Chinese text]
     */
    fun getAmplificationAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getAmplificationAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }
}