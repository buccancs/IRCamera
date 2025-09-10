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
import com.topdon.lib.core.utils.BitmapUtils
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
open class IRThermalNightActivity : BaseIRActivity(), ITsTempListener {

    private fun switchTs001Mode(isToTemp: Boolean) {
        if (isToTemp == isTs001TempMode) {
            return
        }

        tvTitleTemp.isSelected = isToTemp
        tvTitleObserve.isSelected = !isToTemp
        SaveSettingUtil.isMeasureTempMode = isToTemp

        // Activity logicSettingsMenuactivity PopupWindow
        // ViewStub removed - resource not found
        popupWindow?.dismiss()

        showCameraLoading()

        stopIfVideoing() // Activity logicVideo

        // Activity logicPhoto
        isAutoCamera = false
        autoJob?.cancel()

        // Activity logicPhoto
        // if (timeDownView.isRunning) {
        //     timeDownView.cancel()
        //     updateDelayView()
        // }

        // Activity logic
        setDefLimit()
        updateTemperatureSeekBar(false) // Activity logic

        if (isToTemp) { // Observation->Temperature measurement
            // Activity logicVisible light
            if (SaveSettingUtil.isOpenTwoLight && XXPermissions.isGranted(this, Permission.CAMERA)) {
                cameraPreviewConfig(false)
            }

            // Activity logic AI activity(Dynamic recognition、High temperatureactivity、Low temperatureactivity)
            aiConfig = ObserveBean.TYPE_NONE
            imageThread?.typeAi = aiConfig
            thermalRecyclerNight // setTempSource - synthetic method removed

            // Activity logicTarget
            // thermalRecyclerNight.setTargetSelected - synthetic method removed
            // thermalRecyclerNight.setTargetSelected - synthetic method removed
            // thermalRecyclerNight.setTargetSelected - synthetic method removed
            // thermalRecyclerNight.setTargetSelected - synthetic method removed
            // thermalRecyclerNight.setTargetMode - synthetic method removed
            targetMeasureMode = ObserveBean.TYPE_MEASURE_PERSON
            targetStyle = ObserveBean.TYPE_TARGET_HORIZONTAL
            targetColorType = ObserveBean.TYPE_TARGET_COLOR_GREEN
            // zoomView.hideView - synthetic method removed

            // Activity logic
            saveSetBean.isOpenCompass = false
            // thermalRecyclerNight.setSettingSelected - synthetic method removed
            compassView?.visibility = View.GONE
            zoomView?.visibility = View.GONE
            stopCompass()
            zoomView // .del() - synthetic method removed

            // Activity logicPseudo-coloractivityState
            saveSetBean.isOpenPseudoBar = SaveSettingUtil.isOpenPseudoBar
            cl_seek_bar.isVisible = saveSetBean.isOpenPseudoBar
            // thermalRecyclerNight.setSettingSelected - synthetic method removed
            temperatureSeekbar?.setPseudocode(pseudoColorMode)

            // ClearHigh temperatureactivity、Low temperatureactivity
            temperatureView.clear()
            temperatureView.isUserHighTemp = false
            temperatureView.isUserLowTemp = false
            temperatureView.isVisible = true
            temperatureView.temperatureRegionMode = REGION_MODE_CENTER
            showCross(false)
            // thermalRecyclerNight.clearTempPointSelect() - synthetic method removed
            // thermalRecyclerNight.fenceSelectType - synthetic property removed

            // Activity logic
            alarmBean = SaveSettingUtil.alarmBean
            imageThread?.alarmBean = alarmBean
            if (alarmBean.isHighOpen || alarmBean.isLowOpen) {
                // thermalRecyclerNight.setSettingSelected - synthetic method removed
                AlarmHelp.getInstance(this).updateData(alarmBean)
            } else {
                // thermalRecyclerNight.setSettingSelected - synthetic method removed
                AlarmHelp.getInstance(this).updateData(null, null, null)
            }
        } else { // Temperature measurement->Observation
            // Activity logicPicture in picture
            if (isOpenPreview) {
                isOpenPreview = false
                cameraPreview.closeCamera()
                thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                cameraPreview.visibility = View.INVISIBLE
            }

            // Clearactivity、activity、activity、activity、activity
            temperatureView.clear()
            temperatureView.visibility = View.INVISIBLE
            temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
            hasClickTrendDel = true
            spaceChart.isVisible = false
            clTrendOpen.isVisible = false
            llTrendClose.isVisible = false
            showCross(false)

            // SwitchactivityLow temperatureMode
            switchTempGain(isLow = true, false)

            // SwitchactivityDynamic recognition
            aiConfig = SaveSettingUtil.aiTraceType
            imageThread?.typeAi = aiConfig
            thermalRecyclerNight // setTempSource - synthetic method removed

            // Activity logic
            saveSetBean.isOpenCompass = SaveSettingUtil.isOpenCompass
            // thermalRecyclerNight.setSettingSelected - synthetic method removed

            // Activity logicLow temperatureactivity
            if (SaveSettingUtil.isOpenHighPoint || SaveSettingUtil.isOpenLowPoint) {
                temperatureView.temperatureRegionMode = REGION_MODE_RESET
                temperatureView.visibility = View.VISIBLE
            }
            temperatureView.isUserHighTemp = SaveSettingUtil.isOpenHighPoint
            temperatureView.isUserLowTemp = SaveSettingUtil.isOpenLowPoint
            thermalRecyclerNight.setTempPointSelect(TempPointType.HIGH, SaveSettingUtil.isOpenHighPoint)
            thermalRecyclerNight.setTempPointSelect(TempPointType.LOW, SaveSettingUtil.isOpenLowPoint)

            // Targetactivity
            targetMeasureMode = SaveSettingUtil.targetMeasureMode
            targetStyle = SaveSettingUtil.targetType
            targetColorType = SaveSettingUtil.targetColorType
            thermalRecyclerNight.setTargetMode(targetMeasureMode)
            // thermalRecyclerNight.setTargetSelected - synthetic method removed

            // Activity logicPseudo-coloractivity
            cl_seek_bar.visibility = View.GONE

            // Activity logicObservationModeactivity
            if (SharedManager.isTipObservePhoto) {
                TipObserveDialog.Builder(this)
                    .setTitle(R.string.app_tip)
                    .setMessage(R.string.tips_observe_photo_content)
                    .setCancelListener { isCheck ->
                        SharedManager.isTipObservePhoto = !isCheck
                    }
                    .create().show()
            }

            // Activity logic
            alarmBean = AlarmBean()
            imageThread?.alarmBean = alarmBean
            AlarmHelp.getInstance(this).updateData(null, null, null)
        }

        // Activity logic
        customPseudoBean.isUseCustomPseudo = false
        customPseudoBean.saveToShared()
        updateImageAndSeekbarColorList(customPseudoBean)

        if (!SaveSettingUtil.isSaveSetting) {
            // Activity logicPseudo-color
            setPColor(3)

            // Activity logicPhoto
            cameraDelaySecond = DELAY_TIME_0
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[0].time = DELAY_TIME_0
                cameraItemAdapter!!.notifyItemChanged(0)
            }

            // Activity logic
            isRecordAudio = false
            videoRecord?.updateAudioState(false)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[3].isSel = false
                cameraItemAdapter!!.notifyItemChanged(3)
            }

