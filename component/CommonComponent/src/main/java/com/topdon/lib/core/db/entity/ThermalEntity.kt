package com.topdon.lib.core.db.entity

/**
 * Thermal entity for database storage
 */
data class ThermalEntity(
    val id: Long = 0,
    val userId: String = "local",
    val thermal: Float = 0f,
    val thermalMax: Float = 0f,
    val thermalMin: Float = 0f,
    val createTime: Long = System.currentTimeMillis(),
    val info: String = "",
    val type: String = "TC001",
    val sn: String = ""
)