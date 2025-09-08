//package com.topdon.module.thermal.ir.activity
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.pm.ActivityInfo
//import android.content.pm.PackageManager
//import android.content.res.Configuration
//import android.graphics.Bitmap
//import android.graphics.Paint
//import android.graphics.drawable.ColorDrawable
//import android.hardware.SensorManager
//import android.os.SystemClock
//import android.provider.Settings
//import android.util.Log
//import android.view.*
//import android.widget.*
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.text.HtmlCompat
//import androidx.core.view.drawToBitmap
//import androidx.core.view.isVisible
//import androidx.lifecycle.Observer
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.alibaba.android.arouter.facade.annotation.Route
//import com.alibaba.android.arouter.launcher.ARouter
//import com.blankj.utilcode.util.*
//import com.blankj.utilcode.util.ScreenUtils
//import com.elvishew.xlog.XLog
//import com.google.gson.Gson
//import com.hjq.permissions.OnPermissionCallback
//import com.hjq.permissions.Permission
//import com.hjq.permissions.XXPermissions
//import com.infisense.iruvc.ircmd.IRCMD
//import com.infisense.iruvc.ircmd.IRCMDType
//import com.infisense.iruvc.ircmd.IRUtils
//import com.infisense.iruvc.sdkisp.LibIRParse
//import com.infisense.iruvc.sdkisp.LibIRProcess
//import com.infisense.iruvc.utils.CommonParams
//import com.infisense.iruvc.utils.CommonParams.*
//import com.infisense.iruvc.utils.CommonUtils
//import com.infisense.iruvc.utils.DeviceType
//import com.infisense.iruvc.utils.SynchronizedBitmap
//import com.infisense.iruvc.uvc.ConnectCallback
//import com.infisense.iruvc.uvc.UVCCamera
//import com.infisense.usbir.camera.IRUVCTC
//import com.infisense.usbir.config.MsgCode
//import com.infisense.usbir.event.IRMsgEvent
//import com.infisense.usbir.event.PreviewComplete
//import com.infisense.usbir.thread.ImageThreadTC
//import com.infisense.usbir.utils.*
//import com.infisense.usbir.view.DragViewUtil
//import com.infisense.usbir.view.ITsTempListener
//import com.infisense.usbir.view.TemperatureView.*
//import com.topdon.lib.core.BaseApplication
//import com.topdon.lib.core.bean.AlarmBean
//import com.topdon.lib.core.bean.CameraItemBean
//import com.topdon.lib.core.bean.event.device.DeviceCameraEvent
//import com.topdon.lib.core.common.SharedManager
//import com.topdon.lib.core.common.SharedManager.getTemperature
//import com.topdon.lib.core.config.DeviceConfig
//import com.topdon.lib.core.config.ExtraKeyConfig
//import com.topdon.lib.core.config.RouterConfig
//import com.topdon.lib.core.ktbase.BaseActivity
//import com.topdon.lib.core.tools.*
//import com.topdon.lib.core.utils.CameraLiveDateUtil
//import com.topdon.lib.core.utils.ImageUtils
//import com.topdon.lib.core.widget.dialog.*
//import com.topdon.menu.MenuFirstTabView
//import com.topdon.lib.ui.config.CameraHelp
//import com.topdon.lib.ui.dialog.ThermalInputDialog
//import com.topdon.lib.ui.dialog.TipPreviewDialog
//import com.topdon.lib.ui.widget.CommSeekBar
//import com.topdon.lib.ui.widget.seekbar.OnRangeChangedListener
//import com.topdon.lib.ui.widget.seekbar.RangeSeekBar
//import com.topdon.libcom.AlarmHelp
//import com.topdon.libcom.dialog.ColorDialog
//import com.topdon.libcom.dialog.TempAlarmSetDialog
//import com.topdon.module.thermal.ir.KotlinEx.showChangePseudoDialog
//import com.topdon.module.thermal.ir.R
//import com.topdon.module.thermal.ir.adapter.CameraItemAdapter
//import com.topdon.module.thermal.ir.extension.countDownCoroutines
//import com.topdon.module.thermal.ir.frame.FrameStructTool
//import com.topdon.module.thermal.ir.repository.ConfigRepository
//import com.topdon.module.thermal.ir.video.VideoRecordFFmpeg
//import com.topdon.module.thermal.ir.view.TimeDownView
//import com.topdon.pseudo.activity.PseudoSetActivity
//import com.topdon.pseudo.bean.CustomPseudoBean
//import kotlinx.android.synthetic.main.activity_ir_gallery_edit.*
//import kotlinx.android.synthetic.main.activity_thermal_ir.*
//import kotlinx.android.synthetic.main.activity_thermal_ir.temperature_iv_input
//import kotlinx.android.synthetic.main.activity_thermal_ir.temperature_iv_lock
//import kotlinx.android.synthetic.main.activity_thermal_ir.temperature_seekbar
//import kotlinx.android.synthetic.main.activity_thermal_ir.thermal_recycler
//import kotlinx.android.synthetic.main.activity_thermal_ir.tv_temp_content
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.flow
//import org.greenrobot.eventbus.Subscribe
//import org.greenrobot.eventbus.ThreadMode
//import java.io.File
//import java.util.*
//
//
//@Route(path = RouterConfig.IR_MAIN)
//open class IRThermalActivity : BaseActivity(), ITsTempListener {
//
//    private var alphaPaint: Paint ?=null
//    private var tempAlarmSetDialog: TempAlarmSetDialog ?= null
//    private var isShowC: Boolean = false
//    private var isInit = true
//    private lateinit var orientationEventListener: OrientationEventListener
//    private var isRotation = true
//    private var isReverseRotation = true
//    private var mOrientation = 0
//    private var editMaxValue = Float.MAX_VALUE
//    private var editMinValue = Float.MIN_VALUE
//    private var realLeftValue = -1f
//    private var realRightValue = -1f
//    private var isFirst = true
//    private var popupWindow : PopupWindow ?= null
//    var isAutoCamera = false
//    var autoJob : Job ?= null
//    var alarmBean : AlarmBean  = if (SharedManager.isSaveSetting()){
//        val json = SharedManager.getTempAlarmMarkJson()
//        if (json.isNotEmpty()){
//            Gson().fromJson(json, AlarmBean::class.java)
//        }else{
//            AlarmBean()
//        }
//    }else{
//        AlarmBean()
//    }
//    var customPseudoBean = CustomPseudoBean.loadFromShared()
//    private var ts_data_H: ByteArray ?= null
//    private var ts_data_L: ByteArray ?= null
//    override fun initContentView() = R.layout.activity_thermal_ir
//
//    override fun initView() {
//
//        setTitleText(R.string.main_thermal)
//        setToolListener {
//            if (time_down_view.isRunning){
//                return@setToolListener
//            }
//            setResult(200)
//            finish()
//        }
//        setRightSecondImg(R.drawable.ic_info_svg) {
//            showInfo()
//        }
//        thermal_back_img.setOnClickListener {
//            if (time_down_view.isRunning){
//                return@setOnClickListener
//            }
//            setResult(200)
//            finish()
//        }
//        //===
//        isInit = true
//        mToolBar?.setBackgroundColor(blackColor)
//        BarUtils.setStatusBarColor(this, blackColor)
//        BarUtils.setNavBarColor(window, blackColor)
//        initRecycler()
//        thermal_tab.setOnItemListener(object : MenuFirstTabView.OnItemListener {
//            override fun selectItem(position: Int) {
//                ViewStubUtils.showViewStub(view_stub_camera,false,null)
//                popupWindow?.dismiss()
//                temperatureView.canTouch = position == 2
//                //[Chinese text]menu[Chinese text]
//                showRecycler(position)
//            }
//        })
//        temperature_seekbar.setIndicatorTextDecimalFormat("0.0")
//        updateTemperatureSeekBar(false,R.drawable.ic_edit_pseudo_lock_svg,"lock")//[Chinese text]
//        isShowC = getTemperature() == 1
//        temperatureView.listener = TempListener { max, min ,tempData->
//            realLeftValue = UnitTools.showUnitValue(min,isShowC)
//            realRightValue = UnitTools.showUnitValue(max,isShowC)
////            Log.w("temperature[Chinese text]","${max}--${min}//${realRightValue}--${realLeftValue}:::")
//            cl_seek_bar.changeData = true
//            this@IRThermalActivity.runOnUiThread {
//                if (!customPseudoBean.isUseCustomPseudo){
//                    //[Chinese text]mode
//                    try {
//                        temperature_seekbar.setRangeAndPro(UnitTools.showUnitValue(editMinValue,isShowC),
//                            UnitTools.showUnitValue(editMaxValue,isShowC),realLeftValue,realRightValue)
//                        if (editMinValue != Float.MIN_VALUE && editMaxValue != Float.MAX_VALUE){
//                            imageThread?.setLimit(
//                                editMaxValue, editMinValue,
//                                upColor, downColor) //[Chinese text]
//                        }
//                    }catch (e:Exception){
//                        Log.e("temperature[Chinese text]",e.message.toString())
//                    }
//                    try {
//                        if (isVideo){
//                            cl_seek_bar.updateBitmap()
//                        }
//                    }catch (e:Exception){
//                        Log.w("[Chinese text]:","${e.message}")
//                    }
//                    try {
//                        AlarmHelp.getInstance(application).alarmData(max,min,temp_bg)
//                        tv_temp_content.text = "Max:${UnitTools.showC(max,isShowC)}\nMin:${UnitTools.showC(min,isShowC)}"
//                    }catch (e:Exception){
//                        Log.e("temperature[Chinese text]",e.message.toString())
//                    }
//                }else{
//                    //[Chinese text]
//                    try {
//                        tv_temp_content.text = "Max:${UnitTools.showC(max,isShowC)}\nMin:${UnitTools.showC(min,isShowC)}"
//                    }catch (e:Exception){
//                        Log.e("temperature[Chinese text]",e.message.toString())
//                    }
//                }
//                try {
//                    AlarmHelp.getInstance(application).alarmData(max,min,temp_bg)
//                }catch (e:Exception){
//                    Log.e("temperature[Chinese text]",e.message.toString())
//                }
//            }
//
//        }
//
//        pop_time_lay.visibility = View.GONE
//        cameraPreview.visibility = View.INVISIBLE
//        initOrientationEventListener()
//        setCameraDataListener()
//        addTemperatureListener()
//        cameraView.post {
//            cameraView.postDelayed({
//                if (CameraLiveDateUtil.getInstance().isOpenCameraPreviewState(this@IRThermalActivity)){
//                    cameraPreviewConfig(false)
//                }
//            },500)
//        }
//        if (ScreenTool.isP()){
//            cl_seek_bar.setPadding(0,SizeUtils.dp2px(40f),0,SizeUtils.dp2px(40f))
//        }
//        DragViewUtil.registerDragAction(zoomView)
//    }
//
//    var isTouchSeekBar = false
//    private val pseudoSetResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        if (it.resultCode == RESULT_OK) {
//            updateImageAndSeekbarColorList(it.data?.getParcelableExtra(ExtraKeyConfig.CUSTOM_PSEUDO_BEAN) ?: CustomPseudoBean())
//            customPseudoBean.saveToShared()
//        }
//    }
//
//    //[Chinese text]
//    private fun updateImageAndSeekbarColorList(customPseudoBean: CustomPseudoBean?){
//        customPseudoBean?.let {
//            temperature_seekbar.setColorList(customPseudoBean.getColorList()?.reversedArray()?:null)
//            imageThread?.setColorList(customPseudoBean.getColorList(),
//                customPseudoBean.isUseGray,it.maxTemp,it.minTemp)
//            if (it.isUseCustomPseudo){
//                temperature_iv_lock.visibility = View.INVISIBLE
//                tv_temp_content.visibility = View.VISIBLE
//                updateTemperatureSeekBar(false,R.drawable.ic_edit_pseudo_lock_svg,"lock")//[Chinese text]
//                temperature_seekbar.setRangeAndPro(UnitTools.showUnitValue(it.minTemp),
//                    UnitTools.showUnitValue(it.maxTemp),UnitTools.showUnitValue(it.minTemp),
//                    UnitTools.showUnitValue(it.maxTemp))
//                thermal_recycler.setPseudoColor(-1)
//                temperature_iv_input.setImageResource(R.drawable.ir_model)
//            }else{
//                temperature_iv_lock.visibility = View.VISIBLE
//                thermal_recycler.setPseudoColor(pseudocolorMode)
//                if (this.customPseudoBean.isUseCustomPseudo){
//                    setDefLimit()
//                }
//                tv_temp_content.visibility = View.GONE
//                temperature_iv_input.setImageResource(R.drawable.ic_color_edit)
//            }
//            this.customPseudoBean = it
//        }
//    }
//
//    private fun addTemperatureListener() {
//
//        temperature_iv_lock.setOnClickListener {
//            if (temperature_iv_lock.visibility != View.VISIBLE){
//                return@setOnClickListener
//            }
//            if (temperature_iv_lock.contentDescription == "lock") {
//                updateTemperatureSeekBar(true,R.drawable.ic_edit_pseudo_unlock_svg,"unlock")//[Chinese text]
//            } else {
//                setDefLimit()
//                updateTemperatureSeekBar(false,R.drawable.ic_edit_pseudo_lock_svg,"lock")//[Chinese text]
//            }
//        }
//        temperature_iv_input.setOnClickListener {
//            pseudoSetResult.launch(Intent(this,PseudoSetActivity::class.java))
//        }
//        temperature_seekbar.setOnRangeChangedListener(object : OnRangeChangedListener{
//            override fun onRangeChanged(
//                view: RangeSeekBar?,
//                leftValue: Float,
//                rightValue: Float,
//                isFromUser: Boolean
//            ) {
//                if (isTouchSeekBar){
//                    editMinValue = UnitTools.showToCValue(leftValue)
//                    editMaxValue = UnitTools.showToCValue(rightValue)
//                    imageThread?.setLimit(
//                        editMaxValue,
//                        editMinValue,
//                        upColor, downColor) //[Chinese text]
//                    CameraLiveDateUtil.getInstance().saveEditMaxMinValue(editMaxValue,editMinValue)
//                }
//            }
//
//            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
//                isTouchSeekBar = true
//            }
//
//            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
//                isTouchSeekBar = false
//            }
//
//        })
//
//    }
//
//
//
//    /**
//     * [Chinese text]high[Chinese text]low[Chinese text]
//     */
//    fun setDefLimit(){
//        editMaxValue = Float.MAX_VALUE
//        editMinValue = Float.MIN_VALUE
//        CameraLiveDateUtil.getInstance().saveEditMaxMinValue(editMaxValue,editMinValue)
//        imageThread?.setLimit(editMaxValue, editMinValue, upColor, downColor) //[Chinese text]
//        temperature_seekbar.setRangeAndPro(downValue, upValue,realLeftValue,realRightValue) //[Chinese text]
//    }
//
//    private fun updateTemperatureSeekBar(isEnabled: Boolean,resource: Int,content: String){
//        temperature_seekbar.isEnabled = isEnabled
//        temperature_seekbar.drawIndPath(isEnabled)
//        temperature_iv_lock.setImageResource(resource)
//        temperature_iv_lock.contentDescription = content
//        if(isEnabled){
//            temperature_seekbar.leftSeekBar.indicatorBackgroundColor = ContextCompat.getColor(this, R.color.orgen)
//            temperature_seekbar.rightSeekBar.indicatorBackgroundColor = ContextCompat.getColor(this, R.color.orgen)
//            temperature_seekbar.invalidate()
//        }else{
//            temperature_seekbar.leftSeekBar.indicatorBackgroundColor = ContextCompat.getColor(this, R.color.app_color_transparent)
//            temperature_seekbar.rightSeekBar.indicatorBackgroundColor = ContextCompat.getColor(this, R.color.app_color_transparent)
//            temperature_seekbar.invalidate()
//        }
//    }
//
//    private fun initOrientationEventListener(){
//        orientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
//            override fun onOrientationChanged(orientation: Int) {
//                Log.w("[Chinese text]2: ", "onOrientationChanged: $orientation")
//                if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
//                    return
//                }
//                startOrientation()
//                if(mOrientation == 1){
//                   return
//                }
//                requestedOrientation = if ((orientation in 315..360) || (orientation in 0..45)) {
//                    if(isRotation && rotateAngle != 270){
//                        rotateAngle = 270
//                        updateRotateAngle(rotateAngle)
//                        isRotation = !isRotation
//                        isReverseRotation = true
//                        cameraPreview?.setRotation(false)
//                    }
//                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                } else (if(orientation in 135..225) {
//                    if(isReverseRotation && rotateAngle!=90){
//                        rotateAngle = 90
//                        updateRotateAngle(rotateAngle)
//                        isReverseRotation = !isReverseRotation
//                        isRotation = true
//                        cameraPreview?.setRotation(true)
//
//                    }
//                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
//                } else{
//                    isRotation = true
//                    isReverseRotation = true
//                    ActivityInfo.SCREEN_ORIENTATION_LOCKED
//                })
//        }
//      }
//    }
//
//    private fun updateRotateAngle(rotateAngle: Int){
//        // [Chinese text]limitsettings
//        imageThread?.setLimit(
//            editMaxValue,
//            editMinValue,
//            upColor, downColor) //[Chinese text]
//        lifecycleScope.launch(Dispatchers.IO) {
//            launch(Dispatchers.Main) {
//                thermal_recycler.rotateStats = 411
//            }
//            Log.w("123", "[Chinese text]: $rotateAngle")
//            temperatureView?.clear()
//            temperatureView?.temperatureRegionMode = REGION_MODE_CENTER
//            setRotate(rotateAngle)
//            delay(100)
//            launch(Dispatchers.Main) {
//                thermal_recycler.rotateStats = 410
//                thermal_recycler.setSecondDefault()
//            }
//        }
//    }
//
//    override fun initData() {
//        initDataIR()
//        AlarmHelp.getInstance(this).updateData()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (!BaseApplication.instance.isConnected()){
//            DeviceTools.isConnect()
//        }
//        AlarmHelp.getInstance(this).onResume()
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        startOrientation()
//    }
//
//
//    override fun onPause() {
//        super.onPause()
//        AlarmHelp.getInstance(this).pause()
//        isAutoCamera = false
//        autoJob?.cancel()
//        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        orientationEventListener?.disable()
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        startOrientation()
//    }
//
//    private fun startOrientation(){
//        orientationEventListener?.enable()
//        mOrientation = if(Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION)==0){
//    //  orientationEventListener.disable()
//            1
//        }else {
//    //  orientationEventListener.enable()
//            2
//        }
//        Log.w("[Chinese text]: ", "mOrientation: $mOrientation")
//    }
//
//    private fun initRecycler() {
//        thermal_recycler.cameraListener = {
////            closeCameraPreviewConfig()
//            setCamera(it)
//        }
//        thermal_recycler.galleryImgListener = {
//            lifecycleScope.launch {
//                if (isVideo){
//                    videoRecord?.stopRecord()
//                    isVideo = false
//                    videoTimeClose()
//                    delay(500)
//                }
//                ARouter.getInstance().build(RouterConfig.IR_GALLERY).navigation()
//            }
//        }
//        thermal_recycler.fencelistener = {
////            closeCameraPreviewConfig()
//            setTemp(it)
//        }
//        thermal_recycler.colorListener = { _, it ->
//            if (customPseudoBean!=null && customPseudoBean.isUseCustomPseudo){
//                showChangePseudoDialog(positiveClassBack = {
//                    customPseudoBean.isUseCustomPseudo = false
//                    customPseudoBean.saveToShared()
//                    setPColor(it)
//                    setDefLimit()
//                    updateImageAndSeekbarColorList(customPseudoBean)
//                },cancelClassBack = {
//                    thermal_recycler.setPseudoColor(pseudocolorMode)
//                })
//            }else{
//                setPColor(it)
//            }
//        }
//        thermal_recycler.settingListener = {
//            setSetting(it)
//        }
//        thermal_recycler.temperatureListener = {
////            closeCameraPreviewConfig()
//            setTemperature(it)
//        }
//        thermal_recycler.menuCameraListener = {
//            setSetting(it)
//        }
//        thermal_recycler.cameraStatsChangeListener = {
//            autoJob?.cancel()
//        }
//        cameraPreview.cameraPreViewCloseListener={
//            closeCameraPreviewConfig()
//        }
//    }
//
//    var showTask: Job? = null
//
//    fun showRecycler(select: Int) {
//
//        thermal_recycler.selectPosition(select)
//
//        if (select == 4 && !isOpenPreview) {
//            thermal_recycler.cameraPreviewStats = 450
////            lifecycleScope.launch(Dispatchers.Main) {
////                delay(100)
////            }
//        }
//    }
//
//    private val strBuilder = StringBuilder()
//
//    private fun setCamera(code: Int) {
//        when (code) {
//            1002 -> {
//                if (isVideo){
//                    centerCamera()
//                    return
//                }
//                if (CameraLiveDateUtil.getInstance().getCameraSBeanData().delayTime > 0){
//                    autoJob?.cancel()
//                }
//                if (time_down_view.isRunning){
//                    time_down_view.cancel()
//                    updateDelayView()
//                }else{
//                    if (time_down_view.downTimeWatcher == null){
//                        time_down_view.setOnTimeDownListener(object :TimeDownView.DownTimeWatcher{
//                            override fun onTime(num: Int) {
//                                updateDelayView()
//                            }
//                            override fun onLastTime(num: Int) {
//                            }
//                            override fun onLastTimeFinish(num: Int) {
//                                updateDelayView()
//                                centerCamera()
//                            }
//                        })
//                    }
//                    time_down_view.downSecond( CameraLiveDateUtil.getInstance().getCameraSBeanData().delayTime)
//                }
//            }
//            1003 -> {
//                //switchmode
//                settingCamera()
//            }
//        }
//    }
//
//    /**
//     * [Chinese text]UI
//     */
//    fun updateDelayView(){
//        try {
//            if (time_down_view.isRunning){
//                lifecycleScope.launch(Dispatchers.Main) {
//                    thermal_recycler.showDelayActive()
//                }
//            }else{
//                lifecycleScope.launch(Dispatchers.Main) {
//                    thermal_recycler.refreshImg()
//                }
//            }
//        }catch (e:Exception){
//            Log.e("line[Chinese text]",e.message.toString())
//        }
//    }
//
//    //temperaturemeasurement
//    private fun setTemp(code: Int) {
//        temperatureView?.canTouch = true
//        when (code) {
//            1 -> {
//                //point
//                temperatureView?.visibility = View.VISIBLE
//                temperatureView?.temperatureRegionMode = REGION_MODE_POINT
//                showCross(true)
//            }
//            2 -> {
//                //line
//                temperatureView?.visibility = View.VISIBLE
//                temperatureView?.temperatureRegionMode = REGION_MODE_LINE
//                showCross(true)
//            }
//            3 -> {
//                //[Chinese text]
//                temperatureView?.visibility = View.VISIBLE
//                temperatureView?.temperatureRegionMode = REGION_MODE_RECTANGLE
//                showCross(true)
//            }
//            4 -> {
//                //[Chinese text] temperature[Chinese text]
//                addLimit()
//            }
//            5 -> {
//                //[Chinese text]
//                temperatureView?.visibility = View.VISIBLE
//                temperatureView?.temperatureRegionMode = REGION_MODE_CENTER
//                showCross(true)
//            }
//            6 -> {
//                //[Chinese text]
//                temperatureView?.clear()
//                temperatureView?.visibility = View.INVISIBLE
//                temperatureView?.temperatureRegionMode = REGION_MODE_CLEAN
//                showCross(false)
//            }
//            -1 ->{
//                temperatureView?.canTouch = false
//            }
//            -2 -> {
//                temperatureView?.clear()
//                temperatureView?.visibility = View.VISIBLE
//                temperatureView?.temperatureRegionMode = REGION_MODE_CENTER
//                showCross(true)
//            }
//        }
//
//    }
//
//    private fun showCross(boolean: Boolean){
//        if(cameraView != null){
//            cameraView.setShowCross(boolean)
//        }
//    }
//
//    //settings[Chinese text]
//    private fun setPColor(code: Int) {
//        pseudocolorMode = code
//        temperature_seekbar.setPseudocode(pseudocolorMode)
//        /**
//         * settings[Chinese text][set pseudocolor]
//         * [Chinese text]implement([Chinese text],settings[Chinese text])
//         */
//        imageThread?.pseudocolorMode = pseudocolorMode//settings[Chinese text]
////        ircmd!!.setPseudoColor(PreviewPathChannel.PREVIEW_PATH0, PseudocodeUtils.changePseudocodeModeByOld(pseudocolorMode))
//        CameraLiveDateUtil.getInstance().savePseudoColorMode(pseudocolorMode)
//        thermal_recycler.setPseudoColor(code)
//    }
//    private var initRotate = 0//[Chinese text]
//    private var correctRotate = 0//[Chinese text]
//    private var rotateAngle = DeviceConfig.ROTATE_ANGLE //[Chinese text]0
//    private var defaultIsPortrait = DeviceConfig.IS_PORTRAIT //[Chinese text]
//    private fun setSetting(code: Int) {
//        when (code) {
//            CameraHelp.TYPE_SET_ROTATE -> {
//                // [Chinese text]
//                popupWindow?.dismiss()
//                setRotateAction()
//            }
//            CameraHelp.TYPE_SET_ParamLevelDde -> {
//                // [Chinese text]
//                setParamLevelDde()
//            }
//            CameraHelp.TYPE_SET_ParamLevelContrast -> {
//                // [Chinese text]
//                setParamLevelContrast()
//            }
//            CameraHelp.TYPE_SET_PSEUDOCOLOR -> {
//                // [Chinese text]
//                setPseudoColor()
//            }
//            CameraHelp.TYPE_SET_PREVIEWCONFIG -> {
//                // [Chinese text]in progress[Chinese text], [Chinese text]dual light
//                cameraPreviewConfig(true)
//            }
//            CameraHelp.TYPE_SET_LIMIT -> {
//                // [Chinese text]
//                addLimit()
//            }
//            CameraHelp.TYPE_SET_IR -> {
//                // [Chinese text]
//                closeCameraPreviewConfig()
//            }
//            CameraHelp.TYPE_SET_RH -> {
//                // [Chinese text]
//                if (!isOpenPreview && thermal_recycler.cameraAlphaStats != 471){
//                    //[Chinese text]dual light
//                    cameraPreviewConfig(false)
//                }
//                setCameraAlpha()
//            }
//            CameraHelp.TYPE_SET_ZOOM -> {
//                // [Chinese text]
//                setTemp(-2)//[Chinese text]pointline[Chinese text]
//                setZoom()
//            }
//            CameraHelp.TYPE_SET_ALARM -> {
//                // [Chinese text]
//                if(null == tempAlarmSetDialog){
//                    tempAlarmSetDialog = TempAlarmSetDialog(this,alarmBean,supportFragmentManager)
//                    tempAlarmSetDialog?.onSaveListener = {low: Float?, high: Float?, ringtone: Int?,alarmBean : AlarmBean ->
//                        if (tempAlarmSetDialog?.isOpenAlarm() == true){
//                            thermal_recycler.alarmStats = 501
//                        }else{
//                            thermal_recycler.alarmStats = 500
//                        }
//                        this@IRThermalActivity.alarmBean = alarmBean
//                        imageThread?.alarmBean = alarmBean
//                    }
//                }
//                tempAlarmSetDialog?.show()
//            }
//            CameraHelp.TYPE_SET_COLOR -> {
//                thermal_recycler.textColorStats = 491
//                // [Chinese text]
//                val colorDialog = ColorDialog.newInstance(CameraLiveDateUtil.getInstance().getTextColor())
//                colorDialog.positiveEvent = { color ->
//                    CameraLiveDateUtil.getInstance().saveTextColor(color)
//                    temperatureView?.setLinePaintColor(color)
//                }
//                colorDialog.cancelEvent ={
//                    thermal_recycler.textColorStats = 490
//                }
//                colorDialog.show(supportFragmentManager,"")
//            }
//            CameraHelp.TYPE_SET_TURNOVER -> {
//                // 180[Chinese text]
//                if (rotateAngle == 90){
//                    rotateAngle  = 270
//                    thermal_recycler.rotationStats = 510
//                }else{
//                    rotateAngle  = 90
//                    thermal_recycler.rotationStats = 511
//                }
//                updateRotateAngle(rotateAngle)
////                setRotateAction()
//            }
//            CameraHelp.TYPE_SET_MIRROR -> {
//                // [Chinese text]
//                openMirror = !openMirror
//                if (openMirror){
//                    ircmd?.setPropImageParams(
//                        PropImageParams.IMAGE_PROP_SEL_MIRROR_FLIP,
//                        PropImageParamsValue.MirrorFlipType.ONLY_FLIP
//                    )
//                    thermal_recycler.mirrorStats = 521
//                }else{
//                    thermal_recycler.mirrorStats = 520
//                    ircmd?.setPropImageParams(
//                        PropImageParams.IMAGE_PROP_SEL_MIRROR_FLIP,
//                        PropImageParamsValue.MirrorFlipType.NO_MIRROR_FLIP
//                    )
//                }
//            }
//        }
//    }
//    var openMirror : Boolean = false
////    var cameraAlpha = CameraLiveDateUtil.getInstance().getCameraSBeanData().irSettingBean.alpha
//    var cameraAlpha = 50
//    private fun setCameraAlpha() {
//        if (thermal_recycler.cameraAlphaStats == 471){
//            popupWindow?.dismiss()
//            return
//        }
//        popupWindow?.dismiss()
//        thermal_recycler.cameraAlphaStats = 471
//        popupWindow = PopupWindow(this)
//        val contentView = LayoutInflater.from(this).inflate(R.layout.layout_camera_seek_bar_t,null)
//        popupWindow?.contentView = contentView
//        popupWindow?.isFocusable = false
//        popupWindow?.animationStyle = R.style.SeekBarAnimation
//        popupWindow?.width = WindowManager.LayoutParams.MATCH_PARENT
//        popupWindow?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        popupWindow?.setBackgroundDrawable(ColorDrawable(0))
//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        val contentHeight = contentView.measuredHeight
//        val seekBar = contentView?.findViewById<CommSeekBar>(R.id.seek_bar)
//        val tvProcess = contentView?.findViewById<TextView>(R.id.tv_value)
//        seekBar?.progress = cameraAlpha
//        tvProcess?.text = "${cameraAlpha}%"
//        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(
//                seekBar: SeekBar?,
//                progress: Int,
//                fromUser: Boolean
//            ) {
//                tvProcess?.text = "${progress}%"
//                cameraAlpha = seekBar!!.progress
//                CameraLiveDateUtil.getInstance().saveAlpha(cameraAlpha)
//                cameraPreview?.setCameraAlpha(cameraAlpha / 100.0f)
//            }
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//            }
//
//        })
//        popupWindow?.setOnDismissListener {
//            thermal_recycler.cameraAlphaStats = 470
//        }
//        //[Chinese text]
//        popupWindow?.showAsDropDown(thermal_lay, 0, getPopupWindowY(contentHeight), Gravity.NO_GRAVITY)
//    }
//
//
//    private fun setTemperature(select: Int) {
//        CameraLiveDateUtil.getInstance().saveTemperatureConfig(select)
//        lifecycleScope.launch {
//            when (select) {
//                CameraItemBean.TYPE_TMP_ZD -> {
//                    //[Chinese text]
//                    autoConfig()
//                }
//                CameraItemBean.TYPE_TMP_C -> {
//                    //low[Chinese text]
//                    if (gainSelChar == 1) {
//                        return@launch
//                    }
//                    showCameraLoading()
//                    iruvc?.auto_gain_switch = false
//                    launch(Dispatchers.IO) {
//                        ircmd?.setPropTPDParams(
//                            PropTPDParams.TPD_PROP_GAIN_SEL,
//                            PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH
//                        )
//                        gainSelChar = 1
//                        setTsBin()
//                    }
//                    delay(4000)
//                    dismissCameraLoading()
//
//                }
//                CameraItemBean.TYPE_TMP_H -> {
//                    //high[Chinese text]
//                    if (gainSelChar == 0) {
//                        return@launch
//                    }
//                    showCameraLoading()
//                    iruvc?.auto_gain_switch = false
//                    launch(Dispatchers.IO) {
//                        ircmd?.setPropTPDParams(
//                            PropTPDParams.TPD_PROP_GAIN_SEL,
//                            PropTPDParamsValue.GAINSELStatus.GAIN_SEL_LOW
//                        )
//                        gainSelChar = 0
//                        setTsBin()
//                    }
//                    delay(4000)
//                    dismissCameraLoading()
//                }
//            }
//        }
//    }
//
//    private var imageThread: ImageThreadTC? = null
//    private var bitmap: Bitmap? = null
//    private var iruvc: IRUVCTC? = null
//    private val cameraWidth = 256
//    private val cameraHeight = 384
//    private val tempHeight = 192
//    private var imageWidth = cameraWidth
//    private var imageHeight = cameraHeight - tempHeight
//    private val imageBytes = ByteArray(imageWidth * imageHeight * 2) //[Chinese text]
//    private val temperatureBytes = ByteArray(imageWidth * imageHeight * 2) //temperature[Chinese text]
//    private val imageEditBytes = ByteArray(imageWidth * imageHeight * 4) //[Chinese text]
//    private val syncimage = SynchronizedBitmap()
//    private var isrun = false
//    private var pseudocolorMode = CameraLiveDateUtil.getInstance().getCameraSBeanData().irSettingBean.pseudoColorMode
//    private var temperaturerun = false
//    private lateinit var tau_data: ByteArray
//    private var tempinfo: Long = 0
//    private var cameraSBean = CameraLiveDateUtil.getInstance().getCameraSBeanData()
//
//    //highlow[Chinese text] 1:low[Chinese text] 0: high[Chinese text]
//    private var gainSelChar: Int = -1
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun irEvent(event: IRMsgEvent) {
//        if (event.code == MsgCode.RESTART_USB) {
//            //
//            startUSB(true)
//        }
//    }
//
//    /**
//     * [Chinese text]listener[Chinese text]
//     */
//    fun setCameraDataListener(){
//        //[Chinese text]
//        CameraLiveDateUtil.getInstance().cameraIRConfigLiveDate.observe(this, Observer { cameraIt ->
//            videoRecord?.updateAudioState(cameraSBean.openAudioRecord)
//            cameraSBean = cameraIt
//        })
//    }
//
//
//
//    /**
//     * [Chinese text]
//     */
//    private fun initDataIR() {
//        imageWidth = cameraHeight - tempHeight
//        imageHeight = cameraWidth
//        if (ScreenUtils.isPortrait()) {
//            bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
//            temperatureView?.setImageSize(imageWidth, imageHeight,this@IRThermalActivity)
//            defaultIsPortrait = DeviceConfig.S_IS_PORTRAIT
//            rotateAngle = DeviceConfig.S_ROTATE_ANGLE
//            initRotate = 270
//            correctRotate = 270
//        } else {
//            bitmap = Bitmap.createBitmap(imageHeight, imageWidth, Bitmap.Config.ARGB_8888)
//            temperatureView?.setImageSize(imageHeight, imageWidth,this@IRThermalActivity)
//            defaultIsPortrait = DeviceConfig.IS_PORTRAIT
//            rotateAngle = DeviceConfig.ROTATE_ANGLE
//            initRotate = 270
//            correctRotate = 0
//        }
//        cameraView.setSyncimage(syncimage)
//        cameraView.bitmap = bitmap
////        temperatureView!!.setImageSize(imageWidth, imageHeight)
//        temperatureView?.setSyncimage(syncimage)
//        temperatureView?.setTemperature(temperatureBytes)
//        temperatureView.nowZoomLevel = nowZoomLevel
//        setViewLay(defaultIsPortrait)
////        temperatureView.setTemperatureRegionMode(TemperatureView.FOCUSABLES_TOUCH_MODE)
//        // [Chinese text]sensor
////        if (Usbcontorl.isload) {
////            Usbcontorl.usb3803_mode_setting(1) //[Chinese text]5V
////            XLog.w("[Chinese text]5V")
////        }
//        //[Chinese text]
//        temperatureView?.post {
//            if (!temperaturerun) {
//                temperaturerun = true
//                //[Chinese text]
//                temperatureView?.visibility = View.VISIBLE
//                temperatureView?.postDelayed({
//                    temperatureView?.temperatureRegionMode = REGION_MODE_CENTER//[Chinese text]
//                }, 1000)
//            }
//        }
//    }
//
//    /**
//     * @param isPortrait    true: [Chinese text]
//     */
//    private fun setViewLay(isPortrait: Boolean) {
//        thermal_lay.post {
//            val params = thermal_lay.layoutParams
//            if (ScreenUtils.isPortrait()) {
//                // [Chinese text]
//                if (isPortrait) {
//                    params.width = ScreenUtils.getAppScreenWidth()
//                    params.height = params.width * imageHeight / imageWidth
//                } else {
//                    params.width = ScreenUtils.getAppScreenWidth()
//                    params.height = params.width * imageWidth / imageHeight
//                }
//            } else {
//                // [Chinese text]
//                if (isPortrait) {
//                    //[Chinese text]
//                    params.height = thermal_lay.height
//                    params.width = params.height * imageWidth / imageHeight
//                } else {
//                    //[Chinese text]
//                    params.height = thermal_lay.height
//                    params.width = params.height * imageHeight / imageWidth
//                }
//            }
//            Log.w("123", "set imageWidth = ${imageWidth}, imageHeight = ${imageHeight}")
//            Log.w("123", "set width = ${params.width}, height = ${params.height}")
//            thermal_lay.layoutParams = params
//            if (isPortrait){
//                zoomView.setImageSize(imageHeight, imageWidth,params.width,params.height)
//            }else{
//                zoomView.setImageSize(imageWidth, imageHeight,params.width,params.height)
//            }
//        }
//    }
//
//    /**
//     * [Chinese text]
//     */
//    private fun startISP() {
//        try {
//            imageThread = ImageThreadTC(this@IRThermalActivity, imageWidth, imageHeight)
//            imageThread?.setDataFlowMode(defaultDataFlowMode)
//            imageThread?.setSyncImage(syncimage)
//            imageThread?.setColorList(customPseudoBean.getColorList(),customPseudoBean.isUseGray,
//                customPseudoBean.maxTemp,customPseudoBean.minTemp)
//            imageThread?.setImageSrc(imageBytes)
//            imageThread?.pseudocolorMode = pseudocolorMode
//            imageThread?.setTemperatureSrc(temperatureBytes)
//            imageThread?.setBitmap(bitmap)
//            imageThread?.setRotate(rotateAngle)
//            imageThread?.setRotate(true)
//            imageThread?.alarmBean = alarmBean
//            imageThread?.start()
//        }catch (e : Exception){
//            Log.e("[Chinese text]line[Chinese text]",e.message.toString())
//        }
//    }
//
//    private var uvcCamera: UVCCamera? = null
//    private var defaultDataFlowMode: DataFlowMode? = DataFlowMode.IMAGE_AND_TEMP_OUTPUT
//    private var isUseIRISP = true
//    // [Chinese text]GPU[Chinese text]
//    private var isUseGPU = false
//
//    private var ircmd: IRCMD? = null
//    private var isCMDDataComplete = false
//
//
//    /**
//     * @param isRestart [Chinese text]
//     */
//    private fun startUSB(isRestart: Boolean) {
//        showCameraLoading()
//        //
//        iruvc = IRUVCTC(cameraWidth, cameraHeight, this@IRThermalActivity, syncimage,
//            defaultDataFlowMode, isUseIRISP, isUseGPU, object : ConnectCallback {
//                override fun onCameraOpened(uvcCamera: UVCCamera) {
//                    Log.i(
//                        TAG,
//                        "ConnectCallback->onCameraOpened"
//                    )
//                    this@IRThermalActivity.uvcCamera = uvcCamera
//                }
//
//                override fun onIRCMDCreate(ircmd: IRCMD) {
//                    Log.i(
//                        TAG,
//                        "ConnectCallback->onIRCMDCreate"
//                    )
//                    this@IRThermalActivity.ircmd = ircmd
//                    // [Chinese text]IRCMD[Chinese text]
//                    temperatureView.setIrcmd(ircmd)
////                    ircmd?.setPseudoColor(PreviewPathChannel.PREVIEW_PATH0, PseudocodeUtils.changePseudocodeModeByOld(pseudocolorMode))
//                    isConfigWait = false
//                }
//            }, object : USBMonitorCallback {
//                override fun onAttach() {}
//                override fun onGranted() {}
//                override fun onConnect() {}
//                override fun onDisconnect() {}
//                override fun onDettach() {
//                    finish()
//                }
//
//                override fun onCancel() {
//                    finish()
//                }
//            })
//        iruvc?.isRestart = isRestart
//        iruvc?.setImageSrc(imageBytes)
//        iruvc?.setTemperatureSrc(temperatureBytes)
//        iruvc?.imageEditTemp = imageEditBytes
//        iruvc?.setRotate(true)
//        iruvc?.setRotate(rotateAngle)
//        iruvc?.setHandler(mHandler)
//        iruvc?.setCMDDataCallback { // [Chinese text]in progress[Chinese text], [Chinese text]operation[Chinese text]
//            this@IRThermalActivity.isCMDDataComplete = true
//        }
//        iruvc?.registerUSB()
//        // [Chinese text]settings
////        popupCalibration.setRotate(true)
//    }
//    private var nuc_table_high = ShortArray(8192)
//    private var nuc_table_low = ShortArray(8192)
//    //[Chinese text]SN[Chinese text]key[Chinese text]
//    private var md5PNSNKey : String? = null
//    // [Chinese text]Flashin progress[Chinese text]nuc[Chinese text], [Chinese text]
//    private var isGetNucFromFlash = false
//    private val gainMode = GainMode.GAIN_MODE_HIGH_LOW
//    private var gainStatus = GainStatus.HIGH_GAIN
//
//    suspend fun getUTable(){
//        val SN = ByteArray(16)
//        ircmd!!.getDeviceInfo(DeviceInfoType.DEV_INFO_GET_SN, SN)
//        val deviceSNUnCodePath: String = FileUtil.getTableDirPath() + File.separator
//        // [Chinese text]key,[Chinese text]
//        md5PNSNKey = FileUtil.getMD5Key(String(SN))
//        val nucHighFileName = md5PNSNKey + "_nuc_table_high.bin"
//        val nucLowFileName = md5PNSNKey + "_nuc_table_low.bin"
//        if (!md5PNSNKey?.isEmpty()!! && FileUtil.isFileExists(
//                this@IRThermalActivity,
//                deviceSNUnCodePath + nucHighFileName
//            ) &&
//            FileUtil.isFileExists(
//                this@IRThermalActivity,
//                deviceSNUnCodePath + nucLowFileName
//            ) && ContextCompat.checkSelfPermission(
//                this@IRThermalActivity,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            // [Chinese text]SD[Chinese text]
//            val nuc_table_high_byte = FileUtil.readFile2BytesByStream(
//                this@IRThermalActivity,
//                File(deviceSNUnCodePath + nucHighFileName)
//            )
//            nuc_table_high = FileUtil.toShortArray(nuc_table_high_byte)
//            //
//            val nuc_table_low_byte = FileUtil.readFile2BytesByStream(
//                this@IRThermalActivity,
//                File(deviceSNUnCodePath + nucLowFileName)
//            )
//            nuc_table_low = FileUtil.toShortArray(nuc_table_low_byte)
//            Log.e("[Chinese text]","[Chinese text]temperature[Chinese text]: [Chinese text]")
//        }else{
//            //[Chinese text]
//            if (ircmd != null && !md5PNSNKey?.isEmpty()!!) {
//                isGetNucFromFlash = true
//                tempinfo = ircmd!!.readNucTableFromFlash(
//                    gainMode, gainStatus, nuc_table_high,
//                    nuc_table_low
//                )
//                // [Chinese text], [Chinese text], [Chinese text]
//                FileUtil.saveShortFileForDeviceData(nuc_table_high, nucHighFileName)
//                FileUtil.saveShortFileForDeviceData(nuc_table_low, nucLowFileName)
//                Log.e("[Chinese text]","[Chinese text]temperature[Chinese text]: [Chinese text]")
//            }
//        }
//        var i = 0
//        while (i < nuc_table_low.size) {
//            Log.i(
//                TAG,
//                "nuc_table_high[" + i + "]=" + nuc_table_high[i] + " nuc_table_low[" + i +
//                        "]=" + nuc_table_low[i]
//            )
//            i += 1000
//        }
//    }
//
//    //settingsTS001[Chinese text]temperature[Chinese text]
//    suspend fun setTsBin(){
//        ircmd?.let {
//            val getSnBytes = ByteArray(16)
//            val fwBuildVersionInfoBytes = ByteArray(50)
//            ircmd?.getDeviceInfo(
//                DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
//                fwBuildVersionInfoBytes
//            ) //ok
//            val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
//            it.getDeviceInfo(DeviceInfoType.DEV_INFO_GET_SN, getSnBytes) //ok
//            val snStr = String(getSnBytes) //sn
//            val infoBuilder = StringBuilder()
//            infoBuilder.append("Firmware version: ").append(arm).append("<br>")
//            infoBuilder.append("SN: ").append(snStr).append("<br>")
//            val str = HtmlCompat.fromHtml(infoBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
//            if (str.contains("Mini256",true)){
//                getUTable()
//                // [Chinese text]highlow[Chinese text]
//                val value = IntArray(1)
//                ircmd?.getPropTPDParams(PropTPDParams.TPD_PROP_GAIN_SEL, value)
//                Log.d(TAG, "TPD_PROP_GAIN_SEL=" + value[0])
//                gainStatus = if (value[0] == 1) {
//                    // [Chinese text]high[Chinese text]
//                    GainStatus.HIGH_GAIN
//                    // [Chinese text]
//                } else {
//                    // [Chinese text]low[Chinese text]
//                    GainStatus.LOW_GAIN
//                }
//                ts_data_H = CommonUtils.getTauData(this@IRThermalActivity, "ts/TS001_H.bin")
//                ts_data_L = CommonUtils.getTauData(this@IRThermalActivity, "ts/TS001_L.bin")
//                //[Chinese text], [Chinese text]temperature[Chinese text]
//                val orgEMS = IntArray(1)
//                ircmd?.getPropTPDParams(PropTPDParams.TPD_PROP_EMS, orgEMS)
//                val orgTAU = IntArray(1)
//                ircmd?.getPropTPDParams(PropTPDParams.TPD_PROP_TAU, orgTAU)
//                val orgTA = IntArray(1)
//                ircmd?.getPropTPDParams(PropTPDParams.TPD_PROP_TA, orgTA)
//                val orgTU = IntArray(1)
//                ircmd?.getPropTPDParams(PropTPDParams.TPD_PROP_TU, orgTU)
//                tempinfo = IRUtils.getTemperatureCorrectionTempCalInfo(
//                    IRCMDType.USB_IR_256_384,
//                    gainMode,
//                    gainStatus, nuc_table_high, nuc_table_low,
//                    orgEMS[0], orgTAU[0], orgTA[0], orgTU[0]
//                )
//            }
//        }
//    }
//
//
//
//    /**
//     * [Chinese text]point[Chinese text]
//     *
//     * @param params_array
//     */
//    fun tempCorrect(temp: Float,
//                    gainStatus : GainStatus,tempinfo : Long) : Float {
//
//        val configRepository = ConfigRepository()
//        val config = configRepository.readConfig()
//        config.radiation
//        val params_array = floatArrayOf(temp,config.radiation,config.environment,
//            config.environment, config.distance,0.8f)
//        if (ts_data_H == null || ts_data_L == null || tempinfo == 0L){
//            return temp
//        }
//        val newTemp = IRUtils.temperatureCorrection(
//            IRCMDType.USB_IR_256_384,
//            ProductType.MINI256,
//            params_array[0],
//            ts_data_H,
//            ts_data_L,
//            params_array[1],
//            params_array[2],
//            params_array[3],
//            params_array[4],
//            params_array[5],
//            tempinfo,
//            gainStatus
//        )
//        Log.i(TAG, "temp correct, oldTemp = " + params_array[0] + " ems = " + params_array[1] + " ta = " + params_array[2] + " " +
//                    "distance = " + params_array[4] + " hum = " + params_array[5] + " productType = ${ProductType.MINI256}" + " " +
//                    "newtemp = " + newTemp
//        )
//        return newTemp
//    }
//    //[Chinese text]operation
//    private fun setRotateAction() {
//        if (rotateAngle == 0) {
//            rotateAngle = 270
//        } else {
//            rotateAngle -= 90
//        }
//        updateRotateAngle(rotateAngle)
//    }
//
//    /**
//     * 270[Chinese text]
//     * @param rotate 0, 90, 180, 270
//     */
//    private fun setRotate(rotateInt: Int) {
//        val  rotate: Boolean = true
//        if (imageThread != null) {
//            imageThread?.setRotate(rotateInt)
//        }
//        if (iruvc != null) {
//            iruvc?.setRotate(rotateInt)
//        }
//        if (rotateInt!=0) {
//            imageThread?.interrupt()
//            if (rotateInt == 180) {
//                //180
//                bitmap = Bitmap.createBitmap(imageHeight, imageWidth, Bitmap.Config.ARGB_8888)
//                temperatureView?.setImageSize(imageHeight, imageWidth,this@IRThermalActivity)
//                setViewLay(false)
//            } else {
//                //90 270
//                bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
//                temperatureView?.setImageSize(imageWidth, imageHeight,this@IRThermalActivity)
//                setViewLay(true)
//            }
//            try {
//                imageThread?.join()
//            } catch (e: InterruptedException) {
//                Log.e(
//                    TAG,
//                    "imageThread.join(): catch an interrupted exception"
//                )
//            }
//            startISP()
//        } else {
//            imageThread?.interrupt()
//            //0
//            bitmap = Bitmap.createBitmap(imageHeight, imageWidth, Bitmap.Config.ARGB_8888)
//            temperatureView?.setImageSize(imageHeight, imageWidth,this@IRThermalActivity)
//            setViewLay(false)
//            try {
//                imageThread?.join()
//            } catch (e: InterruptedException) {
//                Log.e(TAG, "[Chinese text] imageThread.join(): catch an interrupted exception")
//            }
//            startISP()
//        }
//        cameraView.bitmap = bitmap
//        imageThread?.setBitmap(bitmap)
//        runOnUiThread {
//            cl_seek_bar.bitmap = null
//            cl_seek_bar.requestLayout()
//            cl_seek_bar.updateBitmap()
//        }
//    }
//
//
//    override fun onStart() {
//        super.onStart()
//        Log.w(TAG, "onStart")
//        if (!isrun) {
//            // [Chinese text],[Chinese text]
////          pseudocolorMode = 3
//            tv_type_ind.visibility = GONE
//            thermal_recycler.limitStats = 460 //[Chinese text]DIY
//            startUSB(false)
//            startISP()
//            temperatureView?.start()
//            cameraView?.start()
//            isrun = true
//            //[Chinese text]
//            configParam()
//            thermal_recycler.updateCameraModel()
//            initIRConfig()
//        }
//    }
//
//    /**
//     * IRmode[Chinese text]
//     */
//    private fun initIRConfig(){
//        //[Chinese text]
//        if (CameraLiveDateUtil.getInstance().getCameraSBeanData().irSettingBean.openColorBar){
//            cl_seek_bar.visibility = View.VISIBLE
//            thermal_recycler.colorBarStats = 441 //[Chinese text]
//        }else{
//            cl_seek_bar.visibility = View.GONE
//            thermal_recycler.colorBarStats = 440 //[Chinese text]
//        }
//        temperature_seekbar?.setPseudocode(pseudocolorMode)
//        if (customPseudoBean!=null && customPseudoBean.isUseCustomPseudo){
//            temperature_seekbar.setColorList(customPseudoBean.getColorList()?.reversedArray()?:null)
//            temperature_iv_lock.visibility = View.INVISIBLE
//            temperature_seekbar.setRangeAndPro(UnitTools.showUnitValue(customPseudoBean.minTemp),
//                UnitTools.showUnitValue(customPseudoBean.maxTemp),
//                UnitTools.showUnitValue(customPseudoBean.minTemp),
//                UnitTools.showUnitValue(customPseudoBean.maxTemp))
//            tv_temp_content.visibility = View.VISIBLE
//            thermal_recycler.setPseudoColor(-1)
//            temperature_iv_input.setImageResource(R.drawable.ir_model)
//        }else{
//            temperature_iv_lock.visibility = View.VISIBLE
//            tv_temp_content.visibility = View.GONE
//            temperature_iv_input.setImageResource(R.drawable.ic_color_edit)
//            thermal_recycler.setPseudoColor(CameraLiveDateUtil.getInstance().getCameraSBeanData().irSettingBean.pseudoColorMode)
//        }
//        if (alarmBean.openHigh || alarmBean.openLow){
//            thermal_recycler.alarmStats = 501
//        }else{
//            thermal_recycler.alarmStats = 500
//        }
//    }
//
//
//
//    override fun onStop() {
//        super.onStop()
//        try {
//            configJob?.cancel()
//            time_down_view?.cancel()
//            iruvc?.stopPreview()
//            iruvc?.unregisterUSB()
//            imageThread?.interrupt()
//            imageThread?.join()
//            syncimage?.valid = false
//            temperatureView?.stop()
//            cameraView?.stop()
//            isrun = false
//            if (isVideo) {
//                videoTimeClose()
//                videoRecord?.stopRecord()
//                isVideo = false
//            }
//        }catch (e:Exception){
//
//        }
//    }
//
//    override fun onDestroy() {
//        CameraLiveDateUtil.getInstance().setDefData()
//        super.onDestroy()
//        AlarmHelp.getInstance(application).onDestroy()
//        temp_bg?.stopAnimation()
//        time_down_view?.cancel()
//        CameraLiveDateUtil.getInstance().setDefData()
//        try {
//            imageThread?.join()
//            if (tempinfo != 0L) {
//                IRUtils.releaseTemperatureCorrection(
//                    IRCMDType.USB_IR_256_384,
//                    tempinfo,
//                    false
//                )
//            }
//        } catch (e: InterruptedException) {
//            Log.e(TAG, "imageThread.join(): catch an interrupted exception")
//        }
//
//        // [Chinese text]sensor
////        if (Usbcontorl.isload) {
////            Usbcontorl.usb3803_mode_setting(0) //[Chinese text]5V
////        }
//
//    }
//
//    private fun showInfo() {
//        // [Chinese text]
//        Log.i(
//            TAG, """
//     P2-PN:
//     ${CommonUtils.getPNInfo(DeviceType.P2, "P2STDMD25602011XHWRXX-1170100010")}
//     """.trimIndent()
//        )
//        Log.i(
//            TAG, """
//     P2-SN:
//     ${CommonUtils.getSNInfo(DeviceType.P2, "YMN32091XD032200001")}
//     """.trimIndent()
//        )
//        /**
//         * [Chinese text]OEM[Chinese text]
//         */
//        val oemWriteInfo =
//            "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
//        /**
//         * [Chinese text]OEM[Chinese text]
//         *
//         */
//        val oemInfo = ByteArray(oemWriteInfo.length)
//        ircmd?.oemRead(oemInfo)
////        Log.i(TAG, "oemRead:" + String(oemInfo))
//        val CHIP_FW_INFO = ByteArray(8)
//        val FW_COMPILE_DATE = ByteArray(8)
//        val DEV_QUALIFICATION = ByteArray(8)
//        val IR_INFO = ByteArray(26)
//        val PROJECT_INFO = ByteArray(4)
//        val FW_BUILD_VERSION_INFO = ByteArray(50)
//        val PN = ByteArray(48)
//        val SN = ByteArray(16)
//        try {
//            ircmd?.getDeviceInfo(CommonParams.DeviceInfoType.DEV_INFO_CHIP_ID, CHIP_FW_INFO) //ok
//
//            ircmd?.getDeviceInfo(
//                CommonParams.DeviceInfoType.DEV_INFO_FW_COMPILE_DATE,
//                FW_COMPILE_DATE
//            ) //ok
//
//            ircmd?.getDeviceInfo(
//                CommonParams.DeviceInfoType.DEV_INFO_DEV_QUALIFICATION,
//                DEV_QUALIFICATION
//            ) //ok
//
//            ircmd?.getDeviceInfo(DeviceInfoType.DEV_INFO_PROJECT_INFO, PROJECT_INFO) //ok
//
//            ircmd?.getDeviceInfo(DeviceInfoType.DEV_INFO_IR_INFO, IR_INFO) //ok
//
//            ircmd?.getDeviceInfo(
//                DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
//                FW_BUILD_VERSION_INFO
//            ) //ok
//            ircmd?.getDeviceInfo(DeviceInfoType.DEV_INFO_GET_PN, PN) //ok
//            ircmd?.getDeviceInfo(DeviceInfoType.DEV_INFO_GET_SN, SN) //ok
//        }catch (e: Exception){
//
//        }
//        var info: String? = "Version:"
//        info += FileUtil.getVersionName(this)
//        info += """
//
//            PN:${String(PN)}
//            """.trimIndent()
//        info += """
//
//            SN:${String(SN)}
//            """.trimIndent()
//        info += """
//
//            IRProcessVer:${LibIRProcess.getIRProcessVersion()}
//            """.trimIndent()
//        info += """
//
//            IRParseVer:${LibIRParse.getIRParseVersion()}
//            """.trimIndent()
//        Log.i("[Chinese text]","$info")
//
//        TipDeviceDialog.Builder(this)
//            .setMessage("$info")
//            .setResetListener {
//                resetDevice()
//            }
//            .setCanceled(true)
//            .create().show()
//    }
//    private fun dealStop() {
//        isrun = false
//        syncimage.valid = false
//        temperatureView.stop()
//        iruvc?.unregisterUSB()
//        SystemClock.sleep(200)
//        iruvc?.stopPreview()
//        iruvc = null
//    }
//    private fun dealStart() {
//        startUSB(false)
//        temperatureView.start()
//    }
//    /**
//     * [Chinese text]
//     */
//    private fun restartUSBCamera() {
//        if (isUseIRISP) {
//            dealStop()
//            SystemClock.sleep(500)
//            dealStart()
//        }
//    }
//    private val permissionList by lazy{
//        if (this.applicationInfo.targetSdkVersion >= 33){
//            mutableListOf(Permission.READ_MEDIA_VIDEO,Permission.READ_MEDIA_IMAGES,
//                Permission.READ_MEDIA_AUDIO,Permission.WRITE_EXTERNAL_STORAGE
//            )
//        }else{
//            mutableListOf(Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
//        }
//    }
//    //photo capturein progress[Chinese text]button
//    @SuppressLint("CheckResult")
//    private fun centerCamera() {
//
//
//        XXPermissions.with(this)
//            .permission(
//                permissionList
//            )
//            .request(object  : OnPermissionCallback{
//                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
//                    if (allGranted) {
//                        if (thermal_recycler.cameraStats == CameraItemBean.TYPE_CAMERA) {
//                            val setting = CameraLiveDateUtil.getInstance().getCameraSettingData().continuousBean
//                            if (setting.isSel){
//                                if (!isAutoCamera){
//                                    //[Chinese text]photo capture
//                                    autoJob = countDownCoroutines(
//                                        setting.number,
//                                        (setting.time * 1000).toLong(),
//                                        this@IRThermalActivity.lifecycleScope,
//                                        onTick = {
//                                            camera()
//                                        }, onStart = {
//                                            tv_type_ind.visibility = VISIBLE
//                                            isAutoCamera = true
////                                        ToastTools.showShort(R.string.app_auto_photo)
//                                        },onFinish = {
//                                            tv_type_ind.visibility = GONE
//                                            isAutoCamera = false
//                                        })
//                                    autoJob?.start()
//                                }else{
//                                    isAutoCamera = false
//                                    autoJob?.cancel()
//                                }
//                            }else{
//                                camera()
//                            }
//                        }else{
//                            //[Chinese text]
//                            video()
//                        }
//                    } else {
//                        ToastUtils.showShort(R.string.scan_ble_tip_authorize)
//                    }
//                }
//
//                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
//                    if (doNotAskAgain){
//                        //[Chinese text]
//                        TipDialog.Builder(this@IRThermalActivity)
//                            .setTitleMessage(getString(R.string.app_tip))
//                            .setMessage(R.string.app_storage_content)
//                            .setPositiveListener(R.string.app_open){
//                                AppUtils.launchAppDetailsSettings()
//                            }
//                            .setCancelListener(R.string.app_cancel){
//                            }
//                            .setCanceled(true)
//                            .create().show()
//                    }
//                }
//            })
//    }
//    var showCameraSetting = false
//    val cameraItemBeanList by lazy {
//        mutableListOf(
//            CameraItemBean("[Chinese text]",CameraItemBean.TYPE_DELAY,
//                time = CameraLiveDateUtil.getInstance().getCameraSBeanData().delayTime),
//            CameraItemBean("[Chinese text]",CameraItemBean.TYPE_ZDKM,
//                isSel = CameraLiveDateUtil.getInstance().getAutoShutter()),
//            CameraItemBean("[Chinese text]",CameraItemBean.TYPE_SDKM),
//            CameraItemBean("[Chinese text]",CameraItemBean.TYPE_AUDIO,
//                isSel = CameraLiveDateUtil.getInstance().getCameraSBeanData().openAudioRecord &&
//                        ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
//                        == PackageManager.PERMISSION_GRANTED
//            ),
//            CameraItemBean("settings",CameraItemBean.TYPE_SETTING),
//        )
//    }
//    //photo capture[Chinese text]button
//    private fun settingCamera() {
//        showCameraSetting = !showCameraSetting
//        if (showCameraSetting){
//            ViewStubUtils.showViewStub(view_stub_camera,true, callback = { view : View? ->
//                view?.let {
//                    val recyclerView = it.findViewById<RecyclerView>(R.id.recycler_view)
//                    if (ScreenUtils.isPortrait()) {
//                        recyclerView.layoutManager = GridLayoutManager(this, 5)
//                    }else{
//                        recyclerView.layoutManager = GridLayoutManager(this, 5
//                            ,GridLayoutManager.VERTICAL,false)
//                    }
//                    val cameraItemAdapter = CameraItemAdapter(cameraItemBeanList)
//                    cameraItemAdapter.listener = listener@{ position, item ->
//                        when (cameraItemAdapter.data[position].type){
//                            CameraItemBean.TYPE_SETTING ->{
//                                ARouter.getInstance().build(RouterConfig.IR_CAMERA_SETTING)
//                                    .navigation(this)
//                                return@listener
//                            }
//                            CameraItemBean.TYPE_DELAY ->{
//                                if (time_down_view.isRunning){
//                                    return@listener
//                                }
//                                cameraItemAdapter.data[position].changeDelayType()
//                                cameraItemAdapter.notifyItemChanged(position)
//                                when (cameraItemAdapter.data[position].time) {
//                                    CameraItemBean.DELAY_TIME_0 -> {
//                                        ToastUtils.showShort(R.string.off_photography)
//                                    }
//                                    CameraItemBean.DELAY_TIME_3 -> {
//                                        ToastUtils.showShort(R.string.seconds_dalay_3)
//                                    }
//                                    CameraItemBean.DELAY_TIME_6 -> {
//                                        ToastUtils.showShort(R.string.seconds_dalay_6)
//                                    }
//                                }
//                                cameraSBean.delayTime = cameraItemAdapter.data[position].time
//                            }
//                            CameraItemBean.TYPE_AUDIO ->{
//                                if (!cameraItemAdapter.data[position].isSel){
//                                    XXPermissions.with(this@IRThermalActivity)
//                                        .permission(
//                                            Manifest.permission.RECORD_AUDIO,
//                                        )
//                                        .request(object : OnPermissionCallback{
//                                            override fun onGranted(
//                                                permissions: MutableList<String>,
//                                                allGranted: Boolean
//                                            ) {
//                                                try {
//                                                    if (allGranted) {
//                                                        //[Chinese text]
//                                                        cameraSBean.openAudioRecord = !cameraItemAdapter.data[position].isSel
//                                                        videoRecord?.updateAudioState(cameraSBean.openAudioRecord)
//                                                        cameraItemAdapter.data[position].isSel =
//                                                            !cameraItemAdapter.data[position].isSel
//                                                        cameraItemAdapter.notifyItemChanged(position)
//                                                        CameraLiveDateUtil.getInstance().saveAllConfig(cameraSBean)
//                                                    } else {
//                                                        ToastUtils.showShort(R.string.scan_ble_tip_authorize)
//                                                    }
//                                                }catch (e:Exception){
//                                                    Log.e("[Chinese text]",""+e.message)
//                                                }
//                                            }
//
//                                            override fun onDenied(
//                                                permissions: MutableList<String>,
//                                                doNotAskAgain: Boolean
//                                            ) {
//                                                if (doNotAskAgain){
//                                                    //[Chinese text]
//                                                    TipDialog.Builder(this@IRThermalActivity)
//                                                        .setTitleMessage(getString(R.string.app_tip))
//                                                        .setMessage(getString(R.string.app_microphone_content))
//                                                        .setPositiveListener(R.string.app_open){
//                                                            AppUtils.launchAppDetailsSettings()
//                                                        }
//                                                        .setCancelListener(R.string.app_cancel){
//                                                        }
//                                                        .setCanceled(true)
//                                                        .create().show()
//                                                }
//                                            }
//                                        })
//                                }else{
//                                    cameraSBean.openAudioRecord = !cameraItemAdapter.data[position].isSel
//                                    videoRecord?.updateAudioState(cameraSBean.openAudioRecord)
//                                    cameraItemAdapter.data[position].isSel =
//                                        !cameraItemAdapter.data[position].isSel
//                                    cameraItemAdapter.notifyItemChanged(position)
//                                    CameraLiveDateUtil.getInstance().saveAllConfig(cameraSBean)
//                                }
//                                return@listener
//                            }
//                            CameraItemBean.TYPE_SDKM -> {
//                                lifecycleScope.launch {
//                                    cameraItemAdapter.data[position].isSel = true
//                                    cameraItemAdapter.notifyItemChanged(position)
//                                    delay(500)
//                                    cameraItemAdapter.data[position].isSel = false
//                                    cameraItemAdapter.notifyItemChanged(position)
//                                }
//                                //[Chinese text]
//                                if (syncimage.type == 1) {
//                                    ircmd?.tiny1bShutterManual()
//                                } else {
//                                    ircmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
//                                }
//                                ToastUtils.showShort(R.string.app_Manual_Shutter)
//                                return@listener
//                            }
//                            CameraItemBean.TYPE_ZDKM -> {
//                                //[Chinese text]
//                                CameraLiveDateUtil.getInstance().saveAutoShutter(
//                                    !CameraLiveDateUtil.getInstance().getAutoShutter())
//                                cameraItemAdapter.data[position].isSel =
//                                    !cameraItemAdapter.data[position].isSel
//                                cameraItemAdapter.notifyItemChanged(position)
//                                if(CameraLiveDateUtil.getInstance().needShowShutterTip() &&
//                                    !CameraLiveDateUtil.getInstance().getAutoShutter()){
//                                    val dialog = TipShutterDialog.Builder(this)
//                                        .setMessage(R.string.shutter_tips)
//                                        .setCancelListener{isCheck ->
//                                            CameraLiveDateUtil.getInstance().setShowShutterTip(isCheck)
//                                        }
//                                        .create()
//                                    dialog?.show()
//                                }
//                                if (CameraLiveDateUtil.getInstance().getAutoShutter()){
//                                    ircmd?.setPropAutoShutterParameter(
//                                        PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
//                                        PropAutoShutterParameterValue.StatusSwith.ON
//                                    )
//                                }else{
//                                    ircmd?.setPropAutoShutterParameter(
//                                        PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
//                                        PropAutoShutterParameterValue.StatusSwith.OFF
//                                    )
//                                }
//                                return@listener
//                            }
//                        }
//                        cameraItemAdapter.data[position].isSel =
//                            !cameraItemAdapter.data[position].isSel
//                        cameraItemAdapter.notifyItemChanged(position)
//                        CameraLiveDateUtil.getInstance().saveAllConfig(cameraSBean)
//
//                    }
//                    recyclerView.adapter = cameraItemAdapter
//                }
//            })
//        }else{
//            ViewStubUtils.showViewStub(view_stub_camera,false,null)
//        }
//    }
//
//    // photo capture
//    private fun camera() {
//        lifecycleScope.launch(Dispatchers.Default) {
//            launch(Dispatchers.Main) {
//                thermal_recycler.showCameraActive()
//            }
////            System.arraycopy(imageEditBytes, 0, imageEditTempBytes, 0, imageEditBytes.size)//[Chinese text]
//            synchronized(syncimage.dataLock) {
//                // [Chinese text]
//                var cameraViewBitmap = cameraView.bitmap
//                var tmpFile = ""
//                if(isOpenPreview){
//                    cameraViewBitmap = BitmapUtils.mergeBitmapByView(
//                        cameraViewBitmap,
//                        cameraPreview.getBitmap(),
//                        cameraPreview
//                    )
//                    //[Chinese text]in progress[Chinese text]
//                    cameraPreview.getBitmap()?.let {
//                        tmpFile = ImageUtils.saveImageToApp(bitmap = it)
//                    }
//                }
//                var seekBarBitmap : Bitmap ?= null;
//                if (cl_seek_bar.visibility == VISIBLE){
//                    seekBarBitmap = cl_seek_bar.drawToBitmap()
//                }
//                if (temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) {
//                    // [Chinese text]temperature[Chinese text], [Chinese text]pointline[Chinese text], temperature[Chinese text], [Chinese text]bitmap
//                    cameraViewBitmap = BitmapUtils.mergeBitmap(
//                        cameraViewBitmap,
//                        temperatureView?.regionAndValueBitmap,
//                        0,
//                        0
//                    )
//                }
//                // [Chinese text]
//                seekBarBitmap?.let {
//                    cameraViewBitmap = BitmapUtils.mergeBitmap(
//                        cameraViewBitmap,
//                        seekBarBitmap,
//                        cameraViewBitmap!!.width - seekBarBitmap.width,
//                        (cameraViewBitmap!!.height - seekBarBitmap.height)/2
//                    )
//                    seekBarBitmap?.recycle()
//                }
//                if (temp_bg.isVisible){
//                    if (alphaPaint == null){
//                        alphaPaint = Paint()
//                    }
//                    alphaPaint?.alpha = (temp_bg.animatorAlpha * 255).toInt()
//                    //[Chinese text]
//                    cameraViewBitmap = BitmapUtils.mergeBitmapAlpha(
//                        cameraViewBitmap,
//                        temp_bg.drawToBitmap(),alphaPaint,
//                        0,
//                        0
//                    )
//                }
//                if (temperatureView.temperatureRegionMode != REGION_MODE_CLEAN) {
//                    // [Chinese text]temperature[Chinese text], [Chinese text]pointline[Chinese text], temperature[Chinese text], [Chinese text]bitmap
//                    cameraViewBitmap = BitmapUtils.mergeBitmap(
//                        cameraViewBitmap,
//                        temperatureView!!.regionAndValueBitmap,
//                        0,
//                        0
//                    )
//                }
//                var name = ""
//                if (CameraLiveDateUtil.getInstance().getCameraSettingData().watermarkBean.isSel){
//                    //[Chinese text]
//                    cameraViewBitmap = BitmapUtils.drawCenterLable(cameraViewBitmap,
//                        CameraLiveDateUtil.getInstance().getCameraSettingData().watermarkBean.title,
//                        CameraLiveDateUtil.getInstance().getCameraSettingData().watermarkBean.address,
//                        if (CameraLiveDateUtil.getInstance().getCameraSettingData().watermarkBean
//                                .addTime) TimeTool.getNowTime() else "")
//                }
//                cameraViewBitmap?.let {
//                    name = ImageUtils.save(bitmap = it)
//                }
//                var w = 192
//                var h = 256
//                if (cameraViewBitmap!!.height < cameraViewBitmap!!.width) {
//                    w = 256
//                    h = 192
//                }
//                val capital = FrameStructTool().code(
//                    width = w,
//                    height = h,
//                    rotate = rotateAngle,
//                    pseudo = pseudocolorMode,
//                    initRotate = initRotate,
//                    correctRotate = correctRotate,
//                    customPseudoBean = customPseudoBean
//                ) //[Chinese text]
////                val le = 256 * 192 * 2
////                val tmpBy = ByteArray(le)
////                System.arraycopy(
////                    imageEditTempBytes, le, tmpBy, 0,
////                    le
////                )
////                val tmp1 = LibIRTemp(256, 192)
////                tmp1.setTempData(tmpBy)
////                val result1 = tmp1.getTemperatureOfRect(Rect(0, 0, 256, 192))
////                Log.w("temperature[Chinese text]19", result1.maxTemperature.toString() + "///" + result1.minTemperature)
//                ImageUtils.saveFrame(bs = imageEditBytes, capital = capital, name = name)
//                //[Chinese text]argb[Chinese text], [Chinese text], [Chinese text]
////                ImageUtils.saveOneFrameAGRB(bs = imageThread!!.imageTemp, name = System.currentTimeMillis().toString())
//                //[Chinese text]argb
//                launch(Dispatchers.Main) {
//                    thermal_recycler.refreshImg()
//                }
//            }
//        }
//    }
//
//
//    private var isVideo = false
//
//    private var videoRecord: VideoRecordFFmpeg? = null
//
//    private fun video() {
//        if (!isVideo) {
//            //start[Chinese text]
//            videoRecord = VideoRecordFFmpeg(cameraView,cameraPreview, temperatureView, cl_seek_bar,bitmap,temp_bg)
//            if (!videoRecord!!.canStartVideoRecord(null)){
//                return
//            }
//            videoRecord?.stopVideoRecordListener = {isShowVideoRecordTips ->
//                this@IRThermalActivity.runOnUiThread {
//                    if (isShowVideoRecordTips){
//                        try {
//                            val dialog = TipDialog.Builder(this@IRThermalActivity)
//                                .setMessage(R.string.tip_video_record)
//                                .create()
//                            dialog?.show()
//                        }catch (e:Exception){
//                        }
//                    }
//                    videoRecord?.stopRecord()
//                    isVideo = false
//                    videoTimeClose()
//                    lifecycleScope.launch(Dispatchers.Main) {
//                        delay(500)
//                        thermal_recycler.refreshImg()
//                    }
//                }
//            }
//            cl_seek_bar.updateBitmap()
//            videoRecord?.updateAudioState(cameraSBean.openAudioRecord)
//            videoRecord?.startRecord()
//            isVideo = true
//            lifecycleScope.launch(Dispatchers.Main) {
//                thermal_recycler.showVideoActive()
//            }
//            videoTimeShow()
//        } else {
//            videoRecord?.stopRecord()
//            isVideo = false
//            videoTimeClose()
//            lifecycleScope.launch(Dispatchers.Main) {
//                delay(500)
//                thermal_recycler.refreshImg()
//            }
//        }
//    }
//
//
//    private var flow: Job? = null
//
//    private fun videoTimeShow() {
//        flow = lifecycleScope.launch {
//            val time = 60 * 60 * 4
//            flow {
//                repeat(time) {
//                    emit(it)
//                    delay(1000)
//                }
//            }.collect {
//                launch(Dispatchers.Main) {
//                    pop_time_text.text = TimeTool.showVideoTime(it * 1000L)
//                }
//                if (it == time - 1) {
//                    //stop
//                    video()
//                }
//            }
//        }
//        pop_time_lay.visibility = View.VISIBLE
//    }
//
//    private fun videoTimeClose() {
//        flow?.cancel()
//        flow = null
//        pop_time_lay.visibility = View.GONE
//    }
//
//
//    // [Chinese text]
//    private fun setPseudoColor() {
//        cl_seek_bar.isVisible = !cl_seek_bar.isVisible
//        CameraLiveDateUtil.getInstance().saveColorBarStats(cl_seek_bar.isVisible)
////        thermal_pseudo_bar.isVisible = !thermal_pseudo_bar.isVisible
//        if (cl_seek_bar.isVisible) {
//            thermal_recycler.colorBarStats = 441
//        } else {
//            thermal_recycler.colorBarStats = 440
//        }
//    }
////    private var contrastConfig = CameraLiveDateUtil.getInstance().getCameraSBeanData().irSettingBean.contrastConfig
////    private var ddeConfig = CameraLiveDateUtil.getInstance().getCameraSBeanData().irSettingBean?.ddeConfig
//    private var contrastConfig = 128
//    private var ddeConfig = 2
//
//    //IMAGE_PROP_LEVEL_CONTRAST (0~255) [Chinese text]in progress[Chinese text]
//    private fun setParamLevelContrast() {
//        if (thermal_recycler.contrastStats == 431){
//            popupWindow?.dismiss()
//            return
//        }
//        popupWindow?.dismiss()
//        thermal_recycler.contrastStats = 431
//        popupWindow = PopupWindow(this)
//        val contentView = LayoutInflater.from(this).inflate(R.layout.layout_camera_seek_bar,null)
//        popupWindow?.contentView = contentView
//        popupWindow?.isFocusable = false
//        popupWindow?.isOutsideTouchable = false
//        popupWindow?.animationStyle = R.style.SeekBarAnimation
//        popupWindow?.width = WindowManager.LayoutParams.MATCH_PARENT
//        popupWindow?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        popupWindow?.setBackgroundDrawable(ColorDrawable(0))
//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        val contentHeight = contentView.measuredHeight
//        val seekBar = contentView?.findViewById<CommSeekBar>(R.id.seek_bar)
//        val tvProcess = contentView?.findViewById<TextView>(R.id.tv_value)
//        // AGC[Chinese text]level[Chinese text]MAXGAIN[Chinese text]BOS,[Chinese text]AGCswitchlevel[Chinese text]MAXGAIN[Chinese text]BOS,[Chinese text]level[Chinese text]settings[Chinese text]
//        val mode = IntArray(1)
//        ircmd?.getPropImageParams(
//            PropImageParams.IMAGE_PROP_LEVEL_CONTRAST,
//            mode
//        )
//        val modeInt = mode.first()
//        val saturation = NumberTools.scale(modeInt / 2.56f, 0).toInt()
//        seekBar?.progress = saturation
//        tvProcess?.text = "${saturation}%"
//        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(
//                seekBar: SeekBar?,
//                progress: Int,
//                fromUser: Boolean
//            ) {
//                tvProcess?.text = "${progress}%"
//            }
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                ircmd?.getPropImageParams(
//                    PropImageParams.IMAGE_PROP_LEVEL_CONTRAST,
//                    mode
//                )
//                val modeInt = mode.first()
//                val saturation = NumberTools.scale(modeInt / 2.56f, 0).toInt()
//                var p: Int = (seekBar!!.progress * 2.56f).toInt()
//                if (p > 255) p = 255
//                contrastConfig = p
//                setImageParams(PropImageParams.IMAGE_PROP_LEVEL_CONTRAST, contrastConfig.toString())
//                CameraLiveDateUtil.getInstance().saveIRContrastConfig(contrastConfig)
//            }
//
//        })
//        popupWindow?.setOnDismissListener {
//            thermal_recycler.contrastStats = 430
//        }
//        //[Chinese text]
//        popupWindow?.showAsDropDown(thermal_lay, 0, getPopupWindowY(contentHeight), Gravity.NO_GRAVITY)
//    }
//
//
//    fun getPopupWindowY(contentHeight : Int) : Int{
//        if (rotateAngle == 180 || rotateAngle == 0){
//           return 0
//        }
//        val location = IntArray(2)
//        thermal_lay.getLocationInWindow(location)
//        val menuLocation = IntArray(2)
//        thermal_recycler.getLocationInWindow(menuLocation)
//        return if (location[1]+thermal_lay.measuredHeight > menuLocation[1]){
//            thermal_lay.measuredHeight - contentHeight - (location[1]+thermal_lay.measuredHeight - menuLocation[1])
//        }else{
//            thermal_lay.measuredHeight - contentHeight
//        }
//    }
//
//    var nowZoomLevel = CameraLiveDateUtil.getInstance().cameraZoom
//    /**
//     * [Chinese text]1/2/3/4/5
//     * [Chinese text]
//     */
//    private fun setZoom() {
//        if (thermal_recycler.cameraZoomStats == 481){
//            popupWindow?.dismiss()
//            return
//        }
//        popupWindow?.dismiss()
//        thermal_recycler.cameraZoomStats = 481
//        popupWindow = PopupWindow(this)
//        val contentView = LayoutInflater.from(this).inflate(R.layout.layout_camera_zoom,null)
//        popupWindow?.contentView = contentView
//        popupWindow?.isFocusable = false
//        popupWindow?.animationStyle = R.style.SeekBarAnimation
//        popupWindow?.width = WindowManager.LayoutParams.MATCH_PARENT
//        popupWindow?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        popupWindow?.setBackgroundDrawable(ColorDrawable(0))
//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        val contentHeight = contentView.measuredHeight
//        val imgUp = contentView?.findViewById<ImageView>(R.id.img_up)
//        val imgDown = contentView?.findViewById<ImageView>(R.id.img_down)
//        val tvZoom = contentView?.findViewById<TextView>(R.id.tv_zoom)
//        tvZoom?.text = nowZoomLevel.toString()
//        if (nowZoomLevel <= 1){
//            imgDown?.setImageResource(R.drawable.ic_reduce_disable)
//            imgUp?.setImageResource(R.drawable.ic_add_nor)
//        }else if (nowZoomLevel >= 5){
//            imgDown?.setImageResource(R.drawable.ic_reduce_nor)
//            imgUp?.setImageResource(R.drawable.ic_add_disable)
//        }else{
//            imgDown?.setImageResource(R.drawable.ic_reduce_nor)
//            imgUp?.setImageResource(R.drawable.ic_add_nor)
//        }
//        imgUp?.setOnClickListener {
//            if (nowZoomLevel >= 5){
//                return@setOnClickListener
//            }
//            cameraView.scaleX = 1f + 0.3f * (nowZoomLevel -1)
//            cameraView.scaleY = 1f + 0.3f * (nowZoomLevel -1)
////            temperatureView.scaleX = 1f + 0.3f * (nowZoomLevel -1)
////            temperatureView.scaleY = 1f + 0.3f * (nowZoomLevel -1)
////            ircmd?.zoomCenterUp(
////                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
////                CommonParams.ZoomScaleStep.ZOOM_STEP2
////            )
//            nowZoomLevel ++
//            if (nowZoomLevel <= 1){
//                imgDown?.setImageResource(R.drawable.ic_reduce_disable)
//                imgUp?.setImageResource(R.drawable.ic_add_nor)
//            }else if (nowZoomLevel >= 5){
//                imgDown?.setImageResource(R.drawable.ic_reduce_nor)
//                imgUp?.setImageResource(R.drawable.ic_add_disable)
//            }else{
//                imgDown?.setImageResource(R.drawable.ic_reduce_nor)
//                imgUp?.setImageResource(R.drawable.ic_add_nor)
//            }
//            tvZoom?.text = nowZoomLevel.toString()
//            temperatureView.nowZoomLevel = nowZoomLevel
//            CameraLiveDateUtil.getInstance().cameraZoom = nowZoomLevel
//        }
//        imgDown?.setOnClickListener {
//            if (nowZoomLevel <= 1){
//                return@setOnClickListener
//            }
//            cameraView.scaleX = 1f + 0.3f * (nowZoomLevel -1)
//            cameraView.scaleY = 1f + 0.3f * (nowZoomLevel -1)
////            temperatureView.scaleX = 1f + 0.3f * (nowZoomLevel -1)
////            temperatureView.scaleY = 1f + 0.3f * (nowZoomLevel -1)
////            ircmd?.zoomCenterDown(
////                CommonParams.PreviewPathChannel.PREVIEW_PATH0,
////                CommonParams.ZoomScaleStep.ZOOM_STEP2
////            )
//            nowZoomLevel --
//            if (nowZoomLevel <= 1){
//                imgDown?.setImageResource(R.drawable.ic_reduce_disable)
//                imgUp?.setImageResource(R.drawable.ic_add_nor)
//            }else if (nowZoomLevel >= 5){
//                imgDown?.setImageResource(R.drawable.ic_reduce_nor)
//                imgUp?.setImageResource(R.drawable.ic_add_disable)
//            }else{
//                imgDown?.setImageResource(R.drawable.ic_reduce_nor)
//                imgUp?.setImageResource(R.drawable.ic_add_nor)
//            }
//            tvZoom?.text = nowZoomLevel.toString()
//            temperatureView.nowZoomLevel = nowZoomLevel
//            CameraLiveDateUtil.getInstance().cameraZoom = nowZoomLevel
//        }
//        popupWindow?.setOnDismissListener {
//            thermal_recycler.cameraZoomStats = 480
//        }
//        //[Chinese text]
//        popupWindow?.showAsDropDown(thermal_lay, 0, getPopupWindowY(contentHeight), Gravity.NO_GRAVITY)
//    }
//
//
//    val levelMax = 4//[Chinese text], 0-4
//    //IMAGE_PROP_LEVEL_DDE (0~4) [Chinese text]([Chinese text]2)
//    private fun setParamLevelDde() {
//        if (thermal_recycler.ddeStats == 421){
//            popupWindow?.dismiss()
//            return
//        }
//        popupWindow?.dismiss()
//        thermal_recycler.ddeStats = 421
//        popupWindow = PopupWindow(this)
//        val contentView = LayoutInflater.from(this).inflate(R.layout.layout_camera_seek_bar,null)
//        popupWindow?.contentView = contentView
//        popupWindow?.isFocusable = false
//        popupWindow?.animationStyle = R.style.SeekBarAnimation
//        popupWindow?.width = WindowManager.LayoutParams.MATCH_PARENT
//        popupWindow?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        popupWindow?.setBackgroundDrawable(ColorDrawable(0))
//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        val contentHeight = contentView.measuredHeight
//        val seekBar = contentView?.findViewById<CommSeekBar>(R.id.seek_bar)
//        val tvProcess = contentView?.findViewById<TextView>(R.id.tv_value)
//        val mode = CharArray(1)
////        Libircmd.get_prop_image_params(Libircmd.IMAGE_PROP_LEVEL_DDE, mode, iruvc!!.uvcCamera.nativePtr)
////        val levelCurrent = mode.first().code
//        val saturation = ddeConfig.toFloat() / levelMax * 100
//        seekBar?.progress = saturation.toInt()
//        tvProcess?.text = "${saturation}%"
//        seekBar?.level = levelMax
//        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(
//                seekBar: SeekBar?,
//                progress: Int,
//                fromUser: Boolean
//            ) {
//                tvProcess?.text = "${progress}%"
//            }
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//            override fun onStopTrackingTouch(s: SeekBar?) {
//                seekBar?.stopTrackTouchLevel()
//                s?.let {
//                    ddeConfig = (it.progress.toFloat() / 100 * levelMax).toInt()
//                    when(ddeConfig){
//                        0 ->
//                            ircmd?.setPropImageParams(PropImageParams.IMAGE_PROP_LEVEL_DDE, PropImageParamsValue.DDEType.DDE_0)
//                        1 ->
//                            ircmd?.setPropImageParams(PropImageParams.IMAGE_PROP_LEVEL_DDE, PropImageParamsValue.DDEType.DDE_1)
//                        2 ->
//                            ircmd?.setPropImageParams(PropImageParams.IMAGE_PROP_LEVEL_DDE, PropImageParamsValue.DDEType.DDE_2)
//                        3 ->
//                            ircmd?.setPropImageParams(PropImageParams.IMAGE_PROP_LEVEL_DDE, PropImageParamsValue.DDEType.DDE_3)
//                        4 ->
//                            ircmd?.setPropImageParams(PropImageParams.IMAGE_PROP_LEVEL_DDE, PropImageParamsValue.DDEType.DDE_4)
//                    }
//                    CameraLiveDateUtil.getInstance().saveDDEConfig(ddeConfig)
//                }
//            }
//
//        })
//        popupWindow?.setOnDismissListener {
//            thermal_recycler.ddeStats = 420
//        }
//        //[Chinese text]
//        popupWindow?.showAsDropDown(thermal_lay, 0, getPopupWindowY(contentHeight), Gravity.NO_GRAVITY)
//    }
//
////    //IMAGE_PROP_LEVEL_SNR (0~3) [Chinese text]([Chinese text]2) [Chinese text]
////    //IMAGE_PROP_LEVEL_TNR (0~3) [Chinese text]([Chinese text]2) [Chinese text]
//
//    /**
//     * [Chinese text]
//     * IMAGE_PROP_MODE_AGC: [Chinese text]2
//     * IMAGE_PROP_ONOFF_AGC: [Chinese text]1
//     */
//    private fun autoConfig() {
//        lifecycleScope.launch(Dispatchers.IO) {
////            val value = CharArray(1)
////            Libircmd.get_prop_image_params(
////                Libircmd.IMAGE_PROP_MODE_AGC, value,
////                iruvc!!.uvcCamera.nativePtr
////            )
////            Log.w("123", "IMAGE_PROP_MODE_AGC: ${value[0].toInt()}")
////            Libircmd.set_prop_image_params(
////                Libircmd.IMAGE_PROP_MODE_AGC,
////                3.toChar(),
////                iruvc!!.uvcCamera.nativePtr
////            )
//
////            val value = CharArray(1)
////            Libircmd.get_prop_image_params(
////                Libircmd.IMAGE_PROP_ONOFF_AGC, value,
////                iruvc!!.uvcCamera.nativePtr
////            )
////            Log.w("123", "IMAGE_PROP_ONOFF_AGC: ${value[0].toInt()}")
////            setImageParams(
////                Libircmd.IMAGE_PROP_ONOFF_AGC,
////                (if (value[0] == 0.toChar()) 1 else 0).toChar()
////            )
//
//            iruvc?.let {
//                if (!it.auto_gain_switch) {
//                    it.auto_gain_switch = true
//                    ToastTools.showShort(R.string.auto_open)
//                } else {
//                    it.auto_gain_switch = false
//                    ToastTools.showShort(R.string.auto_close)
//                }
//                gainSelChar = -1
//            }
//        }
//
//    }
//
//    /**
//     * [Chinese text]
//     * TPD_PROP_DISTANCE: 32    ([Chinese text]temperature[Chinese text] 0-25600(0-200m) 128cnt = 1m, [Chinese text]: 0.25 * 128 = 32)
//     * TPD_PROP_TU: 300         ([Chinese text]temperature)
//     * TPD_PROP_TA: 300         ([Chinese text]temperature)
//     * TPD_PROP_EMS: 128        (target[Chinese text] 1-128(0.01-1))
//     * TPD_PROP_TAU: 128        ([Chinese text])
//     * TPD_PROP_GAIN_SEL: 1     (highlow[Chinese text]switch)
//     *
//     * [Chinese text]temperatureTU[Chinese text]TA[Chinese text]settings
//     *
//     * [Chinese text]
//     *  [Chinese text]TPD_PROP DISTANCE: 32
//     *  [Chinese text]TPD_PROP TU: 300
//     *  [Chinese text]TPD_PROP TA: 300
//     *  [Chinese text]TPD_PROP EMS: 128
//     *  [Chinese text]TPD_PROP TAU: 128
//     */
//    val paramType = arrayOf("DISTANCE", "TU", "TA", "EMS", "TAU", "GAIN_SEL")
//    var isConfigWait = true
//    var configJob : Job ?= null
//    //[Chinese text]
//    private fun configParam() {
//        configJob =  lifecycleScope.launch {
////            showLoading()
//            while (isConfigWait) {
//                delay(100)
//            }
//            delay(300)
//            // [Chinese text]highlow[Chinese text] 1:low[Chinese text] 0: high[Chinese text]
////            val gainSelValue = CharArray(1)
////            iruvc?.uvcCamera?.nativePtr?.let {
////                Libircmd.get_prop_tpd_params(5, gainSelValue, it)
//////                gainSelChar = gainSelValue[0].code
//////                thermal_recycler.setFiveSelectCode(gainSelChar)
////                XLog.w("[Chinese text]TPD_PROP ${paramType[5]}: ${gainSelValue[0].code}")
////            }
//
//            val gainSelValue = CharArray(1)
////            iruvc?.uvcCamera?.nativePtr?.let {
////                Libircmd.get_prop_tpd_params(5, gainSelValue, it)
//////                gainSelChar = gainSelValue[0].code
//////                thermal_recycler.setFiveSelectCode(gainSelChar)
////                XLog.w("[Chinese text]TPD_PROP ${paramType[5]}: ${gainSelValue[0].code}")
////            }
//
//            val configRepository = ConfigRepository()
//            val config = configRepository.readConfig()
//            val disChar = (config.distance * 128).toInt() //[Chinese text]([Chinese text])
//            val emsChar = (config.radiation * 128).toInt() //[Chinese text]
////            val tuChar = (config.environment * 10).toInt().toChar() //[Chinese text]temperature
//            XLog.w("settingsTPD_PROP DISTANCE:${disChar.toInt()}, EMS:${emsChar.toInt()}}")
//            val timeMillis = 250L
//            delay(timeMillis)
//            //[Chinese text]
//            /// Emissivity property.unit:1/128, range:1-128(0.01-1)
//            ircmd?.setPropTPDParams(
//                PropTPDParams.TPD_PROP_EMS,
//                PropTPDParamsValue.NumberType(emsChar.toString())
//            )
//            delay(timeMillis)
//            //[Chinese text]
//            ircmd?.setPropTPDParams(
//                PropTPDParams.TPD_PROP_DISTANCE,
//                PropTPDParamsValue.NumberType(disChar.toString())
//            )
//            when(CameraLiveDateUtil.getInstance().getCameraSBeanData().irSettingBean.temperature){
//                CameraItemBean.TYPE_TMP_H ->ircmd?.setPropTPDParams(
//                    PropTPDParams.TPD_PROP_GAIN_SEL,
//                    PropTPDParamsValue.GAINSELStatus.GAIN_SEL_LOW
//                )
//                CameraItemBean.TYPE_TMP_C ->ircmd?.setPropTPDParams(
//                    PropTPDParams.TPD_PROP_GAIN_SEL,
//                    PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH
//                )
//            }
////            delay(timeMillis)
////            //[Chinese text]temperature
////            setTpdParams(Libircmd.TPD_PROP_TA, tuChar) //ok
////            delay(timeMillis)
////            //[Chinese text]temperature
////            setTpdParams(Libircmd.TPD_PROP_TU, tuChar) //ok
//            // [Chinese text]
//            delay(timeMillis)
//            if (isFirst && isrun){
//                ircmd?.zoomCenterDown(
//                    CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                    CommonParams.ZoomScaleStep.ZOOM_STEP2
//                )
//                delay(timeMillis)
//                ircmd?.zoomCenterDown(
//                    CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                    CommonParams.ZoomScaleStep.ZOOM_STEP2
//                )
//                delay(timeMillis)
//                ircmd?.zoomCenterDown(
//                    CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                    CommonParams.ZoomScaleStep.ZOOM_STEP2
//                )
//                delay(timeMillis)
//                ircmd?.zoomCenterDown(
//                    CommonParams.PreviewPathChannel.PREVIEW_PATH0,
//                    CommonParams.ZoomScaleStep.ZOOM_STEP2
//                )
//                //[Chinese text]
//                ircmd?.setPropImageParams(
//                    PropImageParams.IMAGE_PROP_SEL_MIRROR_FLIP,
//                    PropImageParamsValue.MirrorFlipType.NO_MIRROR_FLIP
//                )
//                iruvc?.uvcCamera?.nativePtr?.let {
//                    // [Chinese text], [Chinese text]
//                    withContext(Dispatchers.IO){
//                        if (!CameraLiveDateUtil.getInstance().getAutoShutter()) {
//                            ircmd?.setPropAutoShutterParameter(
//                                PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
//                                CommonParams.PropAutoShutterParameterValue.StatusSwith.OFF
//                            )
//                        }else{
//                            ircmd?.setPropAutoShutterParameter(
//                                PropAutoShutterParameter.SHUTTER_PROP_SWITCH,
//                                PropAutoShutterParameterValue.StatusSwith.ON
//                            )
//                        }
//                        isFirst = false
//                    }
//                }
//                ircmd?.setPropImageParams(
//                    CommonParams.PropImageParams.IMAGE_PROP_LEVEL_DDE,
//                    CommonParams.PropImageParamsValue.DDEType.DDE_0
//                )
//                //[Chinese text], [Chinese text]
//                ircmd?.setPropImageParams(
//                    CommonParams.PropImageParams.IMAGE_PROP_LEVEL_CONTRAST,
//                    PropImageParamsValue.NumberType(contrastConfig.toString())
//                )
//            }
//            ircmd?.setPropImageParams(
//                CommonParams.PropImageParams.IMAGE_PROP_ONOFF_AGC,
//                PropImageParamsValue.StatusSwith.ON
//            )
//            lifecycleScope.launch {
//                withContext(Dispatchers.IO){
//                    setTsBin()
//                }
//                dismissCameraLoading()
//            }
//            printSN()
////            dismissLoading()
//        }
//    }
//
//    //settingstdp[Chinese text]
//    private fun setTpdParams(params: PropTPDParams, value: String) {
//        ircmd?.setPropTPDParams(params, PropTPDParamsValue.NumberType(value))
//    }
//
//    //settingsimg[Chinese text]
//    private fun setImageParams(params: PropImageParams, value: String) {
//        ircmd?.setPropImageParams(params, PropImageParamsValue.NumberType(value))
//    }
//
//    private var upValue = -273f
//    private var downValue = -273f
//    private var upColor = 0
//    private var downColor = 0
//
//    //temperaturerange
//    private fun addLimit() {
//        ThermalInputDialog.Builder(this)
//            .setMessage(getString(R.string.thermal_threshold_setting))
//            .setNum(max = upValue, min = downValue)
//            .setColor(maxColor = upColor, minColor = downColor)
//            .setPositiveListener(R.string.app_confirm) { up, down, upColor, downColor ->
//                this.upValue = up
//                this.downValue = down
//                this.upColor = upColor
//                this.downColor = downColor
//                imageThread?.setLimit(upValue, downValue, upColor, downColor) //[Chinese text]
//                if (upValue.toInt() == -273 && downValue.toInt() == -273) {
//                    // [Chinese text]DIY
//                    thermal_recycler.limitStats = 460
//                } else {
//                    // [Chinese text]DIY
//                    thermal_recycler.limitStats = 461
//                }
//            }
//            .setCancelListener(getString(R.string.app_close)) {
//                upValue = -273f
//                downValue = -273f
//                thermal_recycler.limitStats = 460
//                imageThread?.setLimit(upValue, downValue, upColor, downColor) //[Chinese text]
//            }
//            .create().show()
//    }
//
//    var isOpenPreview = false
//
//    private fun cameraPreviewConfig(needShowTip: Boolean) {
//        if (!CheckDoubleClick.isFastDoubleClick()) {
//            if (isOpenPreview) {
//                //[Chinese text]
//                isOpenPreview = false
//                cameraPreview.closeCamera()
//                thermal_recycler.cameraPreviewStats = 450
//                if (thermal_recycler.cameraAlphaStats == 471){
//                    popupWindow?.dismiss()
//                }
//                cameraPreview.visibility = View.INVISIBLE
//                CameraLiveDateUtil.getInstance().saveCameraPreview(false)
//            } else {
//                //[Chinese text]
//                XXPermissions.with(this@IRThermalActivity)
//                    .permission(
//                        Manifest.permission.CAMERA,
//                    )
//                    .request(object : OnPermissionCallback{
//                        override fun onGranted(
//                            permissions: MutableList<String>,
//                            allGranted: Boolean
//                        ) {
//                            try {
//                                if (allGranted) {
//                                    //[Chinese text]in progress[Chinese text]
//                                    thermal_recycler.cameraPreviewStats = 451
//                                    cameraPreview.visibility = View.VISIBLE
//                                    cameraPreview?.setCameraAlpha(cameraAlpha / 100.0f)
//                                    cameraPreview.post {
//                                        isOpenPreview = true
//                                        cameraPreview.openCamera()
//                                        CameraLiveDateUtil.getInstance().saveCameraPreview(isOpenPreview)
//                                    }
//                                    if(needShowTip && CameraLiveDateUtil.getInstance().needShowPreviewTip()){
//                                        val dialog = TipPreviewDialog.newInstance()
//                                        dialog.closeEvent = {
//                                            CameraLiveDateUtil.getInstance().setShowPreviewTip(it)
//                                        }
//                                        dialog.show(supportFragmentManager,"")
//                                    }
//                                } else {
//                                    thermal_recycler.cameraPreviewStats = 450
//                                    ToastUtils.showShort(R.string.scan_ble_tip_authorize)
//                                }
//                            }catch (e:Exception){
//                                XLog.e("[Chinese text]in progress[Chinese text]"+e.message)
//                            }
//                        }
//
//                        override fun onDenied(
//                            permissions: MutableList<String>,
//                            doNotAskAgain: Boolean
//                        ) {
//                            if (doNotAskAgain){
//                                //[Chinese text]
//                                TipDialog.Builder(this@IRThermalActivity)
//                                    .setTitleMessage(getString(R.string.app_tip))
//                                    .setMessage(getString(R.string.app_camera_content))
//                                    .setPositiveListener(R.string.app_open){
//                                        AppUtils.launchAppDetailsSettings()
//                                    }
//                                    .setCancelListener(R.string.app_cancel){
//                                    }
//                                    .setCanceled(true)
//                                    .create().show()
//                            }
//                            thermal_recycler.cameraPreviewStats = 450
//                        }
//                    })
//            }
//        }
//
//    }
//
//    private fun closeCameraPreviewConfig() {
//        if (isOpenPreview) {
//            cameraPreviewConfig(false)
//        }
//    }
//
////    private var isResetFlag = false
//
//    //[Chinese text]
//    private fun resetDevice() {
//        if (iruvc == null) {
//            return
//        }
//        TipDialog.Builder(this).setMessage(R.string.reset_device)
//            .setPositiveListener(R.string.app_confirm) {
//                GlobalScope.launch {
//                    launch(Dispatchers.Main) {
//                        showLoading()
//                    }
//                    pseudocolorMode = 3
//                    rotateAngle = 270
//                    iruvc?.auto_gain_switch = false
//                    setRotate(rotateAngle)
//                    gainSelChar = CameraItemBean.TYPE_TMP_C
//                    delay(2000)
//                    //[Chinese text],[Chinese text]
//                    restartUSBCamera()
//                    delay(5000)
//                    launch(Dispatchers.Main) {
//                        dismissLoading()
//                        thermal_recycler.setDefaultIndex()
//                    }
//                    XLog.i("[Chinese text]")
//                }
//            }
//            .setCanceled(true)
//            .create().show()
//
//    }
//
//    override fun disConnected() {
//        super.disConnected()
//        BaseApplication.instance.actionIR = 0
//
////        finish()
//        if (!isInit) {
//            finish()
//        }
//    }
//
//    override fun finish() {
//        videoRecord?.stopRecord()
//        super.finish()
//    }
//
////    var count = 0
////
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    fun resetEvent(event: ResetConnectEvent) {
////        if (count < 10) {
////            if (event.action == 3) {
////                lifecycleScope.launch {
////                    XLog.e("[Chinese text],[Chinese text]")
////                    delay(1000)
//////                    restartUsbCamera()
//////                    startUSB()
////
////                }
////            }
////        }
////    }
//
//    override fun onBackPressed() {
//        setResult(200)
//        finish()
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun iruvctc(event: PreviewComplete) {
//        dealY16ModePreviewComplete()
//    }
//
//    private fun dealY16ModePreviewComplete() {
//        isInit = false
//        iruvc?.setFrameReady(true)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun cameraEvent(event: DeviceCameraEvent) {
//        when (event.action) {
//            100 -> {
//                //[Chinese text]
//                showCameraLoading()
//            }
//            101 -> {
//                //[Chinese text]
//                lifecycleScope.launch {
//                    delay(500)
//                    isConfigWait = false
//                    delay(1000)
//                    dismissCameraLoading()
//                    isInit = false
//                }
//            }
//        }
//    }
//
//
//
//    /**
//     * [Chinese text]
//     */
//    private fun printSN() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                if (iruvc == null) {
//                    return@launch
//                }
//                val fwBuildVersionInfoBytes = ByteArray(50)
//                val getSnBytes = ByteArray(16)
//                ircmd?.getDeviceInfo(
//                    DeviceInfoType.DEV_INFO_FW_BUILD_VERSION_INFO,
//                    fwBuildVersionInfoBytes
//                ) //ok
//                ircmd?.getDeviceInfo(DeviceInfoType.DEV_INFO_GET_SN, getSnBytes) //ok
//                val snStr = String(getSnBytes) //sn
//                val arm = String(fwBuildVersionInfoBytes.copyOfRange(0, 8))
//                SharedManager.setDeviceSn(snStr)
//                SharedManager.setDeviceVersion(arm)
//                val infoBuilder = StringBuilder()
//                infoBuilder.append("Firmware version: ").append(arm).append("<br>")
//                infoBuilder.append("SN: ").append(snStr).append("<br>")
//                val str = HtmlCompat.fromHtml(infoBuilder.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
//                XLog.i("[Chinese text]: $str")
//            } catch (e: Exception) {
//                XLog.e("[Chinese text]SN[Chinese text]: ${e.message}")
//            }
//        }
//    }
//
//    override fun tempCorrectByTs(temp: Float) : Float {
//        var tmp = temp
//        try {
//            tmp = tempCorrect(temp,gainStatus,tempinfo)
//        }catch (e : Exception){
//            XLog.e("temperature[Chinese text]: ${e.message}")
//        }finally {
//            return tmp
//        }
//    }
//}
