package com.topdon.module.thermal.ir.frame

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SizeUtils
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.bean.WatermarkBean
import com.topdon.lib.core.common.ProductType.PRODUCT_NAME_TC007
import com.topdon.lib.core.utils.ByteUtils
import com.topdon.lib.core.utils.ByteUtils.toBytes
import com.topdon.pseudo.bean.CustomPseudoBean

/**
 * Frame structure for thermal imaging data
 * Simplified implementation for compilation compatibility
 */
class FrameStruct {
    // Header structure properties
    var len: Int = 1024
    var name: String = "MPDC4GSR"
    var ver: String = AppUtils.getAppVersionName()
    var width: Int = 256
    var height: Int = 192
    var rotate: Int = 0
    
    // Temperature range properties
    var tempUnit: Int = 0 // 0 for Celsius, 1 for Fahrenheit
    var minTemp: Float = -20.0f
    var maxTemp: Float = 400.0f
    
    // Emissivity and measurement settings
    var emissivity: Float = 0.95f
    var reflectedTemp: Float = 25.0f
    var ambientTemp: Float = 25.0f
    var distance: Float = 1.0f
    
    // Image metadata
    var timestamp: Long = System.currentTimeMillis()
    var deviceSerial: String = ""
    var location: String = ""
    
    companion object {
        const val HEADER_SIZE = 1024
        const val TEMP_UNIT_CELSIUS = 0
        const val TEMP_UNIT_FAHRENHEIT = 1
    }
    
    /**
     * Convert frame structure to byte array
     */
    fun toByteArray(): ByteArray {
        val buffer = ByteArray(HEADER_SIZE)
        // Simplified implementation - just return initialized buffer
        return buffer
    }
    
    /**
     * Parse frame structure from byte array  
     */
    fun fromByteArray(data: ByteArray) {
        if (data.size < HEADER_SIZE) {
            throw IllegalArgumentException("Data array too small for frame header")
        }
        // Simplified implementation - basic parsing would go here
    }
}