package com.topdon.module.thermal.ir.activity

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
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
import com.infisense.usbir.utils.PseudocodeUtils
import com.infisense.usbir.utils.USBMonitorCallback
import com.infisense.usbir.view.ITsTempListener
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
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.bean.SelectPositionBean
import com.topdon.module.thermal.ir.event.MonitorSaveEvent
import com.topdon.module.thermal.ir.repository.ConfigRepository
import android.widget.TextView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.infisense.usbir.view.CameraPreView
import com.infisense.usbir.view.TemperatureView
import com.topdon.lib.core.ui.TitleView
import com.topdon.lib.ui.widget.MPChartView
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 温度实时监控
 */
class IRMonitorChartActivity : BaseActivity(),ITsTempListener {
    
    companion object {
        private const val TAG = "IRMonitorChartActivity"
    }

    /** 默认数据流模式：图像+温度复合数据 */
    protected var defaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT

    private var gainStatus = CommonParams.GainStatus.HIGH_GAIN
    private var isTS001 = false

    /**
     * 从上一界面传递过来的，当前选中的 点/线/面 信息.
     */
    private var selectBean: SelectPositionBean = SelectPositionBean()

    private var ircmd: IRCMD?= null
    private val bean = ThermalBean()
    private var ts_data_H: ByteArray? = null
    private var ts_data_L: ByteArray? = null
    override fun initContentView() = R.layout.activity_ir_monitor_chart

    override fun initView() {
        val titleView = findViewById<TitleView>(R.id.title_view)
        val monitorCurrentVol = findViewById<TextView>(R.id.monitor_current_vol)
        val monitorRealVol = findViewById<TextView>(R.id.monitor_real_vol)
        val monitorRealImg = findViewById<View>(R.id.monitor_real_img)
        val temperatureView = findViewById<TemperatureView>(R.id.temperatureView)
        val llTime = findViewById<LinearLayout>(R.id.ll_time)
        val cameraView = findViewById<CameraPreView>(R.id.cameraView)
        val mpChartView = findViewById<MPChartView>(R.id.mp_chart_view)
        val tvTime = findViewById<TextView>(R.id.tv_time)
        val thermalLay = findViewById<ConstraintLayout>(R.id.thermal_lay)
        
        titleView.setRightClickListener {
            recordJob?.cancel()
            lifecycleScope.launch {
                delay(200)
                finish()
            }
        }
        ts_data_H = CommonUtils.getTauData(this@IRMonitorChartActivity, "ts/TS001_H.bin")
        ts_data_L = CommonUtils.getTauData(this@IRMonitorChartActivity, "ts/TS001_L.bin")
        selectBean = intent.getParcelableExtra("select")!!

        monitorCurrentVol.text = getString(if (selectBean.type == 1) R.string.chart_temperature else R.string.chart_temperature_high)
        monitorRealVol.visibility = if (selectBean.type == 1) View.GONE else View.VISIBLE
        monitorRealImg.visibility = if (selectBean.type == 1) View.GONE else View.VISIBLE

        temperatureView.isEnabled = false
        temperatureView.setTextSize(SaveSettingUtil.tempTextSize)


        initDataIR()
    }

    override fun finish() {
        super.finish()
        EventBus.getDefault().post(MonitorSaveEvent())
    }

    private var showTask: Job? = null

