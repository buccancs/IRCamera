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
import com.csl.irCamera.libapp.R as LibAppR
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
import com.topdon.module.thermal.ir.databinding.ActivityThermalIrNightBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt


@Route(path = RouterConfig.IR_FRAME)
open class IRThermalNightActivity : BaseIRActivity(), ITsTempListener {

    protected lateinit var binding: ActivityThermalIrNightBinding

     * SDK
     * +
    protected val defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT


     * start
     *  ircmd  ircmd  false
    protected var isConfigWait = true

     *  onStart()
    protected var isrun = false

     * .
    protected var ircmd: IRCMD? = null

     * .
    protected var pseudoColorMode: Int = SaveSettingUtil.pseudoColorMode

    // 1: 0:
    protected var gainSelChar = -2
    protected var editMaxValue = Float.MAX_VALUE
    protected var editMinValue = Float.MIN_VALUE
    protected var alarmBean = SaveSettingUtil.alarmBean
    protected var customPseudoBean = CustomPseudoBean.loadFromShared()

    private var initRotate = 0//
    private var correctRotate = 0//

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

    override fun initContentView(): Int {
        binding = ActivityThermalIrNightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        return 0
    }

    private var hasCompass = true
    private lateinit var compass: ICompass
    private lateinit var sensorService: SensorService

     * PopupWindow.
    private var popupWindow: PopupWindow? = null

     *  TS001 .
     * true- false
    private var isTs001TempMode = true

    //tab position1
    protected var curChooseTabPos = 1

    //1- 2
    private var curTargetStyle = 1

     * dialog
    @Volatile
    private var isTempShowDialog = false

    private var storageRequestType = 0

     * TODO TS001
    private var aiConfig = SaveSettingUtil.aiTraceType

    private var isOnRestart = false
    private var emissivityConfig : DataBean? = null
    protected var isOpenAmplify = SaveSettingUtil.isOpenAmplify
    private val bitmapWidth: Int
        get() = if (isOpenAmplify) imageWidth * ImageThreadTC.MULTIPLE else imageWidth

    private val bitmapHeight: Int
        get() = if (isOpenAmplify) imageHeight * ImageThreadTC.MULTIPLE else imageHeight

