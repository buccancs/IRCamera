package com.infisense.usbir.utils

import java.util.*


/**
 * @author: CaiSongL
 * @date: 2023/8/7 10:53
 */
object ColorUtils {

    /**
     * Function description.
     */
    fun getRed(color: Int): Int {
        return color shr 16 and 0xFF
    }

    /**
     * Function description.
     */
    fun getGreen(color: Int): Int {
        return color shr 8 and 0xFF
    }

    /**
     * Function description.
     */
    fun getBlue(color: Int): Int {
        return color and 0xFF
    }

    /**
     * Function description.
     */
    fun to01(float: Float): String {
        return String.format(Locale.ENGLISH, "%.1f", float)
    }

}