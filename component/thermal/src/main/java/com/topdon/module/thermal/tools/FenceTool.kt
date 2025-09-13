package com.topdon.module.thermal.tools

/**
 * Fence tool tools for thermal imaging processing.
 * Contains specialized algorithms and processing functions.
 */
object FenceTool {
\1Text => Text
    fun pointToIndex(
        point: IntArray,
        w: Int,
    ): Int {
        val x = point[0]
        val y = point[1]
        return y * w + x
    }

\1Text => Text
    fun indexToPoint(
        index: Int,
        w: Int,
    ): IntArray {
        val y = index / w
        val x = index % w
        return intArrayOf(x, y)
    }
}
