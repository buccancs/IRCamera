package com.topdon.lib.core

import android.app.Application
import android.app.Activity

/**
 * Base Application class for thermal component compatibility
 * Simplified version with essential properties only
 */
abstract class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
    }
    
    // TAU data for thermal calculations
    var tau_data_H: ByteArray? = null
    var tau_data_L: ByteArray? = null

    // Activity management
    var activitys = arrayListOf<Activity>()
    var hasOtgShow = false

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    abstract fun getSoftWareCode(): String
}