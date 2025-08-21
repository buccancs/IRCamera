package com.topdon.module.user.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.lib.core.tools.ToastTools
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.UrlConstant
import com.topdon.lms.sdk.bean.CommonBean
import com.topdon.lms.sdk.bean.FeedBackBean
import com.topdon.lms.sdk.feedback.activity.FeedbackActivity
import com.topdon.lms.sdk.utils.LanguageUtil
import com.topdon.tc001.R
import com.topdon.module.user.activity.LanguageActivity
import com.topdon.module.user.activity.MoreActivity
import com.zoho.salesiqembed.ZohoSalesIQ
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MineFragment : BaseFragment(), View.OnClickListener {

    private var isNeedRefreshUI = false

    // View declarations
    private lateinit var iv_winter: ImageView
    private lateinit var setting_item_language: View
    private lateinit var setting_item_version: View
    private lateinit var setting_item_clear: View
    private lateinit var setting_user_lay: View
    private lateinit var setting_user_img_night: ImageView
    private lateinit var setting_user_text: TextView
    private lateinit var setting_electronic_manual: View
    private lateinit var setting_faq: View
    private lateinit var setting_feedback: View
    private lateinit var setting_item_unit: View
    private lateinit var drag_customer_view: View
    private lateinit var view_winter_point: View
    private lateinit var tv_email: TextView

    override fun initContentView(): Int = R.layout.fragment_mine

    override fun initView() {
        // Initialize views
        iv_winter = requireView().findViewById(R.id.iv_winter)
        setting_item_language = requireView().findViewById(R.id.setting_item_language)
        setting_item_version = requireView().findViewById(R.id.setting_item_version)
        setting_item_clear = requireView().findViewById(R.id.setting_item_clear)
        setting_user_lay = requireView().findViewById(R.id.setting_user_lay)
        setting_user_img_night = requireView().findViewById(R.id.setting_user_img_night)
        setting_user_text = requireView().findViewById(R.id.setting_user_text)
        setting_electronic_manual = requireView().findViewById(R.id.setting_electronic_manual)
        setting_faq = requireView().findViewById(R.id.setting_faq)
        setting_feedback = requireView().findViewById(R.id.setting_feedback)
        setting_item_unit = requireView().findViewById(R.id.setting_item_unit)
        drag_customer_view = requireView().findViewById(R.id.drag_customer_view)
        view_winter_point = requireView().findViewById(R.id.view_winter_point)
        tv_email = requireView().findViewById(R.id.tv_email)
        iv_winter.setOnClickListener(this)
        setting_item_language.setOnClickListener(this)
        setting_item_version.setOnClickListener(this)
        setting_item_clear.setOnClickListener(this)
        setting_user_lay.setOnClickListener(this)
        setting_user_img_night.setOnClickListener(this)
        setting_user_text.setOnClickListener(this)
        setting_electronic_manual.setOnClickListener(this)
        setting_faq.setOnClickListener(this)
        setting_feedback.setOnClickListener(this)
        setting_item_unit.setOnClickListener(this)//温度单温
        drag_customer_view.setOnClickListener(this)

        view_winter_point.isVisible = !SharedManager.hasClickWinter

        if (BaseApplication.instance.isDomestic()) {//国内版不给切换语言
            setting_item_language.visibility = View.GONE
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
        view_winter_point.isVisible = false
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
            SharedManager.setLanguage(requireContext(), localeStr)
            LanguageUtils.applyLanguage(AppLanguageUtils.getLocaleByLanguage(localeStr))
            ToastTools.showShort(R.string.tip_save_success)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            iv_winter -> {//冬季特辑入口
                view_winter_point.isVisible = false
                SharedManager.hasClickWinter = true
                EventBus.getDefault().post(WinterClickEvent())

                val url = if (UrlConstant.BASE_URL == "https://api.topdon.com/") {
                    "https://app.topdon.com/h5/share/#/detectionGuidanceIndex?showHeader=1&" +
                            "languageId=${LanguageUtil.getLanguageId(requireContext())}"
                } else {
                    "http://172.16.66.77:8081/#/detectionGuidanceIndex?languageId=1&showHeader=1"
                }


                Intent(this, com.topdon.tc001.WebViewActivity::class.java)
// TODO_FIX_AROUTER:                     .withString(ExtraKeyConfig.URL, url)
// TODO_FIX_AROUTER:                     .navigation(requireContext())
            }
            setting_user_lay, setting_user_img_night, setting_user_text -> {
                // Local settings - no login required
                ToastTools.showShort("Local mode - no user account required")
            }
            setting_electronic_manual -> {//电子说明书
// TODO_FIX_AROUTER:                 Intent(this, com.topdon.module.user.activity.ElectronicManualActivity::class.java).withInt(Constants.SETTING_TYPE, Constants.SETTING_BOOK).navigation(requireContext())
            }
            setting_faq -> {//FAQ
// TODO_FIX_AROUTER:                 Intent(this, com.topdon.module.user.activity.ElectronicManualActivity::class.java).withInt(Constants.SETTING_TYPE, Constants.SETTING_FAQ).navigation(requireContext())
            }
            setting_feedback -> {//意见反馈
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
            setting_item_unit -> {//温度单位
                startActivity(Intent(requireContext(), com.topdon.module.user.activity.UnitActivity::class.java))
            }
            setting_item_version -> {//版本
                startActivity(Intent(requireContext(), com.topdon.tc001.VersionActivity::class.java))
            }
            setting_item_language -> {//语言
                languagePickResult.launch(Intent(requireContext(), LanguageActivity::class.java))
            }
            setting_item_clear -> {//清除缓存，实际已隐藏
                clearCache()
            }
            drag_customer_view -> {//客服
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
        layoutParams.startToEnd = R.id.setting_user_img_night
        layoutParams.topToTop = R.id.setting_user_img_night
        layoutParams.bottomToBottom = R.id.setting_user_img_night
        setting_user_text.setPadding(SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), SizeUtils.dp2px(16f))
        setting_user_text.gravity = Gravity.CENTER
        setting_user_text.layoutParams = layoutParams
        setting_user_text.text = "Local Mode"
        setting_user_lay.visibility = View.GONE
        tv_email.text = ""
        setting_user_img_night.setImageResource(R.mipmap.ic_default_user_head)
    }

    private fun clearCache() {
        lifecycleScope.launch {
            showLoadingDialog()
            withContext(Dispatchers.IO) {
                try {
                    AppDatabase.getInstance().thermalDao().deleteByUserId(SharedManager.getUserId())
                    CleanUtils.cleanExternalCache()
                } catch (e: Exception) {
                }
                delay(1000)
            }
            dismissLoadingDialog()
            delay(50)
            TipDialog.Builder(requireContext())
                .setMessage(R.string.clear_finish)
                .setCanceled(true)
                .create().show()

        }
    }
}