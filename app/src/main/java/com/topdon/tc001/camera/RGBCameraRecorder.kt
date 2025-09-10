package com.topdon.tc001.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.DngCreator
import android.media.Image
import android.media.ImageReader
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
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

    private fun cleanupOldCaptureResults() {
        val currentTime = System.nanoTime()
        val iterator = captureResultMap.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            val timestamp = entry.key

            if ((currentTime - timestamp) / 1_000_000 > captureResultTimeout) {
                iterator.remove()
                Log.d(TAG, "Cleaned up old capture result for timestamp: $timestamp")
            }
        }
    }

    private fun createRawImagesDirectory(): File {
        val timestamp = TimeUtil.formatTimestamp(System.currentTimeMillis())
        val dirName = "RAW_Images_${sessionId}_$timestamp"
        return File(context.getExternalFilesDir("RAW_Images"), dirName).apply {
            mkdirs()
        }
    }

    private fun chooseOptimalSize(
        choices: Array<Size>,
        targetWidth: Int,
        targetHeight: Int,
    ): Size {
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
        val cameraPermission =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED

        val audioPermission =
            if (recordingSettings.audioEnabled) {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO,
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

    fun isRecording() = isRecording

    fun isPaused() = isPaused

    fun isRawCaptureEnabled() = isRawCaptureEnabled

    fun getRawCaptureCount() = rawCaptureCount

    fun getCurrentCameraFacing() = currentCameraFacing

    fun getCurrentSettings() = recordingSettings

    fun getCurrentVideoFile() = currentVideoFile

    fun getRawImagesDirectory() = rawImagesDirectory
}
