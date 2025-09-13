package com.example.thermal_lite.util

import android.util.Log
import com.elvishew.xlog.XLog
import com.energy.ac020library.bean.CommonParams
import com.energy.ac020library.bean.IrcmdError
import com.energy.irutilslibrary.LibIRTempAC020
import com.energy.irutilslibrary.bean.GainStatus
import com.example.thermal_lite.camera.CameraPreviewManager
import com.example.thermal_lite.camera.DeviceIrcmdControlManager
import com.topdon.lib.core.bean.CameraItemBean
import kotlinx.coroutines.delay

/**
 * des:
 * author: CaiSongL
 * date: 2024/8/2 16:43
 **/
object IRTool {
    const val TAG: String = "IRTool"

    /**
\1
     */
    fun setAutoShutter(isAutoShutter: Boolean)  {
        val basicAutoFFCStatusSet: IrcmdError? =
            DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                ?.basicAutoFFCStatusSet(
                    if (isAutoShutter) {
                        CommonParams.AutoFFCStatus.AUTO_FFC_ENABLE
                    } else {
                        CommonParams.AutoFFCStatus.AUTO_FFC_DISABLED
                    },
                )
        Log.d(
            TAG,
            "basicAutoFFCStatusSet=$basicAutoFFCStatusSet",
        )
    }

    /**
\1
     */
    fun setOneShutter()  {
        val basicFFCUpdate = DeviceIrcmdControlManager.getInstance().ircmdEngine?.basicFFCUpdate()
        Log.d(
            TAG,
            "basicFFCUpdate=$basicFFCUpdate",
        )
    }

    /**
     *
     *
\1 ([CameraItemBean.TYPE_TMP_C] = 1）gain
     *
\1high temperature ([CameraItemBean.TYPE_TMP_H] = 0) gain
     *
\1 ([CameraItemBean.TYPE_TMP_ZD] = -1)
     */
    fun basicGainSet(gainType: Int)  {
        if (gainType == CameraItemBean.TYPE_TMP_ZD)
            {
                CameraPreviewManager.getInstance().setAutoSwitchGainEnable(true)
            } else if (gainType == CameraItemBean.TYPE_TMP_C)
            {
                CameraPreviewManager.getInstance().setAutoSwitchGainEnable(false)
                val basicGainSet =
                    DeviceIrcmdControlManager.getInstance().ircmdEngine
                        ?.basicGainSet(CommonParams.GainStatus.HIGH_GAIN)
                Log.d(TAG, "basicGainSet=$basicGainSet--$gainType")
            } else if (gainType == CameraItemBean.TYPE_TMP_H)
            {
                CameraPreviewManager.getInstance().setAutoSwitchGainEnable(false)
                val basicGainSet =
                    DeviceIrcmdControlManager.getInstance().ircmdEngine
                        ?.basicGainSet(CommonParams.GainStatus.LOW_GAIN)
                Log.d(TAG, "basicGainSet=$basicGainSet--$gainType")
            }
    }

    /**
\1：parameter0-100
     */
    fun basicGlobalContrastLevelSet(levelValue: Int)  {
        val basicGlobalContrastLevelSetResult =
            DeviceIrcmdControlManager.getInstance().ircmdEngine
                ?.basicGlobalContrastLevelSet(levelValue)
        Log.d(
            TAG,
            "basicGlobalContrastLevelSet=$basicGlobalContrastLevelSetResult",
        )
    }

    /**
\1：parameter0-100，
     */
    fun basicImageDetailEnhanceLevelSet(levelValue: Int)  {
//        val professionModeSetResult = DeviceIrcmdControlManager.getInstance().ircmdEngine
//            .advProfessionModeSet(CommonParams.ProfessionMode.valueOf(0))
//        val basicImageDetailEnhanceLevelSetResult = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
//            ?.basicImageDetailEnhanceLevelSet(levelValue);
//        Log.d(TAG, "basicImageDetailEnhanceLevelSet="Test Data"basicGlobalContrastLevelSet=$basicMirrorAndFlipStatusSet")
    }

    /**
\1calibration
     * https://alidocs.dingtalk.com/i/p/QqWXwywDMb9xKG31/docs/14lgGw3P8vL0P2qbu7OR39d5V5daZ90D
\1Setp1：，3-5。
\1，calibration，。、 )；
\1Setp2：calibrationdata，calibration
\1Setp3：disabled
\1Setp4：
\1Setp5：calibration
\1Setp6：
\1Setp7：calibration，calibration，
     * mIrcmdEngine.advRmcoverCaliCancel();
\1calibration，savecalibrationdata，
     * mIrcmdEngine.basicSaveData(CommonParams.DeviceDataSaveType.BASIC_SAVE_RMCOVER_DATA);
     */
    fun onceAuto(): Boolean  {
        // Setp2
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.basicRestoreDefaultData(CommonParams.DeviceRestoreTypeType.BASIC_RESTROE_RMCOVER_DATA)
        // Setp3
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.basicAutoFFCStatusSet(CommonParams.AutoFFCStatus.AUTO_FFC_DISABLED)
        // Setp4
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()?.basicFFCUpdate()
        // Setp5
        val result = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()?.advAutoRmcoverCali()
        Log.d(TAG, "advAutoRmcoverCali=$result")
        // Setp6
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.basicAutoFFCStatusSet(CommonParams.AutoFFCStatus.AUTO_FFC_ENABLE)
        // Setp7
        val ircmdError =
            DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                ?.basicSaveData(CommonParams.DeviceDataSaveType.BASIC_SAVE_RMCOVER_DATA)
        return ircmdError == IrcmdError.IRCMD_SUCCESS
    }

    /**
\1high/low gaincalibration，calibration
     */
    suspend fun autoStart(): Boolean  {
        basicGainSet(CameraItemBean.TYPE_TMP_C)
        delay(2000)
        XLog.d(TAG, "onceAuto=start")
        if (!onceAuto())
            {
                return false
            }
        XLog.d(TAG, "basicGainSet=start")
        basicGainSet(CameraItemBean.TYPE_TMP_H)
        delay(2000)
        XLog.d(TAG, "onceAuto=start"Test Data"$TAG:temperatureCorrection-${e.message}")
        } finally {
            return newTemp
        }
    }

    /**
\1set
     */
    fun setMode()  {
//        val professionModeSetResult = DeviceIrcmdControlManager.getInstance().ircmdEngine
//            .advProfessionModeSet(CommonParams.ProfessionMode.valueOf(0))
//        val ircmdError = DeviceIrcmdControlManager.getInstance().ircmdEngine
//            .basicImageSceneModeSet(3)
//        Log.d(TAG, "setModel=${ircmdError}")
    }
}
