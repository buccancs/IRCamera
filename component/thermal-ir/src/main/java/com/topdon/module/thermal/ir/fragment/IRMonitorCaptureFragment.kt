package com.topdon.module.thermal.ir.fragment

import android.content.Intent
import androidx.core.view.isVisible
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.tools.ToastTools
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.activity.IRMonitorActivity
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView

/**
 * 温度监控-实时（即生成温度监控）.
 * 
 * 支持TC001等插件式设备 (TS004/TC007 support removed)
 */
class IRMonitorCaptureFragment : BaseFragment() {

    override fun initContentView(): Int = R.layout.fragment_ir_monitor_capture

    override fun initView() {
        // Use default animation since TC007 animation was removed
        animation_view.setAnimation("TDAnimationJSON.json")

        view_start.setOnClickListener {
            if (DeviceTools.isConnect()) {
                if (DeviceTools.isTC001LiteConnect()) {
            // TODO: Replace RouterConfig reference with direct navigation
                } else if (DeviceTools.isHikConnect()) {
            // TODO: Replace RouterConfig reference with direct navigation
                } else {
                    startActivity(Intent(requireContext(), IRMonitorActivity::class.java))
                }
            } else {
                ToastTools.showShort(R.string.device_connect_tip)
            }
        }

        refreshUI(DeviceTools.isConnect())
    }

    override fun onResume() {
        super.onResume()
        refreshUI(DeviceTools.isConnect())
    }

    override fun initData() {
    }

    /**
     * 刷新连接状态
     */
    private fun refreshUI(isConnect: Boolean) {
        animation_view.isVisible = !isConnect
        iv_icon.isVisible = isConnect
        view_start.isVisible = isConnect
        tv_start.isVisible = isConnect
    }

    override fun connected() {
        refreshUI(true)
    }

    override fun disConnected() {
        refreshUI(false)
    }
}