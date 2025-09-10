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

    private fun setUpCamera(
        width: Int,
        height: Int,
    ) {
        // 创建Handler
        mCameraHandler = Handler(Looper.getMainLooper())
        // 获取摄像头的管理者
        mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // 遍历所有摄像头,找到一个取消遍历
            for (cameraId in mCameraManager!!.cameraIdList) {
                XLog.i("camera id: $cameraId")
                cameraCharacteristics = mCameraManager!!.getCameraCharacteristics(cameraId)
                // 获取摄像头是前置还是后置
                val facing = cameraCharacteristics?.get(CameraCharacteristics.LENS_FACING)
                // 前置摄像头跳过
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) continue
                // 获取StreamConfigurationMap，管理摄像头支持的所有输出格式和尺寸
                val map = cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                // 根据TextureView的尺寸设置预览尺寸
                val mapList = map.getOutputSizes(SurfaceTexture::class.java)

                mPreviewSize = getOptimalSize(mapList, width, height)
                val constraintSet = ConstraintSet()
                constraintSet.clone(camera_lay_root)
                constraintSet.constrainHeight(mTextureView.id, width * mPreviewSize!!.width / mPreviewSize!!.height)
                constraintSet.applyTo(camera_lay_root)
                XLog.w("mPreviewSize:$mPreviewSize")
                // 获取相机支持的最大拍照尺寸
                val sizes = map.getOutputSizes(ImageFormat.JPEG)
                XLog.w("size:${sizes.toList()}")
                val w = 1000
                val h = w * sizes[0].height / sizes[0].width
//                mCaptureSize = Size(w, h)//影响拍照尺寸
                XLog.w("选取比例 w:${sizes[0].width}, h:${sizes[0].height}")
                XLog.w("调整后 w: $w, h:$h")
mCameraId = cameraId
                break
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            Log.e("123", "设置相机参数:${e.message}")
        }
    }

    private fun getOptimalSize(
        sizeMap: Array<Size>,
        width: Int,
        height: Int,
    ): Size {
        // 创建列表
        val sizeList: MutableList<Size> = ArrayList()
        // 遍历
        for (option in sizeMap) {
            // 判断宽度是否大于高度
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
        // 判断存储Size的列表是否有数据
        return if (sizeList.size > 0) {
            Collections.min(sizeList) { lhs, rhs ->
                java.lang.Long.signum((lhs.width * lhs.height - rhs.width * rhs.height).toLong())
            }
        } else {
            sizeMap[0]
        }
    }

    /**
     * 打开相机
     */
    @SuppressLint("MissingPermission")
    fun openCamera() {
        isPreviewing = true
        try {
            mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            mCameraManager!!.openCamera(mCameraId, mStateCallback, mCameraHandler)
        } catch (e: Exception) {
            isPreviewing = false
            XLog.e("打开相机失败:${e.message}")
            ToastUtils.showShort("打开相机失败")
        }
    }

    /**
     * 关闭相机
     */
    @SuppressLint("MissingPermission")
    fun closeCamera() {
        isPreviewing = false
        try {
            mCameraDevice?.close()
            // 恢复原始状态
            mTextureView.x = 0f
            mTextureView.y = 0f
            mTextureView.scaleX = 1f
            mTextureView.scaleY = 1f
            scale = 1f
//            isReverse = false
        } catch (e: Exception) {
            XLog.e("关闭相机失败:${e.message}")
            ToastUtils.showShort("关闭相机失败")
        }
    }

    override val viewX: Float
        get() = mTextureView.x - (viewWidth - mTextureView.width) / 2
    override val viewY: Float
        get() = mTextureView.y - (viewHeight - mTextureView.height) / 2
    override val viewAlpha: Float
        get() = mTextureView.alpha
    override val viewWidth: Float
        get() = mTextureView.width * scale
    override val viewHeight: Float
        get() = mTextureView.height * scale
    override val viewScale: Float
        get() = scale

    fun setCameraAlpha(alpha: Float) {
        mTextureView?.alpha = 1 - alpha
    }

    fun setZoom(zoomLeve: Int) {
        scale = zoomLeve * 0.5f
        mTextureView.scaleX = scale
        mTextureView.scaleY = scale
        invalidate()
    }
}
