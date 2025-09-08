package com.topdon.lib.core.tools

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.XLog
import com.topdon.lib.core.bean.event.device.DeviceConnectEvent
import com.topdon.lib.core.bean.event.device.DevicePermissionEvent
import com.topdon.lib.core.broadcast.DeviceBroadcastReceiver
import com.topdon.lib.core.config.DeviceConfig.isHik256
import com.topdon.lib.core.config.DeviceConfig.isTcLiteDevice
import com.topdon.lib.core.config.DeviceConfig.isTcTsDevice
import com.topdon.lib.core.utils.ByteUtils.toBytes
import com.topdon.lib.core.utils.ByteUtils.toHexString
import org.greenrobot.eventbus.EventBus

/**
 * [Chinese text]
 */
object DeviceTools {
    /**
     * [Chinese text] [Chinese text] [Chinese text].
     * [Chinese text]event.
     * [Chinese text].
     */
    fun isConnect(
        isSendConnectEvent: Boolean = false,
        isAutoRequest: Boolean = true,
    ): Boolean {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        for (usbDevice in deviceList.values) {
            if (usbDevice.isTcTsDevice()) {
                return if (usbManager.hasPermission(usbDevice)) {
                    XLog.i("[Chinese text]")
                    if (isSendConnectEvent) {
                        EventBus.getDefault().post(DeviceConnectEvent(true, usbDevice))
                    }
                    true
                } else {
                    XLog.w("[Chinese text]")
                    if (isAutoRequest) {
                        EventBus.getDefault().post(DevicePermissionEvent(usbDevice))
                    }
                    false
                }
            }
        }
        return false
    }

    fun findUsbDevice(): UsbDevice? {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        for (usbDevice in deviceList.values) {
            if (usbDevice.isTcTsDevice()) {
                val productID = usbDevice.productId.toBytes(2).toHexString()
                val vendorID = usbDevice.vendorId.toBytes(2).toHexString()
                XLog.i("[Chinese text]usb[Chinese text] productId:$productID, vendorId:$vendorID, deviceName:${usbDevice.deviceName}")
                return usbDevice
            }
        }
        XLog.i("[Chinese text]${deviceList.size}[Chinese text], [Chinese text]usb[Chinese text]")
        return null
    }

    /**
     * [Chinese text] TC001 Plus [Chinese text].
     */
    fun isTC001PlusConnect(): Boolean {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        var usbCameraNumber = 0
        var isTcTsDev = false
        for (usbDevice in deviceList.values) {
            if ("USB Camera" == usbDevice.productName)
                {
                    usbCameraNumber++
                }
            if (!isTcTsDev)
                {
                    isTcTsDev = usbDevice.isTcTsDevice() && usbManager.hasPermission(usbDevice)
                }
        }
        return isTcTsDev && usbCameraNumber > 1
    }

    /**
     * [Chinese text]TC001 Lite [Chinese text]
     */
    fun isTC001LiteConnect(): Boolean  {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        for (usbDevice in deviceList.values) {
            if (usbDevice.isTcLiteDevice()) {
                return true
            }
        }
        return false
    }

    /**
     * [Chinese text] 256 [Chinese text]
     */
    fun isHikConnect(): Boolean {
        val usbManager: UsbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        for (usbDevice in usbManager.deviceList.values) {
            if (usbDevice.isHik256()) {
                return true
            }
        }
        return false
    }

    /**
     * [Chinese text]usb[Chinese text]
     *
     * UsbManager.requestPermission
     * [Chinese text]android 10[Chinese text]
     * targetSdk 27
     */
    fun requestUsb(
        activity: Activity,
        requestCode: Int,
        device: UsbDevice,
    ) {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val intent = Intent(DeviceBroadcastReceiver.ACTION_USB_PERMISSION)
        val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getBroadcast(activity, requestCode, intent, flag)
        usbManager.requestPermission(device, pendingIntent)
        XLog.i("[Chinese text]usb[Chinese text]")
    }
}
