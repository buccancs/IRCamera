package com.topdon.ble;

/**
 * connectionstate
 * <p>
 * date: 2019/8/12 14:26
 * author: bichuanfeng
 */
public enum ConnectionState {
    /**
     * 已disconnectconnection
     */
    DISCONNECTED,
    /**
     * 正在connection
     */
    CONNECTING,
    /**
     * 正在Search重连
     */
    SCANNING_FOR_RECONNECTION,
    /**
     * 已connection，还未执行发现service
     */
    CONNECTED,
    /**
     * 已connection，正在发现service
     */
    SERVICE_DISCOVERING,
    /**
     * 已connection，success发现service
     */
    SERVICE_DISCOVERED,
    /**
     * connection已release
     */
    RELEASED,
    /**
     * 超时
     */
    TIMEOUT,
    /**
     * MTUssettingssuccess
     */
    MTU_SUCCESS
    }
