package com.topdon.lib.core.common

import android.content.Context
import androidx.preference.PreferenceManager
import android.util.Base64
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.topdon.lib.core.bean.CarDetectChildBean
import com.topdon.lib.core.bean.ContinuousBean
import com.topdon.lib.core.bean.WatermarkBean
import com.topdon.lib.core.dialog.CarDetectDialog

/**
 * Current[CN_TEXT]“[CN_TEXT]Settings[CN_TEXT]”[CN_TEXT]，
 *
 * [SaveSettingUtil] [CN_TEXT]“[CN_TEXT]Settings[CN_TEXT]”[CN_TEXT].
 *
 * create by fylder on 2018/6/14
 **/
object SharedManager {
    /**
     * [CN_TEXT].
     */
    var hasClickWinter: Boolean
        get() = SPUtils.getInstance().getBoolean("hasClickWinter", false)
        set(value) = SPUtils.getInstance().put("hasClickWinter", value)

    /**
     * [CN_TEXT]-[CN_TEXT].
     */
    var isNeedShowTrendTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isNeedShowTrendTips", true)
        set(value) = SPUtils.getInstance().put("isNeedShowTrendTips", value)

    /**
     * [CN_TEXT] - [CN_TEXT] 0-[CN_TEXT] 1-[CN_TEXT] 2-[CN_TEXT]
     */
    var houseSpaceUnit: Int
        get() = SPUtils.getInstance().getInt("houseSpaceUnit", 0)
        set(value) {
            SPUtils.getInstance().put("houseSpaceUnit", value)
        }

    /**
     * [CN_TEXT] - [CN_TEXT]，0-[CN_TEXT]USD 1-[CN_TEXT]EUR 2-[CN_TEXT]GBP 3-[CN_TEXT]AUD 4-[CN_TEXT]JPY 5-[CN_TEXT]CAD 6-[CN_TEXT]NZD 7-Person[CN_TEXT]RMB 8-[CN_TEXT]HKD
     */
    var costUnit: Int
        get() = SPUtils.getInstance().getInt("costUnit", 0)
        set(value) {
            SPUtils.getInstance().put("costUnit", value)
        }

