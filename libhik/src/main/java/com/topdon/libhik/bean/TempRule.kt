package com.topdon.libhik.bean

import android.graphics.Point
import com.topdon.libhik.util.ByteArrayUtil.toFloat
import com.topdon.libhik.util.ByteArrayUtil.toInt
import com.topdon.libhik.util.ByteArrayUtil.toStr

/**
 * [CN_TEXT]Temperature measurement[CN_TEXT]，[CN_TEXT] 208 byte.
 * @param enable [CN_TEXT]
 * @param regionId [CN_TEXT] Id
 * @param distance Temperature measurement[CN_TEXT]，[CN_TEXT] cm
 * @param regionType [CN_TEXT]Type 1-[CN_TEXT] 2-[CN_TEXT] 3-[CN_TEXT]
 * @param regionName [CN_TEXT]
 * @param emissivity [CN_TEXT]，[CN_TEXT] 97 [CN_TEXT] 0.97
 * @param minTemp [CN_TEXT]Low temperature，[CN_TEXT]Celsius
 * @param maxTemp [CN_TEXT]High temperature，[CN_TEXT]Celsius
 * @param aveTemp [CN_TEXT]，[CN_TEXT]Celsius
 * @param diffTemp [CN_TEXT]，[CN_TEXT]Celsius
 * @param maxX [CN_TEXT]High temperature[CN_TEXT] X [CN_TEXT] [0, 1000]
 * @param maxY [CN_TEXT]High temperature[CN_TEXT] Y [CN_TEXT] [0, 1000]
 * @param minX [CN_TEXT]Low temperature[CN_TEXT] X [CN_TEXT] [0, 1000]
 * @param minY [CN_TEXT]Low temperature[CN_TEXT] Y [CN_TEXT] [0, 1000]
 * @param pointCount [CN_TEXT]
 * @param pointList [CN_TEXT]、[CN_TEXT]、[CN_TEXT] [CN_TEXT]
 */
data class TempRule(
    val enable: Boolean,
    val regionId: Int,
    val distance: Int,
    val regionType: Int,
    val regionName: String,
    val emissivity: Int,
    val minTemp: Float,
    val maxTemp: Float,
    val aveTemp: Float,
    val diffTemp: Float,
    val maxX: Int,
    val maxY: Int,
    val minX: Int,
    val minY: Int,
    val pointCount: Int,
    val pointList: ArrayList<Point>,
) {
    constructor(byteArray: ByteArray, index: Int) : this(
        enable = byteArray[index] == 1.toByte(),
        regionId = byteArray[index + 1].toInt() and 0xff,
        distance = byteArray.toFloat(index + 28).toInt(),
        regionType = byteArray.toInt(index + 36),
        regionName = byteArray.toStr(index + 40, 32),
        emissivity = byteArray.toFloat(index + 72).toInt(),
        minTemp = byteArray.toFloat(index + 76),
        maxTemp = byteArray.toFloat(index + 80),
        aveTemp = byteArray.toFloat(index + 84),
        diffTemp = byteArray.toFloat(index + 88),
        maxX = byteArray.toInt(index + 92),
        maxY = byteArray.toInt(index + 96),
        minX = byteArray.toInt(index + 100),
        minY = byteArray.toInt(index + 104),
        pointCount = byteArray.toInt(index + 108),
        pointList = byteArray.toPointList(index + 112, byteArray.toInt(index + 108)),
    )

    companion object {
        /**
         * [CN_TEXT]Specified[CN_TEXT] index [CN_TEXT]，[CN_TEXT] count [CN_TEXT]
         */
        private fun ByteArray.toPointList(index: Int, count: Int): ArrayList<Point> = try {
            val resultList: ArrayList<Point> = ArrayList(count)
            for (i in 0 until count) {
                val x: Int = toInt(index + i * 8)
                val y: Int = toInt(index + i * 8 + 4)
                resultList.add(Point(x, y))
            }
            resultList
        } catch (_: IndexOutOfBoundsException) {
            ArrayList(0)
        }
    }

    override fun toString(): String = "[CN_TEXT]$regionId ${if (enable) "[CN_TEXT]" else "[CN_TEXT]"} " +
            "[CN_TEXT]:$distance cm，Type$regionType，" +
            "[CN_TEXT]:$emissivity，[CN_TEXT]:$regionName，" +
            "[CN_TEXT]Low temperature:($minX,$minY) ${minTemp}°C，" +
            "[CN_TEXT]High temperature:($maxX,$maxY) ${maxTemp}°C，" +
            "[CN_TEXT]:${aveTemp}°C，" +
            "[CN_TEXT]:$pointCount"
}
