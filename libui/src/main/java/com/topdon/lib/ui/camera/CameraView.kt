package com.topdon.lib.ui.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.ScaleGestureDetector
import android.widget.LinearLayout
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.topdon.lib.core.utils.ScreenUtil
import java.nio.ByteBuffer
import java.util.Collections
import kotlin.concurrent.thread

class CameraView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener {

    private fun setUpCamera(
        width: Int,
        height: Int,
    ) {
        // viewHandler
        mCameraHandler = Handler(Looper.getMainLooper())
        // view
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // viewAllview
            for (cameraId in cameraManager.cameraIdList) {
                // view
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                // view
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                // view
                if (null != facing && CameraCharacteristics.LENS_FACING_FRONT == facing) continue
                // viewStreamConfigurationMap，viewAllview
                val map =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // viewTextureViewviewSettingsview
                mPreviewSize =
                    getOptimalSize(
                        map.getOutputSizes(SurfaceTexture::class.java),
                        width,
                        height,
                    )
                // viewPhotoview
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
                mCaptureSize = Size(w, h)
                Log.w("123", "w:${sizes[0].width}, h:${sizes[0].height}")
                Log.w("123", "vieww:$w, h:$h")
//                mCaptureSize = Size(1000, 1000)
//                mCaptureSize =
//                    Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))) { lhs, rhs ->
//                        java.lang.Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth())
//                    }
                // viewImageReaderviewPhotoview
                setupImageReader()
                // view
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun getOptimalSize(
        sizeMap: Array<Size>,
        width: Int,
        height: Int,
    ): Size {
        // view
        val sizeList: MutableList<Size> = ArrayList()
        // view
        for (option in sizeMap) {
            // view
            if (width > height) {
                if (option.width > width && option.height > height) {
                    sizeList.add(option)
                }
            } else {
                if (option.width > height && option.height > width) {
                    sizeList.add(option)
                }
            }
        }
        // viewSizeview
        return if (sizeList.size > 0) {
            Collections.min(
                sizeList,
            ) { lhs, rhs -> java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong()) }
        } else {
            sizeMap[0]
        }
    }

    private var flag = 0

    /**
     * SettingsImageReader
     */
    private fun setupImageReader() {
        // 2viewImageReaderview
        mImageReader =
            ImageReader.newInstance(
                mCaptureSize!!.width,
                mCaptureSize!!.height,
                ImageFormat.JPEG,
                1,
            )
        // Settingsview
        mImageReader.setOnImageAvailableListener({ reader ->
            flag = 1
            // view
            val image: Image = reader.acquireLatestImage()
            // view，view
            mCameraHandler.post(ImageSaver(image))
            // viewUI
            runOnUiThread { // view
                val buffer: ByteBuffer = image.planes[0].buffer
                // view，viewSettings
                buffer.rewind()
                // view
                val bytes = ByteArray(buffer.remaining())
                // view,viewpositionview
                buffer[bytes]
                // viewBitmapview
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                // view
                if (null != bitmap) {
                    val h = bitmap.height
                    val w = bitmap.width
                    mImageView?.let {
                        val sw = ScreenUtil.getScreenWidth(context)
                        it.layoutParams.height = sw / 2 * w / h
                        it.layoutParams.width = sw / 2
                        it.setImageBitmap(bitmap)
                    }
                }
                flag++
                thread {
                    while (flag < 3) {
//                        delay(100)
                        Thread.sleep(100)
                    }
                    flag = 0
                    image.close()
                }
            }
        }, mCameraHandler)
    }

    /**
     * view
     */
    private inner class ImageSaver(image: Image) : Runnable {
        /** Custom view */
        private val mImage: Image = image

        override fun run() {
//            ImageSaverTool().save(mImage)
            flag++
        }
    }
}
