package com.topdon.tc001.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat
import com.topdon.gsr.util.TimeUtil
import java.io.File
import java.io.IOException
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * RGB Camera Recorder with Samsung-style recording options
 * Supports front/back camera switching, multiple resolutions, and quality settings
 */
class RGBCameraRecorder(
    private val context: Context,
    private val textureView: TextureView
) {
    companion object {
        private const val TAG = "RGBCameraRecorder"
        private const val MAX_PREVIEW_WIDTH = 1920
        private const val MAX_PREVIEW_HEIGHT = 1080
    }

    // Camera recording settings
    data class RecordingSettings(
        val resolution: VideoResolution = VideoResolution.HD_1080P,
        val frameRate: Int = 30,
        val bitRate: Int = 8_000_000,
        val enableStabilization: Boolean = true,
        val enableFlash: Boolean = false,
        val audioEnabled: Boolean = true
    )

    // Video resolution options (Samsung camera style)
    enum class VideoResolution(val width: Int, val height: Int, val displayName: String) {
        UHD_4K(3840, 2160, "4K UHD"),
        HD_1080P(1920, 1080, "Full HD"),
        HD_720P(1280, 720, "HD"),
        SD_480P(720, 480, "SD")
    }

    // Camera facing options
    enum class CameraFacing(val displayName: String) {
        BACK("Back Camera"),
        FRONT("Front Camera")
    }

    // Recording state
    private var isRecording = false
    private var isPaused = false
    
    // Camera components
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var mediaRecorder: MediaRecorder? = null
    private var previewRequestBuilder: CaptureRequest.Builder? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private val cameraOpenCloseLock = Semaphore(1)
    
    // Camera settings
    private var currentCameraFacing = CameraFacing.BACK
    private var recordingSettings = RecordingSettings()
    private var videoSize = Size(1920, 1080)
    private var previewSize = Size(1920, 1080)
    
    // File management
    private var currentVideoFile: File? = null
    private var sessionId: String? = null
    
    // Callbacks
    var onRecordingStarted: (() -> Unit)? = null
    var onRecordingStopped: ((File?) -> Unit)? = null
    var onCameraSwitched: ((CameraFacing) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    /**
     * Initialize camera recorder
     */
    fun initialize() {
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera()
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    /**
     * Start RGB video recording with synchronized timing
     */
    fun startRecording(sessionId: String? = null): Boolean {
        if (isRecording) {
            Log.w(TAG, "Already recording")
            return false
        }

        if (!hasRequiredPermissions()) {
            onError?.invoke("Camera and audio permissions required")
            return false
        }

        try {
            this.sessionId = sessionId ?: TimeUtil.generateSessionId("RGB_Video")
            currentVideoFile = createVideoFile()
            
            // Initialize MediaRecorder with Samsung-style settings
            setupMediaRecorder()
            
            // Create capture session for recording
            createCameraRecordingSession()
            
            isRecording = true
            onRecordingStarted?.invoke()
            
            Log.i(TAG, "RGB video recording started: ${currentVideoFile?.absolutePath}")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            onError?.invoke("Failed to start recording: ${e.message}")
            return false
        }
    }

    /**
     * Stop RGB video recording
     */
    fun stopRecording(): File? {
        if (!isRecording) {
            Log.w(TAG, "Not currently recording")
            return null
        }

        try {
            isRecording = false
            isPaused = false
            
            mediaRecorder?.apply {
                stop()
                reset()
            }
            
            // Restart preview session
            createCameraPreviewSession()
            
            val videoFile = currentVideoFile
            currentVideoFile = null
            sessionId = null
            
            onRecordingStopped?.invoke(videoFile)
            
            Log.i(TAG, "RGB video recording stopped: ${videoFile?.absolutePath}")
            return videoFile
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
            onError?.invoke("Failed to stop recording: ${e.message}")
            return null
        }
    }

    /**
     * Pause/resume recording (Android N+)
     */
    @SuppressLint("NewApi")
    fun pauseRecording() {
        if (!isRecording || isPaused || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }
        
        try {
            mediaRecorder?.pause()
            isPaused = true
            Log.i(TAG, "Recording paused")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pause recording", e)
        }
    }

    @SuppressLint("NewApi")
    fun resumeRecording() {
        if (!isRecording || !isPaused || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }
        
        try {
            mediaRecorder?.resume()
            isPaused = false
            Log.i(TAG, "Recording resumed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resume recording", e)
        }
    }

    /**
     * Switch between front and back camera
     */
    fun switchCamera(): CameraFacing {
        closeCamera()
        currentCameraFacing = if (currentCameraFacing == CameraFacing.BACK) {
            CameraFacing.FRONT
        } else {
            CameraFacing.BACK
        }
        
        openCamera()
        onCameraSwitched?.invoke(currentCameraFacing)
        
        Log.i(TAG, "Switched to ${currentCameraFacing.displayName}")
        return currentCameraFacing
    }

    /**
     * Update recording settings
     */
    fun updateSettings(settings: RecordingSettings) {
        recordingSettings = settings
        videoSize = Size(settings.resolution.width, settings.resolution.height)
        
        // Update preview if not recording
        if (!isRecording) {
            closeCamera()
            openCamera()
        }
        
        Log.i(TAG, "Updated recording settings: ${settings.resolution.displayName}")
    }

    /**
     * Get available camera facing options
     */
    fun getAvailableCameraFacing(): List<CameraFacing> {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val availableFacing = mutableListOf<CameraFacing>()
        
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                
                when (facing) {
                    CameraCharacteristics.LENS_FACING_BACK -> {
                        if (!availableFacing.contains(CameraFacing.BACK)) {
                            availableFacing.add(CameraFacing.BACK)
                        }
                    }
                    CameraCharacteristics.LENS_FACING_FRONT -> {
                        if (!availableFacing.contains(CameraFacing.FRONT)) {
                            availableFacing.add(CameraFacing.FRONT)
                        }
                    }
                }
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error accessing camera", e)
        }
        
        return availableFacing
    }

    /**
     * Get supported video resolutions for current camera
     */
    fun getSupportedResolutions(): List<VideoResolution> {
        return VideoResolution.values().toList()
    }

    /**
     * Enable/disable flash torch
     */
    fun setFlashEnabled(enabled: Boolean) {
        recordingSettings = recordingSettings.copy(enableFlash = enabled)
        
        previewRequestBuilder?.let { builder ->
            try {
                builder.set(
                    CaptureRequest.FLASH_MODE,
                    if (enabled) CaptureRequest.FLASH_MODE_TORCH else CaptureRequest.FLASH_MODE_OFF
                )
                captureSession?.setRepeatingRequest(builder.build(), null, backgroundHandler)
            } catch (e: CameraAccessException) {
                Log.e(TAG, "Failed to set flash", e)
            }
        }
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        if (isRecording) {
            stopRecording()
        }
        closeCamera()
        stopBackgroundThread()
    }

    // Private implementation methods
    
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCamera()
        }
        
        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            // Handle size changes if needed
        }
        
        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            closeCamera()
            return true
        }
        
        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {
            // Frame updated - could trigger sync events here
        }
    }

    private fun openCamera() {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            
            val cameraId = getCameraId(currentCameraFacing)
            val characteristics = manager.getCameraCharacteristics(cameraId)
            
            // Setup sizes
            setupCameraSizes(characteristics)
            
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                onError?.invoke("Camera permission not granted")
                return
            }
            
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
            
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to open camera", e)
            onError?.invoke("Failed to open camera: ${e.message}")
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraOpenCloseLock.release()
            camera.close()
            cameraDevice = null
            onError?.invoke("Camera error: $error")
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val surface = Surface(textureView.surfaceTexture)
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder!!.addTarget(surface)

            cameraDevice!!.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return
                        
                        captureSession = session
                        try {
                            previewRequestBuilder!!.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            
                            session.setRepeatingRequest(
                                previewRequestBuilder!!.build(),
                                null,
                                backgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, "Failed to start preview", e)
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        onError?.invoke("Failed to configure camera preview")
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to create preview session", e)
        }
    }

    private fun createCameraRecordingSession() {
        try {
            val previewSurface = Surface(textureView.surfaceTexture)
            val recorderSurface = mediaRecorder!!.surface

            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            previewRequestBuilder!!.addTarget(previewSurface)
            previewRequestBuilder!!.addTarget(recorderSurface)

            cameraDevice!!.createCaptureSession(
                listOf(previewSurface, recorderSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        updatePreview()
                        
                        try {
                            mediaRecorder!!.start()
                        } catch (e: IllegalStateException) {
                            Log.e(TAG, "Failed to start MediaRecorder", e)
                            onError?.invoke("Failed to start video recording")
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        onError?.invoke("Failed to configure recording session")
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to create recording session", e)
        }
    }

    private fun updatePreview() {
        if (cameraDevice == null) return

        try {
            previewRequestBuilder!!.set(
                CaptureRequest.CONTROL_MODE,
                CameraMetadata.CONTROL_MODE_AUTO
            )
            
            // Enable stabilization if supported and requested
            if (recordingSettings.enableStabilization) {
                previewRequestBuilder!!.set(
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON
                )
            }
            
            captureSession!!.setRepeatingRequest(
                previewRequestBuilder!!.build(),
                null,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to update preview", e)
        }
    }

    private fun setupMediaRecorder() {
        mediaRecorder = MediaRecorder().apply {
            if (recordingSettings.audioEnabled) {
                setAudioSource(MediaRecorder.AudioSource.MIC)
            }
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(currentVideoFile!!.absolutePath)
            setVideoEncodingBitRate(recordingSettings.bitRate)
            setVideoFrameRate(recordingSettings.frameRate)
            setVideoSize(videoSize.width, videoSize.height)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            
            if (recordingSettings.audioEnabled) {
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
            }
            
            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to prepare MediaRecorder", e)
                throw e
            }
        }
    }

    private fun getCameraId(facing: CameraFacing): String {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                
                val targetFacing = when (facing) {
                    CameraFacing.BACK -> CameraCharacteristics.LENS_FACING_BACK
                    CameraFacing.FRONT -> CameraCharacteristics.LENS_FACING_FRONT
                }
                
                if (cameraFacing == targetFacing) {
                    return cameraId
                }
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error accessing camera", e)
        }
        
        return "0" // Fallback to first camera
    }

    private fun setupCameraSizes(characteristics: CameraCharacteristics) {
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: return
        
        // Find best video size for current resolution setting
        val videoSizes = map.getOutputSizes(MediaRecorder::class.java)
        videoSize = chooseOptimalSize(
            videoSizes,
            recordingSettings.resolution.width,
            recordingSettings.resolution.height
        )
        
        // Find best preview size
        val previewSizes = map.getOutputSizes(SurfaceTexture::class.java)
        previewSize = chooseOptimalSize(
            previewSizes,
            MAX_PREVIEW_WIDTH,
            MAX_PREVIEW_HEIGHT
        )
        
        textureView.surfaceTexture?.setDefaultBufferSize(previewSize.width, previewSize.height)
    }

    private fun chooseOptimalSize(choices: Array<Size>, targetWidth: Int, targetHeight: Int): Size {
        val bigEnough = mutableListOf<Size>()
        val notBigEnough = mutableListOf<Size>()
        
        for (option in choices) {
            if (option.width >= targetWidth && option.height >= targetHeight) {
                bigEnough.add(option)
            } else {
                notBigEnough.add(option)
            }
        }
        
        return when {
            bigEnough.isNotEmpty() -> bigEnough.minByOrNull { it.width * it.height } ?: choices[0]
            notBigEnough.isNotEmpty() -> notBigEnough.maxByOrNull { it.width * it.height } ?: choices[0]
            else -> choices[0]
        }
    }

    private fun createVideoFile(): File {
        val timestamp = TimeUtil.formatTimestamp(System.currentTimeMillis())
        val filename = "RGB_Video_${sessionId}_$timestamp.mp4"
        return File(context.getExternalFilesDir("RGB_Videos"), filename).apply {
            parentFile?.mkdirs()
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val cameraPermission = ActivityCompat.checkSelfPermission(
            context, 
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        val audioPermission = if (recordingSettings.audioEnabled) {
            ActivityCompat.checkSelfPermission(
                context, 
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        
        return cameraPermission && audioPermission
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, "Error stopping background thread", e)
        }
    }

    // Getters for current state
    fun isRecording() = isRecording
    fun isPaused() = isPaused
    fun getCurrentCameraFacing() = currentCameraFacing
    fun getCurrentSettings() = recordingSettings
    fun getCurrentVideoFile() = currentVideoFile
}