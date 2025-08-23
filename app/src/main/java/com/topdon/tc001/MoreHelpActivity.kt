package com.topdon.tc001
import com.topdon.tc001.R
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.text.*
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.view.TitleView

class MoreHelpActivity:BaseActivity() {
    private var connectionType : Int = 0
    private lateinit var wifiManager: WifiManager
    private lateinit var ivTvSetting: TextView
    private lateinit var disconnectTip2: View
    
    // Additional views for layout management
    private lateinit var tvTitle: TextView
    private lateinit var mainGuideTip1: View
    private lateinit var mainGuideTip2: View
    private lateinit var mainGuideTip4: View
    private lateinit var disconnectTip1: View
    override fun initContentView() = R.layout.activity_more_help

    override fun initView() {
        initIntent()
        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private fun initIntent() {
        tvTitle = findViewById(R.id.tv_title)
        val titleView = findViewById<TitleView>(R.id.title_view)
        ivTvSetting = findViewById(R.id.iv_tvSetting)
        disconnectTip2 = findViewById(R.id.disconnect_tip2)
        
        // Initialize other views
        mainGuideTip1 = findViewById(R.id.main_guide_tip1)
        mainGuideTip2 = findViewById(R.id.main_guide_tip2)
        mainGuideTip4 = findViewById(R.id.main_guide_tip4)
        disconnectTip1 = findViewById(R.id.disconnect_tip1)
        
        connectionType = intent.getIntExtra(Constants.SETTING_CONNECTION_TYPE,0)
        if(connectionType == Constants.SETTING_CONNECTION){
            tvTitle.text = getString(R.string.ts004_guide_text8)
            titleView.setTitleText(R.string.ts004_guide_text6)
            main_guide_tip1.visibility = View.VISIBLE
            main_guide_tip2.visibility = View.VISIBLE
            main_guide_tip4.visibility = View.VISIBLE
            disconnect_tip1.visibility = View.GONE
            disconnect_tip2.visibility = View.GONE
            iv_tvSetting.visibility = View.GONE
        }else{
            tvTitle.text = getString(R.string.ts004_disconnect_tips1)
            mainGuideTip1.visibility = View.GONE
            mainGuideTip2.visibility = View.GONE
            mainGuideTip4.visibility = View.GONE
            disconnectTip1.visibility = View.VISIBLE
            disconnectTip2.visibility = View.VISIBLE
            ivTvSetting.visibility = View.VISIBLE
            val spannable = SpannableStringBuilder(getString(R.string.ts004_disconnect_tips4))
            spannable.setSpan(UnderlineSpan(), 0, getString(R.string.ts004_disconnect_tips4).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ivTvSetting.text = spannable
        }
    }

    override fun initData() {
        ivTvSetting.setOnClickListener {
            startWifiList()
        }
    }

    private fun startWifiList(){
        if(wifiManager.isWifiEnabled){
            if (Build.VERSION.SDK_INT < 29) {//低于 Android10
                wifiManager.isWifiEnabled = true
            } else {
                var wifiIntent = Intent(Settings.Panel.ACTION_WIFI)
                if (wifiIntent.resolveActivity(packageManager) == null) {
                    wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    if (wifiIntent.resolveActivity(packageManager) != null) {
                        startActivity(wifiIntent)
                    }
                } else {
                    startActivity(wifiIntent)
                }
            }
        }else{
            TipDialog.Builder(this)
                .setTitleMessage(getString(R.string.app_tip))
                .setMessage(R.string.ts004_wlan_tips)
                .setPositiveListener(R.string.app_open) {
                    if (Build.VERSION.SDK_INT < 29) {//低于 Android10
                        wifiManager.isWifiEnabled = true
                    } else {
                        var wifiIntent = Intent(Settings.Panel.ACTION_WIFI)
                        if (wifiIntent.resolveActivity(packageManager) == null) {
                            wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                            if (wifiIntent.resolveActivity(packageManager) != null) {
                                startActivity(wifiIntent)
                            }
                        } else {
                            startActivity(wifiIntent)
                        }
                    }
                }
                .setCancelListener(R.string.app_cancel) {
                }
                .setCanceled(true)
                .create().show()
        }
    }
}