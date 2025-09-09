package com.topdon.lib.core.http.ts004

object TS004URL {
    const val RTSP_URL = "rtsp://192.168.40.1/ch0/stream0"
    private const val BASE_URL = "http://192.168.40.1:8080"
    const val SET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/setPseudoColor" // SettingsPseudo-color[CN_TEXT]
    const val GET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/getPseudoColor" // [CN_TEXT]Pseudo-color[CN_TEXT]
    const val SET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam" // Settings[CN_TEXT]
    const val GET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam" // [CN_TEXT]
    const val SET_PIP = "$BASE_URL/api/v1/system/setPip" // SettingsPicture in picture
    const val GET_PIP = "$BASE_URL/api/v1/system/getPip" // [CN_TEXT]Picture in pictureState
    const val SET_ZOOM = "$BASE_URL/api/v1/system/setZoom" // Settings[CN_TEXT]
    const val GET_ZOOM = "$BASE_URL/api/v1/system/getZoom" // [CN_TEXT]
    const val SET_SNAPSHOT = "$BASE_URL/api/v1/system/snapshot" // Photo
    const val GET_VRECORD = "$BASE_URL/api/v1/system/vrecord" // Video
    const val GET_RECORD_STATUS = "$BASE_URL/api/v1/system/getRecordStatus" // [CN_TEXT]VideoState
    const val GET_VERSION = "$BASE_URL/api/v1/system/getVersion" // [CN_TEXT]
    const val GET_DEVICE_DETAILS = "$BASE_URL/api/v1/system/getDeviceInfo" // [CN_TEXT]
    const val GET_FREE_SPACE = "$BASE_URL/api/v1/system/getFreeSpace" // [CN_TEXT]
    const val GET_FORMAT_STORAGE = "$BASE_URL/api/v1/system/formatStorage" // [CN_TEXT]
    const val GET_RESET_ALL = "$BASE_URL/api/v1/system/resetAll" // [CN_TEXT]Settings
    const val GET_DELETE_FILE = "$BASE_URL/api/v1/system/deleteFile" // Delete[CN_TEXT]
    const val GET_UPGRADE_STATUS = "$BASE_URL/api/v1/system/getUpgradeStatus" // [CN_TEXT]State
    const val SET_TEMPERATURE_STATE = "$BASE_URL/api/v1/system/setTemperatureState" // SettingsTemperature measurement[CN_TEXT]
    const val GET_FILE_LIST = "$BASE_URL/api/v1/system/getFileList" // [CN_TEXT]
    const val SET_DATE_TIME = "$BASE_URL/api/v1/system/setDateTime" // Settings[CN_TEXT]
    const val GET_SEND_UPGRADE_FILE_START = "$BASE_URL/api/v1/system/sendUpgradeFileStart" // [CN_TEXT]
    const val GET_SEND_UPGRADE_FILE_DATA = "$BASE_URL/api/v1/system/sendUpgradeFileData" // [CN_TEXT]
    const val GET_SEND_UPGRADE_FILE_END = "$BASE_URL/api/v1/system/sendUpgradeFileEnd" // [CN_TEXT]
    const val GET_REMOTE_UPGRADE = "$BASE_URL/api/v1/system/remoteUpgrade" // [CN_TEXT]
    const val SET_WIFI_AP_ON_OFF = "$BASE_URL/api/v1/system/setWifiAPOnOff" // Settingswifi on/off
    const val GET_WIFI_AP_CONFIG = "$BASE_URL/api/v1/system/getWifiAPConfig" // [CN_TEXT]wifi[CN_TEXT]
    const val GET_FILE_COUNT = "$BASE_URL/api/v1/system/getFileCount" // [CN_TEXT]
    const val SET_POWER_ACTION = "$BASE_URL/api/v1/system/setPowerAction" // Settings[CN_TEXT]State
    const val SET_DO_NUC = "$BASE_URL/api/v1/system/doNuc" // nuc
    const val SET_FREEZE = "$BASE_URL/api/v1/system/setFreeze" // [CN_TEXT]
    const val GET_BATTERY_INFO = "$BASE_URL/api/v1/system/getBatteryInfo" // [CN_TEXT]
    const val SET_TISP = "$BASE_URL/api/v1/system/setTISR" // Settings[CN_TEXT]
    const val GET_TISR = "$BASE_URL/api/v1/system/getTISR" // [CN_TEXT]State
    const val GET_DATE_TIME = "$BASE_URL/api/v1/system/getDateTime" // [CN_TEXT]
}
