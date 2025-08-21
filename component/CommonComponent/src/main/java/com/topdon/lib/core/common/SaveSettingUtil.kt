package com.topdon.lib.core.common

/**
 * Save setting utilities for component modules
 */
object SaveSettingUtil {
    
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
     * Reset save settings
     */
    fun reset() {
        // Reset various save settings - simplified
        isSaveSetting = false
    }
}