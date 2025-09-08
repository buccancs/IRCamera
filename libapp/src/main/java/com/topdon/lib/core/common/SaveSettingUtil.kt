package com.topdon.lib.core.common

import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.gson.Gson
import com.topdon.lib.core.bean.AlarmBean
import com.topdon.lib.core.bean.CameraItemBean
import com.topdon.lib.core.bean.ObserveBean
import com.topdon.lib.core.config.DeviceConfig
import com.topdon.lib.core.utils.CommUtils

 * .
 * “”
 * [SharedManager] “”.
object SaveSettingUtil {
     *  SharedPreferences .
    private const val SP_NAME = "SaveSettingUtil"

     * 1
    const val FusionTypeLPYFusion = 4
     * 2
    const val FusionTypeMeanFusion = 2
    const val FusionTypeIROnly = 1 //
    const val FusionTypeVLOnly = 0 //
    const val FusionTypeTC007Fusion = 7 //tc007

    const val FusionTypeHSLFusion = 3
    const val FusionTypeScreenFusion = 5
    const val FusionTypeIROnlyNoFusion = 6


     * .
    fun reset() {
        isMeasureTempMode = true
        isVideoMode = false
        isAutoShutter = true
        isRecordAudio = false
        isOpenMirror = false
        delayCaptureSecond = 0
        contrastValue = 128
        pseudoColorMode = 3
        rotateAngle = DeviceConfig.S_ROTATE_ANGLE

        isOpenPseudoBar = true
        isOpenTwoLight = false
        twoLightAlpha = 50
        ddeConfig = 2
        tempTextColor = 0xffffffff.toInt()
        temperatureMode = CameraItemBean.TYPE_TMP_C
        alarmBean = AlarmBean()

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



     * .
    var isSaveSetting: Boolean
        get() = SPUtils.getInstance(SP_NAME).getBoolean("isSaveSetting", true)
        set(value) {
            SPUtils.getInstance(SP_NAME).put("isSaveSetting", value)
        }



     * true- false
    var isMeasureTempMode: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isMeasureTempMode", true) else true
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isMeasureTempMode", value)
            }
        }
    var isOpenAmplify : Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenAmplify", false) else false
        set(value) {
            SPUtils.getInstance(SP_NAME).put("isOpenAmplify", value)
        }


     * true- false
    var isVideoMode: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isVideoMode", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isVideoMode", value)
            }
        }
     * true- false
    var isAutoShutter: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isAutoShutter", true) else true
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isAutoShutter", value)
            }
        }
     * true- false
    var isRecordAudio: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isRecordAudio", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isRecordAudio", value)
            }
        }
     * 0.
    var delayCaptureSecond: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("delayCaptureSecond", 0) else 0
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("delayCaptureSecond", value)
            }
        }


    var fusionType : Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("fusionType", FusionTypeLPYFusion) else FusionTypeLPYFusion
        set(value) {
            SPUtils.getInstance(SP_NAME).put("fusionType", value)
        }
     * true- false
    var isOpenTwoLight: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenTwoLight", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenTwoLight", value)
            }
        }
     * `[0,100]`0100 50%
    var twoLightAlpha: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("twoLightAlpha", 50) else 50
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("twoLightAlpha", value)
            }
        }


    var pseudoColorMode: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("pseudoColorMode", 3) else 3
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("pseudoColorMode", value)
            }
        }


     * true- false
    var isOpenPseudoBar: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isOpenPseudoBar", true) else true
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenPseudoBar", value)
            }
        }
     * `[0,255]` 128
    var contrastValue: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("contrastValue", 128) else 128
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("contrastValue", value)
            }
        }
     * ()`[0,4]` 2
    var ddeConfig: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("ddeConfig", 2) else 2
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("ddeConfig", value)
            }
        }
     *.
    var alarmBean: AlarmBean
        get() = if (isSaveSetting) {
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
     * 090180270 [DeviceConfig.S_ROTATE_ANGLE]
    var rotateAngle: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("rotateAngle", DeviceConfig.S_ROTATE_ANGLE) else DeviceConfig.S_ROTATE_ANGLE
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("rotateAngle", value)
            }
        }
     * true- false
    var isOpenMirror: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isOpenMirror", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenMirror", value)
            }
        }
     * true- false
    var isOpenCompass: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isOpenCompass", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenCompass", value)
            }
        }
     * .
    var tempTextColor: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("tempTextColor", 0xffffffff.toInt()) else 0xffffffff.toInt()
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("tempTextColor", value)
            }
        }
     * 14sp.
    var tempTextSize: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("tempTextSize", SizeUtils.sp2px(14f)) else SizeUtils.sp2px(14f)
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("tempTextSize", value)
            }
        }


     *  ([CameraItemBean.TYPE_TMP_C] = 1
     *  ([CameraItemBean.TYPE_TMP_H] = 0)
     *  ([CameraItemBean.TYPE_TMP_ZD] = -1)
    var temperatureMode: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("temperatureMode", CameraItemBean.TYPE_TMP_C) else CameraItemBean.TYPE_TMP_C
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("temperatureMode", value)
            }
        }



     * true- false
    var isOpenHighPoint: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isOpenHighPoint", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenHighPoint", value)
            }
        }
     * true- false
    var isOpenLowPoint: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isOpenLowPoint", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenLowPoint", value)
            }
        }

     * AI
     *  ([ObserveBean.TYPE_NONE] = -1)
     *  ([ObserveBean.TYPE_DYN_R] = 0)
     *  ([ObserveBean.TYPE_TMP_H_S] = 1)
     *  ([ObserveBean.TYPE_TMP_L_S] = 2)
    var aiTraceType: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("aiTraceType", ObserveBean.TYPE_NONE) else ObserveBean.TYPE_NONE
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("aiTraceType", value)
            }
        }

     * true- false
    var isOpenTarget: Boolean
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getBoolean("isOpenTarget", false) else false
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("isOpenTarget", value)
            }
        }

     *  ([ObserveBean.TYPE_MEASURE_PERSON] = 10)
     *  ([ObserveBean.TYPE_MEASURE_SHEEP] = 11)
     *  ([ObserveBean.TYPE_MEASURE_DOG] = 12)
     *  ([ObserveBean.TYPE_MEASURE_BIRD] = 13)
    var targetMeasureMode: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt(
            "targetMeasureMode",
            ObserveBean.TYPE_MEASURE_PERSON
        ) else ObserveBean.TYPE_MEASURE_PERSON
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("targetMeasureMode", value)
            }
        }

     *  ([ObserveBean.TYPE_TARGET_HORIZONTAL] = 15)
     *  ([ObserveBean.TYPE_TARGET_VERTICAL] = 16)
     *  ([ObserveBean.TYPE_TARGET_CIRCLE] = 17)
    var targetType: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt(
            "targetType",
            ObserveBean.TYPE_TARGET_HORIZONTAL
        ) else ObserveBean.TYPE_TARGET_HORIZONTAL
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("targetType", value)
            }
        }

     *  ([ObserveBean.TYPE_TARGET_COLOR_GREEN] = 20)
     *  ([ObserveBean.TYPE_TARGET_COLOR_RED] = 21)
     *  ([ObserveBean.TYPE_TARGET_COLOR_BLUE] = 22)
     *  ([ObserveBean.TYPE_TARGET_COLOR_BLACK] = 23)
     *  ([ObserveBean.TYPE_TARGET_COLOR_WHITE] = 24)
    var targetColorType: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt(
            "targetColorType",
            ObserveBean.TYPE_TARGET_COLOR_GREEN
        ) else ObserveBean.TYPE_TARGET_COLOR_GREEN
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("targetColorType", value)
            }
        }


     * App .
    var reportAuthorName: String
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getString("reportAuthorName", CommUtils.getAppName()) else CommUtils.getAppName()
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("reportAuthorName", value)
            }
        }

     * App .
    var reportWatermarkText: String
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getString("reportWatermarkText", CommUtils.getAppName()) else CommUtils.getAppName()
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("reportWatermarkText", value)
            }
        }

     * 500`[0, 1000]`
    var reportHumidity: Int
        get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME)
            .getInt("reportHumidity", 500) else 500
        set(value) {
            if (isSaveSetting) {
                SPUtils.getInstance(SP_NAME).put("reportHumidity", value)
            }
        }
}