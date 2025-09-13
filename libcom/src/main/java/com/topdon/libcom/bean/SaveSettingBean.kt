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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Created by LCG on 2024/12/24.
 */
class SaveSettingBean(private val isWifi: Boolean = false) {
 /**
 * Comment removed (contained Chinese characters)
 */
 private fun getSPUtils(): SPUtils = SPUtils.getInstance(if (isWifi) "WifiSaveSettingUtil" else "SaveSettingUtil")

 /**
 * Comment removed (contained Chinese characters)
 */
 var isSaveSetting: Boolean = getSPUtils().getBoolean("isSaveSetting", true)
 set(value) {
 field = value
 getSPUtils().put("isSaveSetting", value)
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isMeasureTempMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isMeasureTempMode", true) else true
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isMeasureTempMode", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isOpenAmplify: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenAmplify", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenAmplify", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isVideoMode: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isVideoMode", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isVideoMode", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isAutoShutter: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isAutoShutter", true) else true
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isAutoShutter", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isRecordAudio: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isRecordAudio", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isRecordAudio", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 var isOpenTwoLight: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTwoLight", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenTwoLight", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var twoLightAlpha: Int = if (isSaveSetting) getSPUtils().getInt("twoLightAlpha", 50) else 50
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("twoLightAlpha", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var pseudoColorMode: Int = if (isSaveSetting) getSPUtils().getInt("pseudoColorMode", 3) else 3
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("pseudoColorMode", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isOpenPseudoBar: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenPseudoBar", true) else true
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenPseudoBar", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var contrastValue: Int = if (isSaveSetting) getSPUtils().getInt("contrastValue", 128) else 128
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("contrastValue", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var ddeConfig: Int = if (isSaveSetting) getSPUtils().getInt("ddeConfig", 2) else 2
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("ddeConfig", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 fun isRotatePortrait(): Boolean = rotateAngle == 90 || rotateAngle == 270

 /**
 * Comment removed (contained Chinese characters)
 */
 var isOpenMirror: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenMirror", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenMirror", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isOpenCompass: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenCompass", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenCompass", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var tempTextColor: Int = if (isSaveSetting) getSPUtils().getInt("tempTextColor", 0xffffffff.toInt()) else 0xffffffff.toInt()
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("tempTextColor", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var tempTextSize: Int = if (isSaveSetting) getSPUtils().getInt("tempTextSize", SizeUtils.sp2px(14f)) else SizeUtils.sp2px(14f)
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("tempTextSize", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 fun isTempTextDefault(): Boolean = tempTextColor == 0xffffffff.toInt() && tempTextSize == SizeUtils.sp2px(14f)

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 var isOpenHighPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenHighPoint", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenHighPoint", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isOpenLowPoint: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenLowPoint", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenLowPoint", value)
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
 */
 var aiTraceType: Int = if (isSaveSetting) getSPUtils().getInt("aiTraceType", ObserveBean.TYPE_NONE) else ObserveBean.TYPE_NONE
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("aiTraceType", value)
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 var isOpenTarget: Boolean = if (isSaveSetting) getSPUtils().getBoolean("isOpenTarget", false) else false
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("isOpenTarget", value)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 */
 var reportHumidity: Int = if (isSaveSetting) getSPUtils().getInt("reportHumidity", 500) else 500
 set(value) {
 field = value
 if (isSaveSetting) {
 getSPUtils().put("reportHumidity", value)
 }
 }
}
