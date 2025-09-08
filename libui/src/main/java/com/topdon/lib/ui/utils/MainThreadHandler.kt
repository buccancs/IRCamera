package com.topdon.lib.ui.utils
import android.os.Handler
import android.os.Looper

object MainThreadHandler {
    private val handler = Handler(Looper.getMainLooper())
    /**
     * Function description.
     */
    fun runOnUiThread(r: Runnable?) {
        handler.post(r!!)
    }

    /**
     * Function description.
     */
    fun postDelayed(r: Runnable?, millis: Long) {
        handler.postDelayed(r!!, millis)
    }

    /**
     * Function description.
     */
    fun remove(r: Runnable?) {
        handler.removeCallbacks(r!!)
    }
}