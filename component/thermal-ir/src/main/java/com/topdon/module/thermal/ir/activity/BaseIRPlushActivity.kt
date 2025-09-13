package com.topdon.module.thermal.ir.activity

import android.graphics.ImageFormat
import android.hardware.usb.UsbDevice
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.view.SurfaceView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.usb.USBMonitor
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.DualCameraParams
import com.energy.iruvc.utils.IFrameCallback
import com.energy.iruvc.utils.IIRFrameCallback
import com.energy.iruvc.utils.SynchronizedBitmap
import com.energy.iruvc.uvc.ConnectCallback
import com.energy.iruvc.uvc.UVCCamera
import com.example.suplib.wrapper.SupHelp
import com.infisense.usbdual.Const
// import com.infisense.usbdual.camera.DualViewWithExternalCameraCommonApi // Temporarily disabled - hardware specific
// import com.infisense.usbdual.camera.IRUVCDual // Temporarily disabled - hardware specific
// import com.infisense.usbdual.camera.USBMonitorManager // Temporarily disabled - hardware specific
import com.infisense.usbdual.camera.DualViewWithExternalCameraCommonApi
import com.infisense.usbdual.camera.IRUVCDual
import com.infisense.usbdual.camera.USBMonitorManager
import com.infisense.usbdual.inf.OnUSBConnectListener
import com.infisense.usbir.utils.PseudocodeUtils
import com.infisense.usbir.view.TemperatureView
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.utils.DualParamsUtil
import com.topdon.module.thermal.ir.utils.IRCmdTool
import com.topdon.module.thermal.ir.utils.IRCmdTool.getSNStr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

/**
\1initialize
\1
 */
/**
 * Base i r plush activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
abstract class BaseIRPlushActivity : IRThermalNightActivity(), OnUSBConnectListener, IIRFrameCallback {
\1thermal imagingdevicesn,Textid，TextsnText，Text
    private var snStr = ""Test Data"please restart app or reinsert device"Test Data"pseudocolor/White_Hot.bin"Test Data"mjpeg"Test Data"Text")
                }
            }
            if (!SupHelp.getInstance().loadOpenclSuccess)
                {
                    return@launch
                }
            isOpenAmplify = !isOpenAmplify
            dualView?.isOpenAmplify = isOpenAmplify

            val titleView = findViewById<com.topdon.lib.core.view.TitleView>(com.topdon.lib.core.R.id.title_view)
            titleView?.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
            SaveSettingUtil.isOpenAmplify = isOpenAmplify
            if (isOpenAmplify)
                {
                    ToastUtils.showShort(R.string.tips_tisr_on)
                } else
                {
                    ToastUtils.showShort(R.string.tips_tisr_off)
                }
        }
    }

    override fun initAmplify(show: Boolean) {
        lifecycleScope.launch {
            val titleView = findViewById<com.topdon.lib.core.view.TitleView>(com.topdon.lib.core.R.id.title_view)
            titleView?.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
            withContext(Dispatchers.IO) {
                if (isOpenAmplify)
                    {
                        SupHelp.getInstance().initA4KCPP()
                    }
            }
            dualView?.isOpenAmplify = isOpenAmplify
        }
    }
}
