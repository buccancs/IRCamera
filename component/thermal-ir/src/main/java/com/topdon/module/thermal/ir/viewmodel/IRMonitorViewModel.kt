package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.dao.ThermalDao
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.ktbase.BaseViewModel
import kotlinx.coroutines.*

class IRMonitorViewModel : BaseViewModel() {

    /** recordListLD property */
    val recordListLD = MutableLiveData<List<ThermalDao.Record>>()

    /**
     * Function description.
     */
    fun queryRecordList() {
        viewModelScope.launch(Dispatchers.IO) {
            val recordList: List<ThermalDao.Record> = AppDatabase.getInstance().thermalDao().queryRecordList()
            recordListLD.postValue(recordList)
        }
    }





    /** detailListLD property */
    val detailListLD = MutableLiveData<List<ThermalEntity>>()

    /**
     * Function description.
     */
    fun queryDetail(startTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val detailList: List<ThermalEntity> = AppDatabase.getInstance().thermalDao().queryDetail(startTime)
            detailListLD.postValue(detailList)
        }
    }

    /**
     * Function description.
     */
    fun delDetail(startTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().thermalDao().delDetail(startTime)
        }
    }
}