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
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.tabs.TabLayout
import com.topdon.house.R
import com.topdon.lib.core.R as LibR
import com.topdon.house.event.HouseReportAddEvent
import com.topdon.house.fragment.DetectListFragment
import com.topdon.house.fragment.ReportListFragment
import com.topdon.house.viewmodel.DetectViewModel
import com.topdon.house.viewmodel.ReportViewModel
import com.topdon.house.viewmodel.TabViewModel
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * [Chinese text].
 *
 * [Chinese text]: 
 * - [ExtraKeyConfig.IS_TC007] - [Chinese text] TC007([Chinese text], [Chinese text])
 *
 * Created by LCG on 2024/8/20.
 */
class HouseHomeActivity : BaseActivity(), View.OnClickListener {
    private val tabViewModel: TabViewModel by viewModels()

    private val detectViewModel: DetectViewModel by viewModels()

    private val reportViewModel: ReportViewModel by viewModels()

    override fun initContentView(): Int = R.layout.activity_house_home

    override fun initView() {
        val ivEdit = findViewById<ImageView>(R.id.iv_edit)
        val ivBack = findViewById<ImageView>(R.id.iv_back)
        val ivAdd = findViewById<ImageView>(R.id.iv_add)
        val ivExitEdit = findViewById<ImageView>(R.id.iv_exit_edit)
        
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
            val clTitleBar = findViewById<ConstraintLayout>(R.id.cl_title_bar)
            val clEditBar = findViewById<ConstraintLayout>(R.id.cl_edit_bar)
            val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
            val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
            
            backCallback.isEnabled = it
            clTitleBar.isVisible = !it
            clEditBar.isVisible = it
            tabLayout.isVisible = !it
            viewPager2.isUserInputEnabled = !it
        }
        tabViewModel.selectSizeLD.observe(this) {
            val tvEditTitle = findViewById<TextView>(R.id.tv_edit_title)
            tvEditTitle.text = if (it > 0) getString(R.string.chosen_item, it) else getString(R.string.not_selected)
        }

        detectViewModel.detectListLD.observe(this) {
            val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
            val ivEdit = findViewById<ImageView>(R.id.iv_edit)
            if (viewPager2.currentItem == 0) {
                ivEdit.isEnabled = !it.isNullOrEmpty()
            }
        }
        reportViewModel.reportListLD.observe(this) {
            val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
            val ivEdit = findViewById<ImageView>(R.id.iv_edit)
            if (viewPager2.currentItem == 1) {
                ivEdit.isEnabled = !it.isNullOrEmpty()
            }
        }

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        
        viewPager2.adapter = ViewPagerAdapter(this)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {// [Chinese text]
                    ivEdit.isEnabled = !detectViewModel.detectListLD.value.isNullOrEmpty()
                } else {// [Chinese text]
                    ivEdit.isEnabled = !reportViewModel.reportListLD.value.isNullOrEmpty()
                }
            }
        })
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(if (position == 0) LibR.string.app_detection else LibR.string.app_report)
        }.attach()
    }

    override fun initData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDetectCreate(event: HouseReportAddEvent) {
        // [Chinese text], [Chinese text]
        findViewById<ViewPager2>(R.id.view_pager2).currentItem = 1
    }

    override fun onClick(v: View?) {
        val ivBack = findViewById<ImageView>(R.id.iv_back)
        val ivEdit = findViewById<ImageView>(R.id.iv_edit)
        val ivAdd = findViewById<ImageView>(R.id.iv_add)
        val ivExitEdit = findViewById<ImageView>(R.id.iv_exit_edit)
        
        when (v) {
            ivBack -> finish()
            ivEdit -> {// [Chinese text]
                tabViewModel.isEditModeLD.value = true
            }
            ivAdd -> {// [Chinese text]
                val newIntent = Intent(this, DetectAddActivity::class.java)
                newIntent.putExtra(ExtraKeyConfig.IS_TC007, intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false))
                startActivity(newIntent)
            }
            ivExitEdit -> {// [Chinese text]
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