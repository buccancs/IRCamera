package com.example.thermal_lite.activity
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.hardware.SensorManager
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.energy.ac020library.bean.IrcmdError
import com.energy.commoncomponent.bean.RotateDegree
import com.energy.irutilslibrary.LibIRTempAC020
import com.energy.irutilslibrary.bean.GainStatus
import com.energy.iruvc.sdkisp.LibIRProcess
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.SynchronizedBitmap
import com.energy.iruvccamera.usb.USBMonitor
import com.example.thermal_lite.IrConst
import com.example.thermal_lite.R
import com.example.thermal_lite.camera.CameraPreviewManager
import com.example.thermal_lite.camera.DeviceControlManager
import com.example.thermal_lite.camera.DeviceIrcmdControlManager
import com.example.thermal_lite.camera.OnUSBConnectListener
import com.example.thermal_lite.camera.TempCompensation
import com.example.thermal_lite.camera.USBMonitorManager
import com.example.thermal_lite.databinding.ActivityIrThermalLiteBinding
import com.example.thermal_lite.util.CommonUtil
import com.example.thermal_lite.util.IRTool
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.infisense.usbir.inf.ILiteListener
import com.infisense.usbir.utils.ViewStubUtils
import com.infisense.usbir.view.ITsTempListener
import com.infisense.usbir.view.TemperatureView
import com.infisense.usbir.view.TemperatureView.*
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.comm.IrParam
import com.topdon.lib.core.common.ProductType
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.common.SharedManager.getTemperature
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.EmissivityTipPopup
import com.topdon.lib.core.dialog.NotTipsSelectDialog
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.dialog.TipEmissivityDialog
import com.topdon.lib.core.dialog.TipShutterDialog
import com.topdon.lib.core.navigation.NavigationManager
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.lib.core.tools.CheckDoubleClick
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.SpanBuilder
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.tools.ToastTools
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.utils.BitmapUtils
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.ImageUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.dialog.TipPreviewDialog
import com.topdon.lib.ui.widget.seekbar.OnRangeChangedListener
import com.topdon.lib.ui.widget.seekbar.RangeSeekBar
import com.topdon.libcom.AlarmHelp
import com.topdon.libcom.dialog.ColorPickDialog
import com.topdon.libcom.dialog.TempAlarmSetDialog
import com.topdon.lms.sdk.LMS.mContext
import com.topdon.menu.constant.FenceType
import com.topdon.menu.constant.SettingType
import com.topdon.menu.constant.TwoLightType
import com.topdon.module.thermal.ir.activity.BaseIRActivity
import com.topdon.module.thermal.ir.adapter.CameraItemAdapter
import com.topdon.module.thermal.ir.bean.DataBean
import com.topdon.module.thermal.ir.event.GalleryAddEvent
import com.topdon.module.thermal.ir.frame.FrameStruct
import com.topdon.module.thermal.ir.popup.SeekBarPopup
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.utils.IRConfigData
import com.topdon.module.thermal.ir.video.VideoRecordFFmpeg
import com.topdon.module.thermal.ir.view.TimeDownView
import com.topdon.pseudo.activity.PseudoSetActivity
import com.topdon.pseudo.bean.CustomPseudoBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import kotlin.math.abs

/**
\1TC007 .
 *
 * Created by LCG on 2024/4/28.
 */
// Legacy ARouter route annotation - now using NavigationManager
class IRThermalLiteActivity : BaseIRActivity(), ITsTempListener, ILiteListener {
    companion object {
        // Handler message constants
        const val HANDLE_INIT_FAIL = 1001
        const val HANDLE_SHOW_FPS = 1002
        const val HANDLE_SHOW_SUN_PROTECT_FLAG = 1003
        const val HANDLE_SHOW_TOAST = 1004
        const val HIDE_LOADING = 1005
        const val PREVIEW_FAIL = 1006
        const val SHOW_LOADING = 1007
    }

    private lateinit var binding: ActivityIrThermalLiteBinding

    // View declarations using binding pattern - converted from findViewById
    private val spaceChart by lazy { binding.spaceChart }

