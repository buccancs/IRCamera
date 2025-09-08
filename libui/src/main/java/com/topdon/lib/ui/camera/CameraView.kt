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
import com.topdon.lib.ui.R as UiR

class CameraView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener {
    /**[Chinese text] */
    lateinit var mTextureView: TextureView

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
        inflate(context, UiR.layout.camera_lay, this)
        mTextureView = findViewById(UiR.id.camera_texture)
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
                // swipe
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
                isScale = false // [Chinese text]
            }
        }
        return lis.onTouchEvent(event)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // [Chinese text]
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

    private var startX = 0f // [Chinese text]point[Chinese text]
    private var startY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var parentViewW = 0f
    private var parentViewH = 0f
    private var isScale = false
    private var scale = 1f
    private var scaleW = 0f // [Chinese text]
    private var scaleH = 0f

    private lateinit var lis: ScaleGestureDetector

// ////////////////
    /**[Chinese text] */
    private val REQUEST_CAMERA_CODE = 0x100

    /**Photo capturebutton */
    private var mBtnTake: Button? = null

    /**[Chinese text] */
    private var mImageView: ImageView? = null

    /**[Chinese text]ID, [Chinese text] */
    private lateinit var mCameraId: String

    /**[Chinese text] */
    private var mCaptureSize: Size? = null

    /**[Chinese text] */
    private lateinit var mImageReader: ImageReader

    /**[Chinese text]line[Chinese text]Handler */
    private lateinit var mCameraHandler: Handler

    /**[Chinese text] */
    private var mCameraDevice: CameraDevice? = null

    /**[Chinese text] */
    private var mPreviewSize: Size? = null

    /**[Chinese text] */
    private lateinit var mCameraCaptureBuilder: CaptureRequest.Builder

    /**[Chinese text]Photo capture[Chinese text] */
    private var mCameraCaptureSession: CameraCaptureSession? = null

    /**[Chinese text] */
    private var mCameraManager: CameraManager? = null

    /**[Chinese text] */
    private val mStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(
                @NonNull camera: CameraDevice,
            ) {
                // [Chinese text]
                mCameraDevice = camera
                // start[Chinese text]
                takePreview()
            }

            override fun onDisconnected(
                @NonNull camera: CameraDevice,
            ) {
                // [Chinese text]
                camera.close()
                mCameraDevice = null
            }

            override fun onError(
                @NonNull camera: CameraDevice,
                error: Int,
            ) {
                // [Chinese text]
                camera.close()
                mCameraDevice = null
            }
        }

    /**
     * [Chinese text]
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
        mTextureView.rotation = 0f
        // [Chinese text]SurfaceTexture
        val surfaceTexture = mTextureView.surfaceTexture
        // Settings[Chinese text]
        surfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // [Chinese text]Surface
        val previewSurface = Surface(surfaceTexture)
        try {
            // [Chinese text]
            mCameraCaptureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // [Chinese text]previewSurface[Chinese text]in progress
            mCameraCaptureBuilder.addTarget(previewSurface)
            // [Chinese text]
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
                            // [Chinese text]
                            val captureRequest = mCameraCaptureBuilder.build()
                            // [Chinese text]session
                            mCameraCaptureSession = session
                            // Settings[Chinese text]
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
                        // [Chinese text]
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
                    // SurfaceTexture[Chinese text]
                    // Settings[Chinese text]
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
                    // SurfaceTexture[Chinese text]
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    // SurfaceTexture [Chinese text]
                    return false
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                    // SurfaceTexture [Chinese text]
                }
            }
    }

    /**
     * [Chinese text]
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        // [Chinese text]
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            Log.e("123", "[Chinese text]:${e.message}")
            ToastUtils.showShort("[Chinese text]")
        }
    }

    /**
     * Settings[Chinese text]
     * @param width [Chinese text]
     * @param height high[Chinese text]
     */
    private fun setUpCamera(
        width: Int,
        height: Int,
    ) {
        // [Chinese text]Handler
        mCameraHandler = Handler(Looper.getMainLooper())
        // [Chinese text]
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // [Chinese text]
            for (cameraId in cameraManager.cameraIdList) {
                // [Chinese text]
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                // [Chinese text]
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                // [Chinese text]
                if (null != facing && CameraCharacteristics.LENS_FACING_FRONT == facing) continue
                // [Chinese text]StreamConfigurationMap, [Chinese text]
                val map =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // [Chinese text]TextureView[Chinese text]Settings[Chinese text]
                mPreviewSize =
                    getOptimalSize(
                        map.getOutputSizes(SurfaceTexture::class.java),
                        width,
                        height,
                    )
                // [Chinese text]Photo capture[Chinese text]
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
                mCaptureSize = Size(w, h)
                Log.w("123", "w:${sizes[0].width}, h:${sizes[0].height}")
                Log.w("123", "[Chinese text]w:$w, h:$h")
//                mCaptureSize = Size(1000, 1000)
//                mCaptureSize =
//                    Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))) { lhs, rhs ->
//                        java.lang.Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth())
//                    }
                // [Chinese text]ImageReaderforPhoto capture[Chinese text]
                setupImageReader()
                // [Chinese text]
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * [Chinese text]SizeMapin progress[Chinese text]width[Chinese text]height[Chinese text]size
     * @param sizeMap [Chinese text]
     * @param width [Chinese text]
     * @param height high
     * @return [Chinese text]width[Chinese text]height[Chinese text]size
     */
    private fun getOptimalSize(
        sizeMap: Array<Size>,
        width: Int,
        height: Int,
    ): Size {
        // [Chinese text]
        val sizeList: MutableList<Size> = ArrayList()
        // [Chinese text]
        for (option in sizeMap) {
            // [Chinese text]high[Chinese text]
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
        // [Chinese text]Size[Chinese text]
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
        // 2[Chinese text]ImageReaderin progress[Chinese text]
        mImageReader =
            ImageReader.newInstance(
                mCaptureSize!!.width,
                mCaptureSize!!.height,
                ImageFormat.JPEG,
                1,
            )
        // Settings[Chinese text]listener
        mImageReader.setOnImageAvailableListener({ reader ->
            flag = 1
            // [Chinese text]
            val image: Image = reader.acquireLatestImage()
            // [Chinese text], [Chinese text]
            mCameraHandler.post(ImageSaver(image))
            // [Chinese text]UI
            runOnUiThread { // [Chinese text]
                val buffer: ByteBuffer = image.planes[0].buffer
                // [Chinese text], [Chinese text]Settings
                buffer.rewind()
                // [Chinese text]
                val bytes = ByteArray(buffer.remaining())
                // [Chinese text],[Chinese text]position[Chinese text]
                buffer[bytes]
                // [Chinese text]Bitmap[Chinese text]
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                // [Chinese text]
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
     * [Chinese text]
     */
    private inner class ImageSaver(image: Image) : Runnable {
        /**[Chinese text] */
        private val mImage: Image = image

        override fun run() {
//            ImageSaverTool().save(mImage)
            flag++
        }
    }
}
