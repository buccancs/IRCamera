package com.topdon.lib.core.bean

/**
 * Custom pseudo color bean - stub implementation for compilation
 */
data class CustomPseudoBean(
    var id: Int = 0,
    var name: String = "",
    var colorMode: Int = 0,
    var isDefault: Boolean = false,
    var colors: IntArray = intArrayOf(),
    var enabled: Boolean = true,
    var isUseCustomPseudo: Boolean = false,
    var isUseGray: Boolean = false,
    var maxTemp: Float = 100f,
    var minTemp: Float = 0f
) {
    
    companion object {
        const val MODE_RAINBOW = 0
        const val MODE_IRONBOW = 1
        const val MODE_GRAYSCALE = 2
        const val MODE_CUSTOM = 3
        
        /**
         * Create default pseudo color bean
         */
        fun createDefault(): CustomPseudoBean {
            return CustomPseudoBean(
                id = 0,
                name = "Default",
                colorMode = MODE_RAINBOW,
                isDefault = true
            )
        }
        
        /**
         * Create rainbow pseudo color
         */
        fun createRainbow(): CustomPseudoBean {
            return CustomPseudoBean(
                id = 1,
                name = "Rainbow",
                colorMode = MODE_RAINBOW,
                colors = generateRainbowColors()
            )
        }
        
        /**
         * Create iron bow pseudo color
         */
        fun createIronbow(): CustomPseudoBean {
            return CustomPseudoBean(
                id = 2,
                name = "Ironbow",
                colorMode = MODE_IRONBOW,
                colors = generateIronbowColors()
            )
        }
        
        /**
         * Generate rainbow color palette
         */
        private fun generateRainbowColors(): IntArray {
            // Generate simplified rainbow colors
            val colors = IntArray(256)
            for (i in 0 until 256) {
                val hue = (i * 360f) / 256f
                colors[i] = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
            }
            return colors
        }
        
        /**
         * Generate ironbow color palette
         */
        private fun generateIronbowColors(): IntArray {
            // Generate simplified ironbow colors (black -> red -> yellow -> white)
            val colors = IntArray(256)
            for (i in 0 until 256) {
                val ratio = i / 255f
                colors[i] = when {
                    ratio < 0.33f -> {
                        val r = (ratio * 3 * 255).toInt()
                        android.graphics.Color.rgb(r, 0, 0)
                    }
                    ratio < 0.66f -> {
                        val g = ((ratio - 0.33f) * 3 * 255).toInt()
                        android.graphics.Color.rgb(255, g, 0)
                    }
                    else -> {
                        val b = ((ratio - 0.66f) * 3 * 255).toInt()
                        android.graphics.Color.rgb(255, 255, b)
                    }
                }
            }
            return colors
        }
        
        /**
         * Load custom pseudo bean from shared preferences
         */
        fun loadFromShared(): CustomPseudoBean {
            // Stub implementation - return default for compilation
            return createDefault()
        }
    }
    
    /**
     * Check if pseudo bean is valid
     */
    fun isValid(): Boolean {
        return name.isNotBlank() && colors.isNotEmpty()
    }
    
    /**
     * Get color at position
     */
    fun getColorAt(position: Float): Int {
        if (colors.isEmpty()) return android.graphics.Color.BLACK
        val index = (position.coerceIn(0f, 1f) * (colors.size - 1)).toInt()
        return colors[index]
    }
    
    /**
     * Save to shared preferences
     */
    fun saveToShared() {
        // Stub implementation for saving to SharedPreferences
    }
    
    /**
     * Get color array for pseudo color mapping
     */
    fun getColorArray(): IntArray? {
        return if (colors.isNotEmpty()) colors else null
    }
    
    /**
     * Get place list for temperature ranges
     */
    fun getPlaceListArray(): FloatArray? {
        // Generate default place list based on temperature range
        return if (maxTemp > minTemp) {
            val count = colors.size.coerceAtLeast(2)
            FloatArray(count) { i ->
                minTemp + (maxTemp - minTemp) * i / (count - 1)
            }
        } else null
    }
    
    /**
     * Get color list as List<Int> for compatibility with HikSurfaceView
     */
    fun getColorList(): List<Int> {
        return colors.toList()
    }
    
    /**
     * Get place list as List<Float> for compatibility with HikSurfaceView
     */
    fun getPlaceList(): List<Float> {
        val array = getPlaceListArray()
        return array?.toList() ?: emptyList()
    }
}