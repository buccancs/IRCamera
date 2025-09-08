package com.topdon.house.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * [Chinese text] ViewModel.
 *
 * Created by LCG on 2024/8/22.
 */
internal class TabViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * [Chinese text]mode.
     */
    val isEditModeLD: MutableLiveData<Boolean> = MutableLiveData(false)
    /**
     * [Chinese text]in progress[Chinese text].
     */
    val selectSizeLD: MutableLiveData<Int> = MutableLiveData(0)
}