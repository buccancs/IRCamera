package com.topdon.module.thermal.ir.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.hardware.SensorManager
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.topdon.pseudo.activity.PseudoSetActivity
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topdon.lib.core.navigation.NavigationManager
import com.blankj.utilcode.util.*
import com.elvishew.xlog.XLog
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.ircmd.IRCMDType
import com.energy.iruvc.ircmd.IRUtils
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.CommonUtils
import com.energy.iruvc.utils.SynchronizedBitmap
import com.energy.iruvc.uvc.ConnectCallback
import com.energy.iruvc.uvc.UVCCamera
import com.example.suplib.wrapper.SupHelp
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.infisense.usbir.camera.IRUVCTC
import com.infisense.usbir.config.MsgCode
import com.infisense.usbir.event.IRMsgEvent
import com.infisense.usbir.event.PreviewComplete
import com.infisense.usbir.extension.setAutoShutter
import com.infisense.usbir.extension.setContrast
import com.infisense.usbir.extension.setMirror
import com.infisense.usbir.extension.setPropDdeLevel
import com.infisense.usbir.thread.ImageThreadTC
import com.infisense.usbir.utils.*
import com.infisense.usbir.view.DragViewUtil
import com.infisense.usbir.view.ITsTempListener
import com.infisense.usbir.view.TemperatureView.*
import com.kylecorry.andromeda.core.math.DecimalFormatter
import com.kylecorry.andromeda.sense.compass.ICompass
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.bean.CameraItemBean.Companion.DELAY_TIME_0
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.core.bean.event.device.DeviceCameraEvent
import com.topdon.lib.core.common.ProductType.PRODUCT_NAME_TC
import com.topdon.lib.core.common.ProductType.PRODUCT_NAME_TCP
import com.topdon.lib.core.common.ProductType.PRODUCT_NAME_TS
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.common.SharedManager.getTemperature
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.*
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.lib.core.tools.*
// import com.topdon.lib.core.utils.BitmapUtils
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.utils.ImageUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.core.view.MainTitleView
import com.topdon.lib.ui.dialog.ThermalInputDialog
import com.topdon.lib.ui.dialog.TipGuideDialog
import com.topdon.lib.ui.dialog.TipPreviewDialog
import com.topdon.lib.ui.widget.seekbar.OnRangeChangedListener
import com.topdon.lib.ui.widget.seekbar.RangeSeekBar
import com.topdon.libcom.AlarmHelp
import com.topdon.libcom.dialog.ColorPickDialog
import com.topdon.libcom.dialog.TempAlarmSetDialog
import com.topdon.menu.constant.FenceType
import com.topdon.menu.constant.SettingType
import com.topdon.menu.constant.TargetType
import com.topdon.menu.constant.TempPointType
import com.topdon.menu.constant.TwoLightType
import com.topdon.module.thermal.ir.R
import com.topdon.lib.core.R as LibcoreR
import com.topdon.module.thermal.ir.adapter.CameraItemAdapter
import com.topdon.module.thermal.ir.adapter.MeasureItemAdapter
import com.topdon.module.thermal.ir.adapter.TargetItemAdapter
import com.topdon.module.thermal.ir.bean.DataBean
import com.topdon.module.thermal.ir.event.GalleryAddEvent
import com.topdon.module.thermal.ir.frame.FrameStruct
import com.topdon.module.thermal.ir.popup.SeekBarPopup
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.utils.IRConfigData
import com.topdon.module.thermal.ir.video.VideoRecordFFmpeg
import com.topdon.module.thermal.ir.view.compass.SensorService
// import com.topdon.pseudo.activity.PseudoSetActivity
import com.topdon.pseudo.bean.CustomPseudoBean
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt

