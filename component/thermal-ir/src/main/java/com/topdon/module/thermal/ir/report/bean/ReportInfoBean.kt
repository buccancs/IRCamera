package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.topdon.lib.core.utils.CommUtils
import kotlinx.parcelize.Parcelize

 * .
 *  3 .
@Parcelize
data class ReportInfoBean(
    val report_name: String?, //
    val report_author: String?, //
    val is_report_author: Int, //，0、 1、
    val report_date: String?, //
    val is_report_date: Int, //，0、 1、
    val report_place: String?, //
    val is_report_place: Int, //，0、 1、
    val report_watermark: String?,//
    val is_report_watermark: Int, //，0、 1、
) : Parcelable {

    val is_report_name: Int = 1//，0、 1、
    val report_type: Int = 1 //，1、
    val report_version: String = "V1.00"//， V1.00
    val report_number: String = "${CommUtils.getAppName()}${System.currentTimeMillis()}"//，APP +
}