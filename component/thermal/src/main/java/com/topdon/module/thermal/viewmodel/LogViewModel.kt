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
                    0 -> {
                        Log.w("123", "Query seconds")
                        // Seconds
                        endTime = Date().time
                        startTime = endTime - 7200 * 1000L // 2 hours
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
                    1 -> {
                        // Minutes
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
                    2 -> {
                        // Hours
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
// days
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
// First item real-time chart historical record query
// Query historical voltage data (triggered after Bluetooth transfer historical record ends)
// Time interval: current time => rollback to start event
     */
    suspend fun queryLogThermals(
        selectTimeType: Int,
        endLogTime: Long = System.currentTimeMillis(),
        action: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = SharedManager.getUserId()
            val bean = ChartList()
// Synchronize data before querying
            val job = async { syncVol(selectTimeType) }
            job.await()
            syncRun = false // Synchronizeend
            val startLogTime =
                    when (selectTimeType) {
                        1 -> System.currentTimeMillis() - 2 * 60 * 60 * 1000L // seconds (2 hours)
                        2 -> System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L // 分(5days)
                        3 -> System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L // 时(30days)
                        4 -> System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000L // days(1年)
                        else -> System.currentTimeMillis() - 2 * 60 * 60 * 1000L
                }
            
            // Query thermal data based on selectTimeType
            when (selectTimeType) {
                1 -> { // Seconds - main thermal table
                    bean.dataList = AppDatabase.getInstance().thermalDao()
                        .queryByTime(userId, startLogTime, endLogTime) as ArrayList<ThermalEntity>
                    bean.maxVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMax(userId, startLogTime, endLogTime)  
                    bean.minVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMin(userId, startLogTime, endLogTime)
                    Log.w("chart", "voltage data:${bean.dataList.size}")
                    Log.w("chart", "voltage datamax vol:${bean.maxVol},min vol:${bean.minVol}")
                }
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
                    Log.w("chart", "voltage data:${bean.dataList.size}")
                    Log.w("chart", "voltage datamax vol:${bean.maxVol},min vol:${bean.minVol}")
                }
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
                    Log.w("chart", "voltage data:${bean.dataList.size}")
                    Log.w("chart", "voltage datamax vol:${bean.maxVol},min vol:${bean.minVol}")
                }
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
                    Log.w("chart", "voltage data:${bean.dataList.size}")
                    Log.w("chart", "voltage datamax vol:${bean.maxVol},min vol:${bean.minVol}")
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
// Second item historical record query
// Query historical voltage data (triggered after Bluetooth transfer historical record ends)
// Time interval: initial time => advance to end event
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
                        else -> "fence"
                    }
                val bean = ChartList()
// Synchronize data before querying
                val job = async { syncVol(selectTimeType) }
                job.await()
                syncRun = false // Synchronizeend
                val startLogTime =
                    when (selectTimeType) {
                        1 -> System.currentTimeMillis() - 2 * 60 * 60 * 1000L // seconds (2 hours)
                        2 -> System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L // 分(5days)
                        3 -> System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L // 时(30days)
                        4 -> System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000L // days(1年)
                        else -> System.currentTimeMillis() - 2 * 60 * 60 * 1000L
                    }
                    when (selectTimeType) {
                        /**
7200data
// seconds: 2 hours
// 分:5days
// 时:300days
// days:20年
                         */

//                else -> startLogTime + 7200 * 1000L

                        else -> endLogTime - 7200 * 1000L
                    }
                when (selectTimeType) {
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
                        Log.w("chart", "voltage data:${bean.dataList.size}")
                    }
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
                        Log.w("chart", "voltage data:${bean.dataList.size}")
                    }
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
                        Log.w("chart", "voltage data:${bean.dataList.size}")
                    }
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
                        Log.w("chart", "voltage data:${bean.dataList.size}")
                    }
                }
                delay(500)
                resultLiveData.postValue(bean)
            } catch (e: Exception) {
                XLog.e("data query exception:${e.message}")
                resultLiveData.postValue(ChartList())
            }
        }
    }

    /**
// @param type 1:seconds 2:minutes 3:hours 
4:// days
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
// Default entire interval
                    addData(data, newData, 0, endIndex)
                }
            } else {
                // [1]..[size-1]
                val currencyTime = TimeTool.showDateType(data[i].createTime, type)
                val previewTime = TimeTool.showDateType(data[i - 1].createTime, type)
                if (i == data.size - 1) {
// Last value
                    if (currencyTime != previewTime) {
// Calculate both previous interval and current last interval
// Last previous interval
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        startIndex = i
// Last interval
                        endIndex = i
                        addData(data, newData, startIndex, endIndex)
                    } else {
                        endIndex = i
                        if (newData.size == 0) {
// Default entire interval
                            addData(data, newData, 0, endIndex)
                        } else {
// Last interval
                            addData(data, newData, startIndex, endIndex)
                        }
                    }
                } else {
                    if (currencyTime != previewTime) {
// Calculate previous interval
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
// New time period
                        startIndex = i
                    }
                }
            }
        }
        return newData
    }

// Calculate average value
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
        // Note: tempVol:0f startIndex:2 endIndex:1 can result in vol:NaN
        tempVolEntity.thermal = temp / (endIndex - startIndex + 1) // Interval voltage average value
        tempVolEntity.thermalMax = tempMax / (endIndex - startIndex + 1) // Interval voltage average value
        tempVolEntity.thermalMin = tempMin / (endIndex - startIndex + 1) // Interval voltage average value
        newData.add(tempVolEntity)
    }

    @Volatile
    private var syncRun = false

    /**
Synchronizedata
// Earliest time: 1609430400000 (2021-1-1 00:00:00)
     *
// 1. Query latest saved record
time
// 2. Get time period data to update [latest data ~ start point of latest time interval]
// 3. Convert second data to minute data average values
// 4. Add to minute database
// 5. Delete excess data
     */
    private suspend fun syncVol(selectTimeType: Int) {
        Log.i("chart", "syncVol: $syncRun")
        if (syncRun) {
// Task is currently executing
            return
        }
        Log.i("chart", "syncVol start")
        if (selectTimeType == 1) {
// Second data doesn't need processing
            return
        }
        syncRun = true
        val userId = SharedManager.getUserId()
// Query latest saved record time
        when (selectTimeType) {
                val minuteTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2)
// Check最New time period有没有dataSynchronize
                val minuteVolLatestList =
                    AppDatabase.getInstance().thermalMinDao()
                        .queryByTime(
                            userId = userId,
                            startTime = minuteTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (minuteVolLatestList.isNotEmpty()) {
                    Log.w("chart", "最New time period已经有Record，不需// 要Synchronizeupdate")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalMinDao().queryMaxTime(userId = userId)
                Log.w("chart", "minute latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
// Get time period data to update
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
                    Log.w("chart", "To process${secondVolList.size}//  data entries, start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "No data to process")
                }
// Convert second data to minute data average values
                val minVolList = getNewVolData(secondVolList, 2)
// Add to minute database
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
                        bean.createTime = TimeTool.timeToMinute(it.createTime, 2) // Adjust to minute precision
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
                    bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2) // Adjust to minute precision
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalMinDao().insert(bean)
                } catch (e: Exception) {
                    XLog.e("insert error:${e.message}")
                }
// Delete excess data
                AppDatabase.getInstance().thermalMinDao()
                    .deleteRepeatVol(userId)
            }
                val hourTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3)
