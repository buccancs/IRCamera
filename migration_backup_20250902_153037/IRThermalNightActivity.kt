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
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
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
import com.topdon.lib.core.utils.BitmapUtils
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.Constants
import com.topdon.lib.core.utils.ImageUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.core.utils.TemperatureUtil
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
import com.topdon.module.thermal.ir.view.TimeDownView
import com.topdon.module.thermal.ir.view.compass.SensorService
import com.topdon.pseudo.activity.PseudoSetActivity
import com.topdon.pseudo.bean.CustomPseudoBean
import kotlinx.android.synthetic.main.activity_thermal_ir_night.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt


@Route(path = RouterConfig.IR_FRAME)
open class IRThermalNightActivity : BaseIRActivity(), ITsTempListener {

    /**
     * [Chinese text]mode. 
     *
     * [Chinese text], [Chinese text], SDK [Chinese text], 
     * [Chinese text]switch[Chinese text]mode[Chinese text], [Chinese text] [Chinese text]+temperature [Chinese text]mode. 
     */
    protected val defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT


    /**
     * [Chinese text], [Chinese text], [Chinese text], [Chinese text] start [Chinese text], [Chinese text], 
     * [Chinese text] ircmd [Chinese text], [Chinese text] ircmd [Chinese text]in progresssettings[Chinese text] false [Chinese text], 
     * [Chinese text]operation, [Chinese text], [Chinese text], [Chinese text]. 
     */
    protected var isConfigWait = true

    /**
     * [Chinese text] onStart() [Chinese text], [Chinese text], [Chinese text]. 
     */
    protected var isrun = false

    /**
     * [Chinese text].
     *
     * [Chinese text]in progress[Chinese text]. 
     */
    protected var ircmd: IRCMD? = null

    /**
     * [Chinese text]mode.
     */
    protected var pseudoColorMode: Int = SaveSettingUtil.pseudoColorMode

    //highlow[Chinese text] 1:low[Chinese text] 0: high[Chinese text]
    protected var gainSelChar = -2
    protected var editMaxValue = Float.MAX_VALUE
    protected var editMinValue = Float.MIN_VALUE
    protected var alarmBean = SaveSettingUtil.alarmBean
    protected var customPseudoBean = CustomPseudoBean.loadFromShared()

    private var initRotate = 0//[Chinese text]
    private var correctRotate = 0//[Chinese text]

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

    override fun initContentView() = R.layout.activity_thermal_ir_night

    //[Chinese text]
    private var hasCompass = true
    private lateinit var compass: ICompass
    private lateinit var sensorService: SensorService

    /**
     * dual light-[Chinese text], settings-[Chinese text], settings-[Chinese text], [Chinese text]-measurementmode, [Chinese text]-[Chinese text]
     * PopupWindow, for[Chinese text]point[Chinese text]operation[Chinese text].
     */
    private var popupWindow: PopupWindow? = null

    /**
     * only TS001 [Chinese text], [Chinese text]mode.
     * true-[Chinese text]mode false-[Chinese text]mode
     */
    private var isTs001TempMode = true

    //[Chinese text]in progress[Chinese text]tab position, [Chinese text]1
    protected var curChooseTabPos = 1

    //[Chinese text]1-measurementmode 2-[Chinese text]
    private var curTargetStyle = 1

    /**
     * [Chinese text]switch[Chinese text]-high[Chinese text], low[Chinese text], [Chinese text]highlow[Chinese text]switchin progress[Chinese text]dialog[Chinese text]
     */
    @Volatile
    private var isTempShowDialog = false

    private var storageRequestType = 0

    /**
     * TODO TS001 [Chinese text] [Chinese text]-[Chinese text]settings [Chinese text]
     */
    private var aiConfig = SaveSettingUtil.aiTraceType

    private var isOnRestart = false
    private var emissivityConfig : DataBean? = null
    protected var isOpenAmplify = SaveSettingUtil.isOpenAmplify
    private val bitmapWidth: Int
        get() = if (isOpenAmplify) imageWidth * ImageThreadTC.MULTIPLE else imageWidth

    private val bitmapHeight: Int
        get() = if (isOpenAmplify) imageHeight * ImageThreadTC.MULTIPLE else imageHeight

    /**
     * IOS [Chinese text]point[Chinese text]line[Chinese text], [Chinese text]. 
     */
    private var hasClickTrendDel = true

