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
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.gzuliyujiang.wheelpicker.DatimePicker
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
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.navigation.NavigationManager
import com.topdon.lib.core.tools.NumberTools
import com.topdon.lib.core.tools.UnitTools
import com.topdon.lib.core.utils.CommUtils
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.ImageTempBean
import com.topdon.module.thermal.ir.report.bean.ReportConditionBean
import com.topdon.module.thermal.ir.report.bean.ReportInfoBean
import com.topdon.module.thermal.ir.repository.ConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import com.topdon.lib.core.R as LibR

/**
\11（2）.
 *
\1
\1-  TC007: [ExtraKeyConfig.IS_TC007] （ambient temperature、emissivity）
\1- : [ExtraKeyConfig.FILE_ABSOLUTE_PATH] （interface，）
\1- full imagetemperaturedata: [ExtraKeyConfig.IMAGE_TEMP_BEAN] （interface，）
 */
// Legacy ARouter route annotation - now using NavigationManager
class ReportCreateFirstActivity : BaseActivity(), View.OnClickListener {
    /**
\1interface， TC007 device.
\1true-TC007 false-device
     */
    private var isTC007 = false
    private var locationManager: LocationManager? = null
    private var locationProvider: String? = null

    // Views - using findViewById pattern
    private val etReportName: android.widget.EditText by lazy { findViewById(R.id.et_report_name) }
    private val etReportAuthor: android.widget.EditText by lazy { findViewById(R.id.et_report_author) }
    private val switchReportAuthor: android.widget.Switch by lazy { findViewById(R.id.switch_report_author) }
    private val tvReportDate: android.widget.TextView by lazy { findViewById(R.id.tv_report_date) }
    private val switchReportDate: android.widget.Switch by lazy { findViewById(R.id.switch_report_date) }
    private val etReportPlace: android.widget.EditText by lazy { findViewById(R.id.et_report_place) }
    private val switchReportPlace: android.widget.Switch by lazy { findViewById(R.id.switch_report_place) }
    private val etReportWatermark: android.widget.EditText by lazy { findViewById(R.id.et_report_watermark) }
    private val switchReportWatermark: android.widget.Switch by lazy { findViewById(R.id.switch_report_watermark) }
    private val etAmbientTemperature: android.widget.EditText by lazy { findViewById(R.id.et_ambient_temperature) }
    private val tipSeekHumidity: com.topdon.lib.ui.widget.TipsSeekBar by lazy { findViewById(R.id.tip_seek_humidity) }
    private val switchAmbientHumidity: android.widget.Switch by lazy { findViewById(R.id.switch_ambient_humidity) }
    private val switchAmbientTemperature: android.widget.Switch by lazy { findViewById(R.id.switch_ambient_temperature) }
    private val tipSeekEmissivity: com.topdon.lib.ui.widget.TipsSeekBar by lazy { findViewById(R.id.tip_seek_emissivity) }
    private val switchEmissivity: android.widget.Switch by lazy { findViewById(R.id.switch_emissivity) }
    private val etTestDistance: android.widget.EditText by lazy { findViewById(R.id.et_test_distance) }
    private val switchTestDistance: android.widget.Switch by lazy { findViewById(R.id.switch_test_distance) }

    // Chart start time view not found in current layout - commented out for now
    // private val chartStartTime: android.widget.TextView by lazy { findViewById(R.id.chart_start_time) }
    private val tvAmbientTemperature: android.widget.TextView by lazy { findViewById(R.id.tv_ambient_temperature) }
    private val tvEmissivity: android.widget.TextView by lazy { findViewById(R.id.tv_emissivity) }

    override fun initContentView() = R.layout.activity_report_create_first

    private val permissionList =
        listOf(
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION,
        )

