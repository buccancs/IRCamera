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
 * Comment removed (contained Chinese characters)
 */
 const val DEFAULT_ANIMATION_DURATION: Long = 400

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getRotateAnimationByCenter(duration: Long): RotateAnimation {
 return getRotateAnimationByCenter(duration, null)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getRotateAnimationByCenter(animationListener: Animation.AnimationListener?): RotateAnimation {
 return getRotateAnimationByCenter(
 DEFAULT_ANIMATION_DURATION,
 animationListener,
 )
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 val rotateAnimationByCenter: RotateAnimation
 get() = getRotateAnimationByCenter(DEFAULT_ANIMATION_DURATION, null)

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getAlphaAnimation(
 fromAlpha: Float,
 toAlpha: Float,
 durationMillis: Long,
 ): AlphaAnimation {
 return getAlphaAnimation(fromAlpha, toAlpha, durationMillis, null)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getHiddenAlphaAnimation(
 durationMillis: Long,
 animationListener: Animation.AnimationListener?,
 ): AlphaAnimation {
 return getAlphaAnimation(1.0f, 0.0f, durationMillis, animationListener)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getHiddenAlphaAnimation(durationMillis: Long): AlphaAnimation {
 return getHiddenAlphaAnimation(durationMillis, null)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getHiddenAlphaAnimation(animationListener: Animation.AnimationListener?): AlphaAnimation {
 return getHiddenAlphaAnimation(
 DEFAULT_ANIMATION_DURATION,
 animationListener,
 )
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 val hiddenAlphaAnimation: AlphaAnimation
 get() = getHiddenAlphaAnimation(DEFAULT_ANIMATION_DURATION, null)

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getShowAlphaAnimation(
 durationMillis: Long,
 animationListener: Animation.AnimationListener?,
 ): AlphaAnimation {
 return getAlphaAnimation(0.0f, 1.0f, durationMillis, animationListener)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getShowAlphaAnimation(durationMillis: Long): AlphaAnimation {
 return getAlphaAnimation(0.0f, 1.0f, durationMillis, null)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 val showAlphaAnimation: AlphaAnimation
 get() = getAlphaAnimation(0.0f, 1.0f, DEFAULT_ANIMATION_DURATION, null)

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getLessenScaleAnimation(durationMillis: Long): ScaleAnimation {
 return getLessenScaleAnimation(durationMillis, null)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getLessenScaleAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
 return getLessenScaleAnimation(
 DEFAULT_ANIMATION_DURATION,
 animationListener,
 )
 }

 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 fun getAmplificationAnimation(durationMillis: Long): ScaleAnimation {
 return getAmplificationAnimation(durationMillis, null)
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 fun getAmplificationAnimation(animationListener: Animation.AnimationListener?): ScaleAnimation {
 return getAmplificationAnimation(
 DEFAULT_ANIMATION_DURATION,
 animationListener,
 )
 }
}
