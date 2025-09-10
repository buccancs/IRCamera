package com.guide.zm04c.matrix

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.blankj.utilcode.util.ScreenUtils

class IrSurfaceView : SurfaceView, SurfaceHolder.Callback {
    private var mHolder: SurfaceHolder? = null // viewSurfaceView
    private var mCanvas: Canvas? = null // view
    private val p: Paint by lazy { Paint() } // view
    private val mMatrix: Matrix by lazy { Matrix() }
    private var openLut = false

    private val mBeforeRotateMatrixValues = FloatArray(9)
    private val mScaleMatrixValues = FloatArray(9)
    private val mRotateMatrixValues = FloatArray(9)

    @Volatile
    private var isPrepare = false

    @Volatile
    private var isLockImage = false

    private var callback: IfrCamOpenOverCallback? = null

    private var mCtx: Context? = null

    constructor(context: Context) : super(context) {
        mCtx = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mCtx = context
        init()
    }

    private fun init() {
        mHolder = holder // viewSurfaceHolderview
        mHolder?.addCallback(this) // viewSurfaceViewviewStateview
        mHolder?.setFormat(PixelFormat.TRANSPARENT)
        p.alpha = 0xff
        mMatrix.setScale(1.0f, 1.0f)
    }

    fun setIsLockImage(isLock: Boolean) {
        isLockImage = isLock
    }

    fun setMatrix(
        rotate: Float,
        w: Float,
        h: Float,
    ) {
        mMatrix.reset()
        when (rotate) {
            90f -> {
                val sca = ScreenUtils.getScreenWidth() / h
                mMatrix.setRotate(rotate, 0f, 0f)
                mMatrix.postTranslate(h, 0f)
                mMatrix.postScale(sca, sca)
            }
            180f -> {
                val sca = ScreenUtils.getScreenWidth() / w
                mMatrix.setRotate(rotate, 0f, 0f)
                mMatrix.postTranslate(w, h)
                mMatrix.postScale(sca, sca)
            }
            270f -> {
                // View rendering
                val sca = ScreenUtils.getScreenWidth() / h
                mMatrix.setRotate(rotate, 0f, 0f)
                mMatrix.postTranslate(0f, w)
                mMatrix.postScale(sca, sca)
            }
            else -> {
                val sca = ScreenUtils.getScreenWidth() / w
                mMatrix.postScale(sca, sca)
            }
        }
    }

    fun setSaturationValue(saturation: Int) {
        this.saturation = saturation
    }

    fun getSaturationValue(): Int {
        return saturation
    }

    fun setAlpha(alpha: Int) {
        if (alpha in 0..255) {
            p?.alpha = alpha
        }
    }

    /**
     * viewSurfaceViewview，view
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        isPrepare = true
        if (callback != null) {
            callback!!.onSurfaceCreated()
        }
        Logger.d(TAG, "holder onSurfaceCreated")
    }

    /**
     * viewSurfaceViewview，view
     */
    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int,
    ) {
        Logger.d(TAG, "holder surfaceChanged")
    }

    /**
     * viewSurfaceViewview，view
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        synchronized(this) {
            isPrepare = false
            Logger.d(TAG, "holder destroyed")
        }
    }

    companion object {
        private val TAG = "IrSurfaceView"
    }

    interface IfrCamOpenOverCallback {
        fun onSurfaceCreated()
    }
}
