package com.topdon.module.user.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.CleanUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.request.RequestOptions
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.event.PDFEvent
import com.topdon.lib.core.bean.event.WinterClickEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
// Remove duplicate/conflicting imports
import com.topdon.lib.core.common.WebSocketProxy
import com.topdon.lib.core.common.LanguageUtil
import com.topdon.lib.core.common.UrlConstant  
import com.topdon.lib.core.common.FeedBackBean
import com.topdon.lib.core.tools.AppLanguageUtils
import com.bumptech.glide.Glide
import com.topdon.lib.core.utils.Constants
// LMS SDK imports commented out to avoid dependency issues
// import com.topdon.lms.sdk.LMS
// import com.topdon.lms.sdk.UrlConstant  
// import com.topdon.lms.sdk.bean.CommonBean
// import com.topdon.lms.sdk.bean.FeedBackBean
// import com.topdon.lms.sdk.feedback.activity.FeedbackActivity
// import com.topdon.lms.sdk.utils.LanguageUtil
import com.topdon.module.user.R
import com.topdon.module.user.activity.LanguageActivity
import com.zoho.salesiqembed.ZohoSalesIQ
import android.widget.ImageView
import android.widget.TextView
import com.topdon.lib.core.view.SettingNightView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.topdon.lib.core.tools.ToastTools
import com.topdon.lib.core.common.NetWorkUtils

class MineFragment : BaseFragment(), View.OnClickListener {

    private var isNeedRefreshUI = false

    override fun initContentView(): Int = R.layout.fragment_mine

