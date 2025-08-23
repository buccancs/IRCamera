package com.topdon.lib.core.bean

/**
 * Alarm configuration bean - required for thermal IR functionality
 */
data class AlarmBean(
    var isLowOpen: Boolean = false,
    var isHighOpen: Boolean = false,
    var lowTemp: Float = 0f,
    var highTemp: Float = 100f,
    var isRingtoneOpen: Boolean = true,
    var ringtoneType: Int = 0
) {
    
    /**
     * Check if any alarm is enabled
     */
    fun isAlarmEnabled(): Boolean {
        return isLowOpen || isHighOpen
    }
    
    /**
     * Check if temperature is in alarm range
     */
    fun isTemperatureInAlarmRange(temperature: Float): Boolean {
        if (isLowOpen && temperature < lowTemp) {
            return true
        }
        if (isHighOpen && temperature > highTemp) {
            return true
        }
        return false
    }
    
    /**
     * Get alarm status text
     */
    fun getAlarmStatusText(): String {
        return when {
            isLowOpen && isHighOpen -> "Low: ${lowTemp}°C, High: ${highTemp}°C"
            isLowOpen -> "Low: ${lowTemp}°C"
            isHighOpen -> "High: ${highTemp}°C"
            else -> "No alarm set"
        }
    }
}