package com.topdon.lib.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.R
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.ui.bean.ColorBean
import com.topdon.lib.ui.R as UiR
import com.topdon.menu.R as MenuR

@Deprecated("[Chinese text]High/Low temperature sourcemenu, [Chinese text]")
class MenuAIAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * [Chinese text]in progress[Chinese text] code.
     *
     * [Chinese text]([Chinese text] SharedPreferences in progress), [Chinese text] code [Chinese text]
     * - [Chinese text]in progress: -1
     * - [Chinese text]: 0
     * - high[Chinese text]: 1
     * - low[Chinese text]: 2
     */
    var selectCode: Int = -1
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    /**
     * Observation mode-menu2-High/Low temperature source point[Chinese text]eventlistener, [Chinese text]. 
     */
    var onTempSourceListener: ((code: Int) -> Unit)? = null

    private val secondBean =
        arrayListOf(
            ColorBean(
                MenuR.drawable.selector_menu2_source_1_auto,
                context.getString(R.string.main_tab_second_dynamic_recognition),
                ObserveBean.TYPE_DYN_R,
            ),
            ColorBean(
                MenuR.drawable.selector_menu2_source_2_high,
                context.getString(R.string.main_tab_second_high_temperature_source),
                ObserveBean.TYPE_TMP_H_S,
            ),
            ColorBean(
                MenuR.drawable.selector_menu2_source_3_low,
                context.getString(R.string.main_tab_second_low_temperature_source),
                ObserveBean.TYPE_TMP_L_S,
            ),
        )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(UiR.layout.ui_item_menu_second_view, parent, false)
        return ItemView(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ItemView) {
            holder.img.setImageResource(secondBean[position].res)
            holder.lay.setOnClickListener {
                selectCode = secondBean[position].code
                onTempSourceListener?.invoke(secondBean[position].code)
            }
            holder.img.isSelected = secondBean[position].code == selectCode
            holder.name.text = secondBean[position].name
            holder.name.isSelected = secondBean[position].code == selectCode
            holder.name.setTextColor(
                if (secondBean[position].code == selectCode) {
                    ContextCompat.getColor(context, UiR.color.white)
                } else {
                    ContextCompat.getColor(context, UiR.color.font_third_color)
                },
            )
        }
    }

    override fun getItemCount(): Int {
        return secondBean.size
    }

    inner class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        init {
//            val canSeeCount = itemCount.toFloat() // [Chinese text]visible[Chinese text] item [Chinese text], [Chinese text]
//            val with = (ScreenUtils.getScreenWidth() / canSeeCount).toInt()
//            itemView.layoutParams = ViewGroup.LayoutParams(with, ViewGroup.LayoutParams.WRAP_CONTENT)
//            val imageSize = (ScreenUtils.getScreenWidth() * 62 / 375f).toInt()
//            val layoutParams = itemView.item_menu_tab_img.layoutParams
//            layoutParams.width = imageSize
//            layoutParams.height = imageSize
//            itemView.item_menu_tab_img.layoutParams = layoutParams
//        }
        val lay: View = itemView.findViewById(UiR.id.item_menu_tab_lay)
        val img: ImageView = itemView.findViewById(UiR.id.item_menu_tab_img)
        val name: TextView = itemView.findViewById(UiR.id.item_menu_tab_text)
    }
}
