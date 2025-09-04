package com.topdon.module.user.activity

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.view.TitleView
import com.topdon.module.user.R

/**
 * 温度单位切换
 */
// Legacy ARouter route annotation - now using NavigationManager
class UnitActivity : BaseActivity() {

    // View references - migrated from synthetic views
    private lateinit var titleView: TitleView
    private lateinit var ivDegreesCelsius: ImageView
    private lateinit var ivFahrenheit: ImageView
    private lateinit var constraintDegreesCelsius: ConstraintLayout
    private lateinit var constraintFahrenheit: ConstraintLayout

    override fun initContentView() = R.layout.activity_unit

    override fun initView() {
        // Initialize views - migrated from synthetic views
        titleView = findViewById(R.id.title_view)
        ivDegreesCelsius = findViewById(R.id.iv_degrees_celsius)
        ivFahrenheit = findViewById(R.id.iv_fahrenheit)
        constraintDegreesCelsius = findViewById(R.id.constraint_degrees_celsius)
        constraintFahrenheit = findViewById(R.id.constraint_fahrenheit)

        titleView.setRightClickListener {
            SharedManager.setTemperature(if (ivDegreesCelsius.isVisible) 1 else 0)
            finish()
        }

        ivDegreesCelsius.isVisible = SharedManager.getTemperature() == 1
        ivFahrenheit.isVisible = SharedManager.getTemperature() == 0

        constraintDegreesCelsius.setOnClickListener {
            ivDegreesCelsius.isVisible = true
            ivFahrenheit.isVisible = false
        }
        constraintFahrenheit.setOnClickListener {
            ivDegreesCelsius.isVisible = false
            ivFahrenheit.isVisible = true
        }
    }

    override fun initData() {

    }

}

