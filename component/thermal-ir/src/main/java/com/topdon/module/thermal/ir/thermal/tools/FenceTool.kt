package com.topdon.module.thermal.ir.thermal.tools

object FenceTool {

    //  =>
    /**
     * Function description.
     */
    fun pointToIndex(point: IntArray, w: Int): Int {
        val x = point[0]
        val y = point[1]
        return y * w + x
    }

    //  =>
    /**
     * Function description.
     */
    fun indexToPoint(index: Int, w: Int): IntArray {
        val y = index / w
        val x = index % w
        return intArrayOf(x, y)
    }
}