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
    }
}