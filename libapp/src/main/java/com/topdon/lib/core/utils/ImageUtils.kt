package com.topdon.lib.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.config.FileConfig.lineIrGalleryDir
import java.io.*

object ImageUtils {
    /**
     * [CN_TEXT].
     */
    fun saveToCache(
        context: Context,
        bitmap: Bitmap,
    ): String {
        val cacheFile = context.externalCacheDir ?: context.cacheDir
        val file = File(cacheFile, "Report_${System.currentTimeMillis()}.jpg")
        ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG)
        return file.absolutePath
    }

    /**
     * [CN_TEXT] Gallery/APP[CN_TEXT] [CN_TEXT]，[CN_TEXT] APP[CN_TEXT]_[CN_TEXT].jpg
     * [CN_TEXT]Photo [CN_TEXT] 2D[CN_TEXT] [CN_TEXT].
     */
    fun save(
        bitmap: Bitmap,
        isTC007: Boolean = false,
    ): String {
        // [CN_TEXT]，[CN_TEXT]
        val dicName = if (isTC007) "TC007" else CommUtils.getAppName()
        val fileName = "${dicName}_${System.currentTimeMillis()}.jpg"
        val saveFile = ImageUtils.save2Album(bitmap, dicName, Bitmap.CompressFormat.JPEG)
        return if (saveFile != null) {
            val name = saveFile.name
            name.replace(".JPG", "")
        } else {
            fileName.replace(".JPG", "")
        }
    }

    /**
     * [CN_TEXT]Photo[CN_TEXT]，[CN_TEXT]Visible light，[CN_TEXT]Visible light[CN_TEXT]，[CN_TEXT]，[CN_TEXT]，[CN_TEXT]
     */
    fun saveImageToApp(bitmap: Bitmap): String {
        val saveFile = File(Utils.getApp().cacheDir, "PinP_${System.currentTimeMillis()}.jpg")
        ImageUtils.save(bitmap, saveFile, Bitmap.CompressFormat.JPEG)
        return saveFile.absolutePath
    }

    // [CN_TEXT]lite[CN_TEXT]
    fun saveLiteFrame(
        bs: ByteArray,
        capital: ByteArray,
        nuct: ByteArray,
        name: String,
    ) {
        try {
            val dir = lineIrGalleryDir
            val galleryPath = File(dir)
            val fileName = "$name.ir"
            val file = File(galleryPath, fileName)
            file.writeBytes(capital.plus(bs))
            Log.w("[CN_TEXT]:", file.absolutePath)
        } catch (e: Exception) {
            XLog.e("[CN_TEXT]: ${e.message}")
        }
    }

    // [CN_TEXT]
    fun saveFrame(
        bs: ByteArray,
        capital: ByteArray,
        name: String,
    ) {
        try {
            val dir = lineIrGalleryDir
            val galleryPath = File(dir)
            val fileName = "$name.ir"
            val file = File(galleryPath, fileName)
            file.writeBytes(capital.plus(bs))
            Log.w("[CN_TEXT]:", file.absolutePath)
        } catch (e: Exception) {
            XLog.e("[CN_TEXT]: ${e.message}")
        }
    }

    /**
     * [CN_TEXT]argb[CN_TEXT]
     */
    fun saveOneFrameAGRB(
        bs: ByteArray,
        name: String,
    ) {
        try {
            val dir = lineIrGalleryDir
            val galleryPath = File(dir)
            val fileName = "$name.ir"
            val file = File(galleryPath, fileName)
            file.writeBytes(bs)
        } catch (e: Exception) {
            XLog.e("[CN_TEXT]: ${e.message}")
        }
    }
}
