package com.topdon.module.thermal.ir.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.repository.TC007Repository
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.ui.widget.MyItemDecoration
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.adapter.ConfigEmAdapter
import com.topdon.module.thermal.ir.bean.DataBean
import com.topdon.module.thermal.ir.bean.ModelBean
import com.topdon.module.thermal.ir.dialog.ConfigGuideDialog
import com.topdon.module.thermal.ir.dialog.IRConfigInputDialog
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.viewmodel.IRConfigViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.topdon.lib.core.R as LibR

/**
\1temperature correction（setambient temperature、temperature measurement、emissivity）
 *
\1parameter：
\1- [ExtraKeyConfig.IS_TC007] - device TC007
 */
// Legacy ARouter route annotation - now using NavigationManager
/**
 * I r config activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
class IRConfigActivity : BaseActivity(), View.OnClickListener {
    /**
\1interface， TC007 device.
\1true-TC007 false-device
     */
    private var isTC007 = false

    private val viewModel: IRConfigViewModel by viewModels()

    private lateinit var adapter: ConfigAdapter

    override fun initContentView(): Int = R.layout.activity_ir_config

    @SuppressLint("SetTextI18n")
    override fun initView() {
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        val tvDefaultTempTitle = findViewById<android.widget.TextView>(R.id.tv_default_temp_title)
        val tvDefaultDisTitle = findViewById<android.widget.TextView>(R.id.tv_default_dis_title)
        val tvDefaultEmTitle = findViewById<android.widget.TextView>(R.id.tv_default_em_title)
        val tvDefaultTempUnit = findViewById<android.widget.TextView>(R.id.tv_default_temp_unit)
        val ivDefaultSelector = findViewById<android.widget.ImageView>(R.id.iv_default_selector)
        val viewDefaultTempBg = findViewById<android.view.View>(R.id.view_default_temp_bg)
        val viewDefaultDisBg = findViewById<android.view.View>(R.id.view_default_dis_bg)
        val tvDefaultEmValue = findViewById<android.widget.TextView>(R.id.tv_default_em_value)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val tvDefaultTempValue = findViewById<android.widget.TextView>(R.id.tv_default_temp_value)
        val tvDefaultDisValue = findViewById<android.widget.TextView>(R.id.tv_default_dis_value)

        tvDefaultTempTitle.text = "${getString(LibR.string.thermal_config_environment)} ${UnitTools.showConfigC(-10, if (isTC007) 50 else 55)}"
        tvDefaultDisTitle.text = "${getString(LibR.string.thermal_config_distance)} (0.2~${if (isTC007) 4 else 5}m)"
        tvDefaultEmTitle.text = "${getString(LibR.string.thermal_config_radiation)} (${if (isTC007) "0.1" else "0.01"}~1.00)"
        tvDefaultTempUnit.text = UnitTools.showUnit()

        ivDefaultSelector.setOnClickListener(this)
        viewDefaultTempBg.setOnClickListener(this)
        viewDefaultDisBg.setOnClickListener(this)
        tvDefaultEmValue.setOnClickListener(this)

        adapter = ConfigAdapter(this, isTC007)
        adapter.onSelectListener = {
            viewModel.checkConfig(isTC007, it)
        }
        adapter.onDeleteListener = {
            TipDialog.Builder(this)
                .setMessage(getString(LibR.string.tip_config_delete, "${getString(LibR.string.thermal_custom_mode)}${it.name}"Test Data"NotifyDataSetChanged")
        fun refresh(newList: List<DataBean>) {
            dataList.clear()
            dataList.addAll(newList)
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int {
            return if (position < dataList.size) 0 else 1
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): RecyclerView.ViewHolder {
            return if (viewType == 0) {
                ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ir_config_config, parent, false))
            } else {
                FootViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ir_config_foot, parent, false))
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
        ) {
            if (holder is ItemViewHolder) {
                val dataBean = dataList[position]
                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_name).text = "${context.getString(LibR.string.thermal_custom_mode)}${dataBean.name}"
                holder.itemView.findViewById<android.widget.ImageView>(R.id.iv_selector).isSelected = dataBean.use

                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_temp_title).text = "${context.getString(LibR.string.thermal_config_environment)} ${UnitTools.showConfigC(-10, if (isTC007) 50 else 55)}"
                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_dis_title).text = "${context.getString(LibR.string.thermal_config_distance)} (0.2~${if (isTC007) 4 else 5}m)"
                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_em_title).text = "${context.getString(LibR.string.thermal_config_radiation)} (${if (isTC007) "0.1" else "0.01"}~1.00)"
                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_temp_unit).text = UnitTools.showUnit()

                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_temp_value).text = NumberTools.to02(UnitTools.showUnitValue(dataBean.environment))
                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_dis_value).text = NumberTools.to02(dataBean.distance)
                holder.itemView.findViewById<android.widget.TextView>(R.id.tv_em_value).text = NumberTools.to02(dataBean.radiation)
            } else if (holder is FootViewHolder) {
                holder.itemView.findViewById<android.widget.TextView>(
                    R.id.tv_add,
                ).setTextColor(if (dataList.size >= 10) 0x80ffffff.toInt() else 0xccffffff.toInt())
            }
        }

        override fun getItemCount(): Int = dataList.size + 1

        inner class ItemViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
            init {
                rootView.findViewById<android.widget.ImageView>(R.id.iv_selector).setOnClickListener {
                    val position: Int = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onSelectListener?.invoke(dataList[position].id)
                    }
                }
                rootView.findViewById<android.widget.ImageView>(R.id.iv_del).setOnClickListener {
                    val position: Int = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onDeleteListener?.invoke(dataList[position])
                    }
                }
                rootView.findViewById<android.view.View>(R.id.view_temp_bg).setOnClickListener {
                    val position: Int = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        IRConfigInputDialog(context, IRConfigInputDialog.Type.TEMP, isTC007)
                            .setInput(UnitTools.showUnitValue(dataList[position].environment))
                            .setConfirmListener {
                                itemView.findViewById<android.widget.TextView>(R.id.tv_temp_value).text = NumberTools.to02(UnitTools.showToCValue(it))
                                dataList[position].environment = UnitTools.showToCValue(it)
                                onUpdateListener?.invoke(dataList[position])
                            }
                            .show()
                    }
                }
                rootView.findViewById<android.view.View>(R.id.view_dis_bg).setOnClickListener {
                    val position: Int = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        IRConfigInputDialog(context, IRConfigInputDialog.Type.DIS, isTC007)
                            .setInput(dataList[position].distance)
                            .setConfirmListener {
                                itemView.findViewById<android.widget.TextView>(R.id.tv_dis_value).text = it.toString()
                                dataList[position].distance = it
                                onUpdateListener?.invoke(dataList[position])
                            }
                            .show()
                    }
                }
                rootView.findViewById<android.widget.TextView>(R.id.tv_em_value).setOnClickListener {
                    val position: Int = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        IRConfigInputDialog(context, IRConfigInputDialog.Type.EM, isTC007)
                            .setInput(dataList[position].radiation)
                            .setConfirmListener {
                                itemView.findViewById<android.widget.TextView>(R.id.tv_em_value).text = it.toString()
                                dataList[position].radiation = it
                                onUpdateListener?.invoke(dataList[position])
                            }
                            .show()
                    }
                }
            }
        }

        inner class FootViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
            init {
                rootView.findViewById<android.view.View>(R.id.view_add).setOnClickListener {
                    if (dataList.size < 10) {
                        val position: Int = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            onAddListener?.onClick(it)
                        }
                    } else {
                        TToast.shortToast(context, com.topdon.lib.core.R.string.config_add_tip)
                    }
                }
                rootView.findViewById<TextView>(R.id.tv_all_emissivity).setOnClickListener {
                    context.startActivity(Intent(context, IREmissivityActivity::class.java))
                }
            }
        }
    }
}
