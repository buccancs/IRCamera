package com.topdon.module.thermal.ir.view

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.animation.*
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.SizeUtils
import java.util.*

/**
 * @author: CaiSongL
 * @date: 2023/4/7 23:43
 */
public class TimeDownView : AppCompatTextView {
    private var timer: Timer? = null
    private var downTimerTask: DownTimerTask? = null
    private var downCount = 0
    private var lastDown = 0
    private var intervalMills: Long = 0
    private var delayMills: Long = 0
    private var animationSet: AnimationSet? = null
    var isRunning = false
    private fun init() {
        if (animationSet == null) {
            animationSet = AnimationSet(true)
        }
        if (downHandler == null) {
            downHandler = DownHandler()
        }
        gravity = Gravity.CENTER
        textSize = SizeUtils.sp2px(30f).toFloat()
    }
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    /**
     * start[Chinese text]
     *
     * @param seconds
     */
    fun downSecond(seconds: Int) {
        downSecond(seconds,true)
    }

    fun downSecond(seconds: Int,openAnimation: Boolean) {
        if (seconds == 0){
            isRunning = false
            visibility = GONE
            downTimeWatcher?.onLastTimeFinish(seconds)
            onFinishListener?.invoke()
        }else{
            visibility = VISIBLE
            isRunning = true
            downTime(seconds, 1, 0, 1000,openAnimation)
        }
    }

    /**
     * [Chinese text]
     *
     * @param downCount     [Chinese text]
     * @param lastDown      [Chinese text]
     * @param delayMills    [Chinese text]([Chinese text])
     * @param intervalMills [Chinese text]([Chinese text])
     */
    fun downTime(downCount: Int, lastDown: Int, delayMills: Long, intervalMills: Long,startAnimate : Boolean) {
        timer = Timer()
        this.downCount = downCount
        this.lastDown = lastDown
        this.delayMills = delayMills
        this.intervalMills = intervalMills
        if (startAnimate){
            initDefaultAnimate()
        }
        downTimerTask = DownTimerTask()
        timer?.schedule(downTimerTask, delayMills, intervalMills)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (GONE == visibility) {
            downTimerTask = null
            timer?.cancel()
            timer = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (drawTextFlag == DRAW_TEXT_NO) {
            return
        }
        super.onDraw(canvas)
    }

    /**
     * [Chinese text]
     */
    fun cancel() {
        animationSet?.cancel()
        downTimerTask?.cancel()
        timer?.cancel()
        drawTextFlag = DRAW_TEXT_NO
        invalidate() // [Chinese text]
        visibility = GONE
        downTimerTask = null
        timer = null
        isRunning = false
    }

    private inner class DownTimerTask : TimerTask() {
        override fun run() {
            if (downCount >= lastDown - 1) {
                val msg = Message.obtain()
                msg.what = 1
                downHandler!!.sendMessage(msg)
            }
        }
    }

    interface DownTimeWatcher {
        fun onTime(num: Int)
        fun onLastTime(num: Int)
        fun onLastTimeFinish(num: Int)
    }

    /**
     * [Chinese text]eventlistener.
     */
    var onTimeListener: ((time: Int) -> Unit)? = null
    /**
     * [Chinese text]eventlistener.
     */
    var onFinishListener: (() -> Unit)? = null

    var downTimeWatcher: DownTimeWatcher? = null

    /**
     * listener[Chinese text]
     * @param downTimeWatcher
     */
    fun setOnTimeDownListener(downTimeWatcher: DownTimeWatcher?) {
        this.downTimeWatcher = downTimeWatcher
    }

    private var downHandler: DownHandler? = null

    private inner class DownHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                if (downTimeWatcher != null) {
                    downTimeWatcher!!.onTime(downCount)
                }
                onTimeListener?.invoke(downCount)
//                Log.e("[Chinese text]","// handleMessage"+downCount+"// "+lastDown);
                if (downCount >= lastDown - 1) {
                    drawTextFlag = DRAW_TEXT_YES // [Chinese text]
                    // [Chinese text]
                    if (downCount >= lastDown) {
                        text = downCount.toString() + ""
                        startDefaultAnimate()
                        if (downCount == lastDown && downTimeWatcher != null) {
                            downTimeWatcher!!.onLastTime(downCount)
                        }
                    } else if (downCount == lastDown - 1) { // [Chinese text]lastDown[Chinese text]0, downCount == -1[Chinese text]. 
                        // [Chinese text], [Chinese text]setText()[Chinese text]onDraw, [Chinese text]
                        // Settings[Chinese text]
                        if (afterDownDimissFlag == AFTER_LAST_TIME_DIMISS) {
                            drawTextFlag = DRAW_TEXT_NO
                        }
                        invalidate() // [Chinese text]
                        isRunning = false
                        downTimerTask == null
                        timer?.cancel()
                        timer = null
                        if (downTimeWatcher != null) {
                            downTimeWatcher!!.onLastTimeFinish(downCount)
                        }
                        onFinishListener?.invoke()
                    }
                    downCount--
                }
                //
            }
        }
    }

    private val DRAW_TEXT_YES = 1
    private val DRAW_TEXT_NO = 0

    /**
     * [Chinese text]onDraw[Chinese text], [Chinese text]
     */
    private var drawTextFlag = DRAW_TEXT_YES
    private val AFTER_LAST_TIME_DIMISS = 1
    private val AFTER_LAST_TIME_NODIMISS = 0

    /**
     * [Chinese text]text[Chinese text]tag, [Chinese text]
     */
    private var afterDownDimissFlag = AFTER_LAST_TIME_DIMISS

    /**
     * Settings[Chinese text]text[Chinese text]
     */
    fun setAfterDownNoDimiss() {
        afterDownDimissFlag = AFTER_LAST_TIME_NODIMISS
    }

    /**
     * Settings[Chinese text]text[Chinese text]
     */
    fun setAferDownDimiss() {
        afterDownDimissFlag = AFTER_LAST_TIME_DIMISS
    }

    var startDefaultAnimFlag = true

    // [Chinese text]
    fun closeDefaultAnimate() {
        animationSet?.reset()
        startDefaultAnimFlag = false
    }

    // [Chinese text]
    private fun startDefaultAnimate() {
        if (startDefaultAnimFlag) {
            animation?.start()
        }
    }

    private fun initDefaultAnimate() {
        if (animationSet == null) {
            animationSet = AnimationSet(true)
        }
        val scaleAnimation = ScaleAnimation(
            1f,
            0.5f,
            1f,
            0.5f,
            ScaleAnimation.ABSOLUTE,
            measuredWidth / 2f,
            ScaleAnimation.ABSOLUTE,
            measuredHeight / 2f
        )
        scaleAnimation.duration = intervalMills
        val alphaAnimation = AlphaAnimation(1f, 0.3f)
        alphaAnimation.duration = intervalMills
        // [Chinese text]AlphaAnimation[Chinese text]Settings[Chinese text] AnimationSetin progress
        animationSet!!.addAnimation(scaleAnimation)
        animationSet!!.addAnimation(alphaAnimation)
        animationSet!!.interpolator = AccelerateInterpolator()
        animation = animationSet
    }
}