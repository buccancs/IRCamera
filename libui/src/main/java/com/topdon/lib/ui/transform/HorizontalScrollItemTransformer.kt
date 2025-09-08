package com.topdon.lib.ui.transform

import android.view.View

/**
 * @author: CaiSongL
 * @date: 2023/4/1 11:32
 */
@Deprecated("[Chinese text]-menu-Photo capture[Chinese text], [Chinese text]")
interface HorizontalScrollItemTransformer {
    fun transformItem(
        item: View,
        position: Float,
    )
}
