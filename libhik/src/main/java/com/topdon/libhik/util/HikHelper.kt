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
 * [Chinese text]implement[Chinese text], [Chinese text].
 *
 * Settings[Chinese text]: 
 * - [setFrameListener] Settings YUV [Chinese text]
 * - [setTempListener] Settingstemperature[Chinese text]
 *
 * Settings[Chinese text]Settings[Chinese text]: 
 * - [onReadyListener]
 *
 * Settings[Chinese text]: 
 * - [onTimeoutListener]
 *
 * [Chinese text]: 
 * - [init] [Chinese text]
 * - [startStream] [Chinese text]
 * - [stopStream] stop[Chinese text]
 * - [release] [Chinese text]
 *
 * [Chinese text] [ComponentActivity] [Chinese text], 
 * [Chinese text] [bind] [Chinese text], [Chinese text]4[Chinese text]
 *
 * [Chinese text] [Chinese text], [Chinese text], [Chinese text] [Chinese text]
 *
 * Created by LCG on 2024/11/19.
 */
object HikHelper {
    /**
     * [Chinese text] Id.
     */
    private var userId: Int = JavaInterface.USB_INVALID_USER_ID
    /**
     * [Chinese text] Id, forstop[Chinese text].
     */
    private var callbackId: Int = JavaInterface.USB_INVALID_CHANNEL

    /**
     * [Chinese text] Job
     */
    private var timeoutJob: Job? = null
    /**
     * [Chinese text].
     */
    private val streamCallBack = MyFStreamCallBack()

    /**
     * [Chinese text] Activity [Chinese text], [Chinese text], stop[Chinese text], [Chinese text]operation.
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
     * [Chinese text] Fragment [Chinese text], [Chinese text], stop[Chinese text], [Chinese text]operation.
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
     * [Chinese text], for[Chinese text] USB_Cleanup
     */
    private var hasInit = false
    /**
     * [Chinese text], [Chinese text].
     * @return true-[Chinese text] false-[Chinese text]
     */
    fun init(context: Context): Boolean {
        if (userId != JavaInterface.USB_INVALID_USER_ID) {// [Chinese text]
            return true
        }

        // [Chinese text]
        if (!hasInit) {
            if (!HikCmdUtil.init()) {// [Chinese text]
                return false
            }
            hasInit = true
        }

        // Settings[Chinese text]
        HikCmdUtil.setLogPath(context.getExternalFilesDir("hkLog"))

        // [Chinese text]
        userId = HikCmdUtil.login(context)
        return userId != JavaInterface.USB_INVALID_USER_ID
    }

    /**
     * [Chinese text]
     */
    @Volatile
    private var wantCheckTimeout = false
    /**
     * [Chinese text].
     */
    suspend fun startStream() {
        if (callbackId == JavaInterface.USB_INVALID_CHANNEL) {
            callbackId = HikCmdUtil.startStream(userId, streamCallBack)
            wantCheckTimeout = true
            timeoutJob = CoroutineScope(Dispatchers.Main).launch {
                while (wantCheckTimeout) {
                    delay(5000) // 5[Chinese text]
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
     * stop[Chinese text], [Chinese text], YUV [Chinese text] temperature[Chinese text] null.
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
     * [Chinese text].
     */
    fun release() {
        if (userId != JavaInterface.USB_INVALID_USER_ID) {// [Chinese text]
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
     * Settings[Chinese text], [Chinese text], [Chinese text]line[Chinese text]! 
     */
    fun setFrameListener(listener : ((ByteArray, ByteArray) -> Unit)) {
        streamCallBack.onFrameListener = listener
    }
    /**
     * Settingstemperature[Chinese text], [Chinese text], [Chinese text]line[Chinese text]! 
     */
    fun setTempListener(listener : ((ByteArray) -> Unit)) {
        streamCallBack.onTempListener = listener
    }

    /**
     * [Chinese text], [Chinese text]eventlistener.
     */
    var onTimeoutListener: (() -> Unit)? = null
    /**
     * [Chinese text], [Chinese text], [Chinese text]Settings.
     */
    var onReadyListener: (() -> Unit)? = null

    /**
     * [Chinese text](YUV+temperature)[Chinese text]
     */
    fun copyFrameData(): ByteArray = streamCallBack.copyFrameArray()

    private class MyFStreamCallBack : FStreamCallBack {
        /**
         * YUV [Chinese text], YUY2 [Chinese text]
         */
        private val yuvArray = ByteArray(256 * 192 * 2)
        /**
         * temperature[Chinese text]
         */
        private val tempArray = ByteArray(256 * 192 * 2)
        /**
         * temperature[Chinese text].
         */
        @Volatile
        var onTempListener: ((ByteArray) -> Unit)? = null
        /**
         * [Chinese text](YUY2+temperature)[Chinese text].
         */
        @Volatile
        var onFrameListener: ((ByteArray, ByteArray) -> Unit)? = null

        /**
         * [Chinese text]
         */
        @Volatile
        var beforeFrameTime: Long = 0

        /**
         * [Chinese text](YUV+temperature)[Chinese text]
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
                XLog.w("[Chinese text]! ${frameInfo.dwBufSize}")
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
     * [Chinese text]: [Chinese text], [Chinese text]Pseudo color[Chinese text], [Chinese text]temperature[Chinese text], [Chinese text], [Chinese text].
     */
    suspend fun initConfig() = withContext(Dispatchers.IO) {
        HikCmdUtil.initConfig(userId)
    }

    /**
     * [Chinese text], [Chinese text], [Chinese text]line[Chinese text]line[Chinese text]
     */
    suspend fun shutter() = withContext(Dispatchers.IO) {
        HikCmdUtil.shutter(userId)
    }

    /**
     * [Chinese text]
     */
    suspend fun setAutoShutter(isAutoShutter: Boolean) = withContext(Dispatchers.IO) {
        HikCmdUtil.setAutoShutter(userId, isAutoShutter)
    }

    /**
     * Settings[Chinese text], [Chinese text] `[0,100]`
     */
    suspend fun setContrast(value: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setContrast(userId, value)
    }

    /**
     * [Chinese text], [Chinese text] `[0,100]`
     */
    suspend fun setEnhanceLevel(value: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setEnhanceLevel(userId, value)
    }

    /**
     * Settings[Chinese text]
     */
    suspend fun setMirror(rotateAngle: Int, isMirror: Boolean) = withContext(Dispatchers.IO) {
        HikCmdUtil.setMirror(userId, rotateAngle, isMirror)
    }

    /**
     * Settings[Chinese text]
     * @param emissivity [Chinese text]range `[1, 100]`
     */
    suspend fun setEmissivity(emissivity: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setEmissivity(userId, emissivity)
    }

    /**
     * Settings[Chinese text], [Chinese text] cm
     * @param distance [Chinese text]range `[30, 200]`
     */
    suspend fun setDistance(distance: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setDistance(userId, distance)
    }

    /**
     * Settings[Chinese text] [Chinese text](-1)/[Chinese text](1)/high[Chinese text](0) level.
     */
    suspend fun setTemperatureMode(@IntRange(-1, 1) mode: Int) = withContext(Dispatchers.IO) {
        HikCmdUtil.setTemperatureMode(userId, mode)
    }

    /**
     * start[Chinese text]
     */
    suspend fun startCorrect() = withContext(Dispatchers.IO) {
        HikCmdUtil.startCorrect(userId)
    }
}