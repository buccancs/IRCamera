package com.topdon.menu.constant

/**
 * Fence type enumeration - stub implementation for compilation
 */
enum class FenceType(val value: Int, val displayName: String) {
    
    NONE(0, "None"),
    POINT(1, "Point"),
    LINE(2, "Line"), 
    RECTANGLE(3, "Rectangle"),
    ELLIPSE(4, "Ellipse"),
    POLYGON(5, "Polygon"),
    CIRCLE(6, "Circle");
    
    companion object {
        fun fromValue(value: Int): FenceType {
            return values().find { it.value == value } ?: NONE
        }
        
        fun getDisplayNames(): Array<String> {
            return values().map { it.displayName }.toTypedArray()
        }
        
        fun isValidType(type: Int): Boolean {
            return values().any { it.value == type }
        }
        
        fun getTemperatureMeasurementTypes(): Array<FenceType> {
            return arrayOf(POINT, LINE, RECTANGLE)
        }
        
        fun isTemperatureMeasurement(type: FenceType): Boolean {
            return type in getTemperatureMeasurementTypes()
        }
    }
    
    /**
     * Check if fence type supports temperature measurement
     */
    fun supportsTemperature(): Boolean {
        return this in arrayOf(POINT, LINE, RECTANGLE, ELLIPSE, CIRCLE)
    }
    
    /**
     * Get minimum points required for this fence type
     */
    fun getMinPoints(): Int {
        return when (this) {
            NONE -> 0
            POINT -> 1
            LINE -> 2
            RECTANGLE -> 2
            CIRCLE -> 2
            ELLIPSE -> 2
            POLYGON -> 3
        }
    }
}