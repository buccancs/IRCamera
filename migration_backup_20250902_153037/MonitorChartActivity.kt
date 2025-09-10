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

    private fun queryLog(action: Int) {
        startMonitor = false
        lifecycleScope.launch(Dispatchers.IO) {
//            dataList.clear()//清空数据
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
                // 分时天,当前时间段没结束，应当删除最新当前时间段数据
                bean.dataList.removeLast()
            }
        }
//        dataList = bean.dataList
        if (latestTime == 0L) {
            // 图表无数据需要更新
            addEntity(bean.dataList)
        } else if (bean.dataList.size > 0 && latestTime < bean.dataList.last().createTime) {
            // 有新数据再更新
            addEntity(bean.dataList)
        }
    }

    // 整体刷新
    private fun addEntity(data: ArrayList<ThermalEntity>) {
        clearEntity(data.size == 0)
        if (data.size == 0) {
            return
        }
        latestTime = data[data.size - 1].createTime
        startTime = data[0].createTime
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime)
        val lineData: LineData = chart.data
        var volDataSet = lineData.getDataSetByIndex(0) // 读取x为0的坐标点
        if (volDataSet == null) {
            volDataSet = createSet("vol")
            lineData.addDataSet(volDataSet)
        }
        chart.xAxis.valueFormatter = MyValueFormatter(startTime = startTime, type = selectTimeType)
        val mv = MyMarkerView(this, R.layout.marker_lay)
        mv.chartView = chart
        chart.marker = mv // 设置点击坐标显示提示框
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
                    Log.w("123", "添加一个数据:$entity")
                }
                "line" -> {
                    // 第一条线
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                        Log.w("123", "volDataSet.entryCount:${volDataSet.entryCount}")
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    // 第二条线
                    var secondDataSet = lineData.getDataSetByIndex(1) // 读取x为0的坐标点
                    if (secondDataSet == null) {
                        secondDataSet = createSet("blue")
                        lineData.addDataSet(secondDataSet)
                    }
                    val secondEntity = Entry(x, it.thermalMin)
                    secondEntity.data = it
                    secondDataSet.addEntry(secondEntity)
                }
                else -> {
                    // 第一条线
                    if (volDataSet == null) {
                        volDataSet = createSet("red")
                        lineData.addDataSet(volDataSet)
                    }
                    val entity = Entry(x, it.thermalMax)
                    entity.data = it
                    volDataSet.addEntry(entity)

                    // 第二条线
                    var secondDataSet = lineData.getDataSetByIndex(1) // 读取x为0的坐标点
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
        Log.w("123", "曲线数据:${volDataSet.entryCount}个")
        lineData.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMinimum(getMinimum()) // 设置显示X轴区间大小
        chart.setVisibleXRangeMaximum(getMaximum()) // 设置显示X轴区间大小
        Log.i(
            "123",
            "list moveViewToX:${chart.xChartMax}, chart.highestVisibleX:${chart.highestVisibleX}",
        )
        chart.moveViewToX(chart.xChartMax) // 移动到最右端
        chart.xAxis.setLabelCount(getLabCount(volDataSet.entryCount), false) // true保证有刻度数量不变
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
     * x轴显示多少个刻度
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

    // 获取显示最小区间
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

    // 获取显示最大区间，以最小区间的50倍
    private fun getMaximum(): Float {
        return getMinimum() * 50f
    }
}
