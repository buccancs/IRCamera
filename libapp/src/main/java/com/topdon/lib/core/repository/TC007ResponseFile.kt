package com.topdon.lib.core.repository

import android.graphics.Point
import android.graphics.Rect
import java.lang.NumberFormatException

// [Chinese text] TC007 [Chinese text] JSON [Chinese text]

/**
 * TC007 [Chinese text].
 * @param Detail [Chinese text], [Chinese text]message
 * @param Data [Chinese text], [Chinese text]
 */
data class TC007Response<T>(
    val Code: Int,
    val Message: String?,
    val Translate: String?,
    val Detail: String?,
    val Data: T?,
) {
    /**
     * [Chinese text].
     */
    fun isSuccess(): Boolean = Code == 200
}

/**
 * TC007 [Chinese text]: [Chinese text]message
 * @param ProductName [Chinese text]
 * @param ProductPN PN
 * @param ProductSN SN
 * @param Code [Chinese text]
 */
data class ProductBean(
    val ProductName: String,
    val ProductPN: String,
    val ProductSN: String,
    val Code: String,
    val SoftwareVersion: Version07Bean?,
) {
    fun getVersionStr(): String = "${SoftwareVersion?.Major ?: "-"}.${SoftwareVersion?.Minor ?: "-"}${SoftwareVersion?.Build ?: "-"}"
}

data class Version07Bean(
    val Major: String?,
    val Minor: String?,
    val Build: String?,
)

/**
 * TC007 [Chinese text]: [Chinese text]message
 * @param Status Charging-[Chinese text]in progress Discharging-[Chinese text]
 * @param Remaining [Chinese text]
 */
data class BatteryInfo(
    val Status: String?,
    val Remaining: String?,
) {
    fun isCharging(): Boolean = Status == "Charging"

    fun getBattery(): Int? =
        try {
            Remaining?.toInt()
        } catch (_: NumberFormatException) {
            null
        }
}

/**
 * TC007 [Chinese text]: [Chinese text]
 * @param Status [Chinese text] 1-start[Chinese text] 2-[Chinese text]in progress 3-[Chinese text] 4-[Chinese text]
 * @param Percent [Chinese text]
 * @param Code [Chinese text]
 */
data class TC07UpgradeStatus(
    val Status: Int,
    val Percent: Int,
    val Code: Int,
)

/**
 * TC007 [Chinese text]: [Chinese text]
 * @param Fps [Chinese text][0,[Chinese text]], [Chinese text]12, [Chinese text]high[Chinese text]12
 * @param Level [Chinese text]level 0-high[Chinese text] 1-low[Chinese text] 3-[Chinese text]switch
 * @param OsdMode [Chinese text]message[Chinese text] 0-[Chinese text] 1-[Chinese text]message[Chinese text]([Chinese text]) 2-[Chinese text]
 * @param TempUnit temperature[Chinese text] 0-[Chinese text] 1-[Chinese text] 2-[Chinese text]
 * @param DistanceUnit [Chinese text] 0-[Chinese text] 1-[Chinese text]
 */
data class EnvAttr(
    val Fps: Int,
    val Level: Int,
    val OsdMode: Int,
    val TempUnit: Int,
    val DistanceUnit: Int,
)

data class FrameParam(
    var Enable: Boolean,
    val TempRule: TempRule,
)

data class TempRule(
    val AlarmRule: Int,
    val ThresholdTemp: Int,
    val Debounce: Int,
    val ToleranceTemp: Int,
    val TempRise: TempRise,
)

data class TempRise(
    var Enable: Boolean,
    var TRTemp: Int,
    var TRTime: Int,
    var TRNum: Int,
)

data class TempFrameParam(
    val FrameHigh: FrameParam,
    val FrameLow: FrameParam,
    val FrameCenter: FrameParam,
) {
//    constructor(isEnable: Boolean): this(FrameParam(isEnable), FrameParam(isEnable), FrameParam(isEnable))
}

