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
import com.topdon.lib.core.config.DeviceConfig.isTcLiteDevice
import com.topdon.lib.core.config.DeviceConfig.isTcTsDevice
import com.topdon.lib.core.utils.ByteUtils.toBytes
import com.topdon.lib.core.utils.ByteUtils.toHexString
import org.greenrobot.eventbus.EventBus

object DeviceTools {

     *   .
     * .
     * .
    /**
     * Function description.
     */
    fun isConnect(isSendConnectEvent: Boolean = false, isAutoRequest: Boolean = true): Boolean {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        for (usbDevice in deviceList.values) {
            if (usbDevice.isTcTsDevice()) {
                return if (usbManager.hasPermission(usbDevice)) {
                    XLog.i("")
                    if (isSendConnectEvent) {
                        EventBus.getDefault().post(DeviceConnectEvent(true, usbDevice))
                    }
                    true
                } else {
                    XLog.w("")
                    if (isAutoRequest) {
                        EventBus.getDefault().post(DevicePermissionEvent(usbDevice))
                    }
                    false
                }
            }
        }
        return false
    }

    /**
     * Function description.
     */
    fun findUsbDevice(): UsbDevice? {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        for (usbDevice in deviceList.values) {
            if (usbDevice.isTcTsDevice()) {
                val productID = usbDevice.productId.toBytes(2).toHexString()
                val vendorID = usbDevice.vendorId.toBytes(2).toHexString()
                XLog.i("usb productId:${productID}, vendorId:${vendorID}, deviceName:${usbDevice.deviceName}")
                return usbDevice
            }
        }
        XLog.i("${deviceList.size}, usb")
        return null
    }

     *  TC001 Plus .
    /**
     * Function description.
     */
    fun isTC001PlusConnect(): Boolean {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        var usbCameraNumber = 0
        var isTcTsDev = false
        for (usbDevice in deviceList.values) {
            if ("USB Camera" == usbDevice.productName){
                usbCameraNumber ++
            }
            if (!isTcTsDev){
                isTcTsDev = usbDevice.isTcTsDevice() && usbManager.hasPermission(usbDevice)
            }
        }
        return isTcTsDev && usbCameraNumber > 1
    }

     * TC001 Lite
    /**
     * Function description.
     */
    fun isTC001LiteConnect() : Boolean{
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        for (usbDevice in deviceList.values) {
            if (usbDevice.isTcLiteDevice()) {
                return true
            }
        }
        return false
    }


     * usb
     * UsbManager.requestPermission
     * android 10
     * targetSdk 27
    /**
     * Function description.
     */
    fun requestUsb(activity: Activity, requestCode: Int, device: UsbDevice) {
        val usbManager = Utils.getApp().getSystemService(Context.USB_SERVICE) as UsbManager
        val intent = Intent(DeviceBroadcastReceiver.ACTION_USB_PERMISSION)
        val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getBroadcast(activity, requestCode, intent, flag)
        usbManager.requestPermission(device, pendingIntent)
        XLog.i("usb")
    }


}