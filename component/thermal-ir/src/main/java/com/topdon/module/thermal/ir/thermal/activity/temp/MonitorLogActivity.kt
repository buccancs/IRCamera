package com.topdon.module.thermal.ir.thermal.activity

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.R
import com.topdon.module.thermal.databinding.ActivityMonitorLogBinding
import com.topdon.module.thermal.ir.thermal.adapter.MonitorLogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//监控记录
@Route(path = RouterConfig.THERMAL_LOG)
class MonitorLogActivity : BaseActivity() {

    private lateinit var binding: ActivityMonitorLogBinding
    val adapter: MonitorLogAdapter by lazy { MonitorLogAdapter(this) }

    override fun initContentView(): Int {
        binding = ActivityMonitorLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun initView() {
        binding.logRecycler.layoutManager = LinearLayoutManager(this)
        binding.logRecycler.adapter = adapter
        adapter.listener = object : MonitorLogAdapter.OnItemClickListener {
            override fun onClick(index: Int, thermalId: String) {
                ARouter.getInstance().build(RouterConfig.THERMAL_LOG_CHART).navigation(baseContext)
            }

            override fun onLongClick(index: Int, thermalId: String) {

            }

        }
    }

    override fun initData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val datas = AppDatabase.getInstance(baseContext).thermalDao()
                .getAllThermalByDate(SharedManager.getUserId())
            adapter.datas = datas as ArrayList<ThermalEntity>
        }

    }
}