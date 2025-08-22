package com.topdon.lib.core.repository

/**
 * Battery information data class for TC007 device
 */
data class BatteryInfo(
    val status: String,
    val remaining: String
)