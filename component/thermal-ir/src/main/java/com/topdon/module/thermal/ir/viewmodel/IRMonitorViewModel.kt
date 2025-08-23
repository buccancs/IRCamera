package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topdon.lib.core.ktbase.BaseViewModel
import com.topdon.lib.core.db.entity.ThermalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IRMonitorViewModel : BaseViewModel() {

    // Stub record data class for compatibility
    data class Record(
        val thermalId: String = "",
        val createTime: Long = 0L,
        val startTime: Long = 0L
    )

    val recordListLD = MutableLiveData<List<Record>>()

    fun queryRecordList() {
        viewModelScope.launch(Dispatchers.IO) {
            // Stub implementation - return empty list
            recordListLD.postValue(emptyList())
        }
    }

    val detailListLD = MutableLiveData<List<ThermalEntity>>()

    fun queryDetail(startTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Stub implementation - return empty list
            detailListLD.postValue(emptyList())
        }
    }

    fun delDetail(startTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Stub implementation - no operation
        }
    }
}