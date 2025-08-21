package com.topdon.lib.core.tools

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Simplified Permission handler for component modules
 */
object Permission {
    
    // Android permission constants
    const val READ_MEDIA_VIDEO = Manifest.permission.READ_EXTERNAL_STORAGE
    const val READ_MEDIA_IMAGES = Manifest.permission.READ_EXTERNAL_STORAGE  
    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val CAMERA = Manifest.permission.CAMERA
    const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
    
    /**
     * Check if permission is granted
     */
    fun checkPermission(activity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Request permissions from activity
     */
    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
    
    /**
     * Check and request permission if needed
     */
    fun checkAndRequestPermission(activity: Activity, permission: String, requestCode: Int): Boolean {
        if (checkPermission(activity, permission)) {
            return true
        } else {
            requestPermissions(activity, arrayOf(permission), requestCode)
            return false
        }
    }
    
    /**
     * Handle permission result
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onGranted: () -> Unit,
        onDenied: () -> Unit = {}
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            onDenied()
        }
    }
}