package com.topdon.module.thermal.ir.event

import com.topdon.lib.core.repository.GalleryRepository.DirType

/**
 * Gallery[CN_TEXT]Switch[CN_TEXT].
 */
data class GalleryDirChangeEvent(val dirType: DirType)