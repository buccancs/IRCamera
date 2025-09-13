package com.topdon.module.thermal.ir.event

/**
有一张 TS004 thermal imagingimage或video从远端Download完毕Event.
@param filename Downloadsuccessful的file名，如 xxx.jpg
 */
/**
 * Gallery download event for thermal imaging system communication.
 * Facilitates decoupled component interaction.
 */
data class GalleryDownloadEvent(val filename: String)
