package com.topdon.tc001

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.topdon.lib.core.common.SharedManager
import com.topdon.module.thermal.ir.activity.IRMainActivity
import com.topdon.tc001.app.App

class BlankDevActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SharedManager.getHasShowClause()) {
            if (!App.instance.activityNameList.contains(IRMainActivity::class.simpleName)){
            // TODO: Replace RouterConfig reference with direct navigation
                if (!SharedManager.isConnectAutoOpen){
            // TODO: Replace RouterConfig reference with direct navigation
                }
            }
            finish()
        } else {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
    }

    fun isActivityExists(context: Context, activityClassName: String): Boolean {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            ?: return false
        val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)
        for (task in tasks) {
            if (task.topActivity != null && task.topActivity!!.className == activityClassName) {
                return true
            }
            if (task.baseActivity != null && task.baseActivity!!.className == activityClassName) {
                return true
            }
        }
        return false
    }
}