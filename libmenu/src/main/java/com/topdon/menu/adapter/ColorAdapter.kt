package com.topdon.menu.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.menu.view.ColorView
import com.topdon.menu.util.PseudoColorConfig

/**
 * Adapter used for Temperature measurement mode - Menu 3 - Pseudo color / Observation mode - Menu 4 - Pseudo color, supports single selection only.
 *
 * Created by LCG on 2024/11/12.
 */
@SuppressLint("NotifyDataSetChanged")
internal class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    /**
     * Currently selected pseudo color code.
     */
    var selectCode = -1
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }
    /**
     * Selection change event listener.
     * index - Selected pseudo color index in the list, used by TC007
     * code - Pseudo color code, cannot be changed due to legacy reasons (2D editing data, saved settings pseudo color)
     * size - Number of preset pseudo colors, used by TC007
     */
    var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)? = null


    /**
     * 这里的 code 来源不详，由于历史遗留（2D编辑的数据、保存设置开关的伪彩都按这个保存）没法改了
     * 1-白热 3-铁红 4-彩虹1 5-彩虹2 6-彩虹3 7-红热 8-热铁 9-彩虹4 10-彩虹5 11-黑热
     */
    private val colorCodeArray: IntArray = intArrayOf(1, 3, 4, 5, 6, 7, 8, 9, 10, 11)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //按照UI图，宽度与屏幕宽度比例为 62:375
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