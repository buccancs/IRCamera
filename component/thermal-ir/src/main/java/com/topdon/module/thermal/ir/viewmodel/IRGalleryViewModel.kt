package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topdon.lib.core.bean.GalleryBean
import com.topdon.lib.core.bean.GalleryTitle
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.ktbase.BaseViewModel
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.lib.core.tools.TimeTool
import com.topdon.module.thermal.ir.utils.WriteTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class IRGalleryViewModel : BaseViewModel() {
    companion object {
         *  1
        const val PAGE_COUNT = 20
    }

     * .
    /** sourceListLD property */
    val sourceListLD: MutableLiveData<ArrayList<GalleryBean>> = MutableLiveData()
     * .
    /** showListLD property */
    val showListLD: MutableLiveData<ArrayList<GalleryBean>> = MutableLiveData()

     * .
    /**
     * Function description.
     */
    fun queryAllReportImg(dirType: GalleryRepository.DirType) {
        viewModelScope.launch(Dispatchers.IO) {
            val sourceList: ArrayList<GalleryBean> = GalleryRepository.loadAllReportImg(dirType)
            sourceListLD.postValue(sourceList)

            // item
            val showList: ArrayList<GalleryBean> = ArrayList(sourceList.size)
            var beforeTime = 0L
            for (galleryBean in sourceList) {
                val currentTime = TimeTool.timeToMinute(galleryBean.timeMillis, 4)
                if (beforeTime != currentTime) {//
                    showList.add(GalleryTitle(galleryBean.timeMillis))
                    beforeTime = currentTime
                }
                showList.add(galleryBean)
            }
            showListLD.postValue(showList)
        }
    }

    /** hasLoadPage property */
    var hasLoadPage = 0
     * .
     * null
    /** pageListLD property */
    val pageListLD: MutableLiveData<ArrayList<GalleryBean>?> = MutableLiveData()

    /**
     * Function description.
     */
    fun queryGalleryByPage(isVideo: Boolean, dirType: GalleryRepository.DirType) {
        viewModelScope.launch(Dispatchers.IO) {
            val pageList: ArrayList<GalleryBean>? = GalleryRepository.loadByPage(isVideo, dirType, hasLoadPage + 1, PAGE_COUNT)
            pageListLD.postValue(pageList)

            if (pageList != null) {
                val sourceList = if (hasLoadPage == 0) ArrayList(pageList.size) else sourceListLD.value ?: ArrayList(pageList.size)
                val showList = if (hasLoadPage == 0) ArrayList(pageList.size) else showListLD.value ?: ArrayList(pageList.size)
                if (pageList.isNotEmpty()) {
                    hasLoadPage++
                }

                // item
                var beforeTime = if (sourceList.isEmpty()) 0 else TimeTool.timeToMinute(sourceList.last().timeMillis, 4)
                for (galleryBean in pageList) {
                    val currentTime = TimeTool.timeToMinute(galleryBean.timeMillis, 4)
                    if (beforeTime != currentTime) {//
                        showList.add(GalleryTitle(galleryBean.timeMillis))
                        beforeTime = currentTime
                    }
                    showList.add(galleryBean)
                }

                sourceList.addAll(pageList)
                sourceListLD.postValue(sourceList)
                showListLD.postValue(showList)
            }
        }
    }





     * .
    /** deleteResultLD property */
    val deleteResultLD: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Function description.
     */
    fun delete(deleteList: List<GalleryBean>, dirType: GalleryRepository.DirType, isDelLocal: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            // TC001 only - uses local USB connection, no remote device support
            deleteList.forEach {
                val file = File(it.path)
                if (file.exists()) {
                    WriteTools.delete(file)
                }
            }
            deleteResultLD.postValue(true)
        }
    }
}