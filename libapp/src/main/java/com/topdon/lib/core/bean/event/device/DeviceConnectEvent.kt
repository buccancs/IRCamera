package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 *  USB （ productId  vendorId）state.
 * @param isConnect true- false-
 * @param device ，
 */
data class DeviceConnectEvent(val isConnect: Boolean, val device: UsbDevice?)
