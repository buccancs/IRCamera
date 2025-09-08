package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:41
 * author: bichuanfeng
 */
public interface ReadDescriptorCallback extends RequestFailedCallback {
    /**
     * [Chinese text]
     *
     * @param request [Chinese text]
     * @param value   [Chinese text]
     */
    void onDescriptorRead(Request request, byte[] value);
}
