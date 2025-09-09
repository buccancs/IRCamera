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
            
            // Check camera permission
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                emitError(ErrorType.PERMISSION_DENIED, "Camera permission not granted")
                return@withContext false
            }
            
            // Initialize CameraX
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProvider = cameraProviderFuture.get()
            
            // Configure video capture
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.FHD)) // 1080p
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
            
            // Configure image capture
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(targetImageResolution)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setJpegQuality(95) // High quality for analysis
                .build()
            
            Log.i(TAG, "RGB camera initialized successfully")
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
            
            // Start video recording
            val mediaStoreOutput = FileOutputOptions.Builder(videoFile!!).build()
            recording = videoCapture?.output
                ?.prepareRecording(context, mediaStoreOutput)
                ?.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    handleVideoRecordEvent(recordEvent)
                }
            
            // Bind camera with both video and image capture
            bindCamera()
            
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

    private suspend fun bindCamera() = withContext(Dispatchers.Main) {
        try {
            cameraProvider?.unbindAll()
            
            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            // Create preview for monitoring (optional)
            val preview = Preview.Builder()
                .setTargetResolution(Size(1280, 720))
                .build()
            
            // Bind use cases
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                videoCapture,
                imageCapture
            )
            
            // Configure auto-focus and exposure
            camera?.cameraControl?.enableTorch(false) // Ensure flash is off initially
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to bind camera", e)
            emitError(ErrorType.INITIALIZATION_FAILED, "Camera binding failed: ${e.message}")
        }
    }

    private fun startImageCapture() {
        imageCapturJob = recordingScope.launch {
            while (_isRecording.get() && isActive) {
                captureAnalysisFrame()
                delay(IMAGE_CAPTURE_INTERVAL_MS)
            }
        }
    }

    private suspend fun captureAnalysisFrame() {
        try {
            val timestamp = System.nanoTime()
            val frameNumber = frameCount.incrementAndGet()
            val imageFile = File(imagesDirectory, "frame_${frameNumber}_${timestamp}.jpg")
            
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile)
                .build()
            
            val imageCapture = this.imageCapture ?: return
            
            withContext(Dispatchers.Main) {
                imageCapture.takePicture(
                    outputFileOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            Log.d(TAG, "Analysis frame saved: ${imageFile.name}")
                            recordingScope.launch { emitStatus() }
                        }
                        
                        override fun onError(exception: ImageCaptureException) {
                            Log.w(TAG, "Failed to capture analysis frame", exception)
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