package com.guide.zm04c.matrix.utils

import com.guide.zm04c.matrix.Logger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.experimental.and

        fun float2StrWithOneDecimal(number: Float): String {
            try {
                val pattern = "0.0"
                if (df == null) {
                    val enlocale = Locale("en", "US")
                    df = NumberFormat.getNumberInstance(enlocale) as DecimalFormat
                }
                df!!.applyPattern(pattern)
                return float2Str(number, df!!)
            } catch (e: Exception) {
                val newNumber = Math.round(number * 10) / 10f
                val str = newNumber.toString()
                Logger.e(TAG, "float2StrWithOneDecimal number = " + number + " str = " + str)
                return str
            }
        }

        fun float2StrWithTwoDecimal(number: Float): String {
            try {
                val pattern = "0.00"
                if (df == null) {
                    val enlocale = Locale("en", "US")
                    df = NumberFormat.getNumberInstance(enlocale) as DecimalFormat
                }
                df!!.applyPattern(pattern)
                return float2Str(number, df!!)
            } catch (e: Exception) {
                val newNumber = Math.round(number * 100) / 100f
                val str = newNumber.toString()
                Logger.e(TAG, "float2StrWithTwoDecimal number = " + number + " str = " + str)
                return str
            }
        }

        fun float2Str(
            number: Float,
            df: DecimalFormat,
        ): String {
            return df.format(number.toDouble())
        }
    }

    init {
        throw AssertionError("cannot be instantiated")
    }
}
