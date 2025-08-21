package com.topdon.tc001
import com.topdon.tc001.R

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.utils.CommUtils
import com.topdon.lms.sdk.Config
import com.topdon.lms.sdk.LMS
import kotlinx.android.synthetic.main.activity_splash.tv_app_name
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LMS.getInstance().screenOrientation = Config.SCREEN_PORTRAIT
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_splash)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.toolbar_16131E)

        lifecycleScope.launch {
            delay(if (BuildConfig.DEBUG) 3000 else 1000)
            if (SharedManager.getHasShowClause()) {
            // TODO: Replace RouterConfig reference with direct navigation
            } else {
            // TODO: Replace RouterConfig reference with direct navigation
            }
            finish()
        }
        tv_app_name.text = CommUtils.getAppName()
    }

    override fun onBackPressed() {

    }
}