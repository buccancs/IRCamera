package com.topdon.lib.core.tools

import com.topdon.lib.core.common.SharedManager
import java.util.*

object UnitTools {

    @JvmStatic
    fun showC(float: Float): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // Utility function
                "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
            } else {
                // Fahrenheit
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
            }
        return str
    }

    @JvmStatic
    fun showC(
        float: Float,
        isC: Boolean,
    ): String {
        val str =
            if (isC) {
                // Utility function
                "${String.format(Locale.ENGLISH, "%.1f", float)}°C"
            } else {
                // Fahrenheit
                "${String.format(Locale.ENGLISH, "%.1f", (float * 1.8000 + 32.00))}°F"
            }
        return str
    }

    @JvmStatic
    fun showUnit(): String {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // Utility function
                "°C"
            } else {
                // Fahrenheit
                "°F"
            }
        return str
    }

    @JvmStatic
    fun showUnitValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // Utility function
                value
            } else {
                // Fahrenheit
                toF(value)
            }
        return str.toFloat()
    }

    @JvmStatic
    fun showUnitValue(
        value: Float,
        showC: Boolean,
    ): Float {
        if (value == Float.MAX_VALUE || value == Float.MIN_VALUE) {
            return value
        }
        val str =
            if (showC) {
                // Utility function
                value
            } else {
                // Fahrenheit
                toF(value)
            }
        return str.toFloat()
    }

    @JvmStatic
    fun showToCValue(
        value: Float,
        isShowC: Boolean,
    ): Float {
        val str =
            if (isShowC) {
                // Utility function
                value
            } else {
                // Fahrenheit
                toC(value)
            }
        return str.toFloat()
    }

    @JvmStatic
    fun showToCValue(value: Float): Float {
        val str =
            if (SharedManager.getTemperature() == 1) {
                // Utility function
                value
            } else {
                // Fahrenheit
                toC(value)
            }
        return str.toFloat()
    }

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
