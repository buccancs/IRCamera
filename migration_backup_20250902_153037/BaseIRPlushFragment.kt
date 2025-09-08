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
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SizeUtils
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
import com.infisense.usbdual.Const
import com.infisense.usbdual.camera.DualViewWithExternalCameraCommonApi
import com.infisense.usbdual.camera.IRUVCDual
import com.infisense.usbdual.camera.USBMonitorManager
import com.infisense.usbdual.inf.OnUSBConnectListener
import com.infisense.usbir.extension.setAutoShutter
import com.infisense.usbir.extension.setContrast
import com.infisense.usbir.extension.setMirror
import com.infisense.usbir.extension.setPropDdeLevel
import com.infisense.usbir.utils.FileUtil
import com.infisense.usbir.utils.PseudocodeUtils
import com.infisense.usbir.utils.ScreenUtils
import com.infisense.usbir.view.ITsTempListener
import com.infisense.usbir.view.TemperatureView
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.menu.constant.SettingType
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.utils.DualParamsUtil
import com.topdon.module.thermal.ir.utils.IRCmdTool
import com.topdon.module.thermal.ir.utils.IRCmdTool.getSNStr
import kotlinx.android.synthetic.main.activity_thermal_ir_night.thermal_recycler_night
import kotlinx.android.synthetic.main.activity_thermal_ir_night.tv_type_ind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream


/**
 * dual light[Chinese text]
 * dual light[Chinese text]
 */
