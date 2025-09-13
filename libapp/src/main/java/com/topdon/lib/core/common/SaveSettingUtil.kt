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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
object SaveSettingUtil {
 /**
 * Comment removed (contained Chinese characters)
 */
 private const val SP_NAME = "SaveSettingUtil"

 /**
 * dual light1
 */
 const val FusionTypeLPYFusion = 4

 /**
 * dual light2
 */
 const val FusionTypeMeanFusion = 2

 /**
 * Comment removed (contained Chinese characters)
 */
 const val FusionTypeIROnly = 1 // infrared

 /**
 * Comment removed (contained Chinese characters)
 */
 const val FusionTypeVLOnly = 0 // visible light

 /**
 * picture-in-picture
 */
 const val FusionTypeTC007Fusion = 7 // tc007picture-in-picture

 const val FusionTypeHSLFusion = 3
 const val FusionTypeScreenFusion = 5
 const val FusionTypeIROnlyNoFusion = 6

 /**
 * Comment removed (contained Chinese characters)
 */
 fun reset() {
 // Comment removed (contained Chinese characters)
 isMeasureTempMode = true
 isVideoMode = false
 isAutoShutter = true
 isRecordAudio = false
 isOpenMirror = false
 delayCaptureSecond = 0
 contrastValue = 128
 pseudoColorMode = 3
 rotateAngle = DeviceConfig.S_ROTATE_ANGLE

 // Comment removed (contained Chinese characters)
 isOpenPseudoBar = true
 isOpenTwoLight = false
 twoLightAlpha = 50
 ddeConfig = 2
 tempTextColor = 0xffffffff.toInt()
 temperatureMode = CameraItemBean.TYPE_TMP_C
 alarmBean = AlarmBean()

 // Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 var isSaveSetting: Boolean
 get() = SPUtils.getInstance(SP_NAME).getBoolean("isSaveSetting", true)
 set(value) {
 SPUtils.getInstance(SP_NAME).put("isSaveSetting", value)
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isMeasureTempMode: Boolean
 get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isMeasureTempMode", true) else true
 set(value) {
 if (isSaveSetting) {
 SPUtils.getInstance(SP_NAME).put("isMeasureTempMode", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isOpenAmplify: Boolean
 get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenAmplify", false) else false
 set(value) {
 SPUtils.getInstance(SP_NAME).put("isOpenAmplify", value)
 }

 /**
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 var isOpenTwoLight: Boolean
 get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getBoolean("isOpenTwoLight", false) else false
 set(value) {
 if (isSaveSetting) {
 SPUtils.getInstance(SP_NAME).put("isOpenTwoLight", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var twoLightAlpha: Int
 get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("twoLightAlpha", 50) else 50
 set(value) {
 if (isSaveSetting) {
 SPUtils.getInstance(SP_NAME).put("twoLightAlpha", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var pseudoColorMode: Int
 get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("pseudoColorMode", 3) else 3
 set(value) {
 if (isSaveSetting) {
 SPUtils.getInstance(SP_NAME).put("pseudoColorMode", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 var ddeConfig: Int
 get() = if (isSaveSetting) SPUtils.getInstance(SP_NAME).getInt("ddeConfig", 2) else 2
 set(value) {
 if (isSaveSetting) {
 SPUtils.getInstance(SP_NAME).put("ddeConfig", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * normal temperature ([CameraItemBean.TYPE_TMP_C] = 1）
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * dynamic recognition ([ObserveBean.TYPE_DYN_R] = 0)
 *
 * high temperature source ([ObserveBean.TYPE_TMP_H_S] = 1)
 *
 * low temperature source ([ObserveBean.TYPE_TMP_L_S] = 2)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * human ([ObserveBean.TYPE_MEASURE_PERSON] = 10)
 *
 * sheep ([ObserveBean.TYPE_MEASURE_SHEEP] = 11)
 *
 * dog ([ObserveBean.TYPE_MEASURE_DOG] = 12)
 *
 * bird ([ObserveBean.TYPE_MEASURE_BIRD] = 13)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
