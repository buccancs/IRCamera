package com.topdon.lib.core.repository

// [Chinese text] TS004 [Chinese text] JSON [Chinese text]

/**
 * TS004 [Chinese text].
 * @param command [Chinese text], [Chinese text]
 * @param data [Chinese text], [Chinese text]
 * @param detail [Chinese text], [Chinese text] "ok", "error: request process error"
 * @param status [Chinese text] 0-[Chinese text] [Chinese text]
 * @param transmit_cast [Chinese text]
 */
data class TS004Response<T>(
    val command: Int,
    val data: T?,
    val detail: String?,
    val status: Int,
    val transmit_cast: Int,
) {
    /**
     * [Chinese text].
     */
    fun isSuccess(): Boolean = status == 0
}

/**
 * TS004 [Chinese text]: [Chinese text]Pseudo color[Chinese text]
 * @param enable
 * @param mode [Chinese text]Pseudo color[Chinese text]
 */
data class PseudoColorBean(
    val enable: Boolean?,
    val mode: Int?,
)

/**
 * TS004 [Chinese text]: [Chinese text]
 * @param state 0-[Chinese text], 1-[Chinese text]
 */
data class RangeBean(
    val state: Int?,
)

/**
 * TS004 [Chinese text]: [Chinese text]in progress[Chinese text]
 * @param enable true [Chinese text], false [Chinese text]
 */
data class PipBean(
    val enable: Boolean?,
)

/**
 * TS004 [Chinese text]: [Chinese text]
 * brightness: Int
 */
data class BrightnessBean(
    val brightness: Int,
)

/**
 * TS004 [Chinese text]: [Chinese text]
 * @param factor [Chinese text]
 */
data class ZoomBean(
    val factor: Int?,
)

/**
 * TS004 [Chinese text]: [Chinese text]
 * @param enable 0-[Chinese text] 1-[Chinese text]
 */
data class TISRBean(
    val enable: Int?,
)

/**
 * TS004 [Chinese text]: [Chinese text]message
 * @param firmware [Chinese text], [Chinese text]1.0
 */
data class VersionBean(
    val firmware: String?,
)

/**
 * TS004 [Chinese text]: [Chinese text]message
 * @param code [Chinese text]([Chinese text])
 * @param model [Chinese text], [Chinese text] TS004
 * @param sn sn
 * @param uuid [Chinese text]
 */
data class DeviceInfo(
    val code: String,
    val model: String,
    val sn: String,
    val uuid: String,
)

/**
 * TS004 [Chinese text]: [Chinese text]
 * @param fileCount [Chinese text]
 */
data class FileCountBean(
    val fileCount: Int,
)

/**
 * [Chinese text]message
 * @param current [Chinese text]
 * @param total [Chinese text]
 * @param filelist [Chinese text]message[Chinese text]
 */
data class FilePageBean(
    val current: Int,
    val total: Int,
    val filelist: List<FileBean>,
)

/**
 * TS004 [Chinese text]: [Chinese text]message
 * @param type 0-[Chinese text] 1-recording
 * @param duration recording[Chinese text], [Chinese text]
 * @param size [Chinese text], [Chinese text] byte
 * @param name [Chinese text], [Chinese text] 1970_01_02075103.mp4
 * @param thumb recording[Chinese text]
 * @param time [Chinese text] Unix [Chinese text], [Chinese text]
 * @param timezone [Chinese text]Settings[Chinese text]Settings[Chinese text]
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
 * TS004 [Chinese text]: [Chinese text]
 * @param status [Chinese text] 1-start 2-running 3-failed 4-success
 * @param percent [Chinese text]
 */
data class UpgradeStatus(
    val status: Int,
    val percent: Int,
)

/**
 * TS004 [Chinese text]: [Chinese text]message
 * @param total [Chinese text], [Chinese text] byte
 * @param free [Chinese text], [Chinese text] byte
 * @param system [Chinese text], [Chinese text] byte
 * @param image_size [Chinese text], [Chinese text] byte
 * @param video_size [Chinese text], [Chinese text] byte
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
 * TS004 [Chinese text]: [Chinese text]recording[Chinese text]
 * @param errCode [Chinese text],  0:[Chinese text], 1: [Chinese text], 2: [Chinese text]low
 * @param path [Chinese text]
 * @param pts [Chinese text]
 * @param status [Chinese text]
 */
data class RecordStatusBean(
    val errCode: Int,
    val path: String,
    val pts: Int,
    val status: Boolean,
)
