package com.topdon.module.thermal.ir.thermal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.lib.core.tools.TimeTool
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.databinding.ItemLogBinding

class MonitorLogAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /** listener property */
    var listener: OnItemClickListener? = null

    /** datas property */
    var datas = arrayListOf<ThermalEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            val data = datas[position]
            holder.binding.itemLogIndexText.text = "${position + 1}"
            holder.binding.itemLogTimeText.text = TimeTool.showTimeSecond(data.createTime)
            holder.binding.itemLogLay.setOnClickListener {
                listener?.onClick(position, data.thermalId)
            }
            holder.binding.itemLogLay.setOnLongClickListener {
                listener?.onLongClick(position, data.thermalId)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ItemView(val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root)


    interface OnItemClickListener {
        fun onClick(index: Int, thermalId: String)
        fun onLongClick(index: Int, thermalId: String)
    }


}