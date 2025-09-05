package com.topdon.module.thermal.ir.thermal.activity

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.module.thermal.R
import com.topdon.module.thermal.databinding.ActivityVideoBinding
import java.io.File


@Route(path = RouterConfig.IR_THERMAL_VIDEO)
class VideoActivity : BaseActivity() {

    private lateinit var binding: ActivityVideoBinding

    companion object {
        const val KEY_PATH = "video_path"
    }

    var videoPath = ""

    override fun initContentView(): Int {
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    override fun initView() {
        // setTitleText(R.string.video) // Commented out - method not available in BaseIRActivity
        BarUtils.setNavBarColor(this, ContextCompat.getColor(this, R.color.black))
        if (intent.hasExtra(KEY_PATH)) {
            videoPath = intent.getStringExtra(KEY_PATH)!!
        }
        previewVideo(videoPath)
    }

    override fun initData() {
    }

    private fun previewVideo(path: String) {
        Log.w("123", "打开文件:$path")
        val file = File(path.replace("//", "/"))
        Log.i("123", "打开文件file:$file")
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "${packageName}.fileprovider"
            FileProvider.getUriForFile(this, authority, file)
        } else {
            Uri.fromFile(file)
        }
        Log.w("123", "打开文件uri:$uri")
        val videoView = binding.videoPlay
        videoView.setVideoURI(uri)
        videoView.setMediaController(MediaController(this))
        videoView.start()
        videoView.requestFocus()
    }

}