package com.topdon.module.thermal.ir.activity

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.databinding.ActivityIrCorrectionBinding
import com.topdon.module.thermal.ir.event.CorrectionFinishEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

 * @author: CaiSongL
 * @date: 2023/8/4 9:06
 * [ExtraKeyConfig.IS_TC007] -  TC007
@Route(path = RouterConfig.IR_CORRECTION)
class IRCorrectionActivity : BaseActivity() {

    private lateinit var binding: ActivityIrCorrectionBinding

    override fun initContentView(): Int = R.layout.activity_ir_correction

    override fun initView() {
        binding = ActivityIrCorrectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.tvCorrection.setOnClickListener {
            val jumpIntent = Intent(this,IRCorrectionTwoActivity::class.java)
            jumpIntent.putExtra(ExtraKeyConfig.IS_TC007, intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false))
            startActivity(jumpIntent)
        }
    }

    override fun initData() {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    /**
     * Function description.
     */
    fun finishCorrection(event: CorrectionFinishEvent) {
        finish()
    }
}