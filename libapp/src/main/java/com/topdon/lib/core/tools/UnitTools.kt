package com.topdon.lib.core.tools

import com.topdon.lib.core.common.SharedManager
import java.util.*

/**
 * UnitTools represents a unittools component.
 * 
 * This class provides functionality for unittools operations.
 */
object UnitTools {
    /**
     * temperature[Chinese text]
     *
     * @param float temperature
     */
    @JvmStatic
    fun showC(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // temperature
                "${String.format(Locale.ENGLISH, "%.1f", float)}degC"
            } else {
                // [Chinese text]
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}degF"
            }
        return str
    }

    /**
     * temperature[Chinese text]
     *
     * @param float temperature
     */
    @JvmStatic
    fun showC(
        float: Float,
        isC: Boolean,
    ): String {
        val str =
            if (isC) {
                // temperature
                "${String.format(Locale.ENGLISH, "%.1f", float)}degC"
            } else {
                // [Chinese text]
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}degF"
            }
        return str
    }

    /**
     * temperature[Chinese text]
     */
    @JvmStatic
    fun showIntervalC(
        min: Int,
        max: Int,
    ): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // temperature
                "$min~$maxdegC"
            } else {
                // [Chinese text]
                val maxT: Int = (max * 1.8000 + 32.00).toInt()
                val minT: Int = (min * 1.8000 + 32.00).toInt()
                "$minT~$maxTdegF"
            }
        return str
    }

    /**
     * [Chinese text]temperature[Chinese text]
     */
    @JvmStatic
    fun showConfigC(
        min: Int,
        max: Int,
    ): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // temperature
                "($min~$maxdegC)"
            } else {
                // [Chinese text]
                val maxT: Int = (max * 1.8000 + 32.00).toInt()
                val minT: Int = (min * 1.8000 + 32.00).toInt()
                "($minT~$maxTdegF)"
            }
        return str
    }

    /**
     * temperature[Chinese text]
     *
     * @param float temperature
     */
    @JvmStatic
    fun showUnit(): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // temperature
                "degC"
            } else {
                // [Chinese text]
                "degF"
            }
        return str
    }

    /**
     * temperature[Chinese text]
     *
     * @param float temperature
     */
    @JvmStatic
    fun showUnitValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // temperature
                value
            } else {
                // [Chinese text]
                toF(value)
            }
        return str.toFloat()
    }

    /**
     * temperature[Chinese text]
     *
     * @param float temperature
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
                // temperature
                value
            } else {
                // [Chinese text]
                toF(value)
            }
        return str.toFloat()
    }

    /**
     * [Chinese text]
     *
     * @param float temperature
     */
    @JvmStatic
    fun showToCValue(
        value: Float,
        isShowC: Boolean,
    ): Float {
        val str =
            if (isShowC) {
                // temperature
                value
            } else {
                // [Chinese text]
                toC(value)
            }
        return str.toFloat()
    }

    /**
     * [Chinese text]
     *
     * @param float temperature
     */
    @JvmStatic
    fun showToCValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // temperature
                value
            } else {
                // [Chinese text]
                toC(value)
            }
        return str.toFloat()
    }

    /**
     * [Chinese text]
     */
    fun toF(value: Float): Float {
        return value * 1.8000f + 32.00f
    }

    /**
     * [Chinese text]
     * [Chinese text]point[Chinese text],[Chinese text]
     */
    fun toC(value: Float): Float {
        return (value - 32.0f) / 1.8000f
    }

    /**
     * [Chinese text], [Chinese text]1[Chinese text] String.
     *
     * @param float temperature[Chinese text], [Chinese text]
     */
    @JvmStatic
    fun showNoUnit(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) { // [Chinese text]
                String.format(Locale.ENGLISH, "%.1f", float)
            } else {
                String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
            }
        return if (str.endsWith(".0")) str.substring(0, str.length - 2) else str
    }

    /**
     * [Chinese text], [Chinese text]1[Chinese text] String.
     *
     * @param float temperature[Chinese text], [Chinese text]
     */
    @JvmStatic
    fun showWithUnit(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) { // [Chinese text]
                String.format(Locale.ENGLISH, "%.1f", float)
            } else {
                String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))
            }
        return (if (str.endsWith(".0")) str.substring(0, str.length - 2) else str) + showUnit()
    }
}
