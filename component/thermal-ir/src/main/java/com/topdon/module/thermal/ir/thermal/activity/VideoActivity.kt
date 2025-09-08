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
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.databinding.ActivityVideoBinding
import java.io.File


@Route(path = RouterConfig.IR_THERMAL_VIDEO)
class VideoActivity : BaseActivity() {

    companion object {
        const val KEY_PATH = "video_path"
    }

    /** videoPath property */
    var videoPath = ""
    
    private lateinit var binding: ActivityVideoBinding

    override fun initContentView() = R.layout.activity_video

    override fun initView() {
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // setTitleText(LibAppR.string.video) // Commented out - method not available in BaseIRActivity
        BarUtils.setNavBarColor(this, ContextCompat.getColor(this, LibAppR.color.black))
        if (intent.hasExtra(KEY_PATH)) {
            videoPath = intent.getStringExtra(KEY_PATH)!!
        }
        previewVideo(videoPath)
    }

    override fun initData() {
    }

    private fun previewVideo(path: String) {
        Log.w("123", ":$path")
        val file = File(path.replace("//", "/"))
        Log.i("123", "file:$file")
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "${packageName}.fileprovider"
            FileProvider.getUriForFile(this, authority, file)
        } else {
            Uri.fromFile(file)
        }
        Log.w("123", "uri:$uri")
        val videoView = binding.videoPlay
        videoView.setVideoURI(uri)
        videoView.setMediaController(MediaController(this))
        videoView.start()
        videoView.requestFocus()
    }

}