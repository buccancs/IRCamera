package com.topdon.tc001

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.csl.irCamera.R
import com.csl.irCamera.databinding.ActivityPolicyBinding
import com.elvishew.xlog.XLog
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.ktbase.BaseBindingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

    fun getHtmlData(
        htmlBody: String,
        fontColor: String,
        backgroundColor: String,
    ): String {
        val head =
            "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:100%; height:auto;}video{max-width: 100%; width:100%; height:auto;}*{margin:0px;}body{font-size:16px;color: $fontColor; background-color: $backgroundColor;}</style>" + "</head>"
        return "<html>$head<body>$htmlBody</body></html>"
    }

    private fun httpErrorTip(
        text: String,
        requestUrl: String,
    ) {
        XLog.w("activity,activity")
        loadHttp(binding.policyWeb)
        delayShowWebView()
    }

    fun loadHttpWhenNotInit(view: android.webkit.WebView) {
        reloadCount--
        when (themeType) {
            1 -> {
                // Activity operation
                view.loadUrl(
                    "https://plat.topdon.com/topdon-plat/out-user/baseinfo/template/getHtmlContentById?softCode=${BaseApplication.instance.getSoftWareCode()}&language=1&type=21",
                )
            }

            2 -> {
                // Activity operation
                view.loadUrl(
                    "https://plat.topdon.com/topdon-plat/out-user/baseinfo/template/getHtmlContentById?softCode=${BaseApplication.instance.getSoftWareCode()}&language=1&type=22",
                )
            }

            3 -> {
                // Activity operation
                view.loadUrl("file:///android_asset/web/third_statement.html")
            }
        }
    }

    /**
     * activity(activity)
     */
    fun loadHttp(view: android.webkit.WebView) {
        reloadCount--
        when (themeType) {
            1 -> {
                if (BaseApplication.instance.isDomestic()) {
                    view.loadUrl("file:///android_asset/web/services_agreement_default_inside_china.html")
                } else {
                    // Activity operation
                    view.loadUrl("file:///android_asset/web/services_agreement_default.html")
                }
            }

            2 -> {
                if (BaseApplication.instance.isDomestic()) {
                    view.loadUrl("file:///android_asset/web/privacy_default_inside_china.html")
                } else {
                    // Activity operation
                    view.loadUrl("file:///android_asset/web/privacy_default.html")
                }
            }

            3 -> {
                // Activity operation
                view.loadUrl("file:///android_asset/web/third_statement.html")
            }
        }
    }
}
