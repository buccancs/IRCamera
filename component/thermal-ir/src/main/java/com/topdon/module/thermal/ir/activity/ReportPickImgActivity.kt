package com.topdon.module.thermal.ir.activity

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.bean.GalleryTitle
import com.topdon.lib.core.bean.event.ReportCreateEvent
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.FileTools.getUri
import com.topdon.lib.core.tools.ToastTools
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.repository.GalleryRepository.DirType
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.csl.irCamera.libui.R as LibUiR
import com.topdon.module.thermal.ir.adapter.GalleryAdapter
import com.topdon.lib.core.bean.event.GalleryDelEvent
import com.topdon.lib.core.utils.Constants.IS_REPORT_FIRST
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.thermal.ir.viewmodel.IRGalleryViewModel
import com.topdon.module.thermal.ir.databinding.ActivityReportPickImgBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

 * .
 * TC007: [ExtraKeyConfig.IS_TC007]
 * [ExtraKeyConfig.REPORT_INFO]
 * [ExtraKeyConfig.REPORT_CONDITION]
 * [ExtraKeyConfig.REPORT_IR_LIST]
@Route(path = RouterConfig.REPORT_PICK_IMG)
class ReportPickImgActivity : BaseActivity(), View.OnClickListener {

     * TC007 .
     * true-TC007 false
    private var isTC007 = false

    private val viewModel: IRGalleryViewModel by viewModels()

    private val adapter = GalleryAdapter()

    private lateinit var binding: ActivityReportPickImgBinding

    override fun initContentView() = R.layout.activity_report_pick_img

