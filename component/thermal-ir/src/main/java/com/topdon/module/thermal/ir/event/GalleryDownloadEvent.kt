package com.topdon.module.thermal.ir.event

/**
\1 TS004 thermal imaging.
\1@param filename successful， xxx.jpg
 */
/**
 * Gallery download event for thermal imaging system communication.
 * Facilitates decoupled component interaction.
 */
data class GalleryDownloadEvent(val filename: String)
