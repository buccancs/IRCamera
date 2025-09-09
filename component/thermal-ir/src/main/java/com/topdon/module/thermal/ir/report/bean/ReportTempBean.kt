package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportTempBean(
    val max_temperature: String?, //[CN_TEXT]High temperature，[CN_TEXT]
    val is_max_temperature: Int,  //[CN_TEXT]High temperature
    val min_temperature: String?, //[CN_TEXT]Low temperature，[CN_TEXT]
    val is_min_temperature: Int,  //[CN_TEXT]Low temperature

    val comment: String?,//[CN_TEXT]
    val is_comment: Int, //[CN_TEXT]

    val mean_temperature: String? = null,//[CN_TEXT]，[CN_TEXT]
    val is_mean_temperature: Int = 0,    //[CN_TEXT]

    val temperature: String? = null,//[CN_TEXT]，[CN_TEXT]
    val is_temperature: Int = 0,    //[CN_TEXT]
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
