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
 /* Comment removed (contained Chinese characters) */
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
 // Comment removed (contained Chinese characters)
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
 isScale = false // End
 }
 }
 return lis.onTouchEvent(event)
 }

 override fun onScale(detector: ScaleGestureDetector): Boolean {
 // Comment removed (contained Chinese characters)
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
 /* Comment removed (contained Chinese characters) */
 private val REQUEST_CAMERA_CODE = 0x100

 /**capturebutton */
 private var mBtnTake: Button? = null

 /* Comment removed (contained Chinese characters) */
 private var mImageView: ImageView? = null

 /* Comment removed (contained Chinese characters) */
 private lateinit var mCameraId: String

 /* Comment removed (contained Chinese characters) */
 private var mCaptureSize: Size? = null

 /* Comment removed (contained Chinese characters) */
 private lateinit var mImageReader: ImageReader

 /* Comment removed (contained Chinese characters) */
 private lateinit var mCameraHandler: Handler

 /* Comment removed (contained Chinese characters) */
 private var mCameraDevice: CameraDevice? = null

 /* Comment removed (contained Chinese characters) */
 private var mPreviewSize: Size? = null

 /* Comment removed (contained Chinese characters) */
 private lateinit var mCameraCaptureBuilder: CaptureRequest.Builder

 /* Comment removed (contained Chinese characters) */
 private var mCameraCaptureSession: CameraCaptureSession? = null

 /* Comment removed (contained Chinese characters) */
 private var mCameraManager: CameraManager? = null

 /* Comment removed (contained Chinese characters) */
 private val mStateCallback: CameraDevice.StateCallback =
 object : CameraDevice.StateCallback() {
 override fun onOpened(
 @NonNull camera: CameraDevice,
 ) {
 // Comment removed (contained Chinese characters)
 mCameraDevice = camera
 // Comment removed (contained Chinese characters)
 takePreview()
 }

 override fun onDisconnected(
 @NonNull camera: CameraDevice,
 ) {
 // DisconnectConnection
 camera.close()
 mCameraDevice = null
 }

 override fun onError(
 @NonNull camera: CameraDevice,
 error: Int,
 ) {
 // Comment removed (contained Chinese characters)
 camera.close()
 mCameraDevice = null
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 private fun takePreview() {
// mTextureView.rotation = 270f
 mTextureView.rotation = 0f
 // Comment removed (contained Chinese characters)
 val surfaceTexture = mTextureView.surfaceTexture
 // Comment removed (contained Chinese characters)
 surfaceTexture!!.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
 // Comment removed (contained Chinese characters)
 val previewSurface = Surface(surfaceTexture)
 try {
 // Comment removed (contained Chinese characters)
 mCameraCaptureBuilder =
 mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
 // Comment removed (contained Chinese characters)
 mCameraCaptureBuilder.addTarget(previewSurface)
 // Comment removed (contained Chinese characters)
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
 // Comment removed (contained Chinese characters)
 mCameraCaptureSession = session
 // Comment removed (contained Chinese characters)
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
 // configurationFailed
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
 // Comment removed (contained Chinese characters)
 // Comment removed (contained Chinese characters)
 Log.w("123", "width:$width, height:$height")
 // w:h = 1 / 1.33
 setUpCamera(width, height)
// openCamera()
 }

 override fun onSurfaceTextureSizeChanged(
 surface: SurfaceTexture,
 width: Int,
 height: Int,
 ) {
 // Comment removed (contained Chinese characters)
 }

 override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
 // Comment removed (contained Chinese characters)
 return false
 }

 override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
 // SurfaceTexture update
 }
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @SuppressLint("MissingPermission")
 fun openCamera() {
 // Comment removed (contained Chinese characters)
 try {
 mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
 mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
 } catch (e: Exception) {
 Log.e("123", "Failed:${e.message}")
 ToastUtils.showShort("Failed")
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 private fun setUpCamera(
 width: Int,
 height: Int,
 ) {
 // Comment removed (contained Chinese characters)
 mCameraHandler = Handler(Looper.getMainLooper())
 // Comment removed (contained Chinese characters)
 val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
 try {
 // Comment removed (contained Chinese characters)
 for (cameraId in cameraManager.cameraIdList) {
 // Comment removed (contained Chinese characters)
 val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
 // Comment removed (contained Chinese characters)
 val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
 // Comment removed (contained Chinese characters)
 if (null != facing && CameraCharacteristics.LENS_FACING_FRONT == facing) continue
 // Comment removed (contained Chinese characters)
 val map =
 cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
 // Comment removed (contained Chinese characters)
 mPreviewSize =
 getOptimalSize(
 map.getOutputSizes(SurfaceTexture::class.java),
 width,
 height,
 )
 // Comment removed (contained Chinese characters)
 val sizes = map.getOutputSizes(ImageFormat.JPEG)
 val w = 1000
 val h = w * sizes[0].height / sizes[0].width
 mCaptureSize = Size(w, h)
 Log.w("123", "w:${sizes[0].width}, h:${sizes[0].height}")
 Log.w("123", "w:$w, h:$h")
// mCaptureSize = Size(1000, 1000)
// mCaptureSize =
// Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))) { lhs, rhs ->
// java.lang.Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth())
// }
 // Comment removed (contained Chinese characters)
 setupImageReader()
 // Comment removed (contained Chinese characters)
 mCameraId = cameraId
 break
 }
 } catch (e: CameraAccessException) {
 e.printStackTrace()
 }
 }

 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 private fun getOptimalSize(
 sizeMap: Array<Size>,
 width: Int,
 height: Int,
 ): Size {
 // Comment removed (contained Chinese characters)
 val sizeList: MutableList<Size> = ArrayList()
 // Comment removed (contained Chinese characters)
 for (option in sizeMap) {
 // Comment removed (contained Chinese characters)
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
 // Comment removed (contained Chinese characters)
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
 // Comment removed (contained Chinese characters)
 mImageReader =
 ImageReader.newInstance(
 mCaptureSize!!.width,
 mCaptureSize!!.height,
 ImageFormat.JPEG,
 1,
 )
 // Comment removed (contained Chinese characters)
 mImageReader.setOnImageAvailableListener({ reader ->
 flag = 1
 // Comment removed (contained Chinese characters)
 val image: Image = reader.acquireLatestImage()
 // Comment removed (contained Chinese characters)
 mCameraHandler.post(ImageSaver(image))
 // updateUI
 runOnUiThread { // 
 val buffer: ByteBuffer = image.planes[0].buffer
 // Comment removed (contained Chinese characters)
 buffer.rewind()
 // Comment removed (contained Chinese characters)
 val bytes = ByteArray(buffer.remaining())
 // Comment removed (contained Chinese characters)
 buffer[bytes]
 // Comment removed (contained Chinese characters)
 val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
 // Comment removed (contained Chinese characters)
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
// delay(100)
 Thread.sleep(100)
 }
 flag = 0
 image.close()
 }
 }
 }, mCameraHandler)
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 private inner class ImageSaver(image: Image) : Runnable {
 /* Comment removed (contained Chinese characters) */
 private val mImage: Image = image

 override fun run() {
// ImageSaverTool().save(mImage)
 flag++
 }
 }
}
