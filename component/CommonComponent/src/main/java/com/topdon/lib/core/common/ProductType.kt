package com.topdon.lib.core.common

/**
 * Product type enumeration - stub implementation for compilation
 */
enum class ProductType(val value: Int, val displayName: String) {
    
    THERMAL_CAMERA(1, "Thermal Camera"),
    IR_CAMERA(2, "IR Camera"), 
    VISIBLE_CAMERA(3, "Visible Camera"),
    DUAL_CAMERA(4, "Dual Camera"),
    TC001(100, "TC001"),
    TS004(101, "TS004"),
    TC007(102, "TC007"),
    UNKNOWN(0, "Unknown");
    
    companion object {
        // Product name constants for backward compatibility
        const val PRODUCT_NAME_TC001LITE = "TC001 Lite"
        const val PRODUCT_NAME_TC001 = "TC001"
        const val PRODUCT_NAME_TS004 = "TS004"
        const val PRODUCT_NAME_TC007 = "TC007"
        
        fun fromValue(value: Int): ProductType {
            return values().find { it.value == value } ?: UNKNOWN
        }
        
        fun fromName(name: String): ProductType {
            return values().find { it.name.equals(name, ignoreCase = true) } ?: UNKNOWN
        }
        
        fun getCurrentProduct(): ProductType {
            // Stub - return default product
            return THERMAL_CAMERA
        }
        
        fun isTC001(): Boolean {
            return getCurrentProduct() == TC001
        }
        
        fun isTS004(): Boolean {
            return getCurrentProduct() == TS004
        }
        
        fun isTC007(): Boolean {
            return getCurrentProduct() == TC007
        }
        
        fun isThermalCamera(): Boolean {
            return getCurrentProduct() in listOf(THERMAL_CAMERA, TC001, TS004, TC007)
        }
    }
}