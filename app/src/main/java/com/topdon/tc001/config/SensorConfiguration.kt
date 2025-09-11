package com.topdon.tc001.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import android.content.Context
import android.util.Log

/**
 * Centralized configuration management for all sensors in the Multi-Modal Physiological Sensing Platform.
 * 
 * Replaces hard-coded sensor parameters with configurable settings that can be:
 * - Modified through PC Controller GUI
 * - Saved/loaded from configuration files
 * - Validated for hardware compatibility
 * - Updated during runtime for optimization
 * 
 * @author IRCamera Android Sensor Node (Spoke)
 */
data class SensorConfiguration(
    val rgbCamera: RgbCameraConfig = RgbCameraConfig(),
    val thermalCamera: ThermalCameraConfig = ThermalCameraConfig(),
    val gsrSensor: GSRSensorConfig = GSRSensorConfig(),
    val recording: RecordingConfig = RecordingConfig(),
    val synchronization: SynchronizationConfig = SynchronizationConfig(),
    val dataExport: DataExportConfig = DataExportConfig()
)

data class RgbCameraConfig(
    val videoResolution: VideoResolution = VideoResolution.HD_1080P,
    val videoFrameRate: Int = 30,
    val videoFormat: VideoFormat = VideoFormat.MP4,
    val imageResolution: ImageResolution = ImageResolution.FULL_4K,
    val imageFormat: ImageFormat = ImageFormat.JPEG,
    val imageQuality: Int = 95,
    val captureMode: CaptureMode = CaptureMode.DUAL_STREAM, // Video + Images
    val autoFocus: Boolean = true,
    val stabilization: Boolean = true,
    val exposureMode: ExposureMode = ExposureMode.AUTO
)

data class ThermalCameraConfig(
    val frameRate: Double = 9.0,
    val resolution: ThermalResolution = ThermalResolution.TC001_256x192,
    val temperatureRange: TemperatureRange = TemperatureRange.STANDARD,
    val emissivity: Double = 0.95,
    val ambientTemperature: Double = 25.0,
    val reflectedTemperature: Double = 23.0,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val calibrationMode: CalibrationMode = CalibrationMode.AUTOMATIC,
    val dataOutputFormat: ThermalDataFormat = ThermalDataFormat.CSV_MATRIX
)

data class GSRSensorConfig(
    val samplingRate: Int = 128, // Hz
    val sensorType: GSRSensorType = GSRSensorType.SHIMMER3_GSR_PLUS,
    val connectionMode: GSRConnectionMode = GSRConnectionMode.BLUETOOTH_LE,
    val dataFormat: GSRDataFormat = GSRDataFormat.MICROSIEMENS,
    val filterSettings: GSRFilterConfig = GSRFilterConfig(),
    val calibrationParameters: GSRCalibrationConfig = GSRCalibrationConfig()
)

data class RecordingConfig(
    val sessionDirectory: String = "/storage/emulated/0/IRCamera_Sessions",
    val fileNamingPattern: String = "session_{timestamp}_{sensor}",
    val maxSessionDuration: Long = 3600000, // 1 hour in milliseconds
    val autoStopOnLowBattery: Boolean = true,
    val batteryThreshold: Int = 15, // Percent
    val storageThreshold: Long = 1024 * 1024 * 1024, // 1GB remaining
    val compressionEnabled: Boolean = true,
    val encryptionEnabled: Boolean = true
)

data class SynchronizationConfig(
    val timeSyncMethod: TimeSyncMethod = TimeSyncMethod.NTP_LIKE,
    val syncMarkerInterval: Long = 30000, // 30 seconds
    val clockOffsetRecalculation: Long = 300000, // 5 minutes
    val targetAccuracy: Long = 5, // 5ms target accuracy
    val maxClockDrift: Long = 100, // 100ms max allowable drift
    val syncValidationEnabled: Boolean = true
)

data class DataExportConfig(
    val unifiedTimebase: Boolean = true,
    val exportFormat: ExportFormat = ExportFormat.HDF5,
    val includeSyncMarkers: Boolean = true,
    val dataCompression: Boolean = true,
    val metadataIncluded: Boolean = true,
    val anonymizationEnabled: Boolean = true,
    val exportDestination: String = "/storage/emulated/0/IRCamera_Exports"
)

// Configuration enums
enum class VideoResolution(val width: Int, val height: Int) {
    HD_720P(1280, 720),
    HD_1080P(1920, 1080),
    UHD_4K(3840, 2160)
}

enum class VideoFormat { MP4, AVI, MOV }

enum class ImageResolution(val width: Int, val height: Int) {
    HD_1080P(1920, 1080),
    UHD_4K(3840, 2160),
    FULL_4K(4032, 3024)
}

