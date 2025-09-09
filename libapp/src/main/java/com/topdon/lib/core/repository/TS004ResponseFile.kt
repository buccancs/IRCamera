package com.topdon.lib.core.repository

// [CN_TEXT] TS004 [CN_TEXT] JSON [CN_TEXT]

/**
 * TS004 All[CN_TEXT].
 * @param command [CN_TEXT]，[CN_TEXT]
 * @param data [CN_TEXT]，[CN_TEXT]
 * @param detail [CN_TEXT]，[CN_TEXT] "ok"、"error: request process error"
 * @param status State[CN_TEXT] 0-[CN_TEXT] [CN_TEXT]
 * @param transmit_cast [CN_TEXT]
 */
data class TS004Response<T>(
    val command: Int,
    val data: T?,
    val detail: String?,
    val status: Int,
    val transmit_cast: Int,
) {
    /**
     * [CN_TEXT].
     */
    fun isSuccess(): Boolean = status == 0
}

/**
 * TS004 [CN_TEXT]：[CN_TEXT]Pseudo-color[CN_TEXT]
 * @param enable
 * @param mode CurrentPseudo-color[CN_TEXT]
 */
data class PseudoColorBean(
    val enable: Boolean?,
    val mode: Int?,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * @param state 0-[CN_TEXT]，1-[CN_TEXT]
 */
data class RangeBean(
    val state: Int?,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]Picture in picture
 * @param enable true [CN_TEXT]，false [CN_TEXT]
 */
data class PipBean(
    val enable: Boolean?,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * brightness: Int
 */
data class BrightnessBean(
    val brightness: Int,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * @param factor [CN_TEXT]
 */
data class ZoomBean(
    val factor: Int?,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]State
 * @param enable 0-[CN_TEXT] 1-[CN_TEXT]
 */
data class TISRBean(
    val enable: Int?,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * @param firmware [CN_TEXT]，[CN_TEXT]1.0
 */
data class VersionBean(
    val firmware: String?,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * @param code [CN_TEXT]（[CN_TEXT]）
 * @param model [CN_TEXT]Type[CN_TEXT]，[CN_TEXT] TS004
 * @param sn sn
 * @param uuid [CN_TEXT]
 */
data class DeviceInfo(
    val code: String,
    val model: String,
    val sn: String,
    val uuid: String,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * @param fileCount [CN_TEXT]
 */
data class FileCountBean(
    val fileCount: Int,
)

/**
 * [CN_TEXT]
 * @param current Current[CN_TEXT]
 * @param total [CN_TEXT]
 * @param filelist Current[CN_TEXT]
 */
data class FilePageBean(
    val current: Int,
    val total: Int,
    val filelist: List<FileBean>,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * @param type 0-[CN_TEXT] 1-Video
 * @param duration Video[CN_TEXT]，[CN_TEXT]
 * @param size [CN_TEXT]，[CN_TEXT] byte
 * @param name [CN_TEXT]，[CN_TEXT] 1970_01_02075103.mp4
 * @param thumb Video[CN_TEXT]
 * @param time [CN_TEXT] Unix [CN_TEXT]，[CN_TEXT]
 * @param timezone [CN_TEXT]Settings[CN_TEXT]Settings[CN_TEXT]
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
 * TS004 [CN_TEXT]：[CN_TEXT]State
 * @param status Current[CN_TEXT]State 1-start 2-running 3-failed 4-success
 * @param percent Current[CN_TEXT]
 */
data class UpgradeStatus(
    val status: Int,
    val percent: Int,
)

/**
 * TS004 [CN_TEXT]：[CN_TEXT]
 * @param total [CN_TEXT]，[CN_TEXT] byte
 * @param free [CN_TEXT]，[CN_TEXT] byte
 * @param system [CN_TEXT]，[CN_TEXT] byte
 * @param image_size [CN_TEXT]，[CN_TEXT] byte
 * @param video_size [CN_TEXT]，[CN_TEXT] byte
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
 * TS004 [CN_TEXT]：[CN_TEXT]VideoState
 * @param errCode [CN_TEXT]， 0:[CN_TEXT]，1: [CN_TEXT]，2: [CN_TEXT]
 * @param path Current[CN_TEXT]
 * @param pts Current[CN_TEXT]
 * @param status Current[CN_TEXT]
 */
data class RecordStatusBean(
    val errCode: Int,
    val path: String,
    val pts: Int,
    val status: Boolean,
)
