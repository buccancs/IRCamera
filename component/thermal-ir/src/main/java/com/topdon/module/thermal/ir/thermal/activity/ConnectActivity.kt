package com.topdon.module.thermal.ir.thermal.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import com.topdon.module.thermal.databinding.ActivityConnectBinding

//连接设备
@Route(path = RouterConfig.IR_THERMAL_CONNECT)
class ConnectActivity : BaseIRActivity() {

    private lateinit var binding: ActivityConnectBinding

    override fun initContentView(): Int {
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun initView() {
        // Title handling removed as toolbar_lay doesn't support it
        val device = DeviceTools.isConnect()
        if (device == null) {
            //未连接
            binding.bluetoothBtn.text = getString(R.string.app_no_connect)
        } else {
            //已连接
            binding.bluetoothBtn.text = getString(R.string.app_connect)
        }
    }

    override fun initData() {

    }


}