package com.topdon.lib.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
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
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.listener.BitmapViewListener
import com.topdon.lib.ui.R as UiR
import com.topdon.lib.ui.databinding.CameraLayBinding
import java.util.Collections
import android.view.LayoutInflater

/**
 * view
 */
class CameraPreView :
    LinearLayout,
    ScaleGestureDetector.OnScaleGestureListener,
    BitmapViewListener {
    
    private lateinit var binding: CameraLayBinding
    private var cameraCharacteristics: CameraCharacteristics? = null
    private var isReverse: Boolean = false
    private var cameraWidth = 0

    var isPreviewing = false

    var cameraPreViewCloseListener: (() -> Unit)? = null

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
        binding.cameraTexture.post { cameraWidth = binding.cameraTexture.width }
        lis = ScaleGestureDetector(context, this)
        onResumeView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isPreviewing = false
        mCameraDevice?.close()
        mCameraHandler?.removeCallbacksAndMessages(null)
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isScale && event.action != MotionEvent.ACTION_UP) {
            return lis.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleW = binding.cameraTexture.width * (scale - 1) / 2f
                scaleH = binding.cameraTexture.height * (scale - 1) / 2f
                startX = event.x - binding.cameraTexture.x
                startY = event.y - binding.cameraTexture.y
                val view: View = binding.cameraTexture.parent as View
                parentViewW = view.width.toFloat()
                parentViewH = view.height.toFloat()
            }
            MotionEvent.ACTION_MOVE -> {
                // view
                moveX = event.x - startX
                moveY = event.y - startY
                // view，view
//                if (moveX-scaleW < -mTextureView.width ||
//                    moveX+scaleW > parentViewW ||
//                    moveY - scaleH < -mTextureView.height ||
//                    moveY + scaleH > parentViewH){
//                    cameraPreViewCloseListener?.invoke()
//                }

                // view
//                if (moveX - scaleW < 0f) moveX = 0f + scaleW
//                if (moveY - scaleH < 0f) moveY = 0f + scaleH
//                if (moveX + scaleW > parentViewW - mTextureView.width) {
//                    moveX = parentViewW - mTextureView.width - scaleW
//                }
//                if (moveY + scaleH > parentViewH - mTextureView.height) {
//                    moveY = parentViewH - mTextureView.height - scaleH
//                }
//                Log.e("view---","/"+(moveX + scaleW)+"///"+(parentViewW - mTextureView.width))
                binding.cameraTexture.x = moveX
                binding.cameraTexture.y = moveY
            }
            MotionEvent.ACTION_UP -> {
                isScale = false // view
                val startX = viewX
                val startY = viewY
//                Log.e("view","/"+(startX)+"///"+startY+"///"+(mTextureView.width)+"//"+mTextureView.width * scale)
                if ((viewX < 0 && startX < -binding.cameraTexture.width * scale + SizeUtils.dp2px(10f)) ||
                    (startX > 0 && startX > parentViewW - SizeUtils.dp2px(10f)) ||
                    (startY < 0 && startY < -binding.cameraTexture.height * scale + SizeUtils.dp2px(10f)) ||
                    (startY > 0 && startY > parentViewH - SizeUtils.dp2px(10f))
                )
                    {
                        cameraPreViewCloseListener?.invoke()
                    }
            }
        }
        return lis.onTouchEvent(event)
    }

    /**
     * view
     */
    public fun getBitmap(): Bitmap? {
        return binding.cameraTexture.bitmap
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // view
        isScale = true
        detector?.let {
            val scaleFactor = it.scaleFactor - 1
            if (scaleFactor < 0)
                {
                    if (scale > 0.1)
                        {
                            scale += scaleFactor
                            binding.cameraTexture.scaleX = scale
                            binding.cameraTexture.scaleY = scale
                        }
                } else
                {
                    scale += scaleFactor
                    binding.cameraTexture.scaleX = scale
                    binding.cameraTexture.scaleY = scale
                }
        }
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        isScale = true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }

    fun onResume()  {
        // viewSwitchview，view，viewappview
        if (mCameraDevice != null)
            {
                mCameraDevice?.close()
                openCamera()
            }
    }

