package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:39
 * author: bichuanfeng
 */
public interface RequestFailedCallback extends RequestCallback {
    /**
     * 请求failed
     *
     * @param request  请求
     * @param failType failedtype。{@link Connection#REQUEST_FAIL_TYPE_GATT_IS_NULL}等
     * @param value    请求时带的data，可能为null
     */
    void onRequestFailed(Request request, int failType, Object value);
}
