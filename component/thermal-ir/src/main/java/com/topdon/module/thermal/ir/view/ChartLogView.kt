package com.topdon.module.thermal.ir.view

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.elvishew.xlog.XLog
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.module.thermal.ir.R
import com.topdon.lib.core.R as LibR
import com.topdon.lib.core.R as LibcoreR
import com.topdon.module.thermal.R as ThermalR
import com.topdon.module.thermal.ir.chart.IRMyValueFormatter
import com.topdon.module.thermal.ir.chart.YValueFormatter
import com.topdon.module.thermal.ir.utils.ChartTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChartLogView : LineChart {

    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initChart()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
    }

    private val textColor by lazy { ContextCompat.getColor(context, LibcoreR.color.chart_text) }
    private val axisChartColors by lazy { ContextCompat.getColor(context, LibcoreR.color.chart_axis) }
    private val axisLine by lazy { ContextCompat.getColor(context, LibcoreR.color.circle_white) }

    //MPChart
    private fun initChart() {
        synchronized(this) {
            this.setTouchEnabled(true)
            this.isDragEnabled = true
            this.setDrawGridBackground(false)
            this.description = null//[CN_TEXT]
            this.setBackgroundResource(LibcoreR.color.chart_bg)
            this.setScaleEnabled(false)//[CN_TEXT]
            this.setPinchZoom(false)//[CN_TEXT]，[CN_TEXT]x[CN_TEXT]y[CN_TEXT]
            this.isDoubleTapToZoomEnabled = false//[CN_TEXT]
            this.isScaleYEnabled = false//[CN_TEXT]Y[CN_TEXT]
            this.isScaleXEnabled = true//[CN_TEXT]X[CN_TEXT]
            this.setExtraOffsets(
                0f,
                0f,
                SizeUtils.dp2px(8f).toFloat(),
                SizeUtils.dp2px(4f).toFloat()
            )//[CN_TEXT]
            setNoDataText(context.getString(ThermalR.string.lms_http_code998))
            setNoDataTextColor(ContextCompat.getColor(context, LibcoreR.color.chart_text))
            val mv = MyMarkerView(context, R.layout.marker_lay)
            mv.chartView = this
            marker = mv//Settings[CN_TEXT]
            val data = LineData()
            data.setValueTextColor(textColor)
            this.data = data
            val l = this.legend
            l.form = Legend.LegendForm.CIRCLE
            l.textColor = textColor
            l.isEnabled = false//[CN_TEXT]
            //x[CN_TEXT]
            val xAxis = this.xAxis
            xAxis.textColor = textColor
            xAxis.setDrawGridLines(false)//[CN_TEXT]
            xAxis.gridColor = axisChartColors //x[CN_TEXT]
            xAxis.axisLineColor = 0x00000000 //x[CN_TEXT]
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.isEnabled = true
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true//[CN_TEXT]
            xAxis.textSize = 8f
            //y[CN_TEXT]
            val leftAxis = this.axisLeft
            leftAxis.textColor = textColor //y[CN_TEXT]
            leftAxis.axisLineColor = 0x00000000 //y[CN_TEXT]
            leftAxis.setDrawGridLines(true)//[CN_TEXT]
            leftAxis.gridColor = axisChartColors //y[CN_TEXT]
            leftAxis.gridLineWidth = 1.5f
            leftAxis.setLabelCount(6, true)
            leftAxis.valueFormatter = YValueFormatter()//Settings[CN_TEXT]
            leftAxis.textSize = 8f

            this.axisRight.isEnabled = false
        }
    }


    fun initEntry(data: ArrayList<ThermalEntity>, type: Int = 1) {
        synchronized(this) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    clearEntity(data.size == 0)
                } catch (e: Exception) {
                    Log.e("chart", "clearEntity error: ${e.message}")
                }
                Log.w("chart", "clearEntity finish")
                if (data.size == 0) {
                    return@launch
                }
                Log.w("chart", "update chart start")
                val lineData: LineData = this@ChartLogView.data
                if (lineData != null) {
                    val startTime = data[0].createTime / 1000 * 1000  //[CN_TEXT] ([CN_TEXT],[CN_TEXT]x[CN_TEXT])
                    xAxis.valueFormatter = IRMyValueFormatter(startTime = startTime, type = type)
                    XLog.w("chart init startTime:$startTime")
//                    data[0].type = "default"
                    when (data[0].type) {
                        "point" -> {
                            var set = lineData.getDataSetByIndex(0)//[CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                            if (set == null) {
                                set = createSet(0, "point temp")
                                lineData.addDataSet(set)
                            }
                            Log.w("123", "[CN_TEXT]")
                            data.forEach {
                                val x = ChartTools.getChartX(
                                    x = it.createTime,
                                    startTime = startTime,
                                    type = type
                                ).toFloat()
                                val entity = Entry(x, it.thermal)
                                entity.data = it
                                set.addEntry(entity)
                            }
                            XLog.w("DataSet:${set.entryCount}")
                        }
                        "line" -> {
                            var maxDataSet = lineData.getDataSetByIndex(0)//[CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                            if (maxDataSet == null) {
                                maxDataSet = createSet(0, "line max temp")

                            }

                            var minDataSet = lineData.getDataSetByIndex(1)//[CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                            if (minDataSet == null) {
                                minDataSet = createSet(1, "line min temp")

                            }
                            Log.w("123", "[CN_TEXT]")
                            data.forEach {
                                val x = ChartTools.getChartX(
                                    x = it.createTime,
                                    startTime = startTime,
                                    type = type
                                ).toFloat()
//                                Log.w("123", "x: $x")
                                //max
                                val entity = Entry(x, it.thermalMax)
                                entity.data = it
                                maxDataSet.addEntry(entity)
                                //min
                                val entityMin = Entry(x, it.thermalMin)
                                entityMin.data = it
                                minDataSet.addEntry(entityMin)
                            }
                            lineData.addDataSet(maxDataSet)
                            lineData.addDataSet(minDataSet)
                            XLog.w("DataSet:${maxDataSet.entryCount}")
                        }
                        else -> {
                            //max
                            var maxTempDataSet = lineData.getDataSetByIndex(0)//[CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                            if (maxTempDataSet == null) {
                                maxTempDataSet = createSet(0, "fence max temp")
                                lineData.addDataSet(maxTempDataSet)
                            }
                            //center
                            var centerTempDataSet = lineData.getDataSetByIndex(1)//[CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                            if (centerTempDataSet == null) {
                                centerTempDataSet = createSet(1, "fence min temp")
                                lineData.addDataSet(centerTempDataSet)
                            }
                            Log.w("123", "[CN_TEXT]")
                            data.forEach {
                                val x = ChartTools.getChartX(
                                    x = it.createTime,
                                    startTime = startTime,
                                    type = type
                                ).toFloat()
                                //max
                                val entityMax = Entry(x, it.thermalMax)
                                entityMax.data = it
                                maxTempDataSet.addEntry(entityMax)
                                //min
                                val entity = Entry(x, it.thermalMin)
                                entity.data = it
                                centerTempDataSet.addEntry(entity)
                            }
                            XLog.w("DataSet:${centerTempDataSet.entryCount}")
                        }
                    }
                    lineData.notifyDataChanged()
                    notifyDataSetChanged()
                    moveViewToX(xChartMin)
                    setVisibleXRangeMinimum(ChartTools.getMinimum(type = type) / 2)//Settings[CN_TEXT]X[CN_TEXT]
                    setVisibleXRangeMaximum(ChartTools.getMaximum(type = type))//Settings[CN_TEXT]X[CN_TEXT]
                    zoom(1f, 1f, xChartMin, 0f)//[CN_TEXT]，[CN_TEXT]
                    ChartTools.setX(this@ChartLogView, type)
