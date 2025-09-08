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
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.menu.R as MenuR
import com.topdon.module.thermal.ir.databinding.ItemGalleryEditMenuBinding

@Deprecated("2D，")
class GalleryEditMenuAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /** listener property */
    var listener: ((code: Int) -> Unit)? = null

    private var pointColor = false //
    private var pseudoColor = false //
    private var pseudoColorBar = false //
    private var settingColorBar = false //

    private val bean = arrayListOf(
        IconBean(name = context.getString(LibAppR.string.menu_3d_calibrate), icon = MenuR.drawable.selector_menu_first_2_5, code = 1000), //
        IconBean(name = context.getString(LibAppR.string.thermal_false_color), icon = MenuR.drawable.selector_menu_first_4_3, code = 2000), //
        IconBean(name = context.getString(LibAppR.string.app_setting), icon = MenuR.drawable.selector_menu_first_5_6, code = 4000), //
        IconBean(name = context.getString(LibAppR.string.func_temper_ruler), icon = MenuR.drawable.selector_menu_first_edit_4, code = 3000), //
    )

    /**
     * Function description.
     */
    fun enPointColor(pointColor: Boolean) {
        this.pointColor = pointColor
        notifyDataSetChanged()
    }

    /**
     * Function description.
     */
    fun enPseudoColor(pseudoColor: Boolean) {
        this.pseudoColor = pseudoColor
        notifyDataSetChanged()
    }

    /**
     * Function description.
     */
    fun enPseudoColorBar(pseudoColorBar: Boolean) {
        this.pseudoColorBar = pseudoColorBar
        notifyDataSetChanged()
    }

    /**
     * Function description.
     */
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
            holder.name.text = data.name
            holder.img.setImageResource(data.icon)
            holder.lay.setOnClickListener {
                listener?.invoke(data.code)
            }
            when (data.code) {
                1000 -> {
                    iconUI(pointColor, holder.img, holder.name)
                }
                2000 -> {
                    iconUI(pseudoColor, holder.img, holder.name)
                }
                3000 -> {
                    iconUI(pseudoColorBar, holder.img, holder.name)
                }
                4000 -> {
                    iconUI(settingColorBar, holder.img, holder.name)
                }
            }
        }
    }

    private fun iconUI(isActive: Boolean, img: ImageView, nameText: TextView) {
        img.isSelected = isActive
        if (isActive) {
            nameText.setTextColor(ContextCompat.getColor(context, LibAppR.color.white))
        } else {
            nameText.setTextColor(ContextCompat.getColor(context, LibAppR.color.font_third_color))
        }
    }

    inner class ItemView(private val binding: ItemGalleryEditMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        val lay: View = binding.itemEditMenuTabLay
        val img: ImageView = binding.itemEditMenuTabImg
        val name: TextView = binding.itemEditMenuTabText
    }

    data class IconBean(val name: String, @DrawableRes val icon: Int, val code: Int)
}