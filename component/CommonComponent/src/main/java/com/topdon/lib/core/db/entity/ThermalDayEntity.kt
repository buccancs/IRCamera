package com.topdon.lib.core.db.entity

/**
 * Thermal daily summary entity
 */
class ThermalDayEntity {
    var id: Long = 0
    var userId: String = "local"
    var date: String = ""
    var thermal: Float = 0f
    var thermalMax: Float = 0f
    var thermalMin: Float = 0f
    var createTime: Long = System.currentTimeMillis()
    var updateTime: Long = System.currentTimeMillis()
    var info: String = ""
    var type: Int = 0
    var sn: String = ""
}