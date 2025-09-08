package com.topdon.ble;

/**
 * [Chinese text]
 * <p>
 * date: 2019/8/9 22:10
 * author: bichuanfeng
 */
public enum RequestType {
    /**
     * [Chinese text]
     */
    SET_NOTIFICATION,
    /**
     * [Chinese text]Indication
     */
    SET_INDICATION,
    /**
     * [Chinese text]
     */
    READ_CHARACTERISTIC,
    /**
     * [Chinese text]
     */
    READ_DESCRIPTOR,
    /**
     * [Chinese text]
     */
    READ_RSSI,
    /**
     * [Chinese text]
     */
    WRITE_CHARACTERISTIC,
    /**
     * [Chinese text]
     */
    CHANGE_MTU,
    /**
     * [Chinese text]
     */
    READ_PHY,
    /**
     * Settings[Chinese text]
     */
    SET_PREFERRED_PHY
}
