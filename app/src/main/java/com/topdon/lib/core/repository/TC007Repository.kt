package com.topdon.lib.core.repository

/**
 * Stub implementation for removed TC007Repository
 * All methods return default values to maintain compatibility
 */
object TC007Repository {
    
    suspend fun getProductInfo(): ProductBean? = null
    
    suspend fun updateFirmware(file: Any): Boolean = false
    
    suspend fun resetToFactory(): Boolean = false
    
    // Add other methods as needed to satisfy compilation
}

// Stub ProductBean class
class ProductBean {
    fun getVersionStr(): String = "N/A"
}