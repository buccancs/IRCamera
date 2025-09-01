package com.topdon.gsr.util

/**
 * Utility class for time synchronization and timestamp management
 * Implements unified NTP-style timing with Samsung S22 device as ground truth
 */
object TimeUtil {
    private const val TAG = "TimeUtil"
    
    // PC time offset for synchronization (would be set via network communication in production)
    private var pcTimeOffset: Long = 0L
    
    // Samsung S22 device ground truth timestamp base
    private var deviceGroundTruthBase: Long = System.currentTimeMillis()
    
    /**
     * Get UTC timestamp adjusted for PC synchronization with Samsung S22 ground truth
     */
    fun getUtcTimestamp(): Long {
        // Use Samsung S22 device clock as ground truth reference
        val currentDeviceTime = System.currentTimeMillis()
        val deviceOffset = currentDeviceTime - deviceGroundTruthBase
        return deviceGroundTruthBase + deviceOffset + pcTimeOffset
    }
    
    /**
     * Initialize Samsung S22 device as NTP ground truth reference
     * Called at application startup to establish unified time base
     */
    fun initializeGroundTruthTiming() {
        deviceGroundTruthBase = System.currentTimeMillis()
        // Only log if Android Log is available (not in unit tests)
        try {
            android.util.Log.d(TAG, "Samsung S22 device ground truth timestamp initialized: $deviceGroundTruthBase")
        } catch (e: Exception) {
            // Ignore - running in unit tests
        }
    }
    
    /**
     * Set PC time offset for synchronization
     * This would typically be called after network time sync with PC
     */
    fun setPcTimeOffset(offset: Long) {
        pcTimeOffset = offset
        // Only log if Android Log is available (not in unit tests)
        try {
            android.util.Log.d(TAG, "PC time offset set to: ${offset}ms from Samsung S22 ground truth")
        } catch (e: Exception) {
            // Ignore - running in unit tests
        }
    }
    
    /**
     * Get current PC time offset
     */
    fun getPcTimeOffset(): Long = pcTimeOffset
    
    /**
     * Get Samsung S22 device ground truth base timestamp
     */
    fun getGroundTruthBase(): Long = deviceGroundTruthBase
    
    /**
     * Convert system timestamp to UTC with PC offset and ground truth
     */
    fun systemToUtc(systemTime: Long): Long {
        val deviceOffset = systemTime - deviceGroundTruthBase
        return deviceGroundTruthBase + deviceOffset + pcTimeOffset
    }
    
    /**
     * Convert UTC timestamp back to system time
     */
    fun utcToSystem(utcTime: Long): Long {
        return utcTime - pcTimeOffset - (deviceGroundTruthBase - System.currentTimeMillis())
    }
    
    /**
     * Get synchronized timestamp for multi-modal recording
     * Uses Samsung S22 device clock as unified ground truth
     */
    fun getSynchronizedTimestamp(): Long {
        return getUtcTimestamp()
    }
    
    /**
     * Format timestamp for display with ground truth indicator
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
     * Generate session ID with timestamp from ground truth base
     */
    fun generateSessionId(prefix: String = "GSR"): String {
        return try {
            val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US)
                .format(java.util.Date(getSynchronizedTimestamp()))
            "${prefix}_${timestamp}"
        } catch (e: Exception) {
            // Fallback for unit tests
            "${prefix}_${getSynchronizedTimestamp()}"
        }
    }
    
    /**
     * Get timing metadata for session information
     */
    fun getTimingMetadata(): Map<String, String> {
        return mapOf(
            "ground_truth_base" to deviceGroundTruthBase.toString(),
            "pc_offset_ms" to pcTimeOffset.toString(),
            "device_model" to "Samsung_S22",
            "timing_mode" to "unified_ntp_style",
            "current_sync_time" to getSynchronizedTimestamp().toString()
        )
    }
}