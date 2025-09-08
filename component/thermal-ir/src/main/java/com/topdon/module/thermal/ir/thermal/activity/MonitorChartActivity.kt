package com.topdon.module.thermal.ir.thermal.activity

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
import com.topdon.lib.core.tools.NumberTools
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import com.topdon.lib.core.tools.TimeTool
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.thermal.adapter.SettingCheckAdapter
import com.topdon.module.thermal.ir.thermal.adapter.SettingTimeAdapter
import com.topdon.module.thermal.ir.thermal.chart.MyValueFormatter
import com.topdon.module.thermal.ir.thermal.utils.ArrayUtils
import com.topdon.module.thermal.ir.thermal.view.MyMarkerView
import com.topdon.module.thermal.ir.thermal.viewmodel.LogViewModel
import com.topdon.module.thermal.ir.databinding.ActivityMonitorChartBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Route(path = RouterConfig.IR_MONITOR_CHART)
class MonitorChartActivity : BaseIRActivity(), View.OnClickListener, OnChartValueSelectedListener {

    private lateinit var binding: ActivityMonitorChartBinding

    private val viewModel: LogViewModel by viewModels()

    private val timeAdapter: SettingTimeAdapter by lazy { SettingTimeAdapter(this) }//
    private val adapter: SettingCheckAdapter by lazy { SettingCheckAdapter(this) }//

    //    var MONITOR_ACTION = STATS_START
    private var selectDuration = 1
    private var selectType = 1// 1: 2: 3:
    private var selectIndex: ArrayList<Int> = arrayListOf()//
    private val bean = ThermalBean()
    private var selectTimeType = 1
    private var latestTime = 0L//,(, , )
    private var startMonitor = false

    private lateinit var chart: LineChart

