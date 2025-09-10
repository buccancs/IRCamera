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
     * item code item，itemHistorical legacy（2Ditem、itemSettingsitemPseudo-coloritem）item
     * 1-White hot 3-Iron red 4-Rainbow1 5-Rainbow2 6-Rainbow3 7-Red hot 8-Hot iron 9-Rainbow4 10-Rainbow5 11-Black hot
     */
    private val colorCodeArray: IntArray = intArrayOf(1, 3, 4, 5, 6, 7, 8, 9, 10, 11)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Adapter itemUIitem，item 62:375
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