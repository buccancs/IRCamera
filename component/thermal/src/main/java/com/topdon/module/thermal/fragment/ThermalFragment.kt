package com.topdon.module.thermal.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import java.math.RoundingMode
import com.blankj.utilcode.util.ScreenUtils
import com.guide.zm04c.matrix.GuideInterface
import com.guide.zm04c.matrix.IrSurfaceView
// import com.tbruyelle.rxpermissions2.RxPermissions // Temporarily disabled - dependency not available
// import com.topdon.lib.core.bean.tools.ScreenBean // Temporarily disabled - utility class
import com.topdon.lib.core.tools.ToastTools
import com.topdon.lib.core.utils.ByteUtils.getIndex
// import com.topdon.lib.core.utils.ScreenShotUtils // Temporarily disabled - utility class
// import com.topdon.lib.ui.dialog.SeekDialog // Temporarily disabled - class not available
import com.topdon.lib.ui.dialog.ThermalInputDialog
import com.topdon.lib.ui.fence.FenceLineView
import com.topdon.lib.ui.fence.FencePointView
import com.topdon.lib.ui.fence.FenceView
import com.topdon.module.thermal.R
import com.topdon.module.thermal.base.BaseThermalFragment
import com.topdon.module.thermal.fragment.event.ThermalActionEvent
import com.topdon.module.thermal.tools.Fence
import com.topdon.module.thermal.tools.ThermalTool
import com.topdon.module.thermal.tools.medie.IYapVideoProvider
import com.topdon.module.thermal.tools.medie.YapVideoEncoder
import com.topdon.module.thermal.utils.ArrayUtils
import com.topdon.module.thermal.viewmodel.ThermalViewModel
import com.topdon.lib.ui.R as LibUiR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.math.BigDecimal

/**
\1thermal imaging
 */
/**
 * Thermal fragment for thermal imaging components.
 * Handles specific UI sections and user interactions.
 */
class ThermalFragment : BaseThermalFragment(), IYapVideoProvider<Bitmap> {
    private val viewModel: ThermalViewModel by viewModels()

    protected var mIrSurfaceViewLayout: FrameLayout? = null
    protected var mIrSurfaceView: IrSurfaceView? = null

    private val msgLiveData by lazy { MutableLiveData<Int>() }

    override fun initContentView() = R.layout.fragment_thermal

\1settemperatureText
    private fun setViewPosition(
        imageView: ImageView,
        index: Int,
    ) {
        if (rawWidth == 0 || rawHeight == 0) {
            return
        }
        val vg = imageView.parent as ViewGroup
        val pw = vg.width
        val ph = vg.height
        val y = index / rawWidth
        val x = index - y * rawWidth
        val x1 = x * pw / rawWidth
        val y1 = y * ph / rawHeight
        val maxX = x1 - imageView.width / 2
        val maxY = y1 - imageView.height / 2
\1Log.w("123", "Test Data")
        imageView.x = maxX.toFloat()
        imageView.y = maxY.toFloat()
    }

    private var mGuideInterface: GuideInterface? = null

