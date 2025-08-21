package com.topdon.lib.core.db

import android.content.Context

/**
 * Simplified AppDatabase for component modules
 * Stub implementation for thermal data storage
 */
object AppDatabase {
    
    fun insertThermalEntity(entity: com.topdon.lib.core.db.entity.ThermalEntity) {
        // Simplified implementation - actual database operations would go here
    }
    
    fun insertThermalDayEntity(entity: com.topdon.lib.core.db.entity.ThermalDayEntity) {
        // Simplified implementation - actual database operations would go here
    }
    
    fun getThermalEntities(userId: String): List<com.topdon.lib.core.db.entity.ThermalEntity> {
        // Return empty list for now - actual database query would go here
        return emptyList()
    }
    
    fun init(context: Context) {
        // Database initialization
    }
}