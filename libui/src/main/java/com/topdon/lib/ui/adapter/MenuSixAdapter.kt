package com.topdon.lib.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.ui.R
import com.topdon.lib.ui.bean.ColorBean
import com.topdon.lib.ui.databinding.UiItemMenuFourViewBinding
import com.topdon.lib.ui.listener.SingleClickListener

@Deprecated("看起来是旧版 2D 编辑的菜单，根本没使用了")
class MenuSixAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ((index: Int, code: Int) -> Unit)? = null
    private var type = 0
    private var selected = -1
    private var colorEnable = false //伪彩条
    private var contrastEnable = false //对比度
    private var ddeEnable = false //细节

    fun selected(index: Int) {
        selected = index
        notifyDataSetChanged()
    }

    fun enColor(colorEnable: Boolean) {
        this.colorEnable = colorEnable
        notifyDataSetChanged()
    }

    fun enContrast(param: Boolean) {
        this.contrastEnable = param
        notifyDataSetChanged()
    }

    fun enDde(param: Boolean) {
        this.ddeEnable = param
        notifyDataSetChanged()
    }

    private val fourBean = arrayListOf(
        ColorBean(R.drawable.selector_menu2_setting_1, context.getString(R.string.thermal_pseudo), 1),
        ColorBean(R.drawable.selector_menu2_setting_2, context.getString(R.string.thermal_contrast), 2),
        ColorBean(R.drawable.selector_menu2_setting_3, context.getString(R.string.thermal_sharpen), 3),
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UiItemMenuFourViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            val bean = fourBean[position]
            holder.name.text = bean.name
            holder.img.setImageResource(bean.res)
            holder.lay.setOnClickListener(object : SingleClickListener() {
                override fun onSingleClick() {
                    listener?.invoke(position, bean.code)
                    selected(bean.code)
                }

            })
            when (bean.code) {
                1 -> {
                    iconUI(colorEnable, holder.img, holder.name)
                }
                2 -> {
                    iconUI(contrastEnable, holder.img, holder.name)
                }
                3 -> {
                    iconUI(ddeEnable, holder.img, holder.name)
                }
                else -> {
                    iconUI(bean.code == selected, holder.img, holder.name)
                }
            }
        }
    }

    // 状态变化
    private fun iconUI(isActive: Boolean, img: ImageView, nameText: TextView) {
        img.isSelected = isActive
        if (isActive) {
            nameText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            nameText.setTextColor(ContextCompat.getColor(context, R.color.font_third_color))
        }
    }

    override fun getItemCount(): Int {
        return fourBean.size
    }

    inner class ItemView(val binding: UiItemMenuFourViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val lay: View = binding.itemMenuTabLay
        val img: ImageView = binding.itemMenuTabImg
        val name: TextView = binding.itemMenuTabText
    }

}