    open fun switchAmplify() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                try {
                    SupHelp.getInstance().initA4KCPP()
                }catch (e : UnsatisfiedLinkError){
                    SupHelp.getInstance().loadOpenclSuccess = false
                    runOnUiThread {
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setMessage(R.string.tips_tisr_fail)
                            .setPositiveListener(R.string.app_got_it) {
                            }
                            .create().show()
                    }
                    XLog.e("[Chinese text]")
                }
            }
            if (!SupHelp.getInstance().loadOpenclSuccess){
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
            title_view.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
            SaveSettingUtil.isOpenAmplify = isOpenAmplify
            if (isOpenAmplify){
                ToastUtils.showShort(R.string.tips_tisr_on)
            }else{
                ToastUtils.showShort(R.string.tips_tisr_off)
            }
        }
    }


     open fun initAmplify(show : Boolean){
        lifecycleScope.launch {
            if (show){
                title_view.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
            }else{
                title_view.setRight2Drawable(0)
            }
            withContext(Dispatchers.IO){
                if (isOpenAmplify){
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
        title_view.setLeftClickListener {
            if (time_down_view.isRunning) {
                return@setLeftClickListener
            }
            setResult(200)
            finish()
        }
        title_view.setRight2ClickListener{
            if (SupHelp.getInstance().loadOpenclSuccess){
                switchAmplify()
            }else{
                TipDialog.Builder(this)
                    .setMessage(R.string.tips_tisr_fail)
                    .setPositiveListener(R.string.app_got_it) {
                    }
                    .create().show()
            }
        }
        title_view.setRightClickListener {
            val config = ConfigRepository.readConfig(false)
            var text = ""
            for (tmp in IRConfigData.irConfigData(this@IRThermalNightActivity)){
                if (config.radiation.toString() == tmp.value){
                    if (text.isEmpty()){
                        text = "${resources.getString(R.string.tc_temp_test_materials)} : "
                    }
                    text += "${tmp.name}/"
                }
            }
            if (text.isNotEmpty()){
                text = text.substring(0,text.length - 1)
            }
            EmissivityTipPopup(this@IRThermalNightActivity, false)
                .setDataBean(config.environment,config.distance,config.radiation,text)
                .build()
                .showAsDropDown(title_view, 0, 0, Gravity.END)
        }
        tv_title_temp.isSelected = true
        tv_title_temp.setOnClickListener {
            switchTs001Mode(true)
        }
        tv_title_observe.setOnClickListener {
            switchTs001Mode(false)
        }


        view_car_detect.findViewById<LinearLayout>(R.id.ll_car_detect_info).setOnClickListener {
            LongTextDialog(this, SharedManager.getCarDetectInfo().item, SharedManager.getCarDetectInfo().description).show()
        }
        BarUtils.setStatusBarColor(this, 0xff16131e.toInt())
        BarUtils.setNavBarColor(window, 0xff16131e.toInt())
        initRecycler()
        view_menu_first.onTabClickListener = {
            ViewStubUtils.showViewStub(view_stub_camera, false, null)
            popupWindow?.dismiss()
            temperatureView.isEnabled = it.selectPosition == 1
            showTempRecyclerNight(it.isObserveMode, it.selectPosition)
        }
        temperature_seekbar.setIndicatorTextDecimalFormat("0.0")
        updateTemperatureSeekBar(false)//[Chinese text]
        isShowC = getTemperature() == 1
        temperatureView.setTextSize(saveSetBean.tempTextSize)
        temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
        temperatureView.listener = TempListener { max, min, _ ->
            realLeftValue = UnitTools.showUnitValue(min, isShowC)
            realRightValue = UnitTools.showUnitValue(max, isShowC)
            this@IRThermalNightActivity.runOnUiThread {
                if (!customPseudoBean.isUseCustomPseudo) {
                    //[Chinese text]mode
                    try {
                        temperature_seekbar.setRangeAndPro(
                            UnitTools.showUnitValue(editMinValue, isShowC),
                            UnitTools.showUnitValue(editMaxValue, isShowC),
                            realLeftValue,
                            realRightValue
                        )
                        if (editMinValue != Float.MIN_VALUE && editMaxValue != Float.MAX_VALUE) {
                            imageThread?.setLimit(
                                editMaxValue, editMinValue,
                                upColor, downColor
                            ) //[Chinese text]
                        }
                    } catch (e: Exception) {
                        Log.e("temperature[Chinese text]", e.message.toString())
                    }
                    try {
                        tv_temp_content.text = "Max:${UnitTools.showC(max, isShowC)}\nMin:${
                            UnitTools.showC(
                                min,
                                isShowC
                            )
                        }"
                    } catch (e: Exception) {
                        Log.e("temperature[Chinese text]", e.message.toString())
                    }
                } else {
                    //[Chinese text]
                    try {
                        tv_temp_content.text = " Max:${UnitTools.showC(max, isShowC)}\n Min:${
                            UnitTools.showC(
                                min,
                                isShowC
                            )
                        }"
                    } catch (e: Exception) {
                        Log.e("temperature[Chinese text]", e.message.toString())
                    }
                }
                try {
                    if (isVideo) {
                        cl_seek_bar.requestLayout()
                        cl_seek_bar.updateBitmap()
                    }
                } catch (e: Exception) {
                    Log.w("[Chinese text]:", "${e.message}")
                }
                try {
                    AlarmHelp.getInstance(application).alarmData(max, min, temp_bg)
                } catch (e: Exception) {
                    Log.e("temperature[Chinese text]", e.message.toString())
                }
            }

        }
        temperatureView.setOnTrendChangeListener {
            lifecycleScope.launch(Dispatchers.Main) {
                if (cl_trend_open.isVisible) {
                    view_chart_trend.refresh(it)
                }
            }
        }
        temperatureView.setOnTrendAddListener {
            if (hasClickTrendDel) {
                hasClickTrendDel = false
                cl_trend_open.isVisible = true
                ll_trend_close.isVisible = false
            }
        }
        temperatureView.setOnTrendRemoveListener {
            view_chart_trend.setToEmpty()
        }

        thermal_recycler_night.isVideoMode = SaveSettingUtil.isVideoMode //[Chinese text]photo capture/recording[Chinese text]
        thermal_recycler_night.fenceSelectType = FenceType.FULL //[Chinese text]in progress[Chinese text]
        thermal_recycler_night.isUnitF = SharedManager.getTemperature() == 0 //temperaturelevel[Chinese text]
        thermal_recycler_night.setSettingRotate(saveSetBean.rotateAngle) //[Chinese text]in progress[Chinese text]
        thermal_recycler_night.setTempLevel(temperatureMode) //[Chinese text]in progress[Chinese text]temperaturelevel

        //[Chinese text]
        thermal_recycler_night.setSettingSelected(SettingType.FONT, !saveSetBean.isTempTextDefault())

        pop_time_lay.visibility = View.GONE
        cameraPreview.visibility = View.INVISIBLE
        initOrientationEventListener()
        addTemperatureListener()
        cameraView.postDelayed(500) {
            if (SaveSettingUtil.isOpenTwoLight && XXPermissions.isGranted(this, Permission.CAMERA)) {
                cameraPreviewConfig(false)
            }
        }
        if (ScreenTool.isIPad(this)) {
            cl_seek_bar.setPadding(0, SizeUtils.dp2px(40f), 0, SizeUtils.dp2px(40f))
        }
        DragViewUtil.registerDragAction(zoomView)
        initCompass()
        distance_measure_view?.moveListener = {
            thermal_text.text = "[Chinese text]: ${it / thermal_lay.measuredHeight * 256}"
        }
        lifecycleScope.launch {
            delay(1000)
            if (!SharedManager.isHideEmissivityTips){
                showEmissivityTips()
            }
        }

        iv_trend_close.setOnClickListener {
            cl_trend_open.isVisible = false
            ll_trend_close.isVisible = true
        }
        iv_trend_open.setOnClickListener {
            cl_trend_open.isVisible = true
            ll_trend_close.isVisible = false
        }
        startUSB(isRestart = false,false)
    }

    /**
     * only TS001 [Chinese text], switch [Chinese text]/[Chinese text] mode.
     * @param isToTemp true-switch[Chinese text] false-switch[Chinese text]
     */
    private fun switchTs001Mode(isToTemp: Boolean) {
        if (isToTemp == isTs001TempMode) {
            return
        }
        tv_title_temp.isSelected = isToTemp
        tv_title_observe.isSelected = !isToTemp
        SaveSettingUtil.isMeasureTempMode = isToTemp

        //[Chinese text]settingsmenu[Chinese text] PopupWindow
        ViewStubUtils.showViewStub(view_stub_camera, false, null)
        popupWindow?.dismiss()

        showCameraLoading()

        stopIfVideoing() //[Chinese text]recording

        //[Chinese text]photo capture
        isAutoCamera = false
        autoJob?.cancel()

        //[Chinese text]photo capture
        if (time_down_view.isRunning) {
            time_down_view.cancel()
            updateDelayView()
        }

        //[Chinese text]
        setDefLimit()
        updateTemperatureSeekBar(false)//[Chinese text]

        if (isToTemp) {//[Chinese text]->[Chinese text]
            //[Chinese text]visible[Chinese text]
            if (SaveSettingUtil.isOpenTwoLight && XXPermissions.isGranted(this, Permission.CAMERA)) {
                cameraPreviewConfig(false)
            }

            //[Chinese text] AI [Chinese text]([Chinese text], high[Chinese text], low[Chinese text])
            aiConfig = ObserveBean.TYPE_NONE
            imageThread?.typeAi = aiConfig
            thermal_recycler_night.setTempSource(aiConfig)

            //[Chinese text]
            thermal_recycler_night.setTargetSelected(TargetType.MODE, false)
            thermal_recycler_night.setTargetSelected(TargetType.STYLE, false)
            thermal_recycler_night.setTargetSelected(TargetType.COLOR, false)
            thermal_recycler_night.setTargetSelected(TargetType.DELETE, false)
            thermal_recycler_night.setTargetMode(ObserveBean.TYPE_MEASURE_PERSON) //[Chinese text]measurementmode
            targetMeasureMode = ObserveBean.TYPE_MEASURE_PERSON
            targetStyle = ObserveBean.TYPE_TARGET_HORIZONTAL
            targetColorType = ObserveBean.TYPE_TARGET_COLOR_GREEN
            zoomView.hideView()

            //[Chinese text]
            saveSetBean.isOpenCompass = false
            thermal_recycler_night.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)
            compassView.visibility = View.GONE
            zoomView?.visibility = View.GONE
            stopCompass()
            zoomView.del(true)

            //[Chinese text]
            saveSetBean.isOpenPseudoBar = SaveSettingUtil.isOpenPseudoBar
            cl_seek_bar.isVisible = saveSetBean.isOpenPseudoBar
            thermal_recycler_night.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)
            temperature_seekbar?.setPseudocode(pseudoColorMode)

            //[Chinese text]high[Chinese text]point, low[Chinese text]point
            temperatureView.clear()
            temperatureView.isUserHighTemp = false
            temperatureView.isUserLowTemp = false
            temperatureView.isVisible = true
            temperatureView.temperatureRegionMode = REGION_MODE_CENTER
            showCross(false)
            thermal_recycler_night.clearTempPointSelect()
            thermal_recycler_night.fenceSelectType = FenceType.FULL //[Chinese text]in progress[Chinese text]

            //[Chinese text]
            alarmBean = SaveSettingUtil.alarmBean
            imageThread?.alarmBean = alarmBean
            if (alarmBean.isHighOpen || alarmBean.isLowOpen) {
                thermal_recycler_night.setSettingSelected(SettingType.ALARM, true)
                AlarmHelp.getInstance(this).updateData(alarmBean)
            } else {
                thermal_recycler_night.setSettingSelected(SettingType.ALARM, false)
                AlarmHelp.getInstance(this).updateData(null, null, null)
            }
        } else {//[Chinese text]->[Chinese text]
            //[Chinese text]in progress[Chinese text]
            if (isOpenPreview) {
                isOpenPreview = false
                cameraPreview.closeCamera()
                thermal_recycler_night.setTwoLightSelected(TwoLightType.P_IN_P, false)
                cameraPreview.visibility = View.INVISIBLE
            }

            //[Chinese text]point, line, [Chinese text], [Chinese text], [Chinese text]
            temperatureView.clear()
            temperatureView.visibility = View.INVISIBLE
            temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
            hasClickTrendDel = true
            space_chart.isVisible = false
            cl_trend_open.isVisible = false
            ll_trend_close.isVisible = false
            showCross(false)

            //switch[Chinese text]low[Chinese text]mode
            switchTempGain(isLow = true, false)

            //switch[Chinese text]
            aiConfig = SaveSettingUtil.aiTraceType
            imageThread?.typeAi = aiConfig
            thermal_recycler_night.setTempSource(aiConfig)

            //[Chinese text]
            saveSetBean.isOpenCompass = SaveSettingUtil.isOpenCompass
            thermal_recycler_night.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)

            //highlow[Chinese text]
            if(SaveSettingUtil.isOpenHighPoint || SaveSettingUtil.isOpenLowPoint){
                temperatureView.temperatureRegionMode = REGION_MODE_RESET
                temperatureView.visibility = View.VISIBLE
            }
            temperatureView.isUserHighTemp = SaveSettingUtil.isOpenHighPoint
            temperatureView.isUserLowTemp = SaveSettingUtil.isOpenLowPoint
            thermal_recycler_night.setTempPointSelect(TempPointType.HIGH, SaveSettingUtil.isOpenHighPoint)
            thermal_recycler_night.setTempPointSelect(TempPointType.LOW, SaveSettingUtil.isOpenLowPoint)

            //[Chinese text]
            targetMeasureMode = SaveSettingUtil.targetMeasureMode
            targetStyle = SaveSettingUtil.targetType
            targetColorType = SaveSettingUtil.targetColorType
            thermal_recycler_night.setTargetMode(targetMeasureMode)
            thermal_recycler_night.setTargetSelected(TargetType.COLOR, targetColorType != ObserveBean.TYPE_TARGET_COLOR_GREEN)

            //[Chinese text]
            cl_seek_bar.visibility = View.GONE

            //[Chinese text]mode[Chinese text]
            if (SharedManager.isTipObservePhoto) {
                TipObserveDialog.Builder(this)
                    .setTitle(R.string.app_tip)
                    .setMessage(R.string.tips_observe_photo_content)
                    .setCancelListener { isCheck ->
                        SharedManager.isTipObservePhoto = !isCheck
                    }
                    .create().show()
            }

            //[Chinese text]temperature[Chinese text]
            alarmBean = AlarmBean()
            imageThread?.alarmBean = alarmBean
            AlarmHelp.getInstance(this).updateData(null, null, null)
        }

        //[Chinese text]
        customPseudoBean.isUseCustomPseudo = false
        customPseudoBean.saveToShared()
        updateImageAndSeekbarColorList(customPseudoBean)

        if (!SaveSettingUtil.isSaveSetting) {
            //[Chinese text]
            setPColor(3)

            //[Chinese text]photo capture
            cameraDelaySecond = DELAY_TIME_0
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[0].time = DELAY_TIME_0
                cameraItemAdapter!!.notifyItemChanged(0)
            }

            //[Chinese text]
            isRecordAudio = false
            videoRecord?.updateAudioState(false)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[3].isSel = false
                cameraItemAdapter!!.notifyItemChanged(3)
            }

            //[Chinese text]
            isAutoShutter = true
            ircmd?.setAutoShutter(isAutoShutter)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[1].isSel = true
                cameraItemAdapter!!.notifyItemChanged(1)
            }

            //[Chinese text]photo capture[Chinese text]mode[Chinese text]photo capture
            SaveSettingUtil.isVideoMode = false
            thermal_recycler_night.switchToCamera()

            //[Chinese text]
            saveSetBean.contrastValue = 128
            ircmd?.setContrast(saveSetBean.contrastValue)

            //[Chinese text]([Chinese text])
            saveSetBean.ddeConfig = 2
            ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)

            //[Chinese text]
            saveSetBean.rotateAngle = DeviceConfig.S_ROTATE_ANGLE
            updateRotateAngle(saveSetBean.rotateAngle)

            //[Chinese text]
            saveSetBean.tempTextColor = 0xffffffff.toInt()
            saveSetBean.tempTextSize = SizeUtils.sp2px(14f)
            temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
            temperatureView.setTextSize(saveSetBean.tempTextSize)

            //[Chinese text]-[Chinese text]
            zoomConfig = 1

            //[Chinese text]
            saveSetBean.isOpenMirror = false
            thermal_recycler_night.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
            ircmd?.setMirror(saveSetBean.isOpenMirror)
        }

        curChooseTabPos = if (isToTemp) Constants.IR_TEMPERATURE_MODE else Constants.IR_OBSERVE_MODE
        isTs001TempMode = isToTemp
        thermal_recycler_night.selectPosition(if (isToTemp) 0 else 10)
        view_menu_first.isObserveMode = !isToTemp

        //[Chinese text]
        updateCompass()

        /**
         * [Chinese text]highlow[Chinese text]switch[Chinese text]4[Chinese text], [Chinese text]switchhighlow[Chinese text], dismissCameraLoading[Chinese text], 
         * [Chinese text]4[Chinese text]
         */
        if (!isTempShowDialog) {
            dismissCameraLoading()
        }
    }


    private fun showEmissivityTips(){
        val config = ConfigRepository.readConfig(false)
        var text = ""
        for (tmp in IRConfigData.irConfigData(this)){
            if (config.radiation.toString() == tmp.value){
                if (text.isEmpty()){
                    text = "${resources.getString(R.string.tc_temp_test_materials)} : "
                }
                text += "${tmp.name}/"
            }
        }
        if (text.isNotEmpty()){
            text = text.substring(0,text.length - 1)
        }
        val dialog = TipEmissivityDialog.Builder(this@IRThermalNightActivity)
            .setDataBean(config.environment,config.distance,config.radiation,text)
            .create()
        dialog.onDismissListener = {
            SharedManager.isHideEmissivityTips = it
        }
        dialog.show()
    }

    private fun updateCompass() {
        if (curChooseTabPos == 1) {
            compassView.visibility = View.GONE
            stopCompass()
        } else {
            if (saveSetBean.isOpenCompass) {
                startCompass()
                compassView.visibility = View.VISIBLE
            }
        }
    }

    private fun initCompass() {
        sensorService = SensorService(this)
        hasCompass = sensorService.hasCompass()
        compass = sensorService.getCompass()
    }

    var isTouchSeekBar = false
    private val pseudoSetResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    updateImageAndSeekbarColorList(
                        it.data?.getParcelableExtra(ExtraKeyConfig.CUSTOM_PSEUDO_BEAN)
                            ?: CustomPseudoBean()
                    )
                    customPseudoBean.saveToShared()
                }
            }
        }

    //[Chinese text]
    private fun updateImageAndSeekbarColorList(customPseudoBean: CustomPseudoBean?) {
        customPseudoBean?.let {
            temperature_seekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
            temperature_seekbar.setPlaces(customPseudoBean.getPlaceList())
            if (it.isUseCustomPseudo) {
                temperature_iv_lock.visibility = View.INVISIBLE
                tv_temp_content.visibility = View.VISIBLE
                updateTemperatureSeekBar(false)//[Chinese text]
                temperature_seekbar.setRangeAndPro(
                    UnitTools.showUnitValue(it.minTemp),
                    UnitTools.showUnitValue(it.maxTemp), UnitTools.showUnitValue(it.minTemp),
                    UnitTools.showUnitValue(it.maxTemp)
                )
                setDefLimit()
                thermal_recycler_night.setPseudoColor(-1)
                temperature_iv_input.setImageResource(R.drawable.ir_model)
            } else {
                temperature_iv_lock.visibility = View.VISIBLE
                thermal_recycler_night.setPseudoColor(pseudoColorMode)
                if (this.customPseudoBean.isUseCustomPseudo) {
                    setDefLimit()
                }
                tv_temp_content.visibility = View.GONE
                temperature_iv_input.setImageResource(R.drawable.ic_color_edit)
            }
            setCustomPseudoColorList(
                customPseudoBean.getColorList(),
                customPseudoBean.getPlaceList(),
                customPseudoBean.isUseGray, it.maxTemp, it.minTemp
            )
            this.customPseudoBean = it
        }
    }

    private fun addTemperatureListener() {

        temperature_iv_lock.setOnClickListener {
            if (temperature_iv_lock.visibility != View.VISIBLE) {
                return@setOnClickListener
            }
            if (temperature_iv_lock.contentDescription == "lock") {
                updateTemperatureSeekBar(true)//[Chinese text]
            } else {
                setDefLimit()
                updateTemperatureSeekBar(false)//[Chinese text]
            }
        }
        temperature_iv_input.setOnClickListener {
            val intent = Intent(this, PseudoSetActivity::class.java)
            intent.putExtra(ExtraKeyConfig.IS_TC007, false)
            pseudoSetResult.launch(intent)
        }
        temperature_seekbar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean,
                tempMode: Int
            ) {
                if (isTouchSeekBar) {
                    editMinValue = if (tempMode == RangeSeekBar.TEMP_MODE_MIN || tempMode == RangeSeekBar.TEMP_MODE_INTERVAL){
                        UnitTools.showToCValue(leftValue,isShowC)
                    }else{
                        Float.MIN_VALUE
                    }
                    editMaxValue = if (tempMode == RangeSeekBar.TEMP_MODE_MAX || tempMode == RangeSeekBar.TEMP_MODE_INTERVAL){
                        UnitTools.showToCValue(rightValue,isShowC)
                    }else{
                        Float.MAX_VALUE
                    }
                    imageThread?.setLimit(
                        editMaxValue,
                        editMinValue,
                        upColor, downColor
                    ) //[Chinese text]
                }
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                isTouchSeekBar = true
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
                isTouchSeekBar = false
            }

        })

    }




    /**
     * [Chinese text]high[Chinese text]low[Chinese text]
     */
    private fun setDefLimit() {
        editMaxValue = Float.MAX_VALUE
        editMinValue = Float.MIN_VALUE
        imageThread?.setLimit(editMaxValue, editMinValue, upColor, downColor) //[Chinese text]
        temperature_seekbar.setRangeAndPro(editMinValue, editMaxValue, realLeftValue, realRightValue) //[Chinese text]
    }

    private fun updateTemperatureSeekBar(isEnabled: Boolean) {
        temperature_seekbar.isEnabled = isEnabled
        temperature_seekbar.drawIndPath(isEnabled)
        temperature_iv_lock.setImageResource(if (isEnabled) R.drawable.svg_pseudo_bar_unlock else R.drawable.svg_pseudo_bar_lock)
        temperature_iv_lock.contentDescription = if (isEnabled) "unlock" else "lock"
        if (isEnabled) {
            temperature_seekbar.tempMode = RangeSeekBar.TEMP_MODE_CLOSE
            temperature_seekbar.leftSeekBar.indicatorBackgroundColor = 0xffe17606.toInt()
            temperature_seekbar.rightSeekBar.indicatorBackgroundColor = 0xffe17606.toInt()
            temperature_seekbar.invalidate()
        } else {
            temperature_seekbar.leftSeekBar.indicatorBackgroundColor = 0
            temperature_seekbar.rightSeekBar.indicatorBackgroundColor = 0
            temperature_seekbar.invalidate()
        }
    }

    private fun initOrientationEventListener() {
        orientationEventListener =
            object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == ORIENTATION_UNKNOWN) {
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
                                cameraPreview?.setRotation(false)
                            }
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else (if (orientation in 135..225) {
                            if (isReverseRotation && saveSetBean.rotateAngle != 90) {
                                saveSetBean.rotateAngle = 90
                                updateRotateAngle(saveSetBean.rotateAngle)
                                isReverseRotation = !isReverseRotation
                                isRotation = true
                                cameraPreview?.setRotation(true)
                            }
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        } else {
                            isRotation = true
                            isReverseRotation = true
                            ActivityInfo.SCREEN_ORIENTATION_LOCKED
                        })
                }
            }
    }

    private fun updateRotateAngle(rotateAngle: Int) {
        // [Chinese text]limitsettings
        imageThread?.setLimit(
            editMaxValue,
            editMinValue,
            upColor, downColor
        ) //[Chinese text]
        lifecycleScope.launch {
            if(curChooseTabPos == Constants.IR_TEMPERATURE_MODE){
                temperatureView.clear()
                temperatureView.temperatureRegionMode = REGION_MODE_CENTER
                hasClickTrendDel = true
                space_chart.isVisible = false
                cl_trend_open.isVisible = false
                ll_trend_close.isVisible = false
                thermal_recycler_night.fenceSelectType = FenceType.FULL //[Chinese text]in progress[Chinese text]
            }
            setRotate(rotateAngle)
            delay(100)
            thermal_recycler_night.setSettingRotate(rotateAngle)
        }
    }

    /**
     * 270[Chinese text]
     */
    open fun setRotate(rotateInt: Int) {
        imageThread?.setRotate(rotateInt)
        iruvc?.setRotate(rotateInt)
        imageThread?.interrupt()

        if (rotateInt == 0 || rotateInt == 180) {
            bitmap = Bitmap.createBitmap(bitmapHeight, bitmapWidth, Bitmap.Config.ARGB_8888)
            if (getProductName() != PRODUCT_NAME_TCP){
                temperatureView.setImageSize(imageHeight, imageWidth, this)
            }
            cameraView.setImageSize(imageHeight, imageWidth)
            setViewLay(false)
        } else {
            bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            if (getProductName() != PRODUCT_NAME_TCP) {
                temperatureView.setImageSize(imageWidth, imageHeight, this)
            }
            cameraView.setImageSize(imageWidth, imageHeight)
            setViewLay(true)
        }

        try {
            imageThread?.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "imageThread.join(): catch an interrupted exception")
        }
        startISP()


        cameraView.bitmap = bitmap
        imageThread?.setBitmap(bitmap)
        runOnUiThread {
            cl_seek_bar.requestLayout()
            cl_seek_bar.updateBitmap()
        }
    }

    override fun initData() {
        initDataIR()
        AlarmHelp.getInstance(this).updateData(alarmBean)
        updateCompass()
    }

    override fun onStart() {
        super.onStart()
        irStart()
    }

   open fun irStart(){
        if (!isrun) {
            syncimage.valid = true
            tv_type_ind?.visibility = GONE
            startISP()
            temperatureView.start()
            cameraView?.start()
            isrun = true
//            //[Chinese text]
            configParam()
            thermal_recycler_night.updateCameraModel()
            initIRConfig()
        }
    }

    override fun onResume() {
        super.onResume()
        emissivityConfig = ConfigRepository.readConfig(false)
        isShowC = getTemperature() == 1
        DeviceTools.isConnect()
        updateCompass()
        AlarmHelp.getInstance(this).onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        thermal_recycler_night.refreshImg()
        startOrientation()
        if (curChooseTabPos != 1 && isOpenTarget && zoomView.visibility == View.VISIBLE) {
            zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,thermal_lay)
        }
        setCarDetectPrompt()
    }


    override fun onPause() {
        super.onPause()
        AlarmHelp.getInstance(this).pause()
        isAutoCamera = false
        autoJob?.cancel()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        orientationEventListener.disable()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        startOrientation()
    }

    private fun startOrientation() {
        orientationEventListener.enable()
        mOrientation = if (Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 0) {
            1
        } else {
            2
        }
        Log.w("[Chinese text]: ", "mOrientation: $mOrientation")
    }

    private fun initRecycler() {
        thermal_recycler_night.onCameraClickListener = {
            setCamera(it)
        }
        thermal_recycler_night.onFenceListener = { fenceType, isSelected ->
            setTemp(fenceType, isSelected)
        }
        thermal_recycler_night.onColorListener = { _, it, _ ->
            if (customPseudoBean.isUseCustomPseudo) {
                TipDialog.Builder(this)
                    .setTitleMessage(getString(R.string.app_tip))
                    .setMessage(R.string.tip_change_pseudo_mode)
                    .setPositiveListener(R.string.app_yes) {
                        customPseudoBean.isUseCustomPseudo = false
                        customPseudoBean.saveToShared()
                        setPColor(it)
                        setDefLimit()
                        updateImageAndSeekbarColorList(customPseudoBean)
                    }.setCancelListener(R.string.app_no) {

                    }
                    .create().show()
            } else {
                setPColor(it)
            }
        }
        thermal_recycler_night.onSettingListener = { type, isSelected ->
            setSetting(type, isSelected)
        }
        thermal_recycler_night.onTempLevelListener = {
            temperatureMode = it
            SaveSettingUtil.temperatureMode = temperatureMode
            setTemperatureMode(it,true)
            if (it == CameraItemBean.TYPE_TMP_H && SharedManager.isTipHighTemp) {//switch[Chinese text]high[Chinese text]
                val message = SpanBuilder(getString(R.string.tc_high_temp_test_tips1))
                    .appendDrawable(this@IRThermalNightActivity,
                        R.drawable.svg_title_temp, SizeUtils.sp2px(24f))
                    .append(getString(R.string.tc_high_temp_test_tips2))
                TipShutterDialog.Builder(this)
                    .setTitle(R.string.tc_high_temp_test)
                    .setMessage(message)
                    .setCancelListener { isCheck ->
                        SharedManager.isTipHighTemp = !isCheck
                    }
                    .create().show()
            }
        }
        thermal_recycler_night.onTwoLightListener = { twoLightType, isSelected ->
            setTwoLight(twoLightType, isSelected)
        }
        cameraPreview.cameraPreViewCloseListener = {
            if (isOpenPreview) {
                popupWindow?.dismiss()
                cameraPreviewConfig(false)
            }
        }
        thermal_recycler_night.onTempSourceListener = {
            setAiState(it)
        }
        thermal_recycler_night.onTargetListener = {
            setTarget(it)
        }
        thermal_recycler_night.onTempPointListener = { type, isSelected ->
            when (type) {
                TempPointType.HIGH -> {
                    SaveSettingUtil.isOpenHighPoint = isSelected
                    temperatureView.temperatureRegionMode = REGION_MODE_RESET
                    temperatureView.visibility = View.VISIBLE
                    temperatureView.setUserHighTemp(isSelected)
                }
                TempPointType.LOW -> {
                    SaveSettingUtil.isOpenLowPoint = isSelected
                    temperatureView.temperatureRegionMode = REGION_MODE_RESET
                    temperatureView.visibility = View.VISIBLE
                    temperatureView.setUserLowTemp(isSelected)
                }
                TempPointType.DELETE -> {
                    temperatureView.setUserHighTemp(false)
                    temperatureView.setUserLowTemp(false)
                    temperatureView.clear()
                    temperatureView.visibility = View.INVISIBLE
                    temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
                    SaveSettingUtil.isOpenHighPoint = false
                    SaveSettingUtil.isOpenLowPoint = false
                }
            }
        }
    }

    private fun setTemperatureMode(tempMode: Int, isShowLoading: Boolean) {
        when (tempMode) {
            CameraItemBean.TYPE_TMP_ZD -> autoConfig()
            CameraItemBean.TYPE_TMP_C -> switchTempGain(true, isShowLoading)
            CameraItemBean.TYPE_TMP_H -> switchTempGain(false, isShowLoading)
        }
    }

    /**
     * [Chinese text]highlow[Chinese text]switch
     * @param isLow true-switch[Chinese text]low[Chinese text](high[Chinese text]) false-switch[Chinese text]high[Chinese text](low[Chinese text])
     */
    private fun switchTempGain(isLow: Boolean, isShowLoading: Boolean) {
        if ((gainSelChar == 1 && isLow) || (gainSelChar == 0 && !isLow)) {//[Chinese text]targetmode
            return
        }
        isTempShowDialog = true
        thermal_recycler_night.setTempLevel(if (isLow) 1 else 0)
        if (isShowLoading) {
            showCameraLoading()
        }
        switchAutoGain(false)
        lifecycleScope.launch(Dispatchers.IO) {
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL,
                if (isLow) CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH else CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_LOW
            )
            gainSelChar = if (isLow) 1 else 0
            delay(4000)
            launch(Dispatchers.Main) {
                dismissCameraLoading()
                isTempShowDialog = false
            }
            setTsBin()
        }
    }

    /**
     *  AI-[Chinese text], high[Chinese text], low[Chinese text]switch
     */
    private fun switchTempSource(isTempSource: Int) {
        aiConfig = isTempSource
        SaveSettingUtil.aiTraceType = aiConfig
        imageThread?.typeAi = isTempSource
    }

    private fun showTempRecyclerNight(isObserveMode: Boolean, position: Int) {
        if (isObserveMode) {//[Chinese text]mode
            when (position) {
                1 -> {//AI[Chinese text]
                    if (SharedManager.isTipAIRecognition) {
                        val dialog = TipObserveDialog.Builder(this)
                            .setTitle(R.string.tips_ai)
                            .setMessage(R.string.tips_ai_content)
                            .setCancelListener { isCheck ->
                                SharedManager.isTipAIRecognition = !isCheck
                            }
                            .create()
                        dialog.show()
                    }
                }
                3 -> {//[Chinese text]
                    isOpenTarget = true
                    SaveSettingUtil.isOpenTarget = isOpenTarget
                    thermal_recycler_night.setTargetSelected(TargetType.MODE, true)
                    thermal_recycler_night.setTargetSelected(TargetType.STYLE, true)
                    thermal_recycler_night.setTargetSelected(TargetType.DELETE, false)
                    zoomView.visibility = View.VISIBLE
                    zoomView.updateTargetBitmap(targetMeasureMode, targetStyle, targetColorType,thermal_lay)
                    if (!SharedManager.getTargetPop()) {
                        thermal_recycler_night.setTargetSelected(TargetType.HELP, true)
                        val dialog = TipGuideDialog.newInstance()
                        dialog.closeEvent = {
                            thermal_recycler_night.setTargetSelected(TargetType.HELP, false)
                            SharedManager.saveTargetPop(it)
                        }
                        dialog.show(supportFragmentManager, "")
                    }
                }
                4 -> {//[Chinese text]
                    if (SharedManager.isTipCoordinate) {
                        val dialog = TipObserveDialog.Builder(this)
                            .setTitle(R.string.coordinate_mode)
                            .setMessage(R.string.coordinate_tips)
                            .setCancelListener { isCheck ->
                                SharedManager.isTipCoordinate = !isCheck
                            }
                            .create()
                        dialog.show()
                    }
                }
            }
        } else {//[Chinese text]mode
            if (position == 4 && !isOpenPreview) {
                thermal_recycler_night.setTwoLightSelected(TwoLightType.P_IN_P, false)
            }
        }

        thermal_recycler_night.selectPosition(if (isObserveMode) position + 10 else position)
    }

    /**
     * [Chinese text]photo capture[Chinese text], 0[Chinese text].
     */
    private var cameraDelaySecond: Int = SaveSettingUtil.delayCaptureSecond
    /**
     * [Chinese text] 1 [Chinese text]menu-photo capturerecording eachoperation[Chinese text]point[Chinese text]eventlistener.
     * @param actionCode: 0-photo capture/recording  1-gallery  2-moremenu  3-switch[Chinese text]photo capture  4-switch[Chinese text]recording
     */
    private fun setCamera(actionCode: Int) {
        when (actionCode) {
            0 -> {// photo capture/recording
                if (isVideo) {
                    centerCamera()
                    return
                }
                if (cameraDelaySecond > 0) {
                    autoJob?.cancel()
                }
                if (time_down_view.isRunning) {
                    time_down_view.cancel()
                    updateDelayView()
                } else {
                    if (time_down_view.downTimeWatcher == null) {
                        time_down_view.setOnTimeDownListener(object : TimeDownView.DownTimeWatcher {
                            override fun onTime(num: Int) {
                                updateDelayView()
                            }

                            override fun onLastTime(num: Int) {
                            }

                            override fun onLastTimeFinish(num: Int) {
                                if(thermal_recycler_night.isVideoMode){
                                    updateVideoDelayView()
                                }else{
                                    updateDelayView()
                                }
                                centerCamera()
                            }
                        })
                    }
                    time_down_view.downSecond(cameraDelaySecond)
                }
            }
            1 -> {//gallery
                lifecycleScope.launch {
                    if (isVideo) {
                        videoRecord?.stopRecord()
                        isVideo = false
                        videoTimeClose()
                        delay(500)
                    }
                    ARouter.getInstance()
                        .build(RouterConfig.IR_GALLERY_HOME)
                        .withInt(ExtraKeyConfig.DIR_TYPE, GalleryRepository.DirType.LINE.ordinal)
                        .navigation()
                }
            }
            2 -> {//moremenu
                settingCamera()
            }
            3 -> {//switch[Chinese text]photo capture
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = false
            }
            4 -> {//switch[Chinese text]recording
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = true
            }
        }
    }

    private fun updateVideoDelayView(){
        try {
            if (time_down_view.isRunning) {
                lifecycleScope.launch(Dispatchers.Main) {
                    thermal_recycler_night.setToRecord(true)
                }
            }
        } catch (e: Exception) {
            Log.e("line[Chinese text]", e.message.toString())
        }
    }

    /**
     * [Chinese text]UI
     */
    private fun updateDelayView() {
        try {
            if (time_down_view.isRunning) {
                lifecycleScope.launch(Dispatchers.Main) {
                    thermal_recycler_night.setToRecord(true)
                }
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    thermal_recycler_night.refreshImg()
                }
            }
        } catch (e: Exception) {
            Log.e("line[Chinese text]", e.message.toString())
        }
    }

    //temperaturemeasurement
    private fun setTemp(fenceType: FenceType, isSelected: Boolean) {
        temperatureView.isEnabled = true
        when (fenceType) {
            FenceType.POINT -> {//point
                temperatureView.visibility = View.VISIBLE
                temperatureView.temperatureRegionMode = REGION_MODE_POINT
                showCross(true)
            }
            FenceType.LINE -> {//line
                temperatureView.visibility = View.VISIBLE
                temperatureView.temperatureRegionMode = REGION_MODE_LINE
                showCross(true)
            }
            FenceType.RECT -> {//[Chinese text]
                temperatureView.visibility = View.VISIBLE
                temperatureView.temperatureRegionMode = REGION_MODE_RECTANGLE
                showCross(true)
            }
            FenceType.FULL -> {//[Chinese text]
                temperatureView.visibility = View.VISIBLE
                temperatureView.isShowFull = isSelected
                showCross(true)
            }
            FenceType.TREND -> {//[Chinese text]
                if (SharedManager.isNeedShowTrendTips) {
                    NotTipsSelectDialog(this)
                        .setTipsResId(R.string.thermal_trend_tips)
                        .setOnConfirmListener {
                            SharedManager.isNeedShowTrendTips = !it
                        }
                        .show()
                }
                temperatureView.visibility = View.VISIBLE
                temperatureView.temperatureRegionMode = REGION_NODE_TREND
                if (!space_chart.isVisible) {//[Chinese text], [Chinese text]
                    space_chart.isVisible = true
                    cl_trend_open.isVisible = false
                    ll_trend_close.isVisible = true
                }
                showCross(true)
            }
            FenceType.DEL -> {//[Chinese text]
                hasClickTrendDel = true
                temperatureView.clear()
                temperatureView.visibility = View.INVISIBLE
                temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
                space_chart.isVisible = false
                cl_trend_open.isVisible = false
                ll_trend_close.isVisible = false
                showCross(false)
            }
        }
    }

    private fun showCross(boolean: Boolean) {
        if (cameraView != null) {
            cameraView.setShowCross(boolean)
        }
    }

    //settings[Chinese text]
    open fun setPColor(code: Int) {
        pseudoColorMode = code
        temperature_seekbar.setPseudocode(pseudoColorMode)
        /**
         * settings[Chinese text][set pseudocolor]
         * [Chinese text]implement([Chinese text],settings[Chinese text])
         */
        imageThread?.pseudocolorMode = pseudoColorMode//settings[Chinese text]
        SaveSettingUtil.pseudoColorMode = pseudoColorMode
        thermal_recycler_night.setPseudoColor(code)
    }


    private var tempAlarmSetDialog: TempAlarmSetDialog? = null
    /**
     * [Chinese text]temperature[Chinese text]settings[Chinese text].
     */
    private fun showTempAlarmSetDialog() {
        if (tempAlarmSetDialog == null) {
            tempAlarmSetDialog = TempAlarmSetDialog(this, false)
            tempAlarmSetDialog?.onSaveListener = {
                thermal_recycler_night.setSettingSelected(SettingType.ALARM, it.isHighOpen || it.isLowOpen)
                alarmBean = it
                imageThread?.alarmBean = alarmBean
                SaveSettingUtil.alarmBean = alarmBean
                AlarmHelp.getInstance(this).updateData(
                    if (alarmBean.isLowOpen) alarmBean.lowTemp else null,
                    if (alarmBean.isHighOpen) alarmBean.highTemp else null,
                    if (alarmBean.isRingtoneOpen) alarmBean.ringtoneType else null)
            }
        }
        tempAlarmSetDialog?.alarmBean = alarmBean
        tempAlarmSetDialog?.show()
    }

    open fun setTwoLight(twoLightType: TwoLightType, isSelected: Boolean) {
        popupWindow?.dismiss()
        when (twoLightType) {
            TwoLightType.P_IN_P -> {//[Chinese text]in progress[Chinese text]
                cameraPreviewConfig(true)
            }
            TwoLightType.BLEND_EXTENT -> {//[Chinese text]
                if (!isOpenPreview && isSelected) {//[Chinese text]in progress[Chinese text]in progress[Chinese text]
                    cameraPreviewConfig(false)
                }
                if (isSelected) {
                    showBlendExtentPopup()
                }
            }
            else -> {//[Chinese text], [Chinese text]dual light[Chinese text]

            }
        }
    }


    private var defaultIsPortrait = DeviceConfig.IS_PORTRAIT //[Chinese text]
    private fun setSetting(type: SettingType, isSelected: Boolean) {
        popupWindow?.dismiss()
        when (type) {
            SettingType.PSEUDO_BAR -> {//[Chinese text]
                saveSetBean.isOpenPseudoBar = !saveSetBean.isOpenPseudoBar
                cl_seek_bar.isVisible = saveSetBean.isOpenPseudoBar
                thermal_recycler_night.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)
            }
            SettingType.CONTRAST -> {//[Chinese text]
                if (!isSelected) {
                    showContrastPopup()
                }
            }
            SettingType.DETAIL -> {//[Chinese text]
                if (!isSelected) {
                    showSharpnessPopup()
                }
            }
            SettingType.ALARM -> {//[Chinese text]
                showTempAlarmSetDialog()
            }
            SettingType.ROTATE -> {//[Chinese text]
                saveSetBean.rotateAngle = if (saveSetBean.rotateAngle == 0) 270 else (saveSetBean.rotateAngle - 90)
                updateRotateAngle(saveSetBean.rotateAngle)
                zoomView.del(true)
            }
            SettingType.FONT -> {//[Chinese text]
                val colorPickDialog = ColorPickDialog(this, saveSetBean.tempTextColor, saveSetBean.tempTextSize)
                colorPickDialog.onPickListener = { it: Int, textSize: Int ->
                    saveSetBean.tempTextColor = it
                    saveSetBean.tempTextSize = SizeUtils.sp2px(textSize.toFloat())
                    temperatureView.setTextSize(saveSetBean.tempTextSize)
                    temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
                    thermal_recycler_night.setSettingSelected(SettingType.FONT, !saveSetBean.isTempTextDefault())
                }
                colorPickDialog.show()
            }
            SettingType.MIRROR -> {//[Chinese text]
                saveSetBean.isOpenMirror = !saveSetBean.isOpenMirror
                thermal_recycler_night.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
                ircmd?.setMirror(saveSetBean.isOpenMirror)
            }

            SettingType.COMPASS -> {//[Chinese text]
                saveSetBean.isOpenCompass = !saveSetBean.isOpenCompass
                thermal_recycler_night.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)
                compassView.isVisible = saveSetBean.isOpenCompass
                if (saveSetBean.isOpenCompass) {
                    startCompass()
                } else {
                    stopCompass()
                }
            }
            SettingType.WATERMARK -> {
                //[Chinese text]menu[Chinese text] 2D [Chinese text]
            }
        }
    }

    private fun setAiState(it: Int) {
        aiConfig = it
        SaveSettingUtil.aiTraceType = it
        when (it) {
            ObserveBean.TYPE_NONE ->{//[Chinese text]
                switchTempSource(ObserveBean.TYPE_NONE)
            }
            ObserveBean.TYPE_DYN_R -> {//[Chinese text]
                switchTempSource(ObserveBean.TYPE_DYN_R)
            }

            ObserveBean.TYPE_TMP_H_S -> {//high[Chinese text]
                switchTempSource(ObserveBean.TYPE_TMP_H_S)
            }

            ObserveBean.TYPE_TMP_L_S -> {//low[Chinese text]
                switchTempSource(ObserveBean.TYPE_TMP_L_S)
            }
        }
    }

    private fun setTarget(targetType: TargetType) {
        when (targetType) {
            TargetType.MODE -> {//measurementmode
                if (curTargetStyle == 1 && popupWindow?.isShowing == true) {
                    popupWindow?.dismiss()
                } else {
                    popupWindow?.dismiss()
                    showTargetModePopup()
                }
            }
            TargetType.STYLE -> {//[Chinese text]
                if (curTargetStyle == 2 && popupWindow?.isShowing == true) {
                    popupWindow?.dismiss()
                } else {
                    popupWindow?.dismiss()
                    showTargetStylePopup()
                }
            }
            TargetType.COLOR -> {//[Chinese text]
                popupWindow?.dismiss()
                showTargetColorDialog()
            }
            TargetType.DELETE -> {//[Chinese text]
                popupWindow?.dismiss()
                isOpenTarget = false
                SaveSettingUtil.isOpenTarget = isOpenTarget
                thermal_recycler_night.setTargetSelected(TargetType.MODE, false)
                thermal_recycler_night.setTargetSelected(TargetType.STYLE, false)
                thermal_recycler_night.setTargetSelected(TargetType.COLOR, false)
                thermal_recycler_night.setTargetSelected(TargetType.DELETE, true)
                zoomView.del(false)
            }
            TargetType.HELP -> {//[Chinese text]
                popupWindow?.dismiss()
                showTargetHelpDialog()
            }
        }
    }

    //[Chinese text]in progress[Chinese text]mode
    /**
     * [Chinese text]measurement[Chinese text]: [Chinese text], [Chinese text], [Chinese text], [Chinese text]
     */
    private var targetMeasureMode: Int = SaveSettingUtil.targetMeasureMode
    /**
     * [Chinese text]: 
     */
    private var targetStyle: Int = SaveSettingUtil.targetType
    /**
     * [Chinese text]: 
     */
    private var targetColorType: Int = SaveSettingUtil.targetColorType

    /**
     * [Chinese text]measurementmode([Chinese text], [Chinese text], [Chinese text], [Chinese text]) PopupWindow.
     */
    private fun showTargetModePopup() {
        zoomView.visibility =View.VISIBLE
        zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,thermal_lay)
        thermal_recycler_night.setTargetSelected(TargetType.MODE, true)
        thermal_recycler_night.setTargetSelected(TargetType.STYLE, true)
        thermal_recycler_night.setTargetSelected(TargetType.DELETE, false)
        popupWindow = PopupWindow(this)
        val contentView = LayoutInflater.from(this).inflate(R.layout.layout_measure_mode, null)
        popupWindow?.contentView = contentView
        popupWindow?.isFocusable = false
        popupWindow?.isOutsideTouchable = false
        popupWindow?.animationStyle = R.style.SeekBarAnimation
        popupWindow?.width = WindowManager.LayoutParams.MATCH_PARENT
        popupWindow?.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow?.setBackgroundDrawable(ColorDrawable(0))
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val contentHeight = contentView.measuredHeight
        val recyclerView = contentView?.findViewById<RecyclerView>(R.id.recycler_view)
        val measureItemAdapter = MeasureItemAdapter(this)
        recyclerView?.layoutManager = if (ScreenUtil.isPortrait(this)) {
            GridLayoutManager(this, measureItemAdapter.itemCount)
        } else {
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        measureItemAdapter.selected(targetMeasureMode)
        measureItemAdapter.listener = listener@{ _, item ->
            targetMeasureMode = item
            SaveSettingUtil.targetMeasureMode = targetMeasureMode
            zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,thermal_lay)
            thermal_recycler_night.setTargetMode(item)
        }
        recyclerView?.adapter = measureItemAdapter
        val mode = IntArray(1)
        ircmd?.getPropImageParams(
            CommonParams.PropImageParams.IMAGE_PROP_LEVEL_CONTRAST,
            mode
        )
