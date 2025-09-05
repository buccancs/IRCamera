package com.topdon.module.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.topdon.module.user.R
import com.topdon.module.user.databinding.ItemLanguageBinding

class LanguageAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener: ItemOnClickListener? = null

    private var selectIndex = 0
    private var languages: Array<out CharSequence> = context.resources.getTextArray(R.array.setting_language_list)

    fun setSelect(index: Int) {
        selectIndex = index
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            if (position == selectIndex) {
                holder.binding.itemLanguageImg.visibility = View.VISIBLE
            } else {
                holder.binding.itemLanguageImg.visibility = View.INVISIBLE
            }
            holder.binding.itemLanguageText.text = languages[position]
            holder.binding.itemLanguageLay.setOnClickListener {
                listener?.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    inner class ItemViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    interface ItemOnClickListener {
        fun onClick(position: Int)
    }
}