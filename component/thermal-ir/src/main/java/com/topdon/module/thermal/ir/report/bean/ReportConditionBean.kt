package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * data.
 *
 * data 3 data：data、data、Infrareddata.
 */
@Parcelize
data class ReportConditionBean(
    val ambient_humidity: String?,   // Data field
    val is_ambient_humidity: Int,    // Data field，0、data 1、data
    val ambient_temperature: String?,// Data field，data
    val is_ambient_temperature: Int, // Data field，0、data 1、data
    val emissivity: String?,         // Data field
    val is_emissivity: Int,          // Data field，0、data 1、data
    val test_distance: String?,      // Data field
    val is_test_distance: Int,       // Data field，0、data 1、data
) : Parcelable