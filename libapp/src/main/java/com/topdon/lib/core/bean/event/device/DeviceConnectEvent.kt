package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * target USB [Chinese text]([Chinese text] productId [Chinese text] vendorId)[Chinese text]event.
 * @param isConnect true-[Chinese text] false-[Chinese text]
 * @param device [Chinese text], [Chinese text]
 */
data class DeviceConnectEvent(val isConnect: Boolean, val device: UsbDevice?)
