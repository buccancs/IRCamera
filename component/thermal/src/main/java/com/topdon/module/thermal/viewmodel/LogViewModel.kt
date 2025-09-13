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
                        Log.w("123", "Test Data")
\1Text
                        endTime = Date().time
                        startTime = endTime - 7200 * 1000L // 2
                        Log.w("123", "query startTime:$startTime, endTime:$endTime")
                        dataList =
                            AppDatabase.getInstance().thermalDao()
                                .getThermalByDate(
                                    SharedManager.getUserId(),
                                    startTime,
                                    endTime,
                                ) as ArrayList<ThermalEntity>
                        Log.w("123", "data size: ${dataList.size}"Test Data"123", "dataList size:${dataList.size}"Test Data"chart", "Test Data")
                    Log.w("chart", "Test Data")
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
                    Log.w("chart", "Test Data")
                    Log.w("chart", "Test Data")
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
                    Log.w("chart", "Test Data")
                    Log.w("chart", "Test Data")
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
                    Log.w("chart", "Test Data")
                    Log.w("chart", "Test Data")
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
\1
\1data()
\1:  => 
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
                        else -> "fence"Test Data"chart", "Test Data")
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
                        Log.w("chart", "Test Data")
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
                        Log.w("chart", "Test Data")
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
                        Log.w("chart", "Test Data")
                    }
                }
                delay(500)
                resultLiveData.postValue(bean)
            } catch (e: Exception) {
                XLog.e("Test Data")
                resultLiveData.postValue(ChartList())
            }
        }
    }

    /**
\1@param type 1: 2: 3: 4:
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
\1Text
                    addData(data, newData, 0, endIndex)
                }
            } else {
                // [1]..[size-1]
                val currencyTime = TimeTool.showDateType(data[i].createTime, type)
                val previewTime = TimeTool.showDateType(data[i - 1].createTime, type)
                if (i == data.size - 1) {
\1Text
                    if (currencyTime != previewTime) {
\1TextcalculationText
\1Text
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        startIndex = i
\1Text
                        endIndex = i
                        addData(data, newData, startIndex, endIndex)
                    } else {
                        endIndex = i
                        if (newData.size == 0) {
\1Text
                            addData(data, newData, 0, endIndex)
                        } else {
\1Text
                            addData(data, newData, startIndex, endIndex)
                        }
                    }
                } else {
                    if (currencyTime != previewTime) {
\1calculationText
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
\1Text
                        startIndex = i
                    }
                }
            }
        }
        return newData
    }

\1calculationText
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
\1tempVol:0f    startIndex:2    endIndex:1 Textvol:NaN
        tempVolEntity.thermal = temp / (endIndex - startIndex + 1) // 
        tempVolEntity.thermalMax = tempMax / (endIndex - startIndex + 1) // 
        tempVolEntity.thermalMin = tempMin / (endIndex - startIndex + 1) // 
        newData.add(tempVolEntity)
    }

    @Volatile
    private var syncRun = false

    /**
\1data
\1: 1609430400000 (2021-1-1 00:00:00)
     *
\11. save
\12. getupdatedata[data ~ ]
\13. datadata
\14. data
\15. data
     */
    private suspend fun syncVol(selectTimeType: Int) {
        Log.i("chart", "syncVol: $syncRun"Test Data"chart", "syncVol start"Test Data"chart", "Test Data")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalMinDao().queryMaxTime(userId = userId)
                Log.w("chart", "minute latest time: $maxTime, ${TimeTool.showDateType(maxTime)}"Test Data"chart", "Test Data")
                } else {
                    Log.w("chart", "Test Data")
                }
\1TextdataTextdataText
                val minVolList = getNewVolData(secondVolList, 2)
\1TextdataText
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
                        bean.createTime = TimeTool.timeToMinute(it.createTime, 2) // 
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
                    bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2) // 
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalMinDao().insert(bean)
                } catch (e: Exception) {
                    XLog.e("insert error:${e.message}"Test Data"chart", "Test Data")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalHourDao().queryMaxTime(userId = userId)
                Log.w("chart", "hour latest  time: $maxTime, ${TimeTool.showDateType(maxTime)}"Test Data"chart", "Test Data")
                } else {
                    Log.w("chart", "Test Data")
                }
\1TextdataTextdataText
                val hourVolList = getNewVolData(secondVolList, 3)
\1TextdataText
                hourVolList.forEach {
                    val bean = ThermalHourEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 3) // 
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalHourDao().insert(bean)
                }
                val bean = ThermalHourEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3) // 
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalHourDao().insert(bean)
\1Textdata
                AppDatabase.getInstance().thermalHourDao().deleteRepeatVol(userId)
            }
            4 -> {
                val todayStartTime =
                    TimeTool.timeToMinute(System.currentTimeMillis(), 4) // 
\1TextdataText
                val todayVolLatestList =
                    AppDatabase.getInstance().thermalDayDao()
                        .queryByTime(
                            userId = userId,
                            startTime = todayStartTime,
                            endTime = System.currentTimeMillis(),
                        )
                if (todayVolLatestList.isNotEmpty()) {
\1Text，Text，Textupdate
                    Log.w("chart", "Test Data")
                    return
                }
                val maxTime =
                    AppDatabase.getInstance().thermalDayDao().queryMaxTime(userId = userId)
                Log.w("chart", "day latest time: $maxTime, ${TimeTool.showDateType(maxTime)}"Test Data"chart", "Test Data")
                } else {
                    Log.w("chart", "Test Data")
                }
                val dayVolList = getNewVolData(secondVolList, 4)
\1TextdataText
                dayVolList.forEach {
                    val bean = ThermalDayEntity()
                    bean.userId = it.userId
                    bean.sn = it.sn
                    bean.thermal = it.thermal
                    bean.thermalMax = it.thermalMax
                    bean.thermalMin = it.thermalMin
                    bean.info = it.info
                    bean.type = it.type
                    bean.createTime = TimeTool.timeToMinute(it.createTime, 4) // 
                    bean.updateTime = System.currentTimeMillis()
                    AppDatabase.getInstance().thermalDayDao().insert(bean)
\1TextupdateText
                }
\1Text，Text，Textdata(0f)，TextupdateTimeText
                val bean = ThermalDayEntity()
                bean.userId = userId
                bean.thermal = 0f
                bean.thermalMax = 0f
                bean.thermalMin = 0f
                bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 4) // 
                bean.updateTime = System.currentTimeMillis()
                AppDatabase.getInstance().thermalDayDao().insert(bean)
\1Textdata
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
