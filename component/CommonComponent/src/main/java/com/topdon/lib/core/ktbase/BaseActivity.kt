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
    
    /**
     * Show toast message
     */
    fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    fun showToast(messageRes: Int) {
        android.widget.Toast.makeText(this, messageRes, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Camera-related loading methods expected by thermal-ir components
     */
    fun showCameraLoading() {
        showLoadingDialog("Camera loading...")
    }
    
    fun dismissCameraLoading() {
        dismissLoadingDialog()
    }
    
    /**
     * Connection status methods expected by thermal-ir components
     */
    open fun disConnected() {
        // Default implementation - can be overridden
    }
    
    /**
     * Temperature listener interface methods expected by thermal-ir components
     */
    open fun onTempChanged() {
        // Default implementation - can be overridden
    }
    
    open fun onTempRangeChanged() {
        // Default implementation - can be overridden
    }
    
    open fun onTempMeasureComplete() {
        // Default implementation - can be overridden
    }
    
    open fun onTempError() {
        // Default implementation - can be overridden
    }
    
    // Additional methods for thermal-ir component compatibility
    open fun showLoading() {
        // Default loading implementation
    }
    
    open fun hideLoading() {
        // Default hide loading implementation
    }
    
    open fun dismissLoading() {
        // Alias for hideLoading for compatibility
        hideLoading()
    }
    
    open fun queryLogsByTimeRange(startTime: Long, endTime: Long) {
        // Default query implementation - can be overridden
    }
    
    // Color properties for compatibility
    protected val blackColor: Int get() = android.graphics.Color.BLACK
    protected val white: Int get() = android.graphics.Color.WHITE
    
    // Toolbar support
    protected var mToolBar: androidx.appcompat.widget.Toolbar? = null
        set(value) {
            field = value
            setSupportActionBar(value)
        }
    
    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        supportActionBar?.title = title
    }
    
    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)
        supportActionBar?.setTitle(titleId)
    }
}