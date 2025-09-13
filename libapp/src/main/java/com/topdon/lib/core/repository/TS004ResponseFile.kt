package com.topdon.lib.core.repository

// Comment removed (contained Chinese characters)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class TS004Response<T>(
 val command: Int,
 val data: T?,
 val detail: String?,
 val status: Int,
 val transmit_cast: Int,
) {
 /**
 * Comment removed (contained Chinese characters)
 */
 fun isSuccess(): Boolean = status == 0
}

/**
 * Comment removed (contained Chinese characters)
 * @param enable
 * Comment removed (contained Chinese characters)
 */
data class PseudoColorBean(
 val enable: Boolean?,
 val mode: Int?,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class RangeBean(
 val state: Int?,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class PipBean(
 val enable: Boolean?,
)

/**
 * Comment removed (contained Chinese characters)
 * brightness: Int
 */
data class BrightnessBean(
 val brightness: Int,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class ZoomBean(
 val factor: Int?,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class TISRBean(
 val enable: Int?,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class VersionBean(
 val firmware: String?,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * @param sn sn
 * Comment removed (contained Chinese characters)
 */
data class DeviceInfo(
 val code: String,
 val model: String,
 val sn: String,
 val uuid: String,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class FileCountBean(
 val fileCount: Int,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class FilePageBean(
 val current: Int,
 val total: Int,
 val filelist: List<FileBean>,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class FileBean(
 val id: Int,
 val type: Int,
 val duration: Int,
 val size: Long,
 val name: String,
 val thumb: String,
 val time: Long,
 val timezone: Int,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class UpgradeStatus(
 val status: Int,
 val percent: Int,
)

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class FreeSpaceBean(
 val total: Long,
 val free: Long,
 val system: Long,
 val image_size: Long,
 val video_size: Long,
) {
 fun hasUseSize(): Long = system + image_size + video_size
}

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class RecordStatusBean(
 val errCode: Int,
 val path: String,
 val pts: Int,
 val status: Boolean,
)
