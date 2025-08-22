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
    }
}