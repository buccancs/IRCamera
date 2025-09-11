package com.topdon.tc001.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.topdon.tc001.controller.RecordingController
import com.topdon.tc001.controller.RecordingState
import com.csl.irCamera.R
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

/**
 * Background service for multi-modal sensor recording.
 * 
 * This service ensures continuous recording operation even when the app is in the background.
 * It manages the RecordingController and provides status updates through notifications.
 * 
 * Key Features:
 * - Foreground service for uninterrupted recording
 * - Real-time status notifications
 * - Automatic recovery from errors
 * - Integration with PC Controller communication
 * - Power management awareness
 * 
 * @author IRCamera Android Sensor Node (Spoke)
 */
class RecordingService : LifecycleService() {

    companion object {
        private const val TAG = "RecordingService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "recording_service_channel"
        
        // Actions
        const val ACTION_START_RECORDING = "com.topdon.tc001.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.topdon.tc001.STOP_RECORDING"
        const val ACTION_ADD_SYNC_MARKER = "com.topdon.tc001.ADD_SYNC_MARKER"
        
        // Extras
        const val EXTRA_SESSION_DIRECTORY = "session_directory"
        const val EXTRA_MARKER_TYPE = "marker_type"
        const val EXTRA_TIMESTAMP_NS = "timestamp_ns"
        
        /**
         * Start the recording service
         */
        fun startRecording(context: Context, sessionDirectory: String) {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = ACTION_START_RECORDING
                putExtra(EXTRA_SESSION_DIRECTORY, sessionDirectory)
            }
            context.startForegroundService(intent)
        }
        
