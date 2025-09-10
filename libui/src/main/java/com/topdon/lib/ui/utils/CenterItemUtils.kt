package com.topdon.lib.ui.utils

internal object CenterItemUtils {
    fun getMinDifferItem(itemHeights: List<CenterViewItem>): CenterViewItem {
        var minItem = itemHeights[0]
        for (i in itemHeights.indices) {
            // Find item with minimum difference value
            if (itemHeights[i].differ <= minItem.differ) {
                minItem = itemHeights[i]
            }
        }
        return minItem
    }

    class CenterViewItem
    // Data class for center view item positioning
    // Represents current item position and difference value
    (var position: Int, var differ: Int)
}
