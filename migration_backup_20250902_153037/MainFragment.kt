package com.topdon.tc001.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.bean.event.SocketMsgEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.repository.BatteryInfo
import com.topdon.lib.core.repository.TC007Repository
import com.topdon.lib.core.socket.SocketCmdUtil
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lib.core.utils.WsCmdConstants
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.tc001.DeviceTypeActivity
import com.topdon.tc001.R
import com.topdon.tc001.popup.DelPopup
import kotlinx.android.synthetic.main.fragment_main.cl_has_device
import kotlinx.android.synthetic.main.fragment_main.cl_no_device
import kotlinx.android.synthetic.main.fragment_main.iv_add
import kotlinx.android.synthetic.main.fragment_main.recycler_view
import kotlinx.android.synthetic.main.fragment_main.tv_connect_device
import kotlinx.android.synthetic.main.fragment_main.tv_has_device_title
import kotlinx.android.synthetic.main.fragment_main.tv_no_device_title
import kotlinx.android.synthetic.main.item_device_connect.view.battery_view
import kotlinx.android.synthetic.main.item_device_connect.view.iv_bg
import kotlinx.android.synthetic.main.item_device_connect.view.iv_image
import kotlinx.android.synthetic.main.item_device_connect.view.tv_battery
import kotlinx.android.synthetic.main.item_device_connect.view.tv_device_name
import kotlinx.android.synthetic.main.item_device_connect.view.tv_device_state
import kotlinx.android.synthetic.main.item_device_connect.view.tv_title
import kotlinx.android.synthetic.main.item_device_connect.view.view_device_state
import kotlinx.coroutines.launch
import org.bytedeco.librealsense.context
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

        var hasConnectLine: Boolean = false
            set(value) {
                field = value
                notifyItemRangeChanged(0, 3)
            }

    private fun showGSROptions() {
        TipDialog.Builder(requireContext())
            .setTitleMessage("GSR Multi-modal Recording")
            .setMessage("Choose GSR recording option:")
            .setPositiveListener("Full Recording") {

                ARouter.getInstance()
                    .build(RouterConfig.GSR_MULTI_MODAL)
                    .navigation(requireContext())
            }
            .setCancelListener("GSR Demo") {

                ARouter.getInstance()
                    .build(RouterConfig.GSR_DEMO)
                    .navigation(requireContext())
            }
            .create().show()
    }

    enum class ConnectType {
        LINE,
        TS004,
        TC007,
    }
}
