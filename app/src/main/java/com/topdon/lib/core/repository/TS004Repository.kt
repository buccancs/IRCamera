package com.topdon.lib.core.repository

import java.io.File

/**
 * Stub implementation for removed TS004Repository  
 * All methods return default values to maintain compatibility
 */
object TS004Repository {
    
    suspend fun getDeviceInfo(): Any? = null
    
    suspend fun syncTime(): Boolean = false
    
    suspend fun getTISR(): Any? = null
    
    suspend fun setTISR(state: Any): Boolean = false
    
    suspend fun getFreeSpace(): Any? = null
    
    suspend fun getFormatStorage(): Boolean = false
    
    suspend fun updateFirmware(file: Any): Boolean = false
    
    suspend fun getVersion(): Any? = null
    
    suspend fun getResetAll(): Boolean = false
    
    suspend fun deleteFiles(files: Array<Any>): Boolean = false
    
    suspend fun download(path: String, file: File): Boolean = false
}