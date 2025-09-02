package com.topdon.lib.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR

class SettingNightView : LinearLayout {

    var isRightArrowVisible: Boolean
        get() = endImg.isVisible
        set(value) {
            endImg.isVisible = value
        }

    fun setRightTextId(@StringRes resId: Int) {
        val tvEnd: TextView = findViewById(UiR.id.tv_end)
        tvEnd.isVisible = resId != 0
        if (resId != 0) {
            tvEnd.setText(resId)
        }
    }



    private var iconRes: Int = 0
    private var contentStr: String = ""
    private var moreShow: Boolean = true
    private var lineShow: Boolean = false
    private var iconShow: Boolean = false

    private lateinit var headImg: ImageView
    private lateinit var endImg: ImageView
    private lateinit var contentText: TextView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, UiR.styleable.SettingNightView)
        for (i in 0 until ta.indexCount) {
            when (ta.getIndex(i)) {
                UiR.styleable.SettingNightView_setting_icon_night -> iconRes =
                    ta.getResourceId(
                        UiR.styleable.SettingNightView_setting_icon_night,
                        UiR.drawable.ic_setting_default_svg
                    )
                UiR.styleable.SettingNightView_setting_text_night -> contentStr =
                    ta.getString(UiR.styleable.SettingNightView_setting_text_night).toString()
                UiR.styleable.SettingNightView_setting_more_night -> moreShow =
                    ta.getBoolean(UiR.styleable.SettingNightView_setting_more_night, true)
                UiR.styleable.SettingNightView_setting_line_night -> lineShow =
                    ta.getBoolean(UiR.styleable.SettingNightView_setting_line_night, false)
                UiR.styleable.SettingNightView_setting_icon_show_night -> iconShow =
                    ta.getBoolean(UiR.styleable.SettingNightView_setting_icon_show_night, false)
            }
        }
        ta.recycle()
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun initView() {
        inflate(context, UiR.layout.ui_setting_view_night, this)
        contentText = findViewById(UiR.id.item_setting_text)
        headImg = findViewById(UiR.id.item_setting_image)
        endImg = findViewById(UiR.id.item_setting_end_image)

        headImg.setImageResource(iconRes)
        if (iconShow) {
            headImg.visibility = View.VISIBLE
        } else {
            headImg.visibility = View.GONE
        }
        contentText.text = contentStr
        if (moreShow) {
            endImg.visibility = View.VISIBLE
        } else {
            endImg.visibility = View.GONE
        }
        findViewById<View>(UiR.id.item_setting_line).visibility = if (lineShow) View.VISIBLE else View.GONE
    }
}