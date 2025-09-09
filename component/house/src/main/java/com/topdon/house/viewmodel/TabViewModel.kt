package com.topdon.house.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * [CN_TEXT] ViewModel.
 *
 * Created by LCG on 2024/8/22.
 */
internal class TabViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * [CN_TEXT]Mode.
     */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)
    /**
     * CurrentSelected[CN_TEXT].
     */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)
}