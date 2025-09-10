package com.topdon.lib.core.repository

// data TS004 data JSON data

data class TS004Response<T>(
    val command: Int,
    val data: T?,
    val detail: String?,
    val status: Int,
    val transmit_cast: Int,
) {

data class PseudoColorBean(
    val enable: Boolean?,
    val mode: Int?,
)

data class CameraStatusBean(
    val state: Int?,
)

data class BatteryStatusBean(
    val enable: Boolean?,
)

data class ZoomFactorBean(
    val factor: Int?,
)

data class DeviceStatusBean(
    val enable: Int?,
)

data class FirmwareBean(
    val firmware: String?,
)

    val code: String,
    val model: String,
    val sn: String,
    val uuid: String,
)

data class FileCountBean(
    val fileCount: Int,
)

data class FileListBean(
    val current: Int,
    val total: Int,
    val filelist: List<FileBean>,
)

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

data class DownloadStatusBean(
    val status: Int,
    val percent: Int,
)

data class StorageBean(
    val total: Long,
    val free: Long,
    val system: Long,
    val image_size: Long,
    val video_size: Long,
) {
    fun hasUseSize(): Long = system + image_size + video_size
}

data class ResponseStatusBean(
    val errCode: Int,
    val path: String,
    val pts: Int,
    val status: Boolean,
)
