package com.topdon.tc001.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.SocketMsgEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.repository.BatteryInfo
import com.topdon.lib.core.socket.SocketCmdUtil
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.tools.LocaleContextWrapper
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lib.core.utils.WsCmdConstants
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.tc001.DeviceTypeActivity
import com.topdon.tc001.R
import com.topdon.tc001.databinding.FragmentMainBinding
import com.topdon.tc001.popup.DelPopup
import kotlinx.coroutines.launch
import org.bytedeco.librealsense.context
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


/**
 * 首页 Fragment.
 *
 * Created by LCG on 2024/4/18.
 */
@SuppressLint("NotifyDataSetChanged")
class MainFragment : BaseFragment(), View.OnClickListener {

    private lateinit var adapter : MyAdapter
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun initContentView(): Int = R.layout.fragment_main

    override fun initView() {
        _binding = FragmentMainBinding.bind(requireView())
        
        adapter = MyAdapter()
        binding.tvConnectDevice.setOnClickListener(this)
        binding.ivAdd.setOnClickListener(this)
        adapter.hasConnectLine = DeviceTools.isConnect()
        adapter.hasConnectTS004 = WebSocketProxy.getInstance().isTS004Connect()
        adapter.hasConnectTC007 = WebSocketProxy.getInstance().isTC007Connect()
        adapter.onItemClickListener = {
            when (it) {
                ConnectType.LINE -> {
                    ARouter.getInstance()
                        .build(RouterConfig.IR_MAIN)
                        .withBoolean(ExtraKeyConfig.IS_TC007, false) // TC001 always false
                        .navigation(requireContext())
                }
                // TC007 and TS004 support removed - TC001 only
                else -> {
                    // Only TC001 (LINE) is supported
                    ARouter.getInstance()
                        .build(RouterConfig.IR_MAIN)
                        .withBoolean(ExtraKeyConfig.IS_TC007, false) // TC001 always false
                        .navigation(requireContext())
                }
            }
        }
        adapter.onItemLongClickListener = { view, type ->
            val popup = DelPopup(requireContext())
            popup.onDelListener = {
                TipDialog.Builder(requireContext())
                    .setTitleMessage(AppLanguageUtils.attachBaseContext(
                        context, SharedManager.getLanguage(requireContext())).getString(R.string.tc_delete_device))
                    .setMessage(R.string.tc_delete_device_tips)
                    .setPositiveListener(R.string.report_delete) {
                        when (type) {
                            ConnectType.LINE -> SharedManager.hasTcLine = false
                            ConnectType.TS004 -> SharedManager.hasTS004 = false
                            ConnectType.TC007 -> SharedManager.hasTC007 = false
                        }
                        refresh()
                        TToast.shortToast(requireContext(), R.string.test_results_delete_success)
                    }
                    .setCancelListener(R.string.app_cancel)
                    .create().show()
            }
            popup.show(view)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // TC001 devices use USB connection, no WebSocket battery info needed
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                // 要是当前已连接 TS004、TC007，切到流量上，不然登录注册意见反馈那些没网
                if (WebSocketProxy.getInstance().isConnected()) {
                    NetWorkUtils.switchNetwork(true)
                }
            }
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        refresh()
        adapter?.notifyDataSetChanged()
    }

    private fun refresh() {
        val hasAnyDevice = SharedManager.hasTcLine || SharedManager.hasTS004 || SharedManager.hasTC007
        binding.clHasDevice.isVisible = hasAnyDevice
        binding.clNoDevice.isVisible = !hasAnyDevice
        adapter.hasConnectLine = DeviceTools.isConnect(isAutoRequest = false)
        adapter.hasConnectTS004 = WebSocketProxy.getInstance().isTS004Connect()
        adapter.hasConnectTC007 = WebSocketProxy.getInstance().isTC007Connect()
        adapter.notifyDataSetChanged()
    }

    override fun connected() {
        adapter.hasConnectLine = true
        SharedManager.hasTcLine = true
        refresh()
    }

    override fun disConnected() {
        adapter.hasConnectLine = false
    }

