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
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : BaseApplication() {
companion object {
        lateinit var instance: App

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
                    XLog.e("bcf", "ZohoSalesIQdata")
                }

                override fun onInitError(errorCode: Int, errorMessage: String?) {
                    //your code
                    XLog.e("bcf", "ZohoSalesIQdata")
                }
            })
         */
    }
}
