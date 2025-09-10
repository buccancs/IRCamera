package com.infisense.usbir.utils
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation

/**
 * @author: CaiSongL
 * @date: 2022/6/9 22:14
 */
public object AnimaUtils {
    /**
     * utility
     */
    const val DEFAULT_ANIMATION_DURATION: Long = 400

    /**
     * utilityRotateutility
     *
     * @param fromDegrees       utilityAngle
     * @param toDegrees         utilityAngle
     * @param pivotXType        RotateutilityXutilityType
     * @param pivotXValue       RotateutilityXutility
     * @param pivotYType        RotateutilityYutilityType
     * @param pivotYValue       RotateutilityYutility
     * @param durationMillis    utility
     * @param animationListener utility
     * @return utilityRotateutility
     */
    fun getRotateAnimation(
        fromDegrees: Float,
        toDegrees: Float,
        pivotXType: Int,
        pivotXValue: Float,
        pivotYType: Int,
        pivotYValue: Float,
        durationMillis: Long,
        animationListener: Animation.AnimationListener?,
    ): RotateAnimation {
        val rotateAnimation =
            RotateAnimation(
                fromDegrees,
                toDegrees,
                pivotXType,
                pivotXValue,
                pivotYType,
                pivotYValue,
            )
        rotateAnimation.duration = durationMillis
        if (animationListener != null) {
            rotateAnimation.setAnimationListener(animationListener)
        }
        return rotateAnimation
    }

    /**
     * utilityRotateutility
     *
     * @param durationMillis    utility
     * @param animationListener utility
     * @return utilityRotateutility
     */
    fun getRotateAnimationByCenter(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?,
    ): RotateAnimation {
        return getRotateAnimation(
            0f,
            359f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            durationMillis,
            animationListener,
        )
    }

    /**
     * utilityRotateutility
     *
     * @param duration utility
     * @return utilityRotateutility
     */
    fun getRotateAnimationByCenter(duration: Long): RotateAnimation {
        return getRotateAnimationByCenter(duration, null)
    }

    /**
     * utilityRotateutility
     *
     * @param animationListener utility
     * @return utilityRotateutility
     */
    fun getRotateAnimationByCenter(animationListener: Animation.AnimationListener?): RotateAnimation {
        return getRotateAnimationByCenter(
            DEFAULT_ANIMATION_DURATION,
            animationListener,
        )
    }

    /**
     * utilityRotateutility
     *
     * @return utilityRotateutility，utilityDEFAULT_ANIMATION_DURATION
     */
    val rotateAnimationByCenter: RotateAnimation
        get() = getRotateAnimationByCenter(DEFAULT_ANIMATION_DURATION, null)

    /**
     * utility
     *
     * @param fromAlpha         utility
     * @param toAlpha           utility
     * @param durationMillis    utility
     * @param animationListener utility
     * @return utility
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long,
        animationListener: Animation.AnimationListener?,
    ): AlphaAnimation {
        val alphaAnimation = AlphaAnimation(fromAlpha, toAlpha)
        alphaAnimation.duration = durationMillis
        if (animationListener != null) {
            alphaAnimation.setAnimationListener(animationListener)
        }
        return alphaAnimation
    }

    /**
     * utility
     *
     * @param fromAlpha      utility
     * @param toAlpha        utility
     * @param durationMillis utility
     * @return utility
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        durationMillis: Long,
    ): AlphaAnimation {
        return getAlphaAnimation(fromAlpha, toAlpha, durationMillis, null)
    }

    /**
     * utility
     *
     * @param fromAlpha         utility
     * @param toAlpha           utility
     * @param animationListener utility
     * @return utility，utilityDEFAULT_ANIMATION_DURATION
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
        animationListener: Animation.AnimationListener?,
    ): AlphaAnimation {
        return getAlphaAnimation(
            fromAlpha,
            toAlpha,
            DEFAULT_ANIMATION_DURATION,
            animationListener,
        )
    }

    /**
     * utility
     *
     * @param fromAlpha utility
     * @param toAlpha   utility
     * @return utility，utilityDEFAULT_ANIMATION_DURATION
     */
    fun getAlphaAnimation(
        fromAlpha: Float,
        toAlpha: Float,
    ): AlphaAnimation {
        return getAlphaAnimation(
            fromAlpha,
            toAlpha,
            DEFAULT_ANIMATION_DURATION,
            null,
        )
    }

