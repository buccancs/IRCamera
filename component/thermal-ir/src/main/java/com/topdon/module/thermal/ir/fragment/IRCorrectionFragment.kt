package com.topdon.module.thermal.ir.fragment

import android.graphics.Bitmap
import android.util.Log
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
import com.infisense.usbir.view.ITsTempListener
import com.infisense.usbir.view.TemperatureView.*
import com.topdon.lib.core.bean.event.device.DeviceCameraEvent
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.utils.CalibrationTools
import com.topdon.module.thermal.ir.databinding.FragmentIrMonitorThermalBinding
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class IRCorrectionFragment : BaseFragment(),ITsTempListener{

    private lateinit var binding: FragmentIrMonitorThermalBinding

    /** + */
    protected var defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT

    private var ircmd: IRCMD? = null

    override fun initContentView() = R.layout.fragment_ir_monitor_thermal

    private var rotateAngle = 270 //270

    override fun initView() {
        binding = FragmentIrMonitorThermalBinding.bind(requireView())
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

    private fun initDataIR() {
        imageWidth = cameraHeight - tempHeight
        imageHeight = cameraWidth
        binding.temperatureView.setTextSize(SaveSettingUtil.tempTextSize)
        if (ScreenUtil.isPortrait(requireContext())) {
            bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
            binding.temperatureView.setImageSize(imageWidth, imageHeight,this@IRCorrectionFragment)
            rotateAngle = DeviceConfig.S_ROTATE_ANGLE
        } else {
            bitmap = Bitmap.createBitmap(imageHeight, imageWidth, Bitmap.Config.ARGB_8888)
            binding.temperatureView.setImageSize(imageHeight, imageWidth,this@IRCorrectionFragment)
            rotateAngle = DeviceConfig.ROTATE_ANGLE
        }
        binding.cameraView.setSyncimage(syncimage)
        binding.cameraView.bitmap = bitmap
        binding.cameraView.isDrawLine = false
        binding.temperatureView.setSyncimage(syncimage)
        binding.temperatureView.setTemperature(temperature)
        binding.temperatureView.isEnabled = false
        setViewLay()
        // sensor
        if (Usbcontorl.isload) {
            Usbcontorl.usb3803_mode_setting(1) //5V
            Log.w("123", "5V")
        }
        binding.temperatureView.clear()
        binding.temperatureView.temperatureRegionMode = REGION_MODE_CLEAN
    }

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
        }catch (e : Exception){
            Log.e("",e.message.toString())
        }
    }

    private fun startUSB(isRestart : Boolean) {
        context?.let {
            iruvc = IRUVCTC(cameraWidth, cameraHeight, context, syncimage,
                defaultDataFlowMode, object : ConnectCallback {
                    override fun onCameraOpened(uvcCamera: UVCCamera) {

                    }

                    override fun onIRCMDCreate(ircmd: IRCMD) {
                        Log.i(
                            TAG,
                            "ConnectCallback->onIRCMDCreate"
                        )
                        this@IRCorrectionFragment.ircmd = ircmd
                        // IRCMD
//                        ircmd.setPseudoColor(CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                            PseudocodeUtils.changePseudocodeModeByOld(pseudocolorMode))
                    }
                }, object : USBMonitorCallback {
                    override fun onAttach() {}
                    override fun onGranted() {}
                    override fun onConnect() {}
                    override fun onDisconnect() {}
                    override fun onDettach() {
                        activity?.finish()
                    }

                    override fun onCancel() {
                        activity?.finish()
                    }
                })
            iruvc!!.isRestart = isRestart
            iruvc!!.setImageSrc(image)
            iruvc!!.setTemperatureSrc(temperature)
            iruvc!!.setRotate(rotateAngle)
            iruvc!!.registerUSB()
        }
    }

    private fun restartUsbCamera() {
        if (iruvc != null) {
            iruvc!!.stopPreview()
            iruvc!!.unregisterUSB()
        }
        startUSB(true)
    }

    override fun onStart() {
        super.onStart()
        Log.w(TAG, "onStart")
        if (!isrun) {
            //,
            binding.temperatureView.postDelayed({
                pseudocolorMode = 3
                startUSB(false)
                startISP()
                binding.temperatureView.start()
                binding.cameraView.start()
                isrun = true
                configParam()
            },1500)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.w(TAG, "onStop")
        if (iruvc != null) {
            iruvc!!.stopPreview()
            iruvc!!.unregisterUSB()
        }
        imageThread?.interrupt()
        syncimage.valid = false
        binding.temperatureView.stop()
        binding.cameraView.stop()
        isrun = false
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "onDestroy")
        try {
            imageThread?.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "imageThread.join(): catch an interrupted exception")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun iruvctc(event: PreviewComplete) {
        dealY16ModePreviewComplete()
    }

    var frameReady = false;
    private fun dealY16ModePreviewComplete() {
        isConfigWait = false
        iruvc?.setFrameReady(true)
        frameReady = true
    }

    private fun setViewLay() {
        binding.thermalLay.post {
            if (ScreenUtil.isPortrait(requireContext())) {
                val params = binding.thermalLay.layoutParams
                params.width = ScreenUtil.getScreenWidth(requireContext())
                params.height = params.width * imageHeight / imageWidth
                binding.thermalLay.layoutParams = params
            } else {
                val params = binding.thermalLay.layoutParams
                params.height = binding.thermalLay.height
                params.width = params.height * imageHeight / imageWidth
                binding.thermalLay.layoutParams = params
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun cameraEvent(event: DeviceCameraEvent) {
        when (event.action) {
            100 -> {
                showLoadingDialog()
            }
            101 -> {
                lifecycleScope.launch {
                    delay(500)
                    isConfigWait = false
                    delay(1000)
                    dismissLoadingDialog()
                }
            }
        }
    }

    private var isConfigWait = true

    private fun configParam() {
        lifecycleScope.launch {
            isConfigWait = true
            while (isConfigWait) {
                delay(100)
            }
            val config = ConfigRepository.readConfig(false)
            val disChar = (config.distance * 128).toInt() //()
            val emsChar = (config.radiation * 128).toInt() //
            XLog.w("TPD_PROP DISTANCE:${disChar}, EMS:${emsChar}}")
            val timeMillis = 250L
            delay(timeMillis)
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_EMS,
                CommonParams.PropTPDParamsValue.NumberType(emsChar.toString())
            )
            delay(timeMillis)
            ircmd?.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_DISTANCE,
                CommonParams.PropTPDParamsValue.NumberType(disChar.toString())
            )
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2
            )
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2
            )
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2
            )
            delay(timeMillis)
            ircmd?.zoomCenterDown(
                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
                CommonParams.ZoomScaleStep.ZOOM_STEP2
            )
            iruvc?.let {
                withContext(Dispatchers.IO){
                    if (SaveSettingUtil.isAutoShutter) {
                        ircmd?.setPropAutoShutterParameter(
                            CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
                            CommonParams.PropAutoShutterParameterValue.StatusSwith.ON
                        )
                    }else{
                        ircmd?.setPropAutoShutterParameter(
                            CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
                            CommonParams.PropAutoShutterParameterValue.StatusSwith.OFF
                        )
                    }
                }
            }
            delay(timeMillis)
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_LEVEL_CONTRAST,
                CommonParams.PropImageParamsValue.NumberType(128.toString())
            )
            delay(timeMillis)
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_LEVEL_DDE,
                CommonParams.PropImageParamsValue.DDEType.DDE_2
            )
            delay(timeMillis)
            ircmd?.setPropImageParams(
                CommonParams.PropImageParams.IMAGE_PROP_ONOFF_AGC,
                CommonParams.PropImageParamsValue.StatusSwith.ON
            )
        }
    }


    suspend fun autoStart() {
        withContext(Dispatchers.IO){
            //            ToastUtils.showShort()
            // 1
            // 2
            CalibrationTools.autoShutter(irCmd = ircmd, false)
            XLog.w("："+"")
            // 3
//            CalibrationTools.shutter(irCmd = ircmd, syncImage = syncimage)
//            XLog.w(+)
            // 4
            delay(2000)
            XLog.w("："+"")
            CalibrationTools.stsSwitch(irCmd = ircmd, false)
            // 5
            CalibrationTools.pot(irCmd = ircmd!!, 1)
            XLog.w("："+"")
            // 6
            delay(5000)
            XLog.w("："+"")
            CalibrationTools.stsSwitch(irCmd = ircmd, true)
            delay(20000)
            XLog.w("："+"20000")
            // 11
//            CalibrationTools.shutter(irCmd = ircmd, syncImage = syncimage)
//            XLog.w(+)
            // 12
            delay(2000)
            CalibrationTools.stsSwitch(irCmd = ircmd, false)
            XLog.w("："+"")
            // 13
            CalibrationTools.pot(irCmd = ircmd!!, 1)
            // 14
            delay(5000)
            XLog.w("："+"")
            CalibrationTools.stsSwitch(irCmd = ircmd, true)
            // 17
            CalibrationTools.autoShutter(irCmd = ircmd, true)
            XLog.w("："+"")
        }
    }

}