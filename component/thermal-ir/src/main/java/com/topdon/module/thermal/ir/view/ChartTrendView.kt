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

/**
 * Custom Chart trend view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class ChartTrendView : LineChart {
 constructor(context: Context) : this(context, null)
 constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
 constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
 val textColor: Int = ContextCompat.getColor(context, LibR.color.chart_text)
 val axisChartColors: Int = ContextCompat.getColor(context, LibR.color.chart_axis)

 this.isDragEnabled = false
 this.isScaleYEnabled = false // Y
 this.isScaleXEnabled = false // X
 this.isDoubleTapToZoomEnabled = false // 
 this.setScaleEnabled(false) // 
 this.setPinchZoom(false) // ，xy
 this.setTouchEnabled(true)
 this.setDrawGridBackground(false)
 this.description = null // 
 this.axisRight.isEnabled = false // Y
 this.setExtraOffsets(
 0f,
 0f,
 SizeUtils.dp2px(8f).toFloat(),
 SizeUtils.dp2px(4f).toFloat(),
 ) // 

 setNoDataText(context.getString(ThermalR.string.lms_http_code998))
 setNoDataTextColor(ContextCompat.getColor(context, LibR.color.chart_text))

 val mv = MyMarkerView(context, R.layout.marker_lay)
 mv.chartView = this
 marker = mv // Settings

 legend.form = Legend.LegendForm.CIRCLE
 legend.textColor = textColor
 legend.isEnabled = false // 

\1x
 val xAxis = this.xAxis
 xAxis.textColor = textColor
 xAxis.setDrawGridLines(false) // 
 xAxis.axisLineColor = 0x00000000 // x
 xAxis.setAvoidFirstLastClipping(true)
 xAxis.isEnabled = true
 xAxis.position = XAxis.XAxisPosition.BOTTOM
 xAxis.granularity = 1f
 xAxis.isGranularityEnabled = true // Value
 xAxis.textSize = 11f
 xAxis.isJumpFirstLabel = false
 xAxis.axisMinimum = 0f
 xAxis.axisMaximum = 10f
 xAxis.setLabelCount(3, true)
 xAxis.valueFormatter =
 object : ValueFormatter() {
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

\1y
 val leftAxis = this.axisLeft
 leftAxis.textColor = textColor // y
 leftAxis.axisLineColor = 0x00000000 // y
 leftAxis.setDrawGridLines(true) // 
 leftAxis.gridColor = axisChartColors // y
 leftAxis.gridLineWidth = 1.5f
 leftAxis.setLabelCount(6, true)
 leftAxis.valueFormatter =
 object : ValueFormatter() {
 override fun getFormattedValue(value: Float): String = ""
 }
 leftAxis.textSize = 11f
 leftAxis.axisMinimum = 0f
 leftAxis.axisMaximum = 50f

 data = LineData()
 }

 fun setToEmpty() {
 axisLeft.valueFormatter =
 object : ValueFormatter() {
 override fun getFormattedValue(value: Float): String = ""
 }
 data = LineData()
 invalidate()
 }

 /**
 * Refreshes chart data based on the provided temperature list.
 * @param tempList List of temperature values in Celsius.
 */
 fun refresh(tempList: List<Float>) {
 if (tempList.isEmpty()) {
 setToEmpty()
 return
 }

 xAxis.axisMinimum = 0f
 xAxis.axisMaximum = (tempList.size - 1).toFloat()
 xAxis.setLabelCount(3, true)
 xAxis.valueFormatter =
 object : ValueFormatter() {
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
 axisLeft.valueFormatter =
 object : ValueFormatter() {
 override fun getFormattedValue(value: Float): String = "${String.format("%.1f", value)}${UnitTools.showUnit()}"
 }

 val lineDataSet = LineDataSet(entryList, "point temp")
 lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
 lineDataSet.color = 0xffffffff.toInt() // 
 lineDataSet.circleHoleColor = 0xffffffff.toInt() // 
 lineDataSet.setCircleColor(0xffffffff.toInt()) // 
 lineDataSet.valueTextColor = Color.WHITE
 lineDataSet.lineWidth = 2f
 lineDataSet.circleRadius = 1f // 
 lineDataSet.fillAlpha = 200
 lineDataSet.valueTextSize = 10f
 lineDataSet.setDrawValues(false) // SettingsWhetherValue

 data = LineData(lineDataSet)
 invalidate()
 }
}
