package com.topdon.module.thermal.ir.fragment

import android.os.Bundle
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
import com.csl.irCamera.libapp.R as LibAppR
import com.csl.irCamera.libui.R as LibUiR
import com.topdon.module.thermal.ir.event.GalleryDirChangeEvent
import com.topdon.module.thermal.ir.popup.GalleryChangePopup
import com.topdon.module.thermal.ir.popup.OptionPickPopup
import com.topdon.module.thermal.ir.viewmodel.IRGalleryTabViewModel
import com.topdon.module.thermal.ir.databinding.FragmentGalleryTabBinding
import org.greenrobot.eventbus.EventBus

 *  Tab .
 * [ExtraKeyConfig.HAS_BACK_ICON] -  false
 * [ExtraKeyConfig.CAN_SWITCH_DIR] -  TS004TC007  true
 * [ExtraKeyConfig.DIR_TYPE] -   [DirType]
/**
 * @author chenggeng.lin
 * @since Unknown
 */
class IRGalleryTabFragment : BaseFragment() {
    private var hasBackIcon = false
     * TS004TC007
    private var canSwitchDir = true
    private var currentDirType = DirType.LINE


    private val viewModel: IRGalleryTabViewModel by activityViewModels()

    private var viewPagerAdapter: ViewPagerAdapter? = null
    
    private var _binding: FragmentGalleryTabBinding? = null
    private val binding get() = _binding!!

    override fun initContentView(): Int {
        _binding = FragmentGalleryTabBinding.inflate(layoutInflater)
        return R.layout.fragment_gallery_tab
    }

    override fun initView() {
        hasBackIcon = arguments?.getBoolean(ExtraKeyConfig.HAS_BACK_ICON, false) ?: false
        canSwitchDir = arguments?.getBoolean(ExtraKeyConfig.CAN_SWITCH_DIR, false) ?: false
        currentDirType = DirType.LINE // TC001 only - no other device types supported

        binding.tvTitleDir.text = getString(LibAppR.string.tc_has_line_device) // TC001 only
        binding.tvTitleDir.isVisible = canSwitchDir
        binding.tvTitleDir.setOnClickListener {
            // Directory switching disabled for TC001-only support
        }

        binding.titleView.setTitleText(if (canSwitchDir) "" else getString(LibAppR.string.app_gallery))
        binding.titleView.setLeftDrawable(if (hasBackIcon) LibAppR.drawable.ic_back_white_svg else 0)
        binding.titleView.setLeftClickListener {
            if (viewModel.isEditModeLD.value == true) {//，
                viewModel.isEditModeLD.value = false
            } else {//，
                if (hasBackIcon) {
                    requireActivity().finish()
                }
            }
        }
        binding.titleView.setRightDrawable(LibUiR.drawable.ic_toolbar_check_svg)
        binding.titleView.setRightClickListener {
            if (viewModel.isEditModeLD.value == true) {//，
                viewModel.selectAllIndex.value = binding.viewPager2.currentItem
            } else {//，
                viewModel.isEditModeLD.value = true
            }
        }

        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.setText(if (position == 0) LibAppR.string.album_menu_Photos else LibAppR.string.app_video)
        }.attach()

        viewModel.isEditModeLD.observe(viewLifecycleOwner) { isEditMode ->
            if (isEditMode) {
                binding.titleView.setLeftDrawable(LibAppR.drawable.svg_x_cc)
            } else {
                binding.titleView.setLeftDrawable(if (hasBackIcon) LibAppR.drawable.ic_back_white_svg else 0)
            }
            binding.titleView.setRightDrawable(if (isEditMode) 0 else LibUiR.drawable.ic_toolbar_check_svg)
            binding.titleView.setRightText(if (isEditMode) getString(LibAppR.string.report_select_all) else "")
            binding.tabLayout.isVisible = !isEditMode
            binding.viewPager2.isUserInputEnabled = !isEditMode
            if (isEditMode) {
                binding.titleView.setTitleText(getString(LibAppR.string.chosen_item, viewModel.selectSizeLD.value))
                binding.tvTitleDir.isVisible = false
            } else {
                binding.titleView.setTitleText(if (canSwitchDir) "" else getString(LibAppR.string.app_gallery))
                binding.tvTitleDir.isVisible = canSwitchDir
            }
        }
        viewModel.selectSizeLD.observe(viewLifecycleOwner) {
            if (viewModel.isEditModeLD.value == true) {
                binding.titleView.setTitleText(getString(LibAppR.string.chosen_item, it))
                binding.tvTitleDir.isVisible = false
            } else {
                binding.titleView.setTitleText(if (canSwitchDir) "" else getString(LibAppR.string.app_gallery))
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