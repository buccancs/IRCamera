package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * event USB event（event productId event vendorId）event.
 * @param device event
 */
data class DevicePermissionEvent(val device: UsbDevice)
