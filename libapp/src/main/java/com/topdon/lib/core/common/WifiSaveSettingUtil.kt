package com.topdon.lib.core.common

import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.core.common.SaveSettingUtil.FusionTypeIROnly
import com.topdon.lib.core.common.SaveSettingUtil.FusionTypeLPYFusion
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.utils.CommUtils

/**
 * wifi[CN_TEXT]
 *
 * Current[CN_TEXT]“[CN_TEXT]Settings[CN_TEXT]”[CN_TEXT]，
 *
 * [SharedManager] [CN_TEXT]“[CN_TEXT]Settings[CN_TEXT]”[CN_TEXT].
 */
object WifiSaveSettingUtil {
    /**
     * [CN_TEXT]Settings[CN_TEXT] SharedPreferences [CN_TEXT].
     */
    private const val SP_NAME = "WifiSaveSettingUtil"

    /**
     * [CN_TEXT]
     */
    const val TYPE_PLUG = 0
    const val TYPE_WIFI = 1

    /**
     * [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT]All[CN_TEXT].
     */
    fun reset() {
        // [CN_TEXT]Temperature measurementObservationMode[CN_TEXT]
        isMeasureTempMode = true
        isVideoMode = false
        isAutoShutter = true
        isRecordAudio = false
        isOpenMirror = false
        delayCaptureSecond = 0
        contrastValue = 128
        pseudoColorMode = 3
        rotateAngle = DeviceConfig.S_ROTATE_ANGLE

        // Temperature measurementMode[CN_TEXT]
        isOpenPseudoBar = true
        isOpenTwoLight = false
        twoLightAlpha = 50
        ddeConfig = 2
        tempTextColor = 0xffffffff.toInt()
        temperatureMode = CameraItemBean.TYPE_TMP_C
        alarmBean = AlarmBean()

        // ObservationMode[CN_TEXT]
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
        registrationX = 0
        registrationY = 0
    }

    var registrationX: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("registrationX", 0) else 0
        set(value) {
            SPUtils.getInstance(SP_NAME).put("registrationX", value)
        }
    var registrationY: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("registrationY", 0) else 0
        set(value) {
            SPUtils.getInstance(SP_NAME).put("registrationY", value)
        }
    var fusionType: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("fusionType", FusionTypeIROnly) else FusionTypeIROnly
        set(value) {
            SPUtils.getInstance(SP_NAME).put("fusionType", value)
        }

    /**
     * [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT].
     */
    var isSaveSetting: Boolean
        get() = SPUtils.getInstance(SP_NAME).getBoolean("isSaveSetting", true)
        set(value) {
            SPUtils.getInstance(SP_NAME).put("isSaveSetting", value)
        }

    /**
     * [CN_TEXT]Temperature measurementMode，[CN_TEXT]Temperature measurementMode true-Temperature measurement false-Observation
     */
    var isMeasureTempMode: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isMeasureTempMode", true) else true
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isMeasureTempMode", value)
            }
        }

    /**
     * [CN_TEXT]VideoMode，[CN_TEXT]Photo true-Video false-Photo
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
     * [CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]Video[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]Photo[CN_TEXT]，[CN_TEXT]，[CN_TEXT]0[CN_TEXT].
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

    /**
     * [CN_TEXT]，[CN_TEXT]`[0,255]`，[CN_TEXT] 128
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
     * [CN_TEXT]Pseudo-colorMode，[CN_TEXT]Pseudo-color[CN_TEXT]，[CN_TEXT]Iron red
     */
    var pseudoColorMode: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("pseudoColorMode", 3) else 3
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("pseudoColorMode", value)
            }
        }

    /**
     * [CN_TEXT]RotateAngle，[CN_TEXT] 0、90、180、270，[CN_TEXT] [DeviceConfig.S_ROTATE_ANGLE]
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
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]Pseudo-color[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]Dual light，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenTwoLight: Boolean
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getBoolean("isOpenTwoLight", false)
            } else {
                false
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenTwoLight", value)
            }
        }

    /**
     * [CN_TEXT]-Temperature measurementMode-Dual light[CN_TEXT]Fusion degree，[CN_TEXT]`[0,100]`，0[CN_TEXT]，100[CN_TEXT]，[CN_TEXT] 50%
     */
    var twoLightAlpha: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("twoLightAlpha", 50) else 50
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("twoLightAlpha", value)
            }
        }

    /**
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]([CN_TEXT])，[CN_TEXT]`[0,4]`，[CN_TEXT] 2
     */
    var ddeConfig: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("ddeConfig", 2) else 2
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("ddeConfig", value)
            }
        }

    /**
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]，[CN_TEXT].
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
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]，[CN_TEXT]14sp.
     */
    var tempTextSize: Int
        get() =
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME)
                    .getInt("tempTextSize", 14)
            } else {
                14
            }
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("tempTextSize", value)
            }
        }

    /**
     * [CN_TEXT]-Temperature measurementMode-Temperature level，[CN_TEXT]Normal temperature，[CN_TEXT]
     *
     * Normal temperature ([CameraItemBean.TYPE_TMP_C] = 1）
     *
     * High temperature ([CameraItemBean.TYPE_TMP_H] = 0)
     *
     * [CN_TEXT] ([CameraItemBean.TYPE_TMP_ZD] = -1)
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
     *[CN_TEXT]-Temperature measurementMode-[CN_TEXT]Settings[CN_TEXT].
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
     * [CN_TEXT]-ObservationMode-[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]-ObservationMode-[CN_TEXT]High temperature[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]-ObservationMode-[CN_TEXT]Low temperature[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]-ObservationMode-SelectedAI[CN_TEXT]Type，[CN_TEXT]Selected，[CN_TEXT]
     *
     * [CN_TEXT]Selected ([ObserveBean.TYPE_NONE] = -1)
     *
     * Dynamic recognition ([ObserveBean.TYPE_DYN_R] = 0)
     *
     * High temperature[CN_TEXT] ([ObserveBean.TYPE_TMP_H_S] = 1)
     *
     * Low temperature[CN_TEXT] ([ObserveBean.TYPE_TMP_L_S] = 2)
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
     * [CN_TEXT]-ObservationMode-Target-[CN_TEXT]Target，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
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
     * [CN_TEXT]-ObservationMode-Target-Target[CN_TEXT]Mode，[CN_TEXT]Person，[CN_TEXT]
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
     * [CN_TEXT]-ObservationMode-Target-TargetType，[CN_TEXT]，[CN_TEXT]
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_HORIZONTAL] = 15)
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_VERTICAL] = 16)
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_CIRCLE] = 17)
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
     * [CN_TEXT]-ObservationMode-Target-Target[CN_TEXT]，[CN_TEXT]，[CN_TEXT]
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_COLOR_GREEN] = 20)
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_COLOR_RED] = 21)
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_COLOR_BLUE] = 22)
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_COLOR_BLACK] = 23)
     *
     * [CN_TEXT] ([ObserveBean.TYPE_TARGET_COLOR_WHITE] = 24)
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
     * [CN_TEXT]-[CN_TEXT]，[CN_TEXT] App [CN_TEXT].
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
     * [CN_TEXT]-[CN_TEXT]，[CN_TEXT] App [CN_TEXT].
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
     * [CN_TEXT]-[CN_TEXT]，[CN_TEXT]500，[CN_TEXT]`[0, 1000]`
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
