package com.topdon.module.user.activity

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.view.TitleView
import com.topdon.tc001.R

class UnitActivity : BaseActivity() {

    override fun initContentView() = R.layout.activity_unit

    override fun initView() {
        val titleView = findViewById<TitleView>(R.id.title_view)
        val ivDegreesCelsius = findViewById<ImageView>(R.id.iv_degrees_celsius)
        val ivFahrenheit = findViewById<ImageView>(R.id.iv_fahrenheit)
        val constraintDegreesCelsius = findViewById<ConstraintLayout>(R.id.constraint_degrees_celsius)
        val constraintFahrenheit = findViewById<ConstraintLayout>(R.id.constraint_fahrenheit)

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

