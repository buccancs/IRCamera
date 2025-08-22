package com.topdon.lib.core.bean

/**
 * Gallery bean for image/video file information
 */
data class GalleryBean(
    val id: String = "",
    val name: String = "",
    val path: String = "",
    override val time: Long = 0L,
    val size: Long = 0L,
    var hasDownload: Boolean = true,  // Changed to var to allow modification
    val isVideo: Boolean = false,
    val duration: Long = 0L
) : GalleryItem