    // private val thermalTrendTips by lazy { binding.thermalTrendTips } // View not found in layout
    private val tvTypeInd by lazy { binding.tvTypeInd }
    private val popTimeText by lazy { binding.popTimeText }
    private val popTimeLay by lazy { binding.popTimeLay }
    private var mPreviewWidth = 256
    private var mPreviewHeight = 192
    private var mPreviewLayoutParams: RelativeLayout.LayoutParams? = null
    private var mOnUSBConnectListener: OnUSBConnectListener? = null
    private var mProgressDialog: ProgressDialog? = null

    private var pseudoColorMode = SaveSettingUtil.pseudoColorMode

    /**
\1-fusion、set- PopupWindow，.
     */
    private var popupWindow: PopupWindow? = null

    private var tempAlarmSetDialog: TempAlarmSetDialog? = null
    private var alarmBean = SaveSettingUtil.alarmBean
    private var temperatureMode: Int = SaveSettingUtil.temperatureMode // 

    private var upColor = 0
    private var downColor = 0
    private var editMaxValue = Float.MAX_VALUE
    private var editMinValue = Float.MIN_VALUE
    private var realLeftValue = -1f
    private var realRightValue = -1f
    private val syncimage = SynchronizedBitmap()
    private val cameraWidth = 256
    private val cameraHeight = 384
    private val tempHeight = 192
    private var imageWidth = cameraWidth
    private var imageHeight = cameraHeight - tempHeight

    private var initRotate = 0 // 
    private var correctRotate = 0 // 

    @Volatile
    private var temperatureBytes = ByteArray(imageWidth * imageHeight * 2) // 
    private var temperaturerun = false
    private var isShowC: Boolean = false
    private var customPseudoBean = CustomPseudoBean.loadFromShared()
    private var isVideo = false
    private var videoRecord: VideoRecordFFmpeg? = null
    var isTouchSeekBar = false
    private val imageRes = LibIRProcess.ImageRes_t() // 
    private val dstTempBytes = ByteArray(192 * 256 * 2)

    /**
\1temperature measurement-high temperature、low temperature，low temperaturedialogdisplayhide
     */
    @Volatile
    private var isTempShowDialog = false

\1high/low gain 1:Textgain 0: Textgain
    private var gainSelChar = -2

