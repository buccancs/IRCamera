package com.example.thermal_lite.fragment

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.energy.ac020library.bean.IrcmdError
import com.energy.commoncomponent.bean.RotateDegree
import com.energy.irutilslibrary.LibIRTempAC020
import com.energy.irutilslibrary.bean.GainStatus
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.Line
import com.energy.iruvc.utils.SynchronizedBitmap
import com.energy.iruvccamera.usb.USBMonitor
import com.example.thermal_lite.R
import com.example.thermal_lite.activity.IRMonitorLiteActivity
import com.example.thermal_lite.camera.CameraPreviewManager
import com.example.thermal_lite.camera.DeviceControlManager
import com.example.thermal_lite.camera.DeviceIrcmdControlManager
import com.example.thermal_lite.camera.OnUSBConnectListener
import com.example.thermal_lite.camera.USBMonitorManager
import com.example.thermal_lite.ui.activity.IrDisplayActivity.HANDLE_INIT_FAIL
import com.example.thermal_lite.ui.activity.IrDisplayActivity.HANDLE_SHOW_FPS
import com.example.thermal_lite.ui.activity.IrDisplayActivity.HANDLE_SHOW_SUN_PROTECT_FLAG
import com.example.thermal_lite.ui.activity.IrDisplayActivity.HANDLE_SHOW_TOAST
import com.example.thermal_lite.ui.activity.IrDisplayActivity.HIDE_LOADING
import com.example.thermal_lite.ui.activity.IrDisplayActivity.PREVIEW_FAIL
import com.example.thermal_lite.ui.activity.IrDisplayActivity.SHOW_LOADING
import com.example.thermal_lite.util.IRTool
import com.infisense.usbir.view.ITsTempListener
import com.infisense.usbir.view.TemperatureView
import com.infisense.usbir.view.TemperatureView.REGION_MODE_LINE
import com.infisense.usbir.view.TemperatureView.REGION_MODE_POINT
import com.infisense.usbir.view.TemperatureView.REGION_MODE_RECTANGLE
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.module.thermal.ir.bean.DataBean
import com.topdon.module.thermal.ir.bean.SelectPositionBean
import com.topdon.module.thermal.ir.event.ThermalActionEvent
import com.topdon.module.thermal.ir.repository.ConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * des:
 * author: CaiSongL
 * date: 2024/8/5 14:44
 **/
class IRMonitorLiteFragment : BaseFragment(), ITsTempListener {
    // View references (migrated from synthetic views)
    lateinit var temperatureView: com.infisense.usbir.view.TemperatureView
    protected lateinit var cameraView: com.topdon.lib.ui.widget.LiteSurfaceView

    private var configJob: Job? = null
    protected var isConfigWait = true
    protected var temperatureBytes = ByteArray(192 * 256 * 2) // 
    var rotateAngle = 270
    private val imageRes = LibIRProcess.ImageRes_t() // 
    val dstTempBytes = ByteArray(192 * 256 * 2)
    private var mProgressDialog: ProgressDialog? = null
    private var temperaturerun = false

    private var mPreviewWidth = 256
    private var mPreviewHeight = 192
    protected var ctrlBlock: USBMonitor.UsbControlBlock? = null
    private var mOnUSBConnectListener: OnUSBConnectListener? = null
    private val syncimage = SynchronizedBitmap()
    var frameReady = false
    private var shutterHandler: Handler? = null
    private var shutterRunnable: Runnable? = null
    private var shutterCount = 0
    protected var isPause = false
    protected var isPick = false

