package com.topdon.lib.core.db.entity

/**
 * Thermal entity for database storage
 */
class ThermalEntity {
    var id: Long = 0
    var userId: String = "local"
    var thermal: Float = 0f
    var thermalMax: Float = 0f
    var thermalMin: Float = 0f
    var createTime: Long = System.currentTimeMillis()
    var info: String = ""
    var type: Int = 0
    var sn: String = ""
}