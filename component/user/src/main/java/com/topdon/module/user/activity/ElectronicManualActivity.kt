package com.topdon.module.user.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.utils.Constants
import com.topdon.module.user.R
import com.topdon.module.user.databinding.ActivityElectronicManualBinding
import com.topdon.module.user.databinding.ItemElectronicManualBinding

/**
 * 电子说明书 或 FAQ 设备类型选择页面
 */
@Route(path = RouterConfig.ELECTRONIC_MANUAL)
class ElectronicManualActivity : BaseActivity() {
    
    private lateinit var binding: ActivityElectronicManualBinding

    override fun initContentView() = R.layout.activity_electronic_manual

    override fun initView() {
        binding = ActivityElectronicManualBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val productType = intent.getIntExtra(Constants.SETTING_TYPE, 0) //0-电子说明书 1-FAQ

        binding.titleView.setTitleText(if (productType == Constants.SETTING_BOOK) R.string.electronic_manual else R.string.app_question)

        val adapter = MyAdapter(productType == 1)
        adapter.onPickListener = { isTS001 ->
            if (isTS001) {
                if (productType == Constants.SETTING_BOOK) {
                    //电子说明书-TS001
                } else {
                    //FAQ-TS001
                    ARouter.getInstance().build(RouterConfig.QUESTION).withBoolean("isTS001", true).navigation(this)
                }
            } else {
                if (productType == Constants.SETTING_BOOK) {
                    //电子说明书-TS004
                    ARouter.getInstance().build(RouterConfig.PDF).withBoolean("isTS001", false).navigation(this)
                } else {
                    //FAQ-TS004
                    ARouter.getInstance().build(RouterConfig.QUESTION).withBoolean("isTS001", false).navigation(this)
                }
            }
        }

        binding.electronicManualRecycler.layoutManager = LinearLayoutManager(this)
        binding.electronicManualRecycler.adapter = adapter
    }

    override fun initData() {

    }




    private class MyAdapter(private val isFAQ: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var onPickListener: ((isTS001: Boolean) -> Unit)? = null

        private val optionList: ArrayList<String> = ArrayList(2)

        init {
            // 由于 TC001 的说明书为旧版本 样式， 2024-4-9 产品决定先隐藏，只放 TS004 的说明书
            if (isFAQ) {
                optionList.add("TS001")
            }
            optionList.add("TS004")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ItemElectronicManualBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemViewHolder) {
                holder.binding.itemText.text = optionList[position]
                holder.binding.itemLay.setOnClickListener {
                    onPickListener?.invoke(isFAQ && position == 0)
                }
            }
        }

        override fun getItemCount(): Int = optionList.size

        private class ItemViewHolder(val binding: ItemElectronicManualBinding) : RecyclerView.ViewHolder(binding.root)
    }

}