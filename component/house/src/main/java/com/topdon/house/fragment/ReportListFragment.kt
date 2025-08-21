package com.topdon.house.fragment

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.FileUtils
import com.topdon.house.R
import com.topdon.house.activity.DetectAddActivity
import com.topdon.house.adapter.HouseAdapter
import com.topdon.house.dialog.InputTextDialog
import com.topdon.house.event.HouseReportAddEvent
import com.topdon.house.popup.ThreePickPopup
import com.topdon.house.util.PDFUtil
import com.topdon.house.viewmodel.ReportViewModel
import com.topdon.house.viewmodel.TabViewModel
import com.topdon.lib.core.config.ExtraKeyConfig
import com.topdon.lib.core.config.FileConfig
import com.topdon.lib.core.db.AppDatabase
import com.topdon.lib.core.db.entity.HouseReport
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.ktbase.BaseFragment
import com.topdon.lms.sdk.weiget.TToast
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 报告列表.
 *
 * 需要传递参数：
 * - [ExtraKeyConfig.IS_TC007] - 当前设备是否为 TC007（不使用，透传）
 *
 * Created by LCG on 2024/8/20.
 */
internal class ReportListFragment : BaseFragment(), View.OnClickListener {
    private lateinit var adapter: HouseAdapter

    private val tabViewModel: TabViewModel by activityViewModels()
    private val viewModel: ReportViewModel by activityViewModels()
    
    private lateinit var ivEmpty: ImageView
    private lateinit var tvEmpty: TextView
    private lateinit var tvAdd: TextView
    private lateinit var groupEmpty: Group
    private lateinit var clDel: ConstraintLayout
    private lateinit var ivDel: ImageView
    private lateinit var tvDel: TextView
    private lateinit var recyclerView: RecyclerView

    override fun initContentView(): Int = R.layout.fragment_report_list

    override fun initView() {
        ivEmpty = requireView().findViewById(R.id.iv_empty)
        tvEmpty = requireView().findViewById(R.id.tv_empty)
        tvAdd = requireView().findViewById(R.id.tv_add)
        groupEmpty = requireView().findViewById(R.id.group_empty)
        clDel = requireView().findViewById(R.id.cl_del)
        ivDel = requireView().findViewById(R.id.iv_del)
        tvDel = requireView().findViewById(R.id.tv_del)
        recyclerView = requireView().findViewById(R.id.recycler_view)
        
        clDel.isEnabled = false
        ivDel.isEnabled = false
        tvDel.isEnabled = false

        adapter = HouseAdapter(requireContext(), false)
        adapter.onItemClickListener = {
            // TODO: Replace RouterConfig reference with direct navigation
// TODO_FIX_AROUTER:                 .withBoolean(ExtraKeyConfig.IS_REPORT, true)
                .withLong(ExtraKeyConfig.LONG_ID, adapter.dataList[it].id)
// TODO_FIX_AROUTER:                 .navigation(requireContext())
        }
        adapter.onShareClickListener = {
            lifecycleScope.launch {
                showLoadingDialog()
                PDFUtil.delAllPDF(requireContext())
                val pdfUri: Uri? = PDFUtil.savePDF(requireContext(), adapter.dataList[it] as HouseReport)
                dismissLoadingDialog()
                if (pdfUri != null) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                    shareIntent.type = "application/pdf"
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.battery_share)))
                }
            }
        }
        adapter.onMoreClickListener = { position, v ->
            ThreePickPopup(requireContext(), arrayListOf(R.string.app_rename, R.string.report_delete)) {
                if (it == 0) {//重命名
                    val houseReport: HouseReport = adapter.dataList[position] as HouseReport
                    InputTextDialog(requireContext(), houseReport.name) { newName ->
                        if (houseReport.name != newName) {
                            FileUtils.delete(File(FileConfig.documentsDir, houseReport.getPdfFileName()))
                            houseReport.name = newName
                            viewModel.update(houseReport)
                            adapter.notifyItemChanged(position)
                        }
                    }.show()
                } else {//删除
                    TipDialog.Builder(requireContext())
                        .setTitleMessage(getString(R.string.monitor_report_delete))
                        .setMessage(R.string.report_delete_tips)
                        .setCancelListener(R.string.app_cancel)
                        .setPositiveListener(R.string.thermal_delete) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val houseReport: HouseReport = adapter.dataList[position] as HouseReport
                                AppDatabase.getInstance().houseReportDao().deleteReport(houseReport)
                                PDFUtil.delPDF(requireContext(), houseReport)
                                withContext(Dispatchers.Main) {
                                    adapter.dataList.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    if (adapter.dataList.isEmpty()) {
                                        viewModel.queryAll()
                                    }
                                    TToast.shortToast(requireContext(), R.string.test_results_delete_success)
                                }
                            }
                        }
                        .create().show()
                }
            }.show(v, false)
        }
        adapter.onSelectChangeListener = {
            tabViewModel.selectSizeLD.value = it
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        tvAdd.setOnClickListener(this)
        clDel.setOnClickListener(this)


        tabViewModel.isEditModeLD.observe(viewLifecycleOwner) {
            adapter.isEditMode = it
            clDel.isVisible = it
        }
        tabViewModel.selectSizeLD.observe(viewLifecycleOwner) {
            clDel.isEnabled = it > 0
            ivDel.isEnabled = it > 0
            tvDel.isEnabled = it > 0
        }

        viewModel.reportListLD.observe(viewLifecycleOwner) {
            groupEmpty.isVisible = it.isEmpty()
            recyclerView.isVisible = it.isNotEmpty()
            adapter.refresh(it)
        }
        viewModel.queryAll()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDetectCreate(event: HouseReportAddEvent) {
        viewModel.queryAll()
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v) {
            tvAdd -> {//添加
                val intent = Intent(requireContext(), DetectAddActivity::class.java)
                intent.putExtra(ExtraKeyConfig.IS_TC007, arguments?.getBoolean(ExtraKeyConfig.IS_TC007, false) ?: false)
                startActivity(intent)
            }
            clDel -> {//批量删除
                if (adapter.selectIndexList.isNotEmpty()) {
                    TipDialog.Builder(requireContext())
                        .setTitleMessage(getString(R.string.monitor_report_delete))
                        .setMessage(R.string.report_delete_tips)
                        .setCancelListener(R.string.app_cancel)
                        .setPositiveListener(R.string.thermal_delete) {
                            val resultArray: Array<HouseReport> = Array(adapter.selectIndexList.size) {
                                adapter.dataList[adapter.selectIndexList[it]] as HouseReport
                            }
                            viewModel.deleteMore(*resultArray)
                            tabViewModel.isEditModeLD.value = false
                            TToast.shortToast(requireContext(), R.string.test_results_delete_success)
                        }
                        .create().show()
                }
            }
        }
    }
}