package com.topdon.lib.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Image utilities - stub implementation for compilation
 */
class ImageUtils {
    
    companion object {
        
        /**
         * Save bitmap to file
         */
        fun saveBitmapToFile(bitmap: Bitmap, file: File, quality: Int = 90): Boolean {
            return try {
                val fileOutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
                true
            } catch (e: IOException) {
                false
            }
        }
        
        /**
         * Load bitmap from file
         */
        fun loadBitmapFromFile(file: File): Bitmap? {
            return if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        }
        
        /**
         * Resize bitmap maintaining aspect ratio
         */
        fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            
            val ratio = if (width > height) {
                maxWidth.toFloat() / width
            } else {
                maxHeight.toFloat() / height
            }
            
            val newWidth = (width * ratio).toInt()
            val newHeight = (height * ratio).toInt()
            
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
        }
        
        /**
         * Rotate image bitmap
         */
        fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        
        /**
         * Get image file extension
         */
        fun getImageExtension(mimeType: String): String {
            return when (mimeType) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                "image/gif" -> ".gif"
                "image/bmp" -> ".bmp"
                else -> ".jpg"
            }
        }
        
        /**
         * Check if file is image
         */
        fun isImageFile(file: File): Boolean {
            val extension = file.extension.lowercase()
            return extension in listOf("jpg", "jpeg", "png", "gif", "bmp")
        }
        
        /**
         * Save image to app directory
         */
        fun saveImageToApp(bitmap: Bitmap): String? {
            return try {
                val timestamp = System.currentTimeMillis()
                val filename = "IR_${timestamp}.jpg"
                val file = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES), filename)
                saveBitmapToFile(bitmap, file)
                file.absolutePath
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Get current time string for watermark
         */
        fun getNowTime(): String {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            return formatter.format(java.util.Date())
        }
    }
}