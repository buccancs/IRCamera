package com.topdon.module.thermal.ir.thermal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.databinding.ItemSettingTimeBinding

/**
 * 设置时间
 */
class SettingTimeAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var datas = arrayOf("秒", "分", "时", "天")
    private var dataTimes = arrayOf(1, 2, 3, 4)

    var listener: OnItemClickListener? = null
    var select = 0

    fun setCheck(index: Int) {
        this.select = index
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemSettingTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            holder.btn.text = datas[position]
            if (position == select) {
                holder.btn.setBackgroundResource(LibAppR.drawable.ui_btn_round_theme)
                holder.btn.setTextColor(ContextCompat.getColor(context, LibAppR.color.white))
            } else {
                holder.btn.background = null
                holder.btn.setTextColor(ContextCompat.getColor(context, LibAppR.color.font_gray))
            }
            holder.btn.setOnClickListener {
                listener?.onClick(position, dataTimes[position])
                setCheck(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ItemView(private val binding: ItemSettingTimeBinding) : RecyclerView.ViewHolder(binding.root) {
        val btn: Button = binding.itemSettingTimeBtn
    }


    interface OnItemClickListener {
        fun onClick(index: Int, time: Int)
    }


}