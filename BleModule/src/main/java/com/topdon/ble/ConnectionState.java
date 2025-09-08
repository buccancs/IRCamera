package com.topdon.ble;

/**
 * [Chinese text]
 * <p>
 * date: 2019/8/12 14:26
 * author: bichuanfeng
 */
public enum ConnectionState {
    /**
     * [Chinese text]
     */
    DISCONNECTED,
    /**
     * [Chinese text]
     */
    CONNECTING,
    /**
     * [Chinese text]
     */
    SCANNING_FOR_RECONNECTION,
    /**
     * [Chinese text], [Chinese text]
     */
    CONNECTED,
    /**
     * [Chinese text], [Chinese text]
     */
    SERVICE_DISCOVERING,
    /**
     * [Chinese text], [Chinese text]
     */
    SERVICE_DISCOVERED,
    /**
     * [Chinese text]
     */
    RELEASED,
    /**
     * [Chinese text]
     */
    TIMEOUT,
    /**
     * MTUsSettings[Chinese text]
     */
    MTU_SUCCESS
    }
