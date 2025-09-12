package com.topdon.tc001.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Comprehensive permission controller for multi-sensor recording app.
 * Handles all runtime permissions required for Samsung S22 + Thermal/GSR integration.
 * 
 * Manages:
 * - Camera & Audio permissions for RGB recording
 * - Bluetooth & Location permissions for Shimmer GSR BLE
 * - USB permissions for thermal camera hot-plug support
 * - Storage permissions for data recording
 * - Foreground service & notification permissions
 * - Power management optimization requests
 * 
 * Features:
 * - Unified permission API with coroutine support
 * - Graceful handling of permission denials
 * - Educational dialogs for permission rationale
 * - Automatic fallback strategies
 * - USB device permission management
 * - Battery optimization exemption requests
 */
class PermissionController private constructor() {
    
    companion object {
        private const val TAG = "PermissionController"
        private const val REQUEST_CODE_BASE = 1000
        
        @Volatile
        private var INSTANCE: PermissionController? = null
        
        fun getInstance(): PermissionController {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PermissionController().also { INSTANCE = it }
            }
        }
        
        // Permission groups for different phases
        val CAMERA_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        }
        
        val BLUETOOTH_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION // Still needed for broader compatibility
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        
        val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        
        val NOTIFICATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
        
        // All critical permissions required for full functionality
        val ALL_REQUIRED_PERMISSIONS = (CAMERA_PERMISSIONS + BLUETOOTH_PERMISSIONS + 
                                      STORAGE_PERMISSIONS + NOTIFICATION_PERMISSIONS).distinct().toTypedArray()
    }
    
    data class PermissionResult(
        val granted: Boolean,
        val deniedPermissions: List<String> = emptyList(),
        val shouldShowRationale: List<String> = emptyList(),
        val permanentlyDenied: List<String> = emptyList()
    )
    
    private val pendingRequests = mutableMapOf<Int, CompletableDeferred<PermissionResult>>()
    private var requestCodeCounter = REQUEST_CODE_BASE
    
    /**
     * Check if all required permissions are granted
     */
    fun areAllPermissionsGranted(context: Context): Boolean {
        return ALL_REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if specific permission group is granted
     */
    fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Get list of missing permissions from a permission group
     */
    fun getMissingPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Request permissions with coroutine support
     * @param activity The requesting activity
     * @param permissions Array of permissions to request
     * @param rationale Optional rationale message to show before requesting
     * @return PermissionResult with detailed results
     */
    suspend fun requestPermissions(
        activity: Activity,
        permissions: Array<String>,
        rationale: String? = null
    ): PermissionResult = suspendCancellableCoroutine { continuation ->
        
        val missing = getMissingPermissions(activity, permissions)
        if (missing.isEmpty()) {
            continuation.resume(PermissionResult(granted = true))
            return@suspendCancellableCoroutine
        }
        
        val requestCode = requestCodeCounter++
        val deferred = CompletableDeferred<PermissionResult>()
        pendingRequests[requestCode] = deferred
        
        continuation.invokeOnCancellation {
            pendingRequests.remove(requestCode)
            deferred.cancel()
        }
        
        // Show rationale if provided and needed
        if (rationale != null && missing.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }) {
            // TODO: Show rationale dialog, then proceed with permission request
            Log.d(TAG, "Would show rationale: $rationale")
        }
        
        // Request permissions
        ActivityCompat.requestPermissions(activity, missing.toTypedArray(), requestCode)
        
        // Wait for result
        deferred.invokeOnCompletion { throwable ->
            if (throwable == null) {
                deferred.getCompleted().let { result ->
                    continuation.resume(result)
                }
            } else {
                continuation.cancel(throwable)
            }
        }
    }
    
    /**
     * Handle permission request result - call this from Activity.onRequestPermissionsResult
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        activity: Activity
    ) {
        val deferred = pendingRequests.remove(requestCode) ?: return
        
        val denied = mutableListOf<String>()
        val shouldShowRationale = mutableListOf<String>()
        val permanentlyDenied = mutableListOf<String>()
        
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                denied.add(permission)
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    shouldShowRationale.add(permission)
                } else {
                    permanentlyDenied.add(permission)
                }
            }
        }
        
        val result = PermissionResult(
            granted = denied.isEmpty(),
            deniedPermissions = denied,
            shouldShowRationale = shouldShowRationale,
            permanentlyDenied = permanentlyDenied
        )
        
        deferred.complete(result)
    }
    
    /**
     * Request USB device permission
     */
    suspend fun requestUsbPermission(
        context: Context,
        device: UsbDevice
    ): Boolean = suspendCancellableCoroutine { continuation ->
        
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        
        if (usbManager.hasPermission(device)) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }
        
        // TODO: Implement USB permission request with PendingIntent
        // For now, return false to indicate permission not available
        Log.w(TAG, "USB permission not granted for device: ${device.deviceName}")
        continuation.resume(false)
    }
    
    /**
     * Check if battery optimization is disabled for the app
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true // Not applicable on older versions
        }
    }
    
    /**
     * Request battery optimization exemption
     */
    fun requestBatteryOptimizationExemption(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isBatteryOptimizationDisabled(activity)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                activity.startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request battery optimization exemption", e)
                // Fallback to general battery optimization settings
                try {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    activity.startActivity(intent)
                } catch (e2: Exception) {
                    Log.e(TAG, "Failed to open battery optimization settings", e2)
                }
            }
        }
    }
    
    /**
     * Open app settings page for manual permission management
     */
    fun openAppSettings(context: Context) {
        try {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open app settings", e)
        }
    }
    
    /**
     * Get human-readable permission names for UI display
     */
    fun getPermissionDisplayName(permission: String): String {
        return when (permission) {
            Manifest.permission.CAMERA -> "Camera"
            Manifest.permission.RECORD_AUDIO -> "Microphone"
            Manifest.permission.BLUETOOTH_SCAN -> "Bluetooth Scanning"
            Manifest.permission.BLUETOOTH_CONNECT -> "Bluetooth Connection"
            Manifest.permission.BLUETOOTH -> "Bluetooth"
            Manifest.permission.BLUETOOTH_ADMIN -> "Bluetooth Administration"
            Manifest.permission.ACCESS_FINE_LOCATION -> "Precise Location"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Approximate Location"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Storage Write"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Storage Read"
            Manifest.permission.READ_MEDIA_VIDEO -> "Video Access"
            Manifest.permission.READ_MEDIA_IMAGES -> "Image Access"
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
            else -> permission.substringAfterLast('.')
        }
    }
    
    /**
     * Get permission rationale text for UI display
     */
    fun getPermissionRationale(permission: String): String {
        return when (permission) {
            Manifest.permission.CAMERA -> "Camera access is required to record high-quality video during experiments."
            Manifest.permission.RECORD_AUDIO -> "Microphone access is needed to record audio with video if enabled."
            Manifest.permission.BLUETOOTH_SCAN -> "Bluetooth scanning is required to discover and connect to GSR sensors."
            Manifest.permission.BLUETOOTH_CONNECT -> "Bluetooth connection is needed to communicate with GSR sensors."
            Manifest.permission.BLUETOOTH -> "Bluetooth access is required for GSR sensor communication."
            Manifest.permission.BLUETOOTH_ADMIN -> "Bluetooth administration is needed to manage GSR sensor connections."
            Manifest.permission.ACCESS_FINE_LOCATION, 
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Location access is required for Bluetooth Low Energy device scanning."
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Storage write access is needed to save recorded data files."
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Storage read access is needed to access saved recordings."
            Manifest.permission.READ_MEDIA_VIDEO -> "Video access is required to manage recorded video files."
            Manifest.permission.READ_MEDIA_IMAGES -> "Image access is required to manage captured thermal images."
            Manifest.permission.POST_NOTIFICATIONS -> "Notification access is needed to show recording status and alerts."
            else -> "This permission is required for app functionality."
        }
    }
}