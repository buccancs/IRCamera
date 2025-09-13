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

/**
 * adddevice。
 * Created by LCG on 2024/2/19.
 */
@SuppressLint("NotifyDataSetChanged")
@Route(path = RouterConfig.IR_DEVICE_ADD)
class DeviceAddActivity : BaseActivity() {
    /**
     * 从上一界area传递过来的，当前想要connection的device是 TS004 还是 TC007.
     */
    private var isTS004 = true

    /**
     * 根据产品需求，初次进来需要按流程弹相应弹框申请相关Permission及开启开关；
     * 但若该初次流程未success走完，后续触发相应流程时不再弹框，用该variable进行区分。
     */
    private var isFirstRequest = true

    /**
     * 申请Permission、开启bluetooth、开启 WIFI 时顶部tiptext，太多太乱，抽取封装到这里统一processing
     */
    private lateinit var topTipHolder: TopTipHolder
    private lateinit var locationManager: LocationManager
    private lateinit var wifiManager: WifiManager
    private lateinit var btAdapter: BluetoothAdapter

    private val adapter = MyAdapter()

    override fun initContentView(): Int = R.layout.activity_device_add

    var job: Job? = null

    override fun initView() {
        title_view.setLeftClickListener {
            finish()
        }
        title_view.setRightClickListener {
            ARouter.getInstance().build(RouterConfig.IR_CONNECT_TIPS)
                .withBoolean(ExtraKeyConfig.IS_TC007, !isTS004)
                .navigation(this)
        }

        isTS004 = intent.getBooleanExtra("isTS004", true)
        topTipHolder = TopTipHolder(tv_top_tips)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        btAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        tv_scan_tips.movementMethod = LinkMovementMethod.getInstance()

        adapter.isTS004 = isTS004
        adapter.onConnectClickListener = {
            connectWIFI(it)
        }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter

        requestPermission(2)

        LocationUtil.addBtStateListener(this) {
            // 进入界area时不会收到开启或Close位置info广播，要发生实际变化时才有广播
            XLog.i("【adddevice】 位置info开关state：${if (it) "已开启" else "已Close"}")
            refreshStateAndTips()
            if (it) {
                if (isFirstRequest) {
                    requestPermission(2)
                } else {
                    startBtScan()
                }
                openLocationDialog?.dismiss()
                if (topTipHolder.state == TopTipHolder.State.LOCATION_INFO) {
                    topTipHolder.state = TopTipHolder.State.NONE
                }
            } else {
                stopBtScan()
                adapter.dataList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        BluetoothUtil.setLeScanListener(isTS004) {
            adapter.addOne(it)
        }
        BluetoothUtil.addBtStateListener(this) {
            // 进入界area时不会收到开启或Closebluetooth广播，要发生实际变化时才有广播
            XLog.i("【adddevice】 bluetooth开关state：${if (it) "已开启" else "已Close"}")
            refreshStateAndTips()
            if (it) {
                if (isFirstRequest) {
                    requestPermission(2)
                } else {
                    startBtScan()
                }
                openBtDialog?.dismiss()
                if (topTipHolder.state == TopTipHolder.State.BLUETOOTH_SWITCH) {
                    topTipHolder.state = TopTipHolder.State.NONE
                }
            } else {
                stopBtScan()
                adapter.dataList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        WifiUtil.addWifiStateListener(this) {
            // 进入界area时会收到一次开启或Close WIFI 广播，无论 WIFI state有无变化
            XLog.i("【adddevice】 WIFI 开关state：${if (it) "已开启" else "已Close或未知"}")
            refreshStateAndTips()
            if (it) {
                if (isFirstRequest) {
                    requestPermission(2)
                } else {
                    startBtScan()
                }
                openWifiDialog?.dismiss()
                if (topTipHolder.state == TopTipHolder.State.WIFI_SWITCH) {
                    topTipHolder.state = TopTipHolder.State.NONE
                }
            } else {
                stopBtScan()
                adapter.dataList.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * refresh中间的state及tiptext.
     */
    private fun refreshStateAndTips() {
        val isBtEnable = btAdapter.isEnabled
        val isWifiEnable = wifiManager.isWifiEnabled
        val isLocationEnable = if (Build.VERSION.SDK_INT < 28) true else locationManager.isLocationEnabled
        val hasPermission = PermissionTool.hasBtPermission(this)
        if (isBtEnable && isWifiEnable && isLocationEnable && hasPermission) {
            tv_scan_state.setText(R.string.ts004_scan_doing)
            tv_scan_tips.setText(R.string.ts004_device_open)
        } else {
            tv_scan_state.setText(R.string.ts004_scan_wait)
            tv_scan_tips.text =
                SpanBuilder(getString(R.string.ts004_sure_open)).apply {
                    if (!isLocationEnable) {
                        appendColorAndClick(getString(R.string.ts004_auth_location), 0xff06aaff.toInt()) {
                            showOpenLocationDialog()
                        }
                    }
                    if (!isWifiEnable) {
                        if (!isLocationEnable) {
                            appendColor(", ", 0xff06aaff.toInt())
                        }
                        appendColorAndClick(getString(R.string.ts004_wifi_switch), 0xff06aaff.toInt()) {
                            showOpenWifiDialog()
                        }
                    }
                    if (!isBtEnable) {
                        if (!isLocationEnable || !isWifiEnable) {
                            appendColor(", ", 0xff06aaff.toInt())
                        }
                        appendColorAndClick(getString(R.string.app_bluetooth), 0xff06aaff.toInt()) {
                            requestPermission(1)
                        }
                    }
                    if (!hasPermission) {
                        if (!isLocationEnable || !isWifiEnable || !isBtEnable) {
                            appendColor(", ", 0xff06aaff.toInt())
                        }
                        val hasLocationPermission = XXPermissions.isGranted(this@DeviceAddActivity, Permission.ACCESS_FINE_LOCATION)
                        appendColorAndClick(getString(if (hasLocationPermission) R.string.nearby_device_permissions else R.string.ts004_location_permission), 0xff06aaff.toInt()) {
                            requestPermission(0)
                        }
                    }
                }
        }
    }

    override fun initData() {
    }

    override fun onRestart() {
        super.onRestart()
        startBtScan()
    }

    override fun onStop() {
        super.onStop()
        stopBtScan()
    }

    private var openLocationDialog: TipDialog? = null

    /**
     * Show/Display开启位置info开关tip弹框.
     */
    private fun showOpenLocationDialog() {
        if (openLocationDialog?.isShowing == true) {
            return
        }
        openBtDialog?.dismiss()
        openWifiDialog?.dismiss()
        topTipHolder.state = TopTipHolder.State.LOCATION_INFO
        if (openLocationDialog == null) {
            openLocationDialog =
                TipDialog.Builder(this)
                    .setTitleMessage(getString(R.string.app_tip))
                    .setMessage(R.string.ts004_location_tips)
                    .setPositiveListener(R.string.app_open) {
                        var intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        if (intent.resolveActivity(packageManager) == null) {
                            XLog.e("【adddevice】 位置info Intent 没有对应 Activity，尝试跳转系统settings首页")
                            intent = Intent(Settings.ACTION_SETTINGS)
                            if (intent.resolveActivity(packageManager) == null) {
                                XLog.e("【adddevice】 不可能！系统settings首页 Intent 没有对应 Activity!")
                                return@setPositiveListener
                            }
                        }
                        startActivity(intent)
                    }
                    .setCancelListener(R.string.app_cancel) {
                        isFirstRequest = false
                        topTipHolder.state = TopTipHolder.State.NONE
                    }
                    .create()
        }
        openLocationDialog?.show()
    }

    private var openBtDialog: TipDialog? = null

    /**
     * Show/Display开启 WIFI 开关tip弹框，该method只在Permission申请通过后调用，故而理论上已拥有相应Permission.
     */
    private fun showOpenBtDialog() {
        if (openBtDialog?.isShowing == true) {
            return
        }
        openLocationDialog?.dismiss()
        openWifiDialog?.dismiss()
        topTipHolder.state = TopTipHolder.State.BLUETOOTH_SWITCH
        if (openBtDialog == null) {
            openBtDialog =
                TipDialog.Builder(this)
                    .setTitleMessage(getString(R.string.app_tip))
                    .setMessage(R.string.ts004_bluetooth_tips)
                    .setPositiveListener(R.string.app_open) {
                        var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        if (intent.resolveActivity(packageManager) == null) {
                            XLog.e("【adddevice】 不可能！开启bluetooth Intent 没有对应 Activity! 尝试跳转系统settings首页")
                            intent = Intent(Settings.ACTION_SETTINGS)
                            if (intent.resolveActivity(packageManager) == null) {
                                XLog.e("【adddevice】 不可能！系统settings首页 Intent 没有对应 Activity!")
                                return@setPositiveListener
                            }
                        }
                        startActivity(intent)
                    }
                    .setCancelListener(R.string.app_cancel) {
                        isFirstRequest = false
                        topTipHolder.state = TopTipHolder.State.NONE
                    }
                    .create()
        }
        openBtDialog?.show()
    }

    private var openWifiDialog: TipDialog? = null

    /**
     * Show/Display开启 WIFI 开关tip弹框.
     */
    private fun showOpenWifiDialog() {
        if (openWifiDialog?.isShowing == true) {
            return
        }
        openLocationDialog?.dismiss()
        openBtDialog?.dismiss()
        topTipHolder.state = TopTipHolder.State.WIFI_SWITCH
        if (openWifiDialog == null) {
            openWifiDialog =
                TipDialog.Builder(this)
                    .setTitleMessage(getString(R.string.app_tip))
                    .setMessage(R.string.ts004_wlan_tips)
                    .setPositiveListener(R.string.app_open) {
                        if (Build.VERSION.SDK_INT < 29) { // 低于 Android10
                            wifiManager.isWifiEnabled = true
                        } else {
                            var wifiIntent = Intent(Settings.Panel.ACTION_WIFI)
                            if (wifiIntent.resolveActivity(packageManager) == null) {
                                XLog.e("【adddevice】 浮窗开启 WIFI Intent 没有对应 Activity! 尝试Open WIFI settings Intent")
                                wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                if (wifiIntent.resolveActivity(packageManager) == null) {
                                    XLog.e("【adddevice】 不可能！WIFI settings Intent 没有对应 Activity! 尝试跳转系统settings首页")
                                    wifiIntent = Intent(Settings.ACTION_SETTINGS)
                                    if (wifiIntent.resolveActivity(packageManager) == null) {
                                        XLog.e("【adddevice】 不可能！系统settings首页 Intent 没有对应 Activity!")
                                        return@setPositiveListener
                                    }
                                }
                            }
                            startActivity(wifiIntent)
                        }
                    }
                    .setCancelListener(R.string.app_cancel) {
                        isFirstRequest = false
                        topTipHolder.state = TopTipHolder.State.NONE
                    }
                    .create()
        }
        openWifiDialog?.show()
    }

    /**
     * Permission申请可能同时触发多次，用该variable进行控制。
     */
    private var isRequesting = false

    /**
     * 请求相应Permission如定位、bluetoothPermission.
     * @param actionType 所有Permission授予后要执行的processing 0-不做任何processing 1-仅检测并弹出bluetooth开关弹框 2-检测并弹出所有需要的开关弹框
     */
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
                            if (!btAdapter.isEnabled) { // bluetooth未开启
                                showOpenBtDialog()
                                return
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= 28 && !locationManager.isLocationEnabled) { // 位置info未开启
                                showOpenLocationDialog()
                                return
                            }
                            if (!wifiManager.isWifiEnabled) { // WIFI 未开启
                                showOpenWifiDialog()
                                return
                            }
                            if (!btAdapter.isEnabled) { // bluetooth未开启
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
     * startbluetoothSearch，若缺少相应Permission或开关未开启，则直接 return.
     */
    private fun startBtScan() {
        if (!PermissionTool.hasBtPermission(this)) { // 没有Permission
            return
        }
        if (Build.VERSION.SDK_INT >= 28 && !locationManager.isLocationEnabled) { // 位置info未开启
            return
        }
        if (!btAdapter.isEnabled) { // bluetooth未开启
            return
        }
        if (!wifiManager.isWifiEnabled) { // WIFI 未开启
            return
        }
        isFirstRequest = false

        // connection WIFI 前执行一次扫描似乎可以加快connection速度，实际效果待观望
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
                            SpanBuilder().appendColorAndClick(getString(R.string.ts004_scan_again), 0xff06aaff.toInt()) {
                                requestPermission(2)
                            }
                    }
                }
        }
    }

    /**
     * stopbluetoothSearch，pause扫描动画.
     */
    private fun stopBtScan() {
        iv_scan_gif.pauseAnimation()

        timeoutEmptyJob?.cancel()
        BluetoothUtil.stopLeScan(this)
    }

    private fun connectWIFI(wifiName: String) {
        if (WifiUtil.getCurrentWifiSSID(this) == wifiName && WebSocketProxy.getInstance().isConnected()) { // 已connection
            EventBus.getDefault().post(SocketStateEvent(true, isTS004))
            ToastUtils.showLong(R.string.app_connect)
            NetWorkUtils.switchNetwork(true)
            if (isTS004) {
                SharedManager.hasTS004 = true
                ARouter.getInstance().build(RouterConfig.IR_MONOCULAR).navigation(this)
            } else {
                SharedManager.hasTC007 = true
                ARouter.getInstance().build(RouterConfig.IR_MAIN).withBoolean(ExtraKeyConfig.IS_TC007, true).navigation(this)
            }
            finish()
            return
        }

        XLog.i("当前connection ${WifiUtil.getCurrentWifiSSID(this)} 准备connection $wifiName")
        showCameraLoading()
        // 部分device部分情况下即没有 onAvailable 也没有 onUnavailable Callback，15秒后把 Loading 弹框 dismiss，避免流程卡死
        // 没有Callback是 connectWifi method中的 listener 未refresh，Fix/Repair那个问题后，理论上不存在没Callback情况了，这个逻辑先comment掉
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
     * 递归Check是否链接
     */
    suspend fun examineConnect()  {
        delay(10 * 1000)
        if (WebSocketProxy.getInstance().isConnected())
            {
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
            } else
            {
                examineConnect()
            }
        dismissCameraLoading()
    }

    /**
     * 申请Permission、开启位置info、开启bluetooth、开启 WIFI 时顶部tiptext，太多太乱，抽取封装到这里统一processing
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
            NONE, // 不Show/Display
            LOCATION_PERMISSION, // Show/Display精确定位Permission申请tiptext
            LOCATION_INFO, // Show/Display开启位置infotiptext
            BLUETOOTH_PERMISSION, // Show/DisplaybluetoothPermission申请text
            BLUETOOTH_SWITCH, // Show/Display开启bluetooth开关tiptext
            WIFI_SWITCH, // Show/Display开启WIFItiptext
        }
    }

    private class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        /**
         * 从上一界area传递过来的，当前想要connection的device是 TS004 还是 TC007.
         */
        var isTS004 = true

        val dataList: ArrayList<String> = ArrayList()

        /**
         * “connection”clickEventListener.
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
            holder.itemView.iv_icon.setImageResource(if (isTS004) R.mipmap.ic_device_add_ts004 else R.mipmap.ic_device_add_tc007)
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