    override fun initView() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        rotateType = 3 // 270
        mCenterTextView = requireView().findViewById(R.id.temp_display)
        mMaxTextView = requireView().findViewById(R.id.max_temp_display)
        mMinTextView = requireView().findViewById(R.id.min_temp_display)
        maxImg = requireView().findViewById(R.id.max_img)
        minImg = requireView().findViewById(R.id.min_img)
        mDisplayFrameLayout = requireView().findViewById(R.id.temp_display_layout)
        mFenceLayout = requireView().findViewById(R.id.fence_lay)
        mCameraLayout = requireView().findViewById(R.id.temp_camera_layout)
        mDisplayFrameLayout!!.visibility = View.GONE
        mFenceLayout!!.visibility = View.GONE
        mIrSurfaceViewLayout = requireView().findViewById(R.id.final_ir_layout)
        mIrSurfaceView = IrSurfaceView(requireContext())
        val ifrSurfaceViewLayoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER,
            )
        mIrSurfaceView!!.layoutParams = ifrSurfaceViewLayoutParams
        mIrSurfaceView!!.setMatrix(ThermalTool.getRotate(rotateType), 256f, 192f)
        mIrSurfaceViewLayout!!.addView(mIrSurfaceView)
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = screenWidth * 192 / 256
        width = screenWidth
        height = screenHeight
        highCrossWidth = resources.getDimension(R.dimen.high_cross_width).toInt()
        highCrossHeight = resources.getDimension(R.dimen.high_cross_height).toInt()
        mIrSurfaceViewLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            irSurfaceViewLayoutParams =
                mIrSurfaceViewLayout!!.layoutParams as ConstraintLayout.LayoutParams?
            displayViewLayoutParams = mDisplayFrameLayout!!.layoutParams as FrameLayout.LayoutParams
            fenceLayoutParams = mFenceLayout!!.layoutParams as FrameLayout.LayoutParams
            cameraLayoutParams = mCameraLayout!!.layoutParams as FrameLayout.LayoutParams
            when (rotateType) {
                1, 3 -> {
                    irSurfaceViewWidth = height
                    irSurfaceViewHeight = width
                    if (irSurfaceViewWidth < width) {
                        irSurfaceViewWidth = width
                        irSurfaceViewHeight = screenWidth * 256 / 192
                    }
                }
                0, 2 -> {
                    irSurfaceViewWidth = width
                    irSurfaceViewHeight = height
                }
            }
            irSurfaceViewLayoutParams!!.width = irSurfaceViewWidth
            irSurfaceViewLayoutParams!!.height = irSurfaceViewHeight
            mIrSurfaceViewLayout!!.layoutParams = irSurfaceViewLayoutParams

            displayViewLayoutParams!!.width = irSurfaceViewWidth
            displayViewLayoutParams!!.height = irSurfaceViewHeight
            mDisplayFrameLayout!!.layoutParams = displayViewLayoutParams

            fenceLayoutParams!!.width = irSurfaceViewWidth
            fenceLayoutParams!!.height = irSurfaceViewHeight
            mFenceLayout!!.layoutParams = fenceLayoutParams

            cameraLayoutParams!!.width = irSurfaceViewWidth
            cameraLayoutParams!!.height = irSurfaceViewHeight
            mFenceLayout!!.layoutParams = cameraLayoutParams
        }
\1Text
        initFence()
