package com.topdon.lib.core

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LanguageUtils
import com.csl.irCamera.libapp.BuildConfig
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.SocketMsgEvent
import com.topdon.lib.core.broadcast.DeviceBroadcastReceiver
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.socket.SocketCmdUtil
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lib.core.utils.WifiUtil
import com.topdon.lib.core.utils.WsCmdConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.File

abstract class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
        val usbObserver by lazy { DeviceBroadcastReceiver() }
    }
    var tau_data_H: ByteArray? = null
    var tau_data_L: ByteArray? = null

    var activitys = arrayListOf<Activity>()
    var hasOtgShow = false//otg

     * .
    abstract fun getSoftWareCode(): String

     *  2024/8/27 “”
     * @return true- false
    abstract fun isDomestic(): Boolean


    override fun onCreate() {
        super.onCreate()
        instance = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            webviewSetPath(this)
        }
        initARouter()
        onLanguageChange()

        WebSocketProxy.getInstance().onMessageListener = {
            parserSocketMessage(it)
        }

    }

    open fun initWebSocket(){
        connectWebSocket()
        if (Build.VERSION.SDK_INT < 33) {
            registerReceiver(NetworkChangedReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        } else {
            registerReceiver(NetworkChangedReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), Context.RECEIVER_NOT_EXPORTED)
        }
    }

    private fun connectWebSocket() {
        val ssid = WifiUtil.getCurrentWifiSSID(this) ?: return
        Log.i("WebSocket", " Wifi SSID: $ssid")
        // TC001 uses USB connection, but preserve WiFi capability for other uses
        NetWorkUtils.switchNetwork(true)
    }

    fun disconnectWebSocket() {
        Log.i("WebSocket", "disconnectWebSocket()")
        WebSocketProxy.getInstance().stopWebSocket()
    }

     * socket
     * @param msgJson
    private fun parserSocketMessage(msgJson: String) {
        if (TextUtils.isEmpty(msgJson)) return
        EventBus.getDefault().post(SocketMsgEvent(msgJson))

    }



    private inner class NetworkChangedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
                val manager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo = manager.activeNetworkInfo ?: return
                if (activeNetwork.isConnected && activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    connectWebSocket()
                }else{
//                    NetWorkUtils.
                }
                Log.i("WebSocket", " Wifi SSID: $activeNetwork"+activeNetwork.type)
            }
        }
    }



     * webviewandroid9
    @RequiresApi(api = 28)
    open fun webviewSetPath(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName(context)
            if (!applicationContext.packageName.equals(processName)) { //
                WebView.setDataDirectorySuffix(processName!!)
            }
        }
    }

    open fun getProcessName(context: Context?): String? {
        if (context == null) return null
        val manager: ActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName
            }
        }
        return null
    }

    private fun initARouter() {
        try {
            if (BuildConfig.DEBUG) {
                Log.e("TopInfrared_LOG", "router init debug")
                ARouter.openDebug()
            }
            ARouter.init(this)
        } catch (e: Exception) {
            // ( )
            if (SharedManager.getHasShowClause()) {
                Log.e("TopInfrared_LOG", "router init error: ${e.message}")
            }
            ARouter.openDebug()
            ARouter.init(this)
        }
    }

    fun clearDb() {
        GlobalScope.launch(Dispatchers.Default) {
            try {
                AppDatabase.getInstance().thermalDao().deleteZero(SharedManager.getUserId())
            } catch (e: Exception) {
                XLog.e("delete db error: ${e.message}")
            }
        }
    }

    open fun onLanguageChange() {
        val selectLan = SharedManager.getLanguage(baseContext)
        if (TextUtils.isEmpty(selectLan)) {
            if (isDomestic()) {
                val autoSelect = AppLanguageUtils.getChineseSystemLanguage()
                val locale = AppLanguageUtils.getLocaleByLanguage(autoSelect)
                LanguageUtils.applyLanguage(locale)
                SharedManager.setLanguage(baseContext, autoSelect)
            } else {
                val autoSelect = AppLanguageUtils.getSystemLanguage()
                val locale = AppLanguageUtils.getLocaleByLanguage(autoSelect)
                LanguageUtils.applyLanguage(locale)
                SharedManager.setLanguage(baseContext, autoSelect)
            }
        } else {
            val locale = AppLanguageUtils.getLocaleByLanguage(SharedManager.getLanguage(this))
            LanguageUtils.applyLanguage(locale)
        }
        WebView(this).destroy()
    }

    open fun getAppLanguage(context: Context): String? {
        return SharedManager.getLanguage(context)
    }

    fun exitAll() {
        hasOtgShow = false
        activitys.forEach {
            it.finish()
        }
    }

}
