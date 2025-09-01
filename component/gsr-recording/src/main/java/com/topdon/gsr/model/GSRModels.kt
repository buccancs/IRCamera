package com.topdon.gsr.model

/**
 * Represents a single GSR data sample with timestamp information
 */
data class GSRSample(
    val timestamp: Long,           // System timestamp in milliseconds
    val utcTimestamp: Long,        // UTC timestamp for synchronization
    val conductance: Double,       // GSR conductance value in microsiemens
    val resistance: Double,        // GSR resistance value in kilohms
    val sampleIndex: Long,         // Sequential sample index
    val sessionId: String          // Session identifier
) {
    companion object {
        /**
         * Create a simulated GSR sample for testing/demo purposes
         */
        fun createSimulated(
            timestamp: Long, 
            utcTimestamp: Long, 
            sampleIndex: Long, 
            sessionId: String
        ): GSRSample {
            // Simulate realistic GSR values with some variation
            val baseConductance = 10.0 // Base conductance in microsiemens
            val variation = Math.sin(sampleIndex * 0.1) * 2.0 + Math.random() * 1.0
            val conductance = baseConductance + variation
            val resistance = 1000.0 / conductance // Convert to kilohms
            
            return GSRSample(
                timestamp = timestamp,
                utcTimestamp = utcTimestamp,
                conductance = conductance,
                resistance = resistance,
                sampleIndex = sampleIndex,
                sessionId = sessionId
            )
        }
    }
    
    /**
     * Convert sample to CSV row format
     */
    fun toCsvRow(): Array<String> {
        return arrayOf(
            timestamp.toString(),
            utcTimestamp.toString(), 
            String.format("%.6f", conductance),
            String.format("%.6f", resistance),
            sampleIndex.toString(),
            sessionId
        )
    }
}

/**
 * Represents session information and metadata
 */
data class SessionInfo(
    val sessionId: String,
    val startTime: Long,
    var endTime: Long? = null,
    val participantId: String? = null,
    val studyName: String? = null,
    var sampleCount: Long = 0,
    var syncMarks: MutableList<SyncMark> = mutableListOf(),
    val metadata: MutableMap<String, String> = mutableMapOf()
) {
    fun getDurationMs(): Long {
        return if (endTime != null) endTime!! - startTime else System.currentTimeMillis() - startTime
    }
    
    fun isActive(): Boolean {
        return endTime == null
    }
}

/**
 * Represents synchronization marks for cross-modal alignment
 */
data class SyncMark(
    val timestamp: Long,
    val utcTimestamp: Long,
    val eventType: String,           // e.g., "THERMAL_CAPTURE", "SYNC_FLASH", "USER_TRIGGER"
    val sessionId: String,
    val metadata: Map<String, String> = emptyMap()
) {
    fun toCsvRow(): Array<String> {
        val metadataJson = if (metadata.isNotEmpty()) {
            metadata.entries.joinToString(";") { "${it.key}=${it.value}" }
        } else {
            ""
        }
        
        return arrayOf(
            timestamp.toString(),
            utcTimestamp.toString(),
            eventType,
            sessionId,
            metadataJson
        )
    }
}