package com.topdon.lib.core.bean

/**
 * dataPhotodata.
 * @param isOpen data
 * @param continuaTime dataPhotodata，data
 * @param count dataPhotodata
 */
data class ContinuousBean(var isOpen: Boolean = false, var continuaTime: Long = 1000, var count: Int = 3)

class ObserveBean {
    companion object {
        // data
        const val TYPE_NONE = -1 // dataDynamic recognition
        const val TYPE_DYN_R = 0 // Dynamic recognition
        const val TYPE_TMP_H_S = 1 // High temperaturedata
        const val TYPE_TMP_L_S = 2 // Low temperaturedata

        const val TYPE_MEASURE_PERSON = 10 // Person
        const val TYPE_MEASURE_SHEEP = 11 // Sheep
        const val TYPE_MEASURE_DOG = 12 // Dog
        const val TYPE_MEASURE_BIRD = 13 // Bird

        const val TYPE_TARGET_HORIZONTAL = 15 // data
        const val TYPE_TARGET_VERTICAL = 16 // data
        const val TYPE_TARGET_CIRCLE = 17 // data

        const val TYPE_TARGET_COLOR_GREEN = 20 // data
        const val TYPE_TARGET_COLOR_RED = 21 // data
        const val TYPE_TARGET_COLOR_BLUE = 22 // data
        const val TYPE_TARGET_COLOR_BLACK = 23 // data
        const val TYPE_TARGET_COLOR_WHITE = 24 // data
    }
}

data class CameraItemBean(
    var name: String = "data",
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

        const val DELAY_TIME_0 = 0 // data3data
        const val DELAY_TIME_3 = 3 // data3data
        const val DELAY_TIME_6 = 6 // data6data

        // dataMode
        const val TYPE_TMP_ZD = -1 // dataMode
        const val TYPE_TMP_C = 1 // Normal temperatureMode
        const val TYPE_TMP_H = 0 // High temperatureMode
    }
}
