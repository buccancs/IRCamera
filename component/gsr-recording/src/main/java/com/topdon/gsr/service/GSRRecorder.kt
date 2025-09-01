package com.topdon.gsr.service

import android.content.Context
import android.os.Environment
import android.util.Log
import com.opencsv.CSVWriter
import com.topdon.gsr.model.GSRSample
import com.topdon.gsr.model.SessionInfo
import com.topdon.gsr.model.SyncMark
import com.topdon.gsr.util.TimeUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Core GSR recorder that handles 128 Hz data acquisition and CSV logging
 */
class GSRRecorder(
    private val context: Context,
    private val samplingRateHz: Int = 128
) {
    companion object {
        private const val TAG = "GSRRecorder"
        private const val SESSIONS_DIR = "IRCamera_Sessions"
        private const val SIGNALS_FILENAME = "signals.csv"
        private const val SYNC_MARKS_FILENAME = "sync_marks.csv"
        private const val SESSION_METADATA_FILENAME = "session_metadata.json"
        
        private val SIGNALS_HEADER = arrayOf(
            "timestamp_ms", "utc_timestamp_ms", "conductance_us", "resistance_kohms", "sample_index", "session_id"
        )
        
        private val SYNC_MARKS_HEADER = arrayOf(
            "timestamp_ms", "utc_timestamp_ms", "event_type", "session_id", "metadata"
        )
    }

    private val sampleIntervalMs = 1000L / samplingRateHz
    private val isRecording = AtomicBoolean(false)
    private val sampleIndex = AtomicLong(0)
    
    private var currentSession: SessionInfo? = null
    private var recordingJob: Job? = null
    private var sessionDirectory: File? = null
    private var signalsWriter: CSVWriter? = null
    private var syncMarksWriter: CSVWriter? = null
    
    private val listeners = mutableListOf<GSRRecordingListener>()
    
    interface GSRRecordingListener {
        fun onRecordingStarted(sessionInfo: SessionInfo)
        fun onRecordingStopped(sessionInfo: SessionInfo)
        fun onSampleRecorded(sample: GSRSample)
        fun onSyncMarkAdded(syncMark: SyncMark)
        fun onError(error: String)
    }
    
    fun addListener(listener: GSRRecordingListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: GSRRecordingListener) {
        listeners.remove(listener)
    }
    
    /**
     * Start GSR recording session
     */
    fun startRecording(sessionId: String, participantId: String? = null, studyName: String? = null): Boolean {
        if (isRecording.get()) {
            Log.w(TAG, "Recording already in progress")
            return false
        }
        
        try {
            // Create session directory
            sessionDirectory = createSessionDirectory(sessionId)
            if (sessionDirectory == null) {
                notifyError("Failed to create session directory")
                return false
            }
            
            // Initialize CSV writers
            if (!initializeCsvWriters()) {
                notifyError("Failed to initialize CSV writers")
                return false
            }
            
            // Create session info
            currentSession = SessionInfo(
                sessionId = sessionId,
                startTime = System.currentTimeMillis(),
                participantId = participantId,
                studyName = studyName
            )
            
            // Reset counters
            sampleIndex.set(0)
            isRecording.set(true)
            
            // Start recording coroutine
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                recordingLoop()
            }
            
            currentSession?.let { session ->
                listeners.forEach { it.onRecordingStarted(session) }
            }
            
            Log.i(TAG, "GSR recording started: sessionId=$sessionId, samplingRate=${samplingRateHz}Hz")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            cleanup()
            notifyError("Failed to start recording: ${e.message}")
            return false
        }
    }
    
    /**
     * Stop GSR recording session
     */
    fun stopRecording(): SessionInfo? {
        if (!isRecording.get()) {
            Log.w(TAG, "No recording in progress")
            return currentSession
        }
        
        isRecording.set(false)
        recordingJob?.cancel()
        
        currentSession?.let { session ->
            session.endTime = System.currentTimeMillis()
            session.sampleCount = sampleIndex.get()
            
            // Save session metadata
            saveSessionMetadata(session)
            
            listeners.forEach { it.onRecordingStopped(session) }
            Log.i(TAG, "GSR recording stopped: sessionId=${session.sessionId}, samples=${session.sampleCount}")
        }
        
        cleanup()
        val completedSession = currentSession
        currentSession = null
        
        return completedSession
    }
    
    /**
     * Add synchronization mark during recording
     */
    fun addSyncMark(eventType: String, metadata: Map<String, String> = emptyMap()): Boolean {
        val session = currentSession ?: return false
        
        val syncMark = SyncMark(
            timestamp = System.currentTimeMillis(),
            utcTimestamp = TimeUtil.getUtcTimestamp(),
            eventType = eventType,
            sessionId = session.sessionId,
            metadata = metadata
        )
        
        session.syncMarks.add(syncMark)
        
        // Write to CSV immediately
        syncMarksWriter?.writeNext(syncMark.toCsvRow())
        syncMarksWriter?.flush()
        
        listeners.forEach { it.onSyncMarkAdded(syncMark) }
        Log.d(TAG, "Sync mark added: eventType=$eventType")
        
        return true
    }
    
    /**
     * Get current recording status
     */
    fun isRecording(): Boolean = isRecording.get()
    
    /**
     * Get current session info
     */
    fun getCurrentSession(): SessionInfo? = currentSession
    
    /**
     * Get session directory path
     */
    fun getSessionDirectory(): File? = sessionDirectory
    
    private fun createSessionDirectory(sessionId: String): File? {
        return try {
            val externalStorage = context.getExternalFilesDir(null) 
                ?: Environment.getExternalStorageDirectory()
            
            val sessionsDir = File(externalStorage, SESSIONS_DIR)
            val sessionDir = File(sessionsDir, sessionId)
            
            if (!sessionDir.exists() && !sessionDir.mkdirs()) {
                Log.e(TAG, "Failed to create session directory: ${sessionDir.absolutePath}")
                return null
            }
            
            Log.d(TAG, "Created session directory: ${sessionDir.absolutePath}")
            sessionDir
        } catch (e: Exception) {
            Log.e(TAG, "Error creating session directory", e)
            null
        }
    }
    
    private fun initializeCsvWriters(): Boolean {
        return try {
            sessionDirectory?.let { dir ->
                // Initialize signals CSV writer
                val signalsFile = File(dir, SIGNALS_FILENAME)
                signalsWriter = CSVWriter(FileWriter(signalsFile)).apply {
                    writeNext(SIGNALS_HEADER)
                    flush()
                }
                
                // Initialize sync marks CSV writer
                val syncMarksFile = File(dir, SYNC_MARKS_FILENAME)
                syncMarksWriter = CSVWriter(FileWriter(syncMarksFile)).apply {
                    writeNext(SYNC_MARKS_HEADER)
                    flush()
                }
                
                true
            } ?: false
        } catch (e: IOException) {
            Log.e(TAG, "Failed to initialize CSV writers", e)
            false
        }
    }
    
    private suspend fun recordingLoop() {
        Log.d(TAG, "Starting GSR recording loop at ${samplingRateHz}Hz")
        
        while (isRecording.get()) {
            try {
                val currentTime = System.currentTimeMillis()
                val utcTime = TimeUtil.getUtcTimestamp()
                val currentIndex = sampleIndex.getAndIncrement()
                
                currentSession?.let { session ->
                    val sample = GSRSample.createSimulated(
                        timestamp = currentTime,
                        utcTimestamp = utcTime,
                        sampleIndex = currentIndex,
                        sessionId = session.sessionId
                    )
                    
                    // Write to CSV
                    signalsWriter?.writeNext(sample.toCsvRow())
                    if (currentIndex % 10 == 0L) { // Flush every 10 samples to balance performance and data safety
                        signalsWriter?.flush()
                    }
                    
                    // Notify listeners
                    listeners.forEach { it.onSampleRecorded(sample) }
                }
                
                // Maintain precise sampling rate
                delay(sampleIntervalMs)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error in recording loop", e)
                if (isRecording.get()) { // Only notify if still supposed to be recording
                    notifyError("Recording error: ${e.message}")
                }
                break
            }
        }
        
        Log.d(TAG, "GSR recording loop ended")
    }
    
    private fun saveSessionMetadata(session: SessionInfo) {
        try {
            sessionDirectory?.let { dir ->
                val metadataFile = File(dir, SESSION_METADATA_FILENAME)
                
                val gson = com.google.gson.Gson()
                val json = gson.toJson(session)
                
                metadataFile.writeText(json)
                Log.d(TAG, "Session metadata saved")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save session metadata", e)
        }
    }
    
    private fun cleanup() {
        try {
            signalsWriter?.close()
            syncMarksWriter?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing CSV writers", e)
        } finally {
            signalsWriter = null
            syncMarksWriter = null
        }
    }
    
    private fun notifyError(error: String) {
        Log.e(TAG, error)
        listeners.forEach { it.onError(error) }
    }
}