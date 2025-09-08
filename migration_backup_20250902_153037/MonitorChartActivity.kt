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
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.guide.zm04c.matrix.GuideInterface
import com.topdon.lib.core.bean.tools.ThermalBean
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.TimeTool
import com.topdon.module.thermal.R
import com.topdon.module.thermal.adapter.SettingCheckAdapter
import com.topdon.module.thermal.adapter.SettingTimeAdapter
import com.topdon.module.thermal.chart.MyValueFormatter
import com.topdon.module.thermal.utils.ArrayUtils
import com.topdon.module.thermal.view.MyMarkerView
import com.topdon.module.thermal.viewmodel.LogViewModel
import kotlinx.android.synthetic.main.activity_monitor_chart.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

/**
 * temperature[Chinese text]
 */
@Route(path = RouterConfig.MONITOR_CHART)
class MonitorChartActivity : BaseActivity(), View.OnClickListener, OnChartValueSelectedListener {

    private val viewModel: LogViewModel by viewModels()

    private val timeAdapter: SettingTimeAdapter by lazy { SettingTimeAdapter(this) }//[Chinese text]
    private val adapter: SettingCheckAdapter by lazy { SettingCheckAdapter(this) }//[Chinese text]

    //    var MONITOR_ACTION = STATS_START
    private var selectDuration = 1
    private var selectType = 1//[Chinese text]point 1:[Chinese text]point    2:line[Chinese text]    3:area
    private var selectIndex: ArrayList<Int> = arrayListOf()//[Chinese text]point
    private val bean = ThermalBean()
    private var selectTimeType = 1
    private var latestTime = 0L//[Chinese text],for[Chinese text]([Chinese text], [Chinese text], [Chinese text])[Chinese text]
    private var startMonitor = false

    private lateinit var chart: LineChart

    override fun initContentView() = R.layout.activity_monitor_chart

    override fun initView() {
        setTitleText(R.string.main_thermal_motion)
        selectType = intent.getIntExtra("type", 3)
        selectIndex = intent.getIntegerArrayListExtra("select")!!
        Log.w("123", "selectType:$selectType")
        Log.w("123", "selectIndex:${selectIndex.joinToString()}")
        SharedManager.setSelectFenceType(selectType)
        type = when (selectType) {
            1 -> "point"
            2 -> "line"
            else -> "fence"
        }
        chart = mp_chart_view
        initChart()
        initRecycler()
        viewModel.resultLiveData.observe(this)
        {
            //[Chinese text]
            Log.w("123", "[Chinese text]:${it.dataList.size}")
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
        monitor_chart_time_recycler.layoutManager = GridLayoutManager(this, 4)
        monitor_chart_time_recycler.adapter = timeAdapter
        monitor_chart_setting_recycler.layoutManager = GridLayoutManager(this, 3)
        monitor_chart_setting_recycler.adapter = adapter
        //settings[Chinese text]([Chinese text] [Chinese text] [Chinese text] [Chinese text])
        timeAdapter.listener = object : SettingTimeAdapter.OnItemClickListener {
            override fun onClick(index: Int, timeType: Int) {
                selectTimeType = timeType
                chart.highlightValue(null) //[Chinese text]high[Chinese text]pointMarker
                latestTime = 0L
                showLoading()
                lifecycleScope.launch {
                    delay(500)
                    queryLog(2)
                }
            }
        }
        //[Chinese text]
        adapter.listener = object : SettingCheckAdapter.OnItemClickListener {
            override fun onClick(index: Int, time: Int) {
                if (recordTask != null && recordTask!!.isActive) {
                    recordTask!!.cancel()
                    recordTask = null
                }
//                canUpdate = false
                Log.w("123", "select:$time")
                adapter.setCheck(index)
                timeMillis = time * 1000L
                pointIndex = startIndex - defaultCount
                recordThermal()//start[Chinese text]
            }
        }
    }

    val defaultCount = 20//[Chinese text]10[Chinese text]
    val startIndex = 0f
    var pointIndex = startIndex - defaultCount

    ///////////
    var mIsIrVideoStart = false

    private var mGuideInterface: GuideInterface? = null
    var rotateType = 3

    /**
     * [Chinese text]
     */
    private fun onIrVideoStart() {
        mIsIrVideoStart = if (mIsIrVideoStart) {
            ToastUtils.showShort("[Chinese text]")
            return
        } else {
            true
        }
        mGuideInterface = GuideInterface()
        val ret = mGuideInterface!!.init(this, object : GuideInterface.IrDataCallback {
            override fun processIrData(yuv: ByteArray, temp: FloatArray) {
                try {
                    //[Chinese text]area
                    val centerTempIndex: Int = 256 * (192 / 2) + 256 / 2
                    val maxTempIndex = ArrayUtils.getMaxIndex(temp, rotateType, selectIndex)
                    val minTempIndex = ArrayUtils.getMinIndex(temp, rotateType, selectIndex)
                    val rotateData = ArrayUtils.matrixRotate(srcData = temp, rotateType)
                    val bigDecimal = BigDecimal.valueOf(rotateData[centerTempIndex].toDouble())
                    val maxBigDecimal = BigDecimal.valueOf(rotateData[maxTempIndex].toDouble())
                    val minBigDecimal = BigDecimal.valueOf(rotateData[minTempIndex].toDouble())
                    bean.centerTemp = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toFloat()
                    bean.maxTemp = maxBigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toFloat()
                    bean.minTemp = minBigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toFloat()
                    bean.createTime = System.currentTimeMillis()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "[Chinese text]temperature[Chinese text]:${e.message}")
                }
            }

        })

        if (ret == 5) {
            Log.w("123", "[Chinese text]")
            recordThermal()//start[Chinese text]
        } else {
//            ToastUtils.showShort("[Chinese text]")
            Log.w("123", "[Chinese text]")
            mGuideInterface = null
            mIsIrVideoStart = false
        }
    }

