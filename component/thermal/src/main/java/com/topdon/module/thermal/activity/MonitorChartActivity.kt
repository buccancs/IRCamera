package com.topdon.module.thermal.activity

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
// import com.guide.zm04c.matrix.GuideInterface // Temporarily disabled - hardware specific
import com.topdon.lib.core.bean.tools.ThermalBean
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.tools.NumberTools
import com.topdon.module.thermal.R
import com.topdon.module.thermal.adapter.SettingCheckAdapter
import com.topdon.module.thermal.adapter.SettingTimeAdapter
import com.topdon.module.thermal.chart.MyValueFormatter
import com.topdon.module.thermal.view.MyMarkerView
import com.topdon.module.thermal.viewmodel.LogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * [CN_TEXT]
 */
// Legacy ARouter route annotation - now using NavigationManager
class MonitorChartActivity : BaseActivity(), View.OnClickListener, OnChartValueSelectedListener {
    private val viewModel: LogViewModel by viewModels()

    private val timeAdapter: SettingTimeAdapter by lazy { SettingTimeAdapter(this) } // [CN_TEXT]
    private val adapter: SettingCheckAdapter by lazy { SettingCheckAdapter(this) } // [CN_TEXT]

    //    var MONITOR_ACTION = STATS_START
    private var selectDuration = 1
    private var selectType = 1 // [CN_TEXT] 1:[CN_TEXT]    2:[CN_TEXT]    3:[CN_TEXT]
    private var selectIndex: ArrayList<Int> = arrayListOf() // [CN_TEXT]
    private val bean = ThermalBean()
    private var selectTimeType = 1
    private var latestTime = 0L // [CN_TEXT]Current[CN_TEXT],[CN_TEXT]([CN_TEXT], [CN_TEXT], [CN_TEXT])[CN_TEXT]
    private var startMonitor = false

    private lateinit var chart: LineChart

    override fun initContentView() = R.layout.activity_monitor_chart

    override fun initView() {
        // Set toolbar title
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(com.topdon.lib.core.R.id.toolbar_lay)
        toolbar?.title = getString(R.string.main_thermal_motion)

        selectType = intent.getIntExtra("type", 3)
        selectIndex = intent.getIntegerArrayListExtra("select")!!
        Log.w("123", "selectType:$selectType")
        Log.w("123", "selectIndex:${selectIndex.joinToString()}")
        // SharedManager.setSelectFenceType(selectType) // Temporarily disabled
        type =
            when (selectType) {
                1 -> "point"
                2 -> "line"
                else -> "fence"
            }
        chart = findViewById(R.id.mp_chart_view)
        initChart()
        initRecycler()
        viewModel.resultLiveData.observe(this) {
            // [CN_TEXT]
            Log.w("123", "[CN_TEXT]:${it.dataList.size}")
            resultVol(it)
        }
        lifecycleScope.launch {
            delay(300)
            onIrVideoStart()
        }
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        onIrVideoStop()
    }

    override fun onClick(v: View?) {
        when (v) {
        }
    }

