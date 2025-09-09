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
 * [CN_TEXT]
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
                // [CN_TEXT]
                moveX = event.x - startX
                moveY = event.y - startY
                // [CN_TEXT]，[CN_TEXT]
//                if (moveX-scaleW < -mTextureView.width ||
//                    moveX+scaleW > parentViewW ||
//                    moveY - scaleH < -mTextureView.height ||
//                    moveY + scaleH > parentViewH){
//                    cameraPreViewCloseListener?.invoke()
//                }

                // [CN_TEXT]
//                if (moveX - scaleW < 0f) moveX = 0f + scaleW
//                if (moveY - scaleH < 0f) moveY = 0f + scaleH
//                if (moveX + scaleW > parentViewW - mTextureView.width) {
//                    moveX = parentViewW - mTextureView.width - scaleW
//                }
//                if (moveY + scaleH > parentViewH - mTextureView.height) {
//                    moveY = parentViewH - mTextureView.height - scaleH
//                }
//                Log.e("[CN_TEXT]---","/"+(moveX + scaleW)+"///"+(parentViewW - mTextureView.width))
                binding.cameraTexture.x = moveX
                binding.cameraTexture.y = moveY
            }
            MotionEvent.ACTION_UP -> {
                isScale = false // [CN_TEXT]
                val startX = viewX
                val startY = viewY
//                Log.e("[CN_TEXT]","/"+(startX)+"///"+startY+"///"+(mTextureView.width)+"//"+mTextureView.width * scale)
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
     * [CN_TEXT]
     */
    public fun getBitmap(): Bitmap? {
        return binding.cameraTexture.bitmap
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // [CN_TEXT]
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
        // [CN_TEXT]Switch[CN_TEXT]，[CN_TEXT]，[CN_TEXT]app[CN_TEXT]
        if (mCameraDevice != null)
            {
                mCameraDevice?.close()
                openCamera()
            }
    }

// ////////////////

    /**[CN_TEXT] */
    private val REQUEST_CAMERA_CODE = 0x100

    /**[CN_TEXT] */
    private var mImageView: ImageView? = null

    /**[CN_TEXT]ID，[CN_TEXT] */
    private lateinit var mCameraId: String

    /**[CN_TEXT] */
    private var mCaptureSize: Size? = null

    /**[CN_TEXT] */
    private var mImageReader: ImageReader? = null

    /**[CN_TEXT]Handler */
    private var mCameraHandler: Handler? = null

    /**[CN_TEXT] */
    private var mCameraDevice: CameraDevice? = null

    /**[CN_TEXT] */
    private var mPreviewSize: Size? = null

    /**[CN_TEXT] */
    private lateinit var mCaptureBuilder: CaptureRequest.Builder

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
                XLog.i("[CN_TEXT]")
                mCameraDevice = camera
                takePreview()
            }

            override fun onDisconnected(
                @NonNull camera: CameraDevice,
            ) {
                // [CN_TEXT]
                XLog.i("[CN_TEXT]")
                isPreviewing = false
//                camera.close()
//                mCameraDevice = null
            }

            override fun onError(
                @NonNull camera: CameraDevice,
                error: Int,
            ) {
                // [CN_TEXT]
                isPreviewing = false
                camera.close()
                mCameraDevice = null
                XLog.e("[CN_TEXT] error: $error")
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
     * [CN_TEXT]
     * [CN_TEXT]
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
//        mTextureView.rotation = 0f
        updateRotation()
//        val layoutParams = mTextureView.layoutParams
//        layoutParams.width = cameraWidth / 2
//        mTextureView.layoutParams = layoutParams
        val surfaceTexture = binding.cameraTexture.surfaceTexture
        // Settings[CN_TEXT]
        surfaceTexture?.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // [CN_TEXT]Surface  
        val previewSurface = Surface(surfaceTexture)
        try {
            // [CN_TEXT]
            mCaptureBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // [CN_TEXT]previewSurface[CN_TEXT]
            mCaptureBuilder.addTarget(previewSurface)
            // [CN_TEXT]
            @Suppress("DEPRECATION")
            mCameraDevice!!.createCaptureSession(
                listOf(previewSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(
                        @NonNull session: CameraCaptureSession,
                    ) {
                        try {
                            // [CN_TEXT]
                            val captureRequest = mCaptureBuilder.build()
                            // [CN_TEXT]session
                            mCameraCaptureSession = session
                            // Settings[CN_TEXT]
                            mCameraCaptureSession?.setRepeatingRequest(
                                captureRequest,
                                null,
                                mCameraHandler,
                            )
                        } catch (e: CameraAccessException) {
                            XLog.e("[CN_TEXT]：${e.printStackTrace()}")
                        }
                    }

                    override fun onConfigureFailed(
                        @NonNull session: CameraCaptureSession,
                    ) {
                        // [CN_TEXT]
                        XLog.e("[CN_TEXT]")
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
                    // SurfaceTexture[CN_TEXT]
                    XLog.w("width:$width, height:$height")
                    setUpCamera(width, height)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
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
        mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // [CN_TEXT]All[CN_TEXT],[CN_TEXT]
            for (cameraId in mCameraManager!!.cameraIdList) {
                XLog.i("camera id: $cameraId")
                cameraCharacteristics = mCameraManager!!.getCameraCharacteristics(cameraId)
                // [CN_TEXT]
                val facing = cameraCharacteristics?.get(CameraCharacteristics.LENS_FACING)
                // [CN_TEXT]
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) continue
                // [CN_TEXT]StreamConfigurationMap，[CN_TEXT]All[CN_TEXT]
                val map = cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // [CN_TEXT]TextureView[CN_TEXT]Settings[CN_TEXT]
                val mapList = map.getOutputSizes(SurfaceTexture::class.java)

                mPreviewSize = getOptimalSize(mapList, width, height)
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.cameraLayRoot)
                constraintSet.constrainHeight(binding.cameraTexture.id, width * mPreviewSize!!.width / mPreviewSize!!.height)
                constraintSet.applyTo(binding.cameraLayRoot)
                XLog.w("mPreviewSize:$mPreviewSize")
                // [CN_TEXT]Photo[CN_TEXT]
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                XLog.w("size:${sizes.toList()}")
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
//                mCaptureSize = Size(w, h)//[CN_TEXT]Photo[CN_TEXT]
                XLog.w("[CN_TEXT] w:${sizes[0].width}, h:${sizes[0].height}")
                XLog.w("[CN_TEXT] w: $w, h:$h")
                // [CN_TEXT]ImageReader[CN_TEXT]Photo[CN_TEXT]
//                setupImageReader()
                // [CN_TEXT]
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Log.e("123", "Settings[CN_TEXT]:${e.message}")
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
            Collections.min(sizeList) { lhs, rhs ->
                java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong())
            }
        } else {
            sizeMap[0]
        }
    }

    /**
     * [CN_TEXT]
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        isPreviewing = true
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            isPreviewing = false
            XLog.e("[CN_TEXT]:${e.message}")
            ToastUtils.showShort("[CN_TEXT]")
        }
    }

    /**
     * [CN_TEXT]
     */
    @SuppressLint("MissingPermission")
    fun closeCamera() {
        isPreviewing = false
        try {
            mCameraDevice?.close()
            // [CN_TEXT]State
            binding.cameraTexture.x = 0f
            binding.cameraTexture.y = 0f
            binding.cameraTexture.scaleX = 1f
            binding.cameraTexture.scaleY = 1f
            scale = 1f
//            isReverse = false
        } catch (e: Exception) {
            XLog.e("[CN_TEXT]:${e.message}")
            ToastUtils.showShort("[CN_TEXT]")
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
