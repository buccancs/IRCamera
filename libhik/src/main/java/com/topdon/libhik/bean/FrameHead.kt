package com.topdon.libhik.bean

import com.topdon.libhik.util.ByteArrayUtil.toFloat
import com.topdon.libhik.util.ByteArrayUtil.toInt

/**
 * [CN_TEXT]
 * @param tempWidth [CN_TEXT]
 * @param tempHeight [CN_TEXT]
 * @param yuvWidth YUV[CN_TEXT]
 * @param yuvHeight YUV[CN_TEXT]
 * @param tempType [CN_TEXT]
 * @param minTemp [CN_TEXT]Low temperature
 * @param maxTemp [CN_TEXT]High temperature
 * @param aveTemp [CN_TEXT]
 * @param maxX [CN_TEXT]High temperature[CN_TEXT] X [CN_TEXT] [0, 1000]
 * @param maxY [CN_TEXT]High temperature[CN_TEXT] Y [CN_TEXT] [0, 1000]
 * @param minX [CN_TEXT]Low temperature[CN_TEXT] X [CN_TEXT] [0, 1000]
 * @param minY [CN_TEXT]Low temperature[CN_TEXT] Y [CN_TEXT] [0, 1000]
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
         * [CN_TEXT]Specified[CN_TEXT] index [CN_TEXT]，[CN_TEXT]Temperature measurement[CN_TEXT]
         */
        private fun ByteArray.toRuleList(index: Int): ArrayList<TempRule> = try {
            val resultList: ArrayList<TempRule> = ArrayList(21)
            for (i in 0 until 21) {
                // 208 [CN_TEXT] TempRule [CN_TEXT]
                if (this[index + i * 208] == 1.toByte()) {//[CN_TEXT]
                    resultList.add(TempRule(this, index + i * 208))
                }
            }
            resultList
        } catch (_: IndexOutOfBoundsException) {
            ArrayList(0)
        }
    }

    override fun toString(): String = "[CN_TEXT]:${tempWidth}x$tempHeight, ${yuvWidth}x$yuvHeight，" +
            "[CN_TEXT]Low temperature:($minX,$minY) ${minTemp}°C，[CN_TEXT]High temperature:($maxX,$maxY) ${maxTemp}°C " +
            "[CN_TEXT]:${aveTemp}°C，${if (isNormalTest) "[CN_TEXT]" else "[CN_TEXT]"}Temperature measurement，" +
            "$pointCount + $rectCount + $lineCount = $totalCount"
}
