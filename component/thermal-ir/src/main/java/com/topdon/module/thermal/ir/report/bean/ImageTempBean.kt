package com.topdon.module.thermal.ir.report.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * [CN_TEXT]Point/Line/Area[CN_TEXT].
 */
@Parcelize
data class ImageTempBean(
    val full: TempBean?,//[CN_TEXT]
    val pointList: ArrayList<TempBean>,//[CN_TEXT]
    val lineList: ArrayList<TempBean>, //[CN_TEXT]
    val rectList: ArrayList<TempBean>, //[CN_TEXT]
) : Parcelable {

    @Parcelize
    data class TempBean(
        val max: String,//[CN_TEXT]High temperature，[CN_TEXT]
        val min: String? = null,//[CN_TEXT]Low temperature，[CN_TEXT]
        val average: String? = null,//[CN_TEXT]，[CN_TEXT]
    ) : Parcelable
}
