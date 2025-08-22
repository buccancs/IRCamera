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
        
        /**
         * Merge two bitmaps at specified position
         */
        fun mergeBitmap(background: Bitmap?, overlay: Bitmap?, x: Int = 0, y: Int = 0): Bitmap? {
            if (background == null) return overlay
            if (overlay == null) return background
            
            val mutableBitmap = background.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)
            canvas.drawBitmap(overlay, x.toFloat(), y.toFloat(), null)
            return mutableBitmap
        }
        
        /**
         * Merge bitmap with view
         */
        fun mergeBitmapByView(background: Bitmap?, overlay: Bitmap?, view: android.view.View?): Bitmap? {
            return mergeBitmap(background, overlay, 0, 0)
        }
        
        /**
         * Draw center label on bitmap
         */
        fun drawCenterLable(bitmap: Bitmap?, text: String): Bitmap? {
            if (bitmap == null) return null
            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 48f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            val x = bitmap.width / 2f
            val y = bitmap.height / 2f
            return overlayTextOnBitmap(bitmap, text, x, y, paint)
        }
    }
}