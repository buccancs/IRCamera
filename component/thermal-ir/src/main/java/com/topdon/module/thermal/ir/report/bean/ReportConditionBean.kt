package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
\1.
 *
\1 3 ：、、infrareddata.
 */
/**
 * Report condition data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
@Parcelize
data class ReportConditionBean(
    val ambient_humidity: String?, // 
    val is_ambient_humidity: Int, // ，0、 1、
    val ambient_temperature: String?, // ，
    val is_ambient_temperature: Int, // ，0、 1、
    val emissivity: String?, // 
    val is_emissivity: Int, // ，0、 1、
    val test_distance: String?, // 
    val is_test_distance: Int, // ，0、 1、
) : Parcelable