    /**
     * stop[Chinese text]
     */
    private fun onIrVideoStop() {
        mIsIrVideoStart = if (!mIsIrVideoStart) {
            Log.w("123", "[Chinese text]stop")
            return
        } else {
            false
        }
        mGuideInterface!!.exit()
        mGuideInterface = null
        Log.w("123", "[Chinese text]stop[Chinese text]")
    }

    var isRecord = false
    var type = ""
    var timeMillis = 1000L //[Chinese text]1s
    var canUpdate = false
    var recordTask: Job? = null
    var thermalId = TimeTool.showDateSecond()
    var startTime = 0L

    /**
     * [Chinese text]listener-[Chinese text]
     */
    private fun recordThermal() {
        recordTask = lifecycleScope.launch(Dispatchers.IO) {
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
                    Log.w("123", "[Chinese text]")
                }
            }
            Log.w("123", "stop[Chinese text], [Chinese text]:$time")
        }
    }


    //MPChart
    private fun initChart() {
        chart.clear()
        chart.setOnChartValueSelectedListener(this)
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setDrawGridBackground(false)
        chart.description = null//[Chinese text]
        chart.setBackgroundResource(R.color.chart_bg)
        chart.setScaleEnabled(true)//[Chinese text]
        chart.setPinchZoom(false)//[Chinese text], [Chinese text]x[Chinese text]y[Chinese text]
        chart.isDoubleTapToZoomEnabled = false//[Chinese text]
        chart.isScaleYEnabled = false//[Chinese text]Y[Chinese text]
        chart.setExtraOffsets(
            0f,
            0f,
            SizeUtils.dp2px(8f).toFloat(),
            SizeUtils.dp2px(4f).toFloat()
        )//[Chinese text]area[Chinese text]
        chart.setNoDataText(getString(R.string.lms_http_code998))
        chart.setNoDataTextColor(textColor)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv//settingspoint[Chinese text]
        val data = LineData()
        data.setValueTextColor(textColor)
        chart.data = data
        val l = chart.legend
        l.form = Legend.LegendForm.CIRCLE
        l.textColor = textColor
        l.isEnabled = false//[Chinese text]line[Chinese text]
        val xAxis = chart.xAxis
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineColor = textColor
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true//[Chinese text]
        xAxis.textSize = 9f
//        xAxis.setLabelCount(6, true)//true[Chinese text]
        xAxis.setLabelCount(6, false)//true[Chinese text]
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
        canUpdate = true//[Chinese text]start[Chinese text]
    }


    /**
     * [Chinese text]
     */
    private fun updateChart() {
        ++pointIndex
        when (selectTimeType) {
            1 -> {
                //[Chinese text]
                addPointToChart(bean)
            }
            2 -> {
                //[Chinese text]
                val addTime = 2 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 2) + addTime) {
                    queryLog(3)
                }
            }
            3 -> {
                //[Chinese text]
                val addTime = 2 * 60 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 3) + addTime) {
                    queryLog(3)
                }
            }
            4 -> {
                //[Chinese text]([Chinese text], [Chinese text])
                val addTime = 2 * 24 * 60 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 4) + addTime) {
                    queryLog(3)
                }
            }
        }
    }

    /**
     * [Chinese text]
     */
    private fun addPointToChart(bean: ThermalBean) {
        synchronized(chart) {
            try {
                if (bean.createTime == 0L) {
                    Log.w("123", "createTime = 0L, bean:${bean}")
                    return
                }
                val data = ThermalEntity()
                data.thermalMax = bean.maxTemp
                data.thermalMin = bean.minTemp
                data.thermal = bean.centerTemp
                data.createTime = bean.createTime
                val lineData: LineData = chart.data
                var volDataSet = lineData.getDataSetByIndex(0) //[Chinese text]x[Chinese text]0[Chinese text]point
                if (volDataSet == null) {
                    startTime = data.createTime
                    Log.w("123", "settings[Chinese text]startTime:$startTime")
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
                        Log.w("123", "[Chinese text]:$entity")
                    }
                    "line" -> {
                        //[Chinese text]line
                        if (volDataSet == null) {
                            volDataSet = createSet("red")
                            lineData.addDataSet(volDataSet)
                            Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                        }
                        val entity = Entry(x, data.thermalMax)
                        entity.data = data
                        volDataSet.addEntry(entity)

                        //[Chinese text]line
                        var secondDataSet = lineData.getDataSetByIndex(1) //[Chinese text]x[Chinese text]0[Chinese text]point
                        if (secondDataSet == null) {
                            secondDataSet = createSet("blue")
                            lineData.addDataSet(secondDataSet)
                        }
                        val secondEntity = Entry(x, data.thermalMin)
                        secondEntity.data = data
                        secondDataSet.addEntry(secondEntity)
                    }
                    else -> {
                        //[Chinese text]line
                        if (volDataSet == null) {
                            volDataSet = createSet("red")
                            lineData.addDataSet(volDataSet)
                        }
                        val entity = Entry(x, data.thermalMax)
                        entity.data = data
                        volDataSet.addEntry(entity)

                        //[Chinese text]line
                        var secondDataSet = lineData.getDataSetByIndex(1) //[Chinese text]x[Chinese text]0[Chinese text]point
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
                chart.setVisibleXRangeMinimum(getMinimum())//settings[Chinese text]X[Chinese text]
                chart.setVisibleXRangeMaximum(getMaximum())//settings[Chinese text]X[Chinese text]
                chart.xAxis.setLabelCount(getLabCount(volDataSet.entryCount), false)//true[Chinese text]
                chart.moveViewToX(chart.xChartMax)//[Chinese text]
                if (volDataSet.entryCount == 20) {
                    chart.zoom(100f, 1f, chart.xChartMax, 0f)
                }
                return@synchronized
            } catch (e: Exception) {
                Log.e("123", "[Chinese text]:${e.message}")
                return@synchronized
            }
        }
    }

    private val fillColor by lazy { ContextCompat.getDrawable(this, R.drawable.bg_chart_fill2) }
    private val lineRed by lazy { ContextCompat.getColor(this, R.color.chart_line_max) }
    private val lineBlue by lazy { ContextCompat.getColor(this, R.color.chart_line_min) }
    private val lineGreen by lazy { ContextCompat.getColor(this, R.color.chart_line_center) }
    private val whiteColors by lazy { ContextCompat.getColor(this, R.color.circle_white) }
    private val textColor by lazy { ContextCompat.getColor(this, R.color.chart_text) }

    /**
     * [Chinese text]line[Chinese text]
     */
    private fun createSet(label: String): LineDataSet {
        val set = LineDataSet(null, label)
//        set.mode = LineDataSet.Mode.LINEAR
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawFilled(false)
//        set.fillDrawable = fillColor//settings[Chinese text]
        set.axisDependency = YAxis.AxisDependency.LEFT

        when (label) {
            "red" -> {
                set.color = lineRed//[Chinese text]line[Chinese text]
                set.circleHoleColor = lineRed//[Chinese text]
            }
            "blue" -> {
                set.color = lineBlue//[Chinese text]line[Chinese text]
                set.circleHoleColor = lineBlue//[Chinese text]
            }
            else -> {
                set.color = lineGreen//[Chinese text]line[Chinese text]
                set.circleHoleColor = lineGreen//[Chinese text]
            }
        }

        set.setCircleColor(whiteColors)//[Chinese text]
        set.circleHoleRadius = 4f//[Chinese text]point[Chinese text]
        set.circleRadius = 5f//[Chinese text]point[Chinese text]
        set.valueTextColor = Color.WHITE
        set.lineWidth = 2f
        set.fillAlpha = 200
        set.valueTextSize = 10f
        set.setDrawValues(false)//settings[Chinese text]
        set.isHighlightEnabled = true//[Chinese text]line
        set.setDrawHorizontalHighlightIndicator(false)//[Chinese text]line[Chinese text]
        set.enableDashedHighlightLine(8f, 8f, 0f)//[Chinese text]line
        return set
    }

    /**
     * [Chinese text]([Chinese text])
     * [Chinese text]: [Chinese text] => [Chinese text]startevent
     *
     * @param action
     * 0: [Chinese text]
     * 1: [Chinese text]
     * 2: switch[Chinese text]
     * 3: listener[Chinese text]
     * 4: [Chinese text]
     */
    private fun queryLog(action: Int) {
        startMonitor = false
        lifecycleScope.launch(Dispatchers.IO) {
//            dataList.clear()//[Chinese text]
//            dataList = arrayListOf()
            viewModel.queryLogThermals(selectTimeType = selectTimeType, action = action)
        }
    }

    private fun resultVol(bean: LogViewModel.ChartList) {
        dismissLoading()
        if (selectTimeType != 1 && bean.dataList.size > 0) {
            val logTime = TimeTool.showDateType(bean.dataList.last().createTime, selectTimeType)
            val nowTime = TimeTool.showDateType(System.currentTimeMillis(), selectTimeType)
            if (TextUtils.equals(logTime, nowTime)) {
                //[Chinese text],[Chinese text], [Chinese text]
                bean.dataList.removeLast()
            }
        }
//        dataList = bean.dataList
        if (latestTime == 0L) {
            //[Chinese text]
            addEntity(bean.dataList)
        } else if (bean.dataList.size > 0 && latestTime < bean.dataList.last().createTime) {
            //[Chinese text]
            addEntity(bean.dataList)
        }
    }

    //[Chinese text]
    private fun addEntity(data: ArrayList<ThermalEntity>) {
        clearEntity(data.size == 0)
        if (data.size == 0) {
            return
        }
        latestTime = data[data.size - 1].createTime
        startTime = data[0].createTime
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime)
        val lineData: LineData = chart.data
        var volDataSet = lineData.getDataSetByIndex(0) //[Chinese text]x[Chinese text]0[Chinese text]point
        if (volDataSet == null) {
            volDataSet = createSet("vol")
            lineData.addDataSet(volDataSet)
        }
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime, type = selectTimeType)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv//settingspoint[Chinese text]
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
                    Log.w("123", "[Chinese text]:$entity")
                }
                "line" -> {
                    //[Chinese text]line
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                        Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    //[Chinese text]line
                    var secondDataSet = lineData.getDataSetByIndex(1) //[Chinese text]x[Chinese text]0[Chinese text]point
                    if (secondDataSet == null) {
                        secondDataSet = createSet("blue")
                        lineData.addDataSet(secondDataSet)
                    }
                    val secondEntity = Entry(x, it.thermalMin)
                    secondEntity.data = it
                    secondDataSet.addEntry(secondEntity)
                }
                else -> {
                    //[Chinese text]line
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    //[Chinese text]line
                    var secondDataSet = lineData.getDataSetByIndex(1) //[Chinese text]x[Chinese text]0[Chinese text]point
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
        Log.w("123", "[Chinese text]line[Chinese text]:${volDataSet.entryCount}[Chinese text]")
        lineData.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMinimum(getMinimum())//settings[Chinese text]X[Chinese text]
        chart.setVisibleXRangeMaximum(getMaximum())//settings[Chinese text]X[Chinese text]
        Log.i(
            "123",
            "list moveViewToX:${chart.xChartMax}, chart.highestVisibleX:${chart.highestVisibleX}"
        )
        chart.moveViewToX(chart.xChartMax)//[Chinese text]
        chart.xAxis.setLabelCount(getLabCount(volDataSet.entryCount), false)//true[Chinese text]
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

    override fun onValueSelected(e: Entry?, h: Highlight?) {

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

    //[Chinese text]
    private fun getMinimum(): Float {
        val min = when (selectTimeType) {
            1 -> 1 * 10 * 1000f //10s
            2 -> 10 * 60 * 1000f //10min
            3 -> 10 * 60 * 60 * 1000f //10hour
            4 -> 10 * 24 * 60 * 60 * 1000f //10day
            else -> 1 * 10 * 1000f //10s
        }
        return min
    }

    //[Chinese text], [Chinese text]50[Chinese text]
    private fun getMaximum(): Float {
        return getMinimum() * 50f
    }
}