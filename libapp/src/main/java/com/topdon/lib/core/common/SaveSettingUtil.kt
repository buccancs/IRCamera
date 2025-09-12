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
 * Settings persistence utility for thermal camera configuration
 */
object SaveSettingUtil {

    fun reset() {
        // Measure view dimensionsTemperature measurementObservationModeutility
        isMeasureTempMode = true
        isVideoMode = false
        isAutoShutter = true
        isRecordAudio = false
        isOpenMirror = false
        delayCaptureSecond = 0
        contrastValue = 128
        pseudoColorMode = 3
        rotateAngle = DeviceConfig.S_ROTATE_ANGLE

        // Temperature measurementModeutility
        isOpenPseudoBar = true
        isOpenTwoLight = false
        twoLightAlpha = 50
        ddeConfig = 2
        tempTextColor = 0xffffffff.toInt()
        temperatureMode = CameraItemBean.TYPE_TMP_C
        alarmBean = AlarmBean()

        // ObservationModeutility
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
     * utilitySettingsutility，utility.
     */
    var isSaveSetting: Boolean
        get() = SPUtils.getInstance(SP_NAME).getBoolean("isSaveSetting", true)
        set(value) {
            SPUtils.getInstance(SP_NAME).put("isSaveSetting", value)
        }

    /**
     * utilityTemperature measurementMode，utilityTemperature measurementMode true-Temperature measurement false-Observation
     */
    var isMeasureTempMode: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isMeasureTempMode", true) else true
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isMeasureTempMode", value)
            }
        }

    /**
     * utility
     */
    var isOpenAmplify: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenAmplify", false) else false
        set(value) {
            SPUtils.getInstance(SP_NAME).put("isOpenAmplify", value)
        }

    /**
     * utilityVideoMode，utilityPhoto true-Video false-Photo
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
     * utility，utility true-utility false-utility
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
     * utilityVideoutility，utility true-utility false-utility
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
     * utilityPhotoutility，utility，utility0utility.
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
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(
                    SP_NAME,
                ).getInt("fusionType", FusionTypeLPYFusion)
            } else {
                FusionTypeLPYFusion
            }
        set(value) {
            SPUtils.getInstance(SP_NAME).put("fusionType", value)
        }

    /**
     * utility-Temperature measurementMode-utilityDual light，utility true-utility false-utility
     */
    var isOpenTwoLight: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenTwoLight", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenTwoLight", value)
            }
        }

    /**
     * utility-Temperature measurementMode-Dual lightutilityFusion degree，utility`[0,100]`，0utility，100utility，utility 50%
     */
    var twoLightAlpha: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("twoLightAlpha", 50) else 50
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("twoLightAlpha", value)
            }
        }

    /**
     * utilityPseudo-colorMode，utilityPseudo-colorutility，utilityIron red
     */
    var pseudoColorMode: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("pseudoColorMode", 3) else 3
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("pseudoColorMode", value)
            }
        }

    /**
     * utility-Temperature measurementMode-utilityPseudo-colorutility，utility true-utility false-utility
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
     * utility，utility`[0,255]`，utility 128
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
     * utility-Temperature measurementMode-utility(utility)，utility`[0,4]`，utility 2
     */
    var ddeConfig: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("ddeConfig", 2) else 2
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("ddeConfig", value)
            }
        }

    /**
     *utility-Temperature measurementMode-utilitySettingsutility.
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
     * utilityRotateAngle，utility 0、90、180、270，utility [DeviceConfig.S_ROTATE_ANGLE]
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
     * utility，utility true-utility false-utility
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
     * utility-ObservationMode-utility，utility true-utility false-utility
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
     * utility-Temperature measurementMode-utility，utility.
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
     * utility-Temperature measurementMode-utility，utility14sp.
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
     * utility-Temperature measurementMode-Temperature level，utilityNormal temperature，utility
     *
     * Normal temperature ([CameraItemBean.TYPE_TMP_C] = 1）
     *
     * High temperature ([CameraItemBean.TYPE_TMP_H] = 0)
     *
     * utility ([CameraItemBean.TYPE_TMP_ZD] = -1)
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
     * utility-ObservationMode-utilityHigh temperatureutility，utility true-utility false-utility
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
     * utility-ObservationMode-utilityLow temperatureutility，utility true-utility false-utility
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
     * utility-ObservationMode-SelectedAIutilityType，utilitySelected，utility
     *
     * utilitySelected ([ObserveBean.TYPE_NONE] = -1)
     *
     * Dynamic recognition ([ObserveBean.TYPE_DYN_R] = 0)
     *
     * High temperatureutility ([ObserveBean.TYPE_TMP_H_S] = 1)
     *
     * Low temperatureutility ([ObserveBean.TYPE_TMP_L_S] = 2)
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
     * utility-ObservationMode-Target-utilityTarget，utility true-utility false-utility
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
     * utility-ObservationMode-Target-TargetutilityMode，utilityPerson，utility
     *
     * Person ([ObserveBean.TYPE_MEASURE_PERSON] = 10)
     *
     * Sheep ([ObserveBean.TYPE_MEASURE_SHEEP] = 11)
     *
     * Dog ([ObserveBean.TYPE_MEASURE_DOG] = 12)
     *
     * Bird ([ObserveBean.TYPE_MEASURE_BIRD] = 13)
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
     * utility-ObservationMode-Target-TargetType，utility，utility
     *
     * utility ([ObserveBean.TYPE_TARGET_HORIZONTAL] = 15)
     *
     * utility ([ObserveBean.TYPE_TARGET_VERTICAL] = 16)
     *
     * utility ([ObserveBean.TYPE_TARGET_CIRCLE] = 17)
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
     * utility-ObservationMode-Target-Targetutility，utility，utility
     *
     * utility ([ObserveBean.TYPE_TARGET_COLOR_GREEN] = 20)
     *
     * utility ([ObserveBean.TYPE_TARGET_COLOR_RED] = 21)
     *
     * utility ([ObserveBean.TYPE_TARGET_COLOR_BLUE] = 22)
     *
     * utility ([ObserveBean.TYPE_TARGET_COLOR_BLACK] = 23)
     *
     * utility ([ObserveBean.TYPE_TARGET_COLOR_WHITE] = 24)
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
     * utility-utility，utility App utility.
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
     * utility-utility，utility App utility.
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
     * utility-utility，utility500，utility`[0, 1000]`
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
