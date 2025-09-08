package com.topdon.lib.core.tools

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

object NumberTools {

    /**
     * Function description.
     */
    fun to01(float: Float): String {
        return String.format(Locale.ENGLISH, "%.1f", float)
    }

    /**
     * Function description.
     */
    fun to01f(float: Float): Float {
        return to01(float).toFloat()
    }

    /**
     * Function description.
     */
    fun to02(float: Float): String {
        return String.format(Locale.ENGLISH, "%.2f", float)
    }

    /**
     * Function description.
     */
    fun to02f(float: Float): Float {
        return to02(float).toFloat()
    }

     * @param newScale
    /**
     * Function description.
     */
    fun scale(value: Float, newScale: Int): Float {
        return BigDecimal(value.toDouble()).setScale(newScale, RoundingMode.HALF_UP).toFloat()
    }
}