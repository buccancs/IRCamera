package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

 * .
@Parcelize
data class ReportIRBean(
    /** picture_id property */
    var picture_id: String, //Id
    /** picture_url property */
    var picture_url: String,//URL
    /** full_graph_data property */
    val full_graph_data: ReportTempBean?, //
    /** point_data property */
    val point_data: List<ReportTempBean>, //
    /** line_data property */
    val line_data: List<ReportTempBean>, //
    /** surface_data property */
    val surface_data: List<ReportTempBean>,//
) : Parcelable