enum class ImageFormat { JPEG, PNG, RAW }

enum class CaptureMode {
    VIDEO_ONLY,
    IMAGES_ONLY,
    DUAL_STREAM // Video + Images simultaneously
}

enum class ExposureMode { AUTO, MANUAL, PRIORITY_SHUTTER, PRIORITY_ISO }

enum class ThermalResolution(val width: Int, val height: Int) {
    TC001_256x192(256, 192),
    FLIR_320x240(320, 240),
    FLIR_640x480(640, 480)
}

enum class TemperatureRange(val min: Float, val max: Float) {
    STANDARD(-20f, 400f),
    HIGH_TEMP(0f, 800f),
    LOW_TEMP(-40f, 200f)
}

enum class TemperatureUnit { CELSIUS, FAHRENHEIT, KELVIN }

enum class CalibrationMode { AUTOMATIC, MANUAL, FACTORY }

enum class ThermalDataFormat { CSV_MATRIX, BINARY, HDF5 }

enum class GSRSensorType {
    SHIMMER3_GSR_PLUS,
    SHIMMER3_GSR_UNIT,
    EMPATICA_E4,
    GENERIC_BLE
}

enum class GSRConnectionMode { BLUETOOTH_LE, BLUETOOTH_CLASSIC, USB_SERIAL }

enum class GSRDataFormat { MICROSIEMENS, KOHMS, RAW_ADC }

enum class TimeSyncMethod { NTP_LIKE, PTP, CUSTOM_PROTOCOL }

enum class ExportFormat { HDF5, CSV, JSON, MATLAB }

data class GSRFilterConfig(
    val lowPassEnabled: Boolean = false,
    val lowPassCutoff: Double = 1.0, // Hz
    val highPassEnabled: Boolean = false,
    val highPassCutoff: Double = 0.05, // Hz
    val notchFilterEnabled: Boolean = true,
    val notchFrequency: Double = 50.0 // Hz (for power line noise)
)

data class GSRCalibrationConfig(
    val referenceResistor: Double = 40200.0, // Ohms
    val adcResolution: Int = 12, // bits
    val adcReference: Double = 3.0, // Volts
    val gainFactor: Double = 1.0,
    val offsetCorrection: Double = 0.0
)

/**
 * Configuration manager for loading, saving, and validating sensor configurations
 */
class ConfigurationManager(private val context: Context) {
    companion object {
        private const val TAG = "ConfigurationManager"
        private const val CONFIG_FILENAME = "sensor_config.json"
        private const val CONFIG_BACKUP_FILENAME = "sensor_config_backup.json"
    }
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    
    private var currentConfig: SensorConfiguration = SensorConfiguration()
    
