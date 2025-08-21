package com.topdon.lib.core.db.entity

/**
 * ThermalHourEntity for hourly thermal data storage
 */
class ThermalHourEntity {
    var id: Long = 0
    var userId: String = ""
    var thermal: Float = 0f
    var thermalMax: Float = 0f
    var thermalMin: Float = 0f
    var sn: String = ""
    var info: String = ""
    var type: Int = 0
    var time: Long = 0
    var maxTime: Long = 0
    var createTime: Long = System.currentTimeMillis()
    var updateTime: Long = System.currentTimeMillis()
}