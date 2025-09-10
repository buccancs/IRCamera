package com.guide.zm04c.matrix

object ResultCode {

    val TAG = "mobilelibrary"

    // ImplementationState
    val READY_CONNECT_DEVICE = 1

    // Implementation
    val SUCC_FIND_MATCHED_DEVICE = 2

    // Implementation
    val SUCC_FIND_DEVICE_INTERFACE = 3

    // Implementation
    val SUCC_CONNECT_INTERFACE = 4

    // Implementation
    val SUCC_FIND_ENDPOINT = 5

    //USBdata
    val SUCC_USB_SEND_CMD = 6


    // dataUSBdata，data
    val ERROR_FIND_DEVICE_NOT_MATCH = -100

    // Implementation
    val ERROR_NOT_FIND_DEVICE = -101

    // Implementation
    val ERROR_NOT_FIND_INTERFACE = -102

    // Implementation
    val ERROR_OPEN_DEVICE_FAILD = -103

    // Implementation
    val ERROR_CONNECT_DEVICE_FAILD = -104

    // Implementation
    val ERROR_FIND_ENDPOINT_FAILD = -105

    // ImplementationUSBdata
    val ERROR_USE_NOT_AGRREN_PERMISSIONS = -106

    //usbisvalid
    val ERROR_USE_USB_ISVALID = -107

    //USBdata
    val ERROE_USB_SEND_CMD_FAILD = -108
}