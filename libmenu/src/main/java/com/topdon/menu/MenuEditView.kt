package com.topdon.menu

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.topdon.menu.databinding.ViewMenuEditBinding

/**
 * 2D编辑页面所用底部menu栏.
 *
 * point/line/area、pseudo color、settings、pseudo color条
 */
/**
 * MenuEditView class
 */
/**
 * Custom Menu edit view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class MenuEditView : FrameLayout, View.OnClickListener {
    /**
     * pseudo color条图标current是否处于selectedstate
     */
    var isBarSelect: Boolean
        get() = binding.ivMenu4.isSelected
        set(value) {
            binding.ivMenu4.isSelected = value
            binding.tvMenu4.isSelected = value
        }

    /**
     * 0-point/line/area、1-pseudo colorcolor、2-settings menuclickevent listener.
     */
    var onTabClickListener: ((selectPosition: Int) -> Unit)? = null

    /**
     * pseudo color条图标clickevent listener.
     */
    var onBarClickListener: ((isBarSelect: Boolean) -> Unit)? = null

    private lateinit var binding: ViewMenuEditBinding

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
    ) {
        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.view_menu_edit, this, true)
        } else {
            binding = ViewMenuEditBinding.inflate(LayoutInflater.from(context), this, true)
            binding.clMenu1.setOnClickListener(this)
            binding.clMenu2.setOnClickListener(this)
            binding.clMenu3.setOnClickListener(this)
            binding.clMenu4.setOnClickListener(this)
        }
    }

    /**
     * currentselected那个 tab，取值 `[0,2]`
     */
    private var selectPosition = -1
        set(value) {
            if (field != value) {
                field = value
                binding.ivMenu1.isSelected = value == 0
                binding.tvMenu1.isSelected = value == 0
                binding.ivMenu2.isSelected = value == 1
                binding.tvMenu2.isSelected = value == 1
                binding.ivMenu3.isSelected = value == 2
                binding.tvMenu3.isSelected = value == 2
            }
        }

    override fun onClick(v: View?) {
        if (v == binding.clMenu4) {
            isBarSelect = !isBarSelect
            onBarClickListener?.invoke(isBarSelect)
        } else {
            when (v) {
                binding.clMenu1 -> selectPosition = 0
                binding.clMenu2 -> selectPosition = 1
                binding.clMenu3 -> selectPosition = 2
            }
            onTabClickListener?.invoke(selectPosition)
        }
    }
}
