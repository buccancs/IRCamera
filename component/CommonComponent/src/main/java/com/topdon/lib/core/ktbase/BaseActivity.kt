package com.topdon.lib.core.ktbase

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Simplified BaseActivity for component modules
 * Removed complex dependencies and kept only essential functionality
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layoutId = initContentView()
        if (layoutId != 0) {
            setContentView(layoutId)
        }
        
        initView()
        initData()
    }

    /**
     * Return the layout resource ID for this activity
     */
    abstract fun initContentView(): Int

    /**
     * Initialize views after setContentView
     */
    abstract fun initView()

    /**
     * Initialize data and setup listeners
     */
    abstract fun initData()

    /**
     * Set title text for toolbar/actionbar
     */
    fun setTitleText(title: String) {
        supportActionBar?.title = title
    }

    fun setTitleText(@StringRes titleRes: Int) {
        supportActionBar?.setTitle(titleRes)
    }

    /**
     * Simple loading dialog functionality
     */
    private var isLoading = false
    
    fun showLoadingDialog(message: String = "Loading...") {
        // Simplified implementation - subclasses can override for custom dialogs
        isLoading = true
    }
    
    fun dismissLoadingDialog() {
        isLoading = false
    }
}