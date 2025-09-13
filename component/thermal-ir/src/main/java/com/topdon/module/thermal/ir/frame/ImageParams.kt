package com.topdon.module.thermal.ir.frame

/**
 * Image parameters for thermal imaging
 * Simplified implementation for compilation compatibility
 */
data class ImageParams(
    val width: Int = 256,
    val height: Int = 192,
    val format: String = "RGBA",
    val bitDepth: Int = 8,
    val channels: Int = 4,
    val timestamp: Long = System.currentTimeMillis(),
    val frameIndex: Long = 0,
    val exposure: Float = 1.0f,
    val gain: Float = 1.0f,
    val rotation: Int = 0
) {
    
    companion object {
        // Standard thermal camera resolutions
        val RESOLUTION_256x192 = ImageParams(256, 192)
        val RESOLUTION_384x288 = ImageParams(384, 288)
        val RESOLUTION_640x480 = ImageParams(640, 480)
        
        // Standard formats
        const val FORMAT_RGBA = "RGBA"
        const val FORMAT_RGB = "RGB"
        const val FORMAT_GRAYSCALE = "GRAYSCALE"
        const val FORMAT_THERMAL = "THERMAL"
    }
    
    /**
     * Calculate total image size in bytes
     */
    fun getImageSizeBytes(): Int {
        return width * height * channels * (bitDepth / 8)
    }
    
    /**
     * Get image parameters as map for serialization
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "width" to width,
            "height" to height,
            "format" to format,
            "bitDepth" to bitDepth,
            "channels" to channels,
            "timestamp" to timestamp,
            "frameIndex" to frameIndex,
            "exposure" to exposure,
            "gain" to gain,
            "rotation" to rotation
        )
    }
}