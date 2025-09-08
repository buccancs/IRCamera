package com.topdon.module.thermal.ir.report.activity

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.gzuliyujiang.wheelpicker.DatimePicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.bean.event.ReportCreateEvent
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.utils.CommUtils
import com.topdon.module.thermal.ir.BuildConfig
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.report.bean.ImageTempBean
import com.topdon.module.thermal.ir.report.bean.ReportConditionBean
import com.topdon.module.thermal.ir.report.bean.ReportInfoBean
import com.topdon.module.thermal.ir.repository.ConfigRepository
import com.topdon.module.thermal.ir.databinding.ActivityReportCreateFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

 * 12.
 * TC007: [ExtraKeyConfig.IS_TC007]
 * : [ExtraKeyConfig.FILE_ABSOLUTE_PATH]
 * : [ExtraKeyConfig.IMAGE_TEMP_BEAN]
@Route(path = RouterConfig.REPORT_CREATE_FIRST)
class ReportCreateFirstActivity: BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityReportCreateFirstBinding

     * TC007 .
     * true-TC007 false
    private var isTC007 = false
    private var locationManager: LocationManager? = null
    private var locationProvider: String? = null

    override fun initContentView() = R.layout.activity_report_create_first

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        binding = ActivityReportCreateFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
    }

    private val permissionList = listOf(
        Permission.ACCESS_FINE_LOCATION,
        Permission.ACCESS_COARSE_LOCATION
    )

    @SuppressLint("SetTextI18n")
    override fun initView() {
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        binding.etReportName.setText("TC${TimeUtils.millis2String(System.currentTimeMillis(), "yyyyMMdd_HHmm")}")
        binding.etReportAuthor.setText(SaveSettingUtil.reportAuthorName)
        binding.tvReportDate.text = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy.MM.dd HH:mm")
        binding.etReportWatermark.setText(SaveSettingUtil.reportWatermarkText)
        binding.tvAmbientTemperature.text = getString(LibAppR.string.thermal_config_environment) + "(${UnitTools.showUnit()})"
        binding.tvEmissivity.text = getString(LibAppR.string.album_report_emissivity) + "(0~1)"

        binding.etReportAuthor.addTextChangedListener {
            SaveSettingUtil.reportAuthorName = it?.toString() ?: ""
        }
        binding.etReportWatermark.addTextChangedListener {
            SaveSettingUtil.reportWatermarkText = it?.toString() ?: ""
        }

        binding.switchReportAuthor.setOnCheckedChangeListener { _, isChecked ->
            binding.etReportAuthor.isVisible = isChecked
        }
        binding.switchReportDate.setOnCheckedChangeListener { _, isChecked ->
            binding.tvReportDate.isVisible = isChecked
        }
        binding.switchReportPlace.setOnCheckedChangeListener { _, isChecked ->
            binding.etReportPlace.isVisible = isChecked
        }
        binding.switchReportWatermark.setOnCheckedChangeListener { _, isChecked ->
            binding.etReportWatermark.isVisible = isChecked
        }
        binding.switchAmbientHumidity.setOnCheckedChangeListener { _, isChecked ->
            binding.tipSeekHumidity.isVisible = isChecked
        }
        binding.switchAmbientTemperature.setOnCheckedChangeListener { _, isChecked ->
            binding.etAmbientTemperature.isVisible = isChecked
        }
        binding.switchEmissivity.setOnCheckedChangeListener { _, isChecked ->
            binding.tipSeekEmissivity.isVisible = isChecked
        }
        binding.switchTestDistance.setOnCheckedChangeListener { _, isChecked ->
            binding.etTestDistance.isVisible = isChecked
        }
        binding.tipSeekHumidity.progress = SaveSettingUtil.reportHumidity
        binding.tipSeekHumidity.onStopTrackingTouch = {
            SaveSettingUtil.reportHumidity = it
        }
        binding.tipSeekHumidity.valueFormatListener = {
            if (it % 10 == 0) "${it / 10}%" else "${it / 10}.${it % 10}%"
        }
        binding.tipSeekEmissivity.valueFormatListener = {
            when (it) {
                0 -> "0"
                100 -> "1"
                else -> if (it < 10) "0.0$it" else "0.$it"
            }
        }

        binding.tvReportDate.setOnClickListener(this)
        binding.tvPreview.setOnClickListener(this)
        binding.tvNext.setOnClickListener(this)
        binding.imgLocation.setOnClickListener(this)

        readConfig()
    }

    @SuppressLint("SetTextI18n")
    private fun readConfig() {
        var environment = 30f //
        var distance = 0.25f //
        var radiation = 0.95f //
        val config = ConfigRepository.readConfig(isTC007)
        distance = config.distance
        radiation = config.radiation
        environment = config.environment
        binding.etAmbientTemperature.setText(NumberTools.to01(UnitTools.showUnitValue(environment)))
        binding.etTestDistance.setText(NumberTools.to02(distance) + "m")
        binding.tipSeekEmissivity.progress = (radiation * 100).toInt()
    }

    override fun initData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReportCreate(event: ReportCreateEvent) {
        finish()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvReportDate -> {//
                selectTime()
            }
            binding.tvPreview -> {//
                val reportInfoBean = buildReportInfo()
                val reportConditionBean = buildReportCondition()
                ARouter.getInstance().build(RouterConfig.REPORT_PREVIEW_FIRST)
                    .withParcelable(ExtraKeyConfig.REPORT_INFO, reportInfoBean)
                    .withParcelable(ExtraKeyConfig.REPORT_CONDITION, reportConditionBean)
                    .navigation(this)
            }
            binding.tvNext -> {//
                val reportInfoBean = buildReportInfo()
                val reportConditionBean = buildReportCondition()
                val imageTempBean: ImageTempBean? = intent.getParcelableExtra(ExtraKeyConfig.IMAGE_TEMP_BEAN)
                ARouter.getInstance().build(RouterConfig.REPORT_CREATE_SECOND)
                    .withBoolean(ExtraKeyConfig.IS_TC007, isTC007)
                    .withString(ExtraKeyConfig.FILE_ABSOLUTE_PATH, intent.getStringExtra(ExtraKeyConfig.FILE_ABSOLUTE_PATH))
                    .withParcelable(ExtraKeyConfig.IMAGE_TEMP_BEAN, imageTempBean)
                    .withParcelable(ExtraKeyConfig.REPORT_INFO, reportInfoBean)
                    .withParcelable(ExtraKeyConfig.REPORT_CONDITION, reportConditionBean)
                    .navigation(this)
            }
            binding.imgLocation -> {
                checkLocationPermission()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() : String? {
        //1.
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        //2.GPSNetWork
        val providers = locationManager?.getProviders(true)
        locationProvider = if (providers!!.contains(LocationManager.GPS_PROVIDER)) {
            //GPS
            LocationManager.GPS_PROVIDER
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //Network
            LocationManager.NETWORK_PROVIDER
        } else {
            return null
        }
        var location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location == null){
            location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        return if (location == null){
            null
        }else{
            getAddress(location)

        }
    }

    //:
    private fun getAddress(location: Location?): String {
        var result: List<Address?>? = null
        try {
            if (location != null) {
                val gc = Geocoder(this, Locale.getDefault())
                result = gc.getFromLocation(
                    location.latitude,
                    location.longitude, 1
                )
                Log.v("TAG", "：$result")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var str = ""
        if (result!=null && result.isNotEmpty()){
            result?.get(0)?.let {
                str += getNullString(it.adminArea)
                if (TextUtils.isEmpty(it.subLocality) && !str.contains(getNullString(it.subAdminArea))){
                    str += getNullString(it.subAdminArea)
                }
                if (!str.contains(getNullString(it.locality))){
                    str += getNullString(it.locality)
                }
                if (!str.contains(getNullString(it.subLocality))){
                    str += getNullString(it.subLocality)
                }
            }
        }
        return str
    }

    private fun getNullString(str : String?):String{
        return if (str.isNullOrEmpty()){
            ""
        }else{
            str
        }
    }

    private fun buildReportInfo(): ReportInfoBean = ReportInfoBean(
        binding.etReportName.text.toString(),
        binding.etReportAuthor.text.toString(),
        if (binding.switchReportAuthor.isChecked && binding.etReportAuthor.text.isNotEmpty()) 1 else 0,
        binding.tvReportDate.text.toString(),
        if (binding.switchReportDate.isChecked) 1 else 0,
        binding.etReportPlace.text.toString(),
        if (binding.switchReportPlace.isChecked && binding.etReportPlace.text.isNotEmpty()) 1 else 0,
        binding.etReportWatermark.text.toString(),
        if (binding.switchReportWatermark.isChecked && binding.etReportWatermark.text.isNotEmpty()) 1 else 0
    )

    private fun buildReportCondition(): ReportConditionBean {
        val temperature = try {
            "${binding.etAmbientTemperature.text.toString().toFloat()}${UnitTools.showUnit()}"
        } catch (ignore: NumberFormatException) {
            null
        }
        return ReportConditionBean(
            binding.tipSeekHumidity.valueText,
            if (binding.switchAmbientHumidity.isChecked) 1 else 0,
            temperature,
            if (binding.switchAmbientTemperature.isChecked && temperature != null) 1 else 0,
            binding.tipSeekEmissivity.valueText,
            if (binding.switchEmissivity.isChecked) 1 else 0,
            binding.etTestDistance.text.toString(),
            if (binding.switchTestDistance.isChecked && binding.etTestDistance.text.isNotEmpty()) 1 else 0
        )
    }



     * .
    private var startTime = 0L
    private fun selectTime() {
        val picker = DatimePicker(this)
        picker.setTitle(LibAppR.string.chart_start_time)
        picker.setOnDatimePickedListener { year, month, day, hour, minute, second ->
            val timeStr = "$year-$month-$day $hour:$minute:$second"
            val pattern = "yyyy-MM-dd HH:mm:ss"
            val time: Long = SimpleDateFormat(pattern, Locale.getDefault()).parse(timeStr, ParsePosition(0)).time
            binding.tvReportDate.text = TimeUtils.millis2String(time, "yyyy.MM.dd HH:mm")
            startTime = time
        }

        val startTimeEntity = DatimeEntity()
        startTimeEntity.date = DateEntity.target(2020, 1, 1)
        startTimeEntity.time = TimeEntity.target(0, 0, 0)

        val endTimeEntity = DatimeEntity.yearOnFuture(10)
        if (startTime == 0L) {
            picker.wheelLayout.setRange(startTimeEntity, endTimeEntity, DatimeEntity.now())
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = startTime
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            val seconds = calendar.get(Calendar.SECOND)
            val timeEntity = DatimeEntity()
            timeEntity.date = DateEntity.target(year, month, day)
            timeEntity.time = TimeEntity.target(hours, minutes, seconds)
            picker.wheelLayout.setRange(startTimeEntity, endTimeEntity, timeEntity)
        }
        picker.show()
    }

    private fun checkLocationPermission() {
        if (!XXPermissions.isGranted(this, permissionList)) {
            if (BaseApplication.instance.isDomestic()) {
                TipDialog.Builder(this)
                    .setMessage(getString(LibAppR.string.permission_request_location_app, CommUtils.getAppName()))
                    .setCancelListener(LibAppR.string.app_cancel)
                    .setPositiveListener(LibAppR.string.app_confirm) {
                        initLocationPermission()
                    }
                    .create().show()
            } else {
                initLocationPermission()
            }
        } else {
            initLocationPermission()
        }
    }

    private fun initLocationPermission() {
        XXPermissions.with(this@ReportCreateFirstActivity)
            .permission(
                permissionList
            ).request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (all){
                        showLoadingDialog(LibAppR.string.get_current_address)
                        lifecycleScope.launch{
                            var addressText : String ?= ""
                            withContext(Dispatchers.IO){
                                addressText = getLocation()
                            }
                            dismissLoadingDialog()
                            if (addressText == null){
                                TipDialog.Builder(this@ReportCreateFirstActivity)
                                    .setMessage(LibAppR.string.get_Location_failed)
                                    .setPositiveListener(LibAppR.string.app_ok)
                                    .setCanceled(false)
                                    .create().show()
                            }else{
                                binding.etReportPlace.setText(addressText)
                            }
                        }
                    }else{
                        ToastUtils.showShort(LibAppR.string.scan_ble_tip_authorize)
                    }
                }
                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        if (BaseApplication.instance.isDomestic()){
                            ToastUtils.showShort(getString(LibAppR.string.app_location_content))
                            return
                        }
                        TipDialog.Builder(this@ReportCreateFirstActivity)
                            .setTitleMessage(getString(LibAppR.string.app_tip))
                            .setMessage(getString(LibAppR.string.app_location_content))
                            .setPositiveListener(LibAppR.string.app_open){
                                XXPermissions.startPermissionActivity(this@ReportCreateFirstActivity, permissions);
                            }
                            .setCancelListener(LibAppR.string.app_cancel){
                            }
                            .setCanceled(true)
                            .create().show()
                    } else {
                        ToastUtils.showShort(LibAppR.string.scan_ble_tip_authorize)
                    }
                }

            })
    }
}