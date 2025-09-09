package com.topdon.house.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.UriUtils
import com.bumptech.glide.Glide
import com.github.gzuliyujiang.wheelpicker.DatimePicker
import com.github.gzuliyujiang.wheelpicker.StrArrayPicker
import com.github.gzuliyujiang.wheelpicker.YearPicker
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity
import com.topdon.house.R
import com.topdon.lib.core.R as LibR
import com.topdon.house.dialog.ImagePickFromDialog
import com.topdon.house.event.HouseDetectAddEvent
import com.topdon.house.event.HouseDetectEditEvent
import com.topdon.house.viewmodel.DetectViewModel
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.HouseDetect
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.PermissionTool
import com.topdon.lib.core.tools.SpanBuilder
import com.topdon.lib.core.utils.LocationUtil
import com.topdon.lms.sdk.weiget.TToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * [CN_TEXT].
 *
 * [CN_TEXT]：
 * - [ExtraKeyConfig.DETECT_ID] - [CN_TEXT]，[CN_TEXT] Id
 * - [ExtraKeyConfig.IS_TC007] - [CN_TEXT]，Current[CN_TEXT] TC007（[CN_TEXT]，[CN_TEXT]）
 *
 * Created by LCG on 2024/8/21.
 */
class DetectAddActivity : BaseActivity(), View.OnClickListener {
    private val viewModel: DetectViewModel  by viewModels()

    // View references for performance
    private val titleView by lazy { findViewById<com.topdon.lib.core.view.TitleView>(R.id.title_view) }
    private val tvCreateReport by lazy { findViewById<android.widget.TextView>(R.id.tv_create_report) }
    private val etDetectName by lazy { findViewById<android.widget.EditText>(R.id.et_detect_name) }
    private val etInspectorName by lazy { findViewById<android.widget.EditText>(R.id.et_inspector_name) }
    private val tvDetectTime by lazy { findViewById<android.widget.TextView>(R.id.tv_detect_time) }
    private val etHouseAddress by lazy { findViewById<android.widget.EditText>(R.id.et_house_address) }
    private val ivHouseImage by lazy { findViewById<android.widget.ImageView>(R.id.iv_house_image) }
    private val ivHouseImageCamera by lazy { findViewById<android.widget.ImageView>(R.id.iv_house_image_camera) }
    private val tvHouseImageCamera by lazy { findViewById<android.widget.TextView>(R.id.tv_house_image_camera) }
    private val tvHouseYear by lazy { findViewById<android.widget.TextView>(R.id.tv_house_year) }

    /**
     * [CN_TEXT]Mode[CN_TEXT]，[CN_TEXT]，[CN_TEXT] Id.
     */
    private var editId: Long = 0
    /**
     * Current[CN_TEXT].
     */
    private var houseDetect = HouseDetect()

    /**
     * Current[CN_TEXT].
     */
    private var inputDetectTime: Long? = null


    override fun initContentView(): Int = R.layout.activity_detect_add

