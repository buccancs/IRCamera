package com.topdon.pseudo.bean

/**
 * Custom pseudo color configuration bean
 */
data class CustomPseudoBean(
    val colorList: List<Int> = emptyList(),
    val placeList: List<Float> = emptyList(),
    val isUseGray: Boolean = false,
    val maxTemp: Float = 100f,
    val minTemp: Float = 0f
) {
    
    fun setColorList(colors: List<Int>): CustomPseudoBean {
        return this.copy(colorList = colors)
    }
}