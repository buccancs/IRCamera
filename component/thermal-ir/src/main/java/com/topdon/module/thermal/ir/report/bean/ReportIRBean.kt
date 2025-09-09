package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * [CN_TEXT].
 */
@Parcelize
data class ReportIRBean(
    var picture_id: String, //[CN_TEXT]Id
    var picture_url: String,//[CN_TEXT]URL
    val full_graph_data: ReportTempBean?,   //[CN_TEXT]
    val point_data: List<ReportTempBean>,  //[CN_TEXT]
    val line_data: List<ReportTempBean>,   //[CN_TEXT]
    val surface_data: List<ReportTempBean>,//[CN_TEXT]
) : Parcelable
