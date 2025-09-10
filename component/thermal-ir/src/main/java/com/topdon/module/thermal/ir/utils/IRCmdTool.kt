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
import com.infisense.usbir.utils.HexDump
import com.topdon.lib.core.common.SharedManager
import java.io.IOException
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.floor

object IRCmdTool {
    val TAG = "IRCmdTool"
    var dispNumber = 30

    fun getDualBytes(irCmd: IRCMD?): ByteArray {
        val calibrationDataSize = 192
        val INIT_ALIGN_DATA = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)

        val oemInfo = ByteArray(512)
        val snData = ByteArray(256)
        val dispData = ByteArray(5) // Registration data
        irCmd?.oemRead(CommonParams.ProductType.P2, oemInfo)
        XLog.w("Core calibration", "data loaded:")
        val calibrationData = ByteArray(calibrationDataSize)
        val productTypeData = ByteArray(2)
        System.arraycopy(oemInfo, 0, calibrationData, 0, calibrationData.size)
        System.arraycopy(oemInfo, calibrationDataSize, productTypeData, 0, productTypeData.size)
        System.arraycopy(
            oemInfo,
            calibrationDataSize + productTypeData.size,
            dispData,
            0,
            dispData.size,
        )
        System.arraycopy(oemInfo, 256, snData, 0, snData.size)
        try {
            var str = String(dispData)
            str = str.replace(Regex("[^-\\d]"), "")
            dispNumber = str.toInt()
            if (dispNumber > 60) {
                dispNumber = dispNumber / 10
            }
            if (dispNumber < -20) {
                dispNumber = -20
            }
            XLog.w("Registration offset:", "" + dispNumber)
        } catch (e: Exception) {
            XLog.w("Registration error")
        }
        val snList = String(snData).split(";")
        val snStr =
            if (snList.isNotEmpty() && snList[0].contains("sn", true)) {
                snList[0].replace("SN:", "")
            } else {
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
                // Using calibration file, loading alignment data
                val alignByte = SharedManager.getManualData(snStr)
                System.arraycopy(alignByte, 0, parameters, calibrationDataSize + 1, alignByte.size)
                XLog.w("Core calibration using file, loading alignment data")
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

    fun getSNStr(irCmd: IRCMD?): String {
        val oemInfo = ByteArray(512)
        irCmd?.oemRead(CommonParams.ProductType.P2, oemInfo)
        val snData = ByteArray(256)
        System.arraycopy(oemInfo, 256, snData, 0, snData.size)
        val snList = String(snData).split(";")
        return if (snList.isNotEmpty() && snList[0].contains("sn", true)) {
            snList[0].replace("SN:", "")
        } else {
            ""
        }
    }

    fun setTpdEms(
        irCmd: IRCMD?,
        value: Int,
    ) {
        val data = CommonParams.PropTPDParamsValue.NumberType(value.toString())
        setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_EMS, value = data)
    }

