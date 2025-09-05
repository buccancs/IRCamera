package com.topdon.module.thermal.ir.thermal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ScreenUtils
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseViewModelFragment
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.module.thermal.R
import com.topdon.module.thermal.ir.thermal.adapter.GalleryAdapter
import com.topdon.module.thermal.ir.thermal.viewmodel.GalleryViewModel
import com.topdon.module.thermal.databinding.FragmentGalleryVideoBinding

/**
 * 图片
 */
class GalleryVideoFragment : BaseViewModelFragment<GalleryViewModel>() {
    
    private var _binding: FragmentGalleryVideoBinding? = null
    private val binding get() = _binding!!
    
    private val adapter by lazy { GalleryAdapter(requireContext()) }

    override fun providerVMClass() = GalleryViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGalleryVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initContentView() = 0  // Not used with ViewBinding

    override fun initView() {
        val span = if (ScreenUtils.isLandscape()) 6 else 3
        binding.galleryVideoRecycler.layoutManager = GridLayoutManager(requireContext(), span)
        binding.galleryVideoRecycler.adapter = adapter

        viewModel.galleryLiveData.observe(this) {
            adapter.datas = it
        }
        adapter.listener = object : GalleryAdapter.OnItemClickListener {
            override fun onClick(index: Int, path: String) {
                openVideo(path)
            }

            override fun onLongClick(index: Int, path: String) {
                TipDialog.Builder(requireContext()).setMessage("导出图片")
                    .setPositiveListener("分享") {
//                            share(path)
                    }
                    .create().show()
            }

        }

    }

    override fun initData() {

    }

    override fun onStart() {
        super.onStart()
        viewModel.getVideoData()
    }


//    fun previewVideo(path: String) {
//        val imageEngine = GlideImageEngine()
//        MNImageBrowser.with(context)
//            .setCurrentPosition(0)
//            .setImageEngine(imageEngine)
//            .setImageUrl(path)
//            .show()
//    }


    fun openVideo(path: String) {
        ARouter.getInstance().build(RouterConfig.VIDEO).withString("video_path", path)
            .navigation(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}