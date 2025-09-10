package com.infisense.usbir.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.SizeUtils
import kotlin.math.max
import kotlin.math.min

    fun drawCircle(
        canvas: Canvas,
        x: Int,
        y: Int,
        isMax: Boolean,
    ) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), CIRCLE_RADIUS.toFloat(), if (isMax) redPaint else bluePaint)
    }

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
        if (x > width - textWidth - TEMP_TEXT_OFFSET) {
            textX = x - TEMP_TEXT_OFFSET - textWidth
        }

        val textFontTop: Float = -textPaint.getFontMetrics().top
        if (y < textFontTop + TEMP_TEXT_OFFSET / 2) {
            textY = y + TEMP_TEXT_OFFSET / 2 + textFontTop
        }

        canvas.drawText(text, textX, textY, textPaint)
    }

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

        if (textX < 0) { // Iterate over X axis
            textX = 0f
        }
        if (textX + textWidth > width) { // Iterate over X axis
            textX = width - textWidth
        }
        if (textY > height) {
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

        if (textX < 0) { // Iterate over X axis
            textX = 0f
        }
        if (textX + textWidth > width) { // Iterate over X axis
            textX = width - textWidth
        }
        if (textY < textHeight) { // Iterate over Y axis
            textY = textHeight
        }
        if (textY > height) { // Iterate over Y axis
            textY = height.toFloat()
        }
        canvas.drawText(name, textX, textY, textPaint)
    }

    // ******************************************** Touch ********************************************
}
