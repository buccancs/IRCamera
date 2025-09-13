package com.topdon.module.thermal.ir.view.compass

/**
 * Null compass utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
class NullCompass : NullSensor() {
    
    val bearing: Float = 0f
    
    var declination: Float = 0f
    
    val rawBearing: Float = 0f
}