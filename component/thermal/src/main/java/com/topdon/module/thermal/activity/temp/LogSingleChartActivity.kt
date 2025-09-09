// package com.topdon.module.thermal.activity.temp
//
// import android.util.Log
// import androidx.lifecycle.lifecycleScope
// import androidx.recyclerview.widget.GridLayoutManager
// import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
// import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
// import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
// import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
// import com.github.aachartmodel.aainfographics.aaoptionsmodel.*
// import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
// import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
// import com.topdon.lib.core.common.SharedManager
// import com.topdon.lib.core.config.RouterConfig
// import com.topdon.lib.core.db.AppDatabase
// import com.topdon.lib.core.db.entity.ThermalEntity
// import com.topdon.lib.core.ktbase.BaseActivity
// import com.topdon.module.thermal.R
// import com.topdon.module.thermal.adapter.SettingTimeAdapter
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.delay
// import kotlinx.coroutines.launch
//
// /**
// * [CN_TEXT]thermalId[CN_TEXT]([CN_TEXT])
// */
// @Route(path = RouterConfig.THERMAL_LOG_SINGLE_CHART)
// class LogSingleChartActivity : BaseActivity() {
//
//    val adapter: SettingTimeAdapter by lazy { SettingTimeAdapter(this) }
//
//    companion object {
//        const val KEY_THERMAL_ID = "thermalId"
//    }
//
//    private var thermalId = ""
//    private var dataList: ArrayList<ThermalEntity> = arrayListOf()
//
//    override fun initContentView() = R.layout.activity_log_chart
//
//    override fun initView() {
//        setTitleText("[CN_TEXT]")
//        thermalId = intent.getStringExtra(KEY_THERMAL_ID)!!
//
//        log_chart_time_recycler.layoutManager = GridLayoutManager(this, 4)
//        log_chart_time_recycler.adapter = adapter
//        adapter.listener = object : SettingTimeAdapter.OnItemClickListener {
//            override fun onClick(index: Int, time: Int) {
//                adapter.setCheck(index)
//            }
//
//        }
//
//    }
//
//    override fun initData() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            dataList = AppDatabase.getInstance().thermalDao()
//                .getByThermalId(SharedManager.getUserId(), thermalId) as ArrayList<ThermalEntity>
//            delay(300)
//            launch(Dispatchers.Main) {
//                initChart()
//            }
//        }
//    }
//
//    private var dataSeries = arrayOfNulls<Float>(0)
//
//    private fun getSeriesModel(): Array<AASeriesElement> {
//        dataList.forEach {
//            dataSeries = dataSeries.plus(it.thermal)
//        }
//        Log.w("123", "ahh:${dataSeries.joinToString()}")
//        return arrayOf(
//            AASeriesElement()
//                .name("Tokyo")
//                .data(dataSeries as Array<Any>)
//        )
//    }
//
//    private fun configureSpecialStyleMarkerOfSingleDataElementChart(): AAChartModel {
//        return AAChartModel()
//            .chartType(AAChartType.Spline)
//            .titleStyle(AAStyle.Companion.style("#FFFFFF"))
//            .subtitleStyle(AAStyle.Companion.style(color = "#FFFFFF", fontSize = 12f))
//            .backgroundColor("#3598E8")
//            .yAxisTitle("")
//            .axesTextColor("#FFFFFF")
//            .dataLabelsEnabled(false)//[CN_TEXT]
//            .tooltipEnabled(true)
//            .markerRadius(0f)
// //            .gradientColorEnable(true)
// //            .colorsTheme(arrayOf("#FFFFFF", "#000000"))
// //            .scrollablePlotArea(AAScrollablePlotArea().minWidth(10).minHeight(10))
//            .xAxisVisible(true)
//            .yAxisVisible(true)
//            .series(
//                arrayOf(
//                    AASeriesElement()
//                        .name("vol")
//                        .lineWidth(2f)
//                        .data(arrayOf(0))
//                        .color("#FFFFFF")//[CN_TEXT]
//                )
//            )
//    }
//
//    val defaultCount = 20//[CN_TEXT]10[CN_TEXT]
//    val startIndex = 0f
//    var pointIndex = startIndex - defaultCount
//
//    private fun initChart() {
//        initOption()
//    }
//
//    private fun initOption() {
//        aa_chart_view.clearCache(true)
//        val options = configureSpecialStyleMarkerOfSingleDataElementChart().aa_toAAOptions()
//        val series = initSeries()
//        val chart = AAChart()
//            .scrollablePlotArea(AAScrollablePlotArea().minWidth(20)).backgroundColor("#383d45")
//            .type(AAChartType.Area)//[CN_TEXT]Type
//
//        val xAxis = AAXAxis()
//            .lineWidth(1f)
//            .gridLineWidth(0f)
//            .gridLineColor("#717a8f")
//            .lineColor("#717a8f")
//            .tickColor("#717a8f")//[CN_TEXT]
//            .minRange(9)//Settings[CN_TEXT]，[CN_TEXT]
//            .minorTickColor("#000000")
//            .labels(AALabels().style(AAStyle.style("#717a8f")))//[CN_TEXT]
//
//        val yAxis = AAYAxis()
//            .lineWidth(1f)
//            .gridLineWidth(1f)
//            .gridLineColor("#454b56")
//            .lineColor("#383d45")
// //            .max(100f)//Settings[CN_TEXT]y[CN_TEXT]
//            .min(0f)//Settings[CN_TEXT]y[CN_TEXT]
//            .labels(AALabels().style(AAStyle.style("#717a8f")))
//            .title(AATitle().text("").style(AAStyle().color("#FFFFFF")))//[CN_TEXT]
//
//        //[CN_TEXT]
//        options.series(series).chart(chart).xAxis(xAxis).yAxis(yAxis)
//        //[CN_TEXT]
//        aa_chart_view.aa_drawChartWithChartOptions(options)
//    }
//
//    /**
//     * [CN_TEXT]Type[CN_TEXT]
//     */
//    private fun initSeries(): Array<AASeriesElement> {
//        val maxTempListData = Array<Any>(dataList.size) { dataList[it].thermalMax }
//        val minTempListData = Array<Any>(dataList.size) { dataList[it].thermalMin }
//        val centerTempListData = Array<Any>(dataList.size) { dataList[it].thermal }
//
//        val firstColor = "#3d6eb6"
//        val secondColor = "#ff6e73"
//        val thirdColor = "#2bdb1f"
//        val gradientColorDic: Map<*, *> = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            "#3f7ad1AA",  //DodgerBlue, alpha [CN_TEXT] 1
//            "#3f7ad100" //DodgerBlue, alpha [CN_TEXT] 0.1 ([CN_TEXT]android[CN_TEXT])
//        )
//        val gradientColorDicSecond: Map<*, *> = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            "#ff6e73AA",
//            "#ff6e7300"
//        )
//        val gradientColorDicThird: Map<*, *> = AAGradientColor.linearGradient(
//            AALinearGradientDirection.ToBottom,
//            "#2bdb1fAA",
//            "#2bdb1f00"
//        )
//        when (dataList[0].type) {
//            "point" -> {
//                return arrayOf(
//                    AASeriesElement()
//                        .color(firstColor)
//                        .fillColor(gradientColorDic)
//                        .name("temp")
//                        .data(centerTempListData),
//                )
//            }
//            "line" -> {
//                return arrayOf(
//                    AASeriesElement()
//                        .color(firstColor)
//                        .fillColor(gradientColorDic)
//                        .name("maxTemp")
//                        .data(maxTempListData),
//                    AASeriesElement()
//                        .color(secondColor)
//                        .fillColor(gradientColorDicSecond)
//                        .name("minTemp")
//                        .data(minTempListData)
//                )
//            }
//            else -> {
//                return arrayOf(
//                    AASeriesElement()
//                        .color(firstColor)
//                        .fillColor(gradientColorDic)
//                        .name("maxTemp")
//                        .data(maxTempListData),
//                    AASeriesElement()
//                        .color(secondColor)
//                        .fillColor(gradientColorDicSecond)
//                        .name("temp")
//                        .data(centerTempListData),
//                    AASeriesElement()
//                        .color(thirdColor)
//                        .fillColor(gradientColorDicThird)
//                        .name("minTemp")
//                        .data(minTempListData)
//                )
//            }
//        }
//
//    }
//
// }
