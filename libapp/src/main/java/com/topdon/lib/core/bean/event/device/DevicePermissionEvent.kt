package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * 目标 USB device（即符合 productId 及 vendorId）已connection但需要进行Permission申请Event.
 * @param device 已connection但没有Authorization的device
 */
data class DevicePermissionEvent(val device: UsbDevice)
