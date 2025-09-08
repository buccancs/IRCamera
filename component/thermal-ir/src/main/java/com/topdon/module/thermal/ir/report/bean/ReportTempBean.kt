package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportTempBean(
    /** max_temperature property */
    val max_temperature: String?, //，
    /** is_max_temperature property */
    val is_max_temperature: Int, //
    /** min_temperature property */
    val min_temperature: String?, //，
    /** is_min_temperature property */
    val is_min_temperature: Int, //

    /** comment property */
    val comment: String?,//
    /** is_comment property */
    val is_comment: Int, //

    /** mean_temperature property */
    val mean_temperature: String? = null,//，
    /** is_mean_temperature property */
    val is_mean_temperature: Int = 0, //

    /** temperature property */
    val temperature: String? = null,//，
    /** is_temperature property */
    val is_temperature: Int = 0, //
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

    /**
     * Function description.
     */
    fun isMaxOpen() = is_max_temperature == 1
    /**
     * Function description.
     */
    fun isMinOpen() = is_min_temperature == 1
    /**
     * Function description.
     */
    fun isAverageOpen() = is_mean_temperature == 1
    /**
     * Function description.
     */
    fun isExplainOpen() = is_comment == 1

    /**
     * Function description.
     */
    fun isTempOpen() = is_temperature == 1
}