    /**
     * [CN_TEXT] TC [CN_TEXT]，[CN_TEXT] false.
     */
    var hasTcLine: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTcLine", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTcLine", value)
        }

    /**
     * [CN_TEXT] TS004 [CN_TEXT]，[CN_TEXT] false.
     */
    var hasTS004: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTS004", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTS004", value)
        }

    /**
     * [CN_TEXT] TC007 [CN_TEXT]，[CN_TEXT] false.
     */
    var hasTC007: Boolean
        get() = SPUtils.getInstance().getBoolean("hasConnectTC007", false)
        set(value) {
            SPUtils.getInstance().put("hasConnectTC007", value)
        }

    /**
     * TC007 [CN_TEXT]，JSON [CN_TEXT].
     */
    var irConfigJsonTC007: String
        get() = SPUtils.getInstance().getString("irConfigJsonTC007")
        set(value) {
            SPUtils.getInstance().put("irConfigJsonTC007", value)
        }

    /**
     * [CN_TEXT] 1-[CN_TEXT]1[CN_TEXT] 2-[CN_TEXT]2[CN_TEXT] 3-[CN_TEXT]3[CN_TEXT] 0-[CN_TEXT]
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
     * [CN_TEXT] 1-[CN_TEXT]1[CN_TEXT] 2-[CN_TEXT]2[CN_TEXT] 0-[CN_TEXT]
     */
    var configGuideStep: Int
        get() = SPUtils.getInstance().getInt("configGuideStep", 1)
        set(value) = SPUtils.getInstance().put("configGuideStep", value)

    /**
     * [CN_TEXT]
     */
    var isHideEmissivityTips: Boolean
        get() = SPUtils.getInstance().getBoolean("isHideEmissivityTips", false)
        set(value) {
            SPUtils.getInstance().put("isHideEmissivityTips", value)
        }

    /**
     * tc007[CN_TEXT]
     */
    var is07HideEmissivityTips: Boolean
        get() = SPUtils.getInstance().getBoolean("is07HideEmissivityTips", false)
        set(value) {
            SPUtils.getInstance().put("is07HideEmissivityTips", value)
        }

    /**
     * TS004[CN_TEXT]”[CN_TEXT]“
     */
    var is04TISR: Boolean
        get() = SPUtils.getInstance().getBoolean("is04TISR", false)
        set(value) {
            SPUtils.getInstance().put("is04TISR", value)
        }

    /**
     * TS004 [CN_TEXT]”[CN_TEXT]“
     */
    var is04AutoSync: Boolean
        get() = SPUtils.getInstance().getBoolean("is04AutoSync", false)
        set(value) {
            SPUtils.getInstance().put("is04AutoSync", value)
        }

    /**
     * Dual light[CN_TEXT]RotateAngle，[CN_TEXT] [0, 2000]，[CN_TEXT] SeekBar [CN_TEXT].id[CN_TEXT]sid[CN_TEXT]
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
     * Dual light[CN_TEXT]，[CN_TEXT] 24.
     */
    fun getManualData(sId: String): ByteArray {
        val strValue = SPUtils.getInstance().getString("manualData_$sId")
        return if (strValue.isNullOrEmpty()) {
            // [CN_TEXT] 1,0,0,0,1,0 6[CN_TEXT] float，[CN_TEXT]
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
     * [CN_TEXT].
     */
    var isConnectAutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnectAutoOpen", false)
        set(value) {
            SPUtils.getInstance().put("isConnectAutoOpen", value)
        }

    /**
     * [CN_TEXT] TC007 [CN_TEXT].
     */
    var isConnect07AutoOpen: Boolean
        get() = SPUtils.getInstance().getBoolean("isConnect07AutoOpen", false)
        set(value) {
            SPUtils.getInstance().put("isConnect07AutoOpen", value)
        }

    /**
     * [CN_TEXT]，[CN_TEXT] OTG [CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipOTG: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipOTG", true)
        set(value) {
            SPUtils.getInstance().put("isTipOTG", value)
        }

    /**
     * [CN_TEXT]-[CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipShutter: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipShutter", true)
        set(value) {
            SPUtils.getInstance().put("isTipShutter", value)
        }

    /**
     * [CN_TEXT]-High temperature[CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipHighTemp: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipHighTemp", true)
        set(value) {
            SPUtils.getInstance().put("isTipHighTemp", value)
        }

    /**
     * [CN_TEXT]-Picture in picture（[CN_TEXT]Dual light）[CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipPinP: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipPinP", true)
        set(value) {
            SPUtils.getInstance().put("isTipPinP", value)
        }

    /**
     * [CN_TEXT]-Observation[CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipCoordinate: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipCoordinate", true)
        set(value) {
            SPUtils.getInstance().put("isTipCoordinate", value)
        }

    /**
     * [CN_TEXT]-AI[CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipAIRecognition: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipAIRecognition", true)
        set(value) {
            SPUtils.getInstance().put("isTipAIRecognition", value)
        }

    /**
     * [CN_TEXT]-ObservationMode-Photo[CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipObservePhoto: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipObservePhoto", true)
        set(value) {
            SPUtils.getInstance().put("isTipObservePhoto", value)
        }

    /**
     * [CN_TEXT]Photo[CN_TEXT]，[CN_TEXT]Settings[CN_TEXT].
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
     * wifi[CN_TEXT]
     * [CN_TEXT]，[CN_TEXT]Settings[CN_TEXT].
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
     * [CN_TEXT]，[CN_TEXT]Settings[CN_TEXT].
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
     * [CN_TEXT]TS004-[CN_TEXT]Switch[CN_TEXT]，[CN_TEXT].
     * true-[CN_TEXT] false-[CN_TEXT]，[CN_TEXT]
     */
    var isTipChangeDevice: Boolean
        get() = SPUtils.getInstance().getBoolean("isTipChangeDevice", true)
        set(value) {
            SPUtils.getInstance().put("isTipChangeDevice", value)
        }

    /**
     * [CN_TEXT]，[CN_TEXT]Switch.
     * true-Switch false-[CN_TEXT]Switch
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
    private const val LANGUAGE = "language" // [CN_TEXT]Settings

    private const val HAS_SHOW_CLAUSE = "hasShowClause" // [CN_TEXT]
    private const val TEMPERATURE_UNIT = "temperature" // [CN_TEXT]
    private const val VERSION_CHECK_DATE = "version_check_date" // [CN_TEXT]

    private const val DEVICE_SN = "deviceSn" // [CN_TEXT]SN
    private const val DEVICE_VERSION = "deviceVersion" // [CN_TEXT]

    private const val IR_CONFIG = "ir_config" // [CN_TEXT](json)
    private const val SP_CUSTOM_PSEUDO = "sp_custom_pseudo" // [CN_TEXT]Pseudo-color[CN_TEXT]
    private const val SP_TARGET_POP = "sp_target_pop" // Target[CN_TEXT]

    private const val SP_SETTING_IS_PUSH = "sp_setting_is_push" // [CN_TEXT]
    private const val SP_SETTING_IS_RECOMMEND = "sp_setting_is_recommend"

    /************************TS004************************************/
    private const val SP_HOT_MODE = "sp_hot_mode" // White hot
    private const val SP_CHANGE_DEVICE = "sp_change_device" // ts001[CN_TEXT]ts004[CN_TEXT]Switch
    private const val SP_TC007_CUSTOM_PSEUDO = "sp_tc007_custom_pseudo" // tc007[CN_TEXT]Pseudo-color[CN_TEXT]

    private const val SP_CAR_DETECT = "sp_car_detect" // [CN_TEXT]

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

    // [CN_TEXT]Application[CN_TEXT]applicationContext[CN_TEXT]，[CN_TEXT]context
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

    // 1: Celsius    0: Fahrenheit
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
     * Target[CN_TEXT]
     */
    fun getTargetPop(): Boolean {
        return SPUtils.getInstance().getBoolean(SP_TARGET_POP, false)
    }

    fun saveTargetPop(targetPop: Boolean) {
        SPUtils.getInstance().put(SP_TARGET_POP, targetPop)
    }

    private const val IR_DUAL_DISP = "ir_dual_disp" // Dual lightRegistration-[CN_TEXT]
    private const val IR_DUAL_DISP_V = "ir_dual_disp_v" // Dual lightRegistration-[CN_TEXT]

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
     * [CN_TEXT]
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
     * TS004[CN_TEXT]-Black hot
     */
    fun getHotMode(): Int {
        return SPUtils.getInstance().getInt(SP_HOT_MODE, 1)
    }

    fun saveHotMode(hotMode: Int) {
        SPUtils.getInstance().put(SP_HOT_MODE, hotMode)
    }

    /**
     * TS004[CN_TEXT]TS001[CN_TEXT]Switch device 0:[CN_TEXT]  1:TS001[CN_TEXT]  2:TS004[CN_TEXT]
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
