package com.topdon.house.activity

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.topdon.house.R
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.view.BaseTitleView

/**
 * 多张图片详情.
 *
 * 需要传递：
 * - [ExtraKeyConfig.CURRENT_ITEM] - 当前要查看的图片在图片列表中的 index
 * - [ExtraKeyConfig.IMAGE_PATH_LIST] - 要查看的图片在本地绝对路径列表
 *
 * Created by LCG on 2024/8/27.
 */
class ImagesDetailActivity : BaseActivity() {
    
    private lateinit var titleView: BaseTitleView
    private lateinit var viewPager2: ViewPager2
    
    override fun initContentView(): Int = R.layout.activity_images_detail

    override fun initView() {
        titleView = findViewById(R.id.title_view)
        viewPager2 = findViewById(R.id.view_pager2)
        
        val imageList: List<String> = intent.getStringArrayListExtra(ExtraKeyConfig.IMAGE_PATH_LIST) ?: return
        viewPager2.adapter = MyAdapter(imageList)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                titleView.setTitleText("${position + 1}/${imageList.size}")
            }
        })
        viewPager2.setCurrentItem(intent.getIntExtra(ExtraKeyConfig.CURRENT_ITEM, 0), false)
    }

    override fun initData() {
    }

    private class MyAdapter(private val imageList: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val imageView = ImageView(parent.context)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return ViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(holder.imageView).load(imageList[position]).into(holder.imageView)
        }

        override fun getItemCount(): Int = imageList.size

        class ViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }
}