package com.topdon.lib.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import java.io.ByteArrayOutputStream

/**
 * Bitmap utility class - stub implementation for compilation
 */
class BitmapUtils {
    
    companion object {
        
        /**
         * Scale bitmap to target size
         */
        fun scaleBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
            return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
        }
        
        /**
         * Rotate bitmap by angle
         */
        fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        
        /**
         * Convert bitmap to byte array
         */
        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }
        
        /**
         * Convert byte array to bitmap
         */
        fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
        
        /**
         * Create bitmap from color
         */
        fun createColorBitmap(width: Int, height: Int, color: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(color)
            return bitmap
        }
        
        /**
         * Overlay text on bitmap
         */
        fun overlayTextOnBitmap(bitmap: Bitmap, text: String, x: Float, y: Float, paint: Paint): Bitmap {
            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)
            canvas.drawText(text, x, y, paint)
            return mutableBitmap
        }
    }
}