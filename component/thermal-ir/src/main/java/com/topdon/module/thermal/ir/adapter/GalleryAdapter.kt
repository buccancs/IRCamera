package com.topdon.module.thermal.ir.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.bean.GalleryBean
import com.topdon.lib.core.bean.GalleryTitle
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.lib.core.tools.TimeTool
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.databinding.ItemGalleryHeadLayBinding
import com.topdon.module.thermal.ir.databinding.ItemGalleryLayBinding

@SuppressLint("NotifyDataSetChanged")
class GalleryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEAD = 0
        private const val TYPE_DATA = 1
    }

     * item.
    /** dataList property */
    val dataList: ArrayList<GalleryBean> = ArrayList()

     * position .
    /** selectList property */
    val selectList: ArrayList<Int> = ArrayList()

     *  TS004 .
    /** isTS004Remote property */
    var isTS004Remote = false
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

     * .
    /** isEditMode property */
    var isEditMode = false
        set(value) {
            if (field != value) {
                field = value
                if (!value) {
                    selectList.clear()
                    selectCallback?.invoke(selectList)
                }
                notifyDataSetChanged()
            }
        }


     *  item .
    /** onLongEditListener property */
    var onLongEditListener: (() -> Unit)? = null
     * .
     * data  item position
    /** selectCallback property */
    var selectCallback: ((data: ArrayList<Int>) -> Unit)? = null
     * item .
    /** itemClickCallback property */
    var itemClickCallback: ((position: Int) -> Unit)? = null


    /**
     * Function description.
     */
    fun refreshList(newList: List<GalleryBean>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * Function description.
     */
    fun buildSelectList(): ArrayList<GalleryBean> {
        val resultList: ArrayList<GalleryBean> = ArrayList()
        selectList.forEach {
            resultList.add(dataList[it])
        }
        return resultList
    }

    /**
     * Function description.
     */
    fun selectAll() {
        var dataCount = 0
        dataList.forEach {
            if (it !is GalleryTitle) {
                dataCount++
            }
        }
        if (selectList.size >= dataCount) {
            selectList.clear()
        } else {
            selectList.clear()
            for (i in 0 until dataList.size) {
                if (dataList[i] !is GalleryTitle) {
                    selectList.add(i)
                }
            }
        }
        selectCallback?.invoke(selectList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position] is GalleryTitle) {
            TYPE_HEAD
        } else {
            TYPE_DATA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEAD) {
            val binding = ItemGalleryHeadLayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemHeadView(binding)
        } else {
            val binding = ItemGalleryLayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemView(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]
        if (holder is ItemView) {
            GlideLoader.load(holder.binding.itemGalleryImg, data.thumb)
            if (data.name.uppercase().endsWith(".MP4")) {
                holder.binding.itemGalleryText.text = TimeTool.showVideoTime(data.duration)
                holder.binding.ivVideoTime.isVisible = true
            } else {
                holder.binding.itemGalleryText.text = ""
                holder.binding.ivVideoTime.isVisible = false
            }

            holder.binding.ivHasDownload.isVisible = isTS004Remote && data.hasDownload

            holder.binding.ivCheck.isVisible = isEditMode
            holder.binding.ivCheck.isSelected = selectList.contains(position)

            holder.binding.itemGalleryImg.setOnClickListener {
                if (isEditMode) {
                    if (selectList.contains(position)) {
                        selectList.remove(position)
                    } else {
                        selectList.add(position)
                    }
                    selectCallback?.invoke(selectList)

                    holder.binding.ivCheck.isSelected = selectList.contains(position)
                } else {
                    itemClickCallback?.invoke(position)
                }
            }
            holder.binding.itemGalleryImg.setOnLongClickListener {
                if (!isEditMode) {
                    selectList.add(position)
                    selectCallback?.invoke(selectList)
                    holder.binding.ivCheck.isVisible = true
                    holder.binding.ivCheck.isSelected = true
                    isEditMode = true
                    onLongEditListener?.invoke()
                }
                return@setOnLongClickListener true
            }
        } else if (holder is ItemHeadView) {
            holder.binding.itemGalleryHeadText.text = TimeTool.showDateType(data.timeMillis, 4)
            holder.binding.itemGalleryHeadText.setTextColor(0x80ffffff.toInt())
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ItemHeadView(val binding: ItemGalleryHeadLayBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ItemView(val binding: ItemGalleryLayBinding) : RecyclerView.ViewHolder(binding.root)


}