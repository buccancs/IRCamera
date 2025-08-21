package com.topdon.module.thermal.ir.activity

import android.content.Intent
import android.media.MediaScannerConnection
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.BarUtils
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.topdon.lib.core.bean.GalleryBean
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.FileTools
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.tools.ToastTools
import com.topdon.module.thermal.ir.R
import com.topdon.lib.core.dialog.ConfirmSelectDialog
import com.topdon.lib.core.bean.event.GalleryDelEvent
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.thermal.ir.event.GalleryDownloadEvent
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.topdon.lib.core.view.TitleView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import java.io.File


class IRVideoGSYActivity : BaseActivity() {

    private var isRemote = false
    private lateinit var data: GalleryBean
    override fun initContentView() = R.layout.activity_ir_video_gsy

    override fun initView() {
        BarUtils.setNavBarColor(this, ContextCompat.getColor(this, R.color.black))

        isRemote = intent.getBooleanExtra("isRemote", false)
        data = intent.getParcelableExtra("data") ?: throw NullPointerException("传递 data")

        val clBottom: ConstraintLayout = findViewById(R.id.cl_bottom)
        val titleView: TitleView = findViewById(R.id.title_view)
        val clDownload: ConstraintLayout = findViewById(R.id.cl_download)
        val clShare: ConstraintLayout = findViewById(R.id.cl_share)
        val clDelete: ConstraintLayout = findViewById(R.id.cl_delete)
        val ivDownload: ImageView = findViewById(R.id.iv_download)

        clBottom.isVisible = isRemote //查看远端时底部才有3个按钮

        if (!isRemote) {
            titleView.setRightDrawable(R.drawable.ic_toolbar_info_svg)
            titleView.setRight2Drawable(R.drawable.ic_toolbar_share_svg)
            titleView.setRight3Drawable(R.drawable.ic_toolbar_delete_svg)
            titleView.setRightClickListener { actionInfo() }
            titleView.setRight2ClickListener { actionShare() }
            titleView.setRight3ClickListener { showDeleteDialog() }
        }

        clDownload.setOnClickListener {
            actionDownload(false)
        }
        clShare.setOnClickListener {
            if (data.hasDownload) {
                actionShare()
            } else {
                actionDownload(true)
            }
        }
        clDelete.setOnClickListener {
            showDeleteDialog()
        }

        ivDownload.isSelected = data.hasDownload
        ivDownload.setImageResource(if (isRemote) R.drawable.selector_download else R.drawable.ic_toolbar_info_svg)

        previewVideo(isRemote, data.path)
    }

    override fun initData() {
    }

    private fun previewVideo(isRemote: Boolean, path: String) {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        val url = if (isRemote) {
            path
        } else {
            path.replace("//", "/")
            "file://$path"
        }

        val gsyPlay: StandardGSYVideoPlayer = findViewById(R.id.gsy_play)
        GSYVideoOptionBuilder()
            .setUrl(url)
            .build(gsyPlay)
        //界面设置
        gsyPlay.isNeedShowWifiTip = false //不显示消耗流量弹框
        gsyPlay.titleTextView.visibility = View.GONE
        gsyPlay.backButton.visibility = View.GONE
        gsyPlay.fullscreenButton.visibility = View.GONE
    }

    private fun actionDownload(isToShare: Boolean) {
        if (data.hasDownload) {//已下载
            if (isToShare) {
                actionShare()
            }
            return
        }
        lifecycleScope.launch {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            showCameraLoading()
            val isSuccess = TS004Repository.download(data.path, File(FileConfig.ts004GalleryDir, data.name))
            MediaScannerConnection.scanFile(this@IRVideoGSYActivity, arrayOf(FileConfig.ts004GalleryDir), null, null)
            dismissCameraLoading()
            if (isSuccess) {
                ToastTools.showShort(R.string.tip_save_success)
                EventBus.getDefault().post(GalleryDownloadEvent(data.name))
                data.hasDownload = true
                val ivDownload: ImageView = findViewById(R.id.iv_download)
                ivDownload.isSelected = true
                if (isToShare) {
                    actionShare()
                }
            } else {
                ToastTools.showShort(R.string.liveData_save_error)
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun actionInfo() {
        val sizeStr = FileTools.getFileSize(data.path)
        val str = StringBuilder()
        str.append(getString(R.string.detail_date)).append("\n")
        str.append(TimeTool.showDateType(data.timeMillis)).append("\n\n")
        str.append(getString(R.string.detail_info)).append("\n")
//        str.append("尺寸: ").append(whStr).append("\n")
        str.append("${getString(R.string.detail_len)}: ").append(sizeStr).append("\n")
        str.append("${getString(R.string.detail_path)}: ").append(data.path).append("\n")
        TipDialog.Builder(this)
            .setMessage(str.toString())
            .setCanceled(true)
            .create().show()
    }

    private fun actionShare() {
        val uri = FileTools.getUri(File(data.path))
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "video/*"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.battery_share)))
    }

    private fun showDeleteDialog() {
        ConfirmSelectDialog(this).run {
            setTitleRes(R.string.tip_delete)
            setMessageRes(R.string.also_del_from_phone_album)
            setShowMessage(isRemote && data.hasDownload)
            onConfirmClickListener = {
                deleteFile(it)
            }
            show()
        }
    }

    private fun deleteFile(isDelLocal: Boolean) {
        if (isRemote) {
            lifecycleScope.launch {
                showCameraLoading()
                val isSuccess = TS004Repository.deleteFiles(arrayOf(data.id))
                if (isSuccess) {
                    if (isDelLocal) {
                        File(FileConfig.ts004GalleryDir, data.name).delete()
                        MediaScannerConnection.scanFile(this@IRVideoGSYActivity, arrayOf(FileConfig.ts004GalleryDir), null, null)
                    }
                    dismissCameraLoading()
                    ToastTools.showShort(R.string.test_results_delete_success)
                    EventBus.getDefault().post(GalleryDelEvent())
                    finish()
                } else {
                    dismissCameraLoading()
                    TToast.shortToast(this@IRVideoGSYActivity, R.string.test_results_delete_failed)
                }
            }
        } else {
            EventBus.getDefault().post(GalleryDelEvent())
            File(data.path).delete()
            MediaScannerConnection.scanFile(this, arrayOf(FileConfig.ts004GalleryDir), null, null)
            finish()
        }
    }

    override fun onResume() {
        getCurPlay().onVideoResume(false)
        super.onResume()
    }

    override fun onPause() {
        getCurPlay().onVideoPause()
        super.onPause()
    }

    private fun getCurPlay(): GSYVideoPlayer {
        val gsyPlay: StandardGSYVideoPlayer = findViewById(R.id.gsy_play)
        return if (gsyPlay.fullWindowPlayer != null) {
            gsyPlay.fullWindowPlayer
        } else {
            gsyPlay
        }
    }
}