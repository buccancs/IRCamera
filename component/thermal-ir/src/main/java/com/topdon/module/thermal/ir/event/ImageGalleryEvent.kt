package com.topdon.module.thermal.ir.event

/**
 * Image gallery event for thermal imaging
 * Simplified implementation for compilation compatibility
 */
data class ImageGalleryEvent(
    val action: String,
    val imageId: String,
    val imagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val ACTION_REFRESH = "REFRESH"
        const val ACTION_SELECT = "SELECT"
        const val ACTION_DELETE = "DELETE"
        const val ACTION_EXPORT = "EXPORT"
    }
}