    /**
     * utility
     *
     * @param durationMillis    utility
     * @param animationListener utility
     * @return utility
     */
    fun getHiddenAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?,
    ): AlphaAnimation {
        return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener)
    }

    /**
     * utility
     *
     * @param durationMillis utility
     * @return utility
     */
    fun getHiddenAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getHiddenAlphaAnimation(durationMillis, null)
    }

    /**
     * utility
     *
     * @param animationListener utility
     * @return utility，utilityDEFAULT_ANIMATION_DURATION
     */
    fun getHiddenAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getHiddenAlphaAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener,
        )
    }

    /**
     * utility
     *
     * @return utility，utilityDEFAULT_ANIMATION_DURATION
     */
    val hiddenAlphaAnimation: AlphaAnimation
        get() = getHiddenAlphaAnimation(DEFAULT_ANIMATION_DURATION, null)

    /**
     * utility
     *
     * @param durationMillis    utility
     * @param animationListener utility
     * @return utility
     */
    fun getShowAlphaAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?,
    ): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, animationListener)
    }

    /**
     * utility
     *
     * @param durationMillis utility
     * @return utility
     */
    fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
        return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
    }

    /**
     * utility
     *
     * @param animationListener utility
     * @return utility，utilityDEFAULT_ANIMATION_DURATION
     */
    fun getShowAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
        return getAlphaAnimation(
            0.0f,
            1.0f,
            DEFAULT_ANIMATION_DURATION,
            animationListener,
        )
    }

    /**
     * utility
     *
     * @return utility，utilityDEFAULT_ANIMATION_DURATION
     */
    val showAlphaAnimation: AlphaAnimation
        get() = getAlphaAnimation(0.0f, 1.0f, DEFAULT_ANIMATION_DURATION, null)

    /**
     * utility
     *
     * @param durationMillis   utility
     * @param animationListener  utility
     * @return utility
     */
    fun getLessenScaleAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?,
    ): ScaleAnimation {
        val scaleAnimation =
            ScaleAnimation(
                1.0f,
                0.0f,
                1.0f,
                0.0f,
                ScaleAnimation.RELATIVE_TO_SELF.toFloat(),
                ScaleAnimation.RELATIVE_TO_SELF.toFloat(),
            )
        scaleAnimation.duration = durationMillis
        scaleAnimation.setAnimationListener(animationListener)
        return scaleAnimation
    }

    /**
     * utility
     *
     * @param durationMillis utility
     * @return utility
     */
    fun getLessenScaleAnimation(durationMillis: Long): ScaleAnimation {
        return getLessenScaleAnimation(durationMillis, null)
    }

    /**
     * utility
     *
     * @param animationListener  utility
     * @return utility
     */
    fun getLessenScaleAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getLessenScaleAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener,
        )
    }

    /**
     * utility
     * @param durationMillis   utility
     * @param animationListener  utility
     *
     * @return utility
     */
    fun getAmplificationAnimation(
        durationMillis: Long,
        animationListener: Animation.AnimationListener?,
    ): ScaleAnimation {
        val scaleAnimation =
            ScaleAnimation(
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                ScaleAnimation.RELATIVE_TO_SELF.toFloat(),
                ScaleAnimation.RELATIVE_TO_SELF.toFloat(),
            )
        scaleAnimation.duration = durationMillis
        scaleAnimation.setAnimationListener(animationListener)
        return scaleAnimation
    }

    /**
     * utility
     *
     * @param durationMillis   utility
     *
     * @return utility
     */
    fun getAmplificationAnimation(durationMillis: Long): ScaleAnimation {
        return getAmplificationAnimation(durationMillis, null)
    }

    /**
     * utility
     *
     * @param animationListener  utility
     * @return utility
     */
    fun getAmplificationAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
        return getAmplificationAnimation(
            DEFAULT_ANIMATION_DURATION,
            animationListener,
        )
    }
}