    override fun initContentView() = R.layout.activity_monitor_chart

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        binding = ActivityMonitorChartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        // setTitleText(LibAppR.string.main_thermal_motion) // Commented out - method not available in BaseIRActivity
        selectType = intent.getIntExtra("type", 3)
        selectIndex = intent.getIntegerArrayListExtra("select")!!
        Log.w("123", "selectType:$selectType")
        Log.w("123", "selectIndex:${selectIndex.joinToString()}")
        SharedManager.selectFenceType = selectType
        type = when (selectType) {
            1 -> "point"
            2 -> "line"
            else -> "fence"
        }
        chart = binding.mpChartView
        initChart()
        initRecycler()
        viewModel.resultLiveData.observe(this)
        {
            Log.w("123", ":${it.dataList.size}")
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
        binding.monitorChartTimeRecycler.layoutManager = GridLayoutManager(this, 4)
        binding.monitorChartTimeRecycler.adapter = timeAdapter
        binding.monitorChartSettingRecycler.layoutManager = GridLayoutManager(this, 3)
        binding.monitorChartSettingRecycler.adapter = adapter
        //(   )
        timeAdapter.listener = object : SettingTimeAdapter.OnItemClickListener {
            override fun onClick(index: Int, timeType: Int) {
                selectTimeType = timeType
                chart.highlightValue(null) //Marker
                latestTime = 0L
                showLoadingDialog()
                lifecycleScope.launch {
                    delay(500)
                    queryLog(2)
                }
            }
        }
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
                recordThermal()//
            }
        }
    }

    /** defaultCount property */
    val defaultCount = 20//10
    /** startIndex property */
    val startIndex = 0f
    /** pointIndex property */
    var pointIndex = startIndex - defaultCount

    /** mIsIrVideoStart property */
    var mIsIrVideoStart = false

    private var mGuideInterface: GuideInterface? = null
    /** rotateType property */
    var rotateType = 3

    private fun onIrVideoStart() {
        mIsIrVideoStart = if (mIsIrVideoStart) {
            ToastUtils.showShort("")
            return
        } else {
            true
        }
        mGuideInterface = GuideInterface()
        val ret = mGuideInterface!!.init(this, object : GuideInterface.IrDataCallback {
            override fun processIrData(yuv: ByteArray, temp: FloatArray) {
                try {
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
                    Log.e(TAG, ":${e.message}")
                }
            }

        })

        if (ret == 5) {
            Log.w("123", "")
            recordThermal()//
        } else {
//            ToastUtils.showShort()
            Log.w("123", "")
            mGuideInterface = null
            mIsIrVideoStart = false
        }
    }

    private fun onIrVideoStop() {
        mIsIrVideoStart = if (!mIsIrVideoStart) {
            Log.w("123", "")
            return
        } else {
            false
        }
        mGuideInterface!!.exit()
        mGuideInterface = null
        Log.w("123", "")
    }

    /** isRecord property */
    var isRecord = false
    /** type property */
    var type = ""
    /** timeMillis property */
    var timeMillis = 1000L //1s
    /** canUpdate property */
    var canUpdate = false
    /** recordTask property */
    var recordTask: Job? = null
    /** thermalId property */
    var thermalId = TimeTool.showDateSecond()
    /** startTime property */
    var startTime = 0L

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
                    Log.w("123", "")
                }
            }
            Log.w("123", ", :$time")
        }
    }


    //MPChart
    private fun initChart() {
        chart.clear()
        chart.setOnChartValueSelectedListener(this)
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setDrawGridBackground(false)
        chart.description = null//
        chart.setBackgroundResource(LibAppR.color.chart_bg)
        chart.setScaleEnabled(true)//
        chart.setPinchZoom(false)//，xy
        chart.isDoubleTapToZoomEnabled = false//
        chart.isScaleYEnabled = false//Y
        chart.setExtraOffsets(
            0f,
            0f,
            SizeUtils.dp2px(8f).toFloat(),
            SizeUtils.dp2px(4f).toFloat()
        )//
        chart.setNoDataText(getString(R.string.lms_http_code998))
        chart.setNoDataTextColor(textColor)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv//
        val data = LineData()
        data.setValueTextColor(textColor)
        chart.data = data
        val l = chart.legend
        l.form = Legend.LegendForm.CIRCLE
        l.textColor = textColor
        l.isEnabled = false//
        val xAxis = chart.xAxis
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineColor = textColor
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true//
        xAxis.textSize = 9f
//        xAxis.setLabelCount(6, true)//true
        xAxis.setLabelCount(6, false)//true
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
        canUpdate = true//
    }


    private fun updateChart() {
        ++pointIndex
        when (selectTimeType) {
            1 -> {
                addPointToChart(bean)
            }
            2 -> {
                val addTime = 2 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 2) + addTime) {
                    queryLog(3)
                }
            }
            3 -> {
                val addTime = 2 * 60 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 3) + addTime) {
                    queryLog(3)
                }
            }
            4 -> {
                //()
                val addTime = 2 * 24 * 60 * 60 * 1000L
                if (bean.createTime > TimeTool.timeToMinute(latestTime, 4) + addTime) {
                    queryLog(3)
                }
            }
        }
    }

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
                var volDataSet = lineData.getDataSetByIndex(0) //x0
                if (volDataSet == null) {
                    startTime = data.createTime
                    Log.w("123", "startTime:$startTime")
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
                        Log.w("123", ":$entity")
                    }
                    "line" -> {
                        if (volDataSet == null) {
                            volDataSet = createSet("red")
                            lineData.addDataSet(volDataSet)
                            Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                        }
                        val entity = Entry(x, data.thermalMax)
                        entity.data = data
                        volDataSet.addEntry(entity)

                        var secondDataSet = lineData.getDataSetByIndex(1) //x0
                        if (secondDataSet == null) {
                            secondDataSet = createSet("blue")
                            lineData.addDataSet(secondDataSet)
                        }
                        val secondEntity = Entry(x, data.thermalMin)
                        secondEntity.data = data
                        secondDataSet.addEntry(secondEntity)
                    }
                    else -> {
                        if (volDataSet == null) {
                            volDataSet = createSet("red")
                            lineData.addDataSet(volDataSet)
                        }
                        val entity = Entry(x, data.thermalMax)
                        entity.data = data
                        volDataSet.addEntry(entity)

                        var secondDataSet = lineData.getDataSetByIndex(1) //x0
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
                chart.setVisibleXRangeMinimum(getMinimum())//X
                chart.setVisibleXRangeMaximum(getMaximum())//X
                chart.xAxis.setLabelCount(getLabCount(volDataSet.entryCount), false)//true
                chart.moveViewToX(chart.xChartMax)//
                if (volDataSet.entryCount == 20) {
                    chart.zoom(100f, 1f, chart.xChartMax, 0f)
                }
                return@synchronized
            } catch (e: Exception) {
                Log.e("123", ":${e.message}")
                return@synchronized
            }
        }
    }

    private val fillColor by lazy { ContextCompat.getDrawable(this, R.drawable.bg_chart_fill2) }
    private val lineRed by lazy { ContextCompat.getColor(this, LibAppR.color.chart_line_max) }
    private val lineBlue by lazy { ContextCompat.getColor(this, LibAppR.color.chart_line_min) }
    private val lineGreen by lazy { ContextCompat.getColor(this, LibAppR.color.chart_line_center) }
    private val whiteColors by lazy { ContextCompat.getColor(this, LibAppR.color.circle_white) }
    private val textColor by lazy { ContextCompat.getColor(this, LibAppR.color.chart_text) }

    private fun createSet(label: String): LineDataSet {
        val set = LineDataSet(null, label)
//        set.mode = LineDataSet.Mode.LINEAR
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawFilled(false)
//        set.fillDrawable = fillColor//
        set.axisDependency = YAxis.AxisDependency.LEFT

        when (label) {
            "red" -> {
                set.color = lineRed//
                set.circleHoleColor = lineRed//
            }
            "blue" -> {
                set.color = lineBlue//
                set.circleHoleColor = lineBlue//
            }
            else -> {
                set.color = lineGreen//
                set.circleHoleColor = lineGreen//
            }
        }

        set.setCircleColor(whiteColors)//
        set.circleHoleRadius = 4f//
        set.circleRadius = 5f//
        set.valueTextColor = Color.WHITE
        set.lineWidth = 2f
        set.fillAlpha = 200
        set.valueTextSize = 10f
        set.setDrawValues(false)//
        set.isHighlightEnabled = true//
        set.setDrawHorizontalHighlightIndicator(false)//
        set.enableDashedHighlightLine(8f, 8f, 0f)//
        return set
    }

     * ()
     * :  =>
     * @param action
     * 0:
     * 1:
     * 2:
     * 3:
     * 4:
    private fun queryLog(action: Int) {
        startMonitor = false
        lifecycleScope.launch(Dispatchers.IO) {
//            dataList.clear()//
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
                //,
                bean.dataList.removeLast()
            }
        }
