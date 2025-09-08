package com.topdon.module.thermal.ir.thermal.fragment

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.topdon.lib.core.ktbase.BaseViewModelFragment
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.thermal.adapter.GalleryAdapter
import com.topdon.module.thermal.ir.thermal.tools.GlideImageEngine
import com.topdon.module.thermal.ir.thermal.viewmodel.GalleryViewModel
import com.topdon.module.thermal.ir.databinding.FragmentGalleryPictureBinding
import java.io.File


class GalleryPictureFragment : BaseViewModelFragment<GalleryViewModel>() {

    private val adapter by lazy { GalleryAdapter(requireContext()) }
    private var _binding: FragmentGalleryPictureBinding? = null
    private val binding get() = _binding!!

    override fun providerVMClass() = GalleryViewModel::class.java
    override fun initContentView() = R.layout.fragment_gallery_picture

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun initView() {
        _binding = FragmentGalleryPictureBinding.bind(requireView())
        
        val span = if (ScreenUtils.isLandscape()) 6 else 3
        binding.galleryRecycler.layoutManager = GridLayoutManager(requireContext(), span)
        binding.galleryRecycler.adapter = adapter

        viewModel.galleryLiveData.observe(this) {
            adapter.datas = it
        }
        adapter.listener = object : GalleryAdapter.OnItemClickListener {
            override fun onClick(index: Int, path: String) {
                previewPicture(path)
            }

            override fun onLongClick(index: Int, path: String) {
                TipDialog.Builder(requireContext()).setMessage("")
                    .setPositiveListener("") {
                        share(path)
                    }

                    .create().show()
            }

        }

    }

    override fun initData() {

    }

    override fun onStart() {
        super.onStart()
        viewModel.getData()
    }

    fun share(path: String) {
        val file = File(path)
        var intent = Intent()
        intent.action = Intent.ACTION_SEND //
        intent.type = "image/*" //
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "${requireContext().packageName}.fileprovider"
            FileProvider.getUriForFile(requireContext(), authority, file)
        } else {
            Uri.fromFile(file)
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent = Intent.createChooser(intent, "")
        startActivity(intent)
    }

    fun previewPicture(path: String) {
        val imageEngine = GlideImageEngine()
        MNImageBrowser.with(context) //
            .setCurrentPosition(0) //
            .setImageEngine(imageEngine) //
            .setImageUrl(path)
            .show()
    }

}