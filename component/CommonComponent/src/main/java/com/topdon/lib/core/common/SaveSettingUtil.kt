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
    var alarmBean: com.topdon.lib.core.bean.AlarmBean
        get() = com.topdon.lib.core.bean.AlarmBean(
            isLowOpen = SharedManager.getBoolean("alarm_low_open", false),
            isHighOpen = SharedManager.getBoolean("alarm_high_open", false),
            lowTemp = SharedManager.getFloat("alarm_low_temp", 0f),
            highTemp = SharedManager.getFloat("alarm_high_temp", 100f),
            isRingtoneOpen = SharedManager.getBoolean("alarm_ringtone_open", true),
            ringtoneType = SharedManager.getInt("alarm_ringtone_type", 0)
        )
        set(value) {
            SharedManager.setBoolean("alarm_low_open", value.isLowOpen)
            SharedManager.setBoolean("alarm_high_open", value.isHighOpen)
            SharedManager.setFloat("alarm_low_temp", value.lowTemp)
            SharedManager.setFloat("alarm_high_temp", value.highTemp)
            SharedManager.setBoolean("alarm_ringtone_open", value.isRingtoneOpen)
            SharedManager.setInt("alarm_ringtone_type", value.ringtoneType)
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
        alarmBean = com.topdon.lib.core.bean.AlarmBean()
    }
}