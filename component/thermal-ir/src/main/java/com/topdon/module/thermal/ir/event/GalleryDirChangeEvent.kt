package com.topdon.module.thermal.ir.event

import com.topdon.lib.core.repository.GalleryRepository.DirType

/**
图库目录switchEvent.
 */
/**
 * Gallery dir change event for thermal imaging system communication.
 * Facilitates decoupled component interaction.
 */
data class GalleryDirChangeEvent(val dirType: DirType)
