package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * dataPoint/Line/Areadata.
 */
@Parcelize
data class ImageTempBean(
    val full: TempBean?, // Data field
    val pointList: ArrayList<TempBean>, // Data field
    val lineList: ArrayList<TempBean>, // Data field
    val rectList: ArrayList<TempBean>, // Data field
) : Parcelable {
    @Parcelize
    data class TempBean(
        val max: String, // Data fieldHigh temperature，data
        val min: String? = null, // Data fieldLow temperature，data
        val average: String? = null, // Data field，data
    ) : Parcelable
}
