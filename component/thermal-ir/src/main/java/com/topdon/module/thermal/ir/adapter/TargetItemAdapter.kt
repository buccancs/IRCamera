package com.topdon.module.thermal.ir.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.ui.bean.ColorBean
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.csl.irCamera.libui.R as LibUiR
import com.topdon.module.thermal.ir.databinding.ItmeTargetModeBinding

class TargetItemAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /** listener property */
    var listener: ((index: Int, code: Int) -> Unit)? = null
    private var type = 0
    private var selected = -1

    /**
     * Function description.
     */
    fun selected(index: Int) {
        selected = index
        notifyDataSetChanged()
    }

    /**
     * Function description.
     */
    fun getSelected(): Int {
        return selected
    }

    private val secondBean = arrayListOf(
        ColorBean(LibUiR.drawable.ic_menu_thermal6002, "", ObserveBean.TYPE_TARGET_HORIZONTAL),
        ColorBean(LibUiR.drawable.ic_menu_thermal6001, "", ObserveBean.TYPE_TARGET_VERTICAL),
        ColorBean(LibUiR.drawable.ic_menu_thermal6003, "", ObserveBean.TYPE_TARGET_CIRCLE),
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItmeTargetModeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            val bean = secondBean[position]
            holder.binding.itemMenuTabImg.setImageResource(bean.res)
            holder.binding.itemMenuTabLay.setOnClickListener {
                listener?.invoke(position, bean.code)
                selected(bean.code)
            }
            holder.binding.itemMenuTabImg.isSelected = bean.code == selected
            holder.binding.itemMenuTabText.text = bean.name
            holder.binding.itemMenuTabText.isSelected = bean.code == selected
            holder.binding.itemMenuTabText.setTextColor(
                if (position == selected) ContextCompat.getColor(context, LibAppR.color.white)
                else ContextCompat.getColor(context, LibAppR.color.font_third_color)
            )
        }
    }

    override fun getItemCount(): Int {
        return secondBean.size
    }

    inner class ItemView(val binding: ItmeTargetModeBinding) : RecyclerView.ViewHolder(binding.root) {
//        init {
//            val canSeeCount = itemCount.toFloat() // item
//            val with = (ScreenUtils.getScreenWidth() / canSeeCount).toInt()
//            itemView.layoutParams = ViewGroup.LayoutParams((with * 0.95).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
//            val imageSize = (ScreenUtils.getScreenWidth() * 29 / 375f).toInt()
//            val layoutParams = itemView.item_menu_tab_img.layoutParams
//            layoutParams.width = imageSize
//            layoutParams.height = imageSize
//            itemView.item_menu_tab_img.layoutParams = layoutParams
//        }
    }
}