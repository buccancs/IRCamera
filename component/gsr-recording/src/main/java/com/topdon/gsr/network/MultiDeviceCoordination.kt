package com.topdon.gsr.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

    suspend fun initializeCoordination(sessionId: String) =
        withContext(Dispatchers.IO) {
            currentSessionId = sessionId
            isCoordinating.set(true)

            startDeviceDiscovery()

            initiateLeaderElection()

            startSynchronizationLoop()

            Log.d(TAG, "Multi-device coordination initialized for session: $sessionId")
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
        coordinationJob.cancel()

        Log.d(TAG, "Multi-device coordination stopped")
    }
}
