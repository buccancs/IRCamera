package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportTempBean(
    val max_temperature: String?, // Data fieldHigh temperature，data
    val is_max_temperature: Int,  // Data fieldHigh temperature
    val min_temperature: String?, // Data fieldLow temperature，data
    val is_min_temperature: Int,  // Data fieldLow temperature

    val comment: String?,// Data field
    val is_comment: Int, // Data field

    val mean_temperature: String? = null,// Data field，data
    val is_mean_temperature: Int = 0,    // Data field

    val temperature: String? = null,// Data field，data
    val is_temperature: Int = 0,    // Data field
): Parcelable {

    constructor(temperature: String?, is_temperature: Int, comment: String?, is_comment: Int): this(
        null,
        0,
        null,
        0,
        comment,
        is_comment,
        null,
        0,
        temperature,
        is_temperature
    )

    fun isMaxOpen() = is_max_temperature == 1
    fun isMinOpen() = is_min_temperature == 1
    fun isAverageOpen() = is_mean_temperature == 1
    fun isExplainOpen() = is_comment == 1

    fun isTempOpen() = is_temperature == 1
}
