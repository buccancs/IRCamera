package com.shimmer.driver

/**
 * Stub implementation of ObjectCluster for compilation
 * This should be replaced with the actual Shimmer Android API
 */
class ObjectCluster {
    
    class FormatClusterValue(val data: Double, val unit: String, val format: String)
    
    fun getFormatClusterValue(sensorName: String, format: String): FormatClusterValue? {
        // Stub implementation - returns simulated GSR data
        return when (sensorName) {
            "GSR_Conductance", "GSR" -> FormatClusterValue(15.0 + Math.random() * 5.0, "µS", format)
            "GSR_Resistance" -> FormatClusterValue(60.0 + Math.random() * 20.0, "kΩ", format)
            else -> null
        }
    }
}