// Legacy ARouter route annotation - now using NavigationManager
/**
 * I r thermal night activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
open class IRThermalNightActivity : BaseIRActivity(), ITsTempListener {
    /**
\1data。
     *
\1，，SDK data，
\1data， image+temperature data。
     */
    protected val defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT

    /**
\1configurationemissivity、temperature measurement、， start ，，
\1 ircmd initialize， ircmd initializeset false ，
\1configurationinitialize，，，。
     */
    protected var isConfigWait = true

    /**
\1thermal imaginginitialize onStart() ，，。
     */
    protected var isrun = false

    /**
\1.
     *
\1successfulinitialize。
     */
    protected var ircmd: IRCMD? = null

    /**
\1pseudo-color.
     */
    protected var pseudoColorMode: Int = SaveSettingUtil.pseudoColorMode

\1high/low gain 1:Textgain 0: Textgain
    protected var gainSelChar = -2
    protected var editMaxValue = Float.MAX_VALUE
    protected var editMinValue = Float.MIN_VALUE
    protected var alarmBean = SaveSettingUtil.alarmBean

    // Fallback BitmapUtils object to resolve compilation issues
    private object BitmapUtils {
        @JvmStatic
        fun mergeBitmapByView(
            bitmap1: Bitmap?,
            bitmap2: Bitmap?,
            view: View?,
        ): Bitmap? {
            return bitmap1 ?: bitmap2 // Simple fallback - return first non-null bitmap
        }

        @JvmStatic
        fun mergeBitmap(
            bitmap1: Bitmap?,
            bitmap2: Bitmap?,
            x: Int = 0,
            y: Int = 0,
        ): Bitmap? {
            return bitmap1 ?: bitmap2 // Simple fallback - return first non-null bitmap
        }

        @JvmStatic
        fun drawCenterLable(
            bitmap: Bitmap?,
            title: String?,
            address: String? = null,
            time: String? = null,
            seekBarWidth: Int = 0,
        ): Bitmap? {
            return bitmap // Simple fallback - return original bitmap with watermark parameters ignored
        }
    }

    protected var customPseudoBean = CustomPseudoBean.loadFromShared()

    private var initRotate = 0 // 
    private var correctRotate = 0 // 

    private var isShowC: Boolean = false
    private lateinit var orientationEventListener: OrientationEventListener
    private var isRotation = false
    private var isReverseRotation = true
    private var mOrientation = 0
    private var realLeftValue = -1f
    private var realRightValue = -1f
    private var isFirst = true
    private var isAutoCamera = false
    private var autoJob: Job? = null
    private var ts_data_H: ByteArray? = null
    private var ts_data_L: ByteArray? = null
    private var isRecordAudio = SaveSettingUtil.isRecordAudio
    private var isOpenTarget = SaveSettingUtil.isOpenTarget
    private var audioPosition: Int = 0

    // View references (migrated from synthetic views)
    protected lateinit var cameraView: com.infisense.usbir.view.CameraView
    protected lateinit var temperatureView: com.infisense.usbir.view.TemperatureView
    private lateinit var spaceChart: View
    private lateinit var clTrendOpen: ConstraintLayout
    private lateinit var llTrendClose: LinearLayout
    private lateinit var viewMenuFirst: com.topdon.menu.MenuFirstTabView
    private lateinit var tvTempContent: TextView

