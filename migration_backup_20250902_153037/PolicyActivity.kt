package com.topdon.tc001

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.elvishew.xlog.XLog
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseViewModelActivity
import com.topdon.tc001.viewmodel.PolicyViewModel
import kotlinx.android.synthetic.main.activity_policy.*
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

    override fun httpErrorTip(
        text: String,
        requestUrl: String,
    ) {
        XLog.w("声明接口异常,打开默认链接")
        loadHttp(policy_web)
        delayShowWebView()
    }

    fun loadHttpWhenNotInit(view: WebView) {
        reloadCount--
        when (themeType) {
            1 -> {
                // 用户服务协议
                view.loadUrl(
                    "https://plat.topdon.com/topdon-plat/out-user/baseinfo/template/getHtmlContentById?softCode=${BaseApplication.instance.getSoftWareCode()}&language=1&type=21",
                )
            }

            2 -> {
                // 隐私政策
                view.loadUrl(
                    "https://plat.topdon.com/topdon-plat/out-user/baseinfo/template/getHtmlContentById?softCode=${BaseApplication.instance.getSoftWareCode()}&language=1&type=22",
                )
            }

            3 -> {
                // 第三方组件
                view.loadUrl("file:///android_asset/web/third_statement.html")
            }
        }
    }

    /**
     * 加载默认协议网址(英文版)
     */
    fun loadHttp(view: WebView) {
        reloadCount--
        when (themeType) {
            1 -> {
                if (BaseApplication.instance.isDomestic()) {
                    view.loadUrl("file:///android_asset/web/services_agreement_default_inside_china.html")
                } else {
                    // 用户服务协议
                    view.loadUrl("file:///android_asset/web/services_agreement_default.html")
                }
            }

            2 -> {
                if (BaseApplication.instance.isDomestic()) {
                    view.loadUrl("file:///android_asset/web/privacy_default_inside_china.html")
                } else {
                    // 隐私政策
                    view.loadUrl("file:///android_asset/web/privacy_default.html")
                }
            }

            3 -> {
                // 第三方组件
                view.loadUrl("file:///android_asset/web/third_statement.html")
            }
        }
    }
}
