package com.topdon.module.thermal.ir.utils

import android.util.Log
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.XLog
import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.DualCameraParams
import com.energy.iruvc.utils.SynchronizedBitmap
import com.infisense.usbdual.camera.BaseDualView
import com.infisense.usbdual.camera.USBMonitorManager
import com.infisense.usbir.utils.FileUtil
import com.infisense.usbir.utils.HexDump
import com.topdon.lib.core.common.SaveSettingUtil
import com.topdon.lib.core.common.SharedManager
import java.io.IOException
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.floor

object IRCmdTool {
    val TAG = "IRCmdTool"
    var dispNumber = 30

    fun getDualBytes(irCmd: IRCMD?):ByteArray {
        val calibrationDataSize = 192
        val INIT_ALIGN_DATA = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)

        val oemInfo = ByteArray(512)
        val snData = ByteArray(256)
        val dispData = ByteArray(5)//Registration[CN_TEXT]
        irCmd?.oemRead(CommonParams.ProductType.P2, oemInfo)
        XLog.w("Core[CN_TEXT]", "[CN_TEXT]:")
        val calibrationData = ByteArray(calibrationDataSize)
        val productTypeData = ByteArray(2)
        System.arraycopy(oemInfo, 0, calibrationData, 0, calibrationData.size)
        System.arraycopy(oemInfo, calibrationDataSize, productTypeData, 0, productTypeData.size)
        System.arraycopy(oemInfo, calibrationDataSize+productTypeData.size, dispData,
            0, dispData.size)
        System.arraycopy(oemInfo,256,snData,0,snData.size)
        try {
            var str = String(dispData)
            str = str.replace(Regex("[^-\\d]"),"")
            dispNumber = str.toInt()
            if (dispNumber > 60){
                dispNumber = dispNumber/10
            }
            if (dispNumber < -20){
                dispNumber = -20
            }
            XLog.w("Registration[CN_TEXT]:", ""+dispNumber)
        }catch (e:Exception){
            XLog.w("Registration[CN_TEXT]")
        }
        val snList = String(snData).split(";")
        val snStr = if (snList.isNotEmpty() && snList[0].contains("sn",true)){
            snList[0].replace("SN:","")
        }else{
            ""
        }
        val parameters = ByteArray(calibrationDataSize + 1 + 24)
        if (String(productTypeData) == "TD") {
            System.arraycopy(calibrationData, 0, parameters, 0, calibrationData.size)
            parameters[calibrationDataSize] = 1
            val alignByte = SharedManager.getManualData(snStr)
            System.arraycopy(alignByte, 0, parameters, calibrationDataSize + 1, alignByte.size)
        } else {
            val am = Utils.getApp().assets
            var `is`: InputStream? = null
            val length: Int
            try {
                `is` = am.open("dual_calibration_parameters2.bin")
                length = `is`.available()
                if (`is`.read(parameters) != length) {
                    Log.e(TAG, "read file fail ")
                }
                parameters[length] = 1
                //[CN_TEXT]，[CN_TEXT]
                val alignByte = SharedManager.getManualData(snStr)
                System.arraycopy(alignByte, 0, parameters, calibrationDataSize + 1, alignByte.size)
                XLog.w("Core[CN_TEXT]，[CN_TEXT]")
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    `is`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return parameters
    }

    fun getSNStr(irCmd: IRCMD?) : String{
        val oemInfo = ByteArray(512)
        irCmd?.oemRead(CommonParams.ProductType.P2, oemInfo)
        val snData = ByteArray(256)
        System.arraycopy(oemInfo,256,snData,0,snData.size)
        val snList = String(snData).split(";")
        return if (snList.isNotEmpty() && snList[0].contains("sn",true)){
            snList[0].replace("SN:","")
        }else{
            ""
        }
    }


    /**
     * Settings[CN_TEXT] unit:cnt(128cnt = 1)
     * @param value 1 ~ 128
     */
    fun setTpdEms(irCmd: IRCMD?, value: Int) {
        val data = CommonParams.PropTPDParamsValue.NumberType(value.toString())
        setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_EMS, value = data)
    }

