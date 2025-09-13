package com.topdon.module.thermal.ir.event

/**
 * Gallery add event for thermal imaging
 * Simplified implementation for compilation compatibility
 */
data class GalleryAddEvent(
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String = "ADD"
) {
    companion object {
        const val EVENT_TYPE_ADD = "ADD"
        const val EVENT_TYPE_UPDATE = "UPDATE"
        const val EVENT_TYPE_DELETE = "DELETE"
    }
}