package com.topdon.lib.core.tools

import android.graphics.Point

/**
 * Temperature drawing helper - stub implementation
 */
class TempDrawHelper {
    
    companion object {
        
        /**
         * Correct point coordinates within bounds
         */
        fun correctPoint(point: Point, width: Int, height: Int): Point {
            return Point(
                point.x.coerceIn(0, width),
                point.y.coerceIn(0, height)
            )
        }
        
        /**
         * Draw temperature visualization
         */
        fun drawTemperature(canvas: android.graphics.Canvas, x: Float, y: Float, temp: Float) {
            // Stub implementation for temperature drawing
        }
        
        /**
         * Get temperature color based on value
         */
        fun getTempColor(temperature: Float, minTemp: Float, maxTemp: Float): Int {
            // Simple color mapping - blue for cold, red for hot
            val ratio = if (maxTemp > minTemp) {
                (temperature - minTemp) / (maxTemp - minTemp)
            } else 0.5f
            
            return when {
                ratio < 0.33f -> android.graphics.Color.BLUE
                ratio < 0.66f -> android.graphics.Color.GREEN
                else -> android.graphics.Color.RED
            }
        }
        
        /**
         * Format temperature for display
         */
        fun formatTemperature(temp: Float, unit: String = "°C"): String {
            return "${String.format("%.1f", temp)}$unit"
        }
        
        /**
         * Draw trend text (A, B markers for line endpoints)
         */
        fun drawTrendText(canvas: android.graphics.Canvas, viewWidth: Int, viewHeight: Int, 
                         startX: Float, startY: Float, endX: Float, endY: Float) {
            // Stub implementation for drawing trend text markers
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 24f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText("A", startX, startY - 10, paint)
            canvas.drawText("B", endX, endY - 10, paint)
        }
        
        /**
         * Draw point name text
         */
        fun drawPointName(canvas: android.graphics.Canvas, name: String, viewWidth: Int, viewHeight: Int, x: Float, y: Float) {
            // Stub implementation for drawing point name
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 20f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            canvas.drawText(name, x, y + 30, paint)
        }
        
        /**
         * Draw rectangle point name text
         */
        fun drawPointRectName(canvas: android.graphics.Canvas, name: String, viewWidth: Int, viewHeight: Int, 
                             left: Float, top: Float, right: Float, bottom: Float) {
            // Stub implementation for drawing rectangle point name
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 20f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val centerX = (left + right) / 2
            val centerY = (top + bottom) / 2
            canvas.drawText(name, centerX, centerY + 30, paint)
        }
        
        /**
         * Get drawable rectangle bounds based on view dimensions
         */
        fun getRect(viewWidth: Int, viewHeight: Int): android.graphics.Rect {
            // Return a rect that represents the drawable area within the view bounds
            val margin = 20 // Small margin from edges
            return android.graphics.Rect(
                margin, 
                margin, 
                viewWidth - margin, 
                viewHeight - margin
            )
        }
    }
}