package com.topdon.lib.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.topdon.lib.ui.R

class WifiSteeringWheelView : LinearLayout, OnClickListener {

    var listener: ((action: Int, moveX: Int,moveY:Int) -> Unit)? = null
    var moveX = 0
    var moveY = 0
    var rotationIR = 270
    set(value) {
        field = value
        val tvConfirm = findViewById<TextView>(R.id.tv_confirm)
        if (value == 270 || value == 90){
            tvConfirm?.rotation = 270f
            rotation = 90f
        }else{
            tvConfirm?.rotation = 0f
            rotation = 0f
        }
        requestLayout()
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun initView() {
        inflate(context, R.layout.ui_wifi_steering_wheel_view, this)
        val steeringWheelStartBtn = findViewById<Button>(R.id.steering_wheel_start_btn)
        val steeringWheelCenterBtn = findViewById<Button>(R.id.steering_wheel_center_btn)
        val steeringWheelEndBtn = findViewById<Button>(R.id.steering_wheel_end_btn)
        val steeringWheelTopBtn = findViewById<Button>(R.id.steering_wheel_top_btn)
        val steeringWheelBottomBtn = findViewById<Button>(R.id.steering_wheel_bottom_btn)
        val tvConfirm = findViewById<TextView>(R.id.tv_confirm)
        
        steeringWheelStartBtn.setOnClickListener(this)
        steeringWheelCenterBtn.setOnClickListener(this)
        steeringWheelEndBtn.setOnClickListener(this)
        steeringWheelTopBtn.setOnClickListener(this)
        steeringWheelBottomBtn.setOnClickListener(this)
        
        if (rotationIR == 270 || rotationIR == 90){
            tvConfirm.rotation = 270f
            rotation = 90f
        }else{
            tvConfirm.rotation = 0f
            rotation = 0f
        }
    }

    val moveI = 2
    override fun onClick(v: View?) {
        val steeringWheelStartBtn = findViewById<Button>(R.id.steering_wheel_start_btn)
        val steeringWheelCenterBtn = findViewById<Button>(R.id.steering_wheel_center_btn)
        val steeringWheelEndBtn = findViewById<Button>(R.id.steering_wheel_end_btn)
        val steeringWheelTopBtn = findViewById<Button>(R.id.steering_wheel_top_btn)
        val steeringWheelBottomBtn = findViewById<Button>(R.id.steering_wheel_bottom_btn)
        
        when (v) {
            steeringWheelStartBtn -> {
                listener?.invoke(-1, moveX,moveY)
            }
            steeringWheelCenterBtn -> {
                listener?.invoke(0, moveX,moveY)
            }
            steeringWheelTopBtn -> {
                listener?.invoke(2, moveX,moveY)
            }
            steeringWheelBottomBtn ->{
                listener?.invoke(3, moveX,moveY)
            }
            steeringWheelEndBtn -> {
                listener?.invoke(1,moveX,moveY)
            }
        }
    }


}