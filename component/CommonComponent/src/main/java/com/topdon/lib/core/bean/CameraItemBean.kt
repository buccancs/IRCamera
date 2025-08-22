package com.topdon.lib.core.bean

/**
 * Camera item configuration bean for thermal camera support
 */
data class CameraItemBean(
    var cameraType: Int = 0,
    var cameraName: String = "",
    var cameraModel: String = "",
    var deviceId: String = "",
    var isConnected: Boolean = false,
    var serialNumber: String = "",
    var firmwareVersion: String = "",
    var hardwareVersion: String = "",
    var maxTempRange: Float = 400f,
    var minTempRange: Float = -40f,
    var currentTemp: Float = 0f,
    var tempUnit: String = "°C",
    var imageWidth: Int = 320,
    var imageHeight: Int = 240,
    var frameRate: Int = 25,
    var isRecording: Boolean = false,
    var batteryLevel: Int = 100,
    var isCharging: Boolean = false,
    var type: Int = 0,  // Menu item type
    var time: Int = 0,  // Delay time  
    var isSel: Boolean = false  // Selection state
) {
    
    /**
     * Change delay type cycling through available delay times
     */
    fun changeDelayType() {
        when (time) {
            DELAY_TIME_0 -> time = DELAY_TIME_3
            DELAY_TIME_3 -> time = DELAY_TIME_6
            DELAY_TIME_6 -> time = DELAY_TIME_0
            else -> time = DELAY_TIME_0
        }
    }

    companion object {
        const val CAMERA_TYPE_TC001 = 1
        const val CAMERA_TYPE_TC002 = 2
        const val CAMERA_TYPE_OTHER = 99
        
        // Temperature gain types referenced in IRTool
        const val TYPE_TMP_ZD = -1  // Auto temperature mode
        const val TYPE_TMP_C = 1    // Normal temperature mode (high gain)
        const val TYPE_TMP_H = 0    // High temperature mode (low gain)
        
        // Camera menu item types
        const val TYPE_SETTING = 100  // Settings menu item
        const val TYPE_DELAY = 101    // Delay capture menu item
        const val TYPE_ZDKM = 102     // Auto shutter mode
        const val TYPE_SDKM = 103     // Manual shutter mode  
        const val TYPE_AUDIO = 104    // Audio recording mode
        
        // Delay time constants
        const val DELAY_TIME_0 = 0    // No delay
        const val DELAY_TIME_3 = 3    // 3 second delay
        const val DELAY_TIME_6 = 6    // 6 second delay
    }
}