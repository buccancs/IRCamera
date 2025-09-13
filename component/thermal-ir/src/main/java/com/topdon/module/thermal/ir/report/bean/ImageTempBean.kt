package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
\1full imagetemperature.
 */
/**
 * Image temp data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
@Parcelize
data class ImageTempBean(
    val full: TempBean?, // 
    val pointList: ArrayList<TempBean>, // 
    val lineList: ArrayList<TempBean>, // 
    val rectList: ArrayList<TempBean>, // 
) : Parcelable {
/**
 * Temp data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
    @Parcelize
    data class TempBean(
        val max: String, // ，
        val min: String? = null, // ，
        val average: String? = null, // ，
    ) : Parcelable
}
