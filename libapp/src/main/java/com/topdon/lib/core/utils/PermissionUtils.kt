package com.topdon.lib.core.utils

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.content.ContextCompat
import com.topdon.lib.core.BaseApplication

/**
 * des: 统一处理Android权限，支持Android 14和Bluetooth权限
 * author: CaiSongL
 * date: 2024/9/9 9:45
 **/
object PermissionUtils {
    
    /**
     * Storage permissions based on API level
     */
    fun getStoragePermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= 34 -> { // Android 14
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }
            Build.VERSION.SDK_INT >= 33 -> { // Android 13
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            else -> { // Android 12 and below
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
    
    /**
     * Bluetooth permissions based on API level
     */
    fun getBluetoothPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }
    
    /**
     * All required permissions for thermal camera operations
     */
    fun getAllRequiredPermissions(): Array<String> {
        val storagePerms = getStoragePermissions()
        val bluetoothPerms = getBluetoothPermissions()
        val basicPerms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        return storagePerms + bluetoothPerms + basicPerms
    }

    /**
     * android 14是否授权了部分读取权限
     * @return Boolean
     */
    fun isVisualUser(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            ContextCompat.checkSelfPermission(
                BaseApplication.instance,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            ) == PERMISSION_GRANTED
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            BaseApplication.instance,
            Manifest.permission.CAMERA,
        ) == PERMISSION_GRANTED
    }
    
    /**
     * Check if all storage permissions are granted
     */
    fun hasStoragePermissions(): Boolean {
        // For Android 14, check if we have either full access or partial access
        return if (Build.VERSION.SDK_INT >= 34) {
            val hasPartialAccess = ContextCompat.checkSelfPermission(
                BaseApplication.instance,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PERMISSION_GRANTED
            
            val hasFullAccess = getStoragePermissions().dropLast(1).all { permission ->
                ContextCompat.checkSelfPermission(
                    BaseApplication.instance,
                    permission
                ) == PERMISSION_GRANTED
            }
            
            hasPartialAccess || hasFullAccess
        } else {
            getStoragePermissions().all { permission ->
                ContextCompat.checkSelfPermission(
                    BaseApplication.instance,
                    permission
                ) == PERMISSION_GRANTED
            }
        }
    }
    
    /**
     * Check if all Bluetooth permissions are granted
     */
    fun hasBluetoothPermissions(): Boolean {
        return getBluetoothPermissions().all { permission ->
            ContextCompat.checkSelfPermission(
                BaseApplication.instance,
                permission
            ) == PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if all required permissions are granted
     */
    fun hasAllRequiredPermissions(): Boolean {
        return getAllRequiredPermissions().all { permission ->
            ContextCompat.checkSelfPermission(
                BaseApplication.instance,
                permission
            ) == PERMISSION_GRANTED
        }
    }
    
    /**
     * Get list of missing permissions for a specific type
     */
    fun getMissingPermissions(): List<String> {
        return getAllRequiredPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(
                BaseApplication.instance,
                permission
            ) != PERMISSION_GRANTED
        }
    }
    
    /**
     * Get missing storage permissions only
     */
    fun getMissingStoragePermissions(): List<String> {
        return getStoragePermissions().filter { permission ->
            ContextCompat.checkSelfPermission(
                BaseApplication.instance,
                permission
            ) != PERMISSION_GRANTED
        }
    }
    
    /**
     * Get missing bluetooth permissions only
     */
    fun getMissingBluetoothPermissions(): List<String> {
        return getBluetoothPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(
                BaseApplication.instance,
                permission
            ) != PERMISSION_GRANTED
        }
    }
}
