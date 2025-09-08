package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.topdon.lib.core.utils.CommUtils
import kotlinx.parcelize.Parcelize

 * .
 *  3 .
@Parcelize
data class ReportInfoBean(
    /** report_name property */
    val report_name: String?, //
    /** report_author property */
    val report_author: String?, //
    /** is_report_author property */
    val is_report_author: Int, //，0、 1、
    /** report_date property */
    val report_date: String?, //
    /** is_report_date property */
    val is_report_date: Int, //，0、 1、
    /** report_place property */
    val report_place: String?, //
    /** is_report_place property */
    val is_report_place: Int, //，0、 1、
    /** report_watermark property */
    val report_watermark: String?,//
    /** is_report_watermark property */
    val is_report_watermark: Int, //，0、 1、
) : Parcelable {

    /** is_report_name property */
    val is_report_name: Int = 1//，0、 1、
    /** report_type property */
    val report_type: Int = 1 //，1、
    /** report_version property */
    val report_version: String = "V1.00"//， V1.00
    /** report_number property */
    val report_number: String = "${CommUtils.getAppName()}${System.currentTimeMillis()}"//，APP +
}