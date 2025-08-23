package com.topdon.lib.core.config

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Simplified FileConfig for component modules
 * Contains basic file path configuration functionality
 */
object FileConfig {
    
    private const val APP_NAME = "IRCamera"
    
    fun getDetectImageDir(context: Context, child: String): File {
        val externalDir = context.getExternalFilesDir("detect")
        return if (externalDir == null) {
            val fileDir = File(context.filesDir, "detect")
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            File(fileDir, child)
        } else {
            File(externalDir, child)
        }
    }

    fun getSignImageDir(context: Context, child: String): File {
        val externalDir = context.getExternalFilesDir("sign")
        return if (externalDir == null) {
            val fileDir = File(context.filesDir, "sign")
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            File(fileDir, child)
        } else {
            File(externalDir, child)
        }
    }
    
    fun getImagesDirectory(context: Context): File {
        val externalDir = context.getExternalFilesDir("images")
        return if (externalDir == null) {
            val fileDir = File(context.filesDir, "images")
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            fileDir
        } else {
            externalDir
        }
    }
    
    fun getDownloadDirectory(context: Context): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }
    
    @JvmStatic
    val oldTc001GalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + "TC001"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    @JvmStatic
    val lineGalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + APP_NAME
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    @JvmStatic
    val lineIrGalleryDir: String
        get() {
            // Simplified version - using external storage
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + "${APP_NAME}-ir"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }
        
        // Gallery path for thermal images 
        fun getGalleryPath(context: Context): String {
            val externalDir = context.getExternalFilesDir("Pictures")
            val path = "${externalDir?.absolutePath}/thermal"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }
        
        // TS004 gallery directory path for compatibility
        fun getTs004GalleryDir(context: Context): String {
            val externalDir = context.getExternalFilesDir("Pictures") 
            val path = "${externalDir?.absolutePath}/ts004"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }
        
        // TS004 gallery directory as property for compatibility
        val ts004GalleryDir: String
            get() = "/storage/emulated/0/Android/data/com.topdon.ircamera/files/Pictures/ts004"
            
        /**
         * Gallery path for thermal images and videos
         */
        val galleryPath: String
            get() = "/storage/emulated/0/IRCamera/Gallery/"
    }