package com.topdon.lib.core.tools

import java.text.DecimalFormat

/**
 * Number formatting utilities
 */
object NumberTools {
    
    fun format(value: Double, pattern: String = "#.##"): String {
        return DecimalFormat(pattern).format(value)
    }
    
    fun format(value: Float, pattern: String = "#.##"): String {
        return DecimalFormat(pattern).format(value)
    }
    
    fun formatTemperature(temp: Float): String {
        return format(temp, "#.#") + "°C"
    }
    
    fun formatTemperature(temp: Double): String {
        return format(temp, "#.#") + "°C"
    }
    
    /**
     * Format number to 2 decimal places  
     */
    fun to02f(value: Float): String {
        return format(value, "#.00")
    }
    
    fun to02f(value: Double): String {
        return format(value, "#.00")
    }
    
    /**
     * Scale a float value to a specified number of decimal places
     */
    fun scale(value: Float, decimalPlaces: Int): Float {
        val factor = Math.pow(10.0, decimalPlaces.toDouble()).toFloat()
        return kotlin.math.round(value * factor) / factor
    }
    
    /**
     * Scale a double value to a specified number of decimal places  
     */
    fun scale(value: Double, decimalPlaces: Int): Double {
        val factor = Math.pow(10.0, decimalPlaces.toDouble())
        return kotlin.math.round(value * factor) / factor
    }
}