package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 *  USB （ productId  vendorId）.
 * @param device 
 */
data class DevicePermissionEvent(val device: UsbDevice)
