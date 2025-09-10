package com.topdon.module.thermal.ir.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.energy.iruvc.sdkisp.LibIRTemp
import com.energy.iruvc.utils.Line
import com.infisense.usbir.utils.TempDrawHelper.Companion.correct
import com.infisense.usbir.view.ITsTempListener
import java.lang.ref.WeakReference

    private fun drawOnePoint(
        canvas: Canvas,
        point: Point,
        index: Int,
    ): LibIRTemp.TemperatureSampleResult? {
        val result =
            try {
                irtemp.getTemperatureOfPoint(Point((point.x / xScale).toInt(), (point.y / yScale).toInt()))
            } catch (_: IllegalArgumentException) {
                // View rendering View view xScale、yScale view，viewPoint/Line/Areaview
                // View rendering view scale view，view，view
                return null
            }
        drawPoint(canvas, point)
        drawCircle(canvas, point.x, point.y, true)
        drawTempText(canvas, point.x, point.y, getTSTemp(result.maxTemperature))
        if (isShowName) {
            drawPointName(canvas, "P${index + 1}", point)
        }
        return result
    }

    private fun drawOneLine(
        canvas: Canvas,
        line: Line,
        index: Int,
    ): LibIRTemp.TemperatureSampleResult? {
        drawLine(canvas, line)

        val tempStartX: Int = (line.start.x / xScale).toInt()
        val tempStartY: Int = (line.start.y / yScale).toInt()
        val tempStopX: Int = (line.end.x / xScale).toInt()
        val tempStopY: Int = (line.end.y / yScale).toInt()
        if (tempStartX == tempStopX && tempStartY == tempStopY) {
            return null
        }

        val result =
            try {
                irtemp.getTemperatureOfLine(Line(Point(tempStartX, tempStartY), Point(tempStopX, tempStopY)))
            } catch (_: IllegalArgumentException) {
                // View rendering View view xScale、yScale view，viewPoint/Line/Areaview
                // View rendering view scale view，view，view
                return null
            }
        val maxX: Int = (result.maxTemperaturePixel.x * xScale).correct(width)
        val maxY: Int = (result.maxTemperaturePixel.y * yScale).correct(height)
        val minX: Int = (result.minTemperaturePixel.x * xScale).correct(width)
        val minY: Int = (result.minTemperaturePixel.y * yScale).correct(height)
        drawCircle(canvas, maxX, maxY, true)
        drawCircle(canvas, minX, minY, false)
        drawTempText(canvas, maxX, maxY, getTSTemp(result.maxTemperature))
        drawTempText(canvas, minX, minY, getTSTemp(result.minTemperature))

        if (isShowName) {
            drawLineName(canvas, "L${index + 1}", line)
        }
        return result
    }

    private fun drawOneRect(
        canvas: Canvas,
        rect: Rect,
        index: Int,
    ): LibIRTemp.TemperatureSampleResult? {
        drawRect(canvas, rect)

        // rect view touch view，left < right, top < bottom
        val left = (rect.left / xScale).toInt()
        val top = (rect.top / yScale).toInt()
        val right = (rect.right / xScale).toInt()
        val bottom = (rect.bottom / yScale).toInt()
        if (left == right || top == bottom) {
            return null
        }
        val result =
            try {
                irtemp.getTemperatureOfRect(Rect(left, top, right, bottom))
            } catch (_: IllegalArgumentException) {
                // View rendering View view xScale、yScale view，viewPoint/Line/Areaview
                // View rendering view scale view，view，view
                return null
            }
        val maxX: Int = (result.maxTemperaturePixel.x * xScale).correct(width)
        val maxY: Int = (result.maxTemperaturePixel.y * yScale).correct(height)
        val minX: Int = (result.minTemperaturePixel.x * xScale).correct(width)
        val minY: Int = (result.minTemperaturePixel.y * yScale).correct(height)
        drawCircle(canvas, maxX, maxY, true)
        drawCircle(canvas, minX, minY, false)
        drawTempText(canvas, maxX, maxY, getTSTemp(result.maxTemperature))
        drawTempText(canvas, minX, minY, getTSTemp(result.minTemperature))

        if (isShowName) {
            drawRectName(canvas, "R${index + 1}", rect)
        }
        return result
    }
}