    override fun initView() {
        editId = intent.getLongExtra(ExtraKeyConfig.DETECT_ID, 0)

        titleView.setTitleText(if (editId > 0) LibR.string.edit_detection_report else LibR.string.add_detection_report)
        titleView.setLeftClickListener { showExitTipsDialog() }

        tvCreateReport.setText(if (editId > 0) LibR.string.person_save else LibR.string.create_report)

        viewModel.detectLD.observe(this) {
            houseDetect = it ?: return@observe
            inputDetectTime = houseDetect.detectTime
            etDetectName.setText(houseDetect.name)
            etInspectorName.setText(houseDetect.inspectorName)
            tvDetectTime.text = TimeUtils.millis2String(houseDetect.detectTime, "yyyy-MM-dd HH:mm")
            etHouseAddress.setText(houseDetect.address)

            Glide.with(ivHouseImage).load(houseDetect.imagePath).into(ivHouseImage)
            ivHouseImageCamera.isVisible = false
            tvHouseImageCamera.isVisible = false

            tvHouseYear.text = houseDetect.year?.toString() ?: ""

            findViewById<android.widget.EditText>(R.id.et_house_space).setText(houseDetect.houseSpace)
            findViewById<android.widget.TextView>(R.id.tv_house_space_unit).text = resources.getStringArray(R.array.area)[houseDetect.houseSpaceUnit]

            findViewById<android.widget.EditText>(R.id.et_cost).setText(houseDetect.cost)
            findViewById<android.widget.TextView>(R.id.tv_cost_unit).text = resources.getStringArray(R.array.currency)[houseDetect.costUnit]
        }

        if (editId > 0) {
            viewModel.queryById(editId)
        } else {
            houseDetect.houseSpaceUnit = SharedManager.houseSpaceUnit
            houseDetect.costUnit = SharedManager.costUnit
            findViewById<android.widget.TextView>(R.id.tv_house_space_unit).text = resources.getStringArray(R.array.area)[houseDetect.houseSpaceUnit]
            findViewById<android.widget.TextView>(R.id.tv_cost_unit).text = resources.getStringArray(R.array.currency)[houseDetect.costUnit]
        }


        findViewById<android.widget.TextView>(R.id.tv_detect_time).setOnClickListener(this)
        findViewById<android.widget.ImageView>(R.id.iv_address_location).setOnClickListener(this)
        findViewById<android.widget.ImageView>(R.id.iv_house_image).setOnClickListener(this)
        findViewById<android.widget.TextView>(R.id.tv_house_year).setOnClickListener(this)
        findViewById<android.widget.TextView>(R.id.tv_house_space_unit).setOnClickListener(this)
        findViewById<android.widget.TextView>(R.id.tv_cost_unit).setOnClickListener(this)
        findViewById<android.widget.TextView>(R.id.tv_create_report).setOnClickListener(this)

        // [CN_TEXT]*[CN_TEXT]
        findViewById<android.widget.TextView>(R.id.tv_detect_name_title).text = SpanBuilder().appendColor("*", 0xffff4848.toInt()).append(getString(LibR.string.album_report_name))
        findViewById<android.widget.TextView>(R.id.tv_inspector_name_title).text = SpanBuilder().appendColor("*", 0xffff4848.toInt()).append(getString(LibR.string.inspector_name))
        findViewById<android.widget.TextView>(R.id.tv_detect_time_title).text = SpanBuilder().appendColor("*", 0xffff4848.toInt()).append(getString(LibR.string.detect_time))
        findViewById<android.widget.TextView>(R.id.tv_house_address_title).text = SpanBuilder().appendColor("*", 0xffff4848.toInt()).append(getString(LibR.string.house_detail_address))
        findViewById<android.widget.TextView>(R.id.tv_house_image_title).text = SpanBuilder().appendColor("*", 0xffff4848.toInt()).append(getString(LibR.string.house_image))

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitTipsDialog()
            }
        })
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v) {
            findViewById<android.widget.TextView>(R.id.tv_detect_time) -> {//[CN_TEXT]
                showDetectTimeDialog()
            }
            findViewById<android.widget.ImageView>(R.id.iv_address_location) -> {//[CN_TEXT]
                getLocation()
            }
            findViewById<android.widget.ImageView>(R.id.iv_house_image) -> {//[CN_TEXT]
                ImagePickFromDialog(this)
                    .setSelectListener {
                        if (it == 0) {//[CN_TEXT]
                            PermissionTool.requestImageRead(this) {
                                galleryPickResult.launch("image/*")
                            }
                        } else {
                            PermissionTool.requestCamera(this) {
                                val fileName = "Cover${System.currentTimeMillis()}.png"
                                val file = FileConfig.getDetectImageDir(this, fileName)
                                lightPhotoResult.launch(file)
                            }
                        }
                    }
                    .show()
            }
            findViewById<android.widget.TextView>(R.id.tv_house_year) -> {//[CN_TEXT]
                YearPicker(this, houseDetect.year).also {
                    it.setTitle(LibR.string.year_built)
                    it.setOnYearPickedListener { year ->
                        houseDetect.year = year
                        findViewById<android.widget.TextView>(R.id.tv_house_year).text = houseDetect.year?.toString()
                    }
                }.show()
            }
            findViewById<android.widget.TextView>(R.id.tv_house_space_unit) -> {//[CN_TEXT]
                StrArrayPicker(this, resources.getStringArray(R.array.area), SharedManager.houseSpaceUnit).also {
                    it.setTitle(LibR.string.area)
                    it.setOnOptionPickedListener { position, item ->
                        SharedManager.houseSpaceUnit = position
                        houseDetect.houseSpaceUnit = position
                        findViewById<android.widget.TextView>(R.id.tv_house_space_unit).text = item.toString()
                    }
                }.show()
            }
            findViewById<android.widget.TextView>(R.id.tv_cost_unit) -> {//[CN_TEXT]
                StrArrayPicker(this, resources.getStringArray(R.array.currency), SharedManager.costUnit).also {
                    it.setTitle(LibR.string.diagnosis_unit)
                    it.setOnOptionPickedListener { position, item ->
                        SharedManager.costUnit = position
                        houseDetect.costUnit = position
                        findViewById<android.widget.TextView>(R.id.tv_cost_unit).text = item.toString()
                    }
                }.show()
            }
            findViewById<android.widget.TextView>(R.id.tv_create_report) -> {//[CN_TEXT] or [CN_TEXT]
                val reportName = findViewById<android.widget.EditText>(R.id.et_detect_name).text.toString()
                if (reportName.isEmpty()) {
                    TToast.shortToast(this, LibR.string.album_report_input_name_tips)
                    return
                }

                val inspectorName = findViewById<android.widget.EditText>(R.id.et_inspector_name).text.toString()
                if (inspectorName.isEmpty()) {
                    TToast.shortToast(this, LibR.string.inspector_name_input_hint)
                    return
                }

                val detectTime = this.inputDetectTime
                if (detectTime == null) {
                    TToast.shortToast(this, LibR.string.please_select_detect_time)
                    return
                }

                val address = findViewById<android.widget.EditText>(R.id.et_house_address).text.toString()
                if (address.isEmpty()) {
                    TToast.shortToast(this, LibR.string.house_detail_address_input_hint)
                    return
                }

                if (houseDetect.imagePath.isEmpty()) {
                    TToast.shortToast(this, LibR.string.house_image_input_hint)
                    return
                }

                lifecycleScope.launch {
                    showLoadingDialog()
                    withContext(Dispatchers.IO) {
                        val currentTime = System.currentTimeMillis()
                        houseDetect.name = reportName
                        houseDetect.inspectorName = inspectorName
                        houseDetect.address = address
                        houseDetect.houseSpace = findViewById<android.widget.EditText>(R.id.et_house_space).text.toString()
                        houseDetect.cost = findViewById<android.widget.EditText>(R.id.et_cost).text.toString()
                        houseDetect.createTime = if (editId > 0) houseDetect.createTime else currentTime
                        houseDetect.updateTime = currentTime

                        if (editId > 0) {//[CN_TEXT]Mode
                            AppDatabase.getInstance().houseDetectDao().updateDetect(houseDetect)
                            EventBus.getDefault().post(HouseDetectEditEvent(houseDetect.id))
                        } else {
                            houseDetect.id = AppDatabase.getInstance().houseDetectDao().insert(houseDetect)
                            EventBus.getDefault().post(HouseDetectAddEvent())
                        }
                    }
                    if (editId == 0L) {
                        val newIntent = Intent(this@DetectAddActivity, ReportAddActivity::class.java)
                        newIntent.putExtra(ExtraKeyConfig.DETECT_ID, houseDetect.id)
                        newIntent.putExtra(ExtraKeyConfig.IS_TC007, intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false))
                        startActivity(newIntent)
                    }
                    dismissLoadingDialog()
                    finish()
                }
            }
        }
    }

    /**
     * [CN_TEXT] [CN_TEXT].
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        PermissionTool.requestLocation(this) {
            lifecycleScope.launch {
                showLoadingDialog(LibR.string.get_current_address)
                val addressText = LocationUtil.getLastLocationStr(this@DetectAddActivity)
                dismissLoadingDialog()
                if (addressText == null) {
                    TToast.shortToast(this@DetectAddActivity, LibR.string.get_Location_failed)
                } else {
                    houseDetect.address = addressText
                    findViewById<android.widget.EditText>(R.id.et_house_address).setText(addressText)
                }
            }
        }
    }

    /**
     * [CN_TEXT]
     */
    private val galleryPickResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val srcFile: File? = UriUtils.uri2File(it)
        if (srcFile != null) {
            val copyFile = FileConfig.getDetectImageDir(this, "Cover${System.currentTimeMillis()}.png")
            FileUtils.copy(srcFile, copyFile)
            houseDetect.imagePath = copyFile.absolutePath
            Glide.with(findViewById<android.widget.ImageView>(R.id.iv_house_image)).load(copyFile.absolutePath).into(findViewById<android.widget.ImageView>(R.id.iv_house_image))
            findViewById<android.widget.ImageView>(R.id.iv_house_image_camera).isVisible = false
            findViewById<android.widget.TextView>(R.id.tv_house_image_camera).isVisible = false
        }
    }

    /**
     * [CN_TEXT]Photo[CN_TEXT]
     */
    private val lightPhotoResult = registerForActivityResult(TakePhotoResult()) {
        if (it != null) {
            houseDetect.imagePath = it.absolutePath
            Glide.with(findViewById<android.widget.ImageView>(R.id.iv_house_image)).load(it.absolutePath).into(findViewById<android.widget.ImageView>(R.id.iv_house_image))
            findViewById<android.widget.ImageView>(R.id.iv_house_image_camera).isVisible = false
            findViewById<android.widget.TextView>(R.id.tv_house_image_camera).isVisible = false
        }
    }

    /**
     * [CN_TEXT]
     */
    private fun showExitTipsDialog() {
        TipDialog.Builder(this)
            .setMessage(LibR.string.diy_tip_save)
            .setPositiveListener(LibR.string.app_exit) {
                finish()
            }
            .setCancelListener(LibR.string.app_cancel)
            .create().show()
    }

    /**
     * [CN_TEXT]
     */
    private fun showDetectTimeDialog() {
        val picker = DatimePicker(this)
        picker.setTitle(LibR.string.detect_time)
        picker.setOnDatimePickedListener { year, month, day, hour, minute, second ->
            val timeStr = "$year-$month-$day $hour:$minute:$second"
            val pattern = "yyyy-MM-dd HH:mm:ss"
            val time: Long = SimpleDateFormat(pattern, Locale.getDefault()).parse(timeStr, ParsePosition(0))?.time ?: 0
            findViewById<android.widget.TextView>(R.id.tv_detect_time).text = TimeUtils.millis2String(time, "yyyy-MM-dd HH:mm")
            inputDetectTime = time
            houseDetect.detectTime = time
        }

        val calendar = Calendar.getInstance()
        val nowYear = calendar.get(Calendar.YEAR)

        val startTimeEntity = DatimeEntity()
        startTimeEntity.date = DateEntity.target(nowYear - 1000, 1, 1)

        val endTimeEntity = DatimeEntity.yearOnFuture(1000)

        val timeEntity = if (inputDetectTime == null) DatimeEntity.now() else DatimeEntity()
        if (inputDetectTime != null) {
            calendar.timeInMillis = inputDetectTime ?: 0
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            val seconds = calendar.get(Calendar.SECOND)
            timeEntity.date = DateEntity.target(year, month, day)
            timeEntity.time = TimeEntity.target(hours, minutes, seconds)
        }

        picker.wheelLayout.setRange(startTimeEntity, endTimeEntity, timeEntity)
        picker.show()
    }
}