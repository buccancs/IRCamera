package com.topdon.lib.core.ktbase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Base Fragment class for component modules
 */
abstract class BaseFragment : Fragment() {

    protected var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = initContentView()
        if (layoutId != 0) {
            rootView = inflater.inflate(layoutId, container, false)
            return rootView
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    /**
     * Get layout resource ID
     */
    abstract fun initContentView(): Int

    /**
     * Initialize views after view creation
     */
    abstract fun initView()

    /**
     * Initialize data and setup listeners
     */
    abstract fun initData()

    /**
     * Find view by ID using the root view
     */
    fun <T : View> findViewById(id: Int): T? {
        return rootView?.findViewById(id)
    }

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