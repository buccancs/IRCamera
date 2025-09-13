package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 18:42
* author: bichuanfeng
 */
public interface ReadCharacteristicCallback extends RequestFailedCallback {
 /**
 *
 */
 void onCharacteristicRead(Request request, byte[] value);
}
