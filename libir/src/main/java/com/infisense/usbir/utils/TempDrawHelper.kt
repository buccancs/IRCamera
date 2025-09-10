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
 * TC007、2Dutility、utility Point/Line/Areautility View、SurfaceView utility，
 * utility draw utility，Consider in the futureutility。
 *
 * Created by LCG on 2024/12/6.
 */
class TempDrawHelper {
    companion object {
        /**
         * utility，utility，utility px.
         */
        private val POINT_SIZE: Int = SizeUtils.dp2px(16f)

        /**
         * utility、utility、utility、utility utilityHigh temperatureutilityLow temperatureutility，utility px.
         */
        private val CIRCLE_RADIUS: Int = SizeUtils.dp2px(3f)

        /**
         * utility，utility，utility，Xutility，Yutility/2，utility px.
         */
        private val TEMP_TEXT_OFFSET = SizeUtils.dp2px(6f)

        /**
         * utilitySpecifiedutility View utility，utility View utility.
         */
        fun Float.correctPoint(max: Int): Int =
            this.toInt()
                .coerceAtLeast(POINT_SIZE / 2)
                .coerceAtMost(max - POINT_SIZE / 2)

        /**
         * utility、utility、utilityHigh temperatureutility、utilityLow temperatureutility View utility，utility View utility。
         */
        fun Float.correct(max: Int): Int =
            this.toInt()
                .coerceAtLeast(CIRCLE_RADIUS)
                .coerceAtMost(max - CIRCLE_RADIUS)

        /**
         * utility View utility Rect.
         */
        fun getRect(
            width: Int,
            height: Int,
        ): Rect = Rect(CIRCLE_RADIUS, CIRCLE_RADIUS, width - CIRCLE_RADIUS, height - CIRCLE_RADIUS)
    }

    /**
     * utility、utility AB utility、Point/Line/Areautility utility，utility px.
     */
    var textSize: Int
        get() = textPaint.textSize.toInt()
        set(value) {
            textPaint.textSize = value.toFloat()
        }

    /**
     * utility、utility AB utility、Point/Line/Areautility utility.
     */
    var textColor: Int
        @ColorInt get() = textPaint.color
        set(
        @ColorInt value
        ) {
            textPaint.color = value
        }

    /**
     * utility utility、utility、utility、utility Paint，utility.
     * utility 1dp.
     */
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * utility、utility、utility、utility Low temperatureutility Paint，utility.
     */
    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * utility、utility、utility、utility High temperatureutility Paint，utility.
     */
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * High temperatureutility、Low temperatureutility、utility AB utility、Point/Line/Areautility Paint，
     * utility，utility 14sp，utility、utilitySettingsutility.
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

    // ******************************************** Draw ********************************************

    /**
     * utility (x,y) utility.
     *
     * Note，utility x、y utility，utility。
     */
    fun drawPoint(
        canvas: Canvas,
        x: Int,
        y: Int,
    ) {
        val left: Float = x - POINT_SIZE / 2f
        val top: Float = y - POINT_SIZE / 2f
        val right: Float = x + POINT_SIZE / 2f
        val bottom: Float = y + POINT_SIZE / 2f
        canvas.drawLine(left, y.toFloat(), right, y.toFloat(), linePaint) // Utility function
        canvas.drawLine(x.toFloat(), top, x.toFloat(), bottom, linePaint) // Utility function
    }

