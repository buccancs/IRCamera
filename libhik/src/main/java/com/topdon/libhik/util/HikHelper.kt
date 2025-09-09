package com.topdon.libhik.util

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.hcusbsdk.Interface.FStreamCallBack
import com.hcusbsdk.Interface.JavaInterface
import com.hcusbsdk.Interface.USB_FRAME_INFO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays

/**
 * [CN_TEXT]Core[CN_TEXT]、[CN_TEXT].
 *
 * Settings[CN_TEXT]：
 * - [setFrameListener] Settings YUV [CN_TEXT]
 * - [setTempListener] Settings[CN_TEXT]
 *
 * Settings[CN_TEXT]Settings[CN_TEXT]：
 * - [onReadyListener]
 *
 * Settings[CN_TEXT]：
 * - [onTimeoutListener]
 *
 * [CN_TEXT]：
 * - [init] [CN_TEXT]
 * - [startStream] [CN_TEXT]
 * - [stopStream] [CN_TEXT]
 * - [release] [CN_TEXT]
 *
 * [CN_TEXT] [ComponentActivity] [CN_TEXT]，
 * [CN_TEXT] [bind] [CN_TEXT]，[CN_TEXT]4[CN_TEXT]
 *
 * [CN_TEXT] [CN_TEXT]、Rotate、[CN_TEXT] [CN_TEXT]
 *
 * Created by LCG on 2024/11/19.
 */
object HikHelper {
    /**
     * Current[CN_TEXT] Id.
     */
    private var userId: Int = JavaInterface.USB_INVALID_USER_ID
    /**
     * [CN_TEXT] Id，[CN_TEXT].
     */
    private var callbackId: Int = JavaInterface.USB_INVALID_CHANNEL

    /**
     * [CN_TEXT] Job
     */
    private var timeoutJob: Job? = null
    /**
     * [CN_TEXT].
     */
    private val streamCallBack = MyFStreamCallBack()


