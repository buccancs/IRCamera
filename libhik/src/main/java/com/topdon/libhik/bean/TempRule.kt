package com.topdon.libhik.bean

import android.graphics.Point
import com.topdon.libhik.util.ByteArrayUtil.toFloat
import com.topdon.libhik.util.ByteArrayUtil.toInt
import com.topdon.libhik.util.ByteArrayUtil.toStr

/**
 * [Chinese text], [Chinese text] 208 byte.
 * @param enable [Chinese text]
 * @param regionId area Id
 * @param distance [Chinese text], [Chinese text] cm
 * @param regionType area[Chinese text] 1-point 2-line 3-[Chinese text]
 * @param regionName area[Chinese text]
 * @param emissivity [Chinese text], [Chinese text] 97 [Chinese text] 0.97
 * @param minTemp [Chinese text]low[Chinese text], [Chinese text]
 * @param maxTemp [Chinese text]high[Chinese text], [Chinese text]
 * @param aveTemp [Chinese text], [Chinese text]
 * @param diffTemp [Chinese text], [Chinese text]
 * @param maxX area[Chinese text]high[Chinese text] X [Chinese text] [0, 1000]
 * @param maxY area[Chinese text]high[Chinese text] Y [Chinese text] [0, 1000]
 * @param minX area[Chinese text]low[Chinese text] X [Chinese text] [0, 1000]
 * @param minY area[Chinese text]low[Chinese text] Y [Chinese text] [0, 1000]
 * @param pointCount [Chinese text]point[Chinese text]
 * @param pointList point, line, [Chinese text] [Chinese text]point[Chinese text]
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
         * [Chinese text] index start, [Chinese text] count [Chinese text]point[Chinese text]
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

    override fun toString(): String = "[Chinese text]$regionId ${if (enable) "[Chinese text]" else "[Chinese text]"} " +
            "[Chinese text]:$distance cm, [Chinese text]$regionType, " +
            "[Chinese text]:$emissivity, [Chinese text]:$regionName, " +
            "[Chinese text]low[Chinese text]:($minX,$minY) ${minTemp}degC, " +
            "[Chinese text]high[Chinese text]:($maxX,$maxY) ${maxTemp}degC, " +
            "[Chinese text]:${aveTemp}degC, " +
            "[Chinese text]point[Chinese text]:$pointCount"
}
