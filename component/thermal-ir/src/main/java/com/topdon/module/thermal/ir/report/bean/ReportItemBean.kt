package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import com.blankj.utilcode.util.GsonUtils
import kotlinx.parcelize.Parcelize

 * .
@Parcelize
data class ReportItemBean(
    /** testReportId property */
    val testReportId: String?,
    /** testInfo property */
    val testInfo: String?, // JSON
    /** testTime property */
    val testTime: String?,
    /** uploadTime property */
    val uploadTime: String?, //
    /** sn property */
    val sn: String?,
    /** url property */
    val url: String?,
    /** status property */
    val status: Int?
) : Parcelable {

    /** reportBean property */
    var reportBean: ReportBean? = null
        get() {
            if (field == null) {
                field = GsonUtils.fromJson(testInfo, ReportBean::class.java)
            }
            return field
        }

    /** isFirst property */
    var isFirst: Boolean = false
    /** isTitle property */
    var isTitle: Boolean = false
}