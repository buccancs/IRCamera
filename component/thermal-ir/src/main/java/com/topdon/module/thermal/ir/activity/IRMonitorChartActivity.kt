package com.topdon.module.thermal.ir.activity

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.yt.jni.Usbcontorl
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.ircmd.IRCMDType
import com.energy.iruvc.ircmd.IRUtils
import com.energy.iruvc.sdkisp.LibIRTemp
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.CommonUtils
import com.energy.iruvc.utils.Line
import com.energy.iruvc.utils.SynchronizedBitmap
import com.energy.iruvc.uvc.ConnectCallback
import com.energy.iruvc.uvc.UVCCamera
import com.infisense.usbir.camera.IRUVCTC
import com.infisense.usbir.config.MsgCode
import com.infisense.usbir.event.IRMsgEvent
import com.infisense.usbir.event.PreviewComplete
import com.infisense.usbir.thread.ImageThreadTC
import com.infisense.usbir.utils.USBMonitorCallback
import com.infisense.usbir.view.CameraView
import com.infisense.usbir.view.ITsTempListener
import com.infisense.usbir.view.TemperatureView
import com.infisense.usbir.view.TemperatureView.*
import com.topdon.lib.core.bean.event.device.DeviceCameraEvent
import com.topdon.lib.core.bean.tools.ThermalBean
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.ThermalEntity
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.core.view.TitleView
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.bean.SelectPositionBean
import com.topdon.module.thermal.ir.event.MonitorSaveEvent
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.view.ChartMonitorView
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal
import java.math.RoundingMode
import com.topdon.lib.core.R as LibR

    private fun startUSB(isRestart: Boolean) {
        iruvc =
            IRUVCTC(
                cameraWidth,
                cameraHeight,
                this@IRMonitorChartActivity,
                syncimage,
                defaultDataFlowMode,
                object : ConnectCallback {
                    override fun onCameraOpened(uvcCamera: UVCCamera) {
                    }

                    override fun onIRCMDCreate(ircmd: IRCMD) {
                        Log.i(
                            TAG,
                            "ConnectCallback->onIRCMDCreate",
                        )
                        this@IRMonitorChartActivity.ircmd = ircmd
                        // Activity operationIRCMDactivity
//                    ircmd.setPseudoColor(
//                        CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                        PseudocodeUtils.changePseudocodeModeByOld(pseudoColorMode))
                        val fwBuildVersionInfoBytes = ByteArray(50)
                        ircmd?.getDeviceInfo(
                            CommonParams.DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
                            fwBuildVersionInfoBytes,
                        ) // ok
                        val value = IntArray(1)
                        val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
                        isTS001 = arm.contains("Mini256", true)
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
                    }
                },
                object : USBMonitorCallback {
                    override fun onAttach() {}

                    override fun onGranted() {}

                    override fun onConnect() {}

                    override fun onDisconnect() {}

                    override fun onDettach() {
                        finish()
                    }

                    override fun onCancel() {
                        finish()
                    }
                },
            )
        iruvc!!.isRestart = isRestart
        iruvc!!.setImageSrc(imageBytes)
        iruvc!!.setTemperatureSrc(temperatureBytes)
        iruvc!!.setRotate(rotateAngle)
        iruvc!!.registerUSB()
    }

    /**
     *
     */
    private fun restartUsbCamera() {
        if (iruvc != null) {
            iruvc!!.stopPreview()
            iruvc!!.unregisterUSB()
        }
        startUSB(true)
    }

    private var isConfigWait = false

    // Activity logic
    private fun configParam() {
        lifecycleScope.launch {
            isConfigWait = true
            while (isConfigWait) {
                delay(100)
            }
            val config = ConfigRepository.readConfig(false)
            val disChar = (config.distance * 128).toInt() // Activity logic(activity)
            val emsChar = (config.radiation * 128).toInt() // Activity logic
            XLog.w("SettingsTPD_PROP DISTANCE:$disChar, EMS:$emsChar}")
            val timeMillis = 250L
            delay(timeMillis)
            // Activity logic
            ircmd!!.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_EMS,
                CommonParams.PropTPDParamsValue.NumberType(emsChar.toString()),
            )
            delay(timeMillis)
            // Activity logic
            ircmd!!.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_DISTANCE,
                CommonParams.PropTPDParamsValue.NumberType(disChar.toString()),
            )
            // Activity operation
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2,
            )
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2,
            )
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2,
            )
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2,
            )
            iruvc?.let {
                // Activity operation，activity
                withContext(Dispatchers.IO) {
                    if (SaveSettingUtil.isAutoShutter) {
                        ircmd!!.setPropAutoShutterParameter(
                            CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
                            CommonParams.PropAutoShutterParameterValue.StatusSwith.ON,
                        )
                    } else {
                        ircmd!!.setPropAutoShutterParameter(
                            CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
                            CommonParams.PropAutoShutterParameterValue.StatusSwith.OFF,
                        )
                    }
                }
            }
            // Activity logic、activity
            delay(timeMillis)
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_LEVEL_CONTRAST,
                CommonParams.PropImageParamsValue.NumberType(128.toString()),
            )
            delay(timeMillis)
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_LEVEL_DDE,
                CommonParams.PropImageParamsValue.DDEType.DDE_2,
            )
            delay(timeMillis)
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_ONOFF_AGC,
                CommonParams.PropImageParamsValue.StatusSwith.ON,
            )
        }
    }

    /**
     * activityPoint/Line/Area
     */
    private fun addTempLine() {
        temperatureView.visibility = View.VISIBLE
        when (selectBean.type) {
            1 -> {
                // Activity logic
                temperatureView.addScalePoint(selectBean.startPosition)
                temperatureView.temperatureRegionMode = REGION_MODE_POINT
            }
            2 -> {
                // Activity logic
                temperatureView.addScaleLine(
                    Line(
                        selectBean.startPosition,
                        selectBean.endPosition,
                    ),
                )
                temperatureView.temperatureRegionMode = REGION_MODE_LINE
            }
            3 -> {
                // Activity logic
                temperatureView.addScaleRectangle(
                    Rect(
                        selectBean.startPosition!!.x,
                        selectBean.startPosition!!.y,
                        selectBean.endPosition!!.x,
                        selectBean.endPosition!!.y,
                    ),
                )
                temperatureView.temperatureRegionMode = REGION_MODE_RECTANGLE
            }
        }
        temperatureView.drawLine()
    }

    private fun setViewLay() {
        thermalLay.post {
            val params = thermalLay.layoutParams
            if (ScreenUtil.isPortrait(this)) {
                params.height = thermalLay.height
                var w = params.height * imageWidth / imageHeight
                if (w > ScreenUtil.getScreenWidth(this)) {
                    w = ScreenUtil.getScreenWidth(this)
                }
                params.width = w
            } else {
                params.width = thermalLay.width
                params.height = params.width * imageWidth / imageHeight
            }
            thermalLay.layoutParams = params
        }
    }

    override fun tempCorrectByTs(temp: Float?): Float {
        var tmp = temp
        try {
            tmp = tempCorrect(temp!!, gainStatus, 0)
        } catch (e: Exception) {
            XLog.i("activity: ${e.message}")
        }
        return tmp!!
    }

    /**
     * activity
     */
    private fun tempCorrect(
        temp: Float,
        gainStatus: CommonParams.GainStatus,
        tempInfo: Long,
    ): Float {
        if (!isTS001) {
            // Activity logicts001activity
            return temp
        }
        if (ts_data_H == null || ts_data_L == null) {
            return temp
        }
        val config = ConfigRepository.readConfig(false)
        config.radiation
        val paramsArray =
            floatArrayOf(
                temp,
                config.radiation,
                config.environment,
                config.environment,
                config.distance,
                0.8f,
            )
        val newTemp =
            IRUtils.temperatureCorrection(
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
                gainStatus,
            )
        Log.i(
            TAG,
            "temp correct, oldTemp = " + paramsArray[0] + " ems = " + paramsArray[1] + " ta = " + paramsArray[2] + " " +
                "distance = " + paramsArray[4] + " hum = " + paramsArray[5] + " productType = ${CommonParams.ProductType.WN256_ADVANCED}" + " " +
                "newtemp = " + newTemp,
        )
        return newTemp
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
                dismissCameraLoading()
                addTempLine()
            }
        }
    }
}
