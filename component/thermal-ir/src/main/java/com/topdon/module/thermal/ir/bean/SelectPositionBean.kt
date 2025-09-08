package com.topdon.module.thermal.ir.bean

import android.graphics.Point
import android.graphics.Rect
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * temperature[Chinese text] [Chinese text]1[Chinese text] [Chinese text]2[Chinese text] [Chinese text]message.
 * @param type 1-point 2-line 3-[Chinese text]
 */
@Parcelize
data class SelectPositionBean(
    val type: Int = 0, // 1-point 2-line 3-[Chinese text]
    val startPosition: Point = Point(),
    val endPosition: Point = Point(),
) : Parcelable {

    constructor(rect: Rect): this(3, Point(rect.left, rect.top), Point(rect.right, rect.bottom))

    fun getRect(): Rect = Rect(startPosition.x, startPosition.y, endPosition.x, endPosition.y)
}
