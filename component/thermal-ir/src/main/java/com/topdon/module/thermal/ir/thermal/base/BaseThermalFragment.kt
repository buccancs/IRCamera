package com.topdon.module.thermal.ir.thermal.base

import android.graphics.Bitmap
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.topdon.lib.core.ktbase.BaseFragment
import kotlinx.coroutines.Job

open class BaseThermalFragment : BaseFragment() {
    /** mIrBitmap property */
    var mIrBitmap: Bitmap? = null
    /** REQUEST_CODE_FROM_UPGRADE property */
    val REQUEST_CODE_FROM_UPGRADE = 1001
    /** SRC_WIDTH property */
    val SRC_WIDTH = 192
    /** SRC_HEIGHT property */
    val SRC_HEIGHT = 256

    //0-9
    /** paletteIndex property */
    var paletteIndex = 0
    /** irSurfaceViewLayoutParams property */
    var irSurfaceViewLayoutParams: ConstraintLayout.LayoutParams? = null
    /** displayViewLayoutParams property */
    var displayViewLayoutParams: FrameLayout.LayoutParams? = null
    /** fenceLayoutParams property */
    var fenceLayoutParams: FrameLayout.LayoutParams? = null
    /** cameraLayoutParams property */
    var cameraLayoutParams: FrameLayout.LayoutParams? = null


    /** mCenter property */
    var mCenter = 0f
    /** mMaxTemp property */
    var mMaxTemp = 0f
    /** mMinTemp property */
    var mMinTemp = 0f
    /** maxImg property */
    var maxImg: ImageView? = null
    /** minImg property */
    var minImg: ImageView? = null
    /** maxIndex property */
    var maxIndex = 0//
    /** minIndex property */
    var minIndex = 0//

    /** mCenterTextView property */
    var mCenterTextView: TextView? = null
    /** mMaxTextView property */
    var mMaxTextView: TextView? = null
    /** mMinTextView property */
    var mMinTextView: TextView? = null
    /** mDistanceEditText property */
    var mDistanceEditText: EditText? = null
    /** mBrightEditText property */
    var mBrightEditText: EditText? = null
    /** mContrastEditText property */
    var mContrastEditText: EditText? = null
    /** mTempMatrixEditText property */
    var mTempMatrixEditText: EditText? = null
    /** mEmissEditText property */
    var mEmissEditText: EditText? = null
    /** mUpgradePathEditText property */
    var mUpgradePathEditText: EditText? = null
    /** mFirmwareVersionTextView property */
    var mFirmwareVersionTextView: TextView? = null
    /** mSnTextView property */
    var mSnTextView: TextView? = null
    /** mIdTextView property */
    var mIdTextView: TextView? = null
    /** mDisplayFrameLayout property */
    var mDisplayFrameLayout: FrameLayout? = null
    /** mFenceLayout property */
    var mFenceLayout: FrameLayout? = null
    /** mCameraLayout property */
    var mCameraLayout: FrameLayout? = null
    /** mExpertLayout property */
    var mExpertLayout: LinearLayout? = null

    /** isDispLayTemp property */
    var isDispLayTemp = false
    /** timerJob property */
    var timerJob: Job? = null
    /** EXPERT_MODE_HIT_COUNT property */
    val EXPERT_MODE_HIT_COUNT = 5
    /** EXPERT_MODE_HIT_DURATION property */
    val EXPERT_MODE_HIT_DURATION = (2 * 1000).toLong()
    /** EXPERT_HITS property */
    var EXPERT_HITS = LongArray(EXPERT_MODE_HIT_COUNT)

    /** rawWidth property */
    var rawWidth = 0
    /** rawHeight property */
    var rawHeight = 0
    /** highCrossWidth property */
    var highCrossWidth = 40
    /** highCrossHeight property */
    var highCrossHeight = 40
    /** rotateType property */
    var rotateType = 0 //1:90 2:180 3:270
    /** irSurfaceViewWidth property */
    var irSurfaceViewWidth = 0
    /** irSurfaceViewHeight property */
    var irSurfaceViewHeight = 0

    /** width property */
    var width = 0
    /** height property */
    var height = 0
    /** mIsIrVideoStart property */
    var mIsIrVideoStart = false

    override fun initContentView() = 0

    override fun initView() {

    }

    override fun initData() {

    }
}