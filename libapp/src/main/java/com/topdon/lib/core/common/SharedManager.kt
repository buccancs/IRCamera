package com.topdon.lib.core.common

import android.content.Context
import android.preference.PreferenceManager
import android.util.Base64
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.topdon.lib.core.bean.CarDetectChildBean
import com.topdon.lib.core.bean.ContinuousBean
import com.topdon.lib.core.bean.WatermarkBean
import com.topdon.lib.core.dialog.CarDetectDialog

/**
 * [Chinese text]"[Chinese text]Settings[Chinese text]"[Chinese text], 
 *
 * [SaveSettingUtil] [Chinese text]"[Chinese text]Settings[Chinese text]"[Chinese text].
 *
 * create by fylder on 2018/6/14
 **/
object SharedManager {
    /**
     * [Chinese text]point[Chinese text].
     */
    var hasClickWinter: Boolean
        get() = SPUtils.getInstance().getBoolean("hasClickWinter", false)
        set(value) = SPUtils.getInstance().put("hasClickWinter", value)

    /**
     * [Chinese text]-[Chinese text].
     */
    var isNeedShowTrendTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isNeedShowTrendTips", true)
        set(value) = SPUtils.getInstance().put("isNeedShowTrendTips", value)

    /**
     * [Chinese text] - [Chinese text] 0-[Chinese text] 1-[Chinese text] 2-[Chinese text]
     */
    var houseSpaceUnit: Int
        get() = SPUtils.getInstance().getInt("houseSpaceUnit", 0)
        set(value) {
            SPUtils.getInstance().put("houseSpaceUnit", value)
        }

    /**
     * [Chinese text] - [Chinese text], 0-[Chinese text]USD 1-[Chinese text]EUR 2-[Chinese text]GBP 3-[Chinese text]AUD 4-[Chinese text]JPY 5-[Chinese text]CAD 6-[Chinese text]NZD 7-[Chinese text]RMB 8-[Chinese text]HKD
     */
    var costUnit: Int
        get() = SPUtils.getInstance().getInt("costUnit", 0)
        set(value) {
            SPUtils.getInstance().put("costUnit", value)
        }

