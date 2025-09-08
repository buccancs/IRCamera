package com.topdon.libcom.bean

import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.gson.Gson
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.utils.CommUtils

/**
 * [Chinese text]Settings[Chinese text], [Chinese text] SharedPreferences [Chinese text]; 
 *
 * [Chinese text]Settings[Chinese text], [Chinese text], 
 * [Chinese text] Activity [Chinese text]. 
 *
 * [Chinese text]Settings[Chinese text], [Chinese text]. 
 *
 * Created by LCG on 2024/12/24.
 */
class SaveSettingBean(private val isWifi: Boolean = false) {

    /**
     * [Chinese text] SPUtil [Chinese text].
     */
    private fun getSPUtils(): SPUtils = SPUtils.getInstance(if (isWifi) "WifiSaveSettingUtil" else "SaveSettingUtil")

    /**
     * [Chinese text]Settings[Chinese text], [Chinese text].
     */
    var isSaveSetting: Boolean = getSPUtils().getBoolean("isSaveSetting", true)
        set(value) {
            field = value
            getSPUtils().put("isSaveSetting", value)
        }

    /**
     * [Chinese text]Temperature measurement mode, [Chinese text]Temperature measurement mode true-[Chinese text] false-[Chinese text]
     */
    var isMeasureTempMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isMeasureTempMode", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isMeasureTempMode", value)
            }
        }
    /**
     * [Chinese text]
     */
    var isOpenAmplify : Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenAmplify", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenAmplify", value)
            }
        }

    /**
     * [Chinese text]recordingmode, [Chinese text]Photo capture true-recording false-Photo capture
     */
    var isVideoMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isVideoMode", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isVideoMode", value)
            }
        }
    /**
     * [Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isAutoShutter: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isAutoShutter", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isAutoShutter", value)
            }
        }
    /**
     * [Chinese text]recording[Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isRecordAudio: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isRecordAudio", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isRecordAudio", value)
            }
        }
    /**
     * [Chinese text]Photo capture[Chinese text], [Chinese text], [Chinese text]0[Chinese text].
     */
    var delayCaptureSecond: Int = if (isSaveSetting) getSPUtils().getInt("delayCaptureSecond", 0) else 0
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("delayCaptureSecond", value)
            }
        }

    var fusionType : Int = if (isSaveSetting) getSPUtils().getInt("fusionType", SaveSettingUtil.FusionTypeLPYFusion) else SaveSettingUtil.FusionTypeLPYFusion
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("fusionType", value)
            }
        }
    /**
     * [Chinese text]-Temperature measurement mode-[Chinese text]Dual light, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenTwoLight: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTwoLight", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenTwoLight", value)
            }
        }
    /**
     * [Chinese text]-Temperature measurement mode-Dual light[Chinese text], [Chinese text]`[0,100]`, 0[Chinese text], 100[Chinese text], [Chinese text] 50%
     */
    var twoLightAlpha: Int = if (isSaveSetting) getSPUtils().getInt("twoLightAlpha", 50) else 50
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("twoLightAlpha", value)
            }
        }

    /**
     * [Chinese text]Pseudo colormode, [Chinese text]Pseudo color[Chinese text], [Chinese text]
     */
    var pseudoColorMode: Int = if (isSaveSetting) getSPUtils().getInt("pseudoColorMode", 3) else 3
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("pseudoColorMode", value)
            }
        }

    /**
     * [Chinese text]-Temperature measurement mode-[Chinese text]Pseudo color[Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenPseudoBar: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenPseudoBar", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenPseudoBar", value)
            }
        }
    /**
     * [Chinese text], [Chinese text]range`[0,255]`, [Chinese text] 128
     */
    var contrastValue: Int = if (isSaveSetting) getSPUtils().getInt("contrastValue", 128) else 128
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("contrastValue", value)
            }
        }
    /**
     * [Chinese text]-Temperature measurement mode-[Chinese text]([Chinese text]), [Chinese text]range`[0,4]`, [Chinese text] 2
     */
    var ddeConfig: Int = if (isSaveSetting) getSPUtils().getInt("ddeConfig", 2) else 2
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("ddeConfig", value)
            }
        }
    /**
     *[Chinese text]-Temperature measurement mode-temperature[Chinese text]Settings[Chinese text].
     */
    var alarmBean: AlarmBean = if (isSaveSetting) {
        val json = getSPUtils().getString("alarmBean", "")
        if (json.isNullOrEmpty()) AlarmBean() else Gson().fromJson(json, AlarmBean::class.java)
    } else {
        AlarmBean()
    }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("alarmBean", Gson().toJson(value))
            }
        }
    /**
     * [Chinese text], [Chinese text] 0, 90, 180, 270, [Chinese text] [DeviceConfig.S_ROTATE_ANGLE]
     */
    var rotateAngle: Int = if (isSaveSetting) getSPUtils().getInt("rotateAngle", DeviceConfig.S_ROTATE_ANGLE) else DeviceConfig.S_ROTATE_ANGLE
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("rotateAngle", value)
            }
        }
    /**
     * [Chinese text](192x256)
     */
    fun isRotatePortrait(): Boolean = rotateAngle == 90 || rotateAngle == 270

    /**
     * [Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenMirror: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenMirror", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenMirror", value)
            }
        }
    /**
     * [Chinese text]-Observation mode-[Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenCompass: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenCompass", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenCompass", value)
            }
        }
    /**
     * [Chinese text]-Temperature measurement mode-temperature[Chinese text], [Chinese text].
     */
    var tempTextColor: Int = if (isSaveSetting) getSPUtils().getInt("tempTextColor", 0xffffffff.toInt()) else 0xffffffff.toInt()
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("tempTextColor", value)
            }
        }
    /**
     * [Chinese text]-Temperature measurement mode-temperature[Chinese text], [Chinese text] px, [Chinese text]14sp.
     */
    var tempTextSize: Int = if (isSaveSetting) getSPUtils().getInt("tempTextSize", SizeUtils.sp2px(14f)) else SizeUtils.sp2px(14f)
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("tempTextSize", value)
            }
        }
    /**
     * [Chinese text]temperature[Chinese text]Settings
     */
    fun isTempTextDefault(): Boolean = tempTextColor == 0xffffffff.toInt() && tempTextSize == SizeUtils.sp2px(14f)

    /**
     * [Chinese text]-Temperature measurement mode-temperaturelevel, [Chinese text], [Chinese text]
     *
     * [Chinese text] ([CameraItemBean.TYPE_TMP_C] = 1)
     *
     * high[Chinese text] ([CameraItemBean.TYPE_TMP_H] = 0)
     *
     * [Chinese text] ([CameraItemBean.TYPE_TMP_ZD] = -1)
     */
    var temperatureMode: Int = if (isSaveSetting) getSPUtils().getInt("temperatureMode", CameraItemBean.TYPE_TMP_C) else CameraItemBean.TYPE_TMP_C
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("temperatureMode", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-[Chinese text]high[Chinese text]point, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenHighPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenHighPoint", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenHighPoint", value)
            }
        }
    /**
     * [Chinese text]-Observation mode-[Chinese text]low[Chinese text]point, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenLowPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenLowPoint", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenLowPoint", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-[Chinese text]in progressAI[Chinese text], [Chinese text]in progress, [Chinese text]
     *
     * [Chinese text]in progress ([ObserveBean.TYPE_NONE] = -1)
     *
     * [Chinese text] ([ObserveBean.TYPE_DYN_R] = 0)
     *
     * high[Chinese text] ([ObserveBean.TYPE_TMP_H_S] = 1)
     *
     * low[Chinese text] ([ObserveBean.TYPE_TMP_L_S] = 2)
     */
    var aiTraceType: Int = if (isSaveSetting) getSPUtils().getInt("aiTraceType", ObserveBean.TYPE_NONE) else ObserveBean.TYPE_NONE
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("aiTraceType", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-Target-[Chinese text]Target, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenTarget: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTarget", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenTarget", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-Target-Targetmeasurementmode, [Chinese text], [Chinese text]
     *
     * [Chinese text] ([ObserveBean.TYPE_MEASURE_PERSON] = 10)
     *
     * [Chinese text] ([ObserveBean.TYPE_MEASURE_SHEEP] = 11)
     *
     * [Chinese text] ([ObserveBean.TYPE_MEASURE_DOG] = 12)
     *
     * [Chinese text] ([ObserveBean.TYPE_MEASURE_BIRD] = 13)
     */
    var targetMeasureMode: Int = if (isSaveSetting) getSPUtils().getInt("targetMeasureMode", ObserveBean.TYPE_MEASURE_PERSON) else ObserveBean.TYPE_MEASURE_PERSON
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetMeasureMode", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-Target-Target[Chinese text], [Chinese text], [Chinese text]
     *
     * [Chinese text] ([ObserveBean.TYPE_TARGET_HORIZONTAL] = 15)
     *
     * [Chinese text] ([ObserveBean.TYPE_TARGET_VERTICAL] = 16)
     *
     * circular ([ObserveBean.TYPE_TARGET_CIRCLE] = 17)
     */
    var targetType: Int = if (isSaveSetting) getSPUtils().getInt("targetType", ObserveBean.TYPE_TARGET_HORIZONTAL) else ObserveBean.TYPE_TARGET_HORIZONTAL
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetType", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-Target-Target[Chinese text], [Chinese text], [Chinese text]
     *
     * [Chinese text] ([ObserveBean.TYPE_TARGET_COLOR_GREEN] = 20)
     *
     * [Chinese text] ([ObserveBean.TYPE_TARGET_COLOR_RED] = 21)
     *
     * [Chinese text] ([ObserveBean.TYPE_TARGET_COLOR_BLUE] = 22)
     *
     * [Chinese text] ([ObserveBean.TYPE_TARGET_COLOR_BLACK] = 23)
     *
     * [Chinese text] ([ObserveBean.TYPE_TARGET_COLOR_WHITE] = 24)
     */
    var targetColorType: Int = if (isSaveSetting) getSPUtils().getInt("targetColorType", ObserveBean.TYPE_TARGET_COLOR_GREEN) else ObserveBean.TYPE_TARGET_COLOR_GREEN
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetColorType", value)
            }
        }

    /**
     * [Chinese text]-[Chinese text], [Chinese text] App [Chinese text].
     */
    var reportAuthorName: String = if (isSaveSetting) getSPUtils().getString("reportAuthorName", CommUtils.getAppName()) else CommUtils.getAppName()
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportAuthorName", value)
            }
        }

    /**
     * [Chinese text]-[Chinese text], [Chinese text] App [Chinese text].
     */
    var reportWatermarkText: String = if (isSaveSetting) getSPUtils().getString("reportWatermarkText", CommUtils.getAppName()) else CommUtils.getAppName()
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportWatermarkText", value)
            }
        }

    /**
     * [Chinese text]-[Chinese text], [Chinese text]500, [Chinese text]`[0, 1000]`
     */
    var reportHumidity: Int = if (isSaveSetting) getSPUtils().getInt("reportHumidity", 500) else 500
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportHumidity", value)
            }
        }
}