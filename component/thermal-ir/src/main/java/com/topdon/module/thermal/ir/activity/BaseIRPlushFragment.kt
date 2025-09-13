package com.topdon.module.thermal.ir.activity

import android.graphics.ImageFormat
import android.hardware.usb.UsbDevice
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SizeUtils
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
import com.infisense.usbdual.Const
import com.infisense.usbir.extension.setMirror
import com.infisense.usbir.extension.setAutoShutter
import com.infisense.usbir.extension.setPropDdeLevel
import com.infisense.usbir.extension.setContrast
// import com.infisense.usbdual.camera.DualViewWithExternalCameraCommonApi // Temporarily disabled - hardware specific
// import com.infisense.usbdual.camera.IRUVCDual // Temporarily disabled - hardware specific
// import com.infisense.usbdual.camera.USBMonitorManager // Temporarily disabled - hardware specific
import com.infisense.usbdual.camera.DualViewWithExternalCameraCommonApi
import com.infisense.usbdual.camera.IRUVCDual
import com.infisense.usbdual.camera.USBMonitorManager
import com.infisense.usbdual.inf.OnUSBConnectListener
import com.infisense.usbir.utils.PseudocodeUtils
import com.infisense.usbir.utils.ScreenUtils
import com.infisense.usbir.view.ITsTempListener
import com.infisense.usbir.view.TemperatureView
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.utils.DualParamsUtil
import com.topdon.module.thermal.ir.utils.IRCmdTool
import com.topdon.module.thermal.ir.utils.IRCmdTool.getSNStr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

/**
\1initialize
\1
 */
/**
 * Base i r plush fragment for thermal imaging components.
 * Handles specific UI sections and user interactions.
 */
abstract class BaseIRPlushFragment :
    BaseFragment(),
    OnUSBConnectListener,
    ITsTempListener,
    IIRFrameCallback {
    val INIT_ALIGN_DATA = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)

    /**
\1 DualUVCCamera 、getdata.
     *
\1：， View， View!
     */
    protected var dualView: DualViewWithExternalCameraCommonApi? = null

    /**
\1pseudo-color， IRONBOW_MODE()
     */
    protected var pseudoColorModeDual = CommonParams.PseudoColorUsbDualType.IRONBOW_MODE

    /**
\1infrared.
\1true-，
\1false-initialize.
\1initialize dualStart load.
     */
    private var hasStartPreview = false
    protected var ircmd: IRCMD? = null