     * IOS
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
                            .setMessage(LibAppR.string.tips_tisr_fail)
                            .setPositiveListener(LibAppR.string.app_got_it) {
                            }
                            .create().show()
                    }
                    XLog.e("")
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
            binding.cameraView.bitmap = bitmap
            binding.cameraView.isOpenAmplify = isOpenAmplify
            binding.titleView.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
            SaveSettingUtil.isOpenAmplify = isOpenAmplify
            if (isOpenAmplify){
                ToastUtils.showShort(LibAppR.string.tips_tisr_on)
            }else{
                ToastUtils.showShort(LibAppR.string.tips_tisr_off)
            }
        }
    }


     open fun initAmplify(show : Boolean){
        lifecycleScope.launch {
            if (show){
                binding.titleView.setRight2Drawable(if (isOpenAmplify) R.drawable.svg_tisr_on else R.drawable.svg_tisr_off)
            }else{
                binding.titleView.setRight2Drawable(0)
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
            binding.cameraView.bitmap = bitmap
            binding.cameraView.isOpenAmplify = isOpenAmplify
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.titleView.setLeftClickListener {
            if (binding.timeDownView.isRunning) {
                return@setLeftClickListener
            }
            setResult(200)
            finish()
        }
        binding.titleView.setRight2ClickListener{
            if (SupHelp.getInstance().loadOpenclSuccess){
                switchAmplify()
            }else{
                TipDialog.Builder(this)
                    .setMessage(LibAppR.string.tips_tisr_fail)
                    .setPositiveListener(LibAppR.string.app_got_it) {
                    }
                    .create().show()
            }
        }
        binding.titleView.setRightClickListener {
            val config = ConfigRepository.readConfig(false)
            var text = ""
            for (tmp in IRConfigData.irConfigData(this@IRThermalNightActivity)){
                if (config.radiation.toString() == tmp.value){
                    if (text.isEmpty()){
                        text = "${resources.getString(LibAppR.string.tc_temp_test_materials)} : "
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
                .showAsDropDown(binding.titleView, 0, 0, Gravity.END)
        }
        binding.tvTitleTemp.isSelected = true
        binding.tvTitleTemp.setOnClickListener {
            switchTs001Mode(true)
        }
        binding.tvTitleObserve.setOnClickListener {
            switchTs001Mode(false)
        }


        binding.viewCarDetect.llCarDetectInfo.setOnClickListener {
            LongTextDialog(this, SharedManager.getCarDetectInfo().item, SharedManager.getCarDetectInfo().description).show()
        }
        BarUtils.setStatusBarColor(this, 0xff16131e.toInt())
        BarUtils.setNavBarColor(window, 0xff16131e.toInt())
        initRecycler()
        binding.viewMenuFirst.onTabClickListener = {
            ViewStubUtils.showViewStub(binding.viewStubCamera, false, null)
            popupWindow?.dismiss()
            binding.temperatureView.isEnabled = it.selectPosition == 1
            showTempRecyclerNight(it.isObserveMode, it.selectPosition)
        }
        binding.temperatureSeekbar.setIndicatorTextDecimalFormat("0.0")
        updateTemperatureSeekBar(false)//
        isShowC = getTemperature() == 1
        binding.temperatureView.setTextSize(saveSetBean.tempTextSize)
        binding.temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
        binding.temperatureView.listener = TempListener { max, min, _ ->
            realLeftValue = UnitTools.showUnitValue(min, isShowC)
            realRightValue = UnitTools.showUnitValue(max, isShowC)
            this@IRThermalNightActivity.runOnUiThread {
                if (!customPseudoBean.isUseCustomPseudo) {
                    try {
                        binding.temperatureSeekbar.setRangeAndPro(
                            UnitTools.showUnitValue(editMinValue, isShowC),
                            UnitTools.showUnitValue(editMaxValue, isShowC),
                            realLeftValue,
                            realRightValue
                        )
                        if (editMinValue != Float.MIN_VALUE && editMaxValue != Float.MAX_VALUE) {
                            imageThread?.setLimit(
                                editMaxValue, editMinValue,
                                upColor, downColor
                            ) //
                        }
                    } catch (e: Exception) {
                        Log.e("", e.message.toString())
                    }
                    try {
                        binding.tvTempContent.text = "Max:${UnitTools.showC(max, isShowC)}\nMin:${
                            UnitTools.showC(
                                min,
                                isShowC
                            )
                        }"
                    } catch (e: Exception) {
                        Log.e("", e.message.toString())
                    }
                } else {
                    try {
                        binding.tvTempContent.text = " Max:${UnitTools.showC(max, isShowC)}\n Min:${
                            UnitTools.showC(
                                min,
                                isShowC
                            )
                        }"
                    } catch (e: Exception) {
                        Log.e("", e.message.toString())
                    }
                }
                try {
                    if (isVideo) {
                        binding.clSeekBar.requestLayout()
                        binding.clSeekBar.updateBitmap()
                    }
                } catch (e: Exception) {
                    Log.w(":", "${e.message}")
                }
                try {
                    AlarmHelp.getInstance(application).alarmData(max, min, binding.tempBg)
                } catch (e: Exception) {
                    Log.e("", e.message.toString())
                }
            }

        }
        binding.temperatureView.setOnTrendChangeListener {
            lifecycleScope.launch(Dispatchers.Main) {
                if (binding.clTrendOpen.isVisible) {
                    binding.viewChartTrend.refresh(it)
                }
            }
        }
        binding.temperatureView.setOnTrendAddListener {
            if (hasClickTrendDel) {
                hasClickTrendDel = false
                binding.clTrendOpen.isVisible = true
                binding.llTrendClose.isVisible = false
            }
        }
        binding.temperatureView.setOnTrendRemoveListener {
            binding.viewChartTrend.setToEmpty()
        }

        binding.thermalRecyclerNight.isVideoMode = SaveSettingUtil.isVideoMode ///
        binding.thermalRecyclerNight.fenceSelectType = FenceType.FULL //
        binding.thermalRecyclerNight.isUnitF = SharedManager.getTemperature() == 0 //
        binding.thermalRecyclerNight.setSettingRotate(saveSetBean.rotateAngle) //
        binding.thermalRecyclerNight.setTempLevel(temperatureMode) //

        binding.thermalRecyclerNight.setSettingSelected(SettingType.FONT, !saveSetBean.isTempTextDefault())

        binding.popTimeLay.visibility = View.GONE
        binding.cameraPreview.visibility = View.INVISIBLE
        initOrientationEventListener()
        addTemperatureListener()
        binding.cameraView.postDelayed(500) {
            if (SaveSettingUtil.isOpenTwoLight && XXPermissions.isGranted(this, Permission.CAMERA)) {
                cameraPreviewConfig(false)
            }
        }
        if (ScreenTool.isIPad(this)) {
            binding.clSeekBar.setPadding(0, SizeUtils.dp2px(40f), 0, SizeUtils.dp2px(40f))
        }
        DragViewUtil.registerDragAction(binding.zoomView)
        initCompass()
        binding.distanceMeasureView?.moveListener = {
            binding.thermalText.text = "：${it / binding.thermalLay.measuredHeight * 256}"
        }
        lifecycleScope.launch {
            delay(1000)
            if (!SharedManager.isHideEmissivityTips){
                showEmissivityTips()
            }
        }

        binding.ivTrendClose.setOnClickListener {
            binding.clTrendOpen.isVisible = false
            binding.llTrendClose.isVisible = true
        }
        binding.ivTrendOpen.setOnClickListener {
            binding.clTrendOpen.isVisible = true
            binding.llTrendClose.isVisible = false
        }
        startUSB(isRestart = false,false)
    }

     *  TS001  / .
     * @param isToTemp true- false
    private fun switchTs001Mode(isToTemp: Boolean) {
        if (isToTemp == isTs001TempMode) {
            return
        }
        binding.tvTitleTemp.isSelected = isToTemp
        binding.tvTitleObserve.isSelected = !isToTemp
        SaveSettingUtil.isMeasureTempMode = isToTemp

        // PopupWindow
        ViewStubUtils.showViewStub(binding.viewStubCamera, false, null)
        popupWindow?.dismiss()

        showCameraLoading()

        stopIfVideoing() //

        isAutoCamera = false
        autoJob?.cancel()

        if (binding.timeDownView.isRunning) {
            binding.timeDownView.cancel()
            updateDelayView()
        }

        setDefLimit()
        updateTemperatureSeekBar(false)//

        if (isToTemp) {//->
            if (SaveSettingUtil.isOpenTwoLight && XXPermissions.isGranted(this, Permission.CAMERA)) {
                cameraPreviewConfig(false)
            }

            // AI ()
            aiConfig = ObserveBean.TYPE_NONE
            imageThread?.typeAi = aiConfig
            binding.thermalRecyclerNight.setTempSource(aiConfig)

            binding.thermalRecyclerNight.setTargetSelected(TargetType.MODE, false)
            binding.thermalRecyclerNight.setTargetSelected(TargetType.STYLE, false)
            binding.thermalRecyclerNight.setTargetSelected(TargetType.COLOR, false)
            binding.thermalRecyclerNight.setTargetSelected(TargetType.DELETE, false)
            binding.thermalRecyclerNight.setTargetMode(ObserveBean.TYPE_MEASURE_PERSON) //
            targetMeasureMode = ObserveBean.TYPE_MEASURE_PERSON
            targetStyle = ObserveBean.TYPE_TARGET_HORIZONTAL
            targetColorType = ObserveBean.TYPE_TARGET_COLOR_GREEN
            binding.zoomView.hideView()

            saveSetBean.isOpenCompass = false
            binding.thermalRecyclerNight.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)
            binding.compassView.visibility = View.GONE
            binding.zoomView?.visibility = View.GONE
            stopCompass()
            binding.zoomView.del(true)

            saveSetBean.isOpenPseudoBar = SaveSettingUtil.isOpenPseudoBar
            binding.clSeekBar.isVisible = saveSetBean.isOpenPseudoBar
            binding.thermalRecyclerNight.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)
            binding.temperatureSeekbar?.setPseudocode(pseudoColorMode)

            binding.temperatureView.clear()
            binding.temperatureView.isUserHighTemp = false
            binding.temperatureView.isUserLowTemp = false
            binding.temperatureView.isVisible = true
            binding.temperatureView.temperatureRegionMode = REGION_MODE_CENTER
            showCross(false)
            binding.thermalRecyclerNight.clearTempPointSelect()
            binding.thermalRecyclerNight.fenceSelectType = FenceType.FULL //

            alarmBean = SaveSettingUtil.alarmBean
            imageThread?.alarmBean = alarmBean
            if (alarmBean.isHighOpen || alarmBean.isLowOpen) {
                binding.thermalRecyclerNight.setSettingSelected(SettingType.ALARM, true)
                AlarmHelp.getInstance(this).updateData(alarmBean)
            } else {
                binding.thermalRecyclerNight.setSettingSelected(SettingType.ALARM, false)
                AlarmHelp.getInstance(this).updateData(null, null, null)
            }
        } else {//->
            if (isOpenPreview) {
                isOpenPreview = false
                binding.cameraPreview.closeCamera()
                binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                binding.cameraPreview.visibility = View.INVISIBLE
            }

            binding.temperatureView.clear()
            binding.temperatureView.visibility = View.INVISIBLE
            binding.temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
            hasClickTrendDel = true
            binding.spaceChart.isVisible = false
            binding.clTrendOpen.isVisible = false
            binding.llTrendClose.isVisible = false
            showCross(false)

            switchTempGain(isLow = true, false)

            aiConfig = SaveSettingUtil.aiTraceType
            imageThread?.typeAi = aiConfig
            binding.thermalRecyclerNight.setTempSource(aiConfig)

            saveSetBean.isOpenCompass = SaveSettingUtil.isOpenCompass
            binding.thermalRecyclerNight.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)

            if(SaveSettingUtil.isOpenHighPoint || SaveSettingUtil.isOpenLowPoint){
                binding.temperatureView.temperatureRegionMode = REGION_MODE_RESET
                binding.temperatureView.visibility = View.VISIBLE
            }
            binding.temperatureView.isUserHighTemp = SaveSettingUtil.isOpenHighPoint
            binding.temperatureView.isUserLowTemp = SaveSettingUtil.isOpenLowPoint
            binding.thermalRecyclerNight.setTempPointSelect(TempPointType.HIGH, SaveSettingUtil.isOpenHighPoint)
            binding.thermalRecyclerNight.setTempPointSelect(TempPointType.LOW, SaveSettingUtil.isOpenLowPoint)

            targetMeasureMode = SaveSettingUtil.targetMeasureMode
            targetStyle = SaveSettingUtil.targetType
            targetColorType = SaveSettingUtil.targetColorType
            binding.thermalRecyclerNight.setTargetMode(targetMeasureMode)
            binding.thermalRecyclerNight.setTargetSelected(TargetType.COLOR, targetColorType != ObserveBean.TYPE_TARGET_COLOR_GREEN)

            binding.clSeekBar.visibility = View.GONE

            if (SharedManager.isTipObservePhoto) {
                TipObserveDialog.Builder(this)
                    .setTitle(LibAppR.string.app_tip)
                    .setMessage(LibAppR.string.tips_observe_photo_content)
                    .setCancelListener { isCheck ->
                        SharedManager.isTipObservePhoto = !isCheck
                    }
                    .create().show()
            }

            alarmBean = AlarmBean()
            imageThread?.alarmBean = alarmBean
            AlarmHelp.getInstance(this).updateData(null, null, null)
        }

        customPseudoBean.isUseCustomPseudo = false
        customPseudoBean.saveToShared()
        updateImageAndSeekbarColorList(customPseudoBean)

        if (!SaveSettingUtil.isSaveSetting) {
            setPColor(3)

            cameraDelaySecond = DELAY_TIME_0
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[0].time = DELAY_TIME_0
                cameraItemAdapter!!.notifyItemChanged(0)
            }

            isRecordAudio = false
            videoRecord?.updateAudioState(false)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[3].isSel = false
                cameraItemAdapter!!.notifyItemChanged(3)
            }

            isAutoShutter = true
            ircmd?.setAutoShutter(isAutoShutter)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[1].isSel = true
                cameraItemAdapter!!.notifyItemChanged(1)
            }

            SaveSettingUtil.isVideoMode = false
            binding.thermalRecyclerNight.switchToCamera()

            saveSetBean.contrastValue = 128
            ircmd?.setContrast(saveSetBean.contrastValue)

            saveSetBean.ddeConfig = 2
            ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)

            saveSetBean.rotateAngle = DeviceConfig.S_ROTATE_ANGLE
            updateRotateAngle(saveSetBean.rotateAngle)

            saveSetBean.tempTextColor = 0xffffffff.toInt()
            saveSetBean.tempTextSize = SizeUtils.sp2px(14f)
            binding.temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
            binding.temperatureView.setTextSize(saveSetBean.tempTextSize)

            zoomConfig = 1

            saveSetBean.isOpenMirror = false
            binding.thermalRecyclerNight.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
            ircmd?.setMirror(saveSetBean.isOpenMirror)
        }

        curChooseTabPos = if (isToTemp) Constants.IR_TEMPERATURE_MODE else Constants.IR_OBSERVE_MODE
        isTs001TempMode = isToTemp
        binding.thermalRecyclerNight.selectPosition(if (isToTemp) 0 else 10)
        binding.viewMenuFirst.isObserveMode = !isToTemp

        updateCompass()

         * 4dismissCameraLoading
         * 4
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
                    text = "${resources.getString(LibAppR.string.tc_temp_test_materials)} : "
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
            binding.compassView.visibility = View.GONE
            stopCompass()
        } else {
            if (saveSetBean.isOpenCompass) {
                startCompass()
                binding.compassView.visibility = View.VISIBLE
            }
        }
    }

    private fun initCompass() {
        sensorService = SensorService(this)
        hasCompass = sensorService.hasCompass()
        compass = sensorService.getCompass()
    }

    /** isTouchSeekBar property */
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

    private fun updateImageAndSeekbarColorList(customPseudoBean: CustomPseudoBean?) {
        customPseudoBean?.let {
            binding.temperatureSeekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
            binding.temperatureSeekbar.setPlaces(customPseudoBean.getPlaceList())
            if (it.isUseCustomPseudo) {
                binding.temperatureIvLock.visibility = View.INVISIBLE
                binding.tvTempContent.visibility = View.VISIBLE
                updateTemperatureSeekBar(false)//
                binding.temperatureSeekbar.setRangeAndPro(
                    UnitTools.showUnitValue(it.minTemp),
                    UnitTools.showUnitValue(it.maxTemp), UnitTools.showUnitValue(it.minTemp),
                    UnitTools.showUnitValue(it.maxTemp)
                )
                setDefLimit()
                binding.thermalRecyclerNight.setPseudoColor(-1)
                binding.temperatureIvInput.setImageResource(R.drawable.ir_model)
            } else {
                binding.temperatureIvLock.visibility = View.VISIBLE
                binding.thermalRecyclerNight.setPseudoColor(pseudoColorMode)
                if (this.customPseudoBean.isUseCustomPseudo) {
                    setDefLimit()
                }
                binding.tvTempContent.visibility = View.GONE
                binding.temperatureIvInput.setImageResource(R.drawable.ic_color_edit)
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

        binding.temperatureIvLock.setOnClickListener {
            if (binding.temperatureIvLock.visibility != View.VISIBLE) {
                return@setOnClickListener
            }
            if (binding.temperatureIvLock.contentDescription == "lock") {
                updateTemperatureSeekBar(true)//
            } else {
                setDefLimit()
                updateTemperatureSeekBar(false)//
            }
        }
        binding.temperatureIvInput.setOnClickListener {
            val intent = Intent(this, PseudoSetActivity::class.java)
            intent.putExtra(ExtraKeyConfig.IS_TC007, false)
            pseudoSetResult.launch(intent)
        }
        binding.temperatureSeekbar.setOnRangeChangedListener(object : OnRangeChangedListener {
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
                    ) //
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




    private fun setDefLimit() {
        editMaxValue = Float.MAX_VALUE
        editMinValue = Float.MIN_VALUE
        imageThread?.setLimit(editMaxValue, editMinValue, upColor, downColor) //
        binding.temperatureSeekbar.setRangeAndPro(editMinValue, editMaxValue, realLeftValue, realRightValue) //
    }

    private fun updateTemperatureSeekBar(isEnabled: Boolean) {
        binding.temperatureSeekbar.isEnabled = isEnabled
        binding.temperatureSeekbar.drawIndPath(isEnabled)
        binding.temperatureIvLock.setImageResource(if (isEnabled) R.drawable.svg_pseudo_bar_unlock else R.drawable.svg_pseudo_bar_lock)
        binding.temperatureIvLock.contentDescription = if (isEnabled) "unlock" else "lock"
        if (isEnabled) {
            binding.temperatureSeekbar.tempMode = RangeSeekBar.TEMP_MODE_CLOSE
            binding.temperatureSeekbar.leftSeekBar.indicatorBackgroundColor = 0xffe17606.toInt()
            binding.temperatureSeekbar.rightSeekBar.indicatorBackgroundColor = 0xffe17606.toInt()
            binding.temperatureSeekbar.invalidate()
        } else {
            binding.temperatureSeekbar.leftSeekBar.indicatorBackgroundColor = 0
            binding.temperatureSeekbar.rightSeekBar.indicatorBackgroundColor = 0
            binding.temperatureSeekbar.invalidate()
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
                                binding.cameraPreview?.setRotation(false)
                            }
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else (if (orientation in 135..225) {
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
                            ActivityInfo.SCREEN_ORIENTATION_LOCKED
                        })
                }
            }
    }

    private fun updateRotateAngle(rotateAngle: Int) {
        // limit
        imageThread?.setLimit(
            editMaxValue,
            editMinValue,
            upColor, downColor
        ) //
        lifecycleScope.launch {
            if(curChooseTabPos == Constants.IR_TEMPERATURE_MODE){
                binding.temperatureView.clear()
                binding.temperatureView.temperatureRegionMode = REGION_MODE_CENTER
                hasClickTrendDel = true
                binding.spaceChart.isVisible = false
                binding.clTrendOpen.isVisible = false
                binding.llTrendClose.isVisible = false
                binding.thermalRecyclerNight.fenceSelectType = FenceType.FULL //
            }
            setRotate(rotateAngle)
            delay(100)
            binding.thermalRecyclerNight.setSettingRotate(rotateAngle)
        }
    }

     * 270
    open fun setRotate(rotateInt: Int) {
        imageThread?.setRotate(rotateInt)
        iruvc?.setRotate(rotateInt)
        imageThread?.interrupt()

        if (rotateInt == 0 || rotateInt == 180) {
            bitmap = Bitmap.createBitmap(bitmapHeight, bitmapWidth, Bitmap.Config.ARGB_8888)
            if (getProductName() != PRODUCT_NAME_TCP){
                binding.temperatureView.setImageSize(imageHeight, imageWidth, this)
            }
            binding.cameraView.setImageSize(imageHeight, imageWidth)
            setViewLay(false)
        } else {
            bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            if (getProductName() != PRODUCT_NAME_TCP) {
                binding.temperatureView.setImageSize(imageWidth, imageHeight, this)
            }
            binding.cameraView.setImageSize(imageWidth, imageHeight)
            setViewLay(true)
        }

        try {
            imageThread?.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "imageThread.join(): catch an interrupted exception")
        }
        startISP()


        binding.cameraView.bitmap = bitmap
        imageThread?.setBitmap(bitmap)
        runOnUiThread {
            binding.clSeekBar.requestLayout()
            binding.clSeekBar.updateBitmap()
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
            binding.tvTypeInd?.visibility = GONE
            startISP()
            binding.temperatureView.start()
            binding.cameraView?.start()
            isrun = true
            configParam()
            binding.thermalRecyclerNight.updateCameraModel()
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
        binding.thermalRecyclerNight.refreshImg()
        startOrientation()
        if (curChooseTabPos != 1 && isOpenTarget && binding.zoomView.visibility == View.VISIBLE) {
            binding.zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,binding.thermalLay)
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
        Log.w(": ", "mOrientation: $mOrientation")
    }

    private fun initRecycler() {
        binding.thermalRecyclerNight.onCameraClickListener = {
            setCamera(it)
        }
        binding.thermalRecyclerNight.onFenceListener = { fenceType, isSelected ->
            setTemp(fenceType, isSelected)
        }
        binding.thermalRecyclerNight.onColorListener = { _, it, _ ->
            if (customPseudoBean.isUseCustomPseudo) {
                TipDialog.Builder(this)
                    .setTitleMessage(getString(LibAppR.string.app_tip))
                    .setMessage(LibAppR.string.tip_change_pseudo_mode)
                    .setPositiveListener(LibAppR.string.app_yes) {
                        customPseudoBean.isUseCustomPseudo = false
                        customPseudoBean.saveToShared()
                        setPColor(it)
                        setDefLimit()
                        updateImageAndSeekbarColorList(customPseudoBean)
                    }.setCancelListener(LibAppR.string.app_no) {

                    }
                    .create().show()
            } else {
                setPColor(it)
            }
        }
        binding.thermalRecyclerNight.onSettingListener = { type, isSelected ->
            setSetting(type, isSelected)
        }
        binding.thermalRecyclerNight.onTempLevelListener = {
            temperatureMode = it
            SaveSettingUtil.temperatureMode = temperatureMode
            setTemperatureMode(it,true)
            if (it == CameraItemBean.TYPE_TMP_H && SharedManager.isTipHighTemp) {//
                val message = SpanBuilder(getString(LibAppR.string.tc_high_temp_test_tips1))
                    .appendDrawable(this@IRThermalNightActivity,
                        R.drawable.svg_title_temp, SizeUtils.sp2px(24f))
                    .append(getString(LibAppR.string.tc_high_temp_test_tips2))
                TipShutterDialog.Builder(this)
                    .setTitle(LibAppR.string.tc_high_temp_test)
                    .setMessage(message)
                    .setCancelListener { isCheck ->
                        SharedManager.isTipHighTemp = !isCheck
                    }
                    .create().show()
            }
        }
        binding.thermalRecyclerNight.onTwoLightListener = { twoLightType, isSelected ->
            setTwoLight(twoLightType, isSelected)
        }
        binding.cameraPreview.cameraPreViewCloseListener = {
            if (isOpenPreview) {
                popupWindow?.dismiss()
                cameraPreviewConfig(false)
            }
        }
        binding.thermalRecyclerNight.onTempSourceListener = {
            setAiState(it)
        }
        binding.thermalRecyclerNight.onTargetListener = {
            setTarget(it)
        }
        binding.thermalRecyclerNight.onTempPointListener = { type, isSelected ->
            when (type) {
                TempPointType.HIGH -> {
                    SaveSettingUtil.isOpenHighPoint = isSelected
                    binding.temperatureView.temperatureRegionMode = REGION_MODE_RESET
                    binding.temperatureView.visibility = View.VISIBLE
                    binding.temperatureView.setUserHighTemp(isSelected)
                }
                TempPointType.LOW -> {
                    SaveSettingUtil.isOpenLowPoint = isSelected
                    binding.temperatureView.temperatureRegionMode = REGION_MODE_RESET
                    binding.temperatureView.visibility = View.VISIBLE
                    binding.temperatureView.setUserLowTemp(isSelected)
                }
                TempPointType.DELETE -> {
                    binding.temperatureView.setUserHighTemp(false)
                    binding.temperatureView.setUserLowTemp(false)
                    binding.temperatureView.clear()
                    binding.temperatureView.visibility = View.INVISIBLE
                    binding.temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
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

     * @param isLow true-( false-()
    private fun switchTempGain(isLow: Boolean, isShowLoading: Boolean) {
        if ((gainSelChar == 1 && isLow) || (gainSelChar == 0 && !isLow)) {//
            return
        }
        isTempShowDialog = true
        binding.thermalRecyclerNight.setTempLevel(if (isLow) 1 else 0)
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

     *  AI
    private fun switchTempSource(isTempSource: Int) {
        aiConfig = isTempSource
        SaveSettingUtil.aiTraceType = aiConfig
        imageThread?.typeAi = isTempSource
    }

    private fun showTempRecyclerNight(isObserveMode: Boolean, position: Int) {
        if (isObserveMode) {//
            when (position) {
                1 -> {//AI
                    if (SharedManager.isTipAIRecognition) {
                        val dialog = TipObserveDialog.Builder(this)
                            .setTitle(LibAppR.string.tips_ai)
                            .setMessage(LibAppR.string.tips_ai_content)
                            .setCancelListener { isCheck ->
                                SharedManager.isTipAIRecognition = !isCheck
                            }
                            .create()
                        dialog.show()
                    }
                }
                3 -> {//
                    isOpenTarget = true
                    SaveSettingUtil.isOpenTarget = isOpenTarget
                    binding.thermalRecyclerNight.setTargetSelected(TargetType.MODE, true)
                    binding.thermalRecyclerNight.setTargetSelected(TargetType.STYLE, true)
                    binding.thermalRecyclerNight.setTargetSelected(TargetType.DELETE, false)
                    binding.zoomView.visibility = View.VISIBLE
                    binding.zoomView.updateTargetBitmap(targetMeasureMode, targetStyle, targetColorType,binding.thermalLay)
                    if (!SharedManager.getTargetPop()) {
                        binding.thermalRecyclerNight.setTargetSelected(TargetType.HELP, true)
                        val dialog = TipGuideDialog.newInstance()
                        dialog.closeEvent = {
                            binding.thermalRecyclerNight.setTargetSelected(TargetType.HELP, false)
                            SharedManager.saveTargetPop(it)
                        }
                        dialog.show(supportFragmentManager, "")
                    }
                }
                4 -> {//
                    if (SharedManager.isTipCoordinate) {
                        val dialog = TipObserveDialog.Builder(this)
                            .setTitle(LibAppR.string.coordinate_mode)
                            .setMessage(LibAppR.string.coordinate_tips)
                            .setCancelListener { isCheck ->
                                SharedManager.isTipCoordinate = !isCheck
                            }
                            .create()
                        dialog.show()
                    }
                }
            }
        } else {//
            if (position == 4 && !isOpenPreview) {
                binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
            }
        }

        binding.thermalRecyclerNight.selectPosition(if (isObserveMode) position + 10 else position)
    }

     * 0.
    private var cameraDelaySecond: Int = SaveSettingUtil.delayCaptureSecond
     *  1 - .
     * @param actionCode: 0-/  1-  2-  3-  4
    private fun setCamera(actionCode: Int) {
        when (actionCode) {
            0 -> {// /
                if (isVideo) {
                    centerCamera()
                    return
                }
                if (cameraDelaySecond > 0) {
                    autoJob?.cancel()
                }
                if (binding.timeDownView.isRunning) {
                    binding.timeDownView.cancel()
                    updateDelayView()
                } else {
                    if (binding.timeDownView.downTimeWatcher == null) {
                        binding.timeDownView.setOnTimeDownListener(object : TimeDownView.DownTimeWatcher {
                            override fun onTime(num: Int) {
                                updateDelayView()
                            }

                            override fun onLastTime(num: Int) {
                            }

                            override fun onLastTimeFinish(num: Int) {
                                if(binding.thermalRecyclerNight.isVideoMode){
                                    updateVideoDelayView()
                                }else{
                                    updateDelayView()
                                }
                                centerCamera()
                            }
                        })
                    }
                    binding.timeDownView.downSecond(cameraDelaySecond)
                }
            }
            1 -> {//
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
            2 -> {//
                settingCamera()
            }
            3 -> {//
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = false
            }
            4 -> {//
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = true
            }
        }
    }

    private fun updateVideoDelayView(){
        try {
            if (binding.timeDownView.isRunning) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.thermalRecyclerNight.setToRecord(true)
                }
            }
        } catch (e: Exception) {
            Log.e("", e.message.toString())
        }
    }

     * UI
    private fun updateDelayView() {
        try {
            if (binding.timeDownView.isRunning) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.thermalRecyclerNight.setToRecord(true)
                }
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.thermalRecyclerNight.refreshImg()
                }
            }
        } catch (e: Exception) {
            Log.e("", e.message.toString())
        }
    }

    private fun setTemp(fenceType: FenceType, isSelected: Boolean) {
        binding.temperatureView.isEnabled = true
        when (fenceType) {
            FenceType.POINT -> {//
                binding.temperatureView.visibility = View.VISIBLE
                binding.temperatureView.temperatureRegionMode = REGION_MODE_POINT
                showCross(true)
            }
            FenceType.LINE -> {//
                binding.temperatureView.visibility = View.VISIBLE
                binding.temperatureView.temperatureRegionMode = REGION_MODE_LINE
                showCross(true)
            }
            FenceType.RECT -> {//
                binding.temperatureView.visibility = View.VISIBLE
                binding.temperatureView.temperatureRegionMode = REGION_MODE_RECTANGLE
                showCross(true)
            }
            FenceType.FULL -> {//
                binding.temperatureView.visibility = View.VISIBLE
                binding.temperatureView.isShowFull = isSelected
                showCross(true)
            }
            FenceType.TREND -> {//
                if (SharedManager.isNeedShowTrendTips) {
                    NotTipsSelectDialog(this)
                        .setTipsResId(LibAppR.string.thermal_trend_tips)
                        .setOnConfirmListener {
                            SharedManager.isNeedShowTrendTips = !it
                        }
                        .show()
                }
                binding.temperatureView.visibility = View.VISIBLE
                binding.temperatureView.temperatureRegionMode = REGION_NODE_TREND
                if (!binding.spaceChart.isVisible) {//，
                    binding.spaceChart.isVisible = true
                    binding.clTrendOpen.isVisible = false
                    binding.llTrendClose.isVisible = true
                }
                showCross(true)
            }
            FenceType.DEL -> {//
                hasClickTrendDel = true
                binding.temperatureView.clear()
                binding.temperatureView.visibility = View.INVISIBLE
                binding.temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
                binding.spaceChart.isVisible = false
                binding.clTrendOpen.isVisible = false
                binding.llTrendClose.isVisible = false
                showCross(false)
            }
        }
    }

    private fun showCross(boolean: Boolean) {
        if (binding.cameraView != null) {
            binding.cameraView.setShowCross(boolean)
        }
    }

    open fun setPColor(code: Int) {
        pseudoColorMode = code
        binding.temperatureSeekbar.setPseudocode(pseudoColorMode)
         * set pseudocolor
         * (,)
        imageThread?.pseudocolorMode = pseudoColorMode//
        SaveSettingUtil.pseudoColorMode = pseudoColorMode
        binding.thermalRecyclerNight.setPseudoColor(code)
    }


    private var tempAlarmSetDialog: TempAlarmSetDialog? = null
     * .
    private fun showTempAlarmSetDialog() {
        if (tempAlarmSetDialog == null) {
            tempAlarmSetDialog = TempAlarmSetDialog(this, false)
            tempAlarmSetDialog?.onSaveListener = {
                binding.thermalRecyclerNight.setSettingSelected(SettingType.ALARM, it.isHighOpen || it.isLowOpen)
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
            TwoLightType.P_IN_P -> {//
                cameraPreviewConfig(true)
            }
            TwoLightType.BLEND_EXTENT -> {//
                if (!isOpenPreview && isSelected) {//
                    cameraPreviewConfig(false)
                }
                if (isSelected) {
                    showBlendExtentPopup()
                }
            }
            else -> {//，

            }
        }
    }


    private var defaultIsPortrait = DeviceConfig.IS_PORTRAIT //
    private fun setSetting(type: SettingType, isSelected: Boolean) {
        popupWindow?.dismiss()
        when (type) {
            SettingType.PSEUDO_BAR -> {//
                saveSetBean.isOpenPseudoBar = !saveSetBean.isOpenPseudoBar
                binding.clSeekBar.isVisible = saveSetBean.isOpenPseudoBar
                binding.thermalRecyclerNight.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)
            }
            SettingType.CONTRAST -> {//
                if (!isSelected) {
                    showContrastPopup()
                }
            }
            SettingType.DETAIL -> {//
                if (!isSelected) {
                    showSharpnessPopup()
                }
            }
            SettingType.ALARM -> {//
                showTempAlarmSetDialog()
            }
            SettingType.ROTATE -> {//
                saveSetBean.rotateAngle = if (saveSetBean.rotateAngle == 0) 270 else (saveSetBean.rotateAngle - 90)
                updateRotateAngle(saveSetBean.rotateAngle)
                binding.zoomView.del(true)
            }
            SettingType.FONT -> {//
                val colorPickDialog = ColorPickDialog(this, saveSetBean.tempTextColor, saveSetBean.tempTextSize)
                colorPickDialog.onPickListener = { it: Int, textSize: Int ->
                    saveSetBean.tempTextColor = it
                    saveSetBean.tempTextSize = SizeUtils.sp2px(textSize.toFloat())
                    binding.temperatureView.setTextSize(saveSetBean.tempTextSize)
                    binding.temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
                    binding.thermalRecyclerNight.setSettingSelected(SettingType.FONT, !saveSetBean.isTempTextDefault())
                }
                colorPickDialog.show()
            }
            SettingType.MIRROR -> {//
                saveSetBean.isOpenMirror = !saveSetBean.isOpenMirror
                binding.thermalRecyclerNight.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
                ircmd?.setMirror(saveSetBean.isOpenMirror)
            }

            SettingType.COMPASS -> {//
                saveSetBean.isOpenCompass = !saveSetBean.isOpenCompass
                binding.thermalRecyclerNight.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)
                binding.compassView.isVisible = saveSetBean.isOpenCompass
                if (saveSetBean.isOpenCompass) {
                    startCompass()
                } else {
                    stopCompass()
                }
            }
            SettingType.WATERMARK -> {
                // 2D
            }
        }
    }

    private fun setAiState(it: Int) {
        aiConfig = it
        SaveSettingUtil.aiTraceType = it
        when (it) {
            ObserveBean.TYPE_NONE ->{//
                switchTempSource(ObserveBean.TYPE_NONE)
            }
            ObserveBean.TYPE_DYN_R -> {//
                switchTempSource(ObserveBean.TYPE_DYN_R)
            }

            ObserveBean.TYPE_TMP_H_S -> {//
                switchTempSource(ObserveBean.TYPE_TMP_H_S)
            }

            ObserveBean.TYPE_TMP_L_S -> {//
                switchTempSource(ObserveBean.TYPE_TMP_L_S)
            }
        }
    }

    private fun setTarget(targetType: TargetType) {
        when (targetType) {
            TargetType.MODE -> {//
                if (curTargetStyle == 1 && popupWindow?.isShowing == true) {
                    popupWindow?.dismiss()
                } else {
                    popupWindow?.dismiss()
                    showTargetModePopup()
                }
            }
            TargetType.STYLE -> {//
                if (curTargetStyle == 2 && popupWindow?.isShowing == true) {
                    popupWindow?.dismiss()
                } else {
                    popupWindow?.dismiss()
                    showTargetStylePopup()
                }
            }
            TargetType.COLOR -> {//
                popupWindow?.dismiss()
                showTargetColorDialog()
            }
            TargetType.DELETE -> {//
                popupWindow?.dismiss()
                isOpenTarget = false
                SaveSettingUtil.isOpenTarget = isOpenTarget
                binding.thermalRecyclerNight.setTargetSelected(TargetType.MODE, false)
                binding.thermalRecyclerNight.setTargetSelected(TargetType.STYLE, false)
                binding.thermalRecyclerNight.setTargetSelected(TargetType.COLOR, false)
                binding.thermalRecyclerNight.setTargetSelected(TargetType.DELETE, true)
                binding.zoomView.del(false)
            }
            TargetType.HELP -> {//
                popupWindow?.dismiss()
                showTargetHelpDialog()
            }
        }
    }

    private var targetMeasureMode: Int = SaveSettingUtil.targetMeasureMode
    private var targetStyle: Int = SaveSettingUtil.targetType
    private var targetColorType: Int = SaveSettingUtil.targetColorType

     * PopupWindow.
    private fun showTargetModePopup() {
        binding.zoomView.visibility =View.VISIBLE
        binding.zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,binding.thermalLay)
        binding.thermalRecyclerNight.setTargetSelected(TargetType.MODE, true)
        binding.thermalRecyclerNight.setTargetSelected(TargetType.STYLE, true)
        binding.thermalRecyclerNight.setTargetSelected(TargetType.DELETE, false)
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
            binding.zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,binding.thermalLay)
            binding.thermalRecyclerNight.setTargetMode(item)
        }
        recyclerView?.adapter = measureItemAdapter
        val mode = IntArray(1)
        ircmd?.getPropImageParams(
            CommonParams.PropImageParams.IMAGE_PROP_LEVEL_CONTRAST,
            mode
        )
//        popupWindow?.setOnDismissListener {
//            binding.thermalRecyclerNight.modeStats = 620
//        }
        popupWindow?.showAsDropDown(binding.thermalLay, 0, getPopupWindowY(contentHeight), Gravity.NO_GRAVITY)
        curTargetStyle = 1
    }

     *  PopupWindow
    private fun showTargetStylePopup() {
        binding.zoomView.visibility =View.VISIBLE
        binding.zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,binding.thermalLay)
        binding.thermalRecyclerNight.setTargetSelected(TargetType.MODE, true)
        binding.thermalRecyclerNight.setTargetSelected(TargetType.STYLE, true)
        binding.thermalRecyclerNight.setTargetSelected(TargetType.DELETE, false)
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
            binding.zoomView?.updateSelectBitmap(targetMeasureMode, targetStyle, targetColorType,binding.thermalLay)
        }
        recyclerView?.adapter = targetItemAdapter
