package com.topdon.lib.ui.utils

/**
 * @author: CaiSongL
 * @date: 2023/3/31 9:56
 */
internal object CenterItemUtils {
    fun getMinDifferItem(itemHeights: List<CenterViewItem>): CenterViewItem {
        var minItem = itemHeights[0] // [Chinese text]
        for (i in itemHeights.indices) {
            // [Chinese text]
            if (itemHeights[i].differ <= minItem.differ) {
                minItem = itemHeights[i]
            }
        }
        return minItem
    }

    class CenterViewItem
    // [Chinese text]Item[Chinese text]
    // [Chinese text]item[Chinese text]in progress[Chinese text]
    (var position: Int, var differ: Int)
}
