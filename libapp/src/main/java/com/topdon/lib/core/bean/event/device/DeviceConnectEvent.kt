package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * event USB event（event productId event vendorId）eventStateevent.
 * @param isConnect true-event false-event
 * @param device event，event
 */
data class DeviceConnectEvent(val isConnect: Boolean, val device: UsbDevice?)
