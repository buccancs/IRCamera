package com.topdon.lib.core.utils

/**
 * Temperature utilities - stub implementation
 */
class TempUtil {
    
    companion object {
        
        /**
         * Format temperature value
         */
        fun formatTemp(temp: Float): String {
            return "${String.format("%.1f", temp)}°C"
        }
        
        /**
         * Convert temperature from Celsius to Fahrenheit
         */
        fun celsiusToFahrenheit(celsius: Float): Float {
            return celsius * 9 / 5 + 32
        }
        
        /**
         * Convert temperature from Fahrenheit to Celsius
         */
        fun fahrenheitToCelsius(fahrenheit: Float): Float {
            return (fahrenheit - 32) * 5 / 9
        }
        
        /**
         * Validate temperature range
         */
        fun isValidTemperature(temp: Float, minTemp: Float = -40f, maxTemp: Float = 100f): Boolean {
            return temp in minTemp..maxTemp
        }
        
        /**
         * Get temperature display string
         */
        fun getTemperatureDisplay(temp: Float, isCelsius: Boolean = true): String {
            return if (isCelsius) {
                "${String.format("%.1f", temp)}°C"
            } else {
                "${String.format("%.1f", celsiusToFahrenheit(temp))}°F"
            }
        }
        
        /**
         * Get line temperatures between two points
         */
        fun getLineTemps(startPoint: android.graphics.Point, endPoint: android.graphics.Point, tempArray: FloatArray, imageWidth: Int): List<Float> {
            val temps = mutableListOf<Float>()
            
            // Calculate line points
            val dx = endPoint.x - startPoint.x
            val dy = endPoint.y - startPoint.y
            val steps = kotlin.math.max(kotlin.math.abs(dx), kotlin.math.abs(dy)).coerceAtLeast(1)
            
            for (i in 0..steps) {
                val x = startPoint.x + dx * i / steps
                val y = startPoint.y + dy * i / steps
                
                // Calculate temperature array index
                val index = y * imageWidth + x
                if (index >= 0 && index < tempArray.size) {
                    temps.add(tempArray[index])
                }
            }
            
            return temps
        }
    }
}