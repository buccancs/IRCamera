package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:39
 * author: bichuanfeng
 */
public interface RequestFailedCallback extends RequestCallback {
    /**
     * [Chinese text]
     *
     * @param request  [Chinese text]
     * @param failType [Chinese text]. {@link Connection#REQUEST_FAIL_TYPE_GATT_IS_NULL}[Chinese text]
     * @param value    [Chinese text], [Chinese text]null
     */
    void onRequestFailed(Request request, int failType, Object value);
}
