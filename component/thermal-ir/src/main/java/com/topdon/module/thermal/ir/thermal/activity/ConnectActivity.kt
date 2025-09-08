package com.topdon.module.thermal.ir.thermal.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import com.topdon.module.thermal.ir.databinding.ActivityConnectBinding

@Route(path = RouterConfig.IR_THERMAL_CONNECT)
class ConnectActivity : BaseIRActivity() {

    private lateinit var binding: ActivityConnectBinding

    override fun initContentView() = R.layout.activity_connect

    override fun initView() {
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Title handling removed as toolbar_lay doesn't support it
        val device = DeviceTools.isConnect()
        if (device == null) {
            binding.bluetoothBtn.text = getString(LibAppR.string.app_no_connect)
        } else {
            binding.bluetoothBtn.text = getString(LibAppR.string.app_connect)
        }
    }

    override fun initData() {

    }


}