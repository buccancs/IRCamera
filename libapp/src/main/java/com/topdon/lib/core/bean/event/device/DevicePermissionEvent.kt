package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * target USB [Chinese text]([Chinese text] productId [Chinese text] vendorId)[Chinese text]event.
 * @param device [Chinese text]
 */
data class DevicePermissionEvent(val device: UsbDevice)
