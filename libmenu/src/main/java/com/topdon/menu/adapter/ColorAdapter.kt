package com.topdon.menu.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.menu.util.PseudoColorConfig
import com.topdon.menu.view.ColorView

/**
 * Comment removed (contained Chinese characters)
 *
 * Created by LCG on 2024/11/12.
 */
@SuppressLint("NotifyDataSetChanged")
internal class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {
 /**
 * Comment removed (contained Chinese characters)
 */
 var selectCode = -1
 set(value) {
 if (field != value) {
 field = value
 notifyDataSetChanged()
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 var onColorListener: ((index: Int, code: Int, size: Int) -> Unit)? = null

 /**
 * Comment removed (contained Chinese characters)
 * 1-White Hot 3-Iron Red 4-Rainbow 1 5-Rainbow 2 6-Rainbow 3 7-Red Hot 8-Hot Iron 9-Rainbow 4 10-Rainbow 5 11-Black Hot
 */
 private val colorCodeArray: IntArray = intArrayOf(1, 3, 4, 5, 6, 7, 8, 9, 10, 11)

 override fun onCreateViewHolder(
 parent: ViewGroup,
 viewType: Int,
 ): ViewHolder {
 // Comment removed (contained Chinese characters)
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
