package com.example.thermal_lite.util

import android.content.Context
import java.io.IOException

object CommonUtil {
    fun getAssetData(context: Context, assetPath: String): ByteArray? {
        return try {
            context.assets.open(assetPath).use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}