package com.topdon.tc001
import com.topdon.tc001.R

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseBindingActivity
import com.topdon.tc001.databinding.ActivityWebViewBinding

class WebViewActivity : BaseBindingActivity<ActivityWebViewBinding>() {

    override fun initContentLayoutId(): Int = R.layout.activity_web_view

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLoadingDialog()

        val url: String = intent.extras?.getString(ExtraKeyConfig.URL) ?: ""

        binding.tvReload.setOnClickListener {
            showLoadingDialog()
            binding.viewCover.isVisible = true
            binding.clError.isVisible = false
            binding.webView.loadUrl(url)
        }

        val webSettings: WebSettings = binding.webView.settings
        webSettings.setSupportZoom(false)//设置不支持字体缩放
        webSettings.useWideViewPort = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true //允许js弹出窗口
        webSettings.defaultTextEncodingName = "UTF-8"
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        binding.webView.webViewClient = object : BridgeWebViewClient(binding.webView) {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                dismissLoadingDialog()
                binding.viewCover.isVisible = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                dismissLoadingDialog()
                binding.viewCover.isVisible = false
                if (request?.isForMainFrame == true) {
                    binding.clError.isVisible = true
                }
            }
        }

        binding.webView.registerHandler("goBack") { _, function ->
            function.onCallBack("android")
            finish()
        }
        binding.webView.loadUrl(url)
    }
}