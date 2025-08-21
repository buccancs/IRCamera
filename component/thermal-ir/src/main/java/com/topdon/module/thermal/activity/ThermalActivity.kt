package com.topdon.module.thermal.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.ui.MenuFirstTabView
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.adapter.MenuTabAdapter
import com.topdon.module.thermal.ir.event.ThermalActionEvent
import org.greenrobot.eventbus.EventBus

class ThermalActivity : BaseActivity() {

    private val menuAdapter by lazy { MenuTabAdapter(this) }

    override fun initContentView() = R.layout.activity_thermal

    override fun initView() {
        setTitleText(R.string.main_thermal)
        mToolBar!!.setBackgroundColor(blackColor)
        BarUtils.setStatusBarColor(this, blackColor)
        BarUtils.setNavBarColor(window, blackColor)
        initRecycler()
        findViewById<MenuFirstTabView>(R.id.thermal_tab).setOnItemListener(object : MenuFirstTabView.OnItemListener {
            override fun selectPosition(position: Int) {
                //一级菜单选择
                showRecycler(position)
            }

        })
    }

    override fun initData() {

    }

    private fun initRecycler() {
        val thermalRecycler = findViewById<RecyclerView>(R.id.thermal_recycler)
        thermalRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        thermalRecycler.adapter = menuAdapter
        thermalRecycler.visibility = View.GONE
        menuAdapter.initType(1)
        menuAdapter.listener = object : MenuTabAdapter.OnItemClickListener {
            override fun onClick(index: Int) {
                //二级菜单选择
                EventBus.getDefault().post(ThermalActionEvent(action = index))
            }

        }
    }

    fun showRecycler(select: Int) {
        val thermalRecycler = findViewById<RecyclerView>(R.id.thermal_recycler)
        menuAdapter.initType(select)
        if (select == 5) {
            thermalRecycler.visibility = View.GONE
            EventBus.getDefault().post(ThermalActionEvent(action = 5000))
        } else {
            thermalRecycler.visibility = View.VISIBLE
        }
    }

}