    override fun initView() {
        findViewById<ImageView>(R.id.iv_winter)?.setOnClickListener(this)
        findViewById<SettingNightView>(R.id.setting_item_language)?.setOnClickListener(this)
        findViewById<SettingNightView>(R.id.setting_item_version)?.setOnClickListener(this)
        findViewById<SettingNightView>(R.id.setting_item_clear)?.setOnClickListener(this)
        findViewById<ConstraintLayout>(R.id.setting_user_lay)?.setOnClickListener(this)
        findViewById<ImageView>(R.id.setting_user_img_night)?.setOnClickListener(this)
        findViewById<TextView>(R.id.setting_user_text)?.setOnClickListener(this)
        findViewById<SettingNightView>(R.id.setting_electronic_manual)?.setOnClickListener(this)
        findViewById<SettingNightView>(R.id.setting_faq)?.setOnClickListener(this)
        findViewById<SettingNightView>(R.id.setting_feedback)?.setOnClickListener(this)
        findViewById<SettingNightView>(R.id.setting_item_unit)?.setOnClickListener(this)//温度单温
        findViewById<View>(R.id.drag_customer_view)?.setOnClickListener(this)

        findViewById<View>(R.id.view_winter_point)?.isVisible = !SharedManager.hasClickWinter

        if (BaseApplication.instance.isDomestic()) {//国内版不给切换语言
            findViewById<SettingNightView>(R.id.setting_item_language)?.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                if (WebSocketProxy.getInstance().isConnected()) {
                    NetWorkUtils.switchNetwork(false)
                }
            }
        })
    }

    override fun initData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updatePDF(event: PDFEvent) {
        isNeedRefreshUI = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWinterClick(event: WinterClickEvent) {
        findViewById<View>(R.id.view_winter_point)?.isVisible = false
    }

    override fun onResume() {
        super.onResume()
        updateUIStyle()
        if (isNeedRefreshUI) {
            isNeedRefreshUI = false
        }
    }


    private val languagePickResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val localeStr: String = it.data?.getStringExtra("localeStr") ?: return@registerForActivityResult
            SharedManager.setLanguage(localeStr)
            LanguageUtils.applyLanguage(AppLanguageUtils.getLocaleByLanguage(localeStr))
            ToastTools.showShort(requireContext(), R.string.tip_save_success)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            findViewById<ImageView>(R.id.iv_winter) -> {//冬季特辑入口
                findViewById<View>(R.id.view_winter_point)?.isVisible = false
                SharedManager.hasClickWinter = true
                EventBus.getDefault().post(WinterClickEvent())

                val url = if (UrlConstant.BASE_URL == "https://api.topdon.com/") {
                    "https://app.topdon.com/h5/share/#/detectionGuidanceIndex?showHeader=1&" +
                            "languageId=${LanguageUtil.getLanguageId(requireContext())}"
                } else {
                    "http://172.16.66.77:8081/#/detectionGuidanceIndex?languageId=1&showHeader=1"
                }


                // TODO: Open web view for winter special - simplified for now
                // Intent(requireContext(), WebViewActivity::class.java)
                //     .putExtra("url", url)
                //     .let { startActivity(it) }
            }
            findViewById<ConstraintLayout>(R.id.setting_user_lay), findViewById<ImageView>(R.id.setting_user_img_night), findViewById<TextView>(R.id.setting_user_text) -> {
                // Local settings - no login required
                ToastTools.showShort(requireContext(), "Local mode - no user account required")
            }
            findViewById<SettingNightView>(R.id.setting_electronic_manual) -> {//电子说明书
// TODO_FIX_AROUTER:                 Intent(this, com.topdon.module.user.activity.ElectronicManualActivity::class.java).withInt(Constants.SETTING_TYPE, Constants.SETTING_BOOK).navigation(requireContext())
            }
            findViewById<SettingNightView>(R.id.setting_faq) -> {//FAQ
// TODO_FIX_AROUTER:                 Intent(this, com.topdon.module.user.activity.ElectronicManualActivity::class.java).withInt(Constants.SETTING_TYPE, Constants.SETTING_FAQ).navigation(requireContext())
            }
            findViewById<SettingNightView>(R.id.setting_feedback) -> {//意见反馈
                // Simplified feedback - no login required
                val devSn = SharedManager.getDeviceSn()
                FeedBackBean().apply {
                    logPath = logPath
                    sn = devSn
                    lastConnectSn = devSn
                }.let { feedBackBean ->
                    val intent = Intent(requireContext(), FeedbackActivity::class.java)
                    intent.putExtra(FeedbackActivity.FEEDBACKBEAN, feedBackBean)
                    startActivity(intent)
                }
            }
            findViewById<SettingNightView>(R.id.setting_item_unit) -> {//温度单位
                startActivity(Intent(requireContext(), com.topdon.module.user.activity.UnitActivity::class.java))
            }
            findViewById<SettingNightView>(R.id.setting_item_version) -> {//版本
                // TODO: Fix version activity reference
                // startActivity(Intent(requireContext(), VersionActivity::class.java))
            }
            findViewById<SettingNightView>(R.id.setting_item_language) -> {//语言
                languagePickResult.launch(Intent(requireContext(), LanguageActivity::class.java))
            }
            findViewById<SettingNightView>(R.id.setting_item_clear) -> {//清除缓存，实际已隐藏
                clearCache()
            }
            findViewById<View>(R.id.drag_customer_view) -> {//客服
                val sn = SharedManager.getDeviceSn()
                if (!TextUtils.isEmpty(sn)) {
                    ZohoSalesIQ.Visitor.addInfo("SN", sn)
                }
                ZohoSalesIQ.Visitor.addInfo("Model", "Topinfrared")
                ZohoSalesIQ.Chat.show()
            }
        }
    }

    private fun updateUIStyle() {
        // Local mode - show local settings UI
        val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.startToEnd = R.id.findViewById<ImageView>(R.id.setting_user_img_night)
        layoutParams.topToTop = R.id.findViewById<ImageView>(R.id.setting_user_img_night)
        layoutParams.bottomToBottom = R.id.findViewById<ImageView>(R.id.setting_user_img_night)
        findViewById<TextView>(R.id.setting_user_text).setPadding(SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), SizeUtils.dp2px(16f))
        findViewById<TextView>(R.id.setting_user_text).gravity = Gravity.CENTER
        findViewById<TextView>(R.id.setting_user_text).layoutParams = layoutParams
        findViewById<TextView>(R.id.setting_user_text).text = "Local Mode"
        findViewById<ConstraintLayout>(R.id.setting_user_lay).visibility = View.GONE
        // tv_email.text = "" // Comment out missing view reference
        findViewById<ImageView>(R.id.setting_user_img_night).setImageResource(R.mipmap.ic_default_user_head)
    }

    private fun clearCache() {
        lifecycleScope.launch {
            // showLoadingDialog() // Comment out missing dialog method
            withContext(Dispatchers.IO) {
                try {
                    // AppDatabase.getInstance().thermalDao().deleteByUserId(SharedManager.getUserId()) // Database removed
                    CleanUtils.cleanExternalCache()
                } catch (e: Exception) {
                }
                delay(1000)
            }
            // dismissLoadingDialog() // Comment out missing dialog method
            delay(50)
            TipDialog.Builder(requireContext())
                .setMessage(R.string.clear_finish)
                .setCanceled(true)
                .create().show()

        }
    }
}