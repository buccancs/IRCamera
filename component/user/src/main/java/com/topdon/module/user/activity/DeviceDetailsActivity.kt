package com.topdon.module.user.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.repository.ProductBean

import com.topdon.lms.sdk.utils.TLog
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.user.R
import kotlinx.android.synthetic.main.activity_device_details.*
import kotlinx.coroutines.launch

/**
 * TC001 设备信息 (legacy TC007/TS004 support removed)
 *
 * Legacy parameters (now ignored):
 * - [ExtraKeyConfig.IS_TC007] - Always treated as false for TC001
 */
@Route(path = RouterConfig.DEVICE_INFORMATION)
class DeviceDetailsActivity : BaseActivity(), View.OnClickListener {

    /**
     * Legacy TC007 flag - now always false for TC001 devices.
     */
    private var isTC007 = false

    override fun initContentView() = R.layout.activity_device_details

    override fun initView() {
        // TC001 devices only - ignore legacy TC007 parameter
        isTC007 = false // Always false for TC001
        cl_layout_copy.setOnClickListener(this)
    }

    override fun initData() {
        getDeviceDetails()
    }

    private fun getDeviceDetails() {
        lifecycleScope.launch {
            // Only TC001 is supported - device details not available via network for USB connection
            TToast.shortToast(this@DeviceDetailsActivity, R.string.operation_failed_tips)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            cl_layout_copy -> {//复制信息
                val text = "${tv_sn.text}:${tv_sn_value.text}  ${tv_device_model.text}:${tv_device_model_value.text}"
                val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                val mClipData = ClipData.newPlainText("text", text)
                cm!!.setPrimaryClip(mClipData)
                TToast.shortToast(this@DeviceDetailsActivity, R.string.ts004_copy_success)
            }
        }
    }

}