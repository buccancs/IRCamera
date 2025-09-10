package com.topdon.module.thermal.ir.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.GalleryBean
import com.topdon.lib.core.bean.event.GalleryDelEvent
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.navigation.NavigationManager
import com.topdon.lib.core.tools.FileTools
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.tools.ToastTools
import com.topdon.lib.core.utils.ByteUtils.bytesToInt
import com.topdon.lib.core.utils.Constants.IS_REPORT_FIRST
import com.topdon.lib.core.view.TitleView
import com.topdon.lib.ui.dialog.ProgressDialog
import com.topdon.libcom.ExcelUtil
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.event.ImageGalleryEvent
import com.topdon.module.thermal.ir.fragment.GalleryFragment
import com.topdon.module.thermal.ir.frame.FrameTool
import com.topdon.module.thermal.ir.viewmodel.IRGalleryEditViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import com.topdon.lib.core.R as LibR

    private var progressDialog: ProgressDialog? = null
    private var excelName: String = ""

    private fun actionExcel() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
        }
        progressDialog?.show()

        excelName = dataList[position].name.substringBeforeLast(".")
        val irPath = "${FileConfig.lineIrGalleryDir}/$excelName.ir"
        if (!File(irPath).exists()) {
            ToastTools.showShort(getString(LibR.string.album_report_on_edit))
            progressDialog?.dismiss()
            return
        }
        irViewModel.initData(irPath)
    }

    override fun onClick(v: View?) {
        when (v) {
            findViewById<LinearLayout>(R.id.ll_ir_edit_2D) -> {
                // 2dactivity
                actionEditOrReport(false)
            }

            findViewById<LinearLayout>(R.id.ll_ir_edit_3D) -> {
                // Activity logic3D
                val data = dataList[position]
                val fileName = data.name.substringBeforeLast(".")
                val irPath = "${FileConfig.lineIrGalleryDir}/$fileName.ir"
                if (!File(irPath).exists()) {
                    ToastTools.showShort(LibR.string.album_report_on_edit)
                    return
                }
                var tempHigh = 0f
                var tempLow = 0f
                lifecycleScope.launch {
//                    showLoading()
                    withContext(Dispatchers.IO) {
                        val file = File(irPath)
                        if (!file.exists()) {
                            XLog.w("IRactivity: ${file.absolutePath}")
                            return@withContext
                        }
                        XLog.w("IRactivity: ${file.absolutePath}")
                        val bytes = file.readBytes()
                        val headLenBytes = ByteArray(2)
                        System.arraycopy(bytes, 0, headLenBytes, 0, 2)
                        val headLen = headLenBytes.bytesToInt()
                        val headDataBytes = ByteArray(headLen)
                        val frameDataBytes = ByteArray(bytes.size - headLen)
                        System.arraycopy(bytes, 0, headDataBytes, 0, headDataBytes.size)
                        System.arraycopy(bytes, headLen, frameDataBytes, 0, frameDataBytes.size)
                        frameTool.read(frameDataBytes)
                        tempHigh = frameTool.getSrcTemp().maxTemperature
                        tempLow = frameTool.getSrcTemp().minTemperature
                    }
//                    dismissLoading()
                    NavigationManager.getInstance().build(
                        RouterConfig.IR_GALLERY_3D,
                    ).withString(ExtraKeyConfig.IR_PATH, irPath)
                        .withFloat(ExtraKeyConfig.TEMP_HIGH, tempHigh).withFloat(ExtraKeyConfig.TEMP_LOW, tempLow)
                        .navigation(this@IRGalleryDetail01Activity)
                }
            }

            findViewById<LinearLayout>(R.id.ll_ir_report) -> {
                // Activity logic
                actionEditOrReport(true)
            }

            findViewById<LinearLayout>(R.id.ll_ir_ex) -> {
                TipDialog.Builder(
                    this,
                ).setMessage(LibR.string.tip_album_temp_exportfile).setPositiveListener(LibR.string.app_confirm) {
                    actionExcel()
                }.setCancelListener(LibR.string.app_cancel) {}.setCanceled(true).create().show()
            }
        }
    }

    private fun actionEditOrReport(isReport: Boolean) {
        val data = dataList[position]
        val fileName = data.name.substringBeforeLast(".")
        val irPath = "${FileConfig.lineIrGalleryDir}/$fileName.ir"
        if (!File(irPath).exists()) {
            ToastTools.showShort(LibR.string.album_report_on_edit)
            return
        }
        NavigationManager.getInstance().build(RouterConfig.IR_GALLERY_EDIT)
            .withBoolean(ExtraKeyConfig.IS_TC007, isTC007)
            .withBoolean(ExtraKeyConfig.IS_PICK_REPORT_IMG, isReport)
            .withBoolean(IS_REPORT_FIRST, true)
            .withString(ExtraKeyConfig.FILE_ABSOLUTE_PATH, irPath)
            .navigation(this)
    }

    inner class GalleryViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = GalleryFragment()
            val bundle = Bundle()
            bundle.putString("path", dataList[position].path)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveFinishBean(imageGalleryEvent: ImageGalleryEvent) {
        finish()
    }
}
