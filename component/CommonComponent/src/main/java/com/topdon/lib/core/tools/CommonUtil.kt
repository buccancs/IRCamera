package com.topdon.lib.core.tools

import android.content.Context
import android.text.TextUtils
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * CommonUtil for general utility functions
 */
object CommonUtil {
    
    fun isStringEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }
    
    fun formatTemperature(temperature: Float): String {
        val df = DecimalFormat("#.0")
        return df.format(temperature)
    }
    
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatTime(timeMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timeMillis))
    }
    
    fun isValidString(str: String?): Boolean {
        return !TextUtils.isEmpty(str)
    }
    
    fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
    
    fun dpToPx(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
    
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
}