abstract class BaseIRPlushFragment : BaseFragment(), OnUSBConnectListener,ITsTempListener,
    IIRFrameCallback {

    val INIT_ALIGN_DATA = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)

    /**
     * [Chinese text] DualUVCCamera [Chinese text], [Chinese text].
     *
     * [Chinese text]: [Chinese text], [Chinese text] View, [Chinese text] View!
     */
    protected var dualView: DualViewWithExternalCameraCommonApi? = null

    /**
     * [Chinese text]mode, [Chinese text] IRONBOW_MODE([Chinese text])
     */
    protected var pseudoColorModeDual = CommonParams.PseudoColorUsbDualType.IRONBOW_MODE



    /**
     * [Chinese text]startvisible[Chinese text].
     * true-[Chinese text], [Chinese text]
     * false-[Chinese text].
     * [Chinese text] dualStart [Chinese text]in progress[Chinese text]in progress[Chinese text].
     */
    private var hasStartPreview = false
    protected var ircmd: IRCMD? = null

    //[Chinese text]sn,[Chinese text]id, [Chinese text]sn[Chinese text], [Chinese text]
    protected var snStr = ""

    /** [Chinese text]mode: [Chinese text]+temperature[Chinese text] */
    protected var defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT
    /**
     * ir camera
     * 22576 - 0x5830
     * 22592 - 0x5840
     */
    private var irPid = 0x5830
    private var irFps = 25
    private var irCameraWidth // [Chinese text]
            = 0
    private var irCameraHeight // [Chinese text]high[Chinese text]
            = 0
    private var irTempHeight // temperature[Chinese text]high[Chinese text]
            = 0
    private var imageWidth // [Chinese text]
            = 0
    private var imageHeight // [Chinese text]high[Chinese text]
            = 0
    protected var temperatureSrc: ByteArray ?= null

    protected var mCurrentFusionType = DualParamsUtil.fusionTypeToParams(SaveSettingUtil.fusionType)
    private var syncimage = SynchronizedBitmap()
    protected var isConfigWait = true
    protected var pseudoColorMode = SaveSettingUtil.pseudoColorMode

    /**
     * vl camera
     * 12341 - 0x3035  30 fps 640*480
     * 38704 - 0x9730  25 fps 1280*720
     * 8833
     */
    private var vlPid = 12337
    private var vlFps = 30 // [Chinese text]

    protected var vlCameraWidth = 1280
    protected var vlCameraHeight = 720
    private var vlData = ByteArray(vlCameraWidth * vlCameraHeight * 3) // [Chinese text]visible[Chinese text]

    /**
     * dual camera
     */
    private var dualCameraWidth = 480
    private var dualCameraHeight = 640

    protected var isrun = false

    // [Chinese text]IRISP[Chinese text]
    protected val isUseIRISP = false

    protected var fullScreenlayoutParams: FrameLayout.LayoutParams? = null

    protected var psedocolor: Array<ByteArray> ?= null

    protected var dualRotate = 0;

    protected var dualDisp = 30

    /**
     * camera [Chinese text]
     */
    private var vlUVCCamera: IRUVCDual? = null


    /**
     * [Chinese text]implement[Chinese text], [Chinese text]for[Chinese text] SurfaceView
     */
    abstract fun getSurfaceView(): SurfaceView

    /**
     * [Chinese text]implement[Chinese text], [Chinese text]for[Chinese text]temperature[Chinese text] TemperatureDualView
     */
    abstract fun getTemperatureDualView(): TemperatureView

    /**
     * [Chinese text]implement[Chinese text], [Chinese text] USBMonitor [Chinese text] onConnect [Chinese text], [Chinese text] DualView [Chinese text].
     */
    abstract suspend fun onDualViewCreate(dualView: DualViewWithExternalCameraCommonApi?)


    open fun initdata() {

    }

    /**
     * @param dataFlowMode
     */
    open fun initDataFlowMode(dataFlowMode: CommonParams.DataFlowMode) {
        when (dataFlowMode) {
            CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT -> {
                /**
                 * [Chinese text]+temperature
                 */
                irCameraWidth = 256 // [Chinese text]
                irCameraHeight = 384 // [Chinese text]high[Chinese text]
                irTempHeight = 192
                imageWidth = irCameraHeight - irTempHeight
                imageHeight = irCameraWidth
                temperatureSrc = ByteArray(imageWidth * imageHeight * 2)
            }
            CommonParams.DataFlowMode.IMAGE_OUTPUT -> {
                /**
                 * [Chinese text]
                 */
                irCameraWidth = 256 // [Chinese text]
                irCameraHeight = 192 // [Chinese text]high[Chinese text]
                irTempHeight = 0
                imageWidth = irCameraHeight - irTempHeight
                imageHeight = irCameraWidth
                temperatureSrc = ByteArray(imageWidth * imageHeight * 2)
            }
            CommonParams.DataFlowMode.TEMP_OUTPUT -> {
                /**
                 * temperature
                 */
                irCameraWidth = 256 // [Chinese text]
                irCameraHeight = 192 // [Chinese text]high[Chinese text]
                irTempHeight = 0
                imageWidth = irCameraHeight - irTempHeight
                imageHeight = irCameraWidth
                temperatureSrc = ByteArray(imageWidth * imageHeight * 2)
            }
            else -> {
                irCameraWidth = 256 // [Chinese text]
                irCameraHeight = 192 // [Chinese text]high[Chinese text]
                irTempHeight = 0
                imageWidth = irCameraHeight - irTempHeight
                imageHeight = irCameraWidth
                temperatureSrc = ByteArray(imageWidth * imageHeight * 2)
            }
        }
    }



    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        dualStart()
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * [Chinese text]dual light[Chinese text]
     */
    abstract fun isDualIR() : Boolean

    abstract fun setTemperatureViewType()


    override fun initView() {
        if (isDualIR()){
            getTemperatureDualView().setTextSize(SaveSettingUtil.tempTextSize)
            initDataFlowMode(defaultDataFlowMode)
            initIrDualdata()
        }
    }

    private fun initIrDualdata() {
        // [Chinese text]high, [Chinese text]
        var width = 0
        var height = 0
        val screenWidth: Int = ScreenUtils.getScreenWidth(context)
        val screenHeight: Int = ScreenUtils.getScreenHeight(context) - SizeUtils.dp2px(52f)
        if (screenWidth > screenHeight) {
            width = screenHeight * imageWidth / imageHeight
            height = screenHeight
        } else {
            width = screenWidth
            height = screenWidth * imageHeight / imageWidth
        }
        fullScreenlayoutParams = FrameLayout.LayoutParams(
            width,
            height
        )
        getSurfaceView().layoutParams = fullScreenlayoutParams
        getTemperatureDualView().layoutParams = fullScreenlayoutParams
        USBMonitorManager.getInstance().init(irPid, isUseIRISP, defaultDataFlowMode)
        USBMonitorManager.getInstance().addOnUSBConnectListener(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        mIrHandler.removeCallbacksAndMessages(null)
        USBMonitorManager.getInstance().removeOnUSBConnectListener(this)
    }

    open fun initPseudocolor() {
        val am = requireContext().assets
        var `is`: InputStream? = null
        try {
            //[Chinese text], [Chinese text], [Chinese text]sdk[Chinese text]
            psedocolor = Array(11) { ByteArray(0) }
            `is` = am.open("pseudocolor/White_Hot.bin")
            var lenth = `is`.available()
            psedocolor!![0] = ByteArray(lenth + 1)
            if (`is`.read(psedocolor!![0]) != lenth) {
                Log.d(
                    TAG,
                    "read file fail "
                )
            }
            psedocolor!![0][lenth] = 0
            dualView!!.getDualUVCCamera().loadPseudocolor(
                CommonParams.PseudoColorUsbDualType.WHITE_HOT_MODE,
                psedocolor!![0]
            )
            // [Chinese text]settings[Chinese text]mode
            setFusion(mCurrentFusionType)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    open fun setFusion(fusion : DualCameraParams.FusionType) {
        dualView?.setCurrentFusionType(fusion)
        getTemperatureDualView().setCurrentFusionType(fusion)
        if (fusion == DualCameraParams.FusionType.IROnlyNoFusion) {
            getTemperatureDualView().setImageSize(Const.IR_HEIGHT, Const.IR_WIDTH,null)
        } else {
            getTemperatureDualView().setImageSize(dualCameraWidth, dualCameraHeight,null)
        }
    }

    val calibrationDataSize = 192
    val SAVE_DUAL_BIN = "dual_calibration_parameters2.bin"

    /**
     * [Chinese text]
     */
    open fun initDefIntegralArgsDISP_VALUE(typeLoadParameters: DualCameraParams.TypeLoadParameters) {
        if (!isDualIR()){
            return
        }
        lifecycleScope.launch{
            val parameters = IRCmdTool.getDualBytes(USBMonitorManager.getInstance().ircmd)
            val data = dualView?.dualUVCCamera?.loadParameters(parameters, typeLoadParameters)
            dualDisp = IRCmdTool.dispNumber
            setDispViewData(dualDisp)
            // [Chinese text]
            dualView?.dualUVCCamera?.setDisp(dualDisp)
            dualView?.startPreview()
        }
    }
    open fun setDispViewData(dualDisp : Int){

    }
    open fun restartDualCamera() {
        if (isrun) {
            USBMonitorManager.getInstance().isReStart = true
            dualStop()
            SystemClock.sleep(200)
            dualStart()
        }
    }

    override fun onStop() {
        super.onStop()
        dualStop()
    }

    open fun dualStart() {
        if (!isDualIR()){
            return
        }
        Log.d(
            TAG,
            "dualStart"
        )
        /**
         * [Chinese text]
         * [Chinese text]pid[Chinese text]
         */
        USBMonitorManager.getInstance().registerUSB()
        //[Chinese text]USBMonitorManager onConnect[Chinese text]in progress[Chinese text]visible[Chinese text]
        //
//        getTemperatureDualView().setTemperatureRegionMode(View.FOCUSABLES_TOUCH_MODE)
        getTemperatureDualView().setUseIRISP(isUseIRISP)
        if (mCurrentFusionType == DualCameraParams.FusionType.IROnlyNoFusion) {
            getTemperatureDualView().setImageSize(Const.IR_HEIGHT, Const.IR_WIDTH,null)
        } else {
            getTemperatureDualView().setImageSize(dualCameraWidth, dualCameraHeight,null)
        }
        setTemperatureViewType()
        getTemperatureDualView().start()
    }
    /**
     *
     */
    var mIrHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.d(
                TAG,
                "USBMonitorManager [Chinese text]${msg.what}"
            )
            if (!isDualIR()){
                return
            }
            if (msg.what == Const.RESTART_USB) {
                Log.d(
                    TAG,
                    "restartDualCamera"
                )
                restartDualCamera()
            } else if (msg.what == Const.HANDLE_CONNECT) {
                Log.d(
                    TAG,
                    "USBMonitorManager HANDLE_CONNECT"
                )
                // [Chinese text], [Chinese text]
                /**
                 * [Chinese text]visible[Chinese text]
                 * [Chinese text]pid[Chinese text]
                 */
                lifecycleScope.launch(Dispatchers.Main){
                    startVLCamera(vlPid, vlFps, vlCameraWidth, vlCameraHeight)
                    initDualCamera()
                    // [Chinese text]
                    initDefIntegralArgsDISP_VALUE(DualCameraParams.TypeLoadParameters.ROTATE_270)
                }
            } else if (msg.what == Const.HANDLE_REGISTER) {
                USBMonitorManager.getInstance().registerUSB()
            } else if (msg.what == Const.SHOW_LOADING) {
                Log.d(
                    TAG,
                    "SHOW_LOADING"
                )
                showLoadingDialog()
            } else if (msg.what == Const.HIDE_LOADING) {
                Log.d(
                    TAG,
                    "HIDE_LOADING"
                )
                dismissLoadingDialog()
            } else if (msg.what == Const.SHOW_RESTART_MESSAGE) {
                Toast.makeText(
                    context,
                    "please restart app or reinsert device",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }
    open fun initDualCamera() {
        if (!isDualIR()){
            return
        }
        if (dualView != null) {
            return
        }
        Log.d(
            TAG,
            "initDualCamera"
        )
        dualView = DualViewWithExternalCameraCommonApi(
            getSurfaceView(),
            USBMonitorManager.getInstance().uvcCamera, defaultDataFlowMode,
            irCameraWidth, irCameraHeight - irTempHeight,
            vlCameraWidth, vlCameraHeight, dualCameraWidth, dualCameraHeight,
            isUseIRISP,dualRotate,this
        )
        dualView?.addFrameCallback(getTemperatureDualView())
        //
        getTemperatureDualView().setDualUVCCamera(dualView!!.getDualUVCCamera())
        initPseudocolor()
        // [Chinese text]settings[Chinese text]mode
//        setFusion(mCurrentFusionType)
//        dualView!!.startPreview()
        dualView?.setHandler(mIrHandler)
        isrun = true
    }
    /**
     * visible[Chinese text]
     *
     * @param pid          [Chinese text]pid
     * @param cameraWidth  [Chinese text]
     * @param cameraHeight [Chinese text]high
     */
    open fun startVLCamera(pid: Int, fps: Int, cameraWidth: Int, cameraHeight: Int) {
        if (!isDualIR()){
            return
        }
        Log.i(
            TAG,
            "startVLCamera"
        )
        vlUVCCamera = IRUVCDual(cameraWidth,
            cameraHeight,
            context,
            pid,
            fps,
            object : ConnectCallback {
                override fun onCameraOpened(uvcCamera: UVCCamera) {
                    Log.i(
                        TAG,
                        "ConnectCallback-startVLCamera-onCameraOpened"
                    )
                }

                override fun onIRCMDCreate(ircmd: IRCMD) {
                    setUVCCameraICMD(ircmd)
                }
            },
            IFrameCallback { frame ->
                Log.i(
                    TAG,
                    "startVLCamera-onFrame->frame.length = " + frame.size
                )
                if (dualView != null && dualView?.getDualUVCCamera() != null &&
                    Const.isDeviceConnected
                ) {
                    System.arraycopy(frame, 0, vlData, 0, vlData.size)
                    dualView?.getDualUVCCamera()?.updateFrame(
                        ImageFormat.FLEX_RGB_888, vlData, vlCameraWidth,
                        vlCameraHeight
                    )
                }
            })
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
            "ConnectCallback-startVLCamera-onIRCMDCreate"
        )
//        getTemperatureDualView().setIrcmd(ircmd)
//        popupCalibration.setIrcmd(ircmd)
//        popupImage.setIrcmd(ircmd)
//        popupOthers.setIrcmd(ircmd)
//        getTemperatureDualView().setIrcmd(ircmd)
//        // [Chinese text]settings
//        popupCalibration.setRotate(true)
//        popupImage.setRotate(true)
    }

    override fun onStart() {
        super.onStart()
        if (!isrun) {
            isrun = true
            //[Chinese text]
            configParam()
        }
    }

    private var isFirst = true
    private var configJob: Job? = null
    private val timeMillis = 150L
    //[Chinese text]
    private fun configParam() {
        configJob = lifecycleScope.launch{
            while (isConfigWait && isActive) {
                delay(200)
            }
            delay(500)
            val config = ConfigRepository.readConfig(false)
            val disChar = (config.distance * 128).toInt() //[Chinese text]([Chinese text])
            val emsChar = (config.radiation * 128).toInt() //[Chinese text]
            XLog.w("settingsTPD_PROP DISTANCE:${disChar}, EMS:${emsChar}}")
            delay(timeMillis)
            //[Chinese text]
            /// Emissivity property. unit:1/128, range:1-128(0.01-1)
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_EMS,
                CommonParams.PropTPDParamsValue.NumberType(emsChar.toString())
            )
            delay(timeMillis)
            //[Chinese text]
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_DISTANCE,
                CommonParams.PropTPDParamsValue.NumberType(disChar.toString())
            )
            // [Chinese text]
            delay(timeMillis)
            XLog.w("settingsTPD_PROP DISTANCE:${disChar}, EMS:${emsChar}}")
            if (isFirst && isrun) {
                //[Chinese text]
                ircmd?.setMirror(false)
                // [Chinese text]
                delay(timeMillis)
                withContext(Dispatchers.IO) {
                    // [Chinese text], [Chinese text]
                    ircmd?.setAutoShutter(true)
                    isFirst = false
                }
                //[Chinese text]([Chinese text])
                ircmd?.setPropDdeLevel(2)
                //[Chinese text]
                ircmd?.setContrast(128)
            }
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_ONOFF_AGC,
                CommonParams.PropImageParamsValue.StatusSwith.ON
            )
            //[Chinese text]
            if (syncimage.type == 1) {
                ircmd?.tc1bShutterManual()
            } else {
                ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
            }
            XLog.w("settingsTPD_PROP DISTANCE2:${disChar}, EMS:${emsChar}}")
        }
    }


    open fun dualStop() {
        if (!isDualIR()){
            return
        }
        Log.d(
            TAG,
            "USBMonitorManager dualStop"
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
                "[Chinese text] dualStop"
            )
        }
    }

    override fun onAttach(device: UsbDevice?) {
        Log.d(
            TAG,
            "USBMonitorManager onAttach"
        )
    }

    override fun onGranted(usbDevice: UsbDevice?, granted: Boolean) {
        Log.d(
            TAG,
            "USBMonitorManager onGranted"
        )
    }

    override fun onDettach(device: UsbDevice?) {
        Log.d(
            TAG,
            "USBMonitorManager onDettach"
        )
    }

    override fun onConnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?, createNew: Boolean) {
        Log.d(
            TAG,
            "USBMonitorManager onConnect[Chinese text]"
        )
        mIrHandler.sendEmptyMessage(Const.HANDLE_CONNECT)
    }

    override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
        XLog.d(
            TAG,
            "USBMonitorManager onDisconnect"
        )
    }

    override fun onCancel(device: UsbDevice?) {
        Log.d(
            TAG,
            "USBMonitorManager onCancel"
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
    //[Chinese text]ARGB[Chinese text] 192 * 256 * 4
    protected val preIrARGBData = ByteArray(256*192*4)
    protected val preIrData = ByteArray(256*192*2)
    protected val preTempData = ByteArray(256*192*2)

    override fun onIrFrame(irFrame: ByteArray?): ByteArray {
        /**
         * @param irFrame [Chinese text]YUV422[Chinese text] + temperature[Chinese text] [Chinese text] irWidth * irHeight * 2 + irWidth * irHeight * 2
         * @return
         */
        System.arraycopy(irFrame, 0, preIrData, 0, preIrData.size);
        LibIRProcess.convertYuyvMapToARGBPseudocolor(
            preIrData, (Const.IR_WIDTH * Const.IR_HEIGHT).toLong(),
            PseudocodeUtils.changePseudocodeModeByOld(pseudoColorMode), preIrARGBData
        )
        return preIrARGBData
    }


}