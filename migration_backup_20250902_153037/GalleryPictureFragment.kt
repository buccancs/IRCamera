package com.topdon.module.thermal.fragment

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.topdon.lib.core.ktbase.BaseViewModelFragment
import com.topdon.lib.core.widget.dialog.TipDialog
import com.topdon.module.thermal.R
import com.topdon.module.thermal.adapter.GalleryAdapter
import com.topdon.module.thermal.tools.GlideImageEngine
import com.topdon.module.thermal.viewmodel.GalleryViewModel
import kotlinx.android.synthetic.main.fragment_gallery_picture.*
import java.io.File


/**
 * [Chinese text]
 */
class GalleryPictureFragment : BaseViewModelFragment<GalleryViewModel>() {

    private val adapter by lazy { GalleryAdapter(requireContext()) }

    override fun providerVMClass() = GalleryViewModel::class.java
    override fun initContentView() = R.layout.fragment_gallery_picture


    override fun initView() {
        val span = if (ScreenUtils.isLandscape()) 6 else 3
        gallery_recycler.layoutManager = GridLayoutManager(requireContext(), span)
        gallery_recycler.adapter = adapter

        viewModel.galleryLiveData.observe(this) {
            adapter.datas = it
        }
        adapter.listener = object : GalleryAdapter.OnItemClickListener {
            override fun onClick(index: Int, path: String) {
                previewPicture(path)
            }

            override fun onLongClick(index: Int, path: String) {
                TipDialog.Builder(requireContext()).setMessage("[Chinese text]")
                    .setPositiveListener("[Chinese text]") {
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

    /**
     * [Chinese text]
     */
    fun share(path: String) {
        val file = File(path)
        var intent = Intent()
        intent.action = Intent.ACTION_SEND //settings[Chinese text]
        intent.type = "image/*" //settings[Chinese text]
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "${requireContext().packageName}.fileprovider"
            FileProvider.getUriForFile(requireContext(), authority, file)
        } else {
            Uri.fromFile(file)
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent = Intent.createChooser(intent, "[Chinese text]")
        startActivity(intent)
    }

    fun previewPicture(path: String) {
        val imageEngine = GlideImageEngine()
        MNImageBrowser.with(context) //[Chinese text]
            .setCurrentPosition(0) //[Chinese text]
            .setImageEngine(imageEngine) //[Chinese text]
            .setImageUrl(path)
            .show()
    }

}