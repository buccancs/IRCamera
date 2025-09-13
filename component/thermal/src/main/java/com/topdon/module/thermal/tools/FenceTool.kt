package com.topdon.module.thermal.tools

/**
 * Fence tool tools for thermal imaging processing.
 * Contains specialized algorithms and processing functions.
 */
object FenceTool {
\1坐标 => 序列
    fun pointToIndex(
        point: IntArray,
        w: Int,
    ): Int {
        val x = point[0]
        val y = point[1]
        return y * w + x
    }

\1序列 => 坐标
    fun indexToPoint(
        index: Int,
        w: Int,
    ): IntArray {
        val y = index / w
        val x = index % w
        return intArrayOf(x, y)
    }
}
