package com.topdon.lib.core.common

/**
 * Temperature listener interface for thermal measurements
 */
interface ITsTempListener {
    fun onTempChanged(temperature: Float)
    fun onTempRangeChanged(minTemp: Float, maxTemp: Float)
    fun onTempMeasureComplete()
    fun onTempError(error: String)
    
    /**
     * Temperature correction method
     */
    fun tempCorrectByTs(temp: Float?): Float {
        return temp ?: 0f
    }
}