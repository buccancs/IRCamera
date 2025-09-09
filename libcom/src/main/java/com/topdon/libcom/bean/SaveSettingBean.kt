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
 * [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT] SharedPreferences [CN_TEXT]；
 *
 * [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT]Current[CN_TEXT]，
 * [CN_TEXT] Activity [CN_TEXT]。
 *
 * [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT]。
 *
 * Created by LCG on 2024/12/24.
 */
class SaveSettingBean(private val isWifi: Boolean = false) {

    /**
     * [CN_TEXT] SPUtil [CN_TEXT].
     */
    private fun getSPUtils(): SPUtils = SPUtils.getInstance(if (isWifi) "WifiSaveSettingUtil" else "SaveSettingUtil")




    /**
     * [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT].
     */
    var isSaveSetting: Boolean = getSPUtils().getBoolean("isSaveSetting", true)
        set(value) {
            field = value
            getSPUtils().put("isSaveSetting", value)
        }



    /**
     * [CN_TEXT]Temperature measurementMode，[CN_TEXT]Temperature measurementMode true-Temperature measurement false-Observation
     */
    var isMeasureTempMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isMeasureTempMode", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isMeasureTempMode", value)
            }
        }
    /**
     * [CN_TEXT]
     */
    var isOpenAmplify : Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenAmplify", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenAmplify", value)
            }
        }


    /**
     * [CN_TEXT]VideoMode，[CN_TEXT]Photo true-Video false-Photo
     */
    var isVideoMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isVideoMode", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isVideoMode", value)
            }
        }
    /**
     * [CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isAutoShutter: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isAutoShutter", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isAutoShutter", value)
            }
        }
    /**
     * [CN_TEXT]Video[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isRecordAudio: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isRecordAudio", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isRecordAudio", value)
            }
        }
    /**
     * [CN_TEXT]Photo[CN_TEXT]，[CN_TEXT]，[CN_TEXT]0[CN_TEXT].
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
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]Dual light，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenTwoLight: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTwoLight", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenTwoLight", value)
            }
        }
    /**
     * [CN_TEXT]-Temperature measurementMode-Dual light[CN_TEXT]Fusion degree，[CN_TEXT]`[0,100]`，0[CN_TEXT]，100[CN_TEXT]，[CN_TEXT] 50%
     */
    var twoLightAlpha: Int = if (isSaveSetting) getSPUtils().getInt("twoLightAlpha", 50) else 50
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("twoLightAlpha", value)
            }
        }


    /**
     * [CN_TEXT]Pseudo-colorMode，[CN_TEXT]Pseudo-color[CN_TEXT]，[CN_TEXT]Iron red
     */
    var pseudoColorMode: Int = if (isSaveSetting) getSPUtils().getInt("pseudoColorMode", 3) else 3
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("pseudoColorMode", value)
            }
        }


    /**
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]Pseudo-color[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenPseudoBar: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenPseudoBar", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenPseudoBar", value)
            }
        }
    /**
     * [CN_TEXT]，[CN_TEXT]`[0,255]`，[CN_TEXT] 128
     */
    var contrastValue: Int = if (isSaveSetting) getSPUtils().getInt("contrastValue", 128) else 128
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("contrastValue", value)
            }
        }
    /**
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]([CN_TEXT])，[CN_TEXT]`[0,4]`，[CN_TEXT] 2
     */
    var ddeConfig: Int = if (isSaveSetting) getSPUtils().getInt("ddeConfig", 2) else 2
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("ddeConfig", value)
            }
        }
    /**
     *[CN_TEXT]-Temperature measurementMode-[CN_TEXT]Settings[CN_TEXT].
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
     * [CN_TEXT]RotateAngle，[CN_TEXT] 0、90、180、270，[CN_TEXT] [DeviceConfig.S_ROTATE_ANGLE]
     */
    var rotateAngle: Int = if (isSaveSetting) getSPUtils().getInt("rotateAngle", DeviceConfig.S_ROTATE_ANGLE) else DeviceConfig.S_ROTATE_ANGLE
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("rotateAngle", value)
            }
        }
    /**
     * [CN_TEXT](192x256)
     */
    fun isRotatePortrait(): Boolean = rotateAngle == 90 || rotateAngle == 270

    /**
     * [CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenMirror: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenMirror", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenMirror", value)
            }
        }
    /**
     * [CN_TEXT]-ObservationMode-[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenCompass: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenCompass", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenCompass", value)
            }
        }
    /**
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]，[CN_TEXT].
     */
    var tempTextColor: Int = if (isSaveSetting) getSPUtils().getInt("tempTextColor", 0xffffffff.toInt()) else 0xffffffff.toInt()
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("tempTextColor", value)
            }
        }
    /**
     * [CN_TEXT]-Temperature measurementMode-[CN_TEXT]，[CN_TEXT] px，[CN_TEXT]14sp.
     */
    var tempTextSize: Int = if (isSaveSetting) getSPUtils().getInt("tempTextSize", SizeUtils.sp2px(14f)) else SizeUtils.sp2px(14f)
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("tempTextSize", value)
            }
        }
    /**
     * [CN_TEXT]Current[CN_TEXT]Settings
     */
    fun isTempTextDefault(): Boolean = tempTextColor == 0xffffffff.toInt() && tempTextSize == SizeUtils.sp2px(14f)


    /**
     * [CN_TEXT]-Temperature measurementMode-Temperature level，[CN_TEXT]Normal temperature，[CN_TEXT]
     *
     * Normal temperature ([CameraItemBean.TYPE_TMP_C] = 1）
     *
     * High temperature ([CameraItemBean.TYPE_TMP_H] = 0)
     *
     * [CN_TEXT] ([CameraItemBean.TYPE_TMP_ZD] = -1)
     */
    var temperatureMode: Int = if (isSaveSetting) getSPUtils().getInt("temperatureMode", CameraItemBean.TYPE_TMP_C) else CameraItemBean.TYPE_TMP_C
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("temperatureMode", value)
            }
        }



    /**
     * [CN_TEXT]-ObservationMode-[CN_TEXT]High temperature[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenHighPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenHighPoint", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenHighPoint", value)
            }
        }
    /**
     * [CN_TEXT]-ObservationMode-[CN_TEXT]Low temperature[CN_TEXT]，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenLowPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenLowPoint", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenLowPoint", value)
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
    var aiTraceType: Int = if (isSaveSetting) getSPUtils().getInt("aiTraceType", ObserveBean.TYPE_NONE) else ObserveBean.TYPE_NONE
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("aiTraceType", value)
            }
        }

    /**
     * [CN_TEXT]-ObservationMode-Target-[CN_TEXT]Target，[CN_TEXT] true-[CN_TEXT] false-[CN_TEXT]
     */
    var isOpenTarget: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTarget", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenTarget", value)
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
    var targetMeasureMode: Int = if (isSaveSetting) getSPUtils().getInt("targetMeasureMode", ObserveBean.TYPE_MEASURE_PERSON) else ObserveBean.TYPE_MEASURE_PERSON
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetMeasureMode", value)
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
    var targetType: Int = if (isSaveSetting) getSPUtils().getInt("targetType", ObserveBean.TYPE_TARGET_HORIZONTAL) else ObserveBean.TYPE_TARGET_HORIZONTAL
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetType", value)
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
    var targetColorType: Int = if (isSaveSetting) getSPUtils().getInt("targetColorType", ObserveBean.TYPE_TARGET_COLOR_GREEN) else ObserveBean.TYPE_TARGET_COLOR_GREEN
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetColorType", value)
            }
        }


    /**
     * [CN_TEXT]-[CN_TEXT]，[CN_TEXT] App [CN_TEXT].
     */
    var reportAuthorName: String = if (isSaveSetting) getSPUtils().getString("reportAuthorName", CommUtils.getAppName()) else CommUtils.getAppName()
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportAuthorName", value)
            }
        }

    /**
     * [CN_TEXT]-[CN_TEXT]，[CN_TEXT] App [CN_TEXT].
     */
    var reportWatermarkText: String = if (isSaveSetting) getSPUtils().getString("reportWatermarkText", CommUtils.getAppName()) else CommUtils.getAppName()
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportWatermarkText", value)
            }
        }

    /**
     * [CN_TEXT]-[CN_TEXT]，[CN_TEXT]500，[CN_TEXT]`[0, 1000]`
     */
    var reportHumidity: Int = if (isSaveSetting) getSPUtils().getInt("reportHumidity", 500) else 500
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportHumidity", value)
            }
        }
}