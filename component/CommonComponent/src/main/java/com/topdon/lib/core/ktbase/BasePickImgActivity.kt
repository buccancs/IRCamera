package com.topdon.lib.core.ktbase

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Base activity for image picking functionality
 */
abstract class BasePickImgActivity : AppCompatActivity() {
    
    protected var savedInstanceState: Bundle? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        initView()
        initData()
    }
    
    /**
     * Initialize the view components
     */
    abstract fun initView()
    
    /**
     * Initialize data and setup logic
     */
    abstract fun initData()
    
    /**
     * Get bitmap from image picking operation
     */
    abstract suspend fun getPickBitmap(): Bitmap?
}