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
     * [CN_TEXT]
     */
    fun setAutoShutter(isAutoShutter : Boolean){
        val basicAutoFFCStatusSet: IrcmdError? =
            DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
                ?.basicAutoFFCStatusSet(if (isAutoShutter) CommonParams.AutoFFCStatus.AUTO_FFC_ENABLE
                else CommonParams.AutoFFCStatus.AUTO_FFC_DISABLED)
        Log.d(TAG,
            "basicAutoFFCStatusSet=$basicAutoFFCStatusSet"
        )
    }

    /**
     * [CN_TEXT]
     */
    fun setOneShutter(){
        val basicFFCUpdate = DeviceIrcmdControlManager.getInstance().ircmdEngine?.basicFFCUpdate()
        Log.d(TAG,
            "basicFFCUpdate=$basicFFCUpdate"
        )
    }

    /**
     *
     *
     * Normal temperature ([CameraItemBean.TYPE_TMP_C] = 1）[CN_TEXT]High gain
     *
     * High temperature ([CameraItemBean.TYPE_TMP_H] = 0) [CN_TEXT]Low gain
     *
     * [CN_TEXT] ([CameraItemBean.TYPE_TMP_ZD] = -1)
     */
    fun basicGainSet(gainType : Int){
        if (gainType == CameraItemBean.TYPE_TMP_ZD){
            CameraPreviewManager.getInstance().setAutoSwitchGainEnable(true)
        }else if (gainType == CameraItemBean.TYPE_TMP_C){
            CameraPreviewManager.getInstance().setAutoSwitchGainEnable(false)
            val basicGainSet = DeviceIrcmdControlManager.getInstance().ircmdEngine
                ?.basicGainSet(CommonParams.GainStatus.HIGH_GAIN)
            Log.d(TAG, "basicGainSet=$basicGainSet--$gainType")
        }else if (gainType == CameraItemBean.TYPE_TMP_H){
            CameraPreviewManager.getInstance().setAutoSwitchGainEnable(false)
            val basicGainSet = DeviceIrcmdControlManager.getInstance().ircmdEngine
                ?.basicGainSet(CommonParams.GainStatus.LOW_GAIN)
            Log.d(TAG, "basicGainSet=$basicGainSet--$gainType")
        }
    }

    /**
     * [CN_TEXT]：[CN_TEXT]0-100
     */
    fun basicGlobalContrastLevelSet(levelValue : Int){
        val basicGlobalContrastLevelSetResult = DeviceIrcmdControlManager.getInstance().ircmdEngine
            ?.basicGlobalContrastLevelSet(levelValue)
        Log.d(TAG,
            "basicGlobalContrastLevelSet=$basicGlobalContrastLevelSetResult"
        )
    }
    /**
     * [CN_TEXT]：[CN_TEXT]0-100，[CN_TEXT]
     */
    fun basicImageDetailEnhanceLevelSet(levelValue : Int){
//        val professionModeSetResult = DeviceIrcmdControlManager.getInstance().ircmdEngine
//            .advProfessionModeSet(CommonParams.ProfessionMode.valueOf(0))
//        val basicImageDetailEnhanceLevelSetResult = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
//            ?.basicImageDetailEnhanceLevelSet(levelValue);
//        Log.d(TAG, "basicImageDetailEnhanceLevelSet=" + basicImageDetailEnhanceLevelSetResult)
    }

    /**
     * Settings[CN_TEXT]
     */
    fun basicMirrorAndFlipStatusSet(openMirror : Boolean){
        //Settings[CN_TEXT] PASS
        val basicMirrorAndFlipStatusSet = DeviceIrcmdControlManager.getInstance().ircmdEngine
            ?.basicMirrorAndFlipStatusSet(if (openMirror) CommonParams.MirrorFlipType.ONLY_FLIP else
                CommonParams.MirrorFlipType.NO_MIRROR_OR_FLIP)
        Log.d(TAG, "basicGlobalContrastLevelSet=$basicMirrorAndFlipStatusSet")
    }

    /**
     * [CN_TEXT]
     * https://alidocs.dingtalk.com/i/p/QqWXwywDMb9xKG31/docs/14lgGw3P8vL0P2qbu7OR39d5V5daZ90D
     * Setp1：[CN_TEXT]Current[CN_TEXT]State，[CN_TEXT]3-5[CN_TEXT]。
     * [CN_TEXT]，[CN_TEXT]，[CN_TEXT]。[CN_TEXT]、 [CN_TEXT])；
     * Setp2：[CN_TEXT]，[CN_TEXT]
     * Setp3：[CN_TEXT]
     * Setp4：[CN_TEXT]
     * Setp5：[CN_TEXT]
     * Setp6：[CN_TEXT]
     * Setp7：[CN_TEXT]，[CN_TEXT]，[CN_TEXT]
     * mIrcmdEngine.advRmcoverCaliCancel();
     * [CN_TEXT]，[CN_TEXT]，[CN_TEXT]
     * mIrcmdEngine.basicSaveData(CommonParams.DeviceDataSaveType.BASIC_SAVE_RMCOVER_DATA);
     */
    fun onceAuto() : Boolean{
        //Setp2
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.basicRestoreDefaultData(CommonParams.DeviceRestoreTypeType.BASIC_RESTROE_RMCOVER_DATA)
        //Setp3
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.basicAutoFFCStatusSet(CommonParams.AutoFFCStatus.AUTO_FFC_DISABLED)
        //Setp4
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()?.basicFFCUpdate()
        //Setp5
        val result = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()?.advAutoRmcoverCali()
        Log.d(TAG, "advAutoRmcoverCali=${result}")
        //Setp6
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.basicAutoFFCStatusSet(CommonParams.AutoFFCStatus.AUTO_FFC_ENABLE)
        //Setp7
        val ircmdError = DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.basicSaveData(CommonParams.DeviceDataSaveType.BASIC_SAVE_RMCOVER_DATA)
        return ircmdError == IrcmdError.IRCMD_SUCCESS
    }


    /**
     * [CN_TEXT]Low gainMode[CN_TEXT]，[CN_TEXT]
     */
    suspend fun autoStart() : Boolean{
        basicGainSet(CameraItemBean.TYPE_TMP_C)
        delay(2000)
        XLog.d(TAG, "onceAuto=start")
        if (!onceAuto()){
            return false
        }
        XLog.d(TAG, "basicGainSet=start")
        basicGainSet(CameraItemBean.TYPE_TMP_H)
        delay(2000)
        XLog.d(TAG, "onceAuto=start")
        return onceAuto()
    }


    /**
     * [CN_TEXT]Core[CN_TEXT]
     */
    fun advEnvCorrectSwitchSet(open : Boolean){
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.advEnvCorrectSwitchSet(if (open) CommonParams.BasicEnableStatus.BASIC_ENABLE
            else CommonParams.BasicEnableStatus.BASIC_DISABLE)
    }

    /**
     * Core[CN_TEXT]
     * [CN_TEXT]：range:1~16384
     */
    fun advEnvCorrectEMSSet(value : Int){
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            .advEnvCorrectEMSSet(value);
    }

    /**
     * Core[CN_TEXT]
     * [CN_TEXT](units:Celsius)：range:233~373
     */
    fun advEnvCorrectTUSet(value : Int){
        DeviceIrcmdControlManager.getInstance().getIrcmdEngine()
            ?.advEnvCorrectTUSet(value);
    }


    /**
     * lite[CN_TEXT]
     * @param temp Float
     * @param params_array FloatArray
     * @param tau_data_H ByteArray High gain[CN_TEXT]
     * @param tau_data_L ByteArray Low gain[CN_TEXT]
     * @return Float
     */
    fun temperatureCorrection(temp : Float, params_array: FloatArray, tau_data_H: ByteArray, tau_data_L: ByteArray,basicGainGetValue : Int) : Float {
        var newTemp = temp
        //[CN_TEXT]State PASS
        try {
            if (tau_data_H == null || tau_data_L == null) return temp
            newTemp = LibIRTempAC020.temperatureCorrection(
                params_array[0],
                tau_data_H,
                tau_data_L,
                params_array[1],
                params_array[2],
                params_array[3],
                params_array[4],
                params_array[5],
                if (basicGainGetValue == 0) GainStatus.LOW_GAIN else GainStatus.HIGH_GAIN
            )
        }catch (e : Exception){
            XLog.e("$TAG:temperatureCorrection-${e.message}")
        }finally {
            return newTemp
        }
    }

    /**
     * Settings[CN_TEXT]Mode[CN_TEXT]
     */
    fun setMode(){
//        val professionModeSetResult = DeviceIrcmdControlManager.getInstance().ircmdEngine
//            .advProfessionModeSet(CommonParams.ProfessionMode.valueOf(0))
//        val ircmdError = DeviceIrcmdControlManager.getInstance().ircmdEngine
//            .basicImageSceneModeSet(3)
//        Log.d(TAG, "setModel=${ircmdError}")
    }



}