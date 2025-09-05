package com.topdon.module.thermal.ir.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.tools.ToastTools
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.activity.IRMonitorActivity
import com.topdon.module.thermal.databinding.FragmentIrMonitorCaptureBinding

/**
 * 温度监控-实时（即生成温度监控）.
 *
 * 需要传递参数：
 * - [ExtraKeyConfig.IS_TC007] - 当前设备是否为 TC007
 */
class IRMonitorCaptureFragment : BaseFragment() {

    private var _binding: FragmentIrMonitorCaptureBinding? = null
    private val binding get() = _binding!!

    /**
     * 从上一界面传递过来的，当前是否为 TC007 设备类型.
     * true-TC007 false-其他插件式设备
     */
    private var isTC007 = false

    override fun initContentView(): Int {
        return R.layout.fragment_ir_monitor_capture
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentIrMonitorCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        isTC007 = arguments?.getBoolean(ExtraKeyConfig.IS_TC007, false) ?: false
        binding.animationView.setAnimation(if (isTC007) "TC007AnimationJSON.json" else "TDAnimationJSON.json")

        binding.viewStart.setOnClickListener {
            // TC001 only - no TC007 support
            if (DeviceTools.isConnect()) {
                if (DeviceTools.isTC001LiteConnect()){
                    ARouter.getInstance().build(RouterConfig.IR_THERMAL_MONITOR_LITE).navigation(requireContext())
                } else{
                    startActivity(Intent(requireContext(), IRMonitorActivity::class.java))
                }
            } else {
                ToastTools.showShort(R.string.device_connect_tip)
            }
        }

        refreshUI(if (isTC007) WebSocketProxy.getInstance().isTC007Connect() else DeviceTools.isConnect())
    }

    override fun onResume() {
        super.onResume()
        refreshUI(if (isTC007) WebSocketProxy.getInstance().isTC007Connect() else DeviceTools.isConnect())
    }

    override fun initData() {
    }

    /**
     * 刷新连接状态
     */
    private fun refreshUI(isConnect: Boolean) {
        binding.animationView.isVisible = !isConnect
        binding.ivIcon.isVisible = isConnect
        binding.viewStart.isVisible = isConnect
        binding.tvStart.isVisible = isConnect
    }

    override fun connected() {
        if (!isTC007) {
            refreshUI(true)
        }
    }

    override fun disConnected() {
        if (!isTC007) {
            refreshUI(false)
        }
    }

    override fun onSocketConnected(isTS004: Boolean) {
        if (isTC007 && !isTS004) {
            refreshUI(true)
        }
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        if (isTC007 && !isTS004) {
            refreshUI(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}