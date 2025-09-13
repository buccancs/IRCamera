package com.topdon.lib.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.NonNull
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.blankj.utilcode.util.ToastUtils
import com.topdon.lib.core.utils.ScreenUtil
import com.topdon.lib.ui.databinding.CameraLayBinding
import java.nio.ByteBuffer
import java.util.Collections
import kotlin.concurrent.thread

/**
 * Custom Camera view for thermal imaging display.
 * Provides specialized rendering and interaction capabilities.
 */
class CameraView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener {
    /** */
    lateinit var mTextureView: TextureView
    private lateinit var binding: CameraLayBinding

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    )

    private fun initView() {
        binding = CameraLayBinding.inflate(LayoutInflater.from(context), this, true)
        mTextureView = binding.cameraTexture
        mTextureView.alpha = 0.4f
        lis = ScaleGestureDetector(context, this)

        onResumeView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mCameraDevice?.close()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isScale && event.action != MotionEvent.ACTION_UP) {
            return lis.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleW = mTextureView.width * (scale - 1) / 2f
                scaleH = mTextureView.height * (scale - 1) / 2f
                startX = event.x - mTextureView.x
                startY = event.y - mTextureView.y
                val view: View = mTextureView.parent as View
                parentViewW = view.width.toFloat()
                parentViewH = view.height.toFloat()
            }
            MotionEvent.ACTION_MOVE -> {
                // 
                moveX = event.x - startX
                moveY = event.y - startY
                if (moveX - scaleW < 0f) moveX = 0f + scaleW
                if (moveY - scaleH < 0f) moveY = 0f + scaleH
                if (moveX + scaleW > parentViewW - mTextureView.width) {
                    moveX = parentViewW - mTextureView.width - scaleW
                }
                if (moveY + scaleH > parentViewH - mTextureView.height) {
                    moveY = parentViewH - mTextureView.height - scaleH
                }
                mTextureView.x = moveX
                mTextureView.y = moveY
            }
            MotionEvent.ACTION_UP -> {
                isScale = false // 
            }
        }
        return lis.onTouchEvent(event)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // 
        isScale = true
        detector?.let {
            val scaleFactor = it.scaleFactor - 1
            scale += scaleFactor
            mTextureView.scaleX = scale
            mTextureView.scaleY = scale
        }
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        isScale = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }

    private var startX = 0f // 
    private var startY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var parentViewW = 0f
    private var parentViewH = 0f
    private var isScale = false
    private var scale = 1f
    private var scaleW = 0f // 
    private var scaleH = 0f

    private lateinit var lis: ScaleGestureDetector

