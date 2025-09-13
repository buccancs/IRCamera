package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
检测条件.
 *
report由 3 部Group成：reportinfo、检测条件、infrareddata.
 */
/**
 * Report condition data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
@Parcelize
data class ReportConditionBean(
    val ambient_humidity: String?, // 环境湿度
    val is_ambient_humidity: Int, // 是否Show/Display环境湿度，0、不Show/Display 1、Show/Display
    val ambient_temperature: String?, // 带单位符号的环境temperature，单位跟随Userconfiguration
    val is_ambient_temperature: Int, // 是否Show/Display环境temperature，0、不Show/Display 1、Show/Display
    val emissivity: String?, // 发射率
    val is_emissivity: Int, // 是否Show/Display发射率，0、不Show/Display 1、Show/Display
    val test_distance: String?, // Test距离
    val is_test_distance: Int, // 是否Show/DisplayTest距离，0、不Show/Display 1、Show/Display
) : Parcelable
