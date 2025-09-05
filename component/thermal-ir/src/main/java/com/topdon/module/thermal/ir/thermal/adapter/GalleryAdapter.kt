package com.topdon.module.thermal.ir.thermal.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.databinding.ItemGalleryBinding

class GalleryAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener: OnItemClickListener? = null

    var datas = arrayListOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemView(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            GlideLoader.load(holder.binding.itemGalleryImg, datas[position])
            holder.binding.itemGalleryLay.setOnClickListener {
                Log.w("123", "文件: ${datas[position]}")
                listener?.onClick(position, datas[position])
            }
            holder.binding.itemGalleryLay.setOnLongClickListener(View.OnLongClickListener {
                Log.w("123", "文件: ${datas[position]}")
                listener?.onLongClick(position, datas[position])
                return@OnLongClickListener true
            })
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ItemView(val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root)


    interface OnItemClickListener {
        fun onClick(index: Int, path: String)
        fun onLongClick(index: Int, path: String)
    }


}