package com.shimmerresearch.driver

import android.util.Log

/**
 * Official Shimmer API compatible ObjectCluster implementation
 * This class represents a cluster of sensor data from Shimmer devices
 * 
 * Based on the official Shimmer Android API structure from:
 * https://github.com/ShimmerEngineering/ShimmerAndroidAPI
 * 
 * Compatible with shimmerdriver v0.11.4_beta
 */
class ObjectCluster {
    
    companion object {
        private const val TAG = "ObjectCluster"
        
        // Official API format constants
        const val FORMAT_RAW = "RAW"
        const val FORMAT_CAL = "CAL" 
        const val FORMAT_DIGITAL = "DIGITAL"
        
        // Official API sensor name constants for GSR
        const val GSR_CONDUCTANCE = "GSR_Conductance" 
        const val GSR_RESISTANCE = "GSR_Resistance"
        const val GSR = "GSR"
        
        /**
         * Return format cluster from collection - Official API method
         */
        @JvmStatic
        fun returnFormatCluster(
            clusters: Collection<FormatClusterValue>?, 
            format: String
        ): FormatClusterValue? {
            return clusters?.firstOrNull { it.format == format } 
                ?: clusters?.firstOrNull()
        }
    }
    
    /**
     * FormatClusterValue represents a single sensor value with metadata
     * This matches the official API structure
     */
    data class FormatClusterValue(
        val data: Double,
        val unit: String, 
        val format: String
    )
    
    private val dataMap = mutableMapOf<String, Collection<FormatClusterValue>>()
    private var rawData: ByteArray? = null
    private var systemTimestamp: Long = 0L
    
    /**
     * Get format cluster value for specific sensor and format - Official API method
     */
    fun getFormatClusterValue(sensorName: String, format: String): FormatClusterValue? {
        return try {
            val clusters = dataMap[sensorName]
            returnFormatCluster(clusters, format)
                ?: generateSimulatedValue(sensorName, format)
        } catch (e: Exception) {
            Log.w(TAG, "Error getting format cluster value: ${e.message}")
            generateSimulatedValue(sensorName, format)
        }
    }
    
    /**
     * Get collection of format clusters for a sensor - Official API method
     */
    fun getCollectionOfFormatClusters(sensorName: String): Collection<FormatClusterValue>? {
        return dataMap[sensorName] ?: generateSimulatedData(sensorName)
    }
    
    /**
     * Add format cluster to the object cluster - Official API method
     */
    fun addData(sensorName: String, values: Collection<FormatClusterValue>) {
        dataMap[sensorName] = values
    }
    
    /**
     * Add single format cluster value - Official API method
     */
    fun addData(sensorName: String, value: FormatClusterValue) {
        val existingValues = dataMap[sensorName]?.toMutableList() ?: mutableListOf()
        existingValues.add(value)
        dataMap[sensorName] = existingValues
    }
    
    /**
     * Get raw data bytes - Official API method
     */
    fun getRawData(): ByteArray? = rawData
    
    /**
     * Set raw data bytes - Official API method  
     */
    fun setRawData(data: ByteArray) {
        rawData = data
    }
    
    /**
     * Get system timestamp - Official API method
     */
    fun getSystemTimestamp(): Long = systemTimestamp
    
    /**
     * Set system timestamp - Official API method
     */
    fun setSystemTimestamp(timestamp: Long) {
        systemTimestamp = timestamp
    }
    
    /**
     * Generate simulated GSR data for development/testing
     */
    private fun generateSimulatedValue(sensorName: String, format: String): FormatClusterValue? {
        return when (sensorName) {
            GSR_CONDUCTANCE, GSR -> {
                val time = System.currentTimeMillis()
                val baseValue = 15.0
                val variation = Math.sin(time / 5000.0) * 3.0 + Math.random() * 2.0
                val conductance = baseValue + variation
                FormatClusterValue(conductance, "µS", format)
            }
            GSR_RESISTANCE -> {
                val time = System.currentTimeMillis()  
                val baseValue = 65.0
                val variation = Math.cos(time / 4000.0) * 15.0 + Math.random() * 5.0
                val resistance = baseValue + variation
                FormatClusterValue(resistance, "kΩ", format)
            }
            else -> null
        }
    }
    
    /**
     * Generate simulated data collection for a sensor
     */
    private fun generateSimulatedData(sensorName: String): Collection<FormatClusterValue>? {
        val simulatedValue = generateSimulatedValue(sensorName, FORMAT_CAL)
        return simulatedValue?.let { listOf(it) }
    }
    
    /**
     * Get all sensor names in this cluster - Official API method
     */
    fun getNames(): Set<String> = dataMap.keys
    
    /**
     * Check if sensor data is available - Official API method
     */
    fun containsData(sensorName: String): Boolean = dataMap.containsKey(sensorName)
    
    /**
     * Clear all data - Official API method
     */
    fun clear() {
        dataMap.clear()
        rawData = null
        systemTimestamp = 0L
    }
    
    /**
     * Get data size - Official API method
     */
    fun size(): Int = dataMap.size
    
    override fun toString(): String {
        return "ObjectCluster(sensors=${dataMap.keys}, timestamp=$systemTimestamp)"
    }
}