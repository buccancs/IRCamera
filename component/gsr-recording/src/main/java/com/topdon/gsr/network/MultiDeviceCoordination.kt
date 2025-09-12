package com.topdon.gsr.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Coordinates multiple device synchronization and recording
 */
class MultiDeviceCoordination(
    private val context: Context,
    private val networkClient: NetworkClient
) {
    companion object {
        private const val TAG = "MultiDeviceCoordination"
        private const val SYNC_INTERVAL_MS = 1000L
        private const val LEADER_ELECTION_TIMEOUT_MS = 5000L
    }

    // State management
    private val isCoordinating = AtomicBoolean(false)
    private val isLeader = AtomicBoolean(false)
    
    // Device tracking
    private val connectedDevices = ConcurrentHashMap<String, DeviceInfo>()
    private var currentSessionId: String? = null
    private val deviceId = android.provider.Settings.Secure.getString(
        context.contentResolver,
        android.provider.Settings.Secure.ANDROID_ID
    )
    
    // Synchronization
    private val syncEvents = ConcurrentHashMap<String, SyncEvent>()
    private var syncJob: Job? = null
    
    // Coroutine scope
    private val coordinationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Initialize coordination for multi-device recording
     */
    suspend fun initializeCoordination(sessionId: String) {
        withContext(Dispatchers.IO) {
            currentSessionId = sessionId
            isCoordinating.set(true)

            startDeviceDiscovery()

            initiateLeaderElection()

            startSynchronizationLoop()

            Log.d(TAG, "Multi-device coordination initialized for session: $sessionId")
        }
    }

    private fun handleDeviceDiscoveryRequest(message: JSONObject) {
        val response =
            JSONObject().apply {
                put("type", "device_discovery_response")
                put("device_id", deviceId)
                put("device_name", android.os.Build.MODEL)
                put("capabilities", JSONArray(listOf("gsr", "thermal", "rgb_camera")))
                put("session_id", currentSessionId)
                put("clock_offset", networkClient.getClockOffset())
                put("battery_level", getBatteryLevel())
                put("timestamp", System.currentTimeMillis())
            }

        coordinationScope.launch {
            networkClient.sendMessage(response)
        }
    }

    private fun handleLeaderElection(message: JSONObject) {
        val remoteDeviceId = message.optString("device_id")
        val remotePriority = message.optInt("priority", 0)
        val myPriority = calculateLeadershipPriority()

        if (remotePriority > myPriority ||
            (remotePriority == myPriority && remoteDeviceId < deviceId)
        ) {
            // Remote device has higher priority or tiebreaker
            isLeader.set(false)
        }
    }

    suspend fun scheduleCoordinatedEvent(
        eventType: CoordinationEvent,
        delayMs: Long = 1000L,
    ): String =
        withContext(Dispatchers.IO) {
            val eventId = generateEventId(eventType.eventType)
            val scheduledTime = System.currentTimeMillis() + delayMs

            val syncEvent =
                SyncEvent(
                    eventId = eventId,
                    eventType = eventType.eventType,
                    scheduledTime = scheduledTime,
                )

            syncEvents[eventId] = syncEvent

            // Broadcast event to all devices
            val eventMessage =
                JSONObject().apply {
                    put("type", "coordination_event")
                    put("event_id", eventId)
                    put("event_type", eventType.eventType)
                    put("scheduled_time", scheduledTime)
                    put("session_id", currentSessionId)
                }

            networkClient.broadcastMessage(eventMessage)

            Log.d(TAG, "Scheduled coordinated event: ${eventType.eventType} at $scheduledTime")
            eventId
        }

    fun stopCoordination() {
        isCoordinating.set(false)
        isLeader.set(false)
        syncJob?.cancel()
        connectedDevices.clear()
        syncEvents.clear()
        coordinationScope.cancel()

        Log.d(TAG, "Multi-device coordination stopped")
    }
    
    /**
     * Calculate leadership priority
     */
    private fun calculateLeadershipPriority(): Int {
        // Simple priority based on device capabilities and battery level
        val batteryLevel = getBatteryLevel()
        val hasHighCapabilities = context.packageManager.hasSystemFeature("android.hardware.camera")
        
        return batteryLevel + if (hasHighCapabilities) 50 else 0
    }
    
    /**
     * Get battery level
     */
    private fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
        return batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
    
    /**
     * Generate event ID
     */
    private fun generateEventId(eventType: String): String {
        return "$eventType-${System.currentTimeMillis()}-${deviceId.takeLast(4)}"
    }
    
    /**
     * Start device discovery
     */
    private suspend fun startDeviceDiscovery() {
        // Implementation for device discovery
    }

    /**
     * Initiate leader election
     */
    private suspend fun initiateLeaderElection() {
        // Implementation for leader election
    }

    /**
     * Start synchronization loop
     */
    private suspend fun startSynchronizationLoop() {
        // Implementation for sync loop
    }
}

/**
 * Device information data class
 */
data class DeviceInfo(
    val deviceId: String,
    val deviceType: String,
    val ipAddress: String,
    val capabilities: List<String>,
    val lastSeen: Long
)

/**
 * Coordination event types
 */
enum class CoordinationEvent(val eventType: String) {
    START_RECORDING("start_recording"),
    STOP_RECORDING("stop_recording"),
    SYNC_FLASH("sync_flash"),
    PAUSE_RECORDING("pause_recording"),
    RESUME_RECORDING("resume_recording"),
    CALIBRATION("calibration")
}

/**
 * Synchronization event data class
 */
data class SyncEvent(
    val eventId: String,
    val eventType: String,
    val scheduledTime: Long,
    val status: SyncEventStatus = SyncEventStatus.SCHEDULED
)

/**
 * Sync event status
 */
enum class SyncEventStatus {
    SCHEDULED, EXECUTED, CANCELLED, FAILED
}
