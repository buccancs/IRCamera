package com.topdon.lib.ui.bean

/**
 * Temperature data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
data class TemperatureBean(
    val res: Int,
    val name: String,
    val info: String,
    val code: Int,
)
