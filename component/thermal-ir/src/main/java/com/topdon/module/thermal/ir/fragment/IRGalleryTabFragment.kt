package com.topdon.module.thermal.ir.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elvishew.xlog.XLog
import com.google.android.material.tabs.TabLayoutMediator
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.repository.GalleryRepository.DirType
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.event.GalleryDirChangeEvent
import com.topdon.module.thermal.ir.popup.GalleryChangePopup
import com.topdon.module.thermal.ir.popup.OptionPickPopup
import com.topdon.module.thermal.ir.viewmodel.IRGalleryTabViewModel
import com.topdon.module.thermal.ir.databinding.FragmentGalleryTabBinding
import org.greenrobot.eventbus.EventBus

/**
 * 图库 Tab 页，下分图片和视频.
 *
 * 需要传递参数：
 * - [ExtraKeyConfig.HAS_BACK_ICON] - 图库是否有返回箭头，默认 false
 * - [ExtraKeyConfig.CAN_SWITCH_DIR] - 图库是否可切换 有线设备、TS004、TC007 目录，默认 true
 * - [ExtraKeyConfig.DIR_TYPE] - 进入图库时初始的目录类型 具体取值由 [DirType] 定义
 *
 * Created by chenggeng.lin on 2023/11/14.
 */
class IRGalleryTabFragment : BaseFragment() {
    
    private var _binding: FragmentGalleryTabBinding? = null
    private val binding get() = _binding!!
    
    /**
     * 从上一界面传递过来的，图库是否有返回箭头
     */
    private var hasBackIcon = false
    /**
     * 从上一界面传递过来的，图库是否可切换 有线设备、TS004、TC007 目录
     */
    private var canSwitchDir = true
    /**
     * 从上一界面传递过来的，进入图库时初始的目录类型
     */
    private var currentDirType = DirType.LINE


    private val viewModel: IRGalleryTabViewModel by activityViewModels()

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override fun initContentView(): Int {
        return R.layout.fragment_gallery_tab
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGalleryTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        hasBackIcon = arguments?.getBoolean(ExtraKeyConfig.HAS_BACK_ICON, false) ?: false
        canSwitchDir = arguments?.getBoolean(ExtraKeyConfig.CAN_SWITCH_DIR, false) ?: false
        currentDirType = DirType.LINE // TC001 only - no other device types supported

        binding.tvTitleDir.text = getString(R.string.tc_has_line_device) // TC001 only
        binding.tvTitleDir.isVisible = canSwitchDir
        binding.tvTitleDir.setOnClickListener {
            // Directory switching disabled for TC001-only support
        }

        binding.titleView.setTitleText(if (canSwitchDir) "" else getString(R.string.app_gallery))
        binding.titleView.setLeftDrawable(if (hasBackIcon) R.drawable.ic_back_white_svg else 0)
        binding.titleView.setLeftClickListener {
            if (viewModel.isEditModeLD.value == true) {//当前为编辑状态，退出编辑
                viewModel.isEditModeLD.value = false
            } else {//当前为非编辑状态，退出页面
                if (hasBackIcon) {
                    requireActivity().finish()
                }
            }
        }
        binding.titleView.setRightDrawable(R.drawable.ic_toolbar_check_svg)
        binding.titleView.setRightClickListener {
            if (viewModel.isEditModeLD.value == true) {//当前为编辑状态，全选
                viewModel.selectAllIndex.value = binding.viewPager2.currentItem
            } else {//当前为非编辑状态，进入编辑
                viewModel.isEditModeLD.value = true
            }
        }

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.setText(if (position == 0) R.string.album_menu_Photos else R.string.app_video)
        }.attach()

        viewModel.isEditModeLD.observe(viewLifecycleOwner) { isEditMode ->
            if (isEditMode) {
                binding.titleView.setLeftDrawable(R.drawable.svg_x_cc)
            } else {
                binding.titleView.setLeftDrawable(if (hasBackIcon) R.drawable.ic_back_white_svg else 0)
            }
            binding.titleView.setRightDrawable(if (isEditMode) 0 else R.drawable.ic_toolbar_check_svg)
            binding.titleView.setRightText(if (isEditMode) getString(R.string.report_select_all) else "")
            binding.tabLayout.isVisible = !isEditMode
            binding.viewPager2.isUserInputEnabled = !isEditMode
            if (isEditMode) {
                binding.titleView.setTitleText(getString(R.string.chosen_item, viewModel.selectSizeLD.value))
                binding.tvTitleDir.isVisible = false
            } else {
                binding.titleView.setTitleText(if (canSwitchDir) "" else getString(R.string.app_gallery))
                binding.tvTitleDir.isVisible = canSwitchDir
            }
        }
        viewModel.selectSizeLD.observe(viewLifecycleOwner) {
            if (viewModel.isEditModeLD.value == true) {
                binding.titleView.setTitleText(getString(R.string.chosen_item, it))
                binding.tvTitleDir.isVisible = false
            } else {
                binding.titleView.setTitleText(if (canSwitchDir) "" else getString(R.string.app_gallery))
                binding.tvTitleDir.isVisible = canSwitchDir
            }
        }
    }

    override fun initData() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putBoolean(ExtraKeyConfig.IS_VIDEO, position == 1)
            bundle.putInt(ExtraKeyConfig.DIR_TYPE, currentDirType.ordinal)
            val fragment = IRGalleryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}