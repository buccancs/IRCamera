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
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.CleanUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.request.RequestOptions
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.event.PDFEvent
import com.topdon.lib.core.bean.event.WinterClickEvent
import com.topdon.lib.core.bean.response.ResponseUserInfo
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.common.UserInfoManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
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
import com.topdon.module.user.R
import com.topdon.module.user.activity.LanguageActivity
import com.topdon.module.user.activity.MoreActivity
import com.zoho.salesiqembed.ZohoSalesIQ
import com.topdon.module.user.databinding.FragmentMineBinding
import com.topdon.module.user.databinding.FragmentMoreBinding
import com.topdon.module.user.databinding.LayoutCustomerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 公共设置页，即公共 “我的”
 * [MoreActivity] - TS004 “我的”
 * [MoreFragment] - 插件式 “我的”
 *
 * Created by LCG on 2024/4/19.
 */
class MineFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentMineBinding
    
    /**
     * onResume() 阶段是否需要刷新登录状态相关 UI.
     */
    private var isNeedRefreshLogin = false

    override fun initContentView(): Int = R.layout.fragment_mine

    override fun initView() {
        binding = FragmentMineBinding.inflate(layoutInflater)
        
        binding.ivWinter.setOnClickListener(this)
        binding.settingItemLanguage.setOnClickListener(this)
        binding.settingItemVersion.setOnClickListener(this)
        binding.settingItemClear.setOnClickListener(this)
        binding.settingUserLay.setOnClickListener(this)
        binding.settingUserImgNight.setOnClickListener(this)
        binding.settingUserText.setOnClickListener(this)
        binding.settingElectronicManual.setOnClickListener(this)
        binding.settingFaq.setOnClickListener(this)
        binding.settingFeedback.setOnClickListener(this)
        binding.settingItemUnit.setOnClickListener(this)//温度单温
        binding.dragCustomerView.setOnClickListener(this)

        binding.viewWinterPoint.isVisible = !SharedManager.hasClickWinter

        if (BaseApplication.instance.isDomestic()) {//国内版不给切换语言
            binding.settingItemLanguage.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                // 要是当前已连接 TS004、TC007，切到流量上，不然登录注册意见反馈那些没网
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
        isNeedRefreshLogin = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWinterClick(event: WinterClickEvent) {
        binding.viewWinterPoint.isVisible = false
    }

    override fun onResume() {
        super.onResume()
        changeLoginStyle()
        if (isNeedRefreshLogin) {
            isNeedRefreshLogin = false
            checkLoginResult()
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
            binding.ivWinter -> {//冬季特辑入口
                binding.viewWinterPoint.isVisible = false
                SharedManager.hasClickWinter = true
                EventBus.getDefault().post(WinterClickEvent())

                val url = if (UrlConstant.BASE_URL == "https://api.topdon.com/") {
                    "https://app.topdon.com/h5/share/#/detectionGuidanceIndex?showHeader=1&" +
                            "languageId=${LanguageUtil.getLanguageId(requireContext())}"
                } else {
                    "http://172.16.66.77:8081/#/detectionGuidanceIndex?languageId=1&showHeader=1"
                }


                ARouter.getInstance().build(RouterConfig.WEB_VIEW)
                    .withString(ExtraKeyConfig.URL, url)
                    .navigation(requireContext())
            }
            binding.settingUserLay, binding.settingUserImgNight -> {
                if (UserInfoManager.getInstance().isLogin()) {
                    isNeedRefreshLogin = true
                    LMS.getInstance().activityUserInfo()
                } else {
                    loginAction()
                }
            }
            binding.settingUserText -> {
                if (!LMS.getInstance().isLogin) {
                    loginAction()
                }
            }
            binding.settingElectronicManual -> {//电子说明书
                ARouter.getInstance().build(RouterConfig.ELECTRONIC_MANUAL).withInt(Constants.SETTING_TYPE, Constants.SETTING_BOOK).navigation(requireContext())
            }
            binding.settingFaq -> {//FAQ
                ARouter.getInstance().build(RouterConfig.ELECTRONIC_MANUAL).withInt(Constants.SETTING_TYPE, Constants.SETTING_FAQ).navigation(requireContext())
            }
            binding.settingFeedback -> {//意见反馈
                if (LMS.getInstance().isLogin) {
                    val devSn = SharedManager.getDeviceSn()
                    FeedBackBean().apply {
                        logPath = logPath
                        sn = devSn
                        lastConnectSn = devSn
                        XLog.e("bcf","sn $sn  logPath $logPath")
                    }.let { feedBackBean ->
                        val intent = Intent(requireContext(), FeedbackActivity::class.java)
                        intent.putExtra(FeedbackActivity.FEEDBACKBEAN, feedBackBean)
                        startActivity(intent)
                    }
                } else {
                    loginAction()
                }
            }
            binding.settingItemUnit -> {//温度单位
                ARouter.getInstance().build(RouterConfig.UNIT).navigation(requireContext())
            }
            binding.settingItemVersion -> {//版本
                ARouter.getInstance().build(RouterConfig.VERSION).navigation(requireContext())
            }
            binding.settingItemLanguage -> {//语言
                languagePickResult.launch(Intent(requireContext(), LanguageActivity::class.java))
            }
            binding.settingItemClear -> {//清除缓存，实际已隐藏
                clearCache()
            }
            binding.dragCustomerView -> {//客服
//                ActivityUtil.goSystemCustomer(requireContext())
                val sn = SharedManager.getDeviceSn()
                if (!TextUtils.isEmpty(sn)) {
                    ZohoSalesIQ.Visitor.addInfo("SN", sn)
                }
                ZohoSalesIQ.Visitor.addInfo("Model", "Topinfrared")
                ZohoSalesIQ.Chat.show()
            }
        }
    }

    private fun loginAction() {
        isNeedRefreshLogin = true
        //activityLogin()回调不可靠，但必然触发onResume()
        val bgBitmap = BitmapFactory.decodeResource(resources, R.mipmap.bg_login)
        LMS.getInstance().activityLogin(null, null, false, null, bgBitmap)
    }

    private fun checkLoginResult() {
        if (LMS.getInstance().isLogin) {
            //登录成功
            LMS.getInstance().getUserInfo { userinfo: CommonBean ->
                try {
                    val json = userinfo.data
                    val infoData = Gson().fromJson(json, ResponseUserInfo::class.java)
                    UserInfoManager.getInstance().login(
                        token = LMS.getInstance().token,
                        userId = infoData.topdonId,
                        phone = infoData.phone,
                        email = infoData.email,
                        nickname = infoData.userName,
                        headUrl = infoData.avatar,
                    )

                    //更新ui
                    changeLoginStyle()
                } catch (e: Exception) {
                    XLog.e(" 登录异常: ${e.message}")
                }
            }
        } else {
            //登录失败
            XLog.e(" 登录失败")
            changeLoginStyle()
            binding.settingUserImgNight.setImageResource(R.mipmap.ic_default_user_head)//恢复默认头像
        }
    }

    private fun changeLoginStyle() {
        if (LMS.getInstance().isLogin) {
            val layoutParams = ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.startToEnd = R.id.setting_user_img_night
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.marginStart = SizeUtils.dp2px(16f)
            layoutParams.marginEnd = SizeUtils.dp2px(16f)
            binding.settingUserText.setPadding(0,0,0,0)
            binding.settingUserText.gravity = Gravity.LEFT
            binding.settingUserText.layoutParams = layoutParams
            val drawable = ContextCompat.getDrawable(requireContext(), R.color.transparent)
            drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            binding.settingUserText.setCompoundDrawables(null, null, drawable, null)
            binding.settingUserText.text = SharedManager.getNickname()
            binding.tvEmail.text = SharedManager.getUsername()
            binding.settingUserLay.visibility = View.VISIBLE

            if (binding.settingUserImgNight != null) {
                GlideLoader.loadCircle(
                    binding.settingUserImgNight,
                    SharedManager.getHeadIcon(),
                    R.mipmap.ic_default_user_head,
                    RequestOptions().optionalCircleCrop()
                )
            }
        } else {
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.startToEnd = R.id.setting_user_img_night
            layoutParams.topToTop = R.id.setting_user_img_night
            layoutParams.bottomToBottom = R.id.setting_user_img_night
            binding.settingUserText.setPadding(SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), SizeUtils.dp2px(16f), SizeUtils.dp2px(16f))
            binding.settingUserText.gravity = Gravity.CENTER
            binding.settingUserText.layoutParams = layoutParams
            binding.settingUserText.setText(
                AppLanguageUtils.attachBaseContext(
                context, SharedManager.getLanguage(requireContext())).getString(R.string.app_sign_in))
            val drawable = ContextCompat.getDrawable(requireContext(), R.mipmap.ic_arrow_login)
            drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            binding.settingUserText.setCompoundDrawables(null, null, drawable, null)
            binding.settingUserLay.visibility = View.GONE
            binding.tvEmail.text = ""
            binding.settingUserImgNight.setImageResource(R.mipmap.ic_default_user_head)//恢复默认头像
        }
    }

    /**
     * 清除缓存
     */
    private fun clearCache() {
        lifecycleScope.launch {
            showLoadingDialog()
            withContext(Dispatchers.IO) {
                try {
                    AppDatabase.getInstance().thermalDao().deleteByUserId(SharedManager.getUserId())
                    CleanUtils.cleanExternalCache()
                } catch (e: Exception) {
                    XLog.w("清除缓存异常: ${e.message}")
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