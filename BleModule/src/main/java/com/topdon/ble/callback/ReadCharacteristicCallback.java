package com.topdon.ble.callback;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 18:42
* author: bichuanfeng
 */
public interface ReadCharacteristicCallback extends RequestFailedCallback {
 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 void onCharacteristicRead(Request request, byte[] value);
}
