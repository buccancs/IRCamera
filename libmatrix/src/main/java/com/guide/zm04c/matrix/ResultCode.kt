package com.guide.zm04c.matrix

object ResultCode {

    val TAG = "mobilelibrary"

    //[CN_TEXT]State
    val READY_CONNECT_DEVICE = 1

    //[CN_TEXT]
    val SUCC_FIND_MATCHED_DEVICE = 2

    //[CN_TEXT]
    val SUCC_FIND_DEVICE_INTERFACE = 3

    //[CN_TEXT]
    val SUCC_CONNECT_INTERFACE = 4

    //[CN_TEXT]
    val SUCC_FIND_ENDPOINT = 5

    //USB[CN_TEXT]
    val SUCC_USB_SEND_CMD = 6


    // [CN_TEXT]USB[CN_TEXT]，[CN_TEXT]
    val ERROR_FIND_DEVICE_NOT_MATCH = -100

    //[CN_TEXT]
    val ERROR_NOT_FIND_DEVICE = -101

    //[CN_TEXT]
    val ERROR_NOT_FIND_INTERFACE = -102

    //[CN_TEXT]
    val ERROR_OPEN_DEVICE_FAILD = -103

    //[CN_TEXT]
    val ERROR_CONNECT_DEVICE_FAILD = -104

    //[CN_TEXT]
    val ERROR_FIND_ENDPOINT_FAILD = -105

    //[CN_TEXT]USB[CN_TEXT]
    val ERROR_USE_NOT_AGRREN_PERMISSIONS = -106

    //usbisvalid
    val ERROR_USE_USB_ISVALID = -107

    //USB[CN_TEXT]
    val ERROE_USB_SEND_CMD_FAILD = -108
}