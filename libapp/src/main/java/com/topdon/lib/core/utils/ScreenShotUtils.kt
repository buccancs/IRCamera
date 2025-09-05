package com.topdon.lib.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.topdon.lib.core.bean.tools.ScreenBean
import com.topdon.lib.core.utils.ImageUtils

/**
 * Screen shot utility for thermal imaging
 */
object ScreenShotUtils {
    
    /**
     * Take screenshot of a view and save as ScreenBean
     */
    fun takeScreenShot(view: View, callback: (ScreenBean) -> Unit) {
        try {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache(true)
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            
            val screenBean = ScreenBean(
                bitmap = bitmap,
                width = bitmap.width,
                height = bitmap.height
            )
            callback(screenBean)
        } catch (e: Exception) {
            // Return empty screen bean on error
            callback(ScreenBean())
        }
    }
    
    /**
     * Create ScreenBean from bitmap
     */
    fun createScreenBean(bitmap: Bitmap): ScreenBean {
        return ScreenBean(
            bitmap = bitmap,
            width = bitmap.width,
            height = bitmap.height
        )
    }
    
    /**
     * Take screenshot and save bitmap
     */
    fun shotScreenBitmap(context: Context, bitmap: Bitmap?, type: Int, screenBean: ScreenBean) {
        if (bitmap != null) {
            try {
                val fileName = ImageUtils.save(bitmap)
                // Callback or update UI here
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Take screenshot of view
     */
    fun shotScreen(context: Context, view: View, type: Int, screenBean: ScreenBean) {
        takeScreenShot(view) { bean ->
            if (bean.bitmap != null) {
                shotScreenBitmap(context, bean.bitmap, type, screenBean)
            }
        }
    }
}