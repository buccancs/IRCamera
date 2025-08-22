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
    var isCharging: Boolean = false
) {
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
        
        // Delay time constants
        const val DELAY_TIME_0 = 0    // No delay
        const val DELAY_TIME_3 = 3    // 3 second delay
        const val DELAY_TIME_6 = 6    // 6 second delay
    }
}