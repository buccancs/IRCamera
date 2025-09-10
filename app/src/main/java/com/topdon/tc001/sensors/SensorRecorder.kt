package com.topdon.tc001.sensors

import kotlinx.coroutines.flow.Flow

    suspend fun startRecording(sessionDirectory: String): Boolean

    suspend fun addSyncMarker(
        markerType: String,
        timestampNs: Long,
        metadata: Map<String, String> = emptyMap(),
    )

    /**
     * Clean up all resources and disconnect from hardware.
     * Should be called when the sensor is no longer needed.
     */
    suspend fun cleanup()

    /**
     * Flow of recording status updates.
     * Emits RecordingStatus updates for real-time monitoring.
     */
    fun getStatusFlow(): Flow<RecordingStatus>

    /**
     * Flow of error events from this sensor.
     * Critical for error handling and recovery in the RecordingController.
     */
    fun getErrorFlow(): Flow<SensorError>

    /**
     * Get current recording statistics.
     * Used for real-time monitoring and quality assurance.
     */
    fun getRecordingStats(): RecordingStats
}

/**
 * Recording status for real-time monitoring
 */
data class RecordingStatus(
    val sensorId: String,
    val sensorType: String,
    val isRecording: Boolean,
    val samplesRecorded: Long,
    val currentDataRate: Double,
    val storageUsedMB: Double,
    val timestampNs: Long,
)

/**
 * Error information from sensor operations
 */
data class SensorError(
    val sensorId: String,
    val sensorType: String,
    val errorType: ErrorType,
    val errorMessage: String,
    val timestampNs: Long,
    val isRecoverable: Boolean = true,
)

/**
 * Types of sensor errors for classification and handling
 */
enum class ErrorType {
    INITIALIZATION_FAILED,
    HARDWARE_DISCONNECTED,
    RECORDING_FAILED,
    STORAGE_FULL,
    PERMISSION_DENIED,
    SYNC_FAILED,
    DATA_CORRUPTION,
    DEVICE_ERROR,
    STORAGE_ERROR,
    UNKNOWN,
}

/**
 * Recording statistics for monitoring and quality assurance
 */
data class RecordingStats(
    val sensorId: String,
    val sensorType: String,
    val sessionDurationMs: Long,
    val totalSamplesRecorded: Long,
    val averageDataRate: Double,
    val droppedSamples: Long,
    val storageUsedMB: Double,
    val syncMarkersCount: Int,
    val lastSampleTimestampNs: Long,
)
