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
 * WIFI [CN_TEXT].
 */
object WifiUtil {
    /**
     * [CN_TEXT] SSID.
     */
    fun ScanResult.getWifiName(): String = if (Build.VERSION.SDK_INT < 33) {
        @Suppress("DEPRECATION")
        SSID
    } else {
        removeQuotation(wifiSsid.toString())
    }

    fun WifiInfo.getWifiName(): String = removeQuotation(ssid)

    /**
     * [CN_TEXT]Specified[CN_TEXT]，[CN_TEXT]
     */
    private fun removeQuotation(source: String): String {
        return if (source.length > 1 && source[0] == '\"' && source[source.length - 1] == '\"') {
            source.subSequence(1, source.length - 1).toString()
        } else {
            source
        }
    }

    /**
     * [CN_TEXT]Current[CN_TEXT] Wifi ssid，[CN_TEXT]，[CN_TEXT]。
     * @return [CN_TEXT] WIFI [CN_TEXT] [CN_TEXT] [Manifest.permission.ACCESS_FINE_LOCATION] [CN_TEXT]，[CN_TEXT] null
     */
    fun getCurrentWifiSSID(context: Context): String? {
        if (!XXPermissions.isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            return null
        }
        val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        return wifiManager.connectionInfo?.getWifiName()
    }

    /**
     * [CN_TEXT] activity [CN_TEXT] WIFI [CN_TEXT]State[CN_TEXT].
     */
    fun addWifiStateListener(
        activity: ComponentActivity,
        listener: ((isEnable: Boolean) -> Unit),
    ) {
        activity.lifecycle.addObserver(WifiStateObserver(activity, WifiStateReceiver(listener)))
    }

    /**
     * [CN_TEXT] activity [CN_TEXT] WIFI [CN_TEXT].
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
     * WIFI State[CN_TEXT].
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
     * WIFI [CN_TEXT].
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
