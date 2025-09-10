package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.topdon.lib.core.utils.CommUtils
import kotlinx.android.parcel.Parcelize

/**
 * data.
 *
 * data 3 data：data、data、Infrareddata.
 */
@Parcelize
data class ReportInfoBean(
    val report_name: String?,    // Data field
    val report_author: String?, // Data field
    val is_report_author: Int,  // Data field，0、data 1、data
    val report_date: String?,   // Data field
    val is_report_date: Int,    // Data field，0、data 1、data
    val report_place: String?,  // Data field
    val is_report_place: Int,   // Data field，0、data 1、data
    val report_watermark: String?,// Data field
    val is_report_watermark: Int, // Data field，0、data 1、data
) : Parcelable {

    val is_report_name: Int = 1// Data field，0、data 1、data
    val report_type: Int = 1     // Data fieldType，1、Point/Line/Areadata
    val report_version: String = "V1.00"// Data field，Currentdata V1.00
    val report_number: String = "${CommUtils.getAppName()}${System.currentTimeMillis()}"// Data field，APPdata + data
}