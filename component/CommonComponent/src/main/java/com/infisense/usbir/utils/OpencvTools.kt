package com.infisense.usbir.utils

import android.graphics.Bitmap
import com.energy.commoncomponent.utils.OpencvTools as CommonOpencvTools

/**
 * OpenCV tools compatibility layer for infisense package
 */
class OpencvTools {
    
    companion object {
        
        /**
         * Apply pseudocolor mapping to thermal image data
         */
        fun customPseudoColor(
            thermalData: ByteArray,
            width: Int,
            height: Int,
            colorList: List<Int>? = null,
            isUseGray: Boolean = false,
            maxTemp: Float = 100f,
            minTemp: Float = 0f
        ): Bitmap? {
            return CommonOpencvTools.customPseudoColor(
                thermalData, width, height, colorList, isUseGray, maxTemp, minTemp
            )
        }
        
        /**
         * Set pseudocolor max and min temperatures
         */
        fun setPseudoColorMaxMin(maxTemp: Float, minTemp: Float) {
            // Stub implementation for thermal range setting
        }
        
        /**
         * Change pseudocode mode by old configuration
         */
        fun changePseudocodeModeByOld(oldMode: Int): Int {
            // Stub implementation - return same mode for now
            return oldMode
        }
        
        /**
         * Contour detection on thermal data
         */
        fun contourDetection(bitmap: Bitmap?): Bitmap? {
            // Stub implementation - return original bitmap
            return bitmap
        }
        
        /**
         * Process thermal frame data
         */
        fun processFrame(frameData: ByteArray, width: Int, height: Int): Bitmap? {
            return customPseudoColor(frameData, width, height)
        }
        
        /**
         * Super image amplification processing
         */
        fun supImage(sourceArray: IntArray, width: Int, height: Int, outputArray: IntArray) {
            // Stub implementation for image amplification
            // Simply copy source to output for now
            if (sourceArray.size <= outputArray.size) {
                System.arraycopy(sourceArray, 0, outputArray, 0, sourceArray.size)
            }
        }
        
        /**
         * Super image amplification processing with ByteArray (compatibility method)
         */
        fun supImage(sourceArray: ByteArray, width: Int, height: Int, outputArray: ByteArray) {
            // Stub implementation for image amplification with ByteArray
            // Simply copy source to output for now
            if (sourceArray.size <= outputArray.size) {
                System.arraycopy(sourceArray, 0, outputArray, 0, sourceArray.size)
            }
        }
    }
}