// ////////////////

    /** Custom view */
    private val REQUEST_CAMERA_CODE = 0x100

    /** Custom view */
    private var mImageView: ImageView? = null

    /**viewID，view */
    private lateinit var mCameraId: String

    /** Custom view */
    private var mCaptureSize: Size? = null

    /** Custom view */
    private var mImageReader: ImageReader? = null

    /**viewHandler */
    private var mCameraHandler: Handler? = null

    /** Custom view */
    private var mCameraDevice: CameraDevice? = null

    /** Custom view */
    private var mPreviewSize: Size? = null

    /** Custom view */
    private lateinit var mCaptureBuilder: CaptureRequest.Builder

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
                XLog.i("view")
                mCameraDevice = camera
                takePreview()
            }

            override fun onDisconnected(
                @NonNull camera: CameraDevice,
            ) {
                // view
                XLog.i("view")
                isPreviewing = false
//                camera.close()
//                mCameraDevice = null
            }

            override fun onError(
                @NonNull camera: CameraDevice,
                error: Int,
            ) {
                // view
                isPreviewing = false
                camera.close()
                mCameraDevice = null
                XLog.e("view error: $error")
            }
        }

    fun setRotation(isReverse: Boolean)  {
        this.isReverse = isReverse
        updateRotation()
    }

    private fun updateRotation()  {
        if (isReverse)
            {
                binding.cameraTexture.rotation = 180f
            } else
            {
                binding.cameraTexture.rotation = 0f
            }
    }

    /**
     * view
     * view
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
//        mTextureView.rotation = 0f
        updateRotation()
//        val layoutParams = mTextureView.layoutParams
//        layoutParams.width = cameraWidth / 2
//        mTextureView.layoutParams = layoutParams
        val surfaceTexture = binding.cameraTexture.surfaceTexture
        // Settingsview
        surfaceTexture?.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // viewSurface  
        val previewSurface = Surface(surfaceTexture)
        try {
            // view
            mCaptureBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // viewpreviewSurfaceview
            mCaptureBuilder.addTarget(previewSurface)
            // view
            @Suppress("DEPRECATION")
            mCameraDevice!!.createCaptureSession(
                listOf(previewSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(
                        @NonNull session: CameraCaptureSession,
                    ) {
                        try {
                            // view
                            val captureRequest = mCaptureBuilder.build()
                            // viewsession
                            mCameraCaptureSession = session
                            // Settingsview
                            mCameraCaptureSession?.setRepeatingRequest(
                                captureRequest,
                                null,
                                mCameraHandler,
                            )
                        } catch (e: CameraAccessException) {
                            XLog.e("view：${e.printStackTrace()}")
                        }
                    }

                    override fun onConfigureFailed(
                        @NonNull session: CameraCaptureSession,
                    ) {
                        // view
                        XLog.e("view")
                    }
                },
                mCameraHandler,
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun onResumeView() {
        binding.cameraTexture.surfaceTextureListener =
            object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture,
                    width: Int,
                    height: Int,
                ) {
                    // SurfaceTextureview
                    XLog.w("width:$width, height:$height")
                    setUpCamera(width, height)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
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
        mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // viewAllview,view
            for (cameraId in mCameraManager!!.cameraIdList) {
                XLog.i("camera id: $cameraId")
                cameraCharacteristics = mCameraManager!!.getCameraCharacteristics(cameraId)
                // view
                val facing = cameraCharacteristics?.get(CameraCharacteristics.LENS_FACING)
                // view
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) continue
                // viewStreamConfigurationMap，viewAllview
                val map = cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // viewTextureViewviewSettingsview
                val mapList = map.getOutputSizes(SurfaceTexture::class.java)

                mPreviewSize = getOptimalSize(mapList, width, height)
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.cameraLayRoot)
                constraintSet.constrainHeight(binding.cameraTexture.id, width * mPreviewSize!!.width / mPreviewSize!!.height)
                constraintSet.applyTo(binding.cameraLayRoot)
                XLog.w("mPreviewSize:$mPreviewSize")
                // viewPhotoview
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                XLog.w("size:${sizes.toList()}")
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
//                mCaptureSize = Size(w, h)// View renderingPhotoview
                XLog.w("view w:${sizes[0].width}, h:${sizes[0].height}")
                XLog.w("view w: $w, h:$h")
                // viewImageReaderviewPhotoview
//                setupImageReader()
                // view
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Log.e("123", "Settingsview:${e.message}")
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
            Collections.min(sizeList) { lhs, rhs ->
                java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong())
            }
        } else {
            sizeMap[0]
        }
    }

    /**
     * view
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        isPreviewing = true
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            isPreviewing = false
            XLog.e("view:${e.message}")
            ToastUtils.showShort("view")
        }
    }

    /**
     * view
     */
    @SuppressLint("MissingPermission")
    fun closeCamera() {
        isPreviewing = false
        try {
            mCameraDevice?.close()
            // viewState
            binding.cameraTexture.x = 0f
            binding.cameraTexture.y = 0f
            binding.cameraTexture.scaleX = 1f
            binding.cameraTexture.scaleY = 1f
            scale = 1f
//            isReverse = false
        } catch (e: Exception) {
            XLog.e("view:${e.message}")
            ToastUtils.showShort("view")
        }
    }

    override val viewX: Float
        get() = binding.cameraTexture.x - (viewWidth - binding.cameraTexture.width) / 2
    override val viewY: Float
        get() = binding.cameraTexture.y - (viewHeight - binding.cameraTexture.height) / 2
    override val viewAlpha: Float
        get() = binding.cameraTexture.alpha
    override val viewWidth: Float
        get() = binding.cameraTexture.width * scale
    override val viewHeight: Float
        get() = binding.cameraTexture.height * scale
    override val viewScale: Float
        get() = scale

    fun setCameraAlpha(alpha: Float)  {
        binding.cameraTexture.alpha = 1 - alpha
    }

    fun setZoom(zoomLeve: Int)  {
        scale = zoomLeve * 0.5f
        binding.cameraTexture.scaleX = scale
        binding.cameraTexture.scaleY = scale
        invalidate()
    }
}
