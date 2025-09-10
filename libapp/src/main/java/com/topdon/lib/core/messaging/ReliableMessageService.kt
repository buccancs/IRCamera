package com.topdon.lib.core.messaging

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

    fun shutdown() {
        cleanupJob?.cancel()
        messageScope.cancel()
        pendingMessages.clear()
        messageHandlers.clear()
        Log.i(TAG, "Reliable messaging service shutdown")
    }
}