\1Textimage
        onIrVideoStart()
        mIrSurfaceView!!.post {
            Log.w("123", "w:${mIrSurfaceView!!.width}, h:${mIrSurfaceView!!.height}")
        }

        msgLiveData.observe(this) { msg ->
            if (msg == 0) {
                when (selectType) {
                    0 -> {
                        mCenterTextView!!.visibility = View.VISIBLE
                        mMaxTextView!!.visibility = View.VISIBLE
                        mMinTextView!!.visibility = View.VISIBLE
                        mCenterTextView!!.text = "Test Data"
                        mMaxTextView!!.text = "Test Data"
                        mMinTextView!!.text = "Test Data"
                        maxImg!!.visibility = View.GONE
                        minImg!!.visibility = View.GONE
                    }
                    1 -> {
                        mCenterTextView!!.visibility = View.VISIBLE
                        mMaxTextView!!.visibility = View.GONE
                        mMinTextView!!.visibility = View.GONE
                        mCenterTextView!!.text = "Test Data"
                        maxImg!!.visibility = View.GONE
                        minImg!!.visibility = View.GONE
                    }
                    else -> {
                        mCenterTextView!!.visibility = View.VISIBLE
                        mMaxTextView!!.visibility = View.VISIBLE
                        mMinTextView!!.visibility = View.VISIBLE
                        mCenterTextView!!.text = "Test Data"
                        mMaxTextView!!.text = "Test Data"
                        mMinTextView!!.text = "Test Data"
                        maxImg!!.visibility = View.VISIBLE
                        minImg!!.visibility = View.VISIBLE
                        maxImg?.let { setViewPosition(it, maxIndex) }
                        minImg?.let { setViewPosition(it, minIndex) }
                    }
                }
            }
        }
        onTempBtnClick()
    }

    override fun initData() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onIrVideoStop()
    }

    /**
\1enabled
     */
    fun onIrVideoStart() {
        mIsIrVideoStart =
            if (mIsIrVideoStart) {
                ToastTools.showShort("Test Data")
                return
            } else {
                true
            }
        mGuideInterface = GuideInterface()
        val ret =
            mGuideInterface!!.init(
                requireContext(),
                object : GuideInterface.IrDataCallback {
                    override fun processIrData(
                        yuv: ByteArray,
                        temp: FloatArray,
                    ) {
\1Textimage
                        if (mIrBitmap == null) {
                            mIrBitmap = Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888)
                        }
                        if (upValue > downValue) {
                            viewModel.yuvArea(yuv, temp, upValue, downValue)
                        }
                        mGuideInterface!!.yuv2Bitmap(mIrBitmap, yuv) // yuv
//                mIrBitmap = mIrBitmap?.let { rotateBitmap(it, 90f) }
                        try {
                            mIrSurfaceView!!.doDraw(mIrBitmap, mGuideInterface!!.getImageStatus())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (rotateType == 1 || rotateType == 3) {
                            rawWidth = SRC_WIDTH
                            rawHeight = SRC_HEIGHT
                        } else {
                            rawWidth = SRC_HEIGHT
                            rawHeight = SRC_WIDTH
                        }
                        val centerIndex = rawWidth * (rawHeight / 2) + rawWidth / 2
                        try {
\1Text
\1calculationText
                            val maxTempIndex = ArrayUtils.getMaxIndex(temp, rotateType, selectIndex)
                            val minTempIndex = ArrayUtils.getMinIndex(temp, rotateType, selectIndex)
                            maxIndex = maxTempIndex
                            minIndex = minTempIndex
\1rotationTexttemperaturearray
                            val rotateData = ArrayUtils.matrixRotate(srcData = temp, rotateType)
\1calculationTexttemperature
                            val bigDecimal = BigDecimal.valueOf(rotateData[centerIndex].toDouble())
                            val maxBigDecimal = BigDecimal.valueOf(rotateData[maxTempIndex].toDouble())
                            val minBigDecimal = BigDecimal.valueOf(rotateData[minTempIndex].toDouble())
                            mCenter = bigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                            mMaxTemp = maxBigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                            mMinTemp = minBigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e(TAG, "Test Data")
                        }
                    }
                },
            )

        if (ret == 5) {
            Log.w("123", "Test Data")
        } else {
            ToastTools.showShort("Test Data")
        }
    }

    private fun rotateBitmap(
        origin: Bitmap,
        rotate: Float,
    ): Bitmap? {
        try {
            if (origin == null) {
                return null
            }
            val width = origin.width
            val height = origin.height
            val matrix = Matrix()
            matrix.setRotate(rotate)
            val newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
            if (newBitmap.equals(origin)) {
                return newBitmap
            }
            origin.recycle()
            return newBitmap
        } catch (e: Exception) {
            Log.e("123", "error:${e.message}")
            return origin
        }
    }

    /**
\1
     */
    fun onIrVideoStop() {
        mIsIrVideoStart =
            if (!mIsIrVideoStart) {
                ToastTools.showShort("Test Data")
                return
            } else {
                false
            }
        mGuideInterface!!.exit()
        mGuideInterface = null
        ToastTools.showShort("Test Data")
    }

    fun onLowRangeBtnClick(view: View?) {
        if (mGuideInterface == null) {
            ToastTools.showShort("Test Data")
            return
        }
        mGuideInterface!!.setRange(1)
        ToastTools.showShort("Test Data")
    }

    fun onHighRangeBtnClick(view: View?) {
        if (mGuideInterface == null) {
            ToastTools.showShort("Test Data")
            return
        }
        mGuideInterface!!.setRange(2)
        ToastTools.showShort("Test Data")
    }

    /**
\1temperaturedisplay
     */
    fun onTempBtnClick() {
        if (mGuideInterface == null) {
            ToastTools.showShort("Test Data")
            return
        }
        isDispLayTemp = !isDispLayTemp
        if (isDispLayTemp) {
            mDisplayFrameLayout!!.visibility = View.VISIBLE
            timerJob =
                lifecycleScope.launch {
                    repeat(Int.MAX_VALUE) {
                        msgLiveData.postValue(0)
                        delay(1000)
                    }
                }
        } else {
            mDisplayFrameLayout!!.visibility = View.GONE
            if (timerJob != null && timerJob!!.isActive) {
                timerJob!!.cancel()
                timerJob = null
            }
        }
    }

    private var upValue = 0f
    private var downValue = 0f

    private fun addLimit() {
        ThermalInputDialog.Builder(requireContext())
            .setMessage("Test Data")
            .setPositiveListener(LibUiR.string.app_confirm) { up, down, _, _ ->
                ToastTools.showShort("Test Data")
                upValue = up
                downValue = down
            }
            .setCancelListener(LibUiR.string.app_cancel)
            .create().show()
    }

