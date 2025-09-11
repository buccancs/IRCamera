package com.topdon.tc001.sensors.rgb

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.media.MediaRecorder
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.topdon.tc001.sensors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * RGB Camera recorder implementing dual-stream capture as specified in FR5:
 * - 1080p MP4 video recording for playback
 * - High-resolution timestamped JPEG image capture for analysis
 * 
 * Uses CameraX for robust Android camera handling with lifecycle awareness.
 * 
 * Technical Requirements:
 * - Simultaneous video (1080p 30fps) and image (max resolution) capture
 * - Frame-accurate timestamps for temporal synchronization
 * - Storage optimized with JPEG compression
 * - Automatic exposure and focus control
 * 
 * @author IRCamera Android Sensor Node (Spoke)
 */
class RgbCameraRecorder(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    override val sensorId: String = "rgb_camera_1",
    private val targetVideoResolution: Size = Size(1920, 1080),
    private val targetImageResolution: Size = Size(4032, 3024), // Max resolution
    private val videoFrameRate: Int = 30
) : SensorRecorder {

    companion object {
        private const val TAG = "RgbCameraRecorder"
        private const val VIDEO_FILENAME = "rgb_video.mp4"
        private const val IMAGES_SUBDIRECTORY = "rgb_images"
        private const val IMAGE_CAPTURE_INTERVAL_MS = 100L // 10fps for analysis frames
    }

    override val sensorType: String = "RGB Camera"
    override val samplingRate: Double = videoFrameRate.toDouble()
    
    private var _isRecording = AtomicBoolean(false)
    override val isRecording: Boolean get() = _isRecording.get()

    // CameraX components
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var imageCapture: ImageCapture? = null
    private var recording: Recording? = null
    
    // Threading
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val recordingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Data flows
    private val _statusFlow = MutableSharedFlow<RecordingStatus>()
    private val _errorFlow = MutableSharedFlow<SensorError>()
    
    // Recording state
    private var sessionDirectory: String = ""
    private var videoFile: File? = null
    private var imagesDirectory: File? = null
    private var frameCount = AtomicLong(0)
    private var recordingStartTime: Long = 0
    private var imageCapturJob: Job? = null

    override suspend fun initialize(): Boolean = withContext(Dispatchers.Main) {
        try {
            Log.i(TAG, "Initializing RGB camera for sensor $sensorId")
            
            // Check camera permission - critical for Samsung devices
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Camera permission not granted - this is required for RGB recording")
                emitError(ErrorType.PERMISSION_DENIED, "Camera permission not granted. Please grant camera permission in app settings.")
                return@withContext false
            }
            
            // Additional permission checks for video recording
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Audio permission not granted - video will be recorded without audio")
            }
            
            // Initialize CameraX - handle potential provider initialization failures
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            try {
                cameraProvider = cameraProviderFuture.get()
                Log.d(TAG, "CameraX ProcessCameraProvider initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get CameraProvider - camera hardware may not be available", e)
                emitError(ErrorType.INITIALIZATION_FAILED, "Camera hardware initialization failed: ${e.message}")
                return@withContext false
            }
            
            if (cameraProvider == null) {
                Log.e(TAG, "CameraProvider is null after initialization")
                emitError(ErrorType.INITIALIZATION_FAILED, "Camera provider is not available")
                return@withContext false
            }
            
            // Configure video capture with Samsung-compatible settings
            try {
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.FHD)) // 1080p
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)
                Log.d(TAG, "Video capture configured successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to configure video capture", e)
                emitError(ErrorType.INITIALIZATION_FAILED, "Video capture configuration failed: ${e.message}")
                return@withContext false
            }
            
            // Configure image capture with conservative settings for Samsung compatibility
            try {
                imageCapture = ImageCapture.Builder()
                    .setTargetResolution(targetImageResolution)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setJpegQuality(95) // High quality for analysis
                    .build()
                Log.d(TAG, "Image capture configured successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to configure image capture", e)
                emitError(ErrorType.INITIALIZATION_FAILED, "Image capture configuration failed: ${e.message}")
                return@withContext false
            }
            
            Log.i(TAG, "RGB camera initialized successfully for $sensorId")
            emitStatus()
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize RGB camera", e)
            emitError(ErrorType.INITIALIZATION_FAILED, "Camera initialization failed: ${e.message}")
            return@withContext false
        }
    }

    override suspend fun startRecording(sessionDirectory: String): Boolean = withContext(Dispatchers.Main) {
        try {
            if (_isRecording.get()) {
                Log.w(TAG, "RGB camera already recording")
                return@withContext true
            }
            
            this@RgbCameraRecorder.sessionDirectory = sessionDirectory
            recordingStartTime = System.nanoTime()
            
            // Create output files
            videoFile = File(sessionDirectory, VIDEO_FILENAME)
            imagesDirectory = File(sessionDirectory, IMAGES_SUBDIRECTORY).apply { mkdirs() }
            
            // Bind camera with both video and image capture - critical step for Samsung devices
            val cameraBindSuccess = bindCamera()
            if (!cameraBindSuccess) {
                Log.e(TAG, "Failed to bind camera - cannot start recording")
                return@withContext false
            }
            
            // Validate camera binding was successful
            if (camera == null) {
                Log.e(TAG, "Camera is null after binding - recording cannot proceed")
                emitError(ErrorType.RECORDING_FAILED, "Camera binding validation failed")
                return@withContext false
            }
            
            // Start video recording - validate videoCapture is available
            val videoCapture = this@RgbCameraRecorder.videoCapture
            if (videoCapture == null) {
                Log.e(TAG, "VideoCapture is null - cannot start video recording")
                emitError(ErrorType.RECORDING_FAILED, "Video capture not configured")
                return@withContext false
            }
            
            try {
                val mediaStoreOutput = FileOutputOptions.Builder(videoFile!!).build()
                recording = videoCapture.output
                    .prepareRecording(context, mediaStoreOutput)
                    .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                        handleVideoRecordEvent(recordEvent)
                    }
                Log.d(TAG, "Video recording started successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start video recording", e)
                emitError(ErrorType.RECORDING_FAILED, "Video recording start failed: ${e.message}")
                return@withContext false
            }
            
            // Start periodic image capture
            startImageCapture()
            
            _isRecording.set(true)
            frameCount.set(0)
            
            Log.i(TAG, "RGB camera recording started")
            emitStatus()
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start RGB camera recording", e)
            emitError(ErrorType.RECORDING_FAILED, "Failed to start recording: ${e.message}")
            return@withContext false
        }
    }

    private suspend fun bindCamera(): Boolean = withContext(Dispatchers.Main) {
        try {
            Log.d(TAG, "Binding camera for RGB recording")
            
            // Validate prerequisites before binding
            val cameraProvider = this@RgbCameraRecorder.cameraProvider
            if (cameraProvider == null) {
                Log.e(TAG, "CameraProvider is null - cannot bind camera")
                emitError(ErrorType.INITIALIZATION_FAILED, "CameraProvider not initialized")
                return@withContext false
            }
            
            if (videoCapture == null || imageCapture == null) {
                Log.e(TAG, "Camera use cases not configured - cannot bind camera")
                emitError(ErrorType.INITIALIZATION_FAILED, "Camera use cases not configured")
                return@withContext false
            }
            
            // Unbind any existing use cases
            cameraProvider.unbindAll()
            Log.d(TAG, "Unbound all existing camera use cases")
            
            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            // Check if back camera is available
            if (!cameraProvider.hasCamera(cameraSelector)) {
                Log.e(TAG, "Back camera is not available on this device")
                emitError(ErrorType.INITIALIZATION_FAILED, "Back camera not available")
                return@withContext false
            }
            
            // For Samsung devices, try progressive binding to avoid concurrent use-case issues
            try {
                // First, try binding with just video and image capture (no preview to reduce load)
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    videoCapture!!,
                    imageCapture!!
                )
                Log.d(TAG, "Camera bound successfully without preview")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to bind with both video and image, trying video only", e)
                
                // If concurrent use-cases fail, try with video only (fallback for Samsung devices)
                try {
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        videoCapture!!
                    )
                    Log.w(TAG, "Camera bound with video only - image capture disabled for compatibility")
                    // Disable image capture for this session
                    imageCapture = null
                } catch (fallbackException: Exception) {
                    Log.e(TAG, "Failed to bind camera even with video only", fallbackException)
                    emitError(ErrorType.INITIALIZATION_FAILED, "Camera binding failed: ${fallbackException.message}")
                    return@withContext false
                }
            }
            
            val boundCamera = camera
            if (boundCamera == null) {
                Log.e(TAG, "Camera is null after successful binding - this shouldn't happen")
                emitError(ErrorType.INITIALIZATION_FAILED, "Camera binding returned null")
                return@withContext false
            }
            
            // Configure camera controls
            try {
                boundCamera.cameraControl.enableTorch(false) // Ensure flash is off initially
                Log.d(TAG, "Camera controls configured successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to configure camera controls, continuing anyway", e)
            }
            
            Log.i(TAG, "Camera bound successfully for RGB recording")
            return@withContext true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind camera", e)
            emitError(ErrorType.INITIALIZATION_FAILED, "Camera binding failed: ${e.message}")
            return@withContext false
        }
    }

    private fun startImageCapture() {
        // Check if image capture is available (may be disabled for Samsung compatibility)
        val imageCapture = this.imageCapture
        if (imageCapture == null) {
            Log.w(TAG, "Image capture disabled - running in video-only mode for device compatibility")
            return
        }
        
        imageCapturJob = recordingScope.launch {
            Log.d(TAG, "Starting periodic image capture at ${IMAGE_CAPTURE_INTERVAL_MS}ms intervals")
            while (_isRecording.get() && isActive) {
                captureAnalysisFrame()
                delay(IMAGE_CAPTURE_INTERVAL_MS)
            }
            Log.d(TAG, "Image capture stopped")
        }
    }

    private suspend fun captureAnalysisFrame() {
        try {
            // Validate image capture is available
            val imageCapture = this.imageCapture
            if (imageCapture == null) {
                Log.w(TAG, "Image capture not available - skipping frame capture")
                return
            }
            
            // Validate images directory exists
            val imagesDir = this.imagesDirectory
            if (imagesDir == null || !imagesDir.exists()) {
                Log.w(TAG, "Images directory not available - skipping frame capture")
                return
            }
            
            val timestamp = System.nanoTime()
            val frameNumber = frameCount.incrementAndGet()
            val imageFile = File(imagesDir, "frame_${frameNumber}_${timestamp}.jpg")
            
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile)
                .build()
            
            withContext(Dispatchers.Main) {
                imageCapture.takePicture(
                    outputFileOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            Log.d(TAG, "Analysis frame saved: ${imageFile.name} (${frameNumber})")
                            recordingScope.launch { emitStatus() }
                        }
                        
                        override fun onError(exception: ImageCaptureException) {
                            Log.w(TAG, "Failed to capture analysis frame ${frameNumber}: ${exception.message}", exception)
                        }
                    }
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Image capture error", e)
        }
    }

    private fun handleVideoRecordEvent(recordEvent: VideoRecordEvent) {
        when (recordEvent) {
            is VideoRecordEvent.Start -> {
                Log.i(TAG, "Video recording started")
            }
            is VideoRecordEvent.Finalize -> {
                if (recordEvent.hasError()) {
                    Log.e(TAG, "Video recording failed: ${recordEvent.cause}")
                    recordingScope.launch {
                        emitError(ErrorType.RECORDING_FAILED, "Video recording error: ${recordEvent.cause?.message}")
                    }
                } else {
                    Log.i(TAG, "Video recording completed successfully")
                }
            }
            is VideoRecordEvent.Status -> {
                // Update recording statistics
                recordingScope.launch { emitStatus() }
            }
        }
    }

    override suspend fun stopRecording(): Boolean {
        try {
            if (!_isRecording.get()) {
                Log.w(TAG, "RGB camera not recording")
                return true
            }
            
            // Stop image capture
            imageCapturJob?.cancel()
            
            // Stop video recording
            recording?.stop()
            recording = null
            
            _isRecording.set(false)
            
            Log.i(TAG, "RGB camera recording stopped")
            emitStatus()
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop RGB camera recording", e)
            emitError(ErrorType.RECORDING_FAILED, "Failed to stop recording: ${e.message}")
            return false
        }
    }

    override suspend fun addSyncMarker(markerType: String, timestampNs: Long, metadata: Map<String, String>) {
        try {
            // Create sync marker file in images directory
            val syncFile = File(imagesDirectory, "sync_${markerType}_${timestampNs}.txt")
            syncFile.writeText("marker_type=$markerType\ntimestamp_ns=$timestampNs\n" + 
                metadata.map { "${it.key}=${it.value}" }.joinToString("\n"))
            
            Log.i(TAG, "Sync marker added: $markerType at $timestampNs")
            
        } catch (e: Exception) {
            Log.w(TAG, "Failed to add sync marker", e)
            emitError(ErrorType.SYNC_FAILED, "Sync marker failed: ${e.message}")
        }
    }

    override suspend fun cleanup() {
        try {
            if (_isRecording.get()) {
                stopRecording()
            }
            
            cameraProvider?.unbindAll()
            cameraExecutor.shutdown()
            recordingScope.cancel()
            
            Log.i(TAG, "RGB camera cleaned up")
            
        } catch (e: Exception) {
            Log.e(TAG, "Cleanup failed", e)
        }
    }

    override fun getStatusFlow(): Flow<RecordingStatus> = _statusFlow.asSharedFlow()
    override fun getErrorFlow(): Flow<SensorError> = _errorFlow.asSharedFlow()

    override fun getRecordingStats(): com.topdon.tc001.sensors.RecordingStats {
        val currentTime = System.nanoTime()
        val sessionDuration = if (recordingStartTime > 0) (currentTime - recordingStartTime) / 1_000_000 else 0L
        
        return com.topdon.tc001.sensors.RecordingStats(
            sensorId = sensorId,
            sensorType = sensorType,
            sessionDurationMs = sessionDuration,
            totalSamplesRecorded = frameCount.get(),
            averageDataRate = if (sessionDuration > 0) frameCount.get() * 1000.0 / sessionDuration else 0.0,
            droppedSamples = 0L, // CameraX handles frame drops internally
            storageUsedMB = calculateStorageUsed(),
            syncMarkersCount = getSyncMarkerCount(),
            lastSampleTimestampNs = currentTime
        )
    }

    private fun calculateStorageUsed(): Double {
        val videoSize = videoFile?.length() ?: 0L
        val imagesSize = imagesDirectory?.listFiles()?.sumOf { it.length() } ?: 0L
        return (videoSize + imagesSize) / (1024.0 * 1024.0)
    }

    private fun getSyncMarkerCount(): Int {
        return imagesDirectory?.listFiles { _, name -> name.startsWith("sync_") }?.size ?: 0
    }

    private suspend fun emitStatus() {
        val status = RecordingStatus(
            sensorId = sensorId,
            sensorType = sensorType,
            isRecording = _isRecording.get(),
            samplesRecorded = frameCount.get(),
            currentDataRate = samplingRate,
            storageUsedMB = calculateStorageUsed(),
            timestampNs = System.nanoTime()
        )
        _statusFlow.emit(status)
    }

    private suspend fun emitError(errorType: ErrorType, message: String, isRecoverable: Boolean = true) {
        val error = SensorError(
            sensorId = sensorId,
            sensorType = sensorType,
            errorType = errorType,
            errorMessage = message,
            timestampNs = System.nanoTime(),
            isRecoverable = isRecoverable
        )
        _errorFlow.emit(error)
    }
}