//        popupWindow?.setOnDismissListener {
//            thermal_recycler_night.modeStats = 620
//        }
        //[Chinese text]
        popupWindow?.showAsDropDown(thermal_lay, 0, getPopupWindowY(contentHeight), Gravity.NO_GRAVITY)
        curTargetStyle = 1
    }

    /**
     * [Chinese text] PopupWindow
     */
    private fun showTargetStylePopup() {
        zoomView.visibility =View.VISIBLE
        zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,thermal_lay)
        thermal_recycler_night.setTargetSelected(TargetType.MODE, true)
        thermal_recycler_night.setTargetSelected(TargetType.STYLE, true)
        thermal_recycler_night.setTargetSelected(TargetType.DELETE, false)
        popupWindow = PopupWindow(this)
        val contentView =
            LayoutInflater.from(this).inflate(R.layout.layout_second_target, null)
        popupWindow?.contentView = contentView
        popupWindow?.isFocusable = false
        popupWindow?.animationStyle = R.style.SeekBarAnimation
        popupWindow?.width = WindowManager.LayoutParams.MATCH_PARENT
        popupWindow?.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow?.setBackgroundDrawable(ColorDrawable(0))
        popupWindow?.isOutsideTouchable
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val contentHeight = contentView.measuredHeight
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recycler_view)
        val targetItemAdapter = TargetItemAdapter(this)
        recyclerView.layoutManager = if (ScreenUtil.isPortrait(this)) {
            GridLayoutManager(this, targetItemAdapter.itemCount)
        } else {
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        targetItemAdapter.selected(targetStyle)
        targetItemAdapter.listener = listener@{ _, item ->
            targetStyle = item
            SaveSettingUtil.targetType = targetStyle
            zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,thermal_lay)
        }
        recyclerView?.adapter = targetItemAdapter
