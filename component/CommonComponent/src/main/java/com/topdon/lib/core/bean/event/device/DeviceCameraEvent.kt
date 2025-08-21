package com.topdon.lib.core.bean.event.device

/**
 * Device camera event for thermal camera state changes
 */
data class DeviceCameraEvent(
    val isConnected: Boolean,
    val deviceType: String = "TC001"
)