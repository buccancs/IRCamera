package com.topdon.lib.core.tools

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

/**
 * Temperature drawing helper for thermal IR components
 */
class TempDrawHelper {
    
    var textSize: Int = 14
    var textColor: Int = android.graphics.Color.WHITE
    
    fun drawPoint(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        canvas.drawCircle(x, y, 5f, paint)
    }
    
    fun drawLine(canvas: Canvas, startX: Float, startY: Float, stopX: Float, stopY: Float, paint: Paint) {
        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }
    
    fun drawRect(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        canvas.drawRect(left, top, right, bottom, paint)
    }
    
    fun drawCircle(canvas: Canvas, cx: Float, cy: Float, radius: Float, paint: Paint) {
        canvas.drawCircle(cx, cy, radius, paint)
    }
    
    fun drawTempText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint, isShowC: Boolean = true) {
        val displayText = if (isShowC) "$text°C" else "$text°F"
        canvas.drawText(displayText, x, y, paint)
    }
    
    fun drawTrendText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        canvas.drawText(text, x, y, paint)
    }
    
    fun drawPointName(canvas: Canvas, name: String, x: Float, y: Float, paint: Paint) {
        canvas.drawText(name, x, y, paint)
    }
    
    fun drawPointRectName(canvas: Canvas, name: String, x: Float, y: Float, paint: Paint) {
        canvas.drawText(name, x, y, paint)
    }
    
    companion object {
        fun getRect(width: Int, height: Int): Rect {
            return Rect(0, 0, width, height)
        }
    }
}