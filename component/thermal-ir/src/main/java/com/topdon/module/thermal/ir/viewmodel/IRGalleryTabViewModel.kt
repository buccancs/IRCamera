package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import com.topdon.lib.core.ktbase.BaseViewModel

class IRGalleryTabViewModel : BaseViewModel() {
    /**
     * [CN_TEXT]Mode.
     */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)
    /**
     * CurrentSelected[CN_TEXT].
     */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)

    /**
     * [CN_TEXT] Fragment index，[CN_TEXT] 0 [CN_TEXT]，1[CN_TEXT].
     */
    val selectAllIndex: MutableLiveData<Int> = MutableLiveData(0)
}