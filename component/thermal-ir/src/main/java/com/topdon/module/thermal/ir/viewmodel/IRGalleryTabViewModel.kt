package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import com.topdon.lib.core.ktbase.BaseViewModel

class IRGalleryTabViewModel : BaseViewModel() {
    /**
     * [Chinese text]mode.
     */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)
    /**
     * [Chinese text]in progress[Chinese text].
     */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)

    /**
     * point[Chinese text] Fragment index, [Chinese text] 0 [Chinese text], 1[Chinese text].
     */
    val selectAllIndex: MutableLiveData<Int> = MutableLiveData(0)
}