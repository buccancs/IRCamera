package com.topdon.lib.core.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hjq.permissions.XXPermissions

/**
 * WIFI [Chinese text].
 */
object WifiUtil {
    /**
     * [Chinese text] SSID.
     */
    fun ScanResult.getWifiName(): String = if (Build.VERSION.SDK_INT < 33) SSID else removeQuotation(wifiSsid.toString())

    fun WifiInfo.getWifiName(): String = removeQuotation(ssid)

    /**
     * [Chinese text], [Chinese text]
     */
    private fun removeQuotation(source: String): String {
        return if (source.length > 1 && source[0] == '\"' && source[source.length - 1] == '\"') {
            source.subSequence(1, source.length - 1).toString()
        } else {
            source
        }
    }

    /**
     * [Chinese text] Wifi ssid, [Chinese text], [Chinese text]. 
     * @return [Chinese text] WIFI [Chinese text] [Chinese text] [Manifest.permission.ACCESS_FINE_LOCATION] [Chinese text], [Chinese text] null
     */
    fun getCurrentWifiSSID(context: Context): String? {
        if (!XXPermissions.isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            return null
        }
        val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo?.getWifiName()
    }

    /**
     * [Chinese text] activity [Chinese text] WIFI [Chinese text]listener.
     */
    fun addWifiStateListener(
        activity: ComponentActivity,
        listener: ((isEnable: Boolean) -> Unit),
    ) {
        activity.lifecycle.addObserver(WifiStateObserver(activity, WifiStateReceiver(listener)))
    }

    /**
     * [Chinese text] activity [Chinese text] WIFI [Chinese text]listener.
     */
    fun addWifiScanListener(
        activity: ComponentActivity,
        listener: ((isSuccess: Boolean) -> Unit),
    ) {
        activity.lifecycle.addObserver(WifiScanObserver(activity, WifiScanReceiver(listener)))
    }

    private class WifiStateObserver(val context: Context, val receiver: BroadcastReceiver) : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            context.registerReceiver(receiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
        }

        override fun onDestroy(owner: LifecycleOwner) {
            context.unregisterReceiver(receiver)
            owner.lifecycle.removeObserver(this)
        }
    }

    private class WifiScanObserver(val context: Context, val receiver: BroadcastReceiver) : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        }

        override fun onDestroy(owner: LifecycleOwner) {
            context.unregisterReceiver(receiver)
            owner.lifecycle.removeObserver(this)
        }
    }

    /**
     * WIFI [Chinese text]listener.
     */
    private class WifiStateReceiver(val listener: ((isEnable: Boolean) -> Unit)) : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?,
        ) {
            when (intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                WifiManager.WIFI_STATE_ENABLED -> listener.invoke(true)
                WifiManager.WIFI_STATE_DISABLED, WifiManager.WIFI_STATE_UNKNOWN -> listener.invoke(false)
            }
        }
    }

    /**
     * WIFI [Chinese text]listener.
     */
    private class WifiScanReceiver(val listener: ((isSuccess: Boolean) -> Unit)) : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent?,
        ) {
            listener.invoke(intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false)
        }
    }
}
