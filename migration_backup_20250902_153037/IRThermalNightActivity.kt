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

    private fun switchTs001Mode(isToTemp: Boolean) {
        if (isToTemp == isTs001TempMode) {
            return
        }
        tv_title_temp.isSelected = isToTemp
        tv_title_observe.isSelected = !isToTemp
        SaveSettingUtil.isMeasureTempMode = isToTemp

        // 关闭设置菜单的 PopupWindow
        ViewStubUtils.showViewStub(view_stub_camera, false, null)
        popupWindow?.dismiss()

        showCameraLoading()

        stopIfVideoing() // 结束正在执行的录像

        // 结束正在执行的连续拍照
        isAutoCamera = false
        autoJob?.cancel()

        // 结束正在执行的延迟拍照
        if (time_down_view.isRunning) {
            time_down_view.cancel()
            updateDelayView()
        }

        // 重置等温尺
        setDefLimit()
        updateTemperatureSeekBar(false) // 加锁

        if (isToTemp) { // 观测->测温
            // 恢复可见光
            if (SaveSettingUtil.isOpenTwoLight && XXPermissions.isGranted(this, Permission.CAMERA)) {
                cameraPreviewConfig(false)
            }

            // 关闭 AI 算法(动态识别、高温源、低温源)
            aiConfig = ObserveBean.TYPE_NONE
            imageThread?.typeAi = aiConfig
            thermal_recycler_night.setTempSource(aiConfig)

            // 关闭标靶
            thermal_recycler_night.setTargetSelected(TargetType.MODE, false)
            thermal_recycler_night.setTargetSelected(TargetType.STYLE, false)
            thermal_recycler_night.setTargetSelected(TargetType.COLOR, false)
            thermal_recycler_night.setTargetSelected(TargetType.DELETE, false)
            thermal_recycler_night.setTargetMode(ObserveBean.TYPE_MEASURE_PERSON) // 重置测量模式
            targetMeasureMode = ObserveBean.TYPE_MEASURE_PERSON
            targetStyle = ObserveBean.TYPE_TARGET_HORIZONTAL
            targetColorType = ObserveBean.TYPE_TARGET_COLOR_GREEN
            zoomView.hideView()

            // 关闭指南针
            saveSetBean.isOpenCompass = false
            thermal_recycler_night.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)
            compassView.visibility = View.GONE
            zoomView?.visibility = View.GONE
            stopCompass()
            zoomView.del(true)

            // 刷新伪彩条显示状态
            saveSetBean.isOpenPseudoBar = SaveSettingUtil.isOpenPseudoBar
            cl_seek_bar.isVisible = saveSetBean.isOpenPseudoBar
            thermal_recycler_night.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)
            temperature_seekbar?.setPseudocode(pseudoColorMode)

            // 清除高温点、低温点
            temperatureView.clear()
            temperatureView.isUserHighTemp = false
            temperatureView.isUserLowTemp = false
            temperatureView.isVisible = true
            temperatureView.temperatureRegionMode = REGION_MODE_CENTER
            showCross(false)
            thermal_recycler_night.clearTempPointSelect()
            thermal_recycler_night.fenceSelectType = FenceType.FULL // 选中全图

            // 警示是否打开
            alarmBean = SaveSettingUtil.alarmBean
            imageThread?.alarmBean = alarmBean
            if (alarmBean.isHighOpen || alarmBean.isLowOpen) {
                thermal_recycler_night.setSettingSelected(SettingType.ALARM, true)
                AlarmHelp.getInstance(this).updateData(alarmBean)
            } else {
                thermal_recycler_night.setSettingSelected(SettingType.ALARM, false)
                AlarmHelp.getInstance(this).updateData(null, null, null)
            }
        } else { // 测温->观测
            // 关闭画中画
            if (isOpenPreview) {
                isOpenPreview = false
                cameraPreview.closeCamera()
                thermal_recycler_night.setTwoLightSelected(TwoLightType.P_IN_P, false)
                cameraPreview.visibility = View.INVISIBLE
            }

            // 清除点、线、面、全图、趋势图
            temperatureView.clear()
            temperatureView.visibility = View.INVISIBLE
            temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
            hasClickTrendDel = true
            space_chart.isVisible = false
            cl_trend_open.isVisible = false
            ll_trend_close.isVisible = false
            showCross(false)

            // 切换到低温模式
            switchTempGain(isLow = true, false)

            // 切换到动态识别
            aiConfig = SaveSettingUtil.aiTraceType
            imageThread?.typeAi = aiConfig
            thermal_recycler_night.setTempSource(aiConfig)

            // 指南针是否打开
            saveSetBean.isOpenCompass = SaveSettingUtil.isOpenCompass
            thermal_recycler_night.setSettingSelected(SettingType.COMPASS, saveSetBean.isOpenCompass)

            // 高低温显示
            if (SaveSettingUtil.isOpenHighPoint || SaveSettingUtil.isOpenLowPoint) {
                temperatureView.temperatureRegionMode = REGION_MODE_RESET
                temperatureView.visibility = View.VISIBLE
            }
            temperatureView.isUserHighTemp = SaveSettingUtil.isOpenHighPoint
            temperatureView.isUserLowTemp = SaveSettingUtil.isOpenLowPoint
            thermal_recycler_night.setTempPointSelect(TempPointType.HIGH, SaveSettingUtil.isOpenHighPoint)
            thermal_recycler_night.setTempPointSelect(TempPointType.LOW, SaveSettingUtil.isOpenLowPoint)

            // 标靶是否打开
            targetMeasureMode = SaveSettingUtil.targetMeasureMode
            targetStyle = SaveSettingUtil.targetType
            targetColorType = SaveSettingUtil.targetColorType
            thermal_recycler_night.setTargetMode(targetMeasureMode)
            thermal_recycler_night.setTargetSelected(
                TargetType.COLOR,
                targetColorType != ObserveBean.TYPE_TARGET_COLOR_GREEN,
            )

            // 关闭伪彩条
            cl_seek_bar.visibility = View.GONE

            // 弹出观测模式说明提示框
            if (SharedManager.isTipObservePhoto) {
                TipObserveDialog.Builder(this)
                    .setTitle(R.string.app_tip)
                    .setMessage(R.string.tips_observe_photo_content)
                    .setCancelListener { isCheck ->
                        SharedManager.isTipObservePhoto = !isCheck
                    }
                    .create().show()
            }

            // 关闭温度报警
            alarmBean = AlarmBean()
            imageThread?.alarmBean = alarmBean
            AlarmHelp.getInstance(this).updateData(null, null, null)
        }

        // 关闭自定义渲染
        customPseudoBean.isUseCustomPseudo = false
        customPseudoBean.saveToShared()
        updateImageAndSeekbarColorList(customPseudoBean)

        if (!SaveSettingUtil.isSaveSetting) {
            // 重置伪彩
            setPColor(3)

            // 重置延时拍照
            cameraDelaySecond = DELAY_TIME_0
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[0].time = DELAY_TIME_0
                cameraItemAdapter!!.notifyItemChanged(0)
            }

            // 重置录音开关
            isRecordAudio = false
            videoRecord?.updateAudioState(false)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[3].isSel = false
                cameraItemAdapter!!.notifyItemChanged(3)
            }

            // 重置自动快门
            isAutoShutter = true
            ircmd?.setAutoShutter(isAutoShutter)
            if (cameraItemAdapter != null) {
                cameraItemAdapter!!.data[1].isSel = true
                cameraItemAdapter!!.notifyItemChanged(1)
            }

            // 重置拍照录制模式为拍照
            SaveSettingUtil.isVideoMode = false
            thermal_recycler_night.switchToCamera()

            // 重置对比度
            saveSetBean.contrastValue = 128
            ircmd?.setContrast(saveSetBean.contrastValue)

            // 重置锐度（细节）
            saveSetBean.ddeConfig = 2
            ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)

            // 重置旋转角度
            saveSetBean.rotateAngle = DeviceConfig.S_ROTATE_ANGLE
            updateRotateAngle(saveSetBean.rotateAngle)

            // 重置字体颜色及大小
            saveSetBean.tempTextColor = 0xffffffff.toInt()
            saveSetBean.tempTextSize = SizeUtils.sp2px(14f)
            temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
            temperatureView.setTextSize(saveSetBean.tempTextSize)

            // 重置标靶-缩放
            zoomConfig = 1

            // 重置镜像
            saveSetBean.isOpenMirror = false
            thermal_recycler_night.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
            ircmd?.setMirror(saveSetBean.isOpenMirror)
        }

        curChooseTabPos = if (isToTemp) Constants.IR_TEMPERATURE_MODE else Constants.IR_OBSERVE_MODE
        isTs001TempMode = isToTemp
        thermal_recycler_night.selectPosition(if (isToTemp) 0 else 10)
        view_menu_first.isObserveMode = !isToTemp

        // 指南针显示状态改变
        updateCompass()

    private fun switchTempGain(
        isLow: Boolean,
        isShowLoading: Boolean,
    ) {
        if ((gainSelChar == 1 && isLow) || (gainSelChar == 0 && !isLow)) { // 已处于目标模式
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
            0 -> { // 拍照/录像
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
                        time_down_view.setOnTimeDownListener(
                            object : TimeDownView.DownTimeWatcher {
                                override fun onTime(num: Int) {
                                    updateDelayView()
                                }

                                override fun onLastTime(num: Int) {
                                }

                                override fun onLastTimeFinish(num: Int) {
                                    if (thermal_recycler_night.isVideoMode) {
                                        updateVideoDelayView()
                                    } else {
                                        updateDelayView()
                                    }
                                    centerCamera()
                                }
                            },
                        )
                    }
                    time_down_view.downSecond(cameraDelaySecond)
                }
            }
            1 -> { // 图库
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
            2 -> { // 更多菜单
                settingCamera()
            }
            3 -> { // 切换到拍照
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = false
            }
            4 -> { // 切换到录像
                autoJob?.cancel()
                SaveSettingUtil.isVideoMode = true
            }
        }
    }

    private fun updateVideoDelayView() {
        try {
            if (time_down_view.isRunning) {
                lifecycleScope.launch(Dispatchers.Main) {
                    thermal_recycler_night.setToRecord(true)
                }
            }
        } catch (e: Exception) {
            Log.e("线程", e.message.toString())
        }
    }

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
                        val childLayoutParams = temperatureView.layoutParams
                        childLayoutParams.width = thermal_lay.measuredWidth
                        childLayoutParams.height = thermal_lay.measuredHeight
                        temperatureView.layoutParams = childLayoutParams
                        zoomView.setImageSize(imageHeight, imageWidth, thermal_lay.width, thermal_lay.height)
                        thermal_lay.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else if (!saveSetBean.isRotatePortrait() && thermal_lay.measuredHeight < thermal_lay.measuredWidth) {
                        val childLayoutParams = temperatureView.layoutParams
                        childLayoutParams.width = thermal_lay.measuredWidth
                        childLayoutParams.height = thermal_lay.measuredHeight
                        temperatureView.layoutParams = childLayoutParams
                        zoomView.setImageSize(imageWidth, imageHeight, thermal_lay.width, thermal_lay.height)
                        thermal_lay.viewTreeObserver.removeOnGlobalLayoutListener(this)
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
                        XLog.w("设置onCameraOpened:$uvcCamera}")
                    }

                    override fun onIRCMDCreate(ircmd: IRCMD) {
                        this@IRThermalNightActivity.ircmd = ircmd
                        // 需要等IRCMD初始化完成之后才可以调用
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
            // 第一帧数据
            this@IRThermalNightActivity.runOnUiThread {
                // 第一次进入，还会执行高低低温切换，这里没必要dismissCameraLoading
                // 从第二次开始，需要执行dismiss，否则dialog不消失
                if (isOnRestart) {
                    dismissCameraLoading()
                    isOnRestart = false
                }
            }
            // 使用Toast来显示异常信息
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

    // 设置TS001的温度校正
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
                    tv_title_temp.isVisible = true
                    tv_title_observe.isVisible = true
                }
                // 根据不同的高低增益加载不同的等效大气透过率表
