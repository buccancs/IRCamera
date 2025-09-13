package com.topdon.module.thermal.ir.fragment

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.yt.jni.Usbcontorl
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.CommonUtils
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
import com.topdon.module.thermal.ir.activity.IRMonitorActivity
import com.topdon.module.thermal.ir.bean.SelectPositionBean
import com.topdon.module.thermal.ir.event.ThermalActionEvent
import com.topdon.module.thermal.ir.repository.ConfigRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
\1thermal imaging
 */
/**
 * I r monitor thermal fragment for thermal imaging components.
 * Handles specific UI sections and user interactions.
 */
class IRMonitorThermalFragment : BaseFragment(), ITsTempListener {
\1TextdataText：image+temperatureTextdata */
    protected var defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT

    private var ircmd: IRCMD? = null
    private var gainStatus = CommonParams.GainStatus.HIGH_GAIN

    // findViewById declarations
    private lateinit var temperatureView: TemperatureView
    private lateinit var thermalLay: FrameLayout
    private lateinit var cameraView: CameraView

    override fun initContentView() = R.layout.fragment_ir_monitor_thermal

    private var rotateAngle = 270 // 270
    private var ts_data_H: ByteArray? = null
    private var ts_data_L: ByteArray? = null
    private var isPick = false

    companion object {
        fun newInstance(isPick: Boolean): IRMonitorThermalFragment {
            val fragment = IRMonitorThermalFragment()
            val bundle = Bundle()
            bundle.putBoolean("isPick", isPick)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        // Initialize findViewById using view
        temperatureView = view!!.findViewById(R.id.temperatureView)
        thermalLay = view!!.findViewById(R.id.thermal_lay)
        cameraView = view!!.findViewById(R.id.cameraView)

        ts_data_H = CommonUtils.getTauData(context, "ts/TS001_H.bin")
        ts_data_L = CommonUtils.getTauData(context, "ts/TS001_L.bin")
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initDataIR()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.containsKey("isPick") == true)
            {
                isPick = requireArguments().getBoolean("isPick")
            }
    }

    override fun initData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun action(event: ThermalActionEvent) {
        temperatureView.isEnabled = true
        Log.w("123", "event:${event.action}"Test Data"123", "Test Data")
        }
\1Texttemperature measurement
        temperatureView.post {
            if (!temperaturerun) {
                temperaturerun = true
\1TextrenderingTextdisplay
                temperatureView.visibility = View.VISIBLE
            }
        }
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
                            "ConnectCallback->onIRCMDCreate"Test Data"Mini256", true)
                        ircmd!!.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
                        Log.d(TAG, "TPD_PROP_GAIN_SEL="Test Data"onStart"Test Data"onStop")
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
            Log.e(TAG, "imageThread.join(): catch an interrupted exception"Test Data"TextTPD_PROP DISTANCE:${disChar.toInt()}, EMS:${emsChar.toInt()}}"Test Data"Text: ${e.message}")
        }
        return tmp!!
    }

    /**
\1
     */
    private fun tempCorrect(
        temp: Float,
        gainStatus: CommonParams.GainStatus,
        tempInfo: Long,
    ): Float {
        return temp
    }
}
