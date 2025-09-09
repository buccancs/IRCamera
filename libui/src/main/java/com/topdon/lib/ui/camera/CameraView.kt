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
import java.nio.ByteBuffer
import java.util.Collections
import kotlin.concurrent.thread
import android.view.LayoutInflater
import com.topdon.lib.ui.databinding.CameraLayBinding

class CameraView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener {
    /**[CN_TEXT] */
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
                // [CN_TEXT]
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
                isScale = false // [CN_TEXT]
            }
        }
        return lis.onTouchEvent(event)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // [CN_TEXT]
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

    private var startX = 0f // [CN_TEXT]
    private var startY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var parentViewW = 0f
    private var parentViewH = 0f
    private var isScale = false
    private var scale = 1f
    private var scaleW = 0f // [CN_TEXT]
    private var scaleH = 0f

    private lateinit var lis: ScaleGestureDetector

// ////////////////
    /**[CN_TEXT] */
    private val REQUEST_CAMERA_CODE = 0x100

    /**Photo[CN_TEXT] */
    private var mBtnTake: Button? = null

    /**[CN_TEXT] */
    private var mImageView: ImageView? = null

    /**[CN_TEXT]ID，[CN_TEXT] */
    private lateinit var mCameraId: String

    /**[CN_TEXT] */
    private var mCaptureSize: Size? = null

    /**[CN_TEXT] */
    private lateinit var mImageReader: ImageReader

    /**[CN_TEXT]Handler */
    private lateinit var mCameraHandler: Handler

    /**[CN_TEXT] */
    private var mCameraDevice: CameraDevice? = null

    /**[CN_TEXT] */
    private var mPreviewSize: Size? = null

    /**[CN_TEXT] */
    private lateinit var mCameraCaptureBuilder: CaptureRequest.Builder

    /**[CN_TEXT]Photo[CN_TEXT] */
    private var mCameraCaptureSession: CameraCaptureSession? = null

    /**[CN_TEXT] */
    private var mCameraManager: CameraManager? = null

    /**[CN_TEXT]State[CN_TEXT] */
    private val mStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(
                @NonNull camera: CameraDevice,
            ) {
                // [CN_TEXT]
                mCameraDevice = camera
                // [CN_TEXT]
                takePreview()
            }

            override fun onDisconnected(
                @NonNull camera: CameraDevice,
            ) {
                // [CN_TEXT]
                camera.close()
                mCameraDevice = null
            }

            override fun onError(
                @NonNull camera: CameraDevice,
                error: Int,
            ) {
                // [CN_TEXT]
                camera.close()
                mCameraDevice = null
            }
        }

    /**
     * [CN_TEXT]
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
        mTextureView.rotation = 0f
        // [CN_TEXT]SurfaceTexture
        val surfaceTexture = mTextureView.surfaceTexture
        // Settings[CN_TEXT]
        surfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // [CN_TEXT]Surface
        val previewSurface = Surface(surfaceTexture)
        try {
            // [CN_TEXT]
            mCameraCaptureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // [CN_TEXT]previewSurface[CN_TEXT]
            mCameraCaptureBuilder.addTarget(previewSurface)
            // [CN_TEXT]
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
                            // [CN_TEXT]
                            val captureRequest = mCameraCaptureBuilder.build()
                            // [CN_TEXT]session
                            mCameraCaptureSession = session
                            // Settings[CN_TEXT]
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
                        // [CN_TEXT]
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
                    // SurfaceTexture[CN_TEXT]
                    // Settings[CN_TEXT]
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
                    // SurfaceTexture[CN_TEXT]
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    // SurfaceTexture [CN_TEXT]
                    return false
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                    // SurfaceTexture [CN_TEXT]
                }
            }
    }

    /**
     * [CN_TEXT]
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        // [CN_TEXT]
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            Log.e("123", "[CN_TEXT]:${e.message}")
            ToastUtils.showShort("[CN_TEXT]")
        }
    }

    /**
     * Settings[CN_TEXT]
     * @param width [CN_TEXT]
     * @param height [CN_TEXT]
     */
    private fun setUpCamera(
        width: Int,
        height: Int,
    ) {
        // [CN_TEXT]Handler
        mCameraHandler = Handler(Looper.getMainLooper())
        // [CN_TEXT]
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // [CN_TEXT]All[CN_TEXT]
            for (cameraId in cameraManager.cameraIdList) {
                // [CN_TEXT]
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                // [CN_TEXT]
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                // [CN_TEXT]
                if (null != facing && CameraCharacteristics.LENS_FACING_FRONT == facing) continue
                // [CN_TEXT]StreamConfigurationMap，[CN_TEXT]All[CN_TEXT]
                val map =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // [CN_TEXT]TextureView[CN_TEXT]Settings[CN_TEXT]
                mPreviewSize =
                    getOptimalSize(
                        map.getOutputSizes(SurfaceTexture::class.java),
                        width,
                        height,
                    )
                // [CN_TEXT]Photo[CN_TEXT]
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
                mCaptureSize = Size(w, h)
                Log.w("123", "w:${sizes[0].width}, h:${sizes[0].height}")
                Log.w("123", "[CN_TEXT]w:$w, h:$h")
//                mCaptureSize = Size(1000, 1000)
//                mCaptureSize =
//                    Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))) { lhs, rhs ->
//                        java.lang.Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth())
//                    }
                // [CN_TEXT]ImageReader[CN_TEXT]Photo[CN_TEXT]
                setupImageReader()
                // [CN_TEXT]
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * [CN_TEXT]SizeMap[CN_TEXT]width[CN_TEXT]height[CN_TEXT]size
     * @param sizeMap [CN_TEXT]
     * @param width [CN_TEXT]
     * @param height [CN_TEXT]
     * @return [CN_TEXT]width[CN_TEXT]height[CN_TEXT]size
     */
    private fun getOptimalSize(
        sizeMap: Array<Size>,
        width: Int,
        height: Int,
    ): Size {
        // [CN_TEXT]
        val sizeList: MutableList<Size> = ArrayList()
        // [CN_TEXT]
        for (option in sizeMap) {
            // [CN_TEXT]
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
        // [CN_TEXT]Size[CN_TEXT]
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
        // 2[CN_TEXT]ImageReader[CN_TEXT]
        mImageReader =
            ImageReader.newInstance(
                mCaptureSize!!.width,
                mCaptureSize!!.height,
                ImageFormat.JPEG,
                1,
            )
        // Settings[CN_TEXT]
        mImageReader.setOnImageAvailableListener({ reader ->
            flag = 1
            // [CN_TEXT]
            val image: Image = reader.acquireLatestImage()
            // [CN_TEXT]，[CN_TEXT]
            mCameraHandler.post(ImageSaver(image))
            // [CN_TEXT]UI
            runOnUiThread { // [CN_TEXT]
                val buffer: ByteBuffer = image.planes[0].buffer
                // [CN_TEXT]，[CN_TEXT]Settings
                buffer.rewind()
                // [CN_TEXT]
                val bytes = ByteArray(buffer.remaining())
                // [CN_TEXT],[CN_TEXT]position[CN_TEXT]
                buffer[bytes]
                // [CN_TEXT]Bitmap[CN_TEXT]
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                // [CN_TEXT]
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
     * [CN_TEXT]
     */
    private inner class ImageSaver(image: Image) : Runnable {
        /**[CN_TEXT] */
        private val mImage: Image = image

        override fun run() {
//            ImageSaverTool().save(mImage)
            flag++
        }
    }
}