    override fun initData() {
        if (showTask != null && showTask!!.isActive) {
            showTask!!.cancel()
            showTask = null
        }
        showTask = lifecycleScope.launch {
            var isFirstRead = true
            var errorReadCount = 0
            while (true) {
                delay(1000)
                val result: LibIRTemp.TemperatureSampleResult = when (selectBean.type) {
                    1 -> temperatureView.getPointTemp(selectBean.startPosition)
                    2 -> temperatureView.getLineTemp(Line(selectBean.startPosition, selectBean.endPosition))
                    else -> temperatureView.getRectTemp(selectBean.getRect())
                } ?: continue
                if (isFirstRead) {
                    if (result.maxTemperature > 200f || result.minTemperature < -200f) {
                        errorReadCount++
                        if (errorReadCount > 10) {
                            isFirstRead = false
                        }
                        continue
                    } else {
                        isFirstRead = false
                        lifecycleScope.launch(Dispatchers.Main) {
                            llTime.isVisible = true
                        }
                    }
                }
                if (result.maxTemperature >= -270f) {
                    val maxBigDecimal = BigDecimal.valueOf(tempCorrectByTs(result.maxTemperature).toDouble())
                    val minBigDecimal = BigDecimal.valueOf(tempCorrectByTs(result.minTemperature).toDouble())
                    bean.centerTemp = maxBigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                    bean.maxTemp = maxBigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                    bean.minTemp = minBigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                    bean.createTime = System.currentTimeMillis()
                    canUpdate = true//可以开始更新记录
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        isStop = false
        if (!isrun) {
            configParam()
            val temperatureView = findViewById<TemperatureView>(R.id.temperatureView)
            val cameraView = findViewById<CameraPreView>(R.id.cameraView)
            temperatureView.postDelayed({
                //初始配置,伪彩铁红
                try {
                    if (!isStop){
                        pseudoColorMode = 3
                        startUSB(false)
                        startISP()
                        temperatureView.start()
                        cameraView.start()
                        isrun = true
                        if (!isRecord){
                            recordThermal()//开始记录
                        }
                    }
                }catch (e:Exception){
                }
            }, 1500)
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val mpChartView = findViewById<MPChartView>(R.id.mp_chart_view)
        mpChartView.highlightValue(null) //关闭高亮点Marker
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private var isStop = false
    override fun onStop() {
        super.onStop()
        isStop = true
        if (iruvc != null) {
            iruvc!!.stopPreview()
            iruvc!!.unregisterUSB()
        }
        imageThread?.interrupt()
        syncimage.valid = false
        val temperatureView = findViewById<TemperatureView>(R.id.temperatureView)
        val cameraView = findViewById<CameraPreView>(R.id.cameraView)
        temperatureView.stop()
        cameraView.stop()
        isrun = false
    }

    override fun onDestroy() {
        super.onDestroy()
        recordJob?.cancel()
        try {
            imageThread?.join()
        } catch (e: InterruptedException) {
        }
    }

    override fun disConnected() {
        super.disConnected()
        finish()
    }

    private var isRecord = false
    private var timeMillis = 1000L //间隔1s
    private var canUpdate = false

    private var recordJob: Job? = null
    /**
     * 开始每隔1秒记录一个温度数据到数据库.
     */
    private fun recordThermal() {
        recordJob = lifecycleScope.launch(Dispatchers.IO) {
            isRecord = true
            val thermalId = TimeTool.showDateSecond()
            val startTime = System.currentTimeMillis()
            val typeStr = when (selectBean.type) {
                1 -> "point"
                2 -> "line"
                else -> "fence"
            }
            var time = 0L
            while (isRecord) {
                if (!isStop){
                    if (canUpdate) {
                        val entity = ThermalEntity()
                        entity.userId = SharedManager.getUserId()
                        entity.thermalId = thermalId
                        entity.thermal = NumberTools.to02f(bean.centerTemp)
                        entity.thermalMax = NumberTools.to02f(bean.maxTemp)
                        entity.thermalMin = NumberTools.to02f(bean.minTemp)
                        entity.type = typeStr
                        entity.startTime = startTime
                        entity.createTime = System.currentTimeMillis()
                        AppDatabase.getInstance().thermalDao().insert(entity)
                        time++
                        launch(Dispatchers.Main) {
                            mpChartView.addPointToChart(bean = entity, selectType = selectBean.type)
                        }
                        delay(timeMillis)
                    } else {
                        delay(100)
                    }
                    lifecycleScope.launch(Dispatchers.Main) {
                        tvTime.text = TimeTool.showVideoLongTime(System.currentTimeMillis() - startTime)
                    }
                }
            }
        }
    }


    private var imageThread: ImageThreadTC? = null
    private var bitmap: Bitmap? = null //不需要显示图像，可去掉
    private var iruvc: IRUVCTC? = null
    private val cameraWidth = 256
    private val cameraHeight = 384
    private val tempHeight = 192
    private var imageWidth = cameraWidth
    private var imageHeight = cameraHeight - tempHeight
    private val imageBytes = ByteArray(imageWidth * imageHeight * 2)
    private val temperatureBytes = ByteArray(imageWidth * imageHeight * 2)
    private val syncimage = SynchronizedBitmap()
    private var isrun = false
    private var pseudoColorMode = 0

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun irEvent(event: IRMsgEvent) {
        if (event.code == MsgCode.RESTART_USB) {
            restartUsbCamera()
        }
    }

    private var rotateAngle = 270

    /**
     * 初始数据
     *
     * 不做图像更新
     * 去掉cameraView
     * syncimage.valid = true
     */
    private fun initDataIR() {
        imageWidth = cameraHeight - tempHeight
        imageHeight = cameraWidth
        val temperatureView = findViewById<TemperatureView>(R.id.temperatureView)
        val cameraView = findViewById<CameraPreView>(R.id.cameraView)
        if (ScreenUtil.isPortrait(this)) {
            bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
            temperatureView.setImageSize(imageWidth, imageHeight,this@IRMonitorChartActivity)
            rotateAngle = DeviceConfig.S_ROTATE_ANGLE
        } else {
            bitmap = Bitmap.createBitmap(imageHeight, imageWidth, Bitmap.Config.ARGB_8888)
            temperatureView.setImageSize(imageHeight, imageWidth,this@IRMonitorChartActivity)
            rotateAngle = DeviceConfig.ROTATE_ANGLE
        }
        cameraView.setSyncimage(syncimage)
        cameraView.bitmap = bitmap
        temperatureView.setSyncimage(syncimage)
        temperatureView.setTemperature(temperatureBytes)
        setViewLay()
        // 某些特定客户的特殊设备需要使用该命令关闭sensor
        if (Usbcontorl.isload) {
            Usbcontorl.usb3803_mode_setting(1) //打开5V
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun iruvctc(event: PreviewComplete) {
        dealY16ModePreviewComplete()
    }

    private fun dealY16ModePreviewComplete() {
        dismissLoadingDialog()
        isConfigWait = false
        iruvc!!.setFrameReady(true)
        addTempLine()
    }

    /**
     * 图像信号处理
     */
    private fun startISP() {
        try {
            imageThread = ImageThreadTC(this@IRMonitorChartActivity, imageWidth, imageHeight)
            imageThread!!.setDataFlowMode(defaultDataFlowMode)
            imageThread!!.setSyncImage(syncimage)
            imageThread!!.setImageSrc(imageBytes)
            imageThread!!.setTemperatureSrc(temperatureBytes)
            imageThread!!.setBitmap(bitmap)
            imageThread!!.setRotate(rotateAngle)
            imageThread!!.start()
        }catch (e : Exception){
        }
    }


    /**
     * @param isRestart 是否是重启模组
     */
    private fun startUSB(isRestart: Boolean) {
        iruvc = IRUVCTC(cameraWidth, cameraHeight, this@IRMonitorChartActivity, syncimage,
            defaultDataFlowMode, object : ConnectCallback {
                override fun onCameraOpened(uvcCamera: UVCCamera) {

                }

                override fun onIRCMDCreate(ircmd: IRCMD) {
            XLog.d(
                TAG,
                        "ConnectCallback->onIRCMDCreate"
            )
                    this@IRMonitorChartActivity.ircmd = ircmd
                    // 需要等IRCMD初始化完成之后才可以调用
//                    ircmd.setPseudoColor(
//                        CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                        PseudocodeUtils.changePseudocodeModeByOld(pseudoColorMode))
                    val fwBuildVersionInfoBytes = ByteArray(50)
                    ircmd?.getDeviceInfo(
                        CommonParams.DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
                        fwBuildVersionInfoBytes
                    ) //ok
                    val value = IntArray(1)
                    val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
                    isTS001 = arm.contains("Mini256", true)
                    ircmd!!.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
                    gainStatus = if (value[0] == 1) {
                        // 当前机芯为高增益
                        CommonParams.GainStatus.HIGH_GAIN
                        // 等效大气透过率表
                    } else {
                        // 当前机芯为低增益
                        CommonParams.GainStatus.LOW_GAIN
                    }
                }
            }, object : USBMonitorCallback {
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
            })
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
    //配置
    private fun configParam() {
        lifecycleScope.launch {
            isConfigWait = true
            while (isConfigWait) {
                delay(100)
            }
            val config = ConfigRepository.readConfig(false)
            val disChar = (config.distance * 128).toInt() //距离(米)
            val emsChar = (config.radiation * 128).toInt() //发射率
            val timeMillis = 250L
            delay(timeMillis)
            //发射率
            ircmd!!.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_EMS,
                CommonParams.PropTPDParamsValue.NumberType(emsChar.toString())
            )
            delay(timeMillis)
            //距离
            ircmd!!.setPropTPDParams(
                CommonParams.PropTPDParams.TPD_PROP_DISTANCE,
                CommonParams.PropTPDParamsValue.NumberType(disChar.toString())
            )
            // 自动快门
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
                // 部分机型在关闭自动快门，初始会花屏
                withContext(Dispatchers.IO){
                    if (SaveSettingUtil.isAutoShutter) {
                        ircmd!!.setPropAutoShutterParameter(
                            CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
                            CommonParams.PropAutoShutterParameterValue.StatusSwith.ON
                        )
                    } else {
                        ircmd!!.setPropAutoShutterParameter(
                            CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
                            CommonParams.PropAutoShutterParameterValue.StatusSwith.OFF
                        )
                    }
                }
            }
            //复位对比度、细节
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

    /**
     * 绘制点线面
     */
    private fun addTempLine() {
        val temperatureView = findViewById<TemperatureView>(R.id.temperatureView)
        temperatureView.visibility = View.VISIBLE
        when (selectBean.type) {
            1 -> {
                //点
                temperatureView.addScalePoint(selectBean.startPosition)
                temperatureView.temperatureRegionMode = REGION_MODE_POINT
            }
            2 -> {
                //线
                temperatureView.addScaleLine(
                    Line(
                        selectBean.startPosition,
                        selectBean.endPosition
                    )
                )
                temperatureView.temperatureRegionMode = REGION_MODE_LINE
            }
            3 -> {
                //面
                temperatureView.addScaleRectangle(
                    Rect(
                        selectBean.startPosition!!.x,
                        selectBean.startPosition!!.y,
                        selectBean.endPosition!!.x,
                        selectBean.endPosition!!.y,
                    )
                )
                temperatureView.temperatureRegionMode = REGION_MODE_RECTANGLE
            }
        }
        temperatureView.drawLine()
    }

    private fun setViewLay() {
        val thermalLay = findViewById<ConstraintLayout>(R.id.thermal_lay)
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
        }
        return tmp!!
    }


    /**
     * 单点修正过程
     */
    private fun tempCorrect(
        temp: Float,
        gainStatus: CommonParams.GainStatus, tempInfo: Long
    ): Float {

        if (!isTS001) {
            //不是ts001不需要修正
            return temp
        }
        if (ts_data_H == null || ts_data_L == null) {
            return temp
        }
        val config = ConfigRepository.readConfig(false)
        config.radiation
        val paramsArray = floatArrayOf(
            temp, config.radiation, config.environment,
            config.environment, config.distance, 0.8f
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
            XLog.d(
                TAG,
            "temp correct, oldTemp = " + paramsArray[0] + " ems = " + paramsArray[1] + " ta = " + paramsArray[2] + " " +
                    "distance = " + paramsArray[4] + " hum = " + paramsArray[5] + " productType = ${CommonParams.ProductType.WN256_ADVANCED}" + " " +
                    "newtemp = " + newTemp
        )
        return newTemp
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun cameraEvent(event: DeviceCameraEvent) {
        when (event.action) {
            100 -> {
                //准备图像
                showCameraLoading()
            }
            101 -> {
                //显示图像
                dismissCameraLoading()
                addTempLine()
            }
        }
    }
}