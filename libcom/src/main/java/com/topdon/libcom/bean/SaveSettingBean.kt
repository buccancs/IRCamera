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
 * dataSettingsdata，data SharedPreferences data；
 *
 * dataSettingsdata，dataCurrentdata，
 * data Activity data。
 *
 * dataSettingsdata，data。
 *
 * Created by LCG on 2024/12/24.
 */
class SaveSettingBean(private val isWifi: Boolean = false) {
    /**
     * data SPUtil data.
     */
    private fun getSPUtils(): SPUtils = SPUtils.getInstance(if (isWifi) "WifiSaveSettingUtil" else "SaveSettingUtil")

    /**
     * dataSettingsdata，data.
     */
    var isSaveSetting: Boolean = getSPUtils().getBoolean("isSaveSetting", true)
        set(value) {
            field = value
            getSPUtils().put("isSaveSetting", value)
        }

    /**
     * dataTemperature measurementMode，dataTemperature measurementMode true-Temperature measurement false-Observation
     */
    var isMeasureTempMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isMeasureTempMode", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isMeasureTempMode", value)
            }
        }

    /**
     * data
     */
    var isOpenAmplify: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenAmplify", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenAmplify", value)
            }
        }

    /**
     * dataVideoMode，dataPhoto true-Video false-Photo
     */
    var isVideoMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isVideoMode", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isVideoMode", value)
            }
        }

    /**
     * data，data true-data false-data
     */
    var isAutoShutter: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isAutoShutter", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isAutoShutter", value)
            }
        }

    /**
     * dataVideodata，data true-data false-data
     */
    var isRecordAudio: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isRecordAudio", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isRecordAudio", value)
            }
        }

    /**
     * dataPhotodata，data，data0data.
     */
    var delayCaptureSecond: Int = if (isSaveSetting) getSPUtils().getInt("delayCaptureSecond", 0) else 0
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("delayCaptureSecond", value)
            }
        }

    var fusionType: Int =
        if (isSaveSetting) {
            getSPUtils().getInt(
                "fusionType",
                SaveSettingUtil.FusionTypeLPYFusion,
            )
        } else {
            SaveSettingUtil.FusionTypeLPYFusion
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("fusionType", value)
            }
        }

    /**
     * data-Temperature measurementMode-dataDual light，data true-data false-data
     */
    var isOpenTwoLight: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTwoLight", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenTwoLight", value)
            }
        }

    /**
     * data-Temperature measurementMode-Dual lightdataFusion degree，data`[0,100]`，0data，100data，data 50%
     */
    var twoLightAlpha: Int = if (isSaveSetting) getSPUtils().getInt("twoLightAlpha", 50) else 50
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("twoLightAlpha", value)
            }
        }

    /**
     * dataPseudo-colorMode，dataPseudo-colordata，dataIron red
     */
    var pseudoColorMode: Int = if (isSaveSetting) getSPUtils().getInt("pseudoColorMode", 3) else 3
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("pseudoColorMode", value)
            }
        }

    /**
     * data-Temperature measurementMode-dataPseudo-colordata，data true-data false-data
     */
    var isOpenPseudoBar: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenPseudoBar", true) else true
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenPseudoBar", value)
            }
        }

    /**
     * data，data`[0,255]`，data 128
     */
    var contrastValue: Int = if (isSaveSetting) getSPUtils().getInt("contrastValue", 128) else 128
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("contrastValue", value)
            }
        }

    /**
     * data-Temperature measurementMode-data(data)，data`[0,4]`，data 2
     */
    var ddeConfig: Int = if (isSaveSetting) getSPUtils().getInt("ddeConfig", 2) else 2
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("ddeConfig", value)
            }
        }

    /**
     *data-Temperature measurementMode-dataSettingsdata.
     */
    var alarmBean: AlarmBean =
        if (isSaveSetting) {
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
     * dataRotateAngle，data 0、90、180、270，data [DeviceConfig.S_ROTATE_ANGLE]
     */
    var rotateAngle: Int =
        if (isSaveSetting) {
            getSPUtils().getInt(
                "rotateAngle",
                DeviceConfig.S_ROTATE_ANGLE,
            )
        } else {
            DeviceConfig.S_ROTATE_ANGLE
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("rotateAngle", value)
            }
        }

    /**
     * data(192x256)
     */
    fun isRotatePortrait(): Boolean = rotateAngle == 90 || rotateAngle == 270

    /**
     * data，data true-data false-data
     */
    var isOpenMirror: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenMirror", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenMirror", value)
            }
        }

    /**
     * data-ObservationMode-data，data true-data false-data
     */
    var isOpenCompass: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenCompass", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenCompass", value)
            }
        }

    /**
     * data-Temperature measurementMode-data，data.
     */
    var tempTextColor: Int = if (isSaveSetting) getSPUtils().getInt("tempTextColor", 0xffffffff.toInt()) else 0xffffffff.toInt()
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("tempTextColor", value)
            }
        }

    /**
     * data-Temperature measurementMode-data，data px，data14sp.
     */
    var tempTextSize: Int = if (isSaveSetting) getSPUtils().getInt("tempTextSize", SizeUtils.sp2px(14f)) else SizeUtils.sp2px(14f)
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("tempTextSize", value)
            }
        }

    /**
     * dataCurrentdataSettings
     */
    fun isTempTextDefault(): Boolean = tempTextColor == 0xffffffff.toInt() && tempTextSize == SizeUtils.sp2px(14f)

    /**
     * data-Temperature measurementMode-Temperature level，dataNormal temperature，data
     *
     * Normal temperature ([CameraItemBean.TYPE_TMP_C] = 1）
     *
     * High temperature ([CameraItemBean.TYPE_TMP_H] = 0)
     *
     * data ([CameraItemBean.TYPE_TMP_ZD] = -1)
     */
    var temperatureMode: Int =
        if (isSaveSetting) {
            getSPUtils().getInt(
                "temperatureMode",
                CameraItemBean.TYPE_TMP_C,
            )
        } else {
            CameraItemBean.TYPE_TMP_C
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("temperatureMode", value)
            }
        }

    /**
     * data-ObservationMode-dataHigh temperaturedata，data true-data false-data
     */
    var isOpenHighPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenHighPoint", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenHighPoint", value)
            }
        }

    /**
     * data-ObservationMode-dataLow temperaturedata，data true-data false-data
     */
    var isOpenLowPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenLowPoint", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenLowPoint", value)
            }
        }

    /**
     * data-ObservationMode-SelectedAIdataType，dataSelected，data
     *
     * dataSelected ([ObserveBean.TYPE_NONE] = -1)
     *
     * Dynamic recognition ([ObserveBean.TYPE_DYN_R] = 0)
     *
     * High temperaturedata ([ObserveBean.TYPE_TMP_H_S] = 1)
     *
     * Low temperaturedata ([ObserveBean.TYPE_TMP_L_S] = 2)
     */
    var aiTraceType: Int = if (isSaveSetting) getSPUtils().getInt("aiTraceType", ObserveBean.TYPE_NONE) else ObserveBean.TYPE_NONE
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("aiTraceType", value)
            }
        }

    /**
     * data-ObservationMode-Target-dataTarget，data true-data false-data
     */
    var isOpenTarget: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTarget", false) else false
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("isOpenTarget", value)
            }
        }

    /**
     * data-ObservationMode-Target-TargetdataMode，dataPerson，data
     *
     * Person ([ObserveBean.TYPE_MEASURE_PERSON] = 10)
     *
     * Sheep ([ObserveBean.TYPE_MEASURE_SHEEP] = 11)
     *
     * Dog ([ObserveBean.TYPE_MEASURE_DOG] = 12)
     *
     * Bird ([ObserveBean.TYPE_MEASURE_BIRD] = 13)
     */
    var targetMeasureMode: Int =
        if (isSaveSetting) {
            getSPUtils().getInt(
                "targetMeasureMode",
                ObserveBean.TYPE_MEASURE_PERSON,
            )
        } else {
            ObserveBean.TYPE_MEASURE_PERSON
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetMeasureMode", value)
            }
        }

    /**
     * data-ObservationMode-Target-TargetType，data，data
     *
     * data ([ObserveBean.TYPE_TARGET_HORIZONTAL] = 15)
     *
     * data ([ObserveBean.TYPE_TARGET_VERTICAL] = 16)
     *
     * data ([ObserveBean.TYPE_TARGET_CIRCLE] = 17)
     */
    var targetType: Int =
        if (isSaveSetting) {
            getSPUtils().getInt(
                "targetType",
                ObserveBean.TYPE_TARGET_HORIZONTAL,
            )
        } else {
            ObserveBean.TYPE_TARGET_HORIZONTAL
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetType", value)
            }
        }

    /**
     * data-ObservationMode-Target-Targetdata，data，data
     *
     * data ([ObserveBean.TYPE_TARGET_COLOR_GREEN] = 20)
     *
     * data ([ObserveBean.TYPE_TARGET_COLOR_RED] = 21)
     *
     * data ([ObserveBean.TYPE_TARGET_COLOR_BLUE] = 22)
     *
     * data ([ObserveBean.TYPE_TARGET_COLOR_BLACK] = 23)
     *
     * data ([ObserveBean.TYPE_TARGET_COLOR_WHITE] = 24)
     */
    var targetColorType: Int =
        if (isSaveSetting) {
            getSPUtils().getInt(
                "targetColorType",
                ObserveBean.TYPE_TARGET_COLOR_GREEN,
            )
        } else {
            ObserveBean.TYPE_TARGET_COLOR_GREEN
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("targetColorType", value)
            }
        }

    /**
     * data-data，data App data.
     */
    var reportAuthorName: String =
        if (isSaveSetting) {
            getSPUtils().getString(
                "reportAuthorName",
                CommUtils.getAppName(),
            )
        } else {
            CommUtils.getAppName()
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportAuthorName", value)
            }
        }

    /**
     * data-data，data App data.
     */
    var reportWatermarkText: String =
        if (isSaveSetting) {
            getSPUtils().getString(
                "reportWatermarkText",
                CommUtils.getAppName(),
            )
        } else {
            CommUtils.getAppName()
        }
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportWatermarkText", value)
            }
        }

    /**
     * data-data，data500，data`[0, 1000]`
     */
    var reportHumidity: Int = if (isSaveSetting) getSPUtils().getInt("reportHumidity", 500) else 500
        set(value) {
            field = value
            if (isSaveSetting) {
                getSPUtils().put("reportHumidity", value)
            }
        }
}
