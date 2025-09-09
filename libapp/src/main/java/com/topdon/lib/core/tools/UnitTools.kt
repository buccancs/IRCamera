package com.topdon.lib.core.tools

import com.topdon.lib.core.common.SharedManager
import java.util.*

object UnitTools {
    /**
     * [CN_TEXT]
     *
     * @param float [CN_TEXT]
     */
    @JvmStatic
    fun showC(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // [CN_TEXT]
                "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
            } else {
                // Fahrenheit
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
            }
        return str
    }

    /**
     * [CN_TEXT]
     *
     * @param float [CN_TEXT]
     */
    @JvmStatic
    fun showC(
        float: Float,
        isC: Boolean,
    ): String {
        val str =
            if (isC) {
                // [CN_TEXT]
                "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
            } else {
                // Fahrenheit
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
            }
        return str
    }

    /**
     * [CN_TEXT]
     */
    @JvmStatic
    fun showIntervalC(
        min: Int,
        max: Int,
    ): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // [CN_TEXT]
                "$min~$max°C"
            } else {
                // Fahrenheit
                val maxT: Int = (max * 1.8000 + 32.00).toInt()
                val minT: Int = (min * 1.8000 + 32.00).toInt()
                "$minT~$maxT°F"
            }
        return str
    }

    /**
     * [CN_TEXT]
     */
    @JvmStatic
    fun showConfigC(
        min: Int,
        max: Int,
    ): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // [CN_TEXT]
                "($min~$max°C)"
            } else {
                // Fahrenheit
                val maxT: Int = (max * 1.8000 + 32.00).toInt()
                val minT: Int = (min * 1.8000 + 32.00).toInt()
                "($minT~$maxT°F)"
            }
        return str
    }

    /**
     * [CN_TEXT]
     *
     * @param float [CN_TEXT]
     */
    @JvmStatic
    fun showUnit(): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // [CN_TEXT]
                "°C"
            } else {
                // Fahrenheit
                "°F"
            }
        return str
    }

    /**
     * [CN_TEXT]
     *
     * @param float [CN_TEXT]
     */
    @JvmStatic
    fun showUnitValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // [CN_TEXT]
                value
            } else {
                // Fahrenheit
                toF(value)
            }
        return str.toFloat()
    }

    /**
     * [CN_TEXT]
     *
     * @param float [CN_TEXT]
     */
    @JvmStatic
    fun showUnitValue(
        value: Float,
        showC: Boolean,
    ): Float {
        if (value == Float.MAX_VALUE || value == Float.MIN_VALUE)
            {
                return value
            }
        val str =
            if (showC) {
                // [CN_TEXT]
                value
            } else {
                // Fahrenheit
                toF(value)
            }
        return str.toFloat()
    }

    /**
     * [CN_TEXT]Celsius
     *
     * @param float [CN_TEXT]
     */
    @JvmStatic
    fun showToCValue(
        value: Float,
        isShowC: Boolean,
    ): Float {
        val str =
            if (isShowC) {
                // [CN_TEXT]
                value
            } else {
                // Fahrenheit
                toC(value)
            }
        return str.toFloat()
    }

    /**
     * [CN_TEXT]Celsius
     *
     * @param float [CN_TEXT]
     */
    @JvmStatic
    fun showToCValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // [CN_TEXT]
                value
            } else {
                // Fahrenheit
                toC(value)
            }
        return str.toFloat()
    }

    /**
     * [CN_TEXT]Fahrenheit
     */
    fun toF(value: Float): Float {
        return value * 1.8000f + 32.00f
    }

    /**
     * [CN_TEXT]Celsius
     * [CN_TEXT],[CN_TEXT]Fahrenheit[CN_TEXT]Celsius[CN_TEXT]
     */
    fun toC(value: Float): Float {
        return (value - 32.0f) / 1.8000f
    }

    /**
     * [CN_TEXT]Celsius，[CN_TEXT]1[CN_TEXT] String.
     *
     * @param float [CN_TEXT]，[CN_TEXT]Celsius
     */
    @JvmStatic
    fun showNoUnit(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) { // Celsius
                String.format(Locale.ENGLISH, "%.1f", float)
            } else {
                String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
            }
        return if (str.endsWith(".0")) str.substring(0, str.length - 2) else str
    }

    /**
     * [CN_TEXT]Celsius，[CN_TEXT]1[CN_TEXT] String.
     *
     * @param float [CN_TEXT]，[CN_TEXT]Celsius
     */
    @JvmStatic
    fun showWithUnit(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) { // Celsius
                String.format(Locale.ENGLISH, "%.1f", float)
            } else {
                String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
            }
        return (if (str.endsWith(".0")) str.substring(0, str.length - 2) else str) + showUnit()
    }
}
