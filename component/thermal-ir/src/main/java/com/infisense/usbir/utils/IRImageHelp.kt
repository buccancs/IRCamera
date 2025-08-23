package com.infisense.usbir.utils

import com.topdon.lib.core.bean.AlarmBean

/**
 * IR image processing helper
 */
class IRImageHelp {
    
    /**
     * Set color list for pseudocolor rendering
     */
    fun setColorList(
        colorList: List<Int>,
        placeList: List<Float>,
        isUseGray: Boolean,
        maxTemp: Float,
        minTemp: Float
    ) {
        // Stub implementation for setting color mapping
    }
    
    /**
     * Get current color list
     */
    fun getColorList(): List<Int>? {
        // Stub implementation - return null to indicate default pseudocolor should be used
        return null
    }
    
    /**
     * Custom pseudocolor processing with ARGB array and temperature data
     */
    fun customPseudoColor(argbArray: IntArray, tempArray: FloatArray, width: Int, height: Int) {
        // Stub implementation for pseudocolor processing with existing arrays
    }
    
    /**
     * Custom pseudocolor processing with ByteArray inputs (compatibility method)
     */
    fun customPseudoColor(argbArray: ByteArray, tempArray: ByteArray, width: Int, height: Int) {
        // Stub implementation for pseudocolor processing with ByteArray inputs
        // Convert ByteArray to appropriate types internally if needed
    }
    
    /**
     * Set pseudocolor max and min values with array processing
     */
    fun setPseudoColorMaxMin(
        argbArray: IntArray, 
        tempArray: FloatArray, 
        maxTemp: Float, 
        minTemp: Float,
        width: Int,
        height: Int
    ) {
        // Stub implementation for temperature range setting with array processing
    }
    
    /**
     * Set pseudocolor max and min values with ByteArray inputs (compatibility method)
     */
    fun setPseudoColorMaxMin(
        argbArray: ByteArray, 
        tempArray: ByteArray, 
        maxTemp: Float, 
        minTemp: Float,
        width: Int,
        height: Int
    ) {
        // Stub implementation for temperature range setting with ByteArray processing
    }
    
    /**
     * Contour detection processing with alarm bean
     */
    fun contourDetection(
        alarmBean: AlarmBean?,
        argbArray: IntArray,
        tempArray: FloatArray,
        width: Int,
        height: Int
    ): IntArray? {
        // Stub implementation for contour detection - return null to use original array
        return null
    }
    
    /**
     * Contour detection processing with ByteArray inputs (compatibility method)
     */
    fun contourDetection(
        alarmBean: AlarmBean?,
        argbArray: ByteArray,
        tempArray: ByteArray,
        width: Int,
        height: Int
    ): ByteArray? {
        // Stub implementation for contour detection - return null to use original array
        return null
    }
}