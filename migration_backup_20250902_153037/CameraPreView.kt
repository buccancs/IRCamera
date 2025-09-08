package com.topdon.lib.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintSet
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.listener.BitmapViewListener
import com.topdon.lib.ui.R
import kotlinx.android.synthetic.main.camera_lay.view.*
import java.util.*


/**
 * [Chinese text]
 */
class CameraPreView : LinearLayout, ScaleGestureDetector.OnScaleGestureListener,
    BitmapViewListener {
    private var cameraCharacteristics: CameraCharacteristics ?= null
    private var isReverse : Boolean = false
    private lateinit var mTextureView: TextureView
    private var cameraWidth = 0

    var isPreviewing = false

    var cameraPreViewCloseListener : (() -> Unit) ?= null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun initView() {
        inflate(context, R.layout.camera_lay, this)
        mTextureView = findViewById(R.id.camera_texture)
        mTextureView.post { cameraWidth = mTextureView.width }
        lis = ScaleGestureDetector(context, this)
        onResumeView()
    }



    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isPreviewing = false
        mCameraDevice?.close()
        mCameraHandler?.removeCallbacksAndMessages(null)
    }

    private var startX = 0f//[Chinese text]point[Chinese text]
    private var startY = 0f
    private var moveX = 0f
    private var moveY = 0f
    private var parentViewW = 0f
    private var parentViewH = 0f
    private var isScale = false
    private var scale = 1f
    private var scaleW = 0f//[Chinese text]
    private var scaleH = 0f

    private lateinit var lis: ScaleGestureDetector

    @SuppressLint("ClickableViewAccessibility")
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
                //swipe
                moveX = event.x - startX
                moveY = event.y - startY
                //[Chinese text], [Chinese text]visible[Chinese text]
//                if (moveX-scaleW < -mTextureView.width ||
//                    moveX+scaleW > parentViewW ||
//                    moveY - scaleH < -mTextureView.height ||
//                    moveY + scaleH > parentViewH){
//                    cameraPreViewCloseListener?.invoke()
//                }

                //[Chinese text]
