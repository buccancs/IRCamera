package com.topdon.lib.core.repository

/**
 * Data classes for device-specific functionality
 * These are stub implementations since device support was removed
 */

data class FileBean(
    /** name property */
    val name: String = "",
    /** id property */
    val id: Int = 0, // Changed from String to Int
    /** path property */
    val path: String = "",
    /** thumb property */
    val thumb: String = "",
    /** duration property */
    val duration: Long = 0L,
    /** time property */
    val time: Long = 0L
)

data class DeviceInfo(
    /** sn property */
    val sn: String = "",
    /** model property */
    val model: String = "",
    /** code property */
    val code: String = ""
)

data class ProductBean(
    /** ProductSN property */
    val ProductSN: String = "",
    /** ProductName property */
    val ProductName: String = "",
    /** Code property */
    val Code: String = ""
) {
    /**
     * Function description.
     */
    fun getVersionStr(): String = "N/A"
}

data class BatteryInfo(
    /** level property */
    val level: Int = 0,
    /** isCharging property */
    val isCharging: Boolean = false
) {
    /**
     * Function description.
     */
    fun getBattery(): Int = level
}

data class FreeSpaceBean(
    /** total property */
    val total: Long = 0L,
    /** image_size property */
    val image_size: Long = 0L,
    /** video_size property */
    val video_size: Long = 0L,
    /** system property */
    val system: Long = 0L
) {
    /**
     * Function description.
     */
    fun hasUseSize(): Long = image_size + video_size + system
}