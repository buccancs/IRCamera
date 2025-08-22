package com.topdon.lib.core.tool

import java.text.SimpleDateFormat
import java.util.*

/**
 * Time utility tool for date/time operations
 */
object TimeTool {
    
    /**
     * Get current time as formatted string
     */
    fun getNowTime(format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return formatter.format(Date())
    }
    
    /**
     * Get current timestamp
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Format timestamp to string
     */
    fun formatTimestamp(timestamp: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * Get time for file naming
     */
    fun getFileTimeString(): String {
        return getNowTime("yyyyMMdd_HHmmss")
    }
}