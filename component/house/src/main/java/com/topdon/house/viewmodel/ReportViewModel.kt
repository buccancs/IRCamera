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
 * [CN_TEXT] ViewModel.
 *
 * Created by LCG on 2024/8/28.
 */
class ReportViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * All[CN_TEXT]，[CN_TEXT] [queryAll] [CN_TEXT].
     */
    val reportListLD =  MutableLiveData<List<HouseReport>>()
    /**
     * [CN_TEXT]All[CN_TEXT]，[CN_TEXT] [reportListLD] [CN_TEXT].
     */
    fun queryAll() {
        viewModelScope.launch(Dispatchers.IO) {
            reportListLD.postValue(AppDatabase.getInstance().houseReportDao().queryAllReport())
        }
    }


    /**
     * [CN_TEXT]，[CN_TEXT] [queryById] [CN_TEXT].
     */
    val reportLD = MutableLiveData<HouseReport?>()
    /**
     * [CN_TEXT]Specified id [CN_TEXT]，[CN_TEXT] [reportLD] [CN_TEXT].
     */
    fun queryById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            reportLD.postValue(AppDatabase.getInstance().houseReportDao().queryById(id))
        }
    }


    /**
     * [CN_TEXT]Specified[CN_TEXT].
     */
    fun update(houseReport: HouseReport) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().houseReportDao().updateReport(houseReport)
        }
    }


    /**
     * DeleteSpecified[CN_TEXT].
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