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
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.SocketMsgEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
// BatteryInfo import removed - TC007 support removed
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
import com.topdon.tc001.popup.DelPopup
import android.widget.TextView
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
// BatteryView import removed - TC007 support removed
import kotlinx.coroutines.launch
import org.bytedeco.librealsense.context
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


@SuppressLint("NotifyDataSetChanged")
class MainFragment : BaseFragment(), View.OnClickListener {

    private lateinit var adapter : MyAdapter

    override fun initContentView(): Int = R.layout.fragment_main

    override fun initView() {
        val tvConnectDevice = requireView().findViewById<TextView>(R.id.tv_connect_device)
        val ivAdd = requireView().findViewById<ImageView>(R.id.iv_add)
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recycler_view)
        val clHasDevice = requireView().findViewById<ConstraintLayout>(R.id.cl_has_device)
        val clNoDevice = requireView().findViewById<ConstraintLayout>(R.id.cl_no_device)
        
        adapter = MyAdapter()
        tvConnectDevice.setOnClickListener(this)
        ivAdd.setOnClickListener(this)
        adapter.hasConnectLine = DeviceTools.isConnect()
        // TS004/TC007 support removed
        adapter.onItemClickListener = {
            when (it) {
                ConnectType.LINE -> {
                    val intent = Intent(requireContext(), com.topdon.module.thermal.ir.activity.IRMainActivity::class.java)
                    startActivity(intent)
                }
                // TS004/TC007 cases removed
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
                            // TS004/TC007 cases removed (SharedManager properties no longer exist)
                        }
                        refresh()
                        TToast.shortToast(requireContext(), R.string.test_results_delete_success)
                    }
                    .setCancelListener(R.string.app_cancel)
                    .create().show()
            }
            popup.show(view)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // TC007 support removed - only TC001 supported
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                if (WebSocketProxy.getInstance().isConnected()) {
                    NetWorkUtils.switchNetwork(true)
                }
            }
        })
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        refresh()
        adapter?.notifyDataSetChanged()
    }

    private fun refresh() {
        val clHasDevice = requireView().findViewById<ConstraintLayout>(R.id.cl_has_device)
        val clNoDevice = requireView().findViewById<ConstraintLayout>(R.id.cl_no_device)
        
        val hasAnyDevice = SharedManager.hasTcLine
        clHasDevice.isVisible = hasAnyDevice
        clNoDevice.isVisible = !hasAnyDevice
        adapter.hasConnectLine = DeviceTools.isConnect(isAutoRequest = false)
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
        // Only TC001 supported - TS004/TC007 support removed
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        // Only TC001 supported - TS004/TC007 support removed
    }

    override fun onClick(v: View?) {
        val tvConnectDevice = requireView().findViewById<TextView>(R.id.tv_connect_device)
        val ivAdd = requireView().findViewById<ImageView>(R.id.iv_add)
        
        when (v) {
            tvConnectDevice, ivAdd -> {//添加设备
                startActivity(Intent(requireContext(), DeviceTypeActivity::class.java))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSocketMsgEvent(event: SocketMsgEvent) {
        // TC007 heartbeat handling removed - only TC001 supported
    }

    private class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        var hasConnectLine: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, 3)
            }


        var onItemClickListener: ((type: ConnectType) -> Unit)? = null
        var onItemLongClickListener: ((view: View, type: ConnectType) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device_connect, parent, false))
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val type = holder.getConnectType(position)
            val hasTitle: Boolean = (position == 0) // Show title for first (and only) item
            val hasConnect: Boolean = hasConnectLine // Only LINE is supported

            val tvTitle = holder.itemView.findViewById<TextView>(R.id.tv_title)
            val ivBg = holder.itemView.findViewById<ImageView>(R.id.iv_bg)
            val tvDeviceName = holder.itemView.findViewById<TextView>(R.id.tv_device_name)
            val viewDeviceState = holder.itemView.findViewById<View>(R.id.view_device_state)
            val tvDeviceState = holder.itemView.findViewById<TextView>(R.id.tv_device_state)
            val tvBattery = holder.itemView.findViewById<TextView>(R.id.tv_battery)
            // BatteryView removed - TC007 support removed
            val ivImage = holder.itemView.findViewById<ImageView>(R.id.iv_image)

            tvTitle.isVisible = hasTitle
            tvTitle.text = holder.itemView.context.getString(R.string.tc_connect_line) // Always LINE

            ivBg.isSelected = hasConnect
            tvDeviceName.isSelected = hasConnect
            viewDeviceState.isSelected = hasConnect
            tvDeviceState.isSelected = hasConnect
            tvDeviceState.text = if (hasConnect) "online" else "offline"
            tvBattery.isVisible = false // No battery UI for TC001
            // batteryView.isVisible removed - TC007 support removed

            // Only LINE type supported for TC001
            tvDeviceName.text = holder.itemView.context.getString(R.string.tc_has_line_device)
            if (hasConnect) {
                ivImage.setImageResource(R.drawable.ic_main_device_line_connect)
            } else {
                ivImage.setImageResource(R.drawable.ic_main_device_line_disconnect)
            }
        }

        override fun getItemCount(): Int {
            // Only LINE/TC001 supported
            return if (SharedManager.hasTcLine) 1 else 0
        }

        inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
            init {
                val ivBg = rootView.findViewById<ImageView>(R.id.iv_bg)
                ivBg.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.invoke(getConnectType(position))
                    }
                }
                ivBg.setOnLongClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val deviceType = getConnectType(position)
                        // Only allow long click if TC001 is not connected
                        if (deviceType == ConnectType.LINE && !DeviceTools.isConnect()) {
                            onItemLongClickListener?.invoke(it, deviceType)
                        }
                    }
                    true
                }
            }

            fun getConnectType(position: Int): ConnectType = ConnectType.LINE // Only LINE supported
        }
    }

    enum class ConnectType {
        LINE
    }
}