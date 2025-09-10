package com.topdon.lib.core.repository

// data TS004 data JSON data

/**
 * TS004 Alldata.
 * @param command data，data
 * @param data data，data
 * @param detail data，data "ok"、"error: request process error"
 * @param status Statedata 0-data data
 * @param transmit_cast data
 */
data class TS004Response<T>(
    val command: Int,
    val data: T?,
    val detail: String?,
    val status: Int,
    val transmit_cast: Int,
) {
    /**
     * data.
     */
    fun isSuccess(): Boolean = status == 0
}

/**
 * TS004 data：dataPseudo-colordata
 * @param enable
 * @param mode CurrentPseudo-colordata
 */
data class PseudoColorBean(
    val enable: Boolean?,
    val mode: Int?,
)

/**
 * TS004 data：data
 * @param state 0-data，1-data
 */
data class RangeBean(
    val state: Int?,
)

/**
 * TS004 data：dataPicture in picture
 * @param enable true data，false data
 */
data class PipBean(
    val enable: Boolean?,
)

/**
 * TS004 data：data
 * brightness: Int
 */
data class BrightnessBean(
    val brightness: Int,
)

/**
 * TS004 data：data
 * @param factor data
 */
data class ZoomBean(
    val factor: Int?,
)

/**
 * TS004 data：dataState
 * @param enable 0-data 1-data
 */
data class TISRBean(
    val enable: Int?,
)

/**
 * TS004 data：data
 * @param firmware data，data1.0
 */
data class VersionBean(
    val firmware: String?,
)

/**
 * TS004 data：data
 * @param code data（data）
 * @param model dataTypedata，data TS004
 * @param sn sn
 * @param uuid data
 */
data class DeviceInfo(
    val code: String,
    val model: String,
    val sn: String,
    val uuid: String,
)

/**
 * TS004 data：data
 * @param fileCount data
 */
data class FileCountBean(
    val fileCount: Int,
)

/**
 * data
 * @param current Currentdata
 * @param total data
 * @param filelist Currentdata
 */
data class FilePageBean(
    val current: Int,
    val total: Int,
    val filelist: List<FileBean>,
)

/**
 * TS004 data：data
 * @param type 0-data 1-Video
 * @param duration Videodata，data
 * @param size data，data byte
 * @param name data，data 1970_01_02075103.mp4
 * @param thumb Videodata
 * @param time data Unix data，data
 * @param timezone dataSettingsdataSettingsdata
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
 * TS004 data：dataState
 * @param status CurrentdataState 1-start 2-running 3-failed 4-success
 * @param percent Currentdata
 */
data class UpgradeStatus(
    val status: Int,
    val percent: Int,
)

/**
 * TS004 data：data
 * @param total data，data byte
 * @param free data，data byte
 * @param system data，data byte
 * @param image_size data，data byte
 * @param video_size data，data byte
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
 * TS004 data：dataVideoState
 * @param errCode data， 0:data，1: data，2: data
 * @param path Currentdata
 * @param pts Currentdata
 * @param status Currentdata
 */
data class RecordStatusBean(
    val errCode: Int,
    val path: String,
    val pts: Int,
    val status: Boolean,
)
