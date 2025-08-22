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
     * Show video time in long format (HH:mm:ss)
     */
    fun showVideoTime(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = (durationMs / (1000 * 60 * 60)) % 24
        
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    /**
     * Get time for file naming
     */
    fun getFileTimeString(): String {
        return getNowTime("yyyyMMdd_HHmmss")
    }
}