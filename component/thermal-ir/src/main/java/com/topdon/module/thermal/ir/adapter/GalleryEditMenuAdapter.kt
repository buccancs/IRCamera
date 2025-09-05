package com.topdon.module.thermal.ir.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.databinding.ItemGalleryEditMenuBinding

@Deprecated("旧的2D编辑一级菜单，已重构过了")
class GalleryEditMenuAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener: ((code: Int) -> Unit)? = null

    private var pointColor = false //点
    private var pseudoColor = false //伪彩
    private var pseudoColorBar = false //伪彩条
    private var settingColorBar = false //设置

    private val bean = arrayListOf(
        IconBean(name = context.getString(R.string.menu_3d_calibrate), icon = R.drawable.selector_menu_first_2_5, code = 1000), //标定
        IconBean(name = context.getString(R.string.thermal_false_color), icon = R.drawable.selector_menu_first_4_3, code = 2000), //伪彩
        IconBean(name = context.getString(R.string.app_setting), icon = R.drawable.selector_menu_first_5_6, code = 4000), //设置
        IconBean(name = context.getString(R.string.func_temper_ruler), icon = R.drawable.selector_menu_first_edit_4, code = 3000), //等温尺
    )

    fun enPointColor(pointColor: Boolean) {
        this.pointColor = pointColor
        notifyDataSetChanged()
    }

    fun enPseudoColor(pseudoColor: Boolean) {
        this.pseudoColor = pseudoColor
        notifyDataSetChanged()
    }

    fun enPseudoColorBar(pseudoColorBar: Boolean) {
        this.pseudoColorBar = pseudoColorBar
        notifyDataSetChanged()
    }

    fun enSettingColorBar(settingColorBar: Boolean) {
        this.settingColorBar = settingColorBar
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemGalleryEditMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun getItemCount(): Int {
        return bean.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            val data = bean[position]
            holder.binding.itemEditMenuTabText.text = data.name
            holder.binding.itemEditMenuTabImg.setImageResource(data.icon)
            holder.binding.itemEditMenuTabLay.setOnClickListener {
                listener?.invoke(data.code)
            }
            when (data.code) {
                1000 -> {
                    iconUI(pointColor, holder.binding.itemEditMenuTabImg, holder.binding.itemEditMenuTabText)
                }
                2000 -> {
                    iconUI(pseudoColor, holder.binding.itemEditMenuTabImg, holder.binding.itemEditMenuTabText)
                }
                3000 -> {
                    iconUI(pseudoColorBar, holder.binding.itemEditMenuTabImg, holder.binding.itemEditMenuTabText)
                }
                4000 -> {
                    iconUI(settingColorBar, holder.binding.itemEditMenuTabImg, holder.binding.itemEditMenuTabText)
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

    inner class ItemView(val binding: ItemGalleryEditMenuBinding) : RecyclerView.ViewHolder(binding.root)

    data class IconBean(val name: String, @DrawableRes val icon: Int, val code: Int)
}