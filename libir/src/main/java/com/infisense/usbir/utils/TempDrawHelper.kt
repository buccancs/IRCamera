package com.infisense.usbir.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.SizeUtils
import kotlin.math.max
import kotlin.math.min

/**
 * TC007, 2D[Chinese text], [Chinese text] Point/Line/Areatemperature[Chinese text] View, SurfaceView [Chinese text]implement, 
 * [Chinese text] draw [Chinese text], [Chinese text]. 
 *
 * Created by LCG on 2024/12/6.
 */
class TempDrawHelper {
    companion object {
        /**
         * point[Chinese text], [Chinese text], [Chinese text] px.
         */
        private val POINT_SIZE: Int = SizeUtils.dp2px(16f)
        /**
         * point, line, [Chinese text], [Chinese text] [Chinese text]high[Chinese text]low[Chinese text]in progress[Chinese text], [Chinese text] px.
         */
        private val CIRCLE_RADIUS: Int = SizeUtils.dp2px(3f)
        /**
         * temperature[Chinese text]text, [Chinese text], [Chinese text]text[Chinese text], X[Chinese text], Y[Chinese text]/2, [Chinese text] px.
         */
        private val TEMP_TEXT_OFFSET = SizeUtils.dp2px(6f)

        /**
         * [Chinese text] View [Chinese text], [Chinese text] View [Chinese text].
         */
        fun Float.correctPoint(max: Int): Int = this.toInt()
            .coerceAtLeast(POINT_SIZE / 2)
            .coerceAtMost(max - POINT_SIZE / 2)

        /**
         * [Chinese text]line, [Chinese text], [Chinese text]high[Chinese text]point, [Chinese text]low[Chinese text]point[Chinese text] View [Chinese text], [Chinese text] View [Chinese text]. 
         */
        fun Float.correct(max: Int): Int = this.toInt()
            .coerceAtLeast(CIRCLE_RADIUS)
            .coerceAtMost(max - CIRCLE_RADIUS)

        /**
         * [Chinese text] View [Chinese text] Rect.
         */
        fun getRect(width: Int, height: Int): Rect = Rect(CIRCLE_RADIUS, CIRCLE_RADIUS, width - CIRCLE_RADIUS, height - CIRCLE_RADIUS)
    }

    /**
     * temperature[Chinese text]text, [Chinese text] AB [Chinese text], Point/Line/Area[Chinese text] text[Chinese text], [Chinese text] px.
     */
    var textSize: Int
        get() = textPaint.textSize.toInt()
        set(value) {
            textPaint.textSize = value.toFloat()
        }

    /**
     * temperature[Chinese text]text, [Chinese text] AB [Chinese text], Point/Line/Area[Chinese text] text[Chinese text].
     */
    var textColor: Int
        @ColorInt get() = textPaint.color
        set(@ColorInt value) {
            textPaint.color = value
        }

    /**
     * [Chinese text] point, line, [Chinese text], [Chinese text]line Paint, [Chinese text].
     * [Chinese text] 1dp.
     */
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * point, line, [Chinese text], [Chinese text] low[Chinese text]point[Chinese text] Paint, [Chinese text].
     */
    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * point, line, [Chinese text], [Chinese text] high[Chinese text]point[Chinese text] Paint, [Chinese text].
     */
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * high[Chinese text]temperaturetext, low[Chinese text]temperaturetext, [Chinese text] AB [Chinese text], Point/Line/Area[Chinese text] Paint, 
     * [Chinese text], [Chinese text] 14sp, [Chinese text]text[Chinese text], [Chinese text]Settings[Chinese text].
     */
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        linePaint.strokeWidth = SizeUtils.dp2px(1f).toFloat()
        linePaint.color = Color.WHITE

        bluePaint.color = Color.BLUE

        redPaint.color = Color.RED

