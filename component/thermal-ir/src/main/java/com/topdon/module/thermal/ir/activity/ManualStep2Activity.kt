package com.topdon.module.thermal.ir.activity

import android.app.Activity
import android.graphics.ImageFormat
import android.hardware.usb.UsbDevice
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.usb.USBMonitor
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.DualCameraParams
import com.infisense.usbdual.Const
import com.infisense.usbdual.camera.DualViewWithManualAlignExternalCamera
import com.infisense.usbdual.camera.USBMonitorDualManager
import com.infisense.usbdual.inf.OnUSBConnectListener
import com.infisense.usbir.utils.HexDump
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.ktbase.BaseActivity
import com.topdon.lib.core.utils.ByteUtils.toLittleBytes
import com.topdon.lms.sdk.weiget.LmsLoadDialog
import com.topdon.module.thermal.ir.R
import com.topdon.module.thermal.ir.event.ManualFinishBean
import com.topdon.module.thermal.ir.utils.IRCmdTool
import com.topdon.module.thermal.ir.view.MoveImageView
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.io.InputStream

/**
 * Created by fengjibo on 2024/1/10.
 */
/**
 * Manual step2 activity for thermal imaging interface.
 * Manages UI interactions and thermal data display.
 */
class ManualStep2Activity :
    BaseActivity(),
    OnUSBConnectListener,
    View.OnClickListener {
    override fun initContentView(): Int {
        return R.layout.activity_manual_step2
    }

    private var snStr = ""
    private var mThisActivity: Activity? = null
    private var mProgressDialog: LmsLoadDialog? = null
    private var mDualView: DualViewWithManualAlignExternalCamera? = null
    private val mDefaultDataFlowMode = CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT
    protected var dualDisp: Int = 0

    /**
     * ir camera
     * 22576 - 0x5830
     * 22592 - 0x5840
     */
    private val mIrPid = 0x5830
    private val mIrFps = 25
    private var mIrCameraWidth = 0 // 
    private var mIrCameraHeight = 0 // 
    private var mImageWidth = 0 // 
    private var mImageHeight = 0 // 

    /**
     * vl camera
     * 12341 - 0x3035  30 fps 640*480
     * 38704 - 0x9730  25 fps 1280*720
     */
    private val mVlPid = 12337
    private val mVlFps = 30 // 
    private val mVlCameraWidth = 1280
    private val mVlCameraHeight = 720

    /**
\1fusion
     */
    private val mDualWidth = 480
    private val mDualHeight = 640
    private var mPseudoColors: Array<ByteArray?> = arrayOf()
    private var mFullScreenLayoutParams: FrameLayout.LayoutParams? = null
    private var sId: String = ""

    /**
\1initializeparameter
     */
    private val INIT_ALIGN_DATA = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    private var alignScaleX = 0f // 
    private var alignScaleY = 0f // 
    private var canOperate = false // 
    private val mIrDualHandler: Handler =
        object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == SHOW_LOADING) {
                    Log.d(TAG, "SHOW_LOADING")
                    showLoadingDialog()
                } else if (msg.what == HIDE_LOADING) {
                    Log.d(TAG, "HIDE_LOADING"Test Data"initdata screenWidth : $screenWidth screenHeight: $screenHeight")
//        Log.d(TAG, "initdata imageWidth : $mImageWidth imageHeight: $mImageHeight"Test Data"pseudocolor/White_Hot.bin")
            var lenth = `is`.available()
            mPseudoColors[0] = ByteArray(lenth + 1)
            if (`is`.read(mPseudoColors[0]) != lenth) {
                Log.d(Companion.TAG, "read file fail ")
            }
            mPseudoColors[0]!![lenth] = 0
            mDualView!!.dualUVCCamera.loadPseudocolor(
                CommonParams.PseudoColorUsbDualType.WHITE_HOT_MODE,
                mPseudoColors[0],
            )
            `is` = am.open("pseudocolor/Black_Hot.bin")
            lenth = `is`.available()
            mPseudoColors[1] = ByteArray(lenth + 1)
            if (`is`.read(mPseudoColors[1]) != lenth) {
                Log.d(Companion.TAG, "read file fail ")
            }
            mPseudoColors[1]!![lenth] = 1
            mDualView!!.dualUVCCamera.loadPseudocolor(
                CommonParams.PseudoColorUsbDualType.BLACK_HOT_MODE,
                mPseudoColors[1],
            )
            `is` = am.open("pseudocolor/new_Rainbow.bin")
            lenth = `is`.available()
            mPseudoColors[2] = ByteArray(lenth + 1)
            if (`is`.read(mPseudoColors[2]) != lenth) {
                Log.d(Companion.TAG, "read file fail ")
            }
            mPseudoColors[2]!![lenth] = 2
            mDualView!!.dualUVCCamera.loadPseudocolor(
                CommonParams.PseudoColorUsbDualType.RAINBOW_MODE,
                mPseudoColors[2],
            )
            `is` = am.open("pseudocolor/Ironbow.bin")
            lenth = `is`.available()
            mPseudoColors[3] = ByteArray(lenth + 1)
            if (`is`.read(mPseudoColors[3]) != lenth) {
                Log.d(Companion.TAG, "read file fail "Test Data"Text", "Test Data")
        }
    }

    fun onViewClicked(view: View?) {}

    override fun onStart() {
        Log.w(Companion.TAG, "onStart"Test Data"onDestroy"Test Data"prex :$preX prey : $preY curx : $curX cury : $curY")
        if (mDualView != null) {
            updateSaveButton()
            val newSrc = ByteArray(8)
            val xSrc = ByteArray(4)
            HexDump.float2byte((curX - preX) / alignScaleX, xSrc)
            System.arraycopy(xSrc, 0, newSrc, 0, 4)
            val ySrc = ByteArray(4)
            HexDump.float2byte((curY - preY) / alignScaleY, ySrc)
            System.arraycopy(ySrc, 0, newSrc, 4, 4)
            mDualView!!.dualUVCCamera.setAlignTranslateParameter(newSrc)
        }
    }

    /**
\1processingdata
     */
    private fun handleAngle(angle: Float) {
        if (!canOperate) {
            return
        }
        Log.d(Companion.TAG, "angle :$angle")
        if (mDualView != null) {
            val newSrc = ByteArray(4)
            val xSrc = ByteArray(4)
            HexDump.float2byte(angle, xSrc)
            System.arraycopy(xSrc, 0, newSrc, 0, 4)
            mDualView!!.dualUVCCamera.setAlignRotateParameter(newSrc)
        }
    }

    /**
\1calibration
     */
    private fun finishAlign(isSavePara: Boolean) {
        if (!canOperate) {
            return
        }
    }

    fun updateSaveButton() {
        if (ivTakePhoto!!.visibility == View.INVISIBLE) {
            ivTakePhoto!!.visibility = View.VISIBLE
            ivTakePhoto!!.setOnClickListener { // 
                val message = Message.obtain()
                message.what = SHOW_LOADING
                message.obj = ""
                mIrDualHandler.sendMessage(message)
                finishSafe(true)
            }
        }
    }

    fun setButtonEnable(isEnable: Boolean) {
        ivTakePhoto!!.setEnabled(isEnable)
    }

    private fun initListener() {
        moveImageView!!.setOnMoveListener { preX, preY, curX, curY ->
            handleMove(
                preX,
                preY,
                curX,
                curY,
            )
        }
    }

    private fun finishSafe(isSavePara: Boolean) {
        Thread {
            finishAlign(isSavePara)
            canOperate = false
            mIrDualHandler.sendEmptyMessage(HIDE_LOADING_FINISH)
        }.start()
    }

    companion object {
        private const val TAG = "ManualStep2Activity"Test Data"ACTION_MOVE isFastClick flag : $flag")
            return flag
        }
    }
}
