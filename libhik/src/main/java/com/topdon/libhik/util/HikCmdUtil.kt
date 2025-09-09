package com.topdon.libhik.util

import android.content.Context
import com.elvishew.xlog.XLog
import com.hcusbsdk.Interface.FStreamCallBack
import com.hcusbsdk.Interface.JavaInterface
import com.hcusbsdk.Interface.USB_COMMAND_STATE
import com.hcusbsdk.Interface.USB_DEVICE_INFO
import com.hcusbsdk.Interface.USB_DEVICE_REG_RES
import com.hcusbsdk.Interface.USB_IMAGE_CONTRAST
import com.hcusbsdk.Interface.USB_IMAGE_ENHANCEMENT
import com.hcusbsdk.Interface.USB_IMAGE_VIDEO_ADJUST
import com.hcusbsdk.Interface.USB_STREAM_CALLBACK_PARAM
import com.hcusbsdk.Interface.USB_SYSTEM_SERIAL_DATA_TRANSMISSION
import com.hcusbsdk.Interface.USB_THERMAL_STREAM_PARAM
import com.hcusbsdk.Interface.USB_THERMOMETRY_BASIC_PARAM
import com.hcusbsdk.Interface.USB_USER_LOGIN_INFO
import com.hcusbsdk.Interface.USB_VIDEO_PARAM
import kotlinx.coroutines.delay
import java.io.File

/**
 * [CN_TEXT]Core[CN_TEXT].
 *
 * [CN_TEXT] SDK [CN_TEXT] API [CN_TEXT]，[CN_TEXT]，
 * [CN_TEXT]。
 *
 * Created by LCG on 2024/11/19.
 */
object HikCmdUtil {
    /**
     * [CN_TEXT]
     */
    private const val TAG = "HikCmd"


