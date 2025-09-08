package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import com.topdon.lib.core.ktbase.BaseViewModel

class IRGalleryTabViewModel : BaseViewModel() {
     * .
    /** isEditModeLD property */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)
     * .
    /** selectSizeLD property */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)

     *  Fragment index 0 1.
    /** selectAllIndex property */
    val selectAllIndex: MutableLiveData<Int> = MutableLiveData(0)
}