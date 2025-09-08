package com.guide.zm04c.matrix

object ResultCode {

    /** TAG property */
    val TAG = "mobilelibrary"

    /** READY_CONNECT_DEVICE property */
    val READY_CONNECT_DEVICE = 1

    /** SUCC_FIND_MATCHED_DEVICE property */
    val SUCC_FIND_MATCHED_DEVICE = 2

    /** SUCC_FIND_DEVICE_INTERFACE property */
    val SUCC_FIND_DEVICE_INTERFACE = 3

    /** SUCC_CONNECT_INTERFACE property */
    val SUCC_CONNECT_INTERFACE = 4

    /** SUCC_FIND_ENDPOINT property */
    val SUCC_FIND_ENDPOINT = 5

    //USB
    /** SUCC_USB_SEND_CMD property */
    val SUCC_USB_SEND_CMD = 6


    // USB
    /** ERROR_FIND_DEVICE_NOT_MATCH property */
    val ERROR_FIND_DEVICE_NOT_MATCH = -100

    /** ERROR_NOT_FIND_DEVICE property */
    val ERROR_NOT_FIND_DEVICE = -101

    /** ERROR_NOT_FIND_INTERFACE property */
    val ERROR_NOT_FIND_INTERFACE = -102

    /** ERROR_OPEN_DEVICE_FAILD property */
    val ERROR_OPEN_DEVICE_FAILD = -103

    /** ERROR_CONNECT_DEVICE_FAILD property */
    val ERROR_CONNECT_DEVICE_FAILD = -104

    /** ERROR_FIND_ENDPOINT_FAILD property */
    val ERROR_FIND_ENDPOINT_FAILD = -105

    //USB
    /** ERROR_USE_NOT_AGRREN_PERMISSIONS property */
    val ERROR_USE_NOT_AGRREN_PERMISSIONS = -106

    //usbisvalid
    /** ERROR_USE_USB_ISVALID property */
    val ERROR_USE_USB_ISVALID = -107

    //USB
    /** ERROE_USB_SEND_CMD_FAILD property */
    val ERROE_USB_SEND_CMD_FAILD = -108
}