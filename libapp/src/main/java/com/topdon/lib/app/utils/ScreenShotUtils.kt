package com.topdon.lib.app.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

object ScreenShotUtils {
    
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
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun captureScreen(view: View, callback: (ScreenBean) -> Unit) {
        val bitmap = shotScreenBitmap(view)
        callback(ScreenBean(bitmap = bitmap))
    }
}