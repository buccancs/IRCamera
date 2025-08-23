package com.topdon.lib.core.ktbase

import android.content.*
import android.content.pm.ActivityInfo
import android.os.*
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.topdon.lib.core.BaseApplication
import com.topdon.tc001.R
import com.topdon.lib.core.bean.event.SocketStateEvent
import com.topdon.lib.core.bean.event.device.DeviceConnectEvent
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.dialog.LoadingDialog
import com.topdon.lib.core.tools.*
import com.topdon.lib.core.dialog.TipCameraProgressDialog
import com.topdon.lib.core.dialog.TipProgressDialog
import com.topdon.lms.sdk.LMS
import com.topdon.lms.sdk.bean.CommonBean
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

abstract class BaseActivity : RxAppCompatActivity() {

    val TAG = this.javaClass.simpleName

    protected abstract fun initContentView(): Int
    protected abstract fun initView()
    protected abstract fun initData()

    protected var savedInstanceState: Bundle? = null

    protected open fun isLockPortrait(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApplication.instance.activitys.add(this)
        this.savedInstanceState = savedInstanceState
        // Register EventBus with try-catch to handle already registered cases
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
            // Already registered or other EventBus error
        }

        if (isLockPortrait()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        window.navigationBarColor = ContextCompat.getColor(this,R.color.toolbar_16131E)
        setContentView(initContentView())
        initView()
        initData()
        synLogin()
    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, SharedManager.getLanguage(newBase!!)))
    }

    override fun onStart() {
        super.onStart()
        // Register EventBus with try-catch to handle already registered cases
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
            // Already registered or other EventBus error
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }



    override fun onDestroy() {
        cameraDialog?.dismiss()
        super.onDestroy()
        // Unregister EventBus with try-catch to handle not registered cases
        try {
            EventBus.getDefault().unregister(this)
        } catch (e: Exception) {
            // Not registered or other EventBus error
        }
        BaseApplication.instance.activitys.remove(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getConnectState(event: DeviceConnectEvent) {
        if (event.isConnect) {
            connected()
        } else {
            disConnected()
        }
    }
    protected open fun connected() {

    }
    protected open fun disConnected() {

    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSocketConnectState(event: SocketStateEvent) {
        if (event.isConnect) {
            onSocketConnected(event.isTS004)
        } else {
            onSocketDisConnected(event.isTS004)
        }
    }
    protected open fun onSocketConnected(isTS004: Boolean) {

    }
    protected open fun onSocketDisConnected(isTS004: Boolean) {

    }


    private var loadingDialog: LoadingDialog? = null
    fun showLoadingDialog(@StringRes resId: Int = R.string.tip_loading) {
        showLoadingDialog(getString(resId))
    }
    fun showLoadingDialog(text: CharSequence?) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.setTips(text)
        loadingDialog?.show()
    }
    fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }


    private var cameraDialog: TipCameraProgressDialog? = null
    fun showCameraLoading() {
        if (cameraDialog != null && cameraDialog!!.isShowing) {
            return
        }
        if (cameraDialog == null) {
            cameraDialog =
                TipCameraProgressDialog.Builder(this)
                    .setCanceleable(false)
                    .create()
        }
        try {
            if (!(isFinishing && isDestroyed)) {
                cameraDialog?.show()
            }
        }catch (e:Exception){
        }
    }
    fun dismissCameraLoading() {
        if (cameraDialog != null && cameraDialog!!.isShowing) {
            cameraDialog?.dismiss()
        }
    }

    private fun synLogin() {
        // Local mode - no user sync needed
        if (SharedManager.getHasShowClause()) {
            XLog.d("BaseActivity: Local mode - clause shown")
        }
    }

    protected class TakePhotoResult : ActivityResultContract<File, File?>() {
        private lateinit var file: File

        override fun createIntent(context: Context, input: File): Intent {
            file = input
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): File? = if (resultCode == RESULT_OK) file else null
    }
}
