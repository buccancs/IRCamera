package com.topdon.lib.core.bean

/**
 * Gallery title bean for grouping gallery items by date
 */
data class GalleryTitle(
    override val time: Long,
    val title: String
) : GalleryItem