package com.topdon.module.thermal.activity

import android.widget.Button
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.module.thermal.ir.R

//连接设备
class ConnectActivity : BaseActivity() {

    override fun initContentView() = R.layout.activity_connect

    override fun initView() {
        setTitleText(R.string.app_name)
        val device = DeviceTools.isConnect()
        val bluetoothBtn = findViewById<Button>(R.id.bluetooth_btn)
        if (device == null) {
            //未连接
            bluetoothBtn.text = getString(R.string.app_no_connect)
        } else {
            //已连接
            bluetoothBtn.text = getString(R.string.app_connect)
        }


    }

    override fun initData() {

    }


}