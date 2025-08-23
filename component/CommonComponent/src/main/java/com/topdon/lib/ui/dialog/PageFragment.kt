package com.topdon.lib.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Base fragment for paged dialogs and content
 * Provides pagination support for thermal UI components
 */
abstract class PageFragment : Fragment() {
    
    protected var currentPage: Int = 0
    protected var totalPages: Int = 0
    
    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?, 
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResource(), container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        loadData()
    }
    
    /**
     * Get layout resource ID for this page
     */
    abstract fun getLayoutResource(): Int
    
    /**
     * Initialize view components
     */
    abstract fun initView(view: View)
    
    /**
     * Load data for this page
     */
    open fun loadData() {
        // Override in subclasses if needed
    }
    
    /**
     * Set page information
     */
    fun setPageInfo(currentPage: Int, totalPages: Int) {
        this.currentPage = currentPage
        this.totalPages = totalPages
        onPageInfoChanged()
    }
    
    /**
     * Called when page info changes
     */
    open fun onPageInfoChanged() {
        // Override in subclasses if needed
    }
    
    /**
     * Go to next page if available
     */
    fun nextPage(): Boolean {
        return if (currentPage < totalPages - 1) {
            currentPage++
            onPageChanged()
            true
        } else {
            false
        }
    }
    
    /**
     * Go to previous page if available
     */
    fun previousPage(): Boolean {
        return if (currentPage > 0) {
            currentPage--
            onPageChanged()
            true
        } else {
            false
        }
    }
    
    /**
     * Called when page changes
     */
    open fun onPageChanged() {
        // Override in subclasses if needed
        loadData()
    }
}