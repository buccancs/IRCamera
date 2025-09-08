package com.topdon.lib.core.common

import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.gson.Gson
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.utils.CommUtils

/**
 * [Chinese text]Settings[Chinese text], [Chinese text], [Chinese text].
 *
 * [Chinese text]"[Chinese text]Settings[Chinese text]"[Chinese text], 
 *
 * [SharedManager] [Chinese text]"[Chinese text]Settings[Chinese text]"[Chinese text].
 */
object SaveSettingUtil {
    /**
     * [Chinese text]Settings[Chinese text] SharedPreferences [Chinese text].
     */
    private const val SP_NAME = "SaveSettingUtil"

    /**
     * Dual light1
     */
    const val FusionTypeLPYFusion = 4

    /**
     * Dual light2
     */
    const val FusionTypeMeanFusion = 2

    /**
     * [Chinese text]
     */
    const val FusionTypeIROnly = 1 // [Chinese text]

    /**
     * [Chinese text]visible[Chinese text]
     */
    const val FusionTypeVLOnly = 0 // [Chinese text]visible[Chinese text]

    /**
     * [Chinese text]in progress[Chinese text]
     */
    const val FusionTypeTC007Fusion = 7 // tc007[Chinese text]in progress[Chinese text]

    const val FusionTypeHSLFusion = 3
    const val FusionTypeScreenFusion = 5
    const val FusionTypeIROnlyNoFusion = 6

    /**
     * [Chinese text]Settings[Chinese text], [Chinese text].
     */
    fun reset() {
        // [Chinese text]Observation mode[Chinese text]
        isMeasureTempMode = true
        isVideoMode = false
        isAutoShutter = true
        isRecordAudio = false
        isOpenMirror = false
        delayCaptureSecond = 0
        contrastValue = 128
        pseudoColorMode = 3
        rotateAngle = DeviceConfig.S_ROTATE_ANGLE

        // Temperature measurement mode[Chinese text]
        isOpenPseudoBar = true
        isOpenTwoLight = false
        twoLightAlpha = 50
        ddeConfig = 2
        tempTextColor = 0xffffffff.toInt()
        temperatureMode = CameraItemBean.TYPE_TMP_C
        alarmBean = AlarmBean()

        // Observation mode[Chinese text]
        isOpenCompass = false
        isOpenHighPoint = false
        isOpenLowPoint = false
        aiTraceType = ObserveBean.TYPE_NONE
        isOpenTarget = false
        targetMeasureMode = ObserveBean.TYPE_MEASURE_PERSON
        targetType = ObserveBean.TYPE_TARGET_HORIZONTAL
        targetColorType = ObserveBean.TYPE_TARGET_COLOR_GREEN

        reportAuthorName = CommUtils.getAppName()
        reportWatermarkText = CommUtils.getAppName()
        reportHumidity = 500
        fusionType = FusionTypeLPYFusion
        isOpenAmplify = false
    }

    /**
     * [Chinese text]Settings[Chinese text], [Chinese text].
     */
    var isSaveSetting: Boolean
        get() = SPUtils.getInstance(SP_NAME).getBoolean("isSaveSetting", true)
        set(value) {
            SPUtils.getInstance(SP_NAME).put("isSaveSetting", value)
        }

