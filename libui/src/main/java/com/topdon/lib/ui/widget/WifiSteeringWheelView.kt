package com.topdon.lib.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR
// import kotlinx.android.synthetic.  // TODO: Replace with ViewBindingmain.ui_wifi_steering_wheel_view.view.*

/**
 * 校准方向
 */
class WifiSteeringWheelView : LinearLayout, OnClickListener {

    var listener: ((action: Int, moveX: Int,moveY:Int) -> Unit)? = null
    var moveX = 0
    var moveY = 0
    
    private val steeringWheelStartBtn by lazy { findViewById<View>(UiR.id.steering_wheel_start_btn) }
    private val steeringWheelCenterBtn by lazy { findViewById<View>(UiR.id.steering_wheel_center_btn) }
    private val steeringWheelEndBtn by lazy { findViewById<View>(UiR.id.steering_wheel_end_btn) }
    private val steeringWheelTopBtn by lazy { findViewById<View>(UiR.id.steering_wheel_top_btn) }
    private val steeringWheelBottomBtn by lazy { findViewById<View>(UiR.id.steering_wheel_bottom_btn) }
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
        inflate(context, UiR.layout.ui_wifi_steering_wheel_view, this)
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
        when (v) {
            steeringWheelStartBtn -> {
//                moveY -= moveI
                listener?.invoke(-1, moveX,moveY)
            }
            steeringWheelCenterBtn -> {
                listener?.invoke(0, moveX,moveY)
            }
            steeringWheelTopBtn -> {
//                moveX += moveI
                listener?.invoke(2, moveX,moveY)
            }
            steeringWheelBottomBtn ->{
//                moveX -= moveI
                listener?.invoke(3, moveX,moveY)
            }
            steeringWheelEndBtn -> {
//                moveY += moveI
                listener?.invoke(1,moveX,moveY)
            }
        }
    }


}