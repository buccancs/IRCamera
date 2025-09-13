package com.topdon.module.thermal.ir.view.compass

/**
 * Mag quality compass wrapper utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
class MagQualityCompassWrapper {
    
    var bearing: Float = 0f
        private set
    
    var declination: Float = 0f
        set(value) {
            field = value
        }
    
    val hasValidReading: Boolean = false
    
    val rawBearing: Float = 0f
    
    fun start() {
        // Minimal implementation
    }
    
    fun stop() {
        // Minimal implementation
    }
    
    private fun onReading(): Boolean {
        return true
    }
}