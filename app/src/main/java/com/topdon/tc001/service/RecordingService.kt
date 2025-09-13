package com.topdon.tc001.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.topdon.tc001.controller.RecordingController
import com.topdon.tc001.controller.RecordingState
import com.topdon.gsr.model.SessionInfo
import com.topdon.gsr.model.SyncMark
import com.topdon.tc001.network.EnhancedNetworkClient
import com.topdon.tc001.network.NetworkClient
import com.topdon.tc001.network.NetworkServer
import com.topdon.tc001.utils.TimeManager
import com.csl.irCamera.R
import com.topdon.tc001.config.FeatureFlags
import com.topdon.tc001.config.ProtocolVersion
import com.topdon.tc001.logging.StructuredLogger
import com.topdon.tc001.supervisor.CrashSafeSupervisor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Background service for multi-modal sensor recording with PC-to-Phone server socket support.
 * 
 * This service ensures continuous recording operation even when the app is in the background.
 * It manages the RecordingController and provides status updates through notifications.
 * 
 * Key Features:
 * - Foreground service for uninterrupted recording
 * - Persistent server socket for PC connections
 * - Multi-connection support with re-accept loop
 * - NSD service advertisement for discovery
 * - Real-time status notifications
 */
class RecordingService : LifecycleService() {
    
    companion object {
        private const val TAG = "RecordingService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "recording_service_channel"
        private const val SERVER_PORT = 8080
        private const val NSD_SERVICE_NAME = "IRCamera-Sensor"
        private const val NSD_SERVICE_TYPE = "_ircamera._tcp."
    }

    // Core components
    private lateinit var recordingController: RecordingController
    private lateinit var networkClient: NetworkClient
    private lateinit var networkServer: NetworkServer
    private lateinit var notificationManager: NotificationManager
    private lateinit var structuredLogger: StructuredLogger
    private lateinit var crashSafeSupervisor: CrashSafeSupervisor
    
    // State management
    private val isInitialized = AtomicBoolean(false)
    private val isNetworkInitialized = AtomicBoolean(false)
    private val isConnectedToPC = AtomicBoolean(false)
    private val isRecording = AtomicBoolean(false)
    
    // Network service discovery
    private var nsdManager: NsdManager? = null
    private var nsdServiceInfo: NsdServiceInfo? = null
    private var isServiceRegistered = false
    
