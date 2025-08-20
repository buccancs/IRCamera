package com.topdon.lib.core.config

import android.content.Context
import android.os.Build
import android.os.Environment
import com.blankj.utilcode.util.Utils
import com.topdon.lib.core.repository.GalleryRepository.DirType
import com.topdon.lib.core.utils.CommUtils
import java.io.File

object FileConfig {
    // Helper method to create directories and reduce duplication
    private fun ensureDirectoryExists(path: String): String {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return path
    }
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

    fun getFirmwareFile(filename: String): File = File(Utils.getApp().getExternalFilesDir("firmware"), filename)

    @JvmStatic
    fun getPdfDir(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
            val path = dir + File.separator + CommUtils.getAppName() + File.separator + "pdf"
            ensureDirectoryExists(path)
        } else {
            Environment.DIRECTORY_DOCUMENTS + "/${CommUtils.getAppName()}/pdf"
        }
    }

    @JvmStatic
    val excelDir: String
        get() {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
                val path = dir + File.separator + CommUtils.getAppName() + File.separator + "excel"
                ensureDirectoryExists(path)
            } else {
                Environment.DIRECTORY_DOCUMENTS + "/${CommUtils.getAppName()}/excel"
            }
        }


    @JvmStatic
    val gallerySourDir: String
        get() {
            val result = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + "TopInfrared"
            return ensureDirectoryExists(result)
        }

    @JvmStatic
    val oldTc001GalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + "TC001"
            return ensureDirectoryExists(path)
        }

    fun getGalleryDirByType(currentDirType : DirType) : String = when (currentDirType) {
        DirType.LINE -> lineGalleryDir
        DirType.TC007 -> tc007GalleryDir
        else -> ts004GalleryDir
    }

    @JvmStatic
    val lineGalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + CommUtils.getAppName()
            return ensureDirectoryExists(path)
        }

    @JvmStatic
    val ts004GalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + "TS004"
            return ensureDirectoryExists(path)
        }

    @JvmStatic
    val tc007GalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + "TC007"
            return ensureDirectoryExists(path)
        }

    @JvmStatic
    val lineIrGalleryDir: String
        get() {
            val dir = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath
            val path = dir + File.separator + "${CommUtils.getAppName()}-ir"
            return ensureDirectoryExists(path)
        }

    @JvmStatic
    val tc007IrGalleryDir: String
        get() {
            val dir = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath
            val path = dir + File.separator + "TC007-ir"
            return ensureDirectoryExists(path)
        }



    @JvmStatic
    val documentsDir: String
        get() {
            return if (Build.VERSION.SDK_INT < 29) {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
                val path = dir + File.separator + CommUtils.getAppName() + "/house"
                ensureDirectoryExists(path)
            } else {
                Environment.DIRECTORY_DOCUMENTS + File.separator + CommUtils.getAppName() + "/house/"
            }
        }
}