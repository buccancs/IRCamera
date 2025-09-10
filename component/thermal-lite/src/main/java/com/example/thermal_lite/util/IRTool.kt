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

    fun temperatureCorrection(
        temp: Float,
        params_array: FloatArray,
        tau_data_H: ByteArray,
        tau_data_L: ByteArray,
        basicGainGetValue: Int,
    ): Float {
        var newTemp = temp
        // Utility functionState PASS
        try {
            if (tau_data_H == null || tau_data_L == null) return temp
            newTemp =
                LibIRTempAC020.temperatureCorrection(
                    params_array[0],
                    tau_data_H,
                    tau_data_L,
                    params_array[1],
                    params_array[2],
                    params_array[3],
                    params_array[4],
                    params_array[5],
                    if (basicGainGetValue == 0) GainStatus.LOW_GAIN else GainStatus.HIGH_GAIN,
                )
        } catch (e: Exception) {
            XLog.e("$TAG:temperatureCorrection-${e.message}")
        } finally {
            return newTemp
        }
    }

    /**
     * SettingsutilityModeutility
     */
    fun setMode() {
}
}
