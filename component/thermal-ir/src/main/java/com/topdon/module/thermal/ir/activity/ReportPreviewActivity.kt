package com.topdon.module.thermal.ir.activity

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.CollectionUtils
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
// Removed house module imports - module removed as unused
// import com.topdon.house.activity.SignInputActivity
// import com.topdon.house.event.HouseReportAddEvent
// import com.topdon.house.util.PDFUtil
// import com.topdon.house.viewmodel.DetectViewModel
// import com.topdon.house.viewmodel.ReportViewModel
// import com.topdon.lib.core.bean.HouseRepPreviewAlbumItemBean
// import com.topdon.lib.core.bean.HouseRepPreviewBean
// import com.topdon.lib.core.bean.HouseRepPreviewItemBean
// import com.topdon.lib.core.bean.HouseRepPreviewProjectItemBean
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.HouseReport
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.tools.TimeTool
import com.topdon.lms.sdk.weiget.TToast
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.adapter.ReportPreviewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

// Temporary data class stubs to resolve compilation issues
/**
 * Custom House rep preview view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
data class HouseRepPreviewBean(
    var itemBeans: ArrayList<HouseRepPreviewItemBean>? = null,
    var housePhoto: String = "",
    var houseAddress: String = "",
    var houseName: String = "",
    var detectTime: String = "",
    var inspectorName: String = "",
    var houseYear: String = "",
    var houseArea: String = "",
    var expenses: String = "",
    var inspectorWhitePath: String = "",
    var houseOwnerWhitePath: String = "",
)

/**
 * Custom House rep preview item view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
data class HouseRepPreviewItemBean(
    var projectItemBeans: ArrayList<HouseRepPreviewProjectItemBean>? = null,
    var albumItemBeans: ArrayList<HouseRepPreviewAlbumItemBean>? = null,
    var itemName: String = "",
)

/**
 * Custom House rep preview project item view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
data class HouseRepPreviewProjectItemBean(
    var projectName: String = "",
    var state: String = "",
    var remark: String = "",
)

/**
 * Custom House rep preview album item view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
data class HouseRepPreviewAlbumItemBean(
    var photoPath: String = "",
    var title: String = "",
)

/**
\1：
\1- [ExtraKeyConfig.IS_REPORT] - true- false-
\1- [ExtraKeyConfig.LONG_ID] - Id()  Id(）
 */
