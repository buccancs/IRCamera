package com.shimmer.driver

/**
 * Stub implementation of Configuration for compilation
 * This should be replaced with the actual Shimmer Android API
 */
object Configuration {
    
    object Shimmer3 {
        
        object ObjectClusterSensorName {
            const val GSR_CONDUCTANCE = "GSR_Conductance"
            const val GSR_RESISTANCE = "GSR_Resistance"
        }
        
        fun createGSRConfigurationBytes(samplingRateHz: Int): ByteArray {
            // Stub configuration - would contain actual device configuration bytes
            val config = ByteArray(12)
            
            val samplingRateConfig = when (samplingRateHz) {
                128 -> 0x04.toByte()
                256 -> 0x03.toByte()
                512 -> 0x02.toByte()
                1024 -> 0x01.toByte()
                else -> 0x04.toByte()
            }
            
            config[0] = samplingRateConfig
            config[1] = 0x08.toByte() // GSR sensor enable bit
            
            return config
        }
    }
}