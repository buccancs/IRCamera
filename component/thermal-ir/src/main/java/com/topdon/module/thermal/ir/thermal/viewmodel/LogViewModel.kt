package com.topdon.module.thermal.ir.thermal.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.ktbase.BaseViewModel
import com.topdon.lib.core.tools.TimeTool
import kotlinx.coroutines.*
import java.util.*

class LogViewModel : BaseViewModel() {

    /** resultLiveData property */
    val resultLiveData = MutableLiveData<ChartList>()

    private var queryJob: Job? = null

    /**
     * Function description.
     */
    fun queryLogByType(selectType: Int) {
        if (queryJob != null && queryJob!!.isActive) {
            queryJob!!.cancel()
            queryJob = null
        }
        queryJob = viewModelScope.launch(Dispatchers.IO) {
            var dataList: ArrayList<ThermalEntity>? = arrayListOf()
            var startTime = 0L
            var endTime = 0L
            when (selectType) {
                1 -> {
                    Log.w("123", "")
                    endTime = Date().time
                    startTime = endTime - 7200 * 1000L //2
                    Log.w("123", "query startTime:$startTime, endTime:$endTime")
                    dataList = AppDatabase.getInstance().thermalDao()
                        .getThermalByDate(
                            SharedManager.getUserId(),
                            startTime,
                            endTime
                        ) as ArrayList<ThermalEntity>
                    Log.w("123", "data size: ${dataList.size}")
                }
                2 -> {
                    endTime = Date().time
                    startTime = endTime - 7200 * 60 * 1000L
                    dataList = AppDatabase.getInstance().thermalDao()
                        .getThermalByDate(
                            SharedManager.getUserId(),
                            startTime,
                            endTime
                        ) as ArrayList<ThermalEntity>
                }
                3 -> {
                    endTime = Date().time
                    startTime = endTime - 7200 * 60 * 60 * 1000L
                    dataList = AppDatabase.getInstance().thermalDao()
                        .getThermalByDate(
                            SharedManager.getUserId(),
                            startTime,
                            endTime
                        ) as ArrayList<ThermalEntity>
                }
                else -> {
                    dataList = AppDatabase.getInstance().thermalDao()
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

     * ()
     * :  =>
    suspend fun queryLogThermals(
        selectTimeType: Int,
        endLogTime: Long = System.currentTimeMillis(),
        action: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = SharedManager.getUserId()
            val bean = ChartList()
            val job = async { syncVol(selectTimeType) }
            job.await()
            syncRun = false//
            val startLogTime = when (selectTimeType) {
                 * 7200
                 * :2
                 * :5
                 * :300
                 * :20
                1 -> endLogTime - 7200 * 1000L //(2)
                2 -> endLogTime - 7200 * 60 * 1000L //(5)
                3 -> endLogTime - 7200 * 60 * 60 * 1000L //(300)
                4 -> endLogTime - 1 * 365 * 24 * 60 * 60 * 1000L //(1)
                else -> endLogTime - 7200 * 1000L
            }
            when (selectTimeType) {
                1 -> {
                    bean.dataList = AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) as ArrayList<ThermalEntity>
                    bean.maxVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMax(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    bean.minVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMin(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    Log.w("chart", ":${bean.dataList.size}")
                    Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
                2 -> {
                    bean.dataList = AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) as ArrayList<ThermalEntity>
                    bean.maxVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMax(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    bean.minVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMin(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    Log.w("chart", ":${bean.dataList.size}")
                    Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
                3 -> {
                    bean.dataList = AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) as ArrayList<ThermalEntity>
                    bean.maxVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMax(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    bean.minVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMin(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    Log.w("chart", ":${bean.dataList.size}")
                    Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
                4 -> {
                    bean.dataList = AppDatabase.getInstance().thermalDao()
                        .queryByTime(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) as ArrayList<ThermalEntity>
                    bean.maxVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMax(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    bean.minVol = AppDatabase.getInstance().thermalDao()
                        .queryByTimeMin(
                            userId = userId,
                            startTime = startLogTime,
                            endTime = endLogTime
                        ) ?: 0f
                    Log.w("chart", ":${bean.dataList.size}")
                    Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
                }
            }
            bean.action = action
            if (action == 4 && bean.dataList.isNotEmpty()) {
                val startTime = TimeTool.showDateType(bean.dataList.first().createTime)
                val endTime = TimeTool.showDateType(bean.dataList.last().createTime)
                Log.w("123", "log start:${startTime}, end:$endTime")
            }
            resultLiveData.postValue(bean)
        }
    }

     * ()
     * :  =>
    suspend fun queryLogVolsByStartTime(
        type: Int = 3,
        selectTimeType: Int,
        endLogTime: Long = System.currentTimeMillis(),
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = SharedManager.getUserId()
                val typeStr = when (type) {
                    1 -> "point"
                    2 -> "line"
                    else -> "fence"
                }
                val bean = ChartList()
                val job = async { syncVol(selectTimeType) }
                job.await()
                syncRun = false//
                val startLogTime = when (selectTimeType) {
                     * 7200
                     * :2
                     * :5
                     * :300
                     * :20
//                1 -> startLogTime + 2 * 60 * 60 * 1000L //(2)
//                2 -> startLogTime + 24 * 60 * 60 * 1000L //(1)
//                3 -> startLogTime + 30 * 24 * 60 * 60 * 1000L //(30)
//                4 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //(1)

//                1 -> startLogTime + 7200 * 1000L //(2)
//                2 -> startLogTime + 7200 * 60 * 1000L //(5)
//                3 -> startLogTime + 7200 * 60 * 60 * 1000L //(300)
//                4 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //(1)
//                else -> startLogTime + 7200 * 1000L

                    1 -> endLogTime - 7200 * 1000L //(2)
                    2 -> endLogTime - 7200 * 60 * 1000L //(5)
                    3 -> endLogTime - 7200 * 60 * 60 * 1000L //(300)
                    4 -> endLogTime - 10 * 365 * 24 * 60 * 60 * 1000L //(10)
                    else -> endLogTime - 7200 * 1000L
                }
                when (selectTimeType) {
                    1 -> {
                        bean.dataList = AppDatabase.getInstance().thermalDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) as ArrayList<ThermalEntity>
                        bean.maxVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        bean.minVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        Log.w("chart", ":${bean.dataList.size}")
                    }
                    2 -> {
                        bean.dataList = AppDatabase.getInstance().thermalDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) as ArrayList<ThermalEntity>
                        bean.maxVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        bean.minVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        Log.w("chart", ":${bean.dataList.size}")
                    }
                    3 -> {
                        bean.dataList = AppDatabase.getInstance().thermalDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) as ArrayList<ThermalEntity>
                        bean.maxVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        bean.minVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        Log.w("chart", ":${bean.dataList.size}")
                    }
                    4 -> {
                        bean.dataList = AppDatabase.getInstance().thermalDao()
                            .queryByTime(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) as ArrayList<ThermalEntity>
                        bean.maxVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMax(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        bean.minVol = AppDatabase.getInstance().thermalDao()
                            .queryByTimeMin(
                                userId = userId,
                                startTime = startLogTime,
                                endTime = endLogTime,
                                type = typeStr
                            ) ?: 0f
                        Log.w("chart", ":${bean.dataList.size}")
                    }
                }
                delay(500)
                resultLiveData.postValue(bean)
            } catch (e: Exception) {
                XLog.e(":${e.message}")
                resultLiveData.postValue(ChartList())
            }
        }
    }


     * @param type 1: 2: 3: 4:
    private fun getNewVolData(data: List<ThermalEntity>, type: Int = 2): ArrayList<ThermalEntity> {
        val newData: ArrayList<ThermalEntity> = arrayListOf()
        var startIndex = 0
        var endIndex = 0
        for (i in data.indices) {
            if (i == 0) {
                if (i == data.size - 1) {
                    addData(data, newData, 0, endIndex)
                }
            } else {
                //[1]..[size-1]
                val currencyTime = TimeTool.showDateType(data[i].createTime, type)
                val previewTime = TimeTool.showDateType(data[i - 1].createTime, type)
                if (i == data.size - 1) {
                    if (currencyTime != previewTime) {
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        startIndex = i
                        endIndex = i
                        addData(data, newData, startIndex, endIndex)
                    } else {
                        endIndex = i
                        if (newData.size == 0) {
                            addData(data, newData, 0, endIndex)
                        } else {
                            addData(data, newData, startIndex, endIndex)
                        }
                    }
                } else {
                    if (currencyTime != previewTime) {
                        endIndex = i - 1
                        addData(data, newData, startIndex, endIndex)
                        startIndex = i
                    }
                }
            }
        }
        return newData
    }

    private fun addData(
        data: List<ThermalEntity>,
        newData: ArrayList<ThermalEntity>,
        startIndex: Int,
        endIndex: Int
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
        //tempVol:0f    startIndex:2    endIndex:1 vol:NaN
        tempVolEntity.thermal = temp / (endIndex - startIndex + 1)//
        tempVolEntity.thermalMax = tempMax / (endIndex - startIndex + 1)//
        tempVolEntity.thermalMin = tempMin / (endIndex - startIndex + 1)//
        newData.add(tempVolEntity)
    }

    @Volatile
    private var syncRun = false

     * : 1609430400000 (2021-1-1 00:00:00)
     * 1.
     * 2. [ ~ ]
     * 3.
     * 4.
     * 5.
    private suspend fun syncVol(selectTimeType: Int) {
        Log.i("chart", "syncVol: $syncRun")
        if (syncRun) {
            return
        }
        Log.i("chart", "syncVol start")
        syncRun = true
        
        // Simplified sync logic - just ensure data is clean
        val userId = SharedManager.getUserId()
        AppDatabase.getInstance().thermalDao().deleteZero(userId)
        
        syncRun = false
        Log.w("chart", "syncVol end")
    }

    data class ChartList(
        var dataList: ArrayList<ThermalEntity> = arrayListOf(),
        var maxVol: Float = 0f,
        var minVol: Float = 0f,
        var action: Int = 0
    )
}