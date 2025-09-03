package com.example.thermal_lite.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.thermal_lite.R
import com.example.thermal_lite.databinding.ActivityIrCorrectionLiteThreeBinding
import com.example.thermal_lite.fragment.IRMonitorLiteFragment
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 * 锅盖矫正
 * @author: CaiSongL
 * @date: 2023/8/4 9:06
 */
@Route(path = RouterConfig.IR_CORRECTION_THREE_LITE)
class IRCorrectionLiteThreeActivity : BaseActivity() {

    private lateinit var binding: ActivityIrCorrectionLiteThreeBinding

    override fun initContentView(): Int = R.layout.activity_ir_correction_lite_three

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val fragment: IRMonitorLiteFragment = if (savedInstanceState == null) {
            IRMonitorLiteFragment()
        } else {
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as IRMonitorLiteFragment
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, fragment)
                .commit()
        }

        binding.tvCorrection.setOnClickListener {
            lifecycleScope.launch {
                if (fragment.frameReady) {
                    fragment.closeFragment()
                    showCameraLoading()
                    delay(1000)
                    dismissCameraLoading()
                    val intent = Intent(this@IRCorrectionLiteThreeActivity,
                        IRCorrectionLiteFourActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun initView() {
        binding = ActivityIrCorrectionLiteThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {}
}