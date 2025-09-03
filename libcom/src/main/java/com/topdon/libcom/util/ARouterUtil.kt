package com.topdon.libcom.util

import android.app.Activity
import com.topdon.libcom.navigation.NavigationManager

/**
 * Navigation utility using modern NavigationManager
 * author: CaiSongL
 * date: 2024/8/26 19:50
 **/
object ARouterUtil {
    /**
     * 统一跳转红外拍照界面
     * @param activity Activity
     * @param isTC007 Boolean
     */
    fun jumpImagePick(activity: Activity, isTC007: Boolean, imgPath: String) {
        NavigationManager.jumpImagePick(activity, isTC007, imgPath)
    }
}