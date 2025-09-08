package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:40
 * author: bichuanfeng
 */
public interface WriteCharacteristicCallback extends RequestFailedCallback {
    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param value   [Chinese text]
     */
    void onCharacteristicWrite(Request request, byte[] value);
}
