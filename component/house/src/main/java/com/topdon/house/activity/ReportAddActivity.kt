package com.topdon.house.activity

import android.content.Intent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.topdon.lib.core.navigation.NavigationManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.UriUtils
import com.topdon.house.R
import com.topdon.lib.core.R as LibR
import com.topdon.house.event.DetectDirListEvent
import com.topdon.house.event.DetectItemListEvent
import com.topdon.house.event.HouseReportAddEvent
import com.topdon.house.popup.ThreePickPopup
import com.topdon.house.viewmodel.DetectViewModel
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.db.entity.HouseDetect
import com.topdon.lib.core.db.entity.ItemDetect
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.socket.WebSocketProxy
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.tools.PermissionTool
import com.topdon.house.view.HouseDetectView
import com.topdon.lms.sdk.weiget.TToast
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * [Chinese text]([Chinese text]).
 *
 * [Chinese text]: 
 * - [ExtraKeyConfig.DETECT_ID] - [Chinese text] Id
 * - [ExtraKeyConfig.IS_TC007] - [Chinese text] TC007
 *
 * Created by LCG on 2024/8/23.
 */
class ReportAddActivity : BaseActivity(), View.OnClickListener {
    /**
     * [Chinese text], [Chinese text] TC007 [Chinese text].
     * true-TC007 false-[Chinese text]
     */
    private var isTC007 = false

    /**
     * [Chinese text].
     * true-[Chinese text] false-[Chinese text]1[Chinese text]
     */
    private var isAllExpand = false

    private val viewModel: DetectViewModel by viewModels()
    
    // View references
    private lateinit var ivExpand: View
    private lateinit var ivBack: View  
    private lateinit var ivEdit: View
    private lateinit var tvAdd: View
    private lateinit var tvExportReport: View
    private lateinit var clEmpty: View
    private lateinit var viewHouseDetect: HouseDetectView

    override fun initContentView(): Int = R.layout.activity_report_add