    override fun initView() {
        binding = ActivityReportPickImgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        binding.titleView.setRightDrawable(LibUiR.drawable.ic_toolbar_check_svg)
        binding.titleView.setRightClickListener { setEditMode(true) }

        initRecycler()

        binding.clShare.setOnClickListener(this)
        binding.clDelete.setOnClickListener(this)

        showLoadingDialog()

        viewModel.showListLD.observe(this) {
            adapter.refreshList(it)
            dismissLoadingDialog()
        }
        viewModel.deleteResultLD.observe(this) {
            if (it) {
                TToast.shortToast(this@ReportPickImgActivity, LibAppR.string.test_results_delete_success)
                adapter.isEditMode = false
                EventBus.getDefault().post(GalleryDelEvent())
                MediaScannerConnection.scanFile(this, arrayOf(FileConfig.lineGalleryDir), null, null) // TC001 only
                viewModel.queryAllReportImg(DirType.LINE)
            } else {
                TToast.shortToast(this@ReportPickImgActivity, LibAppR.string.test_results_delete_failed)
            }
        }
        viewModel.queryAllReportImg(DirType.LINE) // TC001 only
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReportCreate(event: ReportCreateEvent) {
        finish()
    }

    override fun initData() {

    }

    override fun onBackPressed() {
        if (adapter.isEditMode) {
            setEditMode(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun setEditMode(isEditMode: Boolean) {
        adapter.isEditMode = isEditMode
        binding.groupBottom.isVisible = isEditMode
        binding.titleView.setTitleText(if (isEditMode) getString(LibAppR.string.chosen_item, adapter.selectList.size) else getString(LibAppR.string.app_gallery))
        binding.titleView.setLeftDrawable(if (isEditMode) LibAppR.drawable.svg_x_cc else LibAppR.drawable.ic_back_white_svg)
        binding.titleView.setLeftClickListener {
            if (isEditMode) {
                setEditMode(false)
            } else {
                finish()
            }
        }
        binding.titleView.setRightDrawable(if (isEditMode) 0 else LibUiR.drawable.ic_toolbar_check_svg)
        binding.titleView.setRightText(if (isEditMode) getString(LibAppR.string.report_select_all) else "")
        binding.titleView.setRightClickListener {
            if (isEditMode) {
                adapter.selectAll()
            } else {
                setEditMode(true)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.clShare -> {
                shareImage()
            }
            binding.clDelete -> {
                deleteImage()
            }
        }
    }

    private fun initRecycler() {
        val spanCount = 3
        val gridLayoutManager = GridLayoutManager(this, spanCount)
        //span
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.dataList[position] is GalleryTitle) spanCount else 1
            }
        }
        binding.irGalleryRecycler.adapter = adapter
        binding.irGalleryRecycler.layoutManager = gridLayoutManager

        adapter.onLongEditListener = {
            // adapter
            binding.groupBottom.isVisible = true
            binding.titleView.setTitleText(getString(LibAppR.string.chosen_item, adapter.selectList.size))
            binding.titleView.setLeftDrawable(LibAppR.drawable.svg_x_cc)
            binding.titleView.setLeftClickListener {
                setEditMode(false)
            }
            binding.titleView.setRightDrawable(0)
            binding.titleView.setRightText(LibAppR.string.report_select_all)
            binding.titleView.setRightClickListener {
                adapter.selectAll()
            }
        }

        adapter.selectCallback = {
            binding.titleView.setTitleText(getString(LibAppR.string.chosen_item, it.size))
        }
        adapter.itemClickCallback = {
            val data = adapter.dataList[it]
            val fileName = data.name.substringBeforeLast(".")
            val irPath = "${FileConfig.lineIrGalleryDir}/${fileName}.ir"
            if (File(irPath).exists()) {
                ARouter.getInstance().build(RouterConfig.IR_GALLERY_EDIT)
                    .withBoolean(ExtraKeyConfig.IS_TC007, isTC007)
                    .withBoolean(ExtraKeyConfig.IS_PICK_REPORT_IMG, true)
                    .withBoolean(IS_REPORT_FIRST, false)
                    .withString(ExtraKeyConfig.FILE_ABSOLUTE_PATH, irPath)
                    .withParcelable(ExtraKeyConfig.REPORT_INFO, intent.getParcelableExtra(ExtraKeyConfig.REPORT_INFO))
                    .withParcelable(ExtraKeyConfig.REPORT_CONDITION, intent.getParcelableExtra(ExtraKeyConfig.REPORT_CONDITION))
                    .withParcelableArrayList(ExtraKeyConfig.REPORT_IR_LIST, intent.getParcelableArrayListExtra(ExtraKeyConfig.REPORT_IR_LIST))
                    .navigation(this)
            } else {
                ToastTools.showShort(LibAppR.string.album_report_on_edit)
            }
        }
    }

    private fun deleteImage() {
        val deleteList = adapter.buildSelectList()
        if (deleteList.size > 0) {
            TipDialog.Builder(this)
                .setMessage(getString(
                        LibAppR.string.tip_delete_chosen,
                        deleteList.size
                    ))
                .setPositiveListener(LibAppR.string.app_confirm) {
                    viewModel.delete(deleteList, DirType.LINE, true) // TC001 only
                }.setCancelListener(LibAppR.string.app_cancel)
                .create().show()
        } else {
            ToastTools.showShort(getString(LibAppR.string.tip_least_select))
        }
    }

    private fun shareImage() {
        val data = adapter.buildSelectList()
        if (data.size == 0) {
            ToastTools.showShort(getString(LibAppR.string.tip_least_select))
            return
        }
        if (data.size > 9) {
            ToastTools.showShort(getString(LibAppR.string.Limite_di_9carte))
            return
        }
        val imageUris = ArrayList<Uri>()
        val shareIntent = Intent()
        if (data.size == 1) {
            if (data[0].name.uppercase().endsWith(".MP4")) {
                shareIntent.type = "video/*"
            } else {
                shareIntent.type = "image/*"
            }
            shareIntent.action = Intent.ACTION_SEND
            val uri = getUri(File(data[0].path))
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        } else {
            shareIntent.type = "video/*"
            for (bean in data) {
                imageUris.add(getUri(File(bean.path)))
            }
            shareIntent.action = Intent.ACTION_SEND_MULTIPLE
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUris)
        }
        startActivity(Intent.createChooser(shareIntent, getString(LibAppR.string.battery_share)))
    }
}

