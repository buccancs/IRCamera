package com.topdon.lib.core.bean

import java.nio.ByteBuffer

 * @author: CaiSongL
 * @date: 2023/5/16 15:56
data class AlarmBean(
    /** isHighOpen property */
    var isHighOpen: Boolean = false,
    /** isLowOpen property */
    var isLowOpen: Boolean = false,
    /** highTemp property */
    var highTemp: Float = Float.MAX_VALUE,
    /** lowTemp property */
    var lowTemp: Float = Float.MIN_VALUE,

    /** isMarkOpen property */
    var isMarkOpen: Boolean = true,
    /** highColor property */
    var highColor: Int = 0xffff0000.toInt(),
    /** lowColor property */
    var lowColor: Int = 0xff0000ff.toInt(),
    /** markType property */
    var markType: Int = TYPE_ALARM_MARK_STROKE,

    /** isRingtoneOpen property */
    var isRingtoneOpen: Boolean = false,
    /** ringtoneType property */
    var ringtoneType: Int = 0,
) {
    companion object {
        const val TYPE_ALARM_MARK_STROKE = 1
        const val TYPE_ALARM_MARK_MATRIX = 2

        fun loadFromArray(data: ByteArray): AlarmBean {
            val buffer = ByteBuffer.wrap(data)
            val isHighOpen = buffer.get() == 1.toByte()
            val isLowOpen = buffer.get() == 1.toByte()
            val highTemp = buffer.float
            val lowTemp = buffer.float

            val isMarkOpen = buffer.get() == 1.toByte()
            val highColor = buffer.int
            val lowColor = buffer.int
            val markType = buffer.int

            val isRingtoneOpen = buffer.get() == 1.toByte()
            val ringtoneType = buffer.int

            return AlarmBean(
                isHighOpen = isHighOpen,
                isLowOpen = isLowOpen,
                highTemp = highTemp,
                lowTemp = lowTemp,

                isMarkOpen = isMarkOpen,
                highColor = if (highColor == 0) 0xffff0000.toInt() else highColor,
                lowColor = if (lowColor == 0) 0xff0fa752.toInt() else lowColor,
                markType = if (markType == 0) 1 else markType,

                isRingtoneOpen = isRingtoneOpen,
                ringtoneType = ringtoneType,
            )
        }
    }


    /**
     * Function description.
     */
    fun toByteArray(): ByteArray = ByteBuffer.allocate(28)
        .put(if (isHighOpen) 1 else 0)
        .put(if (isLowOpen) 1 else 0)
        .putFloat(highTemp)
        .putFloat(lowTemp)
        .put(if (isMarkOpen) 1 else 0)
        .putInt(highColor)
        .putInt(lowColor)
        .putInt(markType)
        .put(if (isRingtoneOpen) 1 else 0)
        .putInt(ringtoneType)
        .array()

    /**
     * Function description.
     */
    fun isOpen(): Boolean = isHighOpen || isLowOpen
}