package com.topdon.lib.core.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.R

/**
 * des:
 * author: CaiSongL
 * date: 2024/2/22 17:06S
 **/
object CommUtils {

    fun getAppName() : String{
        var msg = ""
        var appInfo: ApplicationInfo? = null
        appInfo = Utils.getApp().packageManager
            .getApplicationInfo(
                Utils.getApp().packageName,
                PackageManager.GET_META_DATA
            )
        try {
            msg = appInfo.metaData.getString("app_name")?.toString() ?: ""
        } catch (e: NullPointerException) {
            XLog.w("MetaData is null: ${e.message}")
        } catch (e: ClassCastException) {
            XLog.w("MetaData type error: ${e.message}")
        }
        return msg

    }
}