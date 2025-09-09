package com.topdon.lib.core.utils

import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.content.ContextCompat
import com.topdon.lib.core.BaseApplication

/**
 * des: 统一处理android 14的Permission
 * author: CaiSongL
 * date: 2024/9/9 9:45
 **/
object PermissionUtils {

    /**
     * android 14是否授权了部分读取Permission
     * @return Boolean
     */
    fun isVisualUser() : Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                ContextCompat.checkSelfPermission(BaseApplication.instance,
                    READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED
    }

    fun hasCameraPermission() : Boolean{
        return ContextCompat.checkSelfPermission(BaseApplication.instance,
            android.Manifest.permission.CAMERA) == PERMISSION_GRANTED
    }

}