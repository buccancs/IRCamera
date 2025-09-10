package com.topdon.tc001.network

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.topdon.gsr.model.SessionInfo
import com.topdon.lib.core.discovery.NetworkDiscoveryService
import com.topdon.lib.core.messaging.ReliableMessageService
import com.topdon.lib.core.security.CertificateManager
import com.topdon.lib.core.sync.TimeSyncService
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import javax.net.ssl.*

    fun getErrorRecoveryManager(): NetworkErrorRecoveryManager = errorRecoveryManager

    /**
     * Start device discovery with callback-based result handling
     */
    fun startDiscovery(callback: (Boolean) -> Unit) {
        heartbeatScope.launch {
            try {
                discoverControllers()
                callback(true)
            } catch (e: Exception) {
                Log.e(TAG, "Discovery failed", e)
                callback(false)
            }
        }
    }

    /**
     * Connect to controller with callback-based result handling
     */
    fun connectToController(
        address: String,
        port: Int,
        callback: (Boolean) -> Unit,
    ) {
        heartbeatScope.launch {
            try {
                val result = connectToController(address, port, useSecureDefault)
                callback(result)
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed", e)
                callback(false)
            }
        }
    }

    /**
     * Get current network latency in milliseconds
     */
    fun getLatencyMs(): Long {
        return errorRecoveryManager.getAverageLatency()
    }

    /**
     * Get current throughput in KB/s
     */
    fun getThroughputKBps(): Double {
        return errorRecoveryManager.getThroughputKBps()
    }

    /**
     * Cleanup all resources
     */
    fun cleanup() {
        disconnect()
        discoveryService.cleanup()
        timeSyncService.cleanup()
        reliableMessaging.shutdown()
        errorRecoveryManager.cleanup()
        discoveredControllers.clear()
        eventListener = null
    }
}
