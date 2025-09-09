package com.topdon.lib.core.repository

import android.graphics.Point
import android.graphics.Rect
import java.lang.NumberFormatException

// [CN_TEXT] TC007 [CN_TEXT] JSON [CN_TEXT]

/**
 * TC007 All[CN_TEXT].
 * @param Detail [CN_TEXT]，[CN_TEXT]
 * @param Data [CN_TEXT]，[CN_TEXT]
 */
data class TC007Response<T>(
    val Code: Int,
    val Message: String?,
    val Translate: String?,
    val Detail: String?,
    val Data: T?,
) {
    /**
     * [CN_TEXT].
     */
    fun isSuccess(): Boolean = Code == 200
}

/**
 * TC007 [CN_TEXT]：[CN_TEXT]
 * @param ProductName [CN_TEXT]
 * @param ProductPN PN
 * @param ProductSN SN
 * @param Code [CN_TEXT]
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
 * TC007 [CN_TEXT]：[CN_TEXT]
 * @param Status Charging-[CN_TEXT] Discharging-[CN_TEXT]
 * @param Remaining [CN_TEXT]
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
 * TC007 [CN_TEXT]：[CN_TEXT]State
 * @param Status Current[CN_TEXT]State 1-[CN_TEXT] 2-[CN_TEXT] 3-[CN_TEXT] 4-[CN_TEXT]
 * @param Percent Current[CN_TEXT]
 * @param Code [CN_TEXT]
 */
data class TC07UpgradeStatus(
    val Status: Int,
    val Percent: Int,
    val Code: Int,
)

/**
 * TC007 [CN_TEXT]：Temperature measurement[CN_TEXT]
 * @param Fps Temperature measurement[CN_TEXT][0,[CN_TEXT]]，[CN_TEXT]12，[CN_TEXT]12
 * @param Level Temperature measurement[CN_TEXT] 0-High gain 1-Low gain 3-[CN_TEXT]Switch
 * @param OsdMode Temperature measurement[CN_TEXT] 0-[CN_TEXT] 1-[CN_TEXT]([CN_TEXT]) 2-[CN_TEXT]
 * @param TempUnit [CN_TEXT] 0-Celsius 1-[CN_TEXT] 2-Fahrenheit
 * @param DistanceUnit [CN_TEXT] 0-[CN_TEXT] 1-[CN_TEXT]
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
 * @param DCFile Visible light[CN_TEXT]
 * @param IRFile Infrared[CN_TEXT]
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
 * [CN_TEXT]TC007[CN_TEXT]All[CN_TEXT]
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
    var brightness: Int = 50, // [CN_TEXT], 0-100, [CN_TEXT]50
    var contrast: Int = 50, // [CN_TEXT], 0-100, [CN_TEXT]50
    var saturation: Int = 50, // [CN_TEXT], 0-100, [CN_TEXT]50
    var sharpness: Int = 50, // [CN_TEXT], 0-100, [CN_TEXT]50
    var flipMode: Int = 0, // [CN_TEXT], 0:[CN_TEXT], 1:[CN_TEXT] 2:[CN_TEXT] 3:180[CN_TEXT]
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
    val mode: Int, // 0：[CN_TEXT]，1：[CN_TEXT]，2：[CN_TEXT]，3：[CN_TEXT]
    val highThreshold: Int,
    val lowThreshold: Int,
    var greaterThreshold: Int = 0,
    var lessThreshold: Int = 0,
)
