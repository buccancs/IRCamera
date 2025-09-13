package com.topdon.menu.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.menu.util.PseudoColorConfig
import com.topdon.menu.view.ColorView

/**
 * temperature measurement模式-menu3-pseudo color/observation模式-menu4-pseudo color Adapter used for，只支持single selection.
 *
 * Created by LCG on 2024/11/12.
 */
@SuppressLint("NotifyDataSetChanged")
internal class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {
    /**
     * currentselected的pseudo colorcode.
     */
    var selectCode = -1
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    /**
     * selected变更event listener.
     * index-selectedpseudo color在list中的 index，也就 TC007 要用
     * code-pseudo colorcode，由于legacy（2D编辑的数据、savedsettings开关的pseudo color）没法改了
     * size-presetpseudo colorquantity，也就 TC007 要用
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)? = null

    /**
     * 这里的 code 来源不详，由于legacy（2D编辑的数据、savedsettings开关的pseudo color都按这个saved）没法改了
     * 1-White Hot 3-Iron Red 4-Rainbow 1 5-Rainbow 2 6-Rainbow 3 7-Red Hot 8-Hot Iron 9-Rainbow 4 10-Rainbow 5 11-Black Hot
     */
    private val colorCodeArray: IntArray = intArrayOf(1, 3, 4, 5, 6, 7, 8, 9, 10, 11)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        // 按照UI图，宽度与屏幕宽度比例为 62:375
        val width: Int = (parent.context.resources.displayMetrics.widthPixels * 62f / 375).toInt()
        val colorView = ColorView(parent.context)
        colorView.layoutParams = ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        return ViewHolder(colorView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val code: Int = colorCodeArray[position]
        holder.colorView.isSelected = code == selectCode
        holder.colorView.refreshColor(PseudoColorConfig.getColors(code), PseudoColorConfig.getPositions(code))
        holder.colorView.setOnClickListener {
            if (selectCode != code) {
                selectCode = code
                onColorListener?.invoke(position, code, colorCodeArray.size)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = colorCodeArray.size

    /**
     * ViewHolder(val class
     */
/**
 * Custom View holder view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
    class ViewHolder(val colorView: ColorView) : RecyclerView.ViewHolder(colorView)
}
