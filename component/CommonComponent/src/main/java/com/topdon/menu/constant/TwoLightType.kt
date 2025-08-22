package com.topdon.menu.constant

/**
 * Two light type enumeration - stub implementation for compilation
 */
enum class TwoLightType(val value: Int, val displayName: String) {
    
    NONE(0, "None"),
    VISIBLE_LIGHT(1, "Visible Light"),
    IR_LIGHT(2, "IR Light"),
    DUAL_LIGHT(3, "Dual Light"),
    THERMAL_LIGHT(4, "Thermal Light"),
    COMBINED(5, "Combined");
    
    companion object {
        fun fromValue(value: Int): TwoLightType {
            return values().find { it.value == value } ?: NONE
        }
        
        fun getDisplayNames(): Array<String> {
            return values().map { it.displayName }.toTypedArray()
        }
        
        fun getLightTypes(): Array<TwoLightType> {
            return arrayOf(VISIBLE_LIGHT, IR_LIGHT, DUAL_LIGHT, THERMAL_LIGHT)
        }
        
        fun isValidType(value: Int): Boolean {
            return values().any { it.value == value }
        }
        
        fun getDefault(): TwoLightType {
            return VISIBLE_LIGHT
        }
    }
    
    /**
     * Check if light type supports IR functionality
     */
    fun supportsIR(): Boolean {
        return this in arrayOf(IR_LIGHT, DUAL_LIGHT, THERMAL_LIGHT, COMBINED)
    }
    
    /**
     * Check if light type supports visible light
     */
    fun supportsVisible(): Boolean {
        return this in arrayOf(VISIBLE_LIGHT, DUAL_LIGHT, COMBINED)
    }
    
    /**
     * Check if light type supports thermal imaging
     */
    fun supportsThermal(): Boolean {
        return this in arrayOf(THERMAL_LIGHT, COMBINED)
    }
    
    /**
     * Get compatibility with other light type
     */
    fun isCompatibleWith(other: TwoLightType): Boolean {
        return when (this) {
            NONE -> true
            VISIBLE_LIGHT -> other.supportsVisible() || other == NONE
            IR_LIGHT -> other.supportsIR() || other == NONE  
            THERMAL_LIGHT -> other.supportsThermal() || other == NONE
            DUAL_LIGHT -> other.supportsVisible() || other.supportsIR()
            COMBINED -> true
        }
    }
}