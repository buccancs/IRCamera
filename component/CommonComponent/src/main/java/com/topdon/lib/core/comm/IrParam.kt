package com.topdon.lib.core.comm

/**
 * IR parameter configuration - stub implementation for compilation
 */
data class IrParam(
    var emissivity: Float = 0.95f,
    var distance: Float = 1.0f,
    var reflectedTemperature: Float = 25.0f,
    var ambientTemperature: Float = 25.0f,
    var humidity: Float = 50.0f,
    var atmosphericTransmission: Float = 1.0f
) {
    
    object ParamTemperature {
        const val TYPE_LOW = 0
        const val TYPE_HIGH = 1
        const val TYPE_AUTO = 2
    }
    
    companion object {
        // Parameter types for configuration
        const val ParamPColor = 1  // Pseudo color parameter
        const val ParamAlarm = 2   // Alarm parameter
        
        /**
         * Create default IR parameters
         */
        fun createDefault(): IrParam {
            return IrParam()
        }
        
        /**
         * Validate parameter ranges
         */
        fun isValidEmissivity(emissivity: Float): Boolean {
            return emissivity in 0.01f..1.0f
        }
        
        fun isValidDistance(distance: Float): Boolean {
            return distance > 0f && distance <= 1000f
        }
        
        fun isValidTemperature(temperature: Float): Boolean {
            return temperature >= -40f && temperature <= 200f
        }
        
        fun isValidHumidity(humidity: Float): Boolean {
            return humidity >= 0f && humidity <= 100f
        }
        
        /**
         * Apply atmospheric correction
         */
        fun calculateAtmosphericTransmission(distance: Float, humidity: Float, temperature: Float): Float {
            // Simplified atmospheric transmission calculation
            val distanceFactor = 1.0f / (1.0f + distance * 0.01f)
            val humidityFactor = 1.0f - (humidity / 100f * 0.05f)
            val temperatureFactor = 1.0f + (temperature - 25f) * 0.001f
            
            return (distanceFactor * humidityFactor * temperatureFactor).coerceIn(0.5f, 1.0f)
        }
    }
    
    /**
     * Apply parameter corrections
     */
    fun applyCorrection(rawTemperature: Float): Float {
        // Simple temperature correction using IR parameters
        val corrected = rawTemperature * emissivity + 
                       (reflectedTemperature * (1 - emissivity)) +
                       ((ambientTemperature - 25f) * 0.1f) -
                       (distance * 0.01f)
        
        return corrected
    }
    
    /**
     * Validate all parameters
     */
    fun isValid(): Boolean {
        return isValidEmissivity(emissivity) && 
               isValidDistance(distance) &&
               isValidTemperature(reflectedTemperature) &&
               isValidTemperature(ambientTemperature) &&
               isValidHumidity(humidity)
    }
}