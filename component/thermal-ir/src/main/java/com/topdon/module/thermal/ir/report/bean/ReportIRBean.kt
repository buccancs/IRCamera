package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * [Chinese text].
 */
@Parcelize
data class ReportIRBean(
    var picture_id: String, //[Chinese text]Id
    var picture_url: String,//[Chinese text]URL
    val full_graph_data: ReportTempBean?,   //[Chinese text]
    val point_data: List<ReportTempBean>,  //point[Chinese text]
    val line_data: List<ReportTempBean>,   //line[Chinese text]
    val surface_data: List<ReportTempBean>,//[Chinese text]
) : Parcelable
