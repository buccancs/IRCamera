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
        
        /**
         * Get line temperatures between two points (ByteArray version)
         * Converts ByteArray thermal data to temperature values
         */
        fun getLineTemps(startPoint: android.graphics.Point, endPoint: android.graphics.Point, tempArray: ByteArray, imageWidth: Int): List<Float> {
            val temps = mutableListOf<Float>()
            
            // Calculate line points
            val dx = endPoint.x - startPoint.x
            val dy = endPoint.y - startPoint.y
            val steps = kotlin.math.max(kotlin.math.abs(dx), kotlin.math.abs(dy)).coerceAtLeast(1)
            
            for (i in 0..steps) {
                val x = startPoint.x + dx * i / steps
                val y = startPoint.y + dy * i / steps
                
                // Calculate temperature array index (2 bytes per pixel for thermal data)
                val index = (y * imageWidth + x) * 2
                if (index >= 0 && index + 1 < tempArray.size) {
                    // Convert 2 bytes to temperature value
                    val lowByte = tempArray[index].toInt() and 0xFF
                    val highByte = tempArray[index + 1].toInt() and 0xFF
                    val rawValue = (highByte shl 8) or lowByte
                    
                    // Convert raw value to temperature (simplified conversion)
                    val temperature = (rawValue / 100.0f) - 273.15f  // Convert from Kelvin to Celsius
                    temps.add(temperature)
                }
            }
            
            return temps
        }
    }
}