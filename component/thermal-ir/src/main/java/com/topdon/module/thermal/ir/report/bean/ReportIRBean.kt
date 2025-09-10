package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * data.
 */
@Parcelize
data class ReportIRBean(
    var picture_id: String, // Picture ID
    var picture_url: String,// Picture URL
    val full_graph_data: ReportTempBean?,   // Full temperature graph data
    val point_data: List<ReportTempBean>,  // Point temperature data
    val line_data: List<ReportTempBean>,   // Line temperature data
    val surface_data: List<ReportTempBean>,// Surface temperature data
) : Parcelable
