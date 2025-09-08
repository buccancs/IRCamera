package com.topdon.lib.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.csl.irCamera.libui.R
import com.topdon.lib.ui.bean.ColorBean
import com.csl.irCamera.libui.databinding.UiItemMenuFourViewBinding
import com.topdon.menu.R as MenuR
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.lib.ui.listener.SingleClickListener

@Deprecated(" 2D ，")
class MenuSixAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ((index: Int, code: Int) -> Unit)? = null
    private var type = 0
    private var selected = -1
    private var colorEnable = false //
    private var contrastEnable = false //
    private var ddeEnable = false //

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
        ColorBean(MenuR.drawable.selector_menu2_setting_1, context.getString(MenuR.string.thermal_pseudo), 1),
        ColorBean(MenuR.drawable.selector_menu2_setting_2, context.getString(MenuR.string.thermal_contrast), 2),
        ColorBean(MenuR.drawable.selector_menu2_setting_3, context.getString(MenuR.string.thermal_sharpen), 3),
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

    private fun iconUI(isActive: Boolean, img: ImageView, nameText: TextView) {
        img.isSelected = isActive
        if (isActive) {
            nameText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            nameText.setTextColor(ContextCompat.getColor(context, LibAppR.color.font_third_color))
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