package com.topdon.tc001.sync

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.topdon.tc001.utils.TimeManager
import com.topdon.tc001.utils.SyncMarker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileWriter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Enhanced data synchronization and timestamping system for Phase 6 implementation.
 * 
 * Provides comprehensive temporal alignment for multi-modal sensor data with:
 * - Common start time reference across all sensors
 * - Per-sample timestamps with nanosecond precision
 * - Synchronization markers for alignment verification
 * - Post-processing support for data merging
 * - Clock drift compensation during long recordings
 * 
 * Technical Approach:
 * - Uses TimeManager for PC synchronization
 * - Establishes common session start reference
 * - Distributes sync markers across sensors
 * - Monitors timing quality during recording
 * - Generates alignment metadata for post-processing
 */
class DataSynchronizationManager(
    private val context: Context
) {
    companion object {
        private const val TAG = "DataSyncManager"
        private const val SYNC_MARKER_INTERVAL_MS = 10000L // 10 seconds
        private const val TIMING_LOG_FILENAME = "session_timing.json"
        private const val SYNC_EVENTS_FILENAME = "sync_events.csv"
        private const val MAX_TIMING_DRIFT_MS = 50.0 // Maximum acceptable drift
    }
    
    private val timeManager = TimeManager.getInstance(context)
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Session timing state
    private var sessionStartTime: SessionStartTime? = null
    private var isActiveSession = false
    private var sessionId: String? = null
    private var sessionDirectory: File? = null
    
    // Sync marker management
    private val syncMarkerCounter = AtomicLong(0)
    private val distributedMarkers = ConcurrentHashMap<Long, SyncMarkerEvent>()
    
    // Monitoring
    private var syncMarkerJob: Job? = null
    private var timingLogWriter: FileWriter? = null
    private var syncEventsWriter: FileWriter? = null
    
    // Data flows
    private val _syncEvents = MutableSharedFlow<SyncMarkerEvent>()
    val syncEvents: SharedFlow<SyncMarkerEvent> = _syncEvents.asSharedFlow()
    
    private val _timingQuality = MutableStateFlow<TimingQuality?>(null)
    val timingQuality: StateFlow<TimingQuality?> = _timingQuality.asStateFlow()
    
    /**
     * Session start time with multiple time references
     */
    data class SessionStartTime(
        val sessionId: String,
        val wallClockMs: Long,              // System.currentTimeMillis() at start
        val monotonicNs: Long,              // SystemClock.elapsedRealtimeNanos() at start  
        val synchronizedNs: Long,           // TimeManager synchronized timestamp
        val pcClockOffsetNs: Long,          // Clock offset applied for PC sync
        val syncQuality: com.topdon.tc001.utils.SyncQuality  // Quality of PC sync at start
    )
    
    /**
     * Synchronization marker event
     */
    data class SyncMarkerEvent(
        val markerId: Long,
        val eventType: String,
        val sessionId: String,
        val timestampNs: Long,
        val monotonicNs: Long,
        val wallClockMs: Long,
        val metadata: Map<String, Any> = emptyMap()
    )
    
    /**
     * Current timing quality assessment
     */
    data class TimingQuality(
        val sessionDurationMs: Long,
        val syncQuality: com.topdon.tc001.utils.SyncQuality,
        val markerCount: Long,
        val estimatedDriftMs: Double,
        val isQualityAcceptable: Boolean
    )
    
    /**
     * Initialize synchronization for a new recording session
     */
    suspend fun startSession(sessionId: String, sessionDirectory: File, pcControllerAddress: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Starting data synchronization session: $sessionId")
                
                this@DataSynchronizationManager.sessionId = sessionId
                this@DataSynchronizationManager.sessionDirectory = sessionDirectory
                
                // Attempt PC synchronization if address provided
                var pcSyncSuccessful = false
                if (pcControllerAddress != null) {
                    Log.i(TAG, "Attempting PC Controller synchronization...")
                    pcSyncSuccessful = timeManager.synchronizeWithPC(pcControllerAddress)
                    
                    if (pcSyncSuccessful) {
                        Log.i(TAG, "PC Controller synchronization successful")
                    } else {
                        Log.w(TAG, "PC Controller synchronization failed - using local timing")
                    }
                }
                
                // Capture session start time with multiple references
                val wallClock = System.currentTimeMillis()
                val monotonic = SystemClock.elapsedRealtimeNanos()
                val synchronized = timeManager.getCurrentTimestampNs()
                val syncQuality = timeManager.getSyncQuality()
                
                sessionStartTime = SessionStartTime(
                    sessionId = sessionId,
                    wallClockMs = wallClock,
                    monotonicNs = monotonic,
                    synchronizedNs = synchronized,
                    pcClockOffsetNs = syncQuality.offsetNs,
                    syncQuality = syncQuality
                )
                
                // Initialize logging
                initializeSessionLogging()
                
                // Log session start event
                logSessionEvent("session_start", mapOf(
                    "pc_sync_successful" to pcSyncSuccessful,
                    "sync_quality_level" to syncQuality.level.name,
                    "sync_quality_ms" to (syncQuality.qualityMs ?: -1),
                    "clock_offset_ns" to syncQuality.offsetNs
                ))
                
                // Start periodic sync marker distribution
                startSyncMarkerDistribution()
                
                isActiveSession = true
                Log.i(TAG, "Data synchronization session started successfully")
                
                // Update initial timing quality
                updateTimingQuality()
                
                return@withContext true
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start synchronization session", e)
                return@withContext false
            }
        }
    }
    
    /**
     * Generate synchronized timestamp for sensor data
     */
    fun generateTimestamp(): SensorTimestamp {
        val sessionStart = sessionStartTime ?: throw IllegalStateException("No active session")
        
        val wallClock = System.currentTimeMillis()
        val monotonic = SystemClock.elapsedRealtimeNanos()
        val synchronized = timeManager.getCurrentTimestampNs()
        
        // Calculate relative times from session start
        val sessionElapsedMs = wallClock - sessionStart.wallClockMs
        val sessionElapsedNs = monotonic - sessionStart.monotonicNs
        
        return SensorTimestamp(
            sessionId = sessionStart.sessionId,
            wallClockMs = wallClock,
            monotonicNs = monotonic,
            synchronizedNs = synchronized,
            sessionElapsedMs = sessionElapsedMs,
            sessionElapsedNs = sessionElapsedNs
        )
    }
    
    /**
     * Distribute synchronization marker across all sensors
     */
    suspend fun distributeSyncMarker(eventType: String, metadata: Map<String, Any> = emptyMap()): Long {
        return withContext(Dispatchers.IO) {
            try {
                val sessionStart = sessionStartTime ?: throw IllegalStateException("No active session")
                val markerId = syncMarkerCounter.incrementAndGet()
                
                val timestamp = generateTimestamp()
                
                val markerEvent = SyncMarkerEvent(
                    markerId = markerId,
                    eventType = eventType,
                    sessionId = sessionStart.sessionId,
                    timestampNs = timestamp.synchronizedNs,
                    monotonicNs = timestamp.monotonicNs,
                    wallClockMs = timestamp.wallClockMs,
                    metadata = metadata
                )
                
                // Store marker
                distributedMarkers[markerId] = markerEvent
                
                // Log sync event
                logSyncEvent(markerEvent)
                
                // Emit event
                _syncEvents.emit(markerEvent)
                
                Log.d(TAG, "Sync marker distributed: $eventType (ID: $markerId)")
                
                markerId
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to distribute sync marker", e)
                -1L
            }
        }
    }
    
    // Additional methods would be included here...
    
    /**
     * Clean up synchronization manager
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up data synchronization manager")
        
        syncMarkerJob?.cancel()
        syncScope.cancel()
        
        try {
            timingLogWriter?.close()
            syncEventsWriter?.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing log writers", e)
        }
        
        isActiveSession = false
        sessionStartTime = null
        distributedMarkers.clear()
    }
    
    // Private helper methods would be included here...
    
    private suspend fun initializeSessionLogging() {
        // Implementation details...
    }
    
    private suspend fun logSessionEvent(eventType: String, metadata: Map<String, Any>) {
        // Implementation details...
    }
    
    private suspend fun logSyncEvent(event: SyncMarkerEvent) {
        // Implementation details...
    }
    
    private fun startSyncMarkerDistribution() {
        // Implementation details...
    }
    
    private suspend fun updateTimingQuality() {
        // Implementation details...
    }
}

/**
 * Sensor timestamp with multiple time references
 */
data class SensorTimestamp(
    val sessionId: String,
    val wallClockMs: Long,
    val monotonicNs: Long,
    val synchronizedNs: Long,
    val sessionElapsedMs: Long,
    val sessionElapsedNs: Long
)