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
                        Log.w("123", "[CN_TEXT]")
                        // [CN_TEXT]
                        endTime = Date().time
                        startTime = endTime - 7200 * 1000L // 2[CN_TEXT]
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
                        // [CN_TEXT]
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
                        // [CN_TEXT]
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
                        // [CN_TEXT]
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
     * [CN_TEXT]
     * [CN_TEXT]([CN_TEXT])
     * [CN_TEXT]: [CN_TEXT] => [CN_TEXT]
     */
    suspend fun queryLogThermals(
        selectTimeType: Int,
        endLogTime: Long = System.currentTimeMillis(),
        action: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = SharedManager.getUserId()
            val bean = ChartList()
            // [CN_TEXT]
            val job = async { syncVol(selectTimeType) }
            job.await()
            syncRun = false // [CN_TEXT]
            val startLogTime =
                when (selectTimeType) {
                    /**
                     * 7200[CN_TEXT]
                     * [CN_TEXT]:2[CN_TEXT]
                     * [CN_TEXT]:5[CN_TEXT]
                     * [CN_TEXT]:300[CN_TEXT]
                     * [CN_TEXT]:20[CN_TEXT]
                     */
                    1 -> endLogTime - 7200 * 1000L // [CN_TEXT](2[CN_TEXT])
                    2 -> endLogTime - 7200 * 60 * 1000L // [CN_TEXT](5[CN_TEXT])
                    3 -> endLogTime - 7200 * 60 * 60 * 1000L // [CN_TEXT](300[CN_TEXT])
                    4 -> endLogTime - 1 * 365 * 24 * 60 * 60 * 1000L // [CN_TEXT](1[CN_TEXT])
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
                    Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
                    Log.w("chart", "[CN_TEXT]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
                    Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
                    Log.w("chart", "[CN_TEXT]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
                    Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
                    Log.w("chart", "[CN_TEXT]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
                    Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
                    Log.w("chart", "[CN_TEXT]max vol:${bean.maxVol},min vol:${bean.minVol}")
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
     * [CN_TEXT]
     * [CN_TEXT]([CN_TEXT])
     * [CN_TEXT]: [CN_TEXT] => [CN_TEXT]
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
                // [CN_TEXT]
                val job = async { syncVol(selectTimeType) }
                job.await()
                syncRun = false // [CN_TEXT]
                val startLogTime =
                    when (selectTimeType) {
                        /**
                         * 7200[CN_TEXT]
                         * [CN_TEXT]:2[CN_TEXT]
                         * [CN_TEXT]:5[CN_TEXT]
                         * [CN_TEXT]:300[CN_TEXT]
                         * [CN_TEXT]:20[CN_TEXT]
                         */
//                1 -> startLogTime + 2 * 60 * 60 * 1000L //[CN_TEXT](2[CN_TEXT])
//                2 -> startLogTime + 24 * 60 * 60 * 1000L //[CN_TEXT](1[CN_TEXT])
//                3 -> startLogTime + 30 * 24 * 60 * 60 * 1000L //[CN_TEXT](30[CN_TEXT])
//                4 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //[CN_TEXT](1[CN_TEXT])

//                1 -> startLogTime + 7200 * 1000L //[CN_TEXT](2[CN_TEXT])
//                2 -> startLogTime + 7200 * 60 * 1000L //[CN_TEXT](5[CN_TEXT])
//                3 -> startLogTime + 7200 * 60 * 60 * 1000L //[CN_TEXT](300[CN_TEXT])
//                4 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //[CN_TEXT](1[CN_TEXT])
//                else -> startLogTime + 7200 * 1000L

                        1 -> endLogTime - 7200 * 1000L // [CN_TEXT](2[CN_TEXT])
                        2 -> endLogTime - 7200 * 60 * 1000L // [CN_TEXT](5[CN_TEXT])
                        3 -> endLogTime - 7200 * 60 * 60 * 1000L // [CN_TEXT](300[CN_TEXT])
                        4 -> endLogTime - 10 * 365 * 24 * 60 * 60 * 1000L // [CN_TEXT](10[CN_TEXT])
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
                        Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
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
                        Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
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
                        Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
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
                        Log.w("chart", "[CN_TEXT]:${bean.dataList.size}")
                    }
                }
                delay(500)
                resultLiveData.postValue(bean)
            } catch (e: Exception) {
                XLog.e("[CN_TEXT]:${e.message}")
                resultLiveData.postValue(ChartList())
            }
        }
    }

    /**
     * @param type 1:[CN_TEXT] 2:[CN_TEXT] 3:[CN_TEXT] 4:[CN_TEXT]
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
                    // [CN_TEXT]
                    addData(data, newData, 0, endIndex)
                }
            } else {
                // [1]..[size-1]
                val currencyTime = TimeTool.showDateType(data[i].createTime, type)
                val previewTime = TimeTool.showDateType(data[i - 1].createTime, type)
                if (i == data.size - 1) {
                    // [CN_TEXT]
                    if (currencyTime != previewTime) {
                        // [CN_TEXT]Current[CN_TEXT]
                        // [CN_TEXT]
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        startIndex = i
                        // [CN_TEXT]
                        endIndex = i
                        addData(data, newData, startIndex, endIndex)
                    } else {
                        endIndex = i
                        if (newData.size == 0) {
                            // [CN_TEXT]
                            addData(data, newData, 0, endIndex)
                        } else {
                            // [CN_TEXT]
                            addData(data, newData, startIndex, endIndex)
                        }
                    }
                } else {
                    if (currencyTime != previewTime) {
                        // [CN_TEXT]
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        // [CN_TEXT]
                        startIndex = i
                    }
                }
            }
        }
        return newData
    }

    // [CN_TEXT]
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
        // tempVol:0f    startIndex:2    endIndex:1 [CN_TEXT]vol:NaN
        tempVolEntity.thermal = temp / (endIndex - startIndex + 1) // [CN_TEXT]
        tempVolEntity.thermalMax = tempMax / (endIndex - startIndex + 1) // [CN_TEXT]
        tempVolEntity.thermalMin = tempMin / (endIndex - startIndex + 1) // [CN_TEXT]
        newData.add(tempVolEntity)
    }

    @Volatile
    private var syncRun = false

    /**
     * [CN_TEXT]
     * [CN_TEXT]: 1609430400000 (2021-1-1 00:00:00)
     *
     * 1. [CN_TEXT]
     * 2. [CN_TEXT][[CN_TEXT] ~ [CN_TEXT]]
     * 3. [CN_TEXT]
     * 4. [CN_TEXT]
     * 5. Delete[CN_TEXT]
     */
    private suspend fun syncVol(selectTimeType: Int) {
        Log.i("chart", "syncVol: $syncRun")
        if (syncRun) {
            // [CN_TEXT]
            return
        }
        Log.i("chart", "syncVol start")
        if (selectTimeType == 1) {
            // [CN_TEXT]
            return
        }
        syncRun = true
        val userId = SharedManager.getUserId()
        // [CN_TEXT]
        when (selectTimeType) {
            2 -> {
                val minuteTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2)
                // [CN_TEXT]
                val minuteVolLatestList =
                    AppDatabase.getInstance().thermalMinDao()
                        .queryByTime(
                            userId = userId,
                            startTime = minuteTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (minuteVolLatestList.isNotEmpty()) {
                    Log.w("chart", "[CN_TEXT]，[CN_TEXT]")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalMinDao().queryMaxTime(userId = userId)
                Log.w("chart", "minute latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
                // [CN_TEXT]
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
                    Log.w("chart", "[CN_TEXT]${secondVolList.size}[CN_TEXT], start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "[CN_TEXT]")
                }
                // [CN_TEXT]
                val minVolList = getNewVolData(secondVolList, 2)
                // [CN_TEXT]
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
                        bean.createTime = TimeTool.timeToMinute(it.createTime, 2) // [CN_TEXT]
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
                    bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2) // [CN_TEXT]
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalMinDao().insert(bean)
                } catch (e: Exception) {
                    XLog.e("insert error:${e.message}")
                }
                // Delete[CN_TEXT]
                AppDatabase.getInstance().thermalMinDao()
                    .deleteRepeatVol(userId)
            }
            3 -> {
                val hourTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3)
                // [CN_TEXT]
                val hourVolLatestList =
                    AppDatabase.getInstance().thermalHourDao()
                        .queryByTime(
                            userId = userId,
                            startTime = hourTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (hourVolLatestList.isNotEmpty()) {
                    Log.w("chart", "[CN_TEXT]，[CN_TEXT]")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalHourDao().queryMaxTime(userId = userId)
                Log.w("chart", "hour latest  time: $maxTime, ${TimeTool.showDateType(maxTime)}")
                // [CN_TEXT]
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
                    Log.w("chart", "[CN_TEXT]${secondVolList.size}[CN_TEXT], start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "[CN_TEXT]")
                }
                // [CN_TEXT]
                val hourVolList = getNewVolData(secondVolList, 3)
                // [CN_TEXT]
                hourVolList.forEach {
                    val bean = ThermalHourEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 3) // [CN_TEXT]
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalHourDao().insert(bean)
                }
                val bean = ThermalHourEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3) // [CN_TEXT]
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalHourDao().insert(bean)
                // Delete[CN_TEXT]
                AppDatabase.getInstance().thermalHourDao().deleteRepeatVol(userId)
            }
            4 -> {
                val todayStartTime =
                    TimeTool.timeToMinute(System.currentTimeMillis(), 4) // [CN_TEXT]
                // [CN_TEXT]
                val todayVolLatestList =
                    AppDatabase.getInstance().thermalDayDao()
                        .queryByTime(
                            userId = userId,
                            startTime = todayStartTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (todayVolLatestList.isNotEmpty()) {
                    // [CN_TEXT]，[CN_TEXT]，[CN_TEXT]
                    Log.w("chart", "[CN_TEXT]，[CN_TEXT]")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalDayDao().queryMaxTime(userId = userId)
                Log.w("chart", "day latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
                // [CN_TEXT]
                val secondVolList =
                    AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = maxTime,
                            endTime = todayStartTime,
                        ) as ArrayList<ThermalEntity>
                // [CN_TEXT]
                if (secondVolList.size > 0) {
                    val startTime = TimeTool.showDateType(secondVolList.first().createTime)
                    val endTime = TimeTool.showDateType(secondVolList.last().createTime)
                    Log.w("chart", "[CN_TEXT]${secondVolList.size}[CN_TEXT], start:$startTime, end:$endTime")
                } else {
                    Log.w("chart", "[CN_TEXT]")
                }
                val dayVolList = getNewVolData(secondVolList, 4)
                // [CN_TEXT]
                dayVolList.forEach {
                    val bean = ThermalDayEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 4) // [CN_TEXT]
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalDayDao().insert(bean)
                    // [CN_TEXT]
                }
                // [CN_TEXT]，[CN_TEXT]，[CN_TEXT](0f)，[CN_TEXT]updateTime[CN_TEXT]
                val bean = ThermalDayEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 4) // [CN_TEXT]
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalDayDao().insert(bean)
                // Delete[CN_TEXT]
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
