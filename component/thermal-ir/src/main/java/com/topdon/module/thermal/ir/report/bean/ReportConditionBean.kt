package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * [Chinese text].
 *
 * [Chinese text] 3 [Chinese text]: [Chinese text], [Chinese text], [Chinese text].
 */
@Parcelize
data class ReportConditionBean(
    val ambient_humidity: String?,   //[Chinese text]
    val is_ambient_humidity: Int,    //[Chinese text], 0, [Chinese text] 1, [Chinese text]
    val ambient_temperature: String?,//[Chinese text]temperature, [Chinese text]
    val is_ambient_temperature: Int, //[Chinese text]temperature, 0, [Chinese text] 1, [Chinese text]
    val emissivity: String?,         //[Chinese text]
    val is_emissivity: Int,          //[Chinese text], 0, [Chinese text] 1, [Chinese text]
    val test_distance: String?,      //[Chinese text]
    val is_test_distance: Int,       //[Chinese text], 0, [Chinese text] 1, [Chinese text]
) : Parcelable