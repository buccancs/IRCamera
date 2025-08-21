package com.topdon.module.user.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.view.TitleView
import com.topdon.tc001.R

class ElectronicManualActivity : BaseActivity() {

    override fun initContentView() = R.layout.activity_electronic_manual

    override fun initView() {
        val productType = intent.getIntExtra(Constants.SETTING_TYPE, 0) //0-电子说明书 1-FAQ
        val titleView = findViewById<TitleView>(R.id.title_view)
        val electronicManualRecycler = findViewById<RecyclerView>(R.id.electronic_manual_recycler)

        titleView.setTitleText(if (productType == Constants.SETTING_BOOK) R.string.electronic_manual else R.string.app_question)

        val adapter = MyAdapter(productType == 1)
        adapter.onPickListener = { isTS001 ->
            if (isTS001) {
                if (productType == Constants.SETTING_BOOK) {
                } else {
                    val intent = Intent(this, com.topdon.module.user.activity.QuestionActivity::class.java)
                    intent.putExtra("isTS001", true)
                    startActivity(intent)
                }
            } else {
                if (productType == Constants.SETTING_BOOK) {
                    val intent = Intent(this, com.topdon.tc001.PdfActivity::class.java)
                    intent.putExtra("isTS001", false)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, com.topdon.module.user.activity.QuestionActivity::class.java)
                    intent.putExtra("isTS001", false)
                    startActivity(intent)
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
            if (isFAQ) {
                optionList.add("TS001")
            }
            // TS004 support removed
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_electronic_manual, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemViewHolder) {
                val itemText = holder.rootView.findViewById<TextView>(R.id.item_text)
                val itemLay = holder.rootView.findViewById<View>(R.id.item_lay)
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