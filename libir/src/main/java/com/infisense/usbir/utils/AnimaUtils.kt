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
     * Default animation duration
     */
    const val DEFAULT_ANIMATION_DURATION: Long = 400

    /**
     * Get a rotation animation
     *
     * @param fromDegrees       Start angle
     * @param toDegrees         End angle
     * @param pivotXType        Rotation center point X-axis coordinate relative type
     * @param pivotXValue       Rotation center point X-axis coordinate
     * @param pivotYType        Rotation center point Y-axis coordinate relative type
     * @param pivotYValue       Rotation center point Y-axis coordinate
     * @param durationMillis    Duration
     * @param animationListener Animation listener
     * @return A rotation animation
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
     * Get a rotation animation based on view's center point
     *
     * @param durationMillis    Animation duration
     * @param animationListener Animation listener
     * @return A rotation animation based on center point
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
     * Get a rotation animation based on center point
     *
     * @param duration Animation duration
     * @return A rotation animation based on center point
     */
    fun getRotateAnimationByCenter(duration: Long): RotateAnimation {
        return getRotateAnimationByCenter(duration, null)
    }

    /**
     * Get a rotation animation based on view center point
     *
     * @param animationListener Animation listener
     * @return A rotation animation based on center point
     */
    fun getRotateAnimationByCenter(animationListener: Animation.AnimationListener?): RotateAnimation {
        return getRotateAnimationByCenter(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * Get A rotation animation based on center point
     *
     * @return A rotation animation based on center point, default duration is DEFAULT_ANIMATION_DURATION
     */
    val rotateAnimationByCenter: RotateAnimation
        get() = getRotateAnimationByCenter(DEFAULT_ANIMATION_DURATION, null)

    /**
     * Get an alpha fade animation
     *
     * @param fromAlpha         Starting alpha value
     * @param toAlpha           Ending alpha value
     * @param durationMillis    Duration
     * @param animationListener Animation listener
     * @return An alpha fade animation
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
     * Get an alpha fade animation
     *
     * @param fromAlpha      Starting alpha value
     * @param toAlpha        Ending alpha value
     * @param durationMillis Duration
     * @return An alpha fade animation
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long
    ): AlphaAnimation {
        return getAlphaAnimation(fromAlpha, toAlpha, durationMillis, null)
    }

    /**
     * Get an alpha fade animation
     *
     * @param fromAlpha         Starting alpha value
     * @param toAlpha           Ending alpha value
     * @param animationListener Animation listener
     * @return An alpha fade animation, default duration is DEFAULT_ANIMATION_DURATION
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
     * Get an alpha fade animation
     *
     * @param fromAlpha Starting alpha value
     * @param toAlpha   Ending alpha value
     * @return An alpha fade animation, default duration is DEFAULT_ANIMATION_DURATION
     */
    fun getAlphaAnimation(fromAlpha: Float, toAlpha: Float): AlphaAnimation {
        return getAlphaAnimation(
            fromAlpha, toAlpha, DEFAULT_ANIMATION_DURATION,
            null
        )
    }

    /**
     * Get a fade out animation from fully visible to invisible
     *
     * @param durationMillis    Duration
     * @param animationListener Animation listener
     * @return A fade out animation from fully visible to invisible
     */
    fun getHiddenAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener)
    }

    /**
     * Get a fade out animation from fully visible to invisible
     *
     * @param durationMillis Duration
     * @return A fade out animation from fully visible to invisible
     */
    fun getHiddenAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getHiddenAlphaAnimation(durationMillis, null)
    }

    /**
     * Get a fade out animation from fully visible to invisible
     *
     * @param animationListener Animation listener
     * @return A fade out animation from fully visible to invisible, default duration is DEFAULT_ANIMATION_DURATION
     */
    fun getHiddenAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getHiddenAlphaAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * Get a fade out animation from fully visible to invisible
     *
     * @return A fade out animation from fully visible to invisible, default duration is DEFAULT_ANIMATION_DURATION
     */
    val hiddenAlphaAnimation: AlphaAnimation
        get() = getHiddenAlphaAnimation(DEFAULT_ANIMATION_DURATION, null)

    /**
     * Get a fade in animation from invisible to fully visible
     *
     * @param durationMillis    Duration
     * @param animationListener Animation listener
     * @return A fade in animation from invisible to fully visible
     */
    fun getShowAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?
    ): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, animationListener)
    }

    /**
     * Get a fade in animation from invisible to fully visible
     *
     * @param durationMillis Duration
     * @return A fade in animation from invisible to fully visible
     */
    fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
    }

    /**
     * Get a fade in animation from invisible to fully visible
     *
     * @param animationListener Animation listener
     * @return A fade in animation from invisible to fully visible, default duration is DEFAULT_ANIMATION_DURATION
     */
    fun getShowAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getAlphaAnimation(
            0.0f, 1.0f, DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * Get a fade in animation from invisible to fully visible
     *
     * @return A fade in animation from invisible to fully visible, default duration is DEFAULT_ANIMATION_DURATION
     */
    val showAlphaAnimation: AlphaAnimation
        get() = getAlphaAnimation(0.0f, 1.0f, DEFAULT_ANIMATION_DURATION, null)

    /**
     * Get a scale down animation
     *
     * @param durationMillis   Duration
     * @param animationListener  Listener
     * @return A scale down animation
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
     * Get a scale down animation
     *
     * @param durationMillis Duration
     * @return A scale down animation
     */
    fun getLessenScaleAnimation(durationMillis: Long): ScaleAnimation {
        return getLessenScaleAnimation(durationMillis, null)
    }

    /**
     * Get a scale down animation
     *
     * @param animationListener  Listener
     * @return Return a scale down animation
     */
    fun getLessenScaleAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getLessenScaleAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }

    /**
     * Get a scale up animation
     * @param durationMillis   Duration
     * @param animationListener  Listener
     *
     * @return Return a scale up effect
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
     * Get a scale up animation
     *
     * @param durationMillis   Duration
     *
     * @return Return a scale up effect
     */
    fun getAmplificationAnimation(durationMillis: Long): ScaleAnimation {
        return getAmplificationAnimation(durationMillis, null)
    }

    /**
     * Get a scale up animation
     *
     * @param animationListener  Listener
     * @return Return a scale up effect
     */
    fun getAmplificationAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getAmplificationAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener
        )
    }
}