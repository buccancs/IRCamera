package com.energy.commoncomponent.bean

/**
 * Custom pseudo color configuration bean
 */
data class CustomPseudoBean(
    var colorList: List<Int>? = null,
    var placeList: List<Int>? = null,
    var isUseGray: Boolean = false,
    var maxTemp: Float = 100f,
    var minTemp: Float = 0f
) {
    
    /**
     * Flag to indicate if using custom pseudo color
     */
    var isUseCustomPseudo: Boolean = false
    
    /**
     * Save configuration to shared preferences
     */
    fun saveToShared() {
        // Stub implementation - would normally save to SharedPreferences
        // In a real implementation, this would serialize the bean and save to preferences
    }
}