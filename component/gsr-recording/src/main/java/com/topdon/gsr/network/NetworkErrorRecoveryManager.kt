package com.topdon.gsr.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manages network error recovery and retry logic
 */
class NetworkErrorRecoveryManager(private val context: Context) {
    companion object {
        private const val TAG = "NetworkErrorRecoveryManager"
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val MAX_RETRY_DELAY_MS = 30000L
        private const val RAPID_FAILURE_WINDOW_MS = 60000L
        private const val MAX_RAPID_FAILURES = 5
    }

    // Recovery state
    private val isRecovering = AtomicBoolean(false)
    private val currentRetryAttempt = AtomicInteger(0)
    private var lastFailureTime = 0L
    private var rapidFailureCount = 0

    /**
     * Calculate retry delay with exponential backoff
     */
        // Exponential backoff with jitter
        val baseDelay = INITIAL_RETRY_DELAY_MS * (1L shl (attempt - 1))
        val cappedDelay = minOf(baseDelay, MAX_RETRY_DELAY_MS)
        val jitter = (Math.random() * 0.1 * cappedDelay).toLong()
        return cappedDelay + jitter
    }

    private fun isRapidFailure(): Boolean {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastFailureTime > RAPID_FAILURE_WINDOW_MS) {

            rapidFailureCount.set(1)
        } else {
            rapidFailureCount.incrementAndGet()
        }

        lastFailureTime = currentTime
        return rapidFailureCount.get() >= RAPID_FAILURE_THRESHOLD
    }

    /**
     * Reset recovery state (useful after manual intervention)
     */
    fun resetRecoveryState() {
        reconnectionAttempts.set(0)
        rapidFailureCount.set(0)
        lastFailureTime = 0L
        Log.i(TAG, "Recovery state reset")
    }

    /**
     * Get current recovery statistics
     */
    fun getRecoveryStats(): Map<String, Any> {
        return mapOf(
            "recovery_active" to isRecoveryActive.get(),
            "reconnection_attempts" to reconnectionAttempts.get(),
            "rapid_failure_count" to rapidFailureCount.get(),
            "last_failure_time" to lastFailureTime,
            "has_known_good_controller" to (lastKnownGoodController != null),
        )
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        disableAutoRecovery()
        recoveryJob.cancel()
        eventListener = null
        lastKnownGoodController = null
    }
}
