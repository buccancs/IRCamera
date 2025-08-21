package com.topdon.lib.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.topdon.lib.ui.R
import android.widget.TextView
import android.widget.ImageView

class SteeringWheelView : LinearLayout, OnClickListener {

    var listener: ((action: Int, moveX: Int) -> Unit)? = null
    var moveX = 30
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
        inflate(context, R.layout.ui_steering_wheel_view, this)
        val steeringWheelStartBtn = findViewById<View>(R.id.steering_wheel_start_btn)
        val steeringWheelCenterBtn = findViewById<View>(R.id.steering_wheel_center_btn) 
        val steeringWheelEndBtn = findViewById<View>(R.id.steering_wheel_end_btn)
        val tvConfirm = findViewById<TextView>(R.id.tv_confirm)
        
        steeringWheelStartBtn.setOnClickListener(this)
        steeringWheelCenterBtn.setOnClickListener(this)
        steeringWheelEndBtn.setOnClickListener(this)
        if (rotationIR == 270 || rotationIR == 90){
            tvConfirm.rotation = 270f
            rotation = 90f
        }else{
            tvConfirm.rotation = 0f
            rotation = 0f
        }
    }

    override fun onClick(v: View?) {
        val steeringWheelStartBtn = findViewById<View>(R.id.steering_wheel_start_btn)
        val steeringWheelCenterBtn = findViewById<View>(R.id.steering_wheel_center_btn)
        val steeringWheelEndBtn = findViewById<View>(R.id.steering_wheel_end_btn)
        
        when (v) {
            steeringWheelStartBtn -> {
                moveX += 10
                if (moveX > 60) {
                    moveX = 60
                }
                listener?.invoke(-1, moveX)
            }
            steeringWheelCenterBtn -> {
                listener?.invoke(0, moveX)
            }
            steeringWheelEndBtn -> {
                moveX -= 10
                if (moveX < -20) {
                    moveX = -20
                }
                listener?.invoke(1, moveX)
            }
        }
    }


}