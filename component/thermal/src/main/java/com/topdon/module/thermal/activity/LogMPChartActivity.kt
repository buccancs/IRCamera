package com.topdon.module.thermal.activity

import android.graphics.Color
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.elvishew.xlog.XLog
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.ToastTools
import com.topdon.module.thermal.R
import com.topdon.module.thermal.adapter.SettingTimeAdapter
import com.topdon.module.thermal.chart.MyValueFormatter
import com.topdon.module.thermal.view.MyMarkerView
import com.topdon.module.thermal.viewmodel.LogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Legacy ARouter route annotation - now using NavigationManager
class LogMPChartActivity : BaseActivity(), OnChartValueSelectedListener {
    private val viewModel: LogViewModel by viewModels()

    private val adapter: SettingTimeAdapter by lazy { SettingTimeAdapter(this) }

    //    private var dataList: ArrayList<ThermalEntity> = arrayListOf()
    private lateinit var chart: LineChart
    private var selectType = 1

    override fun initContentView() = R.layout.activity_log_mp_chart

    override fun initView() {
        // Set toolbar title
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(com.topdon.lib.core.R.id.toolbar_lay)
        toolbar?.title = getString(R.string.app_record)

        chart = findViewById(R.id.log_chart_time_chart)
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.log_chart_time_recycler)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter
        adapter.listener =
            object : SettingTimeAdapter.OnItemClickListener {
                override fun onClick(
                    index: Int,
                    time: Int,
                ) {
                    // switch[Chinese text]
                    chart.highlightValue(null) // [Chinese text]high[Chinese text]pointMarker
                    selectType = index + 1
                    queryLog()
                }
            }
        viewModel.resultLiveData.observe(this) {
            dismissLoadingDialog()
            try {
                initEntry(it.dataList)
            } catch (e: Exception) {
                XLog.e("[Chinese text]:${e.message}")
                ToastTools.showShort("[Chinese text], [Chinese text]")
            }
        }
        clearEntity(true)
    }

    override fun initData() {
        queryLog()
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun queryLog() {
        showLoadingDialog()
//        viewModel.queryLogByType(selectType)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.queryLogVolsByStartTime(
                type = 1, // Default fence type since getSelectFenceType() is not available
                selectTimeType = selectType,
            )
        }
    }

    private fun initChart() {
        chart.clear()
        chart.setOnChartValueSelectedListener(this)
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setDrawGridBackground(false)
        chart.description = null // [Chinese text]
        chart.setBackgroundResource(com.topdon.lib.core.R.color.chart_bg)
        chart.setScaleEnabled(true) // [Chinese text]
        chart.setPinchZoom(false) // [Chinese text], [Chinese text]x[Chinese text]y[Chinese text]
        chart.isDoubleTapToZoomEnabled = false // [Chinese text]
        chart.isScaleYEnabled = false // [Chinese text]Y[Chinese text]
        chart.setExtraOffsets(
            0f,
            0f,
            SizeUtils.dp2px(8f).toFloat(),
            SizeUtils.dp2px(4f).toFloat(),
        ) // [Chinese text]area[Chinese text]
        chart.setNoDataText(getString(R.string.lms_http_code998))
        chart.setNoDataTextColor(textColor)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv // Settingspoint[Chinese text]
        val data = LineData()
        data.setValueTextColor(textColor)
        chart.data = data
        val l = chart.legend
        l.form = Legend.LegendForm.CIRCLE
        l.textColor = textColor
        l.isEnabled = false // [Chinese text]line[Chinese text]
        val xAxis = chart.xAxis
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineColor = textColor
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true // [Chinese text]
        xAxis.textSize = 9f
        val leftAxis = chart.axisLeft
        leftAxis.textSize = 9f
        leftAxis.textColor = textColor
        leftAxis.setDrawGridLines(true)
        leftAxis.setLabelCount(6, false) // [Chinese text]x[Chinese text]
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
//        chart.zoom(10f, 1f, chart.xChartMax, 0f)
    }

    private val bgChartColors =
        intArrayOf(
            R.drawable.bg_chart_fill,
            R.drawable.bg_chart_fill2,
            R.drawable.bg_chart_fill3,
        )
    private val lineChartColors =
        intArrayOf(
            com.topdon.lib.core.R.color.chart_line_max,
            com.topdon.lib.core.R.color.chart_line_min,
            com.topdon.lib.core.R.color.chart_line_center,
        )
    private val textColor by lazy { ContextCompat.getColor(this, com.topdon.lib.core.R.color.chart_text) }

    /**
     * [Chinese text]line[Chinese text]
     */
    private fun createSet(
        index: Int,
        label: String,
    ): LineDataSet {
        val set = LineDataSet(null, label)
//        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.mode = LineDataSet.Mode.LINEAR
        set.setDrawFilled(false)
        set.fillDrawable = ContextCompat.getDrawable(this, bgChartColors[index]) // Settings[Chinese text]
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = ContextCompat.getColor(this, lineChartColors[index]) // [Chinese text]line[Chinese text]
        set.setCircleColor(ContextCompat.getColor(this, com.topdon.lib.core.R.color.white)) // [Chinese text]
//        set.fillColor = ContextCompat.getColor(this, R.color.purple_500)
//        set.highLightColor = ContextCompat.getColor(this, R.color.white)
        set.valueTextColor = Color.WHITE
        set.lineWidth = 2f
        set.circleRadius = 1f // [Chinese text]point
        set.setCircleColor(ContextCompat.getColor(this, lineChartColors[index])) // [Chinese text]([Chinese text])
//        set.setCircleColor(ContextCompat.getColor(this, R.color.white))// [Chinese text]([Chinese text])
        set.fillAlpha = 200
        set.valueTextSize = 10f
        set.setDrawValues(false) // Settings[Chinese text]
        return set
    }

    private fun initEntry(data: ArrayList<ThermalEntity>) {
        synchronized(chart) {
            lifecycleScope.launch(Dispatchers.IO) {
                clearEntity(data.size == 0)
                if (data.size == 0) {
                    return@launch
                }
                Log.i("chart", "update chart start")
                val lineData: LineData? = chart.data
                if (lineData != null) {
                    Log.w(
                        "123",
                        "[Chinese text]:${(data.last().createTime - data.first().createTime) / 1000}",
                    )
                    val startTime = data[0].createTime
                    Log.w("123", "Settings[Chinese text]startTime:$startTime")
                    chart.xAxis.valueFormatter =
                        MyValueFormatter(startTime = startTime, type = selectType)
                    XLog.w("chart init startTime:$startTime")
                    data[0].type = "default"
                    when (data[0].type) {
                        "point" -> {
                            var set = lineData.getDataSetByIndex(0) // [Chinese text]x[Chinese text]0[Chinese text]point
                            if (set == null) {
                                set = createSet(2, "temp")
                                lineData.addDataSet(set)
                            }
                            data.forEach {
                                val x = (it.createTime - startTime).toFloat()
                                val entity = Entry(x, it.thermal)
                                entity.data = it
                                set.addEntry(entity)
                            }
                            XLog.w("DataSet:${set.entryCount}")
                        }
                        "line" -> {
                            var maxDataSet = lineData.getDataSetByIndex(0) // [Chinese text]x[Chinese text]0[Chinese text]point
                            if (maxDataSet == null) {
                                maxDataSet = createSet(0, "line maxTemp")
                                lineData.addDataSet(maxDataSet)
                            }

                            var minDataSet = lineData.getDataSetByIndex(1) // [Chinese text]x[Chinese text]0[Chinese text]point
                            if (minDataSet == null) {
                                minDataSet = createSet(1, "line minTemp")
                                lineData.addDataSet(minDataSet)
                            }
                            Log.w("123", "[Chinese text]line")
                            data.forEach {
                                val x = (it.createTime - startTime).toFloat()
                                // max
                                val entity = Entry(x, it.thermalMax)
                                entity.data = it
                                maxDataSet.addEntry(entity)
                                // min
                                val entityMin = Entry(x, it.thermalMin)
                                entityMin.data = it
                                minDataSet.addEntry(entityMin)
                            }
                            XLog.w("DataSet:${maxDataSet.entryCount}")
                        }
                        else -> {
                            // max
                            var maxTempDataSet = lineData.getDataSetByIndex(0) // [Chinese text]x[Chinese text]0[Chinese text]point
                            if (maxTempDataSet == null) {
                                maxTempDataSet = createSet(0, "fence maxTemp")
                                lineData.addDataSet(maxTempDataSet)
                            }
                            // center
                            var centerTempDataSet = lineData.getDataSetByIndex(1) // [Chinese text]x[Chinese text]0[Chinese text]point
                            if (centerTempDataSet == null) {
                                centerTempDataSet = createSet(1, "fence minTemp")
                                lineData.addDataSet(centerTempDataSet)
                            }
                            data.forEach {
                                val x = (it.createTime - startTime).toFloat()
                                // max
                                val entityMax = Entry(x, it.thermalMax)
                                entityMax.data = it
                                maxTempDataSet.addEntry(entityMax)
                                // min
                                val entity = Entry(x, it.thermalMin)
                                entity.data = it
                                centerTempDataSet.addEntry(entity)
                            }
                            XLog.w("DataSet:${centerTempDataSet.entryCount}")
                        }
                    }
                    lineData.notifyDataChanged()
                    chart.notifyDataSetChanged()
                    chart.setVisibleXRangeMinimum(getMinimum()) // Settings[Chinese text]X[Chinese text]
                    chart.setVisibleXRangeMaximum(getMaximum()) // Settings[Chinese text]X[Chinese text]
                    chart.xAxis.setLabelCount(5, false) // true[Chinese text]
                    chart.moveViewToX(chart.xChartMax) // [Chinese text]
                    chart.zoom(1f, 1f, chart.xChartMax, 0f) // [Chinese text], [Chinese text]
                }
                Log.w("chart", "update chart finish")
            }
        }
    }

    override fun onValueSelected(
        e: Entry?,
        h: Highlight?,
    ) {
    }

    override fun onNothingSelected() {
    }

    /**
     * x[Chinese text]
     */
    private fun getLabCount(count: Int): Int {
        return when (count) {
            in 0..2 -> 1
            in 3..4 -> 2
            in 5..6 -> 3
            in 7..9 -> 4
            else -> 5
        }
    }

    // [Chinese text]
    private fun getMinimum(): Float {
        val min =
            when (selectType) {
                1 -> 1 * 10 * 1000f // 10s
                2 -> 10 * 60 * 1000f // 10min
                3 -> 10 * 60 * 60 * 1000f // 10hour
                4 -> 10 * 24 * 60 * 60 * 1000f // 10day
                else -> 1 * 10 * 1000f // 10s
            }
        return min
    }

    // [Chinese text], [Chinese text]50[Chinese text]
    private fun getMaximum(): Float {
        return getMinimum() * 50f
    }

    private fun clearEntity(isEmpty: Boolean) {
        initChart()
        if (isEmpty) {
            chart.clear()
        } else {
            chart.clearValues()
        }
    }
}
