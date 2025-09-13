package com.topdon.module.thermal.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.ThermalDayEntity
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.db.entity.ThermalHourEntity
import com.topdon.lib.core.db.entity.ThermalMinuteEntity
import com.topdon.lib.core.ktbase.BaseViewModel
import com.topdon.lib.core.tools.TimeTool
import kotlinx.coroutines.*
import java.util.*

/**
 * Custom Log view model view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class LogViewModel : BaseViewModel() {
    val resultLiveData = MutableLiveData<ChartList>()

    private var queryJob: Job? = null

    fun queryLogByType(selectType: Int) {
        if (queryJob != null && queryJob!!.isActive) {
            queryJob!!.cancel()
            queryJob = null
        }
        queryJob =
            viewModelScope.launch(Dispatchers.IO) {
                var dataList: ArrayList<ThermalEntity>? = arrayListOf()
                var startTime = 0L
                var endTime = 0L
                when (selectType) {
                    1 -> {
                        Log.w("123", "查询秒")
\1秒
                        endTime = Date().time
                        startTime = endTime - 7200 * 1000L // 2小时
                        Log.w("123", "query startTime:$startTime, endTime:$endTime")
                        dataList =
                            AppDatabase.getInstance().thermalDao()
                                .getThermalByDate(
                                    SharedManager.getUserId(),
                                    startTime,
                                    endTime,
                                ) as ArrayList<ThermalEntity>
                        Log.w("123", "data size: ${dataList.size}")
                    }
                    2 -> {
\1分
                        endTime = Date().time
                        startTime = endTime - 7200 * 60 * 1000L
                        dataList =
                            AppDatabase.getInstance().thermalDao()
                                .getThermalByDate(
                                    SharedManager.getUserId(),
                                    startTime,
                                    endTime,
                                ) as ArrayList<ThermalEntity>
                    }
                    3 -> {
\1时
                        endTime = Date().time
                        startTime = endTime - 7200 * 60 * 60 * 1000L
                        dataList =
                            AppDatabase.getInstance().thermalDao()
                                .getThermalByDate(
                                    SharedManager.getUserId(),
                                    startTime,
                                    endTime,
                                ) as ArrayList<ThermalEntity>
                    }
                    else -> {
\1天
                        dataList =
                            AppDatabase.getInstance().thermalDao()
                                .getAllThermalByDate(SharedManager.getUserId()) as ArrayList<ThermalEntity>
                    }
                }
                delay(500)
                if (dataList == null) {
                    dataList = arrayListOf()
                } else {
                    Log.w("123", "dataList size:${dataList.size}")
                }
                resultLiveData.postValue(ChartList(dataList = dataList))
            }
    }

    /**
\1第一项实时图表的历史记录查询
\1查询历史电压data(等待蓝牙传输历史记录结束后触发)
\1时间区间: 现在时间 => 倒退到开始事件
     */
    suspend fun queryLogThermals(
        selectTimeType: Int,
        endLogTime: Long = System.currentTimeMillis(),
        action: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = SharedManager.getUserId()
            val bean = ChartList()
\1查询之前先同步data
            val job = async { syncVol(selectTimeType) }
            job.await()
            syncRun = false // 同步结束
            val startLogTime =
                when (selectTimeType) {
                    /**
\17200data
\1秒:2小时
\1分:5天
\1时:300天
\1天:20年
                     */
                    1 -> endLogTime - 7200 * 1000L // 秒(2小时)
                    2 -> endLogTime - 7200 * 60 * 1000L // 分(5天)
                    3 -> endLogTime - 7200 * 60 * 60 * 1000L // 时(300天)
                    4 -> endLogTime - 1 * 365 * 24 * 60 * 60 * 1000L // 天(1年)
                    else -> endLogTime - 7200 * 1000L
                }
            when (selectTimeType) {
                1 -> {
                    bean.dataList =
                        AppDatabase.getInstance().thermalDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            ) as ArrayList<ThermalEntity>
                    bean.maxVol =
                        AppDatabase.getInstance().thermalDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    bean.minVol =
                        AppDatabase.getInstance().thermalDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    Log.w("chart", "电压数据:${bean.dataList.size}")
                    Log.w("chart", "电压数据max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
                2 -> {
                    val resultList =
                        AppDatabase.getInstance().thermalMinDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            ) as ArrayList<ThermalMinuteEntity>
                    resultList.forEach {
                        val entity = ThermalEntity()
                        entity.userId = it.userId
                        entity.sn = it.sn
                        entity.thermal = it.thermal
                        entity.thermalMax = it.thermalMax
                        entity.thermalMin = it.thermalMin
                        entity.info = it.info
                        entity.type = it.type
                        entity.createTime = it.createTime
                        bean.dataList.add(entity)
                    }
                    bean.maxVol =
                        AppDatabase.getInstance().thermalMinDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    bean.minVol =
                        AppDatabase.getInstance().thermalMinDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    Log.w("chart", "电压数据:${bean.dataList.size}")
                    Log.w("chart", "电压数据max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
                3 -> {
                    val resultList =
                        AppDatabase.getInstance().thermalHourDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            ) as ArrayList<ThermalHourEntity>
                    resultList.forEach {
                        val entity = ThermalEntity()
                        entity.userId = it.userId
                        entity.sn = it.sn
                        entity.thermal = it.thermal
                        entity.thermalMax = it.thermalMax
                        entity.thermalMin = it.thermalMin
                        entity.info = it.info
                        entity.type = it.type
                        entity.createTime = it.createTime
                        bean.dataList.add(entity)
                    }
                    bean.maxVol =
                        AppDatabase.getInstance().thermalHourDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    bean.minVol =
                        AppDatabase.getInstance().thermalHourDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    Log.w("chart", "电压数据:${bean.dataList.size}")
                    Log.w("chart", "电压数据max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
                4 -> {
                    val resultList =
                        AppDatabase.getInstance().thermalDayDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            ) as ArrayList<ThermalDayEntity>
                    resultList.forEach {
                        val entity = ThermalEntity()
                        entity.userId = it.userId
                        entity.sn = it.sn
                        entity.thermal = it.thermal
                        entity.thermalMax = it.thermalMax
                        entity.thermalMin = it.thermalMin
                        entity.info = it.info
                        entity.type = it.type
                        entity.createTime = it.createTime
                        bean.dataList.add(entity)
                    }
                    bean.maxVol =
                        AppDatabase.getInstance().thermalDayDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    bean.minVol =
                        AppDatabase.getInstance().thermalDayDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                            )
                    Log.w("chart", "电压数据:${bean.dataList.size}")
                    Log.w("chart", "电压数据max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
            }
            bean.action = action
            if (action == 4) {
                val startTime = TimeTool.showDateType(bean.dataList.first().createTime)
                val endTime = TimeTool.showDateType(bean.dataList.last().createTime)
                Log.w("123", "log start:$startTime, end:$endTime")
            }
            resultLiveData.postValue(bean)
        }
    }

    /**
\1第二项历史记录查询
\1查询历史电压data(等待蓝牙传输历史记录结束后触发)
\1时间区间: 初始时间 => 推进到结束事件
     */
    suspend fun queryLogVolsByStartTime(
        type: Int = 3,
        selectTimeType: Int,
        endLogTime: Long = System.currentTimeMillis(),
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = SharedManager.getUserId()
                val typeStr =
                    when (type) {
                        1 -> "point"
                        2 -> "line"
                        else -> "fence"
                    }
                val bean = ChartList()
\1查询之前先同步data
                val job = async { syncVol(selectTimeType) }
                job.await()
                syncRun = false // 同步结束
                val startLogTime =
                    when (selectTimeType) {
                        /**
\17200data
\1秒:2小时
\1分:5天
\1时:300天
\1天:20年
                         */
\11 -> startLogTime + 2 * 60 * 60 * 1000L //秒(2小时)
\12 -> startLogTime + 24 * 60 * 60 * 1000L //分(1天)
\13 -> startLogTime + 30 * 24 * 60 * 60 * 1000L //时(30天)
\14 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //天(1年)

\11 -> startLogTime + 7200 * 1000L //秒(2小时)
\12 -> startLogTime + 7200 * 60 * 1000L //分(5天)
\13 -> startLogTime + 7200 * 60 * 60 * 1000L //时(300天)
\14 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //天(1年)
//                else -> startLogTime + 7200 * 1000L

                        1 -> endLogTime - 7200 * 1000L // 秒(2小时)
                        2 -> endLogTime - 7200 * 60 * 1000L // 分(5天)
                        3 -> endLogTime - 7200 * 60 * 60 * 1000L // 时(300天)
                        4 -> endLogTime - 10 * 365 * 24 * 60 * 60 * 1000L // 天(10年)
                        else -> endLogTime - 7200 * 1000L
                    }
                when (selectTimeType) {
                    1 -> {
                        bean.dataList =
                            AppDatabase.getInstance().thermalDao()
                                .queryByTime(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                    type = typeStr,
                                ) as ArrayList<ThermalEntity>
                        bean.maxVol =
                            AppDatabase.getInstance().thermalDao()
                                .queryByTimeMax(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        bean.minVol =
                            AppDatabase.getInstance().thermalDao()
                                .queryByTimeMin(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        Log.w("chart", "电压数据:${bean.dataList.size}")
                    }
                    2 -> {
                        val resultList =
                            AppDatabase.getInstance().thermalMinDao()
                                .queryByTime(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                    type = typeStr,
                                ) as ArrayList<ThermalMinuteEntity>
                        resultList.forEach {
                            val entity = ThermalEntity()
                            entity.userId = it.userId
                            entity.sn = it.sn
                            entity.thermal = it.thermal
                            entity.thermalMax = it.thermalMax
                            entity.thermalMin = it.thermalMin
                            entity.info = it.info
                            entity.type = it.type
                            entity.createTime = it.createTime
                            bean.dataList.add(entity)
                        }
                        bean.maxVol =
                            AppDatabase.getInstance().thermalMinDao()
                                .queryByTimeMax(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        bean.minVol =
                            AppDatabase.getInstance().thermalMinDao()
                                .queryByTimeMin(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        Log.w("chart", "电压数据:${bean.dataList.size}")
                    }
                    3 -> {
                        val resultList =
                            AppDatabase.getInstance().thermalHourDao()
                                .queryByTime(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                    type = typeStr,
                                ) as ArrayList<ThermalHourEntity>
                        resultList.forEach {
                            val entity = ThermalEntity()
                            entity.userId = it.userId
                            entity.sn = it.sn
                            entity.thermal = it.thermal
                            entity.thermalMax = it.thermalMax
                            entity.thermalMin = it.thermalMin
                            entity.info = it.info
                            entity.type = it.type
                            entity.createTime = it.createTime
                            bean.dataList.add(entity)
                        }
                        bean.maxVol =
                            AppDatabase.getInstance().thermalHourDao()
                                .queryByTimeMax(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        bean.minVol =
                            AppDatabase.getInstance().thermalHourDao()
                                .queryByTimeMin(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        Log.w("chart", "电压数据:${bean.dataList.size}")
                    }
                    4 -> {
                        val resultList =
                            AppDatabase.getInstance().thermalDayDao()
                                .queryByTime(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                    type = typeStr,
                                ) as ArrayList<ThermalDayEntity>
                        resultList.forEach {
                            val entity = ThermalEntity()
                            entity.userId = it.userId
                            entity.sn = it.sn
                            entity.thermal = it.thermal
                            entity.thermalMax = it.thermalMax
                            entity.thermalMin = it.thermalMin
                            entity.info = it.info
                            entity.type = it.type
                            entity.createTime = it.createTime
                            bean.dataList.add(entity)
                        }
                        bean.maxVol =
                            AppDatabase.getInstance().thermalDayDao()
                                .queryByTimeMax(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        bean.minVol =
                            AppDatabase.getInstance().thermalDayDao()
                                .queryByTimeMin(
                                    userId = userId,
                                    startTime = startLogTime,
                                    endTime = endLogTime,
                                )
                        Log.w("chart", "电压数据:${bean.dataList.size}")
                    }
                }
                delay(500)
                resultLiveData.postValue(bean)
            } catch (e: Exception) {
                XLog.e("数据查询异常:${e.message}")
                resultLiveData.postValue(ChartList())
            }
        }
    }

    /**
\1@param type 1:秒 2:分 3:时 4:天
     */
    private fun getNewVolData(
        data: List<ThermalEntity>,
        type: Int = 2,
    ): ArrayList<ThermalEntity> {
        val newData: ArrayList<ThermalEntity> = arrayListOf()
        var startIndex = 0
        var endIndex = 0
        for (i in data.indices) {
            if (i == 0) {
                if (i == data.size - 1) {
\1默认整个区间
                    addData(data, newData, 0, endIndex)
                }
            } else {
                // [1]..[size-1]
                val currencyTime = TimeTool.showDateType(data[i].createTime, type)
                val previewTime = TimeTool.showDateType(data[i - 1].createTime, type)
                if (i == data.size - 1) {
\1最后一个值
                    if (currencyTime != previewTime) {
\1同时calculation上一个区间和当前最后一个区间
\1最后上一个区间
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        startIndex = i
\1最后一个区间
                        endIndex = i
                        addData(data, newData, startIndex, endIndex)
                    } else {
                        endIndex = i
                        if (newData.size == 0) {
\1默认整个区间
                            addData(data, newData, 0, endIndex)
                        } else {
\1最后一个区间
                            addData(data, newData, startIndex, endIndex)
                        }
                    }
                } else {
                    if (currencyTime != previewTime) {
\1calculation上一个区间
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
\1新时间段
                        startIndex = i
                    }
                }
            }
        }
        return newData
    }

\1calculation平均值
    private fun addData(
        data: List<ThermalEntity>,
        newData: ArrayList<ThermalEntity>,
        startIndex: Int,
        endIndex: Int,
    ) {
        val tempVolEntity = data[startIndex]
        var temp = 0f
        var tempMax = 0f
        var tempMin = 0f
        for (x in startIndex..endIndex) {
            temp += data[x].thermal
            tempMax += data[x].thermalMax
            tempMin += data[x].thermalMin
        }
\1tempVol:0f    startIndex:2    endIndex:1 会出现vol:NaN
        tempVolEntity.thermal = temp / (endIndex - startIndex + 1) // 区间电压平均值
        tempVolEntity.thermalMax = tempMax / (endIndex - startIndex + 1) // 区间电压平均值
        tempVolEntity.thermalMin = tempMin / (endIndex - startIndex + 1) // 区间电压平均值
        newData.add(tempVolEntity)
    }

    @Volatile
    private var syncRun = false

    /**
\1同步data
\1最早时间: 1609430400000 (2021-1-1 00:00:00)
     *
\11. 查询save记录的最新时间
\12. get要update的时间段data[最新data ~ 最新一个时间区间的起始点]
\13. 秒data转分data的平均值
\14. 添加到分data库
\15. 删除多余的data
     */
    private suspend fun syncVol(selectTimeType: Int) {
        Log.i("chart", "syncVol: $syncRun")
        if (syncRun) {
\1有任务正在执行
            return
        }
        Log.i("chart", "syncVol start")
        if (selectTimeType == 1) {
\1秒data不用processing
            return
        }
        syncRun = true
        val userId = SharedManager.getUserId()
\1查询save记录的最新时间
        when (selectTimeType) {
            2 -> {
                val minuteTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2)
\1检查最新时间段有没有data同步
                val minuteVolLatestList =
                    AppDatabase.getInstance().thermalMinDao()
                        .queryByTime(
                            userId = userId,
                            startTime = minuteTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (minuteVolLatestList.isNotEmpty()) {
                    Log.w("chart", "最新时间段已经有记录，不需要同步更新")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalMinDao().queryMaxTime(userId = userId)
                Log.w("chart", "minute latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
\1get要update的时间段data
                val secondVolList =
                    AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = maxTime,
//                        endTime = Long.MAX_VALUE
                            endTime = minuteTime,
                        ) as ArrayList<ThermalEntity>
                if (secondVolList.size > 0) {
                    val startTime = TimeTool.showDateType(secondVolList.first().createTime)
                    val endTime = TimeTool.showDateType(secondVolList.last().createTime)
                    Log.w("chart", "要处理${secondVolList.size}个数据, start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "无数据处理")
                }
\1秒data转分data的平均值
                val minVolList = getNewVolData(secondVolList, 2)
\1添加到分data库
                minVolList.forEach {
                    val bean = ThermalMinuteEntity()
                    try {
                        bean.userId = it.userId
                        bean.sn = it.sn
                        bean.thermal = it.thermal
                        bean.thermalMax = it.thermalMax
                        bean.thermalMin = it.thermalMin
                        bean.info = it.info
                        bean.type = it.type
                        bean.createTime = TimeTool.timeToMinute(it.createTime, 2) // 调整精确到分
                        bean.updateTime = System.currentTimeMillis()
                        AppDatabase.getInstance().thermalMinDao().insert(bean)
                    } catch (e: Exception) {
                        XLog.e("insert error:${e.message}")
                    }
                }
                val bean = ThermalMinuteEntity()
                try {
                    bean.userId = userId
                    bean.thermal = 0f
                    bean.thermalMax = 0f
                    bean.thermalMin = 0f
                    bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2) // 调整精确到分
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalMinDao().insert(bean)
                } catch (e: Exception) {
                    XLog.e("insert error:${e.message}")
                }
\1删除多余的data
                AppDatabase.getInstance().thermalMinDao()
                    .deleteRepeatVol(userId)
            }
            3 -> {
                val hourTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3)
\1检查最新时间段有没有data同步
                val hourVolLatestList =
                    AppDatabase.getInstance().thermalHourDao()
                        .queryByTime(
                            userId = userId,
                            startTime = hourTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (hourVolLatestList.isNotEmpty()) {
                    Log.w("chart", "最新时间段已经有记录，不需要同步更新")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalHourDao().queryMaxTime(userId = userId)
                Log.w("chart", "hour latest  time: $maxTime, ${TimeTool.showDateType(maxTime)}")
\1get要update的时间段data
                val secondVolList =
                    AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = maxTime,
                            endTime = hourTime,
                        ) as ArrayList<ThermalEntity>
                if (secondVolList.size > 0) {
                    val startTime = TimeTool.showDateType(secondVolList.first().createTime)
                    val endTime = TimeTool.showDateType(secondVolList.last().createTime)
                    Log.w("chart", "要处理${secondVolList.size}个数据, start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "无数据处理")
                }
\1秒data转分data的平均值
                val hourVolList = getNewVolData(secondVolList, 3)
\1添加到分data库
                hourVolList.forEach {
                    val bean = ThermalHourEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 3) // 调整精确到分
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalHourDao().insert(bean)
                }
                val bean = ThermalHourEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3) // 调整精确到分
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalHourDao().insert(bean)
\1删除多余的data
                AppDatabase.getInstance().thermalHourDao().deleteRepeatVol(userId)
            }
            4 -> {
                val todayStartTime =
                    TimeTool.timeToMinute(System.currentTimeMillis(), 4) // 天只更新到今天凌晨的数据
\1检查今天有没有data同步
                val todayVolLatestList =
                    AppDatabase.getInstance().thermalDayDao()
                        .queryByTime(
                            userId = userId,
                            startTime = todayStartTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (todayVolLatestList.isNotEmpty()) {
\1今天已经有记录，说明已经同步到今天，不需要同步update
                    Log.w("chart", "今天已经有记录，不需要同步更新")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalDayDao().queryMaxTime(userId = userId)
                Log.w("chart", "day latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
\1get要update的时间段data
                val secondVolList =
                    AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = maxTime,
                            endTime = todayStartTime,
                        ) as ArrayList<ThermalEntity>
\1秒data转分data的平均值
                if (secondVolList.size > 0) {
                    val startTime = TimeTool.showDateType(secondVolList.first().createTime)
                    val endTime = TimeTool.showDateType(secondVolList.last().createTime)
                    Log.w("chart", "要处理${secondVolList.size}个数据, start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "无数据处理")
                }
                val dayVolList = getNewVolData(secondVolList, 4)
\1添加到分data库
                dayVolList.forEach {
                    val bean = ThermalDayEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 4) // 调整精确到分
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalDayDao().insert(bean)
\1此处只update到今天凌晨
                }
\1今天的电压值还没检查结束，不能出平均值结果，并值添加一个无效data(0f)，用updateTime来判断已同步到最新的时间节点
                val bean = ThermalDayEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 4) // 调整精确到分
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalDayDao().insert(bean)
\1删除多余的data
                AppDatabase.getInstance().thermalDayDao().deleteRepeatVol(userId)
            }
        }
        syncRun = false
        Log.w("chart", "syncVol end")
    }

/**
 * Chart list utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
    data class ChartList(
        var dataList: ArrayList<ThermalEntity> = arrayListOf(),
        var maxVol: Float = 0f,
        var minVol: Float = 0f,
        var action: Int = 0,
    )
}
