package com.topdon.lib.core.comm

/**
 * des:
 * author: CaiSongL
 * date: 2024/4/30 10:16
 **/
enum class IrParam {
    ParamLevel, // [Chinese text]
    ParamAlarm, // [Chinese text]
    ParamSharpness, // [Chinese text]
    ParamTempFont, // temperature[Chinese text]Settings
    ParamRotate, // [Chinese text]
    ParamColor, // Pseudo color
    ParamMirror, // [Chinese text]
    ParamCompass, // [Chinese text]
    ParamPColor, // Pseudo color[Chinese text]
    ParamTemperature, // temperaturemode, highlow[Chinese text]
}

data class TempFont(val textSize: Int, val textColor: Int)
