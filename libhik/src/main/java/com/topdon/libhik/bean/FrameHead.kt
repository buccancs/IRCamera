package com.topdon.libhik.bean

import com.topdon.libhik.util.ByteArrayUtil.toFloat
import com.topdon.libhik.util.ByteArrayUtil.toInt

/**
 * [Chinese text]
 * @param tempWidth temperature[Chinese text]
 * @param tempHeight temperature[Chinese text]high[Chinese text]
 * @param yuvWidth YUV[Chinese text]
 * @param yuvHeight YUV[Chinese text]high[Chinese text]
 * @param tempType temperature[Chinese text]
 * @param minTemp [Chinese text]low[Chinese text]
 * @param maxTemp [Chinese text]high[Chinese text]
 * @param aveTemp [Chinese text]
 * @param maxX [Chinese text]high[Chinese text] X [Chinese text] [0, 1000]
 * @param maxY [Chinese text]high[Chinese text] Y [Chinese text] [0, 1000]
 * @param minX [Chinese text]low[Chinese text] X [Chinese text] [0, 1000]
 * @param minY [Chinese text]low[Chinese text] Y [Chinese text] [0, 1000]
 *
 * Created by LCG on 2024/11/22.
 */
data class FrameHead(
    val tempWidth: Int,
    val tempHeight: Int,
    val yuvWidth: Int,
    val yuvHeight: Int,
    val tempType: Int,
    val minTemp: Float,
    val maxTemp: Float,
    val aveTemp: Float,
    val maxX: Int,
    val maxY: Int,
    val minX: Int,
    val minY: Int,
    val isNormalTest: Boolean,
    val pointCount: Int,
    val rectCount: Int,
    val lineCount: Int,
    val totalCount: Int,
    val tempRuleList: ArrayList<TempRule>,
) {

    constructor(byteArray: ByteArray) : this(
        tempWidth = byteArray.toInt(64),
        tempHeight = byteArray.toInt(68),
        yuvWidth = byteArray.toInt(88),
        yuvHeight = byteArray.toInt(92),
        tempType = byteArray.toInt(120),
        minTemp = byteArray.toFloat(144),
        maxTemp = byteArray.toFloat(148),
        aveTemp = byteArray.toFloat(152),
        maxX = byteArray.toInt(156),
        maxY = byteArray.toInt(160),
        minX = byteArray.toInt(164),
        minY = byteArray.toInt(168),
        isNormalTest = byteArray.toInt(180) == 1,
        pointCount = byteArray[204].toInt() and 0xff,
        rectCount = byteArray[205].toInt() and 0xff,
        lineCount = byteArray[206].toInt() and 0xff,
        totalCount = byteArray[207].toInt() and 0xff,
        tempRuleList = byteArray.toRuleList(216),
    )

    companion object {
        /**
         * [Chinese text] index start, [Chinese text]
         */
        private fun ByteArray.toRuleList(index: Int): ArrayList<TempRule> = try {
            val resultList: ArrayList<TempRule> = ArrayList(21)
            for (i in 0 until 21) {
                // 208 [Chinese text] TempRule [Chinese text]
                if (this[index + i * 208] == 1.toByte()) {// [Chinese text]
                    resultList.add(TempRule(this, index + i * 208))
                }
            }
            resultList
        } catch (_: IndexOutOfBoundsException) {
            ArrayList(0)
        }
    }

    override fun toString(): String = "[Chinese text]:${tempWidth}x$tempHeight, ${yuvWidth}x$yuvHeight, " +
            "[Chinese text]low[Chinese text]:($minX,$minY) ${minTemp}degC, [Chinese text]high[Chinese text]:($maxX,$maxY) ${maxTemp}degC " +
            "[Chinese text]:${aveTemp}degC, ${if (isNormalTest) "[Chinese text]" else "[Chinese text]"}[Chinese text], " +
            "$pointCount + $rectCount + $lineCount = $totalCount"
}