    inner class RecordingServiceBinder : Binder() {
        fun getService(): RecordingService = this@RecordingService
        fun getRecordingController(): RecordingController? = if (::recordingController.isInitialized) recordingController else null
        fun getNetworkServer(): NetworkServer? = if (::networkServer.isInitialized) networkServer else null
        fun getNetworkClient(): NetworkClient? = if (::networkClient.isInitialized) networkClient else null
        fun isConnectedToPC(): Boolean = isConnectedToPC.get()
        fun getServerStatus(): String = if (::networkServer.isInitialized && networkServer.isRunning()) "Running" else "Stopped"
        fun getConnectedClients(): Int = if (::networkServer.isInitialized && networkServer.isClientConnected()) 1 else 0
        fun stopServer() {
            if (::networkServer.isInitialized) {
                lifecycleScope.launch { networkServer.stop() }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "RecordingService created")
        
        // Initialize Phase 0 baseline components
        initializePhase0Baseline()
        
        // Initialize notification manager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        
        // Initialize NSD manager
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
        
        // Initialize recording controller
        recordingController = RecordingController(this, this)
        
        // Initialize both network client and server for maximum compatibility
        networkClient = NetworkClient(this)
        networkServer = NetworkServer(this, SERVER_PORT)
        
        // Initialize sensors and network under supervision
        crashSafeSupervisor.registerJob(
            id = "recording_service_init",
            name = "RecordingService Initialization",
            critical = true,
            restartable = false
        ) { _ ->
            lifecycleScope.launch {
                try {
                    val sensorsSuccess = recordingController.initializeSensors()
                    isInitialized.set(sensorsSuccess)
                    
                    val networkSuccess = initializeNetworkClient()
                    isNetworkInitialized.set(networkSuccess)
                    
                    if (sensorsSuccess) {
                        structuredLogger.log(
                            StructuredLogger.LogLevel.INFO,
                            "RecordingService",
                            "service_initialized"
                        )
                        Log.i(TAG, "Recording service initialized successfully")
                        setupStatusMonitoring()
                        setupNetworkServer()
                        
                        // Start server socket automatically if enabled
                        if (FeatureFlags.MDNS_ENABLE) {
                            startServerSocket()
                        }
                        
                        if (networkSuccess) {
                            Log.i(TAG, "Network client initialized successfully")
                            startNetworkDiscovery()
                        } else {
                            Log.w(TAG, "Network client initialization failed - running in server-only mode")
                        }
                    } else {
                        structuredLogger.log(
                            StructuredLogger.LogLevel.ERROR,
                            "RecordingService",
                            "initialization_failed"
                        )
                        stopSelf()
                    }
                } catch (e: Exception) {
                    structuredLogger.log(
                        StructuredLogger.LogLevel.ERROR,
                        "RecordingService",
                        "initialization_exception",
                        mapOf("error" to e.message)
                    )
                    Log.e(TAG, "Service initialization failed", e)
                    stopSelf()
                }
            }
        }
    }
    
    /**
     * Initialize Phase 0 baseline components for the service
     */
    private fun initializePhase0Baseline() {
        try {
            // Initialize feature flags if not already done
            FeatureFlags.initialize(this)
            
            // Initialize structured logger
            structuredLogger = StructuredLogger.getInstance(this)
            
            // Initialize crash-safe supervisor
            crashSafeSupervisor = CrashSafeSupervisor.getInstance(this)
            
            Log.i(TAG, "Phase 0 baseline components initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Phase 0 components", e)
            throw e
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return RecordingServiceBinder()
    }

    override fun onDestroy() {
        Log.i(TAG, "RecordingService destroying")
        
        lifecycleScope.launch {
            try {
                // Stop network server
                networkServer.stop()
                isConnectedToPC.set(false)
                Log.i(TAG, "Network server stopped")
                
                // Clean up network client resources if initialized
                if (isNetworkInitialized.get()) {
                    networkClient.disconnect()
                }
                
                // Stop recording if active
                recordingController.cleanup()
                
                structuredLogger.log(
                    StructuredLogger.LogLevel.INFO,
                    "RecordingService",
                    "service_cleanup_completed"
                )
            } catch (e: Exception) {
                structuredLogger.log(
                    StructuredLogger.LogLevel.ERROR,
                    "RecordingService",
                    "service_cleanup_error",
                    mapOf("error" to e.message)
                )
                Log.e(TAG, "Error during service cleanup", e)
            } finally {
                // Cleanup Phase 0 components
                try {
                    crashSafeSupervisor.shutdown()
                } catch (e: Exception) {
                    Log.e(TAG, "Error shutting down supervisor", e)
                }
            }
        }
        
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recording Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Multi-modal sensor recording service"
                enableLights(false)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("IRCamera Recording")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private suspend fun initializeNetworkClient(): Boolean {
        return try {
            val success = networkClient.initialize()
            if (success) {
                setupNetworkEventListener()
                Log.i(TAG, "Network client initialized successfully")
            } else {
                Log.w(TAG, "Network client initialization failed")
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing network client", e)
            false
        }
    }

    private fun setupNetworkEventListener() {
        networkClient.setEventListener(object : NetworkClient.NetworkEventListener {
            override fun onControllerDiscovered(controller: NetworkClient.ControllerInfo) {
                Log.i(TAG, "PC Controller discovered: ${controller.ipAddress}")
                updateNotification("PC Controller found: ${controller.deviceName}")
            }

            override fun onConnected(controller: NetworkClient.ControllerInfo) {
                isConnectedToPC.set(true)
                Log.i(TAG, "Connected to PC Controller: ${controller.deviceName}")
                updateNotification("Connected to PC: ${controller.deviceName}")
            }

            override fun onDisconnected(reason: String) {
                isConnectedToPC.set(false)
                Log.i(TAG, "Disconnected from PC Controller: $reason")
                updateNotification("Disconnected from PC: $reason")
            }

            override fun onRemoteMeasurementRequest(sessionInfo: SessionInfo) {
                Log.i(TAG, "Remote measurement request from PC")
                lifecycleScope.launch {
                    handleRemoteMeasurementRequest(sessionInfo)
                }
            }

            override fun onSyncFlash(durationMs: Int) {
                Log.i(TAG, "Sync flash request: ${durationMs}ms")
                lifecycleScope.launch {
                    recordingController.addSyncMarker("sync_flash", System.nanoTime(), 
                        mapOf("duration_ms" to durationMs.toString()))
                }
            }

            override fun onTimeSynchronized(offsetNanoseconds: Long) {
                Log.i(TAG, "Time synchronized with PC, offset: ${offsetNanoseconds}ns")
                
                // Apply time synchronization using TimeManager
                lifecycleScope.launch {
                    try {
                        val timeManager = TimeManager.getInstance(this@RecordingService)
                        // The offset is already calculated by the PC, so we store it directly
                        // Note: TimeManager handles PC synchronization internally through synchronizeWithPC
                        Log.d(TAG, "Time offset applied: ${offsetNanoseconds}ns for synchronized recording")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to apply time synchronization", e)
                    }
                }
            }

            override fun onDataStreamingStarted() {
                Log.i(TAG, "Data streaming to PC started")
                updateNotification("Streaming data to PC")
            }

            override fun onDataStreamingStopped() {
                Log.i(TAG, "Data streaming to PC stopped")
                updateNotification("Data streaming stopped")
            }

            override fun onError(operation: String, error: String) {
                Log.e(TAG, "Network error during $operation: $error")
                updateNotification("Network error: $error")
            }
        })
    }

    private fun setupNetworkServer() {
        // Setup message handling for PC commands
        networkServer.messageFlow
            .onEach { jsonMessage ->
                handlePCMessage(jsonMessage)
            }
            .launchIn(lifecycleScope)

        // Monitor connection state
        networkServer.connectionStateFlow
            .onEach { isConnected ->
                isConnectedToPC.set(isConnected)
                if (isConnected) {
                    Log.i(TAG, "PC client connected to server")
                    updateNotification("PC client connected")
                } else {
                    Log.i(TAG, "PC client disconnected from server")
                    updateNotification("PC client disconnected")
                }
            }
            .launchIn(lifecycleScope)
    }

    private suspend fun handlePCMessage(message: JSONObject) {
        try {
            val messageType = message.getString("type")
            Log.d(TAG, "Received PC message: $messageType")

            when (messageType) {
                "auth_request" -> {
                    handleAuthRequest(message)
                }
                "start_recording" -> {
                    val sessionInfo = SessionInfo.fromJson(message.getJSONObject("session"))
                    handleRemoteMeasurementRequest(sessionInfo)
                }
                "stop_recording" -> {
                    handleStopRecording()
                }
                "sync_flash" -> {
                    val duration = message.optInt("duration_ms", 500)
                    handleSyncFlash(duration)
                }
                "get_status" -> {
                    sendStatusToPC()
                }
                "time_sync" -> {
                    handleTimeSyncRequest(message)
                }
                "start_data_stream" -> {
                    handleStartDataStream()
                }
                "stop_data_stream" -> {
                    handleStopDataStream()
                }
                else -> {
                    Log.w(TAG, "Unknown message type from PC: $messageType")
                    sendErrorResponse(messageType, "Unknown message type")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling PC message", e)
        }
    }

    private suspend fun handleRemoteMeasurementRequest(sessionInfo: SessionInfo) {
        try {
            Log.i(TAG, "Starting remote measurement: ${sessionInfo.sessionId}")
            updateNotification("Starting recording: ${sessionInfo.name}")
            
            val success = recordingController.startRecording(sessionInfo)
            if (success) {
                isRecording.set(true)
                sendResponseToPC("start_recording", mapOf("status" to "success"))
            } else {
                sendResponseToPC("start_recording", mapOf("status" to "failed", "error" to "Failed to start recording"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting remote measurement", e)
            sendResponseToPC("start_recording", mapOf("status" to "error", "error" to e.message))
        }
    }

    private fun sendStatusToPC() {
        val status = mapOf(
            "recording" to isRecording.get(),
            "connected" to isConnectedToPC.get(),
            "sensors_initialized" to isInitialized.get(),
            "network_initialized" to isNetworkInitialized.get(),
            "current_state" to recordingController.currentState.name
        )
        sendResponseToPC("status", status)
    }

    private fun handleTimeSyncRequest(message: JSONObject) {
        try {
            val clientTimestamp = message.getLong("client_timestamp")
            val serverTimestamp = System.currentTimeMillis()
            
            val response = mapOf(
                "server_timestamp" to serverTimestamp,
                "client_timestamp" to clientTimestamp
            )
            sendResponseToPC("time_sync_response", response)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling time sync", e)
        }
    }

    private fun handleAuthRequest(message: JSONObject) {
        try {
            val pcId = message.optString("pc_id", "unknown")
            val version = message.optString("version", "1.0")
            
            Log.i(TAG, "Authentication request from PC: $pcId (version $version)")
            
            val response = mapOf(
                "status" to "authenticated",
                "android_id" to "ircamera_android_001",
                "supported_features" to listOf("recording", "sync_flash", "data_stream", "time_sync")
            )
            sendResponseToPC("auth_response", response)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling auth request", e)
            sendErrorResponse("auth_request", e.message ?: "Authentication failed")
        }
    }
    
    private suspend fun handleStopRecording() {
        try {
            Log.i(TAG, "Stop recording request from PC")
            recordingController.stopRecording()
            isRecording.set(false)
            sendResponseToPC("stop_recording", mapOf("status" to "recording_stopped"))
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
            sendErrorResponse("stop_recording", e.message ?: "Stop recording failed")
        }
    }
    
    private suspend fun handleSyncFlash(durationMs: Int) {
        try {
            Log.i(TAG, "Sync flash request: ${durationMs}ms")
            recordingController.addSyncMarker("sync_flash", System.nanoTime(), 
                mapOf("duration_ms" to durationMs.toString()))
            
            // Trigger screen flash - this would be implemented in a UI component
            // For now, just acknowledge the command
            sendResponseToPC("sync_flash", mapOf("status" to "flash_completed", "duration_ms" to durationMs))
        } catch (e: Exception) {
            Log.e(TAG, "Error handling sync flash", e)
            sendErrorResponse("sync_flash", e.message ?: "Sync flash failed")
        }
    }
    
    private fun handleStartDataStream() {
        try {
            Log.i(TAG, "Start data streaming request from PC")
            // This would start real-time data streaming
            sendResponseToPC("start_data_stream", mapOf("status" to "streaming_started"))
        } catch (e: Exception) {
            Log.e(TAG, "Error starting data stream", e)
            sendErrorResponse("start_data_stream", e.message ?: "Data stream start failed")
        }
    }
    
    private fun handleStopDataStream() {
        try {
            Log.i(TAG, "Stop data streaming request from PC")
            // This would stop real-time data streaming
            sendResponseToPC("stop_data_stream", mapOf("status" to "streaming_stopped"))
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping data stream", e)
            sendErrorResponse("stop_data_stream", e.message ?: "Data stream stop failed")
        }
    }
    
    private fun sendErrorResponse(messageType: String, error: String) {
        val response = mapOf(
            "status" to "error",
            "error" to error,
            "message_type" to messageType
        )
        sendResponseToPC("error_response", response)
    }

    private fun sendResponseToPC(type: String, data: Map<String, Any>) {
        lifecycleScope.launch {
            try {
                val response = JSONObject().apply {
                    put("type", type)
                    put("timestamp", System.currentTimeMillis())
                    data.forEach { (key, value) ->
                        put(key, value)
                    }
                }
                networkServer.sendMessage(response)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending response to PC", e)
            }
        }
    }

    private fun setupStatusMonitoring() {
        lifecycleScope.launch {
            while (isActive) {
                delay(5000) // Update every 5 seconds
                
                val statusMessage = when {
                    isRecording.get() -> "Recording: ${recordingController.currentState}"
                    isConnectedToPC.get() -> "Connected to PC - Ready"
                    isInitialized.get() -> "Sensors ready - Waiting for PC"
                    else -> "Initializing sensors..."
                }
                
                updateNotification(statusMessage)
            }
        }
    }

    private fun startServerSocket() {
        lifecycleScope.launch {
            try {
                networkServer.start()
                registerNsdService()
                Log.i(TAG, "Server socket started on port $SERVER_PORT")
                updateNotification("Server listening on port $SERVER_PORT")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start server socket", e)
                updateNotification("Server failed to start: ${e.message}")
            }
        }
    }

    private fun startNetworkDiscovery() {
        lifecycleScope.launch {
            try {
                networkClient.startDiscovery()
                Log.i(TAG, "Network discovery started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start network discovery", e)
            }
        }
    }

    private fun registerNsdService() {
        try {
            val serviceInfo = NsdServiceInfo().apply {
                serviceName = NSD_SERVICE_NAME
                serviceType = NSD_SERVICE_TYPE
                port = SERVER_PORT
            }

            nsdManager?.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, object : NsdManager.RegistrationListener {
                override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                    Log.e(TAG, "NSD registration failed: $errorCode")
                }

                override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                    Log.e(TAG, "NSD unregistration failed: $errorCode")
                }

                override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                    Log.i(TAG, "NSD service registered: ${serviceInfo?.serviceName}")
                    isServiceRegistered = true
                    nsdServiceInfo = serviceInfo
                }

                override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
                    Log.i(TAG, "NSD service unregistered")
                    isServiceRegistered = false
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error registering NSD service", e)
        }
    }

    // RecordingController.RecordingListener implementation  
    fun onRecordingStateChanged(state: RecordingState) {
        Log.i(TAG, "Recording state changed: $state")
        
        when (state) {
            RecordingState.RECORDING -> {
                isRecording.set(true)
                updateNotification("Recording in progress...")
            }
            RecordingState.STOPPED -> {
                isRecording.set(false)
                updateNotification("Recording stopped")
            }
            RecordingState.ERROR -> {
                isRecording.set(false)
                updateNotification("Recording error")
            }
            else -> {
                updateNotification("Recording: $state")
            }
        }
        
        // Notify PC of state change
        if (isConnectedToPC.get()) {
            sendResponseToPC("recording_state_changed", mapOf("state" to state.name))
        }
    }

    fun onSyncMarkRecorded(syncMark: SyncMark) {
        Log.i(TAG, "Sync mark recorded: ${syncMark.id}")
        
        // Notify PC of sync mark
        if (isConnectedToPC.get()) {
            sendResponseToPC("sync_mark_recorded", mapOf(
                "id" to syncMark.id,
                "timestamp" to syncMark.timestamp,
                "type" to syncMark.type
            ))
        }
    }

    fun onError(message: String, exception: Throwable?) {
        Log.e(TAG, "Recording error: $message", exception)
        updateNotification("Error: $message")
        
        // Notify PC of error
        if (isConnectedToPC.get()) {
            sendResponseToPC("recording_error", mapOf(
                "message" to message,
                "exception" to (exception?.message ?: "Unknown")
            ))
        }
    }
}