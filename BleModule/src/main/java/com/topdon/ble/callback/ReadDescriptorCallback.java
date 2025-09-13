package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:41
* author: bichuanfeng
 */
public interface ReadDescriptorCallback extends RequestFailedCallback {
 /**
 *
 */
 void onDescriptorRead(Request request, byte[] value);
}
