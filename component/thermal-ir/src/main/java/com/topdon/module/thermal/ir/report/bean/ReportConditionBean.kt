package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

 * .
 *  3 .
@Parcelize
data class ReportConditionBean(
    /** ambient_humidity property */
    val ambient_humidity: String?, //
    /** is_ambient_humidity property */
    val is_ambient_humidity: Int, //，0、 1、
    /** ambient_temperature property */
    val ambient_temperature: String?,//，
    /** is_ambient_temperature property */
    val is_ambient_temperature: Int, //，0、 1、
    /** emissivity property */
    val emissivity: String?, //
    /** is_emissivity property */
    val is_emissivity: Int, //，0、 1、
    /** test_distance property */
    val test_distance: String?, //
    /** is_test_distance property */
    val is_test_distance: Int, //，0、 1、
) : Parcelable