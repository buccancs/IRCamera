package com.topdon.module.thermal.ir.chart

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.topdon.lib.core.tools.UnitTools

/**
 * Y[Chinese text]
 */
class YValueFormatter : IndexAxisValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return try {
            String.format("%.1f", value)//[Chinese text]value[Chinese text]
            UnitTools.showC(value)
        } catch (e: Exception) {
            UnitTools.showC(value)
        }
    }

}
