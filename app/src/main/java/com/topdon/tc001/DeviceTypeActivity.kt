package com.topdon.tc001

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.tc001.databinding.ActivityDeviceTypeBinding
import com.topdon.tc001.databinding.ItemDeviceTypeBinding

/**
 * 设备类型选择.
 *
 * Created by LCG on 2024/4/22.
 */
class DeviceTypeActivity : BaseActivity() {

    private lateinit var binding: ActivityDeviceTypeBinding

    /**
     * 当前点击的设备类型.
     */
    private var clientType: IRDeviceType? = null

    override fun initContentView(): Int = R.layout.activity_device_type

    override fun initView() {
        binding = ActivityDeviceTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MyAdapter(this).apply {
            onItemClickListener = {
                clientType = it
                when (it) {
                    else -> {
                        ARouter.getInstance()
                            .build(RouterConfig.IR_MAIN)
                            .withBoolean(ExtraKeyConfig.IS_TC007, false)
                            .navigation(this@DeviceTypeActivity)
                        if (DeviceTools.isConnect()) {
                            finish()
                        }
                    }
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
        // Only TC001 is supported, which is a line device, so no socket connection handling needed
    }

    private class MyAdapter(val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        var onItemClickListener: ((type: IRDeviceType) -> Unit)? = null

        private data class ItemInfo(val isTitle:Boolean, val firstType: IRDeviceType, val secondType: IRDeviceType?)

        private val dataList: ArrayList<ItemInfo> = arrayListOf(
            ItemInfo(true, IRDeviceType.TC001, null),
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemDeviceTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val firstType: IRDeviceType = dataList[position].firstType
            val secondType: IRDeviceType? = dataList[position].secondType
            holder.binding.tvTitle.isVisible = dataList[position].isTitle
            holder.binding.tvTitle.text = context.getString(if (firstType.isLine()) R.string.tc_connect_line else R.string.tc_connect_wifi)

            holder.binding.tvItem1.text = firstType.getDeviceName()
            when (firstType) {
                IRDeviceType.TC001 -> holder.binding.ivItem1.setImageResource(R.drawable.ic_device_type_tc001)
            }

            holder.binding.groupItem2.isVisible = secondType != null
            if (secondType != null) {
                holder.binding.tvItem2.text = secondType.getDeviceName()
                when (secondType) {
                    IRDeviceType.TC001 -> holder.binding.ivItem2.setImageResource(R.drawable.ic_device_type_tc001)
                }
            }
        }

        override fun getItemCount(): Int = dataList.size

        inner class ViewHolder(val binding: ItemDeviceTypeBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.viewBgItem1.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener?.invoke(dataList[position].firstType)
                    }
                }
                binding.viewBgItem2.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val irDeviceType: IRDeviceType = dataList[position].secondType ?: return@setOnClickListener
                        onItemClickListener?.invoke(irDeviceType)
                    }
                }
            }
        }
    }

    /**
     * 支持的热成像设备类型.
     */
    enum class IRDeviceType {
        TC001 {
            override fun isLine(): Boolean = true
            override fun getDeviceName(): String = "TC001"
        };

        abstract fun isLine(): Boolean
        abstract fun getDeviceName(): String
    }
}