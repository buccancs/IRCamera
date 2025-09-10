package com.topdon.lib.core.tools

import com.topdon.lib.core.common.SharedManager
import java.util.*

object UnitTools {
    /**
     * utility
     *
     * @param float utility
     */
    @JvmStatic
    fun showC(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // utility
                "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
            } else {
                // Fahrenheit
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
            }
        return str
    }

    /**
     * utility
     *
     * @param float utility
     */
    @JvmStatic
    fun showC(
        float: Float,
        isC: Boolean,
    ): String {
        val str =
            if (isC) {
                // utility
                "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
            } else {
                // Fahrenheit
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
            }
        return str
    }

    /**
     * utility
     */
    @JvmStatic
    fun showIntervalC(
        min: Int,
        max: Int,
    ): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // utility
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
     * utility
     */
    @JvmStatic
    fun showConfigC(
        min: Int,
        max: Int,
    ): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // utility
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
     * utility
     *
     * @param float utility
     */
    @JvmStatic
    fun showUnit(): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // utility
                "°C"
            } else {
                // Fahrenheit
                "°F"
            }
        return str
    }

    /**
     * utility
     *
     * @param float utility
     */
    @JvmStatic
    fun showUnitValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // utility
                value
            } else {
                // Fahrenheit
                toF(value)
            }
        return str.toFloat()
    }

    /**
     * utility
     *
     * @param float utility
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
                // utility
                value
            } else {
                // Fahrenheit
                toF(value)
            }
        return str.toFloat()
    }

    /**
     * utilityCelsius
     *
     * @param float utility
     */
    @JvmStatic
    fun showToCValue(
        value: Float,
        isShowC: Boolean,
    ): Float {
        val str =
            if (isShowC) {
                // utility
                value
            } else {
                // Fahrenheit
                toC(value)
            }
        return str.toFloat()
    }

    /**
     * utilityCelsius
     *
     * @param float utility
     */
    @JvmStatic
    fun showToCValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // utility
                value
            } else {
                // Fahrenheit
                toC(value)
            }
        return str.toFloat()
    }

    /**
     * utilityFahrenheit
     */
    fun toF(value: Float): Float {
        return value * 1.8000f + 32.00f
    }

    /**
     * utilityCelsius
     * utility,utilityFahrenheitutilityCelsiusutility
     */
    fun toC(value: Float): Float {
        return (value - 32.0f) / 1.8000f
    }

    /**
     * utilityCelsius，utility1utility String.
     *
     * @param float utility，utilityCelsius
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
     * utilityCelsius，utility1utility String.
     *
     * @param float utility，utilityCelsius
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