    /**
     * utility (startX, startY)、(stopX, stopY) utility.
     *
     * Note，utility utility utility，utility。
     */
    fun drawLine(
        canvas: Canvas,
        startX: Int,
        startY: Int,
        stopX: Int,
        stopY: Int,
    ) {
        canvas.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), linePaint)
    }

    /**
     * utilitySpecifiedutility.
     *
     * Note，utility utility utility，utility。
     */
    fun drawRect(
        canvas: Canvas,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val leftF: Float = left.toFloat()
        val topF: Float = top.toFloat()
        val rightF: Float = right.toFloat()
        val bottomF: Float = bottom.toFloat()
        val points =
            floatArrayOf(leftF, topF, rightF, topF, rightF, topF, rightF, bottomF, rightF, bottomF, leftF, bottomF, leftF, bottomF, leftF, topF)
        canvas.drawLines(points, linePaint)
    }

    /**
     * utility (x,y) utility。
     *
     * Note，utility x、y utility，utility。
     * @param isMax true-utilityHigh temperatureutility false-utilityLow temperatureutility
     */
    fun drawCircle(
        canvas: Canvas,
        x: Int,
        y: Int,
        isMax: Boolean,
    ) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), CIRCLE_RADIUS.toFloat(), if (isMax) redPaint else bluePaint)
    }

    /**
     * Specifiedutility (x,y) utility，utilitySpecifiedutility。
     * utility，utility、utility.
     *
     * Note，utility x、y utility，utility。
     * @param x utility View utility
     */
    fun drawTempText(
        canvas: Canvas,
        text: String,
        width: Int,
        x: Int,
        y: Int,
    ) {
        var textX: Float = (x + TEMP_TEXT_OFFSET).toFloat()
        var textY: Float = (y - TEMP_TEXT_OFFSET).toFloat()

        val textWidth: Float = textPaint.measureText(text)
        if (x > width - textWidth - TEMP_TEXT_OFFSET) { // Utility function，utility
            textX = x - TEMP_TEXT_OFFSET - textWidth
        }

        val textFontTop: Float = -textPaint.getFontMetrics().top
        if (y < textFontTop + TEMP_TEXT_OFFSET / 2) { // Utility function，utility
            textY = y + TEMP_TEXT_OFFSET / 2 + textFontTop
        }

        canvas.drawText(text, textX, textY, textPaint)
    }

    /**
     * Specifiedutility (startX, startY)、(stopX, stopY) utility，
     * utility "A"、"B" utility。
     *
     * Note，utility utility utility，utility。
     */
    fun drawTrendText(
        canvas: Canvas,
        width: Int,
        height: Int,
        startX: Int,
        startY: Int,
        stopX: Int,
        stopY: Int,
    ) {
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
     * Specifiedutility (x,y) utility，utilitySpecifiedutility。
     * utility，utility.
     *
     * Note，utility x、y utility，utility。
     * @param x utility View utility
     */
    fun drawPointName(
        canvas: Canvas,
        name: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int,
    ) {
        val textWidth: Float = textPaint.measureText(name)
        val textHeight: Float = -textPaint.getFontMetrics().top

        var textX = x - textWidth / 2
        var textY = y + POINT_SIZE / 2 + textHeight

        if (textX < 0) { // xutility
            textX = 0f
        }
        if (textX + textWidth > width) { // xutility
            textX = width - textWidth
        }
        if (textY > height) { // Utility function，utility
            textY = y - POINT_SIZE / 2 - textPaint.fontMetrics.bottom
        }
        canvas.drawText(name, textX, textY, textPaint)
    }

    /**
     * Specifiedutility utility utility，
     * utilitySpecifiedutility，utility。
     *
     * Note，utility x、y utility，utility。
     */
    fun drawPointRectName(
        canvas: Canvas,
        name: String,
        width: Int,
        height: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val fontMetrics: Paint.FontMetrics = textPaint.getFontMetrics()
        val textWidth: Float = textPaint.measureText(name)
        val textHeight: Float = -fontMetrics.top
        val centerX: Int = left + (right - left) / 2
        val centerY: Int = top + (bottom - top) / 2
        val offset: Float = (-fontMetrics.ascent + fontMetrics.descent) / 2 - fontMetrics.descent

        var textX: Float = centerX - textWidth / 2
        var textY: Float = centerY + offset

        if (textX < 0) { // xutility
            textX = 0f
        }
        if (textX + textWidth > width) { // xutility
            textX = width - textWidth
        }
        if (textY < textHeight) { // yutility
            textY = textHeight
        }
        if (textY > height) { // yutility
            textY = height.toFloat()
        }
        canvas.drawText(name, textX, textY, textPaint)
    }

    // ******************************************** Touch ********************************************
}
