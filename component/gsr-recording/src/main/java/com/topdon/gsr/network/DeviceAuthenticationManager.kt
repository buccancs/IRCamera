package com.topdon.gsr.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.security.SecureRandom
import java.time.Instant
import java.util.*

/**
 * Manages device authentication with PC Controller
 */
class DeviceAuthenticationManager(private val context: Context) {
    companion object {
        private const val TAG = "DeviceAuthenticationManager"
        private const val PREFS_NAME = "device_auth"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_PAIRED_CONTROLLERS = "paired_controllers"
        private const val KEY_AUTH_TOKENS = "auth_tokens"
        private const val KEY_PAIRING_PIN = "pairing_pin"
        private const val TOKEN_VALIDITY_HOURS = 24
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val deviceId: String = getOrCreateDeviceId()
    private val authTokens = mutableMapOf<String, AuthToken>()
    private var deviceToken: String? = null
    private var authEventListener: AuthEventListener? = null

    /**
     * Process pairing response from PC Controller
     */
    fun processPairingResponse(response: JSONObject): Boolean {
        try {
            val success = response.getBoolean("success")
            val controllerId = response.getString("controller_id")

            if (success) {
                // Store paired controller
                val pairedControllers = getPairedControllers().toMutableSet()
                pairedControllers.add(controllerId)
                storePairedControllers(pairedControllers)

                if (response.has("auth_token")) {
                    val tokenData = response.getJSONObject("auth_token")
                    val authToken =
                        AuthToken(
                            token = tokenData.getString("token"),
                            deviceId = deviceId!!,
                            issuedAt = tokenData.getLong("issued_at"),
                            expiresAt = tokenData.getLong("expires_at"),
                            controllerId = controllerId,
                            permissions =
                                tokenData.getJSONArray("permissions").let { array ->
                                    (0 until array.length()).map { array.getString(it) }
                                },
                        )

                    storeAuthToken(controllerId, authToken)
                    authEventListener?.onAuthTokenReceived(authToken)
                }

                authEventListener?.onPairingCompleted(controllerId, true)
                Log.d(TAG, "Pairing completed successfully with controller: $controllerId")
                return true
            } else {
                val reason = response.optString("reason", "Unknown error")
                authEventListener?.onPairingCompleted(controllerId, false)
                Log.w(TAG, "Pairing failed with controller $controllerId: $reason")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process pairing response", e)
            return false
        }
    }

    fun getPairedControllers(): Set<String> {
        val pairedJson = prefs.getString(KEY_PAIRED_CONTROLLERS, "[]")
        return try {
            val array = org.json.JSONArray(pairedJson)
            (0 until array.length()).map { array.getString(it) }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    /**
     * Store list of paired controllers
     */
    private fun storePairedControllers(controllers: Set<String>) {
        val array = org.json.JSONArray()
        controllers.forEach { array.put(it) }
        prefs.edit().putString(KEY_PAIRED_CONTROLLERS, array.toString()).apply()
    }

    /**
     * Remove paired controller
     */
    fun unpairController(controllerId: String) {
        val pairedControllers = getPairedControllers().toMutableSet()
        pairedControllers.remove(controllerId)
        storePairedControllers(pairedControllers)
        removeAuthToken(controllerId)
        Log.d(TAG, "Unpaired controller: $controllerId")
    }

    /**
     * Clear all pairing data
     */
    fun clearAllPairings() {
        val pairedControllers = getPairedControllers()
        pairedControllers.forEach { removeAuthToken(it) }
        storePairedControllers(emptySet())
        prefs.edit().remove(KEY_PAIRING_PIN).apply()
        Log.d(TAG, "Cleared all pairing data")
    }

    /**
     * Get device name for pairing
     */
    private fun getDeviceName(): String {
        return android.os.Build.MODEL + " (" + android.os.Build.DEVICE + ")"
    }

    /**
     * Get current device ID
     */
    fun getDeviceId(): String? = deviceId

    /**
     * Get current device token
     */
    fun getDeviceToken(): String? = deviceToken

    /**
     * Check if device is paired with any controller
     */
    fun isPaired(): Boolean = getPairedControllers().isNotEmpty()

    /**
     * Check if device is paired with specific controller
     */
    fun isPairedWith(controllerId: String): Boolean = controllerId in getPairedControllers()

    /**
     * Get or create device ID
     */
    private fun getOrCreateDeviceId(): String {
        val existingId = prefs.getString(KEY_DEVICE_ID, null)
        if (existingId != null) return existingId
        
        val newId = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DEVICE_ID, newId).apply()
        return newId
    }

    /**
     * Generate pairing PIN
     */
    fun generatePairingPin(): String {
        val pin = (100000..999999).random().toString()
        prefs.edit().putString(KEY_PAIRING_PIN, pin).apply()
        return pin
    }

    /**
     * Get current pairing PIN
     */
    fun getCurrentPairingPin(): String? {
        return prefs.getString(KEY_PAIRING_PIN, null)
    }

    /**
     * Create pairing request
     */
    fun createPairingRequest(): PairingRequest {
        return PairingRequest(
            deviceId = deviceId,
            deviceName = getDeviceName(),
            deviceType = "android_spoke",
            pairingPin = getCurrentPairingPin() ?: generatePairingPin(),
            timestamp = System.currentTimeMillis(),
            capabilities = listOf("rgb", "thermal", "gsr")
        )
    }

    /**
     * Create authenticated message
     */
    fun createAuthenticatedMessage(messageType: String, data: JSONObject, controllerId: String): JSONObject {
        val authToken = getAuthToken(controllerId)
        return JSONObject().apply {
            put("message_type", messageType)
            put("controller_id", controllerId)
            put("device_id", deviceId)
            put("auth_token", authToken?.token)
            put("timestamp", System.currentTimeMillis())
            put("data", data)
        }
    }

    /**
     * Validate message authentication
     */
    fun validateMessageAuthentication(message: JSONObject, controllerId: String): Boolean {
        return try {
            val authToken = getAuthToken(controllerId)
            val messageToken = message.optString("auth_token")
            authToken?.token == messageToken && !isTokenExpired(authToken)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get auth token for controller
     */
    fun getAuthToken(controllerId: String): AuthToken? {
        return authTokens[controllerId]
    }

    /**
     * Store auth token
     */
    private fun storeAuthToken(controllerId: String, authToken: AuthToken) {
        authTokens[controllerId] = authToken
        // Store persistently
        val tokensJson = JSONObject()
        authTokens.forEach { (id, token) ->
            tokensJson.put(id, JSONObject().apply {
                put("token", token.token)
                put("device_id", token.deviceId)
                put("issued_at", token.issuedAt)
                put("expires_at", token.expiresAt)
                put("controller_id", token.controllerId)
                put("permissions", org.json.JSONArray(token.permissions))
            })
        }
        prefs.edit().putString(KEY_AUTH_TOKENS, tokensJson.toString()).apply()
    }

    /**
     * Remove auth token
     */
    private fun removeAuthToken(controllerId: String) {
        authTokens.remove(controllerId)
        // Update persistent storage
        val tokensJson = JSONObject()
        authTokens.forEach { (id, token) ->
            tokensJson.put(id, JSONObject().apply {
                put("token", token.token)
                put("device_id", token.deviceId)
                put("issued_at", token.issuedAt)
                put("expires_at", token.expiresAt)
                put("controller_id", token.controllerId)
                put("permissions", org.json.JSONArray(token.permissions))
            })
        }
        prefs.edit().putString(KEY_AUTH_TOKENS, tokensJson.toString()).apply()
    }

    /**
     * Check if token is expired
     */
    private fun isTokenExpired(authToken: AuthToken): Boolean {
        return System.currentTimeMillis() > authToken.expiresAt
    }
}

/**
 * Pairing request data class
 */
data class PairingRequest(
    val deviceId: String,
    val deviceName: String,
    val deviceType: String,
    val pairingPin: String,
    val timestamp: Long,
    val capabilities: List<String>
)

/**
 * Data class for authentication token
 */
data class AuthToken(
    val token: String,
    val deviceId: String,
    val issuedAt: Long,
    val expiresAt: Long,
    val controllerId: String,
    val permissions: List<String>
)

/**
 * Auth event listener interface
 */
interface AuthEventListener {
    fun onPairingCompleted(controllerId: String, success: Boolean)
    fun onAuthTokenReceived(authToken: AuthToken)
}
