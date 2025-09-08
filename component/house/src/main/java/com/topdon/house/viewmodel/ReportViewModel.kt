package com.topdon.house.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.HouseReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * [Chinese text] ViewModel.
 *
 * Created by LCG on 2024/8/28.
 */
class ReportViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * [Chinese text], [Chinese text] [queryAll] [Chinese text].
     */
    val reportListLD =  MutableLiveData<List<HouseReport>>()
    /**
     * [Chinese text], [Chinese text] [reportListLD] [Chinese text].
     */
    fun queryAll() {
        viewModelScope.launch(Dispatchers.IO) {
            reportListLD.postValue(AppDatabase.getInstance().houseReportDao().queryAllReport())
        }
    }

    /**
     * [Chinese text], [Chinese text] [queryById] [Chinese text].
     */
    val reportLD = MutableLiveData<HouseReport?>()
    /**
     * [Chinese text] id [Chinese text], [Chinese text] [reportLD] [Chinese text].
     */
    fun queryById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            reportLD.postValue(AppDatabase.getInstance().houseReportDao().queryById(id))
        }
    }

    /**
     * [Chinese text].
     */
    fun update(houseReport: HouseReport) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseReportDao().updateReport(houseReport)
        }
    }

    /**
     * [Chinese text].
     */
    fun deleteMore(vararg houseReports: HouseReport) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseReportDao().deleteReport(*houseReports)
            for (houseReport in houseReports) {
                FileUtils.delete(File(FileConfig.documentsDir, houseReport.getPdfFileName()))
            }
            queryAll()
        }
    }
}