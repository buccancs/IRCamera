//package com.topdon.module.thermal.activity
//
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.topdon.lib.core.common.SharedManager
//import com.topdon.lib.core.db.AppDatabase
//import com.topdon.lib.core.db.entity.ThermalEntity
//import com.topdon.lib.core.ktbase.BaseActivity
//import com.topdon.module.thermal.ir.R
//import com.topdon.module.thermal.adapter.MonitorLogAdapter
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//监控记录
            // TODO: Replace RouterConfig reference with direct navigation
//class MonitorLogActivity : BaseActivity() {
//
//    val adapter: MonitorLogAdapter by lazy { MonitorLogAdapter(this) }
//
//    override fun initContentView() = R.layout.activity_monitor_log
//
//    override fun initView() {
//        log_recycler.layoutManager = LinearLayoutManager(this)
//        log_recycler.adapter = adapter
//        adapter.listener = object : MonitorLogAdapter.OnItemClickListener {
//            override fun onClick(index: Int, thermalId: String) {
            // TODO: Replace RouterConfig reference with direct navigation
//            }
//
//            override fun onLongClick(index: Int, thermalId: String) {
//
//            }
//
//        }
//    }
//
//    override fun initData() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val datas = AppDatabase.getInstance(baseContext).thermalDao()
//                .getThermalId(SharedManager.getUserId())
//            adapter.datas = datas as ArrayList<ThermalEntity>
//        }
//
//    }
//}