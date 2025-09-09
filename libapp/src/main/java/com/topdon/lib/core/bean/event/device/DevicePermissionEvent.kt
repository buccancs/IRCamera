package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * [CN_TEXT] USB [CN_TEXT]（[CN_TEXT] productId [CN_TEXT] vendorId）[CN_TEXT].
 * @param device [CN_TEXT]
 */
data class DevicePermissionEvent(val device: UsbDevice)
