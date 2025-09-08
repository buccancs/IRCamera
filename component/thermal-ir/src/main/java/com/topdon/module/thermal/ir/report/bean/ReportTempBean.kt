package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportTempBean(
    val max_temperature: String?, //[Chinese text]high[Chinese text], [Chinese text]
    val is_max_temperature: Int,  //[Chinese text]high[Chinese text]
    val min_temperature: String?, //[Chinese text]low[Chinese text], [Chinese text]
    val is_min_temperature: Int,  //[Chinese text]low[Chinese text]

    val comment: String?,//[Chinese text]
    val is_comment: Int, //[Chinese text]

    val mean_temperature: String? = null,//[Chinese text], [Chinese text]
    val is_mean_temperature: Int = 0,    //[Chinese text]

    val temperature: String? = null,//[Chinese text]pointtemperature, [Chinese text]
    val is_temperature: Int = 0,    //[Chinese text]pointtemperature
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