    /**
     * [Chinese text]Temperature measurement mode, [Chinese text]Temperature measurement mode true-[Chinese text] false-[Chinese text]
     */
    var isMeasureTempMode: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isMeasureTempMode", true) else true
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isMeasureTempMode", value)
            }
        }

    /**
     * [Chinese text]
     */
    var isOpenAmplify: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenAmplify", false) else false
        set(value) {
            SPUtils.getInstance(SP_NAME).put("isOpenAmplify", value)
        }

    /**
     * [Chinese text]recordingmode, [Chinese text]Photo capture true-recording false-Photo capture
     */
    var isVideoMode: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isVideoMode", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isVideoMode", value)
            }
        }

    /**
     * [Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isAutoShutter: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isAutoShutter", true)
            } else {
                true
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isAutoShutter", value)
            }
        }

    /**
     * [Chinese text]recording[Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isRecordAudio: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isRecordAudio", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isRecordAudio", value)
            }
        }

    /**
     * [Chinese text]Photo capture[Chinese text], [Chinese text], [Chinese text]0[Chinese text].
     */
    var delayCaptureSecond: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("delayCaptureSecond", 0)
            } else {
                0
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("delayCaptureSecond", value)
            }
        }

    var fusionType: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("fusionType", FusionTypeLPYFusion) else FusionTypeLPYFusion
        set(value) {
            SPUtils.getInstance(SP_NAME).put("fusionType", value)
        }

    /**
     * [Chinese text]-Temperature measurement mode-[Chinese text]Dual light, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenTwoLight: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenTwoLight", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenTwoLight", value)
            }
        }

    /**
     * [Chinese text]-Temperature measurement mode-Dual light[Chinese text], [Chinese text]`[0,100]`, 0[Chinese text], 100[Chinese text], [Chinese text] 50%
     */
    var twoLightAlpha: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("twoLightAlpha", 50) else 50
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("twoLightAlpha", value)
            }
        }

    /**
     * [Chinese text]Pseudo colormode, [Chinese text]Pseudo color[Chinese text], [Chinese text]
     */
    var pseudoColorMode: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("pseudoColorMode", 3) else 3
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("pseudoColorMode", value)
            }
        }

    /**
     * [Chinese text]-Temperature measurement mode-[Chinese text]Pseudo color[Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenPseudoBar: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isOpenPseudoBar", true)
            } else {
                true
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenPseudoBar", value)
            }
        }

    /**
     * [Chinese text], [Chinese text]range`[0,255]`, [Chinese text] 128
     */
    var contrastValue: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("contrastValue", 128)
            } else {
                128
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("contrastValue", value)
            }
        }

    /**
     * [Chinese text]-Temperature measurement mode-[Chinese text]([Chinese text]), [Chinese text]range`[0,4]`, [Chinese text] 2
     */
    var ddeConfig: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("ddeConfig", 2) else 2
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("ddeConfig", value)
            }
        }

    /**
     *[Chinese text]-Temperature measurement mode-temperature[Chinese text]Settings[Chinese text].
     */
    var alarmBean: AlarmBean
        get() =
            if (isSaveSetting) {
                val json = SPUtils.getInstance(SP_NAME).getString("alarmBean", "")
                if (json.isNullOrEmpty()) AlarmBean() else Gson().fromJson(json, AlarmBean::class.java)
            } else {
                AlarmBean()
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("alarmBean", Gson().toJson(value))
            }
        }

    /**
     * [Chinese text], [Chinese text] 0, 90, 180, 270, [Chinese text] [DeviceConfig.S_ROTATE_ANGLE]
     */
    var rotateAngle: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("rotateAngle", DeviceConfig.S_ROTATE_ANGLE)
            } else {
                DeviceConfig.S_ROTATE_ANGLE
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("rotateAngle", value)
            }
        }

    /**
     * [Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenMirror: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isOpenMirror", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenMirror", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-[Chinese text], [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenCompass: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isOpenCompass", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenCompass", value)
            }
        }

    /**
     * [Chinese text]-Temperature measurement mode-temperature[Chinese text], [Chinese text].
     */
    var tempTextColor: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("tempTextColor", 0xffffffff.toInt())
            } else {
                0xffffffff.toInt()
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("tempTextColor", value)
            }
        }

    /**
     * [Chinese text]-Temperature measurement mode-temperature[Chinese text], [Chinese text]14sp.
     */
    var tempTextSize: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("tempTextSize", SizeUtils.sp2px(14f))
            } else {
                SizeUtils.sp2px(14f)
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("tempTextSize", value)
            }
        }

    /**
     * [Chinese text]-Temperature measurement mode-temperaturelevel, [Chinese text], [Chinese text]
     *
     * [Chinese text] ([CameraItemBean.TYPE_TMP_C] = 1)
     *
     * high[Chinese text] ([CameraItemBean.TYPE_TMP_H] = 0)
     *
     * [Chinese text] ([CameraItemBean.TYPE_TMP_ZD] = -1)
     */
    var temperatureMode: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("temperatureMode", CameraItemBean.TYPE_TMP_C)
            } else {
                CameraItemBean.TYPE_TMP_C
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("temperatureMode", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-[Chinese text]high[Chinese text]point, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenHighPoint: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isOpenHighPoint", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenHighPoint", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-[Chinese text]low[Chinese text]point, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenLowPoint: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isOpenLowPoint", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenLowPoint", value)
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
    var aiTraceType: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("aiTraceType", ObserveBean.TYPE_NONE)
            } else {
                ObserveBean.TYPE_NONE
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("aiTraceType", value)
            }
        }

    /**
     * [Chinese text]-Observation mode-Target-[Chinese text]Target, [Chinese text] true-[Chinese text] false-[Chinese text]
     */
    var isOpenTarget: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isOpenTarget", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenTarget", value)
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
    var targetMeasureMode: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).getInt(
                    "targetMeasureMode",
                    ObserveBean.TYPE_MEASURE_PERSON,
                )
            } else {
                ObserveBean.TYPE_MEASURE_PERSON
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("targetMeasureMode", value)
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
    var targetType: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).getInt(
                    "targetType",
                    ObserveBean.TYPE_TARGET_HORIZONTAL,
                )
            } else {
                ObserveBean.TYPE_TARGET_HORIZONTAL
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("targetType", value)
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
    var targetColorType: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).getInt(
                    "targetColorType",
                    ObserveBean.TYPE_TARGET_COLOR_GREEN,
                )
            } else {
                ObserveBean.TYPE_TARGET_COLOR_GREEN
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("targetColorType", value)
            }
        }

    /**
     * [Chinese text]-[Chinese text], [Chinese text] App [Chinese text].
     */
    var reportAuthorName: String
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getString("reportAuthorName", CommUtils.getAppName())
            } else {
                CommUtils.getAppName()
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("reportAuthorName", value)
            }
        }

    /**
     * [Chinese text]-[Chinese text], [Chinese text] App [Chinese text].
     */
    var reportWatermarkText: String
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getString("reportWatermarkText", CommUtils.getAppName())
            } else {
                CommUtils.getAppName()
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("reportWatermarkText", value)
            }
        }

    /**
     * [Chinese text]-[Chinese text], [Chinese text]500, [Chinese text]`[0, 1000]`
     */
    var reportHumidity: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("reportHumidity", 500)
            } else {
                500
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("reportHumidity", value)
            }
        }
}
