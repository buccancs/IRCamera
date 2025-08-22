package com.topdon.tc001
import com.topdon.tc001.R

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.DeviceTools

class DeviceTypeActivity : BaseActivity() {

    private var clientType: IRDeviceType? = null

    override fun initContentView(): Int = R.layout.activity_device_type

    override fun initView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(this).apply {
            onItemClickListener = {
                clientType = it
                // Only TC001 is supported
                val intent = Intent(this@DeviceTypeActivity, com.topdon.module.thermal.ir.activity.IRMainActivity::class.java)
                intent.putExtra(ExtraKeyConfig.IS_TC007, false) // Always false since we only support TC001
                startActivity(intent)
                if (DeviceTools.isConnect()) {
                    finish()
                }
            }
        }
    }

    override fun initData() {
    }

    override fun connected() {
        if (clientType?.isLine() == true) {
            finish()
        }
    }

    override fun onSocketConnected(isTS004: Boolean) {
        // TC007 and TS004 support removed - only handle TC001/TS001 connections
        if (clientType?.isLine() == true) {
            finish()
        }
    }

    private class MyAdapter(val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        var onItemClickListener: ((type: IRDeviceType) -> Unit)? = null

        private data class ItemInfo(val isTitle:Boolean, val firstType: IRDeviceType, val secondType: IRDeviceType?)

        private val dataList: ArrayList<ItemInfo> = arrayListOf(
            ItemInfo(true, IRDeviceType.TC001, null),
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device_type, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val firstType: IRDeviceType = dataList[position].firstType
            val tvTitle = holder.itemView.findViewById<TextView>(R.id.tv_title)
            val tvItem1 = holder.itemView.findViewById<TextView>(R.id.tv_item1)
            val ivItem1 = holder.itemView.findViewById<ImageView>(R.id.iv_item1)
            val groupItem2 = holder.itemView.findViewById<View>(R.id.group_item2)
            
            tvTitle.isVisible = dataList[position].isTitle
            tvTitle.text = context.getString(R.string.tc_connect_line)

            tvItem1.text = firstType.getDeviceName()
            ivItem1.setImageResource(R.drawable.ic_device_type_tc001)

            groupItem2.isVisible = false // No second device option
        }

        override fun getItemCount(): Int = dataList.size

        inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
            init {
                val viewBgItem1 = rootView.findViewById<View>(R.id.view_bg_item1)
                
                viewBgItem1.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.invoke(dataList[position].firstType)
                    }
                }
            }
        }
    }

    enum class IRDeviceType {
        TC001 {
            override fun isLine(): Boolean = true
            override fun getDeviceName(): String = "TC001"
        };

        abstract fun isLine(): Boolean
        abstract fun getDeviceName(): String
    }
}