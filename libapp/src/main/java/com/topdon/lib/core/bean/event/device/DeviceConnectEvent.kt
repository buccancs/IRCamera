package com.topdon.lib.core.bean.event.device

import android.hardware.usb.UsbDevice

/**
 * [CN_TEXT] USB [CN_TEXT]（[CN_TEXT] productId [CN_TEXT] vendorId）[CN_TEXT]State[CN_TEXT].
 * @param isConnect true-[CN_TEXT] false-[CN_TEXT]
 * @param device [CN_TEXT]，[CN_TEXT]
 */
data class DeviceConnectEvent(val isConnect: Boolean, val device: UsbDevice?)