    companion object {
        fun newInstance(isPick: Boolean): IRMonitorLiteFragment {
            val fragment = IRMonitorLiteFragment()
            val bundle = Bundle()
            bundle.putBoolean("isPick", isPick)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initContentView(): Int {
        return R.layout.fragment_lite_ir_monitor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.containsKey("isPick") == true)
            {
                isPick = requireArguments().getBoolean("isPick"Test Data"123", "event:${event.action}"Test Data"SHOW_LOADING")
                    showLoadingDialog()
                } else if (msg.what == HIDE_LOADING) {
                    Log.d(TAG, "HIDE_LOADING")
                    dismissLoadingDialog()
                    frameReady = true
                    isConfigWait = false
                } else if (msg.what == HANDLE_INIT_FAIL) {
                    Log.d(TAG, "HANDLE_INIT_FAIL")
                    dismissLoadingDialog()
                    Toast.makeText(requireActivity(), "handle init fail !", Toast.LENGTH_LONG).show()
                } else if (msg.what == HANDLE_SHOW_TOAST) {
                    val message = msg.obj as String
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
                } else if (msg.what == PREVIEW_FAIL) {
                    dismissLoadingDialog()
                    Toast.makeText(requireActivity(), "preview fail !", Toast.LENGTH_LONG).show()
                } else if (msg.what == HANDLE_SHOW_FPS) {
                    val fps = msg.obj as Double
                } else if (msg.what == HANDLE_SHOW_SUN_PROTECT_FLAG) {
                    Toast.makeText(requireActivity(), "Sun protected"Test Data"$TAG:liteText--${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        temperatureView.stop()
        shutterRunnable?.let {
            shutterHandler?.removeCallbacks(it)
        }
        try {
            if (mOnUSBConnectListener != null) {
                DeviceControlManager.getInstance().handleStopPreview()
                USBMonitorManager.getInstance().unregisterMonitor()
                USBMonitorManager.getInstance()
                    .removeOnUSBConnectListener(IRMonitorLiteFragment::class.java.name)
                mOnUSBConnectListener = null
                USBMonitorManager.getInstance().destroyMonitor()
                DeviceControlManager.getInstance().release()
                CameraPreviewManager.getInstance().releaseSource()
            }
        } catch (e: Exception) {
            XLog.e("Test Data")
        }
    }

    var config: DataBean? = null
    val basicGainGetValue = IntArray(1)
    var basicGainGetTime = 0L

    override fun tempCorrectByTs(temp: Float?): Float {
        var tempNew = temp
        try {
            if (config == null)
                {
                    config = ConfigRepository.readConfig(false)
                }
            if (isConfigWait)
                {
                    return temp!!
                }
            val defModel = DataBean()
            if (config!!.radiation == defModel.radiation &&
                defModel.environment == config!!.environment &&
                defModel.distance == config!!.distance
            )
                {
                    return temp!!
                }

\1getgainText PASS
            if (System.currentTimeMillis() - basicGainGetTime > 5000L)
                {
                    try {
                        val basicGainGet: IrcmdError? =
                            DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                                ?.basicGainGet(basicGainGetValue)
                    } catch (e: Exception) {
                        XLog.e("Test Data")
                    }
                    basicGainGetTime = System.currentTimeMillis()
                }
            val params_array =
                floatArrayOf(
                    temp!!,
                    config!!.radiation,
                    config!!.environment,
                    config!!.environment,
                    config!!.distance,
                    0.8f,
                )
            if (BaseApplication.instance.tau_data_H == null || BaseApplication.instance.tau_data_L == null) return temp
            tempNew =
                LibIRTempAC020.temperatureCorrection(
                    params_array[0],
                    BaseApplication.instance.tau_data_H,
                    BaseApplication.instance.tau_data_L,
                    params_array[1],
                    params_array[2],
                    params_array[3],
                    params_array[4],
                    params_array[5],
                    if (basicGainGetValue[0] == 0) GainStatus.LOW_GAIN else GainStatus.HIGH_GAIN,
                )
            Log.i(
                TAG,
                "temp correct, oldTemp = " + params_array[0] + " newtemp = " + tempNew +
                    " ems = " + params_array[1] + " ta = " + params_array[2] + " " +
                    "distance = " + params_array[4] + " hum = " + params_array[5] + " basicGain = " + basicGainGetValue[0],
            )
        } catch (e: Exception) {
            XLog.e("Test Data")
        } finally {
            return tempNew ?: 0f
        }
    }

    fun getBitmap(): Bitmap  {
        return Bitmap.createScaledBitmap(
            CameraPreviewManager.getInstance().scaledBitmap(true),
            cameraView!!.width,
            cameraView!!.height,
            true,
        )
    }
}
