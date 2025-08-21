package com.topdon.lib.core.repository

import android.net.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * TS004 Repository for remote file operations
 * Minimal implementation to maintain compatibility after ARouter removal
 */
object TS004Repository {
    var netWork: Network? = null
    
    /**
     * Get newest files (stub implementation)
     * Returns empty list as TS004 functionality is being phased out
     */
    suspend fun getNewestFile(type: Int): List<FileBean>? = withContext(Dispatchers.IO) {
        // Return empty list as this functionality is being deprecated
        // This prevents crashes but doesn't provide actual TS004 functionality
        emptyList<FileBean>()
    }
    
    /**
     * Get files by page (stub implementation)
     */
    suspend fun getFileByPage(type: Int, pageNum: Int, pageCount: Int): List<FileBean>? = withContext(Dispatchers.IO) {
        // Return empty list as this functionality is being deprecated
        emptyList<FileBean>()
    }
    
    /**
     * Download file (stub implementation)
     */
    suspend fun download(url: String, file: File): Boolean = withContext(Dispatchers.IO) {
        // Return false as download functionality is deprecated
        false
    }
    
    /**
     * Delete files (stub implementation)
     */
    suspend fun deleteFiles(ids: Array<Int>): Boolean = withContext(Dispatchers.IO) {
        // Return false as delete functionality is deprecated
        false
    }
    
    /**
     * Download list (stub implementation)
     */
    suspend fun downloadList(downloadMap: Map<String, File>, callback: (String, Boolean) -> Unit): Int = withContext(Dispatchers.IO) {
        // Return 0 as download functionality is deprecated
        downloadMap.forEach { (path, _) ->
            callback(path, false)
        }
        0
    }
}