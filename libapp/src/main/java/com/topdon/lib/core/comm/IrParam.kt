package com.topdon.lib.core.comm

/**
 * des:
 * author: CaiSongL
 * date: 2024/4/30 10:16
 **/
enum class IrParam {
    ParamLevel, // 对比度
    ParamAlarm, // 预警
    ParamSharpness, // 锐度
    ParamTempFont, // 温度值fontsettings
    ParamRotate, // 旋转
    ParamColor, // pseudo color
    ParamMirror, // 镜像
    ParamCompass, // 指南针
    ParamPColor, // pseudo color样式
    ParamTemperature, // 温度模式、高低gain
}

data class TempFont(val textSize: Int, val textColor: Int)
