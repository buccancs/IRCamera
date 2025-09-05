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
import com.topdon.module.thermal.R
import com.topdon.module.thermal.databinding.ItmeTargetModeBinding

class MeasureItemAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listener: ((index: Int, code: Int) -> Unit)? = null
    private var type = 0
    private var selected = -1

    fun selected(index: Int) {
        selected = index
        notifyDataSetChanged()
    }

    private val secondBean = arrayListOf(
        ColorBean(R.drawable.ic_menu_thermal7001, "1.8m", ObserveBean.TYPE_MEASURE_PERSON),
        ColorBean(R.drawable.ic_menu_thermal7002, "1.0m", ObserveBean.TYPE_MEASURE_SHEEP),
        ColorBean(R.drawable.ic_menu_thermal7003, "0.5m", ObserveBean.TYPE_MEASURE_DOG),
        ColorBean(R.drawable.ic_menu_thermal7004, "0.2m", ObserveBean.TYPE_MEASURE_BIRD),
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
            holder.binding.itemMenuTabText.visibility = View.VISIBLE
            holder.binding.itemMenuTabText.text = bean.name
            holder.binding.itemMenuTabText.isSelected = bean.code == selected
            holder.binding.itemMenuTabText.setTextColor(ContextCompat.getColor(context, R.color.white)
//               if (position == selected) ContextCompat.getColor(context, R.color.white)
//                else ContextCompat.getColor(context, R.color.font_third_color)
            )
        }
    }

    override fun getItemCount(): Int {
        return secondBean.size
    }

    inner class ItemView(val binding: ItmeTargetModeBinding) : RecyclerView.ViewHolder(binding.root)
}