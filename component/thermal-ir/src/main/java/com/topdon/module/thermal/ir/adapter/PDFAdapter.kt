package com.topdon.module.thermal.ir.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.topdon.lib.core.tools.GlideLoader
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.report.bean.ReportData

/**
 * @author: CaiSongL
 * @date: 2023/5/12 15:38
 */
/**
 * P d f adapter for thermal imaging data presentation.
 * Manages data binding and view recycling for efficient display.
 */
class PDFAdapter : BaseQuickAdapter<ReportData.Records?, BaseViewHolder>, LoadMoreModule {
    constructor(layoutResId: Int) : super(layoutResId) {}
    constructor(layoutResId: Int, data: MutableList<ReportData.Records?>?) : super(layoutResId, data) {}

    var delListener: ((item: ReportData.Records, position: Int) -> Unit)? = null
    var jumpDetailListener: ((item: ReportData.Records, position: Int) -> Unit)? = null

    override fun convert(
        baseViewHolder: BaseViewHolder,
        item: ReportData.Records?,
    ) {
        item?.let {
            if (it.isShowTitleTime)
                {
                    baseViewHolder.setVisible(R.id.item_message_read, true)
                    baseViewHolder.setGone(R.id.tv_time, false)
                    baseViewHolder.setText(R.id.tv_time, it.uploadTime?.split(" ")?.get(0))
                } else
                {
                    baseViewHolder.setVisible(R.id.item_message_read, false)
                    baseViewHolder.setGone(R.id.tv_time, true)
                }
            item?.reportContent?.infrared_data?.get(0)?.picture_url?.let { url ->
                GlideLoader.loadP(baseViewHolder.getView(R.id.img_content), url)
            }
            baseViewHolder.setText(R.id.item_pdf_title, item?.reportContent?.report_info?.report_name + "")
            baseViewHolder.setText(R.id.item_pdf_content, it.uploadTime + ""Test Data" ")
                val times = dataList[i]?.uploadTime?.split(" ")
                if (lastTimes?.size!! > 1 && times?.size!! > 1)
                    {
                        if (times[0] != lastTimes[0])
                            {
                                dataList[i]?.isShowTitleTime = true
                            }
                    }
            }
        }
    }
}