    /**
     * [Chinese text]in progress[Chinese text] TC [Chinese text]line[Chinese text], [Chinese text] false.
     */
    var hasTcLine: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTcLine", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTcLine", value)
        }

    /**
     * [Chinese text]in progress[Chinese text] TS004 [Chinese text], [Chinese text] false.
     */
    var hasTS004: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTS004", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTS004", value)
        }

    /**
     * [Chinese text]in progress[Chinese text] TC007 [Chinese text], [Chinese text] false.
     */
    var hasTC007: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTC007", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTC007", value)
        }

    /**
     * TC007 [Chinese text]temperature[Chinese text], JSON [Chinese text].
     */
    var irConfigJsonTC007: String
        get() = SPUtils.getInstance().getString("irConfigJsonTC007")
        set(value) {
            SPUtils.getInstance().put("irConfigJsonTC007", value)
        }

    /**
     * [Chinese text]operation[Chinese text] 1-[Chinese text]1[Chinese text] 2-[Chinese text]2[Chinese text] 3-[Chinese text]3[Chinese text] 0-[Chinese text]
     */
    var homeGuideStep: Int
        get() {
            val value = SPUtils.getInstance().getInt("homeGuideStep", 2)
            return if (value == 1) 2 else value
        }
        set(value) {
            SPUtils.getInstance().put("homeGuideStep", value)
        }

    /**
     * temperature[Chinese text]operation[Chinese text] 1-[Chinese text]1[Chinese text] 2-[Chinese text]2[Chinese text] 0-[Chinese text]
     */
    var configGuideStep: Int
        get() = SPUtils.getInstance().getInt("configGuideStep", 1)
        set(value) = SPUtils.getInstance().put("configGuideStep", value)

    /**
     * [Chinese text]
     */
    var isHideEmissivityTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isHideEmissivityTips", false)
        set(value) {
            SPUtils.getInstance().put("isHideEmissivityTips", value)
        }

    /**
     * tc007[Chinese text]
     */
    var is07HideEmissivityTips: Boolean
        get() = SPUtils.getInstance().getBoolean("is07HideEmissivityTips", false)
        set(value) {
            SPUtils.getInstance().put("is07HideEmissivityTips", value)
        }

    /**
     * TS004[Chinese text]"[Chinese text]"
     */
    var is04TISR: Boolean
        get() = SPUtils.getInstance().getBoolean("is04TISR", false)
        set(value) {
            SPUtils.getInstance().put("is04TISR", value)
        }

    /**
     * TS004 [Chinese text]"[Chinese text]"
     */
    var is04AutoSync: Boolean
        get() = SPUtils.getInstance().getBoolean("is04AutoSync", false)
        set(value) {
            SPUtils.getInstance().put("is04AutoSync", value)
        }

    /**
     * Dual light[Chinese text], [Chinese text]range [0, 2000], [Chinese text] SeekBar [Chinese text].id[Chinese text]sid[Chinese text]
     */
    fun getManualAngle(sId: String): Int {
        return SPUtils.getInstance().getInt("manualAngle_$sId", 1000)
    }

    fun setManualAngle(
        sId: String,
        value: Int,
    ) {
        SPUtils.getInstance().put("manualAngle_$sId", value)
    }

    /**
     * Dual light[Chinese text], [Chinese text] 24.
     */
    fun getManualData(sId: String): ByteArray {
        val strValue = SPUtils.getInstance().getString("manualData_$sId")
        return if (strValue.isNullOrEmpty()) {
            // [Chinese text] 1,0,0,0,1,0 6[Chinese text] float, [Chinese text]
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
                0,
            )
        } else {
            Base64.decode(strValue.toByteArray(), Base64.DEFAULT)
        }
    }

    fun setManualData(
        sId: String,
        value: ByteArray,
    ) {
        if (value.size == 24) {
            SPUtils.getInstance()
                .put("manualData_$sId", String(Base64.encode(value, Base64.DEFAULT)))
        }
    }

    /**
     * [Chinese text].
     */
    var isConnectAutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnectAutoOpen", false)
        set(value) {
            SPUtils.getInstance().put("isConnectAutoOpen", value)
        }

    /**
     * [Chinese text] TC007 [Chinese text].
     */
    var isConnect07AutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnect07AutoOpen", false)
        set(value) {
            SPUtils.getInstance().put("isConnect07AutoOpen", value)
        }

    /**
     * [Chinese text], [Chinese text] OTG [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipOTG: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipOTG", true)
        set(value) {
            SPUtils.getInstance().put("isTipOTG", value)
        }

    /**
     * point[Chinese text]-[Chinese text], [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipShutter: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipShutter", true)
        set(value) {
            SPUtils.getInstance().put("isTipShutter", value)
        }

    /**
     * point[Chinese text]temperature-high[Chinese text], [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipHighTemp: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipHighTemp", true)
        set(value) {
            SPUtils.getInstance().put("isTipHighTemp", value)
        }

    /**
     * point[Chinese text]-[Chinese text]in progress[Chinese text]([Chinese text]Dual light)[Chinese text], [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipPinP: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipPinP", true)
        set(value) {
            SPUtils.getInstance().put("isTipPinP", value)
        }

    /**
     * point[Chinese text]-[Chinese text], [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipCoordinate: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipCoordinate", true)
        set(value) {
            SPUtils.getInstance().put("isTipCoordinate", value)
        }

    /**
     * point[Chinese text]-AI[Chinese text], [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipAIRecognition: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipAIRecognition", true)
        set(value) {
            SPUtils.getInstance().put("isTipAIRecognition", value)
        }

    /**
     * point[Chinese text]-Observation mode-Photo capture[Chinese text], [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipObservePhoto: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipObservePhoto", true)
        set(value) {
            SPUtils.getInstance().put("isTipObservePhoto", value)
        }

    /**
     * [Chinese text]Photo capture[Chinese text], [Chinese text]Settings[Chinese text].
     */
    var continuousBean: ContinuousBean
        get() {
            val json = SPUtils.getInstance().getString("continuousBean", "")
            return if (json.isNullOrEmpty()) {
                ContinuousBean()
            } else {
                Gson().fromJson(
                    json,
                    ContinuousBean::class.java,
                )
            }
        }
        set(value) {
            SPUtils.getInstance().put("continuousBean", Gson().toJson(value))
        }

    /**
     * wifi[Chinese text]
     * [Chinese text], [Chinese text]Settings[Chinese text].
     */
    var wifiWatermarkBean: WatermarkBean
        get() {
            val json = SPUtils.getInstance().getString("wifiWatermarkBean", "")
            return if (json.isNullOrEmpty()) {
                WatermarkBean()
            } else {
                Gson().fromJson(
                    json,
                    WatermarkBean::class.java,
                )
            }
        }
        set(value) {
            SPUtils.getInstance().put("watermarkBean", Gson().toJson(value))
        }

    /**
     * [Chinese text], [Chinese text]Settings[Chinese text].
     */
    var watermarkBean: WatermarkBean
        get() {
            val json = SPUtils.getInstance().getString("watermarkBean", "")
            return if (json.isNullOrEmpty()) {
                WatermarkBean()
            } else {
                Gson().fromJson(
                    json,
                    WatermarkBean::class.java,
                )
            }
        }
        set(value) {
            SPUtils.getInstance().put("watermarkBean", Gson().toJson(value))
        }

    /**
     * point[Chinese text]TS004-[Chinese text]switch[Chinese text], [Chinese text].
     * true-[Chinese text] false-[Chinese text]point[Chinese text], [Chinese text]
     */
    var isTipChangeDevice: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipChangeDevice", true)
        set(value) {
            SPUtils.getInstance().put("isTipChangeDevice", value)
        }

    /**
     * [Chinese text], [Chinese text]switch.
     * true-switch false-[Chinese text]switch
     */
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
    private const val LANGUAGE = "language" // [Chinese text]Settings

    private const val HAS_SHOW_CLAUSE = "hasShowClause" // [Chinese text]
    private const val TEMPERATURE_UNIT = "temperature" // temperature[Chinese text]
    private const val VERSION_CHECK_DATE = "version_check_date" // [Chinese text]

    private const val DEVICE_SN = "deviceSn" // [Chinese text]SN
    private const val DEVICE_VERSION = "deviceVersion" // [Chinese text]

    private const val IR_CONFIG = "ir_config" // temperature[Chinese text](json)
    private const val SP_CUSTOM_PSEUDO = "sp_custom_pseudo" // [Chinese text]Pseudo color[Chinese text]
    private const val SP_TARGET_POP = "sp_target_pop" // Target[Chinese text]

    private const val SP_SETTING_IS_PUSH = "sp_setting_is_push" // [Chinese text]
    private const val SP_SETTING_IS_RECOMMEND = "sp_setting_is_recommend"

    /************************TS004************************************/
    private const val SP_HOT_MODE = "sp_hot_mode" // [Chinese text]
    private const val SP_CHANGE_DEVICE = "sp_change_device" // ts001[Chinese text]ts004[Chinese text]switch
    private const val SP_TC007_CUSTOM_PSEUDO = "sp_tc007_custom_pseudo" // tc007[Chinese text]Pseudo color[Chinese text]

    private const val SP_CAR_DETECT = "sp_car_detect" // [Chinese text]

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

    fun setLanguage(
        context: Context,
        language: String,
    ) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(LANGUAGE, language).apply()
    }

    // [Chinese text]Application[Chinese text]applicationContext[Chinese text], [Chinese text]context
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

    // 1: [Chinese text]    0: [Chinese text]
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

    fun saveTC007CustomPseudo(json: String) {
        SPUtils.getInstance().put(SP_TC007_CUSTOM_PSEUDO, json)
    }

    fun getTC0007CustomPseudo(): String {
        return SPUtils.getInstance().getString(SP_TC007_CUSTOM_PSEUDO, "")
    }

    /**
     * Target[Chinese text]
     */
    fun getTargetPop(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_TARGET_POP, false)
    }

    fun saveTargetPop(targetPop: Boolean) {
        SPUtils.getInstance().put(SP_TARGET_POP, targetPop)
    }

    private const val IR_DUAL_DISP = "ir_dual_disp" // Dual light[Chinese text]-[Chinese text]
    private const val IR_DUAL_DISP_V = "ir_dual_disp_v" // Dual light[Chinese text]-[Chinese text]

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

    /**
     * [Chinese text]
     */
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
    /************************TS004************************************/

    /**
     * TS004[Chinese text]-[Chinese text]
     */
    fun getHotMode(): Int {
        return SPUtils.getInstance().getInt(SP_HOT_MODE, 1)
    }

    fun saveHotMode(hotMode: Int) {
        SPUtils.getInstance().put(SP_HOT_MODE, hotMode)
    }

    /**
     * TS004[Chinese text]TS001[Chinese text]switch device 0:[Chinese text]  1:TS001[Chinese text]  2:TS004[Chinese text]
     */
    fun getChangeDevice(): Int {
        return SPUtils.getInstance().getInt(SP_CHANGE_DEVICE, 0)
    }

    fun saveChangeDevice(device: Int) {
        SPUtils.getInstance().put(SP_CHANGE_DEVICE, device)
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
}
