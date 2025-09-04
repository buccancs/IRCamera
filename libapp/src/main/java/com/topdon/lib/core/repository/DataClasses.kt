package com.topdon.lib.core.repository

/**
 * Data classes for device-specific functionality
 * These are stub implementations since device support was removed
 */

data class FileBean(
    val name: String = "",
    val id: Int = 0, // Changed from String to Int
    val path: String = "",
    val thumb: String = "",
    val duration: Long = 0L,
    val time: Long = 0L
)

data class DeviceInfo(
    val sn: String = "",
    val model: String = "",
    val code: String = ""
)

data class ProductBean(
    val ProductSN: String = "",
    val ProductName: String = "",
    val Code: String = ""
) {
    fun getVersionStr(): String = "N/A"
}

data class BatteryInfo(
    val level: Int = 0,
    val isCharging: Boolean = false
) {
    fun getBattery(): Int = level
}

data class FreeSpaceBean(
    val total: Long = 0L,
    val image_size: Long = 0L,
    val video_size: Long = 0L,
    val system: Long = 0L
) {
    fun hasUseSize(): Long = image_size + video_size + system
}