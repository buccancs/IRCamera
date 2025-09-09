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
    /**
     * [CN_TEXT] RECORD_AUDIO [CN_TEXT].
     */
    fun requestRecordAudio(
        context: Context,
        callback: () -> Unit,
    ) = request(context, Type.RECORD_AUDIO, callback)

    /**
     * [CN_TEXT] CAMERA [CN_TEXT].
     */
    fun requestCamera(
        context: Context,
        callback: () -> Unit,
    ) = request(context, Type.CAMERA, callback)

    /**
     * [CN_TEXT] ACCESS_FINE_LOCATION [CN_TEXT].
     */
    fun requestLocation(
        context: Context,
        callback: () -> Unit,
    ) = request(context, Type.LOCATION, callback)

    /**
     * [CN_TEXT] [CN_TEXT] [CN_TEXT].
     */
    fun requestImageRead(
        context: Context,
        callback: () -> Unit,
    ) = request(context, Type.IMAGE, callback)

    /**
     * Android 10 [CN_TEXT]：[CN_TEXT]、[CN_TEXT]
     *
     * Android 11、Android 12、Android 12L：[CN_TEXT]
     *
     * Android 13 [CN_TEXT]：[CN_TEXT]-[CN_TEXT]、[CN_TEXT]-[CN_TEXT]
     */
    fun requestFile(
        context: Context,
        callback: () -> Unit,
    ) = request(context, Type.FILE, callback)

    private enum class Type { RECORD_AUDIO, CAMERA, LOCATION, IMAGE, FILE }

    private fun request(
        context: Context,
        type: Type,
        callback: () -> Unit,
    ) {
        val permissions: List<String> =
            when (type) {
                Type.RECORD_AUDIO -> listOf(Permission.RECORD_AUDIO)
                Type.CAMERA -> listOf(Permission.CAMERA)
                Type.LOCATION -> listOf(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION)
                Type.IMAGE ->
                    listOf(
                        if (context.applicationInfo.targetSdkVersion < 33) Permission.READ_EXTERNAL_STORAGE else Permission.READ_MEDIA_IMAGES,
                    )
                Type.FILE ->
                    if (context.applicationInfo.targetSdkVersion < 30) { // Android 10[CN_TEXT]
                        listOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    } else if (context.applicationInfo.targetSdkVersion < 33) { // Android 13[CN_TEXT]
                        listOf(Permission.READ_EXTERNAL_STORAGE)
                    } else { // Android 13[CN_TEXT]
                        listOf(Permission.READ_MEDIA_VIDEO, Permission.READ_MEDIA_IMAGES)
                    }
            }

        XXPermissions.with(context)
            .permission(permissions)
            .request(
                object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>,
                        allGranted: Boolean,
                    ) {
                        if (allGranted) {
                            callback.invoke()
                        } else {
                            TToast.shortToast(context, R.string.scan_ble_tip_authorize)
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        never: Boolean,
                    ) {
                        if (never) {
                            val tipsResId: Int =
                                when (type) {
                                    Type.RECORD_AUDIO -> R.string.app_microphone_content
                                    Type.CAMERA -> R.string.app_camera_content
                                    Type.LOCATION -> R.string.app_location_content
                                    Type.IMAGE -> R.string.app_album_content
                                    Type.FILE -> R.string.app_storage_content
                                }
                            if (BaseApplication.instance.isDomestic()) { // [CN_TEXT]
                                TToast.shortToast(context, tipsResId)
                            } else {
                                TipDialog.Builder(context)
                                    .setTitleMessage(context.getString(R.string.app_tip))
                                    .setMessage(tipsResId)
                                    .setPositiveListener(R.string.app_open) {
                                        AppUtils.launchAppDetailsSettings()
                                    }
                                    .setCancelListener(R.string.app_cancel) {
                                    }
                                    .setCanceled(true)
                                    .create().show()
                            }
                        } else {
                            TToast.shortToast(context, R.string.scan_ble_tip_authorize)
                        }
                    }
                },
            )
    }

    /**
     * [CN_TEXT] ACCESS_FINE_LOCATION、BLUETOOTH_SCAN、BLUETOOTH_CONNECT [CN_TEXT]。
     * [CN_TEXT] Android12 [CN_TEXT]。
     */
    fun hasBtPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < 31) { // [CN_TEXT] Android12
            XXPermissions.isGranted(context, Permission.ACCESS_FINE_LOCATION)
        } else {
            XXPermissions.isGranted(context, Permission.ACCESS_FINE_LOCATION, Permission.BLUETOOTH_SCAN, Permission.BLUETOOTH_CONNECT)
        }
    }

    /**
     * [CN_TEXT] Android12 [CN_TEXT]，[CN_TEXT] BLUETOOTH_SCAN、BLUETOOTH_CONNECT [CN_TEXT]
     * @param isBtFirst true-[CN_TEXT] false-[CN_TEXT]
     */
    fun requestBluetooth(
        context: Context,
        isBtFirst: Boolean,
        callback: Callback,
    ) {
        val permissionList: List<String> =
            if (Build.VERSION.SDK_INT < 31) { // [CN_TEXT] Android12
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
                            // [CN_TEXT]Settings[CN_TEXT]
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
         * [CN_TEXT]，[CN_TEXT] [CN_TEXT] [CN_TEXT] [CN_TEXT].
         */
        fun onResult(allGranted: Boolean)

        /**
         * [CN_TEXT]，[CN_TEXT] [CN_TEXT] [CN_TEXT] [CN_TEXT] [CN_TEXT].
         */
        fun onNever(isJump: Boolean)
    }
}
