package com.topdon.module.user.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.utils.Constants
import com.topdon.module.user.R
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.topdon.lib.core.view.BaseTitleView

/**
 * 电子说明书 或 FAQ 设备类型选择页面
 */
class ElectronicManualActivity : BaseActivity() {

    private lateinit var titleView: BaseTitleView
    private lateinit var electronicManualRecycler: RecyclerView

    override fun initContentView() = R.layout.activity_electronic_manual

    override fun initView() {
        titleView = findViewById(R.id.title_view)
        electronicManualRecycler = findViewById(R.id.electronic_manual_recycler)
        
        val productType = intent.getIntExtra(Constants.SETTING_TYPE, 0) //0-电子说明书 1-FAQ

        titleView.setTitleText(if (productType == Constants.SETTING_BOOK) R.string.electronic_manual else R.string.app_question)

        val adapter = MyAdapter(productType == 1)
        adapter.onPickListener = { isTS001 ->
            if (isTS001) {
                if (productType == Constants.SETTING_BOOK) {
                    //电子说明书-TS001
                } else {
                    //FAQ-TS001
            // TODO: Replace RouterConfig reference with direct navigation
                }
            } else {
                if (productType == Constants.SETTING_BOOK) {
                    //电子说明书-TS004
            // TODO: Replace RouterConfig reference with direct navigation
                } else {
                    //FAQ-TS004
            // TODO: Replace RouterConfig reference with direct navigation
                }
            }
        }

        electronicManualRecycler.layoutManager = LinearLayoutManager(this)
        electronicManualRecycler.adapter = adapter
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
            return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_electronic_manual, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemViewHolder) {
                val itemText = holder.rootView.findViewById<TextView>(R.id.item_text)
                val itemLay = holder.rootView.findViewById<ConstraintLayout>(R.id.item_lay)
                
                itemText.text = optionList[position]
                itemLay.setOnClickListener {
                    onPickListener?.invoke(isFAQ && position == 0)
                }
            }
        }

        override fun getItemCount(): Int = optionList.size

        private class ItemViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView)
    }

}