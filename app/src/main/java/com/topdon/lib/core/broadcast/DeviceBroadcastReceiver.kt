package com.topdon.lib.core.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.device.DeviceConnectEvent
import com.topdon.lib.core.config.DeviceConfig.isTcTsDevice
import com.topdon.lib.core.tools.DeviceTools
import org.greenrobot.eventbus.EventBus

class DeviceBroadcastReceiver : BroadcastReceiver() {


    companion object {
        const val ACTION_USB_PERMISSION = "com.topdon.topInfrared.USB_PERMISSION"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }
        when (intent.action) {
        }

        if (intent.action == ACTION_USB_PERMISSION) {
            DeviceTools.isConnect(isSendConnectEvent = true, isAutoRequest = false)//重新确认usb连接
        } else {
            handleUsbEvent(intent)
        }
    }


    private fun handleUsbEvent(intent: Intent) {
        val usbDevice: UsbDevice?
        try {
            usbDevice = intent.extras!!["device"] as UsbDevice?
        } catch (e: Exception) {
            return
        }
        if (usbDevice == null) {
            return
        }
        if (usbDevice.isTcTsDevice()) {
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == intent.action) {//已连接
                DeviceTools.isConnect(isSendConnectEvent = true, isAutoRequest = true)
            }
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {//已断开
                EventBus.getDefault().post(DeviceConnectEvent(false, null))
            }
        }
    }

}