    @SuppressLint("SetTextI18n")
    override fun initView() {
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        etReportName.setText("TC${TimeUtils.millis2String(System.currentTimeMillis(), "yyyyMMdd_HHmm")}")
        etReportAuthor.setText(SaveSettingUtil.reportAuthorName)
        tvReportDate.text = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy.MM.dd HH:mm")
        etReportWatermark.setText(SaveSettingUtil.reportWatermarkText)
        tvAmbientTemperature.text = getString(R.string.thermal_config_environment) + "(${UnitTools.showUnit()})"
        tvEmissivity.text = getString(R.string.album_report_emissivity) + "(0~1)"

        etReportAuthor.addTextChangedListener {
            SaveSettingUtil.reportAuthorName = it?.toString() ?: ""
        }
        etReportWatermark.addTextChangedListener {
            SaveSettingUtil.reportWatermarkText = it?.toString() ?: ""
        }

        switchReportAuthor.setOnCheckedChangeListener { _, isChecked ->
            etReportAuthor.isVisible = isChecked
        }
        switchReportDate.setOnCheckedChangeListener { _, isChecked ->
            tvReportDate.isVisible = isChecked
        }
        switchReportPlace.setOnCheckedChangeListener { _, isChecked ->
            etReportPlace.isVisible = isChecked
        }
        switchReportWatermark.setOnCheckedChangeListener { _, isChecked ->
            etReportWatermark.isVisible = isChecked
        }
        switchAmbientHumidity.setOnCheckedChangeListener { _, isChecked ->
            tipSeekHumidity.isVisible = isChecked
        }
        switchAmbientTemperature.setOnCheckedChangeListener { _, isChecked ->
            etAmbientTemperature.isVisible = isChecked
        }
        switchEmissivity.setOnCheckedChangeListener { _, isChecked ->
            tipSeekEmissivity.isVisible = isChecked
        }
        switchTestDistance.setOnCheckedChangeListener { _, isChecked ->
            etTestDistance.isVisible = isChecked
        }
        tipSeekHumidity.progress = SaveSettingUtil.reportHumidity
        tipSeekHumidity.onStopTrackingTouch = {
            SaveSettingUtil.reportHumidity = it
        }
        tipSeekHumidity.valueFormatListener = {
            if (it % 10 == 0) "${it / 10}%" else "${it / 10}.${it % 10}%"
        }
        tipSeekEmissivity.valueFormatListener = {
            when (it) {
                0 -> "0"
                100 -> "1"
                else -> if (it < 10) "0.0$it" else "0.$it"
            }
        }

        tvReportDate.setOnClickListener(this)
        findViewById<android.widget.TextView>(R.id.tv_preview).setOnClickListener(this)
        findViewById<android.widget.TextView>(R.id.tv_next).setOnClickListener(this)
        findViewById<android.widget.ImageView>(R.id.img_location).setOnClickListener(this)

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
        etAmbientTemperature.setText(NumberTools.to01(UnitTools.showUnitValue(environment)))
        etTestDistance.setText(NumberTools.to02(distance) + "m")
        tipSeekEmissivity.progress = (radiation * 100).toInt()
    }

