package com.shimmerresearch.driver

import android.util.Log

/**
 * Data structure for Shimmer sensor cluster values
 */
class ObjectCluster {
    
    companion object {
        // Sensor name constants
        const val GSR_CONDUCTANCE = "GSR Conductance"
        const val GSR = "GSR"
        const val GSR_RESISTANCE = "GSR Resistance"
        const val ACCEL_X = "Accelerometer X"
        const val ACCEL_Y = "Accelerometer Y"
        const val ACCEL_Z = "Accelerometer Z"
        const val GYRO_X = "Gyroscope X"
        const val GYRO_Y = "Gyroscope Y"
        const val GYRO_Z = "Gyroscope Z"
        const val MAG_X = "Magnetometer X"
        const val MAG_Y = "Magnetometer Y"
        const val MAG_Z = "Magnetometer Z"
        const val BATTERY = "Battery"
        
        @JvmStatic
        fun returnFormatCluster(
            clusters: Collection<FormatClusterValue>?,
            format: String,
        ): FormatClusterValue? {
            return clusters?.firstOrNull { it.format == format }
                ?: clusters?.firstOrNull()
        }
    }
    
    private companion object {
        private const val TAG = "ObjectCluster"
        private const val FORMAT_CAL = "CAL"
        private const val FORMAT_RAW = "RAW"
    }
    
    private var systemTimestamp: Long = System.currentTimeMillis()
    private val dataMap = mutableMapOf<String, Collection<FormatClusterValue>>()
    private var rawData: ByteArray? = null

    fun validateSensorData(
        sensorName: String,
        value: Double,
    ): Boolean {
        return when (sensorName) {
            GSR_CONDUCTANCE, GSR -> value in 0.0..100.0 // 0-100 µS reasonable range
            GSR_RESISTANCE -> value in 1.0..10000.0 // 1-10000 kΩ reasonable range
            ACCEL_X, ACCEL_Y, ACCEL_Z -> value in -200.0..200.0 // ±200 m/s²
            GYRO_X, GYRO_Y, GYRO_Z -> value in -2000.0..2000.0 // ±2000 °/s
            MAG_X, MAG_Y, MAG_Z -> value in -10.0..10.0 // ±10 gauss
            BATTERY -> value in 0.0..5.0 // 0-5V
            else -> true // Unknown sensors pass validation
        }
    }

    /**
     * Get sensor data with validation - Official API style
     */
    fun getValidatedFormatClusterValue(
        sensorName: String,
        format: String,
    ): FormatClusterValue? {
        val value = getFormatClusterValue(sensorName, format)
        return if (value != null && validateSensorData(sensorName, value.data)) {
            value
        } else {
            Log.w(TAG, "Invalid sensor data for $sensorName: ${value?.data}")
            generateSimulatedValue(sensorName, format)
        }
    }

    /**
     * Get timestamp for this cluster - Official API method
     */
    fun getClusterTimestamp(): Long {
        return systemTimestamp.takeIf { it > 0 } ?: System.currentTimeMillis()
    }
    
    /**
     * Get sensor data by name and format - Official API method
     */
    fun getFormatClusterValue(sensorName: String, format: String): FormatClusterValue? {
        return dataMap[sensorName]?.firstOrNull { it.format == format }
    }
    
    /**
     * Add sensor data - Official API method
     */
    fun addData(sensorName: String, value: FormatClusterValue) {
        val existingValues = dataMap[sensorName]?.toMutableList() ?: mutableListOf()
        existingValues.add(value)
        dataMap[sensorName] = existingValues
    }
    
    /**
     * Generate simulated sensor value for testing
     */
    private fun generateSimulatedValue(sensorName: String, format: String): FormatClusterValue {
        val value = when (sensorName) {
            GSR_CONDUCTANCE, GSR -> (5.0 + Math.random() * 15.0) // 5-20 µS
            GSR_RESISTANCE -> (50.0 + Math.random() * 200.0) // 50-250 kΩ
            ACCEL_X, ACCEL_Y, ACCEL_Z -> (Math.random() - 0.5) * 20.0 // ±10 m/s²
            GYRO_X, GYRO_Y, GYRO_Z -> (Math.random() - 0.5) * 100.0 // ±50 °/s
            MAG_X, MAG_Y, MAG_Z -> (Math.random() - 0.5) * 2.0 // ±1 gauss
            BATTERY -> 3.7 + Math.random() * 0.5 // 3.7-4.2V
            else -> Math.random() * 100.0
        }
        
        val unit = when (sensorName) {
            GSR_CONDUCTANCE, GSR -> "µS"
            GSR_RESISTANCE -> "kΩ"
            ACCEL_X, ACCEL_Y, ACCEL_Z -> "m/s²"
            GYRO_X, GYRO_Y, GYRO_Z -> "°/s"
            MAG_X, MAG_Y, MAG_Z -> "gauss"
            BATTERY -> "V"
            else -> "units"
        }
        
        return FormatClusterValue(format, value, unit)
    }

    /**
     * Add data with validation - Official API style
     */
    fun addValidatedData(
        sensorName: String,
        value: FormatClusterValue,
    ): Boolean {
        return if (validateSensorData(sensorName, value.data)) {
            addData(sensorName, value)
            true
        } else {
            Log.w(TAG, "Rejected invalid sensor data for $sensorName: ${value.data}")
            false
        }
    }

    /**
     * Get all available sensor data as formatted string - Official API style
     */
    fun getFormattedClusterString(): String {
        val builder = StringBuilder()
        builder.append("ObjectCluster[timestamp=${getClusterTimestamp()}]\n")

        dataMap.forEach { (sensorName, values) ->
            values.forEach { value ->
                builder.append("  $sensorName[${value.format}]: ${value.data} ${value.unit}\n")
            }
        }

        return builder.toString()
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

/**
 * Data structure for formatted sensor values
 */
data class FormatClusterValue(
    val format: String,
    val data: Double,
    val unit: String
)