//        popupWindow?.setOnDismissListener {
//            thermal_recycler_night.targetStats = 600
//        }
        //[Chinese text]
        popupWindow?.showAsDropDown(
            thermal_lay,
            0,
            getPopupWindowY(contentHeight),
            Gravity.NO_GRAVITY
        )
        curTargetStyle = 2
    }

    private fun showTargetColorDialog() {
        TipTargetColorDialog.Builder(this)
            .setTargetColor(targetColorType)
            .setCancelListener {
                thermal_recycler_night.setTargetSelected(TargetType.MODE, true)
                thermal_recycler_night.setTargetSelected(TargetType.STYLE, true)
                thermal_recycler_night.setTargetSelected(TargetType.DELETE, false)
                thermal_recycler_night.setTargetSelected(TargetType.COLOR, it != ObserveBean.TYPE_TARGET_COLOR_GREEN)
                targetColorType = it
                SaveSettingUtil.targetColorType = targetColorType
                zoomView?.updateTargetBitmap(targetMeasureMode, targetStyle, targetColorType,thermal_lay)
            }
            .create().show()
    }

    private fun showTargetHelpDialog(){
        thermal_recycler_night.setTargetSelected(TargetType.HELP, true)
        val dialog = TipGuideDialog.newInstance()
        dialog.closeEvent = {
            thermal_recycler_night.setTargetSelected(TargetType.HELP, false)
        }
        dialog.show(supportFragmentManager, "")
    }

    private var cameraAlpha = SaveSettingUtil.twoLightAlpha

    /**
     * [Chinese text]settings[Chinese text]
     */
    private fun showBlendExtentPopup() {
        val seekBarPopup = SeekBarPopup(this, true)
        seekBarPopup.isRealTimeTrigger = true
        seekBarPopup.progress = cameraAlpha
        seekBarPopup.onValuePickListener = {
            cameraAlpha = it
            SaveSettingUtil.twoLightAlpha = cameraAlpha
            cameraPreview?.setCameraAlpha(cameraAlpha / 100.0f)
        }
        seekBarPopup.setOnDismissListener {
            thermal_recycler_night.setTwoLightSelected(TwoLightType.BLEND_EXTENT, false)
        }
        seekBarPopup.show(thermal_lay, !saveSetBean.isRotatePortrait())
        popupWindow = seekBarPopup
    }

    protected var bitmap: Bitmap? = null
    private var imageThread: ImageThreadTC? = null
    private var iruvc: IRUVCTC? = null

    private val cameraWidth = 256
    private val cameraHeight = 384
    private val tempHeight = 192
    private var imageWidth = cameraWidth
    private var imageHeight = cameraHeight - tempHeight

    private val imageBytes = ByteArray(imageWidth * imageHeight * 2) //[Chinese text]
    private val temperatureBytes = ByteArray(imageWidth * imageHeight * 2) //temperature[Chinese text]
    protected var imageEditBytes = ByteArray(imageWidth * imageHeight * 4) //[Chinese text]
    private val syncimage = SynchronizedBitmap()

    private var temperaturerun = false
    private var tempinfo: Long = 0

    private var isTS001 = false

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun irEvent(event: IRMsgEvent) {
        if (event.code == MsgCode.RESTART_USB) {
            isOnRestart = true
            //[Chinese text]
            startUSB(isRestart = true,true)
            ToastUtils.showShort("[Chinese text]")
        }
    }


    /**
     * [Chinese text]
     */
    private fun initDataIR() {
        imageWidth = cameraHeight - tempHeight
        imageHeight = cameraWidth
        if (saveSetBean.isRotatePortrait()) {
            bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            temperatureView.setImageSize(imageWidth, imageHeight, this@IRThermalNightActivity)
            defaultIsPortrait = DeviceConfig.S_IS_PORTRAIT
            initRotate = 270
            correctRotate = 270
        } else {
            bitmap = Bitmap.createBitmap(bitmapHeight,bitmapWidth,  Bitmap.Config.ARGB_8888)
            temperatureView.setImageSize(imageHeight, imageWidth, this@IRThermalNightActivity)
            defaultIsPortrait = DeviceConfig.IS_PORTRAIT
            initRotate = 270
            correctRotate = 0
        }
        cameraView.setSyncimage(syncimage)
        cameraView.bitmap = bitmap
        temperatureView.setSyncimage(syncimage)
        temperatureView.setTemperature(temperatureBytes)
        //[Chinese text]-[Chinese text]
        thermal_recycler_night.setTempSource(if(curChooseTabPos == 2) aiConfig else ObserveBean.TYPE_NONE)
        setViewLay(defaultIsPortrait)
        //[Chinese text]
        temperatureView.post {
            if (!temperaturerun) {
                temperaturerun = true
                //[Chinese text]
                temperatureView.visibility = View.VISIBLE
                if (!isTS001 || SaveSettingUtil.isMeasureTempMode) {
                    temperatureView.postDelayed({
                        temperatureView.temperatureRegionMode = REGION_MODE_CENTER//[Chinese text]
                    }, 1000)
                }
            }
        }
        cl_seek_bar.requestLayout()
        cl_seek_bar.updateBitmap()
    }

    /**
     * @param isPortrait    true: [Chinese text]
     */
    private fun setViewLay(isPortrait: Boolean) {
        val params = thermal_lay.layoutParams as ConstraintLayout.LayoutParams
        if (isPortrait) {
            params.dimensionRatio = "192:256"
        } else {
            params.dimensionRatio = "256:192"
        }
        runOnUiThread {
            thermal_lay.layoutParams = params
        }
        thermal_lay.post {
            cl_seek_bar.requestLayout()
        }
        thermal_lay.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (saveSetBean.isRotatePortrait() && thermal_lay.measuredHeight > thermal_lay.measuredWidth) {
                        val childLayoutParams  = temperatureView.layoutParams
                        childLayoutParams.width = thermal_lay.measuredWidth
                        childLayoutParams.height = thermal_lay.measuredHeight
                        temperatureView.layoutParams = childLayoutParams
                        zoomView.setImageSize(imageHeight, imageWidth, thermal_lay.width, thermal_lay.height)
                        thermal_lay.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else if (!saveSetBean.isRotatePortrait() && thermal_lay.measuredHeight < thermal_lay.measuredWidth) {
                        val childLayoutParams  = temperatureView.layoutParams
                        childLayoutParams.width = thermal_lay.measuredWidth
                        childLayoutParams.height = thermal_lay.measuredHeight
                        temperatureView.layoutParams = childLayoutParams
                        zoomView.setImageSize(imageWidth, imageHeight, thermal_lay.width, thermal_lay.height)
                        thermal_lay.viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                }
            });
    }

    /**
     * [Chinese text]
     */
    open fun startISP() {
        try {
            imageThread = ImageThreadTC(this@IRThermalNightActivity, imageWidth, imageHeight)
            imageThread?.setDataFlowMode(defaultDataFlowMode)
            imageThread?.setSyncImage(syncimage)
            setCustomPseudoColorList(
                customPseudoBean.getColorList(),
                customPseudoBean.getPlaceList(),
                customPseudoBean.isUseGray,
                customPseudoBean.maxTemp, customPseudoBean.minTemp
            )
            imageThread?.setLimit(editMaxValue, editMinValue, upColor, downColor) //[Chinese text]
            imageThread?.setOpenAmplify(isOpenAmplify)
            imageThread?.setBitmap(bitmap)
            imageThread?.setImageSrc(imageBytes)
            imageThread?.pseudocolorMode = pseudoColorMode
            imageThread?.setTemperatureSrc(temperatureBytes)
            imageThread?.setRotate(saveSetBean.rotateAngle)
            imageThread?.setRotate(true)
            imageThread?.alarmBean = alarmBean
            imageThread?.typeAi = if(curChooseTabPos == 2) aiConfig else ObserveBean.TYPE_NONE
            imageThread?.start()
        } catch (e: Exception) {
            Log.e("[Chinese text]line[Chinese text]", e.message.toString())
        }
    }

    override fun onRestart() {
        super.onRestart()
        isOnRestart = true
    }

    /**
     * @param isRestart [Chinese text]
     */
    open fun startUSB(isRestart: Boolean,isBadFrames : Boolean) {
        isOnRestart = true
        if (!isBadFrames){
            showCameraLoading()
        }
        iruvc = IRUVCTC(cameraWidth, cameraHeight, this, syncimage,
            defaultDataFlowMode, object : ConnectCallback {
                override fun onCameraOpened(uvcCamera: UVCCamera) {
                    XLog.w("settingsonCameraOpened:${uvcCamera}}")
                }

                override fun onIRCMDCreate(ircmd: IRCMD) {
                    this@IRThermalNightActivity.ircmd = ircmd
                    // [Chinese text]IRCMD[Chinese text]
                    isConfigWait = false
                }
            }, object : USBMonitorCallback {
                override fun onAttach() {}
                override fun onGranted() {}
                override fun onConnect() {}
                override fun onDisconnect() {
                }
                override fun onDettach() {
                    finish()
                }

                override fun onCancel() {
                    finish()
                }
            })
        iruvc?.isRestart = isRestart
        iruvc?.setImageSrc(imageBytes)
        iruvc?.setTemperatureSrc(temperatureBytes)
        iruvc?.imageEditTemp = imageEditBytes
        iruvc?.setRotate(saveSetBean.rotateAngle)
        iruvc?.setIFrameCallBackListener {
            this.runOnUiThread {
                zoomView?.updateMagnifier()
            }
        }
        iruvc?.isFirstFrame = true
        iruvc?.setiFirstFrameListener {
            //[Chinese text]
            this@IRThermalNightActivity.runOnUiThread {
                //[Chinese text], [Chinese text]highlowlow[Chinese text]switch, [Chinese text]dismissCameraLoading
                //[Chinese text]start, [Chinese text]dismiss, [Chinese text]dialog[Chinese text]
                if (isOnRestart) {
                    dismissCameraLoading()
                    isOnRestart = false
                }
            }
            // [Chinese text]Toast[Chinese text]
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    setTsBin()
                }
            }
        }
        iruvc?.registerUSB()
    }

    private var nuc_table_low = ShortArray(8192)
    private var gainStatus = CommonParams.GainStatus.HIGH_GAIN


    //settingsTS001[Chinese text]temperature[Chinese text]
    private fun setTsBin() {
        ircmd?.let {
            val getSnBytes = ByteArray(16)
            val fwBuildVersionInfoBytes = ByteArray(50)
            ircmd?.getDeviceInfo(
                CommonParams.DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
                fwBuildVersionInfoBytes
            ) //ok
            val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
            it.getDeviceInfo(CommonParams.DeviceInfoType.DEV_INFO_GET_SN, getSnBytes) //ok
            val snStr = String(getSnBytes) //sn
            val infoBuilder = StringBuilder()
            infoBuilder.append("Firmware version: ").append(arm).append("<br>")
            infoBuilder.append("SN: ").append(snStr).append("<br>")
            val str =
                HtmlCompat.fromHtml(infoBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (str.contains("Mini256", true)) {
                lifecycleScope.launch(Dispatchers.Main){
                    tv_title_temp.isVisible = true
                    tv_title_observe.isVisible = true
                }
                // [Chinese text]highlow[Chinese text]
//                getUTable()
                val value = IntArray(1)
                ircmd!!.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
                Log.d(TAG, "TPD_PROP_GAIN_SEL=" + value[0])
                gainStatus = if (value[0] == 1) {
                    // [Chinese text]high[Chinese text]
                    CommonParams.GainStatus.HIGH_GAIN
                    // [Chinese text]
                } else {
                    // [Chinese text]low[Chinese text]
                    CommonParams.GainStatus.LOW_GAIN
                }
                if (nuc_table_low == null) {
                    return@let
                }
                if (ts_data_H == null){
                    ts_data_H = CommonUtils.getTauData(this@IRThermalNightActivity, "ts/TS001_H.bin")
                }
                if (ts_data_L == null){
                    ts_data_L = CommonUtils.getTauData(this@IRThermalNightActivity, "ts/TS001_L.bin")
                }
                isTS001 = true
            } else {
                isTS001 = false
            }
            if (!DeviceTools.isTC001PlusConnect()){
                initAmplify(true)
            }else{
//                isOpenAmplify = false
//                initAmplify(false)
            }
        }
    }


    /**
     * [Chinese text]point[Chinese text]
     */
    private fun tempCorrect(
        temp: Float,
        gainStatus: CommonParams.GainStatus, tempInfo: Long
    ): Float {
        if (!isTS001) {
            //[Chinese text]ts001[Chinese text]
            return temp
        }
        if (ts_data_H == null || ts_data_L == null) {
            return temp
        }
        if (emissivityConfig == null){
            emissivityConfig = ConfigRepository.readConfig(false)
        }
        val paramsArray = floatArrayOf(
            temp, emissivityConfig!!.radiation, emissivityConfig!!.environment,
            emissivityConfig!!.environment, emissivityConfig!!.distance, 0.8f
        )
        val newTemp = IRUtils.temperatureCorrection(
            IRCMDType.USB_IR_256_384,
            CommonParams.ProductType.WN256_ADVANCED,
            paramsArray[0],
            ts_data_H,
            ts_data_L,
            paramsArray[1],
            paramsArray[2],
            paramsArray[3],
            paramsArray[4],
            paramsArray[5],
            tempInfo,
            gainStatus
        )
        Log.i(
            TAG,
            "temp correct, oldTemp = " + paramsArray[0] + " ems = " + paramsArray[1] + " ta = " + paramsArray[2] + " " +
                    "distance = " + paramsArray[4] + " hum = " + paramsArray[5] + " productType = ${CommonParams.ProductType.WN256}" + " " +
                    "newtemp = " + newTemp
        )
        return newTemp
    }

    /**
     * IRmode[Chinese text]
     */
    protected fun initIRConfig() {
        //[Chinese text]
        cl_seek_bar.isVisible = curChooseTabPos == 1 && saveSetBean.isOpenPseudoBar
        thermal_recycler_night.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)
        temperature_seekbar?.setPseudocode(pseudoColorMode)
        if (customPseudoBean.isUseCustomPseudo) {
            updateCustomPseudo()
        } else {
            temperature_iv_lock.visibility = View.VISIBLE
            tv_temp_content.visibility = View.GONE
            temperature_iv_input.setImageResource(R.drawable.ic_color_edit)
            thermal_recycler_night.setPseudoColor(pseudoColorMode)
        }
        thermal_recycler_night.setSettingSelected(SettingType.ALARM, alarmBean.isHighOpen || alarmBean.isLowOpen)
    }


    override fun onStop() {
        irStop()
        super.onStop()
    }

    open fun irStop() {
        try {
            configJob?.cancel()
            time_down_view?.cancel()
            imageThread?.interrupt()
            imageThread?.join()
            syncimage.valid = false
            temperatureView.stop()
            cameraView?.stop()
            isrun = false
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
                    thermal_recycler_night.refreshImg()
                }
            }
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmHelp.getInstance(application).onDestroy(SaveSettingUtil.isSaveSetting)
        temp_bg?.stopAnimation()
        time_down_view?.cancel()
        stopCompass()
        try {
            iruvc?.stopPreview()
            iruvc?.unregisterUSB()
            imageThread?.join()
            if (tempinfo != 0L && isTS001) {
                IRUtils.releaseTemperatureCorrection(
                    IRCMDType.USB_IR_256_384,
                    tempinfo,
                    false
                )
            }
        } catch (e: InterruptedException) {
            Log.e(TAG, "imageThread.join(): catch an interrupted exception")
        }

        // [Chinese text]sensor
