package com.topdon.tc001.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csl.irCamera.R
import com.csl.irCamera.databinding.FragmentMainBinding
import com.topdon.lib.core.bean.event.SocketMsgEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseBindingFragment
import com.topdon.lib.core.repository.BatteryInfo
import com.topdon.lib.core.repository.TC007Repository
import com.topdon.lib.core.socket.SocketCmdUtil
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.lib.core.tools.ConstantLanguages
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.utils.NetWorkUtils
import com.topdon.lib.core.utils.WsCmdConstants
import com.topdon.libcom.navigation.NavigationManager
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.tc001.DeviceTypeActivity
import com.topdon.tc001.popup.DelPopup
import kotlinx.coroutines.launch
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

                NavigationManager.getInstance()
                    .build(RouterConfig.GSR_MULTI_MODAL)
                    .navigation(requireContext())
            }
            .setCancelListener("GSR Demo") {

                NavigationManager.getInstance()
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
