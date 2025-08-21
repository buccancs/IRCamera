package com.topdon.lib.core.bean.tools

/**
 * Thermal data bean for temperature measurements
 */
data class ThermalBean(
    val temperature: Float,
    val x: Int,
    val y: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedTemperature(): String {
        return String.format("%.1f°C", temperature)
    }
}