//                if (moveX - scaleW < 0f) moveX = 0f + scaleW
//                if (moveY - scaleH < 0f) moveY = 0f + scaleH
//                if (moveX + scaleW > parentViewW - mTextureView.width) {
//                    moveX = parentViewW - mTextureView.width - scaleW
//                }
//                if (moveY + scaleH > parentViewH - mTextureView.height) {
//                    moveY = parentViewH - mTextureView.height - scaleH
//                }
//                Log.e("[Chinese text]---","/"+(moveX + scaleW)+"///"+(parentViewW - mTextureView.width))
                mTextureView.x = moveX
                mTextureView.y = moveY
            }
            MotionEvent.ACTION_UP -> {
                isScale = false//[Chinese text]
                val startX = viewX
                val startY = viewY
//                Log.e("[Chinese text]","/"+(startX)+"///"+startY+"///"+(mTextureView.width)+"//"+mTextureView.width * scale)
                if ((viewX < 0 && startX <  -mTextureView.width * scale + SizeUtils.dp2px(10f)) ||
                    (startX > 0 && startX > parentViewW - SizeUtils.dp2px(10f)) ||
                    (startY < 0 && startY < -mTextureView.height * scale + SizeUtils.dp2px(10f)) ||
                    (startY > 0 && startY > parentViewH - SizeUtils.dp2px(10f))){
                    cameraPreViewCloseListener?.invoke()
                }
            }
        }
        return lis.onTouchEvent(event)
    }
    /**
     * [Chinese text]
     */
    public fun getBitmap() : Bitmap? {
        return mTextureView.bitmap
    }
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        //[Chinese text]
        isScale = true
        detector?.let {
            val scaleFactor = it.scaleFactor - 1
            if (scaleFactor < 0){
                if (scale > 0.1){
                    scale += scaleFactor
                    mTextureView.scaleX = scale
                    mTextureView.scaleY = scale
                }
            }else{
                scale += scaleFactor
                mTextureView.scaleX = scale
                mTextureView.scaleY = scale
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
    fun onResume(){
        //[Chinese text]switch[Chinese text], [Chinese text], [Chinese text]app[Chinese text]
        if (mCameraDevice!=null){
            mCameraDevice?.close()
            openCamera()
        }
    }

//////////////////
    /**[Chinese text] */
    private val REQUEST_CAMERA_CODE = 0x100

    /**[Chinese text] */
    private var mImageView: ImageView? = null

    /**[Chinese text]ID, [Chinese text] */
    private lateinit var mCameraId: String

    /**[Chinese text] */
    private var mCaptureSize: Size? = null

    /**[Chinese text] */
    private var mImageReader: ImageReader? = null

    /**[Chinese text]line[Chinese text]Handler */
    private var mCameraHandler: Handler? = null

    /**[Chinese text] */
    private var mCameraDevice: CameraDevice? = null

    /**[Chinese text] */
    private var mPreviewSize: Size? = null

    /**[Chinese text] */
    private lateinit var mCaptureBuilder: CaptureRequest.Builder

    /**[Chinese text]photo capture[Chinese text] */
    private var mCameraCaptureSession: CameraCaptureSession? = null

    /**[Chinese text] */
    private var mCameraManager: CameraManager? = null

    /**[Chinese text] */
    private val mStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(@NonNull camera: CameraDevice) {
                // [Chinese text]
                XLog.i("[Chinese text]")
                mCameraDevice = camera
                takePreview()
            }

            override fun onDisconnected(@NonNull camera: CameraDevice) {
                // [Chinese text]
                XLog.i("[Chinese text]")
                isPreviewing = false
//                camera.close()
//                mCameraDevice = null
            }

            override fun onError(@NonNull camera: CameraDevice, error: Int) {
                // [Chinese text]
                isPreviewing = false
                camera.close()
                mCameraDevice = null
                XLog.e("[Chinese text] error: $error")
            }
        }

    fun setRotation(isReverse : Boolean){
        this.isReverse = isReverse
        updateRotation()
    }

    private fun updateRotation(){
        if(isReverse){
            mTextureView.rotation = 180f
        }else{
            mTextureView.rotation = 0f
        }
    }

    /**
     * [Chinese text]
     * point[Chinese text]
     */
    private fun takePreview() {
//        mTextureView.rotation = 270f
//        mTextureView.rotation = 0f
        updateRotation()
//        val layoutParams = mTextureView.layoutParams
//        layoutParams.width = cameraWidth / 2
//        mTextureView.layoutParams = layoutParams
        val surfaceTexture = mTextureView.surfaceTexture
        // settings[Chinese text]
        surfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
        // [Chinese text]Surface
        val previewSurface = Surface(surfaceTexture)
        try {
            // [Chinese text]
            mCaptureBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // [Chinese text]previewSurface[Chinese text]in progress
            mCaptureBuilder.addTarget(previewSurface)
            // [Chinese text]
            mCameraDevice!!.createCaptureSession(
                listOf(previewSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull session: CameraCaptureSession) {
                        try {
                            // [Chinese text]
                            val captureRequest = mCaptureBuilder.build()
                            // [Chinese text]session
                            mCameraCaptureSession = session
                            // settings[Chinese text]
                            mCameraCaptureSession?.setRepeatingRequest(
                                captureRequest,
                                null,
                                mCameraHandler
                            )
                        } catch (e: CameraAccessException) {
                            XLog.e("[Chinese text]: ${e.printStackTrace()}")
                        }
                    }

                    override fun onConfigureFailed(@NonNull session: CameraCaptureSession) {
                        // [Chinese text]
                        XLog.e("[Chinese text]")
                    }
                },
                mCameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun onResumeView() {
        mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                // SurfaceTexture[Chinese text]
                XLog.w("width:$width, height:$height")
                setUpCamera(width, height)
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }


    /**
     * settings[Chinese text]
     * @param width [Chinese text]
     * @param height high[Chinese text]
     */
    private fun setUpCamera(width: Int, height: Int) {
        // [Chinese text]Handler
        mCameraHandler = Handler(Looper.getMainLooper())
        // [Chinese text]
        mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // [Chinese text],[Chinese text]
            for (cameraId in mCameraManager!!.cameraIdList) {
                XLog.i("camera id: $cameraId")
                cameraCharacteristics = mCameraManager!!.getCameraCharacteristics(cameraId)
                // [Chinese text]
                val facing = cameraCharacteristics?.get(CameraCharacteristics.LENS_FACING)
                // [Chinese text]
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) continue
                // [Chinese text]StreamConfigurationMap, [Chinese text]
                val map = cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // [Chinese text]TextureView[Chinese text]settings[Chinese text]
                val mapList = map.getOutputSizes(SurfaceTexture::class.java)

                mPreviewSize = getOptimalSize(mapList, width, height)
                val constraintSet = ConstraintSet()
                constraintSet.clone(camera_lay_root)
                constraintSet.constrainHeight(mTextureView.id,width * mPreviewSize!!.width / mPreviewSize!!.height)
                constraintSet.applyTo(camera_lay_root);
                XLog.w("mPreviewSize:${mPreviewSize}")
                // [Chinese text]photo capture[Chinese text]
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                XLog.w("size:${sizes.toList()}")
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
//                mCaptureSize = Size(w, h)//[Chinese text]photo capture[Chinese text]
                XLog.w("[Chinese text] w:${sizes[0].width}, h:${sizes[0].height}")
                XLog.w("[Chinese text] w: ${w}, h:${h}")
                // [Chinese text]ImageReaderforphoto capture[Chinese text]
//                setupImageReader()
                // [Chinese text]
                mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Log.e("123", "settings[Chinese text]:${e.message}")
        }
    }

    /**
     * [Chinese text]SizeMapin progress[Chinese text]width[Chinese text]height[Chinese text]size
     * @param sizeMap [Chinese text]
     * @param width [Chinese text]
     * @param height high
     * @return [Chinese text]width[Chinese text]height[Chinese text]size
     */
    private fun getOptimalSize(sizeMap: Array<Size>, width: Int, height: Int): Size {
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
            Collections.min(sizeList) { lhs, rhs ->
                java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong())
            }
        } else sizeMap[0]
    }

    /**
     * [Chinese text]
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        isPreviewing = true
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            isPreviewing = false
            XLog.e("[Chinese text]:${e.message}")
            ToastUtils.showShort("[Chinese text]")
        }
    }

    /**
     * [Chinese text]
     */
    @SuppressLint("MissingPermission")
    fun closeCamera() {
        isPreviewing = false
        try {
            mCameraDevice?.close()
            //[Chinese text]
            mTextureView.x = 0f
            mTextureView.y = 0f
            mTextureView.scaleX = 1f
            mTextureView.scaleY = 1f
            scale = 1f
//            isReverse = false
        } catch (e: Exception) {
            XLog.e("[Chinese text]:${e.message}")
            ToastUtils.showShort("[Chinese text]")
        }
    }

    override val viewX: Float
        get() = mTextureView.x - (viewWidth - mTextureView.width)/2
    override val viewY: Float
        get() = mTextureView.y - (viewHeight - mTextureView.height)/2
    override val viewAlpha: Float
        get() = mTextureView.alpha
    override val viewWidth: Float
        get() = mTextureView.width * scale
    override val viewHeight: Float
        get() = mTextureView.height * scale
    override val viewScale: Float
        get() = scale

    fun setCameraAlpha(alpha : Float){
        mTextureView?.alpha = 1 - alpha
    }

    fun setZoom(zoomLeve : Int){
        scale = zoomLeve * 0.5f
        mTextureView.scaleX = scale
        mTextureView.scaleY = scale
        invalidate()
    }


}
