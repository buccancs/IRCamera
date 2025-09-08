package com.topdon.lib.core.socket

import android.net.Network
import android.Manifest
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.os.postDelayed
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.XLog
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.bean.event.SocketStateEvent
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.utils.WifiUtil
import com.topdon.lib.core.utils.WifiUtil.getWifiName
import com.topdon.lib.core.utils.WsCmdConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import org.greenrobot.eventbus.EventBus

class WebSocketProxy {

    companion object {
        @JvmStatic
        private var mWebSocketProxy: WebSocketProxy? = null

        fun getInstance(): WebSocketProxy {
            if (mWebSocketProxy == null) {
                synchronized(WebSocketProxy::class) {
                    if (mWebSocketProxy == null) {
                        mWebSocketProxy = WebSocketProxy()
                    }
                }
            }
            return mWebSocketProxy!!
        }
    }


    private var currentSSID: String? = null
    private var mWsManager: WsManager? = null
    private var webSocketListener: MyWebSocketListener? = null
    private var reconnectHandler = ReconnectHandler()
    private var network : Network ?= null

    private fun getOKHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            //.pingInterval(3, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val originalRequest = chain.request()
                val builder: Request.Builder = originalRequest.newBuilder()
                val compressedRequest: Request = builder.build()
                XLog.tag("WebSocket").d("request:$compressedRequest")
                chain.proceed(compressedRequest)
            })
            .retryOnConnectionFailure(true)
        network?.socketFactory?.let {
            builder.socketFactory(it)
        }
        return builder.build()
    }

     * TC007 Socket
    private var onFrameListener: ((frame: SocketFrameBean) -> Unit)? = null
    fun setOnFrameListener(activity: ComponentActivity, listener: (frame: SocketFrameBean) -> Unit) {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                onFrameListener = listener
            }

            override fun onDestroy(owner: LifecycleOwner) {
                onFrameListener = null
            }
        })
    }

    var onMessageListener: ((text: String) -> Unit)? = null

    fun startWebSocket(ssid: String, network: Network? = null) {
        if (ssid == currentSSID) {
            if (mWsManager != null) {
                XLog.tag("WebSocket").w("$ssid startWebSocket() ")
                return
            }
            this.network = network
        } else {
            XLog.tag("WebSocket").d(" $currentSSID $ssid，")
            if (reconnectHandler.isReconnecting) {
                EventBus.getDefault().post(SocketStateEvent(false, false))
            }
            this.network = network
            currentSSID = ssid
            reconnectHandler.currentSSID = ssid
            stopWebSocket()
        }

        XLog.tag("WebSocket").d("$ssid startWebSocket()")

        // Only TC001 supported - no WebSocket connection needed
        Log.i("WebSocket", "WebSocket not needed for TC001")
    }

     *  Socket .
    fun stopWebSocket() {
        XLog.tag("WebSocket").d("stopWebSocket()")
        webSocketListener?.isNeedReconnect = false
        webSocketListener = null

        mWsManager?.stopConnect()
        mWsManager = null
    }

    fun isConnected(): Boolean = false // Only TC001 supported - no WebSocket needed

    fun isTS004Connect(): Boolean = false // TS004 not supported

    fun isTC007Connect(): Boolean = false // TC007 not supported

    fun sendMessage(cmd: String?) {
        mWsManager?.sendMessage(cmd)
    }


    private class MyWebSocketListener(
        val ssid: String,
        val handler: ReconnectHandler,
        val onMessageListener: ((text: String) -> Unit)?,
        val onFrameListener: (frame: SocketFrameBean) -> Unit
    ) : WsManager.IWebSocketListener() {

         * onFailure
         *  onFailure
         * onFailure
        var isNeedReconnect = true

        override fun onOpen(webSocket: WebSocket, response: Response) {
            XLog.tag("WebSocket").d("$ssid Socket ")
            isNeedReconnect = true
            handler.reset()
            EventBus.getDefault().post(SocketStateEvent(true, false))
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (SocketCmdUtil.getCmdResponse(text) == WsCmdConstants.APP_EVENT_HEART_BEATS) {
                Log.v("WebSocket", "<-- ${text.replace("\n", "").replace(" ", "")}")
            } else {
                XLog.tag("WebSocket").d("$ssid TEXT:$text")
            }
            onMessageListener?.invoke(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // Only TC001 supported - no frame processing needed
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            XLog.tag("WebSocket").d("$ssid ，：$reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (handler.isReconnecting) {
                XLog.tag("WebSocket").d("$ssid ，，：$reason")
            } else {
                XLog.tag("WebSocket").d("$ssid ，：$reason")
                handler.reset()
                EventBus.getDefault().post(SocketStateEvent(false, false))
            }
            mWebSocketProxy?.currentSSID = ""
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            XLog.tag("WebSocket").d("$ssid ，response: ${response?.message}")
            XLog.tag("WebSocket").d("$ssid ，: ${t.message}")
            if (checkNeedReconnect()) {
                handler.handleFail(ssid)
                if (!handler.isReconnecting) {
                    EventBus.getDefault().post(SocketStateEvent(false, false))
                }
            } else {
                XLog.tag("WebSocket").w("")
                handler.reset()
                getInstance().stopWebSocket()
                EventBus.getDefault().post(SocketStateEvent(false, false))
            }
            mWebSocketProxy?.currentSSID = ""
        }

        override fun onHeartBeat(): String? = SocketCmdUtil.getSocketCmd(WsCmdConstants.APP_EVENT_HEART_BEATS)

        override fun onHeartBeatTimeout() {
            XLog.tag("WebSocket").w("")
            handler.handleFail(ssid)
        }

        private fun checkNeedReconnect(): Boolean {
            if (!isNeedReconnect) {
                return false
            }
            if (!XXPermissions.isGranted(Utils.getApp(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                return true
            }
            val wifiName: String = WifiUtil.getCurrentWifiSSID(Utils.getApp()) ?: return true
            XLog.tag("WebSocket").i("， WIFI：$wifiName")
            return wifiName == ssid
        }
    }

    private class ReconnectHandler : Handler(Looper.getMainLooper()) {
        companion object {
             * .
            private const val MAX_RECONNECT_COUNT = 3
             * .
            private const val RECONNECT_MILLIS = 3000L
        }

        var currentSSID: String = ""
            set(value) {
                if (value != field) {
                    field = value
                    reset()
                }
            }

        var reconnectCount: Int = 0
        var isReconnecting: Boolean = false

        fun reset() {
            reconnectCount = 0
            isReconnecting = false
            removeCallbacksAndMessages(null)
        }

        fun handleFail(currentSSID: String) {
            if (this.currentSSID != currentSSID) {
                XLog.tag("WebSocket").w(" ${this.currentSSID} ， $currentSSID fail ")
                return
            }
            if (isReconnecting) {
                reconnectCount++
                if (reconnectCount < MAX_RECONNECT_COUNT) {
                    XLog.tag("WebSocket").w(" $reconnectCount ")

                    getInstance().stopWebSocket()
                    removeCallbacksAndMessages(null)
                    postDelayed(RECONNECT_MILLIS) {
                        getInstance().startWebSocket(currentSSID)
                    }
                } else {
                    XLog.tag("WebSocket").w("， ")
                    reconnectCount = 0
                    isReconnecting = false
                    removeCallbacksAndMessages(null)
                    getInstance().stopWebSocket()
                }
            } else {
                XLog.tag("WebSocket").d("，")
                reconnectCount = 0
                isReconnecting = true

                getInstance().stopWebSocket()
                removeCallbacksAndMessages(null)
                postDelayed(RECONNECT_MILLIS) {
                    getInstance().startWebSocket(currentSSID)
                }
            }
        }
    }
}