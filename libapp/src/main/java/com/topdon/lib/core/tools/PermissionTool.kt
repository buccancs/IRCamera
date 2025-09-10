package com.topdon.lib.core.tools

import android.content.Context
import android.os.Build
import com.blankj.utilcode.util.AppUtils
import com.elvishew.xlog.XLog
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.R
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lms.sdk.weiget.TToast

object PermissionTool {

    fun requestBluetooth(
        context: Context,
        isBtFirst: Boolean,
        callback: Callback,
    ) {
        val permissionList: List<String> =
            if (Build.VERSION.SDK_INT < 31) { // Utility function Android12
                arrayListOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)
            } else {
                arrayListOf(
                    Permission.ACCESS_FINE_LOCATION,
                    Permission.ACCESS_COARSE_LOCATION,
                    Permission.BLUETOOTH_SCAN,
                    Permission.BLUETOOTH_CONNECT,
                )
            }

        XXPermissions.with(context)
            .permission(permissionList)
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        XLog.i("onGranted($allGranted)")
                        callback.onResult(allGranted)
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        never: Boolean,
                    ) {
                        XLog.i("onDenied($never)")
                        if (never) {
                            var isBtNever = false
                            var isLocationNever = false
                            for (permission in permissions) {
                                if (permission == Permission.BLUETOOTH_SCAN || permission == Permission.BLUETOOTH_CONNECT) {
                                    isBtNever = true
                                }
                                if (permission == Permission.ACCESS_FINE_LOCATION || permission == Permission.ACCESS_COARSE_LOCATION) {
                                    isLocationNever = true
                                }
                            }
                            // Utility functionSettingsutility
                            TipDialog.Builder(context)
                                .setTitleMessage(context.getString(R.string.app_tip))
                                .setMessage(
                                    if (!isLocationNever || (isBtNever && isBtFirst)) R.string.app_bluetooth_content else R.string.app_location_content,
                                )
                                .setPositiveListener(R.string.app_open) {
                                    XXPermissions.startPermissionActivity(context, permissions)
                                    callback.onNever(true)
                                }
                                .setCancelListener(R.string.app_cancel) {
                                    callback.onNever(false)
                                }
                                .setCanceled(true)
                                .create().show()
                        } else {
                            callback.onResult(false)
                        }
                    }
                },
            )
    }

    interface Callback {
        /**
         * utility，utility utility utility utility.
         */
        fun onResult(allGranted: Boolean)

        /**
         * utility，utility utility utility utility utility.
         */
        fun onNever(isJump: Boolean)
    }
}
