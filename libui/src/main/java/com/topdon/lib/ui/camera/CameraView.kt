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

class CameraView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener {
    /** Custom view */
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
                // view
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
                isScale = false // view
            }
        }
        return lis.onTouchEvent(event)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // view
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

    private var startX = 0f // view
    private var startY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var parentViewW = 0f
    private var parentViewH = 0f
    private var isScale = false
    private var scale = 1f
    private var scaleW = 0f // view
    private var scaleH = 0f

    private lateinit var lis: ScaleGestureDetector

// ////////////////
    /** Custom view */
    private val REQUEST_CAMERA_CODE = 0x100

    /**Photoview */
    private var mBtnTake: Button? = null

    /** Custom view */
    private var mImageView: ImageView? = null

    /**viewID，view */
    private lateinit var mCameraId: String

    /** Custom view */
    private var mCaptureSize: Size? = null

    /** Custom view */
    private lateinit var mImageReader: ImageReader

    /**viewHandler */
    private lateinit var mCameraHandler: Handler

    /** Custom view */
    private var mCameraDevice: CameraDevice? = null

    /** Custom view */
    private var mPreviewSize: Size? = null

    /** Custom view */
    private lateinit var mCameraCaptureBuilder: CaptureRequest.Builder

    /**viewPhotoview */
    private var mCameraCaptureSession: CameraCaptureSession? = null

    /** Custom view */
    private var mCameraManager: CameraManager? = null

    /**viewStateview */
    private val mStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(
                @NonNull camera: CameraDevice,
            ) {
                // view
                mCameraDevice = camera
                // view
                takePreview()
            }

            override fun onDisconnected(
                @NonNull camera: CameraDevice,
            ) {
                // view
                camera.close()
                mCameraDevice = null
            }

            override fun onError(
                @NonNull camera: CameraDevice,
                error: Int,
            ) {
                // view
                camera.close()
                mCameraDevice = null
            }
        }

    /**
     * view
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
        mTextureView.rotation = 0f
        // viewSurfaceTexture
        val surfaceTexture = mTextureView.surfaceTexture
        // Settingsview
        surfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // viewSurface
        val previewSurface = Surface(surfaceTexture)
        try {
            // view
            mCameraCaptureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // viewpreviewSurfaceview
            mCameraCaptureBuilder.addTarget(previewSurface)
            // view
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
                            // view
                            val captureRequest = mCameraCaptureBuilder.build()
                            // viewsession
                            mCameraCaptureSession = session
                            // Settingsview
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
                        // view
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
                    // SurfaceTextureview
                    // Settingsview
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
                    // SurfaceTextureview
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    // SurfaceTexture view
                    return false
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                    // SurfaceTexture view
                }
            }
    }

    /**
     * view
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        // view
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            Log.e("123", "view:${e.message}")
            ToastUtils.showShort("view")
        }
    }

    /**
     * Settingsview
     * @param width view
     * @param height view
     */
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

    /**
     * viewSizeMapviewwidthviewheightviewsize
     * @param sizeMap view
     * @param width view
     * @param height view
     * @return viewwidthviewheightviewsize
     */
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