internal data class PointParam(val X: Int, val Y: Int) {
    constructor(point: Point?) : this(point?.x ?: 0, point?.y ?: 0)
}

internal data class TargetParam(val Enable: Boolean)

internal data class TempPointParam(
    val Enable: Boolean,
    val ID: Int,
    val Name: String,
    val Point: PointParam,
    val Target: TargetParam,
) {
    constructor(id: Int, point: Point?) : this(
        Enable = point != null,
        ID = id,
        Name = "P$id",
        Point = PointParam(point?.x ?: 0, point?.y ?: 0),
        Target = TargetParam(true),
    )
}

internal data class TempLineParam(
    val Enable: Boolean,
    val ID: Int,
    val Name: String,
    val Line: LineParam,
    val Target: TargetParam,
) {
    constructor(id: Int, start: Point?, end: Point?) : this(
        Enable = start != null && end != null,
        ID = id,
        Name = "L$id",
        Line = LineParam(PointParam(start), PointParam(end)),
        Target = TargetParam(true),
    )

    data class LineParam(val Point0: PointParam, val Point1: PointParam)
}

internal data class TempRectParam(
    val Enable: Boolean,
    val ID: Int,
    val Name: String,
    val Rectangle: RectParam,
    val Target: TargetParam,
) {
    constructor(id: Int, rect: Rect?) : this(
        Enable = rect != null,
        ID = id,
        Name = "L$id",
        Rectangle = RectParam(rect),
        Target = TargetParam(true),
    )

    data class RectParam(val Point0: PointParam, val Point1: PointParam, val Point2: PointParam, val Point3: PointParam) {
        constructor(rect: Rect?) : this(
            Point0 = PointParam(rect?.left ?: 0, rect?.top ?: 0),
            Point1 = PointParam(rect?.right ?: 0, rect?.top ?: 0),
            Point2 = PointParam(rect?.left ?: 0, rect?.bottom ?: 0),
            Point3 = PointParam(rect?.right ?: 0, rect?.bottom ?: 0),
        )
    }
}

/**
 * @param DCFile visible[Chinese text]
 * @param IRFile [Chinese text]
 */
data class PhotoBean(
    val DCFile: String?,
    val IRFile: String?,
)

data class AttributeBean(
    var Fps: Int?,
    var Level: Int?,
    var TempUnit: Int?,
    var DistanceUnit: Int?,
)

/**
 * [Chinese text]TC007[Chinese text]
 */
data class WifiAttributeBean(
    var Ratio: Int? = null,
    var X: Int? = null,
    var Y: Int? = null,
)

data class PalleteBean(
    val palleteMode: Int,
    var stander: Stander? = null,
    var custom: Custom? = null,
)

data class Stander(
    var palleteNo: Int = 0,
    val threshold: List<Int>,
)

data class Custom(
    var customMode: Int,
    var highThreshold: Int,
    var lowThreshold: Int,
    var highColor: CustomColor,
    var middleColor: CustomColor,
    var lowColor: CustomColor,
)

data class CustomColor(
    var red: Int,
    var green: Int,
    var blue: Int,
)

data class Param(
    var brightness: Int = 50, // [Chinese text], 0-100, [Chinese text]50
    var contrast: Int = 50, // [Chinese text], 0-100, [Chinese text]50
    var saturation: Int = 50, // [Chinese text], 0-100, [Chinese text]50
    var sharpness: Int = 50, // [Chinese text], 0-100, [Chinese text]50
    var flipMode: Int = 0, // [Chinese text], 0:[Chinese text], 1:[Chinese text] 2:[Chinese text] 3:180[Chinese text]
)

data class Isotherm(
    val color: Long,
    val size: Int,
)

data class IsothermColor(
    val red: Int,
    val green: Int,
    val blue: Int,
)

data class IsothermC(
    val mode: Int, // 0: [Chinese text], 1: [Chinese text], 2: [Chinese text], 3: [Chinese text]
    val highThreshold: Int,
    val lowThreshold: Int,
    var greaterThreshold: Int = 0,
    var lessThreshold: Int = 0,
)
