package com.topdon.lib.core.messaging

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for reliable message delivery with acknowledgments and retry logic
 */
class ReliableMessageService(private val context: Context) {

    companion object {
        private const val TAG = "ReliableMessageService"
    }

    private val messageScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val pendingMessages = ConcurrentHashMap<String, PendingMessage>()
    private val messageHandlers = ConcurrentHashMap<String, suspend (JSONObject) -> Unit>()
    private var cleanupJob: Job? = null

    fun shutdown() {
        cleanupJob?.cancel()
        messageScope.cancel()
        pendingMessages.clear()
        messageHandlers.clear()
        Log.i(TAG, "Reliable messaging service shutdown")
    }
}
