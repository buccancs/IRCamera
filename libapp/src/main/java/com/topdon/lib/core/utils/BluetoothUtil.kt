package com.topdon.lib.core.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.elvishew.xlog.XLog
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.tools.PermissionTool

object BluetoothUtil {
    /**
     * [CN_TEXT] activity [CN_TEXT] [CN_TEXT] [CN_TEXT]State[CN_TEXT].
     */
    fun addBtStateListener(
        activity: ComponentActivity,
        listener: ((isEnable: Boolean) -> Unit),
    ) {
        activity.lifecycle.addObserver(BtStateObserver(activity, listener))
    }

    private class BtStateObserver(val context: Context, val listener: ((isEnable: Boolean) -> Unit)) : DefaultLifecycleObserver {
        private val receiver = BtStateReceiver()

        override fun onCreate(owner: LifecycleOwner) {
            context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        }

        override fun onDestroy(owner: LifecycleOwner) {
            context.unregisterReceiver(receiver)
            owner.lifecycle.removeObserver(this)
        }

        private inner class BtStateReceiver : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                    BluetoothAdapter.STATE_OFF -> listener.invoke(false)
                    BluetoothAdapter.STATE_ON -> listener.invoke(true)
                }
            }
        }
    }

    private val scanCallback = MyScanCallback()

    /**
     * Settings[CN_TEXT].
     */
    fun setLeScanListener(
        isTS004: Boolean,
        listener: (name: String) -> Unit,
    ) {
        scanCallback.isTS004 = isTS004
        scanCallback.listener = listener
    }

    /**
     * [CN_TEXT]，[CN_TEXT].
     * @return true-[CN_TEXT] false-[CN_TEXT]
     */
    @SuppressLint("MissingPermission")
    fun startLeScan(context: Context): Boolean {
        XLog.i("startLeScan()")

        if (!PermissionTool.hasBtPermission(context)) {
            XLog.e("[CN_TEXT]-[CN_TEXT]!")
            return false
        }

        val btAdapter: BluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val btLeScanner: BluetoothLeScanner? = btAdapter.bluetoothLeScanner
        if (btLeScanner == null) {
            XLog.e("[CN_TEXT]-[CN_TEXT]")
            return false
        }

        val settings =
            ScanSettings.Builder()
                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

        btLeScanner.startScan(null, settings, scanCallback)
        return true
    }

    /**
     * [CN_TEXT]，[CN_TEXT].
     * @return true-[CN_TEXT] false-[CN_TEXT]
     */
    @SuppressLint("MissingPermission")
    fun stopLeScan(context: Context): Boolean {
        XLog.i("stopBtScan()")

        if (!PermissionTool.hasBtPermission(context)) {
            XLog.w("[CN_TEXT]-[CN_TEXT]!")
            return false
        }

        val btAdapter: BluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val btLeScanner: BluetoothLeScanner? = btAdapter.bluetoothLeScanner
        if (btLeScanner == null) {
            XLog.w("[CN_TEXT]-[CN_TEXT]")
            return false
        }

        btLeScanner.stopScan(scanCallback)
        return true
    }

    private class MyScanCallback : ScanCallback() {
        var isTS004: Boolean = false
        var listener: ((name: String) -> Unit)? = null

        @SuppressLint("MissingPermission")
        override fun onScanResult(
            callbackType: Int,
            result: ScanResult?,
        ) {
            val name: String = result?.device?.name ?: return
            if (name.startsWith(if (isTS004) DeviceConfig.TS004_NAME_START else DeviceConfig.TC007_NAME_START)) {
                XLog.v("[CN_TEXT]：$name")
                listener?.invoke(name)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            XLog.e("[CN_TEXT]！$errorCode")
        }
    }
}
