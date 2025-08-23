package com.topdon.lib.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import com.elvishew.xlog.XLog
import com.topdon.lib.core.BaseApplication

object NetWorkUtils {

    private var mNetworkCallback: ConnectivityManager.NetworkCallback ?= null
    private var netWorkListener : ((network: Network?) -> Unit) ?= null
    val connectivityManager by lazy {
        BaseApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private val wifiManager by lazy {
        BaseApplication.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun isWifiNameValid(context: Context, prefixes: List<String>): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ssid = wifiInfo.ssid.replace("\"", "") // 移除双引号
        for (prefix in prefixes) {
            if (ssid.startsWith(prefix)) {
                return true
            }
        }
        return false
    }

    fun connectWifi(ssid: String, password: String, listener: ((network: Network?) -> Unit)? = null) {
        netWorkListener = listener
        if (Build.VERSION.SDK_INT < 29) {//低于 Android10
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)//不需要能访问 internet
                .build()
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if (WifiUtil.getCurrentWifiSSID(BaseApplication.instance) == ssid) {
                        connectivityManager.unregisterNetworkCallback(this)
                        listener?.invoke(network)
                    }
                }

                override fun onUnavailable() {
                    connectivityManager.unregisterNetworkCallback(this)
                    listener?.invoke(null)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                }

                override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                    super.onBlockedStatusChanged(network, blocked)
                }

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    super.onLinkPropertiesChanged(network, linkProperties)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                }
            }
            connectivityManager.registerNetworkCallback(request, callback)

            val configuration = WifiConfiguration()
            configuration.SSID = "\"$ssid\""
            configuration.preSharedKey = "\"$password\""
            configuration.hiddenSSID = false
            configuration.status = WifiConfiguration.Status.ENABLED
            val id = wifiManager.addNetwork(configuration)
            val isSuccess = wifiManager.enableNetwork(id, true)
            if (!isSuccess) {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        } else {
            val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(wifiNetworkSpecifier)
                .build()
            if (mNetworkCallback == null){
                mNetworkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        netWorkListener?.invoke(network)
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        netWorkListener?.invoke(null)
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                    }
                }
            }
            connectivityManager.requestNetwork(request, mNetworkCallback!!)
        }
    }


    fun switchNetwork(isWifi: Boolean, listener: ((network: Network?) -> Unit)? = null) {
        if (Build.VERSION.SDK_INT < 29) {//低于 Android10
            return
        }
        if (isWifi){
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.boundNetworkForProcess)
            if (networkCapabilities != null &&
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return
            }
        }
        val request: NetworkRequest = NetworkRequest.Builder()
            .addTransportType(if (isWifi) NetworkCapabilities.TRANSPORT_WIFI else NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (isWifi) {
                    // TS004Repository.netWork = network // TS004 support removed
                }
                connectivityManager.bindProcessToNetwork(network)
                connectivityManager.unregisterNetworkCallback(this)
                listener?.invoke(network)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                connectivityManager.unregisterNetworkCallback(this)
                listener?.invoke(null)
            }
        })
    }
}