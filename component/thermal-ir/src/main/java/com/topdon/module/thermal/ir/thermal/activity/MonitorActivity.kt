package com.topdon.module.thermal.ir.thermal.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.BarUtils
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import com.topdon.lib.ui.dialog.MonitorSelectDialog
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.thermal.fragment.event.ThermalActionEvent
import com.topdon.module.thermal.databinding.ActivityMonitorBinding
import org.greenrobot.eventbus.EventBus
import java.util.*

@Route(path = RouterConfig.IR_THERMAL_MONITOR)
class MonitorActivity : BaseIRActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMonitorBinding

    companion object {
        const val STATS_START = 101
        const val STATS_MONITOR = 102
        const val STATS_FINISH = 103
    }

    var MONITOR_ACTION = STATS_START

    private var selectType = 1//选取点类型(点 线 面)
    private var selectIndex: ArrayList<Int> = arrayListOf()//选取点

    override fun initContentView(): Int {
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun initView() {
        // Title and toolbar handling commented out - using toolbar_lay layout
        // setTitleText(R.string.main_thermal_motion)
        // mToolBar!!.setBackgroundColor(blackColor)
        // BarUtils.setStatusBarColor(this, blackColor)
        // BarUtils.setNavBarColor(window, blackColor)
        binding.motionLogBtn.setOnClickListener(this)
        binding.motionBtn.setOnClickListener(this)
        binding.motionStartBtn.setOnClickListener(this)
//        if (BaseApplication.instance.isConnected()) {
//            mHandler.postDelayed({
//                EventBus.getDefault().post(ThermalActionEvent(action = 2001))
//            }, 300)
//        }
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        when (v) {
            motion_log_btn -> {
                ARouter.getInstance().build(RouterConfig.IR_THERMAL_LOG_MP_CHART).navigation(this)
            }
            motion_btn -> {
                MonitorSelectDialog.Builder(this)
                    // .setTitle("请选择监控类型") // Commented out - method not available
                    .setPositiveListener { select ->
                        updateUI()
                        when (select) {
                            1 -> EventBus.getDefault().post(ThermalActionEvent(action = 2001))
                            2 -> EventBus.getDefault().post(ThermalActionEvent(action = 2002))
                            else -> EventBus.getDefault()
                                .post(ThermalActionEvent(action = 2003))
                        }
                    }
                    .create().show()
            }
            motion_start_btn -> {
                ARouter.getInstance().build(RouterConfig.IR_MONITOR_CHART)
                    .withInt("type", selectType)
                    .withIntegerArrayList("select", selectIndex)
                    .navigation(this)
                finish()
            }
        }
    }

    fun select(selectType: Int, selectIndex: ArrayList<Int>) {
        motion_start_btn.isEnabled = true
        this.selectType = selectType
        this.selectIndex = selectIndex
    }

    private fun updateUI() {
        motion_start_btn.isEnabled = false
        motion_start_btn.visibility = View.VISIBLE
        motion_log_btn.visibility = View.GONE
        motion_btn.visibility = View.GONE
    }

    //秒
    fun updateTime(time: Long) {
        val ss = time % 60
        val mm = time / 60 % 60
        val ssStr = String.format("%02d", ss)
        val mmStr = String.format("%02d", mm)
        motion_start_btn.text = "${mmStr}:${ssStr}"
    }


}