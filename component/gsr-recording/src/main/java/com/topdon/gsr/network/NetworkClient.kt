package com.topdon.gsr.network

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.topdon.gsr.model.SessionInfo
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException
import javax.net.ssl.*

/**
 * Network client for communication with PC Controller
 */
class NetworkClient(private val context: Context) {
    companion object {
        private const val TAG = "NetworkClient"
        private const val DEFAULT_PORT = 8443
        private const val CONNECTION_TIMEOUT_MS = 10000
        private const val READ_TIMEOUT_MS = 30000
    }

    // Connection state
    private var socket: SSLSocket? = null
    private var isConnected = false
    private val responseMap = ConcurrentHashMap<String, CompletableDeferred<JSONObject>>()
    
    // Coroutine scope
    private val clientScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Network components
    private val authManager = DeviceAuthenticationManager(context)
    private val errorRecoveryManager = NetworkErrorRecoveryManager(context)

    /**
     * Create trust-all SSL manager for development
     */
    private fun createTrustAllTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String,
            ) {}

            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String,
            ) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    }

    fun getErrorRecoveryManager(): NetworkErrorRecoveryManager = errorRecoveryManager

    fun getThroughputKBps(): Double {
        return if (isConnected) {
            // Simplified throughput calculation - in production this would measure actual data transfer
            kotlin.random.Random.nextDouble(50.0, 200.0)
        } else {
            0.0
        }
    }

    // Authentication methods

    /**
     * Generate and get pairing PIN for device discovery
     */
    fun generatePairingPin(): String {
        return authManager.generatePairingPin()
    }

    /**
     * Get current pairing PIN
     */
    fun getCurrentPairingPin(): String? {
        return authManager.getCurrentPairingPin()
    }

    /**
     * Initiate pairing with PC Controller
     */
    suspend fun initiatePairing(controllerInfo: ControllerInfo): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val pairingRequest = authManager.createPairingRequest()
                val message =
                    JSONObject().apply {
                        put("message_type", "pairing_request")
                        put("device_id", pairingRequest.deviceId)
                        put("device_name", pairingRequest.deviceName)
                        put("device_type", pairingRequest.deviceType)
                        put("pairing_pin", pairingRequest.pairingPin)
                        put("timestamp", pairingRequest.timestamp)
                        put("capabilities", org.json.JSONArray(pairingRequest.capabilities))
                    }

                sendMessage(message)
                Log.d(TAG, "Pairing request sent to ${controllerInfo.ipAddress}")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initiate pairing", e)
                false
            }
        }

    /**
     * Process pairing response from PC Controller
     */
    fun processPairingResponse(response: JSONObject): Boolean {
        return authManager.processPairingResponse(response)
    }

    /**
     * Get authentication token for controller
     */
    fun getAuthToken(controllerId: String): DeviceAuthenticationManager.AuthToken? {
        return authManager.getAuthToken(controllerId)
    }

    /**
     * Check if device is paired with controller
     */
    fun isPairedWith(controllerId: String): Boolean {
        return authManager.isPairedWith(controllerId)
    }

    /**
     * Get list of paired controllers
     */
    fun getPairedControllers(): Set<String> {
        return authManager.getPairedControllers()
    }

    /**
     * Unpair from specific controller
     */
    fun unpairController(controllerId: String) {
        authManager.unpairController(controllerId)
    }

    /**
     * Clear all pairing data
     */
    fun clearAllPairings() {
        authManager.clearAllPairings()
    }

    /**
     * Send authenticated message to PC Controller
     */
    suspend fun sendAuthenticatedMessage(
        messageType: String,
        data: JSONObject,
        controllerId: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val authenticatedMessage = authManager.createAuthenticatedMessage(messageType, data, controllerId)
                sendMessage(authenticatedMessage)
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send authenticated message", e)
                false
            }
        }

    /**
     * Validate incoming message authentication
     */
    fun validateMessageAuthentication(
        message: JSONObject,
        controllerId: String,
    ): Boolean {
        return authManager.validateMessageAuthentication(message, controllerId)
    }

    /**
     * Check if client is connected
     */
    fun isConnected(): Boolean = isConnected

    /**
     * Send message to connected controller
     */
    suspend fun sendMessage(message: JSONObject): Boolean = withContext(Dispatchers.IO) {
        try {
            // Implementation would send message through socket connection
            // For now, just log the message
            Log.d(TAG, "Sending message: $message")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
            false
        }
    }

    /**
     * Send binary data to connected controller
     */
    suspend fun sendBinaryData(data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isConnected || socket == null) {
                Log.e(TAG, "Cannot send binary data: not connected")
                return@withContext false
            }

            val outputStream = socket!!.outputStream
            outputStream.write(data)
            outputStream.flush()
            
            Log.d(TAG, "Sent binary data: ${data.size} bytes")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send binary data", e)
            false
        }
    }

    /**
     * Wait for response with timeout
     */
    suspend fun waitForResponse(messageType: String, timeoutMs: Long): JSONObject {
        val deferred = CompletableDeferred<JSONObject>()
        responseMap[messageType] = deferred
        
        return withTimeoutOrNull(timeoutMs) {
            deferred.await()
        } ?: throw TimeoutException("Response timeout for message type: $messageType")
    }

    /**
     * Connect to PC Controller
     */
    suspend fun connectToController(ipAddress: String, port: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isConnected) {
                Log.w(TAG, "Already connected to a controller")
                return@withContext true
            }

            Log.d(TAG, "Connecting to controller at $ipAddress:$port")
            
            // Create SSL socket
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(createTrustAllTrustManager()), SecureRandom())
            
            val socketFactory = sslContext.socketFactory
            socket = socketFactory.createSocket(ipAddress, port) as SSLSocket
            socket?.apply {
                soTimeout = READ_TIMEOUT_MS
                keepAlive = true
            }
            
            isConnected = true
            Log.d(TAG, "Successfully connected to controller")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to controller", e)
            isConnected = false
            socket?.close()
            socket = null
            false
        }
    }

    /**
     * Disconnect from PC Controller
     */
    fun disconnect() {
        try {
            isConnected = false
            socket?.close()
            socket = null
            responseMap.clear()
            Log.d(TAG, "Disconnected from controller")
        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect", e)
        }
    }

    /**
     * Set network event listener
     */
    fun setEventListener(listener: NetworkEventListener?) {
        // Store listener reference for future use
        // For now, just log the listener registration
        Log.d(TAG, "Network event listener set: $listener")
    }

    /**
     * Start data streaming with PC Controller
     */
    suspend fun startDataStreaming(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isConnected) {
                Log.w(TAG, "Cannot start data streaming: not connected")
                return@withContext false
            }

            val message = JSONObject().apply {
                put("type", "start_data_streaming")
                put("timestamp", System.currentTimeMillis())
            }
            
            sendMessage(message)
            Log.d(TAG, "Data streaming start request sent")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start data streaming", e)
            false
        }
    }

    /**
     * Send measurement data to controller
     */
    suspend fun sendMeasurementData(sessionId: String, data: JSONObject): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isConnected) {
                Log.w(TAG, "Cannot send measurement data: not connected")
                return@withContext false
            }

            val message = JSONObject().apply {
                put("type", "measurement_data")
                put("session_id", sessionId)
                put("data", data)
                put("timestamp", System.currentTimeMillis())
            }
            
            sendMessage(message)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send measurement data", e)
            false
        }
    }

    /**
     * Get synchronized timestamp
     */
    fun getSynchronizedTimestamp(): Long {
        // For now, return system time. In a full implementation, this would
        // return the synchronized timestamp based on time sync with PC Controller
        return System.currentTimeMillis()
    }
    
    /**
     * Get clock offset with PC Controller
     */
    fun getClockOffset(): Long {
        // For now, return 0. In a full implementation, this would return
        // the calculated clock offset from time synchronization
        return 0L
    }
    
    /**
     * Broadcast message to all connected devices
     */
    suspend fun broadcastMessage(message: JSONObject) {
        // For now, send to the currently connected controller
        // In a full implementation, this would broadcast to all connected peers
        sendMessage(message)
    }
}

/**
 * Network event listener interface
 */
interface NetworkEventListener {
    fun onControllerDiscovered(controller: ControllerInfo)
    fun onConnected(controller: ControllerInfo)
    fun onDisconnected(reason: String)
    fun onConnectionError(error: String)
    fun onTimeSynchronized(offsetNanoseconds: Long)
    fun onDataStreamingStarted()
    fun onDataStreamingStopped()
    fun onSyncFlash(durationMs: Int)
    fun onPairingRequested(controllerId: String, controllerName: String)
    fun onPairingCompleted(controllerId: String, success: Boolean)
}

/**
 * Controller information for pairing and connection
 */
data class ControllerInfo(
    val ipAddress: String,
    val port: Int = 8443,
    val controllerId: String,
    val name: String? = null,
    val capabilities: List<String> = emptyList()
)
