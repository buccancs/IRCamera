package com.topdon.house.popup

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.topdon.house.R
import kotlinx.android.synthetic.main.popup_three_pick.view.*

    fun show(
        anchor: View,
        isLeft: Boolean,
    ) {
        val heightPixels = context.resources.displayMetrics.heightPixels
        val locationArray = IntArray(2)
        anchor.getLocationInWindow(locationArray)

        val x = if (isLeft) locationArray[0] else locationArray[0] + anchor.width + SizeUtils.dp2px(17f) - width

        if (isLeft) {
            if (locationArray[1] >= height) { // 在 anchor 上面放得下
                showAtLocation(anchor, Gravity.NO_GRAVITY, x, locationArray[1] - height)
            } else { // 上面放不下就放下面吧
                showAsDropDown(anchor, Gravity.NO_GRAVITY, x, locationArray[1] + anchor.height)
            }
        } else {
            if (heightPixels - locationArray[1] - anchor.height - SizeUtils.dp2px(10f) > height) { // 在 anchor 底部放得下
                showAtLocation(anchor, Gravity.NO_GRAVITY, x, locationArray[1] + anchor.height + SizeUtils.dp2px(10f))
            } else { // 下面放不下就放上面吧
                showAtLocation(
                    anchor,
                    Gravity.NO_GRAVITY,
                    x,
                    (locationArray[1] - SizeUtils.dp2px(10f) - height).coerceAtLeast(0),
                )
            }
        }
    }
}
