package com.topdon.module.thermal.ir.activity

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.fragment.IRCorrectionFragment
import com.topdon.module.thermal.databinding.ActivityIrCorrectionThreeBinding

/**
 *
 * 锅盖矫正
 * @author: CaiSongL
 * @date: 2023/8/4 9:06
 */
@Route(path = RouterConfig.IR_CORRECTION_THREE)
class IRCorrectionThreeActivity : BaseActivity() {

    private lateinit var binding: ActivityIrCorrectionThreeBinding

    override fun initContentView(): Int = R.layout.activity_ir_correction_three

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIrCorrectionThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val fragment: IRCorrectionFragment = if (savedInstanceState == null) {
            IRCorrectionFragment()
        } else {
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as IRCorrectionFragment
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, fragment)
                .commit()
        }

        binding.tvCorrection.setOnClickListener {
            if (fragment.frameReady) {
                val intent = Intent(this,IRCorrectionFourActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun initView() {
    }

    override fun initData() {}
}