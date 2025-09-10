package com.shimmerresearch.driver

import android.util.Log

        @JvmStatic
        fun returnFormatCluster(
            clusters: Collection<FormatClusterValue>?,
            format: String,
        ): FormatClusterValue? {
            return clusters?.firstOrNull { it.format == format }
                ?: clusters?.firstOrNull()
        }
    }

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
