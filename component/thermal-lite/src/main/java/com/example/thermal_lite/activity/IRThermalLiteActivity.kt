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

    private fun setCustomPseudoColorList(
        colorList: IntArray?,
        places: FloatArray?,
        isUseGray: Boolean,
        customMaxTemp: Float,
        customMinTemp: Float,
    ) {
        CameraPreviewManager.getInstance()?.setColorList(colorList, places, isUseGray, customMaxTemp, customMinTemp)
    }

    // Activity logicPseudo-coloractivity
    private fun updateImageAndSeekbarColorList(customPseudoBean: CustomPseudoBean?) {
        customPseudoBean?.let {
            binding.temperatureSeekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
            binding.temperatureSeekbar.setPlaces(customPseudoBean.getPlaceList())
            setCustomPseudoColorList(
                customPseudoBean.getColorList(),
                customPseudoBean.getPlaceList(),
                customPseudoBean.isUseGray,
                it.maxTemp,
                it.minTemp,
            )
            if (it.isUseCustomPseudo) {
                binding.temperatureIvLock.visibility = View.INVISIBLE
                binding.tvTempContent.visibility = View.VISIBLE
                setDefLimit()
                updateTemperatureSeekBar(false) // Activity logic
                binding.temperatureSeekbar.setRangeAndPro(
                    UnitTools.showUnitValue(it.minTemp),
                    UnitTools.showUnitValue(it.maxTemp),
                    UnitTools.showUnitValue(it.minTemp),
                    UnitTools.showUnitValue(it.maxTemp),
                )
                binding.thermalRecyclerNight.setPseudoColor(-1)
                binding.temperatureIvInput.setImageResource(com.topdon.module.thermal.ir.R.drawable.ir_model)
            } else {
                binding.temperatureIvLock.visibility = View.VISIBLE
                binding.thermalRecyclerNight.setPseudoColor(pseudoColorMode)
                if (this.customPseudoBean.isUseCustomPseudo) {
                    setDefLimit()
                }
                binding.tvTempContent.visibility = View.GONE
                binding.temperatureIvInput.setImageResource(com.topdon.module.thermal.ir.R.drawable.ic_color_edit)
            }
            this.customPseudoBean = it
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
                if (binding.timeDownView.isRunning) {
                    binding.timeDownView.cancel()
                    updateDelayView()
                } else {
                    if (binding.timeDownView.downTimeWatcher == null) {
                        binding.timeDownView.setOnTimeDownListener(
                            object : TimeDownView.DownTimeWatcher {
                                override fun onTime(num: Int) {
                                    updateDelayView()
                                }

                                override fun onLastTime(num: Int) {
                                }

                                override fun onLastTimeFinish(num: Int) {
                                    if (binding.thermalRecyclerNight.isVideoMode) {
                                        updateVideoDelayView()
                                    } else {
                                        updateDelayView()
                                    }
                                    centerCamera()
                                }
                            },
                        )
                    }
                    binding.timeDownView.downSecond(cameraDelaySecond)
                }
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
                        .navigation(this@IRThermalLiteActivity)
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

    // Photoactivity
    private fun settingCamera() {
        showCameraSetting = !showCameraSetting
        if (showCameraSetting) {
            ViewStubUtils.showViewStub(binding.viewStubCamera, true, callback = { view: View? ->
                view?.let {
                    val recyclerView = it.findViewById<RecyclerView>(com.topdon.module.thermal.ir.R.id.recycler_view)
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
                                NavigationManager.getInstance().build(RouterConfig.IR_CAMERA_SETTING)
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
                                        ToastUtils.showShort(com.topdon.module.thermal.ir.R.string.off_photography)
                                    }

                                    CameraItemBean.DELAY_TIME_3 -> {
                                        ToastUtils.showShort(com.topdon.module.thermal.ir.R.string.seconds_dalay_3)
                                    }

                                    CameraItemBean.DELAY_TIME_6 -> {
                                        ToastUtils.showShort(com.topdon.module.thermal.ir.R.string.seconds_dalay_6)
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
                                IRTool.setOneShutter()
                                ToastUtils.showShort(com.topdon.module.thermal.ir.R.string.app_Manual_Shutter)
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
                                            .setMessage(com.topdon.module.thermal.ir.R.string.shutter_tips)
                                            .setCancelListener { isCheck ->
                                                SharedManager.isTipShutter = !isCheck
                                            }
                                            .create()
                                    dialog.show()
                                }
                                IRTool.setAutoShutter(isAutoShutter)
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

    /**
     *
     */
    fun updateVideoDelayView() {
        try {
            if (binding.timeDownView.isRunning) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.thermalRecyclerNight.setToRecord(true)
                }
            }
        } catch (e: Exception) {
            Log.e("activity", e.message.toString())
        }
    }

    private fun checkStoragePermission() {
        if (!XXPermissions.isGranted(this, getPermissionList())) {
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
        XXPermissions.with(this@IRThermalLiteActivity)
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
                                ToastUtils.showShort(com.topdon.module.thermal.ir.R.string.scan_ble_tip_authorize)
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
                                ToastUtils.showShort(
                                    getString(com.topdon.module.thermal.ir.R.string.app_microphone_content),
                                )
                                return
                            }
                            TipDialog.Builder(this@IRThermalLiteActivity)
                                .setTitleMessage(getString(com.topdon.module.thermal.ir.R.string.app_tip))
                                .setMessage(getString(com.topdon.module.thermal.ir.R.string.app_microphone_content))
                                .setPositiveListener(com.topdon.module.thermal.ir.R.string.app_open) {
                                    AppUtils.launchAppDetailsSettings()
                                }
                                .setCancelListener(com.topdon.module.thermal.ir.R.string.app_cancel) {
                                }
                                .setCanceled(true)
                                .create().show()
                        }
                    }
                },
            )
    }

    /**
     * activityUI
     */
    fun updateDelayView() {
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
            Log.e("activity", e.message.toString())
        }
    }

    private fun initStoragePermission() {
        XXPermissions.with(this)
            .permission(
                getPermissionList(),
            )
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        if (allGranted) {
                            if (!binding.thermalRecyclerNight.isVideoMode) {
                                val setting = SharedManager.continuousBean
                                if (setting.isOpen) {
                                    if (!isAutoCamera) {
                                        // Activity logicPhoto
                                        autoJob =
                                            countDownCoroutines(
                                                setting.count,
                                                setting.continuaTime,
                                                this@IRThermalLiteActivity.lifecycleScope,
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
                            TipDialog.Builder(this@IRThermalLiteActivity)
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

    private fun camera() {
        lifecycleScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) {
                binding.thermalRecyclerNight.setToCamera()
            }
            try {
                synchronized(syncimage.dataLock) {
                    // Activity operation
                    var cameraViewBitmap: Bitmap? = getCameraViewBitmap()
                    // Visible light
                    if (isOpenPreview) {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmapByView(
                                cameraViewBitmap,
                                binding.cameraPreview.getBitmap(),
                                binding.cameraPreview,
                            )
                        // Picture in pictureactivity
                        binding.cameraPreview.getBitmap()?.let {
                            ImageUtils.saveImageToApp(it)
                        }
                    }

                    // Activity operation，activity，activity，activitybitmap
                    if (binding.temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) {
                        cameraViewBitmap =
                            BitmapUtils.mergeBitmap(
                                cameraViewBitmap,
                                binding.temperatureView.regionAndValueBitmap,
                                0,
                                0,
                            )
                    }

                    // Activity operationPseudo-coloractivity
                    val isShowPseudoBar = binding.clSeekBar.visibility == VISIBLE
                    if (isShowPseudoBar) {
                        val seekBarBitmap = binding.clSeekBar.drawToBitmap()
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
                    val watermarkBean = SharedManager.watermarkBean
                    if (watermarkBean.isOpen) {
                        cameraViewBitmap =
                            BitmapUtils.drawCenterLable(
                                cameraViewBitmap,
                                watermarkBean.title,
                                watermarkBean.address,
                                if (watermarkBean.isAddTime) TimeTool.getNowTime() else "",
                                if (binding.temperatureSeekbar.isVisible) {
                                    binding.temperatureSeekbar.measuredWidth
                                } else {
                                    0
                                },
                            )
                    }
                    // Activity logic - disabled due to missing layout
                    // if (binding.layCarDetectPrompt.isVisible){
                    //     cameraViewBitmap = BitmapUtils.mergeBitmap(
                    //         cameraViewBitmap,
                    //         binding.layCarDetectPrompt.drawToBitmap(), 0, 0)
                    // }

                    var name = ""
                    cameraViewBitmap?.let {
                        name = ImageUtils.save(bitmap = it)
                    }
                    // m256activityp2activityRotateAngleactivity
                    val basicGainGetValue = IntArray(1)
                    val basicGainGet: IrcmdError =
                        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                            ?.basicGainGet(basicGainGetValue)!!
                    val p2RotateAngle = if (saveSetBean.rotateAngle == 90) 270 else abs(saveSetBean.rotateAngle - 180)
                    val capital =
                        FrameStruct.toCode(
                            name = getProductName(),
                            width = if (cameraViewBitmap!!.height < cameraViewBitmap.width) 256 else 192,
                            height = if (cameraViewBitmap.height < cameraViewBitmap.width) 192 else 256,
                            rotate = p2RotateAngle,
                            pseudo = pseudoColorMode,
                            initRotate = initRotate,
                            correctRotate = correctRotate,
                            customPseudoBean = customPseudoBean,
                            isShowPseudoBar = isShowPseudoBar,
                            textColor = saveSetBean.tempTextColor,
                            watermarkBean = watermarkBean,
                            alarmBean = alarmBean,
                            basicGainGetValue[0],
                            textSize = saveSetBean.tempTextSize,
                            config?.environment ?: 0f,
                            config?.distance ?: 0f,
                            config?.radiation ?: 0f,
                            false,
                        )
                    ImageUtils.saveFrame(
                        bs = CameraPreviewManager.getInstance().frameIrAndTempData,
                        capital = capital,
                        name = name,
                    )
                    launch(Dispatchers.Main) {
                        binding.thermalRecyclerNight.refreshImg()
                    }
                    EventBus.getDefault().post(GalleryAddEvent())
                }
            } catch (e: Exception) {
                XLog.e(e.message)
            }
        }
    }

    private fun getCameraViewBitmap(): Bitmap {
        return Bitmap.createScaledBitmap(
            CameraPreviewManager.getInstance().scaledBitmap(true),
            binding.cameraView.width,
            binding.cameraView.height,
            true,
        )
    }

    /**
     * activity
     */
    private fun initVideoRecordFFmpeg() {
        videoRecord =
            VideoRecordFFmpeg(
                binding.cameraView,
                binding.cameraPreview,
                binding.temperatureView,
                true,
                binding.clSeekBar,
                binding.tempBg,
                null,
                null,
                carView = null, // binding.layCarDetectPrompt - disabled due to missing layout
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
                this@IRThermalLiteActivity.runOnUiThread {
                    if (isShowVideoRecordTips) {
                        try {
                            val dialog =
                                TipDialog.Builder(this@IRThermalLiteActivity)
                                    .setMessage(com.topdon.module.thermal.ir.R.string.tip_video_record)
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
                binding.thermalRecyclerNight.refreshImg()
                EventBus.getDefault().post(GalleryAddEvent())
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun centerCamera() {
        storageRequestType = 0
        checkStoragePermission()
    }

    override fun initData() {
        initDataIR()
    }

    /**
     * activity
     */
    private fun initDataIR() {
        imageWidth = cameraHeight - tempHeight
        imageHeight = cameraWidth
        CameraPreviewManager.getInstance().alarmBean = alarmBean
        AlarmHelp.getInstance(this).updateData(alarmBean)
        CameraPreviewManager.getInstance().setPseudocolorMode(pseudoColorMode)
        binding.temperatureSeekbar?.setPseudocode(pseudoColorMode)

        binding.clSeekBar.isVisible = saveSetBean.isOpenPseudoBar
        binding.thermalRecyclerNight.setSettingSelected(SettingType.PSEUDO_BAR, saveSetBean.isOpenPseudoBar)

        if (customPseudoBean.isUseCustomPseudo) {
            updateCustomPseudo()
        } else {
            CameraPreviewManager.getInstance().setColorList(null, null, false, 0f, 0f)
            binding.temperatureSeekbar.progressColor
            binding.temperatureIvLock.visibility = View.VISIBLE
            binding.tvTempContent.visibility = View.GONE
            binding.temperatureIvInput.setImageResource(com.topdon.module.thermal.ir.R.drawable.ic_color_edit)
            binding.thermalRecyclerNight.setPseudoColor(pseudoColorMode)
        }
        binding.thermalRecyclerNight.setSettingSelected(SettingType.ALARM, alarmBean.isHighOpen || alarmBean.isLowOpen)
        binding.thermalRecyclerNight.setSettingSelected(SettingType.FONT, !saveSetBean.isTempTextDefault())
        binding.temperatureView.setTextSize(saveSetBean.tempTextSize)
        binding.temperatureView.setLinePaintColor(saveSetBean.tempTextColor)
        binding.thermalRecyclerNight.setSettingRotate(saveSetBean.rotateAngle)
        binding.thermalRecyclerNight.setTempLevel(temperatureMode)

        configJob =
            lifecycleScope.launch {
                while (isConfigWait && isActive) {
                    delay(200)
                }
                delay(500)
                IRTool.setAutoShutter(false)
                // Activity logic
                IRTool.basicGlobalContrastLevelSet((saveSetBean.contrastValue / 255f * 100).toInt())
                // Activity logic
                IRTool.basicMirrorAndFlipStatusSet(saveSetBean.isOpenMirror)
                binding.thermalRecyclerNight.setSettingSelected(SettingType.MIRROR, saveSetBean.isOpenMirror)
                CameraPreviewManager.getInstance()?.setLimit(
                    editMaxValue,
                    editMinValue,
                    upColor,
                    downColor,
                )
                delay(2000)
                // Activity logicModeactivity
                withContext(Dispatchers.IO) {
                    IRTool.basicGainSet(temperatureMode)
                }
                // Activity logic
                delay(30 * 1000)
                IRTool.setAutoShutter(isAutoShutter)
                XLog.i("activity")
            }
    }

    private fun updateCustomPseudo() {
        binding.temperatureSeekbar.setColorList(customPseudoBean.getColorList()?.reversedArray())
        binding.temperatureSeekbar.setPlaces(customPseudoBean.getPlaceList())
        binding.temperatureIvLock.visibility = View.INVISIBLE
        binding.temperatureSeekbar.setRangeAndPro(
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
            UnitTools.showUnitValue(customPseudoBean.minTemp),
            UnitTools.showUnitValue(customPseudoBean.maxTemp),
        )
        setCustomPseudoColorList(
            customPseudoBean.getColorList(),
            customPseudoBean.getPlaceList(),
            customPseudoBean.isUseGray,
            customPseudoBean.maxTemp,
            customPseudoBean.minTemp,
        )
        binding.tvTempContent.visibility = View.VISIBLE
        binding.thermalRecyclerNight.setPseudoColor(-1)
        binding.temperatureIvInput.setImageResource(com.topdon.module.thermal.ir.R.drawable.ir_model)
    }

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

    private fun videoTimeClose() {
        flow?.cancel()
        flow = null
        popTimeLay.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        initPreviewManager()
//        if (USBMonitorManager.getInstance().isDeviceConnected){
//            ctrlBlock?.let {
//                DeviceControlManager.getInstance().handleStartPreview(it)
//            }
//        }
//        binding.temperatureView.start()
        binding.thermalRecyclerNight.updateCameraModel()
    }

    override fun onResume() {
        super.onResume()
        AlarmHelp.getInstance(this).onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (isPause) {
            DeviceControlManager.getInstance().handleResumeDualPreview()
            isPause = false
        }
        binding.thermalRecyclerNight.refreshImg()
        config = ConfigRepository.readConfig(false)
        orientationEventListener.enable()
        setCarDetectPrompt()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        startOrientation()
    }

    override fun onPause() {
        super.onPause()
        orientationEventListener.disable()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        AlarmHelp.getInstance(this).pause()
        isAutoCamera = false
        autoJob?.cancel()
        binding.timeDownView?.cancel()
        isPause = true
        DeviceControlManager.getInstance().handlePauseDualPreview()
    }

    override fun onStop() {
        super.onStop()
        configJob?.cancel()
        binding.temperatureView?.stop()
        binding.timeDownView?.cancel()
        try {
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
        } catch (e: Exception) {
            XLog.e("$TAG:onStop-${e.message}")
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
            XLog.e("$TAG:liteactivity-${e.message}")
        }
        SystemClock.sleep(100)
    }

    private fun setCarDetectPrompt() {
        // Car detection feature not available in thermal-lite layout

        // if(carDetectInfo == null){
        //     tvDetectPrompt.text =  getString(R.string.abnormal_item1) + TemperatureUtil.getTempStr(40, 70)
        // }else{

        //     tvDetectPrompt.text =  carDetectInfo.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
        // }
        // binding.layCarDetectPrompt.visibility = if(intent.getBooleanExtra(ExtraKeyConfig.IS_CAR_DETECT_ENTER,false)) View.VISIBLE else View.GONE
        // binding.viewCarDetect.setOnClickListener {
        //     CarDetectDialog(this) {

        //         tvDetectPrompt.text =  it.item + TemperatureUtil.getTempStr(temperature[0].toInt(), temperature[1].toInt())
        //     }.show()
        // }
    }

    var config: DataBean? = null
    val basicGainGetValue = IntArray(1)
    var basicGainGetTime = 0L

    override fun tempCorrectByTs(temp: Float?): Float {
        if (isPause) {
            return temp!!
        }
        var tempNew = temp
        try {
            if (config == null) {
                config = ConfigRepository.readConfig(false)
            }
            if (isConfigWait) {
                return temp!!
            }
            val defModel = DataBean()
            if (config!!.radiation == defModel.radiation &&
                defModel.environment == config!!.environment &&
                defModel.distance == config!!.distance
            ) {
                return temp!!
            }

            // Activity logicState PASS
            if (System.currentTimeMillis() - basicGainGetTime > 5000L) {
                try {
                    val basicGainGet: IrcmdError? =
                        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                            ?.basicGainGet(basicGainGetValue)
                } catch (e: Exception) {
                    XLog.e("activity")
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
            XLog.e("$TAG--activity：${e.message}")
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
