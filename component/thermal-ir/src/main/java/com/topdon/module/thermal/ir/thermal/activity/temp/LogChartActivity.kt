//package com.topdon.module.thermal.ir.thermal.activity
//import android.util.Log
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.GridLayoutManager
//import com.alibaba.android.arouter.facade.annotation.Route
//import com.github.aachartmodel.aainfographics.aachartcreator.*
//import com.github.aachartmodel.aainfographics.aaoptionsmodel.*
//import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
//import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
//import com.topdon.lib.core.common.SharedManager
//import com.topdon.lib.core.config.RouterConfig
//import com.topdon.lib.core.db.AppDatabase
//import com.topdon.lib.core.db.entity.ThermalEntity
//import com.topdon.lib.core.ktbase.BaseActivity
//import com.topdon.lib.core.tools.TimeTool
//import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
//import com.topdon.module.thermal.ir.thermal.adapter.SettingTimeAdapter

//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//@Route(path = RouterConfig.THERMAL_LOG_CHART)
//class LogChartActivity : BaseActivity() {
//    val adapter: SettingTimeAdapter by lazy { SettingTimeAdapter(this) }
//    private var dataList: ArrayList<ThermalEntity> = arrayListOf()
//    override fun initContentView() = R.layout.activity_log_chart
//    override fun initView() {
//        setTitleText()
//        log_chart_time_recycler.layoutManager = GridLayoutManager(this, 4)
//        log_chart_time_recycler.adapter = adapter
//        adapter.listener = object : SettingTimeAdapter.OnItemClickListener {
//            override fun onClick(index: Int, time: Int) {
//                adapter.setCheck(index)
//            }
//        }
//    }
//    override fun initData() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            dataList = AppDatabase.getInstance(baseContext).thermalDao()
//                .getAllThermalByDate(SharedManager.getUserId()) as ArrayList<ThermalEntity>
//            delay(300)
//            launch(Dispatchers.Main) {
//                initChart()
//            }
//        }
//    }
//    private fun configureSpecialStyleMarkerOfSingleDataElementChart(): AAChartModel {
//        return AAChartModel()
//            .chartType(AAChartType.Spline)
//            .titleStyle(AAStyle.Companion.style(#FFFFFF))
//            .subtitleStyle(AAStyle.Companion.style(color = #FFFFFF, fontSize = 12f))
//            .backgroundColor(#3598E8)
//            .yAxisTitle()
//            .axesTextColor(#FFFFFF)
//            .dataLabelsEnabled(false)//
//            .tooltipEnabled(true)
//            .markerRadius(0f)
//            .xAxisVisible(true)
//            .yAxisVisible(true)
//            .zoomType(AAChartZoomType.X)
//            .animationType(AAChartAnimationType.SwingFromTo)
//    }
//    val defaultCount = 20//10
//    val startIndex = 0f
//    var pointIndex = startIndex - defaultCount
//    private fun initChart() {
//        initOption()
//    }
//    private fun initOption() {
//        aa_chart_view.clearCache(true)
//        val options = configureSpecialStyleMarkerOfSingleDataElementChart().aa_toAAOptions()
//        val series = initSeries()
//        val count = dataList.size
//        val chart = AAChart()
//            .scrollablePlotArea(AAScrollablePlotArea().minWidth(count * 20).scrollPositionX(1f))
//            .backgroundColor(#383d45)
//            .type(AAChartType.Area)//
//        val timeList = Array(dataList.size) {
//            TimeTool.showTimeSecond(dataList[it].createTime)
//        }
//        val xAxis = AAXAxis()
//            .lineWidth(1f)
//            .gridLineWidth(0f)
//            .gridLineColor(#717a8f)
//            .lineColor(#717a8f)
//            .tickColor(#717a8f)//
////            .minRange(20)//
//            .minorTickColor(#FF0000)
//            .labels(AALabels().style(AAStyle.style(#717a8f)))//
////            .categories(timeList)
//        val yAxis = AAYAxis()
//            .lineWidth(1f)
//            .gridLineWidth(1f)
//            .gridLineColor(#454b56)
//            .lineColor(#383d45)
////            .max(100f)//y
//            .min(0f)//y
//            .labels(AALabels().style(AAStyle.style(#717a8f)))
//            .title(AATitle().text().style(AAStyle().color(#FFFFFF)))//
//        options.series(series).chart(chart).xAxis(xAxis).yAxis(yAxis)
//        aa_chart_view.aa_drawChartWithChartOptions(options)
//    }
//    private fun initSeries(): Array<AASeriesElement> {
//        Log.w(123, dataList size: ${dataList.size})
//        val maxTempListData = Array<Any>(dataList.size) {
//            Log.w(123,  ${TimeTool.showTimeSecond(dataList[it].createTime)})
//            arrayOf(TimeTool.showTimeSecond(dataList[it].createTime), dataList[it].thermalMax)
//        }
//        val minTempListData = Array<Any>(dataList.size) {
//            arrayOf(TimeTool.showTimeSecond(dataList[it].createTime), dataList[it].thermalMin)
//        }
//        val centerTempListData = Array<Any>(dataList.size) {
//            arrayOf(TimeTool.showTimeSecond(dataList[it].createTime), dataList[it].thermal)
//        }
//        val firstColor = #3d6eb6
//        val secondColor = #ff6e73
//        val thirdColor = #2bdb1f
//        val gradientColorDic: Map<*, *> = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            #3f7ad1AA,  //DodgerBlue, alpha  1
//            #3f7ad100 //DodgerBlue, alpha  0.1 (android)
//        )
//        val gradientColorDicSecond: Map<*, *> = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            #ff6e73AA,
//            #ff6e7300
//        )
//        val gradientColorDicThird: Map<*, *> = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            #2bdb1fAA,
//            #2bdb1f00
//        )
//        if (dataList.size == 0) {
//            return arrayOf(
//                AASeriesElement()
//                    .color(firstColor)
//                    .fillColor(gradientColorDic)
//                    .name(temp)
//                    .data(centerTempListData),
//            )
//        }
//        when (dataList[0].type) {
//            point -> {
//                return arrayOf(
//                    AASeriesElement()
//                        .color(firstColor)
//                        .fillColor(gradientColorDic)
//                        .name(temp)
//                )
//            }
//            line -> {
//                return arrayOf(
//                    AASeriesElement()
//                        .color(firstColor)
//                        .fillColor(gradientColorDic)
//                        .name(maxTemp)
//                        .data(maxTempListData),
//                    AASeriesElement()
//                        .color(secondColor)
//                        .fillColor(gradientColorDicSecond)
//                        .name(minTemp)
//                        .data(minTempListData)
//                )
//            }
//            else -> {
//                return arrayOf(
//                    AASeriesElement()
//                        .color(firstColor)
//                        .fillColor(gradientColorDic)
//                        .name(maxTemp)
//                        .data(maxTempListData),
//                    AASeriesElement()
//                        .color(secondColor)
//                        .fillColor(gradientColorDicSecond)
//                        .name(temp)
//                        .data(centerTempListData),
//                    AASeriesElement()
//                        .color(thirdColor)
//                        .fillColor(gradientColorDicThird)
//                        .name(minTemp)
//                        .data(minTempListData)
//                )
//            }
//        }
//    }
//}