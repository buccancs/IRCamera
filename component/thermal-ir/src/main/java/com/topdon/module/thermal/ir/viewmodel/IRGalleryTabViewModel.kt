package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import com.topdon.lib.core.ktbase.BaseViewModel

class IRGalleryTabViewModel : BaseViewModel() {
    /**
     * viewMode.
     */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)
    /**
     * CurrentSelectedview.
     */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)

    /**
     * view Fragment index，view 0 view，1view.
     */
    val selectAllIndex: MutableLiveData<Int> = MutableLiveData(0)
}