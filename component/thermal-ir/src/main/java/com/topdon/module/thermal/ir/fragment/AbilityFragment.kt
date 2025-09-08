package com.topdon.module.thermal.ir.fragment

import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter

import com.topdon.lib.core.bean.event.WinterClickEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lms.sdk.UrlConstant
import com.topdon.lms.sdk.utils.LanguageUtil
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.activity.IRThermalNightActivity
import com.topdon.module.thermal.ir.activity.IRThermalPlusActivity
import com.topdon.module.thermal.ir.activity.MonitoryHomeActivity
import com.topdon.module.thermal.ir.databinding.FragmentAbilityBinding
import org.greenrobot.eventbus.EventBus

 *  Tab
 * [ExtraKeyConfig.IS_TC007] -  TC007
class AbilityFragment : BaseFragment(), View.OnClickListener {
    private var mIsTC007 = false

    private var _binding: FragmentAbilityBinding? = null
    private val binding get() = _binding!!

    override fun initContentView(): Int {
        _binding = FragmentAbilityBinding.inflate(layoutInflater)
        return R.layout.fragment_ability
    }

    override fun initView() {
        mIsTC007 = arguments?.getBoolean(ExtraKeyConfig.IS_TC007, false) ?: false
        binding.ivWinter.setOnClickListener(this)
        binding.viewMonitory.setOnClickListener(this)

        binding.viewCar.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivWinter -> {//
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
            binding.viewMonitory -> {//
                val intent = Intent(requireContext(), MonitoryHomeActivity::class.java)
                intent.putExtra(ExtraKeyConfig.IS_TC007, mIsTC007)
                startActivity(intent)
            }



            binding.viewCar -> {//
                // TC001 only - no TC007 support
                if (DeviceTools.isTC001PlusConnect()) {
                    var intent = Intent(requireContext(), IRThermalPlusActivity::class.java)
                    intent.putExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER, true)
                    startActivity(intent)
                } else if (DeviceTools.isTC001LiteConnect()) {
                    ARouter.getInstance().build(RouterConfig.IR_TCLITE)
                        .withBoolean(ExtraKeyConfig.IS_CAR_DETECT_ENTER, true)
                        .navigation(activity)
                } else if (DeviceTools.isConnect(isSendConnectEvent = false, true)) {
                    var intent = Intent(requireContext(), IRThermalNightActivity::class.java)
                    intent.putExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER, true)
                    startActivity(intent)
                } else {
                    TipDialog.Builder(requireContext())
                        .setMessage(LibAppR.string.device_connect_tip)
                        .setPositiveListener(LibAppR.string.app_confirm)
                        .create().show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}