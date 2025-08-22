package com.topdon.lib.core.repository

/**
 * Repository for TC007 device operations
 */
object TC007Repository {
    
    suspend fun getBatteryInfo(): BatteryInfo? {
        // Stub implementation - returns null as TC007 support is removed
        return null
    }
    
    fun setIRConfig(environment: Float, distance: Float, radiation: Float) {
        // Stub implementation - TC007 configuration removed
    }
}