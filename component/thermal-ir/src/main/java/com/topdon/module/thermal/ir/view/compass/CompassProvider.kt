package com.topdon.module.thermal.ir.view.compass

import android.content.Context

/**
 * Compass provider utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
class CompassProvider(private val context: Context) {
    
    fun get(): Any? {
        // Minimal implementation - return null compass
        return NullCompass()
    }

    fun getAvailableSources(context: Context): List<CompassSource> {
        // Return basic compass sources
        return listOf(
            CompassSource.RotationVector,
            CompassSource.Orientation
        )
    }
}