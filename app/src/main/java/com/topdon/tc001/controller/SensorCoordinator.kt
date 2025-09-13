package com.topdon.tc001.controller

import android.content.Context
import android.util.Log
import com.topdon.tc001.sensors.*
import com.topdon.tc001.permissions.PermissionController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Enhanced sensor coordinator for Phase 5 implementation
 * Manages synchronized multi-sensor recording with improved error handling and recovery
 */
class SensorCoordinator(
    private val context: Context
) {
    companion object {
        private const val TAG = "SensorCoordinator"
        private const val SYNC_TIMEOUT_MS = 5000L
        private const val COORDINATION_DELAY_MS = 100L
        private const val HEARTBEAT_INTERVAL_MS = 1000L
    }
    
    // Coordinator state
    private val _isCoordinating = AtomicBoolean(false)
    val isCoordinating: Boolean get() = _isCoordinating.get()
    
    private val _sessionId = AtomicLong(0)
    private var currentSessionDirectory: File? = null
    
    // Sensor management
    private val availableSensors = ConcurrentHashMap<String, SensorInfo>()
    private val activeSensors = ConcurrentHashMap<String, SensorRecorder>()
    
    // Coordination scope
    private val coordinatorScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var heartbeatJob: Job? = null
    private var coordinationJob: Job? = null
    
    // Status flows
    private val _coordinationStatus = MutableStateFlow(CoordinationStatus.IDLE)
    val coordinationStatus: StateFlow<CoordinationStatus> = _coordinationStatus.asStateFlow()
    
    private val _sensorStatuses = MutableSharedFlow<Map<String, SensorStatus>>()
    val sensorStatuses: SharedFlow<Map<String, SensorStatus>> = _sensorStatuses.asSharedFlow()
    
    private val _coordinationEvents = MutableSharedFlow<CoordinationEvent>()
    val coordinationEvents: SharedFlow<CoordinationEvent> = _coordinationEvents.asSharedFlow()
    
    data class SensorInfo(
        val sensorId: String,
        val sensorType: String,
        val isAvailable: Boolean,
        val hasPermissions: Boolean,
        val initializationError: String? = null
    )
    
    data class SensorStatus(
        val sensorId: String,
        val isRecording: Boolean,
        val isHealthy: Boolean,
        val lastHeartbeat: Long,
        val errorMessage: String? = null
    )
    
    enum class CoordinationStatus {
        IDLE,
        INITIALIZING,
        READY,
        STARTING,
        RECORDING,
        STOPPING,
        ERROR
    }
    
    sealed class CoordinationEvent {
        object InitializationStarted : CoordinationEvent()
        data class SensorInitialized(val sensorId: String, val success: Boolean) : CoordinationEvent()
        object AllSensorsReady : CoordinationEvent()
        object RecordingStarted : CoordinationEvent()
        object RecordingPaused : CoordinationEvent()
        object RecordingResumed : CoordinationEvent()
        object RecordingStopped : CoordinationEvent()
        data class SensorError(val sensorId: String, val error: String) : CoordinationEvent()
        data class SyncMarkerDistributed(val timestamp: Long, val eventType: String) : CoordinationEvent()
    }
    
    /**
     * Phase 5: Enhanced sensor initialization with permission checking
     */
    suspend fun initializeAllSensors(): Boolean = withContext(Dispatchers.IO) {
        try {
            _coordinationStatus.value = CoordinationStatus.INITIALIZING
            _coordinationEvents.emit(CoordinationEvent.InitializationStarted)
            
            Log.i(TAG, "Starting comprehensive sensor initialization")
            
            // Check permissions first
            val permissionController = PermissionController.getInstance()
            if (!permissionController.areAllPermissionsGranted(context)) {
                Log.w(TAG, "Not all permissions granted - some sensors may not initialize")
            }
            
            // Discover available sensors
            discoverAvailableSensors()
            
            // Initialize sensors in parallel with timeout
            val initResults = initializeSensorsInParallel()
            
            // Evaluate results
            val successfulSensors = initResults.count { it.value }
            val totalSensors = initResults.size
            
            Log.i(TAG, "Sensor initialization complete: $successfulSensors/$totalSensors successful")
            
            if (successfulSensors > 0) {
                _coordinationStatus.value = CoordinationStatus.READY
                _coordinationEvents.emit(CoordinationEvent.AllSensorsReady)
                startHeartbeatMonitoring()
                return@withContext true
            } else {
                _coordinationStatus.value = CoordinationStatus.ERROR
                return@withContext false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize sensors", e)
            _coordinationStatus.value = CoordinationStatus.ERROR
            _coordinationEvents.emit(CoordinationEvent.SensorError("coordinator", e.message ?: "Unknown error"))
            return@withContext false
        }
    }
    
    /**
     * Discover available sensors and check their prerequisites
     */
    private suspend fun discoverAvailableSensors() {
        try {
            Log.d(TAG, "Discovering available sensors")
            
            // Check thermal camera availability
            val thermalAvailable = checkThermalCameraAvailability()
            availableSensors["thermal_camera_1"] = SensorInfo(
                sensorId = "thermal_camera_1",
                sensorType = "Thermal Camera",
                isAvailable = thermalAvailable.first,
                hasPermissions = thermalAvailable.second,
                initializationError = if (!thermalAvailable.first) "Device not detected" else null
            )
            
            // Check GSR sensor availability  
            val gsrAvailable = checkGSRSensorAvailability()
            availableSensors["gsr_shimmer_1"] = SensorInfo(
                sensorId = "gsr_shimmer_1", 
                sensorType = "GSR Sensor",
                isAvailable = gsrAvailable.first,
                hasPermissions = gsrAvailable.second,
                initializationError = if (!gsrAvailable.first) "Shimmer device not paired" else null
            )
            
            // RGB camera availability (always available on phones)
            availableSensors["rgb_camera_1"] = SensorInfo(
                sensorId = "rgb_camera_1",
                sensorType = "RGB Camera", 
                isAvailable = true,
                hasPermissions = PermissionController.getInstance().arePermissionsGranted(
                    context, 
                    PermissionController.CAMERA_PERMISSIONS
                ),
                initializationError = null
            )
            
            Log.i(TAG, "Discovered ${availableSensors.size} sensors: ${availableSensors.keys}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error discovering sensors", e)
        }
    }
    
    /**
     * Check thermal camera availability and permissions
     */
    private suspend fun checkThermalCameraAvailability(): Pair<Boolean, Boolean> {
        return try {
            val permissionController = PermissionController.getInstance()
            val thermalCameras = permissionController.discoverThermalCameras(context)
            
            val hasDevice = thermalCameras.isNotEmpty()
            val hasPermissions = thermalCameras.any { it.hasPermission }
            
            Log.d(TAG, "Thermal camera check: device=$hasDevice, permissions=$hasPermissions")
            Pair(hasDevice, hasPermissions)
            
        } catch (e: Exception) {
            Log.w(TAG, "Error checking thermal camera availability", e)
            Pair(false, false)
        }
    }
    
    /**
     * Check GSR sensor availability and permissions
     */
    private suspend fun checkGSRSensorAvailability(): Pair<Boolean, Boolean> {
        return try {
            val permissionController = PermissionController.getInstance()
            val hasBluetoothPermissions = permissionController.arePermissionsGranted(
                context,
                PermissionController.BLUETOOTH_PERMISSIONS
            )
            
            // For now, assume device is available if permissions are granted
            // In a real implementation, we'd scan for Shimmer devices
            Log.d(TAG, "GSR sensor check: permissions=$hasBluetoothPermissions")
            Pair(hasBluetoothPermissions, hasBluetoothPermissions)
            
        } catch (e: Exception) {
            Log.w(TAG, "Error checking GSR sensor availability", e) 
            Pair(false, false)
        }
    }
    
    /**
     * Initialize sensors in parallel with timeout and error handling
     */
    private suspend fun initializeSensorsInParallel(): Map<String, Boolean> {
        return withContext(Dispatchers.IO) {
            val initResults = mutableMapOf<String, Boolean>()
            
            // Create initialization jobs for available sensors
            val initJobs = availableSensors.values.mapNotNull { sensorInfo ->
                if (!sensorInfo.isAvailable || !sensorInfo.hasPermissions) {
                    Log.w(TAG, "Skipping ${sensorInfo.sensorId}: available=${sensorInfo.isAvailable}, permissions=${sensorInfo.hasPermissions}")
                    return@mapNotNull null
                }
                
                async {
                    try {
                        val success = withTimeout(SYNC_TIMEOUT_MS) {
                            initializeSensor(sensorInfo)
                        }
                        initResults[sensorInfo.sensorId] = success
                        _coordinationEvents.emit(CoordinationEvent.SensorInitialized(sensorInfo.sensorId, success))
                        success
                    } catch (e: TimeoutCancellationException) {
                        Log.w(TAG, "Timeout initializing ${sensorInfo.sensorId}")
                        initResults[sensorInfo.sensorId] = false
                        _coordinationEvents.emit(CoordinationEvent.SensorInitialized(sensorInfo.sensorId, false))
                        false
                    } catch (e: Exception) {
                        Log.e(TAG, "Error initializing ${sensorInfo.sensorId}", e)
                        initResults[sensorInfo.sensorId] = false
                        _coordinationEvents.emit(CoordinationEvent.SensorError(sensorInfo.sensorId, e.message ?: "Unknown error"))
                        false
                    }
                }
            }
            
            // Wait for all initialization jobs
            initJobs.awaitAll()
            
            initResults
        }
    }
    
    /**
     * Initialize a specific sensor with actual implementation
     */
    private suspend fun initializeSensor(sensorInfo: SensorInfo): Boolean {
        return try {
            Log.d(TAG, "Initializing sensor: ${sensorInfo.sensorId}")
            
            // Create and initialize the actual sensor recorder
            val sensorRecorder = createSensorRecorder(sensorInfo)
            if (sensorRecorder != null) {
                val success = sensorRecorder.initialize()
                if (success) {
                    activeSensors[sensorInfo.sensorId] = sensorRecorder
                    Log.i(TAG, "Sensor ${sensorInfo.sensorId} initialized successfully")
                    true
                } else {
                    Log.w(TAG, "Sensor ${sensorInfo.sensorId} initialization failed")
                    false
                }
            } else {
                Log.e(TAG, "Failed to create sensor recorder for ${sensorInfo.sensorId}")
                false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize sensor ${sensorInfo.sensorId}", e)
            false
        }
    }
    
    /**
     * Create the appropriate sensor recorder based on sensor info
     */
    private fun createSensorRecorder(sensorInfo: SensorInfo): SensorRecorder? {
        return try {
            when (sensorInfo.sensorId) {
                "thermal_camera_1" -> {
                    com.topdon.tc001.sensors.thermal.ThermalCameraRecorder(
                        context = context,
                        sensorId = sensorInfo.sensorId
                    )
                }
                "gsr_shimmer_1" -> {
                    com.topdon.tc001.sensors.gsr.GSRRecorder(
                        context = context,
                        sensorId = sensorInfo.sensorId
                    )
                }
                "rgb_camera_1" -> {
                    com.topdon.tc001.sensors.rgb.RgbCameraRecorder(
                        context = context,
                        sensorId = sensorInfo.sensorId
                    )
                }
                else -> {
                    Log.w(TAG, "Unknown sensor type: ${sensorInfo.sensorId}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sensor recorder for ${sensorInfo.sensorId}", e)
            null
        }
    }
    
    /**
     * Start heartbeat monitoring for all active sensors
     */
    private fun startHeartbeatMonitoring() {
        heartbeatJob?.cancel()
        heartbeatJob = coordinatorScope.launch {
            while (isActive) {
                try {
                    val statuses = collectSensorStatuses()
                    _sensorStatuses.emit(statuses)
                    
                    // Check for unhealthy sensors
                    val unhealthySensors = statuses.values.filter { !it.isHealthy }
                    if (unhealthySensors.isNotEmpty()) {
                        Log.w(TAG, "Unhealthy sensors detected: ${unhealthySensors.map { it.sensorId }}")
                    }
                    
                    delay(HEARTBEAT_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in heartbeat monitoring", e)
                }
            }
        }
    }
    
    /**
     * Collect current status from all sensors
     */
    private suspend fun collectSensorStatuses(): Map<String, SensorStatus> {
        val statuses = mutableMapOf<String, SensorStatus>()
        val currentTime = System.currentTimeMillis()
        
        availableSensors.values.forEach { sensorInfo ->
            statuses[sensorInfo.sensorId] = SensorStatus(
                sensorId = sensorInfo.sensorId,
                isRecording = false, // Would check actual sensor status
                isHealthy = sensorInfo.isAvailable && sensorInfo.hasPermissions,
                lastHeartbeat = currentTime,
                errorMessage = sensorInfo.initializationError
            )
        }
        
        return statuses
    }
    
    /**
     * Start coordinated recording across all sensors
     */
    suspend fun startCoordinatedRecording(sessionDirectory: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (_coordinationStatus.value != CoordinationStatus.READY) {
                    Log.w(TAG, "Cannot start recording, coordinator not ready")
                    return@withContext false
                }
                
                _coordinationStatus.value = CoordinationStatus.STARTING
                _isCoordinating.set(true)
                currentSessionDirectory = sessionDirectory
                
                Log.i(TAG, "Starting coordinated recording to: ${sessionDirectory.absolutePath}")
                
                // Create session directories for each sensor
                val sessionDirectoryPath = sessionDirectory.absolutePath
                
                // Start all sensors simultaneously
                val startResults = startAllSensorsInParallel(sessionDirectoryPath)
                
                // Check results
                val successfulStarts = startResults.count { it.value }
                val totalSensors = startResults.size
                
                Log.i(TAG, "Coordinated recording start complete: $successfulStarts/$totalSensors sensors started")
                
                if (successfulStarts > 0) {
                    _coordinationStatus.value = CoordinationStatus.RECORDING
                    _coordinationEvents.emit(CoordinationEvent.RecordingStarted)
                    Log.i(TAG, "Coordinated recording started successfully")
                    return@withContext true
                } else {
                    _coordinationStatus.value = CoordinationStatus.ERROR
                    _isCoordinating.set(false)
                    Log.e(TAG, "Failed to start any sensors")
                    return@withContext false
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start coordinated recording", e)
                _coordinationStatus.value = CoordinationStatus.ERROR
                _isCoordinating.set(false)
                return@withContext false
            }
        }
    }
    
    /**
     * Start all active sensors in parallel
     */
    private suspend fun startAllSensorsInParallel(sessionDirectory: String): Map<String, Boolean> {
        return withContext(Dispatchers.IO) {
            val startResults = mutableMapOf<String, Boolean>()
            
            // Create start jobs for all active sensors
            val startJobs = activeSensors.values.map { sensor ->
                async {
                    try {
                        val success = withTimeout(SYNC_TIMEOUT_MS) {
                            sensor.startRecording(sessionDirectory)
                        }
                        startResults[sensor.sensorId] = success
                        Log.d(TAG, "Sensor ${sensor.sensorId} start result: $success")
                        success
                    } catch (e: TimeoutCancellationException) {
                        Log.w(TAG, "Timeout starting sensor ${sensor.sensorId}")
                        startResults[sensor.sensorId] = false
                        false
                    } catch (e: Exception) {
                        Log.e(TAG, "Error starting sensor ${sensor.sensorId}", e)
                        startResults[sensor.sensorId] = false
                        false
                    }
                }
            }
            
            // Wait for all start operations
            startJobs.awaitAll()
            
            startResults
        }
    }
    
    /**
     * Stop coordinated recording
     */
    suspend fun stopCoordinatedRecording(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (_coordinationStatus.value != CoordinationStatus.RECORDING) {
                    Log.w(TAG, "Cannot stop recording, not currently recording")
                    return@withContext false
                }
                
                _coordinationStatus.value = CoordinationStatus.STOPPING
                
                Log.i(TAG, "Stopping coordinated recording")
                
                // Stop all sensors simultaneously
                val stopResults = stopAllSensorsInParallel()
                
                // Check results
                val successfulStops = stopResults.count { it.value }
                val totalSensors = stopResults.size
                
                Log.i(TAG, "Coordinated recording stop complete: $successfulStops/$totalSensors sensors stopped")
                
                _coordinationStatus.value = CoordinationStatus.READY
                _isCoordinating.set(false)
                _coordinationEvents.emit(CoordinationEvent.RecordingStopped)
                
                Log.i(TAG, "Coordinated recording stopped successfully")
                return@withContext true
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop coordinated recording", e)
                _coordinationStatus.value = CoordinationStatus.ERROR
                return@withContext false
            }
        }
    }
    
    /**
     * Stop all active sensors in parallel
     */
    private suspend fun stopAllSensorsInParallel(): Map<String, Boolean> {
        return withContext(Dispatchers.IO) {
            val stopResults = mutableMapOf<String, Boolean>()
            
            // Create stop jobs for all active sensors
            val stopJobs = activeSensors.values.map { sensor ->
                async {
                    try {
                        val success = withTimeout(SYNC_TIMEOUT_MS) {
                            sensor.stopRecording()
                        }
                        stopResults[sensor.sensorId] = success
                        Log.d(TAG, "Sensor ${sensor.sensorId} stop result: $success")
                        success
                    } catch (e: TimeoutCancellationException) {
                        Log.w(TAG, "Timeout stopping sensor ${sensor.sensorId}")
                        stopResults[sensor.sensorId] = false
                        false
                    } catch (e: Exception) {
                        Log.e(TAG, "Error stopping sensor ${sensor.sensorId}", e)
                        stopResults[sensor.sensorId] = false
                        false
                    }
                }
            }
            
            // Wait for all stop operations
            stopJobs.awaitAll()
            
            stopResults
        }
    }
    
    /**
     * Distribute sync marker to all active sensors
     */
    suspend fun distributeSyncMarker(eventType: String, metadata: String = ""): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = System.nanoTime()
                
                Log.d(TAG, "Distributing sync marker: $eventType at $timestamp")
                
                // Create metadata map
                val metadataMap = if (metadata.isNotEmpty()) {
                    mapOf("event_metadata" to metadata, "distributor" to "SensorCoordinator")
                } else {
                    mapOf("distributor" to "SensorCoordinator")
                }
                
                // Send sync markers to all active sensors in parallel
                val syncJobs = activeSensors.values.map { sensor ->
                    async {
                        try {
                            sensor.addSyncMarker(eventType, timestamp, metadataMap)
                            Log.d(TAG, "Sync marker sent to ${sensor.sensorId}")
                            true
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to send sync marker to ${sensor.sensorId}", e)
                            false
                        }
                    }
                }
                
                // Wait for all sync operations
                val results = syncJobs.awaitAll()
                val successCount = results.count { it }
                
                if (successCount > 0) {
                    _coordinationEvents.emit(CoordinationEvent.SyncMarkerDistributed(timestamp, eventType))
                    Log.d(TAG, "Sync marker distributed to $successCount/${activeSensors.size} sensors successfully")
                    return@withContext true
                } else {
                    Log.w(TAG, "Failed to distribute sync marker to any sensors")
                    return@withContext false
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to distribute sync marker", e)
                return@withContext false
            }
        }
    }
    
    /**
     * Cleanup coordinator resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up sensor coordinator")
        
        heartbeatJob?.cancel()
        coordinationJob?.cancel()
        coordinatorScope.cancel()
        
        _isCoordinating.set(false)
        _coordinationStatus.value = CoordinationStatus.IDLE
        
        availableSensors.clear()
        activeSensors.clear()
    }
}