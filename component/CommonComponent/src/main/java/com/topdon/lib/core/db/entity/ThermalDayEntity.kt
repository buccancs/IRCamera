package com.topdon.lib.core.db.entity

/**
 * Thermal daily summary entity
 */
data class ThermalDayEntity(
    val id: Long = 0,
    val userId: String = "local",
    val date: String = "",
    val thermalMax: Float = 0f,
    val thermalMin: Float = 0f,
    val createTime: Long = System.currentTimeMillis(),
    val updateTime: Long = System.currentTimeMillis()
)