\1***************************************Text**********************************************

    /**
\1
     */
    fun onExpertModeClick(view: View?) {
        System.arraycopy(EXPERT_HITS, 1, EXPERT_HITS, 0, EXPERT_HITS.size - 1)
        EXPERT_HITS[EXPERT_HITS.size - 1] = System.currentTimeMillis()
        if (EXPERT_HITS[0] >= System.currentTimeMillis() - EXPERT_MODE_HIT_DURATION) {
            if (mExpertLayout!!.visibility == View.GONE) {
                mExpertLayout!!.visibility = View.VISIBLE
            } else {
                mExpertLayout!!.visibility = View.GONE
            }
            EXPERT_HITS = LongArray(EXPERT_MODE_HIT_COUNT)
        }
    }

    fun onNucShutterClick(view: View?) {
        if (mGuideInterface == null) {
            ToastTools.showShort("Test Data")
            return
        }
        mGuideInterface!!.nuc()
    }

//    private fun showTipDialog(tip: String, type: Int) {
//        val tipDialog = TipDialog.Builder(requireContext())
//            .setIconType(type)
//            .setTipWord(tip)
//            .create()
//        tipDialog.show()
//        mHandler.postDelayed({
//            try {
//                tipDialog.dismiss()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }, 1500)
//    }

    fun onLut(view: View) {
        mIrSurfaceView!!.setOpenLut()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun action(event: ThermalActionEvent) {
        Log.w("123", "event:${event.action}"Test Data"Text"Test Data"Text"Test Data"Text"Test Data"Text"Test Data"123", "Test Data")
            return
        }
        // Note: FileConfig.galleryPath requires integration with file configuration module
        // val latestResultPath = "${FileConfig.galleryPath}YapBitmapToMp4_${System.currentTimeMillis()}.mp4"
        val latestResultPath = "/tmp/YapBitmapToMp4_${System.currentTimeMillis()}.mp4" // Temporary fallback
        Log.w("123", "latestResultPath:$latestResultPath")
        YapVideoEncoder(this, File(latestResultPath)).start()
    }

\1rotation
    private fun rotate() {
        rotateType = if (rotateType >= 3) 0 else rotateType + 1
        mIrSurfaceView!!.setMatrix(ThermalTool.getRotate(rotateType), 256f, 192f)
        ToastTools.showShort("Test Data")
    }

\1imageText
    private fun enhance() {
        mIrSurfaceView!!.setOpenLut()
        val saturation = mIrSurfaceView?.getSaturationValue() ?: 0
        // Note: SeekDialog functionality requires integration with dialog utility module
        /*
        SeekDialog.Builder(requireContext())
            .setMessage(LibUiR.string.thermal_enhance)
            .setSaturation(saturation)
            .setPositiveListener(LibUiR.string.app_confirm) { value: Int ->
                mIrSurfaceView?.setSaturationValue(value)//
            }
            .setListener { value: Int ->
\1
\1mIrSurfaceView?.setSaturationValue(value)//set
            }.create().show()
         */
    }

    var isRunCamera = false

    private fun checkCameraPermission() {
        // Check camera permission using modern Android APIs
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            camera()
        } else {
            // Request camera permission
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            camera()
        }
    }

    @SuppressLint("CheckResult"Test Data"123", "progress:$progress")
        isVideoRunning = progress > 0 || progress < 100
    }
}
