package com.topdon.module.thermal.ir.viewmodel

import androidx.lifecycle.MutableLiveData
import com.topdon.lib.core.ktbase.BaseViewModel

/**
 * Custom I r gallery tab view model view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class IRGalleryTabViewModel : BaseViewModel() {
    /**
\1.
     */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
\1.
     */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)

    /**
\1 Fragment index， 0 ，1.
     */
    val selectAllIndex: MutableLiveData<Int> = MutableLiveData(0)
}