// ////////////////
    /** */
    private val REQUEST_CAMERA_CODE = 0x100

    /**capturebutton */
    private var mBtnTake: Button? = null

    /** */
    private var mImageView: ImageView? = null

    /**ID， */
    private lateinit var mCameraId: String

    /** */
    private var mCaptureSize: Size? = null

    /** */
    private lateinit var mImageReader: ImageReader

    /**Handler */
    private lateinit var mCameraHandler: Handler

    /** */
    private var mCameraDevice: CameraDevice? = null

    /** */
    private var mPreviewSize: Size? = null

    /** */
    private lateinit var mCameraCaptureBuilder: CaptureRequest.Builder

    /**capture */
    private var mCameraCaptureSession: CameraCaptureSession? = null

    /** */
    private var mCameraManager: CameraManager? = null

    /**state */
    private val mStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(
                @NonNull camera: CameraDevice,
            ) {
                // 
                mCameraDevice = camera
                // 
                takePreview()
            }

            override fun onDisconnected(
                @NonNull camera: CameraDevice,
            ) {
                // 
                camera.close()
                mCameraDevice = null
            }

            override fun onError(
                @NonNull camera: CameraDevice,
                error: Int,
            ) {
                // 
                camera.close()
                mCameraDevice = null
            }
        }

    /**
     * 
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
        mTextureView.rotation = 0f
        // SurfaceTexture
        val surfaceTexture = mTextureView.surfaceTexture
        // settings
        surfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // Surface
        val previewSurface = Surface(surfaceTexture)
        try {
            // 
            mCameraCaptureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // previewSurface
            mCameraCaptureBuilder.addTarget(previewSurface)
            // 
            @Suppress("DEPRECATION")
            mCameraDevice!!.createCaptureSession(
                listOf(
                    previewSurface,
                    mImageReader.surface,
                ),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(
                        @NonNull session: CameraCaptureSession,
                    ) {
                        try {
                            // configuration
                            val captureRequest = mCameraCaptureBuilder.build()
                            // session
                            mCameraCaptureSession = session
                            // settings
                            mCameraCaptureSession!!.setRepeatingRequest(
                                captureRequest,
                                null,
                                mCameraHandler,
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(
                        @NonNull session: CameraCaptureSession,
                    ) {
                        // configuration
                    }
                },
                mCameraHandler,
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun onResumeView() {
        mTextureView.surfaceTextureListener =
            object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture,
                    width: Int,
                    height: Int,
                ) {
                    // SurfaceTexture
                    // settings
                    Log.w("123", "width:$width, height:$height")
                    // w:h = 1 / 1.33
                    setUpCamera(width, height)
//                openCamera()
                }

                override fun onSurfaceTextureSizeChanged(
                    surface: SurfaceTexture,
                    width: Int,
                    height: Int,
                ) {
                    // SurfaceTexture
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    // SurfaceTexture 
                    return false
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                    // SurfaceTexture update
                }
            }
    }

    /**
     * 
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        // 
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            Log.e("123", "Test Data")
            ToastUtils.showShort("Test Data")
        }
    }

    /**
     * settings
     * @param width 
     * @param height 
     */
    private fun setUpCamera(
        width: Int,
        height: Int,
    ) {
        // Handler
        mCameraHandler = Handler(Looper.getMainLooper())
        // 
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // 
            for (cameraId in cameraManager.cameraIdList) {
                // 
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                // 
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                // 
                if (null != facing && CameraCharacteristics.LENS_FACING_FRONT == facing) continue
                // StreamConfigurationMap，
                val map =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // TextureViewsettings
                mPreviewSize =
                    getOptimalSize(
                        map.getOutputSizes(SurfaceTexture::class.java),
                        width,
                        height,
                    )
                // capture
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
                mCaptureSize = Size(w, h)
                Log.w("123", "w:${sizes[0].width}, h:${sizes[0].height}")
                Log.w("123", "Test Data")
//                mCaptureSize = Size(1000, 1000)
//                mCaptureSize =
//                    Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))) { lhs, rhs ->
//                        java.lang.Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth())
//                    }
                // ImageReadercapture
                setupImageReader()
                // 
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * SizeMapwidthheightsize
     * @param sizeMap 
     * @param width 
     * @param height 
     * @return widthheightsize
     */
    private fun getOptimalSize(
        sizeMap: Array<Size>,
        width: Int,
        height: Int,
    ): Size {
        // list
        val sizeList: MutableList<Size> = ArrayList()
        // 
        for (option in sizeMap) {
            // 
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
        // Sizelist
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
     * settingsImageReader
     */
    private fun setupImageReader() {
        // 2ImageReader
        mImageReader =
            ImageReader.newInstance(
                mCaptureSize!!.width,
                mCaptureSize!!.height,
                ImageFormat.JPEG,
                1,
            )
        // settings
        mImageReader.setOnImageAvailableListener({ reader ->
            flag = 1
            // 
            val image: Image = reader.acquireLatestImage()
            // ，saved
            mCameraHandler.post(ImageSaver(image))
            // updateUI
            runOnUiThread { // 
                val buffer: ByteBuffer = image.planes[0].buffer
                // array，restoresettings
                buffer.rewind()
                // array
                val bytes = ByteArray(buffer.remaining())
                // array,position
                buffer[bytes]
                // Bitmap
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                // 
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
     * saved
     */
    private inner class ImageSaver(image: Image) : Runnable {
        /** */
        private val mImage: Image = image

        override fun run() {
//            ImageSaverTool().save(mImage)
            flag++
        }
    }
}
