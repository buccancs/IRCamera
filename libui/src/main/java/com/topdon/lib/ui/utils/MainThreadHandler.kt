package com.topdon.lib.ui.utils
import android.os.Handler
import android.os.Looper

/**
 * Thread-safe utility for executing tasks on the main UI thread
 * Provides safe wrapper around Android's main looper handler
 */
object MainThreadHandler {
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Execute runnable on main UI thread immediately
     * @param r Runnable to execute, must not be null
     */
    fun runOnUiThread(r: Runnable?) {
        r?.let { handler.post(it) }
    }

    /**
     * Execute runnable on main UI thread after specified delay
     * @param r Runnable to execute, must not be null
     * @param millis Delay in milliseconds
     */
    fun postDelayed(
        r: Runnable?,
        millis: Long,
    ) {
        r?.let { handler.postDelayed(it, millis) }
    }

    /**
     * Remove pending runnable from execution queue
     * @param r Runnable to remove, must not be null
     */
    fun remove(r: Runnable?) {
        r?.let { handler.removeCallbacks(it) }
    }
}