    fun setTpdDis(
        irCmd: IRCMD?,
        value: Int,
    ) {
        val data = CommonParams.PropTPDParamsValue.NumberType(value.toString())
        setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_DISTANCE, value = data)
    }

    fun setLevelContrast(
        irCmd: IRCMD?,
        value: Int,
    ) {
        val data = CommonParams.PropImageParamsValue.NumberType(value.toString())
        setImageParams(irCmd = irCmd, params = CommonParams.PropImageParams.IMAGE_PROP_LEVEL_CONTRAST, value = data)
    }

    fun setLevelDdd(
        irCmd: IRCMD?,
        value: Int,
    ) {
        val data =
            when (value) {
                0 -> CommonParams.PropImageParamsValue.DDEType.DDE_0
                1 -> CommonParams.PropImageParamsValue.DDEType.DDE_1
                2 -> CommonParams.PropImageParamsValue.DDEType.DDE_2
                3 -> CommonParams.PropImageParamsValue.DDEType.DDE_3
                4 -> CommonParams.PropImageParamsValue.DDEType.DDE_4
                else -> CommonParams.PropImageParamsValue.DDEType.DDE_0
            }
        setImageParams(irCmd = irCmd, params = CommonParams.PropImageParams.IMAGE_PROP_LEVEL_DDE, value = data)
    }

    fun setTpdGainSel(
        irCmd: IRCMD?,
        value: Int,
    ): Int {
        val data =
            if (value == 1) {
                CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_HIGH
            } else {
                CommonParams.PropTPDParamsValue.GAINSELStatus.GAIN_SEL_LOW
            }
        return setTpdParams(irCmd = irCmd, params = CommonParams.PropTPDParams.TPD_PROP_GAIN_SEL, value = data)
    }

    fun setDisp(
        dualView: BaseDualView?,
        value: Int,
    ): Int {
        return try {
            if (dualView != null) {
                dualView?.dualUVCCamera!!.setDisp(value)
                0
            } else {
                -1
            }
        } catch (e: Exception) {
            XLog.w("Settings registration error [$value]: ${e.message}")
            0
        }
    }

    fun setAlignTranslate(
        dualView: BaseDualView?,
        moveX: Int,
        moveY: Int,
    ) {
        val newSrc = ByteArray(8)

        val xSrc = ByteArray(4)
        HexDump.float2byte(moveX.toFloat(), xSrc)
        System.arraycopy(xSrc, 0, newSrc, 0, 4)

        val ySrc = ByteArray(4)
        HexDump.float2byte(moveY.toFloat(), ySrc)
        System.arraycopy(ySrc, 0, newSrc, 4, 4)

        dualView?.dualUVCCamera?.setAlignTranslateParameter(newSrc)
    }

    fun setIsoColorOpen(
        dualUVCCamera: DualUVCCamera?,
        highC: Float,
        lowC: Float,
    ) {
        dualUVCCamera?.setIsothermal(DualCameraParams.IsothermalState.ON)
        val normalHighTemp = (highC + 273).toDouble() // Convert to Kelvin
        val normalLowTemp = (lowC + 273).toDouble() // Convert to Kelvin
        val highTemp = ceil(normalHighTemp * 16 * 4).toInt() // High temperature threshold
        val lowTemp = floor(normalLowTemp * 16 * 4).toInt() // Low temperature threshold
        val highData = ByteArray(2)
        highData[0] = highTemp.toByte()
        highData[1] = (highTemp shr 8).toByte()
        val lowData = ByteArray(2)
        lowData[0] = lowTemp.toByte()
        lowData[1] = (lowTemp shr 8).toByte()
        val tempHFin = (highData[0].toInt() and 0x00ff) + (highData[1].toInt() and 0x00ff shl 8)
        val tempLFin = (lowData[0].toInt() and 0x00ff) + (lowData[1].toInt() and 0x00ff shl 8)
        dualUVCCamera?.setTempL(tempLFin) // Low temperature - convert to Int
        dualUVCCamera?.setTempH(tempHFin) // High temperature - convert to Int
    }

    /**
     * Manual shutter calibration
     */
    fun setIsoColorClose(dualUVCCamera: DualUVCCamera?) {
        dualUVCCamera?.setIsothermal(DualCameraParams.IsothermalState.OFF)
    }

    /**
     * Zoom in (digital zoom)
     * ZoomScaleStep.ZOOM_STEP1: 2x magnification
     * ZoomScaleStep.ZOOM_STEP2: 4x magnification
     * ZoomScaleStep.ZOOM_STEP3: 8x magnification
     * ZoomScaleStep.ZOOM_STEP4: 16x magnification
     */
    fun setZoomUp(irCmd: IRCMD?) {
        irCmd?.zoomCenterUp(CommonParams.PreviewPathChannel.PREVIEW_PATH0, CommonParams.ZoomScaleStep.ZOOM_STEP2)
    }

    /**
     * Manual shutter calibration
     */
    fun setZoomDown(irCmd: IRCMD?) {
        irCmd?.zoomCenterDown(CommonParams.PreviewPathChannel.PREVIEW_PATH0, CommonParams.ZoomScaleStep.ZOOM_STEP2)
    }
}