    /**
     * [CN_TEXT]Specified Activity [CN_TEXT]、[CN_TEXT]、[CN_TEXT]、[CN_TEXT].
     */
    fun bind(activity: ComponentActivity) {
        init(activity)
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                owner.lifecycleScope.launch {
                    startStream()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                stopStream()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                release()
            }
        })
    }

    /**
     * [CN_TEXT]Specified Fragment [CN_TEXT]、[CN_TEXT]、[CN_TEXT]、[CN_TEXT].
     */
    fun bind(fragment: Fragment) {
        init(fragment.requireContext())
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                owner.lifecycleScope.launch {
                    startStream()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                stopStream()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                release()
            }
        })
    }

    /**
     * [CN_TEXT]，[CN_TEXT] USB_Cleanup
     */
    private var hasInit = false
    /**
     * [CN_TEXT]，[CN_TEXT].
     * @return true-[CN_TEXT] false-[CN_TEXT]
     */
    fun init(context: Context): Boolean {
        if (userId != JavaInterface.USB_INVALID_USER_ID) {//[CN_TEXT]
            return true
        }

        //[CN_TEXT]
        if (!hasInit) {
            if (!HikCmdUtil.init()) {//[CN_TEXT]
                return false
            }
            hasInit = true
        }

        //Settings[CN_TEXT]
        HikCmdUtil.setLogPath(context.getExternalFilesDir("hkLog"))

        //[CN_TEXT]
        userId = HikCmdUtil.login(context)
        return userId != JavaInterface.USB_INVALID_USER_ID
    }

    /**
     * [CN_TEXT]
     */
    @Volatile
    private var wantCheckTimeout = false
    /**
     * [CN_TEXT].
     */
    suspend fun startStream() {
        if (callbackId == JavaInterface.USB_INVALID_CHANNEL) {
            callbackId = HikCmdUtil.startStream(userId, streamCallBack)
            wantCheckTimeout = true
            timeoutJob = CoroutineScope(Dispatchers.Main).launch {
                while (wantCheckTimeout) {
                    delay(5000) //5[CN_TEXT]
                    if (System.currentTimeMillis() - streamCallBack.beforeFrameTime > 5000) {
                        wantCheckTimeout = false
                        onTimeoutListener?.invoke()
                    }
                }
            }
            onReadyListener?.invoke()
        }
    }

    /**
     * [CN_TEXT]，Note，YUV [CN_TEXT] [CN_TEXT] null.
     */
    fun stopStream() {
        if (callbackId != JavaInterface.USB_INVALID_CHANNEL) {
            timeoutJob?.cancel()
            HikCmdUtil.removeStreamCallback(userId, callbackId)
            callbackId = JavaInterface.USB_INVALID_CHANNEL
            wantCheckTimeout = false
        }
    }

    /**
     * [CN_TEXT].
     */
    fun release() {
        if (userId != JavaInterface.USB_INVALID_USER_ID) {//[CN_TEXT]
            JavaInterface.getInstance().USB_Logout(userId)
            userId = JavaInterface.USB_INVALID_USER_ID
        }
        if (hasInit) {
            JavaInterface.getInstance().USB_Cleanup()
            hasInit = false
        }
        streamCallBack.onTempListener = null
        streamCallBack.onFrameListener = null
        onTimeoutListener = null
    }

    /**
     * Settings[CN_TEXT]，Note，[CN_TEXT]！
     */
    fun setFrameListener(listener : ((ByteArray, ByteArray) -> Unit)) {
        streamCallBack.onFrameListener = listener
    }
    /**
     * Settings[CN_TEXT]，Note，[CN_TEXT]！
     */
    fun setTempListener(listener : ((ByteArray) -> Unit)) {
        streamCallBack.onTempListener = listener
    }

    /**
     * [CN_TEXT]，[CN_TEXT].
     */
    var onTimeoutListener: (() -> Unit)? = null
    /**
     * Core[CN_TEXT]，[CN_TEXT]，[CN_TEXT]Settings.
     */
    var onReadyListener: (() -> Unit)? = null

    /**
     * [CN_TEXT]（YUV+[CN_TEXT]）[CN_TEXT]
     */
    fun copyFrameData(): ByteArray = streamCallBack.copyFrameArray()


    private class MyFStreamCallBack : FStreamCallBack {
        /**
         * YUV [CN_TEXT]，YUY2 [CN_TEXT]
         */
        private val yuvArray = ByteArray(256 * 192 * 2)
        /**
         * [CN_TEXT]
         */
        private val tempArray = ByteArray(256 * 192 * 2)
        /**
         * [CN_TEXT].
         */
        @Volatile
        var onTempListener: ((ByteArray) -> Unit)? = null
        /**
         * [CN_TEXT]（YUY2+[CN_TEXT]）[CN_TEXT].
         */
        @Volatile
        var onFrameListener: ((ByteArray, ByteArray) -> Unit)? = null

        /**
         * [CN_TEXT]
         */
        @Volatile
        var beforeFrameTime: Long = 0

        /**
         * [CN_TEXT]（YUV+[CN_TEXT]）[CN_TEXT]
         */
        fun copyFrameArray(): ByteArray = synchronized(this) {
            val frameArray = ByteArray(256 * 192 * 4)
            System.arraycopy(yuvArray, 0, frameArray, 0, yuvArray.size)
            System.arraycopy(tempArray, 0, frameArray, yuvArray.size, tempArray.size)
            frameArray
        }

        override fun invoke(handle: Int, frameInfo: USB_FRAME_INFO) {
            beforeFrameTime = System.currentTimeMillis()
            if (frameInfo.dwBufSize != 4640 + 256 * 192 * 4) {
                XLog.w("[CN_TEXT]！${frameInfo.dwBufSize}")
                return
            }
            val dataArray: ByteArray = Arrays.copyOf(frameInfo.pBuf, frameInfo.dwBufSize)


            /*val frameHead = FrameHead(dataArray)
            XLog.d(frameHead.toString())
            for (i in frameHead.tempRuleList.indices) {
                XLog.v(frameHead.tempRuleList[i].toString())
                XLog.v(dataArray.buildPrintStr(216 + i * 208, 88))
            }*/

            synchronized(this) {
                System.arraycopy(dataArray, dataArray.size - yuvArray.size, yuvArray, 0, yuvArray.size)
                for (i in tempArray.indices step 2) {
                    val newValue = ((dataArray[4640 + i].toInt() and 0xff) or (dataArray[4640 + i + 1].toInt() and 0xff shl 8)) + 14272
                    tempArray[i] = (newValue and 0xff).toByte()
                    tempArray[i + 1] = (newValue shr 8 and 0xff).toByte()
                }
            }
            onTempListener?.invoke(tempArray)
            onFrameListener?.invoke(yuvArray, tempArray)
        }
    }

    /**
     * [CN_TEXT]：[CN_TEXT]、[CN_TEXT]Pseudo-color[CN_TEXT]White hot、[CN_TEXT]、[CN_TEXT]、[CN_TEXT].
     */
    suspend fun initConfig() = withContext(Dispatchers.IO) {
        HikCmdUtil.initConfig(userId)
    }

    /**
     * [CN_TEXT]，[CN_TEXT]，[CN_TEXT]
     */
    suspend fun shutter() = withContext(Dispatchers.IO) {
        HikCmdUtil.shutter(userId)
    }

    /**
     * [CN_TEXT]
     */
    suspend fun setAutoShutter(isAutoShutter: Boolean) = withContext(Dispatchers.IO) {
        HikCmdUtil.setAutoShutter(userId, isAutoShutter)
    }

    /**
     * Settings[CN_TEXT]，[CN_TEXT] `[0,100]`
     */
    suspend fun setContrast(value: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setContrast(userId, value)
    }

    /**
     * [CN_TEXT]，[CN_TEXT] `[0,100]`
     */
    suspend fun setEnhanceLevel(value: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setEnhanceLevel(userId, value)
    }

    /**
     * Settings[CN_TEXT]
     */
    suspend fun setMirror(rotateAngle: Int, isMirror: Boolean) = withContext(Dispatchers.IO) {
        HikCmdUtil.setMirror(userId, rotateAngle, isMirror)
    }

    /**
     * Settings[CN_TEXT]
     * @param emissivity [CN_TEXT] `[1, 100]`
     */
    suspend fun setEmissivity(emissivity: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setEmissivity(userId, emissivity)
    }

    /**
     * SettingsTemperature measurement[CN_TEXT]，[CN_TEXT] cm
     * @param distance [CN_TEXT] `[30, 200]`
     */
    suspend fun setDistance(distance: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setDistance(userId, distance)
    }

    /**
     * SettingsTemperature measurement [CN_TEXT](-1)/Normal temperature(1)/High temperature(0) [CN_TEXT].
     */
    suspend fun setTemperatureMode(@IntRange(-1, 1) mode: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setTemperatureMode(userId, mode)
    }

    /**
     * [CN_TEXT]
     */
    suspend fun startCorrect() = withContext(Dispatchers.IO) {
        HikCmdUtil.startCorrect(userId)
    }
}