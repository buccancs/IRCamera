package com.guide.zm04c.matrix

object ResultCode {
    val TAG = "mobilelibrary"

    // device初始state
    val READY_CONNECT_DEVICE = 1

    // 找到匹配device
    val SUCC_FIND_MATCHED_DEVICE = 2

    // 找到interfaceport
    val SUCC_FIND_DEVICE_INTERFACE = 3

    // deviceconnectioninterfacesuccess
    val SUCC_CONNECT_INTERFACE = 4

    // deviceconnectionsuccess
    val SUCC_FIND_ENDPOINT = 5

    // USBportCommandSendsuccess
    val SUCC_USB_SEND_CMD = 6

    // 找到USBdevice，model不匹配
    val ERROR_FIND_DEVICE_NOT_MATCH = -100

    // 未发现任何device
    val ERROR_NOT_FIND_DEVICE = -101

    // 未找到deviceport
    val ERROR_NOT_FIND_INTERFACE = -102

    // Opendevicefailed
    val ERROR_OPEN_DEVICE_FAILD = -103

    // connectiondevicefailed
    val ERROR_CONNECT_DEVICE_FAILD = -104

    // 未找到device输入输出port号
    val ERROR_FIND_ENDPOINT_FAILD = -105

    // User不同意开启USBPermission
    val ERROR_USE_NOT_AGRREN_PERMISSIONS = -106

    // usbisvalid
    val ERROR_USE_USB_ISVALID = -107

    // USBportCommandSendfailed
    val ERROE_USB_SEND_CMD_FAILD = -108
}