        textPaint.textSize = SizeUtils.sp2px(14f).toFloat()
        textPaint.color = Color.WHITE

    }

    /* ******************************************** Draw ******************************************** */
    /**
     * [Chinese text] (x,y) [Chinese text].
     *
     * [Chinese text], [Chinese text] x, y [Chinese text], [Chinese text]. 
     */
    fun drawPoint(canvas: Canvas, x: Int, y: Int) {
        val left: Float = x - POINT_SIZE / 2f
        val top: Float = y - POINT_SIZE / 2f
        val right: Float = x + POINT_SIZE / 2f
        val bottom: Float = y + POINT_SIZE / 2f
        canvas.drawLine(left, y.toFloat(), right, y.toFloat(), linePaint) // [Chinese text]line
        canvas.drawLine(x.toFloat(), top, x.toFloat(), bottom, linePaint) // [Chinese text]line
    }

    /**
     * [Chinese text] (startX, startY), (stopX, stopY) [Chinese text]point[Chinese text]line[Chinese text].
     *
     * [Chinese text], [Chinese text] [Chinese text] [Chinese text], [Chinese text]. 
     */
    fun drawLine(canvas: Canvas, startX: Int, startY: Int, stopX: Int, stopY: Int) {
        canvas.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), linePaint)
    }

    /**
     * [Chinese text]range[Chinese text].
     *
     * [Chinese text], [Chinese text] [Chinese text] [Chinese text], [Chinese text]. 
     */
    fun drawRect(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        val leftF: Float = left.toFloat()
        val topF: Float = top.toFloat()
        val rightF: Float = right.toFloat()
        val bottomF: Float = bottom.toFloat()
        val points = floatArrayOf(leftF, topF, rightF, topF, rightF, topF, rightF, bottomF, rightF, bottomF, leftF, bottomF, leftF, bottomF, leftF, topF)
        canvas.drawLines(points, linePaint)
    }

    /**
     * [Chinese text] (x,y) [Chinese text]. 
     *
     * [Chinese text], [Chinese text] x, y [Chinese text], [Chinese text]. 
     * @param isMax true-[Chinese text]high[Chinese text] false-[Chinese text]low[Chinese text]
     */
    fun drawCircle(canvas: Canvas, x: Int, y: Int, isMax: Boolean) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), CIRCLE_RADIUS.toFloat(), if (isMax) redPaint else bluePaint)
    }

    /**
     * [Chinese text] (x,y) [Chinese text], [Chinese text]text. 
     * [Chinese text], [Chinese text], [Chinese text].
     *
     * [Chinese text], [Chinese text] x, y [Chinese text], [Chinese text]. 
     * @param x [Chinese text] View [Chinese text]
     */
    fun drawTempText(canvas: Canvas, text: String, width: Int, x: Int, y: Int) {
        var textX: Float = (x + TEMP_TEXT_OFFSET).toFloat()
        var textY: Float = (y - TEMP_TEXT_OFFSET).toFloat()

        val textWidth: Float = textPaint.measureText(text)
        if (x > width - textWidth - TEMP_TEXT_OFFSET) {// [Chinese text], [Chinese text]
            textX = x - TEMP_TEXT_OFFSET - textWidth
        }

        val textFontTop: Float = -textPaint.getFontMetrics().top
        if (y < textFontTop + TEMP_TEXT_OFFSET / 2) {// [Chinese text], [Chinese text]
            textY = y + TEMP_TEXT_OFFSET / 2 + textFontTop
        }

        canvas.drawText(text, textX, textY, textPaint)
    }

    /**
     * [Chinese text] (startX, startY), (stopX, stopY) [Chinese text]line[Chinese text], 
     * [Chinese text]line[Chinese text] "A", "B" text. 
     *
     * [Chinese text], [Chinese text] [Chinese text] [Chinese text], [Chinese text]. 
     */
    fun drawTrendText(canvas: Canvas, width: Int, height: Int, startX: Int, startY: Int, stopX: Int, stopY: Int) {
        val fontMetrics: Paint.FontMetrics = textPaint.getFontMetrics()
        val textWidth: Float = textPaint.measureText("A")
        val textHeight: Float = -fontMetrics.top

        val minX: Int = min(startX, stopX)
        val maxX: Int = max(startX, stopX)
        val leftX: Float = (minX - textWidth).coerceAtLeast(0f)
        val rightX: Float = maxX.toFloat().coerceAtMost(width - textWidth)

        val minY: Int = min(startY, stopY)
        val maxY: Int = max(startY, stopY)
        val topY: Float = (minY - (-fontMetrics.top + fontMetrics.ascent)).coerceAtLeast(textHeight)
        val bottomY: Float = (maxY + textHeight).coerceAtMost(height.toFloat())

        val k: Float = (startY - stopY).toFloat() / (startX - stopX)
        canvas.drawText("A", leftX, if (k >= 0) topY else bottomY, textPaint)
        canvas.drawText("B", rightX, if (k >= 0) bottomY else topY, textPaint)
    }

    /**
     * [Chinese text] (x,y) [Chinese text], [Chinese text]point[Chinese text]text. 
     * [Chinese text], [Chinese text].
     *
     * [Chinese text], [Chinese text] x, y [Chinese text], [Chinese text]. 
     * @param x [Chinese text] View [Chinese text]
     */
    fun drawPointName(canvas: Canvas, name: String, width: Int, height: Int, x: Int, y: Int) {
        val textWidth: Float = textPaint.measureText(name)
        val textHeight: Float = -textPaint.getFontMetrics().top

        var textX = x - textWidth / 2
        var textY = y + POINT_SIZE / 2 + textHeight

        if (textX < 0) {// x[Chinese text]
            textX = 0f
        }
        if (textX + textWidth > width) {// x[Chinese text]
            textX = width - textWidth
        }
        if (textY > height) {// [Chinese text]point[Chinese text]range[Chinese text], [Chinese text]point[Chinese text]
            textY = y - POINT_SIZE / 2 - textPaint.fontMetrics.bottom
        }
        canvas.drawText(name, textX, textY, textPaint)
    }

    /**
     * [Chinese text] line[Chinese text] [Chinese text]range, 
     * [Chinese text]range[Chinese text]line[Chinese text]text, [Chinese text]rangein progress[Chinese text]. 
     *
     * [Chinese text], [Chinese text] x, y [Chinese text], [Chinese text]. 
     */
    fun drawPointRectName(canvas: Canvas, name: String, width: Int, height: Int, left: Int, top: Int, right: Int, bottom: Int) {
        val fontMetrics: Paint.FontMetrics = textPaint.getFontMetrics()
        val textWidth: Float = textPaint.measureText(name)
        val textHeight: Float = -fontMetrics.top
        val centerX: Int = left + (right - left) / 2
        val centerY: Int = top + (bottom - top) / 2
        val offset: Float = (-fontMetrics.ascent + fontMetrics.descent) / 2 - fontMetrics.descent

        var textX: Float = centerX - textWidth / 2
        var textY: Float = centerY + offset

        if (textX < 0) {// x[Chinese text]
            textX = 0f
        }
        if (textX + textWidth > width) {// x[Chinese text]
            textX = width - textWidth
        }
        if (textY < textHeight) {// y[Chinese text]
            textY = textHeight
        }
        if (textY > height) {// y[Chinese text]
            textY = height.toFloat()
        }
        canvas.drawText(name, textX, textY, textPaint)
    }

    /* ******************************************** Touch ******************************************** */
}