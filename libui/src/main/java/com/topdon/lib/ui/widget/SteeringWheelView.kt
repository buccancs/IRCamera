package com.topdon.lib.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR

/**
 * 校准方向
 */
class SteeringWheelView : LinearLayout, OnClickListener {

    var listener: ((action: Int, moveX: Int) -> Unit)? = null
    var moveX = 30
    
    private val steeringWheelStartBtn by lazy { findViewById<View>(UiR.id.steering_wheel_start_btn) }
    private val steeringWheelCenterBtn by lazy { findViewById<View>(UiR.id.steering_wheel_center_btn) }
    private val steeringWheelEndBtn by lazy { findViewById<View>(UiR.id.steering_wheel_end_btn) }
    private val tvConfirm by lazy { findViewById<View>(UiR.id.tv_confirm) }
    
    var rotationIR = 270
    set(value) {
        field = value
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
        inflate(context, UiR.layout.ui_steering_wheel_view, this)
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