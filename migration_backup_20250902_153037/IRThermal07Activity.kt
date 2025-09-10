package com.topdon.thermal07

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.TextureView
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.infisense.usbir.utils.OpencvTools
import com.infisense.usbir.utils.ViewStubUtils
import com.ir.tas.base.bean.GWData
import com.ir.tas.base.utils.FileUtils
import com.ir.tas.utils.IXUtil
import com.topdon.gsr.util.TimeUtil
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.bean.event.SocketMsgEvent
import com.topdon.lib.core.comm.IrParam
import com.topdon.lib.core.comm.TempFont
import com.topdon.lib.core.common.ProductType.PRODUCT_NAME_TC007
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.common.WifiSaveSettingUtil
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.dialog.CarDetectDialog
import com.topdon.lib.core.dialog.EmissivityTipPopup
import com.topdon.lib.core.dialog.LongTextDialog
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.dialog.TipEmissivityDialog
import com.topdon.lib.core.dialog.TipShutterDialog
import com.topdon.lib.core.ktbase.BaseWifiActivity
import com.topdon.lib.core.repository.BatteryInfo
import com.topdon.lib.core.repository.Custom
import com.topdon.lib.core.repository.CustomColor
import com.topdon.lib.core.repository.GalleryRepository
import com.topdon.lib.core.repository.Isotherm
import com.topdon.lib.core.repository.IsothermC
import com.topdon.lib.core.repository.PalleteBean
import com.topdon.lib.core.repository.Param
import com.topdon.lib.core.repository.Stander
import com.topdon.lib.core.repository.TC007Repository
import com.topdon.lib.core.repository.WifiAttributeBean
import com.topdon.lib.core.socket.SocketCmdUtil
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.SpanBuilder
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.utils.BitmapUtils
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lib.core.utils.ImageUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.core.utils.TemperatureUtil
import com.topdon.lib.core.utils.WsCmdConstants
import com.topdon.lib.ui.widget.seekbar.OnRangeChangedListener
import com.topdon.lib.ui.widget.seekbar.RangeSeekBar
import com.topdon.lib.ui.widget.seekbar.RangeSeekBar.TEMP_MODE_CLOSE
import com.topdon.libcom.AlarmHelp
import com.topdon.libcom.bean.SaveSettingBean
import com.topdon.libcom.dialog.ColorPickDialog
import com.topdon.libcom.dialog.TempAlarmSetDialog
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.menu.constant.FenceType
import com.topdon.menu.constant.SettingType
import com.topdon.menu.constant.TwoLightType
import com.topdon.module.thermal.ir.activity.IRCameraSettingActivity
import com.topdon.module.thermal.ir.adapter.CameraItemAdapter
import com.topdon.module.thermal.ir.event.GalleryAddEvent
import com.topdon.module.thermal.ir.frame.FrameStruct
import com.topdon.module.thermal.ir.popup.SeekBarPopup
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.utils.IRConfigData
import com.topdon.module.thermal.ir.video.VideoRecordFFmpeg
import com.topdon.module.thermal.ir.view.TemperatureBaseView
import com.topdon.module.thermal.ir.view.TimeDownView
import com.topdon.pseudo.activity.PseudoSetActivity
import com.topdon.pseudo.bean.CustomPseudoBean
import com.topdon.tc001.camera.ParallelMultiModalRecorder
import com.topdon.tc001.camera.RGBCameraRecorder
import com.topdon.tc001.camera.ui.CameraSettingsView
import com.topdon.tc001.camera.ui.RecordingStatusIndicator
import com.topdon.tc001.camera.ui.SensorSelectionDialog
import com.topdon.tc001.gsr.EnhancedThermalRecorder
import com.topdon.tc001.gsr.ResearchTemplateActivity
import com.topdon.tc001.gsr.SessionManagerActivity
import com.topdon.tc004.activity.video.PlayFragment
import com.topdon.thermal07.util.WifiAttributeChangeU
import kotlinx.android.synthetic.main.activity_ir_thermal07.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.easydarwin.video.Client
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

    private fun startParallelRecording(sensors: Set<SensorSelectionDialog.SensorType>) {
        Log.i(TAG, "Starting parallel recording with sensors: ${sensors.map { it.displayName }.joinToString(", ")}")

        if (sensors.isEmpty()) {
            TToast.shortToast(this, "No sensors selected for recording")
            return
        }

        if (!hasRequiredPermissions()) {
            requestRequiredPermissions {
                if (it) {
                    startParallelRecordingInternal(sensors)
                } else {
                    TToast.shortToast(this, "Recording permissions required")
                }
            }
            return
        }

        startParallelRecordingInternal(sensors)
    }

    /**
     * Internal method to start parallel recording after permissions are granted
     */
    private fun startParallelRecordingInternal(sensors: Set<SensorSelectionDialog.SensorType>) {
        parallelRecorder?.let { recorder ->
            if (recorder.isRecording()) {

                recorder.stopParallelRecording()
                return
            }

            val rgbSettings =
                if (sensors.contains(SensorSelectionDialog.SensorType.RGB)) {
                    RGBCameraRecorder.RecordingSettings(
                        resolution = RGBCameraRecorder.Resolution.FULL_HD,
                        frameRate = 30,
                        enableStabilization = true,
                    )
                } else {
                    RGBCameraRecorder.RecordingSettings() // Default settings
                }

            if (sensors.contains(SensorSelectionDialog.SensorType.THERMAL)) {
                initVideoRecordFFmpeg()
                if (!videoRecord!!.canStartVideoRecord(null)) {
                    TToast.shortToast(this, "Cannot start thermal recording")
                    return
                }
            }

            val started =
                recorder.startParallelRecording(
                    selectedSensors = sensors,
                    sessionId = TimeUtil.generateSessionId("Parallel"),
                    rgbSettings = rgbSettings,
                )

            if (started) {

                if (sensors.contains(SensorSelectionDialog.SensorType.THERMAL)) {
                    startThermalRecordingComponent()
                }

                Log.i(TAG, "Parallel recording started successfully")
            } else {
                TToast.shortToast(this, "Failed to start parallel recording")
            }
        } ?: run {
            Log.e(TAG, "Parallel recorder not initialized")
            TToast.shortToast(this, "Recording system not available")
        }
    }

    /**
     * Start the thermal recording component (traditional thermal video recording)
     */
    private fun startThermalRecordingComponent() {
        if (!isVideo) {
            isVideo = true
            videoRecord?.startRecord()

            videoRecord?.stopVideoRecordListener = { isShowVideoRecordTips ->
                this@IRThermal07Activity.runOnUiThread {
                    if (isShowVideoRecordTips) {
                        try {
                            val dialog =
                                TipDialog.Builder(this@IRThermal07Activity)
                                    .setMessage(com.topdon.module.thermal.ir.R.string.tip_video_record)
                                    .create()
                        } catch (_: Exception) {
                        }
                    }

                    parallelRecorder?.stopParallelRecording()
                    isVideo = false
                    videoTimeClose()

                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(500)
                        thermal_recycler_night.refreshImg(GalleryRepository.DirType.TC007)
                    }
                }
            }

            videoTimeStart()
            Log.i(TAG, "Thermal recording component started")
        }
    }

    /**
     * Check if required permissions are granted
     */
    private fun hasRequiredPermissions(): Boolean {
        val requiredPermissions =
            mutableListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )

        if (selectedSensors.contains(SensorSelectionDialog.SensorType.RGB)) {
            requiredPermissions.add(Manifest.permission.CAMERA)
            requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (selectedSensors.contains(SensorSelectionDialog.SensorType.GSR) &&
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
        ) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Request required permissions for selected sensors
     */
    private fun requestRequiredPermissions(callback: (Boolean) -> Unit) {
        val requiredPermissions =
            mutableListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )

        if (selectedSensors.contains(SensorSelectionDialog.SensorType.RGB)) {
            requiredPermissions.add(Manifest.permission.CAMERA)
            requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (selectedSensors.contains(SensorSelectionDialog.SensorType.GSR) &&
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
        ) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        XXPermissions.with(this)
            .permission(requiredPermissions.toTypedArray())
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        all: Boolean,
                    ) {
                        callback(all)
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        never: Boolean,
                    ) {
                        callback(false)
                    }
                },
            )
    }

    /**
     * Show RGB camera controls in the thermal interface
     */
    private fun showRGBCameraControls() {
        rgbCameraSettingsView?.visibility = View.VISIBLE
        isRGBRecordingEnabled = true

        if (rgbCameraSettingsView?.parent == null) {
            val layoutParams =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    bottomMargin = 100
                }

            temp_bg.addView(rgbCameraSettingsView, layoutParams)
        }

        Log.i(TAG, "RGB camera controls enabled for mode: ${rgbRecordingMode.displayName}")
    }

    /**
     * Hide RGB camera controls
     */
    private fun hideRGBCameraControls() {
        rgbCameraSettingsView?.visibility = View.GONE
        isRGBRecordingEnabled = false
    }

    /**
     * Show RGB camera settings dialog
     */

    /**
     * Show RGB camera settings dialog (Legacy - Use Parallel Recording Dialog)
     * @deprecated Use long-press on title to access modern parallel recording system
     */
    @Deprecated("Use parallel recording system via long-press on title")
    private fun showRGBSettingsDialog() {
        val availableResolutions = parallelRecorder?.getSupportedRGBResolutions() ?: emptyList()
        val availableCameras = parallelRecorder?.getAvailableRGBCameras() ?: emptyList()

        val resolutionNames = availableResolutions.map { it.displayName }.toTypedArray()
        val cameraNames = availableCameras.map { it.displayName }.toTypedArray()

        val dialog =
            android.app.AlertDialog.Builder(this)
                .setTitle("RGB Camera Settings (Legacy)")
                .setMessage(
                    """
                    Long-press on app title for modern parallel recording system.

                    Available Resolutions: ${resolutionNames.joinToString(", ")}
                    Available Cameras: ${cameraNames.joinToString(", ")}

                    Current Settings:
                    • Resolution: ${parallelRecorder?.getCurrentRGBSettings()?.resolution?.displayName ?: "N/A"}
                    • Frame Rate: ${parallelRecorder?.getCurrentRGBSettings()?.frameRate ?: "N/A"} FPS
                    • Camera: ${parallelRecorder?.getRGBCameraFacing()?.displayName ?: "N/A"}
                    """.trimIndent(),
                )
                .setPositiveButton("Show Parallel System") { _, _ ->
                    showGSROptions()
                }
                .setNegativeButton("Close") { _, _ -> }
                .create()

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmHelp.getInstance(application).onDestroy(WifiSaveSettingUtil.isSaveSetting)
        temp_bg.stopAnimation()
        time_down_view.cancel()

        // Cleanup Multi-Modal Recording Resources
        parallelRecorder?.cleanup()
        parallelRecorder = null
        enhancedThermalRecorder?.cleanup()
        enhancedThermalRecorder = null

        // 退出时把点线面清掉
        CoroutineScope(Dispatchers.IO).launch {
            TC007Repository.clearAllTemp()
        }
    }

    override fun onStop() {
        super.onStop()
        time_down_view?.cancel()
        if (isVideo) {
            isVideo = false
            videoRecord?.stopRecord()
            videoTimeClose()

            parallelRecorder?.stopParallelRecording()
            enhancedThermalRecorder?.stopRecording()
            currentSessionId = null
            Log.d("ThermalSync", "Parallel multi-modal recording stopped in onStop")

            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                EventBus.getDefault().post(GalleryAddEvent())
            }
            lifecycleScope.launch {
                delay(500)
                thermal_recycler_night.refreshImg(GalleryRepository.DirType.TC007)
            }
        }
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
        view_car_detect.findViewById<RelativeLayout>(com.topdon.module.thermal.ir.R.id.rl_content).setOnClickListener {
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
