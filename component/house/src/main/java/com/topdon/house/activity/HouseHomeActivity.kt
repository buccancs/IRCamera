package com.topdon.house.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.topdon.house.R
import com.topdon.house.event.HouseReportAddEvent
import com.topdon.house.fragment.DetectListFragment
import com.topdon.house.fragment.ReportListFragment
import com.topdon.house.viewmodel.DetectViewModel
import com.topdon.house.viewmodel.ReportViewModel
import com.topdon.house.viewmodel.TabViewModel
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 房屋检测首页.
 *
 * 需要传递参数：
 * - [ExtraKeyConfig.IS_TC007] - 当前设备是否为 TC007（不使用，透传）
 *
 * Created by LCG on 2024/8/20.
 */
class HouseHomeActivity : BaseActivity(), View.OnClickListener {
    private val tabViewModel: TabViewModel by viewModels()

    private val detectViewModel: DetectViewModel by viewModels()

    private val reportViewModel: ReportViewModel by viewModels()
    
    private lateinit var ivBack: ImageView
    private lateinit var ivEdit: ImageView
    private lateinit var ivAdd: ImageView
    private lateinit var ivExitEdit: ImageView
    private lateinit var clTitleBar: ConstraintLayout
    private lateinit var clEditBar: ConstraintLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var tvEditTitle: TextView

    override fun initContentView(): Int = R.layout.activity_house_home

    override fun initView() {
        ivBack = findViewById(R.id.iv_back)
        ivEdit = findViewById(R.id.iv_edit)
        ivAdd = findViewById(R.id.iv_add)
        ivExitEdit = findViewById(R.id.iv_exit_edit)
        clTitleBar = findViewById(R.id.cl_title_bar)
        clEditBar = findViewById(R.id.cl_edit_bar)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager2)
        tvEditTitle = findViewById(R.id.tv_edit_title)

        ivEdit.isEnabled = false
        ivBack.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        ivAdd.setOnClickListener(this)
        ivExitEdit.setOnClickListener(this)

        val backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                tabViewModel.isEditModeLD.value = false
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)

        tabViewModel.isEditModeLD.observe(this) {
            backCallback.isEnabled = it
            clTitleBar.isVisible = !it
            clEditBar.isVisible = it
            tabLayout.isVisible = !it
            viewPager2.isUserInputEnabled = !it
        }
        tabViewModel.selectSizeLD.observe(this) {
            tvEditTitle.text = if (it > 0) getString(R.string.chosen_item, it) else getString(R.string.not_selected)
        }

        detectViewModel.detectListLD.observe(this) {
            if (viewPager2.currentItem == 0) {
                ivEdit.isEnabled = !it.isNullOrEmpty()
            }
        }
        reportViewModel.reportListLD.observe(this) {
            if (viewPager2.currentItem == 1) {
                ivEdit.isEnabled = !it.isNullOrEmpty()
            }
        }

        viewPager2.adapter = ViewPagerAdapter(this)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {//检测
                    ivEdit.isEnabled = !detectViewModel.detectListLD.value.isNullOrEmpty()
                } else {//报告
                    ivEdit.isEnabled = !reportViewModel.reportListLD.value.isNullOrEmpty()
                }
            }
        })
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(if (position == 0) R.string.app_detection else R.string.app_report)
        }.attach()
    }

    override fun initData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDetectCreate(event: HouseReportAddEvent) {
        //有新报告被创建时，切到报告页
        viewPager2.currentItem = 1
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> finish()
            ivEdit -> {//编辑
                tabViewModel.isEditModeLD.value = true
            }
            ivAdd -> {//添加
                val newIntent = Intent(this, DetectAddActivity::class.java)
                newIntent.putExtra(ExtraKeyConfig.IS_TC007, intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false))
                startActivity(newIntent)
            }
            ivExitEdit -> {//退出编辑
                tabViewModel.isEditModeLD.value = false
            }
        }
    }


    private class ViewPagerAdapter(val activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putBoolean(ExtraKeyConfig.IS_TC007, activity.intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false))
            val fragment = if (position == 0) DetectListFragment() else ReportListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}