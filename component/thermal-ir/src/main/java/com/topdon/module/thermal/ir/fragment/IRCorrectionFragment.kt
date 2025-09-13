package com.topdon.module.thermal.ir.fragment

import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.yt.jni.Usbcontorl
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.utils.CommonParams
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
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.utils.CalibrationTools
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
\1thermal imaging
 */
/**
 * I r correction fragment for thermal imaging components.
 * Handles specific UI sections and user interactions.
 */
class IRCorrectionFragment : BaseFragment(), ITsTempListener {

\1TextdataText：image+temperatureTextdata */
    protected var defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT

    private var ircmd: IRCMD? = null

    // View references
    private lateinit var temperatureView: TemperatureView
    private var cameraView: CameraView? = null
    private lateinit var thermalLay: ViewGroup

    override fun initContentView() = R.layout.fragment_ir_monitor_thermal

    private var rotateAngle = 270 // 270

    override fun initView() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize views with findViewById
        temperatureView = requireView().findViewById<TemperatureView>(R.id.temperatureView)
        cameraView = requireView().findViewById<CameraView>(R.id.cameraView)
        thermalLay = requireView().findViewById<ViewGroup>(R.id.thermal_lay)

        initDataIR()
    }

    override fun initData() {
    }

    private var imageThread: ImageThreadTC? = null
    private var bitmap: Bitmap? = null
    private var iruvc: IRUVCTC? = null
    private val cameraWidth = 256
    private val cameraHeight = 384
    private val tempHeight = 192
    private var imageWidth = cameraWidth
    private var imageHeight = cameraHeight - tempHeight
    private val image = ByteArray(imageWidth * imageHeight * 2)
    private val temperature = ByteArray(imageWidth * imageHeight * 2)
    private val syncimage = SynchronizedBitmap()
    private var isrun = false
    private var pseudocolorMode = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun irEvent(event: IRMsgEvent) {
        if (event.code == MsgCode.RESTART_USB) {
            restartUsbCamera()
        }
    }

    /**
\1data
     */
    private fun initDataIR() {
        imageWidth = cameraHeight - tempHeight
        imageHeight = cameraWidth
        temperatureView.setTextSize(SaveSettingUtil.tempTextSize)
        if (ScreenUtil.isPortrait(requireContext())) {
            bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
            temperatureView.setImageSize(imageWidth, imageHeight, this@IRCorrectionFragment)
            rotateAngle = DeviceConfig.S_ROTATE_ANGLE
        } else {
            bitmap = Bitmap.createBitmap(imageHeight, imageWidth, Bitmap.Config.ARGB_8888)
            temperatureView.setImageSize(imageHeight, imageWidth, this@IRCorrectionFragment)
            rotateAngle = DeviceConfig.ROTATE_ANGLE
        }
        cameraView?.let { camera ->
            camera.setSyncimage(syncimage)
            camera.bitmap = bitmap
            camera.isDrawLine = false
        }
        temperatureView.setSyncimage(syncimage)
        temperatureView.setTemperature(temperature)
        temperatureView.isEnabled = false
        setViewLay()
\1TextdeviceTextdisabledsensor
        if (Usbcontorl.isload) {
            Usbcontorl.usb3803_mode_setting(1) // 5V
            Log.w("123", "Test Data")
        }
        temperatureView.clear()
        temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
    }

    /**
\1imageprocessing
     */
    private fun startISP() {
        try {
            imageThread = ImageThreadTC(context, imageWidth, imageHeight)
            imageThread!!.setDataFlowMode(defaultDataFlowMode)
            imageThread!!.setSyncImage(syncimage)
            imageThread!!.setImageSrc(image)
            imageThread!!.setTemperatureSrc(temperature)
            imageThread!!.setBitmap(bitmap)
            imageThread?.setRotate(rotateAngle)
            imageThread!!.setRotate(true)
            imageThread!!.start()
        } catch (e: Exception) {
            Log.e("Test Data", e.message.toString())
        }
    }

    /**
     *
     */
    private fun startUSB(isRestart: Boolean) {
        context?.let {
            iruvc =
                IRUVCTC(
                    cameraWidth, cameraHeight, context, syncimage,
                    defaultDataFlowMode,
                    object : ConnectCallback {
                        override fun onCameraOpened(uvcCamera: UVCCamera) {
                        }

                        override fun onIRCMDCreate(ircmd: IRCMD) {
                            Log.i(
                                TAG,
                                "ConnectCallback->onIRCMDCreate"Test Data"onStart"Test Data"onStop")
        if (iruvc != null) {
            iruvc!!.stopPreview()
            iruvc!!.unregisterUSB()
        }
        imageThread?.interrupt()
        syncimage.valid = false
        temperatureView.stop()
        cameraView?.stop()
        isrun = false
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "onDestroy")
        try {
            imageThread?.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "imageThread.join(): catch an interrupted exception"Test Data"TextTPD_PROP DISTANCE:$disChar, EMS:$emsChar}"Test Data"Text"Test Data"Text：" + "Test Data")
\1Text
\13 Text
//            CalibrationTools.shutter(irCmd = ircmd, syncImage = syncimage)
\1XLog.w("Test Data"+"Test Data")
\14 disabledText
            delay(2000)
            XLog.w("Test Data" + "Test Data")
            CalibrationTools.stsSwitch(irCmd = ircmd, false)
\15 Text
            CalibrationTools.pot(irCmd = ircmd!!, 1)
            XLog.w("Test Data" + "Test Data")
\16 Text
            delay(5000)
            XLog.w("Test Data" + "Test Data")
            CalibrationTools.stsSwitch(irCmd = ircmd, true)
            delay(20000)
            XLog.w("Test Data" + "20000"Test Data"Text："+"Test Data")
\112 disabledText
            delay(2000)
            CalibrationTools.stsSwitch(irCmd = ircmd, false)
            XLog.w("Test Data" + "Test Data")
\113 Text
            CalibrationTools.pot(irCmd = ircmd!!, 1)
\114 Text
            delay(5000)
            XLog.w("Test Data" + "Test Data")
            CalibrationTools.stsSwitch(irCmd = ircmd, true)
\117 Text
            CalibrationTools.autoShutter(irCmd = ircmd, true)
\1Text
            XLog.w("Test Data" + "Test Data")
        }
    }
}
