package com.topdon.tc001.camera

import android.content.Context
import android.util.Log
import android.view.TextureView
import com.topdon.gsr.util.TimeUtil
import com.topdon.tc001.gsr.EnhancedThermalRecorder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

    fun generateSessionMetadata(): Map<String, Any> {
        return mapOf(
            "session_id" to (currentSessionId ?: "unknown"),
            "device_model" to android.os.Build.MODEL,
            "device_processor" to detectSamsungS22Processor(),
            "timing_precision" to "sub_millisecond",
            "unified_time_base" to "samsung_s22_ground_truth",
            "recording_components" to
                mapOf(
                    "thermal" to "thermal_camera_video",
                    "rgb" to
                        mapOf(
                            "resolution" to (rgbCameraRecorder?.getCurrentSettings()?.resolution?.displayName ?: "unknown"),
                            "frame_rate" to (rgbCameraRecorder?.getCurrentSettings()?.frameRate ?: 0),
                            "camera_facing" to (rgbCameraRecorder?.getCurrentCameraFacing()?.displayName ?: "unknown"),
                        ),
                    "gsr" to
                        mapOf(
                            "sampling_rate" to "128Hz",
                            "device_type" to "shimmer3_gsr",
                            "data_format" to "conductance_resistance_csv",
                        ),
                ),
            "synchronization_accuracy" to "samsung_s22_hardware_timer",
            "android_version" to android.os.Build.VERSION.RELEASE,
            "api_level" to android.os.Build.VERSION.SDK_INT,
        )
    }

    private fun detectSamsungS22Processor(): String {
        val deviceModel = android.os.Build.MODEL
        return when {
            deviceModel.contains("SM-S901E") -> "Exynos_2200" // International
            deviceModel.contains("SM-S901U") -> "Snapdragon_8_Gen_1" // US
            deviceModel.contains("SM-S901W") -> "Snapdragon_8_Gen_1" // Canada
            deviceModel.contains("SM-S901N") -> "Snapdragon_8_Gen_1" // Korea
            deviceModel.contains("SM-S901") -> "Samsung_S22_Generic" // Generic S22
            else -> "Unknown_Device"
        }
    }
}
