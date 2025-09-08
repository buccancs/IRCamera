package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

 * .
@Parcelize
data class ReportBean(
    /** software_info property */
    val software_info: SoftwareInfo,
    /** report_info property */
    val report_info: ReportInfoBean,
    /** detection_condition property */
    val detection_condition: ReportConditionBean,
    /** infrared_data property */
    val infrared_data: List<ReportIRBean>
) : Parcelable
