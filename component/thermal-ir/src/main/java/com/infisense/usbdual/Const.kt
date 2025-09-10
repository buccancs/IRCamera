package com.infisense.usbdual

/**
 * Temporary Const object to fix compilation issues.
 * This should be replaced with the proper external library dependency.
 */
object Const {
    const val TYPE_IR_DUAL = 1
    const val TYPE_IR_SINGLE = 0
    const val TYPE_VISIBLE = 2
    
    // IR Camera dimensions
    const val IR_WIDTH = 256
    const val IR_HEIGHT = 192
    
    // Sensor dimensions  
    const val SENSOR_WIDTH = 256
    const val SENSOR_HEIGHT = 192
    
    // Handler message constants
    const val RESTART_USB = 1001
    const val HANDLE_CONNECT = 1002
    const val HANDLE_REGISTER = 1003
    const val SHOW_LOADING = 1004
    const val HIDE_LOADING = 1005
    const val SHOW_RESTART_MESSAGE = 1006
    
    // Device connection check
    fun isDeviceConnected(): Boolean {
        // Temporary implementation
        return true
    }
}