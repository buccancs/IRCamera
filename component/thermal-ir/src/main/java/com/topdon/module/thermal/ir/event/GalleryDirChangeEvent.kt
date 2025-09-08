package com.topdon.module.thermal.ir.event

import com.topdon.lib.core.repository.GalleryRepository.DirType

/**
 * gallery[Chinese text]switchevent.
 */
data class GalleryDirChangeEvent(val dirType: DirType)