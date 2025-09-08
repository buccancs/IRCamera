package com.topdon.module.thermal.ir.thermal.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ScreenUtils
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseViewModelFragment
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.thermal.adapter.GalleryAdapter
import com.topdon.module.thermal.ir.thermal.viewmodel.GalleryViewModel
import com.topdon.module.thermal.ir.databinding.FragmentGalleryVideoBinding

class GalleryVideoFragment : BaseViewModelFragment<GalleryViewModel>() {
    private val adapter by lazy { GalleryAdapter(requireContext()) }
    private lateinit var binding: FragmentGalleryVideoBinding

    override fun providerVMClass() = GalleryViewModel::class.java

    override fun initContentView() = R.layout.fragment_gallery_video

    override fun initView() {
        binding = FragmentGalleryVideoBinding.bind(requireView())
        
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
                TipDialog.Builder(requireContext()).setMessage("")
                    .setPositiveListener("") {
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


    /**
     * Function description.
     */
    fun openVideo(path: String) {
        ARouter.getInstance().build(RouterConfig.VIDEO).withString("video_path", path)
            .navigation(requireContext())
    }

}