package com.topdon.lib.core.http.ts004

object TS004URL {
    const val RTSP_URL = "rtsp:// 192.168.40.1/ch0/stream0"
    private const val BASE_URL = "http:// 192.168.40.1:8080"
    const val SET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/setPseudoColor" // SettingsPseudo color[Chinese text]
    const val GET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/getPseudoColor" // [Chinese text]Pseudo color[Chinese text]
    const val SET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam" // Settings[Chinese text]
    const val GET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam" // [Chinese text]
    const val SET_PIP = "$BASE_URL/api/v1/system/setPip" // Settings[Chinese text]in progress[Chinese text]
    const val GET_PIP = "$BASE_URL/api/v1/system/getPip" // [Chinese text]in progress[Chinese text]
    const val SET_ZOOM = "$BASE_URL/api/v1/system/setZoom" // Settings[Chinese text]
    const val GET_ZOOM = "$BASE_URL/api/v1/system/getZoom" // [Chinese text]
    const val SET_SNAPSHOT = "$BASE_URL/api/v1/system/snapshot" // Photo capture
    const val GET_VRECORD = "$BASE_URL/api/v1/system/vrecord" // recording
    const val GET_RECORD_STATUS = "$BASE_URL/api/v1/system/getRecordStatus" // [Chinese text]recording[Chinese text]
    const val GET_VERSION = "$BASE_URL/api/v1/system/getVersion" // [Chinese text]message
    const val GET_DEVICE_DETAILS = "$BASE_URL/api/v1/system/getDeviceInfo" // [Chinese text]message
    const val GET_FREE_SPACE = "$BASE_URL/api/v1/system/getFreeSpace" // [Chinese text]message
    const val GET_FORMAT_STORAGE = "$BASE_URL/api/v1/system/formatStorage" // [Chinese text]
    const val GET_RESET_ALL = "$BASE_URL/api/v1/system/resetAll" // [Chinese text]Settings
    const val GET_DELETE_FILE = "$BASE_URL/api/v1/system/deleteFile" // [Chinese text]
    const val GET_UPGRADE_STATUS = "$BASE_URL/api/v1/system/getUpgradeStatus" // [Chinese text]
    const val SET_TEMPERATURE_STATE = "$BASE_URL/api/v1/system/setTemperatureState" // Settings[Chinese text]
    const val GET_FILE_LIST = "$BASE_URL/api/v1/system/getFileList" // [Chinese text]
    const val SET_DATE_TIME = "$BASE_URL/api/v1/system/setDateTime" // Settings[Chinese text]
    const val GET_SEND_UPGRADE_FILE_START = "$BASE_URL/api/v1/system/sendUpgradeFileStart" // [Chinese text]start
    const val GET_SEND_UPGRADE_FILE_DATA = "$BASE_URL/api/v1/system/sendUpgradeFileData" // [Chinese text]
    const val GET_SEND_UPGRADE_FILE_END = "$BASE_URL/api/v1/system/sendUpgradeFileEnd" // [Chinese text]
    const val GET_REMOTE_UPGRADE = "$BASE_URL/api/v1/system/remoteUpgrade" // [Chinese text]
    const val SET_WIFI_AP_ON_OFF = "$BASE_URL/api/v1/system/setWifiAPOnOff" // Settingswifi on/off
    const val GET_WIFI_AP_CONFIG = "$BASE_URL/api/v1/system/getWifiAPConfig" // [Chinese text]wifi[Chinese text]message
    const val GET_FILE_COUNT = "$BASE_URL/api/v1/system/getFileCount" // [Chinese text]
    const val SET_POWER_ACTION = "$BASE_URL/api/v1/system/setPowerAction" // Settings[Chinese text]
    const val SET_DO_NUC = "$BASE_URL/api/v1/system/doNuc" // nuc
    const val SET_FREEZE = "$BASE_URL/api/v1/system/setFreeze" // [Chinese text]
    const val GET_BATTERY_INFO = "$BASE_URL/api/v1/system/getBatteryInfo" // [Chinese text]message
    const val SET_TISP = "$BASE_URL/api/v1/system/setTISR" // Settings[Chinese text]
    const val GET_TISR = "$BASE_URL/api/v1/system/getTISR" // [Chinese text]
    const val GET_DATE_TIME = "$BASE_URL/api/v1/system/getDateTime" // [Chinese text]
}
