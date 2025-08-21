package com.topdon.lib.core.ktbase

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Base Fragment class for component modules
 */
abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    /**
     * Initialize views after view creation
     */
    abstract fun initView()

    /**
     * Initialize data and setup listeners
     */
    abstract fun initData()

    /**
     * Show toast message
     */
    fun showToast(message: String) {
        activity?.let { 
            android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    fun showToast(messageRes: Int) {
        activity?.let { 
            android.widget.Toast.makeText(it, messageRes, android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Get context safely
     */
    fun getSafeContext(): Context? {
        return context
    }
}