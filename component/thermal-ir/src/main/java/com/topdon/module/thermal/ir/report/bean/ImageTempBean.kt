package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * [Chinese text]pointline[Chinese text]temperature[Chinese text].
 */
@Parcelize
data class ImageTempBean(
    val full: TempBean?,//[Chinese text]
    val pointList: ArrayList<TempBean>,//point
    val lineList: ArrayList<TempBean>, //line
    val rectList: ArrayList<TempBean>, //[Chinese text]
) : Parcelable {

    @Parcelize
    data class TempBean(
        val max: String,//[Chinese text]high[Chinese text], [Chinese text]
        val min: String? = null,//[Chinese text]low[Chinese text], [Chinese text]
        val average: String? = null,//[Chinese text], [Chinese text]
    ) : Parcelable
}
