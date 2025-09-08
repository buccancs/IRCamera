package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

 * .
@Parcelize
data class ReportPageBean(
    /** total property */
    val total: Int = 0,
    /** current property */
    var current: Int = 0,
    /** pages property */
    var pages: Int = 0,
    /** size property */
    var size: Int = 0,
    /** isHitCount property */
    var isHitCount: Boolean = false,
    /** isOptimizeCountSql property */
    var isOptimizeCountSql: Boolean = false,
    /** isSearchCount property */
    var isSearchCount: Boolean = false,
    /** records property */
    var records: MutableList<ReportItemBean>? = null,
) : Parcelable