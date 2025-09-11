package com.topdon.menu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.topdon.menu.databinding.ItemMenuBinding

/**
 * Base adapter for menu items with common layout logic
 */
internal abstract class BaseMenuAdapter : RecyclerView.Adapter<BaseMenuAdapter.ViewHolder>() {
    
    private companion object {
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_FIRST = 1
        private const val VIEW_TYPE_LAST = 2
        
        // UI design proportions
        private const val ICON_SIZE_RATIO = 62f / 375f
        private const val BIG_MARGIN_RATIO = 24f / 375f
        private const val SMALL_MARGIN_RATIO = 8f / 375f
        private const val MAX_EQUAL_WIDTH_ITEMS = 4
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> VIEW_TYPE_FIRST
        itemCount - 1 -> VIEW_TYPE_LAST
        else -> VIEW_TYPE_DEFAULT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val screenWidth = parent.context.resources.displayMetrics.widthPixels
        
        setupIconSize(binding, screenWidth)
        setupItemLayout(binding, viewType, screenWidth)
        
        return ViewHolder(binding)
    }
    
    private fun setupIconSize(binding: ItemMenuBinding, screenWidth: Int) {
        val iconSize = (screenWidth * ICON_SIZE_RATIO).toInt()
        binding.ivIcon.layoutParams.apply {
            width = iconSize
            height = iconSize
        }
    }
    
    private fun setupItemLayout(binding: ItemMenuBinding, viewType: Int, screenWidth: Int) {
        if (itemCount <= MAX_EQUAL_WIDTH_ITEMS) {
            binding.root.layoutParams.width = (screenWidth / itemCount.toFloat()).toInt()
        } else {
            val bigMargin = (screenWidth * BIG_MARGIN_RATIO).toInt()
            val smallMargin = (screenWidth * SMALL_MARGIN_RATIO).toInt()
            
            val (left, right) = when (viewType) {
                VIEW_TYPE_FIRST -> bigMargin to smallMargin
                VIEW_TYPE_LAST -> smallMargin to bigMargin
                else -> smallMargin to smallMargin
            }
            binding.root.setPadding(left, 0, right, 0)
        }
    }
        }
        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)
}
