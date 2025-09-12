package com.topdon.tc001.filemanagement

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToLong

/**
 * Enhanced file management system for Phase 7 implementation.
 * 
 * Manages multi-modal sensor recording files with organized session structure,
 * storage optimization, and cross-platform compatibility.
 */
class EnhancedFileManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "EnhancedFileManager"
        private const val BASE_DIR_NAME = "MultiSensorRecording"
        private const val SESSIONS_DIR_NAME = "sessions"
        private const val MIN_FREE_SPACE_MB = 500L
    }
    
    private val activeSessions = ConcurrentHashMap<String, SessionDirectory>()
    private val baseDirectory: File by lazy { determineOptimalBaseDirectory() }
    private val sessionsDirectory: File by lazy { File(baseDirectory, SESSIONS_DIR_NAME) }
    
    data class SessionDirectory(
        val sessionId: String,
        val sessionDir: File,
        val createdTime: Long = System.currentTimeMillis()
    )
    
    data class StorageInfo(
        val totalSpaceGB: Double,
        val freeSpaceGB: Double,
        val isLowSpace: Boolean
    )
    
    suspend fun initialize(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Initializing enhanced file management system")
                createDirectoryStructure()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize file management system", e)
                false
            }
        }
    }
    
    private fun determineOptimalBaseDirectory(): File {
        return try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    val externalDir = context.getExternalFilesDir(null)
                    if (externalDir?.canWrite() == true) {
                        File(externalDir, BASE_DIR_NAME)
                    } else {
                        File(context.filesDir, BASE_DIR_NAME)
                    }
                }
                else -> {
                    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                        File(Environment.getExternalStorageDirectory(), BASE_DIR_NAME)
                    } else {
                        File(context.filesDir, BASE_DIR_NAME)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error determining optimal directory, using internal storage", e)
            File(context.filesDir, BASE_DIR_NAME)
        }
    }
    
    private fun createDirectoryStructure(): Boolean {
        return try {
            listOf(baseDirectory, sessionsDirectory).forEach { dir ->
                if (!dir.exists() && !dir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory: ${dir.absolutePath}")
                    return false
                }
            }
            Log.d(TAG, "Directory structure created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating directory structure", e)
            false
        }
    }
    
    fun cleanup() {
        activeSessions.clear()
    }
}