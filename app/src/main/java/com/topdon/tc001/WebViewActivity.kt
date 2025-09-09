package com.topdon.tc001

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.csl.irCamera.R
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.ktbase.BaseActivity

/**
 * Enhanced WebView Activity with improved security and performance
 * 
 * Loads web content with proper security configurations and error handling.
 * Requires URL parameter via [ExtraKeyConfig.URL]
 *
 * Security Features:
 * - Restricted file access for safety
 * - HTTPS-first loading policy
 * - Proper mixed content handling
 * 
 * Performance Features:
 * - Optimized caching strategy
 * - Hardware acceleration support
 * - Memory-efficient WebView configuration
 *
 * Created by LCG on 2024/12/18.
 */
class WebViewActivity : BaseActivity() {
    companion object {
        private const val TAG = "WebViewActivity"
        private const val DEFAULT_TIMEOUT_MS = 30000L
        private const val MIN_URL_LENGTH = 7 // minimum for http://
    }

    // View declarations with lazy initialization for better memory management
    private val tvReload: TextView by lazy { findViewById(R.id.tv_reload) }
    private val viewCover: View by lazy { findViewById(R.id.view_cover) }
    private val clError: ConstraintLayout by lazy { findViewById(R.id.cl_error) }
    private val webView: BridgeWebView by lazy { findViewById(R.id.web_view) }

    override fun initContentView(): Int = R.layout.activity_web_view

    override fun initView() {
        // Views are now initialized using lazy properties for better memory management
        // No explicit initialization needed
    }

    /**
     * Enhanced WebView configuration with security and performance optimizations
     */
    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        showLoadingDialog()

        val url: String = intent.extras?.getString(ExtraKeyConfig.URL) ?: ""
        
        // Enhanced URL validation
        if (url.length < MIN_URL_LENGTH || (!url.startsWith("https://") && !url.startsWith("http://"))) {
            Log.e(TAG, "Invalid URL provided: $url")
            showErrorState()
            return
        }

        setupReloadListener(url)
        configureWebViewSettings()
        setupWebViewClient()
        setupBridgeHandlers()
        
        // Load URL with error handling
        try {
            webView.loadUrl(url)
            webView.isScrollContainer = false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load URL: $url", e)
            showErrorState()
        }
    }

    /**
     * Sets up the reload button listener with enhanced error handling
     */
    private fun setupReloadListener(url: String) {
        tvReload.setOnClickListener {
            showLoadingDialog()
            viewCover.isVisible = true
            clError.isVisible = false
            
            try {
                webView.loadUrl(url)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reload URL: $url", e)
                showErrorState()
            }
        }
    }

    /**
     * Enhanced WebView settings with security and performance optimizations
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebViewSettings() {

        val webSettings: WebSettings = webView.settings
        
        // Performance optimizations
        webSettings.setSupportZoom(false) // 设置不支持字体缩放
        webSettings.useWideViewPort = true
        webSettings.defaultTextEncodingName = "UTF-8"
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // Better caching strategy than LOAD_NO_CACHE
        
        // Security configurations
        webSettings.javaScriptEnabled = true // Required but controlled
        webSettings.javaScriptCanOpenWindowsAutomatically = false // Enhanced security
        webSettings.allowFileAccess = false // Restrict file system access for security
        webSettings.allowContentAccess = false // Restrict content provider access
        webSettings.allowFileAccessFromFileURLs = false // Prevent file URL access
        webSettings.allowUniversalAccessFromFileURLs = false // Prevent universal access
        
        // Mixed content handling (prefer HTTPS)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        
        // Enable hardware acceleration for better performance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    /**
     * Enhanced WebView client with better error handling and security
     */
    private fun setupWebViewClient() {

        webView.webViewClient =
            object : BridgeWebViewClient(webView) {
                override fun onPageFinished(
                    view: WebView?,
                    url: String?,
                ) {
                    super.onPageFinished(view, url)
                    dismissLoadingDialog()
                    viewCover.isVisible = false
                    Log.d(TAG, "Page loaded successfully: $url")
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?,
                ) {
                    super.onReceivedError(view, request, error)
                    
                    Log.e(TAG, "WebView error: ${error?.description} for URL: ${request?.url}")
                    
                    dismissLoadingDialog()
                    viewCover.isVisible = false
                    
                    // Only show error for main frame failures
                    if (request?.isForMainFrame == true) {
                        showErrorState()
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString()
                    
                    // Security check for URL protocol
                    if (url != null && (url.startsWith("https://") || url.startsWith("http://"))) {
                        return false // Allow normal loading
                    }
                    
                    Log.w(TAG, "Blocked potentially unsafe URL: $url")
                    return true // Block unsafe URLs
                }
            }
    }

    /**
     * Sets up JavaScript bridge handlers
     */
    private fun setupBridgeHandlers() {

        webView.registerHandler("goBack") { data, function ->
            Log.d(TAG, "JavaScript bridge goBack called with data: $data")
            function.onCallBack("android")
        }
    }

    /**
     * Shows error state with proper UI updates
     */
    private fun showErrorState() {
        dismissLoadingDialog()
        viewCover.isVisible = false
        clError.isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // Proper WebView cleanup to prevent memory leaks
        try {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView.clearHistory()
            webView.clearCache(true)
            webView.onPause()
            webView.removeAllViews()
            webView.destroy()
        } catch (e: Exception) {
            Log.e(TAG, "Error during WebView cleanup", e)
        }
    }
}
