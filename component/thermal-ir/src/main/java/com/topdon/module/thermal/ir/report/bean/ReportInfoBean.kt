package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.topdon.lib.core.utils.CommUtils
import kotlinx.android.parcel.Parcelize

/**
 * [CN_TEXT].
 *
 * [CN_TEXT] 3 [CN_TEXT]：[CN_TEXT]、[CN_TEXT]、Infrared[CN_TEXT].
 */
@Parcelize
data class ReportInfoBean(
    val report_name: String?,    //[CN_TEXT]
    val report_author: String?, //[CN_TEXT]
    val is_report_author: Int,  //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
    val report_date: String?,   //[CN_TEXT]
    val is_report_date: Int,    //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
    val report_place: String?,  //[CN_TEXT]
    val is_report_place: Int,   //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
    val report_watermark: String?,//[CN_TEXT]
    val is_report_watermark: Int, //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
) : Parcelable {

    val is_report_name: Int = 1//[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
    val report_type: Int = 1     //[CN_TEXT]Type，1、Point/Line/Area[CN_TEXT]
    val report_version: String = "V1.00"//[CN_TEXT]，Current[CN_TEXT] V1.00
    val report_number: String = "${CommUtils.getAppName()}${System.currentTimeMillis()}"//[CN_TEXT]，APP[CN_TEXT] + [CN_TEXT]
}