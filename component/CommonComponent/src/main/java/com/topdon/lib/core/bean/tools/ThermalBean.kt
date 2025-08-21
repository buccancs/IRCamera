package com.topdon.lib.core.bean.tools

/**
 * Thermal data bean for temperature measurements
 */
class ThermalBean {
    var centerTemp: Float = 0f
    var maxTemp: Float = 0f  
    var minTemp: Float = 0f
    var createTime: Long = System.currentTimeMillis()
    var temperature: Float = 0f
    var x: Int = 0
    var y: Int = 0
    var timestamp: Long = System.currentTimeMillis()

    constructor()
    
    constructor(temperature: Float, x: Int, y: Int, timestamp: Long = System.currentTimeMillis()) {
        this.temperature = temperature
        this.centerTemp = temperature
        this.x = x
        this.y = y
        this.timestamp = timestamp
        this.createTime = timestamp
    }
    
    fun getFormattedTemperature(): String {
        return String.format("%.1f°C", temperature)
    }
}