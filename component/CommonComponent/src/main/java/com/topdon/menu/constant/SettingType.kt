package com.topdon.menu.constant

/**
 * Setting type enumeration - stub implementation for compilation
 */
enum class SettingType(val value: Int, val displayName: String) {
    
    TEMPERATURE_UNIT(1, "Temperature Unit"),
    EMISSIVITY(2, "Emissivity"),
    DISTANCE(3, "Distance"),
    HUMIDITY(4, "Humidity"),
    AMBIENT_TEMPERATURE(5, "Ambient Temperature"),
    REFLECTED_TEMPERATURE(6, "Reflected Temperature"),
    PALETTE(7, "Color Palette"),
    BRIGHTNESS(8, "Brightness"),
    CONTRAST(9, "Contrast"),
    AUTO_RANGE(10, "Auto Range"),
    MANUAL_RANGE(11, "Manual Range"),
    IMAGE_FORMAT(12, "Image Format"),
    VIDEO_QUALITY(13, "Video Quality"),
    LANGUAGE(14, "Language"),
    TIME_FORMAT(15, "Time Format"),
    AUTO_SAVE(16, "Auto Save"),
    SOUND_ENABLED(17, "Sound"),
    VIBRATION_ENABLED(18, "Vibration");
    
    companion object {
        fun fromValue(value: Int): SettingType {
            return values().find { it.value == value } ?: TEMPERATURE_UNIT
        }
        
        fun getDisplayNames(): Array<String> {
            return values().map { it.displayName }.toTypedArray()
        }
        
        fun getTemperatureSettings(): Array<SettingType> {
            return arrayOf(
                TEMPERATURE_UNIT, 
                EMISSIVITY, 
                DISTANCE, 
                HUMIDITY, 
                AMBIENT_TEMPERATURE, 
                REFLECTED_TEMPERATURE
            )
        }
        
        fun getImageSettings(): Array<SettingType> {
            return arrayOf(PALETTE, BRIGHTNESS, CONTRAST, AUTO_RANGE, MANUAL_RANGE)
        }
        
        fun getGeneralSettings(): Array<SettingType> {
            return arrayOf(
                IMAGE_FORMAT, 
                VIDEO_QUALITY, 
                LANGUAGE, 
                TIME_FORMAT, 
                AUTO_SAVE, 
                SOUND_ENABLED, 
                VIBRATION_ENABLED
            )
        }
    }
    
    /**
     * Check if setting type is related to temperature
     */
    fun isTemperatureSetting(): Boolean {
        return this in getTemperatureSettings()
    }
    
    /**
     * Check if setting type is related to image processing
     */
    fun isImageSetting(): Boolean {
        return this in getImageSettings()
    }
    
    /**
     * Get default value for setting type
     */
    fun getDefaultValue(): Any {
        return when (this) {
            TEMPERATURE_UNIT -> "°C"
            EMISSIVITY -> 0.95f
            DISTANCE -> 1.0f
            HUMIDITY -> 50f
            AMBIENT_TEMPERATURE -> 25f
            REFLECTED_TEMPERATURE -> 25f
            BRIGHTNESS -> 50
            CONTRAST -> 50
            AUTO_RANGE -> true
            IMAGE_FORMAT -> "JPEG"
            VIDEO_QUALITY -> "High"
            LANGUAGE -> "English"
            TIME_FORMAT -> "24h"
            AUTO_SAVE -> false
            SOUND_ENABLED -> true
            VIBRATION_ENABLED -> true
            else -> ""
        }
    }
}