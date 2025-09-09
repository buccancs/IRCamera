package com.topdon.lib.core.bean

/**
 * [CN_TEXT]Photo[CN_TEXT].
 * @param isOpen [CN_TEXT]
 * @param continuaTime [CN_TEXT]Photo[CN_TEXT]，[CN_TEXT]
 * @param count [CN_TEXT]Photo[CN_TEXT]
 */
data class ContinuousBean(var isOpen: Boolean = false, var continuaTime: Long = 1000, var count: Int = 3)

class ObserveBean {
    companion object {
        // [CN_TEXT]
        const val TYPE_NONE = -1 // [CN_TEXT]Dynamic recognition
        const val TYPE_DYN_R = 0 // Dynamic recognition
        const val TYPE_TMP_H_S = 1 // High temperature[CN_TEXT]
        const val TYPE_TMP_L_S = 2 // Low temperature[CN_TEXT]

        const val TYPE_MEASURE_PERSON = 10 // Person
        const val TYPE_MEASURE_SHEEP = 11 // Sheep
        const val TYPE_MEASURE_DOG = 12 // Dog
        const val TYPE_MEASURE_BIRD = 13 // Bird

        const val TYPE_TARGET_HORIZONTAL = 15 // [CN_TEXT]
        const val TYPE_TARGET_VERTICAL = 16 // [CN_TEXT]
        const val TYPE_TARGET_CIRCLE = 17 // [CN_TEXT]

        const val TYPE_TARGET_COLOR_GREEN = 20 // [CN_TEXT]
        const val TYPE_TARGET_COLOR_RED = 21 // [CN_TEXT]
        const val TYPE_TARGET_COLOR_BLUE = 22 // [CN_TEXT]
        const val TYPE_TARGET_COLOR_BLACK = 23 // [CN_TEXT]
        const val TYPE_TARGET_COLOR_WHITE = 24 // [CN_TEXT]
    }
}

data class CameraItemBean(
    var name: String = "[CN_TEXT]",
    var type: Int = 0,
    var time: Int = DELAY_TIME_0,
    var isSel: Boolean = false,
) {
    fun changeDelayType() {
        if (type == TYPE_DELAY) {
            when (time) {
                DELAY_TIME_0 -> {
                    time = DELAY_TIME_3
                }

                DELAY_TIME_3 -> {
                    time = DELAY_TIME_6
                }

                DELAY_TIME_6 -> {
                    time = DELAY_TIME_0
                }
            }
        }
    }

    companion object {
        const val TYPE_DELAY = 0
        const val TYPE_ZDKM = 1
        const val TYPE_SDKM = 2
        const val TYPE_AUDIO = 3
        const val TYPE_SETTING = 4

        const val DELAY_TIME_0 = 0 // [CN_TEXT]3[CN_TEXT]
        const val DELAY_TIME_3 = 3 // [CN_TEXT]3[CN_TEXT]
        const val DELAY_TIME_6 = 6 // [CN_TEXT]6[CN_TEXT]

        // [CN_TEXT]Mode
        const val TYPE_TMP_ZD = -1 // [CN_TEXT]Mode
        const val TYPE_TMP_C = 1 // Normal temperatureMode
        const val TYPE_TMP_H = 0 // High temperatureMode
    }
}
