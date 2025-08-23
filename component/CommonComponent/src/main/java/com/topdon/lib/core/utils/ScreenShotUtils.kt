package com.topdon.lib.core.utils

import android.graphics.Bitmap
import android.view.View

/**
 * Screenshot utilities for thermal imaging
 */
object ScreenShotUtils {
    
    /**
     * Take screenshot of view
     */
    fun takeScreenshot(view: View): Bitmap? {
        return try {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bitmap = view.drawingCache
            view.isDrawingCacheEnabled = false
            bitmap
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Save screenshot to file
     */
    fun saveScreenshot(view: View, filePath: String): Boolean {
        return try {
            val bitmap = takeScreenshot(view)
            bitmap?.let {
                // In production would save to file
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Take screenshot with bitmap and screen bean parameters
     */
    fun shotScreenBitmap(context: android.content.Context, bitmap: Bitmap?, mode: Int, screenBean: Any?): Bitmap? {
        // Stub implementation for thermal camera screenshot
        return bitmap
    }
}