    /**
     * [CN_TEXT].
     */
    fun init(): Boolean = if (JavaInterface.getInstance().USB_Init()) {
        XLog.i("$TAG init() [CN_TEXT]")
        true
    } else {
        XLog.e("$TAG init() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        false
    }

    /**
     * Settings[CN_TEXT]，Note，[CN_TEXT].
     */
    fun setLogPath(logFile: File?) {
        if (logFile != null) {
            JavaInterface.getInstance().USB_SetLogToFile(JavaInterface.INFO_LEVEL, logFile.absolutePath, 1)
        }
    }

    /**
     * [CN_TEXT] userId.
     * @return [CN_TEXT] [JavaInterface.USB_INVALID_USER_ID]
     */
    fun login(context: Context): Int {
        //[CN_TEXT]
        val deviceCount: Int = JavaInterface.getInstance().USB_GetDeviceCount(context)
        if (deviceCount < 0) {
            XLog.e("$TAG login() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_USER_ID
        }
        if (deviceCount == 0) {
            XLog.e("$TAG login() [CN_TEXT]！")
            return JavaInterface.USB_INVALID_USER_ID
        }

        //[CN_TEXT]
        val deviceInfoArray: Array<USB_DEVICE_INFO> = Array(JavaInterface.MAX_DEVICE_NUM) {
            USB_DEVICE_INFO()
        }
        if (!JavaInterface.getInstance().USB_EnumDevices(deviceCount, deviceInfoArray)) {
            XLog.e("$TAG login() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_USER_ID
        }

        val loginInfo = USB_USER_LOGIN_INFO()
        loginInfo.dwTimeout = 5000 //[CN_TEXT]
        loginInfo.dwDevIndex = deviceInfoArray[0].dwIndex
        loginInfo.dwVID = deviceInfoArray[0].dwVID
        loginInfo.dwPID = deviceInfoArray[0].dwPID
        loginInfo.dwFd = deviceInfoArray[0].dwFd
        val userId: Int = JavaInterface.getInstance().USB_Login(loginInfo, USB_DEVICE_REG_RES())
        if (userId == JavaInterface.USB_INVALID_USER_ID) {
            XLog.e("$TAG login() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        } else {
            XLog.i("$TAG login() [CN_TEXT]")
        }
        return userId
    }

    /**
     * [CN_TEXT]：[CN_TEXT]、[CN_TEXT]Pseudo-color[CN_TEXT]White hot、[CN_TEXT]、[CN_TEXT]、[CN_TEXT].
     */
    suspend fun initConfig(userId: Int) {
        //[CN_TEXT]、[CN_TEXT]Pseudo-color[CN_TEXT]White hot
        val imageParam = USB_IMAGE_ENHANCEMENT()
        if (JavaInterface.getInstance().USB_GetImageEnhancement(userId, imageParam)) {
            if (imageParam.byLSEDetailEnabled != 1.toByte() || imageParam.byPaletteMode != 1.toByte()) {
                XLog.i("$TAG initConfig() [CN_TEXT]、[CN_TEXT]Pseudo-color(White hot)")
                //1-White hot 2-Black hot 10-[CN_TEXT]1 11-Rainbow 12-[CN_TEXT]2 13-Iron red1 14-Iron red2 15-[CN_TEXT]
                //16-[CN_TEXT]1 17-[CN_TEXT]2 18-[CN_TEXT] 19-[CN_TEXT] 20-Red hot 21-[CN_TEXT] 22-[CN_TEXT]
                imageParam.byPaletteMode = 1
                imageParam.byLSEDetailEnabled = 1 //0-[CN_TEXT] 1-[CN_TEXT]
                if (JavaInterface.getInstance().USB_SetImageEnhancement(userId, imageParam)) {
                    checkCommandState(userId) {
                        XLog.v("$TAG initConfig() Settings[CN_TEXT] $it")
                    }
                } else {
                    XLog.e("$TAG initConfig() Settings[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
            XLog.e("$TAG initConfig() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }

        //[CN_TEXT]
        val basicTempParam = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, basicTempParam)) {
            if (basicTempParam.byThermometryStreamOverlay != 1.toByte()) {
                XLog.i("$TAG initConfig() Settings[CN_TEXT]")
                basicTempParam.byThermometryStreamOverlay = 1 //2-[CN_TEXT] 1-[CN_TEXT]
                if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, basicTempParam)) {
                    checkCommandState(userId) {
                        XLog.v("$TAG initConfig() SettingsTemperature measurement[CN_TEXT] $it")
                    }
                } else {
                    XLog.e("$TAG initConfig() SettingsTemperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
            XLog.e("$TAG initConfig() [CN_TEXT]Temperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }


    /**
     * [CN_TEXT]
     */
    fun shutter(userId: Int) {
        JavaInterface.getInstance().USB_SetImageManualCorrect(userId)
    }

    /**
     * [CN_TEXT]
     */
    suspend fun setAutoShutter(userId: Int, isAutoShutter: Boolean) {
        XLog.i("$TAG setAutoShutter() ${if (isAutoShutter) "[CN_TEXT]" else "[CN_TEXT]"} [CN_TEXT]")
        val param = USB_SYSTEM_SERIAL_DATA_TRANSMISSION()
        param.byMode = 2 //0-[CN_TEXT] 1-[CN_TEXT] 2-[CN_TEXT]
        param.wDeviceCMDFlag = 0
        param.dwDeviceCMD = 0x2001 //[CN_TEXT] code
        param.dwValue = if (isAutoShutter) 1 else 0
        if (JavaInterface.getInstance().USB_SetSystemSerialData(userId, param)) {
            checkCommandState(userId) {
                XLog.v("$TAG setAutoShutter() [CN_TEXT] $it")
            }
        } else {
            XLog.e("$TAG setAutoShutter() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * [CN_TEXT]
     */
    suspend fun startCorrect(userId: Int) {
        XLog.i("$TAG startCorrect() [CN_TEXT]")
        val param = USB_SYSTEM_SERIAL_DATA_TRANSMISSION()
        param.byMode = 2 //0-[CN_TEXT] 1-[CN_TEXT] 2-[CN_TEXT]
        param.wDeviceCMDFlag = 0
        param.dwDeviceCMD = 0xf026 //[CN_TEXT] code
        param.dwValue = 2
        if (JavaInterface.getInstance().USB_SetSystemSerialData(userId, param)) {
            checkCommandState(userId) {
                XLog.v("$TAG startCorrect() [CN_TEXT] $it")
            }
        } else {
            XLog.e("$TAG startCorrect() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }



    /**
     * Settings[CN_TEXT].
     * @param contrast [CN_TEXT] `[0,100]`
     */
    suspend fun setContrast(userId: Int, contrast: Int) {
        XLog.i("$TAG setContrast($contrast) Settings[CN_TEXT]")
        val param = USB_IMAGE_CONTRAST()
        param.dwContrast = contrast
        if (JavaInterface.getInstance().USB_SetImageContrast(userId, param)) {
            checkCommandState(userId) {
                XLog.v("$TAG setContrast() Settings[CN_TEXT] $it")
            }
        } else {
            XLog.e("$TAG setContrast() Settings[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }



    /* ******************************  [CN_TEXT] ImageEnhancement  ****************************** */
    /**
     * Settings[CN_TEXT]([CN_TEXT])[CN_TEXT]
     */
    suspend fun setEnhanceLevel(userId: Int, level: Int) {
        val param = USB_IMAGE_ENHANCEMENT()
        if (JavaInterface.getInstance().USB_GetImageEnhancement(userId, param)) {
            if (param.dwLSEDetailLevel == level) {
                return
            }
            XLog.i("$TAG setEnhanceLevel() [CN_TEXT] ${param.dwLSEDetailLevel}->$level")
            param.dwLSEDetailLevel = level
            if (JavaInterface.getInstance().USB_SetImageEnhancement(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setEnhanceLevel() Settings[CN_TEXT] $it")
                }
            } else {
                XLog.e("$TAG setEnhanceLevel() Settings[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setEnhanceLevel() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }



    /* ******************************  [CN_TEXT] ImageVideoAdjust  ****************************** */
    /**
     * Settings[CN_TEXT].
     * @param isMirror true-[CN_TEXT] false-[CN_TEXT]
     */
    suspend fun setMirror(userId: Int, rotateAngle: Int, isMirror: Boolean) {
        val videoAdjust = USB_IMAGE_VIDEO_ADJUST()
        if (JavaInterface.getInstance().USB_GetImageVideoAdjust(userId, videoAdjust)) {
            val mirrorCode: Byte = if (isMirror) (if (rotateAngle == 0 || rotateAngle == 180) 2 else 3) else 0
            if (videoAdjust.byImageFlipStyle == mirrorCode) {
                return
            }
            XLog.i("$TAG setMirror() [CN_TEXT] ${videoAdjust.byImageFlipStyle}->$mirrorCode")
            videoAdjust.byImageFlipStyle = mirrorCode
            if (JavaInterface.getInstance().USB_SetImageVideoAdjust(userId, videoAdjust)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setMirror() Settings[CN_TEXT] $it")
                }
            } else {
                XLog.e("$TAG setMirror() Settings[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setMirror() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }



    /* ******************************  [CN_TEXT]  ****************************** */
    /**
     * [CN_TEXT].
     * @return [CN_TEXT] Id，[CN_TEXT] [JavaInterface.USB_INVALID_CHANNEL]
     */
    suspend fun startStream(userId: Int, callback: FStreamCallBack): Int {
        //Settings[CN_TEXT]、[CN_TEXT]
        val videoAdjust = USB_IMAGE_VIDEO_ADJUST()
        if (JavaInterface.getInstance().USB_GetImageVideoAdjust(userId, videoAdjust)) {
            val isRotateChange = videoAdjust.byCorridor != 0.toByte()
            val isMirrorChange = videoAdjust.byImageFlipStyle != 0.toByte()
            if (isRotateChange || isMirrorChange) {
                XLog.i("$TAG startStream() Settings[CN_TEXT]：[CN_TEXT]、[CN_TEXT]")
                videoAdjust.byCorridor = 0 //0-256x192 1-192x256
                videoAdjust.byImageFlipStyle = 0 //[CN_TEXT] 0-[CN_TEXT] 1-[CN_TEXT] 2-[CN_TEXT] 3-[CN_TEXT]
                if (JavaInterface.getInstance().USB_SetImageVideoAdjust(userId, videoAdjust)) {
                    checkCommandState(userId) {
                        XLog.v("$TAG startStream() Settings[CN_TEXT] $it")
                    }
                } else {
                    XLog.e("$TAG startStream() Settings[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
            XLog.e("$TAG startStream() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }

        //Settings[CN_TEXT]
        //[CN_TEXT] 256x392 [CN_TEXT] 192x520 [CN_TEXT](192+4)*2 [CN_TEXT] (256+4)*2
        //Rotate[CN_TEXT]，[CN_TEXT] 256x192 [CN_TEXT]
        XLog.i("$TAG startStream() Settings[CN_TEXT]：256x392")
        val videoParam = USB_VIDEO_PARAM()
        videoParam.dwVideoFormat = JavaInterface.USB_STREAM_YUY2
        videoParam.dwWidth = 256
        videoParam.dwHeight = 392
        videoParam.dwFramerate = 25 //25[CN_TEXT]
        if (JavaInterface.getInstance().USB_SetVideoParam(userId, videoParam)) {
            checkCommandState(userId) {
                XLog.v("$TAG startStream() Settings[CN_TEXT] $it")
            }
        } else {
            XLog.w("$TAG startStream() Settings[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        }

        //[CN_TEXT]
        XLog.i("$TAG startStream() [CN_TEXT]")
        val callbackParam = USB_STREAM_CALLBACK_PARAM()
        callbackParam.dwStreamType = JavaInterface.USB_STREAM_YUY2
        callbackParam.fnStreamCallBack = callback
        val callbackId: Int = JavaInterface.getInstance().USB_StartStreamCallback(userId, callbackParam)
        if (callbackId == JavaInterface.USB_INVALID_CHANNEL) {
            XLog.w("$TAG startStream() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        } else {
            XLog.v("$TAG startStream() [CN_TEXT]")
        }

        //Settings[CN_TEXT]Type
        XLog.i("$TAG startStream() Settings[CN_TEXT]Type：8")
        val streamParam = USB_THERMAL_STREAM_PARAM()
        streamParam.byVideoCodingType = 8
        if (JavaInterface.getInstance().USB_SetThermalStreamParam(userId, streamParam)) {
            checkCommandState(userId) {
                XLog.v("$TAG startStream() Settings[CN_TEXT]Type $it")
            }
        } else {
            XLog.w("$TAG startStream() Settings[CN_TEXT]Type[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        }

        return callbackId
    }
    /**
     * [CN_TEXT].
     */
    fun removeStreamCallback(userId: Int, callbackId: Int) {
        if (callbackId != JavaInterface.USB_INVALID_CHANNEL) {
            if (!JavaInterface.getInstance().USB_StopChannel(userId, callbackId)) {
                XLog.w("$TAG removeStreamCallback() [CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            }
        }
    }



    /* ******************************  Temperature measurement[CN_TEXT] ThermometryBasicParam  ****************************** */
    /**
     * Settings[CN_TEXT]
     * @param emissivity [CN_TEXT] `[1, 100]`
     */
    suspend fun setEmissivity(userId: Int, emissivity: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            if (param.dwEmissivity == emissivity) {
                return
            }
            XLog.i("$TAG setEmissivity() [CN_TEXT] ${param.dwEmissivity}->$emissivity")
            param.dwEmissivity = emissivity
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setEmissivity() SettingsTemperature measurement[CN_TEXT] $it")
                }
            } else {
                XLog.e("$TAG setEmissivity() SettingsTemperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setEmissivity() [CN_TEXT]Temperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * SettingsTemperature measurement[CN_TEXT]，[CN_TEXT] cm
     * @param distance [CN_TEXT] `[30, 200]`
     */
    suspend fun setDistance(userId: Int, distance: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            if (param.dwDistance == distance) {
                return
            }
            XLog.i("$TAG setDistance() [CN_TEXT]Temperature measurement[CN_TEXT] ${param.dwDistance}cm->${distance}cm")
            param.dwDistance = distance
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setDistance() SettingsTemperature measurement[CN_TEXT] $it")
                }
            } else {
                XLog.e("$TAG setDistance() SettingsTemperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setDistance() [CN_TEXT]Temperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * SettingsTemperature measurement [CN_TEXT](-1)/Normal temperature(1)/High temperature(0) [CN_TEXT].
     */
    suspend fun setTemperatureMode(userId: Int, mode: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            //[CN_TEXT]：[CN_TEXT] > Temperature measurement[CN_TEXT]
            val oldAuto: Byte = param.byTemperatureRangeAutoChangedEnabled
            val oldRange: Byte = param.byTemperatureRange
            val newAuto: Byte = if (mode == -1) 1 else 0 //0-[CN_TEXT] 1-[CN_TEXT]
            val newRange: Byte = if (mode == 1) 2 else 3 //2(-20~150Normal temperature[CN_TEXT]) 3(100~550High temperature[CN_TEXT])
            if (oldAuto == newAuto && (oldRange == newRange || newRange == 1.toByte())) {
                return
            }
            XLog.i("$TAG setTemperatureMode() [CN_TEXT]Temperature measurement[CN_TEXT] ${getTempModeStr(oldAuto, oldRange)}->${getTempModeStr(newAuto, newRange)}")
            param.byTemperatureRangeAutoChangedEnabled = newAuto
            param.byTemperatureRange = newRange
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setTemperatureMode() SettingsTemperature measurement[CN_TEXT] $it")
                }
            } else {
                XLog.e("$TAG setTemperatureMode() SettingsTemperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setTemperatureMode() [CN_TEXT]Temperature measurement[CN_TEXT]！[CN_TEXT]：${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    private fun getTempModeStr(autoEnable: Byte, range: Byte): String {
        if (autoEnable == 1.toByte()) {
            return "[CN_TEXT]"
        }
        return if (range == 2.toByte()) "Normal temperature[CN_TEXT]" else "High temperature[CN_TEXT]"
    }







    private fun getCommandState(userId: Int): Int {
        val state = USB_COMMAND_STATE()
        JavaInterface.getInstance().USB_GetCommandState(userId, state)
        return state.byState.toInt() and 0xff
    }

    /**
     * [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT]State[CN_TEXT]
     */
    private suspend inline fun checkCommandState(userId: Int, callback: (state: String) -> Unit) {
        var commandState = 1 //1[CN_TEXT] [CN_TEXT]
        while (commandState == 1) {
            commandState = getCommandState(userId)
            callback.invoke("Command State: ${getCommandState(userId).toComStateStr()}")
            if (commandState == 1) {
                //[CN_TEXT]、Settings[CN_TEXT]Temperature measurement[CN_TEXT] 1000 [CN_TEXT]，[CN_TEXT] 10 [CN_TEXT]
                delay(100)
            }
        }
    }

    private fun Int.toComStateStr(): String = when (this) {
        0x00 -> "0x00: [CN_TEXT]"
        0x01 -> "0x01: [CN_TEXT]"
        0x02 -> "0x02: [CN_TEXT]State"
        0x03 -> "0x03: [CN_TEXT]Mode[CN_TEXT]"
        0x04 -> "0x04: SET_CUR [CN_TEXT]Settings[CN_TEXT]"
        0x05 -> "0x05: [CN_TEXT] Unint ID"
        0x06 -> "0x06: [CN_TEXT] CS ID"
        0x07 -> "0x07: [CN_TEXT]Type"
        0x08 -> "0x08: SET_CUR [CN_TEXT]Settings[CN_TEXT]，[CN_TEXT]Settings[CN_TEXT]"
        0x09 -> "0x09: [CN_TEXT]"
        0x0a -> "0x0a: [CN_TEXT]"
        0x0b -> "0x0b: [CN_TEXT]"
        0x0c -> "0x0c: [CN_TEXT]"
        0xff -> "0xff: [CN_TEXT]"
        else -> "0x${this.toString(16)}: [CN_TEXT]State[CN_TEXT]"
    }
}