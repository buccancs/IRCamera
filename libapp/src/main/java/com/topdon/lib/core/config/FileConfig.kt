package com.topdon.lib.core.config

import android.content.Context
import android.os.Build
import android.os.Environment
import com.blankj.utilcode.util.Utils
import com.topdon.lib.core.repository.GalleryRepository.DirType
import com.topdon.lib.core.utils.CommUtils
import java.io.File

object FileConfig {
    /**
     * [Chinese text].
     * [Chinese text], [Chinese text], [Chinese text].
     */
    fun getDetectImageDir(
        context: Context,
        child: String,
    ): File {
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

    /**
     * [Chinese text]-[Chinese text].
     * [Chinese text], [Chinese text], [Chinese text].
     */
    fun getSignImageDir(
        context: Context,
        child: String,
    ): File {
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

    /**
     * [Chinese text].
     */
    fun getFirmwareFile(filename: String): File = File(Utils.getApp().getExternalFilesDir("firmware"), filename)

    /**
     * [Chinese text].
     */
    @JvmStatic
    fun getPdfDir(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
            val path = dir + File.separator + CommUtils.getAppName() + File.separator + "pdf"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            path
        } else {
            Environment.DIRECTORY_DOCUMENTS + "/${CommUtils.getAppName()}/pdf"
        }
    }

    /**
     * temperature[Chinese text] Excel [Chinese text].
     */
    @JvmStatic
    val excelDir: String
        get() {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
                val path = dir + File.separator + CommUtils.getAppName() + File.separator + "excel"
                val file = File(path)
                if (!file.exists()) {
                    file.mkdirs()
                }
                path
            } else {
                Environment.DIRECTORY_DOCUMENTS + "/${CommUtils.getAppName()}/excel"
            }
        }

    /**
     * [Chinese text]gallery[Chinese text]
     */
    @JvmStatic
    val gallerySourDir: String
        get() {
            val result = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + "MPDC4GSR"
            val file = File(result)
            if (!file.exists()) {
                file.mkdirs()
            }
            return result
        }

    /**
     * [Chinese text] APP TC001 gallery[Chinese text], onlyfor[Chinese text]
     */
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

    fun getGalleryDirByType(currentDirType: DirType): String =
        when (currentDirType) {
            DirType.LINE -> lineGalleryDir
            DirType.TC007 -> tc007GalleryDir
            else -> ts004GalleryDir
        }

    /**
     * [Chinese text]line[Chinese text] gallery[Chinese text]
     */
    @JvmStatic
    val lineGalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + CommUtils.getAppName()
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    /**
     * TS004 [Chinese text]gallery[Chinese text]
     */
    @JvmStatic
    val ts004GalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + "TS004"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    /**
     * TC007 [Chinese text]gallery[Chinese text]
     */
    @JvmStatic
    val tc007GalleryDir: String
        get() {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            val path = dir + File.separator + "TC007"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    /**
     * [Chinese text]line[Chinese text] [Chinese text]gallerytemperature[Chinese text]
     */
    @JvmStatic
    val lineIrGalleryDir: String
        get() {
            val dir = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath
            val path = dir + File.separator + "${CommUtils.getAppName()}-ir"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    /**
     * TC007 [Chinese text]gallerytemperature[Chinese text]
     */
    @JvmStatic
    val tc007IrGalleryDir: String
        get() {
            val dir = Utils.getApp().getExternalFilesDir(Environment.DIRECTORY_DCIM)!!.absolutePath
            val path = dir + File.separator + "TC007-ir"
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    /**
     * [Chinese text]/Documents/APP[Chinese text]/house
     */
    @JvmStatic
    val documentsDir: String
        get() {
            return if (Build.VERSION.SDK_INT < 29) {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
                val path = dir + File.separator + CommUtils.getAppName() + "/house"
                val file = File(path)
                if (!file.exists()) {
                    file.mkdirs()
                }
                path
            } else {
                Environment.DIRECTORY_DOCUMENTS + File.separator + CommUtils.getAppName() + "/house/"
            }
        }
}
