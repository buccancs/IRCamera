package com.topdon.lib.core.common

import android.content.Context
import android.preference.PreferenceManager
import android.util.Base64
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.topdon.lib.core.bean.CarDetectBean
import com.topdon.lib.core.bean.CarDetectChildBean
import com.topdon.lib.core.bean.ContinuousBean
import com.topdon.lib.core.bean.WatermarkBean
import com.topdon.lib.core.dialog.CarDetectDialog

 * “”
 * [SaveSettingUtil] “”.
 * create by fylder on 2018/6/14
object SharedManager {
     * .
    var hasClickWinter: Boolean
        get() = SPUtils.getInstance().getBoolean("hasClickWinter", false)
        set(value) = SPUtils.getInstance().put("hasClickWinter", value)

     * .
    var isNeedShowTrendTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isNeedShowTrendTips", true)
        set(value) = SPUtils.getInstance().put("isNeedShowTrendTips", value)

     *  0- 1- 2
    var houseSpaceUnit: Int
        get() = SPUtils.getInstance().getInt("houseSpaceUnit", 0)
        set(value) {
            SPUtils.getInstance().put("houseSpaceUnit", value)
        }

     *  0-USD 1-EUR 2-GBP 3-AUD 4-JPY 5-CAD 6-NZD 7-RMB 8-HKD
    var costUnit: Int
        get() = SPUtils.getInstance().getInt("costUnit", 0)
        set(value) {
            SPUtils.getInstance().put("costUnit", value)
        }

