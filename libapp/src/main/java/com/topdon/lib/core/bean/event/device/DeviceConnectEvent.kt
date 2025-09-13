package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * 目标 USB device（即符合 productId 及 vendorId）connectionstateEvent.
 * @param isConnect true-已connection false-已disconnect
 * @param device 若已connection，connection的device
 */
data class DeviceConnectEvent(val isConnect: Boolean, val device: UsbDevice?)
