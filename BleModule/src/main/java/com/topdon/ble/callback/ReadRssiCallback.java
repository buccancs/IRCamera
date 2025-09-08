package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:44
 * author: bichuanfeng
 */
public interface ReadRssiCallback extends RequestFailedCallback {
    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param rssi    [Chinese text]
     */
    void onRssiRead(Request request, int rssi);
}
