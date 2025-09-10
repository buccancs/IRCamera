package com.topdon.lib.core.http.ts004

object TS004URL {
    const val RTSP_URL = "rtsp://192.168.40.1/ch0/stream0"
    private const val BASE_URL = "http://192.168.40.1:8080"
    const val SET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/setPseudoColor" // SettingsPseudo-colordata
    const val GET_PSEUDO_COLOR = "$BASE_URL/api/v1/system/getPseudoColor" // dataPseudo-colordata
    const val SET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam" // Settingsdata
    const val GET_PANEL_PARAM = "$BASE_URL/api/v1/system/setPanelParam" // data
    const val SET_PIP = "$BASE_URL/api/v1/system/setPip" // SettingsPicture in picture
    const val GET_PIP = "$BASE_URL/api/v1/system/getPip" // dataPicture in pictureState
    const val SET_ZOOM = "$BASE_URL/api/v1/system/setZoom" // Settingsdata
    const val GET_ZOOM = "$BASE_URL/api/v1/system/getZoom" // data
    const val SET_SNAPSHOT = "$BASE_URL/api/v1/system/snapshot" // Photo
    const val GET_VRECORD = "$BASE_URL/api/v1/system/vrecord" // Video
    const val GET_RECORD_STATUS = "$BASE_URL/api/v1/system/getRecordStatus" // dataVideoState
    const val GET_VERSION = "$BASE_URL/api/v1/system/getVersion" // data
    const val GET_DEVICE_DETAILS = "$BASE_URL/api/v1/system/getDeviceInfo" // data
    const val GET_FREE_SPACE = "$BASE_URL/api/v1/system/getFreeSpace" // data
    const val GET_FORMAT_STORAGE = "$BASE_URL/api/v1/system/formatStorage" // data
    const val GET_RESET_ALL = "$BASE_URL/api/v1/system/resetAll" // dataSettings
    const val GET_DELETE_FILE = "$BASE_URL/api/v1/system/deleteFile" // Deletedata
    const val GET_UPGRADE_STATUS = "$BASE_URL/api/v1/system/getUpgradeStatus" // dataState
    const val SET_TEMPERATURE_STATE = "$BASE_URL/api/v1/system/setTemperatureState" // SettingsTemperature measurementdata
    const val GET_FILE_LIST = "$BASE_URL/api/v1/system/getFileList" // data
    const val SET_DATE_TIME = "$BASE_URL/api/v1/system/setDateTime" // Settingsdata
    const val GET_SEND_UPGRADE_FILE_START = "$BASE_URL/api/v1/system/sendUpgradeFileStart" // data
    const val GET_SEND_UPGRADE_FILE_DATA = "$BASE_URL/api/v1/system/sendUpgradeFileData" // data
    const val GET_SEND_UPGRADE_FILE_END = "$BASE_URL/api/v1/system/sendUpgradeFileEnd" // data
    const val GET_REMOTE_UPGRADE = "$BASE_URL/api/v1/system/remoteUpgrade" // data
    const val SET_WIFI_AP_ON_OFF = "$BASE_URL/api/v1/system/setWifiAPOnOff" // Settingswifi on/off
    const val GET_WIFI_AP_CONFIG = "$BASE_URL/api/v1/system/getWifiAPConfig" // datawifidata
    const val GET_FILE_COUNT = "$BASE_URL/api/v1/system/getFileCount" // data
    const val SET_POWER_ACTION = "$BASE_URL/api/v1/system/setPowerAction" // SettingsdataState
    const val SET_DO_NUC = "$BASE_URL/api/v1/system/doNuc" // nuc
    const val SET_FREEZE = "$BASE_URL/api/v1/system/setFreeze" // data
    const val GET_BATTERY_INFO = "$BASE_URL/api/v1/system/getBatteryInfo" // data
    const val SET_TISP = "$BASE_URL/api/v1/system/setTISR" // Settingsdata
    const val GET_TISR = "$BASE_URL/api/v1/system/getTISR" // dataState
    const val GET_DATE_TIME = "$BASE_URL/api/v1/system/getDateTime" // data
}
