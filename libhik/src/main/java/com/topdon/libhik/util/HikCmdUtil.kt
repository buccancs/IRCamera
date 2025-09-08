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
 * [Chinese text].
 *
 * [Chinese text] SDK [Chinese text] API [Chinese text], [Chinese text]point, 
 * [Chinese text]. 
 *
 * Created by LCG on 2024/11/19.
 */
object HikCmdUtil {
    /**
     * onlyfor[Chinese text]
     */
    private const val TAG = "HikCmd"

    /**
     * [Chinese text].
     */
    fun init(): Boolean = if (JavaInterface.getInstance().USB_Init()) {
        XLog.i("$TAG init() [Chinese text]")
        true
    } else {
        XLog.e("$TAG init() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        false
    }

    /**
     * Settings[Chinese text], [Chinese text], [Chinese text].
     */
    fun setLogPath(logFile: File?) {
        if (logFile != null) {
            JavaInterface.getInstance().USB_SetLogToFile(JavaInterface.INFO_LEVEL, logFile.absolutePath, 1)
        }
    }

    /**
     * [Chinese text] userId.
     * @return [Chinese text] [JavaInterface.USB_INVALID_USER_ID]
     */
    fun login(context: Context): Int {
        // [Chinese text]
        val deviceCount: Int = JavaInterface.getInstance().USB_GetDeviceCount(context)
        if (deviceCount < 0) {
            XLog.e("$TAG login() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_USER_ID
        }
        if (deviceCount == 0) {
            XLog.e("$TAG login() target[Chinese text]! ")
            return JavaInterface.USB_INVALID_USER_ID
        }

        // [Chinese text]message
        val deviceInfoArray: Array<USB_DEVICE_INFO> = Array(JavaInterface.MAX_DEVICE_NUM) {
            USB_DEVICE_INFO()
        }
        if (!JavaInterface.getInstance().USB_EnumDevices(deviceCount, deviceInfoArray)) {
            XLog.e("$TAG login() [Chinese text]message[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_USER_ID
        }

        val loginInfo = USB_USER_LOGIN_INFO()
        loginInfo.dwTimeout = 5000 // [Chinese text]
        loginInfo.dwDevIndex = deviceInfoArray[0].dwIndex
        loginInfo.dwVID = deviceInfoArray[0].dwVID
        loginInfo.dwPID = deviceInfoArray[0].dwPID
        loginInfo.dwFd = deviceInfoArray[0].dwFd
        val userId: Int = JavaInterface.getInstance().USB_Login(loginInfo, USB_DEVICE_REG_RES())
        if (userId == JavaInterface.USB_INVALID_USER_ID) {
            XLog.e("$TAG login() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        } else {
            XLog.i("$TAG login() [Chinese text]")
        }
        return userId
    }

    /**
     * [Chinese text]: [Chinese text], [Chinese text]Pseudo color[Chinese text], [Chinese text]temperature[Chinese text], [Chinese text], [Chinese text].
     */
    suspend fun initConfig(userId: Int) {
        // [Chinese text], [Chinese text]Pseudo color[Chinese text]
        val imageParam = USB_IMAGE_ENHANCEMENT()
        if (JavaInterface.getInstance().USB_GetImageEnhancement(userId, imageParam)) {
            if (imageParam.byLSEDetailEnabled != 1.toByte() || imageParam.byPaletteMode != 1.toByte()) {
                XLog.i("$TAG initConfig() [Chinese text], [Chinese text]Pseudo color([Chinese text])")
                // 1-[Chinese text] 2-[Chinese text] 10-[Chinese text]1 11-[Chinese text] 12-[Chinese text]2 13-[Chinese text]1 14-[Chinese text]2 15-[Chinese text]
                // 16-[Chinese text]1 17-[Chinese text]2 18-[Chinese text] 19-[Chinese text] 20-[Chinese text] 21-[Chinese text] 22-[Chinese text]
                imageParam.byPaletteMode = 1
                imageParam.byLSEDetailEnabled = 1 // 0-[Chinese text] 1-[Chinese text]
                if (JavaInterface.getInstance().USB_SetImageEnhancement(userId, imageParam)) {
                    checkCommandState(userId) {
                        XLog.v("$TAG initConfig() Settings[Chinese text] $it")
                    }
                } else {
                    XLog.e("$TAG initConfig() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
            XLog.e("$TAG initConfig() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }

        // [Chinese text]temperature[Chinese text]
        val basicTempParam = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, basicTempParam)) {
            if (basicTempParam.byThermometryStreamOverlay != 1.toByte()) {
                XLog.i("$TAG initConfig() Settings[Chinese text]temperature[Chinese text]")
                basicTempParam.byThermometryStreamOverlay = 1 // 2-[Chinese text] 1-[Chinese text]
                if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, basicTempParam)) {
                    checkCommandState(userId) {
                        XLog.v("$TAG initConfig() Settings[Chinese text] $it")
                    }
                } else {
                    XLog.e("$TAG initConfig() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
            XLog.e("$TAG initConfig() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * [Chinese text]
     */
    fun shutter(userId: Int) {
        JavaInterface.getInstance().USB_SetImageManualCorrect(userId)
    }

    /**
     * [Chinese text]
     */
    suspend fun setAutoShutter(userId: Int, isAutoShutter: Boolean) {
        XLog.i("$TAG setAutoShutter() ${if (isAutoShutter) "[Chinese text]" else "[Chinese text]"} [Chinese text]")
        val param = USB_SYSTEM_SERIAL_DATA_TRANSMISSION()
        param.byMode = 2 // 0-[Chinese text] 1-[Chinese text] 2-[Chinese text]
        param.wDeviceCMDFlag = 0
        param.dwDeviceCMD = 0x2001 // [Chinese text] code
        param.dwValue = if (isAutoShutter) 1 else 0
        if (JavaInterface.getInstance().USB_SetSystemSerialData(userId, param)) {
            checkCommandState(userId) {
                XLog.v("$TAG setAutoShutter() [Chinese text] $it")
            }
        } else {
            XLog.e("$TAG setAutoShutter() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * start[Chinese text]
     */
    suspend fun startCorrect(userId: Int) {
        XLog.i("$TAG startCorrect() start[Chinese text]")
        val param = USB_SYSTEM_SERIAL_DATA_TRANSMISSION()
        param.byMode = 2 // 0-[Chinese text] 1-[Chinese text] 2-[Chinese text]
        param.wDeviceCMDFlag = 0
        param.dwDeviceCMD = 0xf026 // [Chinese text] code
        param.dwValue = 2
        if (JavaInterface.getInstance().USB_SetSystemSerialData(userId, param)) {
            checkCommandState(userId) {
                XLog.v("$TAG startCorrect() [Chinese text] $it")
            }
        } else {
            XLog.e("$TAG startCorrect() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * Settings[Chinese text].
     * @param contrast [Chinese text]range `[0,100]`
     */
    suspend fun setContrast(userId: Int, contrast: Int) {
        XLog.i("$TAG setContrast($contrast) Settings[Chinese text]")
        val param = USB_IMAGE_CONTRAST()
        param.dwContrast = contrast
        if (JavaInterface.getInstance().USB_SetImageContrast(userId, param)) {
            checkCommandState(userId) {
                XLog.v("$TAG setContrast() Settings[Chinese text] $it")
            }
        } else {
            XLog.e("$TAG setContrast() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /* ******************************  [Chinese text] ImageEnhancement  ****************************** */
    /**
     * Settings[Chinese text]([Chinese text])[Chinese text]
     */
    suspend fun setEnhanceLevel(userId: Int, level: Int) {
        val param = USB_IMAGE_ENHANCEMENT()
        if (JavaInterface.getInstance().USB_GetImageEnhancement(userId, param)) {
            if (param.dwLSEDetailLevel == level) {
                return
            }
            XLog.i("$TAG setEnhanceLevel() [Chinese text] ${param.dwLSEDetailLevel}->$level")
            param.dwLSEDetailLevel = level
            if (JavaInterface.getInstance().USB_SetImageEnhancement(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setEnhanceLevel() Settings[Chinese text] $it")
                }
            } else {
                XLog.e("$TAG setEnhanceLevel() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setEnhanceLevel() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /* ******************************  [Chinese text] ImageVideoAdjust  ****************************** */
    /**
     * Settings[Chinese text].
     * @param isMirror true-left-right[Chinese text] false-[Chinese text]
     */
    suspend fun setMirror(userId: Int, rotateAngle: Int, isMirror: Boolean) {
        val videoAdjust = USB_IMAGE_VIDEO_ADJUST()
        if (JavaInterface.getInstance().USB_GetImageVideoAdjust(userId, videoAdjust)) {
            val mirrorCode: Byte = if (isMirror) (if (rotateAngle == 0 || rotateAngle == 180) 2 else 3) else 0
            if (videoAdjust.byImageFlipStyle == mirrorCode) {
                return
            }
            XLog.i("$TAG setMirror() [Chinese text] ${videoAdjust.byImageFlipStyle}->$mirrorCode")
            videoAdjust.byImageFlipStyle = mirrorCode
            if (JavaInterface.getInstance().USB_SetImageVideoAdjust(userId, videoAdjust)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setMirror() Settings[Chinese text] $it")
                }
            } else {
                XLog.e("$TAG setMirror() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setMirror() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /* ******************************  [Chinese text]  ****************************** */
    /**
     * [Chinese text].
     * @return [Chinese text] Id, [Chinese text] [JavaInterface.USB_INVALID_CHANNEL]
     */
    suspend fun startStream(userId: Int, callback: FStreamCallBack): Int {
        // Settings[Chinese text], [Chinese text]
        val videoAdjust = USB_IMAGE_VIDEO_ADJUST()
        if (JavaInterface.getInstance().USB_GetImageVideoAdjust(userId, videoAdjust)) {
            val isRotateChange = videoAdjust.byCorridor != 0.toByte()
            val isMirrorChange = videoAdjust.byImageFlipStyle != 0.toByte()
            if (isRotateChange || isMirrorChange) {
                XLog.i("$TAG startStream() Settings[Chinese text]: [Chinese text], [Chinese text]")
                videoAdjust.byCorridor = 0 // 0-256x192 1-192x256
                videoAdjust.byImageFlipStyle = 0 // [Chinese text] 0-[Chinese text] 1-in progress[Chinese text] 2-left-right 3-[Chinese text]
                if (JavaInterface.getInstance().USB_SetImageVideoAdjust(userId, videoAdjust)) {
                    checkCommandState(userId) {
                        XLog.v("$TAG startStream() Settings[Chinese text] $it")
                    }
                } else {
                    XLog.e("$TAG startStream() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
                }
            }
        } else {
            XLog.e("$TAG startStream() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }

        // Settings[Chinese text]
        // [Chinese text] 256x392 [Chinese text] 192x520 [Chinese text]high[Chinese text](192+4)*2 [Chinese text] (256+4)*2
        // [Chinese text]implement, [Chinese text] 256x192 [Chinese text]
        XLog.i("$TAG startStream() Settings[Chinese text]: 256x392")
        val videoParam = USB_VIDEO_PARAM()
        videoParam.dwVideoFormat = JavaInterface.USB_STREAM_YUY2
        videoParam.dwWidth = 256
        videoParam.dwHeight = 392
        videoParam.dwFramerate = 25 // 25[Chinese text]
        if (JavaInterface.getInstance().USB_SetVideoParam(userId, videoParam)) {
            checkCommandState(userId) {
                XLog.v("$TAG startStream() Settings[Chinese text] $it")
            }
        } else {
            XLog.w("$TAG startStream() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        }

        // [Chinese text]
        XLog.i("$TAG startStream() [Chinese text]")
        val callbackParam = USB_STREAM_CALLBACK_PARAM()
        callbackParam.dwStreamType = JavaInterface.USB_STREAM_YUY2
        callbackParam.fnStreamCallBack = callback
        val callbackId: Int = JavaInterface.getInstance().USB_StartStreamCallback(userId, callbackParam)
        if (callbackId == JavaInterface.USB_INVALID_CHANNEL) {
            XLog.w("$TAG startStream() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        } else {
            XLog.v("$TAG startStream() [Chinese text]")
        }

        // Settings[Chinese text]
        XLog.i("$TAG startStream() Settings[Chinese text]: 8")
        val streamParam = USB_THERMAL_STREAM_PARAM()
        streamParam.byVideoCodingType = 8
        if (JavaInterface.getInstance().USB_SetThermalStreamParam(userId, streamParam)) {
            checkCommandState(userId) {
                XLog.v("$TAG startStream() Settings[Chinese text] $it")
            }
        } else {
            XLog.w("$TAG startStream() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            return JavaInterface.USB_INVALID_CHANNEL
        }

        return callbackId
    }
    /**
     * [Chinese text].
     */
    fun removeStreamCallback(userId: Int, callbackId: Int) {
        if (callbackId != JavaInterface.USB_INVALID_CHANNEL) {
            if (!JavaInterface.getInstance().USB_StopChannel(userId, callbackId)) {
                XLog.w("$TAG removeStreamCallback() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            }
        }
    }

    /* ******************************  [Chinese text] ThermometryBasicParam  ****************************** */
    /**
     * Settings[Chinese text]
     * @param emissivity [Chinese text]range `[1, 100]`
     */
    suspend fun setEmissivity(userId: Int, emissivity: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            if (param.dwEmissivity == emissivity) {
                return
            }
            XLog.i("$TAG setEmissivity() [Chinese text] ${param.dwEmissivity}->$emissivity")
            param.dwEmissivity = emissivity
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setEmissivity() Settings[Chinese text] $it")
                }
            } else {
                XLog.e("$TAG setEmissivity() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setEmissivity() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * Settings[Chinese text], [Chinese text] cm
     * @param distance [Chinese text]range `[30, 200]`
     */
    suspend fun setDistance(userId: Int, distance: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            if (param.dwDistance == distance) {
                return
            }
            XLog.i("$TAG setDistance() [Chinese text] ${param.dwDistance}cm->${distance}cm")
            param.dwDistance = distance
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setDistance() Settings[Chinese text] $it")
                }
            } else {
                XLog.e("$TAG setDistance() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setDistance() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    /**
     * Settings[Chinese text] [Chinese text](-1)/[Chinese text](1)/high[Chinese text](0) level.
     */
    suspend fun setTemperatureMode(userId: Int, mode: Int) {
        val param = USB_THERMOMETRY_BASIC_PARAM()
        if (JavaInterface.getInstance().USB_GetThermometryBasicParam(userId, param)) {
            // [Chinese text]: [Chinese text] > [Chinese text]range
            val oldAuto: Byte = param.byTemperatureRangeAutoChangedEnabled
            val oldRange: Byte = param.byTemperatureRange
            val newAuto: Byte = if (mode == -1) 1 else 0 // 0-[Chinese text] 1-[Chinese text]
            val newRange: Byte = if (mode == 1) 2 else 3 // 2(-20~150[Chinese text]) 3(100~550high[Chinese text])
            if (oldAuto == newAuto && (oldRange == newRange || newRange == 1.toByte())) {
                return
            }
            XLog.i("$TAG setTemperatureMode() [Chinese text]level ${getTempModeStr(oldAuto, oldRange)}->${getTempModeStr(newAuto, newRange)}")
            param.byTemperatureRangeAutoChangedEnabled = newAuto
            param.byTemperatureRange = newRange
            if (JavaInterface.getInstance().USB_SetThermometryBasicParam(userId, param)) {
                checkCommandState(userId) {
                    XLog.v("$TAG setTemperatureMode() Settings[Chinese text] $it")
                }
            } else {
                XLog.e("$TAG setTemperatureMode() Settings[Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
            }
        } else {
            XLog.e("$TAG setTemperatureMode() [Chinese text]! [Chinese text]: ${JavaInterface.getInstance().USB_GetLastError()}")
        }
    }

    private fun getTempModeStr(autoEnable: Byte, range: Byte): String {
        if (autoEnable == 1.toByte()) {
            return "[Chinese text]"
        }
        return if (range == 2.toByte()) "[Chinese text]" else "high[Chinese text]"
    }

    private fun getCommandState(userId: Int): Int {
        val state = USB_COMMAND_STATE()
        JavaInterface.getInstance().USB_GetCommandState(userId, state)
        return state.byState.toInt() and 0xff
    }

    /**
     * [Chinese text]Settings[Chinese text], [Chinese text]
     */
    private suspend inline fun checkCommandState(userId: Int, callback: (state: String) -> Unit) {
        var commandState = 1 // 1[Chinese text] [Chinese text]
        while (commandState == 1) {
            commandState = getCommandState(userId)
            callback.invoke("Command State: ${getCommandState(userId).toComStateStr()}")
            if (commandState == 1) {
                // [Chinese text], Settings[Chinese text] 1000 [Chinese text], [Chinese text]low[Chinese text] 10 [Chinese text]
                delay(100)
            }
        }
    }

    private fun Int.toComStateStr(): String = when (this) {
        0x00 -> "0x00: [Chinese text]"
        0x01 -> "0x01: [Chinese text]"
        0x02 -> "0x02: [Chinese text]not allowed[Chinese text]"
        0x03 -> "0x03: [Chinese text]mode[Chinese text]"
        0x04 -> "0x04: SET_CUR [Chinese text]Settings[Chinese text]range"
        0x05 -> "0x05: [Chinese text] Unint ID"
        0x06 -> "0x06: [Chinese text] CS ID"
        0x07 -> "0x07: [Chinese text]"
        0x08 -> "0x08: SET_CUR [Chinese text]Settings[Chinese text]range, [Chinese text]Settings[Chinese text]"
        0x09 -> "0x09: [Chinese text]"
        0x0a -> "0x0a: [Chinese text]"
        0x0b -> "0x0b: [Chinese text]"
        0x0c -> "0x0c: [Chinese text]"
        0xff -> "0xff: [Chinese text]"
        else -> "0x${this.toString(16)}: [Chinese text]"
    }
}