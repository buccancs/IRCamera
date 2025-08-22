package com.topdon.module.thermal.ir.event

import com.topdon.lib.core.enum.DirType

/**
 * 图库目录切换事件.
 */
data class GalleryDirChangeEvent(val dirType: DirType)