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
 Log.w("123", "")
\1
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
 Log.w("123", "data size: ${dataList.size}")
 }
 2 -> {
\1
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
\1
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
\1
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
\1
\1data(End)
\1: => Start
 */
 suspend fun queryLogThermals(
 selectTimeType: Int,
 endLogTime: Long = System.currentTimeMillis(),
 action: Int,
 ) {
 viewModelScope.launch(Dispatchers.IO) {
 val userId = SharedManager.getUserId()
 val bean = ChartList()
\1data
 val job = async { syncVol(selectTimeType) }
 job.await()
 syncRun = false // End
 val startLogTime =
 when (selectTimeType) {
 /**
\17200data
\1:2
\1:5
\1:300
\1:20
 */
 1 -> endLogTime - 7200 * 1000L // (2)
 2 -> endLogTime - 7200 * 60 * 1000L // (5)
 3 -> endLogTime - 7200 * 60 * 60 * 1000L // (300)
 4 -> endLogTime - 1 * 365 * 24 * 60 * 60 * 1000L // (1)
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
 Log.w("chart", ":${bean.dataList.size}")
 Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
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
 Log.w("chart", ":${bean.dataList.size}")
 Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
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
 Log.w("chart", ":${bean.dataList.size}")
 Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
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
 Log.w("chart", ":${bean.dataList.size}")
 Log.w("chart", "max vol:${bean.maxVol},min vol:${bean.minVol}")
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
\1data(End)
\1: => End
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
\1data
 val job = async { syncVol(selectTimeType) }
 job.await()
 syncRun = false // End
 val startLogTime =
 when (selectTimeType) {
 /**
\17200data
\1:2
\1:5
\1:300
\1:20
 */
\11 -> startLogTime + 2 * 60 * 60 * 1000L //(2)
\12 -> startLogTime + 24 * 60 * 60 * 1000L //(1)
\13 -> startLogTime + 30 * 24 * 60 * 60 * 1000L //(30)
\14 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //(1)

\11 -> startLogTime + 7200 * 1000L //(2)
\12 -> startLogTime + 7200 * 60 * 1000L //(5)
\13 -> startLogTime + 7200 * 60 * 60 * 1000L //(300)
\14 -> startLogTime + 1 * 365 * 24 * 60 * 60 * 1000L //(1)
// else -> startLogTime + 7200 * 1000L

 1 -> endLogTime - 7200 * 1000L // (2)
 2 -> endLogTime - 7200 * 60 * 1000L // (5)
 3 -> endLogTime - 7200 * 60 * 60 * 1000L // (300)
 4 -> endLogTime - 10 * 365 * 24 * 60 * 60 * 1000L // (10)
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
 Log.w("chart", ":${bean.dataList.size}")
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
 Log.w("chart", ":${bean.dataList.size}")
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
 Log.w("chart", ":${bean.dataList.size}")
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
\1Default
 addData(data, newData, 0, endIndex)
 }
 } else {
 // [1]..[size-1]
 val currencyTime = TimeTool.showDateType(data[i].createTime, type)
 val previewTime = TimeTool.showDateType(data[i - 1].createTime, type)
 if (i == data.size - 1) {
\1Value
 if (currencyTime != previewTime) {
\1calculationCurrent
\1
 endIndex = i - 1
 addData(data, newData, startIndex, endIndex)
 startIndex = i
\1
 endIndex = i
 addData(data, newData, startIndex, endIndex)
 } else {
 endIndex = i
 if (newData.size == 0) {
\1Default
 addData(data, newData, 0, endIndex)
 } else {
\1
 addData(data, newData, startIndex, endIndex)
 }
 }
 } else {
 if (currencyTime != previewTime) {
\1calculation
 endIndex = i - 1
 addData(data, newData, startIndex, endIndex)
\1
 startIndex = i
 }
 }
 }
 }
 return newData
 }

\1calculationValue
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
\1tempVol:0f startIndex:2 endIndex:1 vol:NaN
 tempVolEntity.thermal = temp / (endIndex - startIndex + 1) // Value
 tempVolEntity.thermalMax = tempMax / (endIndex - startIndex + 1) // Value
 tempVolEntity.thermalMin = tempMin / (endIndex - startIndex + 1) // Value
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
\13. datadataValue
\14. data
\15. Deletedata
 */
 private suspend fun syncVol(selectTimeType: Int) {
 Log.i("chart", "syncVol: $syncRun")
 if (syncRun) {
\1
 return
 }
 Log.i("chart", "syncVol start")
 if (selectTimeType == 1) {
\1dataprocessing
 return
 }
 syncRun = true
 val userId = SharedManager.getUserId()
\1save
 when (selectTimeType) {
 2 -> {
 val minuteTime = TimeTool.timeToMinute(System.currentTimeMillis(), 2)
\1data
 val minuteVolLatestList =
 AppDatabase.getInstance().thermalMinDao()
 .queryByTime(
 userId = userId,
 startTime = minuteTime,
 endTime = System.currentTimeMillis(),
 )
 if (minuteVolLatestList.isNotEmpty()) {
 Log.w("chart", "，")
 return
 }
 val maxTime =
 AppDatabase.getInstance().thermalMinDao().queryMaxTime(userId = userId)
 Log.w("chart", "minute latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
\1getupdatedata
 val secondVolList =
 AppDatabase.getInstance().thermalDao()
 .queryByTime(
 userId = userId,
 startTime = maxTime,
// endTime = Long.MAX_VALUE
 endTime = minuteTime,
 ) as ArrayList<ThermalEntity>
 if (secondVolList.size > 0) {
 val startTime = TimeTool.showDateType(secondVolList.first().createTime)
 val endTime = TimeTool.showDateType(secondVolList.last().createTime)
 Log.w("chart", "${secondVolList.size}, start:$startTime, end:$endTime")
 } else {
 Log.w("chart", "")
 }
\1datadataValue
 val minVolList = getNewVolData(secondVolList, 2)
\1data
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
 XLog.e("insert error:${e.message}")
 }
\1Deletedata
 AppDatabase.getInstance().thermalMinDao()
 .deleteRepeatVol(userId)
 }
 3 -> {
 val hourTime = TimeTool.timeToMinute(System.currentTimeMillis(), 3)
\1data
 val hourVolLatestList =
 AppDatabase.getInstance().thermalHourDao()
 .queryByTime(
 userId = userId,
 startTime = hourTime,
 endTime = System.currentTimeMillis(),
 )
 if (hourVolLatestList.isNotEmpty()) {
 Log.w("chart", "，")
 return
 }
 val maxTime =
 AppDatabase.getInstance().thermalHourDao().queryMaxTime(userId = userId)
 Log.w("chart", "hour latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
\1getupdatedata
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
 Log.w("chart", "${secondVolList.size}, start:$startTime, end:$endTime")
 } else {
 Log.w("chart", "")
 }
\1datadataValue
 val hourVolList = getNewVolData(secondVolList, 3)
\1data
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
\1Deletedata
 AppDatabase.getInstance().thermalHourDao().deleteRepeatVol(userId)
 }
 4 -> {
 val todayStartTime =
 TimeTool.timeToMinute(System.currentTimeMillis(), 4) // 
\1data
 val todayVolLatestList =
 AppDatabase.getInstance().thermalDayDao()
 .queryByTime(
 userId = userId,
 startTime = todayStartTime,
 endTime = System.currentTimeMillis(),
 )
 if (todayVolLatestList.isNotEmpty()) {
\1，，update
 Log.w("chart", "，")
 return
 }
 val maxTime =
 AppDatabase.getInstance().thermalDayDao().queryMaxTime(userId = userId)
 Log.w("chart", "day latest time: $maxTime, ${TimeTool.showDateType(maxTime)}")
\1getupdatedata
 val secondVolList =
 AppDatabase.getInstance().thermalDao()
 .queryByTime(
 userId = userId,
 startTime = maxTime,
 endTime = todayStartTime,
 ) as ArrayList<ThermalEntity>
\1datadataValue
 if (secondVolList.size > 0) {
 val startTime = TimeTool.showDateType(secondVolList.first().createTime)
 val endTime = TimeTool.showDateType(secondVolList.last().createTime)
 Log.w("chart", "${secondVolList.size}, start:$startTime, end:$endTime")
 } else {
 Log.w("chart", "")
 }
 val dayVolList = getNewVolData(secondVolList, 4)
\1data
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
\1update
 }
\1ValueEnd，Value，Valuedata(0f)，updateTime
 val bean = ThermalDayEntity()
 bean.userId = userId
 bean.thermal = 0f
 bean.thermalMax = 0f
 bean.thermalMin = 0f
 bean.createTime = TimeTool.timeToMinute(System.currentTimeMillis(), 4) // 
 bean.updateTime = System.currentTimeMillis()
 AppDatabase.getInstance().thermalDayDao().insert(bean)
\1Deletedata
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
