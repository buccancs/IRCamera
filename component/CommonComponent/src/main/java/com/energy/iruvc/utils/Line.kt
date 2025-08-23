package com.energy.iruvc.utils

import android.graphics.Point

/**
 * Line utility class for thermal measurements
 * Stub implementation for external library dependency
 */
data class Line(
    val start: Point,
    val end: Point
) {
    
    /**
     * Get line length
     */
    fun length(): Float {
        val dx = (end.x - start.x).toFloat()
        val dy = (end.y - start.y).toFloat()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
    
    /**
     * Get midpoint of the line
     */
    fun midpoint(): Point {
        return Point(
            (start.x + end.x) / 2,
            (start.y + end.y) / 2
        )
    }
    
    /**
     * Check if point is on the line (with tolerance)
     */
    fun contains(point: Point, tolerance: Float = 5f): Boolean {
        // Simple distance-to-line calculation
        val distanceToLine = distancePointToLine(point)
        return distanceToLine <= tolerance
    }
    
    private fun distancePointToLine(point: Point): Float {
        // Distance from point to line formula
        val a = end.y - start.y
        val b = start.x - end.x
        val c = end.x * start.y - start.x * end.y
        
        val distance = kotlin.math.abs(a * point.x + b * point.y + c).toFloat()
        val lineLength = kotlin.math.sqrt((a * a + b * b).toDouble()).toFloat()
        
        return if (lineLength > 0) distance / lineLength else 0f
    }
}