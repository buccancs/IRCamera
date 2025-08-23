package com.infisense.usbir.camera

/**
 * USB monitor callback interface stub for external library compatibility
 */
interface USBMonitorCallback {
    fun onAttach()
    fun onGranted() 
    fun onConnect()
    fun onDisconnect()
    fun onDettach()
    fun onCancel()
}