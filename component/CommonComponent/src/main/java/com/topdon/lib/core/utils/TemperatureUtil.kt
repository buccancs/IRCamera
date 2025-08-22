package com.topdon.lib.core.utils

import kotlin.math.*

/**
 * Temperature utility class - stub implementation for compilation
 */
class TemperatureUtil {
    
    companion object {
        
        /**
         * Convert Celsius to Fahrenheit
         */
        fun celsiusToFahrenheit(celsius: Float): Float {
            return celsius * 9 / 5 + 32
        }
        
        /**
         * Convert Fahrenheit to Celsius  
         */
        fun fahrenheitToCelsius(fahrenheit: Float): Float {
            return (fahrenheit - 32) * 5 / 9
        }
        
        /**
         * Convert Celsius to Kelvin
         */
        fun celsiusToKelvin(celsius: Float): Float {
            return celsius + 273.15f
        }
        
        /**
         * Convert Kelvin to Celsius
         */
        fun kelvinToCelsius(kelvin: Float): Float {
            return kelvin - 273.15f
        }
        
        /**
         * Format temperature with unit
         */
        fun formatTemperature(temperature: Float, unit: String = "°C", precision: Int = 1): String {
            return String.format("%.${precision}f%s", temperature, unit)
        }
        
        /**
         * Get temperature unit symbol
         */
        fun getTemperatureUnit(type: String): String {
            return when (type.uppercase()) {
                "CELSIUS", "C" -> "°C"
                "FAHRENHEIT", "F" -> "°F" 
                "KELVIN", "K" -> "K"
                else -> "°C"
            }
        }
        
        /**
         * Validate temperature range
         */
        fun isValidTemperature(temperature: Float, unit: String = "C"): Boolean {
            return when (unit.uppercase()) {
                "C" -> temperature >= -273.15f && temperature <= 1000f
                "F" -> temperature >= -459.67f && temperature <= 1832f
                "K" -> temperature >= 0f && temperature <= 1273.15f
                else -> true
            }
        }
        
        /**
         * Calculate temperature difference
         */
        fun getTemperatureDifference(temp1: Float, temp2: Float): Float {
            return abs(temp1 - temp2)
        }
        
        /**
         * Round temperature to precision
         */
        fun roundTemperature(temperature: Float, precision: Int = 1): Float {
            val multiplier = 10.0f.pow(precision)
            return round(temperature * multiplier) / multiplier
        }
    }
}