//                getUTable()
                val value = IntArray(1)
                ircmd!!.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
                Log.d(TAG, "TPD_PROP_GAIN_SEL=" + value[0])
                gainStatus =
                    if (value[0] == 1) {
                        // 当前机芯为高增益
                        CommonParams.GainStatus.HIGH_GAIN
                        // 等效大气透过率表
                    } else {
                        // 当前机芯为低增益
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
        temperature_seekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
        temperature_seekbar.setPlaces(customPseudoBean.getPlaceList())
        temperature_iv_lock.visibility = View.INVISIBLE
        temperature_seekbar.setRangeAndPro(
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
        )
        tv_temp_content.visibility = View.VISIBLE
        thermal_recycler_night.setPseudoColor(-1)
        temperature_iv_input.setImageResource(R.drawable.ir_model)
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

    // 拍照中间按钮
    @SuppressLint("CheckResult")
    private fun centerCamera() {
        storageRequestType = 0
        checkStoragePermission()
    }

    private var showCameraSetting = false
    private val cameraItemBeanList by lazy {
        mutableListOf(
            CameraItemBean(
                "延迟",
                CameraItemBean.TYPE_DELAY,
                time = SaveSettingUtil.delayCaptureSecond,
            ),
            CameraItemBean(
                "自动快门",
                CameraItemBean.TYPE_ZDKM,
                isSel = SaveSettingUtil.isAutoShutter,
            ),
            CameraItemBean("手动快门", CameraItemBean.TYPE_SDKM),
            CameraItemBean(
                "声音",
                CameraItemBean.TYPE_AUDIO,
                isSel =
                    SaveSettingUtil.isRecordAudio &&
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO,
                        )
                        == PackageManager.PERMISSION_GRANTED,
            ),
            CameraItemBean("设置", CameraItemBean.TYPE_SETTING),
        )
    }

    private var cameraItemAdapter: CameraItemAdapter? = null

    private var isAutoShutter: Boolean = SaveSettingUtil.isAutoShutter

    // 拍照右边按钮
    private fun settingCamera() {
        showCameraSetting = !showCameraSetting
        if (showCameraSetting) {
            ViewStubUtils.showViewStub(view_stub_camera, true, callback = { view: View? ->
                view?.let {
                    val recyclerView = it.findViewById<RecyclerView>(R.id.recycler_view)
                    if (ScreenUtil.isPortrait(this)) {
                        recyclerView.layoutManager = GridLayoutManager(this, cameraItemBeanList.size)
                    } else {
                        recyclerView.layoutManager =
                            GridLayoutManager(
                                this,
                                cameraItemBeanList.size,
                                GridLayoutManager.VERTICAL,
                                false,
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
                                // 手动快门
                                if (syncimage.type == 1) {
                                    ircmd?.tc1bShutterManual()
                                } else {
                                    ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
                                }
                                ToastUtils.showShort(R.string.app_Manual_Shutter)
                                return@listener
                            }

                            CameraItemBean.TYPE_ZDKM -> {
                                // 自动快门
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
                    recyclerView.adapter = cameraItemAdapter
                }
            })
        } else {
            ViewStubUtils.showViewStub(view_stub_camera, false, null)
        }
    }

    open fun getCameraViewBitmap(): Bitmap {
        if (isOpenAmplify) {
            // 开启超分按钮，则取原始图像的超分进行处理四倍
            return imageThread?.getBaseBitmap(saveSetBean.rotateAngle)
                ?: cameraView.getScaledBitmap()
        } else {
            return cameraView.getScaledBitmap()
        }
    }

    // 拍照
    private fun camera() {
        lifecycleScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) {
                thermal_recycler_night.setToCamera()
            }
            try {
                synchronized(syncimage.dataLock) {
                    // 获取展示图像信息的图层数据
                    var cameraViewBitmap: Bitmap? =
                        if (isOpenAmplify) {
                            OpencvTools.supImageFourExToBitmap(getCameraViewBitmap())
                        } else {
                            getCameraViewBitmap()
                        }
                    // 可见光
                    if (isOpenPreview) {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmapByView(
                                cameraViewBitmap,
                                cameraPreview.getBitmap(),
                                cameraPreview,
                            )
                        // 画中画原图保存
                        cameraPreview.getBitmap()?.let {
                            ImageUtils.saveImageToApp(it)
                        }
                    }

//                    // 获取温度图层的数据，包括点线框，温度值等，重新合成bitmap
//                    if ((curChooseTabPos == 1 && temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) ||
//                        (curChooseTabPos == 2 && temperatureView.isUserHighTemp && temperatureView.isUserLowTemp)) {
//                        cameraViewBitmap = BitmapUtils.mergeBitmap(cameraViewBitmap, temperatureView.regionAndValueBitmap, 0, 0)
//                    }

                    // 合并伪彩条
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

                    // 合并指南针
                    val compassBitmap: Bitmap? =
                        if (compassView.visibility == VISIBLE) {
                            compassView.drawToBitmap()
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

                    // 产品在 2023/11/24 测试用例评审上确定拍照不需要保存温度报警闪烁效果
                    /*if (temp_bg.isVisible) {
                        if (alphaPaint == null) {
                            alphaPaint = Paint()
                        }
                        alphaPaint?.alpha = (temp_bg.animatorAlpha * 255).toInt()
                        cameraViewBitmap = BitmapUtils.mergeBitmapAlpha(cameraViewBitmap, temp_bg.drawToBitmap(), alphaPaint, 0, 0)
                    }*/

                    // 获取温度图层的数据，包括点线框，温度值等，重新合成bitmap
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
                    // 添加汽车检测
                    if (lay_car_detect_prompt.isVisible) {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmap(
                                cameraViewBitmap,
                                lay_car_detect_prompt.drawToBitmap(),
                                0,
                                0,
                            )
                    }
                    // 添加水印
                    val watermarkBean = SharedManager.watermarkBean
                    if (watermarkBean.isOpen) {
                        cameraViewBitmap =
                            BitmapUtils.drawCenterLable(
                                cameraViewBitmap,
                                watermarkBean.title,
                                watermarkBean.address,
                                if (watermarkBean.isAddTime) TimeTool.getNowTime() else "",
                                if (temperature_seekbar.isVisible) {
                                    temperature_seekbar.measuredWidth
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

                    if (curChooseTabPos == 1) { // 测温模式才需要保存温度数据
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
                        thermal_recycler_night.refreshImg()
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
     * 初始化视频采集组件
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
                carView = lay_car_detect_prompt,
            )
    }

    private fun video() {
        if (!isVideo) {
            // 开始录制
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
     * 如果正在进行录像，则停止录像.
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
                        pop_time_text.text = TimeTool.showVideoTime(it * 1000L)
                    }
                    if (it == time - 1) {
                        // 停止
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
     * 显示对比度设置 PopupWindow
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
     * 显示细节(锐度) 设置 PopupWindow
     */
    private fun showSharpnessPopup() {
        thermal_recycler_night.setSettingSelected(SettingType.DETAIL, true)

        val maxSharpness = 4 // 实际对比度取值 [0, 4]，用于百分比转换
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

//    //IMAGE_PROP_LEVEL_SNR (0~3) 空域降噪(默认2) 看不出
//    //IMAGE_PROP_LEVEL_TNR (0~3) 时域降噪(默认2) 看不出

    /**
     * 自动增益
     * IMAGE_PROP_MODE_AGC: 默认2
     * IMAGE_PROP_ONOFF_AGC: 默认1
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

    open fun switchAutoGain(boolean: Boolean) {
        iruvc?.auto_gain_switch = boolean
    }

    protected var configJob: Job? = null
    private var temperatureMode: Int = SaveSettingUtil.temperatureMode
    private val timeMillis = 150L

    // 配置
    protected fun configParam() {
        configJob =
            lifecycleScope.launch {
//            showLoading()
                while (isConfigWait && isActive) {
                    delay(200)
                }
                delay(500)
                val config = ConfigRepository.readConfig(false)
                val disChar = (config.distance * 128).toInt() // 距离(米)
                val emsChar = (config.radiation * 128).toInt() // 发射率
                XLog.w("设置TPD_PROP DISTANCE:$disChar, EMS:$emsChar}")
                delay(timeMillis)
                // 发射率
                // / Emissivity property. unit:1/128, range:1-128(0.01-1)
                ircmd?.setPropTPDParams(
                    CommonParams.PropTPDParams.TPD_PROP_EMS,
                    CommonParams.PropTPDParamsValue.NumberType(emsChar.toString()),
                )
                delay(timeMillis)
                // 距离
                ircmd?.setPropTPDParams(
                    CommonParams.PropTPDParams.TPD_PROP_DISTANCE,
                    CommonParams.PropTPDParamsValue.NumberType(disChar.toString()),
                )
//            设置高温、低温
                setTemperatureMode(temperatureMode, false)
                // 自动快门
                delay(timeMillis)
                XLog.w("设置TPD_PROP DISTANCE:$disChar, EMS:$emsChar}")
                if (isFirst && isrun) {
                    // 恢复镜像
                    thermal_recycler_night.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
                    ircmd?.setMirror(saveSetBean.isOpenMirror)
                    // 自动快门
                    delay(timeMillis)
                    withContext(Dispatchers.IO) {
                        // 部分机型在关闭自动快门，初始会花屏
                        ircmd?.setAutoShutter(true)
                        delay(2500)
                        ircmd?.setAutoShutter(isAutoShutter)
                        isFirst = false
                    }
                    // 设置锐度（细节）
                    ircmd?.setPropDdeLevel(saveSetBean.ddeConfig)
                    // 复位对比度
                    ircmd?.setContrast(saveSetBean.contrastValue)
                    if (SaveSettingUtil.isSaveSetting) {
                        XLog.i("配置中的模式为：${if (SaveSettingUtil.isMeasureTempMode) "测温" else "观测"}模式")
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
                // 手动快门
                if (syncimage.type == 1) {
                    ircmd?.tc1bShutterManual()
                } else {
                    ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
                }
                XLog.w("设置TPD_PROP DISTANCE2:$disChar, EMS:$emsChar}")
            }
    }

    // 设置tdp参数
    private fun setTpdParams(
        params: CommonParams.PropTPDParams,
        value: String,
    ) {
        ircmd?.setPropTPDParams(params, CommonParams.PropTPDParamsValue.NumberType(value))
    }

    // 设置img参数
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

    // 温度范围
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
                imageThread?.setLimit(upValue, downValue, upColor, downColor) // 自定义颜色
            }
            .setCancelListener(getString(R.string.app_close)) {
                upValue = -273f
                downValue = -273f
                imageThread?.setLimit(upValue, downValue, upColor, downColor) // 自定义颜色
            }
            .create().show()
    }

    private var isOpenPreview = false

    /**
     * 切换 画中画 开启或关闭 状态
     */
    private fun cameraPreviewConfig(needShowTip: Boolean) {
        if (!CheckDoubleClick.isFastDoubleClick()) {
            if (isOpenPreview) {
                // 关闭相机
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
                // 准备图像
                showCameraLoading()
            }

            101 -> {
                // 显示图像
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
     * 记录设备信息
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
                XLog.i("获取设备信息: $str")
            } catch (e: Exception) {
                XLog.e("获取SN失败: ${e.message}")
            }
        }
    }

    override fun tempCorrectByTs(temp: Float): Float {
        var tmp = temp
        try {
            tmp = tempCorrect(temp, gainStatus, tempinfo)
        } catch (e: Exception) {
            XLog.i("温度校正失败: ${e.message}")
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
     * 方位改变监听
     */
    private fun onCompassUpdate(): Boolean {
        val azimuthTxt = formatDegrees(compass.bearing.value, replace360 = true)
        compassView.setCurAzimuth(azimuthTxt.first.toInt())
        return true
    }

    /**
     * 根据传感器传回的值，计算实际角度
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
                                // 画中画开启
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
                            XLog.e("画中画" + e.message)
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // 拒绝授权并且不再提醒
                            if (BaseApplication.instance.isDomestic()) {
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
                                // 录音开启
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
                            Log.e("录音启动失败", "" + e.message)
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // 拒绝授权并且不再提醒
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
                            if (!thermal_recycler_night.isVideoMode) {
                                val setting = SharedManager.continuousBean
                                if (setting.isOpen) {
                                    if (!isAutoCamera) {
                                        // 连续拍照
                                        autoJob =
                                            countDownCoroutines(
                                                setting.count,
                                                setting.continuaTime,
                                                this@IRThermalNightActivity.lifecycleScope,
                                                onTick = {
                                                    camera()
                                                },
                                                onStart = {
                                                    tv_type_ind?.visibility = VISIBLE
                                                    isAutoCamera = true
                                                },
                                                onFinish = {
                                                    tv_type_ind?.visibility = GONE
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
                                // 录制视频
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
                            // 拒绝授权并且不再提醒
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
        var carDetectInfo = SharedManager.getCarDetectInfo()
        var tvDetectPrompt = view_car_detect.findViewById<TextView>(R.id.tv_detect_prompt)
        if (carDetectInfo == null) {
            tvDetectPrompt.text = getString(R.string.abnormal_item1) + TemperatureUtil.getTempStr(40, 70)
        } else {
            var temperature = carDetectInfo.temperature.split("~")
            tvDetectPrompt.text = carDetectInfo.item +
                TemperatureUtil.getTempStr(
                    temperature[0].toInt(),
                    temperature[1].toInt(),
                )
        }
        val test = intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER, false)
        lay_car_detect_prompt.visibility =
            if (intent.getBooleanExtra(
                    ExtraKeyConfig.IS_CAR_DETECT_ENTER,
                    false,
                )
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }
        view_car_detect.findViewById<RelativeLayout>(R.id.rl_content).setOnClickListener {
            CarDetectDialog(this) {
                var temperature = it.temperature.split("~")
                tvDetectPrompt.text = it.item +
                    TemperatureUtil.getTempStr(
                        temperature[0].toInt(),
                        temperature[1].toInt(),
                    )
            }.show()
        }
    }
}
