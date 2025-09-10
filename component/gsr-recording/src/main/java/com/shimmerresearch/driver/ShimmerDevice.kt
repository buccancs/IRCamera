package com.shimmerresearch.driver

import android.content.Context
import android.os.Handler

    open fun getBatteryLevel(): Double = 100.0

    /**
     * Check if streaming - Official API method
     */
    open fun isStreaming(): Boolean = deviceState == STATE_STREAMING

    /**
     * Set device name - Official API method
     */
    open fun setDeviceName(name: String) {
        // Default implementation - override in subclasses
    }

    /**
     * Read configuration from device - Official API method
     */
    open fun readConfigurationBytes() {
        // Default implementation - override in subclasses
    }

    /**
     * Get configuration bytes - Official API method
     */
    open fun getConfigurationBytes(): ByteArray {
        return ByteArray(CONFIG_SETUP_BYTES_SIZE)
    }

    /**
     * Reset device to defaults - Official API method
     */
    open fun resetToDefaultConfiguration() {
        writeSamplingRate(128.0)
        writeEnabledSensors(0x10L) // GSR sensor
        setGSRRange(GSR_RANGE_AUTO)
    }

    /**
     * Get connection timeout - Official API method
     */
    open fun getConnectionTimeout(): Long = CONNECTION_TIMEOUT_MS

    /**
     * Send message through handler - matches official API pattern
     */
    protected fun sendMessage(
        what: Int,
        arg1: Int,
        arg2: Int,
        obj: Any?,
    ) {
        val message = handler.obtainMessage(what, arg1, arg2, obj)
        handler.sendMessage(message)
    }

    /**
     * Update device state and notify
     */
    protected fun updateState(newState: Int) {
        deviceState = newState
        sendMessage(MESSAGE_STATE_CHANGE, newState, -1, null)
    }
}