//        popupWindow?.setOnDismissListener {
//            binding.thermalRecyclerNight.targetStats = 600
//        }
        popupWindow?.showAsDropDown(
            binding.thermalLay,
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
                binding.thermalRecyclerNight.setTargetSelected(TargetType.MODE, true)
                binding.thermalRecyclerNight.setTargetSelected(TargetType.STYLE, true)
                binding.thermalRecyclerNight.setTargetSelected(TargetType.DELETE, false)
                binding.thermalRecyclerNight.setTargetSelected(TargetType.COLOR, it != ObserveBean.TYPE_TARGET_COLOR_GREEN)
                targetColorType = it
                SaveSettingUtil.targetColorType = targetColorType
                binding.zoomView?.updateTargetBitmap(targetMeasureMode, targetStyle, targetColorType,binding.thermalLay)
            }
            .create().show()
    }

    private fun showTargetHelpDialog(){
        binding.thermalRecyclerNight.setTargetSelected(TargetType.HELP, true)
        val dialog = TipGuideDialog.newInstance()
        dialog.closeEvent = {
            binding.thermalRecyclerNight.setTargetSelected(TargetType.HELP, false)
        }
        dialog.show(supportFragmentManager, "")
    }

    private var cameraAlpha = SaveSettingUtil.twoLightAlpha

    private fun showBlendExtentPopup() {
        val seekBarPopup = SeekBarPopup(this, true)
        seekBarPopup.isRealTimeTrigger = true
        seekBarPopup.progress = cameraAlpha
        seekBarPopup.onValuePickListener = {
            cameraAlpha = it
            SaveSettingUtil.twoLightAlpha = cameraAlpha
            binding.cameraPreview?.setCameraAlpha(cameraAlpha / 100.0f)
        }
        seekBarPopup.setOnDismissListener {
            binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.BLEND_EXTENT, false)
        }
        seekBarPopup.show(binding.thermalLay, !saveSetBean.isRotatePortrait())
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

    private val imageBytes = ByteArray(imageWidth * imageHeight * 2) //
    private val temperatureBytes = ByteArray(imageWidth * imageHeight * 2) //
    protected var imageEditBytes = ByteArray(imageWidth * imageHeight * 4) //
    private val syncimage = SynchronizedBitmap()

    private var temperaturerun = false
    private var tempinfo: Long = 0

    private var isTS001 = false

    @Subscribe(threadMode = ThreadMode.MAIN)
    /**
     * Function description.
     */
    fun irEvent(event: IRMsgEvent) {
        if (event.code == MsgCode.RESTART_USB) {
            isOnRestart = true
            startUSB(isRestart = true,true)
            ToastUtils.showShort("")
        }
    }


    private fun initDataIR() {
        imageWidth = cameraHeight - tempHeight
        imageHeight = cameraWidth
        if (saveSetBean.isRotatePortrait()) {
            bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            binding.temperatureView.setImageSize(imageWidth, imageHeight, this@IRThermalNightActivity)
            defaultIsPortrait = DeviceConfig.S_IS_PORTRAIT
            initRotate = 270
            correctRotate = 270
        } else {
            bitmap = Bitmap.createBitmap(bitmapHeight,bitmapWidth, Bitmap.Config.ARGB_8888)
            binding.temperatureView.setImageSize(imageHeight, imageWidth, this@IRThermalNightActivity)
            defaultIsPortrait = DeviceConfig.IS_PORTRAIT
            initRotate = 270
            correctRotate = 0
        }
        binding.cameraView.setSyncimage(syncimage)
        binding.cameraView.bitmap = bitmap
        binding.temperatureView.setSyncimage(syncimage)
        binding.temperatureView.setTemperature(temperatureBytes)
        binding.thermalRecyclerNight.setTempSource(if(curChooseTabPos == 2) aiConfig else ObserveBean.TYPE_NONE)
        setViewLay(defaultIsPortrait)
        binding.temperatureView.post {
            if (!temperaturerun) {
                temperaturerun = true
                binding.temperatureView.visibility = View.VISIBLE
                if (!isTS001 || SaveSettingUtil.isMeasureTempMode) {
                    binding.temperatureView.postDelayed({
                        binding.temperatureView.temperatureRegionMode = REGION_MODE_CENTER//
                    }, 1000)
                }
            }
        }
        binding.clSeekBar.requestLayout()
        binding.clSeekBar.updateBitmap()
    }

     * @param isPortrait    true:
    private fun setViewLay(isPortrait: Boolean) {
        val params = binding.thermalLay.layoutParams as ConstraintLayout.LayoutParams
        if (isPortrait) {
            params.dimensionRatio = "192:256"
        } else {
            params.dimensionRatio = "256:192"
        }
        runOnUiThread {
            binding.thermalLay.layoutParams = params
        }
        binding.thermalLay.post {
            binding.clSeekBar.requestLayout()
        }
        binding.thermalLay.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (saveSetBean.isRotatePortrait() && binding.thermalLay.measuredHeight > binding.thermalLay.measuredWidth) {
                        val childLayoutParams = binding.temperatureView.layoutParams
                        childLayoutParams.width = binding.thermalLay.measuredWidth
                        childLayoutParams.height = binding.thermalLay.measuredHeight
                        binding.temperatureView.layoutParams = childLayoutParams
                        binding.zoomView.setImageSize(imageHeight, imageWidth, binding.thermalLay.width, binding.thermalLay.height)
                        binding.thermalLay.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else if (!saveSetBean.isRotatePortrait() && binding.thermalLay.measuredHeight < binding.thermalLay.measuredWidth) {
                        val childLayoutParams = binding.temperatureView.layoutParams
                        childLayoutParams.width = binding.thermalLay.measuredWidth
                        childLayoutParams.height = binding.thermalLay.measuredHeight
                        binding.temperatureView.layoutParams = childLayoutParams
                        binding.zoomView.setImageSize(imageWidth, imageHeight, binding.thermalLay.width, binding.thermalLay.height)
                        binding.thermalLay.viewTreeObserver.removeOnGlobalLayoutListener(this);
                    }
                }
            });
    }

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
            imageThread?.setLimit(editMaxValue, editMinValue, upColor, downColor) //
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
            Log.e("", e.message.toString())
        }
    }

    override fun onRestart() {
        super.onRestart()
        isOnRestart = true
    }

     * @param isRestart
    open fun startUSB(isRestart: Boolean,isBadFrames : Boolean) {
        isOnRestart = true
        if (!isBadFrames){
            showCameraLoading()
        }
        iruvc = IRUVCTC(cameraWidth, cameraHeight, this, syncimage,
            defaultDataFlowMode, object : ConnectCallback {
                override fun onCameraOpened(uvcCamera: UVCCamera) {
                    XLog.w("onCameraOpened:${uvcCamera}}")
                }

                override fun onIRCMDCreate(ircmd: IRCMD) {
                    this@IRThermalNightActivity.ircmd = ircmd
                    // IRCMD
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
                binding.zoomView?.updateMagnifier()
            }
        }
        iruvc?.isFirstFrame = true
        iruvc?.setiFirstFrameListener {
            this@IRThermalNightActivity.runOnUiThread {
                //dismissCameraLoading
                //dismissdialog
                if (isOnRestart) {
                    dismissCameraLoading()
                    isOnRestart = false
                }
            }
            // Toast
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


    //TS001
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
                    binding.tvTitleTemp.isVisible = true
                    binding.tvTitleObserve.isVisible = true
                }
//                getUTable()
                val value = IntArray(1)
                ircmd!!.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
                Log.d(TAG, "TPD_PROP_GAIN_SEL=" + value[0])
                gainStatus = if (value[0] == 1) {
                    CommonParams.GainStatus.HIGH_GAIN
                } else {
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


    private fun tempCorrect(
        temp: Float,
        gainStatus: CommonParams.GainStatus, tempInfo: Long
    ): Float {
        if (!isTS001) {
            //ts001
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

     * IR
    protected fun initIRConfig() {
        binding.clSeekBar.isVisible = curChooseTabPos == 1 && saveSetBean.isOpenPseudoBar
        binding.thermalRecyclerNight.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)
        binding.temperatureSeekbar?.setPseudocode(pseudoColorMode)
        if (customPseudoBean.isUseCustomPseudo) {
            updateCustomPseudo()
        } else {
            binding.temperatureIvLock.visibility = View.VISIBLE
            binding.tvTempContent.visibility = View.GONE
            binding.temperatureIvInput.setImageResource(R.drawable.ic_color_edit)
            binding.thermalRecyclerNight.setPseudoColor(pseudoColorMode)
        }
        binding.thermalRecyclerNight.setSettingSelected(SettingType.ALARM, alarmBean.isHighOpen || alarmBean.isLowOpen)
    }


    override fun onStop() {
        irStop()
        super.onStop()
    }

    open fun irStop() {
        try {
            configJob?.cancel()
            binding.timeDownView?.cancel()
            imageThread?.interrupt()
            imageThread?.join()
            syncimage.valid = false
            binding.temperatureView.stop()
            binding.cameraView?.stop()
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
                    binding.thermalRecyclerNight.refreshImg()
                }
            }
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmHelp.getInstance(application).onDestroy(SaveSettingUtil.isSaveSetting)
        binding.tempBg?.stopAnimation()
        binding.timeDownView?.cancel()
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

        // sensor
//        if (Usbcontorl.isload) {
//            Usbcontorl.usb3803_mode_setting(0) //5V
//        }

    }

     * .
    private fun closeCustomPseudo() {
        setCustomPseudoColorList(null, null, true, 0f, 0f)
        binding.temperatureSeekbar.setColorList(null)
        binding.temperatureIvLock.visibility = View.VISIBLE
        binding.thermalRecyclerNight.setPseudoColor(pseudoColorMode)
        binding.tvTempContent.visibility = View.GONE
        binding.temperatureIvInput.setImageResource(R.drawable.ic_color_edit)
    }

     * @param colorList IntArray?
     * @param isUseGray Boolean
     * @param customMaxTemp Float
     * @param customMinTemp Float
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
        binding.temperatureSeekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
        binding.temperatureSeekbar.setPlaces(customPseudoBean.getPlaceList())
        binding.temperatureIvLock.visibility = View.INVISIBLE
        binding.temperatureSeekbar.setRangeAndPro(
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp)
        )
        binding.tvTempContent.visibility = View.VISIBLE
        binding.thermalRecyclerNight.setPseudoColor(-1)
        binding.temperatureIvInput.setImageResource(R.drawable.ir_model)
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

    @SuppressLint("CheckResult")
    private fun centerCamera() {
        storageRequestType = 0
        checkStoragePermission()
    }

    private var showCameraSetting = false
    private val cameraItemBeanList by lazy {
        mutableListOf(
            CameraItemBean(
                "", CameraItemBean.TYPE_DELAY,
                time = SaveSettingUtil.delayCaptureSecond
            ),
            CameraItemBean(
                "", CameraItemBean.TYPE_ZDKM,
                isSel = SaveSettingUtil.isAutoShutter
            ),
            CameraItemBean("", CameraItemBean.TYPE_SDKM),
            CameraItemBean(
                "", CameraItemBean.TYPE_AUDIO,
                isSel = SaveSettingUtil.isRecordAudio &&
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        )
                        == PackageManager.PERMISSION_GRANTED
            ),
            CameraItemBean("", CameraItemBean.TYPE_SETTING),
        )
    }

    private var cameraItemAdapter: CameraItemAdapter? = null

    private var isAutoShutter: Boolean = SaveSettingUtil.isAutoShutter
    private fun settingCamera() {
        showCameraSetting = !showCameraSetting
        if (showCameraSetting) {
            ViewStubUtils.showViewStub(binding.viewStubCamera, true, callback = { view: View? ->
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
                                if (binding.timeDownView.isRunning) {
                                    return@listener
                                }
                                cameraItemAdapter!!.data[position].changeDelayType()
                                cameraItemAdapter!!.notifyItemChanged(position)
                                when (cameraItemAdapter!!.data[position].time) {
                                    CameraItemBean.DELAY_TIME_0 -> {
                                        ToastUtils.showShort(LibAppR.string.off_photography)
                                    }

                                    CameraItemBean.DELAY_TIME_3 -> {
                                        ToastUtils.showShort(LibAppR.string.seconds_dalay_3)
                                    }

                                    CameraItemBean.DELAY_TIME_6 -> {
                                        ToastUtils.showShort(LibAppR.string.seconds_dalay_6)
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
                                if (syncimage.type == 1) {
                                    ircmd?.tc1bShutterManual()
                                } else {
                                    ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
                                }
                                ToastUtils.showShort(LibAppR.string.app_Manual_Shutter)
                                return@listener
                            }

                            CameraItemBean.TYPE_ZDKM -> {
                                isAutoShutter = !isAutoShutter
                                SaveSettingUtil.isAutoShutter = isAutoShutter
                                cameraItemAdapter!!.data[position].isSel = !cameraItemAdapter!!.data[position].isSel
                                cameraItemAdapter!!.notifyItemChanged(position)
                                if (SharedManager.isTipShutter && !isAutoShutter) {
                                    val dialog = TipShutterDialog.Builder(this)
                                        .setMessage(LibAppR.string.shutter_tips)
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
            ViewStubUtils.showViewStub(binding.viewStubCamera, false, null)
        }
    }

    open fun getCameraViewBitmap(): Bitmap {
        if (isOpenAmplify){
            return imageThread?.getBaseBitmap(saveSetBean.rotateAngle)
                ?: binding.cameraView.getScaledBitmap()
        }else{
            return binding.cameraView.getScaledBitmap()
        }
    }

    private fun camera() {
        lifecycleScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) {
                binding.thermalRecyclerNight.setToCamera()
            }
            try {
                synchronized(syncimage.dataLock) {
                    var cameraViewBitmap: Bitmap? =
                        if (isOpenAmplify) OpencvTools.supImageFourExToBitmap(getCameraViewBitmap())
                        else getCameraViewBitmap()
                    if (isOpenPreview) {
                        cameraViewBitmap = BitmapUtils.mergeBitmapByView(cameraViewBitmap, binding.cameraPreview.getBitmap(), binding.cameraPreview)
                        binding.cameraPreview.getBitmap()?.let {
                            ImageUtils.saveImageToApp(it)
                        }
                    }

//                    // bitmap
//                    if ((curChooseTabPos == 1 && binding.temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
//                        (curChooseTabPos == 2 && binding.temperatureView.isUserHighTemp && binding.temperatureView.isUserLowTemp)) {
//                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, binding.temperatureView.regionAndValueBitmap, 0, 0)
//                    }

                    val isShowPseudoBar = binding.clSeekBar.visibility == VISIBLE
                    if (isShowPseudoBar) {
                        val seekBarBitmap = binding.clSeekBar.drawToBitmap()
                        cameraViewBitmap = BitmapUtils.mergeBitmap(
                            cameraViewBitmap,
                            seekBarBitmap,
                            cameraViewBitmap!!.width - seekBarBitmap.width,
                            (cameraViewBitmap.height - seekBarBitmap.height) / 2
                        )
                        seekBarBitmap.recycle()
                    }

                    val compassBitmap: Bitmap? = if (binding.compassView.visibility == VISIBLE) {
                        binding.compassView.drawToBitmap()
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

                    //  2023/11/24
                    /*if (binding.tempBg.isVisible) {
                        if (alphaPaint == null) {
                            alphaPaint = Paint()
                        }
                        alphaPaint?.alpha = (binding.tempBg.animatorAlpha * 255).toInt()
                        cameraViewBitmap = BitmapUtils.mergeBitmapAlpha(cameraViewBitmap, binding.tempBg.drawToBitmap(), alphaPaint, 0, 0)
                    }*/

                    // bitmap
                    if ((curChooseTabPos == 1 && binding.temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
                        (curChooseTabPos == 2 && binding.temperatureView.isUserHighTemp() && binding.temperatureView.isUserLowTemp())) {
                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, binding.temperatureView.regionAndValueBitmap, 0, 0)
                    }
                    if (binding.layCarDetectPrompt.isVisible){
                        cameraViewBitmap = BitmapUtils.mergeBitmap(
                            cameraViewBitmap,
                            binding.layCarDetectPrompt.drawToBitmap(), 0, 0)
                    }
                    val watermarkBean = SharedManager.watermarkBean
                    if (watermarkBean.isOpen) {
                        cameraViewBitmap = BitmapUtils.drawCenterLable(
                            cameraViewBitmap,
                            watermarkBean.title,
                            watermarkBean.address,
                            if (watermarkBean.isAddTime) TimeTool.getNowTime() else "",
                            if (binding.temperatureSeekbar.isVisible){
                                binding.temperatureSeekbar.measuredWidth
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

                    if (curChooseTabPos == 1) {//
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
                        binding.thermalRecyclerNight.refreshImg()
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

    open fun initVideoRecordFFmpeg() {
        videoRecord = VideoRecordFFmpeg(
            binding.cameraView,
            binding.cameraPreview,
            binding.temperatureView,
            curChooseTabPos == 1,
            binding.clSeekBar,
            binding.tempBg,
            binding.compassView, null,
            carView = binding.layCarDetectPrompt
        )
    }

    private fun video() {
        if (!isVideo) {
            initVideoRecordFFmpeg()
            if (!videoRecord!!.canStartVideoRecord(null)) {
                return
            }
            videoRecord?.stopVideoRecordListener = { isShowVideoRecordTips ->
                this@IRThermalNightActivity.runOnUiThread {
                    if (isShowVideoRecordTips) {
                        try {
                            val dialog = TipDialog.Builder(this@IRThermalNightActivity)
                                .setMessage(LibAppR.string.tip_video_record)
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
                        binding.thermalRecyclerNight.refreshImg()
                    }
                }
            }
            binding.clSeekBar.updateBitmap()
            videoRecord?.updateAudioState(isRecordAudio)
            videoRecord?.startRecord()
            isVideo = true
            lifecycleScope.launch(Dispatchers.Main) {
                binding.thermalRecyclerNight.setToRecord(false)
            }
            videoTimeShow()
        } else {
            stopIfVideoing()
        }
    }


     * .
    private fun stopIfVideoing() {
        if (isVideo) {
            isVideo = false
            videoRecord?.stopRecord()
            videoTimeClose()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(500)
                binding.thermalRecyclerNight.refreshImg()
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
                    binding.popTimeText.text = TimeTool.showVideoTime(it * 1000L)
                }
                if (it == time - 1) {
                    video()
                }
            }
        }
        binding.popTimeLay.visibility = View.VISIBLE
    }

    protected fun videoTimeClose() {
        flow?.cancel()
        flow = null
        binding.popTimeLay.visibility = View.GONE
    }

    private var zoomConfig = 1

     *  PopupWindow
    private fun showContrastPopup() {
        binding.thermalRecyclerNight.setSettingSelected(SettingType.CONTRAST, true)

        val seekBarPopup = SeekBarPopup(this)
        seekBarPopup.progress = NumberTools.scale(saveSetBean.contrastValue / 2.56f, 0).toInt()
        seekBarPopup.onValuePickListener = {
            saveSetBean.contrastValue = (it * 2.56f).toInt().coerceAtMost(255)
            ircmd?.setContrast(saveSetBean.contrastValue)
        }
        seekBarPopup.setOnDismissListener {
            binding.thermalRecyclerNight.setSettingSelected(SettingType.CONTRAST, false)
        }
        seekBarPopup.show(binding.thermalLay, !saveSetBean.isRotatePortrait())
        popupWindow = seekBarPopup
    }


    private fun getPopupWindowY(contentHeight: Int): Int {
        if (!saveSetBean.isRotatePortrait()) {
            return 0
        }
        val location = IntArray(2)
        binding.thermalLay.getLocationInWindow(location)
        val menuLocation = IntArray(2)
        binding.thermalRecyclerNight.getLocationInWindow(menuLocation)
        return if (location[1] + binding.thermalLay.measuredHeight > menuLocation[1]) {
            binding.thermalLay.measuredHeight - contentHeight - (location[1] + binding.thermalLay.measuredHeight - menuLocation[1])
        } else {
            binding.thermalLay.measuredHeight - contentHeight
        }
    }


    private var nowZoomLevel = 1

     * ()  PopupWindow
    private fun showSharpnessPopup() {
        binding.thermalRecyclerNight.setSettingSelected(SettingType.DETAIL, true)

        val maxSharpness = 4 // [0, 4]，
        val seekBarPopup = SeekBarPopup(this)
        seekBarPopup.progress = (saveSetBean.ddeConfig * 100f / maxSharpness).toInt()
        seekBarPopup.onValuePickListener = {
            saveSetBean.ddeConfig = (it * maxSharpness / 100f).roundToInt()
            seekBarPopup.progress = (saveSetBean.ddeConfig * 100f / maxSharpness).toInt()
            ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)
        }
        seekBarPopup.setOnDismissListener {
            binding.thermalRecyclerNight.setSettingSelected(SettingType.DETAIL, false)
        }
        seekBarPopup.show(binding.thermalLay, !saveSetBean.isRotatePortrait())
        popupWindow = seekBarPopup
    }

//    //IMAGE_PROP_LEVEL_SNR (0~3) (2)
//    //IMAGE_PROP_LEVEL_TNR (0~3) (2)

     * IMAGE_PROP_MODE_AGC: 2
     * IMAGE_PROP_ONOFF_AGC: 1
    open fun autoConfig() {
        lifecycleScope.launch(Dispatchers.IO) {
            iruvc?.let {
                if (!it.auto_gain_switch) {
                    switchAutoGain(true)
                    gainSelChar = CameraItemBean.TYPE_TMP_ZD
                    withContext(Dispatchers.Main) {
                        ToastTools.showShort(LibAppR.string.auto_open)
                    }
                }
//                else {
//                    switchAutoGain(false)
//                    LibAppR.string.auto_close
//                }

            }
        }
        dismissCameraLoading()
        binding.thermalRecyclerNight.setTempLevel(CameraItemBean.TYPE_TMP_ZD)
    }


    open fun switchAutoGain(boolean: Boolean){
        iruvc?.auto_gain_switch = boolean
    }


    protected var configJob: Job? = null
    private var temperatureMode: Int = SaveSettingUtil.temperatureMode
    private val timeMillis = 150L

    protected fun configParam() {
        configJob = lifecycleScope.launch{
//            showLoading()
            while (isConfigWait && isActive) {
                delay(200)
            }
            delay(500)
            val config = ConfigRepository.readConfig(false)
            val disChar = (config.distance * 128).toInt() //()
            val emsChar = (config.radiation * 128).toInt() //
            XLog.w("TPD_PROP DISTANCE:${disChar}, EMS:${emsChar}}")
            delay(timeMillis)
            /// Emissivity property. unit:1/128, range:1-128(0.01-1)
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_EMS,
                CommonParams.PropTPDParamsValue.NumberType(emsChar.toString())
            )
            delay(timeMillis)
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_DISTANCE,
                CommonParams.PropTPDParamsValue.NumberType(disChar.toString())
            )
            setTemperatureMode(temperatureMode, false)
            delay(timeMillis)
            XLog.w("TPD_PROP DISTANCE:${disChar}, EMS:${emsChar}}")
            if (isFirst && isrun) {
                binding.thermalRecyclerNight.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
                ircmd?.setMirror(saveSetBean.isOpenMirror)
                delay(timeMillis)
                withContext(Dispatchers.IO) {
                    ircmd?.setAutoShutter(true)
                    delay(2500)
                    ircmd?.setAutoShutter(isAutoShutter)
                    isFirst = false
                }
                ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)
                ircmd?.setContrast(saveSetBean.contrastValue)
                if (SaveSettingUtil.isSaveSetting) {
                    XLog.i("：${if (SaveSettingUtil.isMeasureTempMode) "" else ""}")
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
            if (syncimage.type == 1) {
                ircmd?.tc1bShutterManual()
            } else {
                ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
            }
            XLog.w("TPD_PROP DISTANCE2:${disChar}, EMS:${emsChar}}")

        }
    }

    //tdp
    private fun setTpdParams(params: CommonParams.PropTPDParams, value: String) {
        ircmd?.setPropTPDParams(params, CommonParams.PropTPDParamsValue.NumberType(value))
    }

    //img
    private fun setImageParams(params: CommonParams.PropImageParams, value: String) {
        ircmd?.setPropImageParams(params, CommonParams.PropImageParamsValue.NumberType(value))
    }

    private var upValue = -273f
    private var downValue = -273f
    private var upColor = 0
    private var downColor = 0

    private fun addLimit() {
        ThermalInputDialog.Builder(this)
            .setMessage(getString(LibAppR.string.thermal_threshold_setting))
            .setNum(max = upValue, min = downValue)
            .setColor(maxColor = upColor, minColor = downColor)
            .setPositiveListener(LibAppR.string.app_confirm) { up, down, upColor, downColor ->
                this.upValue = up
                this.downValue = down
                this.upColor = upColor
                this.downColor = downColor
                imageThread?.setLimit(upValue, downValue, upColor, downColor) //
            }
            .setCancelListener(getString(LibAppR.string.app_close)) {
                upValue = -273f
                downValue = -273f
                imageThread?.setLimit(upValue, downValue, upColor, downColor) //
            }
            .create().show()
    }

    private var isOpenPreview = false

    private fun cameraPreviewConfig(needShowTip: Boolean) {
        if (!CheckDoubleClick.isFastDoubleClick()) {
            if (isOpenPreview) {
                isOpenPreview = false
                binding.cameraPreview.closeCamera()
                binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                binding.cameraPreview.visibility = View.INVISIBLE
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
    /**
     * Function description.
     */
    fun iruvctc(event: PreviewComplete) {
        dealY16ModePreviewComplete()
    }

    private fun dealY16ModePreviewComplete() {
        iruvc?.setFrameReady(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    /**
     * Function description.
     */
    fun cameraEvent(event: DeviceCameraEvent) {
        when (event.action) {
            100 -> {
                showCameraLoading()
            }

            101 -> {
                lifecycleScope.launch {
                    delay(500)
                    isConfigWait = false
                    delay(1000)
                    dismissCameraLoading()
                }
            }
        }
    }


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
                XLog.i(": $str")
            } catch (e: Exception) {
                XLog.e("SN: ${e.message}")
            }
        }
    }

    override fun tempCorrectByTs(temp: Float): Float {
        var tmp = temp
        try {
            tmp = tempCorrect(temp, gainStatus, tempinfo)
        } catch (e: Exception) {
            XLog.i(": ${e.message}")
        }
        return tmp
    }

    private fun startCompass() {
        compass.start(this::onCompassUpdate)
    }

    private fun stopCompass() {
        compass.stop(this::onCompassUpdate)
    }

    private fun onCompassUpdate(): Boolean {
        val azimuthTxt = formatDegrees(compass.bearing.value, replace360 = true)
        binding.compassView.setCurAzimuth(azimuthTxt.first.toInt())
        return true
    }

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
                    .setMessage(getString(LibAppR.string.permission_request_camera_app, CommUtils.getAppName()))
                    .setCancelListener(LibAppR.string.app_cancel)
                    .setPositiveListener(LibAppR.string.app_confirm) {
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
                            binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, true)
                            binding.cameraPreview.visibility = View.VISIBLE
                            binding.cameraPreview?.setCameraAlpha(cameraAlpha / 100.0f)
                            binding.cameraPreview.post {
                                isOpenPreview = true
                                binding.cameraPreview.openCamera()
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
                            binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                            ToastUtils.showShort(LibAppR.string.scan_ble_tip_authorize)
                        }
                    } catch (e: Exception) {
                        XLog.e("" + e.message)
                    }
                }

                override fun onDenied(
                    permissions: MutableList<String>,
                    doNotAskAgain: Boolean
                ) {
                    if (doNotAskAgain) {
                        if (BaseApplication.instance.isDomestic()){
                            ToastUtils.showShort(getString(LibAppR.string.app_camera_content))
                            return
                        }
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setTitleMessage(getString(LibAppR.string.app_tip))
                            .setMessage(getString(LibAppR.string.app_camera_content))
                            .setPositiveListener(LibAppR.string.app_open) {
                                AppUtils.launchAppDetailsSettings()
                            }
                            .setCancelListener(LibAppR.string.app_cancel) {
                            }
                            .setCanceled(true)
                            .create().show()
                    }
                    binding.thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                }
            })
    }

    private fun checkStoragePermission() {
        if (!XXPermissions.isGranted(this,permissionList)) {
            if (BaseApplication.instance.isDomestic()) {
                TipDialog.Builder(this)
                    .setMessage(getString(LibAppR.string.permission_request_storage_app, CommUtils.getAppName()))
                    .setCancelListener(LibAppR.string.app_cancel)
                    .setPositiveListener(LibAppR.string.app_confirm) {
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
                            isRecordAudio = true
                            SaveSettingUtil.isRecordAudio = isRecordAudio
                            videoRecord?.updateAudioState(true)
                            cameraItemAdapter?.data?.get(audioPosition)?.isSel = !(cameraItemAdapter?.data?.get(audioPosition)?.isSel?:false)
                            cameraItemAdapter?.notifyItemChanged(audioPosition)
                        } else {
                            ToastUtils.showShort(LibAppR.string.scan_ble_tip_authorize)
                        }
                    } catch (e: Exception) {
                        Log.e("", "" + e.message)
                    }
                }

                override fun onDenied(
                    permissions: MutableList<String>,
                    doNotAskAgain: Boolean
                ) {
                    if (doNotAskAgain) {
                        if (BaseApplication.instance.isDomestic()){
                            ToastUtils.showShort(getString(LibAppR.string.app_microphone_content))
                            return
                        }
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setTitleMessage(getString(LibAppR.string.app_tip))
                            .setMessage(getString(LibAppR.string.app_microphone_content))
                            .setPositiveListener(LibAppR.string.app_open) {
                                AppUtils.launchAppDetailsSettings()
                            }
                            .setCancelListener(LibAppR.string.app_cancel) {
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
                        if (!binding.thermalRecyclerNight.isVideoMode) {
                            val setting = SharedManager.continuousBean
                            if (setting.isOpen) {
                                if (!isAutoCamera) {
                                    autoJob = countDownCoroutines(
                                        setting.count,
                                        setting.continuaTime,
                                        this@IRThermalNightActivity.lifecycleScope,
                                        onTick = {
                                            camera()
                                        }, onStart = {
                                            binding.tvTypeInd?.visibility = VISIBLE
                                            isAutoCamera = true
                                        }, onFinish = {
                                            binding.tvTypeInd?.visibility = GONE
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
                            video()
                        }
                    } else {
                        ToastUtils.showShort(LibAppR.string.scan_ble_tip_authorize)
                    }
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        if (BaseApplication.instance.isDomestic()){
                            ToastUtils.showShort(getString(LibAppR.string.app_storage_content))
                            return
                        }
                        TipDialog.Builder(this@IRThermalNightActivity)
                            .setTitleMessage(getString(LibAppR.string.app_tip))
                            .setMessage(LibAppR.string.app_storage_content)
                            .setPositiveListener(LibAppR.string.app_open) {
                                AppUtils.launchAppDetailsSettings()
                            }
                            .setCancelListener(LibAppR.string.app_cancel) {
                            }
                            .setCanceled(true)
                            .create().show()
                    }
                }
            })
    }

    private fun setCarDetectPrompt(){
        var carDetectInfo = SharedManager.getCarDetectInfo()
        var tvDetectPrompt = binding.viewCarDetect.tvDetectPrompt
        if(carDetectInfo == null){
            tvDetectPrompt.text = getString(LibAppR.string.abnormal_item1) + TemperatureUtil.getTempStr(40, 70)
        }else{
            var temperature = carDetectInfo.temperature.split("~")
            tvDetectPrompt.text = carDetectInfo.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
        }
        val test = intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)
        binding.layCarDetectPrompt.visibility = if(intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)) View.VISIBLE else View.GONE
        binding.viewCarDetect.rlContent.setOnClickListener {
            CarDetectDialog(this) {
                var temperature = it.temperature.split("~")
                tvDetectPrompt.text = it.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
            }.show()
        }
    }
}
