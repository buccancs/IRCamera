package com.topdon.tc001
import com.csl.irCamera.R
import com.csl.irCamera.BuildConfig
import com.csl.irCamera.libapp.R as LibAppR

import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.CheckDoubleClick
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.UrlConstant
import com.csl.irCamera.databinding.ActivityVersionBinding
import com.topdon.tc001.utils.AppVersionUtil
import com.topdon.tc001.utils.VersionUtils
import java.util.*

@Route(path = RouterConfig.VERSION)
class VersionActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityVersionBinding
    
    override fun initContentView() = R.layout.activity_version

    override fun initView() {
        // Bind to the actual root view that was set by BaseActivity
        binding = ActivityVersionBinding.bind(findViewById<View>(android.R.id.content).rootView)
        
        binding.versionCodeText.text = "${getString(LibAppR.string.set_version)}V${VersionUtils.getCodeStr(this)}"
        val year = Calendar.getInstance().get(Calendar.YEAR)
        binding.versionYearTxt.text = getString(LibAppR.string.version_year, "2023-$year")
        binding.versionStatementPrivateTxt.setOnClickListener(this)
        binding.versionStatementPolicyTxt.setOnClickListener(this)
        binding.versionStatementCopyrightTxt.setOnClickListener(this)

        binding.settingVersionImg.setOnClickListener {
            if (BuildConfig.DEBUG && CheckDoubleClick.isFastDoubleClick()) {
                LMS.getInstance().activityEnv()
            }
        }
        findViewById<View>(R.id.cl_new_version).setOnClickListener {
            if (!CheckDoubleClick.isFastDoubleClick()) {
                checkAppVersion(true)
            }
        }
        binding.settingVersionTxt.text = CommUtils.getAppName()
    }

    override fun initData() {
        if (BaseApplication.instance.isDomestic()) {
            checkAppVersion(false)
        }
    }

    override fun onResume() {
        super.onResume()
        SharedManager.setBaseHost(UrlConstant.BASE_URL)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.versionStatementPrivateTxt -> {
                ARouter.getInstance().build(RouterConfig.POLICY)
                    .withInt(PolicyActivity.KEY_THEME_TYPE, 1)
                    .navigation(this)
            }
            binding.versionStatementPolicyTxt -> {
                ARouter.getInstance().build(RouterConfig.POLICY)
                    .withInt(PolicyActivity.KEY_THEME_TYPE, 2)
                    .navigation(this)
            }
            binding.versionStatementCopyrightTxt -> {
                ARouter.getInstance().build(RouterConfig.POLICY)
                    .withInt(PolicyActivity.KEY_THEME_TYPE, 3)
                    .navigation(this)
            }
        }
    }

    private var appVersionUtil: AppVersionUtil?=null
    private fun checkAppVersion(isShow: Boolean) {
        if (appVersionUtil == null) {
            appVersionUtil = AppVersionUtil(this, object : AppVersionUtil.DotIsShowListener {
                override fun isShow(show: Boolean) {
                    findViewById<View>(R.id.cl_new_version).visibility = View.VISIBLE
                }

                override fun version(version: String) {
                    findViewById<TextView>(R.id.tv_new_version).text = "$version"
                }
            })
        }
        appVersionUtil?.checkVersion(isShow)
    }

}