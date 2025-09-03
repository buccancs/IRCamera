package com.topdon.lib.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.core.R
import com.topdon.menu.R as MenuR
import com.topdon.lib.ui.bean.ColorSelectBean
// import kotlinx.android.synthetic.  // TODO: Replace with ViewBindingmain.ui_item_color_select.view.*

class ColorSelectAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener: ((code: Int, color: Int) -> Unit)? = null
    private var type = 0
    private var selected = -1

    fun selected(index: Int) {
        selected = index
        notifyDataSetChanged()
    }

    private val colorBean = arrayListOf(
        ColorSelectBean(UiR.color.color_select1, "#FF000000", 1),
        ColorSelectBean(UiR.color.color_select2, "#FFFFFFFF", 2),
        ColorSelectBean(UiR.color.color_select3, "#FF2B79D8", 3),
        ColorSelectBean(UiR.color.color_select4, "#FFFF0000", 4),
        ColorSelectBean(UiR.color.color_select5, "#FF0FA752", 5),
        ColorSelectBean(UiR.color.color_select6, "#FF808080", 6),
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(UiR.layout.ui_item_color_select, parent, false)
        return ItemView(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemView) {
            holder.img.setImageResource(colorBean[position].colorRes)
            holder.lay.setOnClickListener {
                listener?.invoke(position, Color.parseColor(colorBean[position].color))
                selected(position)
            }
            holder.img.isSelected = position == selected
            if (position == selected) {
                holder.checkImg.visibility = View.VISIBLE
            } else {
                holder.checkImg.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return colorBean.size
    }

    inner class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lay: View = itemView.findViewById(UiR.id.item_color_lay)
        val img: ImageView = itemView.findViewById(UiR.id.item_color_img)
        val checkImg: ImageView = itemView.findViewById(UiR.id.item_color_check)
    }


}