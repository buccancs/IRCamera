package com.topdon.tc001
import com.topdon.tc001.R

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.dialog.TipProgressDialog
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.view.SettingItem
import com.topdon.lms.sdk.utils.NetworkUtil
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.tc001.app.App
import com.topdon.tc001.utils.VersionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class ClauseActivity : AppCompatActivity() {

    private lateinit var dialog: TipProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clause)
        initView()
    }


    private fun initView() {
        val clauseYearTxt = findViewById<TextView>(R.id.clause_year_txt)
        val clauseAgreeBtn = findViewById<Button>(R.id.clause_agree_btn)
        val clauseDisagreeBtn = findViewById<Button>(R.id.clause_disagree_btn)
        val clauseItem = findViewById<SettingItem>(R.id.clause_item)
        val clauseItem2 = findViewById<SettingItem>(R.id.clause_item2)
        val clauseItem3 = findViewById<SettingItem>(R.id.clause_item3)
        val tvPrivacy = findViewById<TextView>(R.id.tv_privacy)
        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val tvVersion = findViewById<TextView>(R.id.tv_version)
        val clauseName = findViewById<TextView>(R.id.clause_name)
        
        dialog = TipProgressDialog.Builder(this)
            .setMessage(com.topdon.lib.core.R.string.tip_loading)
            .setCanceleable(false)
            .create()

        val year = Calendar.getInstance().get(Calendar.YEAR)
        clauseYearTxt.text = getString(R.string.version_year, "2023-$year")

        clauseAgreeBtn.setOnClickListener {
            confirmInitApp()
        }
        clauseDisagreeBtn.setOnClickListener {
            TipDialog.Builder(this)
                .setMessage(getString(R.string.privacy_tips))
                .setPositiveListener(R.string.privacy_confirm) {
                    confirmInitApp()
                }
                .setCancelListener(R.string.privacy_cancel) {
                    this.finish()
                }
                .setCanceled(true)
                .create().show()
        }
        val keyUseType = if (BaseApplication.instance.isDomestic()) 1 else 0
        clauseItem.setOnClickListener {
            if (!NetworkUtil.isConnected(this)) {
                TToast.shortToast(this, R.string.lms_setting_http_error)
            } else {
                val intent = Intent(this, com.topdon.tc001.PolicyActivity::class.java)
                intent.putExtra(PolicyActivity.KEY_THEME_TYPE, 1)
                intent.putExtra(PolicyActivity.KEY_USE_TYPE, keyUseType)
                startActivity(intent)
            }
        }
        clauseItem2.setOnClickListener {
            if (!NetworkUtil.isConnected(this)) {
                TToast.shortToast(this, R.string.lms_setting_http_error)
            } else {
                val intent = Intent(this, com.topdon.tc001.PolicyActivity::class.java)
                intent.putExtra(PolicyActivity.KEY_THEME_TYPE, 2)
                intent.putExtra(PolicyActivity.KEY_USE_TYPE, keyUseType)
                startActivity(intent)
            }
        }
        clauseItem3.setOnClickListener {
            if (!NetworkUtil.isConnected(this)) {
                TToast.shortToast(this, R.string.lms_setting_http_error)
            } else {
                val intent = Intent(this, com.topdon.tc001.PolicyActivity::class.java)
                intent.putExtra(PolicyActivity.KEY_THEME_TYPE, 3)
                intent.putExtra(PolicyActivity.KEY_USE_TYPE, keyUseType)
                startActivity(intent)
            }
        }

        if (BaseApplication.instance.isDomestic()) {
            tvPrivacy.text = "    ${getString(R.string.privacy_agreement_tips_new, CommUtils.getAppName())}"
            tvPrivacy.visibility = View.VISIBLE
            tvPrivacy.movementMethod = ScrollingMovementMethod.getInstance()
        }
        tvWelcome.text = getString(R.string.welcome_use_app, CommUtils.getAppName())
        tvVersion.text = "${getString(R.string.set_version)}V${VersionUtils.getCodeStr(this)}"
        clauseName.text = CommUtils.getAppName()
    }

    private fun confirmInitApp() {
        lifecycleScope.launch {
            showLoading()
            App.delayInit()
            async(Dispatchers.IO) {
                delay(1000)
                return@async
            }.await().let {
            // TODO: Replace RouterConfig reference with direct navigation
                SharedManager.setHasShowClause(true)
                dismissLoading()
                finish()
            }
        }
    }

    private fun showLoading() {
        dialog.show()
    }

    private fun dismissLoading() {
        dialog.dismiss()
    }
}