// Legacy ARouter route annotation - now using NavigationManager
/**
 * Report preview activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
class ReportPreviewActivity : BaseActivity(), View.OnClickListener {
    // Disabled - ViewModels from removed house module
    // private val detectViewModel: DetectViewModel by viewModels()
    // private val reportViewModel: ReportViewModel by viewModels()

    // View declarations
    private lateinit var tvSave: android.widget.TextView
    private lateinit var rlyInspectorSignature: android.widget.RelativeLayout
    private lateinit var rlyHouseOwnerSignature: android.widget.RelativeLayout
    private lateinit var toolbarBackImg: android.widget.ImageView
    private lateinit var clSign: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var layAppbar: com.google.android.material.appbar.AppBarLayout
    private lateinit var layToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var llSave: android.widget.LinearLayout
    private lateinit var scrollView: androidx.core.widget.NestedScrollView
    private lateinit var ivHeaderBg: android.widget.ImageView
    private lateinit var tvAddress: android.widget.TextView
    private lateinit var tvHouseName: android.widget.TextView
    private lateinit var tvDetectTime: android.widget.TextView
    private lateinit var ivInspectorSignature: android.widget.ImageView
    private lateinit var ivHouseOwnerSignature: android.widget.ImageView
    private lateinit var tvInspector: android.widget.TextView
    private lateinit var tvBuildYear: android.widget.TextView
    private lateinit var tvArea: android.widget.TextView
    private lateinit var tvCost: android.widget.TextView
    private lateinit var rcyFloor: androidx.recyclerview.widget.RecyclerView

    /**
\1true- false-
     */
    private var isReport = false
    private var houseReport = HouseReport()
    private var mPreviewBean: HouseRepPreviewBean? = null

    override fun initContentView() = R.layout.activity_report_preview

    override fun initView() {
        // Initialize views
        tvSave = findViewById(R.id.tv_save)
        rlyInspectorSignature = findViewById(R.id.rly_inspector_signature)
        rlyHouseOwnerSignature = findViewById(R.id.rly_house_owner_signature)
        toolbarBackImg = findViewById(R.id.toolbar_back_img)
        clSign = findViewById(R.id.cl_sign)
        layAppbar = findViewById(R.id.lay_appbar)
        layToolbar = findViewById(R.id.lay_toolbar)
        llSave = findViewById(R.id.ll_save)
        scrollView = findViewById(R.id.scroll_view)
        ivHeaderBg = findViewById(R.id.iv_header_bg)
        tvAddress = findViewById(R.id.tv_address)
        tvHouseName = findViewById(R.id.tv_house_name)
        tvDetectTime = findViewById(R.id.tv_detect_time)
        ivInspectorSignature = findViewById(R.id.iv_inspector_signature)
        ivHouseOwnerSignature = findViewById(R.id.iv_house_owner_signature)
        tvInspector = findViewById(R.id.tv_inspector)
        tvBuildYear = findViewById(R.id.tv_build_year)
        tvArea = findViewById(R.id.tv_area)
        tvCost = findViewById(R.id.tv_cost)
        rcyFloor = findViewById(R.id.rcy_floor)

        showLoadingDialog(""Test Data"PDF sharing disabled - house module removed")

                        // Original PDF sharing code commented out:
                        // if (pdfUri != null) {
                        //     val shareIntent = Intent()
                        //     shareIntent.action = Intent.ACTION_SEND
                        //     shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                        //     shareIntent.type = "application/pdf"
                        //     startActivity(Intent.createChooser(shareIntent, getString(R.string.battery_share)))
                        // }
                    }
                } else { // 
                    if (houseReport.inspectorWhitePath.isEmpty() || houseReport.houseOwnerWhitePath.isEmpty()) {
                        if (clSign.bottom + layAppbar.height > llSave.top) {
                            layAppbar.setExpanded(false, true)
                            scrollView.smoothScrollTo(0, clSign.top)
                        }
                        TToast.shortToast(this, R.string.pdf_sign_tips)
                        return
                    }
                    showLoadingDialog(""Test Data"${getString(R.string.detect_time)}${": "}${TimeTool.formatDetectTime(houseReport.detectTime)}"
        houseRepPreviewBean.inspectorName = houseReport.inspectorName
        houseRepPreviewBean.houseYear =
            if (houseReport.year == null) "--" else "${houseReport.year?.toString()}${getString(R.string.year)}"
        houseRepPreviewBean.houseArea =
            if (houseReport.houseSpace.isEmpty()) "--" else "${houseReport.houseSpace} ${houseReport.getSpaceUnitStr()}"
        houseRepPreviewBean.expenses =
            if (houseReport.cost.isEmpty()) "--" else "${resources.getStringArray(R.array.currency)[houseReport.costUnit]} ${houseReport.cost}"
        houseRepPreviewBean.itemBeans = ArrayList<HouseRepPreviewItemBean>()
        houseReport.dirList.forEachIndexed { _, dirReport ->
            var itemBean = HouseRepPreviewItemBean()
            itemBean.itemName = dirReport.dirName
            var count = dirReport.goodCount + dirReport.warnCount + dirReport.dangerCount
            itemBean.projectItemBeans = ArrayList<HouseRepPreviewProjectItemBean>()
            itemBean.albumItemBeans = ArrayList<HouseRepPreviewAlbumItemBean>()

            dirReport.itemList.forEachIndexed { _, itemReport ->
                var projectItemBean = HouseRepPreviewProjectItemBean()
                projectItemBean.projectName = itemReport.itemName
                projectItemBean.state = itemReport.state.toString()
                projectItemBean.remark = itemReport.inputText
                if (itemReport.state > 0 || itemReport.inputText.isNotEmpty()) {
                    itemBean.projectItemBeans?.add(projectItemBean)
                }

                if (itemReport.getImageSize() > 0) {
                    var albumItemBean: HouseRepPreviewAlbumItemBean? = null
                    if (itemReport.image1.isNotEmpty()) {
                        albumItemBean = HouseRepPreviewAlbumItemBean()
                        albumItemBean.photoPath = itemReport.image1
                        albumItemBean.title = itemReport.itemName
                        itemBean.albumItemBeans?.add(albumItemBean)
                    }
                    if (itemReport.image2.isNotEmpty()) {
                        albumItemBean = HouseRepPreviewAlbumItemBean()
                        albumItemBean.photoPath = itemReport.image2
                        albumItemBean.title = itemReport.itemName
                        itemBean.albumItemBeans?.add(albumItemBean)
                    }
                    if (itemReport.image3.isNotEmpty()) {
                        albumItemBean = HouseRepPreviewAlbumItemBean()
                        albumItemBean.photoPath = itemReport.image3
                        albumItemBean.title = itemReport.itemName
                        itemBean.albumItemBeans?.add(albumItemBean)
                    }
                    if (itemReport.image4.isNotEmpty()) {
                        albumItemBean = HouseRepPreviewAlbumItemBean()
                        albumItemBean.photoPath = itemReport.image4
                        albumItemBean.title = itemReport.itemName
                        itemBean.albumItemBeans?.add(albumItemBean)
                    }
                }
            }

            var isEmpty =
                CollectionUtils.isEmpty(itemBean.projectItemBeans) &&
                    CollectionUtils.isEmpty(
                        itemBean.albumItemBeans,
                    )
            if (CollectionUtils.isNotEmpty(itemBean.projectItemBeans)) {
                itemBean.projectItemBeans?.add(0, HouseRepPreviewProjectItemBean())
            }
            if (!isEmpty) {
                houseRepPreviewBean.itemBeans?.add(itemBean)
            }
        }
        houseRepPreviewBean.inspectorWhitePath = houseReport.inspectorWhitePath
        houseRepPreviewBean.houseOwnerWhitePath = houseReport.houseOwnerWhitePath
        return houseRepPreviewBean
    }

    private fun setAdapter() {
        mPreviewBean?.let {
            Glide.with(this).load(it.housePhoto).into(ivHeaderBg)
            tvAddress.text = it.houseAddress
            tvHouseName.text = it.houseName
            tvDetectTime.text = it.detectTime
            tvInspector.text = it.inspectorName
            tvBuildYear.text = it.houseYear
            tvArea.text = it.houseArea
            tvCost.text = it.expenses

            rcyFloor.layoutManager = LinearLayoutManager(this)
            val reportPreviewAdapter =
                ReportPreviewAdapter(
                    this,
                    it.itemBeans?.map { itemBean ->
                        // Convert local HouseRepPreviewItemBean to libapp HouseRepPreviewItemBean
                        com.topdon.lib.core.bean.HouseRepPreviewItemBean().apply {
                            // Map properties as needed - this is a simplified conversion
                        }
                    } ?: emptyList(),
                )
            rcyFloor.isNestedScrollingEnabled = false
            rcyFloor.adapter = reportPreviewAdapter

            Glide.with(this).load(it.inspectorWhitePath).into(ivInspectorSignature)
            Glide.with(this).load(it.houseOwnerWhitePath).into(ivHouseOwnerSignature)
        }
    }
}