    /**
     * Load configuration from file, falling back to defaults if file doesn't exist
     */
    fun loadConfiguration(): SensorConfiguration {
        return try {
            val configFile = File(context.filesDir, CONFIG_FILENAME)
            
            if (configFile.exists()) {
                val configJson = configFile.readText()
                val loadedConfig = gson.fromJson(configJson, SensorConfiguration::class.java)
                
                // Validate configuration
                val validatedConfig = validateConfiguration(loadedConfig)
                currentConfig = validatedConfig
                
                Log.i(TAG, "Configuration loaded successfully from file")
                validatedConfig
            } else {
                Log.i(TAG, "No configuration file found, using defaults")
                val defaultConfig = SensorConfiguration()
                saveConfiguration(defaultConfig) // Save defaults for future use
                currentConfig = defaultConfig
                defaultConfig
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load configuration, using defaults", e)
            val defaultConfig = SensorConfiguration()
            currentConfig = defaultConfig
            defaultConfig
        }
    }
    
    /**
     * Save configuration to file with backup
     */
    fun saveConfiguration(config: SensorConfiguration): Boolean {
        return try {
            val configFile = File(context.filesDir, CONFIG_FILENAME)
            val backupFile = File(context.filesDir, CONFIG_BACKUP_FILENAME)
            
            // Create backup of existing config
            if (configFile.exists()) {
                configFile.copyTo(backupFile, overwrite = true)
            }
            
            // Validate configuration before saving
            val validatedConfig = validateConfiguration(config)
            
            // Save new configuration
            val configJson = gson.toJson(validatedConfig)
            configFile.writeText(configJson)
            
            currentConfig = validatedConfig
            Log.i(TAG, "Configuration saved successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save configuration", e)
            false
        }
    }
    
    /**
     * Get current configuration
     */
    fun getCurrentConfiguration(): SensorConfiguration = currentConfig
    
    /**
     * Update specific sensor configuration
     */
    fun updateRgbCameraConfig(config: RgbCameraConfig): Boolean {
        val newConfig = currentConfig.copy(rgbCamera = config)
        return saveConfiguration(newConfig)
    }
    
    fun updateThermalCameraConfig(config: ThermalCameraConfig): Boolean {
        val newConfig = currentConfig.copy(thermalCamera = config)
        return saveConfiguration(newConfig)
    }
    
    fun updateGSRSensorConfig(config: GSRSensorConfig): Boolean {
        val newConfig = currentConfig.copy(gsrSensor = config)
        return saveConfiguration(newConfig)
    }
    
    fun updateRecordingConfig(config: RecordingConfig): Boolean {
        val newConfig = currentConfig.copy(recording = config)
        return saveConfiguration(newConfig)
    }
    
    fun updateSynchronizationConfig(config: SynchronizationConfig): Boolean {
        val newConfig = currentConfig.copy(synchronization = config)
        return saveConfiguration(newConfig)
    }
    
    /**
     * Validate configuration parameters for hardware compatibility
     */
    private fun validateConfiguration(config: SensorConfiguration): SensorConfiguration {
        val validatedRgb = validateRgbConfig(config.rgbCamera)
        val validatedThermal = validateThermalConfig(config.thermalCamera)
        val validatedGSR = validateGSRConfig(config.gsrSensor)
        val validatedRecording = validateRecordingConfig(config.recording)
        val validatedSync = validateSyncConfig(config.synchronization)
        
        return config.copy(
            rgbCamera = validatedRgb,
            thermalCamera = validatedThermal,
            gsrSensor = validatedGSR,
            recording = validatedRecording,
            synchronization = validatedSync
        )
    }
    
    private fun validateRgbConfig(config: RgbCameraConfig): RgbCameraConfig {
        // Validate video frame rate
        val validFrameRate = when {
            config.videoFrameRate < 15 -> 15
            config.videoFrameRate > 60 -> 60
            else -> config.videoFrameRate
        }
        
        // Validate image quality
        val validQuality = when {
            config.imageQuality < 50 -> 50
            config.imageQuality > 100 -> 100
            else -> config.imageQuality
        }
        
        return config.copy(
            videoFrameRate = validFrameRate,
            imageQuality = validQuality
        )
    }
    
    private fun validateThermalConfig(config: ThermalCameraConfig): ThermalCameraConfig {
        // Validate frame rate for TC001
        val validFrameRate = when {
            config.frameRate < 1.0 -> 1.0
            config.frameRate > 25.0 -> 25.0
            else -> config.frameRate
        }
        
        // Validate emissivity
        val validEmissivity = when {
            config.emissivity < 0.1 -> 0.1
            config.emissivity > 1.0 -> 1.0
            else -> config.emissivity
        }
        
        return config.copy(
            frameRate = validFrameRate,
            emissivity = validEmissivity
        )
    }
    
    private fun validateGSRConfig(config: GSRSensorConfig): GSRSensorConfig {
        // Validate sampling rate for Shimmer3
        val validSamplingRate = when {
            config.samplingRate < 16 -> 16
            config.samplingRate > 1024 -> 1024
            else -> config.samplingRate
        }
        
        return config.copy(samplingRate = validSamplingRate)
    }
    
    private fun validateRecordingConfig(config: RecordingConfig): RecordingConfig {
        // Validate session duration (max 24 hours)
        val validDuration = when {
            config.maxSessionDuration < 60000 -> 60000 // Min 1 minute
            config.maxSessionDuration > 86400000 -> 86400000 // Max 24 hours
            else -> config.maxSessionDuration
        }
        
        // Validate battery threshold
        val validBattery = when {
            config.batteryThreshold < 5 -> 5
            config.batteryThreshold > 50 -> 50
            else -> config.batteryThreshold
        }
        
        return config.copy(
            maxSessionDuration = validDuration,
            batteryThreshold = validBattery
        )
    }
    
    private fun validateSyncConfig(config: SynchronizationConfig): SynchronizationConfig {
        // Validate target accuracy (1ms to 100ms)
        val validAccuracy = when {
            config.targetAccuracy < 1 -> 1
            config.targetAccuracy > 100 -> 100
            else -> config.targetAccuracy
        }
        
        return config.copy(targetAccuracy = validAccuracy)
    }
    
    /**
     * Reset configuration to factory defaults
     */
    fun resetToDefaults(): Boolean {
        return saveConfiguration(SensorConfiguration())
    }
    
    /**
     * Export configuration as JSON string
     */
    fun exportConfiguration(): String {
        return gson.toJson(currentConfig)
    }
    
    /**
     * Import configuration from JSON string
     */
    fun importConfiguration(configJson: String): Boolean {
        return try {
            val importedConfig = gson.fromJson(configJson, SensorConfiguration::class.java)
            saveConfiguration(importedConfig)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import configuration", e)
            false
        }
    }
}