        /**
         * Stop the recording service
         */
        fun stopRecording(context: Context) {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = ACTION_STOP_RECORDING
            }
            context.startService(intent)
        }
        
        /**
         * Add sync marker through service
         */
        fun addSyncMarker(context: Context, markerType: String, timestampNs: Long) {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = ACTION_ADD_SYNC_MARKER
                putExtra(EXTRA_MARKER_TYPE, markerType)
                putExtra(EXTRA_TIMESTAMP_NS, timestampNs)
            }
            context.startService(intent)
        }
    }

    // Service binding
    private val binder = RecordingServiceBinder()
    
    // Recording controller
    private lateinit var recordingController: RecordingController
    private var isInitialized = false
    
    // Current session
    private var currentSessionDirectory: String? = null
    private var recordingStartTime: Long = 0
    
    // Notification manager
    private lateinit var notificationManager: NotificationManager

    inner class RecordingServiceBinder : Binder() {
        fun getService(): RecordingService = this@RecordingService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "RecordingService created")
        
        // Initialize notification manager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        
        // Initialize recording controller
        recordingController = RecordingController(this, this)
        
        // Initialize sensors with permission validation
        lifecycleScope.launch {
            try {
                // Validate critical permissions before sensor initialization
                if (!validateRequiredPermissions()) {
                    Log.e(TAG, "Required permissions not granted - cannot initialize sensors")
                    stopSelf()
                    return@launch
                }
                
                val success = recordingController.initializeSensors()
                isInitialized = success
                
                if (success) {
                    Log.i(TAG, "Recording service initialized successfully")
                    setupStatusMonitoring()
                } else {
                    Log.e(TAG, "Failed to initialize recording service")
                    stopSelf()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing recording service", e)
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
        when (intent?.action) {
            ACTION_START_RECORDING -> {
                val sessionDirectory = intent.getStringExtra(EXTRA_SESSION_DIRECTORY)
                if (sessionDirectory != null) {
                    startRecordingSession(sessionDirectory)
                } else {
                    Log.e(TAG, "No session directory provided for recording")
                }
            }
            
            ACTION_STOP_RECORDING -> {
                stopRecordingSession()
            }
            
            ACTION_ADD_SYNC_MARKER -> {
                val markerType = intent.getStringExtra(EXTRA_MARKER_TYPE)
                val timestampNs = intent.getLongExtra(EXTRA_TIMESTAMP_NS, System.nanoTime())
                if (markerType != null) {
                    addSyncMarker(markerType, timestampNs)
                }
            }
        }
        
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "RecordingService destroyed")
        
        lifecycleScope.launch {
            try {
                recordingController.cleanup()
            } catch (e: Exception) {
                Log.e(TAG, "Error during service cleanup", e)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recording Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Multi-modal sensor recording service"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startRecordingSession(sessionDirectory: String) {
        if (!isInitialized) {
            Log.e(TAG, "Service not initialized, cannot start recording")
            return
        }
        
        lifecycleScope.launch {
            try {
                // Create session directory
                val sessionDir = File(sessionDirectory)
                if (!sessionDir.exists()) {
                    sessionDir.mkdirs()
                }
                
                currentSessionDirectory = sessionDirectory
                recordingStartTime = System.nanoTime()
                
                // Start foreground service
                startForeground(NOTIFICATION_ID, createRecordingNotification("Starting recording..."))
                
                // Start recording
                val success = recordingController.startRecording(sessionDirectory)
                
                if (success) {
                    Log.i(TAG, "Recording session started: $sessionDirectory")
                    updateNotification("Recording in progress")
                } else {
                    Log.e(TAG, "Failed to start recording session")
                    updateNotification("Recording failed to start")
                    stopRecordingSession()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error starting recording session", e)
                updateNotification("Recording error occurred")
                stopRecordingSession()
            }
        }
    }

    private fun stopRecordingSession() {
        lifecycleScope.launch {
            try {
                updateNotification("Stopping recording...")
                
                val success = recordingController.stopRecording()
                
                if (success) {
                    val sessionDuration = if (recordingStartTime > 0) {
                        (System.nanoTime() - recordingStartTime) / 1_000_000_000.0
                    } else 0.0
                    
                    Log.i(TAG, "Recording session stopped (duration: ${sessionDuration}s)")
                    updateNotification("Recording completed (${String.format("%.1f", sessionDuration)}s)")
                    
                    // Stop foreground service after a brief delay to show completion message
                    kotlinx.coroutines.delay(2000)
                    stopForeground(true)
                    stopSelf()
                } else {
                    Log.e(TAG, "Failed to stop recording session cleanly")
                    updateNotification("Recording stop failed")
                }
                
                currentSessionDirectory = null
                recordingStartTime = 0
                
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recording session", e)
                updateNotification("Recording stop error")
            }
        }
    }

    private fun addSyncMarker(markerType: String, timestampNs: Long) {
        lifecycleScope.launch {
            try {
                recordingController.addSyncMarker(markerType, timestampNs)
                Log.i(TAG, "Sync marker added: $markerType")
                
                // Briefly update notification to show sync event
                val originalText = "Recording in progress"
                updateNotification("Sync marker: $markerType")
                kotlinx.coroutines.delay(1000)
                updateNotification(originalText)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error adding sync marker", e)
            }
        }
    }

    private fun setupStatusMonitoring() {
        // Monitor recording state changes
        recordingController.recordingStateFlow
            .onEach { state ->
                when (state) {
                    RecordingState.STARTING -> updateNotification("Starting sensors...")
                    RecordingState.RECORDING -> updateNotification("Recording in progress")
                    RecordingState.STOPPING -> updateNotification("Stopping sensors...")
                    RecordingState.STOPPED -> updateNotification("Recording stopped")
                    RecordingState.ERROR -> updateNotification("Recording error")
                }
            }
            .launchIn(lifecycleScope)
        
        // Monitor sensor status
        recordingController.sensorStatusFlow
            .onEach { statusList ->
                val activeSensors = statusList.count { it.isRecording }
                val totalSamples = statusList.sumOf { it.samplesRecorded }
                val totalStorage = statusList.sumOf { it.storageUsedMB }
                
                if (activeSensors > 0) {
                    val statusText = "Recording: $activeSensors sensors, " +
                            "${totalSamples} samples, " +
                            "${String.format("%.1f", totalStorage)}MB"
                    updateNotification(statusText)
                }
            }
            .launchIn(lifecycleScope)
        
        // Monitor errors
        recordingController.errorFlow
            .onEach { error ->
                Log.w(TAG, "Recording controller error: ${error.message}")
                
                if (!error.isRecoverable) {
                    updateNotification("Critical error: ${error.message}")
                    stopRecordingSession()
                } else {
                    // Show temporary error notification
                    updateNotification("Warning: ${error.message}")
                    kotlinx.coroutines.delay(3000)
                    updateNotification("Recording in progress")
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun createRecordingNotification(contentText: String): Notification {
        val stopIntent = Intent(this, RecordingService::class.java).apply {
            action = ACTION_STOP_RECORDING
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("IRCamera Recording")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_media_play) // Use system icon for recording
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_media_pause, // Use system stop icon
                "Stop",
                stopPendingIntent
            )
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun updateNotification(contentText: String) {
        try {
            val notification = createRecordingNotification(contentText)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update notification", e)
        }
    }

    /**
     * Get the recording controller instance
     */
    fun getRecordingController(): RecordingController = recordingController

    /**
     * Check if service is initialized and ready
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * Get current session information
     */
    fun getCurrentSession(): SessionInfo? {
        return currentSessionDirectory?.let { directory ->
            SessionInfo(
                directory = directory,
                startTime = recordingStartTime,
                isRecording = recordingController.isRecording
            )
        }
    }

    /**
     * Validate that all required permissions are granted for sensor operation.
     * This is critical for Samsung devices which may fail silently without proper permissions.
     * Includes Bluetooth permissions required for Shimmer GSR sensor integration.
     */
    private fun validateRequiredPermissions(): Boolean {
        val requiredPermissions = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        
        // Add Bluetooth permissions for Shimmer GSR sensor (Android version-specific)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.addAll(listOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ))
        } else {
            requiredPermissions.addAll(listOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
        
        val missingPermissions = requiredPermissions.filter { permission ->
            androidx.core.content.ContextCompat.checkSelfPermission(this, permission) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isNotEmpty()) {
            Log.e(TAG, "Missing required permissions: ${missingPermissions.joinToString(", ")}")
            Log.e(TAG, "These permissions are required for multi-modal recording:")
            missingPermissions.forEach { permission ->
                when (permission) {
                    android.Manifest.permission.CAMERA -> 
                        Log.e(TAG, "  - CAMERA: Required for RGB video/image recording")
                    android.Manifest.permission.RECORD_AUDIO -> 
                        Log.e(TAG, "  - RECORD_AUDIO: Required for video audio track")
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE -> 
                        Log.e(TAG, "  - WRITE_EXTERNAL_STORAGE: Required for saving recording files")
                    android.Manifest.permission.READ_EXTERNAL_STORAGE -> 
                        Log.e(TAG, "  - READ_EXTERNAL_STORAGE: Required for reading existing files")
                    android.Manifest.permission.BLUETOOTH_SCAN -> 
                        Log.e(TAG, "  - BLUETOOTH_SCAN: Required for discovering Shimmer GSR devices")
                    android.Manifest.permission.BLUETOOTH_CONNECT -> 
                        Log.e(TAG, "  - BLUETOOTH_CONNECT: Required for connecting to Shimmer GSR devices")
                    android.Manifest.permission.BLUETOOTH -> 
                        Log.e(TAG, "  - BLUETOOTH: Required for Shimmer GSR device communication (legacy)")
                    android.Manifest.permission.BLUETOOTH_ADMIN -> 
                        Log.e(TAG, "  - BLUETOOTH_ADMIN: Required for Shimmer GSR device management (legacy)")
                    android.Manifest.permission.ACCESS_FINE_LOCATION -> 
                        Log.e(TAG, "  - ACCESS_FINE_LOCATION: Required for BLE device discovery (Android < 12)")
                }
            }
            return false
        }
        
        Log.d(TAG, "All required permissions are granted")
        return true
    }
}

/**
 * Current session information
 */
data class SessionInfo(
    val directory: String,
    val startTime: Long,
    val isRecording: Boolean
)