    private fun initRecycler() {
        val monitorChartTimeRecycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.monitor_chart_time_recycler)
        val monitorChartSettingRecycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.monitor_chart_setting_recycler)

        monitorChartTimeRecycler.layoutManager = GridLayoutManager(this, 4)
        monitorChartTimeRecycler.adapter = timeAdapter
        monitorChartSettingRecycler.layoutManager = GridLayoutManager(this, 3)
        monitorChartSettingRecycler.adapter = adapter
        // Settings[CN_TEXT]Type([CN_TEXT] [CN_TEXT] [CN_TEXT] [CN_TEXT])
        timeAdapter.listener =
            object : SettingTimeAdapter.OnItemClickListener {
                override fun onClick(
                    index: Int,
                    timeType: Int,
                ) {
                    selectTimeType = timeType
                    chart.highlightValue(null) // [CN_TEXT]Marker
                    latestTime = 0L
                    showLoadingDialog()
                    lifecycleScope.launch {
                        delay(500)
                        queryLog(2)
                    }
                }
            }
        // [CN_TEXT]
        adapter.listener =
            object : SettingCheckAdapter.OnItemClickListener {
                override fun onClick(
                    index: Int,
                    time: Int,
                ) {
                    if (recordTask != null && recordTask!!.isActive) {
                        recordTask!!.cancel()
                        recordTask = null
                    }
//                canUpdate = false
                    Log.w("123", "select:$time")
                    adapter.setCheck(index)
                    timeMillis = time * 1000L
                    pointIndex = startIndex - defaultCount
                    recordThermal() // [CN_TEXT]
                }
            }
    }

    val defaultCount = 20 // [CN_TEXT]10[CN_TEXT]
    val startIndex = 0f
    var pointIndex = startIndex - defaultCount

    // /////////
    var mIsIrVideoStart = false

    // private var mGuideInterface: GuideInterface? = null // Temporarily disabled - hardware specific
    var rotateType = 3

    /**
     * [CN_TEXT]
     */
    private fun onIrVideoStart() {
        // Temporarily disabled - guide interface not available
        /*
        mIsIrVideoStart = if (mIsIrVideoStart) {
            ToastUtils.showShort("[CN_TEXT]")
            return
        } else {
            true
        }
        mGuideInterface = GuideInterface()
        val ret = mGuideInterface!!.init(this, object : GuideInterface.IrDataCallback {
            override fun processIrData(yuv: ByteArray, temp: FloatArray) {
                try {
                    //[CN_TEXT]
                    val centerTempIndex: Int = 256 * (192 / 2) + 256 / 2
                    val maxTempIndex = ArrayUtils.getMaxIndex(temp, rotateType, selectIndex)
                    val minTempIndex = ArrayUtils.getMinIndex(temp, rotateType, selectIndex)
                    val rotateData = ArrayUtils.matrixRotate(srcData = temp, rotateType)
                    val bigDecimal = BigDecimal.valueOf(rotateData[centerTempIndex].toDouble())
                    val maxBigDecimal = BigDecimal.valueOf(rotateData[maxTempIndex].toDouble())
                    val minBigDecimal = BigDecimal.valueOf(rotateData[minTempIndex].toDouble())
                    bean.centerTemp = bigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                    bean.maxTemp = maxBigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                    bean.minTemp = minBigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                    bean.createTime = System.currentTimeMillis()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "[CN_TEXT]:${e.message}")
                }
            }

        })

        if (ret == 5) {
            Log.w("123", "[CN_TEXT]")
            recordThermal()//[CN_TEXT]
        } else {
//            ToastUtils.showShort("[CN_TEXT]")
            Log.w("123", "[CN_TEXT]")
            mGuideInterface = null
            mIsIrVideoStart = false
        }
         */
    }

    /**
     * [CN_TEXT]
     */
    private fun onIrVideoStop() {
        // Temporarily disabled - guide interface not available
        /*
        mIsIrVideoStart = if (!mIsIrVideoStart) {
            Log.w("123", "[CN_TEXT]")
            return
        } else {
            false
        }
        mGuideInterface!!.exit()
        mGuideInterface = null
        Log.w("123", "[CN_TEXT]")
         */
    }

    var isRecord = false
    var type = ""
    var timeMillis = 1000L // [CN_TEXT]1s
    var canUpdate = false
    var recordTask: Job? = null
    var thermalId = TimeTool.showDateSecond()
    var startTime = 0L

    /**
     * [CN_TEXT]-[CN_TEXT]
     */
    private fun recordThermal() {
        recordTask =
            lifecycleScope.launch(Dispatchers.IO) {
                isRecord = true
                startTime = System.currentTimeMillis()
                var time = 0L
                while (isRecord) {
                    if (canUpdate) {
                        val entity = ThermalEntity()
                        entity.userId = SharedManager.getUserId()
                        entity.thermalId = thermalId
                        entity.thermal = NumberTools.to02f(bean.centerTemp)
                        entity.thermalMax = NumberTools.to02f(bean.maxTemp)
                        entity.thermalMin = NumberTools.to02f(bean.minTemp)
                        entity.type = type
                        entity.startTime = startTime
                        entity.createTime = System.currentTimeMillis()
                        AppDatabase.getInstance().thermalDao().insert(entity)
                        time++
                        launch(Dispatchers.Main) {
                            updateChart()
                        }
                        delay(timeMillis)
                    } else {
                        Log.w("123", "Current[CN_TEXT]")
                    }
                }
                Log.w("123", "[CN_TEXT], [CN_TEXT]:$time")
            }
    }

    // MPChart
    private fun initChart() {
        chart.clear()
        chart.setOnChartValueSelectedListener(this)
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setDrawGridBackground(false)
        chart.description = null // [CN_TEXT]
        chart.setBackgroundResource(com.topdon.lib.core.R.color.chart_bg)
        chart.setScaleEnabled(true) // [CN_TEXT]
        chart.setPinchZoom(false) // [CN_TEXT]，[CN_TEXT]x[CN_TEXT]y[CN_TEXT]
        chart.isDoubleTapToZoomEnabled = false // [CN_TEXT]
        chart.isScaleYEnabled = false // [CN_TEXT]Y[CN_TEXT]
        chart.setExtraOffsets(
            0f,
            0f,
            SizeUtils.dp2px(8f).toFloat(),
            SizeUtils.dp2px(4f).toFloat(),
        ) // [CN_TEXT]
        chart.setNoDataText(getString(R.string.lms_http_code998))
        chart.setNoDataTextColor(textColor)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv // Settings[CN_TEXT]
        val data = LineData()
        data.setValueTextColor(textColor)
        chart.data = data
        val l = chart.legend
        l.form = Legend.LegendForm.CIRCLE
        l.textColor = textColor
        l.isEnabled = false // [CN_TEXT]
        val xAxis = chart.xAxis
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineColor = textColor
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true // [CN_TEXT]
        xAxis.textSize = 9f
//        xAxis.setLabelCount(6, true)//true[CN_TEXT]
        xAxis.setLabelCount(6, false) // true[CN_TEXT]
        val leftAxis = chart.axisLeft
        leftAxis.textSize = 9f
        leftAxis.textColor = textColor
        leftAxis.setDrawGridLines(true)
        leftAxis.setLabelCount(6, true)
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        chart.zoom(100f, 1f, chart.xChartMax, 0f)
        selectDuration = adapter.selectTime
        startTime = System.currentTimeMillis()
        canUpdate = true // [CN_TEXT]
    }

    /**
     * [CN_TEXT]
     */
    private fun updateChart() {
        ++pointIndex
        when (selectTimeType) {
            1 -> {
                // [CN_TEXT]
                addPointToChart(bean)
            }
            2 -> {
                // [CN_TEXT]
                val addTime = 2 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 2) + addTime) {
                    queryLog(3)
                }
            }
            3 -> {
                // [CN_TEXT]
                val addTime = 2 * 60 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 3) + addTime) {
                    queryLog(3)
                }
            }
            4 -> {
                // [CN_TEXT]([CN_TEXT]，[CN_TEXT])
                val addTime = 2 * 24 * 60 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 4) + addTime) {
                    queryLog(3)
                }
            }
        }
    }

    /**
     * [CN_TEXT]
     */
    private fun addPointToChart(bean: ThermalBean) {
        synchronized(chart) {
            try {
                if (bean.createTime == 0L) {
                    Log.w("123", "createTime = 0L, bean:$bean")
                    return
                }
                val data = ThermalEntity()
                data.thermalMax = bean.maxTemp
                data.thermalMin = bean.minTemp
                data.thermal = bean.centerTemp
                data.createTime = bean.createTime
                val lineData: LineData = chart.data
                var volDataSet = lineData.getDataSetByIndex(0) // [CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                if (volDataSet == null) {
                    startTime = data.createTime
                    Log.w("123", "Settings[CN_TEXT]startTime:$startTime")
                    chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime)
                }
                val x = (data.createTime - startTime).toFloat()
                when (type) {
                    "point" -> {
                        if (volDataSet == null) {
                            volDataSet = createSet("green")
                            lineData.addDataSet(volDataSet)
                            Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                        }
                        val entity = Entry(x, data.thermal)
                        entity.data = data
                        volDataSet.addEntry(entity)
                        Log.w("123", "[CN_TEXT]:$entity")
                    }
                    "line" -> {
                        // [CN_TEXT]
                        if (volDataSet == null) {
                            volDataSet = createSet("red")
                            lineData.addDataSet(volDataSet)
                            Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                        }
                        val entity = Entry(x, data.thermalMax)
                        entity.data = data
                        volDataSet.addEntry(entity)

                        // [CN_TEXT]
                        var secondDataSet = lineData.getDataSetByIndex(1) // [CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                        if (secondDataSet == null) {
                            secondDataSet = createSet("blue")
                            lineData.addDataSet(secondDataSet)
                        }
                        val secondEntity = Entry(x, data.thermalMin)
                        secondEntity.data = data
                        secondDataSet.addEntry(secondEntity)
                    }
                    else -> {
                        // [CN_TEXT]
                        if (volDataSet == null) {
                            volDataSet = createSet("red")
                            lineData.addDataSet(volDataSet)
                        }
                        val entity = Entry(x, data.thermalMax)
                        entity.data = data
                        volDataSet.addEntry(entity)

                        // [CN_TEXT]
                        var secondDataSet = lineData.getDataSetByIndex(1) // [CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                        if (secondDataSet == null) {
                            secondDataSet = createSet("blue")
                            lineData.addDataSet(secondDataSet)
                        }
                        val secondEntity = Entry(x, data.thermalMin)
                        secondEntity.data = data
                        secondDataSet.addEntry(secondEntity)
                    }
                }

                lineData.notifyDataChanged()
                chart.notifyDataSetChanged()
                chart.setVisibleXRangeMinimum(getMinimum()) // Settings[CN_TEXT]X[CN_TEXT]
                chart.setVisibleXRangeMaximum(getMaximum()) // Settings[CN_TEXT]X[CN_TEXT]
                chart.xAxis.setLabelCount(getLabCount(volDataSet.entryCount), false) // true[CN_TEXT]
                chart.moveViewToX(chart.xChartMax) // [CN_TEXT]
                if (volDataSet.entryCount == 20) {
                    chart.zoom(100f, 1f, chart.xChartMax, 0f)
                }
                return@synchronized
            } catch (e: Exception) {
                Log.e("123", "[CN_TEXT]:${e.message}")
                return@synchronized
            }
        }
    }

    private val fillColor by lazy { ContextCompat.getDrawable(this, R.drawable.bg_chart_fill2) }
    private val lineRed by lazy { ContextCompat.getColor(this, com.topdon.lib.core.R.color.chart_line_max) }
    private val lineBlue by lazy { ContextCompat.getColor(this, com.topdon.lib.core.R.color.chart_line_min) }
    private val lineGreen by lazy { ContextCompat.getColor(this, com.topdon.lib.core.R.color.chart_line_center) }
    private val whiteColors by lazy { ContextCompat.getColor(this, com.topdon.lib.core.R.color.circle_white) }
    private val textColor by lazy { ContextCompat.getColor(this, com.topdon.lib.core.R.color.chart_text) }

    /**
     * [CN_TEXT]
     */
    private fun createSet(label: String): LineDataSet {
        val set = LineDataSet(null, label)
//        set.mode = LineDataSet.Mode.LINEAR
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawFilled(false)
//        set.fillDrawable = fillColor//Settings[CN_TEXT]
        set.axisDependency = YAxis.AxisDependency.LEFT

        when (label) {
            "red" -> {
                set.color = lineRed // [CN_TEXT]
                set.circleHoleColor = lineRed // [CN_TEXT]
            }
            "blue" -> {
                set.color = lineBlue // [CN_TEXT]
                set.circleHoleColor = lineBlue // [CN_TEXT]
            }
            else -> {
                set.color = lineGreen // [CN_TEXT]
                set.circleHoleColor = lineGreen // [CN_TEXT]
            }
        }

        set.setCircleColor(whiteColors) // [CN_TEXT]
        set.circleHoleRadius = 4f // [CN_TEXT]
        set.circleRadius = 5f // [CN_TEXT]
        set.valueTextColor = Color.WHITE
        set.lineWidth = 2f
        set.fillAlpha = 200
        set.valueTextSize = 10f
        set.setDrawValues(false) // Settings[CN_TEXT]
        set.isHighlightEnabled = true // [CN_TEXT]
        set.setDrawHorizontalHighlightIndicator(false) // [CN_TEXT]
        set.enableDashedHighlightLine(8f, 8f, 0f) // [CN_TEXT]
        return set
    }

    /**
     * [CN_TEXT]([CN_TEXT])
     * [CN_TEXT]: [CN_TEXT] => [CN_TEXT]
     *
     * @param action
     * 0: [CN_TEXT]
     * 1: [CN_TEXT]
     * 2: Switch[CN_TEXT]
     * 3: [CN_TEXT]
     * 4: [CN_TEXT]
     */
    private fun queryLog(action: Int) {
        startMonitor = false
        lifecycleScope.launch(Dispatchers.IO) {
//            dataList.clear()//[CN_TEXT]
//            dataList = arrayListOf()
            viewModel.queryLogThermals(selectTimeType = selectTimeType, action = action)
        }
    }

    private fun resultVol(bean: LogViewModel.ChartList) {
        dismissLoadingDialog()
        if (selectTimeType != 1 && bean.dataList.size > 0) {
            val logTime = TimeTool.showDateType(bean.dataList.last().createTime, selectTimeType)
            val nowTime = TimeTool.showDateType(System.currentTimeMillis(), selectTimeType)
            if (TextUtils.equals(logTime, nowTime)) {
                // [CN_TEXT],Current[CN_TEXT]，[CN_TEXT]Delete[CN_TEXT]Current[CN_TEXT]
                bean.dataList.removeLast()
            }
        }
//        dataList = bean.dataList
        if (latestTime == 0L) {
            // [CN_TEXT]
            addEntity(bean.dataList)
        } else if (bean.dataList.size > 0 && latestTime < bean.dataList.last().createTime) {
            // [CN_TEXT]
            addEntity(bean.dataList)
        }
    }

    // [CN_TEXT]
    private fun addEntity(data: ArrayList<ThermalEntity>) {
        clearEntity(data.size == 0)
        if (data.size == 0) {
            return
        }
        latestTime = data[data.size - 1].createTime
        startTime = data[0].createTime
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime)
        val lineData: LineData = chart.data
        var volDataSet = lineData.getDataSetByIndex(0) // [CN_TEXT]x[CN_TEXT]0[CN_TEXT]
        if (volDataSet == null) {
            volDataSet = createSet("vol")
            lineData.addDataSet(volDataSet)
        }
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime, type = selectTimeType)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv // Settings[CN_TEXT]
        data.forEach {
            val x = (it.createTime - startTime).toFloat()
            when (type) {
                "point" -> {
                    if (volDataSet == null) {
                        volDataSet = createSet("green")
                        lineData.addDataSet(volDataSet)
                        Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                    }
                    val entity = Entry(x, it.thermal)
                    entity.data = it
                    volDataSet.addEntry(entity)
                    Log.w("123", "[CN_TEXT]:$entity")
                }
                "line" -> {
                    // [CN_TEXT]
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                        Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    // [CN_TEXT]
                    var secondDataSet = lineData.getDataSetByIndex(1) // [CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                    if (secondDataSet == null) {
                        secondDataSet = createSet("blue")
                        lineData.addDataSet(secondDataSet)
                    }
                    val secondEntity = Entry(x, it.thermalMin)
                    secondEntity.data = it
                    secondDataSet.addEntry(secondEntity)
                }
                else -> {
                    // [CN_TEXT]
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    // [CN_TEXT]
                    var secondDataSet = lineData.getDataSetByIndex(1) // [CN_TEXT]x[CN_TEXT]0[CN_TEXT]
                    if (secondDataSet == null) {
                        secondDataSet = createSet("blue")
                        lineData.addDataSet(secondDataSet)
                    }
                    val secondEntity = Entry(x, it.thermalMax)
                    secondEntity.data = it
                    secondDataSet.addEntry(secondEntity)
                }
            }
        }
        Log.w("123", "[CN_TEXT]:${volDataSet.entryCount}[CN_TEXT]")
        lineData.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMinimum(getMinimum()) // Settings[CN_TEXT]X[CN_TEXT]
        chart.setVisibleXRangeMaximum(getMaximum()) // Settings[CN_TEXT]X[CN_TEXT]
        Log.i(
            "123",
            "list moveViewToX:${chart.xChartMax}, chart.highestVisibleX:${chart.highestVisibleX}",
        )
        chart.moveViewToX(chart.xChartMax) // [CN_TEXT]
        chart.xAxis.setLabelCount(getLabCount(volDataSet.entryCount), false) // true[CN_TEXT]
        chart.zoom(100f, 1f, chart.xChartMax, 0f)
        startMonitor = true
    }

    private fun clearEntity(isEmpty: Boolean) {
        initChart()
        if (isEmpty) {
            chart.clear()
        } else {
            chart.clearValues()
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
     * x[CN_TEXT]
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

    // [CN_TEXT]
    private fun getMinimum(): Float {
        val min =
            when (selectTimeType) {
                1 -> 1 * 10 * 1000f // 10s
                2 -> 10 * 60 * 1000f // 10min
                3 -> 10 * 60 * 60 * 1000f // 10hour
                4 -> 10 * 24 * 60 * 60 * 1000f // 10day
                else -> 1 * 10 * 1000f // 10s
            }
        return min
    }

    // [CN_TEXT]，[CN_TEXT]50[CN_TEXT]
    private fun getMaximum(): Float {
        return getMinimum() * 50f
    }
}
