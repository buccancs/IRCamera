package com.topdon.lib.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.topdon.lib.ui.R as UiR

class CountDownView : View {
    // [Chinese text]
    private var mRingColor = 0

    // [Chinese text]
    private var mRingWidth = 0

    // [Chinese text]
    private var mRingProgressTextSize = 0

    // [Chinese text]
    private var mWidth = 0

    // high[Chinese text]
    private var mHeight = 0

    // [Chinese text]
    private var mRingText: String? = null
    private lateinit var mPaint: Paint
    private lateinit var mTextPaint: Paint

    // [Chinese text]area
    private var mRectF: RectF? = null

    //
    private var mProgressTextColor = 0
    private var mCountdownTime = 0
    private var mCurrentProgress = 0f

    private var valueAnimator: ValueAnimator? = null

    /**
     * listenerevent
     */
    private var mListener: OnCountDownListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        val ta = context.obtainStyledAttributes(attrs, UiR.styleable.CountDownView)
        for (i in 0 until ta.indexCount) {
            when (ta.getIndex(i)) {
                UiR.styleable.CountDownView_ringColor ->
                    mRingColor =
                        ta.getColor(
                            UiR.styleable.CountDownView_ringColor,
                            ContextCompat.getColor(context, UiR.color.colorAccent),
                        )
                UiR.styleable.CountDownView_ringWidth ->
                    mRingWidth =
                        ta.getDimensionPixelSize(
                            UiR.styleable.CountDownView_ringWidth,
                            40,
                        )
                UiR.styleable.CountDownView_progressTextSize ->
                    mRingProgressTextSize =
                        ta.getDimensionPixelSize(
                            UiR.styleable.CountDownView_progressTextSize,
                            20,
                        )
                UiR.styleable.CountDownView_progressTextColor ->
                    mProgressTextColor =
                        ta.getColor(
                            UiR.styleable.CountDownView_progressTextColor,
                            context.resources.getColor(UiR.color.colorAccent),
                        )
                UiR.styleable.CountDownView_countdownTime ->
                    mCountdownTime =
                        ta.getInteger(
                            UiR.styleable.CountDownView_countdownTime,
                            60,
                        )
                UiR.styleable.CountDownView_progressText ->
                    mRingText =
                        ta.getString(UiR.styleable.CountDownView_progressText)
            }
        }
        ta.recycle()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.isAntiAlias = true
        mTextPaint = Paint()
        this.setWillNotDraw(false)
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = measuredWidth
        mHeight = measuredHeight
        mRectF =
            RectF(
                0 + mRingWidth / 2f,
                0 + mRingWidth / 2f,
                mWidth - mRingWidth / 2f,
                mHeight - mRingWidth / 2f,
            )
    }

    /**
     * Settings[Chinese text] [Chinese text]
     */
    fun setCountdownTime(mCountdownTime: Int) {
        this.mCountdownTime = mCountdownTime
        mRingText = mCountdownTime.toString()
        invalidate()
    }

    /**
     * [Chinese text]
     */
    private fun getValueAnimator(countdownTime: Long): ValueAnimator? {
        val valueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = countdownTime
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = 0
        return valueAnimator
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // [Chinese text]
        mPaint.color = mRingColor
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = mRingWidth.toFloat()
        canvas.drawArc(mRectF!!, -90f, mCurrentProgress - 360, false, mPaint)
        val font = Typeface.DEFAULT_BOLD
        // [Chinese text]
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.typeface = font
        // [Chinese text](5 4 3 2 1)
        // val text: String = (mCountdownTime - (mCurrentProgress / 360f * mCountdownTime)).toInt().toString()

        mTextPaint.textSize = mRingProgressTextSize.toFloat()
        mTextPaint.color = mProgressTextColor

        // text[Chinese text]in progress[Chinese text]
        val fontMetrics = mTextPaint.fontMetricsInt
        val baseline =
            ((mRectF!!.bottom + mRectF!!.top - fontMetrics.bottom - fontMetrics.top) / 2).toInt()
        canvas.drawText(mRingText!!, mRectF!!.centerX(), baseline.toFloat(), mTextPaint)
    }

    /**
     * start[Chinese text]
     */
    fun startCountDown() {
        valueAnimator = getValueAnimator((mCountdownTime * 1000).toLong())
        valueAnimator!!.addUpdateListener { animation ->
            val i = animation.animatedValue.toString().toFloat()
            mCurrentProgress = (360 * (i / 100f))
            invalidate()
        }
        valueAnimator!!.start()
        valueAnimator!!.addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    // [Chinese text]
                    if (mListener != null) {
                        mListener!!.countDownFinished()
                    }
                }
            },
        )
    }

    /**
     * stop[Chinese text]
     */
    fun stopCountDown() {
        if (valueAnimator!!.isRunning) {
            valueAnimator!!.cancel()
        }
    }

    fun setOnCountDownListener(mListener: OnCountDownListener) {
        this.mListener = mListener
    }

    interface OnCountDownListener {
        fun countDownFinished()
    }
}
