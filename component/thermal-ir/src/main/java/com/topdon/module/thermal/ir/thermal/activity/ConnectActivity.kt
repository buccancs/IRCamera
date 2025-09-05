package com.topdon.module.thermal.ir.thermal.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import kotlinx.android.synthetic.main.activity_connect.*

//连接设备
@Route(path = RouterConfig.IR_THERMAL_CONNECT)
class ConnectActivity : BaseIRActivity() {

    override fun initContentView() = R.layout.activity_connect

    override fun initView() {
        // Title handling removed as toolbar_lay doesn't support it
        val device = DeviceTools.isConnect()
        if (device == null) {
            //未连接
            bluetooth_btn.text = getString(R.string.app_no_connect)
        } else {
            //已连接
            bluetooth_btn.text = getString(R.string.app_connect)
        }
    }

    override fun initData() {

    }


}