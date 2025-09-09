package com.topdon.module.thermal.ir.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.topdon.lib.core.tools.UnitTools
import com.topdon.module.thermal.ir.R
import com.topdon.lib.core.R as LibR
import com.topdon.module.thermal.R as ThermalR

class ChartTrendView : LineChart {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val textColor: Int = ContextCompat.getColor(context, LibR.color.chart_text)
        val axisChartColors: Int = ContextCompat.getColor(context, LibR.color.chart_axis)

        this.isDragEnabled = false
        this.isScaleYEnabled = false //[CN_TEXT]Y[CN_TEXT]
        this.isScaleXEnabled = false //[CN_TEXT]X[CN_TEXT]
        this.isDoubleTapToZoomEnabled = false//[CN_TEXT]
        this.setScaleEnabled(false)//[CN_TEXT]
        this.setPinchZoom(false)//[CN_TEXT]，[CN_TEXT]x[CN_TEXT]y[CN_TEXT]
        this.setTouchEnabled(true)
        this.setDrawGridBackground(false)
        this.description = null//[CN_TEXT]
        this.axisRight.isEnabled = false //[CN_TEXT]Y[CN_TEXT]
        this.setExtraOffsets(
            0f,
            0f,
            SizeUtils.dp2px(8f).toFloat(),
            SizeUtils.dp2px(4f).toFloat()
        )//[CN_TEXT]

        setNoDataText(context.getString(ThermalR.string.lms_http_code998))
        setNoDataTextColor(ContextCompat.getColor(context, LibR.color.chart_text))

        val mv = MyMarkerView(context, R.layout.marker_lay)
        mv.chartView = this
        marker = mv//Settings[CN_TEXT]

        legend.form = Legend.LegendForm.CIRCLE
        legend.textColor = textColor
        legend.isEnabled = false//[CN_TEXT]

        //x[CN_TEXT]
        val xAxis = this.xAxis
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(false)//[CN_TEXT]
        xAxis.axisLineColor = 0x00000000 //x[CN_TEXT]
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true//[CN_TEXT]
        xAxis.textSize = 11f
        xAxis.isJumpFirstLabel = false
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 10f
        xAxis.setLabelCount(3, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value < 5) {
                    return "A"
                }
                if (value > 5) {
                    return "B"
                }
                return ""
            }
        }

        //y[CN_TEXT]
        val leftAxis = this.axisLeft
        leftAxis.textColor = textColor //y[CN_TEXT]
        leftAxis.axisLineColor = 0x00000000 //y[CN_TEXT]
        leftAxis.setDrawGridLines(true)//[CN_TEXT]
        leftAxis.gridColor = axisChartColors //y[CN_TEXT]
        leftAxis.gridLineWidth = 1.5f
        leftAxis.setLabelCount(6, true)
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String = ""
        }
        leftAxis.textSize = 11f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 50f

        data = LineData()
    }

    fun setToEmpty() {
        axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String = ""
        }
        data = LineData()
        invalidate()
    }

    /**
     * [CN_TEXT]Specified[CN_TEXT]
     * @param tempList [CN_TEXT]，[CN_TEXT]Celsius
     */
    fun refresh(tempList: List<Float>) {
        if (tempList.isEmpty()) {
            setToEmpty()
            return
        }

        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (tempList.size - 1).toFloat()
        xAxis.setLabelCount(3, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value < tempList.size / 3) {
                    return "A"
                }
                if (value > tempList.size * 2 / 3) {
                    return "B"
                }
                return ""
            }
        }

        var max = tempList.first()
        var min = tempList.first()
        val entryList: ArrayList<Entry> = ArrayList(tempList.size)
        for (i in tempList.indices) {
            val tempValue = tempList[i]
            max = max.coerceAtLeast(tempValue)
            min = min.coerceAtMost(tempValue)
            entryList.add(Entry(i.toFloat(), UnitTools.showUnitValue(tempValue)))
        }
        val maxUnit = UnitTools.showUnitValue(max)
        val minUnit = UnitTools.showUnitValue(min)
        axisLeft.axisMaximum = (maxUnit + (maxUnit - minUnit) / 3).coerceAtLeast(maxUnit + 0.3f)
        axisLeft.axisMinimum = (minUnit - (maxUnit - minUnit) / 3).coerceAtMost(minUnit - 0.3f)
        axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String = "${String.format("%.1f", value)}${UnitTools.showUnit()}"
        }

        val lineDataSet = LineDataSet(entryList, "point temp")
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.color = 0xffffffff.toInt()//[CN_TEXT]
        lineDataSet.circleHoleColor = 0xffffffff.toInt()//[CN_TEXT]
        lineDataSet.setCircleColor(0xffffffff.toInt())//[CN_TEXT]
        lineDataSet.valueTextColor = Color.WHITE
        lineDataSet.lineWidth = 2f
        lineDataSet.circleRadius = 1f//[CN_TEXT]
        lineDataSet.fillAlpha = 200
        lineDataSet.valueTextSize = 10f
        lineDataSet.setDrawValues(false)//Settings[CN_TEXT]

        data = LineData(lineDataSet)
        invalidate()
    }
}