//        dataList = bean.dataList
        if (latestTime == 0L) {
            addEntity(bean.dataList)
        } else if (bean.dataList.size > 0 && latestTime < bean.dataList.last().createTime) {
            addEntity(bean.dataList)
        }
    }

    private fun addEntity(data: ArrayList<ThermalEntity>) {
        clearEntity(data.size == 0)
        if (data.size == 0) {
            return
        }
        latestTime = data[data.size - 1].createTime
        startTime = data[0].createTime
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime)
        val lineData: LineData = chart.data
        var volDataSet = lineData.getDataSetByIndex(0) //x0
        if (volDataSet == null) {
            volDataSet = createSet("vol")
            lineData.addDataSet(volDataSet)
        }
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime, type = selectTimeType)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv//
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
                    Log.w("123", ":$entity")
                }
                "line" -> {
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                        Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    var secondDataSet = lineData.getDataSetByIndex(1) //x0
                    if (secondDataSet == null) {
                        secondDataSet = createSet("blue")
                        lineData.addDataSet(secondDataSet)
                    }
                    val secondEntity = Entry(x, it.thermalMin)
                    secondEntity.data = it
                    secondDataSet.addEntry(secondEntity)
                }
                else -> {
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    var secondDataSet = lineData.getDataSetByIndex(1) //x0
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
        Log.w("123", ":${volDataSet.entryCount}")
        lineData.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMinimum(getMinimum())//X
        chart.setVisibleXRangeMaximum(getMaximum())//X
        Log.i(
            "123",
            "list moveViewToX:${chart.xChartMax}, chart.highestVisibleX:${chart.highestVisibleX}"
        )
        chart.moveViewToX(chart.xChartMax)//
        chart.xAxis.setLabelCount(getLabCount(volDataSet.entryCount), false)//true
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

     * x
    private fun getLabCount(count: Int): Int {
        return when (count) {
            in 0..2 -> 1
            in 3..4 -> 2
            in 5..6 -> 3
            in 7..9 -> 4
            else -> 5
        }
    }

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

    //50
    private fun getMaximum(): Float {
        return getMinimum() * 50f
    }
}