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
        private const val TOKEN_VALIDITY_HOURS = 24
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val deviceId: String? = getOrCreateDeviceId()
    private val authTokens = mutableMapOf<String, AuthToken>()

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
        prefs.edit().putString(PREF_PAIRED_CONTROLLERS, array.toString()).apply()
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
        prefs.edit().remove(PREF_PAIRING_PIN).apply()
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
     * Get paired controllers
     */
    private fun getPairedControllers(): Set<String> {
        val controllersJson = prefs.getString(KEY_PAIRED_CONTROLLERS, "[]")
        return try {
            val jsonArray = org.json.JSONArray(controllersJson!!)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    /**
     * Store paired controllers
     */
    private fun storePairedControllers(controllers: Set<String>) {
        val jsonArray = org.json.JSONArray()
        controllers.forEach { jsonArray.put(it) }
        prefs.edit().putString(KEY_PAIRED_CONTROLLERS, jsonArray.toString()).apply()
    }
}

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