    override fun initView() {
        isTC007 = intent.getBooleanExtra(ExtraKeyConfig.IS_TC007, false)

        // Initialize view references
        ivExpand = findViewById(R.id.iv_expand)
        ivBack = findViewById(R.id.iv_back)
        ivEdit = findViewById(R.id.iv_edit) 
        tvAdd = findViewById(R.id.tv_add)
        tvExportReport = findViewById(R.id.tv_export_report)
        clEmpty = findViewById(R.id.cl_empty)
        viewHouseDetect = findViewById(R.id.view_house_detect)

        ivExpand.isEnabled = false
        ivBack.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        ivExpand.setOnClickListener(this)
        tvAdd.setOnClickListener(this)
        tvExportReport.setOnClickListener(this)

        initDetectViewListener()

        viewModel.detectLD.observe(this) {
            // [Chinese text]
            if (it != null) {
                isAllExpand = false

                clEmpty.isVisible = it.dirList.isEmpty()
                viewHouseDetect.isVisible = it.dirList.isNotEmpty()
                tvExportReport.isVisible = it.dirList.isNotEmpty()

                viewHouseDetect.refresh(it.dirList)

                ivEdit.isEnabled = it.dirList.isNotEmpty()
                ivExpand.isEnabled = it.dirList.isNotEmpty()
                ivExpand.isSelected = isAllExpand
            }
        }
        viewModel.copyDirLD.observe(this) {
            // [Chinese text]
            viewHouseDetect.notifyDirInsert(it.first, it.second)
            TToast.shortToast(this@ReportAddActivity, LibR.string.ts004_copy_success)
        }
        viewModel.copyItemLD.observe(this) {
            // [Chinese text]
            viewHouseDetect.notifyItemInsert(it.first, it.second)
            TToast.shortToast(this@ReportAddActivity, LibR.string.ts004_copy_success)
        }
        viewModel.delItemLD.observe(this) {
            // [Chinese text]
            viewHouseDetect.notifyItemRemove(it.first, it.second)
            TToast.shortToast(this@ReportAddActivity, LibR.string.test_results_delete_success)
        }

        viewModel.queryById(intent.getLongExtra(ExtraKeyConfig.DETECT_ID, 0))
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> finish()
            ivEdit -> {// [Chinese text]
                val newIntent = Intent(this, DirEditActivity::class.java)
                newIntent.putExtra(ExtraKeyConfig.DETECT_ID, intent.getLongExtra(ExtraKeyConfig.DETECT_ID, 0))
                startActivity(newIntent)
            }
            ivExpand -> {// [Chinese text]
                isAllExpand = !isAllExpand
                if (isAllExpand) {
                    viewHouseDetect.expandAllDir()
                } else {
                    viewHouseDetect.retractAllDir()
                }
                ivExpand.isSelected = isAllExpand
            }
            tvExportReport -> {// [Chinese text]
                NavigationManager.getInstance().build(RouterConfig.REPORT_PREVIEW)
                    .withBoolean(ExtraKeyConfig.IS_REPORT, false)
                    .withLong(ExtraKeyConfig.LONG_ID, intent.getLongExtra(ExtraKeyConfig.DETECT_ID, 0))
                    .navigation(this)
            }
            tvAdd -> {// [Chinese text]
                val detect: HouseDetect? = viewModel.detectLD.value
                if (detect != null) {
                    viewModel.insertDefaultDirs(detect)
                }
            }
        }
    }

    /**
     * [Chinese text], [Chinese text] viewHouseDetect in progress[Chinese text] index.
     */
    private var editLayoutIndex = 0
    /**
     * [Chinese text]
     */
    private var editItemDetect = ItemDetect()

    /**
     * [Chinese text] viewHouseDetect [Chinese text]eventlistener.
     */
    private fun initDetectViewListener() {
        viewHouseDetect.onDirCopyListener = {// [Chinese text]
            viewModel.copyDir(it.first, it.second)
        }
        viewHouseDetect.onItemCopyListener = {// [Chinese text]
            viewModel.copyItem(it.first, it.second)
        }
        viewHouseDetect.onItemDelListener = {
            viewModel.delItem(it.first, it.second)
        }

        viewHouseDetect.onImageAddListener = { layoutIndex, v, item ->
            // [Chinese text]
            editLayoutIndex = layoutIndex
            editItemDetect = item
            ThreePickPopup(this, arrayListOf(LibR.string.person_headshot_phone, LibR.string.light_camera_take_photo, LibR.string.ir_camera_take_photo)) {
                when (it) {
                    0 -> {// [Chinese text]
                        PermissionTool.requestImageRead(this) {
                            galleryPickResult.launch("image/*")
                        }
                    }
                    1 -> {// [Chinese text]Photo capture
                        PermissionTool.requestCamera(this) {
                            val fileName = "Item${System.currentTimeMillis()}.png"
                            val file = FileConfig.getDetectImageDir(this, fileName)
                            lightPhotoResult.launch(file)
                        }
                    }
                    2 -> {// [Chinese text]linePhoto capture
                        if ((isTC007 && !WebSocketProxy.getInstance().isTC007Connect()) || (!isTC007 && !DeviceTools.isConnect())) {
                            TToast.shortToast(this@ReportAddActivity, LibR.string.device_disconnect)
                        } else {
                            val fileName = "Item${System.currentTimeMillis()}.png"
                            val file = FileConfig.getDetectImageDir(this, fileName)
                            NavigationManager.jumpImagePick(this@ReportAddActivity, isTC007, file.absolutePath)
                        }
                    }
                }
            }.show(v, true)
        }
        viewHouseDetect.onTextInputListener = {
            // [Chinese text]text[Chinese text]
            editLayoutIndex = it.first
            editItemDetect = it.second
            val intent = Intent(this, TextInputActivity::class.java)
            intent.putExtra(ExtraKeyConfig.ITEM_NAME, it.second.itemName)
            intent.putExtra(ExtraKeyConfig.RESULT_INPUT_TEXT, it.second.inputText)
            textInputResult.launch(intent)
        }

        viewHouseDetect.onDirChangeListener = {
            // [Chinese text](3[Chinese text])
            viewModel.updateDir(it)
        }
        viewHouseDetect.onDirExpandListener = {
            // [Chinese text]
            if (it) {
                if (!isAllExpand) {
                    val detect: HouseDetect? = viewModel.detectLD.value
                    if (detect != null) {
                        isAllExpand = true
                        for (dir in detect.dirList) {
                            if (!dir.isExpand) {
                                isAllExpand = false
                                break
                            }
                        }
                    }
                }
            } else {
                isAllExpand = false
            }
            ivExpand.isSelected = isAllExpand
        }
        viewHouseDetect.onItemChangeListener = {
            // [Chinese text](3[Chinese text], [Chinese text])
            viewModel.updateItem(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReportCreate(event: HouseReportAddEvent) {
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReportCreate(event: DetectDirListEvent) {
        // [Chinese text], [Chinese text]
        viewModel.queryById(intent.getLongExtra(ExtraKeyConfig.DETECT_ID, 0))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReportCreate(event: DetectItemListEvent) {
        // [Chinese text], [Chinese text]
        viewModel.queryById(intent.getLongExtra(ExtraKeyConfig.DETECT_ID, 0))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 101) {
            val absolutePath: String = data?.getStringExtra(ExtraKeyConfig.RESULT_IMAGE_PATH) ?: return
            editItemDetect.addOneImage(absolutePath)
            viewModel.updateItem(editItemDetect)
            viewHouseDetect.notifyItemChange(editLayoutIndex)
        }
    }

    /**
     * [Chinese text]
     */
    private val galleryPickResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val srcFile: File? = UriUtils.uri2File(it)
        if (srcFile != null) {
            val copyFile = FileConfig.getDetectImageDir(this, "Item${System.currentTimeMillis()}.png")
            FileUtils.copy(srcFile, copyFile)
            editItemDetect.addOneImage(copyFile.absolutePath)
            viewModel.updateItem(editItemDetect)
            viewHouseDetect.notifyItemChange(editLayoutIndex)
        }
    }

    /**
     * [Chinese text]Photo capture[Chinese text]
     */
    private val lightPhotoResult = registerForActivityResult(TakePhotoResult()) {
        if (it != null) {
            editItemDetect.addOneImage(it.absolutePath)
            viewModel.updateItem(editItemDetect)
            viewHouseDetect.notifyItemChange(editLayoutIndex)
        }
    }

    /**
     * [Chinese text]text[Chinese text]
     */
    private val textInputResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val inputText: String = it.data?.getStringExtra(ExtraKeyConfig.RESULT_INPUT_TEXT) ?: ""
            if (editItemDetect.inputText != inputText) {// [Chinese text], [Chinese text]
                editItemDetect.inputText = inputText
                viewModel.updateItem(editItemDetect)
                viewHouseDetect.notifyItemChange(editLayoutIndex)
            }
        }
    }
}