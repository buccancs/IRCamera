package com.topdon.lib.core.comm

/**
 * des:
* author: CaiSongL
 * date: 2024/4/30 10:16
 **/
enum class IrParam {
 ParamLevel, // 
 ParamAlarm, // 
 ParamSharpness, // 
 ParamTempFont, // TemperatureValuefontsettings
 ParamRotate, // 
 ParamColor, // pseudo color
 ParamMirror, // 
 ParamCompass, // 
 ParamPColor, // pseudo color
 ParamTemperature, // TemperatureMode、gain
}

data class TempFont(val textSize: Int, val textColor: Int)
