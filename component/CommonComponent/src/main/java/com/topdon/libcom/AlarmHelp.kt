package com.topdon.libcom

/**
 * Alarm helper utility - stub implementation for compilation
 */
class AlarmHelp {
    
    companion object {
        
        /**
         * Set temperature alarm
         */
        fun setTemperatureAlarm(minTemp: Float, maxTemp: Float, enabled: Boolean = true) {
            // Stub implementation for temperature alarm setting
        }
        
        /**
         * Check if temperature is in alarm range
         */
        fun isTemperatureAlarm(temperature: Float, minTemp: Float, maxTemp: Float): Boolean {
            return temperature < minTemp || temperature > maxTemp
        }
        
        /**
         * Clear temperature alarm
         */
        fun clearTemperatureAlarm() {
            // Stub implementation
        }
        
        /**
         * Enable/disable alarm sound
         */
        fun setAlarmSoundEnabled(enabled: Boolean) {
            // Stub implementation
        }
        
        /**
         * Set alarm vibration
         */
        fun setAlarmVibrationEnabled(enabled: Boolean) {
            // Stub implementation
        }
        
        /**
         * Get alarm configuration
         */
        fun getAlarmConfig(): AlarmConfig {
            return AlarmConfig()
        }
        
        /**
         * Check alarm trigger condition
         */
        fun checkAlarmTrigger(temperature: Float): AlarmResult {
            return AlarmResult(false, "Normal", temperature)
        }
    }
}

/**
 * Alarm configuration data class
 */
data class AlarmConfig(
    var enabled: Boolean = false,
    var soundEnabled: Boolean = true,
    var vibrationEnabled: Boolean = true,
    var minTemperature: Float = 0f,
    var maxTemperature: Float = 100f
)

/**
 * Alarm result data class
 */
data class AlarmResult(
    val triggered: Boolean,
    val message: String,
    val temperature: Float
)