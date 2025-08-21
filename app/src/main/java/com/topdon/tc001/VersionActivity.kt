package com.topdon.tc001
import com.topdon.tc001.R

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.CheckDoubleClick
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.UrlConstant
import com.topdon.tc001.utils.AppVersionUtil
import com.topdon.tc001.utils.VersionUtils
import java.util.*

class VersionActivity : BaseActivity(), View.OnClickListener {
    
    // View declarations
    private lateinit var versionCodeText: TextView
    private lateinit var versionYearTxt: TextView
    private lateinit var versionStatementPrivateTxt: TextView
    private lateinit var versionStatementPolicyTxt: TextView
    private lateinit var versionStatementCopyrightTxt: TextView
    private lateinit var settingVersionImg: View
    private lateinit var settingVersionTxt: TextView
    private lateinit var clNewVersion: ConstraintLayout
    private lateinit var tvNewVersion: TextView
    
    override fun initContentView() = R.layout.activity_version

    override fun initView() {
        // Initialize views
        versionCodeText = findViewById(R.id.version_code_text)
        versionYearTxt = findViewById(R.id.version_year_txt)
        versionStatementPrivateTxt = findViewById(R.id.version_statement_private_txt)
        versionStatementPolicyTxt = findViewById(R.id.version_statement_policy_txt)
        versionStatementCopyrightTxt = findViewById(R.id.version_statement_copyright_txt)
        settingVersionImg = findViewById(R.id.setting_version_img)
        settingVersionTxt = findViewById(R.id.setting_version_txt)
        clNewVersion = findViewById(R.id.cl_new_version)
        tvNewVersion = findViewById(R.id.tv_new_version)
        
        versionCodeText.text = "${getString(R.string.set_version)}V${VersionUtils.getCodeStr(this)}"
        val year = Calendar.getInstance().get(Calendar.YEAR)
        versionYearTxt.text = getString(R.string.version_year, "2023-$year")
        versionStatementPrivateTxt.setOnClickListener(this)
        versionStatementPolicyTxt.setOnClickListener(this)
        versionStatementCopyrightTxt.setOnClickListener(this)

        settingVersionImg.setOnClickListener {
            if (BuildConfig.DEBUG && CheckDoubleClick.isFastDoubleClick()) {
                LMS.getInstance().activityEnv()
            }
        }
        clNewVersion.setOnClickListener {
            if (!CheckDoubleClick.isFastDoubleClick()) {
                checkAppVersion(true)
            }
        }
        settingVersionTxt.text = CommUtils.getAppName()
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
            versionStatementPrivateTxt -> {
                val intent = Intent(this, com.topdon.tc001.PolicyActivity::class.java)
                intent.putExtra(PolicyActivity.KEY_THEME_TYPE, 1)
                startActivity(intent)
            }
            versionStatementPolicyTxt -> {
                val intent = Intent(this, com.topdon.tc001.PolicyActivity::class.java)
                intent.putExtra(PolicyActivity.KEY_THEME_TYPE, 2)
                startActivity(intent)
            }
            versionStatementCopyrightTxt -> {
                val intent = Intent(this, com.topdon.tc001.PolicyActivity::class.java)
                intent.putExtra(PolicyActivity.KEY_THEME_TYPE, 3)
                startActivity(intent)
            }
        }
    }

    private var appVersionUtil: AppVersionUtil?=null
    private fun checkAppVersion(isShow: Boolean) {
        if (appVersionUtil == null) {
            appVersionUtil = AppVersionUtil(this, object : AppVersionUtil.DotIsShowListener {
                override fun isShow(show: Boolean) {
                    clNewVersion.visibility = View.VISIBLE
                }

                override fun version(version: String) {
                    tvNewVersion.text = "$version"
                }
            })
        }
        appVersionUtil?.checkVersion(isShow)
    }

}