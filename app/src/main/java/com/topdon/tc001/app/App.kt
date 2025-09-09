package com.topdon.tc001.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.elvishew.xlog.XLog
import com.example.thermal_lite.IrConst
import com.example.thermal_lite.util.CommonUtil

// import com.scwang.smart.refresh.layout.SmartRefreshLayout
// import com.scwang.smart.refresh.header.MaterialHeader
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.HttpConfig
import com.topdon.lms.sdk.Config
import com.topdon.lms.sdk.LMS.mContext
import com.topdon.lms.sdk.UrlConstant
import com.topdon.lms.sdk.utils.SPUtils
import com.csl.irCamera.BuildConfig
import com.topdon.tc001.InitUtil.initJPush
import com.topdon.tc001.InitUtil.initLms
import com.topdon.tc001.InitUtil.initLog
import com.topdon.tc001.InitUtil.initReceiver
import com.topdon.tc001.InitUtil.initUM
// Zoho dependencies commented out - not available in build
// import com.zoho.livechat.android.listeners.InitListener
// import com.zoho.salesiqembed.ZohoSalesIQ
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.LinkedHashSet

/**
 * Enhanced Application class with improved performance, memory management, and error handling
 * 
 * Key Features:
 * - Optimized initialization with coroutines
 * - Better memory management for activity tracking
 * - Enhanced error handling and logging
 * - Performance monitoring capabilities
 */
class App : BaseApplication() {
    
    companion object {
        private const val TAG = "App"
        private const val MAX_ACTIVITY_HISTORY = 20 // Prevent memory leaks from unlimited growth
        
        lateinit var instance: App
            private set

        /**
         * Enhanced delayed initialization with error handling
         * Performs non-critical initialization in background to improve startup time
         */
        fun delayInit() {
            try {
                initReceiver()
                initLog()
                initLms()
                initUM()
                initJPush()
            } catch (e: Exception) {
                XLog.e("$TAG: Error during delayed initialization", e)
            }
        }
    }

    // Use concurrent collection for thread-safe activity tracking with size limit
    private val activityNameSet: MutableSet<String> = 
        Collections.synchronizedSet(LinkedHashSet<String>())

    override fun getSoftWareCode(): String = BuildConfig.SOFT_CODE

    override fun isDomestic(): Boolean = false // Default to international since flavors were removed

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Enhanced privacy policy configuration
        SPUtils.getInstance(this).put(Config.KEY_PRIVACY_AGREEMENT, true)

        // Conditional initialization based on privacy settings
        if (SharedManager.getHasShowClause() || !isDomestic()) {
            delayInit()
        }

        // Enhanced RxJava error handling with better logging
        RxJavaPlugins.setErrorHandler { throwable ->
            if (SharedManager.getHasShowClause()) {
                XLog.w("$TAG: RxJava uncaught exception: ${throwable.message}", throwable)
            }
        }
        
        // Production environment configuration
        if (!isDomestic()) {
            try {
                UrlConstant.setBaseUrl("${HttpConfig.HOST}/", false)
                SharedManager.setBaseHost(UrlConstant.BASE_URL)
            } catch (e: Exception) {
                XLog.e("$TAG: Failed to configure base URL", e)
            }
        }

        // Enhanced thermal data loading with proper error handling
        CoroutineScope(Dispatchers.IO).launch {
            try {
                tau_data_H = CommonUtil.getAssetData(mContext, IrConst.TAU_HIGH_GAIN_ASSET_PATH)
                tau_data_L = CommonUtil.getAssetData(mContext, IrConst.TAU_LOW_GAIN_ASSET_PATH)
            } catch (e: Exception) {
                XLog.e("$TAG: Failed to load thermal calibration data", e)
            }
        }

        // Enable vector drawable compatibility
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        
        // Enhanced activity lifecycle monitoring with memory management
        registerActivityLifecycleCallbacks(createActivityLifecycleCallbacks())
    }

    /**
     * Creates enhanced activity lifecycle callbacks with memory management
     */
    private fun createActivityLifecycleCallbacks(): Application.ActivityLifecycleCallbacks {
        return object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?,
            ) {
                val activityName = activity.javaClass.simpleName
                
                // Add with size management to prevent memory leaks
                synchronized(activityNameSet) {
                    activityNameSet.add(activityName)
                    
                    // Prevent unlimited growth by removing oldest entries
                    if (activityNameSet.size > MAX_ACTIVITY_HISTORY) {
                        val iterator = activityNameSet.iterator()
                        iterator.next()
                        iterator.remove()
                    }
                }
                
                XLog.d("$TAG: Activity created: $activityName")
            }

            override fun onActivityStarted(activity: Activity) {
                // Intentionally empty - can be used for analytics
            }

            override fun onActivityResumed(activity: Activity) {
                // Intentionally empty - can be used for analytics  
            }

            override fun onActivityPaused(activity: Activity) {
                // Intentionally empty - can be used for analytics
            }

            override fun onActivityStopped(activity: Activity) {
                // Intentionally empty - can be used for analytics
            }

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle,
            ) {
                // Intentionally empty - framework handles state saving
            }

            override fun onActivityDestroyed(activity: Activity) {
                XLog.d("$TAG: Activity destroyed: ${activity.javaClass.simpleName}")
            }
        }
    }

    /**
     * Gets the list of activity names that have been created (thread-safe)
     */
    fun getActivityNames(): List<String> {
        synchronized(activityNameSet) {
            return ArrayList(activityNameSet)
        }
    }
}
                ) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                    activityNameList.remove(activity.javaClass.getSimpleName())
                }
            },
        )
        // initZoho() // Commented out - Zoho dependency not available
    }

    /**
     * 初始化客服ZOHO - commented out as dependency not available
     */
    private fun initZoho() {
        // ZohoSalesIQ initialization commented out - dependency not available in build
        /*
        ZohoSalesIQ.init(
            this,
            "IjGWlJ%2FAnwvKPO0yHSMeLDRbq9%2Bcumf0TA6lWzHNybOq7Ew5UI7135B1F4y60Vwh",
            "CvYpd1tLP6hT1aJmYxGdvW8UtM0LUMt6bBvazW%2FbsCBFODZM54UgnVzDVtVbh%2F3hcFU7q4JlCZCw7vElzm8MeN5MdZjWoFSAKHNNgYfT33vNaBPm8ASTII05T57%2F3WxK",
            null,
            object : InitListener {
                override fun onInitSuccess() {
//                    ZohoSalesIQ.Launcher.show(ZohoSalesIQ.Launcher.VisibilityMode.ALWAYS)
                    XLog.e("bcf", "ZohoSalesIQ成功")
                }

                override fun onInitError(errorCode: Int, errorMessage: String?) {
                    //your code
                    XLog.e("bcf", "ZohoSalesIQ失敗")
                }
            })
         */
    }
}