//                    ChartTools.setY(this@ChartTempView)
                }
                Log.w("chart", "update chart finish")
            }
        }
    }

    private val bgChartColors = intArrayOf(
        R.drawable.bg_chart_fill,
        R.drawable.bg_chart_fill2,
        R.drawable.bg_chart_fill3
    )
    private val lineChartColors = intArrayOf(
        LibcoreR.color.chart_line_max,
        LibcoreR.color.chart_line_min,
        LibcoreR.color.chart_line_center
    )

    private val linePointColors = intArrayOf(
        LibR.color.chart_point_max,
        LibR.color.chart_point_min,
        LibR.color.chart_point_center
    )

    /**
     * [CN_TEXT]
     */
    private fun createSet(index: Int, label: String): LineDataSet {
        val set = LineDataSet(null, label)
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.setDrawFilled(false)
        set.fillDrawable = ContextCompat.getDrawable(context, bgChartColors[index])//Settings[CN_TEXT]
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = ContextCompat.getColor(context, lineChartColors[index])//[CN_TEXT]
        set.circleHoleColor = ContextCompat.getColor(context, linePointColors[index])//[CN_TEXT]
        set.setCircleColor(ContextCompat.getColor(context, lineChartColors[index]))//[CN_TEXT]
        set.valueTextColor = Color.WHITE
        set.lineWidth = 2f
        set.circleRadius = 1f//[CN_TEXT]
        set.fillAlpha = 200
        set.valueTextSize = 10f
        set.setDrawValues(false)//Settings[CN_TEXT]
        return set
    }

    private fun clearEntity(isEmpty: Boolean) {
        initChart()
        if (isEmpty) {
            clear() //[CN_TEXT]
        } else {
            clearValues()
        }
    }

}