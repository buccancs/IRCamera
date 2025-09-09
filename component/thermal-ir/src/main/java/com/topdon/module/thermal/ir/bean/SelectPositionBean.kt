package com.topdon.module.thermal.ir.bean

import android.graphics.Point
import android.graphics.Rect
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * [CN_TEXT] [CN_TEXT]1[CN_TEXT] [CN_TEXT]2[CN_TEXT] [CN_TEXT].
 * @param type 1-[CN_TEXT] 2-[CN_TEXT] 3-[CN_TEXT]
 */
@Parcelize
data class SelectPositionBean(
    val type: Int = 0, //1-[CN_TEXT] 2-[CN_TEXT] 3-[CN_TEXT]
    val startPosition: Point = Point(),
    val endPosition: Point = Point(),
) : Parcelable {


    constructor(rect: Rect): this(3, Point(rect.left, rect.top), Point(rect.right, rect.bottom))

    fun getRect(): Rect = Rect(startPosition.x, startPosition.y, endPosition.x, endPosition.y)
}
