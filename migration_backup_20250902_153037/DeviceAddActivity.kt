package com.topdon.tc004.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.bean.event.SocketStateEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.repository.TC007Repository
import com.topdon.lib.core.repository.TS004Repository
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.PermissionTool
import com.topdon.lib.core.tools.SpanBuilder
import com.topdon.lib.core.utils.BluetoothUtil
import com.topdon.lib.core.utils.LocationUtil
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lib.core.utils.WifiUtil
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.tc004.R
import kotlinx.android.synthetic.main.activity_device_add.*
import kotlinx.android.synthetic.main.item_device_add.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

    private fun requestPermission(actionType: Int) {
        if (isRequesting) {
            return
        }
        isRequesting = true
        val hasLocationPermission = XXPermissions.isGranted(this, Permission.ACCESS_FINE_LOCATION)
        if (!PermissionTool.hasBtPermission(this)) {
            if (hasLocationPermission) {
                topTipHolder.state = TopTipHolder.State.BLUETOOTH_PERMISSION
            } else {
                topTipHolder.state = TopTipHolder.State.LOCATION_PERMISSION
            }
        }
        PermissionTool.requestBluetooth(
            this,
            actionType == 1,
            object : PermissionTool.Callback {
                override fun onResult(allGranted: Boolean) {
                    isRequesting = false
                    if (allGranted) {
                        if (topTipHolder.state == TopTipHolder.State.BLUETOOTH_PERMISSION) {
                            topTipHolder.state = TopTipHolder.State.NONE
                        }
                        if (topTipHolder.state == TopTipHolder.State.LOCATION_PERMISSION) {
                            topTipHolder.state = TopTipHolder.State.NONE
                        }
                        refreshStateAndTips()
                        if (actionType == 0) {
                            return
                        }
                        if (actionType == 1) {
                            if (!btAdapter.isEnabled) { // 蓝牙未开启
                                showOpenBtDialog()
                                return
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= 28 && !locationManager.isLocationEnabled) { // 位置信息未开启
                                showOpenLocationDialog()
                                return
                            }
                            if (!wifiManager.isWifiEnabled) { // WIFI 未开启
                                showOpenWifiDialog()
                                return
                            }
                            if (!btAdapter.isEnabled) { // 蓝牙未开启
                                showOpenBtDialog()
                                return
                            }
                        }
                        startBtScan()
                    } else {
                        TToast.shortToast(this@DeviceAddActivity, R.string.scan_ble_tip_authorize)
                    }
                }

                override fun onNever(isJump: Boolean) {
                    isRequesting = false
                    if (topTipHolder.state == TopTipHolder.State.BLUETOOTH_PERMISSION) {
                        topTipHolder.state = TopTipHolder.State.NONE
                    }
                    if (topTipHolder.state == TopTipHolder.State.LOCATION_PERMISSION) {
                        topTipHolder.state = TopTipHolder.State.NONE
                    }
                    if (isJump) {
                        finish()
                    }
                }
            },
        )
    }

    private var timeoutEmptyJob: Job? = null

    /**
     * 开始蓝牙搜索，若缺少相应权限或开关未开启，则直接 return.
     */
    private fun startBtScan() {
        if (!PermissionTool.hasBtPermission(this)) { // 没有权限
            return
        }
        if (Build.VERSION.SDK_INT >= 28 && !locationManager.isLocationEnabled) { // 位置信息未开启
            return
        }
        if (!btAdapter.isEnabled) { // 蓝牙未开启
            return
        }
        if (!wifiManager.isWifiEnabled) { // WIFI 未开启
            return
        }
        isFirstRequest = false

        // 连接 WIFI 前执行一次扫描似乎可以加快连接速度，实际效果待观望
        wifiManager.startScan()

        val isSuccess = BluetoothUtil.startLeScan(this)
        if (isSuccess) {
            if (!iv_scan_gif.isAnimating) {
                iv_scan_gif.playAnimation()
            }
            tv_scan_state.setText(R.string.ts004_scan_doing)
            tv_scan_tips.setText(R.string.ts004_device_open)

            timeoutEmptyJob?.cancel()
            timeoutEmptyJob =
                lifecycleScope.launch {
                    delay(60 * 1000)
                    if (adapter.dataList.isEmpty()) {
                        stopBtScan()

                        tv_scan_state.setText(R.string.ts004_scan_nothing)
                        tv_scan_tips.text =
                            SpanBuilder().appendColorAndClick(
                                getString(R.string.ts004_scan_again),
                                0xff06aaff.toInt(),
                            ) {
                                requestPermission(2)
                            }
                    }
                }
        }
    }

    /**
     * 停止蓝牙搜索，暂停扫描动画.
     */
    private fun stopBtScan() {
        iv_scan_gif.pauseAnimation()

        timeoutEmptyJob?.cancel()
        BluetoothUtil.stopLeScan(this)
    }

    private fun connectWIFI(wifiName: String) {
        if (WifiUtil.getCurrentWifiSSID(this) == wifiName && WebSocketProxy.getInstance().isConnected()) { // 已连接
            EventBus.getDefault().post(SocketStateEvent(true, isTS004))
            ToastUtils.showLong(R.string.app_connect)
            NetWorkUtils.switchNetwork(true)
            if (isTS004) {
                SharedManager.hasTS004 = true
                ARouter.getInstance().build(RouterConfig.IR_MONOCULAR).navigation(this)
            } else {
                SharedManager.hasTC007 = true
                ARouter.getInstance().build(
                    RouterConfig.IR_MAIN,
                ).withBoolean(ExtraKeyConfig.IS_TC007, true).navigation(this)
            }
            finish()
            return
        }

        XLog.i("当前连接 ${WifiUtil.getCurrentWifiSSID(this)} 准备连接 $wifiName")
        showCameraLoading()
        // 部分设备部分情况下即没有 onAvailable 也没有 onUnavailable 回调，15秒后把 Loading 弹框 dismiss，避免流程卡死
        // 没有回调是 connectWifi 方法中的 listener 未刷新，修复那个问题后，理论上不存在没回调情况了，这个逻辑先注释掉
        job =
            lifecycleScope.launch {
                examineConnect()
            }
        NetWorkUtils.connectWifi(wifiName, if (isTS004) DeviceConfig.TS004_PASSWORD else DeviceConfig.TC007_PASSWORD) {
            lifecycleScope.launch(Dispatchers.Main) {
                dismissCameraLoading()
                job?.cancel()
                if (it == null) {
                    ToastUtils.showShort(R.string.user_device_connecting_fail)
                } else {
                    ToastUtils.showShort(R.string.app_connect)
                    NetWorkUtils.switchNetwork(true)
                    WebSocketProxy.getInstance().startWebSocket(wifiName, it)
                    if (isTS004) {
                        TS004Repository.netWork = it
                        SharedManager.hasTS004 = true
                        ARouter.getInstance().build(RouterConfig.IR_MONOCULAR).navigation(this@DeviceAddActivity)
                    } else {
                        TC007Repository.netWork = it
                        SharedManager.hasTC007 = true
                        ARouter.getInstance().build(RouterConfig.IR_MAIN)
                            .withBoolean(ExtraKeyConfig.IS_TC007, true)
                            .navigation(this@DeviceAddActivity)
                    }
                    finish()
                }
            }
        }
    }

    /**
     * 递归检查是否链接
     */
    suspend fun examineConnect() {
        delay(10 * 1000)
        if (WebSocketProxy.getInstance().isConnected()) {
            NetWorkUtils.switchNetwork(true) {
                if (isTS004) {
                    TS004Repository.netWork = it
                    SharedManager.hasTS004 = true
                    ARouter.getInstance().build(RouterConfig.IR_MONOCULAR)
                        .navigation(this@DeviceAddActivity)
                } else {
                    TC007Repository.netWork = it
                    SharedManager.hasTC007 = true
                    ARouter.getInstance().build(RouterConfig.IR_MAIN)
                        .withBoolean(ExtraKeyConfig.IS_TC007, true)
                        .navigation(this@DeviceAddActivity)
                }
            }
        } else {
            examineConnect()
        }
        dismissCameraLoading()
    }

    /**
     * 申请权限、开启位置信息、开启蓝牙、开启 WIFI 时顶部提示文字，太多太乱，抽取封装到这里统一处理
     */
    private class TopTipHolder(val textView: TextView) {
        var state = State.NONE
            set(value) {
                field = value
                textView.isVisible = value != State.NONE
                when (value) {
                    State.LOCATION_PERMISSION -> textView.setText(R.string.ts004_exact_location_auth)
                    State.LOCATION_INFO -> textView.setText(R.string.ts004_location_auth)
                    State.BLUETOOTH_PERMISSION -> textView.setText(R.string.ts004_bluetooth_auth)
                    State.BLUETOOTH_SWITCH -> textView.setText(R.string.open_bt_switch_tips)
                    State.WIFI_SWITCH -> textView.setText(R.string.ts004_wlan_auth)
                    else -> {
                    }
                }
            }

        enum class State {
            NONE, // 不显示
            LOCATION_PERMISSION, // 显示精确定位权限申请提示文字
            LOCATION_INFO, // 显示开启位置信息提示文字
            BLUETOOTH_PERMISSION, // 显示蓝牙权限申请文字
            BLUETOOTH_SWITCH, // 显示开启蓝牙开关提示文字
            WIFI_SWITCH, // 显示开启WIFI提示文字
        }
    }

    private class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        /**
         * 从上一界面传递过来的，当前想要连接的设备是 TS004 还是 TC007.
         */
        var isTS004 = true

        val dataList: ArrayList<String> = ArrayList()

        /**
         * “连接”点击事件监听.
         * ssid - 不带双引号的 SSID
         */
        var onConnectClickListener: ((ssid: String) -> Unit)? = null

        fun addOne(newDevice: String) {
            for (hasAddDevice in dataList) {
                if (hasAddDevice == newDevice) { // 已扫描出该结果了
                    return
                }
            }
            dataList.add(newDevice)
            notifyItemInserted(dataList.size)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device_add, parent, false))
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
        ) {
            holder.itemView.iv_icon.setImageResource(
                if (isTS004) R.mipmap.ic_device_add_ts004 else R.mipmap.ic_device_add_tc007,
            )
            holder.itemView.tv_name.text = dataList[position]
        }

        override fun getItemCount(): Int = dataList.size

        inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
            init {
                rootView.tv_connect.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onConnectClickListener?.invoke(dataList[position])
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}