//        if (Usbcontorl.isload) {
//            Usbcontorl.usb3803_mode_setting(0) //[Chinese text]5V
//        }

    }

    /**
     * [Chinese text], [Chinese text].
     */
    private fun closeCustomPseudo() {
        setCustomPseudoColorList(null, null, true, 0f, 0f)
        temperature_seekbar.setColorList(null)
        temperature_iv_lock.visibility = View.VISIBLE
        thermal_recycler_night.setPseudoColor(pseudoColorMode)
        tv_temp_content.visibility = View.GONE
        temperature_iv_input.setImageResource(R.drawable.ic_color_edit)
    }

    /**
     * [Chinese text], [Chinese text], [Chinese text]dual light[Chinese text]
     * @param colorList IntArray?
     * @param isUseGray Boolean
     * @param customMaxTemp Float
     * @param customMinTemp Float
     */
    open fun setCustomPseudoColorList(
        colorList: IntArray?,
        places: FloatArray?,
        isUseGray: Boolean,
        customMaxTemp: Float,
        customMinTemp: Float
    ) {
        imageThread?.setColorList(colorList, places, isUseGray, customMaxTemp, customMinTemp)
    }

    private fun updateCustomPseudo() {
        temperature_seekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
        temperature_seekbar.setPlaces(customPseudoBean.getPlaceList())
        temperature_iv_lock.visibility = View.INVISIBLE
        temperature_seekbar.setRangeAndPro(
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp)
        )
        tv_temp_content.visibility = View.VISIBLE
        thermal_recycler_night.setPseudoColor(-1)
        temperature_iv_input.setImageResource(R.drawable.ir_model)
    }

    private val permissionList by lazy {
        if (this.applicationInfo.targetSdkVersion >= 34){
            mutableListOf(
                Permission.WRITE_EXTERNAL_STORAGE
            )
        }else if (this.applicationInfo.targetSdkVersion == 33) {
            mutableListOf(
                Permission.READ_MEDIA_VIDEO, Permission.READ_MEDIA_IMAGES,
                 Permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            mutableListOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun countDownCoroutines(
        total: Int,
        timeDelay: Long,
        scope: CoroutineScope,
        onTick: (Int) -> Unit,
        onStart: (() -> Unit)? = null,
        onFinish: (() -> Unit)? = null,
    ): Job {
        return flow {
            for (i in total downTo 1) {
                emit(i)
                delay(timeDelay)
            }
        }.flowOn(Dispatchers.Main)
            .onStart { onStart?.invoke() }
            .onCompletion { onFinish?.invoke() }
            .onEach { onTick.invoke(it) }
            .launchIn(scope)
    }

    //photo capturein progress[Chinese text]button
    @SuppressLint("CheckResult")
    private fun centerCamera() {
        storageRequestType = 0
        checkStoragePermission()
    }

    private var showCameraSetting = false
    private val cameraItemBeanList by lazy {
        mutableListOf(
            CameraItemBean(
                "[Chinese text]", CameraItemBean.TYPE_DELAY,
                time = SaveSettingUtil.delayCaptureSecond
            ),
            CameraItemBean(
                "[Chinese text]", CameraItemBean.TYPE_ZDKM,
                isSel = SaveSettingUtil.isAutoShutter
            ),
            CameraItemBean("[Chinese text]", CameraItemBean.TYPE_SDKM),
            CameraItemBean(
                "[Chinese text]", CameraItemBean.TYPE_AUDIO,
                isSel = SaveSettingUtil.isRecordAudio &&
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        )
                        == PackageManager.PERMISSION_GRANTED
            ),
            CameraItemBean("settings", CameraItemBean.TYPE_SETTING),
        )
    }

    private var cameraItemAdapter: CameraItemAdapter? = null

    private var isAutoShutter: Boolean = SaveSettingUtil.isAutoShutter
    //photo capture[Chinese text]button
    private fun settingCamera() {
        showCameraSetting = !showCameraSetting
        if (showCameraSetting) {
            ViewStubUtils.showViewStub(view_stub_camera, true, callback = { view: View? ->
                view?.let {
                    val recyclerView = it.findViewById<RecyclerView>(R.id.recycler_view)
                    if (ScreenUtil.isPortrait(this)) {
                        recyclerView.layoutManager = GridLayoutManager(this, cameraItemBeanList.size)
                    } else {
                        recyclerView.layoutManager = GridLayoutManager(
                            this, cameraItemBeanList.size, GridLayoutManager.VERTICAL, false
                        )
                    }
                    cameraItemAdapter = CameraItemAdapter(cameraItemBeanList)
                    cameraItemAdapter?.listener = listener@{ position, _ ->
                        when (cameraItemAdapter!!.data[position].type) {
                            CameraItemBean.TYPE_SETTING -> {
                                ARouter.getInstance().build(RouterConfig.IR_CAMERA_SETTING)
                                    .navigation(this)
                                return@listener
                            }

                            CameraItemBean.TYPE_DELAY -> {
                                if (time_down_view.isRunning) {
                                    return@listener
                                }
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
                                //[Chinese text]
                                if (syncimage.type == 1) {
                                    ircmd?.tc1bShutterManual()
                                } else {
                                    ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
                                }
                                ToastUtils.showShort(R.string.app_Manual_Shutter)
                                return@listener
                            }

                            CameraItemBean.TYPE_ZDKM -> {
                                //[Chinese text]
                                isAutoShutter = !isAutoShutter
                                SaveSettingUtil.isAutoShutter = isAutoShutter
                                cameraItemAdapter!!.data[position].isSel = !cameraItemAdapter!!.data[position].isSel
                                cameraItemAdapter!!.notifyItemChanged(position)
                                if (SharedManager.isTipShutter && !isAutoShutter) {
                                    val dialog = TipShutterDialog.Builder(this)
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
                    recyclerView.adapter = cameraItemAdapter
                }
            })
        } else {
            ViewStubUtils.showViewStub(view_stub_camera, false, null)
        }
    }

    open fun getCameraViewBitmap(): Bitmap {
        if (isOpenAmplify){
            //[Chinese text]button, [Chinese text]
            return imageThread?.getBaseBitmap(saveSetBean.rotateAngle)
                ?: cameraView.getScaledBitmap()
        }else{
            return cameraView.getScaledBitmap()
        }
    }

    // photo capture
    private fun camera() {
        lifecycleScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) {
                thermal_recycler_night.setToCamera()
            }
            try {
                synchronized(syncimage.dataLock) {
                    // [Chinese text]
                    var cameraViewBitmap: Bitmap? =
                        if (isOpenAmplify) OpencvTools.supImageFourExToBitmap(getCameraViewBitmap())
                        else getCameraViewBitmap()
                    // visible[Chinese text]
                    if (isOpenPreview) {
                        cameraViewBitmap = BitmapUtils.mergeBitmapByView(cameraViewBitmap, cameraPreview.getBitmap(), cameraPreview)
                        //[Chinese text]in progress[Chinese text]
                        cameraPreview.getBitmap()?.let {
                            ImageUtils.saveImageToApp(it)
                        }
                    }

//                    // [Chinese text]temperature[Chinese text], [Chinese text]pointline[Chinese text], temperature[Chinese text], [Chinese text]bitmap
//                    if ((curChooseTabPos == 1 && temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
//                        (curChooseTabPos == 2 && temperatureView.isUserHighTemp && temperatureView.isUserLowTemp)) {
//                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, temperatureView.regionAndValueBitmap, 0, 0)
//                    }

                    // [Chinese text]
                    val isShowPseudoBar = cl_seek_bar.visibility == VISIBLE
                    if (isShowPseudoBar) {
                        val seekBarBitmap = cl_seek_bar.drawToBitmap()
                        cameraViewBitmap = BitmapUtils.mergeBitmap(
                            cameraViewBitmap,
                            seekBarBitmap,
                            cameraViewBitmap!!.width - seekBarBitmap.width,
                            (cameraViewBitmap.height - seekBarBitmap.height) / 2
                        )
                        seekBarBitmap.recycle()
                    }

                    //[Chinese text]
                    val compassBitmap: Bitmap? = if (compassView.visibility == VISIBLE) {
                        compassView.drawToBitmap()
                    } else null
                    compassBitmap?.let {
                        cameraViewBitmap = BitmapUtils.mergeBitmap(
                            cameraViewBitmap,
                            compassBitmap,
                            (cameraViewBitmap!!.width - compassBitmap.width) / 2,
                            SizeUtils.dp2px(20f)
                        )
                        compassBitmap.recycle()
                    }

                    // [Chinese text] 2023/11/24 [Chinese text]photo capture[Chinese text]temperature[Chinese text]
                    /*if (temp_bg.isVisible) {
                        if (alphaPaint == null) {
                            alphaPaint = Paint()
                        }
                        alphaPaint?.alpha = (temp_bg.animatorAlpha * 255).toInt()
                        cameraViewBitmap = BitmapUtils.mergeBitmapAlpha(cameraViewBitmap, temp_bg.drawToBitmap(), alphaPaint, 0, 0)
                    }*/

                    // [Chinese text]temperature[Chinese text], [Chinese text]pointline[Chinese text], temperature[Chinese text], [Chinese text]bitmap
                    if ((curChooseTabPos == 1 && temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
                        (curChooseTabPos == 2 && temperatureView.isUserHighTemp() && temperatureView.isUserLowTemp())) {
                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, temperatureView.regionAndValueBitmap, 0, 0)
                    }
                    //[Chinese text]
                    if (lay_car_detect_prompt.isVisible){
                        cameraViewBitmap = BitmapUtils.mergeBitmap(
                            cameraViewBitmap,
                            lay_car_detect_prompt.drawToBitmap(), 0, 0)
                    }
                    //[Chinese text]
                    val watermarkBean = SharedManager.watermarkBean
                    if (watermarkBean.isOpen) {
                        cameraViewBitmap = BitmapUtils.drawCenterLable(
                            cameraViewBitmap,
                            watermarkBean.title,
                            watermarkBean.address,
                            if (watermarkBean.isAddTime) TimeTool.getNowTime() else "",
                            if (temperature_seekbar.isVisible){
                                temperature_seekbar.measuredWidth
                            }else{
                                0
                            }
                        )
                    }

                    var name = ""
                    cameraViewBitmap?.let {
                        name = ImageUtils.save(bitmap = it)
                    }
                    val value = IntArray(1)
                    ircmd?.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)

                    if (curChooseTabPos == 1) {//[Chinese text]mode[Chinese text]temperature[Chinese text]
                        val capital = FrameStruct.toCode(
                            name = getProductName(),
                            width = if (cameraViewBitmap!!.height < cameraViewBitmap!!.width) 256 else 192,
                            height = if (cameraViewBitmap!!.height < cameraViewBitmap!!.width) 192 else 256,
                            rotate = saveSetBean.rotateAngle,
                            pseudo = pseudoColorMode,
                            initRotate = initRotate,
                            correctRotate = correctRotate,
                            customPseudoBean = customPseudoBean,
                            isShowPseudoBar = isShowPseudoBar,
                            textColor = saveSetBean.tempTextColor,
                            watermarkBean = watermarkBean,
                            alarmBean = alarmBean,
                            value[0],
                            textSize = saveSetBean.tempTextSize,
                            emissivityConfig?.environment ?: 0f,
                            emissivityConfig?.distance ?: 0f,
                            emissivityConfig?.radiation ?: 0f,
                            isOpenAmplify
                        )
                        ImageUtils.saveFrame(bs = imageEditBytes, capital = capital, name = name)
                    }

                    launch(Dispatchers.Main) {
                        thermal_recycler_night.refreshImg()
                    }
                    EventBus.getDefault().post(GalleryAddEvent())
                }
            }catch (e:Exception){
                XLog.e(e.message)
            }
        }
    }

   open fun getProductName() : String{
       return if (isTS001) {PRODUCT_NAME_TS} else {PRODUCT_NAME_TC}
    }


    protected var isVideo = false

    protected var videoRecord: VideoRecordFFmpeg? = null

    /**
     * [Chinese text]
     */
    open fun initVideoRecordFFmpeg() {
        videoRecord = VideoRecordFFmpeg(
            cameraView,
            cameraPreview,
            temperatureView,
            curChooseTabPos == 1,
            cl_seek_bar,
            temp_bg,
            compassView, null,
            carView = lay_car_detect_prompt
        )
    }

    private fun video() {
        if (!isVideo) {
            //start[Chinese text]
            initVideoRecordFFmpeg()
            if (!videoRecord!!.canStartVideoRecord(null)) {
                return
            }
            videoRecord?.stopVideoRecordListener = { isShowVideoRecordTips ->
                this@IRThermalNightActivity.runOnUiThread {
                    if (isShowVideoRecordTips) {
                        try {
                            val dialog = TipDialog.Builder(this@IRThermalNightActivity)
                                .setMessage(R.string.tip_video_record)
                                .create()
                            dialog.show()
                        } catch (_: Exception) {
                        }
                    }
                    videoRecord?.stopRecord()
                    isVideo = false
                    videoTimeClose()
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(500)
                        thermal_recycler_night.refreshImg()
                    }
                }
            }
            cl_seek_bar.updateBitmap()
            videoRecord?.updateAudioState(isRecordAudio)
            videoRecord?.startRecord()
            isVideo = true
            lifecycleScope.launch(Dispatchers.Main) {
                thermal_recycler_night.setToRecord(false)
            }
            videoTimeShow()
        } else {
            stopIfVideoing()
        }
    }


    /**
     * [Chinese text]recording, [Chinese text]stoprecording.
     */
    private fun stopIfVideoing() {
        if (isVideo) {
            isVideo = false
            videoRecord?.stopRecord()
            videoTimeClose()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(500)
                thermal_recycler_night.refreshImg()
                EventBus.getDefault().post(GalleryAddEvent())
            }
        }
    }


    private var flow: Job? = null

    private fun videoTimeShow() {
        flow = lifecycleScope.launch {
            val time = 60 * 60 * 4
            flow {
                repeat(time) {
                    emit(it)
                    delay(1000)
                }
            }.collect {
                launch(Dispatchers.Main) {
                    pop_time_text.text = TimeTool.showVideoTime(it * 1000L)
                }
                if (it == time - 1) {
                    //stop
                    video()
                }
            }
        }
        pop_time_lay.visibility = View.VISIBLE
    }

    protected fun videoTimeClose() {
        flow?.cancel()
        flow = null
        pop_time_lay.visibility = View.GONE
    }

    private var zoomConfig = 1

    /**
     * [Chinese text]settings PopupWindow
     */
    private fun showContrastPopup() {
        thermal_recycler_night.setSettingSelected(SettingType.CONTRAST, true)

        val seekBarPopup = SeekBarPopup(this)
        seekBarPopup.progress = NumberTools.scale(saveSetBean.contrastValue / 2.56f, 0).toInt()
        seekBarPopup.onValuePickListener = {
            saveSetBean.contrastValue = (it * 2.56f).toInt().coerceAtMost(255)
            ircmd?.setContrast(saveSetBean.contrastValue)
        }
        seekBarPopup.setOnDismissListener {
            thermal_recycler_night.setSettingSelected(SettingType.CONTRAST, false)
        }
        seekBarPopup.show(thermal_lay, !saveSetBean.isRotatePortrait())
        popupWindow = seekBarPopup
    }


    private fun getPopupWindowY(contentHeight: Int): Int {
        if (!saveSetBean.isRotatePortrait()) {
            return 0
        }
        val location = IntArray(2)
        thermal_lay.getLocationInWindow(location)
        val menuLocation = IntArray(2)
        thermal_recycler_night.getLocationInWindow(menuLocation)
        return if (location[1] + thermal_lay.measuredHeight > menuLocation[1]) {
            thermal_lay.measuredHeight - contentHeight - (location[1] + thermal_lay.measuredHeight - menuLocation[1])
        } else {
            thermal_lay.measuredHeight - contentHeight
        }
    }


    private var nowZoomLevel = 1

    /**
     * [Chinese text]([Chinese text]) settings PopupWindow
     */
    private fun showSharpnessPopup() {
        thermal_recycler_night.setSettingSelected(SettingType.DETAIL, true)

        val maxSharpness = 4 //[Chinese text] [0, 4], for[Chinese text]
        val seekBarPopup = SeekBarPopup(this)
        seekBarPopup.progress = (saveSetBean.ddeConfig * 100f / maxSharpness).toInt()
        seekBarPopup.onValuePickListener = {
            saveSetBean.ddeConfig = (it * maxSharpness / 100f).roundToInt()
            seekBarPopup.progress = (saveSetBean.ddeConfig * 100f / maxSharpness).toInt()
            ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)
        }
        seekBarPopup.setOnDismissListener {
            thermal_recycler_night.setSettingSelected(SettingType.DETAIL, false)
        }
        seekBarPopup.show(thermal_lay, !saveSetBean.isRotatePortrait())
        popupWindow = seekBarPopup
    }

