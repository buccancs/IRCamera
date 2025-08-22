package com.topdon.lib.core.repository

import com.topdon.lib.core.bean.GalleryBean
import com.topdon.lib.core.enum.DirType

/**
 * Stub Gallery repository for file operations
 */
object GalleryRepository {
    fun loadAllReportImg(dirType: DirType): ArrayList<GalleryBean> {
        // Stub implementation - return empty list
        return ArrayList()
    }
    
    fun loadByPage(isVideo: Boolean, dirType: DirType, page: Int, pageCount: Int): ArrayList<GalleryBean>? {
        // Stub implementation - return empty list for first page, null for others
        return if (page == 1) ArrayList() else null
    }
}