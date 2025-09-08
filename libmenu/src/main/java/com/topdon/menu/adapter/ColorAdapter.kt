package com.topdon.menu.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.menu.view.ColorView
import com.topdon.menu.util.PseudoColorConfig

/**
 * Temperature measurement mode-menu3-Pseudo color/Observation mode-menu4-Pseudo color used by Adapter, [Chinese text].
 *
 * Created by LCG on 2024/11/12.
 */
@SuppressLint("NotifyDataSetChanged")
internal class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    /**
     * [Chinese text]in progress[Chinese text]Pseudo color[Chinese text].
     */
    var selectCode = -1
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }
    /**
     * [Chinese text]in progress[Chinese text]eventlistener.
     * index-[Chinese text]in progressPseudo color[Chinese text]in progress[Chinese text] index, [Chinese text] TC007 [Chinese text]
     * code-Pseudo color[Chinese text], [Chinese text](2D[Chinese text], [Chinese text]Settings[Chinese text]Pseudo color)[Chinese text]
     * size-[Chinese text]Pseudo color[Chinese text], [Chinese text] TC007 [Chinese text]
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)? = null

    /**
     * [Chinese text] code [Chinese text], [Chinese text](2D[Chinese text], [Chinese text]Settings[Chinese text]Pseudo color[Chinese text])[Chinese text]
     * 1-[Chinese text] 3-[Chinese text] 4-[Chinese text]1 5-[Chinese text]2 6-[Chinese text]3 7-[Chinese text] 8-[Chinese text] 9-[Chinese text]4 10-[Chinese text]5 11-[Chinese text]
     */
    private val colorCodeArray: IntArray = intArrayOf(1, 3, 4, 5, 6, 7, 8, 9, 10, 11)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // [Chinese text]UI[Chinese text], [Chinese text] 62:375
        val width: Int = (parent.context.resources.displayMetrics.widthPixels * 62f / 375).toInt()
        val colorView = ColorView(parent.context)
        colorView.layoutParams = ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        return ViewHolder(colorView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

    class ViewHolder(val colorView: ColorView) : RecyclerView.ViewHolder(colorView)

}