// Check最New time period有没有dataSynchronize
                val hourVolLatestList =
                    AppDatabase.getInstance().thermalHourDao()
                        .queryByTime(
                            userId = userId,
                            startTime = hourTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (hourVolLatestList.isNotEmpty()) {
                    Log.w("chart", "最New time period已经有Record，不需// 要Synchronizeupdate")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalHourDao().queryMaxTime(userId = userId)
                Log.w("chart", "hour latest  time: $maxTime, ${TimeTool.showDateType(maxTime)}")
// Get time period data to update
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
                    Log.w("chart", "To process${secondVolList.size}//  data entries, start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "No data to process")
                }
// Convert second data to minute data average values
                val hourVolList = getNewVolData(secondVolList, 3)
// Add to minute database
                hourVolList.forEach {
                    val bean = ThermalHourEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 3) // Adjust to minute precision
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalHourDao().insert(bean)
                }
                val bean = ThermalHourEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3) // Adjust to minute precision
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalHourDao().insert(bean)
// Delete excess data
                AppDatabase.getInstance().thermalHourDao().deleteRepeatVol(userId)
            }
                val todayStartTime =
                    TimeTool.timeToMinute(System.currentTimeMillis(), 4) // days只update到今days凌晨的data
// Check今days有没有dataSynchronize
                val todayVolLatestList =
                    AppDatabase.getInstance().thermalDayDao()
                        .queryByTime(
                            userId = userId,
                            startTime = todayStartTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (todayVolLatestList.isNotEmpty()) {
// 今days已经有Record，description已经Synchronize到今days，不需要Synchronizeupdate
                    // Log.w("chart", "今days已经有Record，不需要Synchronizeupdate")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalDayDao().queryMaxTime(userId = userId)
                Log.w("chart", "day latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
// Get time period data to update
                val secondVolList =
                    AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = maxTime,
                            endTime = todayStartTime,
                        ) as ArrayList<ThermalEntity>
                // Convert second data to minute data average values
                if (secondVolList.size > 0) {
                    val startTime = TimeTool.showDateType(secondVolList.first().createTime)
                    val endTime = TimeTool.showDateType(secondVolList.last().createTime)
                    Log.w("chart", "Processing ${secondVolList.size} data entries, start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "No data to process")
                }
                val dayVolList = getNewVolData(secondVolList, 4)
                // Add to day data database
                dayVolList.forEach {
                    val bean = ThermalDayEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 4) // Adjust to minute precision
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalDayDao().insert(bean)
                    // Update only until today midnight
                }
                // Today's thermal values are not checked yet, cannot average results, add placeholder data (0f), use updateTime to judge synchronized to latest time point
                val bean = ThermalDayEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 4) // Adjust to minute precision
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalDayDao().insert(bean)
                // Delete excess data
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