//    //IMAGE_PROP_LEVEL_SNR (0~3) [Chinese text]([Chinese text]2) [Chinese text]
//    //IMAGE_PROP_LEVEL_TNR (0~3) [Chinese text]([Chinese text]2) [Chinese text]

    /**
     * [Chinese text]
     * IMAGE_PROP_MODE_AGC: [Chinese text]2
     * IMAGE_PROP_ONOFF_AGC: [Chinese text]1
     */
    open fun autoConfig() {
        lifecycleScope.launch(Dispatchers.IO) {
            iruvc?.let {
                if (!it.auto_gain_switch) {
                    switchAutoGain(true)
                    gainSelChar = CameraItemBean.TYPE_TMP_ZD
                    withContext(Dispatchers.Main) {
                        ToastTools.showShort(R.string.auto_open)
                    }
                }
//                else {
//                    switchAutoGain(false)
//                    R.string.auto_close
//                }

            }
        }
        dismissCameraLoading()
        thermal_recycler_night.setTempLevel(CameraItemBean.TYPE_TMP_ZD)
    }


    open fun switchAutoGain(boolean: Boolean){
        iruvc?.auto_gain_switch = boolean
    }


    protected var configJob: Job? = null
    private var temperatureMode: Int = SaveSettingUtil.temperatureMode
    private val timeMillis = 150L

    //[Chinese text]
    protected fun configParam() {
        configJob = lifecycleScope.launch{
//            showLoading()
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
            /// Emissivity property.unit:1/128, range:1-128(0.01-1)
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
//            settingshigh[Chinese text], low[Chinese text]
            setTemperatureMode(temperatureMode, false)
            // [Chinese text]
            delay(timeMillis)
            XLog.w("settingsTPD_PROP DISTANCE:${disChar}, EMS:${emsChar}}")
            if (isFirst && isrun) {
                //[Chinese text]
                thermal_recycler_night.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
                ircmd?.setMirror(saveSetBean.isOpenMirror)
                // [Chinese text]
                delay(timeMillis)
                withContext(Dispatchers.IO) {
                    // [Chinese text], [Chinese text]
                    ircmd?.setAutoShutter(true)
                    delay(2500)
                    ircmd?.setAutoShutter(isAutoShutter)
                    isFirst = false
                }
                //settings[Chinese text]([Chinese text])
                ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)
                //[Chinese text]
                ircmd?.setContrast(saveSetBean.contrastValue)
                if (SaveSettingUtil.isSaveSetting) {
                    XLog.i("[Chinese text]in progress[Chinese text]mode[Chinese text]: ${if (SaveSettingUtil.isMeasureTempMode) "[Chinese text]" else "[Chinese text]"}mode")
                    if(isTS001){
                        switchTs001Mode(SaveSettingUtil.isMeasureTempMode)
                    }else{
                        switchTs001Mode(true)
                    }
                }
            }
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_ONOFF_AGC,
                CommonParams.PropImageParamsValue.StatusSwith.ON
            )
            printSN()
            //[Chinese text]
            if (syncimage.type == 1) {
                ircmd?.tc1bShutterManual()
            } else {
                ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
            }
            XLog.w("settingsTPD_PROP DISTANCE2:${disChar}, EMS:${emsChar}}")

        }
    }

    //settingstdp[Chinese text]
    private fun setTpdParams(params: CommonParams.PropTPDParams, value: String) {
        ircmd?.setPropTPDParams(params, CommonParams.PropTPDParamsValue.NumberType(value))
    }

    //settingsimg[Chinese text]
    private fun setImageParams(params: CommonParams.PropImageParams, value: String) {
        ircmd?.setPropImageParams(params, CommonParams.PropImageParamsValue.NumberType(value))
    }

    private var upValue = -273f
    private var downValue = -273f
    private var upColor = 0
    private var downColor = 0

    //temperaturerange
    private fun addLimit() {
        ThermalInputDialog.Builder(this)
            .setMessage(getString(R.string.thermal_threshold_setting))
            .setNum(max = upValue, min = downValue)
            .setColor(maxColor = upColor, minColor = downColor)
            .setPositiveListener(R.string.app_confirm) { up, down, upColor, downColor ->
                this.upValue = up
                this.downValue = down
                this.upColor = upColor
                this.downColor = downColor
                imageThread?.setLimit(upValue, downValue, upColor, downColor) //[Chinese text]
            }
            .setCancelListener(getString(R.string.app_close)) {
                upValue = -273f
                downValue = -273f
                imageThread?.setLimit(upValue, downValue, upColor, downColor) //[Chinese text]
            }
            .create().show()
    }

    private var isOpenPreview = false

    /**
     * switch [Chinese text]in progress[Chinese text] [Chinese text] [Chinese text]
     */
    private fun cameraPreviewConfig(needShowTip: Boolean) {
        if (!CheckDoubleClick.isFastDoubleClick()) {
            if (isOpenPreview) {
                //[Chinese text]
                isOpenPreview = false
                cameraPreview.closeCamera()
                thermal_recycler_night.setTwoLightSelected(TwoLightType.P_IN_P, false)
                cameraPreview.visibility = View.INVISIBLE
                SaveSettingUtil.isOpenTwoLight = false
            } else {
                checkCameraPermission(needShowTip)
            }
        }
    }

    override fun disConnected() {
        super.disConnected()
        finish()
    }

    override fun onBackPressed() {
        setResult(200)
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun iruvctc(event: PreviewComplete) {
        dealY16ModePreviewComplete()
    }

    private fun dealY16ModePreviewComplete() {
        iruvc?.setFrameReady(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun cameraEvent(event: DeviceCameraEvent) {
        when (event.action) {
            100 -> {
                //[Chinese text]
                showCameraLoading()
            }

            101 -> {
                //[Chinese text]
                lifecycleScope.launch {
                    delay(500)
                    isConfigWait = false
                    delay(1000)
                    dismissCameraLoading()
                }
            }
        }
    }


    /**
     * [Chinese text]
     */
    private fun printSN() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fwBuildVersionInfoBytes = ByteArray(50)
                val getSnBytes = ByteArray(16)
                ircmd?.getDeviceInfo(
                    CommonParams.DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
                    fwBuildVersionInfoBytes
                ) //ok
                ircmd?.getDeviceInfo(CommonParams.DeviceInfoType.DEV_INFO_GET_SN, getSnBytes) //ok
                val snStr = String(getSnBytes) //sn
                val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
                SharedManager.setDeviceSn(snStr)
                SharedManager.setDeviceVersion(arm)
                val infoBuilder = StringBuilder()
                infoBuilder.append("Firmware version: ").append(arm).append("<br>")
                infoBuilder.append("SN: ").append(snStr).append("<br>")
                val str =
                    HtmlCompat.fromHtml(
                        infoBuilder.toString(),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                XLog.i("[Chinese text]: $str")
            } catch (e: Exception) {
                XLog.e("[Chinese text]SN[Chinese text]: ${e.message}")
            }
        }
    }

    override fun tempCorrectByTs(temp: Float): Float {
        var tmp = temp
        try {
            tmp = tempCorrect(temp, gainStatus, tempinfo)
        } catch (e: Exception) {
            XLog.i("temperature[Chinese text]: ${e.message}")
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
     * [Chinese text]listener
     */
    private fun onCompassUpdate(): Boolean {
        val azimuthTxt = formatDegrees(compass.bearing.value, replace360 = true)
        compassView.setCurAzimuth(azimuthTxt.first.toInt())
        return true
    }

    /**
     * [Chinese text], [Chinese text]
     */
    private fun formatDegrees(
        degrees: Float,
        decimalPlaces: Int = 0,
        replace360: Boolean = false
    ): Pair<Float, Float> {
        val formatted = DecimalFormatter.format(degrees.toDouble(), decimalPlaces)
        val finalFormatted = if (replace360) formatted.replace("360", "0") else formatted
        return Pair(formatted.toFloat(), finalFormatted.toFloat())
    }

    private fun checkCameraPermission(needShowTip:Boolean) {
        if (!XXPermissions.isGranted(
                this,
                Permission.CAMERA
            )
        ) {
            if (BaseApplication.instance.isDomestic()) {
                TipDialog.Builder(this)
                    .setMessage(getString(R.string.permission_request_camera_app, CommUtils.getAppName()))
                    .setCancelListener(R.string.app_cancel)
                    .setPositiveListener(R.string.app_confirm) {
                        initCameraPermission(needShowTip)
                    }
                    .create().show()
            } else {
                initCameraPermission(needShowTip)
            }
        } else {
            initCameraPermission(needShowTip)
        }
    }

    private fun initCameraPermission(needShowTip:Boolean) {
        XXPermissions.with(this@IRThermalNightActivity)
            .permission(Permission.CAMERA)
            .request(object : OnPermissionCallback {
                override fun onGranted(
                    permissions: MutableList<String>,
                    allGranted: Boolean
                ) {
                    try {
                        if (allGranted) {
                            //[Chinese text]in progress[Chinese text]
                            thermal_recycler_night.setTwoLightSelected(TwoLightType.P_IN_P, true)
                            cameraPreview.visibility = View.VISIBLE
                            cameraPreview?.setCameraAlpha(cameraAlpha / 100.0f)
                            cameraPreview.post {
                                isOpenPreview = true
                                cameraPreview.openCamera()
                                SaveSettingUtil.isOpenTwoLight = true
                            }
                            if (needShowTip && SharedManager.isTipPinP) {
                                val dialog = TipPreviewDialog.newInstance()
                                dialog.closeEvent = {
                                    SharedManager.isTipPinP = !it
                                }
                                dialog.show(supportFragmentManager, "")
                            }
                        } else {
                            thermal_recycler_night.setTwoLightSelected(TwoLightType.P_IN_P, false)
                            ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                        }
                    } catch (e: Exception) {
                        XLog.e("[Chinese text]in progress[Chinese text]" + e.message)
                    }
                }

                override fun onDenied(
                    permissions: MutableList<String>,
                    doNotAskAgain: Boolean
                ) {
                    if (doNotAskAgain) {
                        //[Chinese text]
                        if (BaseApplication.instance.isDomestic()){
                            ToastUtils.showShort(getString(R.string.app_camera_content))
                            return
                        }
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setTitleMessage(getString(R.string.app_tip))
                            .setMessage(getString(R.string.app_camera_content))
                            .setPositiveListener(R.string.app_open) {
                                AppUtils.launchAppDetailsSettings()
                            }
                            .setCancelListener(R.string.app_cancel) {
                            }
                            .setCanceled(true)
                            .create().show()
                    }
                    thermal_recycler_night.setTwoLightSelected(TwoLightType.P_IN_P, false)
                }
            })
    }

    private fun checkStoragePermission() {
        if (!XXPermissions.isGranted(this,permissionList)) {
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
    private fun initAudioPermission(){
        XXPermissions.with(this@IRThermalNightActivity)
            .permission(
                Permission.RECORD_AUDIO,
            )
            .request(object : OnPermissionCallback {
                override fun onGranted(
                    permissions: MutableList<String>,
                    allGranted: Boolean
                ) {
                    try {
                        if (allGranted) {
                            //[Chinese text]
                            isRecordAudio = true
                            SaveSettingUtil.isRecordAudio = isRecordAudio
                            videoRecord?.updateAudioState(true)
                            cameraItemAdapter?.data?.get(audioPosition)?.isSel = !(cameraItemAdapter?.data?.get(audioPosition)?.isSel?:false)
                            cameraItemAdapter?.notifyItemChanged(audioPosition)
                        } else {
                            ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                        }
                    } catch (e: Exception) {
                        Log.e("[Chinese text]", "" + e.message)
                    }
                }

                override fun onDenied(
                    permissions: MutableList<String>,
                    doNotAskAgain: Boolean
                ) {
                    if (doNotAskAgain) {
                        //[Chinese text]
                        if (BaseApplication.instance.isDomestic()){
                            ToastUtils.showShort(getString(R.string.app_microphone_content))
                            return
                        }
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setTitleMessage(getString(R.string.app_tip))
                            .setMessage(getString(R.string.app_microphone_content))
                            .setPositiveListener(R.string.app_open) {
                                AppUtils.launchAppDetailsSettings()
                            }
                            .setCancelListener(R.string.app_cancel) {
                            }
                            .setCanceled(true)
                            .create().show()
                    }
                }
            })
    }

    private fun initStoragePermission() {
        XXPermissions.with(this)
            .permission(
                permissionList
            )
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (allGranted) {
                        if (!thermal_recycler_night.isVideoMode) {
                            val setting = SharedManager.continuousBean
                            if (setting.isOpen) {
                                if (!isAutoCamera) {
                                    //[Chinese text]photo capture
                                    autoJob = countDownCoroutines(
                                        setting.count,
                                        setting.continuaTime,
                                        this@IRThermalNightActivity.lifecycleScope,
                                        onTick = {
                                            camera()
                                        }, onStart = {
                                            tv_type_ind?.visibility = VISIBLE
                                            isAutoCamera = true
                                        }, onFinish = {
                                            tv_type_ind?.visibility = GONE
                                            isAutoCamera = false
                                        })
                                    autoJob?.start()
                                } else {
                                    isAutoCamera = false
                                    autoJob?.cancel()
                                }
                            } else {
                                camera()
                            }
                        } else {
                            //[Chinese text]
                            video()
                        }
                    } else {
                        ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                    }
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        //[Chinese text]
                        if (BaseApplication.instance.isDomestic()){
                            ToastUtils.showShort(getString(R.string.app_storage_content))
                            return
                        }
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setTitleMessage(getString(R.string.app_tip))
                            .setMessage(R.string.app_storage_content)
                            .setPositiveListener(R.string.app_open) {
                                AppUtils.launchAppDetailsSettings()
                            }
                            .setCancelListener(R.string.app_cancel) {
                            }
                            .setCanceled(true)
                            .create().show()
                    }
                }
            })
    }

    private fun setCarDetectPrompt(){
        var carDetectInfo = SharedManager.getCarDetectInfo()
        var tvDetectPrompt = view_car_detect.findViewById<TextView>(R.id.tv_detect_prompt)
        if(carDetectInfo == null){
            tvDetectPrompt.text =  getString(R.string.abnormal_item1) + TemperatureUtil.getTempStr(40, 70)
        }else{
            var temperature = carDetectInfo.temperature.split("~")
            tvDetectPrompt.text =  carDetectInfo.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
        }
        val test = intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)
        lay_car_detect_prompt.visibility = if(intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)) View.VISIBLE else View.GONE
        view_car_detect.findViewById<RelativeLayout>(R.id.rl_content).setOnClickListener {
            CarDetectDialog(this) {
                var temperature = it.temperature.split("~")
                tvDetectPrompt.text =  it.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
            }.show()
        }
    }
}