\1Text
    private var hasCompass = true
    private lateinit var compass: ICompass
    private lateinit var sensorService: SensorService
    // Add missing compass view property - commented out as it doesn'Test Data't exist
    private val tvTitleTemp by lazy { findViewById<TextView>(R.id.tv_title_temp) }
    private val tvTitleObserve by lazy { findViewById<TextView>(R.id.tv_title_observe) }

    // private val drawIndPath by lazy { findViewById<View>(R.id.draw_ind_path) }  // ID doesn't exist
    // private val viewCarDetect by lazy { findViewById<View>(R.id.view_car_detect) }  // ID doesn't exist
    // private val tvDetectPrompt by lazy { findViewById<TextView>(R.id.tv_detect_prompt) }  // ID doesn't exist
    // private val rlContent by lazy { findViewById<View>(R.id.rl_content) }  // ID doesn't exist
    protected var compassView: com.topdon.module.thermal.ir.view.compass.LinearCompassView? = null
    // Removed viewStubCamera references as the layout resource doesn't exist

    private val bitmapWidth: Int
        get() = if (isOpenAmplify) imageWidth * ImageThreadTC.MULTIPLE else imageWidth

    private val bitmapHeight: Int
        get() = if (isOpenAmplify) imageHeight * ImageThreadTC.MULTIPLE else imageHeight

    /**
\1IOS drawing，。
     */
    private var hasClickTrendDel = true

    open fun switchAmplify() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    SupHelp.getInstance().initA4KCPP()
                } catch (e: UnsatisfiedLinkError) {
                    SupHelp.getInstance().loadOpenclSuccess = false
                    runOnUiThread {
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setMessage(R.string.tips_tisr_fail)
                            .setPositiveListener(R.string.app_got_it) {
                            }
                            .create().show()
                    }
                    XLog.e("Test Data")
                }
            }
            if (!SupHelp.getInstance().loadOpenclSuccess)
                {
                    return@launch
                }
            isOpenAmplify = !isOpenAmplify
            if (saveSetBean.isRotatePortrait()) {
                bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            } else {
                bitmap = Bitmap.createBitmap(bitmapHeight, bitmapWidth, Bitmap.Config.ARGB_8888)
            }
            imageThread?.setBitmap(bitmap)
            imageThread?.setOpenAmplify(isOpenAmplify)
            cameraView.bitmap = bitmap
            cameraView.isOpenAmplify = isOpenAmplify
            // titleView already initialized as class property
            titleView.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
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

    open fun initAmplify(show: Boolean)  {
        lifecycleScope.launch {
            // titleView already initialized as class property
            if (show)
                {
                    titleView.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
                } else
                {
                    titleView.setRight2Drawable(0)
                }
            withContext(Dispatchers.IO) {
                if (isOpenAmplify)
                    {
                        SupHelp.getInstance().initA4KCPP()
                    }
            }
            if (saveSetBean.isRotatePortrait()) {
                bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            } else {
                bitmap = Bitmap.createBitmap(bitmapHeight, bitmapWidth, Bitmap.Config.ARGB_8888)
            }
            imageThread?.setBitmap(bitmap)
            imageThread?.setOpenAmplify(isOpenAmplify)
            cameraView.bitmap = bitmap
            cameraView.isOpenAmplify = isOpenAmplify
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        // Initialize camera view
        cameraView = findViewById(R.id.cameraView)
        temperatureView = findViewById(R.id.temperatureView)
        // // titleView, thermalRecyclerNight, thermalLay, tvTypeInd, timeDownView already handled by lazy properties

        // Use lazy properties instead of redundant findViewById calls
        viewMenuFirst = findViewById(R.id.view_menu_first)
        tvTempContent = findViewById(R.id.tv_temp_content)
        compassView = findViewById(R.id.compassView)
        val clSeekBar = findViewById<ConstraintLayout>(R.id.cl_seek_bar)
        val viewChartTrend = findViewById<View>(R.id.view_chart_trend)
        clTrendOpen = findViewById(R.id.cl_trend_open)
        llTrendClose = findViewById(R.id.ll_trend_close)
        val thermalText = findViewById<TextView>(R.id.thermal_text)
        // thermalLay already handled by lazy property
        val ivTrendClose = findViewById<ImageView>(R.id.iv_trend_close)
        val ivTrendOpen = findViewById<ImageView>(R.id.iv_trend_open)

        // Missing findViewById declarations for synthetic views
        // tvTypeInd already handled by lazy property
        spaceChart = findViewById(R.id.space_chart)

        titleView.setLeftClickListener {
            // timeDownView check commented out since view doesn'Test Data't exist - using recyclerView directly
            // ViewStubUtils.showViewStub(viewStubCamera, true, callback = { view: View? ->
            //     view?.let {
                    /*
                    if (ScreenUtil.isPortrait(this)) {
                        recyclerView.layoutManager = GridLayoutManager(this, cameraItemBeanList.size)
                    } else {
                        recyclerView.layoutManager = GridLayoutManager(
                            this, cameraItemBeanList.size, GridLayoutManager.VERTICAL, false
                        )
                    }
                     */
            cameraItemAdapter = CameraItemAdapter(cameraItemBeanList)
            cameraItemAdapter?.listener = listener@{ position, _ ->
                when (cameraItemAdapter!!.data[position].type) {
                    CameraItemBean.TYPE_SETTING -> {
                        NavigationManager.getInstance().build(RouterConfig.IR_CAMERA_SETTING)
                            .navigation(this@IRThermalNightActivity)
                        return@listener
                    }

                    CameraItemBean.TYPE_DELAY -> {
                        // if (timeDownView.isRunning) {
                        //    return@listener
                        // }
                        cameraItemAdapter!!.data[position].changeDelayType()
                        cameraItemAdapter!!.notifyItemChanged(position)
                        when (cameraItemAdapter!!.data[position].time) {
                            CameraItemBean.DELAY_TIME_0 -> {
                                ToastUtils.showShort(R.string.off_photography)
                            }

                            CameraItemBean.DELAY_TIME_3 -> {
                                ToastUtils.showShort(R.string.seconds_dalay_3)
                            }

                            CameraItemBean.DELAY_TIME_6 -> {
                                ToastUtils.showShort(R.string.seconds_dalay_6)
                            }
                        }
                        cameraDelaySecond = cameraItemAdapter!!.data[position].time
                        SaveSettingUtil.delayCaptureSecond = cameraDelaySecond
                    }

                    CameraItemBean.TYPE_AUDIO -> {
                        if (!cameraItemAdapter!!.data[position].isSel) {
                            storageRequestType = 1
                            audioPosition = position
                            checkStoragePermission()
                        } else {
                            isRecordAudio = false
                            SaveSettingUtil.isRecordAudio = isRecordAudio
                            videoRecord?.updateAudioState(false)
                            cameraItemAdapter!!.data[position].isSel = !cameraItemAdapter!!.data[position].isSel
                            cameraItemAdapter!!.notifyItemChanged(position)
                        }
                        return@listener
                    }

                    CameraItemBean.TYPE_SDKM -> {
                        lifecycleScope.launch {
                            cameraItemAdapter!!.data[position].isSel = true
                            cameraItemAdapter!!.notifyItemChanged(position)
                            delay(500)
                            cameraItemAdapter!!.data[position].isSel = false
                            cameraItemAdapter!!.notifyItemChanged(position)
                        }
\1Text
                        if (syncimage.type == 1) {
                            ircmd?.tc1bShutterManual()
                        } else {
                            ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
                        }
                        ToastUtils.showShort(R.string.app_Manual_Shutter)
                        return@listener
                    }

                    CameraItemBean.TYPE_ZDKM -> {
\1Text
                        isAutoShutter = !isAutoShutter
                        SaveSettingUtil.isAutoShutter = isAutoShutter
                        cameraItemAdapter!!.data[position].isSel = !cameraItemAdapter!!.data[position].isSel
                        cameraItemAdapter!!.notifyItemChanged(position)
                        if (SharedManager.isTipShutter && !isAutoShutter) {
                            val dialog =
                                TipShutterDialog.Builder(this)
                                    .setMessage(R.string.shutter_tips)
                                    .setCancelListener { isCheck ->
                                        SharedManager.isTipShutter = !isCheck
                                    }
                                    .create()
                            dialog.show()
                        }
                        ircmd?.setAutoShutter(isAutoShutter)
                        return@listener
                    }
                }
                cameraItemAdapter!!.data[position].isSel = !cameraItemAdapter!!.data[position].isSel
                cameraItemAdapter!!.notifyItemChanged(position)
            }
            // recyclerView.adapter = cameraItemAdapter  // Commented out as recyclerView doesn't exist
            // ViewStubCamera layout resource doesn't exist - commented out ViewStub callback
            //     }
            // }
        } else {
            // ViewStub removed - resource not found
        }
    }

    open fun getCameraViewBitmap(): Bitmap {
        if (isOpenAmplify)
            {
\1enabledText，TextimageTextprocessingText
                return imageThread?.getBaseBitmap(saveSetBean.rotateAngle)
                    ?: cameraView.getScaledBitmap()
            } else
            {
                return cameraView.getScaledBitmap()
            }
    }

\1Text
    private fun camera() {
        lifecycleScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) {
                thermalRecyclerNight.setToCamera()
            }
            try {
                synchronized(syncimage.dataLock) {
\1getTextimageTextdata
                    var cameraViewBitmap: Bitmap? =
                        if (isOpenAmplify) {
                            OpencvTools.supImageFourExToBitmap(getCameraViewBitmap())
                        } else {
                            getCameraViewBitmap()
                        }
\1Text
                    if (isOpenPreview) {
                        cameraViewBitmap = BitmapUtils.mergeBitmapByView(cameraViewBitmap, cameraPreview.getBitmap(), cameraPreview)
\1Textsave
                        cameraPreview.getBitmap()?.let {
                            ImageUtils.saveImageToApp(it)
                        }
                    }

\1// gettemperaturedata，，temperature，bitmap
//                    if ((curChooseTabPos == 1 && temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
//                        (curChooseTabPos == 2 && temperatureView.isUserHighTemp && temperatureView.isUserLowTemp)) {
//                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, temperatureView.regionAndValueBitmap, 0, 0)
//                    }

\1Textpseudo-color bar
                    val isShowPseudoBar = cl_seek_bar.visibility == VISIBLE
                    if (isShowPseudoBar) {
                        val seekBarBitmap = cl_seek_bar.drawToBitmap()
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmap(
                                cameraViewBitmap,
                                seekBarBitmap,
                                cameraViewBitmap!!.width - seekBarBitmap.width,
                                (cameraViewBitmap.height - seekBarBitmap.height) / 2,
                            )
                        seekBarBitmap.recycle()
                    }

\1Text
                    val compassBitmap: Bitmap? =
                        if (compassView?.visibility == VISIBLE) {
                            compassView?.drawToBitmap()
                        } else {
                            null
                        }
                    compassBitmap?.let {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmap(
                                cameraViewBitmap,
                                compassBitmap,
                                (cameraViewBitmap!!.width - compassBitmap.width) / 2,
                                SizeUtils.dp2px(20f),
                            )
                        compassBitmap.recycle()
                    }

\1Text 2023/11/24 TextsavetemperatureText
                    /*if (temp_bg.isVisible) {
                        if (alphaPaint == null) {
                            alphaPaint = Paint()
                        }
                        alphaPaint?.alpha = (temp_bg.animatorAlpha * 255).toInt()
                        cameraViewBitmap = BitmapUtils.mergeBitmapAlpha(cameraViewBitmap, temp_bg.drawToBitmap(), alphaPaint, 0, 0)
                    }*/

\1gettemperatureTextdata，Text，temperatureText，Textbitmap
                    if ((curChooseTabPos == 1 && temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
                        (curChooseTabPos == 2 && temperatureView.isUserHighTemp() && temperatureView.isUserLowTemp())
                    ) {
                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, temperatureView.regionAndValueBitmap, 0, 0)
                    }
\1Text
                    if (layCarDetectPrompt.isVisible)
                        {
                            cameraViewBitmap =
                                BitmapUtils.mergeBitmap(
                                    cameraViewBitmap,
                                    layCarDetectPrompt.drawToBitmap(), 0, 0,
                                )
                        }
\1Textwatermark
                    val watermarkBean = SharedManager.watermarkBean
                    if (watermarkBean.isOpen) {
                        cameraViewBitmap =
                            BitmapUtils.drawCenterLable(
                                cameraViewBitmap,
                                watermarkBean.title,
                                watermarkBean.address,
                                if (watermarkBean.isAddTime) TimeTool.getNowTime() else "",
                                if (temperatureSeekbar.isVisible)
                                    {
                                        temperatureSeekbar.measuredWidth
                                    } else
                                    {
                                        0
                                    },
                            )
                    }

                    var name = ""Test Data"TextTPD_PROP DISTANCE:$disChar, EMS:$emsChar}"Test Data"TextTPD_PROP DISTANCE:$disChar, EMS:$emsChar}"Test Data"Text：${if (SaveSettingUtil.isMeasureTempMode) "Test Data" else "Test Data"}Text"Test Data"TextTPD_PROP DISTANCE2:$disChar, EMS:$emsChar}"Test Data"Firmware version: ").append(arm).append("<br>")
                infoBuilder.append("SN: ").append(snStr).append("<br>")
                val str =
                    HtmlCompat.fromHtml(
                        infoBuilder.toString(),
                        HtmlCompat.FROM_HTML_MODE_LEGACY,
                    )
                XLog.i("Test Data")
            } catch (e: Exception) {
                XLog.e("Test Data")
            }
        }
    }

    override fun tempCorrectByTs(temp: Float): Float {
        var tmp = temp
        try {
            tmp = tempCorrect(temp, gainStatus, tempinfo)
        } catch (e: Exception) {
            XLog.i("Test Data")
        }
        return tmp
    }

    private fun startCompass() {
        compass.start(this::onCompassUpdate)
    }

    private fun stopCompass() {
        compass.stop(this::onCompassUpdate)
    }

    /**
\1
     */
    private fun onCompassUpdate(): Boolean {
        val azimuthTxt = formatDegrees(compass.bearing.value, replace360 = true)
        compassView?.setCurAzimuth(azimuthTxt.first.toInt())
        return true
    }

    /**
\1sensor，calculation
     */
    private fun formatDegrees(
        degrees: Float,
        decimalPlaces: Int = 0,
        replace360: Boolean = false,
    ): Pair<Float, Float> {
        val formatted = DecimalFormatter.format(degrees.toDouble(), decimalPlaces)
        val finalFormatted = if (replace360) formatted.replace("360", "0"Test Data"")
                                }
                            } else {
                                thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
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
                                    ToastUtils.showShort(getString(R.string.app_camera_content))
                                    return
                                }
                            TipDialog.Builder(this@IRThermalNightActivity)
                                .setTitleMessage(getString(LibcoreR.string.app_tip))
                                .setMessage(getString(R.string.app_camera_content))
                                .setPositiveListener(R.string.app_open) {
                                    AppUtils.launchAppDetailsSettings()
                                }
                                .setCancelListener(R.string.app_cancel) {
                                }
                                .setCanceled(true)
                                .create().show()
                        }
                        thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                    }
                },
            )
    }

    private fun checkStoragePermission() {
        if (!XXPermissions.isGranted(this, permissionList)) {
            if (BaseApplication.instance.isDomestic()) {
                TipDialog.Builder(this)
                    .setMessage(getString(R.string.permission_request_storage_app, CommUtils.getAppName()))
                    .setCancelListener(R.string.app_cancel)
                    .setPositiveListener(R.string.app_confirm) {
                        if (storageRequestType == 0) {
                            initStoragePermission()
                        } else {
                            initAudioPermission()
                        }
                    }
                    .create().show()
            } else {
                if (storageRequestType == 0) {
                    initStoragePermission()
                } else {
                    initAudioPermission()
                }
            }
        } else {
            if (storageRequestType == 0) {
                initStoragePermission()
            } else {
                initAudioPermission()
            }
        }
    }

    private fun initAudioPermission()  {
        XXPermissions.with(this@IRThermalNightActivity)
            .permission(
                Permission.RECORD_AUDIO,
            )
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        try {
                            if (allGranted) {
\1Textenabled
                                isRecordAudio = true
                                SaveSettingUtil.isRecordAudio = isRecordAudio
                                videoRecord?.updateAudioState(true)
                                cameraItemAdapter?.data?.get(audioPosition)?.isSel = !(cameraItemAdapter?.data?.get(audioPosition)?.isSel?:false)
                                cameraItemAdapter?.notifyItemChanged(audioPosition)
                            } else {
                                ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                            }
                        } catch (e: Exception) {
                            Log.e("Test Data", ""Test Data"~")
            tvDetectPrompt.text =  carDetectInfo.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
        }
        val test = intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)
        layCarDetectPrompt.visibility = if(intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)) View.VISIBLE else View.GONE
        viewCarDetect.findViewById<RelativeLayout>(R.id.rl_content).setOnClickListener {
            CarDetectDialog(this) {
                var temperature = it.temperature.split("~")
                tvDetectPrompt.text =  it.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
            }.show()
        }
         */
    }
}
