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
    /** hasClickWinter property */
    var hasClickWinter: Boolean
        get() = SPUtils.getInstance().getBoolean("hasClickWinter", false)
        set(value) = SPUtils.getInstance().put("hasClickWinter", value)

     * .
    /** isNeedShowTrendTips property */
    var isNeedShowTrendTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isNeedShowTrendTips", true)
        set(value) = SPUtils.getInstance().put("isNeedShowTrendTips", value)

     *  0- 1- 2
    /** houseSpaceUnit property */
    var houseSpaceUnit: Int
        get() = SPUtils.getInstance().getInt("houseSpaceUnit", 0)
        set(value) {
            SPUtils.getInstance().put("houseSpaceUnit", value)
        }

     *  0-USD 1-EUR 2-GBP 3-AUD 4-JPY 5-CAD 6-NZD 7-RMB 8-HKD
    /** costUnit property */
    var costUnit: Int
        get() = SPUtils.getInstance().getInt("costUnit", 0)
        set(value) {
            SPUtils.getInstance().put("costUnit", value)
        }

     *  TC  false.
    /** hasTcLine property */
    var hasTcLine: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTcLine", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTcLine", value)
        }

     *  TS004  false. (TC001 only - legacy compatibility)
    /** hasTS004 property */
    var hasTS004: Boolean
        get() = false // TC001 only - no TS004 support
        set(value) = Unit // TC001 only - no TS004 support

     *  TC007  false. (TC001 only - legacy compatibility)
    /** hasTC007 property */
    var hasTC007: Boolean
        get() = false // TC001 only - no TC007 support
        set(value) = Unit // TC001 only - no TC007 support


     *  1-1 2-2 3-3 0
    /** homeGuideStep property */
    var homeGuideStep: Int
        get() {
            val value = SPUtils.getInstance().getInt("homeGuideStep", 2)
            return if (value == 1) 2 else value
        }
        set(value) {
            SPUtils.getInstance().put("homeGuideStep", value)
        }

     *  1-1 2-2 0
    /** configGuideStep property */
    var configGuideStep: Int
        get() = SPUtils.getInstance().getInt("configGuideStep", 1)
        set(value) = SPUtils.getInstance().put("configGuideStep", value)


    /** isHideEmissivityTips property */
    var isHideEmissivityTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isHideEmissivityTips", false)
        set(value) {
            SPUtils.getInstance().put("isHideEmissivityTips", value)
        }




     * [0, 2000] SeekBar .idsid
    /**
     * Function description.
     */
    fun getManualAngle(sId: String): Int {
        return SPUtils.getInstance().getInt("manualAngle_${sId}", 1000)
    }

    /**
     * Function description.
     */
    fun setManualAngle(sId: String, value: Int) {
        SPUtils.getInstance().put("manualAngle_${sId}", value)
    }

     * 24.
    /**
     * Function description.
     */
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

    /**
     * Function description.
     */
    fun setManualData(sId: String, value: ByteArray) {
        if (value.size == 24) {
            SPUtils.getInstance()
                .put("manualData_${sId}", String(Base64.encode(value, Base64.DEFAULT)))
        }
    }

     * .
    /** isConnectAutoOpen property */
    var isConnectAutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnectAutoOpen", false)
        set(value) {
            SPUtils.getInstance().put("isConnectAutoOpen", value)
        }



     * OTG .
     * true- false
    /** isTipOTG property */
    var isTipOTG: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipOTG", true)
        set(value) {
            SPUtils.getInstance().put("isTipOTG", value)
        }

     * .
     * true- false
    /** isTipShutter property */
    var isTipShutter: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipShutter", true)
        set(value) {
            SPUtils.getInstance().put("isTipShutter", value)
        }

     * .
     * true- false
    /** isTipHighTemp property */
    var isTipHighTemp: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipHighTemp", true)
        set(value) {
            SPUtils.getInstance().put("isTipHighTemp", value)
        }

     * .
     * true- false
    /** isTipPinP property */
    var isTipPinP: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipPinP", true)
        set(value) {
            SPUtils.getInstance().put("isTipPinP", value)
        }

     * .
     * true- false
    /** isTipCoordinate property */
    var isTipCoordinate: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipCoordinate", true)
        set(value) {
            SPUtils.getInstance().put("isTipCoordinate", value)
        }

     * AI.
     * true- false
    /** isTipAIRecognition property */
    var isTipAIRecognition: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipAIRecognition", true)
        set(value) {
            SPUtils.getInstance().put("isTipAIRecognition", value)
        }

     * .
     * true- false
    /** isTipObservePhoto property */
    var isTipObservePhoto: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipObservePhoto", true)
        set(value) {
            SPUtils.getInstance().put("isTipObservePhoto", value)
        }

     * .
    /** continuousBean property */
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
    /** wifiWatermarkBean property */
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
    /** watermarkBean property */
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
    /** isChangeDevice property */
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


    /**
     * Function description.
     */
    fun setToken(token: String) {
        SPUtils.getInstance().put(TOKEN, token)
    }

    /**
     * Function description.
     */
    fun getToken(): String {
        return SPUtils.getInstance().getString(TOKEN, "")
    }

    /**
     * Function description.
     */
    fun setUserId(token: String) {
        SPUtils.getInstance().put(USER_ID, token)
    }

    /**
     * Function description.
     */
    fun getUserId(): String {
        return SPUtils.getInstance().getString(USER_ID, "0")
    }

    /**
     * Function description.
     */
    fun setUsername(username: String) {
        SPUtils.getInstance().put(USERNAME, username)
    }

    /**
     * Function description.
     */
    fun getUsername(): String {
        return SPUtils.getInstance().getString(USERNAME, "")
    }

    /**
     * Function description.
     */
    fun setNickname(nickname: String) {
        SPUtils.getInstance().put(NICKNAME, nickname)
    }

    /**
     * Function description.
     */
    fun getNickname(): String {
        return SPUtils.getInstance().getString(NICKNAME, "")
    }

    /**
     * Function description.
     */
    fun setHeadIcon(headIcon: String) {
        SPUtils.getInstance().put(HEAD_ICON, headIcon)
    }

    /**
     * Function description.
     */
    fun getHeadIcon(): String {
        return SPUtils.getInstance().getString(HEAD_ICON, "")
    }


    /**
     * Function description.
     */
    fun setBaseHost(value: String) {
        return SPUtils.getInstance().put(BASE_HOST, value)
    }

    /**
     * Function description.
     */
    fun getBaseHost(): String {
        return SPUtils.getInstance().getString(BASE_HOST, "")
    }

    /**
     * Function description.
     */
    fun setLanguage(context: Context, language: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(LANGUAGE, language).apply()
    }

    // ApplicationapplicationContextcontext
    /**
     * Function description.
     */
    fun getLanguage(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGE, "")!!
    }


    /**
     * Function description.
     */
    fun setHasShowClause(hasShowClause: Boolean) {
        return SPUtils.getInstance().put(HAS_SHOW_CLAUSE, hasShowClause)
    }

    /**
     * Function description.
     */
    fun getHasShowClause(): Boolean {
        return SPUtils.getInstance().getBoolean(HAS_SHOW_CLAUSE, false)
    }

    /**
     * Function description.
     */
    fun setIRConfig(value: String) {
        return SPUtils.getInstance().put(IR_CONFIG, value)
    }

    /**
     * Function description.
     */
    fun getIRConfig(): String {
        return SPUtils.getInstance().getString(IR_CONFIG, "")
    }

    /**
     * Function description.
     */
    fun setTemperature(value: Int) {
        return SPUtils.getInstance().put(TEMPERATURE_UNIT, value)
    }

    // 1:     0:
    /**
     * Function description.
     */
    fun getTemperature(): Int {
        return SPUtils.getInstance().getInt(TEMPERATURE_UNIT, 1)
    }

    /**
     * Function description.
     */
    fun setVersionCheckDate(value: Long) {
        return SPUtils.getInstance().put(VERSION_CHECK_DATE, value)
    }

    /**
     * Function description.
     */
    fun getVersionCheckDate(): Long {
        return SPUtils.getInstance().getLong(VERSION_CHECK_DATE, 0)
    }

    /**
     * Function description.
     */
    fun setDeviceSn(value: String) {
        return SPUtils.getInstance().put(DEVICE_SN, value)
    }

    /**
     * Function description.
     */
    fun getDeviceSn(): String {
        return SPUtils.getInstance().getString(DEVICE_SN, "")
    }

    /**
     * Function description.
     */
    fun setDeviceVersion(value: String) {
        return SPUtils.getInstance().put(DEVICE_VERSION, value)
    }

    /**
     * Function description.
     */
    fun getDeviceVersion(): String {
        return SPUtils.getInstance().getString(DEVICE_VERSION, "")
    }

    /**
     * Function description.
     */
    fun saveCustomPseudo(json: String) {
        SPUtils.getInstance().put(SP_CUSTOM_PSEUDO, json)
    }

    /**
     * Function description.
     */
    fun getCustomPseudo(): String {
        return SPUtils.getInstance().getString(SP_CUSTOM_PSEUDO, "")
    }


    /**
     * Function description.
     */
    fun getTargetPop(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_TARGET_POP, false)
    }

    /**
     * Function description.
     */
    fun saveTargetPop(targetPop: Boolean) {
        SPUtils.getInstance().put(SP_TARGET_POP, targetPop)
    }


    private const val IR_DUAL_DISP = "ir_dual_disp"//-
    private const val IR_DUAL_DISP_V = "ir_dual_disp_v"//-


    /**
     * Function description.
     */
    fun saveSettingIsPush(isPush: Boolean) {
        SPUtils.getInstance().put(SP_SETTING_IS_PUSH, isPush)
    }

    /**
     * Function description.
     */
    fun getSettingIsPush(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_SETTING_IS_PUSH, true)
    }

    /**
     * Function description.
     */
    fun saveSettingIsRecommend(isRecommend: Boolean) {
        SPUtils.getInstance().put(SP_SETTING_IS_RECOMMEND, isRecommend)
    }

    /**
     * Function description.
     */
    fun getSettingIsRecommend(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_SETTING_IS_RECOMMEND, true)
    }

    /**
     * Function description.
     */
    fun getMainPermissionsState(): Boolean {
        return SPUtils.getInstance().getBoolean("main_permissions_state", false)
    }

    /**
     * Function description.
     */
    fun setMainPermissionsState(value: Boolean) {
        return SPUtils.getInstance().put("main_permissions_state", value)
    }

    /**
     * Function description.
     */
    fun getImagePermissionsState(): Boolean {
        return SPUtils.getInstance().getBoolean("storage_permissions_state", false)
    }

    /**
     * Function description.
     */
    fun setImagePermissionsState(value: Boolean) {
        return SPUtils.getInstance().put("storage_permissions_state", value)
    }

    /**
     * Function description.
     */
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

    /**
     * Function description.
     */
    fun saveCarDetectInfo(bean: CarDetectChildBean) {
        SPUtils.getInstance().put(SP_CAR_DETECT, GsonUtils.toJson(bean))
    }

    // Legacy device-specific properties - now defaults for TC001 compatibility
    
     * TS004 auto sync setting (legacy - TC001 doesn't use this).
    /** is04AutoSync property */
    var is04AutoSync: Boolean
        get() = SPUtils.getInstance().getBoolean("is04AutoSync", false)
        set(value) = SPUtils.getInstance().put("is04AutoSync", value)
        
     * TS004 TISR setting (legacy - TC001 doesn't use this).
    /** is04TISR property */
    var is04TISR: Boolean
        get() = SPUtils.getInstance().getBoolean("is04TISR", false)
        set(value) = SPUtils.getInstance().put("is04TISR", value)
        
     * TC007 auto connect setting (legacy - TC001 doesn't use this).
    /** isConnect07AutoOpen property */
    var isConnect07AutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnect07AutoOpen", false)
        set(value) = SPUtils.getInstance().put("isConnect07AutoOpen", value)
        
     * Free space information (legacy - TC001 doesn't use this).
    /** freeSpaceBean property */
    var freeSpaceBean: com.topdon.lib.core.repository.FreeSpaceBean
        get() = com.topdon.lib.core.repository.FreeSpaceBean() // Return empty bean for TC001
        set(value) = Unit // No-op for TC001

     * Selected fence type for thermal analysis.
    /** selectFenceType property */
    var selectFenceType: Int
        get() = SPUtils.getInstance().getInt("selectFenceType", 1)
        set(value) = SPUtils.getInstance().put("selectFenceType", value)

     * Get time zone for thermal data display.
    /**
     * Function description.
     */
    fun getShowZone(): String {
        return SPUtils.getInstance().getString("showZone", "GMT")
    }

}