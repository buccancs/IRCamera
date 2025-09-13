package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Report temp data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
@Parcelize
data class ReportTempBean(
 val max_temperature: String?, // UnitTemp，UnitConfiguration
 val is_max_temperature: Int, // WhetherTemp
 val min_temperature: String?, // UnitTemp，UnitConfiguration
 val is_min_temperature: Int, // WhetherTemp
 val comment: String?, // 
 val is_comment: Int, // Whether
 val mean_temperature: String? = null, // UnitTemp，UnitConfiguration
 val is_mean_temperature: Int = 0, // WhetherTemp
 val temperature: String? = null, // UnitTemperature，UnitConfiguration
 val is_temperature: Int = 0, // WhetherTemperature
) : Parcelable {
 constructor(temperature: String?, is_temperature: Int, comment: String?, is_comment: Int) : this(
 null,
 0,
 null,
 0,
 comment,
 is_comment,
 null,
 0,
 temperature,
 is_temperature,
 )

 fun isMaxOpen() = is_max_temperature == 1

 fun isMinOpen() = is_min_temperature == 1

 fun isAverageOpen() = is_mean_temperature == 1

 fun isExplainOpen() = is_comment == 1

 fun isTempOpen() = is_temperature == 1
}
