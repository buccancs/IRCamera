package com.topdon.module.thermal.ir.activity

import android.graphics.Bitmap
import android.view.SurfaceView
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.DualCameraParams
import com.infisense.usbdual.Const
import com.infisense.usbir.utils.IRImageHelp
import com.infisense.usbir.utils.PseudocodeUtils
import com.infisense.usbir.view.TemperatureView
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.common.ProductType.PRODUCT_NAME_TCP
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.tools.ToastTools
import com.topdon.menu.constant.TwoLightType
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.event.GalleryAddEvent
import com.topdon.module.thermal.ir.video.VideoRecordFFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
\1deviceinterface
 * @author: CaiSongL
 * @date: 2024/1/17 17:47
 */
// Legacy ARouter route annotation - now using NavigationManager
/**
 * I r thermal plus activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
class IRThermalPlusActivity : BaseIRPlushActivity() {
    private val irImageHelp by lazy {
        IRImageHelp()
    }

    // Synthetic view properties - migrated from kotlin-android-extensions
    private val dualTextureViewNativeCamera by lazy { findViewById<SurfaceView>(R.id.dualTextureViewNativeCamera) }
    // // private val thermalSteeringView by lazy { findViewById<com.topdon.lib.ui.widget.SteeringWheelView>(R.id.thermalSteeringView) }  // ID doesn'Test Data't exist in current layout
            if (isVideo) {
                isVideo = false
                videoRecord?.stopRecord()
                videoTimeClose()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    EventBus.getDefault().post(GalleryAddEvent())
                }
                lifecycleScope.launch {
                    delay(500)
                    thermalRecyclerNight.refreshImg()
                }
            }
        } catch (_: Exception) {
        } finally {
            ircmd?.onDestroy()
            ircmd = null
        }
    }

    /**
\1initialize
     */
    override fun initVideoRecordFFmpeg() {
        videoRecord =
            VideoRecordFFmpeg(
                cameraView,
                cameraPreview,
                temperatureView,
                curChooseTabPos == 1,
                cl_seek_bar,
                temp_bg,
                compassView, dualView,
                carView = layCarDetectPrompt,
            )
    }

    override fun irStart() {
        if (!isrun) {
            tvTypeInd.isVisible = false
            startUSB(false, false)
            startISP()
            isrun = true
\1Textconfiguration
            configParam()
            thermalRecyclerNight.updateCameraModel()
            initIRConfig()
        }
    }

    override fun setDispViewData(dualDisp: Int) {
        // thermalSteeringView.moveX = dualDisp
    }

    override fun autoConfig() {
        lifecycleScope.launch(Dispatchers.IO) {
            dualView?.let {
                if (!it.auto_gain_switch) {
                    switchAutoGain(true)
                    ToastTools.showShort(R.string.auto_open)
                }
                gainSelChar = CameraItemBean.TYPE_TMP_ZD
            }
        }
        dismissCameraLoading()
        thermalRecyclerNight.setTempLevel(CameraItemBean.TYPE_TMP_ZD)
    }

    override fun switchAutoGain(boolean: Boolean) {
        dualView?.auto_gain_switch = boolean
    }
}
