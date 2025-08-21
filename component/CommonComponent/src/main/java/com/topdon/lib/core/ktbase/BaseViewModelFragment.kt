package com.topdon.lib.core.ktbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

/**
 * Base fragment with ViewModel support for component modules
 */
abstract class BaseViewModelFragment<VM : ViewModel> : BaseFragment() {

    protected lateinit var viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = provideViewModel()
        initViewModel()
    }

    /**
     * Provide the ViewModel instance - to be implemented by subclasses
     */
    abstract fun provideViewModel(): VM

    /**
     * Initialize ViewModel observers and data
     */
    open fun initViewModel() {
        // Override in subclasses
    }
}