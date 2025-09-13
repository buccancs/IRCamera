package com.topdon.tc001.camera.core

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Size

/**
 * Enhanced device capabilities detector with Samsung S22 optimizations
 * Detects and configures optimal settings for multi-sensor recording
 */
data class DeviceCaps(
    val supportsRaw: Boolean,
    val rawSize: Size,
    val supports4k60: Boolean, // true only if 2160p@60 present
    val sensorOrientation: Int,
    val deviceModel: String,
    val isSamsungS22: Boolean,
    val processorType: String, // Exynos 2200 or Snapdragon 8 Gen 1
    val optimalVideoSettings: VideoSettings,
    val optimalRawSettings: RawSettings,
    val maxConcurrentStreams: Int
) {
    
    companion object {
        private const val TAG = "DeviceCaps"
        
        /**
         * Detect device capabilities with Samsung S22 specific optimizations
         */
        fun detect(context: Context, cameraId: String): DeviceCaps {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            
            // Basic capabilities
            val supportsRaw = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                ?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) == true
            
            val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
            
            // RAW size detection
            val rawSize = if (supportsRaw) {
                val sizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?.getOutputSizes(android.graphics.ImageFormat.RAW_SENSOR)
                sizes?.maxByOrNull { it.width * it.height } ?: Size(0, 0)
            } else {
                Size(0, 0)
            }
            
            // 4K60 detection
            val supports4k60 = detectSupports4K60(characteristics)
            
            // Device detection
            val deviceModel = Build.MODEL
            val manufacturer = Build.MANUFACTURER
            val isSamsungS22 = manufacturer.equals("samsung", ignoreCase = true) && 
                              deviceModel.contains("SM-S90", ignoreCase = true)
            
            // Processor detection for Samsung S22
            val processorType = if (isSamsungS22) {
                detectS22ProcessorType()
            } else {
                "Generic"
            }
            
            // Optimal settings based on device capabilities
            val optimalVideoSettings = determineOptimalVideoSettings(isSamsungS22, supports4k60, processorType)
            val optimalRawSettings = determineOptimalRawSettings(isSamsungS22, rawSize, processorType)
            
            // Concurrent streams capability (fallback to safe default)
            val maxConcurrentStreams = try {
                characteristics.get(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_STREAMS)
                    ?.get(0) ?: 3 // Default to 3 streams for modern devices
            } catch (e: Exception) {
                Log.d(TAG, "Cannot get max concurrent streams, defaulting to 3")
                3 // Safe default
            }
            
            Log.i(TAG, "Device capabilities detected:")
            Log.i(TAG, "  Device: $manufacturer $deviceModel")
            Log.i(TAG, "  Samsung S22: $isSamsungS22")
            Log.i(TAG, "  Processor: $processorType")
            Log.i(TAG, "  RAW Support: $supportsRaw (${rawSize.width}x${rawSize.height})")
            Log.i(TAG, "  4K60 Support: $supports4k60")
            Log.i(TAG, "  Max Concurrent Streams: $maxConcurrentStreams")
            
            return DeviceCaps(
                supportsRaw = supportsRaw,
                rawSize = rawSize,
                supports4k60 = supports4k60,
                sensorOrientation = sensorOrientation,
                deviceModel = deviceModel,
                isSamsungS22 = isSamsungS22,
                processorType = processorType,
                optimalVideoSettings = optimalVideoSettings,
                optimalRawSettings = optimalRawSettings,
                maxConcurrentStreams = maxConcurrentStreams
            )
        }
        
        private fun detectSupports4K60(characteristics: CameraCharacteristics): Boolean {
            // Check for 4K60 support in video profile
            return try {
                val configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val videoSizes = configMap?.getOutputSizes(android.media.MediaRecorder::class.java)
                
                videoSizes?.any { size ->
                    size.width == 3840 && size.height == 2160
                } == true
            } catch (e: Exception) {
                Log.w(TAG, "Error detecting 4K60 support", e)
                false
            }
        }
        
        private fun detectS22ProcessorType(): String {
            return try {
                val cpuInfo = Build.HARDWARE.lowercase()
                when {
                    cpuInfo.contains("exynos") -> "Exynos_2200"
                    cpuInfo.contains("qcom") || cpuInfo.contains("snapdragon") -> "Snapdragon_8_Gen_1"
                    else -> "Samsung_S22_Generic"
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error detecting processor type", e)
                "Samsung_S22_Generic"
            }
        }
        
        private fun determineOptimalVideoSettings(isSamsungS22: Boolean, supports4k60: Boolean, processorType: String): VideoSettings {
            return if (isSamsungS22) {
                VideoSettings(
                    resolution = Size(3840, 2160), // 4K UHD
                    frameRate = if (supports4k60) 60 else 30,
                    bitRate = if (processorType == "Exynos_2200") 25_000_000 else 20_000_000, // Higher bitrate for Exynos
                    codec = "H.264",
                    stabilization = true,
                    hdr = false // Disable HDR for multi-sensor sync
                )
            } else {
                VideoSettings(
                    resolution = Size(1920, 1080), // Fall back to 1080p
                    frameRate = 30,
                    bitRate = 10_000_000,
                    codec = "H.264",
                    stabilization = true,
                    hdr = false
                )
            }
        }
        
        private fun determineOptimalRawSettings(isSamsungS22: Boolean, rawSize: Size, processorType: String): RawSettings {
            return if (isSamsungS22 && rawSize.width > 0) {
                RawSettings(
                    size = rawSize,
                    captureRate = if (processorType == "Exynos_2200") 10.0 else 8.0, // fps
                    format = android.graphics.ImageFormat.RAW_SENSOR,
                    bufferCount = 3, // Conservative for sustained capture
                    enableFastCapture = true
                )
            } else {
                RawSettings(
                    size = rawSize,
                    captureRate = 5.0,
                    format = android.graphics.ImageFormat.RAW_SENSOR,
                    bufferCount = 2,
                    enableFastCapture = false
                )
            }
        }
    }
    
    data class VideoSettings(
        val resolution: Size,
        val frameRate: Int,
        val bitRate: Int,
        val codec: String,
        val stabilization: Boolean,
        val hdr: Boolean
    )
    
    data class RawSettings(
        val size: Size,
        val captureRate: Double, // fps
        val format: Int,
        val bufferCount: Int,
        val enableFastCapture: Boolean
    )
}
