package com.topdon.lib.core.common

import com.topdon.libcom.AlarmConfig

/**
 * Save setting utilities for component modules
 */
object SaveSettingUtil {
    
    /**
     * Pseudo color mode setting
     */
    var pseudoColorMode: Int
        get() = SharedManager.getInt("pseudo_color_mode", 0)
        set(value) = SharedManager.setInt("pseudo_color_mode", value)
    
    /**
     * Temperature mode (high/low gain)
     */
    var temperatureMode: Int
        get() = SharedManager.getInt("temperature_mode", 0)
        set(value) = SharedManager.setInt("temperature_mode", value)
    
    /**
     * Two light alpha setting
     */
    var twoLightAlpha: Float
        get() = SharedManager.getFloat("two_light_alpha", 0.5f)
        set(value) = SharedManager.setFloat("two_light_alpha", value)
    
    /**
     * Record audio setting
     */
    var isRecordAudio: Boolean
        get() = SharedManager.getBoolean("record_audio", false)
        set(value) = SharedManager.setBoolean("record_audio", value)
    
    /**
     * Auto shutter setting
     */
    var isAutoShutter: Boolean
        get() = SharedManager.getBoolean("auto_shutter", true)
        set(value) = SharedManager.setBoolean("auto_shutter", value)
    
    /**
     * Alarm bean configuration
     */
    var alarmBean: AlarmConfig
        get() = AlarmConfig(
            enabled = SharedManager.getBoolean("alarm_enabled", false),
            soundEnabled = SharedManager.getBoolean("alarm_sound", true),
            vibrationEnabled = SharedManager.getBoolean("alarm_vibration", true),
            minTemperature = SharedManager.getFloat("alarm_min_temp", 0f),
            maxTemperature = SharedManager.getFloat("alarm_max_temp", 100f)
        )
        set(value) {
            SharedManager.setBoolean("alarm_enabled", value.enabled)
            SharedManager.setBoolean("alarm_sound", value.soundEnabled)
            SharedManager.setBoolean("alarm_vibration", value.vibrationEnabled)
            SharedManager.setFloat("alarm_min_temp", value.minTemperature)
            SharedManager.setFloat("alarm_max_temp", value.maxTemperature)
        }
    
    /**
     * Check if device is TC001Plus connected
     */
    fun isTC001PlusConnect(): Boolean {
        // Simplified check - return false for now
        return false
    }
    
    /**
     * Check if connect auto open is enabled
     */
    fun isConnectAutoOpen(): Boolean {
        return SharedManager.getBoolean("connect_auto_open", false)
    }
    
    /**
     * Set connect auto open preference
     */
    fun setConnectAutoOpen(enabled: Boolean) {
        SharedManager.setBoolean("connect_auto_open", enabled)
    }
    
    /**
     * Check if save setting is enabled
     */
    var isSaveSetting: Boolean
        get() = SharedManager.getBoolean("save_setting", false)
        set(value) = SharedManager.setBoolean("save_setting", value)
    
    /**
     * Video mode setting
     */
    var isVideoMode: Boolean
        get() = SharedManager.getBoolean("video_mode", false)
        set(value) = SharedManager.setBoolean("video_mode", value)
    
    /**
     * Two light mode setting
     */
    var isOpenTwoLight: Boolean
        get() = SharedManager.getBoolean("open_two_light", false)
        set(value) = SharedManager.setBoolean("open_two_light", value)
    
    /**
     * Reset save settings
     */
    fun reset() {
        // Reset various save settings - simplified
        isSaveSetting = false
        pseudoColorMode = 0
        temperatureMode = 0
        twoLightAlpha = 0.5f
        isRecordAudio = false
        isAutoShutter = true
        isVideoMode = false
        alarmBean = AlarmConfig()
    }
}