    override fun initData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReportCreate(event: ReportCreateEvent) {
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_report_date -> { // 
                selectTime()
            }
            R.id.tv_preview -> { // 
                val reportInfoBean = buildReportInfo()
                val reportConditionBean = buildReportCondition()
                NavigationManager.getInstance().build(RouterConfig.REPORT_PREVIEW_FIRST)
                    .withParcelable(ExtraKeyConfig.REPORT_INFO, reportInfoBean)
                    .withParcelable(ExtraKeyConfig.REPORT_CONDITION, reportConditionBean)
                    .navigation(this)
            }
            R.id.tv_next -> { // 
                val reportInfoBean = buildReportInfo()
                val reportConditionBean = buildReportCondition()
                val imageTempBean: ImageTempBean? = intent.getParcelableExtra(ExtraKeyConfig.IMAGE_TEMP_BEAN)
                val fileAbsolutePath = intent.getStringExtra(ExtraKeyConfig.FILE_ABSOLUTE_PATH)
                if (fileAbsolutePath != null && imageTempBean != null) {
                    NavigationManager.getInstance().build(RouterConfig.REPORT_CREATE_SECOND)
                        .withBoolean(ExtraKeyConfig.IS_TC007, isTC007)
                        .withString(ExtraKeyConfig.FILE_ABSOLUTE_PATH, fileAbsolutePath)
                        .withParcelable(ExtraKeyConfig.IMAGE_TEMP_BEAN, imageTempBean)
                        .withParcelable(ExtraKeyConfig.REPORT_INFO, reportInfoBean)
                        .withParcelable(ExtraKeyConfig.REPORT_CONDITION, reportConditionBean)
                        .navigation(this)
                }
            }
            R.id.img_location -> {
                checkLocationPermission()
            }
        }
    }

    @SuppressLint("MissingPermission"Test Data"TAG", "Test Data")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var str = ""
        if (result != null && result.isNotEmpty())
            {
                result?.get(0)?.let {
                    str += getNullString(it.adminArea)
                    if (TextUtils.isEmpty(it.subLocality) && !str.contains(getNullString(it.subAdminArea)))
                        {
                            str += getNullString(it.subAdminArea)
                        }
                    if (!str.contains(getNullString(it.locality)))
                        {
                            str += getNullString(it.locality)
                        }
                    if (!str.contains(getNullString(it.subLocality)))
                        {
                            str += getNullString(it.subLocality)
                        }
                }
            }
        return str
    }

    private fun getNullString(str: String?): String  {
        return if (str.isNullOrEmpty())
            {
                ""
            } else
            {
                str
            }
    }

    private fun buildReportInfo(): ReportInfoBean =
        ReportInfoBean(
            etReportName.text.toString(),
            etReportAuthor.text.toString(),
            if (switchReportAuthor.isChecked && etReportAuthor.text.isNotEmpty()) 1 else 0,
            tvReportDate.text.toString(),
            if (switchReportDate.isChecked) 1 else 0,
            etReportPlace.text.toString(),
            if (switchReportPlace.isChecked && etReportPlace.text.isNotEmpty()) 1 else 0,
            etReportWatermark.text.toString(),
            if (switchReportWatermark.isChecked && etReportWatermark.text.isNotEmpty()) 1 else 0,
        )

    private fun buildReportCondition(): ReportConditionBean {
        val temperature =
            try {
                "${etAmbientTemperature.text.toString().toFloat()}${UnitTools.showUnit()}"
            } catch (ignore: NumberFormatException) {
                null
            }
        return ReportConditionBean(
            tipSeekHumidity.valueText,
            if (switchAmbientHumidity.isChecked) 1 else 0,
            temperature,
            if (switchAmbientTemperature.isChecked && temperature != null) 1 else 0,
            tipSeekEmissivity.valueText,
            if (switchEmissivity.isChecked) 1 else 0,
            etTestDistance.text.toString(),
            if (switchTestDistance.isChecked && etTestDistance.text.isNotEmpty()) 1 else 0,
        )
    }

    /**
\1set.
     */
    private var startTime = 0L

    /**
\1display
     */
    private fun selectTime() {
        val picker = DatimePicker(this)
        picker.setTitle(R.string.chart_start_time)
        picker.setOnDatimePickedListener { year, month, day, hour, minute, second ->
            val timeStr = "$year-$month-$day $hour:$minute:$second"
            val pattern = "yyyy-MM-dd HH:mm:ss"
            val time: Long = SimpleDateFormat(pattern, Locale.getDefault()).parse(timeStr, ParsePosition(0)).time
            tvReportDate.text = TimeUtils.millis2String(time, "yyyy.MM.dd HH:mm"Test Data""
                                    withContext(Dispatchers.IO) {
                                        addressText = getLocation()
                                    }
                                    dismissLoadingDialog()
                                    if (addressText == null)
                                        {
                                            TipDialog.Builder(this@ReportCreateFirstActivity)
                                                .setMessage(LibR.string.get_Location_failed)
                                                .setPositiveListener(R.string.app_ok)
                                                .setCanceled(false)
                                                .create().show()
                                        } else
                                        {
                                            etReportPlace.setText(addressText)
                                        }
                                }
                            } else
                            {
                                ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                            }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        never: Boolean,
                    ) {
                        if (never) {
\1TextsetText
                            if (BaseApplication.instance.isDomestic())
                                {
                                    ToastUtils.showShort(getString(R.string.app_location_content))
                                    return
                                }
                            TipDialog.Builder(this@ReportCreateFirstActivity)
                                .setTitleMessage(getString(R.string.app_tip))
                                .setMessage(getString(R.string.app_location_content))
                                .setPositiveListener(R.string.app_open) {
                                    XXPermissions.startPermissionActivity(this@ReportCreateFirstActivity, permissions)
                                }
                                .setCancelListener(R.string.app_cancel) {
                                }
                                .setCanceled(true)
                                .create().show()
                        } else {
                            ToastUtils.showShort(R.string.scan_ble_tip_authorize)
                        }
                    }
                },
            )
    }
}
