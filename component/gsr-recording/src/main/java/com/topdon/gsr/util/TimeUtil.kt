package com.topdon.gsr.util

/**
 * Utility class for time synchronization and timestamp management
 */
object TimeUtil {
    private const val TAG = "TimeUtil"
    
    // PC time offset for synchronization (would be set via network communication in production)
    private var pcTimeOffset: Long = 0L
    
    /**
     * Get UTC timestamp adjusted for PC synchronization
     */
    fun getUtcTimestamp(): Long {
        return System.currentTimeMillis() + pcTimeOffset
    }
    
    /**
     * Set PC time offset for synchronization
     * This would typically be called after network time sync with PC
     */
    fun setPcTimeOffset(offset: Long) {
        pcTimeOffset = offset
        // Only log if Android Log is available (not in unit tests)
        try {
            android.util.Log.d(TAG, "PC time offset set to: ${offset}ms")
        } catch (e: Exception) {
            // Ignore - running in unit tests
        }
    }
    
    /**
     * Get current PC time offset
     */
    fun getPcTimeOffset(): Long = pcTimeOffset
    
    /**
     * Convert system timestamp to UTC with PC offset
     */
    fun systemToUtc(systemTime: Long): Long {
        return systemTime + pcTimeOffset
    }
    
    /**
     * Convert UTC timestamp back to system time
     */
    fun utcToSystem(utcTime: Long): Long {
        return utcTime - pcTimeOffset
    }
    
    /**
     * Format timestamp for display
     */
    fun formatTimestamp(timestamp: Long): String {
        return try {
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.US)
                .format(java.util.Date(timestamp))
        } catch (e: Exception) {
            // Fallback for unit tests
            timestamp.toString()
        }
    }
    
    /**
     * Generate session ID with timestamp
     */
    fun generateSessionId(prefix: String = "GSR"): String {
        return try {
            val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US)
                .format(java.util.Date())
            "${prefix}_${timestamp}"
        } catch (e: Exception) {
            // Fallback for unit tests
            "${prefix}_${System.currentTimeMillis()}"
        }
    }
}