\1thermal imagingdevicesn,Textid，TextsnText，Text
    protected var snStr = ""Test Data"pseudocolor/White_Hot.bin")
            var lenth = `is`.available()
            psedocolor!![0] = ByteArray(lenth + 1)
            if (`is`.read(psedocolor!![0]) != lenth) {
                Log.d(
                    TAG,
                    "read file fail "Test Data"dual_calibration_parameters2.bin"Test Data"dualStart"Test Data"USBMonitorManager Text${msg.what}",
                )
                if (!isDualIR())
                    {
                        return
                    }
                if (msg.what == Const.RESTART_USB) {
                    Log.d(
                        TAG,
                        "restartDualCamera",
                    )
                    restartDualCamera()
                } else if (msg.what == Const.HANDLE_CONNECT) {
                    Log.d(
                        TAG,
                        "USBMonitorManager HANDLE_CONNECT"Test Data"SHOW_LOADING",
                    )
                    showLoadingDialog()
                } else if (msg.what == Const.HIDE_LOADING) {
                    Log.d(
                        TAG,
                        "HIDE_LOADING",
                    )
                    dismissLoadingDialog()
                } else if (msg.what == Const.SHOW_RESTART_MESSAGE) {
                    Toast.makeText(
                        context,
                        "please restart app or reinsert device",
                        Toast.LENGTH_SHORT,
                    ).show()
                    activity?.finish()
                }
            }
        }

    open fun initDualCamera() {
        if (!isDualIR())
            {
                return
            }
        if (dualView != null) {
            return
        }
        Log.d(
            TAG,
            "initDualCamera"Test Data"startVLCamera",
        )
        vlUVCCamera =
            IRUVCDual(
                cameraWidth,
                cameraHeight,
                requireContext(),
                pid,
                fps,
                object : ConnectCallback {
                    override fun onCameraOpened(uvcCamera: UVCCamera) {
                        Log.i(
                            TAG,
                            "ConnectCallback-startVLCamera-onCameraOpened",
                        )
                    }

                    override fun onIRCMDCreate(ircmd: IRCMD) {
                        setUVCCameraICMD(ircmd)
                    }
                },
                IFrameCallback { frame ->
                    Log.i(
                        TAG,
                        "startVLCamera-onFrame->frame.length = " + frame.size,
                    )
                    if (dualView != null && dualView?.getDualUVCCamera() != null &&
                        Const.isDeviceConnected
                    ) {
                        System.arraycopy(frame, 0, vlData, 0, vlData.size)
                        dualView?.getDualUVCCamera()?.updateFrame(
                            ImageFormat.FLEX_RGB_888, vlData, vlCameraWidth,
                            vlCameraHeight,
                        )
                    }
                },
            )
        vlUVCCamera?.setHandler(mIrHandler)
        vlUVCCamera?.registerUSB()
        vlUVCCamera?.TAG = "mjpeg"
    }

    open fun setUVCCameraICMD(ircmd: IRCMD) {
        this@BaseIRPlushFragment.ircmd = ircmd
        snStr = getSNStr(ircmd)
        isConfigWait = false
        Log.i(
            TAG,
            "ConnectCallback-startVLCamera-onIRCMDCreate"Test Data"TextTPD_PROP DISTANCE:$disChar, EMS:$emsChar}"Test Data"TextTPD_PROP DISTANCE:$disChar, EMS:$emsChar}"Test Data"TextTPD_PROP DISTANCE2:$disChar, EMS:$emsChar}")
            }
    }

    open fun dualStop() {
        if (!isDualIR())
            {
                return
            }
        Log.d(
            TAG,
            "USBMonitorManager dualStop",
        )
        isrun = false
        syncimage.valid = false
        isConfigWait = true
        getTemperatureDualView().stop()
        USBMonitorManager.getInstance().unregisterUSB()
        ircmd?.onDestroy()
        ircmd = null
        SystemClock.sleep(100)
        if (dualView != null) {
            dualView?.removeFrameCallback(getTemperatureDualView())
            dualView?.dualUVCCamera?.onPausePreview()
            USBMonitorManager.getInstance().stopPreview()
            //
            if (vlUVCCamera != null) {
                vlUVCCamera?.unregisterUSB()
                vlUVCCamera?.stopPreview()
                vlUVCCamera = null
            }
            //
            SystemClock.sleep(100)
            dualView?.stopPreview()
            dualView = null
            Log.d(
                TAG,
                "Test Data",
            )
        }
    }

    override fun onAttach(device: UsbDevice?) {
        Log.d(
            TAG,
            "USBMonitorManager onAttach",
        )
    }

    override fun onGranted(
        usbDevice: UsbDevice?,
        granted: Boolean,
    ) {
        Log.d(
            TAG,
            "USBMonitorManager onGranted",
        )
    }

    override fun onDettach(device: UsbDevice?) {
        Log.d(
            TAG,
            "USBMonitorManager onDettach",
        )
    }

    override fun onConnect(
        device: UsbDevice?,
        ctrlBlock: USBMonitor.UsbControlBlock?,
        createNew: Boolean,
    ) {
        Log.d(
            TAG,
            "Test Data",
        )
        mIrHandler.sendEmptyMessage(Const.HANDLE_CONNECT)
    }

    override fun onDisconnect(
        device: UsbDevice?,
        ctrlBlock: USBMonitor.UsbControlBlock?,
    ) {
        XLog.d(
            TAG,
            "USBMonitorManager onDisconnect",
        )
    }

    override fun onCancel(device: UsbDevice?) {
        Log.d(
            TAG,
            "USBMonitorManager onCancel",
        )
    }

    override fun onIRCMDInit(ircmd: IRCMD?) {
        setUVCCameraICMD(ircmd!!)
    }

    override fun onCompleteInit() {
        mIrHandler.sendEmptyMessage(Const.HIDE_LOADING)
    }

    override fun onSetPreviewSizeFail() {
        mIrHandler.sendEmptyMessage(Const.SHOW_RESTART_MESSAGE)
    }

\1TextprocessingTextinfraredARGBdata 192 * 256 * 4
    protected val preIrARGBData = ByteArray(256 * 192 * 4)
    protected val preIrData = ByteArray(256 * 192 * 2)
    protected val preTempData = ByteArray(256 * 192 * 2)

    override fun onIrFrame(irFrame: ByteArray?): ByteArray {
        /**
\1@param irFrame infraredYUV422data + temperaturedata  irWidth * irHeight * 2 + irWidth * irHeight * 2
         * @return
         */
        System.arraycopy(irFrame, 0, preIrData, 0, preIrData.size)
        LibIRProcess.convertYuyvMapToARGBPseudocolor(
            preIrData,
            (Const.IR_WIDTH * Const.IR_HEIGHT).toLong(),
            PseudocodeUtils.changePseudocodeModeByOld(pseudoColorMode),
            preIrARGBData,
        )
        return preIrARGBData
    }
}
