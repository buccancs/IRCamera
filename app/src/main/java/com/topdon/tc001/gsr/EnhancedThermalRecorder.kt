package com.topdon.tc001.gsr

import android.content.Context
import android.util.Log
import com.topdon.gsr.service.GSRRecorder
import com.topdon.gsr.service.SessionManager
import com.topdon.gsr.util.TimeUtil
import com.topdon.gsr.model.SessionInfo
import java.io.File

/**
 * Enhanced thermal recorder that automatically integrates GSR recording
 * Provides drop-in replacement for existing thermal recording functionality
 */
class EnhancedThermalRecorder private constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "EnhancedThermalRecorder"
        
        fun create(context: Context): EnhancedThermalRecorder {
            return EnhancedThermalRecorder(context)
        }
    }
    
    private val gsrRecorder: GSRRecorder = GSRRecorder(context)
    private val sessionManager: SessionManager = SessionManager.getInstance(context)
    
    private var currentSession: SessionInfo? = null
    private var isRecording = false
    
    private val gsrListener = object : GSRRecorder.GSRRecordingListener {
        override fun onRecordingStarted(sessionInfo: SessionInfo) {
            Log.i(TAG, "Enhanced thermal recording with GSR started: ${sessionInfo.sessionId}")
            currentSession = sessionInfo
        }
        
        override fun onRecordingStopped(sessionInfo: SessionInfo) {
            Log.i(TAG, "Enhanced thermal recording with GSR stopped: ${sessionInfo.sessionId}")
            currentSession = null
            isRecording = false
        }
        
        override fun onSampleRecorded(sample: com.topdon.gsr.model.GSRSample) {
            // Log every 1280 samples (10 seconds at 128Hz) to avoid spam
            if (sample.sampleIndex % 1280 == 0L) {
                Log.d(TAG, "GSR recording: ${sample.sampleIndex} samples (${sample.sampleIndex / 128}s)")
            }
        }
        
        override fun onSyncMarkAdded(syncMark: com.topdon.gsr.model.SyncMark) {
            Log.d(TAG, "Thermal sync event: ${syncMark.eventType}")
        }
        
        override fun onError(error: String) {
            Log.e(TAG, "GSR recording error during thermal session: $error")
        }
    }
    
    init {
        gsrRecorder.addListener(gsrListener)
    }
    
    /**
     * Start recording session with automatic GSR integration
     */
    fun startRecording(
        sessionName: String, 
        participantId: String? = null,
        enableGsr: Boolean = true
    ): Boolean {
        if (isRecording) {
            Log.w(TAG, "Recording already in progress")
            return false
        }
        
        val sessionId = if (sessionName.contains("_")) {
            sessionName // Use provided name if it looks like a session ID
        } else {
            TimeUtil.generateSessionId(sessionName)
        }
        
        if (enableGsr) {
            // Start GSR recording automatically
            if (gsrRecorder.startRecording(sessionId, participantId, "Thermal_GSR_Study")) {
                isRecording = true
                Log.i(TAG, "Enhanced thermal recording started with GSR: $sessionId")
                return true
            } else {
                Log.e(TAG, "Failed to start GSR recording for thermal session")
                return false
            }
        } else {
            // Create session without GSR recording
            currentSession = sessionManager.createSession(sessionId, participantId, "Thermal_Only_Study")
            isRecording = true
            Log.i(TAG, "Thermal recording started without GSR: $sessionId")
            return true
        }
    }
    
    /**
     * Stop recording session
     */
    fun stopRecording(): SessionInfo? {
        if (!isRecording) {
            Log.w(TAG, "No recording in progress")
            return currentSession
        }
        
        val session = if (gsrRecorder.isRecording()) {
            gsrRecorder.stopRecording()
        } else {
            currentSession?.let { sessionManager.completeSession(it.sessionId) }
        }
        
        isRecording = false
        Log.i(TAG, "Enhanced thermal recording stopped")
        return session
    }
    
    /**
     * Trigger synchronization event (e.g., thermal capture)
     */
    fun triggerSyncEvent(eventType: String = "THERMAL_CAPTURE", metadata: Map<String, String> = emptyMap()): Boolean {
        return if (isRecording) {
            if (gsrRecorder.isRecording()) {
                gsrRecorder.addSyncMark(eventType, metadata)
            } else {
                // Add sync mark to session manager for thermal-only sessions
                currentSession?.let { session ->
                    val syncMark = com.topdon.gsr.model.SyncMark(
                        timestamp = System.currentTimeMillis(),
                        utcTimestamp = TimeUtil.getUtcTimestamp(),
                        eventType = eventType,
                        sessionId = session.sessionId,
                        metadata = metadata
                    )
                    session.syncMarks.add(syncMark)
                    Log.d(TAG, "Sync event added to thermal-only session: $eventType")
                    true
                } ?: false
            }
        } else {
            Log.w(TAG, "Cannot trigger sync event - not recording")
            false
        }
    }
    
    /**
     * Trigger thermal capture with automatic sync marking
     */
    fun captureFrame(frameMetadata: Map<String, String> = emptyMap()): Boolean {
        val metadata = mutableMapOf<String, String>().apply {
            putAll(frameMetadata)
            put("capture_type", "thermal")
            put("timestamp", TimeUtil.formatTimestamp(System.currentTimeMillis()))
        }
        
        return triggerSyncEvent("THERMAL_FRAME_CAPTURE", metadata)
    }
    
    /**
     * Check if currently recording
     */
    fun isRecording(): Boolean = isRecording
    
    /**
     * Get current session information
     */
    fun getCurrentSession(): SessionInfo? = currentSession
    
    /**
     * Get session directory for file storage
     */
    fun getSessionDirectory(): File? {
        return gsrRecorder.getSessionDirectory()
    }
    
    /**
     * Set PC time offset for synchronization
     */
    fun setPcTimeOffset(offsetMs: Long) {
        TimeUtil.setPcTimeOffset(offsetMs)
        Log.d(TAG, "PC time offset set: ${offsetMs}ms")
    }
    
    /**
     * Add custom metadata to current session
     */
    fun addSessionMetadata(key: String, value: String): Boolean {
        return currentSession?.let { session ->
            session.metadata[key] = value
            true
        } ?: false
    }
    
    /**
     * Get recording statistics
     */
    fun getRecordingStats(): RecordingStats? {
        return currentSession?.let { session ->
            RecordingStats(
                sessionId = session.sessionId,
                duration = session.getDurationMs(),
                gsrSampleCount = if (gsrRecorder.isRecording()) gsrRecorder.getCurrentSession()?.sampleCount ?: 0 else 0,
                syncEventCount = session.syncMarks.size,
                isActive = session.isActive()
            )
        }
    }
    
    data class RecordingStats(
        val sessionId: String,
        val duration: Long,
        val gsrSampleCount: Long,
        val syncEventCount: Int,
        val isActive: Boolean
    )
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        gsrRecorder.removeListener(gsrListener)
        if (isRecording) {
            stopRecording()
        }
    }
}