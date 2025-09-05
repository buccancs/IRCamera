package com.topdon.module.thermal.ir.thermal.activity

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.menu.MenuFirstTabView
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import com.topdon.module.thermal.ir.thermal.adapter.MenuTabAdapter
import com.topdon.module.thermal.ir.thermal.fragment.event.ThermalActionEvent
import com.topdon.module.thermal.ir.databinding.ActivityThermalBinding
import org.greenrobot.eventbus.EventBus

@Route(path = RouterConfig.IR_THERMAL_MAIN)
class ThermalActivity : BaseIRActivity() {

    private lateinit var binding: ActivityThermalBinding
    private val menuAdapter by lazy { MenuTabAdapter(this) }

    override fun initContentView(): Int {
        binding = ActivityThermalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun initView() {
        // setTitleText(R.string.main_thermal) // Commented out - method not available in BaseIRActivity
        val blackColor = ContextCompat.getColor(this, R.color.blackColor)
        // mToolBar!!.setBackgroundColor(blackColor) // Commented out - mToolBar not available in BaseIRActivity
        BarUtils.setStatusBarColor(this, blackColor)
        BarUtils.setNavBarColor(window, blackColor)
        initRecycler()
        binding.thermalTab.onTabClickListener = { view ->
            //一级菜单选择
            showRecycler(view.selectPosition)
        }
    }

    override fun initData() {

    }

    private fun initRecycler() {
        binding.thermalRecycler.adapter = menuAdapter
        binding.thermalRecycler.visibility = View.GONE
        binding.thermalRecycler.initType(1)
        menuAdapter.listener = object : MenuTabAdapter.OnItemClickListener {
            override fun onClick(index: Int) {
                //二级菜单选择
                Log.w("123", "index: $index")
                EventBus.getDefault().post(ThermalActionEvent(action = index))
            }

        }
    }

    fun showRecycler(select: Int) {
        binding.thermalRecycler.initType(select)
        if (select == 5) {
            binding.thermalRecycler.visibility = View.GONE
            EventBus.getDefault().post(ThermalActionEvent(action = 5000))
        } else {
            binding.thermalRecycler.visibility = View.VISIBLE
        }
    }

}