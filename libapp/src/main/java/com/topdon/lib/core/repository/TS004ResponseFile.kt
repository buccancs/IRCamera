package com.topdon.lib.core.repository

//  TS004  JSON 

/**
 * TS004 .
 * @param command ，
 * @param data ，
 * @param detail ， "ok"、"error: request process error"
 * @param status  0- 
 * @param transmit_cast 
 */
data class TS004Response<T>(
    val command: Int,
    val data: T?,
    val detail: String?,
    val status: Int,
    val transmit_cast: Int,
) {
    /**
     * .
     */
    fun isSuccess(): Boolean = status == 0
}

/**
 * TS004 ：
 * @param enable
 * @param mode 
 */
data class PseudoColorBean(
    val enable: Boolean?,
    val mode: Int?,
)

/**
 * TS004 ：
 * @param state 0-，1-
 */
data class RangeBean(
    val state: Int?,
)

/**
 * TS004 ：
 * @param enable true ，false 
 */
data class PipBean(
    val enable: Boolean?,
)

/**
 * TS004 ：
 * brightness: Int
 */
data class BrightnessBean(
    val brightness: Int,
)

/**
 * TS004 ：
 * @param factor 
 */
data class ZoomBean(
    val factor: Int?,
)

/**
 * TS004 ：
 * @param enable 0- 1-
 */
data class TISRBean(
    val enable: Int?,
)

/**
 * TS004 ：
 * @param firmware ，1.0
 */
data class VersionBean(
    val firmware: String?,
)

/**
 * TS004 ：
 * @param code （）
 * @param model ， TS004
 * @param sn sn
 * @param uuid 
 */
data class DeviceInfo(
    val code: String,
    val model: String,
    val sn: String,
    val uuid: String,
)

/**
 * TS004 ：
 * @param fileCount 
 */
data class FileCountBean(
    val fileCount: Int,
)

/**
 * 
 * @param current 
 * @param total 
 * @param filelist 
 */
data class FilePageBean(
    val current: Int,
    val total: Int,
    val filelist: List<FileBean>,
)

/**
 * TS004 ：
 * @param type 0- 1-
 * @param duration ，
 * @param size ， byte
 * @param name ， 1970_01_02075103.mp4
 * @param thumb 
 * @param time  Unix ，
 * @param timezone 
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
 * TS004 ：
 * @param status  1-start 2-running 3-failed 4-success
 * @param percent 
 */
data class UpgradeStatus(
    val status: Int,
    val percent: Int,
)

/**
 * TS004 ：
 * @param total ， byte
 * @param free ， byte
 * @param system ， byte
 * @param image_size ， byte
 * @param video_size ， byte
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
 * TS004 ：
 * @param errCode ， 0:，1: ，2: 
 * @param path 
 * @param pts 
 * @param status 
 */
data class RecordStatusBean(
    val errCode: Int,
    val path: String,
    val pts: Int,
    val status: Boolean,
)