    private var storageRequestType = 0
    private var autoJob: Job? = null
    var isAutoCamera = false
    var isOpenPreview = false // 
    private var flow: Job? = null
    private var ctrlBlock: USBMonitor.UsbControlBlock? = null
    private var cameraAlpha = SaveSettingUtil.twoLightAlpha
    private var isRecordAudio = SaveSettingUtil.isRecordAudio
    private var showCameraSetting = false
    private val cameraItemBeanList by lazy {
        mutableListOf(
            CameraItemBean(
                "Test Data",
                CameraItemBean.TYPE_DELAY,
                time = SaveSettingUtil.delayCaptureSecond,
            ),
            CameraItemBean(
                "Test Data",
                CameraItemBean.TYPE_ZDKM,
                isSel = SaveSettingUtil.isAutoShutter,
            ),
            CameraItemBean("Test Data", CameraItemBean.TYPE_SDKM),
            CameraItemBean(
                "Test Data",
                CameraItemBean.TYPE_AUDIO,
                isSel =
                    SaveSettingUtil.isRecordAudio &&
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO,
                        )
                        == PackageManager.PERMISSION_GRANTED,
            ),
            CameraItemBean("Test Data", CameraItemBean.TYPE_SETTING),
        )
    }
    private var cameraItemAdapter: CameraItemAdapter? = null
    private var audioPosition: Int = 0
    private var isAutoShutter: Boolean = SaveSettingUtil.isAutoShutter
    private var isConfigWait = true
    private var configJob: Job? = null
    private var isRotation = false
    private lateinit var orientationEventListener: OrientationEventListener
    private var mOrientation = 0
    private var isReverseRotation = true
    private var isPause = false

    private fun getPermissionList(): MutableList<String> {
        return if (this.applicationInfo.targetSdkVersion >= 34) {
            mutableListOf(
                Permission.WRITE_EXTERNAL_STORAGE,
            )
        } else if (this.applicationInfo.targetSdkVersion == 33) {
            mutableListOf(
                Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_IMAGES,
                Permission.WRITE_EXTERNAL_STORAGE,
            )
        } else {
            mutableListOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun initContentView(): Int {
        binding = ActivityIrThermalLiteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0 // Return dummy value since setContentView is already called
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        USBMonitorManager.getInstance().registerMonitor()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            if (BaseApplication.instance.tau_data_H == null)
                {
                    BaseApplication.instance.tau_data_H = CommonUtil.getAssetData(mContext, IrConst.TAU_HIGH_GAIN_ASSET_PATH)
                }
            if (BaseApplication.instance.tau_data_L == null)
                {
                    BaseApplication.instance.tau_data_L = CommonUtil.getAssetData(mContext, IrConst.TAU_LOW_GAIN_ASSET_PATH)
                }
        }
    }

    /**
\1IOS drawing，。
     */
    private var hasClickTrendDel = true

    @SuppressLint("SetTextI18n")
    override fun initView() {
        imageRes.width = 256.toChar()
        imageRes.height = 192.toChar()
        isShowC = getTemperature() == 1
        binding.temperatureSeekbar.setIndicatorTextDecimalFormat("0.0")
        initPreviewManager()
        val imageRotate =
            when (saveSetBean.rotateAngle) {
                270 -> RotateDegree.DEGREE_270
                90 -> RotateDegree.DEGREE_90
                180 -> RotateDegree.DEGREE_0
                else -> RotateDegree.DEGREE_180
            }
        CameraPreviewManager.getInstance().imageRotate = imageRotate
        initCameraSize()
        initUSBMonitorManager()
        DeviceControlManager.getInstance().init()
        binding.titleView.setLeftClickListener {
            if (binding.timeDownView.isRunning) {
                return@setLeftClickListener
            }
            setResult(200)
            finish()
        }
        binding.titleView.setRightClickListener {
            val config = ConfigRepository.readConfig(false)
            var text = ""
            for (tmp in IRConfigData.irConfigData(this)) {
                if (config.radiation.toString() == tmp.value)
                    {
                        if (text.isEmpty())
                            {
                                text = "${resources.getString(com.topdon.module.thermal.ir.R.string.tc_temp_test_materials)} : "
                            }
                        text += "${tmp.name}/"
                    }
            }
            if (text.isNotEmpty())
                {
                    text = text.substring(0, text.length - 1)
                }
            EmissivityTipPopup(this@IRThermalLiteActivity, false)
                .setDataBean(config.environment, config.distance, config.radiation, text)
                .build()
                .showAsDropDown(binding.titleView, 0, 0, Gravity.END)
        }
        // Car detect click listener - commented out due to missing viewCarDetect in layout
        // binding.viewCarDetect?.setOnClickListener {
        //     LongTextDialog(this, SharedManager.getCarDetectInfo().item, SharedManager.getCarDetectInfo()?.description).show()
        // }
        binding.cameraPreview.cameraPreViewCloseListener = {
            if (isOpenPreview) {
                popupWindow?.dismiss()
                cameraPreviewConfig(false)
            }
        }
        binding.viewMenuFirst.onTabClickListener = {
            ViewStubUtils.showViewStub(binding.viewStubCamera, false, null)
            popupWindow?.dismiss()
            binding.temperatureView.isEnabled = it.selectPosition == 1
            binding.thermalRecyclerNight.selectPosition(it.selectPosition + (if (it.isObserveMode) 10 else 0))
        }
        binding.temperatureIvLock.setOnClickListener {
            if (binding.temperatureIvLock.visibility != View.VISIBLE) {
                return@setOnClickListener
            }
            if (binding.temperatureIvLock.contentDescription == "lock"Test Data"Text", e.message.toString())
                        }
                        try {
                            binding.tvTempContent.text = "Max:${UnitTools.showC(max, isShowC)}\nMin:${UnitTools.showC(min, isShowC)}"
                        } catch (e: Exception) {
                            Log.e("Test Data", e.message.toString())
                        }
                    } else {
\1Textrendering
                        try {
                            binding.tvTempContent.text = " Max:${UnitTools.showC(max, isShowC)}\nMin:${UnitTools.showC(min, isShowC)}"
                        } catch (e: Exception) {
                            Log.e("Test Data", e.message.toString())
                        }
                    }
                    try {
                        if (isVideo) {
                            binding.clSeekBar.requestLayout()
                            binding.clSeekBar.updateBitmap()
                        }
                    } catch (e: Exception) {
                        Log.w("Test Data", "${e.message}")
                    }
                    try {
                        AlarmHelp.getInstance(application).alarmData(max, min, binding.tempBg)
                    } catch (e: Exception) {
                        Log.e("Test Data", e.message.toString())
                    }
                }
            }
        addTemperatureListener()
        if (SaveSettingUtil.isOpenTwoLight)
            {
                cameraPreviewConfig(false)
            }
        lifecycleScope.launch {
            delay(1000)
            if (!SharedManager.isHideEmissivityTips)
                {
                    showEmissivityTips()
                }
        }
