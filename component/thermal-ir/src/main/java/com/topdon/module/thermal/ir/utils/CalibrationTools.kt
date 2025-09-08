package com.topdon.module.thermal.ir.utils

import android.util.Log
import com.elvishew.xlog.XLog
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.SynchronizedBitmap

/**
 * temperature[Chinese text]
 */
object CalibrationTools {

    /**
     * [Chinese text]point[Chinese text]
     * [Chinese text] - Settingstemperature
     */
    fun sign(irCmd: IRCMD, singlePointTemp: Int): Boolean {
        var success = false
        // [Chinese text],[Chinese text]temperature[Chinese text]
        if (irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD) == 0) {
            irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD)
            val result = irCmd.setTPDKtBtRecalPoint(CommonParams.TPDKtBtRecalPointType.RECAL_1_POINT, singlePointTemp)
            if (result == 0) {
                success = true
            } else {
                XLog.w("[Chinese text]point[Chinese text]")
            }
        } else {
            XLog.w("[Chinese text]point[Chinese text]")
        }
        return success
    }

    /**
     * temperature[Chinese text]
     * low[Chinese text](100 ~ 400)
     */
    fun pointFirst(irCmd: IRCMD, pointTemp: Int): Boolean {
        var success = false
        // [Chinese text],[Chinese text]temperature[Chinese text]
        if (irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD) == 0) {
            val result = irCmd.setTPDKtBtRecalPoint(CommonParams.TPDKtBtRecalPointType.RECAL_2_POINT_FIRST, pointTemp + 273)
            if (result == 0) {
                success = true
            } else {
                XLog.w("low[Chinese text]")
            }
        } else {
            XLog.w("low[Chinese text]")
        }
        return success
    }

    /**
     * temperature[Chinese text]
     * high[Chinese text](20 ~ 100)
     *
     * [Chinese text]low[Chinese text]high[Chinese text]
     */
    fun pointEnd(irCmd: IRCMD, pointTemp: Int): Boolean {
        var success = false
        // [Chinese text],[Chinese text]temperature[Chinese text]
        if (irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD) == 0) {
            val result = irCmd.setTPDKtBtRecalPoint(CommonParams.TPDKtBtRecalPointType.RECAL_2_POINT_END, pointTemp + 273)
            if (result == 0) {
                success = true
            } else {
                Log.w("123", "[Chinese text]")
            }
        } else {
            Log.w("123", "[Chinese text]")
        }
        return success
    }

    /**
     * [Chinese text] - [Chinese text]
     *
     */
    fun potReady(irCmd: IRCMD): Boolean {
        return irCmd.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_DIS) == 0 // [Chinese text]
    }

    /**
     * [Chinese text] - [Chinese text]start
     *
     * @param gainType [Chinese text]GAIN_1
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
        irCmd.rmCoverAutoCalc(gainType) // [Chinese text]
        irCmd.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_EN) // [Chinese text]
    }

    /**
     * [Chinese text]
     */
    fun cancelCalibration(irCmd: IRCMD) {
        irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_TPD)
    }

    /**
     * [Chinese text]
     */
    fun reset(irCmd: IRCMD) {
        irCmd.restoreDefaultConfig(CommonParams.DefaultConfigType.DEF_CFG_ALL)
    }

    /**
     * [Chinese text]mode
     * @return true: high[Chinese text]    false: low[Chinese text]
     */
    fun queryGain(irCmd: IRCMD): Boolean {
        val value = IntArray(1)
        irCmd.getPropTPDParams(CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value)
        return value[0] == 1
    }

    /**
     * Settings[Chinese text]mode
     * @param type 1: [Chinese text]    0: [Chinese text]
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
     * [Chinese text]Tpd
     */
    fun queryTpd(irCmd: IRCMD, params: CommonParams.PropTPDParams): Int {
        val value = IntArray(1)
        irCmd.getPropTPDParams(params, value)
        return value[0]
    }

    /**
     * [Chinese text]
     */
    fun shutter(irCmd: IRCMD?, syncImage: SynchronizedBitmap) {
        if (syncImage.type == 1) {
            irCmd?.tc1bShutterManual()
        } else {
            // [Chinese text]
            irCmd?.updateOOCOrB(CommonParams.UpdateOOCOrBType.B_UPDATE)
        }
    }

    /**
     * [Chinese text]
     */
    fun stsSwitch(irCmd: IRCMD?, flag: Boolean) {
        if (flag) {
            irCmd?.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_EN)
        } else {
            irCmd?.rmCoverStsSwitch(CommonParams.RMCoverStsSwitchStatus.RMCOVER_DIS)
        }
    }

    /**
     * [Chinese text] - [Chinese text]start
     *
     * @param gainType [Chinese text]GAIN_1
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
        irCmd.rmCoverAutoCalc(gainType) // [Chinese text]
    }

    /**
     * [Chinese text]
     */
    fun autoShutter(irCmd: IRCMD?, flag: Boolean) {
        val data = if (flag) CommonParams.PropAutoShutterParameterValue.StatusSwith.ON else CommonParams.PropAutoShutterParameterValue.StatusSwith.OFF
        irCmd?.setPropAutoShutterParameter(CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH, data)
    }

    /**
     * TPD_PROP_DISTANCE[Chinese text]Settings
     * Settings[Chinese text] unit:cnt(128cnt=1m)
     * @param value 0 ~ 25600
     */
    fun setTpdDis(irCmd: IRCMD?, value: Int) {
        val data = CommonParams.PropTPDParamsValue.NumberType(value.toString())
        setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_DISTANCE, value = data)
    }

    /**
     * Settings[Chinese text] unit:cnt(128cnt=1)
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
            XLog.w("Settings[Chinese text][${params.name}]: ${e.message}")
            0
        }
    }
}