            // Activity logic
            isAutoShutter = true
            ircmd?.setAutoShutter(isAutoShutter)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[1].isSel = true
                cameraItemAdapter!!.notifyItemChanged(1)
            }

            // Activity logicPhotoactivityModeactivityPhoto
            SaveSettingUtil.isVideoMode = false
            thermalRecyclerNight.switchToCamera()

            // Activity logic
            saveSetBean.contrastValue = 128
            ircmd?.setContrast(saveSetBean.contrastValue)

            // Activity logic（activity）
            saveSetBean.ddeConfig = 2
            ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)

            // Activity logicRotateAngle
            saveSetBean.rotateAngle = DeviceConfig.S_ROTATE_ANGLE
            updateRotateAngle(saveSetBean.rotateAngle)

            // Activity logic
            saveSetBean.tempTextColor = 0xffffffff.toInt()
            saveSetBean.tempTextSize = SizeUtils.sp2px(14f)
            temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
            temperatureView.setTextSize(saveSetBean.tempTextSize)

            // Activity logicTarget-activity
            zoomConfig = 1

            // Activity logic
            saveSetBean.isOpenMirror = false
            // thermalRecyclerNight.setSettingSelected - synthetic method removed
            ircmd?.setMirror(saveSetBean.isOpenMirror)
        }

        curChooseTabPos = if (isToTemp) Constants.IR_TEMPERATURE_MODE else Constants.IR_OBSERVE_MODE
        isTs001TempMode = isToTemp
        thermalRecyclerNight.selectPosition(if (isToTemp) 0 else 10)
        viewMenuFirst.isObserveMode = !isToTemp

        // Activity logicStateactivity
        updateCompass()

    private fun switchTempGain(
        isLow: Boolean,
        isShowLoading: Boolean,
    ) {
        if ((gainSelChar == 1 && isLow) || (gainSelChar == 0 && !isLow)) { // Activity logicMode
            return
        }
        isTempShowDialog = true
        thermalRecyclerNight.setTempLevel(if (isLow) 1 else 0)
        if (isShowLoading) {
            showCameraLoading()
        }
        switchAutoGain(false)
        lifecycleScope.launch(Dispatchers.IO) {
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL,
                if (isLow) CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH else CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_LOW,
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

    private fun setCamera(actionCode: Int) {
        when (actionCode) {
            0 -> { // Photo/Video
                if (isVideo) {
                    centerCamera()
                    return
                }
                if (cameraDelaySecond > 0) {
                    autoJob?.cancel()
                }
                // if (timeDownView.isRunning) {
                //     timeDownView.cancel()
                //     updateDelayView()
                // } else {
                //     if (timeDownView.downTimeWatcher == null) {
                //         timeDownView.setOnTimeDownListener(object : TimeDownView.DownTimeWatcher {
                //             override fun onTime(num: Int) {
                //                 updateDelayView()
                //             }
                //
                //             override fun onLastTime(num: Int) {
                //             }
                //
                //             override fun onLastTimeFinish(num: Int) {
                //                 if(thermalRecyclerNight.isVideoMode){
                //                     updateVideoDelayView()
                //                 }else{
                //                     updateDelayView()
                //                 }
                //                 centerCamera()
                //             }
                //         })
                //     }
                //     timeDownView.downSecond(cameraDelaySecond)
                // }
            }
            1 -> { // Gallery
                lifecycleScope.launch {
                    if (isVideo) {
                        videoRecord?.stopRecord()
                        isVideo = false
                        videoTimeClose()
                        delay(500)
                    }
                    NavigationManager.getInstance()
                        .build(RouterConfig.IR_GALLERY_HOME)
                        .withInt(ExtraKeyConfig.DIR_TYPE, GalleryRepository.DirType.LINE.ordinal)
                        .navigation(this@IRThermalNightActivity)
                }
            }
            2 -> { // Activity logicMenu
                settingCamera()
            }
            3 -> { // SwitchactivityPhoto
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = false
            }
            4 -> { // SwitchactivityVideo
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = true
            }
        }
    }

    private fun updateVideoDelayView() {
        try {
            // if (timeDownView.isRunning) {
            //     lifecycleScope.launch(Dispatchers.Main) {
            //         thermalRecyclerNight.setToRecord(true)
            //     }
            // }
            lifecycleScope.launch(Dispatchers.Main) {
                thermalRecyclerNight.setToRecord(true)
            }
        } catch (e: Exception) {
            Log.e("updateVideoDelayView", e.message ?: "Unknown error")
        }
    }

    private fun setViewLay(isPortrait: Boolean) {
        val params = thermalLay.layoutParams as ConstraintLayout.LayoutParams
        if (isPortrait) {
            params.dimensionRatio = "192:256"
        } else {
            params.dimensionRatio = "256:192"
        }
        runOnUiThread {
            thermalLay.layoutParams = params
        }
        thermalLay.post {
            cl_seek_bar.requestLayout()
        }
        thermalLay.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (saveSetBean.isRotatePortrait() && thermalLay.measuredHeight > thermalLay.measuredWidth) {
                        val childLayoutParams = temperatureView.layoutParams
                        childLayoutParams.width = thermalLay.measuredWidth
                        childLayoutParams.height = thermalLay.measuredHeight
                        temperatureView.layoutParams = childLayoutParams
                        zoomView.setImageSize(imageHeight, imageWidth, thermalLay.width, thermalLay.height)
                        thermalLay.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else if (!saveSetBean.isRotatePortrait() && thermalLay.measuredHeight < thermalLay.measuredWidth) {
                        val childLayoutParams = temperatureView.layoutParams
                        childLayoutParams.width = thermalLay.measuredWidth
                        childLayoutParams.height = thermalLay.measuredHeight
                        temperatureView.layoutParams = childLayoutParams
                        zoomView.setImageSize(imageWidth, imageHeight, thermalLay.width, thermalLay.height)
                        thermalLay.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            },
        )
    }

    open fun startUSB(
        isRestart: Boolean,
        isBadFrames: Boolean,
    ) {
        isOnRestart = true
        if (!isBadFrames) {
            showCameraLoading()
        }
        iruvc =
            IRUVCTC(
                cameraWidth,
                cameraHeight,
                this,
                syncimage,
                defaultDataFlowMode,
                object : ConnectCallback {
                    override fun onCameraOpened(uvcCamera: UVCCamera) {
                        XLog.w("SettingsonCameraOpened:$uvcCamera}")
                    }

                    override fun onIRCMDCreate(ircmd: IRCMD) {
                        this@IRThermalNightActivity.ircmd = ircmd

                        isConfigWait = false
                    }
                },
                object : USBMonitorCallback {
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
                },
            )
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
            // Activity logic
            this@IRThermalNightActivity.runOnUiThread {
                // Activity logic，activityLow temperatureSwitch，activitydismissCameraLoading
                // Activity logic，activitydismiss，activitydialogactivity
                if (isOnRestart) {
                    dismissCameraLoading()
                    isOnRestart = false
                }
            }
            // Activity operationToastactivity
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

    // SettingsTS001activity
    private fun setTsBin() {
        ircmd?.let {
            val getSnBytes = ByteArray(16)
            val fwBuildVersionInfoBytes = ByteArray(50)
            ircmd?.getDeviceInfo(
                CommonParams.DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
                fwBuildVersionInfoBytes,
            ) // ok
            val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
            it.getDeviceInfo(CommonParams.DeviceInfoType.DEV_INFO_GET_SN, getSnBytes) // ok
            val snStr = String(getSnBytes) // sn
            val infoBuilder = StringBuilder()
            infoBuilder.append("Firmware version: ").append(arm).append("<br>")
            infoBuilder.append("SN: ").append(snStr).append("<br>")
            val str =
                HtmlCompat.fromHtml(infoBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (str.contains("Mini256", true)) {
                lifecycleScope.launch(Dispatchers.Main) {
                    tvTitleTemp.isVisible = true
                    tvTitleObserve.isVisible = true
                }
                // Activity operationLow gainactivity
//                getUTable()
                val value = IntArray(1)
                ircmd!!.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
                Log.d(TAG, "TPD_PROP_GAIN_SEL=" + value[0])
                gainStatus =
                    if (value[0] == 1) {
                        // CurrentCoreactivityHigh gain
                        CommonParams.GainStatus.HIGH_GAIN
                        // Activity operation
                    } else {
                        // CurrentCoreactivityLow gain
                        CommonParams.GainStatus.LOW_GAIN
                    }
                if (nuc_table_low == null) {
                    return@let
                }
                if (ts_data_H == null) {
                    ts_data_H = CommonUtils.getTauData(this@IRThermalNightActivity, "ts/TS001_H.bin")
                }
                if (ts_data_L == null) {
                    ts_data_L = CommonUtils.getTauData(this@IRThermalNightActivity, "ts/TS001_L.bin")
                }
                isTS001 = true
            } else {
                isTS001 = false
            }
            if (!DeviceTools.isTC001PlusConnect()) {
                initAmplify(true)
            } else {
//                isOpenAmplify = false
//                initAmplify(false)
            }
        }
    }

    open fun setCustomPseudoColorList(
        colorList: IntArray?,
        places: FloatArray?,
        isUseGray: Boolean,
        customMaxTemp: Float,
        customMinTemp: Float,
    ) {
        imageThread?.setColorList(colorList, places, isUseGray, customMaxTemp, customMinTemp)
    }

    private fun updateCustomPseudo() {
        temperatureSeekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
        temperatureSeekbar.setPlaces(customPseudoBean.getPlaceList())
        temperatureIvLock.visibility = View.INVISIBLE
        temperatureSeekbar.setRangeAndPro(
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
        )
        tvTempContent.visibility = View.VISIBLE
        thermalRecyclerNight.setPseudoColor(-1)
        temperatureIvInput.setImageResource(R.drawable.ir_model)
    }

    private val permissionList by lazy {
        if (this.applicationInfo.targetSdkVersion >= 34) {
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

    // Photoactivity
    @SuppressLint("CheckResult")
    private fun centerCamera() {
        storageRequestType = 0
        checkStoragePermission()
    }

    private var showCameraSetting = false
    private val cameraItemBeanList by lazy {
        mutableListOf(
            CameraItemBean(
                "activity",
                CameraItemBean.TYPE_DELAY,
                time = SaveSettingUtil.delayCaptureSecond,
            ),
            CameraItemBean(
                "activity",
                CameraItemBean.TYPE_ZDKM,
                isSel = SaveSettingUtil.isAutoShutter,
            ),
            CameraItemBean("activity", CameraItemBean.TYPE_SDKM),
            CameraItemBean(
                "activity",
                CameraItemBean.TYPE_AUDIO,
                isSel =
                    SaveSettingUtil.isRecordAudio &&
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO,
                        )
                        == PackageManager.PERMISSION_GRANTED,
            ),
            CameraItemBean("Settings", CameraItemBean.TYPE_SETTING),
        )
    }

    private var cameraItemAdapter: CameraItemAdapter? = null

    private var isAutoShutter: Boolean = SaveSettingUtil.isAutoShutter

    // Photoactivity
    private fun settingCamera() {
        showCameraSetting = !showCameraSetting
        if (showCameraSetting) {
            // ViewStubCamera layout resource doesn't exist - using recyclerView directly
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
                        // Activity logic
                        if (syncimage.type == 1) {
                            ircmd?.tc1bShutterManual()
                        } else {
                            ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
                        }
                        ToastUtils.showShort(R.string.app_Manual_Shutter)
                        return@listener
                    }

                    CameraItemBean.TYPE_ZDKM -> {
                        // Activity logic
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
        if (isOpenAmplify) {
            // Activity logic，activity
            return imageThread?.getBaseBitmap(saveSetBean.rotateAngle)
                ?: cameraView.getScaledBitmap()
        } else {
            return cameraView.getScaledBitmap()
        }
    }

    // Photo
    private fun camera() {
        lifecycleScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) {
                thermalRecyclerNight.setToCamera()
            }
            try {
                synchronized(syncimage.dataLock) {
                    // Activity operation
                    var cameraViewBitmap: Bitmap? =
                        if (isOpenAmplify) {
                            OpencvTools.supImageFourExToBitmap(getCameraViewBitmap())
                        } else {
                            getCameraViewBitmap()
                        }
                    // Visible light
                    if (isOpenPreview) {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmapByView(
                                cameraViewBitmap,
                                cameraPreview.getBitmap(),
                                cameraPreview,
                            )
                        // Picture in pictureactivity
                        cameraPreview.getBitmap()?.let {
                            ImageUtils.saveImageToApp(it)
                        }
                    }

//                    // Activity operation，activity，activity，activitybitmap
//                    if ((curChooseTabPos == 1 && temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
//                        (curChooseTabPos == 2 && temperatureView.isUserHighTemp && temperatureView.isUserLowTemp)) {
//                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, temperatureView.regionAndValueBitmap, 0, 0)
//                    }

                    // Activity operationPseudo-coloractivity
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

                    // Activity logic
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

                    // Activity operation 2023/11/24 activityPhotoactivity
                    /*if (temp_bg.isVisible) {
                        if (alphaPaint == null) {
                            alphaPaint = Paint()
                        }
                        alphaPaint?.alpha = (temp_bg.animatorAlpha * 255).toInt()
                        cameraViewBitmap = BitmapUtils.mergeBitmapAlpha(cameraViewBitmap, temp_bg.drawToBitmap(), alphaPaint, 0, 0)
                    }*/

                    // Activity operation，activity，activity，activitybitmap
                    if ((curChooseTabPos == 1 && temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
                        (curChooseTabPos == 2 && temperatureView.isUserHighTemp() && temperatureView.isUserLowTemp())
                    ) {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmap(
                                cameraViewBitmap,
                                temperatureView.regionAndValueBitmap,
                                0,
                                0,
                            )
                    }
                    // Activity logic
                    if (layCarDetectPrompt.isVisible) {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmap(
                                cameraViewBitmap,
                                layCarDetectPrompt.drawToBitmap(),
                                0,
                                0,
                            )
                    }
                    // Activity logic
                    val watermarkBean = SharedManager.watermarkBean
                    if (watermarkBean.isOpen) {
                        cameraViewBitmap =
                            BitmapUtils.drawCenterLable(
                                cameraViewBitmap,
                                watermarkBean.title,
                                watermarkBean.address,
                                if (watermarkBean.isAddTime) TimeTool.getNowTime() else "",
                                if (temperatureSeekbar.isVisible) {
                                    temperatureSeekbar.measuredWidth
                                } else {
                                    0
                                },
                            )
                    }

                    var name = ""
                    cameraViewBitmap?.let {
                        name = ImageUtils.save(bitmap = it)
                    }
                    val value = IntArray(1)
                    ircmd?.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)

                    if (curChooseTabPos == 1) { // Temperature measurementModeactivity
                        val capital =
                            FrameStruct.toCode(
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
                                isOpenAmplify,
                            )
                        ImageUtils.saveFrame(bs = imageEditBytes, capital = capital, name = name)
                    }

                    launch(Dispatchers.Main) {
                        thermalRecyclerNight.refreshImg()
                    }
                    EventBus.getDefault().post(GalleryAddEvent())
                }
            } catch (e: Exception) {
                XLog.e(e.message)
            }
        }
    }

    open fun getProductName(): String {
        return if (isTS001) {
            PRODUCT_NAME_TS
        } else {
            PRODUCT_NAME_TC
        }
    }

    protected var isVideo = false

    protected var videoRecord: VideoRecordFFmpeg? = null

    /**
     * activity
     */
    open fun initVideoRecordFFmpeg() {
        videoRecord =
            VideoRecordFFmpeg(
                cameraView,
                cameraPreview,
                temperatureView,
                curChooseTabPos == 1,
                cl_seek_bar,
                temp_bg,
                compassView,
                null,
                carView = layCarDetectPrompt,
            )
    }

    private fun video() {
        if (!isVideo) {
            // Activity logic
            initVideoRecordFFmpeg()
            if (!videoRecord!!.canStartVideoRecord(null)) {
                return
            }
            videoRecord?.stopVideoRecordListener = { isShowVideoRecordTips ->
                this@IRThermalNightActivity.runOnUiThread {
                    if (isShowVideoRecordTips) {
                        try {
                            val dialog =
                                TipDialog.Builder(this@IRThermalNightActivity)
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
                        thermalRecyclerNight.refreshImg()
                    }
                }
            }
            cl_seek_bar // updateBitmap() removed - synthetic method
            videoRecord?.updateAudioState(isRecordAudio)
            videoRecord?.startRecord()
            isVideo = true
            lifecycleScope.launch(Dispatchers.Main) {
                thermalRecyclerNight.setToRecord(false)
            }
            videoTimeShow()
        } else {
            stopIfVideoing()
        }
    }

    /**
     * activityVideo，activityVideo.
     */
    private fun stopIfVideoing() {
        if (isVideo) {
            isVideo = false
            videoRecord?.stopRecord()
            videoTimeClose()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(500)
                thermalRecyclerNight.refreshImg()
                EventBus.getDefault().post(GalleryAddEvent())
            }
        }
    }

    private var flow: Job? = null

    private fun videoTimeShow() {
        flow =
            lifecycleScope.launch {
                val time = 60 * 60 * 4
                flow {
                    repeat(time) {
                        emit(it)
                        delay(1000)
                    }
                }.collect {
                    launch(Dispatchers.Main) {
                        popTimeText.text = TimeTool.showVideoTime(it * 1000L)
                    }
                    if (it == time - 1) {
                        // Activity logic
                        video()
                    }
                }
            }
        popTimeLay.visibility = View.VISIBLE
    }

    protected fun videoTimeClose() {
        flow?.cancel()
        flow = null
        popTimeLay.visibility = View.GONE
    }

    private var zoomConfig = 1

    /**
     * activitySettings PopupWindow
     */
    private fun showContrastPopup() {
        // thermalRecyclerNight.setSettingSelected - synthetic method removed

        val seekBarPopup = SeekBarPopup(this)
        seekBarPopup.progress = NumberTools.scale(saveSetBean.contrastValue / 2.56f, 0).toInt()
        seekBarPopup.onValuePickListener = {
            saveSetBean.contrastValue = (it * 2.56f).toInt().coerceAtMost(255)
            ircmd?.setContrast(saveSetBean.contrastValue)
        }
        seekBarPopup.setOnDismissListener {
            // thermalRecyclerNight.setSettingSelected - synthetic method removed
        }
        seekBarPopup.show(thermalLay, !saveSetBean.isRotatePortrait())
        popupWindow = seekBarPopup
    }

    private fun getPopupWindowY(contentHeight: Int): Int {
        if (!saveSetBean.isRotatePortrait()) {
            return 0
        }
        val location = IntArray(2)
        thermalLay.getLocationInWindow(location)
        val menuLocation = IntArray(2)
        thermalRecyclerNight.getLocationInWindow(menuLocation)
        return if (location[1] + thermalLay.measuredHeight > menuLocation[1]) {
            thermalLay.measuredHeight - contentHeight - (location[1] + thermalLay.measuredHeight - menuLocation[1])
        } else {
            thermalLay.measuredHeight - contentHeight
        }
    }

    private var nowZoomLevel = 1

    /**
     * activity(activity) Settings PopupWindow
     */
    private fun showSharpnessPopup() {
        // thermalRecyclerNight.setSettingSelected - synthetic method removed

        val maxSharpness = 4 // Activity logic [0, 4]，activity
        val seekBarPopup = SeekBarPopup(this)
        seekBarPopup.progress = (saveSetBean.ddeConfig * 100f / maxSharpness).toInt()
        seekBarPopup.onValuePickListener = {
            saveSetBean.ddeConfig = (it * maxSharpness / 100f).roundToInt()
            seekBarPopup.progress = (saveSetBean.ddeConfig * 100f / maxSharpness).toInt()
            ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)
        }
        seekBarPopup.setOnDismissListener {
            // thermalRecyclerNight.setSettingSelected - synthetic method removed
        }
        seekBarPopup.show(thermalLay, !saveSetBean.isRotatePortrait())
        popupWindow = seekBarPopup
    }

//    //IMAGE_PROP_LEVEL_SNR (0~3) activity(activity2) activity
//    //IMAGE_PROP_LEVEL_TNR (0~3) activity(activity2) activity

    /**
     * activity
     * IMAGE_PROP_MODE_AGC: activity2
     * IMAGE_PROP_ONOFF_AGC: activity1
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
        thermalRecyclerNight.setTempLevel(CameraItemBean.TYPE_TMP_ZD)
    }

    open fun switchAutoGain(boolean: Boolean) {
        iruvc?.auto_gain_switch = boolean
    }

    protected var configJob: Job? = null
    private var temperatureMode: Int = SaveSettingUtil.temperatureMode
    private val timeMillis = 150L

    // Activity logic
    protected fun configParam() {
        configJob =
            lifecycleScope.launch {
//            showLoading()
                while (isConfigWait && isActive) {
                    delay(200)
                }
                delay(500)
                val config = ConfigRepository.readConfig(false)
                val disChar = (config.distance * 128).toInt() // Activity logic(activity)
                val emsChar = (config.radiation * 128).toInt() // Activity logic
                XLog.w("SettingsTPD_PROP DISTANCE:$disChar, EMS:$emsChar}")
                delay(timeMillis)
                // Activity logic
                // / Emissivity property. unit:1/128, range:1-128(0.01-1)
                ircmd?.setPropTPDParams(
                    CommonParams.PropTPDParams.TPD_PROP_EMS,
                    CommonParams.PropTPDParamsValue.NumberType(emsChar.toString()),
                )
                delay(timeMillis)
                // Activity logic
                ircmd?.setPropTPDParams(
                    CommonParams.PropTPDParams.TPD_PROP_DISTANCE,
                    CommonParams.PropTPDParamsValue.NumberType(disChar.toString()),
                )
//            SettingsHigh temperature、Low temperature
                setTemperatureMode(temperatureMode, false)
                // Activity operation
                delay(timeMillis)
                XLog.w("SettingsTPD_PROP DISTANCE:$disChar, EMS:$emsChar}")
                if (isFirst && isrun) {
                    // Activity logic
                    // thermalRecyclerNight.setSettingSelected - synthetic method removed
                    ircmd?.setMirror(saveSetBean.isOpenMirror)
                    // Activity operation
                    delay(timeMillis)
                    withContext(Dispatchers.IO) {
                        // Activity operation，activity
                        ircmd?.setAutoShutter(true)
                        delay(2500)
                        ircmd?.setAutoShutter(isAutoShutter)
                        isFirst = false
                    }
                    // Settingsactivity（activity）
                    ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)
                    // Activity logic
                    ircmd?.setContrast(saveSetBean.contrastValue)
                    if (SaveSettingUtil.isSaveSetting) {
                        XLog.i(
                            "activityModeactivity：${if (SaveSettingUtil.isMeasureTempMode) "Temperature measurement" else "Observation"}Mode",
                        )
                        if (isTS001) {
                            switchTs001Mode(SaveSettingUtil.isMeasureTempMode)
                        } else {
                            switchTs001Mode(true)
                        }
                    }
                }
                ircmd?.setPropImageParams(
                    CommonParams.PropImageParams.IMAGE_PROP_ONOFF_AGC,
                    CommonParams.PropImageParamsValue.StatusSwith.ON,
                )
                printSN()
                // Activity logic
                if (syncimage.type == 1) {
                    ircmd?.tc1bShutterManual()
                } else {
                    ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
                }
                XLog.w("SettingsTPD_PROP DISTANCE2:$disChar, EMS:$emsChar}")
            }
    }

    // Settingstdpactivity
    private fun setTpdParams(
        params: CommonParams.PropTPDParams,
        value: String,
    ) {
        ircmd?.setPropTPDParams(params, CommonParams.PropTPDParamsValue.NumberType(value))
    }

    // Settingsimgactivity
    private fun setImageParams(
        params: CommonParams.PropImageParams,
        value: String,
    ) {
        ircmd?.setPropImageParams(params, CommonParams.PropImageParamsValue.NumberType(value))
    }

    private var upValue = -273f
    private var downValue = -273f
    private var upColor = 0
    private var downColor = 0

    // Activity logic
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
                imageThread?.setLimit(upValue, downValue, upColor, downColor) // Activity logic
            }
            .setCancelListener(getString(R.string.app_close)) {
                upValue = -273f
                downValue = -273f
                imageThread?.setLimit(upValue, downValue, upColor, downColor) // Activity logic
            }
            .create().show()
    }

    private var isOpenPreview = false

    /**
     * Switch Picture in picture activity State
     */
    private fun cameraPreviewConfig(needShowTip: Boolean) {
        if (!CheckDoubleClick.isFastDoubleClick()) {
            if (isOpenPreview) {
                // Activity logic
                isOpenPreview = false
                cameraPreview.closeCamera()
                thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
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
                // Activity logic
                showCameraLoading()
            }

            101 -> {
                // Activity logic
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
     * activity
     */
    private fun printSN() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fwBuildVersionInfoBytes = ByteArray(50)
                val getSnBytes = ByteArray(16)
                ircmd?.getDeviceInfo(
                    CommonParams.DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
                    fwBuildVersionInfoBytes,
                ) // ok
                ircmd?.getDeviceInfo(CommonParams.DeviceInfoType.DEV_INFO_GET_SN, getSnBytes) // ok
                val snStr = String(getSnBytes) // sn
                val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
                SharedManager.setDeviceSn(snStr)
                SharedManager.setDeviceVersion(arm)
                val infoBuilder = StringBuilder()
                infoBuilder.append("Firmware version: ").append(arm).append("<br>")
                infoBuilder.append("SN: ").append(snStr).append("<br>")
                val str =
                    HtmlCompat.fromHtml(
                        infoBuilder.toString(),
                        HtmlCompat.FROM_HTML_MODE_LEGACY,
                    )
                XLog.i("activity: $str")
            } catch (e: Exception) {
                XLog.e("activitySNactivity: ${e.message}")
            }
        }
    }

    override fun tempCorrectByTs(temp: Float): Float {
        var tmp = temp
        try {
            tmp = tempCorrect(temp, gainStatus, tempinfo)
        } catch (e: Exception) {
            XLog.i("activity: ${e.message}")
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
     * activity
     */
    private fun onCompassUpdate(): Boolean {
        val azimuthTxt = formatDegrees(compass.bearing.value, replace360 = true)
        compassView?.setCurAzimuth(azimuthTxt.first.toInt())
        return true
    }

    /**
     * activity，activityAngle
     */
    private fun formatDegrees(
        degrees: Float,
        decimalPlaces: Int = 0,
        replace360: Boolean = false,
    ): Pair<Float, Float> {
        val formatted = DecimalFormatter.format(degrees.toDouble(), decimalPlaces)
        val finalFormatted = if (replace360) formatted.replace("360", "0") else formatted
        return Pair(formatted.toFloat(), finalFormatted.toFloat())
    }

    private fun checkCameraPermission(needShowTip: Boolean) {
        if (!XXPermissions.isGranted(
                this,
                Permission.CAMERA,
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

    private fun initCameraPermission(needShowTip: Boolean) {
        XXPermissions.with(this@IRThermalNightActivity)
            .permission(Permission.CAMERA)
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        try {
                            if (allGranted) {
                                // Picture in pictureactivity
                                thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, true)
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
                                thermalRecyclerNight.setTwoLightSelected(TwoLightType.P_IN_P, false)
                                ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                            }
                        } catch (e: Exception) {
                            XLog.e("Picture in picture" + e.message)
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // Activity logic
                            if (BaseApplication.instance.isDomestic()) {
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

    private fun initAudioPermission() {
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
                                // Activity logic
                                isRecordAudio = true
                                SaveSettingUtil.isRecordAudio = isRecordAudio
                                videoRecord?.updateAudioState(true)
                                cameraItemAdapter?.data?.get(
                                    audioPosition,
                                )?.isSel = !(cameraItemAdapter?.data?.get(audioPosition)?.isSel?:false)
                                cameraItemAdapter?.notifyItemChanged(audioPosition)
                            } else {
                                ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                            }
                        } catch (e: Exception) {
                            Log.e("activity", "" + e.message)
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // Activity logic
                            if (BaseApplication.instance.isDomestic()) {
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
                },
            )
    }

    private fun initStoragePermission() {
        XXPermissions.with(this)
            .permission(
                permissionList,
            )
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        if (allGranted) {
                            if (!thermalRecyclerNight.isVideoMode) {
                                val setting = SharedManager.continuousBean
                                if (setting.isOpen) {
                                    if (!isAutoCamera) {
                                        // Activity logicPhoto
                                        autoJob =
                                            countDownCoroutines(
                                                setting.count,
                                                setting.continuaTime,
                                                this@IRThermalNightActivity.lifecycleScope,
                                                onTick = {
                                                    camera()
                                                },
                                                onStart = {
                                                    tvTypeInd?.visibility = VISIBLE
                                                    isAutoCamera = true
                                                },
                                                onFinish = {
                                                    tvTypeInd?.visibility = GONE
                                                    isAutoCamera = false
                                                },
                                            )
                                        autoJob?.start()
                                    } else {
                                        isAutoCamera = false
                                        autoJob?.cancel()
                                    }
                                } else {
                                    camera()
                                }
                            } else {
                                // Activity logic
                                video()
                            }
                        } else {
                            ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // Activity logic
                            if (BaseApplication.instance.isDomestic()) {
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
                },
            )
    }

    private fun setCarDetectPrompt() {
        // Car detection views don't exist in layout - commenting out for now
        /*
        var carDetectInfo = SharedManager.getCarDetectInfo()
        var tvDetectPrompt = viewCarDetect.findViewById<TextView>(R.id.tv_detect_prompt)
        if(carDetectInfo == null){
            tvDetectPrompt.text =  getString(R.string.abnormal_item1) + TemperatureUtil.getTempStr(40, 70)
        }else{
            var temperature = carDetectInfo.temperature.split("~")
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
