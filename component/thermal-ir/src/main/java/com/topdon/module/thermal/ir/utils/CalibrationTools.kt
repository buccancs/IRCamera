package com.topdon.module.thermal.ir.utils

import android.util.Log
import com.elvishew.xlog.XLog
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.SynchronizedBitmap

/**
 * [CN_TEXT]
 */
object CalibrationTools {

    /**
     * [CN_TEXT]
     * [CN_TEXT] - Settings[CN_TEXT]
     */
    fun sign(irCmd: IRCMD, singlePointTemp: Int): Boolean {
        var success = false
        // [CN_TEXT]Temperature measurement[CN_TEXT],[CN_TEXT]
        if (irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD) == 0) {
            irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD)
            val result = irCmd.setTPDKtBtRecalPoint(CommonParams.TPDKtBtRecalPointType.RECAL_1_POINT, singlePointTemp)
            if (result == 0) {
                success = true
            } else {
                XLog.w("[CN_TEXT]")
            }
        } else {
            XLog.w("[CN_TEXT]")
        }
        return success
    }

    /**
     * [CN_TEXT]
     * Low temperature(100 ~ 400)
     */
    fun pointFirst(irCmd: IRCMD, pointTemp: Int): Boolean {
        var success = false
        // [CN_TEXT]Temperature measurement[CN_TEXT],[CN_TEXT]
        if (irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD) == 0) {
            val result = irCmd.setTPDKtBtRecalPoint(CommonParams.TPDKtBtRecalPointType.RECAL_2_POINT_FIRST, pointTemp + 273)
            if (result == 0) {
                success = true
            } else {
                XLog.w("Low temperature[CN_TEXT]")
            }
        } else {
            XLog.w("Low temperature[CN_TEXT]")
        }
        return success
    }

    /**
     * [CN_TEXT]
     * High temperature(20 ~ 100)
     *
     * [CN_TEXT]Low temperature[CN_TEXT]High temperature
     */
    fun pointEnd(irCmd: IRCMD, pointTemp: Int): Boolean {
        var success = false
        // [CN_TEXT]Temperature measurement[CN_TEXT],[CN_TEXT]
        if (irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD) == 0) {
            val result = irCmd.setTPDKtBtRecalPoint(CommonParams.TPDKtBtRecalPointType.RECAL_2_POINT_END, pointTemp + 273)
            if (result == 0) {
                success = true
            } else {
                Log.w("123", "[CN_TEXT]")
            }
        } else {
            Log.w("123", "[CN_TEXT]")
        }
        return success
    }

    /**
     * [CN_TEXT] - [CN_TEXT]
     *
     */
    fun potReady(irCmd: IRCMD): Boolean {
        return irCmd.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_DIS) == 0 //[CN_TEXT]
    }

    /**
     * [CN_TEXT] - [CN_TEXT]
     *
     * @param gainType [CN_TEXT]GAIN_1
     * CommonParams.RMCoverAutoCalcType.GAIN_1
     * CommonParams.RMCoverAutoCalcType.GAIN_2
     * CommonParams.RMCoverAutoCalcType.GAIN_4
     */
    fun potStart(irCmd: IRCMD, type: Int) {
        val gainType = when (type) {
            1 -> CommonParams.RMCoverAutoCalcType.GAIN_1
            2 -> CommonParams.RMCoverAutoCalcType.GAIN_2
            4 -> CommonParams.RMCoverAutoCalcType.GAIN_4
            else -> CommonParams.RMCoverAutoCalcType.GAIN_1
        }
        irCmd.rmCoverAutoCalc(gainType) //[CN_TEXT]
        irCmd.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_EN) //[CN_TEXT]
    }

    /**
     * [CN_TEXT]
     */
    fun cancelCalibration(irCmd: IRCMD) {
        irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD)
    }

    /**
     * [CN_TEXT]
     */
    fun reset(irCmd: IRCMD) {
        irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_ALL)
    }

    /**
     * [CN_TEXT]Mode
     * @return true: High gain    false: Low gain
     */
    fun queryGain(irCmd: IRCMD): Boolean {
        val value = IntArray(1)
        irCmd.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
        return value[0] == 1
    }

    /**
     * Settings[CN_TEXT]Mode
     * @param type 1: [CN_TEXT]    0: [CN_TEXT]
     *
     */
    fun setGain(irCmd: IRCMD, type: Int) {
        if (type == 1) {
            irCmd.setPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH)
        } else {
            irCmd.setPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_LOW)
        }
    }

    /**
     * [CN_TEXT]Tpd
     */
    fun queryTpd(irCmd: IRCMD, params: CommonParams.PropTPDParams): Int {
        val value = IntArray(1)
        irCmd.getPropTPDParams(params, value)
        return value[0]
    }

    /**
     * [CN_TEXT]
     */
    fun shutter(irCmd: IRCMD?, syncImage: SynchronizedBitmap) {
        if (syncImage.type == 1) {
            irCmd?.tc1bShutterManual()
        } else {
            // [CN_TEXT]
            irCmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
        }
    }

    /**
     * [CN_TEXT]
     */
    fun stsSwitch(irCmd: IRCMD?, flag: Boolean) {
        if (flag) {
            irCmd?.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_EN)
        } else {
            irCmd?.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_DIS)
        }
    }

    /**
     * [CN_TEXT] - [CN_TEXT]
     *
     * @param gainType [CN_TEXT]GAIN_1
     * CommonParams.RMCoverAutoCalcType.GAIN_1
     * CommonParams.RMCoverAutoCalcType.GAIN_2
     * CommonParams.RMCoverAutoCalcType.GAIN_4
     */
    fun pot(irCmd: IRCMD, type: Int) {
        val gainType = when (type) {
            1 -> CommonParams.RMCoverAutoCalcType.GAIN_1
            2 -> CommonParams.RMCoverAutoCalcType.GAIN_2
            4 -> CommonParams.RMCoverAutoCalcType.GAIN_4
            else -> CommonParams.RMCoverAutoCalcType.GAIN_1
        }
        irCmd.rmCoverAutoCalc(gainType) //[CN_TEXT]
    }

    /**
     * [CN_TEXT]
     */
    fun autoShutter(irCmd: IRCMD?, flag: Boolean) {
        val data = if (flag) CommonParams.PropAutoShutterParameterValue.StatusSwith.ON else CommonParams.PropAutoShutterParameterValue.StatusSwith.OFF
        irCmd?.setPropAutoShutterParameter(CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH, data)
    }

    /**
     * TPD_PROP_DISTANCE[CN_TEXT]Settings
     * Settings[CN_TEXT] unit:cnt(128cnt=1m)
     * @param value 0 ~ 25600
     */
    fun setTpdDis(irCmd: IRCMD?, value: Int) {
        val data = CommonParams.PropTPDParamsValue.NumberType(value.toString())
        setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_DISTANCE, value = data)
    }

    /**
     * Settings[CN_TEXT] unit:cnt(128cnt=1)
     * @param value 1 ~ 128
     */
    fun setTpdEms(irCmd: IRCMD?, value: Int) {
        val data = CommonParams.PropTPDParamsValue.NumberType(value.toString())
        setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_EMS, value = data)
    }

    /**
     * SettingsTpd
     */
    private fun setTpdParams(irCmd: IRCMD?, params: CommonParams.PropTPDParams, value: CommonParams.PropTPDParamsValue): Int {
        return try {
            irCmd?.setPropTPDParams(params, value) ?: 0
        } catch (e: Exception) {
            XLog.w("Settings[CN_TEXT][${params.name}]: ${e.message}")
            0
        }
    }
}