package com.topdon.module.thermal.ir.activity

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.event.CorrectionFinishEvent
import kotlinx.android.synthetic.main.activity_ir_correction_two.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterConfig.IR_CORRECTION_TWO)
class IRCorrectionTwoActivity : BaseActivity() {

    override fun initContentView(): Int = R.layout.activity_ir_correction_two

    override fun initView() {
        // Only TC001 support - removed TC007 and HIK support
        iv_sketch_map.setImageResource(R.drawable.ic_corrected_line)

        if (DeviceTools.isConnect()) {
            tv_correction.setBackgroundResource(R.drawable.bg_corners05_solid_theme)
        } else {
            tv_correction.setBackgroundResource(R.drawable.bg_corners05_solid_50_theme)
        }

        tv_correction.setOnClickListener {
            if (DeviceTools.isConnect()) {
                if (DeviceTools.isTC001LiteConnect()){
                    ARouter.getInstance().build(RouterConfig.IR_CORRECTION_THREE_LITE).navigation(this)
                } else {
                    startActivity(Intent(this, IRCorrectionThreeActivity::class.java))
                }
            }
        }
    }


    override fun connected() {
        tv_correction.setBackgroundResource(R.drawable.bg_corners05_solid_theme)
    }

    override fun disConnected() {
        tv_correction.setBackgroundResource(R.drawable.bg_corners05_solid_50_theme)
    }

    override fun onSocketConnected(isTS004: Boolean) {
        // No TC007 support needed
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        // No TC007 support needed
    }

    override fun initData() {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun finishCorrection(event: CorrectionFinishEvent) {
        finish()
    }
}