package com.topdon.lib.app.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

object ScreenShotUtils {
    
    /**
     * Function description.
     */
    fun shotScreenBitmap(view: View): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Function description.
     */
    fun captureScreen(view: View, callback: (ScreenBean) -> Unit) {
        val bitmap = shotScreenBitmap(view)
        callback(ScreenBean(bitmap = bitmap))
    }
}