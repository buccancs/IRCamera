package com.topdon.lib.core.tools

import java.text.SimpleDateFormat
import java.util.*

/**
 * Time formatting utilities
 */
object TimeTool {
    
    fun formatTimestamp(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
            else -> String.format("%02d:%02d", minutes, seconds % 60)
        }
    }
    
    fun timeToMinute(timestamp: Long): String {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    fun showDateType(timestamp: Long, type: String = "day"): String {
        val formatter = when (type) {
            "day" -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            "hour" -> SimpleDateFormat("HH:mm", Locale.getDefault()) 
            "minute" -> SimpleDateFormat("mm:ss", Locale.getDefault())
            else -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        }
        return formatter.format(Date(timestamp))
    }
    
    /**
     * Show current date in second format (yyyyMMddHHmmss)
     */
    fun showDateSecond(): String {
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return formatter.format(Date())
    }
    
    /**
     * Show video time in long format (HH:mm:ss)
     */
    fun showVideoLongTime(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = (durationMs / (1000 * 60 * 60)) % 24
        
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}