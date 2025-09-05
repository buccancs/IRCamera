package com.topdon.module.user.activity

import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.user.R
import com.topdon.module.user.databinding.ActivityUnitBinding

/**
 * 温度单位切换
 */
@Route(path = RouterConfig.UNIT)
class UnitActivity : BaseActivity() {
    
    private lateinit var binding: ActivityUnitBinding

    override fun initContentView(): Int {
        binding = ActivityUnitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return R.layout.activity_unit
    }

    override fun initView() {
        binding.titleView.setRightClickListener {
            SharedManager.setTemperature(if (binding.ivDegreesCelsius.isVisible) 1 else 0)
            finish()
        }

        binding.ivDegreesCelsius.isVisible = SharedManager.getTemperature() == 1
        binding.ivFahrenheit.isVisible = SharedManager.getTemperature() == 0

        binding.constraintDegreesCelsius.setOnClickListener {
            binding.ivDegreesCelsius.isVisible = true
            binding.ivFahrenheit.isVisible = false
        }
        binding.constraintFahrenheit.setOnClickListener {
            binding.ivDegreesCelsius.isVisible = false
            binding.ivFahrenheit.isVisible = true
        }
    }

    override fun initData() {

    }

}