     *  TC  false.
    var hasTcLine: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTcLine", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTcLine", value)
        }

     *  TS004  false. (TC001 only - legacy compatibility)
    var hasTS004: Boolean
        get() = false // TC001 only - no TS004 support
        set(value) = Unit // TC001 only - no TS004 support

     *  TC007  false. (TC001 only - legacy compatibility)
    var hasTC007: Boolean
        get() = false // TC001 only - no TC007 support
        set(value) = Unit // TC001 only - no TC007 support


     *  1-1 2-2 3-3 0
    var homeGuideStep: Int
        get() {
            val value = SPUtils.getInstance().getInt("homeGuideStep", 2)
            return if (value == 1) 2 else value
        }
        set(value) {
            SPUtils.getInstance().put("homeGuideStep", value)
        }

     *  1-1 2-2 0
    var configGuideStep: Int
        get() = SPUtils.getInstance().getInt("configGuideStep", 1)
        set(value) = SPUtils.getInstance().put("configGuideStep", value)


    var isHideEmissivityTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isHideEmissivityTips", false)
        set(value) {
            SPUtils.getInstance().put("isHideEmissivityTips", value)
        }




     * [0, 2000] SeekBar .idsid
    fun getManualAngle(sId: String): Int {
        return SPUtils.getInstance().getInt("manualAngle_${sId}", 1000)
    }

    fun setManualAngle(sId: String, value: Int) {
        SPUtils.getInstance().put("manualAngle_${sId}", value)
    }

     * 24.
    fun getManualData(sId: String): ByteArray {
        val strValue = SPUtils.getInstance().getString("manualData_${sId}")
        return if (strValue.isNullOrEmpty()) {
            // 1,0,0,0,1,0 6 float
            byteArrayOf(
                0,
                0,
                -128,
                63,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                -128,
                63,
                0,
                0,
                0,
                0
            )
        } else {
            Base64.decode(strValue.toByteArray(), Base64.DEFAULT)
        }
    }

    fun setManualData(sId: String, value: ByteArray) {
        if (value.size == 24) {
            SPUtils.getInstance()
                .put("manualData_${sId}", String(Base64.encode(value, Base64.DEFAULT)))
        }
    }

     * .
    var isConnectAutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnectAutoOpen", false)
        set(value) {
            SPUtils.getInstance().put("isConnectAutoOpen", value)
        }



     * OTG .
     * true- false
    var isTipOTG: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipOTG", true)
        set(value) {
            SPUtils.getInstance().put("isTipOTG", value)
        }

     * .
     * true- false
    var isTipShutter: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipShutter", true)
        set(value) {
            SPUtils.getInstance().put("isTipShutter", value)
        }

     * .
     * true- false
    var isTipHighTemp: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipHighTemp", true)
        set(value) {
            SPUtils.getInstance().put("isTipHighTemp", value)
        }

     * .
     * true- false
    var isTipPinP: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipPinP", true)
        set(value) {
            SPUtils.getInstance().put("isTipPinP", value)
        }

     * .
     * true- false
    var isTipCoordinate: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipCoordinate", true)
        set(value) {
            SPUtils.getInstance().put("isTipCoordinate", value)
        }

     * AI.
     * true- false
    var isTipAIRecognition: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipAIRecognition", true)
        set(value) {
            SPUtils.getInstance().put("isTipAIRecognition", value)
        }

     * .
     * true- false
    var isTipObservePhoto: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipObservePhoto", true)
        set(value) {
            SPUtils.getInstance().put("isTipObservePhoto", value)
        }

     * .
    var continuousBean: ContinuousBean
        get() {
            val json = SPUtils.getInstance().getString("continuousBean", "")
            return if (json.isNullOrEmpty()) ContinuousBean() else Gson().fromJson(
                json,
                ContinuousBean::class.java
            )
        }
        set(value) {
            SPUtils.getInstance().put("continuousBean", Gson().toJson(value))
        }

     * wifi
     * .
    var wifiWatermarkBean: WatermarkBean
        get() {
            val json = SPUtils.getInstance().getString("wifiWatermarkBean", "")
            return if (json.isNullOrEmpty()) WatermarkBean() else Gson().fromJson(
                json,
                WatermarkBean::class.java
            )
        }
        set(value) {
            SPUtils.getInstance().put("watermarkBean", Gson().toJson(value))
        }

     * .
    var watermarkBean: WatermarkBean
        get() {
            val json = SPUtils.getInstance().getString("watermarkBean", "")
            return if (json.isNullOrEmpty()) WatermarkBean() else Gson().fromJson(
                json,
                WatermarkBean::class.java
            )
        }
        set(value) {
            SPUtils.getInstance().put("watermarkBean", Gson().toJson(value))
        }



     * .
     * true- false
    var isChangeDevice: Boolean
        get() = SPUtils.getInstance().getBoolean("isChangeDevice", false)
        set(value) {
            SPUtils.getInstance().put("isChangeDevice", value)
        }

    private const val TOKEN: String = "token"
    private const val USER_ID: String = "user_id"
    private const val USERNAME: String = "username"
    private const val NICKNAME: String = "nickname"
    private const val HEAD_ICON: String = "head_icon"

    private const val BASE_HOST: String = "base_host"
    private const val LANGUAGE = "language"//

    private const val HAS_SHOW_CLAUSE = "hasShowClause"//
    private const val TEMPERATURE_UNIT = "temperature"//
    private const val VERSION_CHECK_DATE = "version_check_date"//

    private const val DEVICE_SN = "deviceSn"//SN
    private const val DEVICE_VERSION = "deviceVersion"//

    private const val IR_CONFIG = "ir_config"//(json)
    private const val SP_CUSTOM_PSEUDO = "sp_custom_pseudo"//
    private const val SP_TARGET_POP = "sp_target_pop" //

    private const val SP_SETTING_IS_PUSH = "sp_setting_is_push" //
    private const val SP_SETTING_IS_RECOMMEND = "sp_setting_is_recommend"

    private const val SP_CAR_DETECT = "sp_car_detect" //


    fun setToken(token: String) {
        SPUtils.getInstance().put(TOKEN, token)
    }

    fun getToken(): String {
        return SPUtils.getInstance().getString(TOKEN, "")
    }

    fun setUserId(token: String) {
        SPUtils.getInstance().put(USER_ID, token)
    }

    fun getUserId(): String {
        return SPUtils.getInstance().getString(USER_ID, "0")
    }

    fun setUsername(username: String) {
        SPUtils.getInstance().put(USERNAME, username)
    }

    fun getUsername(): String {
        return SPUtils.getInstance().getString(USERNAME, "")
    }

    fun setNickname(nickname: String) {
        SPUtils.getInstance().put(NICKNAME, nickname)
    }

    fun getNickname(): String {
        return SPUtils.getInstance().getString(NICKNAME, "")
    }

    fun setHeadIcon(headIcon: String) {
        SPUtils.getInstance().put(HEAD_ICON, headIcon)
    }

    fun getHeadIcon(): String {
        return SPUtils.getInstance().getString(HEAD_ICON, "")
    }


    fun setBaseHost(value: String) {
        return SPUtils.getInstance().put(BASE_HOST, value)
    }

    fun getBaseHost(): String {
        return SPUtils.getInstance().getString(BASE_HOST, "")
    }

    fun setLanguage(context: Context, language: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(LANGUAGE, language).apply()
    }

    // ApplicationapplicationContextcontext
    fun getLanguage(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGE, "")!!
    }


    fun setHasShowClause(hasShowClause: Boolean) {
        return SPUtils.getInstance().put(HAS_SHOW_CLAUSE, hasShowClause)
    }

    fun getHasShowClause(): Boolean {
        return SPUtils.getInstance().getBoolean(HAS_SHOW_CLAUSE, false)
    }

    fun setIRConfig(value: String) {
        return SPUtils.getInstance().put(IR_CONFIG, value)
    }

    fun getIRConfig(): String {
        return SPUtils.getInstance().getString(IR_CONFIG, "")
    }

    fun setTemperature(value: Int) {
        return SPUtils.getInstance().put(TEMPERATURE_UNIT, value)
    }

    // 1:     0:
    fun getTemperature(): Int {
        return SPUtils.getInstance().getInt(TEMPERATURE_UNIT, 1)
    }

    fun setVersionCheckDate(value: Long) {
        return SPUtils.getInstance().put(VERSION_CHECK_DATE, value)
    }

    fun getVersionCheckDate(): Long {
        return SPUtils.getInstance().getLong(VERSION_CHECK_DATE, 0)
    }

    fun setDeviceSn(value: String) {
        return SPUtils.getInstance().put(DEVICE_SN, value)
    }

    fun getDeviceSn(): String {
        return SPUtils.getInstance().getString(DEVICE_SN, "")
    }

    fun setDeviceVersion(value: String) {
        return SPUtils.getInstance().put(DEVICE_VERSION, value)
    }

    fun getDeviceVersion(): String {
        return SPUtils.getInstance().getString(DEVICE_VERSION, "")
    }

    fun saveCustomPseudo(json: String) {
        SPUtils.getInstance().put(SP_CUSTOM_PSEUDO, json)
    }

    fun getCustomPseudo(): String {
        return SPUtils.getInstance().getString(SP_CUSTOM_PSEUDO, "")
    }


    fun getTargetPop(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_TARGET_POP, false)
    }

    fun saveTargetPop(targetPop: Boolean) {
        SPUtils.getInstance().put(SP_TARGET_POP, targetPop)
    }


    private const val IR_DUAL_DISP = "ir_dual_disp"//-
    private const val IR_DUAL_DISP_V = "ir_dual_disp_v"//-


    fun saveSettingIsPush(isPush: Boolean) {
        SPUtils.getInstance().put(SP_SETTING_IS_PUSH, isPush)
    }

    fun getSettingIsPush(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_SETTING_IS_PUSH, true)
    }

    fun saveSettingIsRecommend(isRecommend: Boolean) {
        SPUtils.getInstance().put(SP_SETTING_IS_RECOMMEND, isRecommend)
    }

    fun getSettingIsRecommend(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_SETTING_IS_RECOMMEND, true)
    }

    fun getMainPermissionsState(): Boolean {
        return SPUtils.getInstance().getBoolean("main_permissions_state", false)
    }

    fun setMainPermissionsState(value: Boolean) {
        return SPUtils.getInstance().put("main_permissions_state", value)
    }

    fun getImagePermissionsState(): Boolean {
        return SPUtils.getInstance().getBoolean("storage_permissions_state", false)
    }

    fun setImagePermissionsState(value: Boolean) {
        return SPUtils.getInstance().put("storage_permissions_state", value)
    }

    fun getCarDetectInfo(): CarDetectChildBean {
        var detectInfo = SPUtils.getInstance().getString(SP_CAR_DETECT, "")
        if (detectInfo.isEmpty()) {
            return CarDetectDialog.getDetectList()[0].detectChildBeans[0]
        }
        var detectChildBean = GsonUtils.fromJson(detectInfo, CarDetectChildBean::class.java)
        var type = detectChildBean.type
        var pos = detectChildBean.pos
        return CarDetectDialog.getDetectList()[type].detectChildBeans[pos]
    }

    fun saveCarDetectInfo(bean: CarDetectChildBean) {
        SPUtils.getInstance().put(SP_CAR_DETECT, GsonUtils.toJson(bean))
    }

    // Legacy device-specific properties - now defaults for TC001 compatibility
    
     * TS004 auto sync setting (legacy - TC001 doesn't use this).
    var is04AutoSync: Boolean
        get() = SPUtils.getInstance().getBoolean("is04AutoSync", false)
        set(value) = SPUtils.getInstance().put("is04AutoSync", value)
        
     * TS004 TISR setting (legacy - TC001 doesn't use this).
    var is04TISR: Boolean
        get() = SPUtils.getInstance().getBoolean("is04TISR", false)
        set(value) = SPUtils.getInstance().put("is04TISR", value)
        
     * TC007 auto connect setting (legacy - TC001 doesn't use this).
    var isConnect07AutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnect07AutoOpen", false)
        set(value) = SPUtils.getInstance().put("isConnect07AutoOpen", value)
        
     * Free space information (legacy - TC001 doesn't use this).
    var freeSpaceBean: com.topdon.lib.core.repository.FreeSpaceBean
        get() = com.topdon.lib.core.repository.FreeSpaceBean() // Return empty bean for TC001
        set(value) = Unit // No-op for TC001

     * Selected fence type for thermal analysis.
    var selectFenceType: Int
        get() = SPUtils.getInstance().getInt("selectFenceType", 1)
        set(value) = SPUtils.getInstance().put("selectFenceType", value)

     * Get time zone for thermal data display.
    fun getShowZone(): String {
        return SPUtils.getInstance().getString("showZone", "GMT")
    }

}