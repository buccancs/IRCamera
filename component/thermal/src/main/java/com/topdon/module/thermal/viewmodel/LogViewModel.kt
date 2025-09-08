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
                        Log.w("123", "[Chinese text]")
                        // [Chinese text]
                        endTime = Date().time
                        startTime = endTime - 7200 * 1000L // 2[Chinese text]
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
                        // [Chinese text]
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
                        // [Chinese text]
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
                        // [Chinese text]
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
     * [Chinese text]
     * [Chinese text]([Chinese text])
     * [Chinese text]: [Chinese text] => [Chinese text]startevent
     */
    suspend fun queryLogThermals(
        selectTimeType: Int,
        endLogTime: Long = System.currentTimeMillis(),
        action: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = SharedManager.getUserId()
            val bean = ChartList()
            // [Chinese text]
            val job = async { syncVol(selectTimeType) }
            job.await()
            syncRun = false // [Chinese text]
            val startLogTime =
                when (selectTimeType) {
                    /**
                     * 7200[Chinese text]
                     * [Chinese text]:2[Chinese text]
                     * [Chinese text]:5[Chinese text]
                     * [Chinese text]:300[Chinese text]
                     * [Chinese text]:20[Chinese text]
                     */
                    1 -> endLogTime - 7200 * 1000L // [Chinese text](2[Chinese text])
                    2 -> endLogTime - 7200 * 60 * 1000L // [Chinese text](5[Chinese text])
                    3 -> endLogTime - 7200 * 60 * 60 * 1000L // [Chinese text](300[Chinese text])
                    4 -> endLogTime - 1 * 365 * 24 * 60 * 60 * 1000L // [Chinese text](1[Chinese text])
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
                    Log.w("chart", "[Chinese text]:${bean.dataList.size}")
                    Log.w("chart", "[Chinese text]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
                    Log.w("chart", "[Chinese text]:${bean.dataList.size}")
                    Log.w("chart", "[Chinese text]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
                    Log.w("chart", "[Chinese text]:${bean.dataList.size}")
                    Log.w("chart", "[Chinese text]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
                    Log.w("chart", "[Chinese text]:${bean.dataList.size}")
                    Log.w("chart", "[Chinese text]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
     * [Chinese text]
     * [Chinese text]([Chinese text])
     * [Chinese text]: [Chinese text] => [Chinese text]event
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
                // [Chinese text]
                val job = async { syncVol(selectTimeType) }
                job.await()
                syncRun = false // [Chinese text]
                val startLogTime =
                    when (selectTimeType) {
                        /**
                         * 7200[Chinese text]
                         * [Chinese text]:2[Chinese text]
                         * [Chinese text]:5[Chinese text]
                         * [Chinese text]:300[Chinese text]
                         * [Chinese text]:20[Chinese text]
                         */
//                1 -> startLogTime + 2 * 60 * 60 * 1000L // [Chinese text](2[Chinese text])
//                2 -> startLogTime + 24 * 60 * 60 * 1000L // [Chinese text](1[Chinese text])
//                3 -> startLogTime + 30 * 24 * 60 * 60 * 1000L // [Chinese text](30[Chinese text])
//                4 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L // [Chinese text](1[Chinese text])

//                1 -> startLogTime + 7200 * 1000L // [Chinese text](2[Chinese text])
//                2 -> startLogTime + 7200 * 60 * 1000L // [Chinese text](5[Chinese text])
//                3 -> startLogTime + 7200 * 60 * 60 * 1000L // [Chinese text](300[Chinese text])
//                4 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L // [Chinese text](1[Chinese text])
//                else -> startLogTime + 7200 * 1000L

                        1 -> endLogTime - 7200 * 1000L // [Chinese text](2[Chinese text])
                        2 -> endLogTime - 7200 * 60 * 1000L // [Chinese text](5[Chinese text])
                        3 -> endLogTime - 7200 * 60 * 60 * 1000L // [Chinese text](300[Chinese text])
                        4 -> endLogTime - 10 * 365 * 24 * 60 * 60 * 1000L // [Chinese text](10[Chinese text])
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
                        Log.w("chart", "[Chinese text]:${bean.dataList.size}")
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
                        Log.w("chart", "[Chinese text]:${bean.dataList.size}")
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
                        Log.w("chart", "[Chinese text]:${bean.dataList.size}")
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
                        Log.w("chart", "[Chinese text]:${bean.dataList.size}")
                    }
                }
                delay(500)
                resultLiveData.postValue(bean)
            } catch (e: Exception) {
                XLog.e("[Chinese text]:${e.message}")
                resultLiveData.postValue(ChartList())
            }
        }
    }

    /**
     * @param type 1:[Chinese text] 2:[Chinese text] 3:[Chinese text] 4:[Chinese text]
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
                    // [Chinese text]
                    addData(data, newData, 0, endIndex)
                }
            } else {
                // [1]..[size-1]
                val currencyTime = TimeTool.showDateType(data[i].createTime, type)
                val previewTime = TimeTool.showDateType(data[i - 1].createTime, type)
                if (i == data.size - 1) {
                    // [Chinese text]
                    if (currencyTime != previewTime) {
                        // [Chinese text]
                        // [Chinese text]
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        startIndex = i
                        // [Chinese text]
                        endIndex = i
                        addData(data, newData, startIndex, endIndex)
                    } else {
                        endIndex = i
                        if (newData.size == 0) {
                            // [Chinese text]
                            addData(data, newData, 0, endIndex)
                        } else {
                            // [Chinese text]
                            addData(data, newData, startIndex, endIndex)
                        }
                    }
                } else {
                    if (currencyTime != previewTime) {
                        // [Chinese text]
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        // [Chinese text]
                        startIndex = i
                    }
                }
            }
        }
        return newData
    }

    // [Chinese text]
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
        // tempVol:0f    startIndex:2    endIndex:1 [Chinese text]vol:NaN
        tempVolEntity.thermal = temp / (endIndex - startIndex + 1) // [Chinese text]
        tempVolEntity.thermalMax = tempMax / (endIndex - startIndex + 1) // [Chinese text]
        tempVolEntity.thermalMin = tempMin / (endIndex - startIndex + 1) // [Chinese text]
        newData.add(tempVolEntity)
    }

    @Volatile
    private var syncRun = false

    /**
     * [Chinese text]
     * [Chinese text]: 1609430400000 (2021-1-1 00:00:00)
     *
     * 1. [Chinese text]
     * 2. [Chinese text][[Chinese text] ~ [Chinese text]point]
     * 3. [Chinese text]
     * 4. [Chinese text]
     * 5. [Chinese text]
     */
    private suspend fun syncVol(selectTimeType: Int) {
        Log.i("chart", "syncVol: $syncRun")
        if (syncRun) {
            // [Chinese text]
            return
        }
        Log.i("chart", "syncVol start")
        if (selectTimeType == 1) {
            // [Chinese text]
            return
        }
        syncRun = true
        val userId = SharedManager.getUserId()
        // [Chinese text]
        when (selectTimeType) {
            2 -> {
                val minuteTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2)
                // [Chinese text]
                val minuteVolLatestList =
                    AppDatabase.getInstance().thermalMinDao()
                        .queryByTime(
                            userId = userId,
                            startTime = minuteTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (minuteVolLatestList.isNotEmpty()) {
                    Log.w("chart", "[Chinese text], [Chinese text]")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalMinDao().queryMaxTime(userId = userId)
                Log.w("chart", "minute latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
                // [Chinese text]
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
                    Log.w("chart", "[Chinese text]${secondVolList.size}[Chinese text], start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "[Chinese text]")
                }
                // [Chinese text]
                val minVolList = getNewVolData(secondVolList, 2)
                // [Chinese text]
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
                        bean.createTime = TimeTool.timeToMinute(it.createTime, 2) // [Chinese text]
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
                    bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2) // [Chinese text]
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalMinDao().insert(bean)
                } catch (e: Exception) {
                    XLog.e("insert error:${e.message}")
                }
                // [Chinese text]
                AppDatabase.getInstance().thermalMinDao()
                    .deleteRepeatVol(userId)
            }
            3 -> {
                val hourTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3)
                // [Chinese text]
                val hourVolLatestList =
                    AppDatabase.getInstance().thermalHourDao()
                        .queryByTime(
                            userId = userId,
                            startTime = hourTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (hourVolLatestList.isNotEmpty()) {
                    Log.w("chart", "[Chinese text], [Chinese text]")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalHourDao().queryMaxTime(userId = userId)
                Log.w("chart", "hour latest  time: $maxTime, ${TimeTool.showDateType(maxTime)}")
                // [Chinese text]
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
                    Log.w("chart", "[Chinese text]${secondVolList.size}[Chinese text], start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "[Chinese text]")
                }
                // [Chinese text]
                val hourVolList = getNewVolData(secondVolList, 3)
                // [Chinese text]
                hourVolList.forEach {
                    val bean = ThermalHourEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 3) // [Chinese text]
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalHourDao().insert(bean)
                }
                val bean = ThermalHourEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3) // [Chinese text]
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalHourDao().insert(bean)
                // [Chinese text]
                AppDatabase.getInstance().thermalHourDao().deleteRepeatVol(userId)
            }
            4 -> {
                val todayStartTime =
                    TimeTool.timeToMinute(System.currentTimeMillis(), 4) // [Chinese text]
                // [Chinese text]
                val todayVolLatestList =
                    AppDatabase.getInstance().thermalDayDao()
                        .queryByTime(
                            userId = userId,
                            startTime = todayStartTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (todayVolLatestList.isNotEmpty()) {
                    // [Chinese text], [Chinese text], [Chinese text]
                    Log.w("chart", "[Chinese text], [Chinese text]")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalDayDao().queryMaxTime(userId = userId)
                Log.w("chart", "day latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
                // [Chinese text]
                val secondVolList =
                    AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = maxTime,
                            endTime = todayStartTime,
                        ) as ArrayList<ThermalEntity>
                // [Chinese text]
                if (secondVolList.size > 0) {
                    val startTime = TimeTool.showDateType(secondVolList.first().createTime)
                    val endTime = TimeTool.showDateType(secondVolList.last().createTime)
                    Log.w("chart", "[Chinese text]${secondVolList.size}[Chinese text], start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "[Chinese text]")
                }
                val dayVolList = getNewVolData(secondVolList, 4)
                // [Chinese text]
                dayVolList.forEach {
                    val bean = ThermalDayEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 4) // [Chinese text]
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalDayDao().insert(bean)
                    // [Chinese text]
                }
                // [Chinese text], [Chinese text], [Chinese text](0f), [Chinese text]updateTime[Chinese text]point
                val bean = ThermalDayEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 4) // [Chinese text]
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalDayDao().insert(bean)
                // [Chinese text]
                AppDatabase.getInstance().thermalDayDao().deleteRepeatVol(userId)
            }
        }
        syncRun = false
        Log.w("chart", "syncVol end")
    }

    data class ChartList(
        var dataList: ArrayList<ThermalEntity> = arrayListOf(),
        var maxVol: Float = 0f,
        var minVol: Float = 0f,
        var action: Int = 0,
    )
}
