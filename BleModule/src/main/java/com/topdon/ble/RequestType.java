package com.topdon.ble;

/**
 * 请求type
 * <p>
 * date: 2019/8/9 22:10
 * author: bichuanfeng
 */
public enum RequestType {
    /**
     * 开关notification
     */
    SET_NOTIFICATION,
    /**
     * 开关Indication
     */
    SET_INDICATION,
    /**
     * 读特征值
     */
    READ_CHARACTERISTIC,
    /**
     * 读描述符
     */
    READ_DESCRIPTOR,
    /**
     * 读信号强度
     */
    READ_RSSI,
    /**
     * 写特征值
     */
    WRITE_CHARACTERISTIC,
    /**
     * modify最大传输单元
     */
    CHANGE_MTU,
    /**
     * 读物物理层Send器和Receive器
     */
    READ_PHY,
    /**
     * settings物理层Send器和Receive器偏好
     */
    SET_PREFERRED_PHY
}
