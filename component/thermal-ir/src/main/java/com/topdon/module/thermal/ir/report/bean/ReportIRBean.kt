package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
\1data.
 */
/**
 * Report i r data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
@Parcelize
data class ReportIRBean(
    var picture_id: String, // Id
    var picture_url: String, // URL
    val full_graph_data: ReportTempBean?, // 
    val point_data: List<ReportTempBean>, // 
    val line_data: List<ReportTempBean>, // 
    val surface_data: List<ReportTempBean>, // 
) : Parcelable
