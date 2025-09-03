package com.topdon.libcom.util

import android.app.Activity
import com.alibaba.android.arouter.launcher.ARouter
import com.topdon.lib.core.common.WifiSaveSettingUtil
import com.topdon.lib.core.config.ExtraKeyConfig.RESULT_IMAGE_PATH
import com.topdon.lib.core.config.RouterConfig
import com.topdon.lib.core.tools.DeviceTools
import com.topdon.lib.core.tools.ToastTools

/**
 * des:
 * author: CaiSongL
 * date: 2024/8/26 19:50
 **/
object ARouterUtil {
    /**
     * 统一跳转红外拍照界面 - Only TC001 supported
     * @param activity Activity
     * @param imgPath String
     */
    fun jumpImagePick(activity: Activity, imgPath : String){
        // Only TC001 variants are supported
        if (DeviceTools.isTC001PlusConnect()){
            ARouter.getInstance().build(RouterConfig.IR_IMG_PICK_PLUS).withString(RESULT_IMAGE_PATH,imgPath).navigation(activity,101)
        }else if (DeviceTools.isTC001LiteConnect()){
            ARouter.getInstance().build(RouterConfig.IR_IMG_PICK_LITE).withString(RESULT_IMAGE_PATH,imgPath).navigation(activity,101)
        } else{
            ARouter.getInstance().build(RouterConfig.IR_IMG_PICK).withString(RESULT_IMAGE_PATH,imgPath).navigation(activity,101)
        }
    }

}