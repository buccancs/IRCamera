package com.topdon.pseudo.bean

import android.os.Parcelable
import com.google.gson.Gson
import com.topdon.lib.core.common.SharedManager
import com.topdon.pseudo.constant.ColorRecommend
import kotlinx.android.parcel.Parcelize
import java.nio.ByteBuffer

/**
 * data.
 */
@Parcelize
data class CustomPseudoBean (
    var selectIndex: Int = 0,                       //CurrentSelecteddata index
    var colors: IntArray? = null,                   //7 data
    var zAltitudes: IntArray? = null,               //7 data
    var places: FloatArray? = null,                 //7 data
    var isUseCustomPseudo: Boolean = false,         //true-data false-data
    var maxTemp: Float = 50f,                       // Data fieldHigh temperature，dataCelsius，data50Celsius
    var minTemp: Float = 0f,                        // Data fieldLow temperature，dataCelsius，data0Celsius
    var isColorCustom: Boolean = true,              //true-data false-data
    var customMinColor: Int = 0xff0000FF.toInt(),   // Data field(dataLow temperature)
    var customMiddleColor: Int = 0xFFFF0000.toInt(),// Data field
    var customMaxColor: Int = 0xFFFFFF00.toInt(),   // Data field(dataHigh temperature)
    var customRecommendIndex: Int = 0,              // Data field index
    var isUseGray: Boolean = true,                  //true-data false-data
) : Parcelable {

    companion object {
        fun loadFromShared(isTC007: Boolean = false): CustomPseudoBean {
            val json = if (isTC007) SharedManager.getTC0007CustomPseudo() else SharedManager.getCustomPseudo()
            return if (json.isNotEmpty()) {
                Gson().fromJson(json, CustomPseudoBean::class.java)
            } else {
                CustomPseudoBean()
            }
        }

        fun toCustomPseudoBean(byteArray : ByteArray): CustomPseudoBean {
            val buffer: ByteBuffer = ByteBuffer.wrap(byteArray)

            var colors: IntArray? = null
            var zAltitudes: IntArray? = null
            var places: FloatArray? = null
            val colorSize: Int = byteArray[0].toInt() and 0xff
            if (colorSize > 0) {
                colors = IntArray(colorSize)
                zAltitudes = IntArray(colorSize)
                places = FloatArray(colorSize)

                buffer.position(2)
                for (i in colors.indices) {
                    colors[i] = buffer.getInt()
                }

                buffer.position(2 + 7 * 4)
                for (i in zAltitudes.indices) {
                    zAltitudes[i] = buffer.get().toInt() and 0xff
                }

                buffer.position(2 + 7 * 4 + 7)
                for (i in places.indices) {
                    places[i] = buffer.getFloat()
                }
            }

            buffer.position(2 + 7 * 4 + 7 + 7 * 4)
            val isUseCustomPseudo = buffer.get() == 1.toByte()
            var maxTemp = buffer.float
            var minTemp = buffer.float
            var isColorCustom = buffer.get() == 0.toByte()
            var customMinColor = buffer.int
            var customMiddleColor = buffer.int
            var customMaxColor = buffer.int
            val customRecommendIndex = buffer.int
            val isUseGray = buffer.get() == 0.toByte()
            if (customMinColor == 0 && customMiddleColor == 0 && customMaxColor == 0){
                maxTemp = 50f
                minTemp = 0f
                isColorCustom = true
                customMinColor = 0xff0000FF.toInt()
                customMiddleColor = 0xFFFF0000.toInt()
                customMaxColor = 0xFFFFFF00.toInt()
            }

            return CustomPseudoBean(
                selectIndex = byteArray[1].toInt() and 0xff,
                colors = colors,
                zAltitudes = zAltitudes,
                places = places,
                isUseCustomPseudo = isUseCustomPseudo,
                maxTemp = maxTemp,
                minTemp = minTemp,
                isColorCustom = isColorCustom,
                customMinColor = customMinColor,
                customMiddleColor = customMiddleColor,
                customMaxColor = customMaxColor,
                customRecommendIndex = customRecommendIndex,
                isUseGray = isUseGray
            )
        }
    }

    fun saveToShared(isTC007: Boolean = false) {
        if (isTC007) {
            SharedManager.saveTC007CustomPseudo(Gson().toJson(this))
        } else {
            SharedManager.saveCustomPseudo(Gson().toJson(this))
        }
    }

    fun getColorList(isTC007: Boolean = false): IntArray? {
        // Note: Synchronization of places calculation required across all usage locations
        if (!isUseCustomPseudo) {// Data field
            return null
        }
        return if (isColorCustom) {// Data field
            val sourceColors = getCustomColors()
            val places = getCustomPlaces()
            val actualColors = IntArray(sourceColors.size)
            System.arraycopy(sourceColors, 0, actualColors, 0, sourceColors.size)
            for (i in places.size - 1 downTo 1) {
                if (places[i - 1] == places[i]) {
                    actualColors[i - 1] = actualColors[i]
                }
            }
            actualColors
        } else {// Data field
            ColorRecommend.getColorByIndex(isTC007, customRecommendIndex)
        }
    }

    fun getPlaceList(): FloatArray? {
        if (!isUseCustomPseudo) {// Data field
            return null
        }
        return if (isColorCustom) {// Data field
            getCustomPlaces()
        } else {// Data field
            null
        }
    }

    fun getCustomColors(): IntArray {
        if (colors == null) {// Data field
            colors = intArrayOf(customMinColor, customMiddleColor, customMaxColor)
        }
        return colors!!
    }

    fun getCustomZAltitudes(): IntArray {
        if (zAltitudes == null) {
            zAltitudes = intArrayOf(0, 0, 0)
        }
        return zAltitudes!!
    }

    fun getCustomPlaces(): FloatArray {
        if (places == null) {
            places = floatArrayOf(0f, 0.5f, 1f)
        }
        return places!!
    }

    fun toByteArray(): ByteArray {
        val buffer: ByteBuffer = ByteBuffer.allocate(92)

        val colors: IntArray = getCustomColors()

        buffer.put(colors.size.toByte()) //colorSize
        buffer.put(selectIndex.toByte())

        for (color in colors) {
            buffer.putInt(color)
        }
        buffer.position(2 + 7 * 4)

        val zAltitudes: IntArray = getCustomZAltitudes()
        for (zAltitude in zAltitudes) {
            buffer.put(zAltitude.toByte())
        }
        buffer.position(2 + 7 * 4 + 7)

        val places: FloatArray = getCustomPlaces()
        for (place in places) {
            buffer.putFloat(place)
        }
        buffer.position(2 + 7 * 4 + 7 + 7 * 4)


        buffer
            .put(if (isUseCustomPseudo) 1.toByte() else 0.toByte())
            .putFloat(maxTemp)
            .putFloat(minTemp)
            .put(if (isColorCustom) 0.toByte() else 1.toByte())
            .putInt(customMinColor)
            .putInt(customMiddleColor)
            .putInt(customMaxColor)
            .putInt(customRecommendIndex)
            .put(if (isUseGray) 0.toByte() else 1.toByte())

        return buffer.array()
    }
}