    override fun onSocketConnected(isTS004: Boolean) {
        // TC001 uses USB connection, no socket connection handling needed
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        // TC001 uses USB connection, no socket disconnection handling needed
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvConnectDevice, binding.ivAdd -> {//添加设备
                startActivity(Intent(requireContext(), DeviceTypeActivity::class.java))
//                ARouter.getInstance().build(RoutePath.UsbIrModule.PAGE_IR_MAIN_ACTIVITY)
//                    .navigation()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSocketMsgEvent(event: SocketMsgEvent) {
        if (SocketCmdUtil.getCmdResponse(event.text) == WsCmdConstants.APP_EVENT_HEART_BEATS) {//心跳
            if (!adapter.hasConnectTC007) {//当前连接的不是 TC007
                return
            }
            try {
                val battery: JSONObject = JSONObject(event.text).getJSONObject("battery")
                val level = battery.getString("remaining").toIntOrNull() ?: 0
                val charging = battery.getString("status") == "charging"
                adapter.tc007Battery = BatteryInfo(level, charging)
            } catch (_: Exception) {

            }
        }
    }

    private class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        /**
         * 有线设备当前是否已连接.
         */
        var hasConnectLine: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, 3)
            }
        /**
         * TS004 当前是否已连接.
         */
        var hasConnectTS004: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        /**
         * TC007 当前是否已连接.
         */
        var hasConnectTC007: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, itemCount)
            }
        /**
         * TC007 设备电池信息.
         */
        var tc007Battery: BatteryInfo? = null
            set(value) {
                if (field != value) {
                    field = value
                    notifyItemRangeChanged(0, itemCount)
                }
            }


        var onItemClickListener: ((type: ConnectType) -> Unit)? = null
        var onItemLongClickListener: ((view: View, type: ConnectType) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = com.topdon.tc001.databinding.ItemDeviceConnectBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val type = holder.getConnectType(position)
            val hasTitle: Boolean = when (position) {
                0 -> true
                1 -> SharedManager.hasTcLine
                else -> false
            }
            val hasConnect: Boolean = when (type) {
                ConnectType.LINE -> hasConnectLine
                ConnectType.TS004 -> hasConnectTS004
                ConnectType.TC007 -> hasConnectTC007
            }

            holder.binding.tvTitle.isVisible = hasTitle
            holder.binding.tvTitle.text = AppLanguageUtils.attachBaseContext(
                holder.binding.root.context, SharedManager.getLanguage(holder.binding.root.context))
                .getString(if (type == ConnectType.LINE) R.string.tc_connect_line else R.string.tc_connect_wifi)

            holder.binding.ivBg.isSelected = hasConnect
            holder.binding.tvDeviceName.isSelected = hasConnect
            holder.binding.viewDeviceState.isSelected = hasConnect
            holder.binding.tvDeviceState.isSelected = hasConnect
            holder.binding.tvDeviceState.text = if (hasConnect) "online" else "offline"
            holder.binding.tvBattery.isVisible = type == ConnectType.TC007 && hasConnectTC007 && tc007Battery != null
            holder.binding.batteryView.isVisible = type == ConnectType.TC007 && hasConnectTC007 && tc007Battery != null

            when (type) {
                ConnectType.LINE -> {
                    holder.binding.tvDeviceName.setText(AppLanguageUtils.attachBaseContext(
                        holder.binding.root.context, SharedManager.getLanguage(holder.binding.root.context))
                        .getString(R.string.tc_has_line_device))
                    if (hasConnect) {
                        holder.binding.ivImage.setImageResource(R.drawable.ic_main_device_line_connect)
                    } else {
                        holder.binding.ivImage.setImageResource(R.drawable.ic_main_device_line_disconnect)
                    }
                }
                ConnectType.TS004 -> {
                    holder.binding.tvDeviceName.text = "TS004"
                    if (hasConnect) {
                        holder.binding.ivImage.setImageResource(R.drawable.ic_main_device_ts004_connect)
                    } else {
                        holder.binding.ivImage.setImageResource(R.drawable.ic_main_device_ts004_disconnect)
                    }
                }
                ConnectType.TC007 -> {
                    holder.binding.tvDeviceName.text = "TC007"
                    if (hasConnect) {
                        holder.binding.ivImage.setImageResource(R.drawable.ic_main_device_line_connect)
                    } else {
                        holder.binding.ivImage.setImageResource(R.drawable.ic_main_device_line_disconnect)
                    }
                    holder.binding.tvBattery.text = "${tc007Battery?.getBattery()}%"
                    holder.binding.batteryView.battery = tc007Battery?.getBattery() ?: 0
                    holder.binding.batteryView.isCharging = tc007Battery?.isCharging ?: false
                }
            }
        }

        override fun getItemCount(): Int {
            var result = 0
            if (SharedManager.hasTcLine) {
                result++
            }
            if (SharedManager.hasTS004) {
                result++
            }
            if (SharedManager.hasTC007) {
                result++
            }
            return result
        }

        inner class ViewHolder(val binding: com.topdon.tc001.databinding.ItemDeviceConnectBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.ivBg.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.invoke(getConnectType(position))
                    }
                }
                binding.ivBg.setOnLongClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        //只有离线设备才能长按删除
                        val deviceType = getConnectType(position)
                        when (deviceType) {
                            ConnectType.LINE -> {
                                if (DeviceTools.isConnect()) {
                                    return@setOnLongClickListener true
                                }
                            }
                            ConnectType.TS004 -> {
                                if (WebSocketProxy.getInstance().isTS004Connect()) {
                                    return@setOnLongClickListener true
                                }
                            }
                            ConnectType.TC007 -> {
                                if (WebSocketProxy.getInstance().isTC007Connect()) {
                                    return@setOnLongClickListener true
                                }
                            }
                        }
                        onItemLongClickListener?.invoke(it, deviceType)
                    }
                    true
                }
            }

            fun getConnectType(position: Int): ConnectType = when (position) {
                0 -> if (SharedManager.hasTcLine) {
                    ConnectType.LINE
                } else if (SharedManager.hasTS004) {
                    ConnectType.TS004
                } else {
                    ConnectType.TC007
                }
                1 -> if (SharedManager.hasTcLine) {
                    if (SharedManager.hasTS004) ConnectType.TS004 else ConnectType.TC007
                } else {
                    ConnectType.TC007
                }
                else -> ConnectType.TC007
            }
        }
    }

    enum class ConnectType {
        LINE,
        TS004,
        TC007,
    }
}