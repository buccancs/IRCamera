package com.topdon.lib.core.comm

/**
 * des:
 * author: CaiSongL
 * date: 2024/4/30 10:16
 **/
enum class IrParam {
    ParamLevel, // [CN_TEXT]
    ParamAlarm, // [CN_TEXT]
    ParamSharpness, // [CN_TEXT]
    ParamTempFont, // [CN_TEXT]Settings
    ParamRotate, // Rotate
    ParamColor, // Pseudo-color
    ParamMirror, // [CN_TEXT]
    ParamCompass, // [CN_TEXT]
    ParamPColor, // Pseudo-color[CN_TEXT]
    ParamTemperature, // [CN_TEXT]Mode、[CN_TEXT]Low gain
}

data class TempFont(val textSize: Int, val textColor: Int)
