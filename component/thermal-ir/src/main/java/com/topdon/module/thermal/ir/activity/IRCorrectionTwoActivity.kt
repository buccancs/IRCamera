package com.topdon.module.thermal.ir.activity

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.event.CorrectionFinishEvent
import com.topdon.module.thermal.databinding.ActivityIrCorrectionTwoBinding
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * 锅盖矫正
 * @author: CaiSongL
 * @date: 2023/8/4 9:06
 *
 * 需要传递参数：
 * - [ExtraKeyConfig.IS_TC007] - 当前设备是否为 TC007
 */
@Route(path = RouterConfig.IR_CORRECTION_TWO)
class IRCorrectionTwoActivity : BaseActivity() {

    private lateinit var binding: ActivityIrCorrectionTwoBinding

    /**
     * 从上一界面传递过来的，当前是否为 TC007 设备类型.
     * true-TC007 false-其他插件式设备
     */
    private var isTC007 = false

    override fun initContentView(): Int = R.layout.activity_ir_correction_two

    override fun initView() {
        binding = ActivityIrCorrectionTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        binding.ivSketchMap.setImageResource(R.drawable.ic_corrected_line) // Use standard corrected line drawable for TC001

        if (if (isTC007) WebSocketProxy.getInstance().isTC007Connect() else DeviceTools.isConnect()) {
            binding.tvCorrection.setBackgroundResource(R.drawable.bg_corners05_solid_theme)
        } else {
            binding.tvCorrection.setBackgroundResource(R.drawable.bg_corners05_solid_50_theme)
        }

        binding.tvCorrection.setOnClickListener {
            if (DeviceTools.isConnect()) {
                // TC001 only - no TC007 support
                if (DeviceTools.isTC001LiteConnect()){
                    ARouter.getInstance().build(RouterConfig.IR_CORRECTION_THREE_LITE).navigation(this)
                } else{
                    startActivity(Intent(this, IRCorrectionThreeActivity::class.java))
                }
            } else {
                TipDialog.Builder(this)
                    .setMessage(R.string.device_connect_tip)
                    .setPositiveListener(R.string.app_confirm)
                    .create().show()
            }
        }
    }


    override fun connected() {
        if (!isTC007) {
            binding.tvCorrection.setBackgroundResource(R.drawable.bg_corners05_solid_theme)
        }
    }

    override fun disConnected() {
        if (!isTC007) {
            binding.tvCorrection.setBackgroundResource(R.drawable.bg_corners05_solid_50_theme)
        }
    }

    override fun onSocketConnected(isTS004: Boolean) {
        if (isTC007 && !isTS004) {
            binding.tvCorrection.setBackgroundResource(R.drawable.bg_corners05_solid_theme)
        }
    }

    override fun onSocketDisConnected(isTS004: Boolean) {
        if (isTC007 && !isTS004) {
            binding.tvCorrection.setBackgroundResource(R.drawable.bg_corners05_solid_50_theme)
        }
    }

    override fun initData() {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun finishCorrection(event: CorrectionFinishEvent) {
        finish()
    }
}