    /**
     * Settings[CN_TEXT] unit:cnt(128cnt = 1m, [CN_TEXT]: 0.25 * 128 = 32)
     * @param value 0 ~ 25600
     *
     * [CN_TEXT]sdk[CN_TEXT]SettingsTPD_PROP_DISTANCE[CN_TEXT]
     */
    fun setTpdDis(irCmd: IRCMD?, value: Int) {
        val data = CommonParams.PropTPDParamsValue.NumberType(value.toString())
        setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_DISTANCE, value = data)
    }


    /**
     * Settings[CN_TEXT]
     * @param value 0 ~ 255
     */
    fun setLevelContrast(irCmd: IRCMD?, value: Int) {
        val data = CommonParams.PropImageParamsValue.NumberType(value.toString())
        setImageParams(irCmd = irCmd, params = CommonParams.PropImageParams.IMAGE_PROP_LEVEL_CONTRAST, value = data)
    }

    /**
     * Settings[CN_TEXT]
     * @param value 0 ~ 4
     *
     */
    fun setLevelDdd(irCmd: IRCMD?, value: Int) {
        val data = when (value) {
            0 -> CommonParams.PropImageParamsValue.DDEType.DDE_0
            1 -> CommonParams.PropImageParamsValue.DDEType.DDE_1
            2 -> CommonParams.PropImageParamsValue.DDEType.DDE_2
            3 -> CommonParams.PropImageParamsValue.DDEType.DDE_3
            4 -> CommonParams.PropImageParamsValue.DDEType.DDE_4
            else -> CommonParams.PropImageParamsValue.DDEType.DDE_0
        }
        setImageParams(irCmd = irCmd, params = CommonParams.PropImageParams.IMAGE_PROP_LEVEL_DDE, value = data)
    }

    /**
     * Settings[CN_TEXT]
     */
    fun setLevelAgc(irCmd: IRCMD?, value: Boolean) {
        val data = if (value) {
            CommonParams.PropImageParamsValue.StatusSwith.ON
        } else {
            CommonParams.PropImageParamsValue.StatusSwith.OFF
        }
        setImageParams(irCmd = irCmd, params = CommonParams.PropImageParams.IMAGE_PROP_ONOFF_AGC, value = data)
    }

    /**
     * [CN_TEXT]Mode
     * @return 1:High gain(Normal temperature)    0:Low gain(High temperature)
     */
    fun getTpdGainSel(irCmd: IRCMD?): Int {
        val result = queryTpdParam(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL)
        return if (result == CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH.value) {
            1
        } else {
            0
        }
    }

    /**
     * Settings[CN_TEXT]Mode
     * @param value 1:High gain(Normal temperature)    0:Low gain(High temperature)
     */
    fun setTpdGainSel(irCmd: IRCMD?, value: Int): Int {
        val data = if (value == 1) {
            CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH
        } else {
            CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_LOW
        }
        return setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value = data)
    }

    /**
     * [CN_TEXT]Tpd
     */
    fun queryTpdParam(irCmd: IRCMD?, params: CommonParams.PropTPDParams): Int {
        val value = IntArray(1)
        irCmd?.getPropTPDParams(params, value)
        return value[0]
    }

    /**
     * [CN_TEXT]Image
     */
    fun queryImageParam(irCmd: IRCMD?, params: CommonParams.PropImageParams): Int {
        val value = IntArray(1)
        irCmd?.getPropImageParams(params, value)
        return value[0]
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

    /**
     * Settings[CN_TEXT]
     */
    private fun setImageParams(irCmd: IRCMD?, params: CommonParams.PropImageParams, value: CommonParams.PropImageParamsValue): Int {
        return try {
            irCmd?.setPropImageParams(params, value) ?: 0
        } catch (e: Exception) {
            XLog.w("Settings[CN_TEXT][${params.name}]: ${e.message}")
            0
        }
    }

    /**
     * Registration
     * [CN_TEXT]
     * @param value (-20 ~ 60)
     */
    fun setDisp(dualView: BaseDualView?, value: Int): Int {
        return try {
            if (dualView != null) {
                dualView?.dualUVCCamera!!.setDisp(value)
                0 // Return success
            } else {
                -1 // Return error
            }
        } catch (e: Exception) {
            XLog.w("SettingsRegistration[CN_TEXT][${value}]: ${e.message}")
            0
        }
    }

    /**
     * @param moveX [CN_TEXT]Current[CN_TEXT]
     */
    fun setAlignTranslate(dualView: BaseDualView?, moveX: Int, moveY: Int) {
        val newSrc = ByteArray(8)

        val xSrc = ByteArray(4)
        HexDump.float2byte(moveX.toFloat(), xSrc)
        System.arraycopy(xSrc, 0, newSrc, 0, 4)

        val ySrc = ByteArray(4)
        HexDump.float2byte(moveY.toFloat(), ySrc)
        System.arraycopy(ySrc, 0, newSrc, 4, 4)

        dualView?.dualUVCCamera?.setAlignTranslateParameter(newSrc)
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
    fun autoShutter(irCmd: IRCMD?, flag: Boolean) {
        val data = if (flag) CommonParams.PropAutoShutterParameterValue.StatusSwith.ON else CommonParams.PropAutoShutterParameterValue.StatusSwith.OFF
        irCmd?.setPropAutoShutterParameter(CommonParams.PropAutoShutterParameter.SHUTTER_PROP_SWITCH, data)
    }

    /**
     * [CN_TEXT]
     * @param highC [CN_TEXT]，[CN_TEXT]Celsius
     * @param lowC [CN_TEXT]，[CN_TEXT]Celsius
     */
    fun setIsoColorOpen(dualUVCCamera: DualUVCCamera?, highC: Float, lowC: Float) {
        dualUVCCamera?.setIsothermal(DualCameraParams.IsothermalState.ON)
        val normalHighTemp = (highC + 273).toDouble() //[CN_TEXT]k
        val normalLowTemp = (lowC + 273).toDouble() //[CN_TEXT]k
        val highTemp = ceil(normalHighTemp * 16 * 4).toInt() // High temperature[CN_TEXT]
        val lowTemp = floor(normalLowTemp * 16 * 4).toInt() // Low temperature[CN_TEXT]
        val highData = ByteArray(2)
        highData[0] = highTemp.toByte()
        highData[1] = (highTemp shr 8).toByte()
        val lowData = ByteArray(2)
        lowData[0] = lowTemp.toByte()
        lowData[1] = (lowTemp shr 8).toByte()
        val tempHFin = (highData[0].toInt() and 0x00ff) + (highData[1].toInt() and 0x00ff shl 8)
        val tempLFin = (lowData[0].toInt() and 0x00ff) + (lowData[1].toInt() and 0x00ff shl 8)
        dualUVCCamera?.setTempL(tempLFin) //Low temperature - convert to Int
        dualUVCCamera?.setTempH(tempHFin) //High temperature - convert to Int
    }

    /**
     * [CN_TEXT]
     */
    fun setIsoColorClose(dualUVCCamera: DualUVCCamera?) {
        dualUVCCamera?.setIsothermal(DualCameraParams.IsothermalState.OFF)
    }

    /**
     * [CN_TEXT]([CN_TEXT])
     * ZoomScaleStep.ZOOM_STEP1: 2[CN_TEXT]
     * ZoomScaleStep.ZOOM_STEP2: 4[CN_TEXT]
     * ZoomScaleStep.ZOOM_STEP3: 8[CN_TEXT]
     * ZoomScaleStep.ZOOM_STEP4: 16[CN_TEXT]
     */
    fun setZoomUp(irCmd: IRCMD?) {
        irCmd?.zoomCenterUp(CommonParams.PreviewPathChannel.PREVIEW_PATH0, CommonParams.ZoomScaleStep.ZOOM_STEP2)
    }

    /**
     * [CN_TEXT]
     */
    fun setZoomDown(irCmd: IRCMD?) {
        irCmd?.zoomCenterDown(CommonParams.PreviewPathChannel.PREVIEW_PATH0, CommonParams.ZoomScaleStep.ZOOM_STEP2)
    }

}