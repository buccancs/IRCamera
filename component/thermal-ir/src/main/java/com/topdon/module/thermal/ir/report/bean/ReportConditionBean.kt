package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * [CN_TEXT].
 *
 * [CN_TEXT] 3 [CN_TEXT]：[CN_TEXT]、[CN_TEXT]、Infrared[CN_TEXT].
 */
@Parcelize
data class ReportConditionBean(
    val ambient_humidity: String?,   //[CN_TEXT]
    val is_ambient_humidity: Int,    //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
    val ambient_temperature: String?,//[CN_TEXT]，[CN_TEXT]
    val is_ambient_temperature: Int, //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
    val emissivity: String?,         //[CN_TEXT]
    val is_emissivity: Int,          //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
    val test_distance: String?,      //[CN_TEXT]
    val is_test_distance: Int,       //[CN_TEXT]，0、[CN_TEXT] 1、[CN_TEXT]
) : Parcelable