package com.topdon.module.thermal.ir.view.compass

import android.content.Context
import android.hardware.SensorManager

/**
 * Sensor background service for thermal imaging operations.
 * Performs long-running thermal data processing tasks.
 */
class SensorService(ctx: Context) {
    private var context = ctx.applicationContext

    fun hasCompass(): Boolean {
        return false // Minimal implementation
    }

    fun getCompass(): Any? {
        return null // Minimal implementation
    }

    companion object {
        const val MOTION_SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME
        const val ENVIRONMENT_SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL
    }
}