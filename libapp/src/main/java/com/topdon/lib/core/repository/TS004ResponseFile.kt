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

    val state: Int?,
)

    val enable: Boolean?,
)

    val factor: Int?,
)

    val enable: Int?,
)

    val firmware: String?,
)

    val code: String,
    val model: String,
    val sn: String,
    val uuid: String,
)

    val fileCount: Int,
)

    val current: Int,
    val total: Int,
    val filelist: List<FileBean>,
)

    val id: Int,
    val type: Int,
    val duration: Int,
    val size: Long,
    val name: String,
    val thumb: String,
    val time: Long,
    val timezone: Int,
)

    val status: Int,
    val percent: Int,
)

    val total: Long,
    val free: Long,
    val system: Long,
    val image_size: Long,
    val video_size: Long,
) {
    fun hasUseSize(): Long = system + image_size + video_size
}

    val errCode: Int,
    val path: String,
    val pts: Int,
    val status: Boolean,
)