//
//        handler = Handler(Looper.getMainLooper())
\1// 
//        fun takePicture() {
//            shutterCount++
//            try {
//                IRTool.setOneShutter()
//            }catch (e : Exception){
//            }
//        }
\1// create Runnable，5
//        shutterRunnable = object : Runnable {
//            override fun run() {
\1if (shutterCount < 4) { // 40（8）
\1handler?.postDelayed(this, 5000L) // 5
//                    takePicture()
//                }
//            }
//        }
\1// 
//        handler?.postDelayed(shutterRunnable!!,5000L)
        initOrientationEventListener()

        binding.ivTrendClose.setOnClickListener {
            binding.clTrendOpen.isVisible = false
            binding.llTrendClose.isVisible = true
        }
        binding.ivTrendOpen.setOnClickListener {
            binding.clTrendOpen.isVisible = true
            binding.llTrendClose.isVisible = false
        }
    }

    private fun initOrientationEventListener() {
        orientationEventListener =
            object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                        return
                    }
                    startOrientation()
                    if (mOrientation == 1) {
                        return
                    }
                    requestedOrientation =
                        if ((orientation in 315..360) || (orientation in 0..45)) {
                            if (isRotation && saveSetBean.rotateAngle != 270) {
                                saveSetBean.rotateAngle = 270
                                updateRotateAngle(saveSetBean.rotateAngle)
                                isRotation = !isRotation
                                isReverseRotation = true
                                binding.cameraPreview?.setRotation(false)
                            }
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            (
                                if (orientation in 135..225) {
                                    if (isReverseRotation && saveSetBean.rotateAngle != 90) {
                                        saveSetBean.rotateAngle = 90
                                        updateRotateAngle(saveSetBean.rotateAngle)
                                        isReverseRotation = !isReverseRotation
                                        isRotation = true
                                        binding.cameraPreview?.setRotation(true)
                                    }
                                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                                } else {
                                    isRotation = true
                                    isReverseRotation = true
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                }
                            )
                        }
                }
            }
        orientationEventListener.enable()
    }

    private fun startOrientation() {
        orientationEventListener.enable()
        mOrientation =
            if (Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 0) {
                1
            } else {
                2
            }
    }

    private fun showEmissivityTips()  {
        val config = ConfigRepository.readConfig(false)
        var text = ""
        for (tmp in IRConfigData.irConfigData(this)) {
            if (config.radiation.toString() == tmp.value)
                {
                    if (text.isEmpty())
                        {
                            text = "${resources.getString(com.topdon.module.thermal.ir.R.string.tc_temp_test_materials)} : "
                        }
                    text += "${tmp.name}/"
                }
        }
        if (text.isNotEmpty())
            {
                text = text.substring(0, text.length - 1)
            }
        val dialog =
            TipEmissivityDialog.Builder(this@IRThermalLiteActivity)
                .setDataBean(config.environment, config.distance, config.radiation, text)
                .create()
        dialog.onDismissListener = {
            SharedManager.isHideEmissivityTips = it
        }
        dialog.show()
    }

    private fun addTemperatureListener() {
        binding.temperatureIvLock.setOnClickListener {
            if (binding.temperatureIvLock.visibility != View.VISIBLE) {
                return@setOnClickListener
            }
            if (binding.temperatureIvLock.contentDescription == "lock"Test Data"unlock" else "lock"Test Data"")
                                }
                            } else {
                                binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                                ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                            }
                        } catch (e: Exception) {
                            XLog.e("Test Data" + e.message)
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
\1Text
                            if (BaseApplication.instance.isDomestic())
                                {
                                    ToastUtils.showShort(getString(com.topdon.module.thermal.ir.R.string.app_camera_content))
                                    return
                                }
                            TipDialog.Builder(this@IRThermalLiteActivity)
                                .setTitleMessage(getString(com.topdon.module.thermal.ir.R.string.app_tip))
                                .setMessage(getString(com.topdon.module.thermal.ir.R.string.app_camera_content))
                                .setPositiveListener(com.topdon.module.thermal.ir.R.string.app_open) {
                                    AppUtils.launchAppDetailsSettings()
                                }
                                .setCancelListener(com.topdon.module.thermal.ir.R.string.app_cancel) {
                                }
                                .setCanceled(true)
                                .create().show()
                        }
                        binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                    }
                },
            )
    }

    private val mLiteHandler: Handler =
        object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == SHOW_LOADING) {
                    Log.d(TAG, "SHOW_LOADING")
                    showLoadingDialog()
                } else if (msg.what == HIDE_LOADING) {
                    Log.d(TAG, "HIDE_LOADING")
                    hideLoadingDialog()
                    isConfigWait = false
                } else if (msg.what == HANDLE_INIT_FAIL) {
                    Log.d(TAG, "HANDLE_INIT_FAIL")
                    hideLoadingDialog()
                    Toast.makeText(this@IRThermalLiteActivity, "handle init fail !", Toast.LENGTH_LONG).show()
                } else if (msg.what == HANDLE_SHOW_TOAST) {
                    val message = msg.obj as String
                    Toast.makeText(this@IRThermalLiteActivity, message, Toast.LENGTH_LONG).show()
                } else if (msg.what == PREVIEW_FAIL) {
                    hideLoadingDialog()
                    Toast.makeText(this@IRThermalLiteActivity, "preview fail !", Toast.LENGTH_LONG).show()
                } else if (msg.what == HANDLE_SHOW_FPS) {
                    val fps = msg.obj as Double
                    binding.fpsText.setText("fps : " + String.format("%.1f", fps))
                } else if (msg.what == HANDLE_SHOW_SUN_PROTECT_FLAG) {
                    Toast.makeText(this@IRThermalLiteActivity, "Sun private"Test Data"192:256"
        } else {
            params.dimensionRatio = "256:192"Test Data"Loading..."Test Data"Text"Test Data"Text", ""Test Data"Text"Test Data""Test Data""Test Data"CheckResult"Test Data"Text"Test Data"$TAG:onStop-${e.message}")
        }
    }

    override fun onDestroy() {
        TempCompensation.getInstance().stopTempCompensation(false)
        DeviceControlManager.getInstance().handleStopPreview()
        USBMonitorManager.getInstance().unregisterMonitor()
        super.onDestroy()
        AlarmHelp.getInstance(application).onDestroy(SaveSettingUtil.isSaveSetting)
        try {
            if (mOnUSBConnectListener != null) {
                USBMonitorManager.getInstance()
                    .removeOnUSBConnectListener(IRThermalLiteActivity::class.java.name)
                mOnUSBConnectListener = null
            }
            USBMonitorManager.getInstance().destroyMonitor()
            DeviceControlManager.getInstance().release()
            CameraPreviewManager.getInstance().releaseSource()
        } catch (e: Exception) {
            XLog.e("Test Data")
        }
        SystemClock.sleep(100)
    }

    private fun setCarDetectPrompt()  {
        // Car detection feature not available in thermal-lite layout
        // var carDetectInfo = SharedManager.getCarDetectInfo()
        // var tvDetectPrompt = binding.viewCarDetect.findViewById<TextView>(R.id.tv_detect_prompt)
        // if(carDetectInfo == null){
        //     tvDetectPrompt.text =  getString(R.string.abnormal_item1) + TemperatureUtil.getTempStr(40, 70)
        // }else{
        //     var temperature = carDetectInfo.temperature.split("~")
        //     tvDetectPrompt.text =  carDetectInfo.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
        // }
        // binding.layCarDetectPrompt.visibility = if(intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)) View.VISIBLE else View.GONE
        // binding.viewCarDetect.setOnClickListener {
        //     CarDetectDialog(this) {
        //         var temperature = it.temperature.split("~"Test Data"Text")
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

    override fun getDeltaNucAndVTemp(): Float {
        TempCompensation.getInstance().getDeltaNucAndVTemp()
        return 0f
    }

    override fun compensateTemp(temp: Float): Float {
        return TempCompensation.getInstance().compensateTemp(temp)
    }
}
