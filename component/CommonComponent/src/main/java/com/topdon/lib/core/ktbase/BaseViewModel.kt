package com.topdon.lib.core.ktbase

import androidx.lifecycle.ViewModel

/**
 * Simplified BaseViewModel for component modules
 */
open class BaseViewModel : ViewModel() {
    
    override fun onCleared() {
        super.onCleared()
    }
}