package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.topdon.lib.core.utils.CommUtils
import kotlinx.android.parcel.Parcelize

/**
 * [Chinese text].
 *
 * [Chinese text] 3 [Chinese text]: [Chinese text], [Chinese text], [Chinese text].
 */
@Parcelize
data class ReportInfoBean(
    val report_name: String?,    //[Chinese text]
    val report_author: String?, //[Chinese text]
    val is_report_author: Int,  //[Chinese text], 0, [Chinese text] 1, [Chinese text]
    val report_date: String?,   //[Chinese text]
    val is_report_date: Int,    //[Chinese text], 0, [Chinese text] 1, [Chinese text]
    val report_place: String?,  //[Chinese text]point
    val is_report_place: Int,   //[Chinese text]point, 0, [Chinese text] 1, [Chinese text]
    val report_watermark: String?,//[Chinese text]
    val is_report_watermark: Int, //[Chinese text], 0, [Chinese text] 1, [Chinese text]
) : Parcelable {

    val is_report_name: Int = 1//[Chinese text], 0, [Chinese text] 1, [Chinese text]
    val report_type: Int = 1     //[Chinese text], 1, pointline[Chinese text]
    val report_version: String = "V1.00"//[Chinese text], [Chinese text] V1.00
    val report_number: String = "${CommUtils.getAppName()}${System.currentTimeMillis()}"//[Chinese text], APP[Chinese text] + [Chinese text]
}