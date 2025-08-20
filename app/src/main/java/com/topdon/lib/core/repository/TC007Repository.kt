package com.topdon.lib.core.repository

/**
 * Stub implementation for removed TC007Repository
 * All methods return default values to maintain compatibility
 */
object TC007Repository {
    
    var netWork: Any? = null
    
    suspend fun getProductInfo(): ProductBean? = null
    
    suspend fun updateFirmware(file: Any): Boolean = false
    
    suspend fun resetToFactory(): Boolean = false
    
    suspend fun setIRConfig(environment: Any, distance: Any, radiation: Any): Boolean = false
    
    suspend fun syncTime(): Boolean = false
    
    suspend fun getBatteryInfo(): BatteryInfo? = null
}

// Stub ProductBean class
class ProductBean {
    fun getVersionStr(): String = "N/A"
}

// Stub BatteryInfo class
class BatteryInfo {
    val level: Int = 0
    val isCharging: Boolean = false
}