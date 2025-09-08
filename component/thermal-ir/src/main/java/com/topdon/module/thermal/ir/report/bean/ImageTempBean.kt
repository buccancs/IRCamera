package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

 * .
@Parcelize
data class ImageTempBean(
    /** full property */
    val full: TempBean?,//
    /** pointList property */
    val pointList: ArrayList<TempBean>,//
    /** lineList property */
    val lineList: ArrayList<TempBean>, //
    /** rectList property */
    val rectList: ArrayList<TempBean>, //
) : Parcelable {

    @Parcelize
    data class TempBean(
        val max: String,//，
        val min: String? = null,//，
        val average: String? = null,//，
    ) : Parcelable
}
