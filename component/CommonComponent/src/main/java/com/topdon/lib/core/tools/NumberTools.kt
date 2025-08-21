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
}