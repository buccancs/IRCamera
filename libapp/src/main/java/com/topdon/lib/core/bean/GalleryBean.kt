package com.topdon.lib.core.bean

import android.os.Parcel
import android.os.Parcelable
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.repository.FileBean
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.tools.VideoTools
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.TimeZone

@Parcelize
open class GalleryBean(
    /** id property */
    val id: Int, // Legacy field (unused for TC001 local files)
    /** path property */
    val path: String,
    /** thumb property */
    val thumb: String,
    /** name property */
    val name: String,
    /** duration property */
    val duration: Long,//，
    /** timeMillis property */
    val timeMillis: Long,
    /** hasDownload property */
    var hasDownload: Boolean,
) : Parcelable {
    constructor(file: File): this(
        id = 0,
        path = file.absolutePath,
        thumb = file.absolutePath,
        name = file.name,
        duration = VideoTools.getLocalVideoDuration(file.absolutePath),
        timeMillis = TimeTool.updateDateTime(file),
        hasDownload = true,
    )

    constructor(isVideo: Boolean, fileBean: FileBean): this(
        id = fileBean.id,
        path = "http://192.168.40.1:8080/DCIM/${fileBean.name}",
        thumb = if (isVideo) "http://192.168.40.1:8080/DCIM/${fileBean.thumb}" else "http://192.168.40.1:8080/DCIM/${fileBean.name}",
        name = fileBean.name,
        duration = fileBean.duration * 1000L,
        timeMillis = fileBean.time * 1000 - TimeZone.getDefault().getOffset(fileBean.time * 1000),
        hasDownload = File(FileConfig.ts004GalleryDir, fileBean.name).exists(),
    )
}

class GalleryTitle(timeMillis: Long) : GalleryBean(
    id = 0,
    path = "",
    thumb = "",
    name = "",
    duration = 0L,
    timeMillis = timeMillis,
    hasDownload = true,
) {
    companion object CREATOR : Parcelable.Creator<GalleryTitle> {
        override fun createFromParcel(parcel: Parcel): GalleryTitle {
            // Read the timeMillis from the parent's parcel data
            val id = parcel.readInt()
            val path = parcel.readString() ?: ""
            val thumb = parcel.readString() ?: ""
            val name = parcel.readString() ?: ""
            val duration = parcel.readLong()
            val timeMillis = parcel.readLong()
            val hasDownload = parcel.readByte() != 0.toByte()
            return GalleryTitle(timeMillis)
        }

        override fun newArray(size: Int): Array<GalleryTitle?> {
            return arrayOfNulls(size)
        }
    }
}

